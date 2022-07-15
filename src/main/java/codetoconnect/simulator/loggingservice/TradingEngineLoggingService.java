package codetoconnect.simulator.loggingservice;

import codetoconnect.tradingengine.TradingEngine;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class TradingEngineLoggingService {
    private static final String format = "%s";
    private static Logger logger;
    private static FileHandler fh;

    private static TradingEngineLoggingService loggingService;

    public static void initializeLoggingService() {
        if (loggingService != null) {
            System.out.println("Logging service already initialized!");
            return;
        }

        loggingService = new TradingEngineLoggingService();
    }

    public static void logDecision(String message) {
        loggingService.addMessage(message);
    }

    public static void startLogAt(Long timestamp) {
        String header = String.format("==================== AT %s ms ====================", timestamp);
        loggingService.addMessage(header);
        loggingService.addNewLine();
    }

    public static void endLog() {
        loggingService.addNewLine();
        loggingService.addNewLine();
    }

    TradingEngineLoggingService() {
        this.logger = Logger.getLogger("TradingEngineLogs");
        this.logger.setUseParentHandlers(false);

        try {
            fh = new FileHandler("./TradingEngineLogs.txt");
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

