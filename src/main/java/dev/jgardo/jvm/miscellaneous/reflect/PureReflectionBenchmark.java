package dev.jgardo.jvm.miscellaneous.reflect;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.LinuxPerfAsmProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class PureReflectionBenchmark {

    private static Method method;
    private static final Pojo POJO = new Pojo();
    static {
        try {
            method = Pojo.class.getMethod("doNothing");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static class Pojo {
        public void doNothing() {

        }
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(PureReflectionBenchmark.class.getSimpleName())
                .forks(1)
                .warmupTime(TimeValue.seconds(1))
                .warmupIterations(7)
                .measurementIterations(10)
                .measurementTime(TimeValue.seconds(1))
                .threads(1)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.NANOSECONDS)
//                .addProfiler(LinuxPerfNormProfiler.class)
                .addProfiler(LinuxPerfAsmProfiler.class)
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public void invoke() throws InvocationTargetException, IllegalAccessException {
        method.invoke(POJO);
    }
}
