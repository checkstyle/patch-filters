package Checker.RegexpMultiline;

public class Test {
    void method() {
        System.out.
                print("Example"); // line3
        // violations are on code below and line 5
        System.out.
                print("Example"); // line3
        // violation is on code below
        System.out.
                print("Example"); // line3




        // violation is on code below
        System.out.
                print("Example");







        // violation is on code below
        System.out.
                print("Example");



    }
}
