package codetoconnect.tradingengine.clientorderreader.exceptions;

public class InvalidFixTagException extends ClientOrderReaderException {
    private static final String ERROR_MESSAGE = "Invalid FIX tag: ";

    public InvalidFixTagException(String invalidFixTag) {
        super(String.format("%s %s", ERROR_MESSAGE, invalidFixTag));
    }
}
