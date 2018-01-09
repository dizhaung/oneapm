package com.github.sgwhp.openapm.agent;

import com.github.sgwhp.openapm.agent.util.Log;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;

/**
 * Created by wuhongping on 15-11-18.
 */
public class InvocationBuilder {
    private final GeneratorAdapter generatorAdapter;
    private Log log ;

    public InvocationBuilder(GeneratorAdapter adapter, Log log) {
        generatorAdapter = adapter;
        this.log = log ;
    }

    public InvocationBuilder loadNull() {
        this.generatorAdapter.visitInsn(Opcodes.ACONST_NULL);
        return this;
    }

    /**
     * 从LOGGER类里treeLock对象从取回InvocationDispatcher
     * @return
     */
    public InvocationBuilder loadInvocationDispatcher() {
        generatorAdapter.visitLdcInsn(Type.getType(TransformAgent.LOGGER));
        generatorAdapter.visitLdcInsn("treeLock");
        generatorAdapter.invokeVirtual(Type.getType(Class.class), new Method("getDeclaredField", "(Ljava/lang/String;)Ljava/lang/reflect/Field;"));
        generatorAdapter.dup();
        generatorAdapter.visitInsn(Opcodes.ICONST_1);
        generatorAdapter.invokeVirtual(Type.getType(Field.class), new Method("setAccessible", "(Z)V"));
        generatorAdapter.visitInsn(Opcodes.ACONST_NULL); //设置成public
        generatorAdapter.invokeVirtual(Type.getType(Field.class), new Method("get", "(Ljava/lang/Object;)Ljava/lang/Object;"));
        return this;
    }

    public InvocationBuilder loadArgumentsArray(String methodDesc) {
        Method localMethod = new Method("dummy", methodDesc);
        generatorAdapter.push(localMethod.getArgumentTypes().length);
        Type localType = Type.getType(Object.class);
        generatorAdapter.newArray(localType);
        for (int i = 0; i < localMethod.getArgumentTypes().length; i++) {
            generatorAdapter.dup();
            generatorAdapter.push(i);
            generatorAdapter.loadArg(i);
            generatorAdapter.arrayStore(localType);
            if( log == null )
                System.out.println("InvocationBuilder loadArgumentsArray " + i + " localType: " + localType);
            else
                log.d("InvocationBuilder loadArgumentsArray " + i + " localType: " + localType);
        }
        return this;
    }

    public InvocationBuilder loadArray(Runnable[] runnables) {
        generatorAdapter.push(runnables.length);
        Type localType = Type.getObjectType("java/lang/Object");
        generatorAdapter.newArray(localType);
        for (int i = 0; i < runnables.length; i++) {
            generatorAdapter.dup();
            generatorAdapter.push(i);
            runnables[i].run();
            generatorAdapter.arrayStore(localType);
        }
        return this;
    }

    public InvocationBuilder printToInfoLogFromBytecode(final String paramString) {
        loadInvocationDispatcher();
        generatorAdapter.visitLdcInsn("PRINT_TO_INFO_LOG");
        generatorAdapter.visitInsn(Opcodes.ACONST_NULL);
        loadArray(new Runnable[]{new Runnable() {
            @Override
            public void run() {
                generatorAdapter.visitLdcInsn(paramString);
            }
        }});
        invokeDispatcher();
        return this;
    }

    public InvocationBuilder invokeDispatcher() {
        return invokeDispatcher(true);
    }

    /**调用具体InvocationHandlr里的invoke方法**/
    public InvocationBuilder invokeDispatcher(boolean pop) {
        generatorAdapter.invokeInterface(Type.getType(InvocationHandler.class)
                , new Method("invoke", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;"));
        if (pop)
            generatorAdapter.pop();
        return this;
    }

    public InvocationBuilder loadInvocationDispatcherKey(String key) {
        generatorAdapter.visitLdcInsn(key);
        generatorAdapter.visitInsn(Opcodes.ACONST_NULL);
        return this;
    }
}
