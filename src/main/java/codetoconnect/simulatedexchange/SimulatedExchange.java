package codetoconnect.simulatedexchange;

import codetoconnect.marketdataprovider.MarketDataProvider;
import codetoconnect.marketdataprovider.orderbook.Ask;
import codetoconnect.marketdataprovider.orderbook.Bid;
import codetoconnect.simulatedexchange.order.Order;
import codetoconnect.simulator.Simulator;
import codetoconnect.simulator.SimulatorComponent;
import codetoconnect.simulator.loggingservice.SimulatedExchangeLoggingService;

import java.util.ArrayList;
import java.util.List;

public class SimulatedExchange implements SimulatorComponent {

    // after 180,000 ms (3 minutes), orders with prices at best-bid prices or better are filled
    private static final Long NEAR_TOUCH_MARKETABLE_AGE = 180000L;

    private final List<Order> openOrders;
    private final List<Order> filledOrders;
    private final List<Order> cancelledOrders;
    private Integer cumulativeExecutedShares;
    private final Simulator simulator;

    public SimulatedExchange(Simulator simulator) {
        this.openOrders = new ArrayList<>();
        this.filledOrders = new ArrayList<>();
        this.cancelledOrders = new ArrayList<>();
        this.cumulativeExecutedShares = 0;

        this.simulator = simulator;
    }

    @Override
    public boolean execute() {
        SimulatedExchangeLoggingService.startLog();

        boolean endSimulation = false;

        for (Order order : getOpenOrders()) {
            if (isMarketable(order)) {
                fillOpenOrder(order);
            }
        }

        SimulatedExchangeLoggingService.endLogAt(getTimestamp());

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

        logCancelledOrderUpdate(order);
    }

    public List<Order> getOpenOrders() {
        List<Order> copy = new ArrayList<>(openOrders);
        return copy;
    }

    public void printEndOfSimulationSummary() {
        System.out.format("Volume-Weighted Average Price: %s\n", calculateVolumeWeightedAveragePrice());
    }

    private Double calculateVolumeWeightedAveragePrice() {
        Double nominator = 0.0;
        Double denominator = 0.0;

        for (Order order : filledOrders) {
            nominator += (order.getPrice() * order.getSize());
            denominator += order.getSize();
        }

        Double result = nominator / denominator;
        if (result.isNaN()) {
            return 0.0;
        }

        return result;
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

    private void fillOrder(Order order) {
        this.filledOrders.add(order);
        this.cumulativeExecutedShares += order.getSize();

        logFilledOrderUpdate(order);
    }

    private void fillOpenOrder(Order order) {
        fillOrder(order);
        openOrders.remove(order);
    }

    private void queueOrder(Order order) {
        this.openOrders.add(order);
        logQueuedOrderUpdate(order);
    }

    public Integer getCumulativeExecutedShares() {
        return cumulativeExecutedShares;
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

    private Long getTimestamp() {
        MarketDataProvider marketDataProvider = this.simulator.getMarketDataProvider();
        return marketDataProvider.getLastUpdatedAtTimestamp();
    }

    private void logQueuedOrderUpdate(Order order) {
        String response = String.format("Queued %s\n", order);

        System.out.println(response);
        SimulatedExchangeLoggingService.logResponse(response);
    }

    private void logFilledOrderUpdate(Order order) {
        String response = String.format("Filled %s; Cumulative Quantity: %d\n", order, getCumulativeExecutedShares());

        System.out.println(response);
        SimulatedExchangeLoggingService.logResponse(response);
    }

    private void logCancelledOrderUpdate(Order order) {
        String response = String.format("Cancelled %s\n", order);

        System.out.println(response);
        SimulatedExchangeLoggingService.logResponse(response);
    }
}
