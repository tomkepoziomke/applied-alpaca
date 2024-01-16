package org.example;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.endpoint.common.enums.SortDirection;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.CurrentOrderStatus;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        final var alpacaAPI = new AlpacaAPI();
        final var ordersEndpoint = alpacaAPI.orders();
        var last5Orders = ordersEndpoint.get(
                CurrentOrderStatus.ALL,
                5,
                ZonedDateTime.now().minusDays(1),
                ZonedDateTime.now(),
                SortDirection.DESCENDING,
                false,
                new ArrayList<>(List.of("AAPL", "ORLEN")));
        last5Orders.forEach(System.out::println);
        for (var order : last5Orders) {
            order.get
        }


    }
}
