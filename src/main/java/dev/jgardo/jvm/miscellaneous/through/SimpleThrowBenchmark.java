package dev.jgardo.jvm.miscellaneous.through;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.LinuxPerfNormProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;

public class SimpleThrowBenchmark {

    private static final RuntimeException RUNTIME_EXCEPTION = new RuntimeException();

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(SimpleThrowBenchmark.class.getSimpleName())
                .forks(1)
                .warmupTime(TimeValue.seconds(1))
                .warmupIterations(2)
                .measurementIterations(10)
                .measurementTime(TimeValue.seconds(1))
                .jvmArgs("-Djava.compiler=NONE")
                .threads(1)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.NANOSECONDS)
                .addProfiler(LinuxPerfNormProfiler.class)
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public int throwBenchmark() {
        try {
            throwRuntimeException();
        } catch (RuntimeException e) {
            return 5;
        }
        return 1;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public int throwThrowableBenchmark() {
        try {
            throwRuntimeException();
        } catch (Throwable e) {
            return 5;
        }
        return 1;
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    private void throwRuntimeException() {
        throw RUNTIME_EXCEPTION;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public int withoutthrowBenchmark() {
        try {
            dontThrowRuntimeException();
        } catch (RuntimeException e) {
            return 5;
        }
        return 1;
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    private void dontThrowRuntimeException() {

    }
}
