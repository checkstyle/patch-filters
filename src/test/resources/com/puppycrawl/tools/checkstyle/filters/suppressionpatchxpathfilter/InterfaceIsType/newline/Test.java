package TreeWalker.InterfaceIsType;

public class Test {
}

interface Test1 {  // violation context
    int a = 32;
}

interface Test2 { // violation context

    int a = 32;
}

interface Test3 { // violation context
    int a = 3;
}
