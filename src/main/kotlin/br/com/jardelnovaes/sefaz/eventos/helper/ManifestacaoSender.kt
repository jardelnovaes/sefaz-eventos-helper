package br.com.jardelnovaes.sefaz.eventos.helper

import br.com.jardelnovaes.sefaz.eventos.helper.model.ManifestacaoEventRequest
import br.com.jardelnovaes.sefaz.eventos.helper.model.ManifestacaoEventResult
import br.com.swconsultoria.certificado.Certificado
import br.com.swconsultoria.certificado.CertificadoService
import br.com.swconsultoria.nfe.Nfe
import br.com.swconsultoria.nfe.dom.ConfiguracoesNfe
import br.com.swconsultoria.nfe.dom.Evento
import br.com.swconsultoria.nfe.dom.enuns.AmbienteEnum
import br.com.swconsultoria.nfe.dom.enuns.EstadosEnum
import br.com.swconsultoria.nfe.schema.envConfRecebto.TEnvEvento
import br.com.swconsultoria.nfe.schema.envConfRecebto.TRetEnvEvento
import br.com.swconsultoria.nfe.schema.envConfRecebto.TretEvento
import br.com.swconsultoria.nfe.util.ManifestacaoUtil
import br.com.swconsultoria.nfe.util.RetornoUtil
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import java.util.function.Consumer

object ManifestacaoSender {

    fun sendEvent(request: ManifestacaoEventRequest, logger: Consumer<String>) {
        require(AppProperties.certificate.lowercase() != "invalid") { "Certificado inválido" }

        logger.accept("Inicializando configurações da lib de NFe em ${AppProperties.nfeEnvironment}")

        val estado = EstadosEnum.valueOf(request.uf.uppercase())
        val nfeEnvironment = AmbienteEnum.valueOf(AppProperties.nfeEnvironment)
        val nfeConfig = ConfiguracoesNfe.criarConfiguracoes(estado, nfeEnvironment, buildCertificate(), "schemas", ZoneId.systemDefault())

        logger.accept("Montando evento (tipo: ${request.event.codigo}, chave de acesso: ${request.accessKey})")
        val manifestacaoEvent = buildManifestacaoEvent(request, nfeConfig)

        logger.accept("Enviado evento (tipo: ${request.event.codigo}, chave de acesso: ${request.accessKey})")
        val eventResult = sendManifestacaoEvent(manifestacaoEvent, nfeConfig)

        logger.accept("Extraindo XML de retorno do evento (tipo: ${request.event.codigo}, chave de acesso: ${request.accessKey})")
        val result = extractManifestacaoEventResult(manifestacaoEvent, eventResult, nfeConfig)
        logger.accept("""tipo: ${request.event.codigo}, chave de acesso: ${request.accessKey}
            
                         Resultado: ${result.info}
                         
                         XML ProcEvento:
                         ${result.xml}""")

        logger.accept("Finalizado (tipo: ${request.event.codigo}, chave de acesso: ${request.accessKey})")
    }

    private fun buildCertificate(): Certificado {
        val certificadoBytes = Base64.getDecoder().decode(AppProperties.certificate)
        return CertificadoService.certificadoPfxBytes(certificadoBytes, AppProperties.certificatePass)
    }

    private fun buildManifestacaoEvent(request: ManifestacaoEventRequest, nfeConfig: ConfiguracoesNfe): TEnvEvento {
        val event = Evento()
        val dhEvento = ZonedDateTime.now()

        event.chave = request.accessKey
        event.cnpj = request.cnpj
        event.dataEvento = dhEvento.toLocalDateTime()
        event.tipoManifestacao = request.event

        return try {
            ManifestacaoUtil.montaManifestacao(event, nfeConfig)
        } catch (e: Exception) {
            throw IllegalStateException(e.message, e)
        }
    }

    private fun sendManifestacaoEvent(enviEvento: TEnvEvento, nfeConfig: ConfiguracoesNfe): TRetEnvEvento {
        val retorno: TRetEnvEvento

        try {
            retorno = Nfe.manifestacao(nfeConfig, enviEvento, false)
        } catch (e: Exception) {
            throw IllegalStateException("Falha ao comunicar com a SEFAZ para enviar evento de manifestação.", e)
        }

        try {
            RetornoUtil.validaManifestacao(retorno)
        } catch (e: Exception) {
            throw IllegalStateException("Falha ao processar retorno da SEFAZ referente à evento de manifestação.", e)
        }
        return retorno
    }

    private fun extractManifestacaoEventResult(enviEvento: TEnvEvento,
                                               eventResult: TRetEnvEvento,
                                               nfeConfig: ConfiguracoesNfe): ManifestacaoEventResult {
        val xml = try {
            ManifestacaoUtil.criaProcEventoManifestacao(nfeConfig, enviEvento, eventResult.retEvento[0])
        } catch (e: Exception) {
            throw IllegalStateException("Falha ao criar xml protocolado do evento de manifestação.", e)
        }

        val infEvento: TretEvento.InfEvento? = eventResult.retEvento[0]?.infEvento
        return ManifestacaoEventResult(xml, "${infEvento?.cStat} - ${infEvento?.xMotivo}")
    }
}
