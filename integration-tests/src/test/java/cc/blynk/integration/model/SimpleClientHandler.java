package cc.blynk.integration.model;

import cc.blynk.common.model.messages.MessageBase;
import cc.blynk.common.model.messages.MessageFactory;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import static org.mockito.Mockito.*;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 1/31/2015.
 */
public class SimpleClientHandler extends SimpleChannelInboundHandler<MessageBase> {

    @Override
    public void channelRead0(ChannelHandlerContext ctx, MessageBase msg) throws Exception {
        //System.out.println(msg);
    }

    public void check(int responseMessageCode) throws Exception {
        verify(this).channelRead(any(), eq(MessageFactory.produce(1, responseMessageCode)));
    }

    public void check(MessageBase responseMessage) throws Exception {
        verify(this).channelRead(any(), eq(responseMessage));
    }

}