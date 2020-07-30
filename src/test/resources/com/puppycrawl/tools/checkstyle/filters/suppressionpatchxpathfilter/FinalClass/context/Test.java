package TreeWalker.FinalClass;

public class Test {  // violation
    private Test() {}
}

class Test1 {  // violation
    private Test1() {}
}

class Test2 {  // violation
    private Test2() {
        System.out.println();
    }
}

class Test3 {  // violation
    private Test3() {}
    private int a;
}
