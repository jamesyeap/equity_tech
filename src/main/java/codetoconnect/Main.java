package codetoconnect;

import codetoconnect.simulator.Simulator;

public class Main {

    private static final String USAGE_FORMAT = "java -jar <PROGRAM_NAME> <CLIENT_ORDER> <CSV_RELATIVE_FILE_PATH> (<BATCH_SIZE>)";
    private static final String EXAMPLE_USAGE = "java -jar codetoconnect_equitiestech-1.0-SNAPSHOT.jar \"54=1; 40=1; 38=50000; 6404=10\" ../../market_data.csv 75";

    private static final String ERROR_MESSAGE = String.format(
            "Hey it seems that the parameters are invalid.\n\n" +
                    "See below for usage format and example usage!\n\n" +
                    "[USAGE FORMAT]\n" +
                    "   %s\n" +
                    "[EXAMPLE USAGE]\n" +
                    "   %s\n",
            USAGE_FORMAT, EXAMPLE_USAGE);

    public static void main(String[] args) {
//        /*

        // ========== CONFIGURATION FOR TESTING PHASE ==============================================================
         String clientOrder = "54=1; 40=1; 38=50000; 6404=10";
         String csvFilePath = "../market_data.csv";
         Integer batchSize = null;
        // =========================================================================================================

//        */
         /*

        // ========== INPUT PARAMETERS =============================================================================
        String clientOrder = null;
        String csvFilePath = null;
        Integer batchSize = null;
        // =========================================================================================================

        if (args.length < 2 || args.length > 3) {
            System.out.println(ERROR_MESSAGE);
        }

        if (args.length >= 2) {
            clientOrder = args[0];
            csvFilePath = args[1];
        }
        if (args.length == 3) {
            batchSize = Integer.parseInt(args[2]);
        }

//         */

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
}
