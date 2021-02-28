function initializeCoreMod() {
    function debug(msg) {
        Java.type('net.minecraftforge.coremod.api.ASMAPI').log("DEBUG", "[oldswing] " + msg);
    }

    /*Class/Interface*/ Opcodes = Java.type("org.objectweb.asm.Opcodes");
    /*Class*/ ASM_API = Java.type("net.minecraftforge.coremod.api.ASMAPI");
    /*Class*/ MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");

    return {
        "FirstPersonRenderer#renderItemInFirstPerson": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.client.renderer.FirstPersonRenderer",
                "methodName": "func_228396_a_",
                "methodDesc": "(FLcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/client/renderer/IRenderTypeBuffer$Impl;Lnet/minecraft/client/entity/player/ClientPlayerEntity;I)V"
            },
            "transformer": function(method) {
                debug("Running FirstPersonRenderer#renderItemInFirstPerson transformer...");

                var isArmPitchHooked = false;
                var isArmYawHooked = false;

                for (var i = 0; i < method.instructions.size(); i++) {
                    if (isArmPitchHooked && isArmYawHooked) { break; }

                    var instruction = method.instructions.get(i);
                    if (instruction.getOpcode() == Opcodes.FMUL && instruction.getNext().getOpcode() == Opcodes.INVOKEVIRTUAL) {
                        // matrixStackIn.rotate(Vector3f.XP.rotationDegrees((playerEntityIn.getPitch(partialTicks) - f3) * 0.1F));
                        // matrixStackIn.rotate(Vector3f.YP.rotationDegrees((playerEntityIn.getYaw(partialTicks) - f4) * 0.1F));

                        method.instructions.set(instruction.getNext().getNext(), new MethodInsnNode(
                            Opcodes.INVOKESTATIC,
                            "mod/adrenix/oldswing/config/TransformerHelper",
                            "armSway",
                            "(Lcom/mojang/blaze3d/matrix/MatrixStack;Lnet/minecraft/util/math/vector/Quaternion;)V",
                            false
                        ));

                        if (!isArmPitchHooked && !isArmYawHooked) {
                            isArmPitchHooked = true;
                            debug("Successfully hooked arm pitch");
                        } else if (!isArmYawHooked) {
                            isArmYawHooked = true;
                            debug("Successfully hooked arm yaw");
                        }
                    }
                }

                return method;
            }
        }
    };
}
