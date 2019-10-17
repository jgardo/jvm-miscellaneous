package dev.jgardo.jvm.miscellaneous.interfaces;

public interface Interface {
    int doSth(int a);

    default int defaultDoSth(int a) {
        return doSth(a);
    }
}
