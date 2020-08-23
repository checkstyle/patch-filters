package TreeWalker.coding.SuperClone;

public class Test {
    public Object clone() throws CloneNotSupportedException {  // violation without filter
        return new Object();
    }
}

class Test1 {
    public Object clone() throws CloneNotSupportedException {  // violation without filter
        return new Object();
    }
}

class Test2 {
    public Integer clone() throws CloneNotSupportedException {
        return (Integer) super.clone();
    }
}

