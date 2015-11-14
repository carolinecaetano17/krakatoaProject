/**

 */
package ast;

/* Authors:
 * Caroline Pessoa Caetano - 408247
 * Henrique Squinello - 408352
 */

import java.util.ArrayList;

/**
 * This class represents a metaobject call as <code>{@literal @}ce(...)</code> in <br>
 * <code>
 *
 * @author José
 * @ce(5, "'class' expected") <br>
 * clas Program <br>
 * public void run() { } <br>
 * end <br>
 * </code>
 */
public class MetaobjectCall extends ASTNode {

    private String name;
    private ArrayList<Object> paramList;

    public MetaobjectCall( String name, ArrayList<Object> paramList ) {
        this.name = name;
        this.paramList = paramList;
    }

    public ArrayList<Object> getParamList() {
        return paramList;
    }

    public String getName() {
        return name;
    }

}
