package com.mrwizard94.nodecore.integration.create;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.lang.reflect.Method;

/**
 * Reflection bridge to Create's kinetic network without a compile-time Create dependency.
 *
 * <p>The speed accessor is resolved against the {@code KineticBlockEntity} base class (located by
 * walking each candidate's class hierarchy), never against a concrete leaf type. This is critical:
 * a {@link Method} resolved from a generator subclass (e.g. {@code WaterWheelBlockEntity}, which
 * declares {@code getGeneratedSpeed}) has that narrow type as its declaring class, and invoking it
 * on an unrelated kinetic block entity (a shaft, a cog) makes the generated reflective accessor cast
 * the receiver to the wrong class — a {@code ClassCastException} surfaced as
 * {@link IllegalArgumentException} that previously crashed the server tick. Resolving against the
 * shared {@code KineticBlockEntity} class guarantees the cached method is valid for every kinetic
 * block entity loaded by the same class loader.
 */
public final class CreateKineticBridge {
    private static final String KINETIC_CLASS_SIMPLE_NAME = "KineticBlockEntity";
    private static final String[] SPEED_METHOD_NAMES = {"getSpeed", "getGeneratedSpeed"};

    private static volatile boolean probed;
    private static volatile Method speedMethod;

    private CreateKineticBridge() {}

    public static boolean isAvailable() {
        return CreateModProbe.isLoaded();
    }

    public static float maxNeighborSpeed(Level level, BlockPos pos) {
        if (!isAvailable()) {
            return 0.0F;
        }

        float max = 0.0F;
        for (Direction direction : Direction.values()) {
            BlockEntity neighbor = level.getBlockEntity(pos.relative(direction));
            if (neighbor == null) {
                continue;
            }
            max = Math.max(max, readSpeed(neighbor));
        }
        return max;
    }

    public static float readSpeed(BlockEntity blockEntity) {
        if (blockEntity == null) {
            return 0.0F;
        }

        Class<?> kineticType = kineticBaseClass(blockEntity.getClass());
        if (kineticType == null) {
            return 0.0F;
        }

        Method method = resolveSpeedMethod(kineticType);
        if (method == null) {
            return 0.0F;
        }

        try {
            Object value = method.invoke(blockEntity);
            if (value instanceof Number number) {
                return Math.abs(number.floatValue());
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
            // A Create API shift or an unexpected receiver must never crash the server tick.
            return 0.0F;
        }
        return 0.0F;
    }

    /**
     * Walks the hierarchy of {@code type} and returns the {@code KineticBlockEntity} class itself,
     * or {@code null} if {@code blockEntity} is not a Create kinetic block entity.
     */
    private static Class<?> kineticBaseClass(Class<?> type) {
        Class<?> cursor = type;
        while (cursor != null) {
            if (KINETIC_CLASS_SIMPLE_NAME.equals(cursor.getSimpleName())) {
                return cursor;
            }
            cursor = cursor.getSuperclass();
        }
        return null;
    }

    private static Method resolveSpeedMethod(Class<?> kineticBaseType) {
        if (probed) {
            return speedMethod;
        }
        Method resolved = findSpeedMethod(kineticBaseType);
        speedMethod = resolved;
        probed = true;
        return resolved;
    }

    private static Method findSpeedMethod(Class<?> kineticBaseType) {
        for (String name : SPEED_METHOD_NAMES) {
            try {
                Method method = kineticBaseType.getMethod(name);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException ignored) {
                // Try the next candidate accessor name.
            }
        }
        return null;
    }
}
