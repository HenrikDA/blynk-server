package cc.blynk.integration;

import cc.blynk.common.model.messages.Message;
import cc.blynk.integration.model.ClientPair;
import cc.blynk.server.core.application.AppServer;
import cc.blynk.server.core.hardware.HardwareServer;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static cc.blynk.common.enums.Command.HARDWARE_COMMAND;
import static cc.blynk.common.enums.Command.PING;
import static cc.blynk.common.enums.Response.*;
import static cc.blynk.common.model.messages.MessageFactory.produce;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/2/2015.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class MainWorkflowTest extends IntegrationBase {

    private AppServer appServer;
    private HardwareServer hardwareServer;

    @Before
    public void init() throws Exception {
        initServerStructures();

        FileUtils.deleteDirectory(fileManager.getDataDir().toFile());

        hardwareServer = new HardwareServer(properties, fileManager, userRegistry, sessionsHolder, stats);
        appServer = new AppServer(properties, fileManager, userRegistry, sessionsHolder, stats);
        new Thread(hardwareServer).start();
        new Thread(appServer).start();

        //todo improve this
        //wait util server starts.
        sleep(500);
    }

    @After
    public void shutdown() {
        appServer.stop();
        hardwareServer.stop();
    }

    @Test
    public void testConnectAppAndHardware() throws Exception {
        initAppAndHardPair();
    }

    @Test
    public void testPingCommandWorks() throws Exception {
        ClientPair clientPair = initAppAndHardPair();
        clientPair.appClient.send("ping");
        verify(clientPair.hardwareClient.responseMock, timeout(500)).channelRead(any(), eq(produce(1, PING, "")));
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(produce(1, OK)));
    }

    @Test
    public void testPingCommandNoDevice() throws Exception {
        ClientPair clientPair = initAppAndHardPair();
        clientPair.appClient.send("ping");
        verify(clientPair.hardwareClient.responseMock, timeout(500)).channelRead(any(), eq(produce(1, PING, "")));
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(produce(1, OK)));

        //closing hard channel
        clientPair.hardwareClient.stop();

        clientPair.appClient.reset();
        clientPair.hardwareClient.reset();

        clientPair.appClient.send("ping");
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(produce(1, DEVICE_NOT_IN_NETWORK)));
    }


    @Test
    public void testTweetException() throws Exception {
        ClientPair clientPair = initAppAndHardPair();
        String userProfile = readTestUserProfile();
        clientPair.appClient.send("saveProfile " + userProfile);
        clientPair.hardwareClient.send("tweet 123");

        verify(clientPair.hardwareClient.responseMock, timeout(3000)).channelRead(any(), eq(produce(1, TWEET_EXCEPTION)));
    }

    @Test
    public void testAppSendAnyHardCommandAndBack() throws Exception {
        ClientPair clientPair = initAppAndHardPair();
        clientPair.appClient.send("hardware 1 1");
        verify(clientPair.hardwareClient.responseMock, timeout(500)).channelRead(any(), eq(produce(1, HARDWARE_COMMAND, "1 1".replaceAll(" ", "\0"))));

        clientPair.hardwareClient.send("hardware 1 1");

        ArgumentCaptor<Message> objectArgumentCaptor = ArgumentCaptor.forClass(Message.class);
        verify(clientPair.appClient.responseMock, timeout(500).times(1)).channelRead(any(), objectArgumentCaptor.capture());

        List<Message> arguments = objectArgumentCaptor.getAllValues();
        Message hardMessage = arguments.get(0);
        assertEquals(1, hardMessage.id);
        assertEquals(HARDWARE_COMMAND, hardMessage.command);
        assertEquals(3, hardMessage.length);
        assertEquals("1 1".replaceAll(" ", "\0"), hardMessage.body);
    }

    @Test
    public void testAppSendWriteHardCommandNotGraphAndBack() throws Exception {
        ClientPair clientPair = initAppAndHardPair();
        clientPair.appClient.send("hardware ar 11");
        verify(clientPair.hardwareClient.responseMock, timeout(500)).channelRead(any(), eq(produce(1, HARDWARE_COMMAND, "ar 11".replaceAll(" ", "\0"))));

        String body = "aw 11 333";
        clientPair.hardwareClient.send("hardware " + body);

        ArgumentCaptor<Message> objectArgumentCaptor = ArgumentCaptor.forClass(Message.class);
        verify(clientPair.appClient.responseMock, timeout(500).times(1)).channelRead(any(), objectArgumentCaptor.capture());

        List<Message> arguments = objectArgumentCaptor.getAllValues();
        Message hardMessage = arguments.get(0);
        assertEquals(1, hardMessage.id);
        assertEquals(HARDWARE_COMMAND, hardMessage.command);
        assertEquals(body.length(), hardMessage.length);
        assertTrue(hardMessage.body.startsWith(body.replaceAll(" ", "\0")));
    }

    @Test
    public void testAppSendWriteHardCommandForGraphAndBack() throws Exception {
        ClientPair clientPair = initAppAndHardPair();
        String userProfileWithGraph = readTestUserProfile();
        clientPair.appClient.send("saveProfile " + userProfileWithGraph);
        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(produce(1, OK)));

        reset(clientPair.appClient.responseMock);
        clientPair.appClient.reset();

        clientPair.appClient.send("hardware ar 8");
        verify(clientPair.hardwareClient.responseMock, timeout(500)).channelRead(any(), eq(produce(1, HARDWARE_COMMAND, "ar 8".replaceAll(" ", "\0"))));

        String body = "aw 8 333";
        clientPair.hardwareClient.send("hardware " + body);

        ArgumentCaptor<Message> objectArgumentCaptor = ArgumentCaptor.forClass(Message.class);
        verify(clientPair.appClient.responseMock, timeout(500).times(1)).channelRead(any(), objectArgumentCaptor.capture());

        List<Message> arguments = objectArgumentCaptor.getAllValues();
        Message hardMessage = arguments.get(0);
        assertEquals(1, hardMessage.id);
        assertEquals(HARDWARE_COMMAND, hardMessage.command);
        //"aw 11 333".length + ts.length + separator
        assertEquals(body.length() + 14, hardMessage.length);
        assertTrue(hardMessage.body.startsWith(body.replaceAll(" ", "\0")));
    }


    @Test
    public void testConnectAppAndHardwareAndSendCommands() throws Exception {
        ClientPair clientPair = initAppAndHardPair();

        for (int i = 0; i < 100; i++) {
            clientPair.appClient.send("hardware 1 1");
        }

        verify(clientPair.hardwareClient.responseMock, timeout(500).times(100)).channelRead(any(), any());
    }

    @Test
    public void testTryReachQuotaLimit() throws Exception {
        ClientPair clientPair = initAppAndHardPair();

        String body = "ar 100 100";

        //within 1 second sending more messages than default limit 100.
        for (int i = 0; i < 1000 / 9; i++) {
            clientPair.appClient.send("hardware " + body, 1);
            sleep(9);
        }

        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(produce(1, MESSAGE_QUOTA_LIMIT_EXCEEDED)));
        verify(clientPair.hardwareClient.responseMock, atLeast(100)).channelRead(any(), eq(produce(1, HARDWARE_COMMAND, body.replaceAll(" ", "\0"))));

        clientPair.appClient.reset();
        clientPair.hardwareClient.reset();

        //check no more accepted
        for (int i = 0; i < 10; i++) {
            clientPair.appClient.send("hardware " + body, 1);
            sleep(9);
        }

        verify(clientPair.appClient.responseMock, times(0)).channelRead(any(), eq(produce(1, MESSAGE_QUOTA_LIMIT_EXCEEDED)));
        verify(clientPair.hardwareClient.responseMock, times(0)).channelRead(any(), eq(produce(1, HARDWARE_COMMAND, body.replaceAll(" ", "\0"))));
    }

    @Test
    @Ignore("hard to test this case...")
    public void testTryReachQuotaLimitAndWarningExceededLimit() throws Exception {
        ClientPair clientPair = initAppAndHardPair();

        String body = "ar 100 100";

        //within 1 second sending more messages than default limit 100.
        for (int i = 0; i < 1000 / 9; i++) {
            clientPair.appClient.send("hardware " + body, 1);
            sleep(9);
        }

        verify(clientPair.appClient.responseMock, timeout(1000)).channelRead(any(), eq(produce(1, MESSAGE_QUOTA_LIMIT_EXCEEDED)));
        verify(clientPair.hardwareClient.responseMock, atLeast(100)).channelRead(any(), eq(produce(1, HARDWARE_COMMAND, body.replaceAll(" ", "\0"))));

        clientPair.appClient.reset();
        clientPair.hardwareClient.reset();

        //waiting to avoid limit.
        sleep(1000);

        for (int i = 0; i < 100000 / 9; i++) {
            clientPair.appClient.send("hardware " + body, 1);
            sleep(9);
        }

        verify(clientPair.appClient.responseMock, timeout(500)).channelRead(any(), eq(produce(1, MESSAGE_QUOTA_LIMIT_EXCEEDED)));
        verify(clientPair.hardwareClient.responseMock, atLeast(100)).channelRead(any(), eq(produce(1, HARDWARE_COMMAND, body.replaceAll(" ", "\0"))));

    }

}
