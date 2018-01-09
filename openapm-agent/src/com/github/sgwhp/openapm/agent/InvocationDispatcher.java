package com.github.sgwhp.openapm.agent;

import com.github.sgwhp.openapm.agent.util.Log;
import com.github.sgwhp.openapm.agent.visitor.*;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by wuhongping on 15-11-18.
 */
public class InvocationDispatcher implements InvocationHandler {
    private final TransformContext context;
    private final TransformConfig config;
    private final Log log;
    HashMap<String, InvocationHandler> invocationHandlerFactory = new HashMap<>();

    public InvocationDispatcher(Log log) throws ClassNotFoundException {
        config = new TransformConfig(log);
        context = new TransformContext(config, log);
        this.log = log;

        invocationHandlerFactory.put(TransformAgent.genDispatcherKey("java/lang/ProcessBuilder", "start")
                , new ProcessBuilderInvocationHandler(this, log));
        invocationHandlerFactory.put(TransformAgent.genDispatcherKey("com/android/dx/command/dexer/Main", "processClass")
                , new DexerMainInvocationHandler(this, log));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        @SuppressWarnings("SuspiciousMethodCalls")
        InvocationHandler invocationHandler = invocationHandlerFactory.get(proxy);
        if (invocationHandler == null) {
            log.e("Unsupported transform target: " + proxy);
            return null;
        }
        try {
            return invocationHandler.invoke(proxy, method, args);
        } catch (Exception e) {
            log.e("Error:" + e.getMessage(), e);
        }
        return null;
    }

    private boolean skip(String className) {
        for (String str : TransformAgent.skip) {
            if (className.contains(str)) return true;
        }
        return false;
    }

    public ClassData transform(byte[] classByte) { //这里要对需要修改的类进行修改，例子里是把Excetpin方法的开头调用收集异常方法，72行
        String str = "an unknown class";
        try {
            ClassReader cr = new ClassReader(classByte);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            context.reset();
            cr.accept(new InitContextClassVisitor(context, log)
                    , ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG | ClassReader.SKIP_CODE);
            str = context.getClassName();
            log.d("invoke transform: " + str);
            if (skip(context.getClassName()))
                return null;
            ClassVisitor cv = cw;
            if(context.getTargetPackage() == null || str.startsWith(context.getTargetPackage())){
                cv = new ExceptionLogClassAdapter(cw, context);
                if( str.contains("Hello") ) {
                    log.d("str.equals(\"Hello\")");
                    cv = new SimpleInceptorClassAdapter(cv, context, log);
                }
//                String actualPackage = context.getTargetPackage().replaceAll("/", ".");
//                cv = new AopClassVisitor(actualPackage, Opcodes.ASM5, cv, log);
            }
            cr.accept(new ContextClassVisitor(cv, context)
                    , ClassReader.EXPAND_FRAMES | ClassReader.SKIP_FRAMES );
            return context.newClassData(cw.toByteArray());

        } catch (TransformedException e) {
            return null;
        } catch (Exception e) {
            log.e("An error occurred while transforming " + str
                    + ".\n" + e.getMessage(), e);
        }
        return new ClassData(classByte, false);
    }

    public TransformContext getContext() {
        return context;
    }
}
