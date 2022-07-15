package codetoconnect.marketdataprovider;

import codetoconnect.marketdataprovider.orderbook.Bid;
import codetoconnect.marketdataprovider.datastreamer.DataStreamer;
import codetoconnect.marketdataprovider.exceptions.NoMoreDataException;
import codetoconnect.marketdataprovider.exceptions.UnrecognizedDataTypeException;
import codetoconnect.marketdataprovider.marketdata.MarketData;
import codetoconnect.marketdataprovider.marketdata.QuoteData;
import codetoconnect.marketdataprovider.marketdata.TradeData;
import codetoconnect.marketdataprovider.orderbook.Ask;
import codetoconnect.marketdataprovider.orderbook.OrderBook;
import codetoconnect.simulator.SimulatorComponent;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MarketDataProvider implements SimulatorComponent {

    private DataStreamer dataStreamer;
    private OrderBook orderBook;

    private Integer marketTradedVolume;

    public static MarketDataProvider fromCsvFile(String csvFilePath) throws UnrecognizedDataTypeException {
        MarketDataProvider marketDataProvider = new MarketDataProvider();
        marketDataProvider.initializeDataStreamer(csvFilePath);

        return marketDataProvider;
    }

    MarketDataProvider() {
        orderBook = new OrderBook();
        this.marketTradedVolume = 0;
    }

    @Override
    public boolean execute() {
        boolean marketClosed = false;
        try {
            update();
        } catch (NoMoreDataException e) {
            marketClosed = true;
        }

        return marketClosed;
    }

    private void initializeDataStreamer(String filePath) throws UnrecognizedDataTypeException {
        this.dataStreamer = new DataStreamer();
        dataStreamer.initializeStreamFromCsv(filePath);
    }

    public void update() throws NoMoreDataException {
        List<MarketData> incomingMarketData = dataStreamer.getLatestMarketData();
        for (MarketData marketData : incomingMarketData) {
            if (marketData.isQuoteData()) {
                QuoteData quoteData = (QuoteData) marketData;
                updateOrderBook(quoteData);
            } else {
                TradeData tradeData = (TradeData) marketData;
                updateMarketTradedVolume(tradeData);
            }
        }

        printLastUpdatedAtTimestampHeader();
        printKeyStatistics();
    }

    public List<Bid> getBids() {
        List<Bid> bids = this.orderBook.getBids();
        sortBidsFromHighestToLowest(bids);

        return bids;
    }

    public List<Ask> getAsks() {
        List<Ask> asks = this.orderBook.getAsks();
        sortAsksFromLowestToHighest(asks);

        return asks;
    }

    public Ask getBestAsk() {
        return getAsks().get(0);
    }

    public Bid getBestBid() {
        return getBids().get(0);
    }

    public Integer getMarketTradedVolume() {
        return this.marketTradedVolume;
    }

    public Long getLastUpdatedAtTimestamp() {
        return dataStreamer.getLastUpdatedAtTimestamp();
    }

    private void updateOrderBook(QuoteData quoteData) {
        this.orderBook.update(quoteData);
    }

    private void updateMarketTradedVolume(TradeData tradeData) {
        Integer additionalVolume = tradeData.getTrade().getSize();
        marketTradedVolume += additionalVolume;
    }

    private void sortBidsFromHighestToLowest(List<Bid> bids) {
        bids.sort(Comparator.comparing(Bid::getPrice));
        Collections.reverse(bids);
    }

    private void sortAsksFromLowestToHighest(List<Ask> asks) {
        asks.sort(Comparator.comparing(Ask::getPrice));
    }

    private void printLastUpdatedAtTimestampHeader() {
        System.out.format(
                "===========================================================================================\n" +
                "=============================================== RECEIVED MARKET UPDATE AT %s ms ======\n",
                getLastUpdatedAtTimestamp());
    }

    private void printKeyStatistics() {
        System.out.format(
                "==================================================== BEST-BID: %s ======================\n" +
                "==================================================== BEST-ASK: %s ======================\n" +
                "==================================================== MARKET-TRADED VOLUME: %s =======\n" +
                "===========================================================================================\n",
                getBestBid().getPrice(),
                getBestAsk().getPrice(),
                getMarketTradedVolume()
        );
    }
}
