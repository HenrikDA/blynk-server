package cc.blynk.server.workers;

import cc.blynk.server.handlers.workflow.BaseSimpleChannelInboundHandler;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/26/2015.
 */
@RunWith(MockitoJUnitRunner.class)
//todo this is ugly test but I don't have time to make it better/simpler/easier
public class PropertiesChangeWatcherWorkerTest {

    @Mock
    private BaseSimpleChannelInboundHandler fakeHandler;

    private PropertiesChangeWatcherWorker propertiesChangeWatcherWorker;

    @Test(expected = RuntimeException.class)
    @Ignore("some problem with codeship envirnoment, it fails this test")
    //todo fix
    public void testPropertiesChanged() throws IOException {
        Path tmpFile = Files.createTempFile("", "");

        doThrow(new RuntimeException()).when(fakeHandler).updateProperties(any());

        System.setProperty("user.dir", System.getProperty("java.io.tmpdir"));
        propertiesChangeWatcherWorker = new PropertiesChangeWatcherWorker(tmpFile.getFileName().toString(), fakeHandler);
        new Thread(new TmpFileChanger(tmpFile)).start();
        propertiesChangeWatcherWorker.run();

    }

    private class TmpFileChanger implements Runnable {
        private Path tmpFilePath;
        private TmpFileChanger(Path tmpFilePath) {
            this.tmpFilePath = tmpFilePath;
        }

        @Override
        public void run() {
            try (OutputStream outputStream = Files.newOutputStream(tmpFilePath)) {
                Thread.sleep(200);
                outputStream.write(new byte[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
