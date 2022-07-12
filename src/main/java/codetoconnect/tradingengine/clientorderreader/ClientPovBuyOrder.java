package codetoconnect.tradingengine.clientorderreader;

public class ClientPovBuyOrder {
    private static final String DEFAULT_SYMBOL = "DBSM.SI";

    public final String symbol;
    public final Integer orderQuantity;
    public final Double targetPercentage;

    public ClientPovBuyOrder(Integer orderQuantity, Double targetPercentage) {
        this.symbol = DEFAULT_SYMBOL;
        this.orderQuantity = orderQuantity;
        this.targetPercentage = targetPercentage;
    }

    public Integer getOrderQuantity() {
        return orderQuantity;
    }

    public Double getTargetPercentage() {
        return targetPercentage;
    }

    @Override
    public String toString() {
        return String.format("Client POV Buy Order: [%d shares with targetPercentage of %f]",
                getOrderQuantity(), getTargetPercentage());
    }
}
