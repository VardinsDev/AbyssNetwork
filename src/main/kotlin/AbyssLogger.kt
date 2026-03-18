package me.totxy

import java.time.LocalTime
import java.time.format.DateTimeFormatter

object AbyssLogger {
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

    private const val RESET = "\u001B[0m"
    private const val DARK_PURPLE = "\u001B[35m"
    private const val PURPLE = "\u001B[95m"
    private const val CYAN = "\u001B[96m"
    private const val GRAY = "\u001B[90m"
    private const val WHITE = "\u001B[97m"
    private const val RED = "\u001B[91m"
    private const val YELLOW = "\u001B[93m"
    private const val GREEN = "\u001B[92m"

    private fun timestamp() = "$GRAY[${timeFormatter.format(LocalTime.now())}]$RESET"
    private fun prefix() = "$DARK_PURPLE[${PURPLE}Abyss Network$DARK_PURPLE]$RESET"

    fun info(message: String) =
        println("${timestamp()} ${prefix()} $CYAN[INFO]$RESET $WHITE$message$RESET")

    fun warn(message: String) =
        println("${timestamp()} ${prefix()} $YELLOW[WARN]$RESET $WHITE$message$RESET")

    fun error(message: String) =
        println("${timestamp()} ${prefix()} $RED[ERROR]$RESET $WHITE$message$RESET")

    fun success(message: String) =
        println("${timestamp()} ${prefix()} $GREEN[OK]$RESET $WHITE$message$RESET")

    fun printBanner() {
        println()
        println("$DARK_PURPLE  ▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄▄  $RESET")
        println("$DARK_PURPLE  █                                       █  $RESET")
        println("$DARK_PURPLE  █  $PURPLE    ▄▄  ▄▄▄▄  ▄   ▄  ▄▄▄▄  ▄▄▄▄    $DARK_PURPLE█  $RESET")
        println("$DARK_PURPLE  █  $PURPLE   █  █ █  ██  █ █  █     █ █       $DARK_PURPLE█  $RESET")
        println("$DARK_PURPLE  █  $PURPLE   █████ █████  ███   ▀▀▀▀  ▀▀▀▀    $DARK_PURPLE█  $RESET")
        println("$DARK_PURPLE  █  $PURPLE   █  █ █  ██   █    █     █ ▄   █  $DARK_PURPLE█  $RESET")
        println("$DARK_PURPLE  █  $PURPLE   █  █ █  ██   █     ▀▀▀▀  ▀▀▀▀   $DARK_PURPLE█  $RESET")
        println("$DARK_PURPLE  █                                       █  $RESET")
        println("$CYAN  █           N E T W O R K                █  $RESET")
        println("$DARK_PURPLE  ▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀▀  $RESET")
        println()
    }
}