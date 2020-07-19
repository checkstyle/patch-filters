package Checker.RegexpMultiline;

public class Test {
    void method() {
        System.out.
                print("Example"); // line3

        System.out.
                print("Example"); // line3

        System.out.
                print("Example"); // line3





        System.out.  // violation patchedline
                print("Example");







        ////
        System.out.  // violation newline/patchedline
                print("Example");



    }
}
