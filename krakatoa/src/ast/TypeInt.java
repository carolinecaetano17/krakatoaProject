package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */
public class TypeInt extends Type {

    public TypeInt() {
        super( "int" );
    }

    public String getCname() {
        return "int";
    }

}