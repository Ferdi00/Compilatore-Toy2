package nodes;

public class IterOP extends Node{

    public IterOP() {
        super("Iter");
    }

    public void addNode(Node n){
        super.addChild(n);
    }

}
