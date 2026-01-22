package com.zetra.econsig.persistence.query.vinculo;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCnvVinculoRegistroFaltanteQuery</p>
 * <p>Description: Listagem de Convênios Vínculos Registros a serem criados</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCnvVinculoRegistroFaltanteQuery extends HQuery {

    public String csaCodigo;
    public String svcCodigo;
    public List<String> vrsCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select vrs.vrsCodigo, vrs.vrsIdentificador ");
        corpoBuilder.append("from VinculoRegistroServidor vrs ");
        corpoBuilder.append("     where vrs.vrsCodigo ").append(criaClausulaNomeada("vrsCodigos", vrsCodigos));
        corpoBuilder.append("     and not exists ( ");
        corpoBuilder.append("     select 1 from ConvenioVinculoRegistro cvr ");
        corpoBuilder.append("     where cvr.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append("     and cvr.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        corpoBuilder.append("     and cvr.vrsCodigo = vrs.vrsCodigo ");
        corpoBuilder.append("  ) ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        defineValorClausulaNomeada("vrsCodigos", vrsCodigos, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[]{
                Columns.VRS_CODIGO,
                Columns.VRS_IDENTIFICADOR
        };
    }
}
