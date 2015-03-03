package cc.blynk.server.model.auth;

import io.netty.channel.Channel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.nio.channels.SocketChannel;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/17/2015.
 */
public class ChannelState extends NioSocketChannel {

    public boolean isHardwareChannel;

    public Integer dashId;

    public User user;

    public ChannelState(Channel parent, SocketChannel socket) {
        super(parent, socket);
    }
}
