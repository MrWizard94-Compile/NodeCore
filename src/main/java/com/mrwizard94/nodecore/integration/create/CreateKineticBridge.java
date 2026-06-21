package com.mrwizard94.nodecore.integration.create;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.lang.reflect.Method;

public final class CreateKineticBridge {
    private static volatile boolean probed;
    private static volatile Method speedMethod;

    private CreateKineticBridge() {}

    public static float maxNeighborSpeed(Level level, BlockPos pos) {
        if (!CreateModProbe.isLoaded()) {
            return 0.0F;
        }

        float max = 0.0F;
        for (Direction direction : Direction.values()) {
            BlockEntity neighbor = level.getBlockEntity(pos.relative(direction));
            if (neighbor == null) {
                continue;
            }
            float speed = readSpeed(neighbor);
            max = Math.max(max, Math.abs(speed));
        }
        return max;
    }

    private static float readSpeed(BlockEntity blockEntity) {
        if (!isKineticBlockEntity(blockEntity)) {
            return 0.0F;
        }

        try {
            Method method = resolveSpeedMethod(blockEntity.getClass());
            if (method == null) {
                return 0.0F;
            }
            Object value = method.invoke(blockEntity);
            if (value instanceof Number number) {
                return number.floatValue();
            }
        } catch (ReflectiveOperationException ignored) {
            return 0.0F;
        }
        return 0.0F;
    }

    private static boolean isKineticBlockEntity(BlockEntity blockEntity) {
        Class<?> type = blockEntity.getClass();
        while (type != null) {
            if ("KineticBlockEntity".equals(type.getSimpleName())) {
                return true;
            }
            type = type.getSuperclass();
        }
        return false;
    }

    private static Method resolveSpeedMethod(Class<?> type) throws NoSuchMethodException {
        if (!probed) {
            probed = true;
            speedMethod = findSpeedMethod(type);
        }
        return speedMethod;
    }

    private static Method findSpeedMethod(Class<?> type) throws NoSuchMethodException {
        Class<?> cursor = type;
        while (cursor != null) {
            try {
                Method method = cursor.getDeclaredMethod("getSpeed");
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException ignored) {
                try {
                    Method method = cursor.getDeclaredMethod("getGeneratedSpeed");
                    method.setAccessible(true);
                    return method;
                } catch (NoSuchMethodException ignoredAgain) {
                    cursor = cursor.getSuperclass();
                }
            }
        }
        throw new NoSuchMethodException("Kinetic speed accessor not found");
    }
}