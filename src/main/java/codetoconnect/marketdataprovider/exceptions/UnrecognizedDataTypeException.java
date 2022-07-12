package codetoconnect.marketdataprovider.exceptions;

public class UnrecognizedDataTypeException extends MarketDataProviderException {
    private static final String ERROR_MESSAGE = "Unrecognized data type! Data type must be either Q or T";

    public UnrecognizedDataTypeException() {
        super(ERROR_MESSAGE);
    }
}
