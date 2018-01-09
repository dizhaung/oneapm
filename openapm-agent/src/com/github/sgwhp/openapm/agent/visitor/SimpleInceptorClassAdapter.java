package com.github.sgwhp.openapm.agent.visitor;

import com.github.sgwhp.openapm.agent.CheckMarkMethodVisitor;
import com.github.sgwhp.openapm.agent.ProcessBuilderMethodVisitor;
import com.github.sgwhp.openapm.agent.util.Log;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by liangjianhua on 2017/12/13.
 */
public class SimpleInceptorClassAdapter extends ClassVisitor {
    private TransformContext context;
    Log log ;

    public SimpleInceptorClassAdapter(ClassVisitor classVisitor, TransformContext context, Log log) {
        super(Opcodes.ASM5, classVisitor);
        this.context = context;
        this.log = log ;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        log.d("SimpleInceptorClassAdapter visitMethod name: " + name);
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if( !name.contains("init") ) {
            return new CheckMarkMethodVisitor(new SimpleInceptorMethodAdapter(context, mv, access, name, desc, log));
        }
        return mv ;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
//        if( name.equals("value") ){
//            log.d("SimpleInceptorClassAdapter feild name.equals(\"value\")");
//            return null ;
//        }
        return super.visitField(access, name, desc, signature, value);
    }
}
