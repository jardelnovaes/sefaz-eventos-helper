package br.com.jardelnovaes.sefaz.eventos.helper

import br.com.jardelnovaes.sefaz.eventos.helper.model.LogLevel
import br.com.jardelnovaes.sefaz.eventos.helper.model.ManifestacaoEventRequest
import br.com.swconsultoria.nfe.dom.enuns.ManifestacaoEnum
import java.awt.BorderLayout
import java.awt.Component
import java.awt.Dimension
import java.time.LocalDateTime
import java.util.function.Consumer
import javax.swing.*
import javax.swing.text.DefaultCaret

class MainForm: JFrame() {
    companion object {
        private const val DEFAULT_COMPONENT_HEIGHT = 25
        private const val DEFAULT_PANE_WIDTH = 700
        private val DEFAULT_PANE_PREFERRED_SIZE = Dimension(DEFAULT_PANE_WIDTH, 30)
    }

    private val txtCnpj: JTextField
    private val txtUf: JTextField
    private val txtAccessKey: JTextField
    private val cboManifestacaoEvents: JComboBox<ManifestacaoEnum>
    private val logger: JTextArea

    init {
        layout = BorderLayout()
        size = Dimension(DEFAULT_PANE_WIDTH + 100, 400)
        title = "Sefaz Eventos - Helper"
        defaultCloseOperation = EXIT_ON_CLOSE

        logger = JTextArea().apply {
            isEditable = false
            wrapStyleWord = true
            lineWrap = true
            (caret as DefaultCaret).updatePolicy = DefaultCaret.ALWAYS_UPDATE
        }

        txtCnpj = JTextField(AppProperties.cnpj).apply {
            minimumSize = Dimension(80, DEFAULT_COMPONENT_HEIGHT)
        }

        txtUf = JTextField(AppProperties.uf, 3).apply {
            minimumSize = Dimension(80, DEFAULT_COMPONENT_HEIGHT)
            maximumSize = Dimension(120, DEFAULT_COMPONENT_HEIGHT)
        }

        txtAccessKey = JTextField()
        cboManifestacaoEvents = JComboBox(ManifestacaoEnum.entries.toTypedArray())

        addTopFieldsArea()
        addLoggerArea()

        log("Iniciado")
    }

    private fun addTopFieldsArea() {
        add(JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            preferredSize = Dimension(DEFAULT_PANE_WIDTH, 60)

            addRowSpace(this)

            add(JPanel().apply {
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                preferredSize = DEFAULT_PANE_PREFERRED_SIZE
                addColumnSpace(this)

                addComponentWithColumnSpace(this, JLabel("CNPJ Emissor"))
                addComponentWithColumnSpace(this, txtCnpj)
                addComponentWithColumnSpace(this, JLabel("UF Emissor"))
                addComponentWithColumnSpace(this, txtUf)
                addComponentWithColumnSpace(this, JLabel("Tipo Evento"))
                addComponentWithColumnSpace(this, cboManifestacaoEvents)
            })

            addRowSpace(this)

            add(JPanel().apply {
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                preferredSize = DEFAULT_PANE_PREFERRED_SIZE
                addColumnSpace(this)

                addComponentWithColumnSpace(this, JLabel("Chave de acesso"))
                addComponentWithColumnSpace(this, txtAccessKey)
                addComponentWithColumnSpace(this, JButton("Enviar").apply { addActionListener { onSendClicked() } })
            })
        }, BorderLayout.NORTH)
    }

    private fun addLoggerArea() {
        val loggerWithScroll = JScrollPane(logger, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS)
        add(JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            preferredSize = Dimension(DEFAULT_PANE_WIDTH - 20, 150)

            addRowSpace(this, 20)

            add(JPanel().apply {
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                preferredSize = DEFAULT_PANE_PREFERRED_SIZE

                addColumnSpace(this)
                addComponentWithColumnSpace(this, JLabel("Log").apply {
                    size = Dimension(40, DEFAULT_COMPONENT_HEIGHT)
                })
            })

            add(JPanel().apply {
                layout = BoxLayout(this, BoxLayout.X_AXIS)
                preferredSize = DEFAULT_PANE_PREFERRED_SIZE

                addColumnSpace(this)
                addComponentWithColumnSpace(this, loggerWithScroll)
            })
        }, BorderLayout.CENTER)
    }

    private fun addColumnSpace(pane: JPanel, width: Int = 10) {
        pane.add(Box.createRigidArea(Dimension(width, 0)))
    }

    private fun addRowSpace(pane: JPanel, height: Int = 5) {
        pane.add(Box.createRigidArea(Dimension(0, height)))
    }

    private fun addComponentWithColumnSpace(panel: JPanel, component: Component) {
        panel.add(component)
        addColumnSpace(panel)
    }

    private fun onSendClicked() {
        if (JOptionPane.showConfirmDialog(null, "Deseja realmente continuar?", "Enviar Evento",
                                          JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return
        }

        try {
            val loggerFunction: Consumer<String> = Consumer { log(it) }
            val event = cboManifestacaoEvents.selectedItem as ManifestacaoEnum
            val request = ManifestacaoEventRequest(txtCnpj.text, txtUf.text, event, txtAccessKey.text)

            log("Iniciando envio do evento ${request.event.codigo}-${request.event.name} para a chave de acesso ${request.accessKey}, aguarde...")

            ManifestacaoSender.sendEvent(request, loggerFunction)
        } catch (e: Exception) {
            val message = "Erro ao enviar o evento: ${e.message}"
            log("$message\n${e.stackTraceToString()}", LogLevel.ERROR)
            JOptionPane.showMessageDialog(null, message, "Erro", JOptionPane.ERROR_MESSAGE)
        }
    }

    private fun log(text: String, level: LogLevel = LogLevel.INFO) {
        val logTime = LocalDateTime.now()
        logger.append("[$logTime] [$level] $text\n")
    }
}
