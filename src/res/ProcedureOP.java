package res;

import java.util.List;

public class ProcedureOP extends Node{
    public ProcedureOP(String id, Node pp, Node b) {
        super("Procedure-"+id);
        this.addChild(pp);
        this.addChild(b);
    }
}
