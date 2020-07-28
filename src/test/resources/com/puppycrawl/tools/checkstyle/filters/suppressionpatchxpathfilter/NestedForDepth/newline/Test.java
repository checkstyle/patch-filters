package TreeWalker.coding.NestedForDepth;

public class Test {
    public void test() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                for (int k = 0; k < 10; k++){  // violation without filter
                    System.out.println();
                }
            }
        }
    }

    public void test1() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                for (int k = 0; k < 10; k++){  // violation without filter
                }
            }
        }
    }

    public void test2() {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                for (int k = 0; k < 10; k++){  // violation without filter
                    for (int l = 0; l < 10; l++) {  // violation without filter

                    }
                }
            }
        }
    }
}
