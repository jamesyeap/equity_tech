package codetoconnect.marketdataprovider.datastreamer;

import codetoconnect.marketdataprovider.exceptions.NoMoreDataException;
import codetoconnect.marketdataprovider.datareader.DataReader;
import codetoconnect.marketdataprovider.exceptions.UnrecognizedDataTypeException;
import codetoconnect.marketdataprovider.marketdata.MarketData;

import java.util.ArrayList;
import java.util.List;

public class DataStreamer {
    private final List<MarketData> marketDataStream;

    private Integer currentPosition;

    private Long lastUpdatedAtTimestamp;

    public DataStreamer() {
        this.marketDataStream = new ArrayList<>();
        this.currentPosition = 0;
        this.lastUpdatedAtTimestamp = 0L;
    }

    public void initializeStreamFromCsv(String filePath) throws UnrecognizedDataTypeException {
        DataReader dataReader = new DataReader();
        dataReader.setFilePath(filePath);

        List<MarketData> marketDataList = dataReader.readAllData();
        this.marketDataStream.addAll(marketDataList);
    }

    public List<MarketData> getLatestMarketData() throws NoMoreDataException {
        List<MarketData> latestMarketData = new ArrayList<>();

        if (this.currentPosition == this.marketDataStream.size()) {
            throw new NoMoreDataException();
        }

        MarketData marketData = marketDataStream.get(currentPosition);
        latestMarketData.add(marketData);
        lastUpdatedAtTimestamp = marketData.getTimestamp();
        currentPosition++;

        while (currentPosition < this.marketDataStream.size()) {
            MarketData nextMarketData = this.marketDataStream.get(currentPosition);

            if (nextMarketData.getTimestamp() > marketData.getTimestamp()) {
                break;
            }

            latestMarketData.add(nextMarketData);
            currentPosition++;
        }

        return latestMarketData;
    }

    public Long getLastUpdatedAtTimestamp() {
        return lastUpdatedAtTimestamp;
    }
}
