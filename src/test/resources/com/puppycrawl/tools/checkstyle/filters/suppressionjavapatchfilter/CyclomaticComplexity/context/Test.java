package TreeWalker.metrics.CyclomaticComplexity;

public class Test {
    public void test(int a, int b) {  // violation without filter
        if (a > b) {
            System.out.println(a);
        }
        else {
            System.out.println(b);
        }
    }
}
