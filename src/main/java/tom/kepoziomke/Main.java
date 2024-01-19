package tom.kepoziomke;

import tom.kepoziomke.algorithm.DebugAlgorithm;

public class Main {

    public static void main(String[] args)  {
        AlpacaApp app = new AlpacaApp();
        app.setAlgorithm(new DebugAlgorithm());
        app.start();
        // app.stop();
    }
}
