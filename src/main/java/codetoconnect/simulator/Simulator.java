package codetoconnect.simulator;

import codetoconnect.marketdataprovider.MarketDataProvider;
import codetoconnect.marketdataprovider.exceptions.UnrecognizedDataTypeException;
import codetoconnect.simulatedexchange.SimulatedExchange;
import codetoconnect.tradingengine.TradingEngine;
import codetoconnect.tradingengine.clientorderreader.exceptions.ClientOrderReaderException;

public class Simulator {

    private MarketDataProvider marketDataProvider;
    private TradingEngine tradingEngine;
    private SimulatedExchange simulatedExchange;

    public Simulator() { }

    public void execute() {
        if (!allComponentsInitialized()) {
            System.out.println("Not all components initialized yet");
            return;
        }

        boolean simulationEnded = false;
        while (true) {
            simulationEnded = marketDataProvider.execute();
            if (simulationEnded) { break; }

            simulationEnded = tradingEngine.execute();
            if (simulationEnded) { break; }

            simulationEnded = simulatedExchange.execute();
            if (simulationEnded) { break; }
        }

        printEndOfSimulationSummary();
    }

    public void initialiseMarketDataProvider(String csvFilePath)
            throws UnrecognizedDataTypeException {
        this.marketDataProvider = MarketDataProvider.fromCsvFile(csvFilePath);
    }

    public void initialiseTradingEngine(String clientOrder)
            throws ClientOrderReaderException {
        this.tradingEngine = TradingEngine.initialize(clientOrder, this);
    }

    public void initialiseTradingEngineWithBatchSize(String clientOrder, Integer batchSize)
            throws ClientOrderReaderException {
        initialiseTradingEngine(clientOrder);
        this.tradingEngine.setBatchSize(batchSize);
    }

    public void initialiseSimulatedExchange() {
        this.simulatedExchange = new SimulatedExchange(this);
    }

    public MarketDataProvider getMarketDataProvider() {
        return this.marketDataProvider;
    }

    public Long getTimestamp() {
        return this.marketDataProvider.getLastUpdatedAtTimestamp();
    }

    public TradingEngine getTradingEngine() {
        return this.tradingEngine;
    }

    public SimulatedExchange getSimulatedExchange() {
        return this.simulatedExchange;
    }

    private void printEndOfSimulationSummary() {
        this.simulatedExchange.printEndOfSimulationSummary();
        System.out.println();
        this.tradingEngine.printEndOfSimulationSummary();
    }

    private boolean allComponentsInitialized() {
        return (this.tradingEngine != null) &&
                (this.simulatedExchange != null);
    }
}
