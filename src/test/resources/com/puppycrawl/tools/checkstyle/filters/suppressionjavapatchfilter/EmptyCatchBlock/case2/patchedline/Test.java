package TreeWalker.blocks.EmptyCatchBlock;

import java.io.IOException;

public class Test {
    private void foo() {
        try {
            throw new RuntimeException();
            // violation without filter at next line
        } catch (Exception expected) {
        }
    }

    private void foo1() {
        try {
            throw new RuntimeException();
            // violation without filter at next line
        } catch (Exception e) { }

    }

    private void foo2() {
        try {
            throw new IOException();
            // violation without filter at next line
        } catch (IOException | NullPointerException | ArithmeticException ignore) {

        }
    }

    private void foo3() { // comment
        try {
            throw new IOException();
        } catch (IOException | NullPointerException | ArithmeticException e) { //This is expected
        }
    }

    private void foo4() {
        try {
            throw new IOException();
        } catch (IOException | NullPointerException | ArithmeticException e) { /* This is expected*/
        }
    }

}
