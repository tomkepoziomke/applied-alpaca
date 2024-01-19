package tom.kepoziomke.connector;

import net.jacobpeterson.alpaca.AlpacaAPI;

public class AlpacaConnectorBase {
    protected final AlpacaAPI api;

    public AlpacaConnectorBase() {
        this.api = new AlpacaAPI();
    }

    public AlpacaConnectorBase(AlpacaAPI api) {
        this.api = api;
    }

}
