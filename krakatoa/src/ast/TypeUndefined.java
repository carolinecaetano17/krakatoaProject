package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */
public class TypeUndefined extends Type {
    // variables that are not declared have this type

    public TypeUndefined() {
        super( "undefined" );
    }

    public String getCname() {
        return "int";
    }

}
