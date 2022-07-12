package codetoconnect.marketdataprovider.datareader;

import codetoconnect.marketdataprovider.Trade;
import codetoconnect.marketdataprovider.exceptions.UnrecognizedDataTypeException;
import codetoconnect.marketdataprovider.marketdata.MarketData;
import codetoconnect.marketdataprovider.marketdata.QuoteData;
import codetoconnect.marketdataprovider.marketdata.TradeData;
import codetoconnect.marketdataprovider.orderbook.Ask;
import codetoconnect.marketdataprovider.orderbook.Bid;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataReader {
    private static final String DELIMITER = ",";
    private static final String QUOTE_DATA_TYPE = "Q";
    private static final String TRADE_DATA_TYPE = "T";

    private BufferedReader bufferedReader;

    public DataReader() { }

    public void setFilePath(String filePath) {
        try {
            this.bufferedReader = new BufferedReader(new FileReader(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<MarketData> readAllData() throws UnrecognizedDataTypeException {
        List<MarketData> allMarketData = new ArrayList<>();

        String nextLine;
        while ((nextLine = readData()) != null) {
            MarketData marketData = processData(nextLine);
            allMarketData.add(marketData);
        }

        return allMarketData;
    }

    private String readData() {
        String nextLine = null;

        try {
            nextLine = this.bufferedReader.readLine();

            if (nextLine == null) {
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return nextLine;
    }

    private MarketData processData(String line)
            throws UnrecognizedDataTypeException {
        String[] data = line.split(DELIMITER);

        if (isQuoteData(data)) {
            return parseQuotes(data);
        } else if (isTradeData(data)) {
            return parseTrade(data);
        } else {
            throw new UnrecognizedDataTypeException();
        }
    }

    private boolean isQuoteData(String[] data) {
        return data[0].equals(QUOTE_DATA_TYPE);
    }

    private boolean isTradeData(String[] data) {
        return data[0].equals(TRADE_DATA_TYPE);
    }

    private QuoteData parseQuotes(String[] data) {
        Long timestamp = Long.parseLong(data[1]);
        List<Bid> bids = parseBids(data[2]);
        List<Ask> asks = parseAsks(data[3]);

        QuoteData quotes = new QuoteData(timestamp, bids, asks);

        return quotes;
    }

    private TradeData parseTrade(String[] data) {
        Long timestamp = Long.parseLong(data[1]);

        Double price = Double.parseDouble(data[2]);
        Integer size = Integer.parseInt(data[3]);
        Trade trade = new Trade(price, size);

        TradeData tradeData = new TradeData(timestamp, trade);

        return tradeData;
    }

    private List<Bid> parseBids(String bidsString) {
        String[] bidsData = bidsString.split(" ");
        List<Bid> bids = new ArrayList<>(3);

        int len = bidsData.length;
        boolean isPrice = true;

        Double price = 0.0;
        Integer size = 0;

        for (int i = 0; i < len; i++) {
            if (isPrice) {
                price = Double.parseDouble(bidsData[i]);
            } else {
                size = Integer.parseInt(bidsData[i]);
                bids.add(new Bid(price, size));
            }

            isPrice = !isPrice;
        }

        return bids;
    }

    private List<Ask> parseAsks(String asksString) {
        String[] asksData = asksString.split(" ");
        List<Ask> asks = new ArrayList<>(3);

        int len = asksData.length;
        boolean isPrice = true;

        Double price = 0.0;
        Integer size = 0;

        for (int i = 0; i < len; i++) {
            if (isPrice) {
                price = Double.parseDouble(asksData[i]);
            } else {
                size = Integer.parseInt(asksData[i]);
                asks.add(new Ask(price, size));
            }

            isPrice = !isPrice;
        }

        return asks;
    }
}
