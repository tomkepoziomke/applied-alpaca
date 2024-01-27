# TradingBot
An Applied Java project by Tomasz Leniart.

## A brief overview of the project structure

`Connector` is the main wrapper around the `AlpacaAPI` object. It is there for two main reasons:

1. To provide simplified Alpaca Market interface for the purpose of this project,
2. To separate read and write requests (more on that later).

All requests are wrapped in the `Response` object. In case an error occurs, any method using the connector, can detect it.
This object is sort of an "extended" `Optional`, where either data or error info is stored.

The center-point of the application is the `AlpacaApp` class. Inside, it stores an arbitrary number of algorithms.

Each algorithm implements the `Algorithm` interface, which contains the `run` method.
This method takes as an argument a `ReadOnlyConnector` object and returns a list of `AlgorithmResult` objects.
This ensures that the algorithm does not actually perform buy/sell operations by itself (single responsibility).
Instead, it only reads the market data, and then delegates the result to the `AlpacaApp`, which actually handles the result by "talking" to the `AlpacaAPI` object.

The `OutputHandler` interface is there exactly for this purpose, and a basic handler, which checks the account status, cash, etc., has been implemented (`BasicOutputHandler`).

All algorithms are working asynchronously, and this is achieved by using Java's scheduled thread pool executor.

## Example algorithm

The algorithm implemented operates on crypto market assets because it's always available and easier to test. 
A single instance of an algorithm observes exactly one symbol on the market.
It gets one-minute bars for the previous 45 minutes and then calculates the symbol's fluctuations by adding open-close and close-open price differences.
If the recent importance coefficient is positive, more recent bars will have greater impact on the fluctuation metric.
If the fluctuations are big, e.g. the metric is below the threshold, and the price has been increasing, we sell some of the symbol.
If the fluctuations are small and the price is decreasing, we buy some of the symbol.