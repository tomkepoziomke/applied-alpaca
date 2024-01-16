package tom.kepoziomke;

import tom.kepoziomke.algorithms.SillyAlgorithm;

public class Main {

    public static void main(String[] args) {
        AlpacaApp app = new AlpacaApp();
        app.setAlgorithm(new SillyAlgorithm(2.0, 3.0));
        app.start();
    }
}
