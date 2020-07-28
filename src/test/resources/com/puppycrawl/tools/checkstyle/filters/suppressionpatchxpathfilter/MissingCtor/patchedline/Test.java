package TreeWalker.coding.MissingCtor;

public class Test {  // violation without filter
    private int a;
}

class Test0 {  // violation without filter
    private int a;
}

class Test2 {  // violation without filter
    private String s;
    void Test() {
        s = "foobar";
    }
}

class Test3 {  // violation without filter
    private int a;
}
