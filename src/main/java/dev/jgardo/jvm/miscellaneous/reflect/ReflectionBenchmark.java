package dev.jgardo.jvm.miscellaneous.reflect;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.LinuxPerfAsmProfiler;
import org.openjdk.jmh.profile.LinuxPerfNormProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.lang.invoke.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.ObjLongConsumer;

public class ReflectionBenchmark {

    private static int i1 = 1;
    private static long l1 = 2L;
    private static Object o1 = new Object();
    private static String s1 = "string";
    private static Object[] valuesToSet = new Object[] {i1, l1, o1, s1};

    private static Method[] methods = new Method[4];
    private static Method setter1;
    private static Method setter2;
    private static Method setter3;
    private static Method setter4;
    private static MethodHandle[] methodHandles = new MethodHandle[4];
    private static MethodHandle methodHandles1;
    private static MethodHandle methodHandles2;
    private static MethodHandle methodHandles3;
    private static MethodHandle methodHandles4;
    private static Object[] setters = new Object[4];
    private static BiConsumer[] biConsumerSetters = new BiConsumer[4];
    private static BiConsumer[] biConsumerSettersInnerClasses = new BiConsumer[4];
    private static AbstractSetter[] abstractSetters = new AbstractSetter[4];
    private static MethodHandles.Lookup lookup = MethodHandles.lookup();


    static {
        prepareMethods();
        prepareMethodHandles();
        prepareBiConsumerOnlyMethodHandles();
        prepareBiConsumerWithAbstractSetters();
        prepareAbstractSetters();
    }

