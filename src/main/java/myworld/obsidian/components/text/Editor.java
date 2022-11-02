package myworld.obsidian.components.text;

public interface Editor {

    void insert(int index, String s);
    void delete(int start, int end);
    String substring(int start, int end);
    int length();
    String toString();

}
