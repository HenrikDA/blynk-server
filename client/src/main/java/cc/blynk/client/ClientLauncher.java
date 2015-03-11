package cc.blynk.client;

import cc.blynk.client.core.AppClient;
import cc.blynk.client.core.BaseClient;
import cc.blynk.client.core.HardwareClient;
import cc.blynk.client.enums.ClientMode;
import cc.blynk.common.utils.ParseUtil;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 11.03.15.
 */
public class ClientLauncher {

    protected static final String DEFAULT_HOST = "localhost";
    protected static final int DEFAULT_HARDWARE_PORT = 8442;
    protected static final int DEFAULT_APPLICATION_SSL_PORT = 8443;

    private static final Options options = new Options();

    static {
        options.addOption("host", true, "Server host or ip.")
               .addOption("hardPort", true, "Hardware server port.")
               .addOption("appPort", true, "Application server ssl port.")
               .addOption("mode", true, "Client mode. 'hardware' or 'app'.");
    }

    public static void main(String[] args) throws ParseException {
        CommandLine cmd = new BasicParser().parse(options, args);

        String host = cmd.getOptionValue("host", DEFAULT_HOST);
        int hardPort = ParseUtil.parseInt(cmd.getOptionValue("hardPort", String.valueOf(DEFAULT_HARDWARE_PORT)));
        int appPort = ParseUtil.parseInt(cmd.getOptionValue("appPort", String.valueOf(DEFAULT_APPLICATION_SSL_PORT)));
        ClientMode mode = ClientMode.parse(cmd.getOptionValue("mode", ClientMode.HARDWARE.name()));

        BaseClient baseClient = mode == ClientMode.APP ? new AppClient(host, appPort) : new HardwareClient(host, hardPort);

        baseClient.start(new BufferedReader(new InputStreamReader(System.in)));
    }

}
