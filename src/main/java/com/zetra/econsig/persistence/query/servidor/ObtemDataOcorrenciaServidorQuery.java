package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemDataOcorrenciaServidorQuery</p>
 * <p>Description: Obtem data de ocorrências do servidor da tabela tb_ocorrencia_servidor pelo serCodigo e tocCodigo</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author: rodrigo.rosa $
 * $Revision: 26246 $
 * $Date: 2020-01-22 09:27:49 -0200 (qua, 22 jan 2020) $
 */
public class ObtemDataOcorrenciaServidorQuery extends HQuery {

    public String serCodigo;
    public String tocCodigo;
    public boolean ordenar;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" SELECT ");
        corpoBuilder.append(" ocs.ocsData AS OCS_DATA ");
        corpoBuilder.append(" FROM OcorrenciaServidor ocs");
        corpoBuilder.append(" WHERE 1=1 ");

        if (!TextHelper.isNull(serCodigo)) {
            corpoBuilder.append(" AND ocs.servidor.serCodigo ").append(criaClausulaNomeada("serCodigo", serCodigo));
        }

        if (!TextHelper.isNull(tocCodigo)) {
            corpoBuilder.append(" AND ocs.tipoOcorrencia.tocCodigo ").append(criaClausulaNomeada("tocCodigo", tocCodigo));
        }
        
        if (ordenar) {
            corpoBuilder.append(" ORDER BY ocs.ocsData DESC ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(serCodigo)) {
            defineValorClausulaNomeada("serCodigo", serCodigo, query);
        }

        if (!TextHelper.isNull(tocCodigo)) {
            defineValorClausulaNomeada("tocCodigo", tocCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] { Columns.OCS_DATA };
    }

}
