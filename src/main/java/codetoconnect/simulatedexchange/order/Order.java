package codetoconnect.simulatedexchange.order;

public class Order {

    private static Long idCounter = 0L;

    private final Long id;

    private final Long placedAtTimestamp;

    private final Double price;

    private final Integer size;
    public Order(Long placedAtTimestamp, Double price, Integer size) {
        this.id = idCounter;
        idCounter++;

        this.placedAtTimestamp = placedAtTimestamp;
        this.price = price;
        this.size = size;
    }

    public Long getPlacedAtTimestamp() {
        return placedAtTimestamp;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Order)) {
            return false;
        }

        Order otherOrder = (Order) o;

        return this.id.equals(otherOrder.id);
    }

    @Override
    public String toString() {
        return String.format("%s@%s", size, price);
    }
}
