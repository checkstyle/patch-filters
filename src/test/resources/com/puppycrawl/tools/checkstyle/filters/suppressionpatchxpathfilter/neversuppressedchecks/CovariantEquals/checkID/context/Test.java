package TreeWalker.coding.CovariantEquals;

public class Test {
    public boolean equals(TreeWalker.coding.CovariantEquals.Test i) {  // violation context
        return false;
    }

}

class Test2 {
    public boolean equals(TreeWalker.coding.CovariantEquals.Test i) {  // violation context
        return false;
    }

//    public boolean equals(Object i) {
//        return false;
//    }
}
