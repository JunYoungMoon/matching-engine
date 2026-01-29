package com.exchange.matching;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class OrderBook {
    // 매수 주문: 가격 높은 순 (내림차순)
    private final TreeMap<Integer, PriceLevel> buyOrders;
    // 매도 주문: 가격 낮은 순 (오름차순)
    private final TreeMap<Integer, PriceLevel> sellOrders;
    // 빠른 주문 조회
    private final HashMap<String, Order> orderMap;

    public OrderBook() {
        this.buyOrders = new TreeMap<>(Collections.reverseOrder());
        this.sellOrders = new TreeMap<>();
        this.orderMap = new HashMap<>();
    }

    // 주문 접수
    public void addOrder(Order order) {
        System.out.println("\n=== 주문 접수: " + order);

        // 체결 시도
        if (order.getSide() == OrderSide.BUY) {
            matchBuyOrder(order);
        } else {
            matchSellOrder(order);
        }

        // 남은 수량이 있으면 호가창에 추가
        if (order.getQuantity() > 0) {
            addToBook(order);
        }
    }

    // 매수 주문 체결
    private void matchBuyOrder(Order buyOrder) {
        while (buyOrder.getQuantity() > 0 && !sellOrders.isEmpty()) {
            Map.Entry<Integer, PriceLevel> bestSell = sellOrders.firstEntry();

            // 가격이 맞지 않으면 중단
            if (bestSell.getKey() > buyOrder.getPrice()) {
                break;
            }

            PriceLevel priceLevel = bestSell.getValue();
            Order sellOrder = priceLevel.getFirstOrder();

            // 체결
            int matchedQty = Math.min(buyOrder.getQuantity(), sellOrder.getQuantity());
            System.out.printf("체결: 가격=%d, 수량=%d (매수:%s <-> 매도:%s)\n",
                    sellOrder.getPrice(), matchedQty, buyOrder.getOrderId(), sellOrder.getOrderId());

            int oldSellQty = sellOrder.getQuantity();

            buyOrder.setQuantity(buyOrder.getQuantity() - matchedQty);
            sellOrder.setQuantity(sellOrder.getQuantity() - matchedQty);

            // 매도 주문이 완전히 체결되면 제거
            if (sellOrder.getQuantity() == 0) {
                // 원래 수량으로 totalVolume 차감
                priceLevel.decreaseTotalVolume(oldSellQty);
                priceLevel.removeOrderFromList(sellOrder);
                orderMap.remove(sellOrder.getOrderId());

                // PriceLevel이 비었으면 제거
                if (priceLevel.isEmpty()) {
                    sellOrders.remove(bestSell.getKey());
                }
            } else {
                // 부분 체결: totalVolume 업데이트
                priceLevel.updateOrderQuantity(sellOrder, oldSellQty, sellOrder.getQuantity());
            }
        }
    }

    // 매도 주문 체결
    private void matchSellOrder(Order sellOrder) {
        while (sellOrder.getQuantity() > 0 && !buyOrders.isEmpty()) {
            Map.Entry<Integer, PriceLevel> bestBuy = buyOrders.firstEntry();

            // 가격이 맞지 않으면 중단
            if (bestBuy.getKey() < sellOrder.getPrice()) {
                break;
            }

            PriceLevel priceLevel = bestBuy.getValue();
            Order buyOrder = priceLevel.getFirstOrder();

            // 체결
            int matchedQty = Math.min(sellOrder.getQuantity(), buyOrder.getQuantity());
            System.out.printf("체결: 가격=%d, 수량=%d (매수:%s <-> 매도:%s)\n",
                    buyOrder.getPrice(), matchedQty, buyOrder.getOrderId(), sellOrder.getOrderId());

            int oldBuyQty = buyOrder.getQuantity();

            sellOrder.setQuantity(sellOrder.getQuantity() - matchedQty);
            buyOrder.setQuantity(buyOrder.getQuantity() - matchedQty);

            // 매수 주문이 완전히 체결되면 제거
            if (buyOrder.getQuantity() == 0) {
                // 원래 수량으로 totalVolume 차감
                priceLevel.decreaseTotalVolume(oldBuyQty);
                priceLevel.removeOrderFromList(buyOrder);
                orderMap.remove(buyOrder.getOrderId());

                // PriceLevel이 비었으면 제거
                if (priceLevel.isEmpty()) {
                    buyOrders.remove(bestBuy.getKey());
                }
            } else {
                // 부분 체결: totalVolume 업데이트
                priceLevel.updateOrderQuantity(buyOrder, oldBuyQty, buyOrder.getQuantity());
            }
        }
    }

    // 호가창에 추가
    private void addToBook(Order order) {
        TreeMap<Integer, PriceLevel> book =
                order.getSide() == OrderSide.BUY ? buyOrders : sellOrders;

        PriceLevel priceLevel = book.computeIfAbsent(order.getPrice(), k -> new PriceLevel(order.getPrice()));

        priceLevel.addOrder(order);
        orderMap.put(order.getOrderId(), order);
        System.out.println("호가창 추가: " + order);
    }

    // 주문 취소
    public void cancelOrder(String orderId) {
        Order order = orderMap.get(orderId);
        if (order == null) {
            System.out.println("주문을 찾을 수 없습니다: " + orderId);
            return;
        }

        TreeMap<Integer, PriceLevel> book =
                order.getSide() == OrderSide.BUY ? buyOrders : sellOrders;

        PriceLevel priceLevel = book.get(order.getPrice());
        if (priceLevel != null) {
            priceLevel.removeOrder(order);

            if (priceLevel.isEmpty()) {
                book.remove(order.getPrice());
            }
        }

        orderMap.remove(orderId);
        System.out.println("주문 취소: " + order);
    }

    // 호가창 출력
    public void printOrderBook() {
        System.out.println("\n========== 호가창 ==========");

        System.out.println("\n[매도 호가]");
        sellOrders.descendingMap().forEach((price, level) -> {
            System.out.printf("  %d원: %d주\n", price, level.getTotalVolume());
        });

        System.out.println("\n[매수 호가]");
        buyOrders.forEach((price, level) -> {
            System.out.printf("  %d원: %d주\n", price, level.getTotalVolume());
        });

        System.out.println("===========================\n");
    }
}