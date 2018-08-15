package org.hildan.minecraft.mining.optimizer.display

/**
 * Provides ANSI color codes to print colors in the console.
 */
object AnsiCodes {

    private val RESET = "\u001B[0m"

    val BLACK = "\u001B[30m"

    val RED = "\u001B[31m"

    val GREEN = "\u001B[32m"

    val YELLOW = "\u001B[33m"

    val BLUE = "\u001B[34m"

    val PURPLE = "\u001B[35m"

    val CYAN = "\u001B[36m"

    val WHITE = "\u001B[37m"

    val BLACK_BG = "\u001B[40m"

    val RED_BG = "\u001B[41m"

    val GREEN_BG = "\u001B[42m"

    val YELLOW_BG = "\u001B[43m"

    val BLUE_BG = "\u001B[44m"

    val PURPLE_BG = "\u001B[45m"

    val CYAN_BG = "\u001B[46m"

    val WHITE_BG = "\u001B[47m"

    fun color(s: String, color1: String, color2: String): String {
        return color1 + color2 + s + RESET
    }

    fun color(s: String, color: String): String {
        return color + s + RESET
    }
}
