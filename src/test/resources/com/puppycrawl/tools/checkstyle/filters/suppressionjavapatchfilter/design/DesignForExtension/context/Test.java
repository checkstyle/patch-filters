package TreeWalker.design.DesignForExtension;

public class Test extends TreeWalker.design.DesignForExtension.B {
    public int foo() {  // violation without filter
        return 2;
    }
}

class B {
    /**
     * This implementation ...
     @return some int value.
     */
    public int foo() {
        return 1;
    }
}