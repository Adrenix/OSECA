function initializeCoreMod() {
    function debug(msg) {
        Java.type('net.minecraftforge.coremod.api.ASMAPI').log("DEBUG", "[oldswing] " + msg);
    }

    /*Class/Interface*/ Opcodes = Java.type("org.objectweb.asm.Opcodes");
    /*Class*/ ASM_API = Java.type("net.minecraftforge.coremod.api.ASMAPI");
    /*Class*/ MethodInsnNode = Java.type("org.objectweb.asm.tree.MethodInsnNode");

    return {
        "LivingEntity#getArmSwingAnimationEnd": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.entity.LivingEntity",
                "methodName": "func_82166_i",
                "methodDesc": "()I"
            },
            "transformer": function(method) {
                debug("Running LivingEntity#getArmSwingAnimationEnd transformer...");

                for (var i = 0; i < method.instructions.size(); i++) {
                    var instruction = method.instructions.get(i);

                    if (instruction.getOpcode() == Opcodes.BIPUSH) {
                        if (instruction.operand == 6) {
                            method.instructions.set(instruction, new MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                "mod/adrenix/oldswing/config/TransformerHelper",
                                "swingSpeed",
                                "()I"
                            ));

                            debug("Swapped an operand from 6 to TransformerHelper.swingSpeed method");
                        }
                    }
                }

                return method;
            }
        },

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
        },

        "FirstPersonRenderer#tick": {
            "target": {
                "type": "METHOD",
                "class": "net.minecraft.client.renderer.FirstPersonRenderer",
                "methodName": "func_78441_a",
                "methodDesc": "()V"
            },
            "transformer": function(method) {
                debug("Running FirstPersonRenderer#tick transformer...");

                var isFloatChanged = false;
                var isReequipChangedMain = false;
                var isReequipChangedOff = false;

                for (var i = 0; i < method.instructions.size(); i++) {
                    if (isFloatChanged && isReequipChangedMain && isReequipChangedOff) { break; }

                    var instruction = method.instructions.get(i);
                    if (!isFloatChanged && instruction.getOpcode() == Opcodes.ALOAD && instruction.var == 1) {
                        if (instruction.getNext().getOpcode() == Opcodes.FCONST_1) {
                            var instruction = instruction.getNext().getNext();
                            if (instruction.getOpcode() == Opcodes.INVOKEVIRTUAL) {
                                method.instructions.set(instruction, new MethodInsnNode(
                                    Opcodes.INVOKESTATIC,
                                    "mod/adrenix/oldswing/config/TransformerHelper",
                                    "getCooldownAnimationFloat",
                                    "(Lnet/minecraft/client/entity/player/ClientPlayerEntity;F)F",
                                    false
                                ));

                                isFloatChanged = true;
                                debug("Successfully reassigned getCooledAttackStrength to TransformerHelper getCooldownAnimationFloat");
                            }
                        }
                    } else if (isFloatChanged && (instruction.getOpcode() == Opcodes.GETFIELD || instruction.getOpcode() == Opcodes.ICONST_M1)) {
                        if (instruction.getNext().getOpcode() == Opcodes.INVOKESTATIC) {
                            method.instructions.set(instruction.getNext(), new MethodInsnNode(
                                Opcodes.INVOKESTATIC,
                                "mod/adrenix/oldswing/config/TransformerHelper",
                                "shouldCauseReequipAnimation",
                                "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;I)Z",
                                false
                            ));

                            if (!isReequipChangedMain && !isReequipChangedOff) {
                                isReequipChangedMain = true;
                                debug("Successfully reassigned shouldCauseReequipAnimation to TransformerHelper for Main");
                            } else if (!isReequipChangedOff) {
                                isReequipChangedOff = true;
                                debug("Successfully reassigned shouldCauseReequipAnimation to TransformerHelper for Off");
                            }
                        }
                    }
                }

                return method;
            }
        }
    };
}
