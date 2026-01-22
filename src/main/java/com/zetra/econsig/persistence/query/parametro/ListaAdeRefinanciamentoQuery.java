package com.zetra.econsig.persistence.query.parametro;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class ListaAdeRefinanciamentoQuery extends HQuery {

    public String csaCodigo;
    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();
        corpo.append(" SELECT ade.adeNumero, ade.adePrazo, ade.adePrdPagas, ade.adeCodigo ");
        corpo.append(" FROM AutDesconto ade INNER JOIN VerbaConvenio tvc on ade.vcoCodigo = tvc.vcoCodigo INNER JOIN Convenio tc on tc.cnvCodigo = tvc.cnvCodigo ");
        corpo.append(" WHERE tc.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo)).append(" AND ade.adePrazo is not null AND ade.adePrdPagas is not null and ade.sadCodigo ").append(criaClausulaNomeada("sadCodigo", CodedValues.SAD_EMANDAMENTO));
        corpo.append(" AND NOT EXISTS ( SELECT 1 FROM OcorrenciaAutorizacao toa WHERE ade.adeCodigo = toa.adeCodigo AND toa.tocCodigo ").append(criaClausulaNomeada("tocCodigo", CodedValues.TOC_ALERTA_PERCENTUAL_PARCELA_PAGA_ENVIADA_EMAIL)).append(" ) ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("tocCodigo", CodedValues.TOC_ALERTA_PERCENTUAL_PARCELA_PAGA_ENVIADA_EMAIL, query);
        defineValorClausulaNomeada("sadCodigo", CodedValues.SAD_EMANDAMENTO, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_NUMERO,
                Columns.ADE_PRAZO,
                Columns.ADE_PRD_PAGAS,
                Columns.ADE_CODIGO
        };
    }
}
