package com.zetalasis.commonloader.inject.api;

import com.zetalasis.commonloader.Main;
import com.zetalasis.commonloader.inject.ClientInject;
import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.List;

public class InjectableClassTransformer implements ClassFileTransformer {
    public static String toTargetClassName(Class<?> clazz) {
        return clazz.getName().replace(".", "/");
    }

    @Override
    public byte[] transform(ClassLoader loader, String classFQN, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {

        String className = classFQN.replace('/', '.');

        List<Class<?>> matchingInjectables = InjectRegistry.REGISTRAR.stream()
                .filter(c -> {
                    Injectable injectable = c.getAnnotation(Injectable.class);
                    return injectable != null && injectable.classFQN().equals(className);
                })
                .toList();

        if (matchingInjectables.isEmpty()) return null;

        Main.LOGGER.info("[Transformer]: Transforming {}", className);

        ClassReader reader = new ClassReader(classfileBuffer);
        ClassWriter writer = new ClassWriter(reader, ClassWriter.COMPUTE_MAXS);

        ClassVisitor visitor = new ClassVisitor(Opcodes.ASM9, writer) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor,
                                             String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                for (Class<?> injectable : matchingInjectables) {
                    for (Method method : injectable.getMethods())
                    {
                        MethodInject methodInject = method.getAnnotation(MethodInject.class);
                        if (methodInject == null)
                            continue;

                        //Main.LOGGER.info("Injectable {} | Method Name {} | MethodInject Position {} | MethodInject Name {} | Name {}", injectable.getSimpleName(), method.getName(), methodInject.position(), methodInject.method(), name);

                        if (methodInject.method().equals(name)) {
                            return new InjectableMethodVisitor(mv, injectable, method, methodInject.position(), (access & Opcodes.ACC_STATIC) != 0);
                        }
                    }
                }
                return mv;
            }
        };

        reader.accept(visitor, ClassReader.EXPAND_FRAMES);
        return writer.toByteArray();
    }
}