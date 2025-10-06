package plz.lizi.objhandles.api.common;

public class ThreadLocal<T> {
    private T obj;
    private final String space;

    public ThreadLocal(String spaceIn, T objIn) {
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
