package com.zetra.econsig.folha.exportacao.impl;

import org.springframework.dao.DataAccessException;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;

/**
 * <p>Title: Ilheus</p>
 * <p>Description: Implementações específicas para a Prefeitura de Ilhéus.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author: luis $
 * $Revision: 11921 $
 * $Date: 2011-04-12 16:26:02 -0300 (Tue, 12 Apr 2011) $
 */
public class Ilheus extends ExportaMovimentoBase {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Ilheus.class);

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        try {
            // Remove da tabela de exportação as ADE que não cabem na margem
            LOG.debug("Ilheus.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
            removerContratosSemMargemMovimentoMensal();
            LOG.debug("fim - Ilheus.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
