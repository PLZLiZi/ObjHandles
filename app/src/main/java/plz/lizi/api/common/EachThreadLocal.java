package plz.lizi.api.common;

public class EachThreadLocal<T> {
    private T obj;
    private final String space;

    public EachThreadLocal(String spaceIn, T objIn) {
        space = spaceIn;
        obj = objIn;
    }

    public void set(T objIn) {
        if (Thread.currentThread().getName().contains(space)){
            obj = objIn;
        }
    }

    public void set0(T objIn) {
        obj = objIn;
    }

    public T get() {
        return obj;
    }
}
