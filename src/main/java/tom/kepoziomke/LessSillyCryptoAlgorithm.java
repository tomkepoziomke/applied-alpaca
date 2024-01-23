package tom.kepoziomke;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.historical.bar.Bar;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.historical.bar.enums.BarTimePeriod;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.bar.CryptoBar;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderSide;
import tom.kepoziomke.algorithm.ActiveResult;
import tom.kepoziomke.algorithm.Algorithm;
import tom.kepoziomke.algorithm.AlgorithmResult;
import tom.kepoziomke.algorithm.EmptyResult;
import tom.kepoziomke.connector.ReadOnlyConnector;

import java.time.ZonedDateTime;
import java.util.*;

public class LessSillyCryptoAlgorithm implements Algorithm {

    private final String symbol;
    private final double multiplier;

    private static final int DEFAULT_BAR_TIME_PERIOD_DURATION = 15;
    private static final BarTimePeriod DEFAULT_BAR_TIME_PERIOD = BarTimePeriod.MINUTE;
    private static final int DEFAULT_BAR_HISTORY_PERIOD_DURATION = 6;
    private static final double DEFAULT_VARIANCE_THRESHOLD = 2.0;

    public LessSillyCryptoAlgorithm(String symbol, double multiplier) {
        this.symbol = symbol;
        this.multiplier = multiplier;
    }

    @Override
    public AlgorithmResult run(ReadOnlyConnector connector) {

        // Filter symbols for only those actually existing in the crypto market.
        var assets = connector.assets(true);
        if (assets.isPresent()) {
            var matching = assets.get().stream().filter (asset -> asset.getSymbol().equals(symbol)).findFirst();
            if (matching.isEmpty())
                return new EmptyResult();
        }
        else {
            return new EmptyResult();
        }

        var bars = connector.cryptoBars(List.of(symbol),
                ZonedDateTime.now().minusHours(DEFAULT_BAR_HISTORY_PERIOD_DURATION),
                ZonedDateTime.now(),
                DEFAULT_BAR_TIME_PERIOD_DURATION,
                DEFAULT_BAR_TIME_PERIOD);

        // Silly part begins here.
        Random rnd = new Random();

        ArrayList<CryptoBar> singleBars = bars.get().get(symbol);

        List<Double> prices = singleBars.stream().mapToDouble(bar -> (bar.getClose() - bar.getOpen())).boxed().toList();
        if (prices.size() <= 1)
            return new EmptyResult();

        // We normalize the observations to calculate their variance
        final double average = prices.stream().mapToDouble(x -> x).average().orElse(0);
        final double variance = prices.stream().mapToDouble(x -> (x - average) * (x - average)).average().orElse(1.0);

        boolean priceIncreased = singleBars.getLast().getVwap() >= singleBars.getFirst().getVwap();
        boolean stableVariance = variance <= DEFAULT_VARIANCE_THRESHOLD;

        if (priceIncreased && !stableVariance) {
            double quantity = Double.MAX_VALUE;
            return new ActiveResult(symbol, OrderSide.SELL, quantity, true);
        }
        else if (!priceIncreased && stableVariance) {
            double quantity = rnd.nextDouble() * multiplier;
            return new ActiveResult(symbol, OrderSide.BUY, quantity, true);
        }
        else
            return new EmptyResult();
    }
}
