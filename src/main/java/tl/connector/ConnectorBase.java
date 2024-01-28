package tom.kepoziomke.connector;

import net.jacobpeterson.alpaca.AlpacaAPI;

/**
 * The base of the connector, containing the AlpacaAPI object.
 */
public class ConnectorBase {
    protected final AlpacaAPI api;

    /**
     * Default constructor.
     */
    public ConnectorBase() {
        this.api = new AlpacaAPI();
    }

    /**
     * Parametrized constructor, admitting AlpacaAPI object.
     * @param api The AlpacaAPI object.
     */
    public ConnectorBase(AlpacaAPI api) {
        this.api = api;
    }

}
