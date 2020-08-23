package TreeWalker.InnerTypeLast;

public class Test {
}

class Test1 {
    private String s; // OK
    class InnerTest1 {}
    public void test() {} // violation without filter
}

class Test2 {
    private String s; // OK
    class InnerTest1 {}
    public void test() {} // violation without filter
}
