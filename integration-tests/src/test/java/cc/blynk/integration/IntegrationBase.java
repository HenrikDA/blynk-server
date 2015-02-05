package cc.blynk.integration;

import cc.blynk.client.Client;
import cc.blynk.common.model.messages.Message;
import cc.blynk.common.model.messages.MessageBase;
import cc.blynk.integration.model.ClientPair;
import cc.blynk.integration.model.SimpleClientHandler;
import cc.blynk.integration.model.TestChannelInitializer;
import cc.blynk.integration.model.TestClient;
import cc.blynk.server.model.UserProfile;
import cc.blynk.server.utils.JsonParser;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

import static cc.blynk.common.enums.Response.OK;
import static cc.blynk.common.model.messages.MessageFactory.produce;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/4/2015.
 */
public abstract class IntegrationBase {

    public static final int TEST_PORT = 9090;

    public SimpleClientHandler responseMock;

    @Mock
    public Random random;

    @Mock
    public Random random2;

    @Mock
    public BufferedReader bufferedReader;

    @Mock
    public BufferedReader bufferedReader2;

    public static void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {

        }

    }

    public static ClientPair initAppAndHardPair(String host, int port) throws Exception {
        return initAppAndHardPair(host, port, "dima@mail.ua 1");
    }

    public static ClientPair initAppAndHardPair(String host, int port, String user) throws Exception {
        SimpleClientHandler appResponseMock = Mockito.mock(SimpleClientHandler.class);
        SimpleClientHandler hardResponseMock = Mockito.mock(SimpleClientHandler.class);

        TestClient appClient = new TestClient(host, port, new TestChannelInitializer(appResponseMock));
        TestClient hardClient = new TestClient(host, port, new TestChannelInitializer(hardResponseMock));

        appClient.send("register " + user)
                .send("login " + user)
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

        return new ClientPair(appClient, hardClient);
    }

    /**
     * Creates client socket, sends 1 command, sleeps for 100ms checks that sever response is OK.
     */
    public void makeCommand(int msgId, MessageBase responseMessage, String... commands) throws Exception {
        responseMock = Mockito.mock(SimpleClientHandler.class);
        Client client = new Client("localhost", TEST_PORT, random);

        when(random.nextInt(Short.MAX_VALUE)).thenReturn(msgId);

        OngoingStubbing<String> ongoingStubbing = when(bufferedReader.readLine());
        for (final String cmd : commands) {
            ongoingStubbing = ongoingStubbing.thenAnswer(invocation -> {
                Thread.sleep(100);
                return cmd;
            });
        }

        client.start(new TestChannelInitializer(responseMock), bufferedReader);

        verify(responseMock).channelRead(any(), eq(responseMessage));
    }

    public String readTestUserProfile() {
        InputStream is = this.getClass().getResourceAsStream("/json_test/user_profile_json.txt");
        UserProfile userProfile = JsonParser.parseProfile(is);
        return userProfile.toString();
    }


}
