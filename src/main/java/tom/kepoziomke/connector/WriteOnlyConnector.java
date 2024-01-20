package tom.kepoziomke.connector;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.endpoint.orders.Order;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderSide;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;

import java.util.Optional;

public class WriteOnlyConnector extends ConnectorBase {

    public WriteOnlyConnector() {
        super();
    }

    public WriteOnlyConnector(AlpacaAPI api) {
        super(api);
    }

    public Optional<Order> buyOrder(String symbol, double quantity) {
        try {
            synchronized (this.api) {
                var endpoint = api.orders();
                return Optional.of(endpoint.requestFractionalMarketOrder(symbol, quantity, OrderSide.BUY));
            }
        } catch (AlpacaClientException e) {
            return Optional.empty();
        }
    }

    public Optional<Order> sellOrder(String symbol, double quantity) {
        try {
            synchronized (this.api) {
                var endpoint = api.orders();
                return Optional.of(endpoint.requestFractionalMarketOrder(symbol, quantity, OrderSide.SELL));
            }
        } catch (AlpacaClientException e) {
            return Optional.empty();
        }
    }

}
