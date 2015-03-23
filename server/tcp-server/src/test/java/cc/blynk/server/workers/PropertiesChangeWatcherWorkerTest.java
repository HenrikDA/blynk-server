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
import java.nio.file.Paths;

import static org.mockito.Mockito.*;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/26/2015.
 */
@RunWith(MockitoJUnitRunner.class)
public class PropertiesChangeWatcherWorkerTest {

    @Mock
    private BaseSimpleChannelInboundHandler fakeHandler;

    @Test
    @Ignore
    public void testPropertiesChanged() throws IOException {
        Path tempDir = Files.createTempDirectory("tmp");
        Path tmpFile = Files.createFile(Paths.get(tempDir.toString(), "temp.txt"));


        System.setProperty("user.dir", tempDir.toString());
        new Thread( new PropertiesChangeWatcherWorker(tmpFile.getFileName().toString(), fakeHandler)).start();


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

        try (OutputStream outputStream = Files.newOutputStream(tmpFile)) {
            outputStream.write(new byte[1]);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //todo find out why it called 2 times
        verify(fakeHandler, timeout(5000).atLeastOnce()).updateProperties(any());

    }

}
