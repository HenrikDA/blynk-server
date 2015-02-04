package cc.blynk.integration;

import cc.blynk.common.model.messages.Message;
import cc.blynk.integration.model.SimpleClientHandler;
import cc.blynk.integration.model.TestChannelInitializer;
import cc.blynk.integration.model.TestClient;
import cc.blynk.server.Server;
import cc.blynk.server.utils.FileManager;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static cc.blynk.common.enums.Response.OK;
import static cc.blynk.common.model.messages.MessageFactory.produce;
import static org.mockito.Mockito.*;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/2/2015.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class MainWorkflowTest extends IntegrationBase {

    private Server server;

    @Before
    public void init() throws Exception {
        FileManager fileManager = new FileManager();
        FileUtils.deleteDirectory(fileManager.getDataDir().toFile());

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
    public void testConnectAppAndHardware() throws Exception {
        SimpleClientHandler appResponseMock = Mockito.mock(SimpleClientHandler.class);
        SimpleClientHandler hardResponseMock = Mockito.mock(SimpleClientHandler.class);

        TestClient appClient = new TestClient("localhost", TEST_PORT, new TestChannelInitializer(appResponseMock));
        TestClient hardClient = new TestClient("localhost", TEST_PORT, new TestChannelInitializer(hardResponseMock));

        appClient.send("register dima@mail.ua 1")
                 .send("login dima@mail.ua 1")
                 .send("getToken 1");

        ArgumentCaptor<Object> objectArgumentCaptor = ArgumentCaptor.forClass(Object.class);
        verify(appResponseMock, times(3)).channelRead(any(), objectArgumentCaptor.capture());

        List<Object> arguments = objectArgumentCaptor.getAllValues();
        Message getTokenMessage = (Message) arguments.get(2);
        String token = getTokenMessage.body;

        hardClient.send("login " + token);
        verify(hardResponseMock).channelRead(any(), eq(produce(1, OK)));
    }

    @Test
    public void testConnectAppAndHardwareAndSendCommands() throws Exception {
        SimpleClientHandler appResponseMock = Mockito.mock(SimpleClientHandler.class);
        SimpleClientHandler hardResponseMock = Mockito.mock(SimpleClientHandler.class);

        TestClient appClient = new TestClient("localhost", TEST_PORT, new TestChannelInitializer(appResponseMock));
        TestClient hardClient = new TestClient("localhost", TEST_PORT, new TestChannelInitializer(hardResponseMock));

        appClient.send("register dima@mail.ua 1")
                .send("login dima@mail.ua 1")
                .send("getToken 1");

        ArgumentCaptor<Object> objectArgumentCaptor = ArgumentCaptor.forClass(Object.class);
        verify(appResponseMock, times(3)).channelRead(any(), objectArgumentCaptor.capture());

        List<Object> arguments = objectArgumentCaptor.getAllValues();
        Message getTokenMessage = (Message) arguments.get(2);
        String token = getTokenMessage.body;


        hardClient.send("login " + token);
        verify(hardResponseMock).channelRead(any(), eq(produce(1, OK)));

        reset(hardResponseMock);
        reset(appResponseMock);
        appClient.reset();
        hardClient.reset();

        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            appClient.sendNoSleep("hardware 1 1");
        }
        System.out.println("Time : " + (System.currentTimeMillis() - start));

        sleep(500);

        for (int i = 1; i <= 100; i++) {
            verify(appResponseMock).channelRead(any(), eq(produce(i, OK)));
        }
        verify(hardResponseMock, times(100)).channelRead(any(), any());
    }

}
