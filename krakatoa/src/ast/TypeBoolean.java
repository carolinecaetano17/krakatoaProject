package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

public class TypeBoolean extends Type {

    public TypeBoolean() {
        super( "boolean" );
    }

    @Override
    public String getCname() {
        return "boolean";
    }

}
