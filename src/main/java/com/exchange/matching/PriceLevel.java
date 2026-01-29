package com.exchange.matching;

public class PriceLevel {
    private final int price;
    private long totalVolume;
    private OrderNode head;
    private OrderNode tail;

    public PriceLevel(int price) {
        this.price = price;
        this.totalVolume = 0;
        this.head = null;
        this.tail = null;
    }

    // 주문 추가 (맨 뒤에)
    public void addOrder(Order order) {
        OrderNode newNode = new OrderNode(order);
        order.setNode(newNode);

        if (tail == null) {
            // 첫 번째 주문
            head = tail = newNode;
        } else {
            // 맨 뒤에 추가
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }

        totalVolume += order.getQuantity();
    }

    // 주문 제거 (O(1))
    public void removeOrder(Order order) {
        OrderNode node = order.getNode();
        if (node == null) return;

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }

        totalVolume -= order.getQuantity();
    }

    // 리스트에서만 제거 (totalVolume 업데이트 없음)
    public void removeOrderFromList(Order order) {
        OrderNode node = order.getNode();
        if (node == null) return;

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }
    }

    // totalVolume 직접 차감
    public void decreaseTotalVolume(int quantity) {
        totalVolume -= quantity;
    }

    // 맨 앞 주문 가져오기
    public Order getFirstOrder() {
        return head != null ? head.order : null;
    }

    // 주문 수량 업데이트 (부분 체결 시)
    public void updateOrderQuantity(Order order, int oldQuantity, int newQuantity) {
        totalVolume = totalVolume - oldQuantity + newQuantity;
    }

    // 비어있는지 확인
    public boolean isEmpty() {
        return head == null;
    }

    public int getPrice() {
        return price;
    }

    public long getTotalVolume() {
        return totalVolume;
    }
}