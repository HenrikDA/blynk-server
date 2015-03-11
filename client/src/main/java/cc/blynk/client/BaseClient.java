package cc.blynk.client;

import cc.blynk.common.enums.Command;
import cc.blynk.common.model.messages.Message;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.channels.UnresolvedAddressException;
import java.util.Random;

import static cc.blynk.common.model.messages.MessageFactory.produce;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 1/31/2015.
 */
public abstract class BaseClient {

    protected static final Logger log = LogManager.getLogger(BaseClient.class);

    protected final String host;
    protected final int port;
    protected final Random random;

    protected Channel channel;
    protected NioEventLoopGroup nioEventLoopGroup;

    public BaseClient(String host, int port, Random messageIdGenerator) {
        this.host = host;
        this.port = port;
        this.random = messageIdGenerator;
    }

    public static Message produceMessageBaseOnUserInput(String line, int msgId) {
        String[] input = line.split(" ", 2);

        short command;

        try {
            command = CommandParser.parseCommand(input[0]);
        } catch (IllegalArgumentException e) {
            log.error("Command not supported {}", input[0]);
            return null;
        }

        String body = input.length == 1 ? "" : input[1];
        if (command == Command.HARDWARE_COMMAND) {
            body = body.replaceAll(" ", "\0");
        }
        return produce(msgId, command, body);
    }

    public void start(BufferedReader commandInputStream) {
        this.nioEventLoopGroup = new NioEventLoopGroup(1);
        try {
            Bootstrap b = new Bootstrap();
            b.group(nioEventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(getChannelInitializer());

            // Start the connection attempt.
            this.channel = b.connect(host, port).sync().channel();
            readUserInput(commandInputStream);
        } catch (UnresolvedAddressException uae) {
            log.error("Host name '{}' is invalid. Please make sure it is correct name.", host);
        } catch (ConnectTimeoutException cte) {
            log.error("Timeout exceeded when connecting to '{}:{}'. Please make sure host available and port is open on target host.", host, port);
        } catch (IOException | InterruptedException e) {
            log.error("Error running client. Shutting down.", e);
        } catch (Exception e) {
            log.error(e);
        } finally {
            // The connection is closed automatically on shutdown.
            nioEventLoopGroup.shutdownGracefully();
        }
    }

    protected abstract ChannelInitializer<SocketChannel> getChannelInitializer();

    protected void readUserInput(BufferedReader commandInputStream) throws IOException {
        String line;
        while ((line = commandInputStream.readLine()) != null) {
            // If user typed the 'quit' command, wait until the server closes the connection.
            if ("quit".equals(line.toLowerCase())) {
                log.info("Got 'quit' command. Closing client.");
                channel.close();
                break;
            }

            Message msg = produceMessageBaseOnUserInput(line, (short) random.nextInt(Short.MAX_VALUE));
            if (msg == null) {
                continue;
            }

            log.trace("Message hex : {}", HexConvertor.messageToHex(msg));

            send(msg);
        }
    }

    protected void send(Message msg) {
        if (channel.isWritable()) {
            channel.writeAndFlush(msg);
        }
    }

    public void stop() {
        channel.close().awaitUninterruptibly();
    }
}
