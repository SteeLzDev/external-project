package com.zetra.econsig.persistence.query.coeficiente;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

public class ListaServicosSemPrazoConvenioCsaQuery extends HQuery {

    public String csaCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT svc.svcCodigo ");
        corpoBuilder.append(" FROM Servico svc ");
        corpoBuilder.append(" WHERE EXISTS (");
        corpoBuilder.append("   SELECT 1 FROM svc.prazoSet prz");
        corpoBuilder.append("   INNER JOIN prz.prazoConsignatariaSet pzc");
        corpoBuilder.append("   INNER JOIN pzc.coeficienteAtivoSet cft");
        corpoBuilder.append("   WHERE prz.przAtivo = ").append(CodedValues.STS_ATIVO);
        corpoBuilder.append("     AND pzc.przCsaAtivo = ").append(CodedValues.STS_ATIVO);
        corpoBuilder.append("     AND pzc.consignataria.csaCodigo != :csaCodigo ");
        corpoBuilder.append("     AND (cft.cftDataFimVig IS NULL OR current_timestamp() BETWEEN cft.cftDataIniVig AND cft.cftDataFimVig)");
        corpoBuilder.append(") ");

        // Define os valores para os parâmetros nomeados
        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SVC_CODIGO
        };
    }

}
