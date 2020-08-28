package TreeWalker.coding.ArrayTrailingComma;

public class Test {
    public void test() {
        int[] a2 = new int[]{
                1,
                2  // violation without filter
                };

        int[] a3 = new int[]{
                1,
                2  // violation without filter
        };
    }
}

class Test2 {
    int[] a3 = new int[]{
            1,
            2  // violation without filter
    };
}
