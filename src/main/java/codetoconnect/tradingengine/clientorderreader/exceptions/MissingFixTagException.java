package codetoconnect.tradingengine.clientorderreader.exceptions;

public class MissingFixTagException extends ClientOrderReaderException {
    private static final String ERROR_MESSAGE = "Invalid FIX input! Missing FIX tag: ";

    public MissingFixTagException(String missingFixTag) {
        super(String.format("%s %s", ERROR_MESSAGE, missingFixTag));
    }
}