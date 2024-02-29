package br.com.jardelnovaes.sefaz.eventos.helper

object Launcher {
    @JvmStatic
    fun main(args: Array<String>) {
        println("Starting...")
        MainForm().isVisible = true
    }
}
