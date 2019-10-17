package dev.jgardo.jvm.miscellaneous.interfaces;

public abstract class AbstractClass {
    public abstract int doSth(int a);

    public int defaultDoSth(int a) {
        return doSth(a);
    }
}
