package tom.kepoziomke.connector;

import net.jacobpeterson.alpaca.AlpacaAPI;

public class AlpacaWriteOnlyConnector extends AlpacaConnectorBase {

    public AlpacaWriteOnlyConnector() {
        super();
    }

    public AlpacaWriteOnlyConnector(AlpacaAPI api) {
        super(api);
    }

}
