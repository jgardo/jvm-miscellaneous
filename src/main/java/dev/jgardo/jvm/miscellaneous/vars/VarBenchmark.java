package dev.jgardo.jvm.miscellaneous.vars;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.lang.reflect.InvocationTargetException;

public class VarBenchmark {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        var nana = new Object() {
            int anInt;
            public int ret() {
                return anInt;
            }
        };

        nana.anInt = 4;
        System.out.println(nana.ret());

        Object o = new Object() {

            public int ret() {
                return 5;
            }
        };

        System.out.println(o.getClass().getDeclaredMethod("ret").invoke(o));

        Object newInstance = o.getClass().newInstance();

        System.out.println(o.getClass().getDeclaredMethod("ret").invoke(newInstance));

//        Options opt = new OptionsBuilder()
//                .include(VarBenchmark.class.getSimpleName())
//                .forks(1)
//                .warmupTime(TimeValue.seconds(1))
//                .warmupIterations(2)
//                .measurementIterations(10)
//                .measurementTime(TimeValue.seconds(1))
//                .threads(1)
//                .mode(Mode.Throughput)
////                .addProfiler(LinuxPerfNormProfiler.class)
//                .build();
//
//        new Runner(opt).run();
    }

//    @Benchmark
//    @CompilerControl(CompilerControl.Mode.PRINT)
//    public int switchInt9() {
//
//    }

}
