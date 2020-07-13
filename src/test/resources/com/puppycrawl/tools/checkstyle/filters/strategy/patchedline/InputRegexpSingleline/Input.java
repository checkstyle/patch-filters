class Input {
    public void a() {
        System.out.print("changed"); // violation, violation as it changed in patch
    }

    public boolean b() {
        System.out.print("b"); // violation as it added in patch
        return false;
    }

    public void c() {
        System.out.print("c"); // violation as it added in patch
    }
}
