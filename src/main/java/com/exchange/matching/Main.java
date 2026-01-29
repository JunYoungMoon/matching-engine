package com.exchange.matching;

public class Main {
    public static void main(String[] args) {
        OrderBook orderBook = new OrderBook();

        System.out.println("====== 주문 체결 시스템 테스트 ======\n");

        // 1. 매수/매도 호가 설정
        orderBook.addOrder(new Order("B1", OrderSide.BUY, 100, 50));
        orderBook.addOrder(new Order("B2", OrderSide.BUY, 99, 30));
        orderBook.addOrder(new Order("B3", OrderSide.BUY, 98, 40));

        orderBook.addOrder(new Order("S1", OrderSide.SELL, 102, 20));
        orderBook.addOrder(new Order("S2", OrderSide.SELL, 103, 35));
        orderBook.addOrder(new Order("S3", OrderSide.SELL, 104, 25));

        orderBook.printOrderBook();

        // 2. 즉시 체결되는 매수 주문
        System.out.println("\n>>> 매수 103원 60주 접수");
        orderBook.addOrder(new Order("B4", OrderSide.BUY, 103, 60));
        orderBook.printOrderBook();

        // 3. 즉시 체결되는 매도 주문
        System.out.println("\n>>> 매도 99원 70주 접수");
        orderBook.addOrder(new Order("S4", OrderSide.SELL, 99, 70));
        orderBook.printOrderBook();

        // 4. 주문 취소
        System.out.println("\n>>> 주문 B2 취소");
        orderBook.cancelOrder("B2");
        orderBook.printOrderBook();

        // 5. 같은 가격에 여러 주문 (시간 우선)
        System.out.println("\n>>> 같은 가격 100원에 3개 주문 추가");
        orderBook.addOrder(new Order("B5", OrderSide.BUY, 100, 10));
        orderBook.addOrder(new Order("B6", OrderSide.BUY, 100, 20));
        orderBook.addOrder(new Order("B7", OrderSide.BUY, 100, 15));
        orderBook.printOrderBook();

        // 6. 시간 우선 확인 (B5 -> B6 -> B7 순서로 체결되어야 함)
        System.out.println("\n>>> 매도 100원 35주 접수 (시간 우선 확인)");
        orderBook.addOrder(new Order("S5", OrderSide.SELL, 100, 35));
        orderBook.printOrderBook();
    }
}