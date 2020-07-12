class Input {
    public void a() {
        System.out.print("changed"); // no violation, because it's not a new line
    }

    public boolean b() {
        System.out.print("b"); // violation
        return false;
    }

    public void c() {
        System.out.print("c"); // violation
    }
}
