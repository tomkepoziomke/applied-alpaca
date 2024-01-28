package tom.kepoziomke;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.endpoint.account.Account;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.historical.bar.enums.BarTimePeriod;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.StockBarsResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.enums.BarAdjustment;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.enums.BarFeed;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.trade.LatestStockTradeResponse;
import net.jacobpeterson.alpaca.model.endpoint.orders.Order;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderSide;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderTimeInForce;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;
import net.jacobpeterson.alpaca.rest.endpoint.account.AccountEndpoint;
import net.jacobpeterson.alpaca.rest.endpoint.marketdata.stock.StockMarketDataEndpoint;
import net.jacobpeterson.alpaca.rest.endpoint.orders.OrdersEndpoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

public class AccountTest {
    @Test
    void connect() {
        try {
            final var alpacaAPI = new AlpacaAPI();
            final var accountEndpoint = alpacaAPI.account();

            final Account account;
            account = accountEndpoint.get();

            final var accountNumber = account.getAccountNumber();
            Assertions.assertEquals("PA3SVJHC0AFH", accountNumber);
        } catch (AlpacaClientException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void bars() {
        try {
            final var alpacaAPI = new AlpacaAPI();
            final var stockMarketDataEndpoint = alpacaAPI.stockMarketData();

            final var stockBarsResponse = stockMarketDataEndpoint.getBars(
                    "AAPL",
                    ZonedDateTime.now().minusWeeks(10),
                    ZonedDateTime.now().minusWeeks(9),
                    null,
                    null,
                    1,
                    BarTimePeriod.HOUR,
                    BarAdjustment.RAW,
                    BarFeed.IEX);

            System.out.println(stockBarsResponse.getBars());
        } catch (AlpacaClientException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void latestTrade() {
        try {
            final var alpacaAPI = new AlpacaAPI();
            final var stockMarketDataEndpoint = alpacaAPI.stockMarketData();

            final var latestTrade = stockMarketDataEndpoint.getLatestTrade("AAPL");

            System.out.println(latestTrade);
        } catch (AlpacaClientException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void latestQuote() {
        try {
            final var alpacaAPI = new AlpacaAPI();
            final var stockMarketDataEndpoint = alpacaAPI.stockMarketData();

            final var latestQuote = stockMarketDataEndpoint.getLatestQuote("AAPL");

            System.out.println(latestQuote);
        } catch (AlpacaClientException e) {
            Assertions.fail(e);
        }
    }

    @Test
    void place() {
        try {
            final var alpacaAPI = new AlpacaAPI();
            final var ordersEndpoint = alpacaAPI.orders();

            final var marketOrder = ordersEndpoint.requestMarketOrder(
                    "AAPL",
                    10,
                    OrderSide.BUY,
                    OrderTimeInForce.DAY);

            System.out.println(marketOrder);
        } catch (AlpacaClientException e) {
            Assertions.fail(e);
        }
    }

}
