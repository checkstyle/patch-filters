package TreeWalker.VariableDeclarationUsageDistance;

public class Test {
    public void test(int a, int b) {
        int c;
        int d;  // violation without filter
        a = a + b;
        b = a + a;
        System.out.println(a);
        System.out.println(b);
        d = b;
    }

    public void test2(int a, int b) {
        int c;
        int d;  // violation without filter
        a = a + b;
        b = a + a;
        System.out.println(a);
        d = b;
    }
}
