package exception;

/**
 * A custom exception thrown from any of the Connect Four classes if something
 * goes wrong.
 *
 * @author Soumya Dayal
 * username: sd9829
 * @author Kelsey Donovan
 * username: ksd4250
 */


public class WhackMoleException extends Exception{

    /**
     * Convenience constructor to create a new {@link WhackMoleException}
     * with an error message.
     *
     * @param message The error message associated with the exception.
     */
    public WhackMoleException(String message) {
        super(message);
    }

    /**
     * Convenience constructor to create a new {@link WhackMoleException}
     * as a result of some other exception.
     *
     * @param cause The root cause of the exception.
     */
    public WhackMoleException(Throwable cause) {
        super(cause);
    }

    /**
     * * Convenience constructor to create a new {@link WhackMoleException}
     * as a result of some other exception.
     *
     * @param message The message associated with the exception.
     * @param cause The root cause of the exception.
     */
    public WhackMoleException(String message, Throwable cause) {
        super(message, cause);
    }
}
