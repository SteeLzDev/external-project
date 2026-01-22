package com.zetra.econsig.persistence.query.parametro;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaSvcByValorFixoQuery</p>
 * <p>Description: Lista os serviços que tem valor fixo por posto de graduação.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Douglas Neves, Leonel Martins
 */
public class ListaSvcByValorFixoQuery extends HQuery {

    public String csaCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String corpo = "select " +
                "psc.csaCodigo, " +
                "csa.csaNome, " +
                "svc.svcCodigo, " +
                "svc.svcDescricao, " +
                "psc.pscVlr," +
                "svc.svcIdentificador";

        final StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from ParamSvcConsignataria psc,");
        corpoBuilder.append(" Consignataria csa,");
        corpoBuilder.append(" Servico svc");
        corpoBuilder.append(" where psc.tpsCodigo = '").append(CodedValues.TPS_VALOR_SVC_FIXO_POSTO).append("'");
        corpoBuilder.append(" and psc.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append(" and svc.svcCodigo = ").append("psc.svcCodigo");
        corpoBuilder.append(" and psc.pscVlr ").append(criaClausulaNomeada("pscVlr", CodedValues.PSC_BOOLEANO_SIM));


        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("pscVlr", CodedValues.PSC_BOOLEANO_SIM, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[]{
                Columns.SVC_CODIGO,
                Columns.CSA_NOME,
                Columns.SVC_CODIGO,
                Columns.SVC_DESCRICAO,
                Columns.PSC_VLR,
                Columns.SVC_IDENTIFICADOR
        };
    }
}
