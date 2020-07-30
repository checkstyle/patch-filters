package TreeWalker.VariableDeclarationUsageDistance;

public class Test {
    public void test(int a, int b) {
        int count;
        a = a + b;
        b = a + a;
        System.out.println(a);
        System.out.println(b);
        count = b;  // violation context
    }
}
