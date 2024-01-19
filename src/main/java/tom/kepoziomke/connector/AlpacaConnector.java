package tom.kepoziomke.connector;

import net.jacobpeterson.alpaca.AlpacaAPI;

public class AlpacaConnector {

    private final AlpacaWriteOnlyConnector writeConnector;
    private final AlpacaReadOnlyConnector readConnector;

    public AlpacaConnector() {
        AlpacaAPI api = new AlpacaAPI();
        writeConnector = new AlpacaWriteOnlyConnector(api);
        readConnector = new AlpacaReadOnlyConnector(api);
    }

    public AlpacaConnector(AlpacaAPI api) {
        writeConnector = new AlpacaWriteOnlyConnector(api);
        readConnector = new AlpacaReadOnlyConnector(api);
    }

    public AlpacaReadOnlyConnector read() {
        return this.readConnector;
    }

    public AlpacaWriteOnlyConnector write() {
        return this.writeConnector;
    }
}
