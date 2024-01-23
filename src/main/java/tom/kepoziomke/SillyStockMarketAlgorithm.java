package tom.kepoziomke;

import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderSide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tom.kepoziomke.algorithm.ActiveResult;
import tom.kepoziomke.algorithm.Algorithm;
import tom.kepoziomke.algorithm.AlgorithmResult;
import tom.kepoziomke.algorithm.EmptyResult;
import tom.kepoziomke.connector.ReadOnlyConnector;

import java.util.List;
import java.util.Random;

public class SillyStockMarketAlgorithm implements Algorithm {

    private List<String> symbols;
    private Logger logger = LoggerFactory.getLogger(SillyStockMarketAlgorithm.class);

    public SillyStockMarketAlgorithm(List<String> symbols) {
        this.symbols = symbols;
    }

    @Override
    public AlgorithmResult run(ReadOnlyConnector connector) {
        // Filter symbols for only those actually existing in the stock market.
        var assets = connector.assets(false);
        if (assets.isPresent()) {
            symbols = symbols.
                    stream().
                    filter(symbol -> assets.
                            get().
                            stream().
                            anyMatch(asset -> asset.getSymbol().equals(symbol))).
                    toList();
            if (symbols.isEmpty())
                return new EmptyResult();
        }
        else {
            logger.info(assets.exception().getMessage());
            return new EmptyResult();
        }

        // Silly part begins here.
        // We randomly sell or buy 1-5 of random stock.
        Random rnd = new Random();
        boolean buy = rnd.nextBoolean();
        String symbol = symbols.get(rnd.nextInt(symbols.size()));
        double quantity = rnd.nextInt(1, 5);

        if (buy) {
            return new ActiveResult(symbol, OrderSide.BUY, quantity, false);
        }
        else {
            return new ActiveResult(symbol, OrderSide.SELL, quantity, false);
        }
    }
}
