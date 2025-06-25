package com.zetalasis.commonloader.inject.api;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;

public class InjectableMethodVisitor extends MethodVisitor {
    private final Class<?> injectable;
    private final String methodName;
    private final InjectPosition position;
    private final Method injectableMethod;
    private final boolean isStatic;
    private boolean replaced = false;
    private boolean tailInjected = false;

    public InjectableMethodVisitor(MethodVisitor mv, Class<?> injectable, Method injectableMethod, InjectPosition position, boolean isStatic) {
        super(Opcodes.ASM9, mv);
        this.injectable = injectable;
        this.methodName = injectableMethod.getName();
        this.position = position;
        this.injectableMethod = injectableMethod;
        this.isStatic = isStatic;
    }

    @Override
    public void visitCode() {
        if (position == InjectPosition.REPLACE) {
            replaced = true;
            loadMethodParameters();
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    InjectableClassTransformer.toTargetClassName(injectable),
                    methodName,
                    Type.getMethodDescriptor(injectableMethod),
                    false);
            mv.visitInsn(Opcodes.RETURN);
            return;
        }

        super.visitCode();

        if (position == InjectPosition.HEAD) {
            loadMethodParameters();
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    InjectableClassTransformer.toTargetClassName(injectable),
                    methodName,
                    Type.getMethodDescriptor(injectableMethod),
                    false);
        }
    }

    @Override
    public void visitInsn(int opcode) {
        if (replaced) return;

        if (position == InjectPosition.RETURN) {
            if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) {
                loadMethodParameters();
                mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                        InjectableClassTransformer.toTargetClassName(injectable),
                        methodName,
                        Type.getMethodDescriptor(injectableMethod),
                        false);
            }
        }

        super.visitInsn(opcode);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        if (!replaced && position == InjectPosition.TAIL && !tailInjected) {
            tailInjected = true;
            loadMethodParameters();
            mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                    InjectableClassTransformer.toTargetClassName(injectable),
                    methodName,
                    Type.getMethodDescriptor(injectableMethod),
                    false);
        }

        super.visitMaxs(maxStack, maxLocals);
    }

    private void loadMethodParameters() {
        Class<?>[] parameterTypes = injectableMethod.getParameterTypes();
        int index = isStatic ? 0 : 1;

        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> param = parameterTypes[i];

            if (param == int.class || param == boolean.class || param == char.class || param == byte.class || param == short.class)
                mv.visitVarInsn(Opcodes.ILOAD, index);
            else if (param == long.class)
                mv.visitVarInsn(Opcodes.LLOAD, index);
            else if (param == float.class)
                mv.visitVarInsn(Opcodes.FLOAD, index);
            else if (param == double.class)
                mv.visitVarInsn(Opcodes.DLOAD, index);
            else
                mv.visitVarInsn(Opcodes.ALOAD, index);

            index += (param == long.class || param == double.class) ? 2 : 1;
        }
    }
}