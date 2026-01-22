package com.zetra.econsig.persistence.query.servico;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class ListaServicosByNseCsaQuery extends HQuery {

    public String csaCodigo;
    public String nseCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo =
                "select distinct svc.svcCodigo, " +
                        "   svc.svcDescricao ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from Servico svc ");
        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append("inner join svc.convenioSet cnv ");
        }
        corpoBuilder.append("inner join svc.naturezaServico nse ");
        corpoBuilder.append("where 1=1 ");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
            corpoBuilder.append(" and cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        }

        if (!TextHelper.isNull(nseCodigo)) {
            corpoBuilder.append(" and nse.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(nseCodigo)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[]{
                Columns.SVC_CODIGO,
                Columns.SVC_DESCRICAO,
        };
    }
}
