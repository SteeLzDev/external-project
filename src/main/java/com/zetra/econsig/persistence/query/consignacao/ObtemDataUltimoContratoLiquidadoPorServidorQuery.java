package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemDataUltimoContratoLiquidadoPorServidorQuery</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemDataUltimoContratoLiquidadoPorServidorQuery extends HQuery {

    public String rseCodigo;
    public String svcCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" SELECT oca.ocaData as oca_data");

        corpoBuilder.append(" FROM OcorrenciaAutorizacao oca ");
        corpoBuilder.append(" INNER JOIN oca.autDesconto ade");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv");
        corpoBuilder.append(" INNER JOIN cnv.servico svc");

        corpoBuilder.append(" WHERE ");
        corpoBuilder.append(" oca.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("' ");
        corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_LIQUIDADA).append("' ");
        corpoBuilder.append(" AND ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append(" AND svc.naturezaServico.nseCodigo = ");
        corpoBuilder.append(" (SELECT svc2.naturezaServico.nseCodigo FROM Servico svc2 ");
        corpoBuilder.append(" WHERE svc2.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        corpoBuilder.append(" ) ");

        corpoBuilder.append(" ORDER BY oca.ocaData desc ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        query.setMaxResults(1);
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.OCA_DATA
        };
    }
}
