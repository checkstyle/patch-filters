package TreeWalker.coding.FinalLocalVariable;

public class Test {
    static int foo(int x, int y) {  // violation without filter
        x = 1;
        return x+y;
    }
    public static void main (String []args) {  // violation without filter
        for (String i : args) {
            System.out.println(i);
        }
        int result=foo(1,2);  // violation without filter

        int myconst  // violation without filter
                = foo(1, 2);
    }
}
