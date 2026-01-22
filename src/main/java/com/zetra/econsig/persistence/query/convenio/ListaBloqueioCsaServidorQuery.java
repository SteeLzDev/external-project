package com.zetra.econsig.persistence.query.convenio;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaBloqueioCsaServidorQuery</p>
 * <p>Description: lista de consignatárias
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaBloqueioCsaServidorQuery extends HQuery {
	
    public String rseCodigo;
    public String csaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo =
            "select distinct " +
            "trs.rseCodigo, " +
            "csa.csaNome, " +
            "csa.csaCodigo, " +
            "csa.csaIdentificador, " +
            "prc.prcVlr, " +
            "prc.prcObs, " +
            "csa.csaNomeAbrev ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from Convenio cnv");
        corpoBuilder.append(" inner join cnv.consignataria csa ");
        corpoBuilder.append(" inner join cnv.orgao org ");
        corpoBuilder.append(" inner join org.registroServidorSet trs ");
        corpoBuilder.append(" left outer join trs.paramCsaRegistroSerSet prc WITH prc.csaCodigo = csa.csaCodigo");
        corpoBuilder.append(" where trs.rseCodigo = :rseCodigo and trs.orgCodigo = cnv.orgCodigo order by csa.csaNome");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        List<String> campos = new ArrayList<>();
        campos.add(Columns.RSE_CODIGO);
        campos.add(Columns.CSA_NOME);
        campos.add(Columns.CSA_CODIGO);
        campos.add(Columns.CSA_IDENTIFICADOR);
        campos.add(Columns.PRC_VLR);
        campos.add(Columns.PRC_OBS);
        campos.add(Columns.CSA_NOME_ABREV);

        return campos.toArray(new String[0]);
    }

}