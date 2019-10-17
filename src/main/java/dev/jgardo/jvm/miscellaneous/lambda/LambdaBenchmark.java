package dev.jgardo.jvm.miscellaneous.lambda;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Function;

public class LambdaBenchmark {

    private static final MethodHandle METHOD_HANDLE;
    private static final Method METHOD;
    static {
        Method method;
        try {
            method = LambdaBenchmark.class.getDeclaredMethod("privateMethod", int.class);
        } catch (NoSuchMethodException e) {
            method = null;
        }
        METHOD = method;
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle unreflect = null;
        try {
            unreflect = lookup.unreflect(method);
        } catch (IllegalAccessException e) {
            unreflect = null;
        }
        METHOD_HANDLE = unreflect;
    }


    public static void main(String[] args) throws RunnerException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Options opt = new OptionsBuilder()
                .include(LambdaBenchmark.class.getSimpleName())
                .forks(1)
                .warmupTime(TimeValue.seconds(1))
                .warmupIterations(2)
                .measurementIterations(10)
                .measurementTime(TimeValue.seconds(1))
                .threads(1)
                .mode(Mode.Throughput)
//                .addProfiler(LinuxPerfNormProfiler.class)
                .build();

        new Runner(opt).run();

//        Function<Integer, Integer> lambda = (Integer i) -> i + 5;
//        System.out.println(Arrays.toString(LambdaBenchmark.class.getDeclaredMethods()));
//
//        System.out.println(LambdaBenchmark.class.getDeclaredMethod("lambda$main$0", Integer.class)
//                .invoke(null, 5));
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public int method() {
        return privateMethod(5);
    }

    private int privateMethod(int i) {
        return i + 5;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public int lambda() {
        Function<Integer, Integer> lambda = (Integer i) -> i + 5;

        return lambda.apply(5);
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public int methodReferance() {
        Function<Integer, Integer> lambda = this::privateMethod;

        return lambda.apply(5);
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public int methodReflection() throws InvocationTargetException, IllegalAccessException {
        return (int) METHOD.invoke(this, 5);
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public int methodHandle() throws Throwable {
        return (int) METHOD_HANDLE.invoke(this, 5);
    }
}
