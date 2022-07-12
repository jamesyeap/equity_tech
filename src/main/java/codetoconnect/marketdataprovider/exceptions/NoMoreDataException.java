package codetoconnect.marketdataprovider.exceptions;

public class NoMoreDataException extends MarketDataProviderException {
    private static final String ERROR_MESSAGE = "No more market data!";

    public NoMoreDataException() {
        super(ERROR_MESSAGE);
    }
}