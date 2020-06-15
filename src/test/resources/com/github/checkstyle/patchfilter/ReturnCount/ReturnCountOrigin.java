package com.github.checkstyle.ReturnCount;

public class ReturnCountOrigin {
    public void fun(int error) {
        if (error > 100) {
            return;
        } else if (error > 50) {
            return;
        } else if (error > 40) {
            System.out.println("ok");
        }
    }

}
