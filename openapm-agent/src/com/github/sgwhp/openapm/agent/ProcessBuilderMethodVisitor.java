package com.github.sgwhp.openapm.agent;

import com.github.sgwhp.openapm.agent.util.Log;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.Method;

/**
 * Created by wuhongping on 15-11-18.
 */
public class ProcessBuilderMethodVisitor extends MarkMethodVisitor {

    protected ProcessBuilderMethodVisitor(MethodVisitor methodVisitor, int access, String name, String desc, Log log) {
        super(methodVisitor, access, name, desc, log);
    }

    /**
     * 调用ProcessBuilderInvocationHandler#invoke
     */
    @Override
    protected void onMethodEnter(){
        log.d("ProcessBuilderMethodVisitor onMethodEnter methodName: " + methodName + ",methodDesc: " + methodDesc);
        invocationBuilder.loadInvocationDispatcher()
                .loadInvocationDispatcherKey(TransformAgent.genDispatcherKey("java/lang/ProcessBuilder", methodName))
                .loadArray(new Runnable[] {new Runnable() {
                    @Override
                    public void run() {
                        loadThis();
                        invokeVirtual(Type.getObjectType("java/lang/ProcessBuilder")
                                , new Method("command", "()Ljava/util/List;"));
                    }
                }}).invokeDispatcher();
    }
}
