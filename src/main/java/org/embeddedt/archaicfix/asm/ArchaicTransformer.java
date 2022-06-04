package org.embeddedt.archaicfix.asm;

import com.google.common.collect.ImmutableSet;
import net.minecraft.launchwrapper.IClassTransformer;
import org.embeddedt.archaicfix.ArchaicFix;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.tree.*;

import java.util.ListIterator;
import java.util.Set;

import static org.objectweb.asm.Opcodes.ASM5;

public class ArchaicTransformer implements IClassTransformer {
    private static final Set<String> threadedFields = ImmutableSet.of(
            "minX",
            "minY",
            "maxX",
            "maxY",
            "minZ",
            "maxZ"
    );
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if(!transformedName.startsWith("org.embeddedt.archaicfix")) {
            final ClassReader cr = new ClassReader(basicClass);
            final ClassWriter cw = new ClassWriter(0);

            final ClassNode cn = new ClassNode(ASM5);
            cr.accept(cn, 0);
            for (MethodNode m : cn.methods) {
                ListIterator<AbstractInsnNode> insns = m.instructions.iterator();
                boolean transformed = false;
                while(insns.hasNext()) {
                    AbstractInsnNode node = insns.next();
                    if(node.getOpcode() == Opcodes.GETFIELD || node.getOpcode() == Opcodes.PUTFIELD) {
                        boolean isSetter = node.getOpcode() == Opcodes.PUTFIELD;
                        FieldInsnNode f = (FieldInsnNode)node;
                        if(f.owner.equals("net/minecraft/block/Block")) {
                            if(threadedFields.contains(f.name)) {
                                transformed = true;
                                ArchaicFix.LOGGER.info("Transforming threaded block data access in {}.{}()", transformedName, m.name);
                                f.owner = "org/embeddedt/archaicfix/block/ThreadedBlockData";
                                insns.previous();
                                if(isSetter) {
                                    /* FIXME: assumes a double is at the top of the stack */
                                    insns.add(new InsnNode(Opcodes.DUP2_X1));
                                    insns.add(new InsnNode(Opcodes.POP2));
                                }
                                String m_name = "arch$getThreadedData";
                                String m_desc = "()Lorg/embeddedt/archaicfix/block/ThreadedBlockData;";
                                insns.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "net/minecraft/block/Block", m_name, m_desc, false));
                                if(isSetter) {
                                    insns.add(new InsnNode(Opcodes.DUP_X2));
                                    insns.add(new InsnNode(Opcodes.POP));
                                }
                                insns.next();
                                insns.next();
                            }
                        }
                    }
                }
            }
            cn.accept(cw);
            return cw.toByteArray();
        }
        return basicClass;
    }
}
