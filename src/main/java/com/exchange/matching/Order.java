package com.exchange.matching;

public class Order {
    private final String orderId;
    private final OrderSide side;  // BUY or SELL
    private final int price;
    private int quantity;
    private OrderNode node;  // 자신이 속한 노드 참조

    public Order(String orderId, OrderSide side, int price, int quantity) {
        this.orderId = orderId;
        this.side = side;
        this.price = price;
        this.quantity = quantity;
    }

    public String getOrderId() {
        return orderId;
    }

    public OrderSide getSide() {
        return side;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public OrderNode getNode() {
        return node;
    }

    public void setNode(OrderNode node) {
        this.node = node;
    }

    @Override
    public String toString() {
        return String.format("Order[id=%s, side=%s, price=%d, qty=%d]",
                orderId, side, price, quantity);
    }
}

enum OrderSide {
    BUY, SELL
}