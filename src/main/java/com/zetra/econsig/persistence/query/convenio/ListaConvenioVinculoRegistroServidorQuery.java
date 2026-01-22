package com.zetra.econsig.persistence.query.convenio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConvenioVinculoRegistroServidorQuery</p>
 * <p>Description: Listagem de Vinculos Ligados aos Convênios</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConvenioVinculoRegistroServidorQuery extends HQuery {

    public String csaCodigo;
    public String svcCodigo;
    public String svcIdentificador = null;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select cvr.vrsCodigo ");
        corpoBuilder.append("from ConvenioVinculoRegistro cvr ");
        if (!TextHelper.isNull(svcIdentificador)) {
            corpoBuilder.append("inner join cvr.servico svc ");
        }
        corpoBuilder.append("where cvr.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));

        if (!TextHelper.isNull(svcIdentificador)) {
            corpoBuilder.append(" and svc.svcIdentificador ").append(criaClausulaNomeada("svcIdentificador", svcIdentificador));
        }

        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" and cvr.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        corpoBuilder.append(" group by cvr.vrsCodigo");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);

        if (!TextHelper.isNull(svcIdentificador)) {
            defineValorClausulaNomeada("svcIdentificador", svcIdentificador, query);
        }

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[]{
                Columns.CVR_VRS_CODIGO,
        };
    }
}
