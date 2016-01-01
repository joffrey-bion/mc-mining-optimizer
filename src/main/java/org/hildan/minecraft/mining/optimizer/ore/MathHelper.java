package org.hildan.minecraft.mining.optimizer.ore;

public class MathHelper {

    private static float[] a = new float[65536];

    private static final int[] b;

    static {
        for (int i = 0; i < 65536; ++i) {
            a[i] = (float) Math.sin((double) i * 3.141592653589793D * 2.0D / 65536.0D);
        }

        b = new int[] { 0, 1, 28, 2, 29, 14, 24, 3, 30, 22, 20, 15, 25, 17, 4, 8, 31, 27, 13, 23, 21, 19, 16, 7, 26, 12, 18, 6, 11, 5, 10,
            9 };
    }

    public static final float sin(float f) {
        return a[(int) (f * 10430.378F) & '\uffff'];
    }

    public static final float cos(float f) {
        return a[(int) (f * 10430.378F + 16384.0F) & '\uffff'];
    }

    public static int floor(double d0) {
        int i = (int) d0;
        return d0 < (double) i ? i - 1 : i;
    }
}
