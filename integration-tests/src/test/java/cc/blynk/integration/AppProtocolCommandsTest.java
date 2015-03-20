package cc.blynk.integration;

import cc.blynk.integration.model.MockHolder;
import cc.blynk.integration.model.TestAppClient;
import cc.blynk.server.core.application.AppServer;
import cc.blynk.server.workers.ProfileSaverWorker;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.OngoingStubbing;

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
public class AppProtocolCommandsTest extends IntegrationBase {

    private AppServer appServer;

    @Before
    public void init() throws Exception {
        initServerStructures();

        FileUtils.deleteDirectory(fileManager.getDataDir().toFile());

        appServer = new AppServer(properties, fileManager, userRegistry, sessionsHolder, stats);

        ProfileSaverWorker profileSaverWorker = new ProfileSaverWorker(jedisWrapper, userRegistry, fileManager, properties.getIntProperty("profile.save.worker.period"), stats);
        new Thread(appServer).start();
        new Thread(profileSaverWorker).start();

        //wait util servers start.
        //todo fix.
        Thread.sleep(500);
    }

    @After
    public void shutdown() {
        appServer.stop();
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
        sleep(200);

        makeCommands("login dmitriy@mail.ua 1", "getToken 1").check(OK).check(produce(1, GET_TOKEN, "12345678901234567890123456789012"));

    }

    @Test
    public void testAppNotRegistered() throws Exception {
        makeCommands("login dmitriy@mail.ua 1").check(produce(1, USER_NOT_REGISTERED));
    }


    @Test
    public void testIllegalCommandForHardLoginOnAppChannel() throws Exception {
        makeCommands("login dasdsadasdasdasdasdas").check(produce(1, ILLEGAL_COMMAND));
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
        makeCommands("activate 1").check(produce(1, USER_NOT_AUTHENTICATED));
        makeCommands("deactivate").check(produce(1, USER_NOT_AUTHENTICATED));
        makeCommands("hardware 1 1").check(produce(1, USER_NOT_AUTHENTICATED));
        makeCommands("ping").check(produce(1, USER_NOT_AUTHENTICATED));
    }

    @Test
    public void testPassNotValid() throws Exception {
        makeCommands("register dmitriy@mail.ua 1").check(OK);

        makeCommands("login dmitriy@mail.ua 2").check(produce(1, USER_NOT_AUTHENTICATED));
    }

    @Test
    public void testActivateWrongFormat() throws Exception {
        makeCommands("register dmitriy@mail.ua 1").check(OK);

        makeCommands("login dmitriy@mail.ua 1", "activate ").check(produce(1, ILLEGAL_COMMAND));
    }

    @Test
    public void testActivateWorks() throws Exception {
        String userProfileString = readTestUserProfile();

        makeCommands("register dmitriy@mail.ua 1").check(OK);

        makeCommands("login dmitriy@mail.ua 1", "saveProfile " + userProfileString, "activate 1").check(3, OK);
    }

    @Test
    public void testActivateWrongDashId() throws Exception {
        String userProfileString = readTestUserProfile();

        makeCommands("register dmitriy@mail.ua 1").check(OK);

        makeCommands("login dmitriy@mail.ua 1", "saveProfile " + userProfileString, "activate 2").check(2, OK).check(produce(1, ILLEGAL_COMMAND));
    }


    @Test
    public void testActivateBadId() throws Exception {
        String userProfileString = readTestUserProfile();

        makeCommands("register dmitriy@mail.ua 1").check(OK);

        makeCommands("login dmitriy@mail.ua 1", "saveProfile " + userProfileString, "activate xxx").check(2, OK).check(produce(1, ILLEGAL_COMMAND));
    }

    @Test
    public void testHardwareNotInNetwork() throws Exception {
        String userProfileString = readTestUserProfile();

        makeCommands("register dmitriy@mail.ua 1").check(OK);

        makeCommands("login dmitriy@mail.ua 1", "saveProfile " + userProfileString, "activate 1", "hardware 1 1")
                .check(3, OK).check(produce(1, DEVICE_NOT_IN_NETWORK));
    }

    @Test
    public void testTryHardLoginInAppChannel() throws Exception {
        makeCommands("register dmitriy@mail.ua 1").check(OK);

        makeCommands("login adsadasdasdasdas").check(produce(1, ILLEGAL_COMMAND));
    }


    @Test
    //all commands together cause all operations requires register and then login =(.
    public void testPingDeviceNotInNetwork() throws Exception {
        makeCommands("register dmitriy@mail.ua 1").check(OK);

        makeCommands("login dmitriy@mail.ua 1", "ping").check(OK).check(produce(1, DEVICE_NOT_IN_NETWORK));
    }

    @Test
    public void testNotSslRecordException() throws Exception {
        TestAppClient appClient = new TestAppClient(host, appPort, true);

        //todo mock exception response
        OngoingStubbing<String> ongoingStubbing = when(bufferedReader.readLine());
        ongoingStubbing = ongoingStubbing.thenReturn("register dmitriy@mail.ua 1");
        ongoingStubbing.thenAnswer(invocation -> {
            sleep(400);
            return "quit";
        });

        appClient.start(bufferedReader);

        verify(appClient.responseMock, times(0)).channelRead(any(), any());
    }

    /**
     * 1) Creates client socket;
     * 2) Sends commands;
     * 3) Sleeps for 100ms between every command send;
     * 4) Checks that sever response is OK;
     * 5) Closing socket.
     */
    private MockHolder makeCommands(String... commands) throws Exception {
        TestAppClient appClient = new TestAppClient(host, appPort);

        OngoingStubbing<String> ongoingStubbing = when(bufferedReader.readLine());
        for (String cmd : commands) {
            ongoingStubbing = ongoingStubbing.thenReturn(cmd);
        }

        ongoingStubbing.thenAnswer(invocation -> {
            //todo think how to avoid this
            sleep(400);
            return "quit";
        });

        appClient.start(bufferedReader);

        verify(appClient.responseMock, times(commands.length)).channelRead(any(), any());
        return new MockHolder(appClient.responseMock);
    }

}
