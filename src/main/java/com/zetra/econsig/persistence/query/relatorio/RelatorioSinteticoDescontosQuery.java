package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;

/**
 * <p>Title: RelatorioSinteticoDescontosQuery</p>
 * <p>Description: Query para relatório Sintético de Descontos de Permissionários</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author:$
 * $Revision:$
 * $Date:$
 */
public class RelatorioSinteticoDescontosQuery extends RelatorioSinteticoQuery {

    @Override
    public void setCriterios(TransferObject criterio) {
    	super.setCriterios(criterio);
    	super.relatorioSinteticoDescontos = true;
        super.echCodigo = (String) criterio.getAttribute("ECH_CODIGO");
        super.plaCodigo = (String) criterio.getAttribute("PLA_CODIGO");
        super.cnvCodVerba = (String) criterio.getAttribute("CNV_COD_VERBA");        
    }
	
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        return super.preparar(session);
    }
}
