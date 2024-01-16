package tom.kepoziomke.algorithms;

import net.jacobpeterson.alpaca.model.endpoint.assets.Asset;
import net.jacobpeterson.alpaca.model.endpoint.marketdata.stock.historical.bar.MultiStockBarsResponse;
import net.jacobpeterson.alpaca.model.endpoint.orders.Order;
import tom.kepoziomke.AlpacaConnector;

import java.util.List;

public abstract class AlpacaAlgorithm {
    public abstract void run(AlpacaConnector connector);
}
