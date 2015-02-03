package cc.blynk.integration;

import cc.blynk.client.Client;
import cc.blynk.common.model.messages.MessageBase;
import cc.blynk.server.Server;
import cc.blynk.server.model.UserProfile;
import cc.blynk.server.utils.FileManager;
import cc.blynk.server.utils.JsonParser;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.OngoingStubbing;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.Random;

import static cc.blynk.common.enums.Command.GET_TOKEN;
import static cc.blynk.common.enums.Command.LOAD_PROFILE;
import static cc.blynk.common.enums.Response.DEVICE_NOT_IN_NETWORK;
import static cc.blynk.common.enums.Response.OK;
import static cc.blynk.common.model.messages.MessageFactory.produce;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/2/2015.
 *
 * Basic integration test. Allows to test base commands workflow. Thus netty is asynchronous
 * test is little bit complex, but I don't know right now how to make it better and simpler.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ProtocolCommandsTest {

    public static final int TEST_PORT = 9090;

    private Server server;

    @Mock
    private BufferedReader bufferedReader;

    @Mock
    private Random random;

    private SimpleClientHandler responseMock;


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
    public void testQuit() throws Exception {
        testQuitClient();
    }

    @Test
    //all commands together cause all operations requires register and then login =(.
    public void testAllCommandOneByOneTestSuit() throws Exception {
        makeCommand(1, produce(1, OK), "register dmitriy@mail.ua 1", "quit");

        makeCommand(2, produce(2, OK), "login dmitriy@mail.ua 1", "quit");

        makeCommands(
                new int[] {3,4},
                new MessageBase[]{produce(3, OK), produce(4, GET_TOKEN, "12345678901234567890123456789012")}, //token is 32 length string
                "login dmitriy@mail.ua 1", "getToken 1", "quit"
        );

        makeCommands(
                new int[] {5,6},
                new MessageBase[]{produce(5, OK), produce(6, LOAD_PROFILE, "{}")}, //token is 32 length string
                "login dmitriy@mail.ua 1", "loadProfile", "quit"
        );

        String userProfileString = readTestUserProfile();

        makeCommands(
                new int[] {7,8},
                new MessageBase[]{produce(7, OK), produce(8, OK)}, //token is 32 length string
                "login dmitriy@mail.ua 1", "saveProfile " + userProfileString, "quit"
        );

        makeCommands(
                new int[] {9,10, 11},
                new MessageBase[]{produce(9, OK), produce(10, OK), produce(11, LOAD_PROFILE, userProfileString)}, //token is 32 length string
                "login dmitriy@mail.ua 1", "saveProfile " + userProfileString, "loadProfile", "quit"
        );

    }

    @Test
    public void testHardwareNotInNetwork() throws Exception {
        makeCommand(1, produce(1, OK), "register dmitriy@mail.ua 1", "quit");

        makeCommands(
                new int[] {2, 3},
                new MessageBase[]{produce(2, OK), produce(3, DEVICE_NOT_IN_NETWORK)},
                "login dmitriy@mail.ua 1", "hardware 1 1", "quit"
        );

    }


    @Test
    //all commands together cause all operations requires register and then login =(.
    public void testPingDeviceNotInNetwork() throws Exception {
        makeCommand(1, produce(1, OK), "register dmitriy@mail.ua 1", "quit");

        makeCommands(
                new int[] {2, 3},
                new MessageBase[]{produce(2, OK), produce(3, DEVICE_NOT_IN_NETWORK)},
                "login dmitriy@mail.ua 1", "ping", "quit"
        );

    }

    private void testQuitClient() throws Exception {
        responseMock = Mockito.mock(SimpleClientHandler.class);
        Client client = new Client("localhost", TEST_PORT, new Random());
        when(bufferedReader.readLine()).thenReturn("quit");
        client.start(new TestChannelInitializer(responseMock), bufferedReader);
        verify(responseMock, never()).channelRead(any(), any());
    }

    /**
     * Creates client socket, sends 1 command, sleeps for 100ms checks that sever response is OK.
     */
    private void makeCommand(int msgId, MessageBase responseMessage, String... commands) throws Exception {
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

    private void makeCommands(int[] msgIds, MessageBase[] responseMessages, String... commands) throws Exception {
        responseMock = Mockito.mock(SimpleClientHandler.class);
        Client client = new Client("localhost", TEST_PORT, random);

        OngoingStubbing<Integer> stubbingRandom = when(random.nextInt(Short.MAX_VALUE));
        for (int messageId : msgIds) {
            stubbingRandom = stubbingRandom.thenReturn(messageId);
        }

        OngoingStubbing<String> ongoingStubbing = when(bufferedReader.readLine());
        for (final String cmd : commands) {
            ongoingStubbing = ongoingStubbing.thenAnswer(invocation -> {
                Thread.sleep(100);
                return cmd;
            });
        }

        client.start(new TestChannelInitializer(responseMock), bufferedReader);

        verify(responseMock, times(responseMessages.length)).channelRead(any(), any());
        for (MessageBase messageBase : responseMessages) {
            verify(responseMock).channelRead(any(), eq(messageBase));
        }
    }

    private String readTestUserProfile() {
        InputStream is = this.getClass().getResourceAsStream("/json_test/user_profile_json.txt");
        UserProfile userProfile = JsonParser.parseProfile(is);
        return userProfile.toString();
    }

}
