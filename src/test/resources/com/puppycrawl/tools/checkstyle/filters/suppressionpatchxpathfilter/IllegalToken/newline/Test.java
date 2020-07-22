package TreeWalker.illegalToken;

public class Test {
    public void myTest() {
        outer:
        for (int i = 0; i < 5; i++) {
            if (i == 1) {
                break outer;
            }
        }
    }


    public void Test() {
        outer:  // violation newline/patchedline
        for (int i = 0; i < 5; i++) {
            if (i == 1) {
                break outer;
            }
        }
    }
}
