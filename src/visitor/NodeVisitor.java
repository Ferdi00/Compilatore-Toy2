package visitor;

public interface NodeVisitor<T> {
    Object visitNode(T node);
}
