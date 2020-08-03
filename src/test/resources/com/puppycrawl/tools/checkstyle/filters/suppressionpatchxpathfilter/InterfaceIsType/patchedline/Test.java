package TreeWalker.InterfaceIsType;

public class Test {
}

interface Test0 {  // violation without filter
    int a = 32;
}

interface Test2 { // violation without filter

    int a = 32;
}

interface Test3 { // violation without filter
    int a = 3;
}

interface Test4 { // violation without filter
    int a = 3;
}
