package TreeWalker.coding.SimplifyBooleanReturn;

public class Test {
    public boolean test(boolean cond) {
        if (cond)  {  // violation without filter
            return true;
        }
        else {
            return false;
        }
    }
}

class Test1 {
    public boolean test1(boolean cond1) {
        if (cond1) {  // violation
            return true;
        }
        else {
            return false;
        }
    }
}
