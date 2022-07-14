package codetoconnect;

import codetoconnect.simulator.Simulator;
import codetoconnect.tradingengine.clientorderreader.ClientOrderReader;
import codetoconnect.tradingengine.clientorderreader.ClientPovBuyOrder;
import codetoconnect.tradingengine.clientorderreader.exceptions.InvalidFixTagException;
import codetoconnect.tradingengine.clientorderreader.exceptions.MissingFixTagException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // ========== INPUT PARAMETERS =============================================================================
        ClientPovBuyOrder clientOrder = null;
        String csvFilePath = null;
        Integer batchSize = null;
        // =========================================================================================================

        // get input from client
        Scanner scanner = new Scanner(System.in);
        try {
            while (true) {
                clientOrder = getClientOrderInput(scanner);
                csvFilePath = getCsvFilePathInput(scanner);
                if (needsCustomBatchSize(scanner)) {
                    batchSize = getBatchSizeInput(scanner);
                }

                break;
            }
        } catch (IllegalStateException | NoSuchElementException e) {
            System.out.println("Exiting...");
        }

        Simulator simulator = new Simulator();

        try {
            simulator.initialiseMarketDataProvider(csvFilePath);

            if (batchSizeIsSpecified(batchSize)) {
                simulator.initialiseTradingEngineWithBatchSize(clientOrder, batchSize);
            } else {
                simulator.initialiseTradingEngine(clientOrder);
            }

            simulator.initialiseSimulatedExchange();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        simulator.execute();
    }

    private static boolean batchSizeIsSpecified(Integer batchSize) {
        return batchSize != null;
    }

    private static ClientPovBuyOrder getClientOrderInput(Scanner scanner) {
        ClientPovBuyOrder clientOrder = null;
        boolean clientInputIsValid = false;

        while (!clientInputIsValid) {
            System.out.println("Please enter your POV buy-order in FIX format:");
            System.out.println("[EXAMPLE INPUT]: 54=1; 40=1; 38=50000; 6404=10");
            System.out.format("[YOUR INPUT]: ");
            String input = scanner.nextLine();

            try {
                clientOrder = ClientOrderReader.readFromInput(input);
                clientInputIsValid = true;
            } catch (InvalidFixTagException | MissingFixTagException e) {
                System.out.print(e.getMessage());
            }

            System.out.println();
        }

        return clientOrder;
    }

    private static String getCsvFilePathInput(Scanner scanner) {
        boolean clientInputIsValid = false;
        String csvFilePathInput = null;

        while (!clientInputIsValid) {
            try {
                System.out.println("Please enter the relative file-path of your CSV file");
                System.out.println("[EXAMPLE INPUT]: ./input/market_data.csv");
                System.out.format("[YOUR INPUT]: ");
                csvFilePathInput = scanner.nextLine();

                new FileReader(csvFilePathInput);

                clientInputIsValid = true;
            } catch (FileNotFoundException e) {
                System.out.println("Oops, I could not find that file. Can you enter a valid file-path?");
            }

            System.out.println();
        }

        return csvFilePathInput;
    }

    private static boolean needsCustomBatchSize(Scanner scanner) {
        String clientDecision = getClientDecision(scanner);

        if (clientDecision.equalsIgnoreCase("y")) {
            return true;
        } else {
            return false;
        }
    }

    private static String getClientDecision(Scanner scanner) {
        String clientResponse = null;

        boolean clientResponseIsValid = false;
        while (!clientResponseIsValid) {
            System.out.println("Would you like to specify a custom batch size? (y/n)");
            System.out.format("[YOUR INPUT]: ");
            clientResponse = scanner.nextLine();

            if (clientResponse.equalsIgnoreCase("y") | clientResponse.equalsIgnoreCase("n")) {
                clientResponseIsValid = true;
            } else {
                System.out.println("I couldn't understand that, please enter a valid response!");
            }

            System.out.println();
        }

        return clientResponse;
    }

    private static Integer getBatchSizeInput(Scanner scanner) {
        Integer batchSize = null;
        boolean clientResponseIsValid = false;

        while (!clientResponseIsValid) {
            System.out.println("Please enter a batch size (in number of shares)");
            System.out.println("[EXAMPLE INPUT]: 500");
            System.out.format("[YOUR INPUT]: ");
            String batchSizeString = scanner.nextLine();

            try {
                batchSize = Integer.parseInt(batchSizeString);
                clientResponseIsValid = true;
            } catch (NumberFormatException e) {
                System.out.println("Oops, that does not seem to be a number. Please try entering a number again.");
            }

            System.out.println();
        }

        return batchSize;
    }
}
