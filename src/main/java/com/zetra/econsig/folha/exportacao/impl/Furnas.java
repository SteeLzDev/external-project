package com.zetra.econsig.folha.exportacao.impl;

import java.util.List;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;

public class Furnas extends ExportaMovimentoBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Furnas.class);

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        // Remove da tabela de exportação as ADE que não cabem na margem
        LOG.debug("Furnas.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
        removerContratosSemMargemMovimentoMensalv2(true, null, true);
        LOG.debug("fim - Furnas.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
    }

    @Override
    protected String getClausulaIncideMargemNaListaContratosSemMargem(List<Short> marCodigos) {
        return "'3' as ade_inc_margem";
    }
}
