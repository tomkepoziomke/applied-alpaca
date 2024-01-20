package tom.kepoziomke.connector;

import net.jacobpeterson.alpaca.AlpacaAPI;

public class Connector {

    private final WriteOnlyConnector writeConnector;
    private final ReadOnlyConnector readConnector;

    public Connector() {
        AlpacaAPI api = new AlpacaAPI();
        writeConnector = new WriteOnlyConnector(api);
        readConnector = new ReadOnlyConnector(api);
    }

    public Connector(AlpacaAPI api) {
        writeConnector = new WriteOnlyConnector(api);
        readConnector = new ReadOnlyConnector(api);
    }

    public ReadOnlyConnector read() {
        return this.readConnector;
    }

    public WriteOnlyConnector write() {
        return this.writeConnector;
    }
}
