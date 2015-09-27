package au.com.scottellis.todoapp.storage.file;

/**
 * Interface for string converters
 */
public interface StringConverter<T> {
    String toString(T item) throws Exception;
    T fromString(String s) throws Exception;
}
