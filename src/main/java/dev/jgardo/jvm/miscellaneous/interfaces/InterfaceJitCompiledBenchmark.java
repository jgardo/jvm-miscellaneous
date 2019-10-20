package dev.jgardo.jvm.miscellaneous.interfaces;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.CompilerControl;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.profile.LinuxPerfNormProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.lang.reflect.InvocationTargetException;

public class InterfaceJitCompiledBenchmark {

    public static void main(String[] args) throws RunnerException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Options opt = new OptionsBuilder()
                .include(InterfaceJitCompiledBenchmark.class.getSimpleName())
                .forks(1)
                .warmupTime(TimeValue.seconds(1))
                .warmupIterations(2)
                .measurementIterations(10)
                .measurementTime(TimeValue.seconds(1))
                .threads(1)
                .mode(Mode.Throughput)
//                .jvmArgs("-XX:LoopUnrollLimit=1")
//                .addProfiler(LinuxPerfNormProfiler.class)
                .build();

        new Runner(opt).run();
    }

    private static Interface IMPLEMENTER = new Implementer();
    private static AbstractClass ABSTRACT_CLASS = new SubClass();

    private static final Interface[] IMPLEMENTERS = new Interface[] {
            new Implementer(),
            new Implementer1(),
            new Implementer2(),
            new Implementer3(),
            new Implementer4()
    };

    private static final AbstractClass[] ABSTRACT_CLASSES = new AbstractClass[] {
            new SubClass(),
            new SubClass1(),
            new SubClass2(),
            new SubClass3(),
            new SubClass4(),
    };

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public int interfaceBenchmark() {
        return IMPLEMENTER.doSth(5);
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public int subClassBenchmark() {
        return ABSTRACT_CLASS.doSth(5);
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public int interfacesBenchmark() {
        int res = 0;
        for (int i = 0; i < 5; i++) {
            res += IMPLEMENTERS[i].doSth(i);
        }
        return res;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public int subClassesBenchmark() {
        int res = 0;
        for (int i = 0; i < 5; i++) {
            res += ABSTRACT_CLASSES[i].doSth(i);
        }
        return res;
    }
}
