package com.github.checkstyle.patchfilter;

public class InputCaseTwoOrigin {
    public void fun(boolean valid) {
        if (valid) {
            {
                System.out.println("ok");
            }
        }
    }
}
