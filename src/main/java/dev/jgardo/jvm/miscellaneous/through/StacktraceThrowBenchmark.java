package dev.jgardo.jvm.miscellaneous.through;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;

public class StacktraceThrowBenchmark {

    private static final RuntimeException RUNTIME_EXCEPTION = new RuntimeException();

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(StacktraceThrowBenchmark.class.getSimpleName())
                .forks(1)
                .warmupTime(TimeValue.seconds(1))
                .warmupIterations(2)
                .measurementIterations(10)
                .measurementTime(TimeValue.seconds(1))
                .threads(1)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.NANOSECONDS)
//                .addProfiler(LinuxPerfNormProfiler.class)
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public int throw100Benchmark() {
        try {
            throwRuntimeException(100);
        } catch (RuntimeException e) {
            return 5;
        }
        return 1;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public int throw50Benchmark() {
        try {
            throwRuntimeException(50);
        } catch (RuntimeException e) {
            return 5;
        }
        return 1;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public int throw10Benchmark() {
        try {
            throwRuntimeException(10);
        } catch (RuntimeException e) {
            return 5;
        }
        return 1;
    }

    private void throwRuntimeException(int i) {
        if (i == 0) {
            throwThrow();
            return;
        } else {
            throwRuntimeException(i - 1);
        }
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    private void throwThrow() {
        throw RUNTIME_EXCEPTION;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public int without100throwBenchmark() {
        try {
            dontThrowRuntimeException(100);
        } catch (RuntimeException e) {
            return 5;
        }
        return 1;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public int without50throwBenchmark() {
        try {
            dontThrowRuntimeException(50);
        } catch (RuntimeException e) {
            return 5;
        }
        return 1;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public int without10throwBenchmark() {
        try {
            dontThrowRuntimeException(10);
        } catch (RuntimeException e) {
            return 5;
        }
        return 1;
    }

    private void dontThrowRuntimeException(int i) {
        if (i == 0) {
            dontThrowThrow(i);
        } else {
            dontThrowRuntimeException(i - 1);
        }
    }

    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    private void dontThrowThrow(int i) {
        i++;
    }
}
