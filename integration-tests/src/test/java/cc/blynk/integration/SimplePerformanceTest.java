package cc.blynk.integration;

import cc.blynk.integration.model.ClientPair;
import cc.blynk.server.Server;
import cc.blynk.server.dao.FileManager;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/2/2015.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class SimplePerformanceTest extends IntegrationBase {

    final AtomicInteger counter = new AtomicInteger();
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
    @Ignore
    public void testConnectAppAndHardware() throws Exception {
        int clientNumber = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        ClientPair[] clients = new ClientPair[clientNumber];
        List<Future<ClientPair>> futures = new ArrayList<>();

        long start = System.currentTimeMillis();
        for (int i = 0; i < clientNumber; i++) {
            Future<ClientPair> future = executorService.submit(
                    () -> initAppAndHardPair("localhost", TEST_PORT, "dima" + counter.incrementAndGet() + "@mail.ua 1")
            );
            futures.add(future);
        }

        int counter = 0;
        for (Future<ClientPair> clientPairFuture : futures) {
            clients[counter++] = clientPairFuture.get();
        }

        System.out.println(clientNumber + " client pairs created in " + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        while (true) {
            for (ClientPair clientPair : clients) {
                clientPair.appClient.sendNoSleep("hardware 1 1");
            }
            sleep(100);
        }
        //System.out.println("1 command sent to all chnnels in " + (System.currentTimeMillis() - start));

        //Thread.sleep(1100000000);

    }

}
