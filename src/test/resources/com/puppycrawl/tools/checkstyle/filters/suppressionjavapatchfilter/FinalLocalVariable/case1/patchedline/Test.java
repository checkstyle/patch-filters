package TreeWalker.coding.FinalLocalVariable;

public class Test {
    static int foo(int x, int y) {  // violation without filter
        return x+y;
    }
    public static void main (String [] args) {  // violation without filter
        for (String i : args) {
            System.out.println(i);
            System.out.println(i);
        }
        int result=foo(1,2);  // violation without filter
    }

    static int foo1(int x, int y) {  // violation without filter
        return x+y;
    }
}
