package TreeWalker.coding.SimplifyBooleanReturn;

public class Test {
    public boolean test(boolean cond) {
        if (cond) {  // violation
            return true;
        }
        else {
            return false;
        }
    }
}
