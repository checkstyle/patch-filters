package TreeWalker.CommentsIndentation;

    // violation without filter
public class Test {
    public void test() {
        try {
        int a;
            // violation without filter
        } catch (Exception ex) {}

        try {
        int a;
            // violation without filter
        } catch (Exception ex) {}
    }

        // violation without filter
    public void test2() {
        if (true) {
        int a;
            // violation without filter
        }
    }

    public void test3() {
    int a;
        // violation without filter
    }
}

class Test2 {
int a;
    // violation without filter
}
