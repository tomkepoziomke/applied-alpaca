package tom.kepoziomke;

import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderSide;
import tom.kepoziomke.algorithm.ActiveResult;
import tom.kepoziomke.algorithm.Algorithm;
import tom.kepoziomke.algorithm.AlgorithmResult;
import tom.kepoziomke.algorithm.EmptyResult;
import tom.kepoziomke.connector.ReadOnlyConnector;

import java.util.List;
import java.util.Random;

public class SillyCryptoAlgorithm implements Algorithm {

    private List<String> symbols;

    public SillyCryptoAlgorithm(List<String> symbols) {
        this.symbols = symbols;
    }

    @Override
    public AlgorithmResult run(ReadOnlyConnector connector) {
        // Filter symbols for only those actually existing in the crypto market.
        var assets = connector.assets(true);
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
            return new EmptyResult();
        }

        // Silly part begins here.
        Random rnd = new Random();
        boolean buy = rnd.nextBoolean();
        String symbol = symbols.get(rnd.nextInt(symbols.size()));
        double quantity = rnd.nextDouble() / 100;


        if (symbol.equals("DOGE/USD")) {    /// FOR DEBUG ONLY
            quantity *= 10000;              /// FOR DEBUG ONLY
        }                                   /// FOR DEBUG ONLY


        if (buy) {
            return new ActiveResult(symbol, OrderSide.BUY, quantity, true);
        }
        else {
            return new ActiveResult(symbol, OrderSide.SELL, quantity, true);
        }
    }
}
