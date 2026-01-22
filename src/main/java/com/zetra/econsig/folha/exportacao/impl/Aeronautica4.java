package com.zetra.econsig.folha.exportacao.impl;

import org.springframework.dao.DataAccessException;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;

/**
 * <p>Title: Aeronautica4</p>
 * <p>Description: Implementações específicas para a Aeronautica.</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Aeronautica4 extends ExportaMovimentoBase {

	private static final long serialVersionUID = 7610708089284623143L;

	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Aeronautica4.class);

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        try {
            // Remove da tabela de exportação contratos de servidores bloqueados
            LOG.debug("Aeronautica.removerContratosServidoresBloqueados: " + DateHelper.getSystemDatetime());
            removerContratosServidoresBloqueados();
            LOG.debug("fim - Aeronautica.removerContratosServidoresBloqueados: " + DateHelper.getSystemDatetime());
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
