package tom.kepoziomke.connector;

import net.jacobpeterson.alpaca.AlpacaAPI;

/**
 * The union of read- and write-only connectors.
 */
public class Connector {

    private final WriteOnlyConnector writeConnector;
    private final ReadOnlyConnector readConnector;

    /**
     * Default constructor.
     */
    public Connector() {
        AlpacaAPI api = new AlpacaAPI();
        writeConnector = new WriteOnlyConnector(api);
        readConnector = new ReadOnlyConnector(api);
    }

    /**
     * Parametrized constructor, admitting the AlpacaAPI reference.
     * @param api The AlpacaAPI object reference.
     */
    public Connector(AlpacaAPI api) {
        writeConnector = new WriteOnlyConnector(api);
        readConnector = new ReadOnlyConnector(api);
    }

    /**
     * Returns read-only connector.
     * @return The read-only connector.
     */
    public ReadOnlyConnector read() {
        return this.readConnector;
    }

    /**
     * Returns write-only connector.
     * @return The write-only connector.
     */
    public WriteOnlyConnector write() {
        return this.writeConnector;
    }
}
