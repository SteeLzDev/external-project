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
 * <p>Title: RioBranco</p>
 * <p>Description: Implementações específicas para Rio Branco.</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RioBranco extends ExportaMovimentoBase {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RioBranco.class);

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        // Só analisa contratos que incidem na margem 1, margem Empréstimo
        List<Short> marCodigos = new ArrayList<Short>();
        marCodigos.add(CodedValues.INCIDE_MARGEM_SIM);

        // Remove da tabela de exportação as ADE que não cabem na margem
        LOG.debug("RioBranco.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
        removerContratosSemMargemMovimentoMensalv2(true, marCodigos);
        LOG.debug("fim - RioBranco.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
    }
}
