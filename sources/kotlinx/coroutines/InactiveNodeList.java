package kotlinx.coroutines;

public final class InactiveNodeList implements Incomplete {
    private final NodeList list;

    public InactiveNodeList(NodeList nodeList) {
        this.list = nodeList;
    }

    @Override
    public NodeList getList() {
        return this.list;
    }

    @Override
    public boolean isActive() {
        return false;
    }

    public String toString() {
        return super.toString();
    }
}
