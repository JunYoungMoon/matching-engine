package com.exchange.matching;

public class OrderNode {
    Order order;
    OrderNode prev;
    OrderNode next;

    public OrderNode(Order order) {
        this.order = order;
        this.prev = null;
        this.next = null;
    }
}