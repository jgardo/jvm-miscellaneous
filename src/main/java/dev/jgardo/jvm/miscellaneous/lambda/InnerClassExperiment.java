package dev.jgardo.jvm.miscellaneous.lambda;

import java.util.function.Function;

public class InnerClassExperiment {
    public static void main(String[] args) {
        System.out.println("Hashcode: " + priv().hashCode());
        System.out.println("Hashcode: " + priv().hashCode());
    }

    private static Function<String, String> priv() {
        return new Function<String, String>() {
            @Override
            public String apply(String s) {
                return s.toUpperCase();
            }
        };
    }
}
