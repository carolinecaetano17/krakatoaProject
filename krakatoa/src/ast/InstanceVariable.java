package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */
public class InstanceVariable extends Variable {
    private boolean isStatic;

    public InstanceVariable( String name, Type type ) {
        super( name, type );
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setIsStatic( boolean isStatic ) {
        this.isStatic = isStatic;
    }

    public void genC( PW pw ) {
        pw.printlnIdent( getType().getName() + " " + getName() + ";" );
    }
}