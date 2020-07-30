package TreeWalker.VariableDeclarationUsageDistance;

public class Test {
    public void test(int a, int b) {
        int count;
        int size;
        a = a + b;
        b = a + a;
        System.out.println(a);
        System.out.println(b);
        count = b;  // violation without filter
        size = b;  // violation without filter
    }
}
