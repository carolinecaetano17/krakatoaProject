package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */
public class ClassType extends Type {
    public ClassType(String name) {
        super(name);
    }

    @Override
    public String getCname() {
        return "void *";
    }
}
