package tom.kepoziomke.algorithm;

import net.jacobpeterson.alpaca.model.endpoint.orders.Order;

public class ActiveResult extends AlgorithmResult {

    public final Order order;

    public ActiveResult(Order order){
        this.order = order;
    }
}
