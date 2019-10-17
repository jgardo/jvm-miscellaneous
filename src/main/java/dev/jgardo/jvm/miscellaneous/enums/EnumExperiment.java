package dev.jgardo.jvm.miscellaneous.enums;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.function.Function;

public class EnumExperiment {

    @Annot(Enum.VAL_1)
    private Object object;

    public static void main(String[] args) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        var constructor = Enum.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        var generated = constructor.newInstance("VAL_G", 2, 2);
        System.out.println(generated);
//        System.out.println(map(generated));
//        Enum e = Enum.VAL_1;
//        System.out.println(map(e));
    }

    private static int map(Enum e) {
        switch (e) {
            case VAL_1:
                return 10;
            case VAL_2:
                return 20;
            default:
                return 30;
        }
    }
}
