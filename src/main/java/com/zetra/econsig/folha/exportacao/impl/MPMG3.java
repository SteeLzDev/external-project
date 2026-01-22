package com.zetra.econsig.folha.exportacao.impl;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: MPMG3</p>
 * <p>Description: Implementações específicas para MPMG - Ministério Público de Minas Gerais.</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco$
 * $Revision$
 * $Date$
 */
public class MPMG3 extends MPMG2 {
    protected static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MPMG3.class);

    @Override
    public void preGeraArqLote(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        LOG.debug("Mantém capital devido sem soma dos contratos");
    }
}
