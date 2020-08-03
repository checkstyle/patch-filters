package TreeWalker.FinalClass;

public class Test {  // violation without filter
    private Test() {}
}

class Test1 {  // violation without filter
    private Test1() {}
}

class Test2 {  // violation without filter
    private Test2() {
        System.out.println();
    }
}

class Test3 {  // violation without filter
    private Test3() {}
    private int a;
}

class Test4 {  // violation without filter
    private Test4() {}
}
