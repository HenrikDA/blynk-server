package cc.blynk.client;

import cc.blynk.common.utils.Config;
import cc.blynk.common.utils.ParseUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import static cc.blynk.common.model.messages.MessageFactory.produce;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 1/31/2015.
 */
public class Client {

    private static final Logger log = LogManager.getLogger(Client.class);

    private final String host;
    private final int port;
    private final Random random;

    public Client(String host, int port, Random messageIdGenerator) {
        this.host = host;
        this.port = port;
        this.random = messageIdGenerator;
    }

    public static void main(String[] args) throws Exception {
        // create Options object
        Options options = new Options();
        options.addOption("host", true, "Server host or ip.");
        options.addOption("port", true, "Server port.");
        CommandLine cmd = new BasicParser().parse(options, args);

        String host = cmd.getOptionValue("host", Config.DEFAULT_HOST);
        String portString = cmd.getOptionValue("port", String.valueOf(Config.DEFAULT_PORT));

        int port = ParseUtil.parsePortString(portString);

        log.info("Using host : " + host + ", port : " + port);

        new Client(host, port, new Random()).start(new ClientHandlersInitializer(), new BufferedReader(new InputStreamReader(System.in)));
    }

    public void start(ChannelInitializer<SocketChannel> channelInitializer, BufferedReader commandInputStream) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(channelInitializer);

            // Start the connection attempt.
            Channel clientChannel = b.connect(host, port).sync().channel();

            ChannelFuture lastWriteFuture = readUserInput(clientChannel, commandInputStream);
            //wait last send command to finish.
            if (lastWriteFuture != null) {
                lastWriteFuture.sync();
            }

        } catch (IOException | InterruptedException e) {
            log.error("Error running client. Shutting down.", e);
        } finally {
            // The connection is closed automatically on shutdown.
            group.shutdownGracefully();
        }
    }

    private ChannelFuture readUserInput(Channel clientChannel, BufferedReader commandInputStream) throws IOException {
        ChannelFuture lastWriteFuture = null;

        String line;
        while ((line = commandInputStream.readLine()) != null) {
            // If user typed the 'quit' command, wait until the server closes the connection.
            if ("quit".equals(line.toLowerCase())) {
                log.info("Got 'quit' command. Shutting down.");
                clientChannel.close();
                break;
            }

            String[] input = line.split(" ");

            short command;

            try {
                command = CommandParser.parseCommand(input[0]);
            } catch (IllegalArgumentException e) {
                log.error("Command not supported {}", input[0]);
                continue;
            }

            input = line.split(" ", 2);
            String body = input.length == 1 ? "" : input[1];
            lastWriteFuture = clientChannel.writeAndFlush(produce((short) random.nextInt(Short.MAX_VALUE), command, body));
        }

        return lastWriteFuture;
    }

}
