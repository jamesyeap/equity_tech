package codetoconnect.tradingengine;

import codetoconnect.marketdataprovider.MarketDataProvider;
import codetoconnect.marketdataprovider.orderbook.Ask;
import codetoconnect.marketdataprovider.orderbook.Bid;
import codetoconnect.simulatedexchange.SimulatedExchange;
import codetoconnect.simulator.Simulator;
import codetoconnect.simulator.SimulatorComponent;
import codetoconnect.simulator.loggingservice.TradingEngineLoggingService;
import codetoconnect.tradingengine.clientorderreader.ClientPovBuyOrder;
import codetoconnect.tradingengine.orderexecutor.OrderExecutor;
import codetoconnect.tradingengine.ordergoal.OrderGoal;

import java.util.ArrayList;
import java.util.List;

public class TradingEngine implements SimulatorComponent {
    private static final Double MIN_RATIO_MODIFIER = 0.8;
    private static final Double MAX_RATIO_MODIFIER = 1.2;

    private final ClientPovBuyOrder clientPovBuyOrder;

    private final OrderExecutor orderExecutor;

    private final Simulator simulator;

    public static TradingEngine initialize(ClientPovBuyOrder clientOrder, Simulator simulator) {
        return new TradingEngine(clientOrder, simulator);
    }

    TradingEngine(ClientPovBuyOrder clientPovBuyOrder, Simulator simulator) {
        this.clientPovBuyOrder = clientPovBuyOrder;
        this.orderExecutor = new OrderExecutor(simulator);
        this.simulator = simulator;
    }

    @Override
    public boolean execute() {
        TradingEngineLoggingService.startLogAt(getTimestamp());

        updateStrategy();

        TradingEngineLoggingService.endLog();

        if (allOrdersFilled()) {
            return true;
        } else {
            return false;
        }
    }

    public void setBatchSize(Integer batchSize) {
        orderExecutor.setBatchSize(batchSize);
    }

    public void printEndOfSimulationSummary() {
        System.out.format("Client Order Size: %s\n" +
                            "Client Order Size Filled: %s\n",
                clientPovBuyOrder.getOrderQuantity(), getCumulativeExecutedShares());
    }

    private void updateStrategy() {
        if (isBelowMinRatio()) {
            printBelowMinRatioUpdate();

            // get shortfall shares at far-touch prices first
            List<OrderGoal> aggressiveOrderGoal = generateAggressiveCatchupStrategy();
            orderExecutor.addOrReplaceOrders(aggressiveOrderGoal);

            // then adjust the rest of the orders with passive posting strategy
            List<OrderGoal> passiveOrderGoals = generatePassivePostingStrategy();
            orderExecutor.addOrReplaceOrders(passiveOrderGoals);

            // remove the open orders that are no longer a part of the strategy
            List<OrderGoal> orderGoals = new ArrayList<>(aggressiveOrderGoal);
            orderGoals.addAll(passiveOrderGoals);
            orderExecutor.cancelOutdatedOrders(orderGoals);

        } else if (hasBreachedMaxRatio()) {
            printBreachedMaxRatioUpdate();

            orderExecutor.cancelAllOrders();
        } else {
            printPassivePostingUpdate();

            // adjust the orders with passive posting strategy
            List<OrderGoal> orderGoals = generatePassivePostingStrategy();

            orderExecutor.addOrReplaceOrders(orderGoals);

            // remove the open orders that are no longer a part of the strategy
            orderExecutor.cancelOutdatedOrders(orderGoals);
        }

        System.out.println();
    }

    private boolean isBelowMinRatio() {
        return (getCumulativeExecutedShares() < getMinRatioSize());
    }

    private boolean hasBreachedMaxRatio() {
        return (getCumulativeExecutedShares() > getMaxRatioSize());
    }

    private boolean allOrdersFilled() {
        return getCumulativeExecutedShares() >= getOrderQuantity();
    }

