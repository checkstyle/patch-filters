package TreeWalker.ThrowsCount;

public class Test {
    public void myFunction() throws CloneNotSupportedException,  // violation without filter
            ArrayIndexOutOfBoundsException,
            StringIndexOutOfBoundsException,

            IllegalStateException,
            NullPointerException {
        // body
    }

    public void myFunc() throws CloneNotSupportedException,  // violation without filter
            StringIndexOutOfBoundsException,
            StringIndexOutOfBoundsException,
            IllegalStateException,
            NullPointerException {
        // body
    }

    public void myFunc1() throws CloneNotSupportedException,  // violation without filter
            ClassNotFoundException,
            IllegalAccessException,
            ArithmeticException,
            ClassCastException {
        // body
        System.out.println();
    }
}
