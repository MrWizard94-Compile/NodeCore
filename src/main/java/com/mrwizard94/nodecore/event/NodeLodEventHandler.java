package com.mrwizard94.nodecore.event;

import com.mrwizard94.nodecore.worldgen.NodeLodBridge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class NodeLodEventHandler {
    @SubscribeEvent
    public void onDepositPlaced(NodeDepositEvent event) {
        NodeLodBridge.registerDeposit(event.getLevel(), event.getPos(), event.getDepositType());
    }
}