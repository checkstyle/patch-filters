package TreeWalker.coding.EqualsHashCode;

public class Test {
    public boolean equals(String o) {
        return false;
    }

    public int hashCode() {  // violation without filter
        return 1;
    }
}

class Test1 {
    public int hashCode() {  // violation without filter
        return 1;
    }

    public boolean equals(Integer o) {
        return false;
    }
}
