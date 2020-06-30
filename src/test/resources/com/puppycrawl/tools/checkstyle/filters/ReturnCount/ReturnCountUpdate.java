package com.puppycrawl.tools.checkstyle.patchfilter.ReturnCount;

public class ReturnCountUpdate {
    public void fun(int error) {
        if (error > 100) {
            return;
        } else if (error > 50) {
            return;
        } else if (error > 40) {
            System.out.println("ok");
            return;
        } else if (error > 30) {
            return;
        } else {
            System.out.println("pass");
        }
    }
}
