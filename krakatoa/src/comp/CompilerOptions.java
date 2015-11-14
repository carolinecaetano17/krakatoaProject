package comp;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */
public class CompilerOptions {

    private boolean count, outputInterface, extractClass;

    public CompilerOptions() {
        count = false;
        outputInterface = false;
        extractClass = false;
    }

    public boolean getCount() {
        return count;
    }

    public void setCount( boolean count ) {
        this.count = count;
    }

    public boolean getOutputInterface() {
        return outputInterface;
    }

    public void setOutputInterface( boolean outputInterface ) {
        this.outputInterface = outputInterface;
    }

    public boolean getExtractClass() {
        return extractClass;
    }

    public void setExtractClass( boolean extractClass ) {
        this.extractClass = extractClass;
    }

}