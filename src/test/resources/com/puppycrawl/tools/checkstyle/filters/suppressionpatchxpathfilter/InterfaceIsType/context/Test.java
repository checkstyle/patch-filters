package TreeWalker.InterfaceIsType;

public class Test {
}

interface Test1 {  // violation
    int a = 32;
}

interface Test2 { // violation

    int a = 32;
}

interface Test3 { // violation
    int a = 3;
}
