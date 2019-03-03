import cli.CommandLineParser;
import config.AutoRSSConfigurator;
import poller.Poller;
import util.Log;

import javax.xml.bind.ValidationException;
import java.util.Scanner;

/**
 * Main running thread
 */
public class Main {
    private static Log log = new Log(Main.class.getName(), System.out);

    /**
     * Initializations, Config reading and running in 3 threads:
     * shutdown hook for saving config, polling for the whole work and CLI parsing and managing.
     *
     * Note: Always tries to save configuration on shutdown.
     *      Delete config file to run application from scratch
     *
     * @param args not in use
     */
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
