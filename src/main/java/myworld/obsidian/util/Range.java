package myworld.obsidian.util;

public record Range<T>(T start, T end) {

    public static int clamp(int a, int b, int c){
        var dup = a;
        a = Math.min(a, c);
        c = Math.max(dup, c);

        return Math.min(Math.max(a, b), c);
    }
}
