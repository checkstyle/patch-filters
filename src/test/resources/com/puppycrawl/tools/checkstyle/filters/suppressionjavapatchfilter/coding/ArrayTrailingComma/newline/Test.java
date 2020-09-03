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

    public void test1() {
        int[] a4 = new int[]{
                3,
                4  // violation without filter
        };
    }
}
