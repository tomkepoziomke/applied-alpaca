package tom.kepoziomke.connector;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.endpoint.account.Account;
import net.jacobpeterson.alpaca.model.endpoint.assets.Asset;
import net.jacobpeterson.alpaca.model.endpoint.assets.enums.AssetClass;
import net.jacobpeterson.alpaca.model.endpoint.assets.enums.AssetStatus;
import net.jacobpeterson.alpaca.model.endpoint.common.enums.SortDirection;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.historical.bar.enums.BarTimePeriod;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.MultiStockBarsResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.StockBar;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.StockBarsResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.enums.BarAdjustment;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.enums.BarFeed;
import net.jacobpeterson.alpaca.model.endpoint.orders.Order;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.CurrentOrderStatus;
import net.jacobpeterson.alpaca.model.endpoint.positions.Position;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;

import java.lang.reflect.Array;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class AlpacaReadOnlyConnector extends AlpacaConnectorBase {

    public AlpacaReadOnlyConnector() {
        super();
    }

    public AlpacaReadOnlyConnector(AlpacaAPI api) {
        super(api);
    }

    public Optional<Account> account() {
        try {
            synchronized (this.api) {
                var endpoint = api.account();
                return Optional.of(endpoint.get());
            }
        }
        catch (AlpacaClientException e) {
            return Optional.empty();
        }
    }

    public Optional<List<Asset>> assets() {
        try {
            synchronized (this.api) {
                var endpoint = api.assets();
                return Optional.of(endpoint.get(AssetStatus.ACTIVE, AssetClass.US_EQUITY));
            }
        }
        catch (AlpacaClientException e) {
            return Optional.empty();
        }
    }

    public Optional<List<Order>> orders(List<String> symbols, ZonedDateTime after, ZonedDateTime until) {
        try {
            synchronized (this.api) {
                var endpoint = api.orders();
                return Optional.of(endpoint.get(
                        CurrentOrderStatus.OPEN,
                        null,
                        after,
                        until,
                        SortDirection.DESCENDING,
                        false,
                        symbols));
            }

        }
        catch (AlpacaClientException e) {
            return Optional.empty();
        }
    }

    public Optional<List<Position>> positions() {
        try {
            synchronized (this.api) {
                var endpoint = api.positions();
                return Optional.of(endpoint.get());
            }
        }
        catch (AlpacaClientException e) {
            return Optional.empty();
        }
    }

    public Optional<HashMap<String, ArrayList<StockBar>>> stockBars(List<String> symbols, ZonedDateTime start, ZonedDateTime end, int duration, BarTimePeriod period) {
        try {
            synchronized (this.api) {
                var endpoint = api.stockMarketData();
                HashMap<String, ArrayList<StockBar>> bars = new HashMap<>();
                String token = "";
                do {
                    MultiStockBarsResponse response = endpoint.getBars(
                                    symbols,
                                    start,
                                    end,
                                    null,
                                    null,
                                    duration,
                                    period,
                                    BarAdjustment.RAW,
                                    BarFeed.IEX);
                    bars.putAll(response.getBars());
                    token = response.getNextPageToken();
                } while (token != "");
                return Optional.of(bars);
            }
        }
        catch (AlpacaClientException e) {
            return Optional.empty();
        }
    }
}
