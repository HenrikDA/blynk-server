package cc.blynk.server;

import cc.blynk.common.utils.Config;
import cc.blynk.common.utils.ParseUtil;
import cc.blynk.server.auth.UserRegistry;
import cc.blynk.server.handlers.logging.LoggingHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/1/2015.
 */
public class Server implements Runnable {

    private static final Logger log = LogManager.getLogger(Server.class);

    private int port;
    private ChannelFuture serverSocketFuture;

    public Server(int port) {
        this.port = port;
        init();
    }

    public static void main(String[] args) throws Exception {
        // create Options object
        Options options = new Options();
        options.addOption("port", true, "Server port.");
        CommandLine cmd = new BasicParser().parse(options, args);

        String portString = cmd.getOptionValue("port", String.valueOf(Config.DEFAULT_PORT));

        int port = ParseUtil.parsePortString(portString);

        log.info("Using port : {}", port);

        new Thread(new Server(port)).start();
    }

    private void init() {
        log.debug("Reading user DB.");
        //reading DB to RAM.
        UserRegistry.init();
        log.debug("Reading user DB finished.");
    }

    @Override
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                            //.handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new LoggingHandler())
                    .childHandler(new ServerHandlersInitializer());

            serverSocketFuture = b.bind(port).sync();
            serverSocketFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public void stop() {
        serverSocketFuture.channel().close();
    }
}
