package codetoconnect.simulator.loggingservice;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class SimulatedExchangeLoggingService {
    private static final String format = "%s";
    private static Logger logger;
    private static FileHandler fh;

    private static SimulatedExchangeLoggingService loggingService;

    public static void initializeLoggingService() {
        if (loggingService != null) {
            System.out.println("Logging service already initialized!");
            return;
        }

        loggingService = new SimulatedExchangeLoggingService();
    }

    public static void logResponse(String message) {
        loggingService.addMessage(message);
        loggingService.addNewLine();
    }

    public static void startLog() {
        loggingService.addNewLine();
    }

    public static void endLogAt(Long timestamp) {
        String footer = String.format("-------------------- END OF UPDATES (AT %s ms) --------------------",
                timestamp);
        loggingService.addMessage(footer);
        loggingService.addNewLine();
        loggingService.addNewLine();
    }

    SimulatedExchangeLoggingService() {
        this.logger = Logger.getLogger("SimulatedExchangeLogs");
        this.logger.setUseParentHandlers(false);

        try {
            fh = new FileHandler("./SimulatedExchangeLogs.txt");
            fh.setFormatter(new SimpleFormatter() {
                @Override
                public synchronized String format(LogRecord lr) {
                    return String.format(format, lr.getMessage());
                }
            });

            logger.addHandler(fh);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addMessage(String message) {
        logger.info(message);
    }

    public void addNewLine() {
        logger.info("\n");
    }
}

