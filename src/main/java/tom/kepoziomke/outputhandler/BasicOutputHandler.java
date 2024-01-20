package tom.kepoziomke.outputhandler;

import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderSide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tom.kepoziomke.algorithm.ActiveResult;
import tom.kepoziomke.algorithm.AlgorithmResult;
import tom.kepoziomke.connector.Connector;

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
                    if (asset.isEmpty())
                        return;

                    // Check if we can obtain account information.
                    var account = connector.read().account();
                    if (account.isEmpty() || account.get().getAccountBlocked() || account.get().getTradingBlocked())
                        return;

                    if (active.getSide() == OrderSide.BUY)
                        connector.write().buyOrder(symbol, active.getQuantity());
                    else if (active.getSide() == OrderSide.SELL)
                        connector.write().sellOrder(symbol, active.getQuantity());
                }
                case null, default -> {}
            }

        }
        catch (InterruptedException e) {
            synchronized (logger) {
                logger.info("Interruption inside output handler", e);
            }
        }
    }
}
