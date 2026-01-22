package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;

/**
 * <p>Title: RelatorioDescontosMovFinQuery</p>
 * <p>Description: Relatório de Descontos (Movimentação financeira)</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author:  $
 * $Revision:  $
 * $Date:  $
 */
public class RelatorioDescontosMovFinQuery extends RelatorioMovFinQuery {

    @Override
    public void setCriterios(TransferObject criterio) {
    	super.setCriterios(criterio);
    	super.relatorioDescontos = true;
        super.echCodigo = (String) criterio.getAttribute("ECH_CODIGO");
        super.plaCodigo = (String) criterio.getAttribute("PLA_CODIGO");
        super.cnvCodVerba = (String) criterio.getAttribute("CNV_COD_VERBA");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        return super.preparar(session);
    }
}
