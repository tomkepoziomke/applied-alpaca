package tom.kepoziomke.connector;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.endpoint.orders.Order;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderSide;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderTimeInForce;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderType;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;

/**
 * Write-only part of the connector.
 */
public class WriteOnlyConnector extends ConnectorBase {

    /**
     * Default constructor.
     */
    public WriteOnlyConnector() {
        super();
    }

    /**
     * Parametrized constructor, admitting AlpacaAPI object.
     * @param api The AlpacaAPI object.
     */
    public WriteOnlyConnector(AlpacaAPI api) {
        super(api);
    }

    /**
     * Attempts to place a fractional buy order. If the symbol is from crypto market, its time in force is
     * good until cancelled, otherwise it's a day order.
     * @param symbol The symbol to be bought.
     * @param quantity The quantity.
     * @param crypto Whether the symbol belongs to the crypto market.
     * @return The response containing the resulting order or an error information.
     */
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

    /**
     * Attempts to place a fractional sell order. If the symbol is from crypto market, its time in force is
     * good until cancelled, otherwise it's a day order.
     * @param symbol The symbol to be sold.
     * @param quantity The quantity.
     * @param crypto Whether the symbol belongs to the crypto market.
     * @return The response containing the resulting order or an error information.
     */
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
}
