package cc.blynk.server.push;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

/**
* The Blynk Project.
* Created by Dmitriy Dumanskiy.
* Created on 2/8/2015.
*/
final class LoggingConnectionListener implements ConnectionListener {

    private static final Logger log = LogManager.getLogger(LoggingConnectionListener.class);

    @Override
    public void connected(XMPPConnection xmppConnection) {
        log.info("Connected.");
    }

    @Override
    public void authenticated(XMPPConnection xmppConnection) {
        log.info("Authenticated.");
    }

    @Override
    public void reconnectionSuccessful() {
        log.info("Reconnecting..");
    }

    @Override
    public void reconnectionFailed(Exception e) {
        log.info("Reconnection failed.. ", e);
    }

    @Override
    public void reconnectingIn(int seconds) {
        log.info("Reconnecting in {} secs", seconds);
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        log.info("Connection closed on error.");
    }

    @Override
    public void connectionClosed() {
        log.info("Connection closed.");
    }
}