    private static void prepareMethods() {
        try {
            methods[0] = Pojo.class.getMethod("setI1", int.class);
            methods[1] = Pojo.class.getMethod("setL1", long.class);
            methods[2] = Pojo.class.getMethod("setO1", Object.class);
            methods[3] = Pojo.class.getMethod("setS1", String.class);
            setter1 = methods[0];
            setter2 = methods[1];
            setter3 = methods[2];
            setter4 = methods[3];
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private static void prepareMethodHandles() {
        try {
            methodHandles[0] = lookup.unreflect(methods[0]);
            methodHandles[1] = lookup.unreflect(methods[1]);
            methodHandles[2] = lookup.unreflect(methods[2]);
            methodHandles[3] = lookup.unreflect(methods[3]);
            setters[0] = getIntSetter(methodHandles[0]);
            setters[1] = getLongSetter(methodHandles[1]);
            setters[2] = getObjectSetter(methodHandles[2]);
            setters[3] = getObjectSetter(methodHandles[3]);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static void prepareBiConsumerOnlyMethodHandles() {
        try {
            // see https://dzone.com/articles/setters-method-handles-and-java-11
            biConsumerSetters[0] = (a, b) -> ((ObjIntConsumer) setters[0]).accept(a, (int) b);
            biConsumerSetters[1] = (a, b) -> ((ObjLongConsumer) setters[1]).accept(a, (long) b);
            biConsumerSetters[2] = getBiConsumerObjectSetter(lookup.unreflect(methods[2]));
            biConsumerSetters[3] = getBiConsumerObjectSetter(lookup.unreflect(methods[3]));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static void prepareBiConsumerWithAbstractSetters() {
        try {
            biConsumerSettersInnerClasses[0] = new SetterI1();
            biConsumerSettersInnerClasses[1] = new SetterL1();
            biConsumerSettersInnerClasses[2] = new SetterO1();
            biConsumerSettersInnerClasses[3] = new SetterS1();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static void prepareAbstractSetters() {
        try {
            abstractSetters[0] = new SetterI1();
            abstractSetters[1] = new SetterL1();
            abstractSetters[2] = new SetterO1();
            abstractSetters[3] = new SetterS1();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static ObjIntConsumer getIntSetter(MethodHandle methodHandle) throws Throwable {
        final Class<?> functionKlaz = ObjIntConsumer.class;
        Object o = getSetter(methodHandle, functionKlaz);
        return (ObjIntConsumer) o;
    }

    private static ObjLongConsumer getLongSetter(MethodHandle methodHandle) throws Throwable {
        final Class<?> functionKlaz = ObjLongConsumer.class;
        Object o = getSetter(methodHandle, functionKlaz);
        return (ObjLongConsumer) o;
    }

    private static BiConsumer getObjectSetter(MethodHandle methodHandle) throws Throwable {
        final Class<?> functionKlaz = BiConsumer.class;
        Object o = getSetter(methodHandle, functionKlaz);
        return (BiConsumer) o;
    }

    private static BiConsumer getBiConsumerObjectSetter(MethodHandle methodHandle) throws Throwable {
        final Class<?> functionKlaz = BiConsumer.class;
        Object o = getBiConsumerSetter(methodHandle, functionKlaz);
        return (BiConsumer) o;
    }

    private static Object getSetter(MethodHandle methodHandle, Class<?> functionKlaz) throws Throwable {
        final String functionName = "accept";
        final Class<?> functionReturn = void.class;
        Class<?> aClass = !methodHandle.type().parameterType(1).isPrimitive()
                ? Object.class
                : methodHandle.type().parameterType(1);
        final Class<?>[] functionParams = new Class<?>[] { Object.class,
                aClass};

        final MethodType factoryMethodType = MethodType
                .methodType(functionKlaz);
        final MethodType functionMethodType = MethodType.methodType(
                functionReturn, functionParams);

        final CallSite setterFactory = LambdaMetafactory.metafactory( //
                lookup, // Represents a lookup context.
                functionName, // The name of the method to implement.
                factoryMethodType, // Signature of the factory method.
                functionMethodType, // Signature of function implementation.
                methodHandle, // Function method implementation.
                methodHandle.type() // Function method type signature.
        );

        final MethodHandle setterInvoker = setterFactory.getTarget();
        return setterInvoker.invoke();
    }

    private static Object getBiConsumerSetter(MethodHandle methodHandle, Class<?> functionKlaz) throws Throwable {
        final String functionName = "accept";
        final Class<?> functionReturn = void.class;
        Class<?> aClass = Object.class;
        final Class<?>[] functionParams = new Class<?>[] { Object.class,
                aClass};

        final MethodType factoryMethodType = MethodType
                .methodType(functionKlaz);
        final MethodType functionMethodType = MethodType.methodType(
                functionReturn, functionParams);

        final CallSite setterFactory = LambdaMetafactory.metafactory( //
                lookup, // Represents a lookup context.
                functionName, // The name of the method to implement.
                factoryMethodType, // Signature of the factory method.
                functionMethodType, // Signature of function implementation.
                methodHandle, // Function method implementation.
                methodHandle.type() // Function method type signature.
        );

        final MethodHandle setterInvoker = setterFactory.getTarget();
        return setterInvoker.invoke();
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(ReflectionBenchmark.class.getSimpleName())
                .forks(1)
                .warmupTime(TimeValue.seconds(1))
                .warmupIterations(7)
                .measurementIterations(10)
                .measurementTime(TimeValue.seconds(1))
                .threads(1)
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.NANOSECONDS)
//                .addProfiler(LinuxPerfNormProfiler.class)
                .addProfiler(LinuxPerfAsmProfiler.class)
                .build();

        new Runner(opt).run();
    }

    @State(Scope.Benchmark)
    public static class PojoHolder {
        Pojo pojo;

        @Setup(Level.Trial)
        public void prepare(){
            pojo = new Pojo();
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public Pojo directSetters(PojoHolder pojoHolder) {
        Pojo pojo = pojoHolder.pojo;
        pojo.setI1(i1);
        pojo.setL1(l1);
        pojo.setO1(o1);
        pojo.setS1(s1);
        return pojo;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public Pojo reflectionSetters(PojoHolder pojoHolder) throws InvocationTargetException, IllegalAccessException {
        Pojo pojo = pojoHolder.pojo;
        setter1.invoke(pojo, i1);
        setter2.invoke(pojo, l1);
        setter3.invoke(pojo, o1);
        setter4.invoke(pojo, s1);
        return pojo;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public Pojo methodHandles(PojoHolder pojoHolder) throws Throwable {
        Pojo pojo = pojoHolder.pojo;
        methodHandles[0].invokeExact(pojo, i1);
        methodHandles[1].invokeExact(pojo, l1);
        methodHandles[2].invokeExact(pojo, o1);
        methodHandles[3].invokeExact(pojo, s1);;
        return pojo;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public Pojo generatedSetter(PojoHolder pojoHolder) throws Throwable {
        Pojo pojo = pojoHolder.pojo;
        ((ObjIntConsumer)setters[0]).accept(pojo, i1);
        ((ObjLongConsumer)setters[1]).accept(pojo, l1);
        ((BiConsumer)setters[2]).accept(pojo, o1);
        ((BiConsumer)setters[3]).accept(pojo, s1);
        return pojo;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public Pojo biConsumerOnly(PojoHolder pojoHolder) throws Throwable {
        Pojo pojo = pojoHolder.pojo;
        biConsumerSetters[0].accept(pojo, i1);
        biConsumerSetters[1].accept(pojo, l1);
        biConsumerSetters[2].accept(pojo, o1);
        biConsumerSetters[3].accept(pojo, s1);
        return pojo;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public Pojo inLoopBiConsumerOnly(PojoHolder pojoHolder) throws Throwable {
        Pojo pojo = pojoHolder.pojo;
        for (int i = 0; i < 4; i++) {
            biConsumerSetters[i].accept(pojo, valuesToSet[i]);
        }
        return pojo;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public Pojo innerClassBiConsumerOnly(PojoHolder pojoHolder) throws Throwable {
        Pojo pojo = pojoHolder.pojo;
        biConsumerSettersInnerClasses[0].accept(pojo, valuesToSet[0]);
        biConsumerSettersInnerClasses[1].accept(pojo, valuesToSet[1]);
        biConsumerSettersInnerClasses[2].accept(pojo, valuesToSet[2]);
        biConsumerSettersInnerClasses[3].accept(pojo, valuesToSet[3]);
        return pojo;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public Pojo inLoopInnerClassBiConsumerOnly(PojoHolder pojoHolder) throws Throwable {
        Pojo pojo = pojoHolder.pojo;
        for (int i = 0; i < 4; i++) {
            biConsumerSettersInnerClasses[i].accept(pojo, valuesToSet[i]);
        }
        return pojo;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public Pojo abstractClasses(PojoHolder pojoHolder) throws Throwable {
        Pojo pojo = pojoHolder.pojo;
        abstractSetters[0].accept(pojo, valuesToSet[0]);
        abstractSetters[1].accept(pojo, valuesToSet[1]);
        abstractSetters[2].accept(pojo, valuesToSet[2]);
        abstractSetters[3].accept(pojo, valuesToSet[3]);
        return pojo;
    }


    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public Pojo inLoopAbstractClasses(PojoHolder pojoHolder) throws Throwable {
        Pojo pojo = pojoHolder.pojo;
        for (int i = 0; i < 4; i++) {
            abstractSetters[i].accept(pojo, valuesToSet[i]);
        }
        return pojo;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public Pojo inLoopMethodHandles(PojoHolder pojoHolder) throws Throwable {
        Pojo pojo = pojoHolder.pojo;
        for (int i = 0; i < 4; i++) {
            methodHandles[i].invoke(pojo, valuesToSet[i]);
        }
        return pojo;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public Pojo inLoopReflectionSetters(PojoHolder pojoHolder) throws InvocationTargetException, IllegalAccessException {
        Pojo pojo = pojoHolder.pojo;
        for (int i = 0; i < 4; i++) {
            methods[i].invoke(pojo, valuesToSet[i]);
        }
        return pojo;
    }

    public static abstract class AbstractSetter implements BiConsumer {

    }

    public static class SetterI1 extends AbstractSetter {
        @Override
        public void accept(Object o, Object o2) {
            ((Pojo) o).setI1((Integer) o2);
        }
    }

    public static class SetterL1 extends AbstractSetter {
        @Override
        public void accept(Object o, Object o2) {
            ((Pojo) o).setL1((Long) o2);
        }
    }

    public static class SetterO1 extends AbstractSetter {
        @Override
        public void accept(Object o, Object o2) {
            ((Pojo) o).setO1(o2);
        }
    }

    public static class SetterS1 extends AbstractSetter {
        @Override
        public void accept(Object o, Object o2) {
            ((Pojo) o).setS1((String) o2);
        }
    }
}
