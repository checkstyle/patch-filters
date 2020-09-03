package TreeWalker.VariableDeclarationUsageDistance;

public class Test {
    static {
        int a = 0;
        int b = 1;
        int c = 2;
        int d = 3;  // violation without filter
        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        d = b;
    }

    static {
        int a = 0;
        int b = 1;
        int c = 2;
        int d = 3;  // violation without filter
        System.out.println(a);
        System.out.println(b);
        System.out.println(c);
        d = b;
    }
}
