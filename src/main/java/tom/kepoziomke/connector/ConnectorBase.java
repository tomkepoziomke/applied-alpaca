package tom.kepoziomke.connector;

import net.jacobpeterson.alpaca.AlpacaAPI;

public class ConnectorBase {
    protected final AlpacaAPI api;

    public ConnectorBase() {
        this.api = new AlpacaAPI();
    }

    public ConnectorBase(AlpacaAPI api) {
        this.api = api;
    }

}
