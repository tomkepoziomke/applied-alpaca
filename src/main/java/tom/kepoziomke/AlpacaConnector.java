package tom.kepoziomke;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.endpoint.assets.Asset;
import net.jacobpeterson.alpaca.model.endpoint.assets.enums.AssetClass;
import net.jacobpeterson.alpaca.model.endpoint.assets.enums.AssetStatus;
import net.jacobpeterson.alpaca.model.endpoint.common.enums.SortDirection;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.historical.bar.enums.BarTimePeriod;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.MultiStockBarsResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.enums.BarAdjustment;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.enums.BarFeed;
import net.jacobpeterson.alpaca.model.endpoint.orders.Order;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.CurrentOrderStatus;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderSide;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderTimeInForce;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;
import net.jacobpeterson.alpaca.rest.endpoint.account.AccountEndpoint;
import net.jacobpeterson.alpaca.rest.endpoint.assets.AssetsEndpoint;
import net.jacobpeterson.alpaca.rest.endpoint.marketdata.stock.StockMarketDataEndpoint;
import net.jacobpeterson.alpaca.rest.endpoint.orders.OrdersEndpoint;
import net.jacobpeterson.alpaca.rest.endpoint.positions.PositionsEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.AccountException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public class AlpacaConnector {

    private final AlpacaAPI api = new AlpacaAPI();
    private final Logger logger = LoggerFactory.getLogger(AlpacaConnector.class);

    public AlpacaConnector() {

    }

    public synchronized Optional<PositionsEndpoint> getPositionsEndpoint() {
        return Optional.of(api.positions());
    }

    public synchronized Optional<AccountEndpoint> getAccountEndpoint() {
        return Optional.of(api.account());
    }

    public synchronized Optional<StockMarketDataEndpoint> getStockMarketEndpoint() {
        return Optional.of(api.stockMarketData());
    }

    public synchronized Optional<OrdersEndpoint> getOrdersEndpoint() {
        return Optional.of(api.orders());
    }

    public synchronized Optional<AssetsEndpoint> getAssetsEndpoint() {
        return Optional.of(api.assets());
    }

}
