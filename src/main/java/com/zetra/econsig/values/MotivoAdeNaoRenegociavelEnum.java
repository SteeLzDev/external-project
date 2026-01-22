package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

public enum MotivoAdeNaoRenegociavelEnum {

    CONTRATO_PROPRIA_CSA("mensagem.indisponibilidade.renegociacao.contrato.propria.csa"),
    SERVIDOR_SEM_CONTRATO_CSA("mensagem.indisponibilidade.renegociacao.servidor.sem.contrato.csa"),
    CONTRATO_NO_LIMITE_CONFIGURADO("mensagem.indisponibilidade.renegociacao.contrato.no.limite.configurado"),
    CONTRATO_COM_SOLICITACAO_SALDO_LIQUIDACAO("mensagem.indisponibilidade.renegociacao.contrato.com.solicitacao.saldo.liquidacao"),
    CONTRATO_COM_DATA_FIM_PASSADA("mensagem.indisponibilidade.renegociacao.contrato.com.data.fim.passada"),
    CONTRATO_SEM_QTD_MIN_PRD_PAGAS("mensagem.indisponibilidade.renegociacao.contrato.sem.qtd.min.prd.pagas"),
    CONTRATO_SEM_PERC_MIN_PRD_PAGAS("mensagem.indisponibilidade.renegociacao.contrato.sem.perc.min.prd.pagas"),
    CONTRATO_SEM_QTD_MIN_MESES_VIGENCIA("mensagem.indisponibilidade.renegociacao.contrato.sem.qtd.min.meses.vigencia"),
    CONTRATO_SEM_PERC_MIN_MESES_VIGENCIA("mensagem.indisponibilidade.renegociacao.contrato.sem.perc.min.meses.vigencia"),
    OUTRO("mensagem.indisponibilidade.renegociacao.outro");


    private String descricao;

    private MotivoAdeNaoRenegociavelEnum(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao(AcessoSistema responsavel) {
        return ApplicationResourcesHelper.getMessage(descricao, responsavel) ;
    }

    public static final String CHAVE_MOTIVO_INDISPONIBILIDADE = "motivoIndisponibilidade";
}
