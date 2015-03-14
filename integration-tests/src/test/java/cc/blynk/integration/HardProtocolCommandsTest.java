package cc.blynk.integration;

import cc.blynk.integration.model.MockHolder;
import cc.blynk.integration.model.TestHardClient;
import cc.blynk.server.core.hardware.HardwareServer;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.OngoingStubbing;

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
public class HardProtocolCommandsTest extends IntegrationBase {

    private HardwareServer hardwareServer;

    @Before
    public void init() throws Exception {
        initServerStructures();

        FileUtils.deleteDirectory(fileManager.getDataDir().toFile());

        hardwareServer = new HardwareServer(properties, fileManager, userRegistry, sessionsHolder, stats);

        new Thread(hardwareServer).start();

        //wait util servers start.
        //todo fix.
        Thread.sleep(500);
    }

    @After
    public void shutdown() {
        hardwareServer.stop();
    }

    @Test
    public void testInvalidHardwareTokenException() throws Exception {
        makeCommands("login 123").check(produce(1, INVALID_TOKEN));
    }

    @Test
    public void testInvalidCommandAppLoginOnHardChannel() throws Exception {
        makeCommands("login dima@dima.ua 1").check(produce(1, ILLEGAL_COMMAND));
    }

    @Test
    public void testNoRegisterHandlerNoResponse() throws Exception {
        TestHardClient hardClient = new TestHardClient(host, hardPort);
        OngoingStubbing<String> ongoingStubbing = when(bufferedReader.readLine()).thenReturn("register dima@dima.ua 1");
        ongoingStubbing.thenAnswer(invocation -> {
            //todo think how to avoid this
            sleep(400);
            return "quit";
        });
        hardClient.start(bufferedReader);

        verify(hardClient.responseMock, never()).channelRead(any(), any());
    }

    @Test
    public void tryTweetWithNoLoggedUser() throws Exception {
        makeCommands("tweet bla").check(produce(1, USER_NOT_AUTHENTICATED));
    }

    @Test
    public void tryHardwareWithNoLoggedUser() throws Exception {
        makeCommands("hardware 1 1").check(produce(1, USER_NOT_AUTHENTICATED));
    }

    @Test
    @Ignore
    //todo finish and fix.
    public void testInvalidTweetBody() throws Exception {
        makeCommands("register dmitriy@mail.ua 1").check(OK);

        makeCommands("login dmitriy@mail.ua 1", "tweet").check(OK).check(produce(1, TWEET_BODY_INVALID_EXCEPTION));
    }

    /**
     * 1) Creates client socket;
     * 2) Sends commands;
     * 3) Sleeps for 100ms between every command send;
     * 4) Checks that sever response is OK;
     * 5) Closing socket.
     */
    private MockHolder makeCommands(String... commands) throws Exception {
        TestHardClient hardClient = new TestHardClient(host, hardPort);

        OngoingStubbing<String> ongoingStubbing = when(bufferedReader.readLine());
        for (String cmd : commands) {
            ongoingStubbing = ongoingStubbing.thenReturn(cmd);
        }

        ongoingStubbing.thenAnswer(invocation -> {
            //todo think how to avoid this
            sleep(400);
            return "quit";
        });

        hardClient.start(bufferedReader);

        verify(hardClient.responseMock, times(commands.length)).channelRead(any(), any());
        return new MockHolder(hardClient.responseMock);
    }

}
