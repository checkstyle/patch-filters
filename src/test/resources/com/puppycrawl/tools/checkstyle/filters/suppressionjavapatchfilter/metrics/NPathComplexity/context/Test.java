package TreeWalker.metrics.NPathComplexity;

public class Test {
    public void test(int a, int b) {  // violation without filter
        if (a > 10) {
            if (a > b) {
                System.out.println(a);
            } else {
                System.out.println(b);
            }
        }
    }
}
