package TreeWalker.coding.NestedIfDepth;

public class Test {
    public void test(int a) {
        if (a > 10) {
            if (a == 10) {
                if (a < 10) {  // violation without filter
                    System.out.println("a");
                }
            }
        }
    }

    public void test1(int a) {
        if (a > 10) {
            if (a == 10) {
                if (a < 10) {  // violation without filter
                }
            }
        }
    }

    public void test2(int a) {
        if (a > 10) {
            if (a == 10) {
                if (a < 10) { // violation without filter
                    if (a < 10) {  // violation without filter

                    }
                }
            }
        }
    }

    public void test3(int a) {
        if (a > 10) {
            if (a == 10) {
                if (a < 10) {  // violation without filter
                    System.out.println();
                }
            }
        }
    }
}
