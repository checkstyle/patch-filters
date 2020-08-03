package Checker.RegexpMultiline;

public class Test {
    void method() {
        System.out.
                print("Example"); // line3

        System.out.
                print("Example"); // line3

        System.out.
                print("Example"); // line3




        // violation without filter is on line above changed
        System.out.
                print("Example");







        // violation without filter is on code below
        System.out.
                print("Example");



    }
}
