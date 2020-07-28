package TreeWalker.coding.SimplifyBooleanReturn;

public class Test {
    public boolean test(boolean cond) {
        if (cond) {  // violation context
            return true;
        }
        else {
            return false;
        }
    }
}
