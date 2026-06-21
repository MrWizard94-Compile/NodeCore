package com.mrwizard94.nodecore.registry;

import com.mrwizard94.nodecore.NodeCore;
import com.mrwizard94.nodecore.command.NodeTypeArgument;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModArgumentTypes {
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPES =
            DeferredRegister.create(ForgeRegistries.COMMAND_ARGUMENT_TYPES, NodeCore.MOD_ID);

    public static final RegistryObject<SingletonArgumentInfo<NodeTypeArgument>> NODE_TYPE =
            ARGUMENT_TYPES.register("node_type", () ->
                    ArgumentTypeInfos.registerByClass(NodeTypeArgument.class,
                            SingletonArgumentInfo.contextFree(NodeTypeArgument::nodeType)));

    private ModArgumentTypes() {}
}
