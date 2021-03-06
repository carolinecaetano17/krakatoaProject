package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

abstract public class Type extends ASTNode {

    public static Type booleanType = new TypeBoolean();
    public static Type intType = new TypeInt();
    public static Type stringType = new TypeString();
    public static Type voidType = new TypeVoid();
    public static Type undefinedType = new TypeUndefined();
    private String name;

    public Type( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    abstract public String getCname();
}
