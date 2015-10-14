package ast;

/**
 * Created by hsq on 10/11/15.
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
