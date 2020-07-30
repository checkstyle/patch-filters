package TreeWalker.ThrowsCount;

public class Test {
    public void myFunction() throws CloneNotSupportedException,  // violation context
            ArrayIndexOutOfBoundsException,
            StringIndexOutOfBoundsException,

            IllegalStateException,
            NullPointerException {
        // body
    }

    public void myFunc() throws CloneNotSupportedException,  // violation context
            StringIndexOutOfBoundsException,
            StringIndexOutOfBoundsException,
            IllegalStateException,
            NullPointerException {
        // body
    }

    public void myFunc1() throws CloneNotSupportedException,  // violation context
            ClassNotFoundException,
            IllegalAccessException,
            ArithmeticException,
            ClassCastException {
        // body
        System.out.println();
    }
}
