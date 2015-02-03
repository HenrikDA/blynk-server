package cc.blynk.integration;

import cc.blynk.client.Client;
import cc.blynk.common.handlers.decoders.ReplayingMessageDecoder;
import cc.blynk.common.handlers.encoders.DeviceMessageEncoder;
import cc.blynk.common.model.messages.ResponseMessage;
import cc.blynk.server.Server;
import cc.blynk.server.utils.FileManager;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Random;

import static cc.blynk.common.enums.Response.OK;
import static cc.blynk.common.model.messages.MessageFactory.produce;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/2/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProtocolCommandsTest {

    public static final int TEST_PORT = 9090;

    private Server server;

    @Mock
    private BufferedReader bufferedReader;

    @Mock
    private Random random;

    @Spy
    private BlockingSimpleClientHandler responseMock = new BlockingSimpleClientHandler();

    @BeforeClass
    public static void deleteDataFolder() throws IOException {
        FileManager fileManager = new FileManager();
        Files.delete(fileManager.getDataDir());
    }

    @Before
    public void init() throws Exception {
        server = new Server(TEST_PORT);
        new Thread(server).start();

        //wait util server start.
        Thread.sleep(500);
    }

    @After
    public void shutdown() {
        server.stop();
    }

    @Test
    public void testQuitClient() throws Exception {
        Client client = new Client("localhost", TEST_PORT, new Random());
        when(bufferedReader.readLine()).thenReturn("quit");
        client.start(new TestChannelInitializer(), bufferedReader);
        verify(responseMock, never()).acceptInboundMessage(any());
    }

    @Test
    public void testSendLogin() throws Exception {
        Client client = new Client("localhost", TEST_PORT, random);

        int msgId = 10000;
        when(random.nextInt(Short.MAX_VALUE)).thenReturn(msgId);

        when(bufferedReader.readLine()).thenReturn("login dima@mail.ru 1").thenAnswer(invocation -> {
            Thread.sleep(100);
            return null;
        });

        client.start(new TestChannelInitializer(), bufferedReader);

        ResponseMessage responseMessage = produce(msgId, OK);
        verify(responseMock, times(1)).channelRead0(any(), eq(responseMessage));
    }


    private class TestChannelInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            //process input
            pipeline.addLast(new ReplayingMessageDecoder());
            //process output
            pipeline.addLast(new DeviceMessageEncoder());

            pipeline.addLast(responseMock);
        }
    }

}
