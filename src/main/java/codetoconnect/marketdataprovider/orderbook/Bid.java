package codetoconnect.marketdataprovider.orderbook;

public class Bid {
    private final Double price;

    private final Integer size;

    public Bid(Double price, Integer size) {
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
