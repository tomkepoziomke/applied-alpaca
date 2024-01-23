package tom.kepoziomke.connector;

import net.jacobpeterson.alpaca.AlpacaAPI;
import net.jacobpeterson.alpaca.rest.AlpacaClientException;
import org.jetbrains.annotations.NotNull;

public class Response<T> {
    private final T value;
    private final AlpacaClientException exception;

    public Response(@NotNull T value) {
        this.value = value;
        this.exception = null;
    }

    public Response(@NotNull AlpacaClientException exception) {
        this.value = null;
        this.exception = exception;
    }

    public static <T> Response<T> exception(@NotNull AlpacaClientException exception) {
        return new Response<>(exception);
    }

    public static <T> Response<T> of(@NotNull T value) {
        return new Response<>(value);
    }

    public boolean isEmpty() {
        return (value == null);
    }

    public boolean isPresent() {
        return (value != null);
    }

    public T get() {
        return value;
    }

    public AlpacaClientException exception() {
        return exception;
    }

}
