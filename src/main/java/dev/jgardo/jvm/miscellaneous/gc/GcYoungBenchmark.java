package dev.jgardo.jvm.miscellaneous.gc;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;

public class GcYoungBenchmark {

    private static final String[][] JVM_GC_ARGS = new String[][]{
            new String[] { "-XX:+UnlockExperimentalVMOptions","-XX:+UseEpsilonGC",       "-XX:-DoEscapeAnalysis", "-Xms8192m", "-Xmx8192m", "-XX:+AlwaysPreTouch", "-XX:CompileThreshold=100",},
            new String[] { "-XX:+UnlockExperimentalVMOptions","-XX:+UseParallelGC",      "-XX:-DoEscapeAnalysis", "-Xms8192m", "-Xmx8192m", "-XX:+AlwaysPreTouch", "-XX:CompileThreshold=100",},
            new String[] { "-XX:+UnlockExperimentalVMOptions","-XX:+UseConcMarkSweepGC", "-XX:-DoEscapeAnalysis", "-Xms8192m", "-Xmx8192m", "-XX:+AlwaysPreTouch", "-XX:CompileThreshold=100",},
            new String[] { "-XX:+UnlockExperimentalVMOptions","-XX:+UseG1GC",            "-XX:-DoEscapeAnalysis", "-Xms8192m", "-Xmx8192m", "-XX:+AlwaysPreTouch", "-XX:CompileThreshold=100",},
            new String[] { "-XX:+UnlockExperimentalVMOptions","-XX:+UseZGC",             "-XX:-DoEscapeAnalysis", "-Xms8192m", "-Xmx8192m","-XX:+AlwaysPreTouch", "-XX:CompileThreshold=100",},
            new String[] { "-XX:+UnlockExperimentalVMOptions","-XX:+UseShenandoahGC",    "-XX:-DoEscapeAnalysis", "-Xms8192m", "-Xmx8192m","-XX:+AlwaysPreTouch", "-XX:CompileThreshold=100",},
    };

    private static class IntWrapper {
        int i = 12;
    }

    public static void main(String[] args) throws RunnerException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Options parent = new OptionsBuilder()
                .include(GcYoungBenchmark.class.getSimpleName())
                .forks(10)
                .warmupTime(TimeValue.milliseconds(150))
                .warmupIterations(5)
                .measurementIterations(5)
                .measurementTime(TimeValue.milliseconds(150))
                .threads(1)
                .mode(Mode.Throughput)
                .timeUnit(TimeUnit.SECONDS)
                .build();

        for (String[] jvmArgs: JVM_GC_ARGS) {
            new Runner(new OptionsBuilder()
                    .parent(parent)
                    .jvmArgs(jvmArgs)
                    .output(jvmArgs[1].substring(5) + "-results.txt")
                    .build()
            ).run();
        }
    }

    @Benchmark
    public void method() {
        new IntWrapper();
    }

}
