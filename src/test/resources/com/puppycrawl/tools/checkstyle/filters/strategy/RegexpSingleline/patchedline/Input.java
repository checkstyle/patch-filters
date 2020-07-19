class Input {
    public void a() {
        System.out.print("changed");  // violation patchedline
    }

    public boolean b() {
        System.out.print("b");  // violation newline/patchedline
        return false;
    }

    public void c() {
        System.out.print("c");  // violation newline/patchedline
    }
}
