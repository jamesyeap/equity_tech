package codetoconnect.marketdataprovider.exceptions;

public abstract class MarketDataProviderException extends Exception {
    public MarketDataProviderException(String message) {
        super(message);
    }
}
