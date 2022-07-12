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

    public void addOrReplaceOrders(List<OrderGoal> orderGoals) {

        // add or replace open orders
        for (OrderGoal goal : orderGoals) {
            Double goalPrice = goal.getPrice();
            Integer goalSize = goal.getSize();
            Integer openOrderSize = getSizeOfOpenOrdersWithSamePrice(goal);

            // top up
            if (goalSize > openOrderSize) {
                Integer topUpSize = goalSize - openOrderSize;
                sendOrder(goalPrice, topUpSize);
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
                    sendOrder(goalPrice, overshotSize);
                }
            }
        }
    }

    public void cancelOutdatedOrders(List<OrderGoal> orderGoals) {
        // cancel open orders that are no longer part of the current order strategy
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
    }

    public void cancelAllOrders() {
        SimulatedExchange simulatedExchange = this.simulator.getSimulatedExchange();
        simulatedExchange.cancelAllOrders();
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
        Order order = new Order(getCurrentTimestamp(), price, size);

        SimulatedExchange simulatedExchange = this.simulator.getSimulatedExchange();
        simulatedExchange.receiveOrder(order);
    }

    private void cancelOrder(Order order) {
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
