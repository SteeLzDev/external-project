package com.zetra.econsig.folha.exportacao.impl;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;

public class ValidaMargemCsaEscolheExportaParcialOrdemPadrao extends ExportaMovimentoBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidaMargemCsaEscolheExportaParcialOrdemPadrao.class);

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        // Remove da tabela de exportação as ADE que não cabem na margem, envia parcial considerando a escolha da CSA
        LOG.debug("ValidaMargemCsaEscolheExportaParcialOrdemPadrao.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
        removerContratosSemMargemMovimentoMensalv2(true, null, true);
        LOG.debug("fim - ValidaMargemCsaEscolheExportaParcialOrdemPadrao.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
    }
}
