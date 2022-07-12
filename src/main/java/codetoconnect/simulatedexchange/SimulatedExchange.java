package codetoconnect.simulatedexchange;

import codetoconnect.marketdataprovider.MarketDataProvider;
import codetoconnect.marketdataprovider.orderbook.Ask;
import codetoconnect.marketdataprovider.orderbook.Bid;
import codetoconnect.simulatedexchange.order.Order;
import codetoconnect.simulator.Simulator;

import java.util.ArrayList;
import java.util.List;

public class SimulatedExchange {

    private static final Long NEAR_TOUCH_MARKETABLE_AGE = 180000L;
    private final Simulator simulator;

    private final List<Order> openOrders;
    private final List<Order> filledOrders;
    private final List<Order> cancelledOrders;

    private Integer cumulativeExecutedShares;

    public SimulatedExchange(Simulator simulator) {
        this.openOrders = new ArrayList<>();
        this.filledOrders = new ArrayList<>();
        this.cancelledOrders = new ArrayList<>();
        this.cumulativeExecutedShares = 0;

        this.simulator = simulator;
    }

    public boolean execute() {
        boolean endSimulation = false;

        for (Order order : getOpenOrders()) {
            if (isMarketable(order)) {
                fillOpenOrder(order);
            }
        }

        return endSimulation;
    }

    public void receiveOrder(Order order) {
        if (atBestAskOrBetter(order)) {
            fillOrder(order);
        } else {
            queueOrder(order);
        }
    }

    public void cancelOrder(Order order) {
        this.openOrders.remove(order);
        this.cancelledOrders.add(order);
    }

    public List<Order> getOpenOrders() {
        List<Order> copy = new ArrayList<>(openOrders);
        return copy;
    }

    private boolean isMarketable(Order order) {
        if (atBestAskOrBetter(order)) {
            return true;
        }

        if (atBestBidOrBetter(order) && getOrderAge(order) >= NEAR_TOUCH_MARKETABLE_AGE) {
            return true;
        }

        return false;
    }

    private boolean atBestAskOrBetter(Order order) {
        return (order.getPrice() >= getBestAsk().getPrice());
    }

    private boolean atBestBidOrBetter(Order order) {
        return (order.getPrice() >= getBestBid().getPrice());
    }

    private void fillOpenOrder(Order order) {
        fillOrder(order);
        openOrders.remove(order);
    }

    private void fillOrder(Order order) {
        this.filledOrders.add(order);
        this.cumulativeExecutedShares += order.getSize();

        System.out.format("Filled %s; Cumulative Quantity: %d\n", order, cumulativeExecutedShares);
    }

    public Integer getCumulativeExecutedShares() {
        return cumulativeExecutedShares;
    }

    private void queueOrder(Order order) {
        this.openOrders.add(order);
    }

    private Long getOrderAge(Order order) {
        MarketDataProvider marketDataProvider = this.simulator.getMarketDataProvider();
        Long marketTimestamp = marketDataProvider.getLastUpdatedAtTimestamp();

        return marketTimestamp - order.getPlacedAtTimestamp();
    }

    private Ask getBestAsk() {
        MarketDataProvider marketDataProvider = this.simulator.getMarketDataProvider();
        return marketDataProvider.getBestAsk();
    }

    private Bid getBestBid() {
        MarketDataProvider marketDataProvider = this.simulator.getMarketDataProvider();
        return marketDataProvider.getBestBid();
    }
}
