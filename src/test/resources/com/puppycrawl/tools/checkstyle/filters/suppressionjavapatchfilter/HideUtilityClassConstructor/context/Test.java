package TreeWalker.design.HideUtilityClassConstructor;

class Test {  // violation without filter

    public Test() {}

    public static void fun() {}
}

class Foo {

    private Foo() {}

    static int n;
}

