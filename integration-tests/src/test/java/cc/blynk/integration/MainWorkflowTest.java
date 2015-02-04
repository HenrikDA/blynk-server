package cc.blynk.integration;

import cc.blynk.client.Client;
import cc.blynk.server.Server;
import cc.blynk.server.utils.FileManager;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.OngoingStubbing;

import static cc.blynk.common.enums.Response.OK;
import static cc.blynk.common.model.messages.MessageFactory.produce;
import static org.mockito.Mockito.when;

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
    public void testHardwareInNetwork() throws Exception {
        makeCommand(1, produce(1, OK), "register dmitriy@mail.ua 1", "quit");


        int[] msgIds = new int[] {1, 2};
        String[] commands = new String[] {"login dmitriy@mail.ua 1", "hardware 1 1", "quit"};

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

        //verify(responseMock, times(responseMessages.length)).channelRead(any(), any());
        //for (MessageBase messageBase : responseMessages) {
        //    verify(responseMock).channelRead(any(), eq(messageBase));
        //}

    }

}
