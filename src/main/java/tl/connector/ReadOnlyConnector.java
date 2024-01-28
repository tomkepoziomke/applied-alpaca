package tom.kepoziomke.connector;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.model.endpoint.account.Account;
import net.jacobpeterson.alpaca.model.endpoint.assets.Asset;
import net.jacobpeterson.alpaca.model.endpoint.assets.enums.AssetClass;
import net.jacobpeterson.alpaca.model.endpoint.assets.enums.AssetStatus;
import net.jacobpeterson.alpaca.model.endpoint.clock.Clock;
import net.jacobpeterson.alpaca.model.endpoint.common.enums.SortDirection;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.historical.bar.enums.BarTimePeriod;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.bar.CryptoBar;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.bar.CryptoBarsResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.quote.LatestCryptoQuotesResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.MultiStockBarsResponse;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.StockBar;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.enums.BarAdjustment;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.enums.BarFeed;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.quote.LatestStockQuoteResponse;
import net.jacobpeterson.alpaca.model.endpoint.orders.Order;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.CurrentOrderStatus;
import net.jacobpeterson.alpaca.model.endpoint.positions.Position;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Read-only part of the connector.
 */
public class ReadOnlyConnector extends ConnectorBase {

    /**
     * Default constructor.
     */
    public ReadOnlyConnector() {
        super();
    }

    /**
     * Parametrized constructor.
     * @param api The AlpacaAPI object.
     */
    public ReadOnlyConnector(AlpacaAPI api) {
        super(api);
    }

    /**
     * Queries the API for account information.
     * @return Response with account info or an error information.
     */
    public Response<Account> account() {
        try {
            synchronized (this.api) {
                var endpoint = api.account();
                return Response.of(endpoint.get());
            }
        }
        catch (AlpacaClientException e) {
            return Response.exception(e);
        }
    }

    /**
     * Queries the API for all available crypto or regular assets.
     * @param crypto Whether to query for crypto or regular assets.
     * @return Response with assets or an error information.
     */
    public Response<List<Asset>> assets(boolean crypto) {
        try {
            synchronized (this.api) {
                var endpoint = api.assets();
                if (crypto)
                    return Response.of(endpoint.get(AssetStatus.ACTIVE, AssetClass.CRYPTO));
                else
                    return Response.of(endpoint.get(AssetStatus.ACTIVE, AssetClass.US_EQUITY));
            }
        }
        catch (AlpacaClientException e) {
            return Response.exception(e);
        }
    }

    /**
     * Queries the API for a single asset with given symbol.
     * @param symbol The symbol of the asset.
     * @return Response with the asset or an error information.
     */
    public Response<Asset> asset(String symbol) {
        try {
            synchronized (this.api) {
                var endpoint = api.assets();
                return Response.of(endpoint.getBySymbol(symbol));
            }
        }
        catch (AlpacaClientException e) {
            return Response.exception(e);
        }
    }

    /**
     * Queries the API for clock info.
     * @return Response with the asset or an error information.
     */
    public Response<Clock> clock() {
        try {
            synchronized (this.api) {
                var endpoint = api.clock();
                return Response.of(endpoint.get());
            }
        } catch (AlpacaClientException e) {
            return Response.exception(e);
        }
    }

    /**
     * Queries the API for the list of orders.
     * @param symbols The symbols to look form.
     * @param after Time after which the order took place.
     * @param until Time before which the order took place.
     * @return Response with orders or an error information.
     */
    public Response<List<Order>> orders(List<String> symbols, ZonedDateTime after, ZonedDateTime until) {
        try {
            synchronized (this.api) {
                var endpoint = api.orders();
                return Response.of(endpoint.get(
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
            return Response.exception(e);
        }
    }

    /**
     * Queries the API for all available positions.
     * @return Response with positions or an error information.
     */
    public Response<List<Position>> positions() {
        try {
            synchronized (this.api) {
                var endpoint = api.positions();
                return Response.of(endpoint.get());
            }
        }
        catch (AlpacaClientException e) {
            return Response.exception(e);
        }
    }

    /**
     * Queries the API for last quote of a stock asset.
     * @param symbol The symbol of the asset.
     * @return Response with the quote or an error information.
     */
    public Response<LatestStockQuoteResponse> latestStockQuote(String symbol) {
        try {
            synchronized (this.api) {
                var endpoint = api.stockMarketData();
                return Response.of(endpoint.getLatestQuote(symbol));
            }
        } catch (AlpacaClientException e) {
            return Response.exception(e);
        }
    }

    /**
     * Queries the API for last quote of a crypto asset.
     * @param symbol The symbol of the asset.
     * @return Response with the quote or an error information.
     */
    public Response<LatestCryptoQuotesResponse> latestCryptoQuote(String symbol) {
        try {
            synchronized (this.api) {
                var endpoint = api.cryptoMarketData();
                return Response.of(endpoint.getLatestQuotes(List.of(symbol)));
            }
        } catch (AlpacaClientException e) {
            return Response.exception(e);
        }
    }

    /**
     * Queries the API for bars of list of stock market symbols.
     * @param symbols The list of symbols.
     * @param start The time moment after which we are interested in the bar data.
     * @param end The time moment until which we are interested in the bar data.
     * @param duration The amount of time units of each bar.
     * @param period The time unit of the bar.
     * @return Response with the bars or an error information.
     */
    public Response<HashMap<String, ArrayList<StockBar>>> stockBars(List<String> symbols, ZonedDateTime start, ZonedDateTime end, int duration, BarTimePeriod period) {
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
                } while (!token.isEmpty());
                return Response.of(bars);
            }
        }
        catch (AlpacaClientException e) {
            return Response.exception(e);
        }
    }

    /**
     * Queries the API for bars of list of crypto symbols.
     * @param symbols The list of symbols.
     * @param start The time moment after which we are interested in the bar data.
     * @param end The time moment until which we are interested in the bar data.
     * @param duration The amount of time units of each bar.
     * @param period The time unit of the bar.
     * @return Response with the bars or an error information.
     */
    public Response<HashMap<String, ArrayList<CryptoBar>>> cryptoBars(List<String> symbols, ZonedDateTime start, ZonedDateTime end, int duration, BarTimePeriod period) {
        try {
            synchronized (this.api) {
                var endpoint = api.cryptoMarketData();
                HashMap<String, ArrayList<CryptoBar>> bars = new HashMap<>();
                String token = "";
                do {
                    CryptoBarsResponse response = endpoint.getBars(
                            symbols,
                            start,
                            end,
                            null,
                            null,
                            duration,
                            period);
                    bars.putAll(response.getBars());
                    token = response.getNextPageToken();
                } while (token != null && !token.isEmpty());
                return Response.of(bars);
            }
        }
        catch (AlpacaClientException e) {
            return Response.exception(e);
        }
    }
}
