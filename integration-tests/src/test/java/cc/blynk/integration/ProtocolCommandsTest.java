package cc.blynk.integration;

import cc.blynk.client.Client;
import cc.blynk.integration.model.MockHolder;
import cc.blynk.integration.model.SimpleClientHandler;
import cc.blynk.integration.model.TestChannelInitializer;
import cc.blynk.server.core.plain.Server;
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
        profileSaverWorker = new ProfileSaverWorker(jedisWrapper, userRegistry, fileManager, properties.getIntProperty("profile.save.worker.period"), stats);
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
        SimpleClientHandler responseMock = Mockito.mock(SimpleClientHandler.class);
        Client client = new Client("localhost", TEST_PORT, new Random());
        when(bufferedReader.readLine()).thenReturn("quit");
        client.start(new TestChannelInitializer(responseMock), bufferedReader);
        verify(responseMock, never()).channelRead(any(), any());
    }

    @Test
    //all commands together cause all operations requires register and then login =(.
    public void testAllCommandOneByOneTestSuit() throws Exception {
        makeCommands("register dmitriy@mail.ua 1").check(OK);

        makeCommands("login dmitriy@mail.ua 1").check(OK);

        makeCommands("login dmitriy@mail.ua 1", "loadProfile").check(OK).check(produce(1, LOAD_PROFILE, "{}"));

        String userProfileString = readTestUserProfile();

        makeCommands("login dmitriy@mail.ua 1", "saveProfile " + userProfileString).check(2, OK);

        makeCommands("login dmitriy@mail.ua 1", "saveProfile " + userProfileString, "loadProfile").check(2, OK).check(produce(1, LOAD_PROFILE, userProfileString));

        //waiting background thread to save profile.
        sleep(600);

        makeCommands("login dmitriy@mail.ua 1", "getToken 1").check(OK).check(produce(1, GET_TOKEN, "12345678901234567890123456789012"));

    }

    @Test
    public void testAppNotRegistered() throws Exception {
        makeCommands("login dmitriy@mail.ua 1").check(produce(1, USER_NOT_REGISTERED));
    }


    @Test
    public void testInvalidToken() throws Exception {
        makeCommands("login dasdsadasdasdasdasdas").check(produce(1, INVALID_TOKEN));
    }

    @Test
    public void testLogin2Times() throws Exception {
        makeCommands("register dmitriy@mail.ua 1").check(OK);

        makeCommands("login dmitriy@mail.ua 1", "login dmitriy@mail.ua 1").check(OK).check(produce(1, USER_ALREADY_LOGGED_IN));
    }

    @Test
    public void testGetTokenForNonExistentDashId() throws Exception {
        makeCommands("register dmitriy@mail.ua 1").check(OK);

        makeCommands("login dmitriy@mail.ua 1", "getToken 1").check(OK).check(produce(1, ILLEGAL_COMMAND));
    }

    @Test
    //all commands together cause all operations requires register and then login =(.
    public void testProfileWithManyDashes() throws Exception {
        makeCommands("register dmitriy@mail.ua 1").check(OK);

        String userProfileString = readTestUserProfile("user_profile_json_many_dashes.txt");

        makeCommands("login dmitriy@mail.ua 1", "saveProfile " + userProfileString).check(OK).check(produce(1, NOT_ALLOWED));
    }

    @Test
    public void testUserNotAuthenticated() throws Exception {
        makeCommands("register dmitriy@mail.ua 1").check(OK);

        makeCommands("loadProfile").check(produce(1, USER_NOT_AUTHENTICATED));
        makeCommands("saveProfile {}").check(produce(1, USER_NOT_AUTHENTICATED));
        makeCommands("getToken").check(produce(1, USER_NOT_AUTHENTICATED));
        makeCommands("tweet bla").check(produce(1, USER_NOT_AUTHENTICATED));
        makeCommands("hardware 1 1").check(produce(1, USER_NOT_AUTHENTICATED));
        makeCommands("ping").check(produce(1, USER_NOT_AUTHENTICATED));
    }


    @Test
    public void testInvalidTweetBody() throws Exception {
        makeCommands("register dmitriy@mail.ua 1").check(OK);

        makeCommands("login dmitriy@mail.ua 1", "tweet").check(OK).check(produce(1, TWEET_BODY_INVALID_EXCEPTION));
    }

    @Test
    public void testPassNotValid() throws Exception {
        makeCommands("register dmitriy@mail.ua 1").check(OK);

        makeCommands("login dmitriy@mail.ua 2").check(produce(1, USER_NOT_AUTHENTICATED));
    }

    @Test
    public void testHardwareNotInNetwork() throws Exception {
        makeCommands("register dmitriy@mail.ua 1").check(OK);

        makeCommands("login dmitriy@mail.ua 1", "hardware 1 1").check(OK).check(produce(1, DEVICE_NOT_IN_NETWORK));
    }

    @Test
    public void testTryHardLoginWithoutToken() throws Exception {
        makeCommands("register dmitriy@mail.ua 1").check(OK);

        makeCommands("login adsadasdasdasdas").check(produce(1, INVALID_TOKEN));
    }


    @Test
    //all commands together cause all operations requires register and then login =(.
    public void testPingDeviceNotInNetwork() throws Exception {
        makeCommands("register dmitriy@mail.ua 1").check(OK);

        makeCommands("login dmitriy@mail.ua 1", "ping").check(OK).check(produce(1, DEVICE_NOT_IN_NETWORK));
    }

    /**
     * 1) Creates client socket;
     * 2) Sends commands;
     * 3) Sleeps for 100ms between every command send;
     * 4) Checks that sever response is OK;
     * 5) Closing socket.
     */
    private MockHolder makeCommands(String... commands) throws Exception {
        SimpleClientHandler responseMock = Mockito.mock(SimpleClientHandler.class);
        Client client = new Client("localhost", TEST_PORT, random);

        when(random.nextInt(Short.MAX_VALUE)).thenReturn(1);


        OngoingStubbing<String> ongoingStubbing = when(bufferedReader.readLine());
        for (final String cmd : commands) {
            ongoingStubbing = ongoingStubbing.thenReturn(cmd);
        }

        ongoingStubbing.thenAnswer(invocation -> {
            sleep(200);
            return "quit";
        });

        client.start(new TestChannelInitializer(responseMock), bufferedReader);

        verify(responseMock, times(commands.length)).channelRead(any(), any());
        return new MockHolder(responseMock);
    }

}
