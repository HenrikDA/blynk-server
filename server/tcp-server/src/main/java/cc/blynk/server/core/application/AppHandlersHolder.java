package cc.blynk.server.core.application;

import cc.blynk.common.utils.ServerProperties;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import cc.blynk.server.handlers.auth.AppLoginHandler;
import cc.blynk.server.handlers.auth.RegisterHandler;
import cc.blynk.server.handlers.workflow.*;
import io.netty.channel.ChannelHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 3/10/2015.
 */
class AppHandlersHolder {

    private final RegisterHandler registerHandler;
    private final AppLoginHandler appLoginHandler;
    private final GetTokenHandler getTokenHandler;
    private final RefreshTokenHandler refreshTokenHandler;
    private final LoadProfileHandler loadProfileHandler;
    private final SaveProfileHandler saveProfileHandler;
    private final ActivateDashboardHandler activateDashboardHandler;
    private final DeActivateDashboardHandler deActivateDashboardHandler;
    private final HardwareHandler hardwareHandler;
    private final PingHandler pingHandler;

    public AppHandlersHolder(ServerProperties props, FileManager fileManager, UserRegistry userRegistry, SessionsHolder sessionsHolder) {
        this.registerHandler = new RegisterHandler(fileManager, userRegistry, sessionsHolder);
        this.appLoginHandler = new AppLoginHandler(fileManager, userRegistry, sessionsHolder);
        this.getTokenHandler = new GetTokenHandler(props, fileManager, userRegistry, sessionsHolder);
        this.refreshTokenHandler = new RefreshTokenHandler(props, fileManager, userRegistry, sessionsHolder);
        this.loadProfileHandler = new LoadProfileHandler(props, fileManager, userRegistry, sessionsHolder);
        this.saveProfileHandler = new SaveProfileHandler(props, fileManager, userRegistry, sessionsHolder);
        this.hardwareHandler = new HardwareHandler(props, fileManager, userRegistry, sessionsHolder);
        this.pingHandler = new PingHandler(props, fileManager, userRegistry, sessionsHolder);
        this.activateDashboardHandler = new ActivateDashboardHandler(props, fileManager, userRegistry, sessionsHolder);
        this.deActivateDashboardHandler = new DeActivateDashboardHandler(props, fileManager, userRegistry, sessionsHolder);
    }

    public List<BaseSimpleChannelInboundHandler> getBaseHandlers() {
        return new ArrayList<BaseSimpleChannelInboundHandler>() {
           {
               add(getTokenHandler);
               add(refreshTokenHandler);
               add(loadProfileHandler);
               add(saveProfileHandler);
               add(hardwareHandler);
               add(pingHandler);
               add(activateDashboardHandler);
               add(deActivateDashboardHandler);
            }
        };
    }

    public List<ChannelHandler> getAllHandlers() {
        return new ArrayList<ChannelHandler>() {
           {
               add(registerHandler);
               add(appLoginHandler);
               add(getTokenHandler);
               add(refreshTokenHandler);
               add(loadProfileHandler);
               add(saveProfileHandler);
               add(hardwareHandler);
               add(pingHandler);
               add(activateDashboardHandler);
               add(deActivateDashboardHandler);
            }
        };
    }
}
