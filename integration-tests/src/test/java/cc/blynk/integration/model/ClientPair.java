package cc.blynk.integration.model;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 2/4/2015.
 */
public class ClientPair {

    public TestClient appClient;

    public TestClient hardwareClient;

    public ClientPair(TestClient appClient, TestClient hardwareClient) {
        this.appClient = appClient;
        this.hardwareClient = hardwareClient;
    }
}
