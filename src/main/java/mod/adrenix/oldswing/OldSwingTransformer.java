package mod.adrenix.oldswing;

import mod.adrenix.oldswing.config.ConfigHandler;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;
import net.minecraft.launchwrapper.IClassTransformer;

import static org.objectweb.asm.Opcodes.*;

import java.util.Arrays;

public class OldSwingTransformer implements IClassTransformer {
    private static final String[] classesToTransform = {
        "net.minecraft.entity.EntityLivingBase",
        "net.minecraft.item.Item",
        "net.minecraft.client.renderer.ItemRenderer"
    };

    @Override
    public byte[] transform(String name, String transformedName, byte[] classBeingTransformed) {
        boolean isObfuscated = !name.equals(transformedName);
        int index = Arrays.asList(classesToTransform).indexOf(transformedName);
        return index != -1 ? transform(index, classBeingTransformed, isObfuscated) : classBeingTransformed;
    }

    private static byte[] transform(int index, byte[] classBeingTransformed, boolean isObfuscated) {
        System.out.println("Transforming: " + classesToTransform[index]);

        try {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(classBeingTransformed);
            classReader.accept(classNode, 0);

            switch(index) {
                case 0:
                    transformEntityLivingBase(classNode, isObfuscated);
                    break;
                case 1:
                    transformItemBase(classNode, isObfuscated);
                    break;
                case 2:
                    transformItemRendererBase(classNode, isObfuscated);
            }

            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);

            return classWriter.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return classBeingTransformed;
    }

    private static void transformItemRendererBase(ClassNode itemRendererBaseClass, boolean isObfuscated) {
        final String UPDATE_EQUIPPED_ITEM = isObfuscated ? "a" : "updateEquippedItem";
        final String UPDATE_EQUIPPED_ITEM_DESCRIPTOR = "()V";

        boolean isFloatChanged = false;

        for (MethodNode method : itemRendererBaseClass.methods) {
            if (method.name.equals(UPDATE_EQUIPPED_ITEM) && method.desc.equals(UPDATE_EQUIPPED_ITEM_DESCRIPTOR)) {
                for (AbstractInsnNode instruction : method.instructions.toArray()) {
                    if (instruction.getOpcode() == ALOAD && ((VarInsnNode) instruction).var == 1) {
                        if (instruction.getNext().getOpcode() == FCONST_1 && !isFloatChanged) {
                            /*
                            CHANGING:
                                Forcing the local float "f" in this method to 1.0F - instead of the getCooledAttackStrength -
                                will bring back the old "bring up" animation everything shared before the combat update.

                                This change will not impact combat, and is combat update compatible. This only changes
                                the client side animation.
                             */

                            method.instructions.remove(instruction.getPrevious());
                            method.instructions.remove(instruction.getNext());
                            method.instructions.remove(instruction.getNext());
                            method.instructions.remove(instruction.getNext());

                            method.instructions.insertBefore(instruction.getNext(), new LdcInsnNode(new Float("1.0")));
                            method.instructions.insertBefore(instruction.getNext().getNext(), new VarInsnNode(FSTORE, 4));

                            method.instructions.remove(instruction);
                            isFloatChanged = true;

                            System.out.println("[oldswing]: Overrode updateEquippedItem cooldown animation float to 1.0F");
                        }
                    }
                }
            }
        }
    }

    private static void transformItemBase(ClassNode itemBaseClass, boolean isObfuscated) {
        final String GET_SHOULD_CAUSE_REEQUIP_ANIMATION = "shouldCauseReequipAnimation";
        final String GET_SHOULD_CAUSE_REEQUIP_ANIMATION_DESCRIPTOR = isObfuscated ? "(Laip;Laip;Z)Z" : "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;Z)Z";

        boolean isMethodReturned = false;

        for (MethodNode method : itemBaseClass.methods) {
            if (method.name.equals(GET_SHOULD_CAUSE_REEQUIP_ANIMATION) && method.desc.equals(GET_SHOULD_CAUSE_REEQUIP_ANIMATION_DESCRIPTOR)) {
                for (AbstractInsnNode instruction : method.instructions.toArray()) {
                    if (instruction instanceof MethodInsnNode && !isMethodReturned) {
                        /*
                        CHANGING:
                            The reequip animation plays unnecessarily after every block break using a tool.

                            This change forces the shouldCauseReequipAnimation method in Item base class to use ItemStack base
                            class' areItemsEqualIgnoreDurability method. This fixes the animation. However, this could cause some
                            issues with mods that are expecting the animation to play if two ItemStacks are the same.
                         */

                        final String InvokeOwner = isObfuscated ? "aip" : "net/minecraft/item/ItemStack";
                        final String InvokeName = isObfuscated ? "d" : "areItemsEqualIgnoreDurability";
                        final String InvokeDesc = isObfuscated ? "(Laip;Laip;)Z" : "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)Z";

                        method.instructions.set(instruction, new MethodInsnNode(INVOKESTATIC, InvokeOwner, InvokeName, InvokeDesc, false));
                        isMethodReturned = true;

                        System.out.println("[oldswing]: shouldCauseReequipAnimation - method now returns areItemsEqualIgnoreDurability");
                    }
                }
            }
        }
    }

    private static void transformEntityLivingBase(ClassNode entityLivingBaseClass, boolean isObfuscated) {
        final String GET_ARM_SWING_ANIMATION = isObfuscated ? "p" : "getArmSwingAnimationEnd";
        final String GET_ARM_SWING_ANIMATION_DESCRIPTOR = "()I";

        for (MethodNode method : entityLivingBaseClass.methods) {
            if (method.name.equals(GET_ARM_SWING_ANIMATION) && method.desc.equals(GET_ARM_SWING_ANIMATION_DESCRIPTOR)) {
                for (AbstractInsnNode instruction : method.instructions.toArray()) {
                    if (instruction.getOpcode() == BIPUSH) {
                        if (((IntInsnNode) instruction).operand == 6) {
                            /*
                                CHANGING:
                                    mv.visitIntInsn(BIPUSH, 6);
                                TO:
                                    mv.visitIntInsn(BIPUSH, 8 or 10);
                                Simulates the old - slower - swinging animation before Minecraft Beta 1.8. This does
                                not impact block breaking speed. Only the animation speed is impacted.
                            */

                            method.instructions.set(instruction, new FieldInsnNode(GETSTATIC, "mod/adrenix/oldswing/config/ConfigHandler", "swingModifier", "I"));
                            System.out.println("[oldswing]: getArmSwingAnimationEnd - changed operand: 6 -> " + ConfigHandler.swingModifier);
                        }
                    }
                }
            }
        }
    }
}
