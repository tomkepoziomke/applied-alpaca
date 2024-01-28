package tom.kepoziomke.connector;

import net.jacobpeterson.alpaca.rest.AlpacaClientException;

/**
 * Objects of this class contain responses from the API.
 * @param <T> The expected type of the object in the response.
 */
public class Response<T> {
    private final T value;
    private final AlpacaClientException exception;

    /**
     * Constructor of a successful response.
     * @param value The object contained in a response.
     */
    public Response(T value) {
        this.value = value;
        this.exception = null;
    }

    /**
     * Constructor of an unsuccessful response.
     * @param exception The error info contained in a response.
     */
    public Response(AlpacaClientException exception) {
        this.value = null;
        this.exception = exception;
    }

    /**
     * Constructs a new unsuccessful response.
     * @param exception The error info contained in a response.
     * @return The response.
     * @param <T> The expected type of the object in the response.
     */
    public static <T> Response<T> exception(AlpacaClientException exception) {
        return new Response<>(exception);
    }

    /**
     * Constructs a new successful response.
     * @param value The object contained in a response.
     * @return The response.
     * @param <T> The expected type of the object in the response.
     */
    public static <T> Response<T> of(T value) {
        return new Response<>(value);
    }

    /**
     * Whether the response is unsuccessful.
     * @return Whether the response doesn't contain a value.
     */
    public boolean isEmpty() {
        return (value == null);
    }

    /**
     * Whether the response is successful.
     * @return Whether the response contains a value.
     */
    public boolean isPresent() {
        return (value != null);
    }

    /**
     * Returns the value of the response.
     * @return The value.
     */
    public T get() {
        return value;
    }

    /**
     * Returns the exception info.
     * @return The exception info.
     */
    public AlpacaClientException exception() {
        return exception;
    }

}
