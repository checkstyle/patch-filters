package com.github.checkstyle.patchfilter;

public class InputCaseThreeUpdate {
    public void fun(boolean valid) {
        check(valid);
    }
    private void check(boolean valid) {
        if (valid) {
            {
                System.out.println("ok");
            }
        }
    }
}
