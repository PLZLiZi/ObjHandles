package plz.lizi.objhandles.api.helfy;

public interface NativeLibrary {
    String name();

    long lookup(String entry);

    default boolean open() {
        return false;
    }
}
