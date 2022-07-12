package codetoconnect.tradingengine.ordergoal;

public class OrderGoal {
    private final Double price;

    private final Integer size;
    public OrderGoal(Double price, Integer size) {
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
