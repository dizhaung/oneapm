package com.github.sgwhp.openapm.agent;

import com.github.sgwhp.openapm.agent.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by wuhongping on 15-11-23.
 */
public class DexerMainInvocationHandler implements InvocationHandler {
    private InvocationDispatcher dispatcher;
    private Log log;

    public DexerMainInvocationHandler(InvocationDispatcher dispatcher, Log log){
        this.dispatcher = dispatcher;
        this.log = log;
    }

    //这里就是Hook点，执行processClass方法时，对需要修改的类调用dispatcher.transform(byte[])方法进行修改，参数里的字节数据即是所有class文件的字节数据
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        byte[] classBytes = (byte[]) args[1];
        synchronized (dispatcher.getContext()) {
            ClassData data = dispatcher.transform(classBytes);
            if ((data != null) && (data.getMainClassBytes() != null) && (data.isModified()))
                return data.getMainClassBytes();
        }
        return classBytes;
    }
}
