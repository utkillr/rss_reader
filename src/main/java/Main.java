import cli.CommandLineParser;
import config.AutoRSSConfigurator;
import poller.Poller;
import util.Log;

import javax.xml.bind.ValidationException;
import java.util.Scanner;

public class Main {
    private static Log log = new Log(Main.class.getName(), System.out);

    public static void main(String[] args) {

        AutoRSSConfigurator.loadRSSConfiguration();

        // Initialize CLI
        CommandLineParser cli = new CommandLineParser();
        Scanner scanner = new Scanner(System.in);
        log.info("Use 'help' for help");

        // Initialize thread and start it
        Poller poller = new Poller();
        Thread pollingThread = new Thread(poller, "Poller");
        pollingThread.start();

        // Save in case of external shutdown
        Runtime.getRuntime().addShutdownHook(
                new Thread(() -> {
                    log.warn("Trying to save configuration");
                    poller.stop();
                    AutoRSSConfigurator.saveRSSConfigurationOnShutdown();
                    log.warn("Configuration saved");
                })
        );

        while (true) {
            try {
                log.emptyLine();
                int result = cli.parse(scanner.nextLine());
                // Graceful thread stop
                if (result == 1) {
                    poller.stop();
                    break;
                }
            } catch (IllegalArgumentException | ValidationException e) {
                log.error(e.getMessage());
            }
        }

        try {
            // Graceful shutdown
            log.info("Waiting for polling thread to stop...");
            pollingThread.join();
        } catch (InterruptedException e) {
            log.error("Waiting for thread is interrupted: " + e.getMessage());
        }

        AutoRSSConfigurator.saveRSSConfigurationOnShutdown();
    }
}
