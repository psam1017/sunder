package psam.portfolio.sunder.english.global.api;

/**
 * Create a business exception by inheriting ApiException so that the controller advice can catch the exception.
 * By implementing abstract method named 'initialize', the constructor will automatically specify that ApiResponse.
 */
public abstract class ApiException extends RuntimeException {

    private final ApiResponse<?> response;

    public ApiException() {
        response = initialize();
    }

    public abstract ApiResponse<?> initialize();

    public ApiResponse<?> getResponse() {
        return response;
    }
}
