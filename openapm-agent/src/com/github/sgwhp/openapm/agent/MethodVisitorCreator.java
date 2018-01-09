package com.github.sgwhp.openapm.agent;

import com.github.sgwhp.openapm.agent.util.Log;
import org.objectweb.asm.MethodVisitor;

/**
 * Created by wuhongping on 15-11-23.
 */
public interface MethodVisitorCreator {

    MethodVisitor create(MethodVisitor mv, int access, String name, String desc, Log log);

    class DexerMainMethodVisitorCreator implements MethodVisitorCreator {

        @Override
        public MethodVisitor create(MethodVisitor mv, int access, String name, String desc, Log log) {
            return new DexerMainMethodVisitor(mv, access, name, desc, log);
        }
    }
}
