package br.com.jardelnovaes.sefaz.eventos.helper.model

import br.com.swconsultoria.nfe.dom.enuns.ManifestacaoEnum

data class ManifestacaoEventRequest(val cnpj: String, val uf: String, val event: ManifestacaoEnum, val accessKey: String) {
    init {
        require(cnpj.length == 14) { "CNPJ inválido" }
        require(uf.length == 2) { "UF inválida" }
        require(accessKey.length == 44) { "Chave de acesso inválida" }
    }
}
