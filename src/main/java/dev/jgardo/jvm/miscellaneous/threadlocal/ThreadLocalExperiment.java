package dev.jgardo.jvm.miscellaneous.threadlocal;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

public class ThreadLocalExperiment {

    private static boolean work = true;

    private static final ThreadLocal<Integer> THREAD_LOCAL = new ThreadLocal<>() {

    };
    public static void main(String[] args) throws Exception {
        var thread1 = new Thread(() -> {
            THREAD_LOCAL.set(12);
            while (work) { }
            System.out.println(THREAD_LOCAL.get());
        });

        thread1.start();

        var clazz = Thread.class;
        var field = clazz.getDeclaredField("threadLocals");
        field.setAccessible(true);
        Object threadLocals = field.get(thread1);
        var method = threadLocals.getClass().getDeclaredMethod("set", ThreadLocal.class, Object.class);
        method.setAccessible(true);
        method.invoke(threadLocals, THREAD_LOCAL, 24);

        work = false;
    }
}
