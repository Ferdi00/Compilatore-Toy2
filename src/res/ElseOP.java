package res;

public class ElseOP extends Node{
    public ElseOP(Node body) {
        super("ElseOP");
        this.addChild(body);
    }
}
