package TreeWalker.coding.OverloadMethodsDeclarationOrder;

public class Test {
    public void foo(int i) {} // OK
    public void foo(String s) {} // OK
    public void notFoo() {}
    public void foo(int i, String s) {}  // violation without filter
    public void foo(String s, int i) {}
}

class Test1 {
    public void foo(int i) {} // OK
    public void foo(String s) {} // OK
    public void notFoo() {}
    public void foo(int i, String s) {}  // violation without filter
    public void foo(String s, int i) {}
}
