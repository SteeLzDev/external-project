package com.zetra.econsig.persistence.query.relatorio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;

/**
 * <p>Title: RelatorioIntegracaoConsignatariaQuery</p>
 * <p>Description: Query de relatório Integração Consignataria</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioIntegracaoConsignatariaQuery extends ReportHQuery {

    private List<String> campos;

    @Override
    public void setCriterios(TransferObject criterio) {
        campos = (List<String>) criterio.getAttribute("CAMPOS");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getReportFields() {
        return campos.toArray(new String[campos.size()]);
    }

}
