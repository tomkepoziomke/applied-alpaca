package tom.kepoziomke;

import tom.kepoziomke.algorithm.Algorithm;
import tom.kepoziomke.algorithm.DebugAlgorithm;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws InterruptedException  {
        AlpacaApp app = new AlpacaApp();
        Algorithm myAlg = new DebugAlgorithm();
        app.addAlgorithm(myAlg, 1000, TimeUnit.MILLISECONDS);
        Thread.sleep(3000);
        app.stop();
    }
}
