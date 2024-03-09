package br.com.jardelnovaes.sefaz.eventos.helper

import javax.swing.SwingUtilities
import javax.swing.UIManager

object Launcher {
    @JvmStatic
    fun main(args: Array<String>) {
        println("Starting...")
        try {
            for (info in UIManager.getInstalledLookAndFeels()) {
                if ("Windows" == info.name || "Macintosh" == info.name) {
                    UIManager.setLookAndFeel(info.className)
                    break
                }
            }
        } catch (e: Exception) {
            println("WARN: Cannot apply LookAndFeel.")
        }

        SwingUtilities.invokeLater { MainForm().isVisible = true }
    }
}
