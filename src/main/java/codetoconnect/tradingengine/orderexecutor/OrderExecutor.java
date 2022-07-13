package codetoconnect.tradingengine.orderexecutor;

import codetoconnect.simulatedexchange.SimulatedExchange;
import codetoconnect.simulatedexchange.order.Order;
import codetoconnect.simulator.Simulator;
import codetoconnect.tradingengine.ordergoal.OrderGoal;

import java.util.ArrayList;
import java.util.List;

public class OrderExecutor {
    private static final Integer DEFAULT_BATCH_SIZE = Integer.MAX_VALUE;

    private Integer batchSize;

    private final Simulator simulator;

    public OrderExecutor(Simulator simulator) {
        this.simulator = simulator;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public void addOrReplaceOrders(List<OrderGoal> orderGoals) {
        // add or replace open orders
        for (OrderGoal goal : orderGoals) {
            Double goalPrice = goal.getPrice();
            Integer goalSize = goal.getSize();
            Integer openOrderSize = getSizeOfOpenOrdersWithSamePrice(goal);

            // top up
            if (goalSize > openOrderSize) {
                Integer topUpSize = goalSize - openOrderSize;
                sendOrderInBatches(goalPrice, topUpSize, getBatchSize());
            }

            // replace orders
            if (goalSize < openOrderSize) {
                Integer excessSize = openOrderSize - goalSize;

                List<Order> openOrdersWithSamePrice = getOpenOrdersWithSamePrice(goalPrice);
                sortOpenOrdersByAscendingSizeAndDescendingTime(openOrdersWithSamePrice);

                for (Order order : openOrdersWithSamePrice) {
                    cancelOrder(order);

                    excessSize -= order.getSize();
                    if (excessSize <= 0) {
                        break;
                    }
                }

                if (excessSize < 0) {
                    Integer overshotSize = -excessSize;
                    sendOrderInBatches(goalPrice, overshotSize, getBatchSize());
                }
            }
        }
    }

    public List<String> cancelAllOrders() {
        List<String> decisionUpdates = new ArrayList<>();

        return decisionUpdates;
    }

    public List<String> cancelOutdatedOrders(List<OrderGoal> orderGoals) {
        // cancel all open orders that are no longer part of the current order strategy

        List<String> decisionUpdates = new ArrayList<>();

        for (Order openOrder : getOpenOrders()) {
            boolean isOutdated = true;

            for (OrderGoal goal : orderGoals) {
                if (goal.getPrice().equals(openOrder.getPrice())) {
                    isOutdated = false;
                    break;
                }
            }

            if (isOutdated) {
                cancelOrder(openOrder);
            }
        }

        return decisionUpdates;
    }

    private Integer getSizeOfOpenOrdersWithSamePrice(OrderGoal orderGoal) {
        List<Order> openOrdersWithSamePrice = getOpenOrdersWithSamePrice(orderGoal.getPrice());

        Integer currentSize = 0;
        for (Order order : openOrdersWithSamePrice) {
            currentSize += order.getSize();
        }

        return currentSize;
    }

    private List<Order> getOpenOrdersWithSamePrice(Double price) {
        List<Order> openOrdersWithSamePrice = new ArrayList<>();

        for (Order order : getOpenOrders()) {
            if (order.getPrice().equals(price)) {
                openOrdersWithSamePrice.add(order);
            }
        }

        return openOrdersWithSamePrice;
    }

    private void sendOrder(Double price, Integer size) {
        String decisionUpdate = String.format("[N:%s:%d]", price, size);
        System.out.format("%s ", decisionUpdate);

        Order order = new Order(getCurrentTimestamp(), price, size);
        SimulatedExchange simulatedExchange = this.simulator.getSimulatedExchange();
        simulatedExchange.receiveOrder(order);
    }

    private void sendOrderInBatches(Double price, Integer size, Integer sizePerBatch) {
        Integer remainingSizeToSend = size;
        while (remainingSizeToSend > 0) {
            Integer currentOrderSize = Math.min(sizePerBatch, remainingSizeToSend);

            sendOrder(price, currentOrderSize);

            remainingSizeToSend -= currentOrderSize;
        }
    }

    private void cancelOrder(Order order) {
        String decisionUpdate = String.format("[C:%s:%d]", order.getPrice(), order.getSize());
        System.out.format("%s ", decisionUpdate);

        SimulatedExchange simulatedExchange = this.simulator.getSimulatedExchange();
        simulatedExchange.cancelOrder(order);
    }

    private List<Order> getOpenOrders() {
        SimulatedExchange simulatedExchange = simulator.getSimulatedExchange();
        return simulatedExchange.getOpenOrders();
    }

    private Long getCurrentTimestamp() {
        return this.simulator.getTimestamp();
    }

    private Integer getBatchSize() {
        if (this.batchSize == null) {
            return DEFAULT_BATCH_SIZE;
        }

        return this.batchSize;
    }

    private void sortOpenOrdersByAscendingSizeAndDescendingTime(List<Order> openOrders) {
        openOrders.sort((a,b) -> {
            if (!(a.getSize().equals(b.getSize()))) {
                return a.getSize().compareTo(b.getSize());
            } else {
                return -a.getPlacedAtTimestamp().compareTo(b.getPlacedAtTimestamp());
            }
        });
    }
}
