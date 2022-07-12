package codetoconnect.marketdataprovider.orderbook;

import codetoconnect.marketdataprovider.marketdata.QuoteData;

import java.util.ArrayList;
import java.util.List;

public class OrderBook {

    private List<Bid> bids;
    private List<Ask> asks;

    public OrderBook() {
        this.bids = new ArrayList<>();
        this.asks = new ArrayList<>();
    }

    public void update(QuoteData quoteData) {
        this.bids = quoteData.getBids();
        this.asks = quoteData.getAsks();
    }

    public List<Bid> getBids() {
        List<Bid> copy = new ArrayList<>(bids);
        return copy;
    }

    public List<Ask> getAsks() {
        List<Ask> copy = new ArrayList<>(asks);
        return copy;
    }
}
