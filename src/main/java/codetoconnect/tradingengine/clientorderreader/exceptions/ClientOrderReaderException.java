package codetoconnect.tradingengine.clientorderreader.exceptions;

public abstract class ClientOrderReaderException extends Exception {
    public ClientOrderReaderException(String message) {
        super(message);
    }
}
