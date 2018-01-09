package com.github.sgwhp.openapm.agent;

import com.github.sgwhp.openapm.agent.util.Log;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

/**
 * Created by wuhongping on 15-11-23.
 */
public class DexerMainMethodVisitor extends MarkMethodVisitor {

    protected DexerMainMethodVisitor(MethodVisitor methodVisitor, int access, String name, String desc, Log log) {
        super(methodVisitor, access, name, desc, log);
    }

    //这里是进入到dexer.Main的boolean processClass(String,byte[]);修改此方法
    @Override
    protected void onMethodEnter() {
        log.d("DexerMainMethodVisitor onMethodEnter methodName: " + methodName + ",methodDesc: " + methodDesc);
        invocationBuilder.loadInvocationDispatcher()
                .loadInvocationDispatcherKey(TransformAgent.genDispatcherKey("com/android/dx/command/dexer/Main", methodName))
                .loadArgumentsArray(methodDesc)
                .invokeDispatcher(false);
        //把修改后的dexer.Main类返回
        checkCast(Type.getType("[B"));// == checkCast(Type.getType(byte[].class));
        storeArg(1);
    }
}
