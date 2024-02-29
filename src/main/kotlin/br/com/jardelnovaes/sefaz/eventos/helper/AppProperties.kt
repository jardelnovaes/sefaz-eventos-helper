package br.com.jardelnovaes.sefaz.eventos.helper

import java.io.FileInputStream
import java.nio.charset.StandardCharsets
import java.util.*

object AppProperties {

    val cnpj: String
    val uf: String
    val certificate: String
    val certificatePass: String
    val nfeEnvironment: String

    init {
        val props = Properties()
        val propFile = FileInputStream("application.properties")
        props.load(propFile)

        cnpj = props.getProperty("app.cnpj", "")
        uf = props.getProperty("app.uf", "SC")
        certificate = props.getProperty("app.certificate", "invalid")
        nfeEnvironment = props.getProperty("app.nfe.environment", "HOMOLOGACAO")

        certificatePass = try {
            String(Base64.getDecoder().decode(props.getProperty("app.certificate.pass", "MTIzNDU2")), StandardCharsets.UTF_8)
                    .reversed()
        } catch (e: Exception) {
            "123465"
        }
    }
}
