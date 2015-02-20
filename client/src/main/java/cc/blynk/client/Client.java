package cc.blynk.client;

import cc.blynk.client.enums.ClientMode;
import cc.blynk.common.enums.Command;
import cc.blynk.common.model.messages.Message;
import cc.blynk.common.utils.ParseUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
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

    public static final String DEFAULT_HOST = "localhost";
    public static final String DEFAULT_PORT = "8442";
    public static final String DEFAULT_SSL_PORT = "8443";
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
        options.addOption("sslPort", true, "Server ssl port.");
        options.addOption("mode", true, "Client mode. 'hardware' or 'app'.");
        CommandLine cmd = new BasicParser().parse(options, args);

        String host = cmd.getOptionValue("host", DEFAULT_HOST);
        int port = ParseUtil.parseInt(cmd.getOptionValue("port", DEFAULT_PORT));
        int sslPort = ParseUtil.parseInt(cmd.getOptionValue("sslPort", DEFAULT_SSL_PORT));
        ClientMode mode = ClientMode.parse(cmd.getOptionValue("mode", ClientMode.HARDWARE.name()));

        //for app client using SSL sockets with test certificate in classpath...
        SslContext sslCtx = null;
        if (mode == ClientMode.APP) {
            log.info("Using host {} , sslPort : {}, mode : {}", host, sslPort, mode.name());

            //todo think how to simplify with real certs?
            //sslCtx = SslContext.newClientContext(getFileFromResources("/test.crt"));
            sslCtx = SslContext.newClientContext(InsecureTrustManagerFactory.INSTANCE);
            port = sslPort;
        } else {
            log.info("Using host {} , port : {}, mode : {}", host, port, mode.name());
        }

        ClientHandlersInitializer clientHandlersInitializer = new ClientHandlersInitializer(sslCtx, host, port);
        Client client = new Client(host, port, new Random());

        client.start(clientHandlersInitializer, new BufferedReader(new InputStreamReader(System.in)));
    }

    public static Message produceMessageBaseOnUserInput(String line, int msgId) {
        String[] input = line.split(" ");

        short command;

        try {
            command = CommandParser.parseCommand(input[0]);
        } catch (IllegalArgumentException e) {
            log.error("Command not supported {}", input[0]);
            return null;
        }

        input = line.split(" ", 2);
        String body = input.length == 1 ? "" : input[1];
        if (command == Command.HARDWARE_COMMAND) {
            body = body.replaceAll(" ", "\0");
        }
        return produce(msgId, command, body);
    }

    public void start(ChannelInitializer<SocketChannel> channelInitializer, BufferedReader commandInputStream) {
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(channelInitializer);

            // Start the connection attempt.
            Channel clientChannel = b.connect(host, port).sync().channel();

            readUserInput(clientChannel, commandInputStream);
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

            Message msg = produceMessageBaseOnUserInput(line, (short) random.nextInt(Short.MAX_VALUE));
            if (msg == null) {
                continue;
            }
            lastWriteFuture = clientChannel.writeAndFlush(msg);
        }

        return lastWriteFuture;
    }

}
