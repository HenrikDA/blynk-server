package cc.blynk.server;

import cc.blynk.common.utils.Config;
import cc.blynk.common.utils.ParseUtil;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import cc.blynk.server.handlers.logging.LoggingHandler;
import cc.blynk.server.utils.JsonParser;
import cc.blynk.server.workers.ProfileSaverRunner;
import cc.blynk.server.workers.timer.TimerRunner;
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

    private UserRegistry userRegistry;
    private FileManager fileManager;
    private SessionsHolder sessionsHolder;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public Server(int port) {
        //just to init mapper on server start and not first access
        JsonParser.check();
        this.port = port;
        this.fileManager = new FileManager();
        this.sessionsHolder = new SessionsHolder();


        log.debug("Reading user DB.");
        this.userRegistry = new UserRegistry(fileManager.deserialize());
        log.debug("Reading user DB finished.");

        new TimerRunner(userRegistry, sessionsHolder).start();
        new ProfileSaverRunner(userRegistry, fileManager).start();
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

    @Override
    public void run() {
        this.bossGroup = new NioEventLoopGroup(1);
        this.workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                            //.handler(new LoggingHandler(LogLevel.INFO))
                    .handler(new LoggingHandler())
                    .childHandler(new ServerHandlersInitializer(fileManager, userRegistry, sessionsHolder));

            ChannelFuture channelFuture = b.bind(port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error(e);
        } finally {
            stop();
        }
    }

    public void stop() {
        log.info("Shutting down...");
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        log.info("Done!");
    }
}
