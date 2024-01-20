package tom.kepoziomke;

import net.jacobpeterson.alpaca.model.endpoint.assets.enums.AssetClass;
import net.jacobpeterson.alpaca.model.endpoint.orders.Order;
import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderSide;
import net.jacobpeterson.alpaca.model.endpoint.positions.Position;
import tom.kepoziomke.algorithm.ActiveResult;
import tom.kepoziomke.algorithm.Algorithm;
import tom.kepoziomke.algorithm.AlgorithmResult;
import tom.kepoziomke.algorithm.EmptyResult;
import tom.kepoziomke.connector.ReadOnlyConnector;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class SillyCryptoAlgorithm implements Algorithm {

    private List<String> symbols;

    public SillyCryptoAlgorithm(List<String> symbols) {
        this.symbols = symbols;
    }

    @Override
    public AlgorithmResult run(ReadOnlyConnector connector) {
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
        Random rnd = new Random();
        boolean buy = rnd.nextBoolean();
        String symbol = symbols.get(rnd.nextInt(symbols.size()));
        double quantity = rnd.nextDouble() / 100;
        if (symbol.equals("DOGE/USD")) {
            quantity *= 10000; // FOR DEBUG
        }
        if (buy) {
            var account = connector.account();
            var cryptoQuotes = connector.latestCryptoQuote(symbol);
            if (account.isPresent() && cryptoQuotes.isPresent()) {
                BigDecimal cash = new BigDecimal(account.get().getCash());
                double askPrice = cryptoQuotes.get().getQuotes().get(symbol).getAskPrice();
                BigDecimal totalPrice = new BigDecimal(quantity * askPrice);
                if (cash.compareTo(totalPrice) > 0) {
                    return new ActiveResult(symbol, OrderSide.BUY, quantity);
                }
            }
        }
        else {
            var positions = connector.positions();
            if (positions.isPresent()) {
                Optional<Position> pos = positions.get().stream().filter(position -> position.getSymbol().equals(symbol.replace("/", ""))).findFirst();
                if (pos.isPresent())
                    return new ActiveResult(symbol, OrderSide.SELL, Math.max(quantity, Double.parseDouble(pos.get().getQuantity())));
            }
        }
        return new EmptyResult();
    }
}
