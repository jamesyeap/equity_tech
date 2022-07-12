package codetoconnect.marketdataprovider.marketdata;

public abstract class MarketData {
    private final Long timestamp;

    public MarketData(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public abstract boolean isQuoteData();

    public abstract boolean isTradeData();
}
