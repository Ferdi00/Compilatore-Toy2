package res;

import java.util.LinkedList;

public class ProgramOP extends Node{
    public ProgramOP(Node iter1 , Node procedure , Node iter2) {
        super("program");
        this.addChild(iter1);
        this.addChild(procedure);
        this.addChild(iter2);
    }

}
