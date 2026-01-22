package com.zetra.econsig.persistence.query.leilao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

public class ListaDadosPortabilidadeLeilaoQuery extends HQuery{
    public String adeCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final String body = "SELECT csa.csaCodigo," +
                " csa.csaNome," +
                " rad.adeCodigoOrigem" +
                " FROM RelacionamentoAutorizacao rad, AutDesconto aut, VerbaConvenio vb, Convenio cnv, Consignataria csa" +
                " WHERE rad.adeCodigoDestino " + criaClausulaNomeada("adeCodigo", adeCodigo) +
                " AND rad.tntCodigo = '" + CodedValues.TNT_SOLICITACAO_PORTABILIDADE + "'" +
                " AND rad.adeCodigoOrigem = aut.adeCodigo" +
                " AND aut.vcoCodigo = vb.vcoCodigo" +
                " AND vb.cnvCodigo = cnv.cnvCodigo" +
                " AND cnv.csaCodigo = csa.csaCodigo";

        final Query<Object[]> query = instanciarQuery(session, body);
        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO,
                Columns.CSA_NOME,
                Columns.ADE_CODIGO
        };
    }
}
