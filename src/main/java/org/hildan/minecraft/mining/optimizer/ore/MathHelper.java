package org.hildan.minecraft.mining.optimizer.ore;

@SuppressWarnings({"PointlessBitwiseExpression", "MagicNumber"})
class MathHelper {

    private static final int N_STEPS = 65536;

    private static final float[] SIN_CACHE = new float[N_STEPS];

    static {
        for (int i = 0; i < SIN_CACHE.length; ++i) {
            SIN_CACHE[i] = (float) Math.sin(i * Math.PI * 2 / N_STEPS);
        }
    }

    public static final float sin(float f) {
        return SIN_CACHE[(int) (f * 10430.378F) & 0xffff];
    }

    public static final float cos(float f) {
        return SIN_CACHE[(int) (f * 10430.378F + 16384.0F) & 0xffff];
    }

    public static int floor(double d0) {
        int i = (int) d0;
        return d0 < i ? i - 1 : i;
    }
}
