package tom.kepoziomke.app;

import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderSide;
import net.jacobpeterson.alpaca.model.endpoint.positions.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tom.kepoziomke.algorithm.ActiveResult;
import tom.kepoziomke.algorithm.AlgorithmResult;
import tom.kepoziomke.connector.Connector;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Basic implementation of the output handler.
 */
public class BasicOutputHandler implements OutputHandler {

    private final Connector connector;

    /**
     * Constructor.
     * @param connector Connector via which the handler handles API requests.
     */
    public BasicOutputHandler(Connector connector) {
        this.connector = connector;
    }

    /**
     * Processes the list of algorithm results. Checks if the account is eligible for market transactions,
     * filters unknown symbols, checks if we have sufficient positions, tries to make transactions if we do.
     * @param results The list of algorithm results.
     */
    @Override
    public synchronized void output(List<AlgorithmResult> results) {
        Logger logger = LoggerFactory.getLogger(BasicOutputHandler.class);
        for (AlgorithmResult result : results) {
            switch (result) {
                case ActiveResult active -> {
                    String symbol = active.getSymbol();
                    // Check if the asset exists in Alpaca database.
                    var asset = connector.read().asset(symbol);
                    if (asset.isEmpty()) {
                        logger.error(asset.exception().getMessage());
                        return;
                    }

                    // Check if we can obtain account information.
                    var account = connector.read().account();
                    if (account.isEmpty()) {
                        logger.error(account.exception().getMessage());
                        return;
                    } else if (account.get().getAccountBlocked() || account.get().getTradingBlocked()) {
                        logger.info("The account is unavailable for trading.");
                        return;
                    }

                    if (active.getSide() == OrderSide.BUY) {
                        // Check if we have sufficient funds and buy if we do.
                        BigDecimal cash = new BigDecimal(account.get().getCash());
                        BigDecimal totalPrice;

                        if (active.isCrypto()) {
                            var cryptoQuotes = connector.read().latestCryptoQuote(symbol);
                            if (cryptoQuotes.isEmpty()) {
                                logger.error(cryptoQuotes.exception().getMessage());
                                return;
                            }
                            double askPrice = cryptoQuotes.get().getQuotes().get(symbol).getAskPrice();
                            totalPrice = new BigDecimal(active.getQuantity() * askPrice);
                        } else {
                            var stockQuotes = connector.read().latestStockQuote(symbol);
                            if (stockQuotes.isEmpty()) {
                                logger.error(stockQuotes.exception().getMessage());
                                return;
                            }
                            double askPrice = stockQuotes.get().getQuote().getAskPrice();
                            totalPrice = new BigDecimal(active.getQuantity() * askPrice);
                        }
                        if (cash.compareTo(totalPrice) > 0) {
                            var response = connector.write().buyOrder(symbol, active.getQuantity(), active.isCrypto());
                            if (response.isPresent())
                                logger.info("Successfully bought " + active.getQuantity() + " of " + active.getSymbol());
                            else
                                logger.error("Error while placing buy order: " + response.exception().getMessage() + " " + active);

                        } else
                            logger.info("Not enough cash to buy " + active.getQuantity() + " of " + active.getSymbol());
                    } else if (active.getSide() == OrderSide.SELL) {
                        // Check if we want to sell more quantity than we actually own.
                        var positions = connector.read().positions();
                        if (positions.isPresent()) {
                            Optional<Position> pos = positions.
                                    get().
                                    stream().
                                    filter(position -> position.getSymbol().equals(symbol.replace("/", ""))).
                                    findFirst();
                            if (pos.isPresent()) {
                                var response = connector.write().sellOrder(
                                        symbol,
                                        Math.min(active.getQuantity(), Double.parseDouble(pos.get().getQuantity())),
                                        active.isCrypto());
                                if (response.isPresent())
                                    logger.info("Successfully sold " + active.getQuantity() + " of " + active.getSymbol());
                                else
                                    logger.error("Error while placing sell order: " + response.exception().getMessage() + " " + active);
                            }
                        }
                    }
                }
                case null, default -> {
                }
            }
        }
    }
}
