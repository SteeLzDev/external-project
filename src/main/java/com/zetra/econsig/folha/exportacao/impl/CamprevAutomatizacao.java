package com.zetra.econsig.folha.exportacao.impl;

import org.springframework.dao.DataAccessException;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;

/**
 * <p>Title: CamprevAutomatizacao</p>
 * <p>Description: Implementações específicas para Camprev validando margens na tabela de registro servidor.
 * Essa classe deverá ser excluída após implantação das modificações para utilização da classe {@link Camprev} que valida as margens na tabela de margem registro servidor.
 * </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CamprevAutomatizacao extends ExportaMovimentoBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CamprevAutomatizacao.class);

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        try {
            // Remove da tabela de exportação as ADE que não cabem na margem
            LOG.debug("CamprevAutomatizacao.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
            removerContratosSemMargemMovimentoMensal();
            LOG.debug("fim - CamprevAutomatizacao.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
