package TreeWalker.illegalToken;

public class Test {
    public void myTest() {
        outer:  // violation without filter
        for (int i = 0; i < 5; i++) {
            if (i == 1) {
                break outer;
            }
        }
    }

    public void test() {
        outer:  // violation without filter
        for (int i = 0; i < 3; i++) {
            if (i == 2) {
                break outer;
            }
        }
    }
}
