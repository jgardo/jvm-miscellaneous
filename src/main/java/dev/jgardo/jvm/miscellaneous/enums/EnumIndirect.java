package dev.jgardo.jvm.miscellaneous.enums;

public class EnumIndirect /*extends java.lang.Enum*/ {
    public static final EnumIndirect VAL_1 = new EnumIndirect("VAL_1", 1, 1);
    public static final EnumIndirect VAL_2 = new EnumIndirect("VAL_2", 2, 2);

    private final int abc;
    private static final EnumIndirect[] $VALUES = new EnumIndirect[] {VAL_1, VAL_2 };
    public static EnumIndirect[] values() {
        return $VALUES;
    }

//    public static EnumIndirect valueOf(String name) {
//        return valueOf(EnumIndirect.class, name);
//    }
    EnumIndirect(String name, int ordinal, int abc) {
//        super(name, ordinal);
        this.abc = abc;
    }
}
