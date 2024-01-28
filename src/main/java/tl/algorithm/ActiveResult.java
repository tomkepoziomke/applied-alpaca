package tom.kepoziomke.algorithm;

import net.jacobpeterson.alpaca.model.endpoint.orders.enums.OrderSide;

/**
 * Returns an active (e.g. revolving around sell and buy orders) result of an algorithm.
 */
public class ActiveResult implements AlgorithmResult {

    private final String symbol;
    private final OrderSide side;
    private final double quantity;
    private final boolean crypto;

    /**
     * Constructor.
     * @param symbol The symbol to be sold or bought.
     * @param side Whether we want to buy or sell.
     * @param quantity The quantity to be bought or sold.
     * @param crypto Whether the symbol is crypto or not.
     */
    public ActiveResult(String symbol, OrderSide side, double quantity, boolean crypto) {
        this.symbol = symbol;
        this.side = side;
        this.quantity = quantity;
        this.crypto = crypto;
    }

    /**
     * Returns the quantity.
     * @return The quantity.
     */
    public double getQuantity() {
        return quantity;
    }

    /**
     * Returns the symbol.
     * @return The symbol.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Returns the side.
     * @return The side.
     */
    public OrderSide getSide() {
        return side;
    }

    /**
     * Returns whether the symbol is crypto.
     * @return Whether the symbol is crypto.
     */
    public boolean isCrypto() {
        return crypto;
    }

    /**
     * String representation of the result.
     * @return String representation.
     */
    @Override
    public String toString() {
        return "ActiveResult: " + symbol + " " + side.value() + " " + quantity;
    }
}
