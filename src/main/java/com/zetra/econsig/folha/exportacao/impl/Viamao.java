package com.zetra.econsig.folha.exportacao.impl;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;

/**
 * <p>Title: Viamao</p>
 * <p>Description: Implementações específicas para Viamão - Prefeitura Municipal de Viamão (RS).</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Viamao extends ExportaMovimentoBase {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Viamao.class);

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {

        // Remove da tabela de exportação as ADE que não cabem na margem
        LOG.debug("Viamao.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
        removerContratosSemMargemMovimentoMensalv2(true, null);
        LOG.debug("fim - Viamao.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
    }
}
