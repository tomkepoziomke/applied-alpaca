package tom.kepoziomke.outputhandler;

import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderSide;
import net.jacobpeterson.alpaca.model.endpoint.positions.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tom.kepoziomke.algorithm.ActiveResult;
import tom.kepoziomke.algorithm.AlgorithmResult;
import tom.kepoziomke.connector.Connector;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class BasicOutputHandler implements OutputHandler {

    private final Connector connector;
    private final BlockingQueue<AlgorithmResult> results;
    public static final Integer DEFAULT_TIMEOUT_DURATION = 1000;
    public static final TimeUnit DEFAULT_TIMEOUT_UNIT = TimeUnit.MILLISECONDS;

    public BasicOutputHandler(Connector connector, BlockingQueue<AlgorithmResult> results) {
        this.connector = connector;
        this.results = results;
    }

    @Override
    public void output() {
        Logger logger = LoggerFactory.getLogger(BasicOutputHandler.class);
        try {
            AlgorithmResult res = results.poll(DEFAULT_TIMEOUT_DURATION, DEFAULT_TIMEOUT_UNIT);
            switch (res) {
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
                    }
                    else if (account.get().getAccountBlocked() || account.get().getTradingBlocked()) {
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
                        }
                        else {
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

                        }
                        else
                            logger.info("Not enough cash to buy " + active.getQuantity() + " of " + active.getSymbol());
                    }
                    else if (active.getSide() == OrderSide.SELL) {
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
                                    logger.error("Error while placing sell order: " + response.exception().getMessage() + " " + active  );
                            }
                        }
                    }
                }
                case null, default -> {}
            }

        }
        catch (InterruptedException e) {
            logger.error("Interruption inside output handler", e);
        }
    }
}
