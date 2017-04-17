package InputWriter;

/**
 * General purpose Exceptions for the InputWriter class.
 */
class InputWriterException extends RuntimeException{
    InputWriterException(String msg) {super(msg); }

    static InputWriterException error(String msgFormat, Object... arguments) {
        return new InputWriterException(String.format(msgFormat, arguments));
    }

}

