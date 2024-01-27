package tom.kepoziomke;

import net.jacobpeterson.alpaca.model.endpoint.marketdata.common.historical.bar.enums.BarTimePeriod;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.crypto.historical.bar.CryptoBar;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderSide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tom.kepoziomke.algorithm.ActiveResult;
import tom.kepoziomke.algorithm.Algorithm;
import tom.kepoziomke.algorithm.AlgorithmResult;
import tom.kepoziomke.algorithm.EmptyResult;
import tom.kepoziomke.connector.ReadOnlyConnector;

import java.time.ZonedDateTime;
import java.util.*;

public class ExampleCryptoAlgorithm implements Algorithm {

    private final String symbol;
    private final double minimumQuantity;
    private final double maximumQuantity;
    private final double stabilityThreshold;
    private final double recentImportanceCoefficient;

    private static final int DEFAULT_BAR_TIME_PERIOD_DURATION = 1;
    private static final BarTimePeriod DEFAULT_BAR_TIME_PERIOD = BarTimePeriod.MINUTE;
    private static final int DEFAULT_BAR_HISTORY_PERIOD_DURATION = 45;

    public ExampleCryptoAlgorithm(String symbol,
                                  double minimumQuantity,
                                  double maximumQuantity,
                                  double stabilityThreshold,
                                  double recentImportanceCoefficient) {
        this.symbol = symbol;
        this.minimumQuantity = minimumQuantity;
        this.maximumQuantity = maximumQuantity;
        this.stabilityThreshold = stabilityThreshold;
        this.recentImportanceCoefficient = recentImportanceCoefficient;
    }

    @Override
    public List<AlgorithmResult> run(ReadOnlyConnector connector) {
        Logger logger = LoggerFactory.getLogger(ExampleCryptoAlgorithm.class);
        // Filter symbols for only those actually existing in the crypto market.
        var assets = connector.assets(true);
        if (assets.isPresent()) {
            var matching = assets.get().stream().filter (asset -> asset.getSymbol().equals(symbol)).findFirst();
            if (matching.isEmpty())
                return List.of(new EmptyResult());
        }
        else {
            return List.of(new EmptyResult());
        }

        // Getting the bars
        var bars = connector.cryptoBars(List.of(symbol),
                ZonedDateTime.now().minusMinutes(DEFAULT_BAR_HISTORY_PERIOD_DURATION),
                ZonedDateTime.now(),
                DEFAULT_BAR_TIME_PERIOD_DURATION,
                DEFAULT_BAR_TIME_PERIOD);

        Random rnd = new Random();
        ArrayList<CryptoBar> singleBars = bars.get().get(symbol);
        List<Double> openCloseIncrements = new ArrayList<>(singleBars.stream().
                mapToDouble(bar -> (bar.getClose() - bar.getOpen())).
                boxed().toList());
        List<Double> closeOpenIncrements = new ArrayList<>(openCloseIncrements.size() - 1);
        for (int i = 0; i < singleBars.size() - 1; i++)
           closeOpenIncrements.add(singleBars.get(i + 1).getOpen() - singleBars.get(i).getClose());

        // Applying importance function regarding recent results
        for (int i = 0; i < openCloseIncrements.size(); i++) {
            double increment = openCloseIncrements.get(i);
            openCloseIncrements.set(i, increment * (recentImportanceCoefficient * Math.log(1 + i / (double)(openCloseIncrements.size() + 1)) + 1));
        }

        for (int i = 0; i < closeOpenIncrements.size(); i++) {
            double increment = closeOpenIncrements.get(i);
            closeOpenIncrements.set(i, increment * (recentImportanceCoefficient * Math.log(1 + i / ((double)(closeOpenIncrements.size() + 1))) + 1));
        }

        double sumOfIncrements = openCloseIncrements.stream().mapToDouble(x -> x).sum() + closeOpenIncrements.stream().mapToDouble(x -> x).sum();
        double sumOfAbsoluteIncrements = openCloseIncrements.stream().mapToDouble(Math::abs).sum() + closeOpenIncrements.stream().mapToDouble(Math::abs).sum();

        double stabilityMetric = Math.abs(sumOfIncrements) / sumOfAbsoluteIncrements;
        boolean priceIncreased = sumOfIncrements > 0;
        boolean isStable = stabilityMetric > stabilityThreshold;
        boolean isUnstable = stabilityMetric < stabilityThreshold;

        logger.info(String.format("%s:\tsum: %.1e\tstability: %.3f", symbol, sumOfIncrements, stabilityMetric));
        if (priceIncreased && isUnstable) {
            double quantity = rnd.nextDouble() * (maximumQuantity - minimumQuantity) + minimumQuantity;
            return List.of(new ActiveResult(symbol, OrderSide.SELL, quantity, true));
        }
        else if (!priceIncreased && isStable) {
            double quantity = rnd.nextDouble() * (maximumQuantity - minimumQuantity) + minimumQuantity;
            return List.of(new ActiveResult(symbol, OrderSide.BUY, quantity, true));
        }
        else
            return List.of(new EmptyResult());
    }
}
