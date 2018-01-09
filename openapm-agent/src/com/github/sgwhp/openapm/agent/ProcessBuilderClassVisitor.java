package com.github.sgwhp.openapm.agent;


import com.github.sgwhp.openapm.agent.util.Log;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * Created by wuhongping on 15-11-18.
 */
public class ProcessBuilderClassVisitor extends ClassVisitor {

    protected Log log ;

    public ProcessBuilderClassVisitor(ClassVisitor cv, Log log) {
        super(Opcodes.ASM5, cv);
        this.log = log ;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor visitor = super.visitMethod(access, name, desc, signature, exceptions);
        if("start".equals(name)){
            return new CheckMarkMethodVisitor(new ProcessBuilderMethodVisitor(visitor, access, name, desc, log));
        }
        return visitor;
    }
}
