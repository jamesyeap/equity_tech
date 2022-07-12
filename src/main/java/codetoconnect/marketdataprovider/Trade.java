package codetoconnect.marketdataprovider;

public class Trade {
    private final Double price;

    private final Integer size;

    public Trade(Double price, Integer size) {
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
