package codetoconnect.marketdataprovider.marketdata;

import codetoconnect.marketdataprovider.Trade;

public class TradeData extends MarketData {
    private final Trade trade;

    public TradeData(Long timestamp, Trade trade) {
        super(timestamp);

        this.trade = trade;
    }

    public Trade getTrade() {
        return trade;
    }

    @Override
    public boolean isQuoteData() {
        return false;
    }

    @Override
    public boolean isTradeData() {
        return true;
    }
}
