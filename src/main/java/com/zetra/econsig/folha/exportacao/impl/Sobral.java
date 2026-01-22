package com.zetra.econsig.folha.exportacao.impl;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;

/**
 * <p>Title: NovaVenecia</p>
 * <p>Description: Implementações específicas para Sobral.</p>
 * <p>Copyright: Copyright (c) 2002-2024</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Sobral extends ModuloBeneficioSaude {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Sobral.class);

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        // Remove da tabela de exportação as ADE que não cabem na margem, seguindo a regra da
        // antiguidade do contrato e não permitindo desconto parcial
        LOG.debug("Sobral.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
        removerContratosSemMargemMovimentoMensalv2(false, null);
        LOG.debug("fim - Sobral.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
    }
}
