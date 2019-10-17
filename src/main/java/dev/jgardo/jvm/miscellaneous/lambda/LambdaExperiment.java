package dev.jgardo.jvm.miscellaneous.lambda;

import java.util.function.Function;

public class LambdaExperiment {
    public static void main(String[] args) {
        System.out.println("Hashcode: " + invocation().hashCode());
        System.out.println("Hashcode: " + invocation().hashCode());

        System.out.println("Equal: " + (invocation() == invocation()));
    }

    private static Function<String, Void> invocation() {
        return s -> { throw new RuntimeException(s); };
    }
}
