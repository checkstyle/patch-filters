package Checker.RegexpMultiline;

public class Test {
    void method() {
        System.out.
                print("Example"); // line3
        // violations without filter are on code below and line 5
        System.out.
                print("Example"); // line3
        // violation without filter is on code below
        System.out.
                print("Example"); // line3




        // violation without filter is on code below
        System.out.
                print("Example");







        // violation without filter is on code below
        System.out.
                print("Example");



    }
}
