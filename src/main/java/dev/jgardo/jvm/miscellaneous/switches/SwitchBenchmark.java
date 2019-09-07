package dev.jgardo.jvm.miscellaneous.switches;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.LinuxPerfNormProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

public class SwitchBenchmark {

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(SwitchBenchmark.class.getSimpleName())
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
    }

    @State(Scope.Benchmark)
    public static class CountToNine {
        private int i = 0;

        @Setup(Level.Invocation)
        public void prepare(){
            i = (i + 1) % 9;
        }
    }

    @State(Scope.Benchmark)
    public static class CountToThirtyThree {
        private int i = 0;

        @Setup(Level.Invocation)
        public void prepare(){
            i = (i + 1) % 66;
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public int switchInt9(CountToNine countToNine) {
        int i = countToNine.i;
        switch (i) {
            case 0: return 0;
            case 1: return 8;
            case 2: return 16;
            case 3: return 24;
            case 4: return 32;
            case 5: return 40;
            case 6: return 48;
            case 7: return 56;
            default:
                return 64;
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public int ifInt9(CountToNine countToNine) {
        int i = countToNine.i;
        if (i == 0) {
            return 0;
        } else if (i == 1) {
            return 8;
        } else if (i == 2) {
            return 16;
        } else if (i == 3) {
            return 24;
        } else if (i == 4) {
            return 32;
        } else if (i == 5) {
            return 40;
        } else if (i == 6) {
            return 48;
        } else if (i == 7) {
            return 56;
        } else  {
            return 64;
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public int switchInt33(CountToThirtyThree count) {
        int i = count.i;
        switch (i) {
            case 0: return 0;
            case 1: return 8;
            case 2: return 16;
            case 3: return 24;
            case 4: return 32;
            case 5: return 40;
            case 6: return 48;
            case 7: return 56;
            case 8: return 0;
            case 9: return 8;
            case 10: return 16;
            case 11: return 24;
            case 12: return 32;
            case 13: return 40;
            case 14: return 48;
            case 15: return 56;
            case 16: return 0;
            case 17: return 8;
            case 18: return 16;
            case 19: return 24;
            case 20: return 32;
            case 21: return 40;
            case 22: return 48;
            case 23: return 56;
            case 24: return 0;
            case 25: return 8;
            case 26: return 16;
            case 27: return 24;
            case 28: return 32;
            case 29: return 40;
            case 30: return 48;
            case 31: return 56;
            case 32: return 56;
            case 33: return 0;
            case 34: return 8;
            case 35: return 16;
            case 36: return 24;
            case 37: return 32;
            case 38: return 40;
            case 39: return 48;
            case 40: return 56;
            case 41: return 0;
            case 42: return 8;
            case 43: return 16;
            case 44: return 24;
            case 45: return 32;
            case 46: return 40;
            case 47: return 48;
            case 48: return 56;
            case 49: return 0;
            case 50: return 8;
            case 51: return 16;
            case 52: return 24;
            case 53: return 32;
            case 54: return 40;
            case 55: return 48;
            case 56: return 56;
            case 57: return 0;
            case 58: return 8;
            case 59: return 16;
            case 60: return 24;
            case 61: return 32;
            case 62: return 40;
            case 63: return 48;
            case 64: return 56;
            case 65: return 56;
            default:
                return 64;
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public int ifInt33(CountToThirtyThree count) {
        int i = count.i;
        if (i == 0) {
            return 0;
        } else if (i == 1) {
            return 8;
        } else if (i == 2) {
            return 16;
        } else if (i == 3) {
            return 24;
        } else if (i == 4) {
            return 32;
        } else if (i == 5) {
            return 40;
        } else if (i == 6) {
            return 48;
        } else if (i == 7) {
            return 56;
        } else if (i == 8) {
            return 8;
        } else if (i == 9) {
            return 16;
        } else if (i == 10) {
            return 24;
        } else if (i == 11) {
            return 32;
        } else if (i == 12) {
            return 40;
        } else if (i == 13) {
            return 48;
        } else if (i == 14) {
            return 56;
        } else if (i == 15) {
            return 8;
        } else if (i == 16) {
            return 16;
        } else if (i == 17) {
            return 24;
        } else if (i == 18) {
            return 32;
        } else if (i == 19) {
            return 40;
        } else if (i == 20) {
            return 48;
        } else if (i == 21) {
            return 56;
        } else if (i == 22) {
            return 8;
        } else if (i == 23) {
            return 16;
        } else if (i == 24) {
            return 24;
        } else if (i == 25) {
            return 32;
        } else if (i == 26) {
            return 40;
        } else if (i == 27) {
            return 48;
        } else if (i == 28) {
            return 56;
        } else if (i == 29) {
            return 40;
        } else if (i == 30) {
            return 48;
        } else if (i == 31) {
            return 56;
        } else if (i == 32) {
            return 40;
        } else if (i == 33) {
            return 8;
        } else if (i == 34) {
            return 16;
        } else if (i == 35) {
            return 24;
        } else if (i == 36) {
            return 32;
        } else if (i == 37) {
            return 40;
        } else if (i == 38) {
            return 48;
        } else if (i == 39) {
            return 56;
        } else if (i == 40) {
            return 8;
        } else if (i == 41) {
            return 16;
        } else if (i == 42) {
            return 24;
        } else if (i == 43) {
            return 32;
        } else if (i == 44) {
            return 40;
        } else if (i == 45) {
            return 48;
        } else if (i == 46) {
            return 56;
        } else if (i == 47) {
            return 8;
        } else if (i == 48) {
            return 16;
        } else if (i == 49) {
            return 24;
        } else if (i == 50) {
            return 32;
        } else if (i == 51) {
            return 40;
        } else if (i == 52) {
            return 48;
        } else if (i == 53) {
            return 56;
        } else if (i == 54) {
            return 8;
        } else if (i == 55) {
            return 16;
        } else if (i == 56) {
            return 24;
        } else if (i == 57) {
            return 32;
        } else if (i == 58) {
            return 40;
        } else if (i == 59) {
            return 48;
        } else if (i == 60) {
            return 56;
        } else if (i == 61) {
            return 40;
        } else if (i == 62) {
            return 48;
        } else if (i == 63) {
            return 56;
        } else if (i == 64) {
            return 40;
        } else if (i == 65) {
            return 40;
        } else  {
            return 64;
        }
    }
}
