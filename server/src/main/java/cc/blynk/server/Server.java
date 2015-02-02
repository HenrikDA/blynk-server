package cc.blynk.server;

import cc.blynk.common.utils.Config;
import cc.blynk.common.utils.ParseUtil;
import cc.blynk.server.handlers.logging.LoggingHandler;
import io.netty.bootstrap.ServerBootstrap;
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
public class Server {

    private static final Logger log = LogManager.getLogger(Server.class);

    private int port;

    public Server(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception {
        // create Options object
        Options options = new Options();
        options.addOption("port", true, "Server port.");
        CommandLine cmd = new BasicParser().parse(options, args);

        String portString = cmd.getOptionValue("port", String.valueOf(Config.DEFAULT_PORT));

        int port = ParseUtil.parsePortString(portString);

        log.info("Using port : {}", port);

        new Server(port).start();
    }


    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    //.handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new LoggingHandler())
                    .childHandler(new ServerHandlersInitializer());

            b.bind(port).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
