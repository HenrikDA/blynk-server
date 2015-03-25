package cc.blynk.server;

import cc.blynk.common.utils.ParseUtil;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.Properties;

/**
 * Simple class for program arguments parsing.
 *
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 25.03.15.
 */
public class ArgumentsParser {

    private final Options options;

    ArgumentsParser() {
        options = new Options();
        options.addOption("hardPort", true, "Hardware server port.")
               .addOption("appPort", true, "Application server port.")
               .addOption("workerThreads", true, "Server worker threads.")
               .addOption("disableAppSsl", false, "Disables SSL for app mode.");
    }


    void processArguments(String[] args, Properties serverProperties) throws ParseException {
        CommandLine cmd = new BasicParser().parse(options, args);

        String hardPort = cmd.getOptionValue("hardPort");
        String appPort = cmd.getOptionValue("appPort");
        String workerThreadsString = cmd.getOptionValue("workerThreads");

        if (hardPort != null) {
            ParseUtil.parseInt(hardPort);
            serverProperties.put("server.default.port", hardPort);
        }
        if (appPort != null) {
            ParseUtil.parseInt(appPort);
            serverProperties.put("server.ssl.port", appPort);
        }
        if (workerThreadsString != null) {
            ParseUtil.parseInt(workerThreadsString);
            serverProperties.put("server.worker.threads", workerThreadsString);
        }
    }

}
