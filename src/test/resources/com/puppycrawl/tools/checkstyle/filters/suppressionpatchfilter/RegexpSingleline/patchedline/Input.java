class Input {
    public void a() {
        System.out.print("changed");  // violation without filter
    }

    public boolean b() {
        System.out.print("b");  // violation without filter
        return false;
    }

    public void c() {
        System.out.print("c");  // violation without filter
    }
}
