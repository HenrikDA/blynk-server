package cc.blynk.integration;

import cc.blynk.common.model.messages.Message;
import cc.blynk.common.stats.GlobalStats;
import cc.blynk.common.utils.ServerProperties;
import cc.blynk.integration.model.ClientPair;
import cc.blynk.integration.model.SimpleClientHandler;
import cc.blynk.integration.model.TestChannelInitializer;
import cc.blynk.integration.model.TestClient;
import cc.blynk.server.dao.FileManager;
import cc.blynk.server.dao.JedisWrapper;
import cc.blynk.server.dao.SessionsHolder;
import cc.blynk.server.dao.UserRegistry;
import cc.blynk.server.model.UserProfile;
import cc.blynk.server.utils.JsonParser;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

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

    @Mock
    public Random random;

    @Mock
    public Random random2;

    @Mock
    public BufferedReader bufferedReader;

    @Mock
    public BufferedReader bufferedReader2;

    public ServerProperties properties = new ServerProperties();

    public FileManager fileManager;
    public SessionsHolder sessionsHolder;
    public UserRegistry userRegistry;
    public GlobalStats stats;
    public JedisWrapper jedisWrapper;

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

        String userProfileString = readTestUserProfile();

        appClient.send("register " + user)
                .send("login " + user)
                .send("saveProfile " + userProfileString)
                .send("getToken 1");

        ArgumentCaptor<Object> objectArgumentCaptor = ArgumentCaptor.forClass(Object.class);
        verify(appResponseMock, timeout(2000).times(4)).channelRead(any(), objectArgumentCaptor.capture());

        List<Object> arguments = objectArgumentCaptor.getAllValues();
        Message getTokenMessage = (Message) arguments.get(3);
        String token = getTokenMessage.body;

        hardClient.send("login " + token);
        verify(hardResponseMock, timeout(2000)).channelRead(any(), eq(produce(1, OK)));

        reset(hardResponseMock);
        reset(appResponseMock);
        appClient.reset();
        hardClient.reset();

        return new ClientPair(appClient, hardClient);
    }

    public static String readTestUserProfile(String fileName) {
        InputStream is = IntegrationBase.class.getResourceAsStream("/json_test/" + fileName);
        UserProfile userProfile = JsonParser.parseProfile(is);
        return userProfile.toString();
    }

    public static String readTestUserProfile() {
        return readTestUserProfile("user_profile_json.txt");
    }

    public void initServerStructures() {
        fileManager = new FileManager(properties.getProperty("data.folder"));
        sessionsHolder = new SessionsHolder();
        userRegistry = new UserRegistry(fileManager.deserialize());
        stats = new GlobalStats();
        jedisWrapper = new JedisWrapper(properties);
    }


}
