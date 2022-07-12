package codetoconnect.tradingengine.orderexecutor;

import codetoconnect.simulatedexchange.SimulatedExchange;
import codetoconnect.simulatedexchange.order.Order;
import codetoconnect.simulator.Simulator;
import codetoconnect.tradingengine.ordergoal.OrderGoal;

import java.util.ArrayList;
import java.util.List;

public class OrderExecutor {
    private final Simulator simulator;

    public OrderExecutor(Simulator simulator) {
        this.simulator = simulator;
    }

    public List<String> addOrReplaceOrders(List<OrderGoal> orderGoals) {
        List<String> decisionUpdates = new ArrayList<>();

        // add or replace open orders
        for (OrderGoal goal : orderGoals) {
            Double goalPrice = goal.getPrice();
            Integer goalSize = goal.getSize();
            Integer openOrderSize = getSizeOfOpenOrdersWithSamePrice(goal);

            // top up
            if (goalSize > openOrderSize) {
                Integer topUpSize = goalSize - openOrderSize;
                String decisionUpdate = sendOrder(goalPrice, topUpSize);
                decisionUpdates.add(decisionUpdate);
            }

            // replace orders
            if (goalSize < openOrderSize) {
                Integer excessSize = openOrderSize - goalSize;

                List<Order> openOrdersWithSamePrice = getOpenOrdersWithSamePrice(goalPrice);
                sortOpenOrdersByAscendingSizeAndDescendingTime(openOrdersWithSamePrice);

                for (Order order : openOrdersWithSamePrice) {
                    String decisionUpdate = cancelOrder(order);
                    decisionUpdates.add(decisionUpdate);

                    excessSize -= order.getSize();
                    if (excessSize <= 0) {
                        break;
                    }
                }

                if (excessSize < 0) {
                    Integer overshotSize = -excessSize;
                    String decisionUpdate = sendOrder(goalPrice, overshotSize);
                    decisionUpdates.add(decisionUpdate);
                }
            }
        }

        return decisionUpdates;
    }

    public List<String> cancelOutdatedOrders(List<OrderGoal> orderGoals) {
        // cancel open orders that are no longer part of the current order strategy
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
                String decisionUpdate = cancelOrder(openOrder);
                decisionUpdates.add(decisionUpdate);
            }
        }

        return decisionUpdates;
    }

    public List<String> cancelAllOrders() {
        List<String> decisionUpdates = new ArrayList<>();

        for (Order order : getOpenOrders()) {
            String decisionUpdate = cancelOrder(order);
            decisionUpdates.add(decisionUpdate);
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

    private String sendOrder(Double price, Integer size) {
        String decisionUpdate = String.format("[N:%s:%d]", price, size);

        Order order = new Order(getCurrentTimestamp(), price, size);
        SimulatedExchange simulatedExchange = this.simulator.getSimulatedExchange();
        simulatedExchange.receiveOrder(order);

        return decisionUpdate;
    }

    private String cancelOrder(Order order) {
        String decisionUpdate = String.format("[C:%s:%d]", order.getPrice(), order.getSize());

        SimulatedExchange simulatedExchange = this.simulator.getSimulatedExchange();
        simulatedExchange.cancelOrder(order);

        return decisionUpdate;
    }

    private List<Order> getOpenOrders() {
        SimulatedExchange simulatedExchange = simulator.getSimulatedExchange();
        return simulatedExchange.getOpenOrders();
    }

    private Long getCurrentTimestamp() {
        return this.simulator.getTimestamp();
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
