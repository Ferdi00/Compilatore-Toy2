package res;

import java.util.List;

public class IfStatOP extends Node{

    public final List<Node> list1;
    public IfStatOP(Node expr, Node body, List<Node> elifs, ElseOP el) {
        super("IfStatOP");
        this.addChild(expr);
        this.addChild(body);
        this.addChild(el);
        list1 = elifs;
        this.setListname1 ("Elifs");
    }

}