    private List<OrderGoal> generatePassivePostingStrategy() {
        List<OrderGoal> goals = new ArrayList<>();

        List<Bid> bids = getBids();

        Integer remainingSize = getRemainingShares();

        for (int i = 0; i < bids.size() - 1; i++) {
            Bid bid = bids.get(i);
            Double bidPrice = bid.getPrice();
            Integer bidSize = bid.getSize();

            Integer targetSize = calculatePercentageOfShares(bidSize, getTargetPercentage());
            Integer goalSize = Math.min(targetSize, remainingSize);

            goals.add(createOrderGoal(bidPrice, goalSize));
            remainingSize -= goalSize;
        }

        if (remainingSize > 0) {
            Bid lastBid = bids.get(bids.size() - 1);
            Double bidPrice = lastBid.getPrice();
            goals.add(createOrderGoal(bidPrice, remainingSize));
        }

        return goals;
    }

    private List<OrderGoal> generateAggressiveCatchupStrategy() {
        List<OrderGoal> goals = new ArrayList<>();

        Integer cumulativeExecutedShares = getCumulativeExecutedShares();
        Integer minRatioSize = getMinRatioSize();

        Integer shortfallSize = minRatioSize - cumulativeExecutedShares;
        Integer remainingSize = getRemainingShares();
        Integer goalSize = Math.min(shortfallSize, remainingSize);

        Double bestAskPrice = getBestAsk().getPrice();
        goals.add(createOrderGoal(bestAskPrice, goalSize));

        return goals;
    }

    private Integer calculatePercentageOfShares(Integer size, Double percentage) {
        Double percentageAsFraction = percentage / 100;
        return (int)Math.round(size * percentageAsFraction);
    }
    private OrderGoal createOrderGoal(Double price, Integer size) {
        return new OrderGoal(price, size);
    }

    private List<Bid> getBids() {
        MarketDataProvider marketDataProvider = simulator.getMarketDataProvider();
        return marketDataProvider.getBids();
    }

    private List<Ask> getAsks() {
        MarketDataProvider marketDataProvider = simulator.getMarketDataProvider();
        return marketDataProvider.getAsks();
    }

    private Ask getBestAsk() {
        MarketDataProvider marketDataProvider = simulator.getMarketDataProvider();
        return marketDataProvider.getBestAsk();
    }

    private Integer getMarketTradedVolume() {
        MarketDataProvider marketDataProvider = simulator.getMarketDataProvider();
        return marketDataProvider.getMarketTradedVolume();
    }

    private Long getTimestamp() {
        MarketDataProvider marketDataProvider = simulator.getMarketDataProvider();
        return marketDataProvider.getLastUpdatedAtTimestamp();
    }

    private Double getTargetPercentage() {
        return clientPovBuyOrder.getTargetPercentage();
    }

    private Double getMinRatio() {
        return getTargetPercentage() * MIN_RATIO_MODIFIER;
    }

    private Double getMaxRatio() {
        return getTargetPercentage() * MAX_RATIO_MODIFIER;
    }

    private Integer getMinRatioSize() {
        return calculatePercentageOfShares(getMarketTradedVolume(), getMinRatio());
    }

    private Integer getMaxRatioSize() {
        return calculatePercentageOfShares(getMarketTradedVolume(), getMaxRatio());
    }

    private Integer getOrderQuantity() {
        return clientPovBuyOrder.getOrderQuantity();
    }

    private Integer getCumulativeExecutedShares() {
        SimulatedExchange simulatedExchange = simulator.getSimulatedExchange();
        return simulatedExchange.getCumulativeExecutedShares();
    }

    private Integer getRemainingShares() {
        Integer orderQuantity = getOrderQuantity();
        Integer cumulativeExecutedShares = getCumulativeExecutedShares();

        return orderQuantity - cumulativeExecutedShares;
    }

    private void printBelowMinRatioUpdate() {
        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        System.out.format("::: TRADING ENGINE ASSESSMENT: (BELOW MIN-RATIO) by %d shares ::::::::::::::::::::::::::\n",
                getMinRatioSize() - getCumulativeExecutedShares());
        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
    }

    private void printBreachedMaxRatioUpdate() {
        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        System.out.format("::: TRADING ENGINE ASSESSMENT: (BREACH MAX-RATIO) by %d shares ::::::::::::::::::::\n",
                getCumulativeExecutedShares() - getMaxRatioSize());
        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
    }

    private void printPassivePostingUpdate() {
        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        System.out.println("::: TRADING ENGINE ASSESSMENT: (PASSIVE POSTING) ::::::::::::::::::::::::::::::::::::::::");
        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
    }
}
