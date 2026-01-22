package com.zetra.econsig.folha.exportacao.impl;

import java.util.ArrayList;
import java.util.List;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: SaoVicente</p>
 * <p>Description: Implementações específicas para a Prefeitura de São Vicente.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SaoVicente extends ExportaMovimentoBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SaoVicente.class);

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        // Só analisa contratos que incidem na margem 1, margem Empréstimo
        List<Short> marCodigos = new ArrayList<>();
        marCodigos.add(CodedValues.INCIDE_MARGEM_SIM);

        // Remove da tabela de exportação as ADE que não cabem na margem
        LOG.debug("SaoVicente.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
        removerContratosSemMargemMovimentoMensalv2(true, marCodigos);
        LOG.debug("fim - SaoVicente.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
    }
}
