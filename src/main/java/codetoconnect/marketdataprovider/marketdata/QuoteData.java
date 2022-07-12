package codetoconnect.marketdataprovider.marketdata;

import codetoconnect.marketdataprovider.orderbook.Ask;
import codetoconnect.marketdataprovider.orderbook.Bid;

import java.util.List;

public class QuoteData extends MarketData {
    private final List<Bid> bids;
    private final List<Ask> asks;

    public QuoteData(Long timestamp, List<Bid> bids, List<Ask> asks) {
        super(timestamp);

        this.bids = bids;
        this.asks = asks;
    }

    public List<Bid> getBids() {
        return bids;
    }

    public List<Ask> getAsks() {
        return asks;
    }

    @Override
    public boolean isQuoteData() {
        return true;
    }

    @Override
    public boolean isTradeData() {
        return false;
    }
}
