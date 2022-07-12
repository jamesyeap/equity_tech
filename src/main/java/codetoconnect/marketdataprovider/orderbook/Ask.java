package codetoconnect.marketdataprovider.orderbook;

public class Ask {
    private final Double price;

    private final Integer size;

    public Ask(Double price, Integer size) {
        this.price = price;
        this.size = size;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getSize() {
        return size;
    }
}
