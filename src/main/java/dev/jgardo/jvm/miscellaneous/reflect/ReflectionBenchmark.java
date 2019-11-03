package dev.jgardo.jvm.miscellaneous.reflect;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.profile.LinuxPerfNormProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.lang.invoke.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.ObjIntConsumer;
import java.util.function.ObjLongConsumer;

public class ReflectionBenchmark {

    private static int i1 = 1;
    private static long l1 = 2L;
    private static Object o1 = new Object();
    private static String s1 = "string";

    private static Method[] methods = new Method[4];
    private static CallSite[] callSites = new CallSite[4];
    private static Object[] setters = new Object[4];
    private static BiConsumer[] biConsumerSetters = new BiConsumer[4];
    private static MethodHandles[] methodsHandles = new MethodHandles[4];
    private static MethodHandles.Lookup lookup = MethodHandles.lookup();


    static {
        prepareMethods();
        prepareMethodHandles();
        prepareBiConsumerOnlyMethodHandles();
    }

    private static void prepareMethods() {
        try {
            methods[0] = Pojo.class.getMethod("setI1", int.class);
            methods[1] = Pojo.class.getMethod("setL1", long.class);
            methods[2] = Pojo.class.getMethod("setO1", Object.class);
            methods[3] = Pojo.class.getMethod("setS1", String.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private static void prepareMethodHandles() {
        try {
            setters[0] = getIntSetter(lookup.unreflect(methods[0]));
            setters[1] = getLongSetter(lookup.unreflect(methods[1]));
            setters[2] = getObjectSetter(lookup.unreflect(methods[2]));
            setters[3] = getObjectSetter(lookup.unreflect(methods[3]));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static void prepareBiConsumerOnlyMethodHandles() {
        try {
            biConsumerSetters[0] = getBiConsumerObjectSetter(lookup.unreflect(methods[0]));
            biConsumerSetters[1] = getBiConsumerObjectSetter(lookup.unreflect(methods[1]));
            biConsumerSetters[2] = getBiConsumerObjectSetter(lookup.unreflect(methods[2]));
            biConsumerSetters[3] = getBiConsumerObjectSetter(lookup.unreflect(methods[3]));
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
                .warmupIterations(2)
                .measurementIterations(10)
                .measurementTime(TimeValue.seconds(1))
                .threads(1)
                .mode(Mode.Throughput)
                .addProfiler(LinuxPerfNormProfiler.class)
                .build();

        new Runner(opt).run();
    }

    @State(Scope.Benchmark)
    public static class PojoHolder {
        Pojo pojo;

        @Setup(Level.Invocation)
        public void prepare(){
            pojo = new Pojo();
        }
    }

//    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public Pojo directSetters(PojoHolder pojoHolder) {
        Pojo pojo = pojoHolder.pojo;
        pojo.setI1(i1);
        pojo.setL1(l1);
        pojo.setO1(o1);
        pojo.setS1(s1);
        return pojo;
    }

//    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public Pojo reflectionSetters(PojoHolder pojoHolder) throws InvocationTargetException, IllegalAccessException {
        Pojo pojo = pojoHolder.pojo;
        methods[0].invoke(pojo, i1);
        methods[1].invoke(pojo, l1);
        methods[2].invoke(pojo, o1);
        methods[3].invoke(pojo, s1);
        return pojo;
    }

//    @Benchmark
    @CompilerControl(CompilerControl.Mode.PRINT)
    public Pojo setter(PojoHolder pojoHolder) throws Throwable {
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
        ((ObjIntConsumer)setters[0]).accept(pojo, i1);
        ((ObjLongConsumer)setters[1]).accept(pojo, l1);
        ((BiConsumer)setters[2]).accept(pojo, o1);
        ((BiConsumer)setters[3]).accept(pojo, s1);
        return pojo;
    }

//    @Benchmark
//    @CompilerControl(CompilerControl.Mode.PRINT)
//    public Pojo setter(PojoHolder pojoHolder) throws Throwable {
//        Pojo pojo = pojoHolder.pojo;
//        ((ObjIntConsumer)setters[0]).accept(pojo, i1);
//        ((ObjLongConsumer)setters[1]).accept(pojo, 1);
//        ((BiConsumer)setters[2]).accept(pojo, o1);
//        ((BiConsumer)setters[3]).accept(pojo, s1);
//        return pojo;
//    }



}
