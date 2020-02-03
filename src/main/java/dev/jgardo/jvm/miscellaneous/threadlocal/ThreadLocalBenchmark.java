package dev.jgardo.jvm.miscellaneous.threadlocal;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import javax.servlet.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ThreadLocalBenchmark {

    private static final ThreadLocal<Integer> THREAD_LOCAL = new ThreadLocal<>();

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ThreadLocalBenchmark.class.getSimpleName())
                .forks(1)
                .warmupTime(TimeValue.seconds(1))
                .warmupIterations(2)
                .measurementIterations(10)
                .measurementTime(TimeValue.seconds(1))
                .threads(1)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.NANOSECONDS)
                .jvmArgs("-XX:-DoEscapeAnalysis")
//                .addProfiler(LinuxPerfNormProfiler.class)
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public Integer local() {
        Integer i = 1;

        return i;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public Integer threadLocal() {
        THREAD_LOCAL.set(1);
        Integer i = THREAD_LOCAL.get();
        THREAD_LOCAL.remove();
        return i;
    }
}
