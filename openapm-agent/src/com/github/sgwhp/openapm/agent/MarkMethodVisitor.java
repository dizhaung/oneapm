package com.github.sgwhp.openapm.agent;

import com.github.sgwhp.openapm.agent.util.Log;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * Created by wuhongping on 15-11-18.
 */
public class MarkMethodVisitor extends AdviceAdapter {
    protected String methodName;
    protected String methodDesc;
    protected Log log ;
    protected final InvocationBuilder invocationBuilder = new InvocationBuilder(this, log);

    protected MarkMethodVisitor(MethodVisitor methodVisitor, int access, String name, String desc, Log log) {
        super(ASM5, methodVisitor, access, name, desc);
        methodName = name;
        methodDesc = desc;
        this.log = log ;
    }

    @Override
    public void visitEnd() {
        super.visitAnnotation(Type.getDescriptor(Transformed.class), false);
        super.visitEnd();
    }
}
