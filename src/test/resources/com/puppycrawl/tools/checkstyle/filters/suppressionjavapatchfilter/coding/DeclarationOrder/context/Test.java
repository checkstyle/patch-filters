package TreeWalker.coding.DeclarationOrder;

public class Test {
    int a;
    void foo(){}
    Test(){}  // violation without filter
    int b;  // violation without filter
    int c;  // violation without filter
}

class Test2 {
    int a;
    void foo(){}
    Test(){}  // violation without filter
}
