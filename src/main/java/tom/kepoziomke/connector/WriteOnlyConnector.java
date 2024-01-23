package tom.kepoziomke.connector;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.endpoint.orders.Order;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderSide;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderTimeInForce;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderType;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;

public class WriteOnlyConnector extends ConnectorBase {

    public WriteOnlyConnector() {
        super();
    }

    public WriteOnlyConnector(AlpacaAPI api) {
        super(api);
    }

    public Response<Order> buyOrder(String symbol, double quantity, boolean crypto) {
        try {
            synchronized (this.api) {
                OrderTimeInForce timeInForce = crypto ? OrderTimeInForce.GOOD_UNTIL_CANCELLED : OrderTimeInForce.DAY;
                var endpoint = api.orders();
                return Response.of(endpoint.requestOrder(
                        symbol,
                        quantity,
                        null,
                        OrderSide.BUY,
                        OrderType.MARKET,
                        timeInForce,
                        null,
                        null,
                        null,
                        null,
                        false,
                        null,
                        null,
                        null,
                        null,
                        null));
            }
        } catch (AlpacaClientException e) {
            return Response.exception(e);
        }
    }

    public Response<Order> sellOrder(String symbol, double quantity, boolean crypto) {
        try {
            synchronized (this.api) {
                OrderTimeInForce timeInForce = crypto ? OrderTimeInForce.GOOD_UNTIL_CANCELLED : OrderTimeInForce.DAY;
                var endpoint = api.orders();
                return Response.of(endpoint.requestOrder(
                        symbol,
                        quantity,
                        null,
                        OrderSide.SELL,
                        OrderType.MARKET,
                        timeInForce,
                        null,
                        null,
                        null,
                        null,
                        false,
                        null,
                        null,
                        null,
                        null,
                        null));
            }
        } catch (AlpacaClientException e) {
            return Response.exception(e);
        }
    }

    public Response<Order> buyOrder(String symbol, double quantity) {
        try {
            synchronized (this.api) {
                var endpoint = api.orders();
                return Response.of(endpoint.requestFractionalMarketOrder(symbol, quantity, OrderSide.BUY));
            }
        } catch (AlpacaClientException e) {
            return Response.exception(e);
        }
    }

    public Response<Order> sellOrder(String symbol, double quantity) {
        try {
            synchronized (this.api) {
                var endpoint = api.orders();
                return Response.of(endpoint.requestFractionalMarketOrder(symbol, quantity, OrderSide.SELL));
            }
        } catch (AlpacaClientException e) {
            return Response.exception(e);
        }
    }

}
