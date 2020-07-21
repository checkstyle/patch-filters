package Checker.RegexpMultiline;

public class Test {
    void method() {
        System.out.
                print("Example"); // line3

        System.out.
                print("Example"); // line3

        System.out.
                print("Example"); // line3




        // violation is on line above changed
        System.out.
                print("Example");







        // violation is on code below
        System.out.
                print("Example");



    }
}
