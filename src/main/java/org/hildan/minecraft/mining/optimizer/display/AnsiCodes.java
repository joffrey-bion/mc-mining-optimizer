package org.hildan.minecraft.mining.optimizer.display;

/**
 * Provides ANSI colorFg codes to print colors in the console.
 */
public class AnsiCodes {

    private static final String RESET = "\u001B[0m";

    private static final String BLACK = "\u001B[30m";

    private static final String RED = "\u001B[31m";

    private static final String GREEN = "\u001B[32m";

    private static final String YELLOW = "\u001B[33m";

    private static final String BLUE = "\u001B[34m";

    private static final String PURPLE = "\u001B[35m";

    private static final String CYAN = "\u001B[36m";

    private static final String WHITE = "\u001B[37m";

    private static final String BLACK_BG = "\u001B[40m";

    private static final String RED_BG = "\u001B[41m";

    private static final String GREEN_BG = "\u001B[42m";

    private static final String YELLOW_BG = "\u001B[43m";

    private static final String BLUE_BG = "\u001B[44m";

    private static final String PURPLE_BG = "\u001B[45m";

    private static final String CYAN_BG = "\u001B[46m";

    private static final String WHITE_BG = "\u001B[47m";

    public static String color(String s, String foregroundColor, String backgroundColor) {
        return foregroundColor + backgroundColor + s + RESET;
    }

    public static String colorFg(String s, String color) {
        return color + s + RESET;
    }

    public static String colorBg(String s, String backgroundColor) {
        return backgroundColor + s + RESET;
    }

    public static String black(String s) {
        return colorBg(s, BLACK);
    }

    public static String red(String s) {
        return colorBg(s, RED);
    }

    public static String green(String s) {
        return colorBg(s, GREEN);
    }

    public static String yellow(String s) {
        return colorBg(s, YELLOW);
    }

    public static String blue(String s) {
        return colorBg(s, BLUE);
    }

    public static String purple(String s) {
        return colorBg(s, PURPLE);
    }

    public static String cyan(String s) {
        return colorBg(s, CYAN);
    }

    public static String white(String s) {
        return colorBg(s, WHITE);
    }
}
