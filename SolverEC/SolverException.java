package SolverEC;

/**
 * General purpose Exceptions for the InputWriter class.
 */
class SolverException extends RuntimeException{
    SolverException(String msg) {super(msg); }

    static SolverException error(String msgFormat, Object... arguments) {
        return new SolverException(String.format(msgFormat, arguments));
    }

}

