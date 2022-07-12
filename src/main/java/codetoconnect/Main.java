package codetoconnect;

import codetoconnect.simulator.Simulator;

public class Main {
    public static void main(String[] args) {
        // ========== CONFIGURATION ================================================================================
        String csvFilePath = "../market_data.csv";
        String clientOrder = "54=1; 40=1; 38=50000; 6404=10";
        // =========================================================================================================

        Simulator simulator = new Simulator();

        try {
            simulator.initialiseMarketDataProvider(csvFilePath);
            simulator.initialiseTradingEngine(clientOrder);
            simulator.initialiseSimulatedExchange();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return;
        }

        simulator.execute();
    }
}
