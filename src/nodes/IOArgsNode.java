package nodes;


import java.util.List;

public class IOArgsNode extends Node {

    public final List<Node> exprNodeList;
    public final List<Boolean> dollarSignList;

    public IOArgsNode(List<Node> exprNodeList, List<Boolean> dollarSignList) {
        this.exprNodeList = exprNodeList;
        this.dollarSignList = dollarSignList;
    }

    public IOArgsNode aggiungi(IOArgsNode ioArgsNode) {
        this.exprNodeList.addAll(ioArgsNode.exprNodeList);
        this.dollarSignList.addAll(ioArgsNode.dollarSignList);
        return this;
    }

    @Override
    public String toString() {
        return "IOArgsNode{" +
                "exprNodeList=" + exprNodeList +
                ", dollarSignList=" + dollarSignList +
                '}';
    }

    public List<Node> getExprNodeList(){
        return exprNodeList;
    }

    public List<Boolean> getDollarSignList(){
        return dollarSignList;
    }

}
