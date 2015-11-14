package comp;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */
public class CompilationError {


    private static final long serialVersionUID = 1L;
    private String message;
    private int lineNumber;
    private String lineWithError;

    public CompilationError( String message, int lineNumber, String lineWithError ) {
        this.message = message;
        this.lineNumber = lineNumber;
        this.lineWithError = lineWithError;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage( String message ) {
        this.message = message;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber( int lineNumber ) {
        this.lineNumber = lineNumber;
    }

    public String getLineWithError() {
        return lineWithError;
    }

    public void setLineWithError( String lineWithError ) {
        this.lineWithError = lineWithError;
    }

}
