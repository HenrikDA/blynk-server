package cc.blynk.integration;

import cc.blynk.client.Client;
import cc.blynk.common.model.messages.MessageBase;
import cc.blynk.common.utils.PropertiesUtil;
import cc.blynk.integration.model.SimpleClientHandler;
import cc.blynk.integration.model.TestChannelInitializer;
import cc.blynk.server.Server;
import cc.blynk.server.workers.ProfileSaverWorker;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.OngoingStubbing;

import java.util.Random;

import static cc.blynk.common.enums.Command.GET_TOKEN;
import static cc.blynk.common.enums.Command.LOAD_PROFILE;
import static cc.blynk.common.enums.Response.*;
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
public class ProtocolCommandsTest extends IntegrationBase {

    private Server server;

    private ProfileSaverWorker profileSaverWorker;

    @Before
    public void init() throws Exception {
        initServerStructures();

        FileUtils.deleteDirectory(fileManager.getDataDir().toFile());

        server = new Server(properties, fileManager, userRegistry, sessionsHolder, stats);
        profileSaverWorker = new ProfileSaverWorker(userRegistry, fileManager, PropertiesUtil.getIntProperty(properties, "profile.save.worker.period"), stats);
        new Thread(server).start();
        new Thread(profileSaverWorker).start();

        //wait util server start.
        Thread.sleep(500);
    }

    @After
    public void shutdown() {
        server.stop();
    }

    @Test
    public void testQuit() throws Exception {
        responseMock = Mockito.mock(SimpleClientHandler.class);
        Client client = new Client("localhost", TEST_PORT, new Random());
        when(bufferedReader.readLine()).thenReturn("quit");
        client.start(new TestChannelInitializer(responseMock), bufferedReader);
        verify(responseMock, never()).channelRead(any(), any());
    }

    @Test
    //all commands together cause all operations requires register and then login =(.
    public void testAllCommandOneByOneTestSuit() throws Exception {
        makeCommand("register dmitriy@mail.ua 1").check(OK);

        makeCommand("login dmitriy@mail.ua 1").check(OK);

        makeCommands(
                new int[] {5,6},
                new MessageBase[]{produce(5, OK), produce(6, LOAD_PROFILE, "{}")},
                "login dmitriy@mail.ua 1", "loadProfile", "quit"
        );

        String userProfileString = readTestUserProfile();

        makeCommands(
                new int[] {7,8},
                new MessageBase[]{produce(7, OK), produce(8, OK)},
                "login dmitriy@mail.ua 1", "saveProfile " + userProfileString, "quit"
        );

        makeCommands(
                new int[] {9,10, 11},
                new MessageBase[]{produce(9, OK), produce(10, OK), produce(11, LOAD_PROFILE, userProfileString)},
                "login dmitriy@mail.ua 1", "saveProfile " + userProfileString, "loadProfile", "quit"
        );

        //waiting background thread to save profile.
        sleep(600);

        makeCommands(
                new int[] {3,4},
                new MessageBase[]{produce(3, OK), produce(4, GET_TOKEN, "12345678901234567890123456789012")}, //token is 32 length string
                "login dmitriy@mail.ua 1", "getToken 1", "quit"
        );

    }

    @Test
    public void testAppNotRegistered() throws Exception {
        makeCommands(
                new int[] {2},
                new MessageBase[]{produce(2, USER_NOT_REGISTERED)},
                "login dmitriy@mail.ua 1", "quit"
        );
    }


    @Test
    public void testInvalidToken() throws Exception {
        makeCommands(
                new int[] {2},
                new MessageBase[]{produce(2, INVALID_TOKEN)},
                "login dasdsadasdasdasdasdas", "quit"
        );
    }

    @Test
    public void testLogin2Times() throws Exception {
        makeCommand("register dmitriy@mail.ua 1").check(OK);

        makeCommands(
                new int[] {2, 3},
                new MessageBase[]{produce(2, OK), produce(3, USER_ALREADY_LOGGED_IN)},
                "login dmitriy@mail.ua 1", "login dmitriy@mail.ua 1", "quit"
        );
    }

