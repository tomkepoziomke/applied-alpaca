package tom.kepoziomke.algorithm;

import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderSide;

public class ActiveResult implements AlgorithmResult {

    private final String symbol;
    private final OrderSide side;
    private final double quantity;


    public ActiveResult(String symbol, OrderSide side, double quantity) {
        this.symbol = symbol;
        this.side = side;
        this.quantity = quantity;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getSymbol() {
        return symbol;
    }

    public OrderSide getSide() {
        return side;
    }

    @Override
    public String toString() {
        return "ActiveResult: " + symbol + " " + side.value() + " " + quantity;
    }
}
