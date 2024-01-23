package tom.kepoziomke.algorithm;

import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderSide;

public class ActiveResult implements AlgorithmResult {

    private final String symbol;
    private final OrderSide side;
    private final double quantity;
    private final boolean crypto;

    public ActiveResult(String symbol, OrderSide side, double quantity, boolean crypto) {
        this.symbol = symbol;
        this.side = side;
        this.quantity = quantity;
        this.crypto = crypto;
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

    public boolean isCrypto() {
        return crypto;
    }

    @Override
    public String toString() {
        return "ActiveResult: " + symbol + " " + side.value() + " " + quantity;
    }
}