    @Test
    public void testGetTokenForNonExistentDashId() throws Exception {
        makeCommand("register dmitriy@mail.ua 1").check(OK);

        makeCommands(
                new int[] {2, 3},
                new MessageBase[]{produce(2, OK), produce(3, ILLEGAL_COMMAND)},
                "login dmitriy@mail.ua 1", "getToken 1", "quit"
        );
    }

    @Test
    //all commands together cause all operations requires register and then login =(.
    public void testProfileWithManyDashes() throws Exception {
        makeCommand("register dmitriy@mail.ua 1").check(OK);

        String userProfileString = readTestUserProfile("user_profile_json_many_dashes.txt");

        makeCommands(
                new int[] {7,8},
                new MessageBase[]{produce(7, OK), produce(8, NOT_ALLOWED)},
                "login dmitriy@mail.ua 1", "saveProfile " + userProfileString, "quit"
        );

    }

    @Test
    public void testInvalidTweetBody() throws Exception {
        makeCommand("register dmitriy@mail.ua 1").check(OK);

        makeCommands(
                new int[] {2,3},
                new MessageBase[]{produce(2, OK), produce(3, TWEET_BODY_INVALID_EXCEPTION)},
                "login dmitriy@mail.ua 1", "tweet", "quit"
        );
    }

    @Test
    public void testPassNotValid() throws Exception {
        makeCommand("register dmitriy@mail.ua 1").check(OK);

        makeCommands(
                new int[]{2},
                new MessageBase[]{produce(2, USER_NOT_AUTHENTICATED)},
                "login dmitriy@mail.ua 2", "quit"
        );

    }

    @Test
    public void testHardwareNotInNetwork() throws Exception {
        makeCommand("register dmitriy@mail.ua 1").check(OK);

        makeCommands(
                new int[]{2, 3},
                new MessageBase[]{produce(2, OK), produce(3, DEVICE_NOT_IN_NETWORK)},
                "login dmitriy@mail.ua 1", "hardware 1 1", "quit"
        );

    }

    @Test
    public void testTryHardLoginWithoutToken() throws Exception {
        makeCommand("register dmitriy@mail.ua 1").check(OK);

        makeCommands(
                new int[]{2},
                new MessageBase[]{produce(2, INVALID_TOKEN)},
                "login adsadasdasdasdas", "quit"
        );

    }


    @Test
    //all commands together cause all operations requires register and then login =(.
    public void testPingDeviceNotInNetwork() throws Exception {
        makeCommand("register dmitriy@mail.ua 1").check(OK);

        makeCommands(
                new int[] {2, 3},
                new MessageBase[]{produce(2, OK), produce(3, DEVICE_NOT_IN_NETWORK)},
                "login dmitriy@mail.ua 1", "ping", "quit"
        );

    }

    /**
     * 1) Creates client socket;
     * 2) Sends commands;
     * 3) Sleeps for 100ms between every command send;
     * 4) Checks that sever response is OK;
     * 5) Closing socket.
     */
    public SimpleClientHandler makeCommand(String command) throws Exception {
        responseMock = Mockito.mock(SimpleClientHandler.class);
        Client client = new Client("localhost", TEST_PORT, random);

        when(random.nextInt(Short.MAX_VALUE)).thenReturn(1);

        OngoingStubbing<String> ongoingStubbing = when(bufferedReader.readLine());
        ongoingStubbing = ongoingStubbing.thenReturn(command);

        ongoingStubbing.thenAnswer(invocation -> {
            sleep(100);
            return "quit";
        });

        client.start(new TestChannelInitializer(responseMock), bufferedReader);

        return responseMock;
    }

    /**
     * 1) Creates client socket;
     * 2) Sends commands;
     * 3) Sleeps for 100ms between every command send;
     * 4) Checks that sever response is OK;
     * 5) Closing socket.
     */
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
                Thread.sleep(200);
                return cmd;
            });
        }

        client.start(new TestChannelInitializer(responseMock), bufferedReader);

        verify(responseMock, times(responseMessages.length)).channelRead(any(), any());
        for (MessageBase messageBase : responseMessages) {
            verify(responseMock).channelRead(any(), eq(messageBase));
        }
    }

}
