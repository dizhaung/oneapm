package com.github.sgwhp.openapm.agent.visitor;

import com.github.sgwhp.openapm.agent.util.Log;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

/**
 * Created by wuhongping on 15-12-4.
 */
public class SimpleInceptorMethodAdapter extends AdviceAdapter {

    private Log log ;
    private TransformContext context ;

    protected SimpleInceptorMethodAdapter(TransformContext context, MethodVisitor methodVisitor, int access, String name, String desc, Log log) {
        super(Opcodes.ASM5, methodVisitor, access, name, desc);
        this.log = log ;
        this.context = context ;
    }

    @Override
    protected void onMethodEnter() {
        super.onMethodEnter();
        log.d("SimpleInceptorMethodAdapter onMethodEnter");
        mv.visitMethodInsn(INVOKESTATIC, "com/github/sgwhp/openapm/codeinceptor/MethodSimpleInceptor", "beforeMethod", "()V", false);

    }

    @Override
    protected void onMethodExit(int i) {
        log.d("SimpleInceptorMethodAdapter onMethodExit");
        mv.visitMethodInsn(INVOKESTATIC, "com/github/sgwhp/openapm/codeinceptor/MethodSimpleInceptor", "afterMethod", "()V", false);
        super.onMethodExit(i);
        context.markModified();
    }

}
