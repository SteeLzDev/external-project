package com.zetra.econsig.persistence.query.juros;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaLimiteTaxaJurosPorServicoQuery</p>
 * <p>Description: Query que lista os limites de taxas de juros
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */

public class ListaLimiteTaxaJurosPorServicoQuery extends HQuery {
    public BigDecimal taxaJuros;
    public List<String> svcCodigos;
    public Short faixaPrazoInicial;
    public Short faixaPrazoFinal;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final String corpo = "select ltj.ltjCodigo, " +
                             "   svc.svcCodigo, " +
                             "   svc.svcDescricao, " +
                             "   ltj.ltjPrazoRef, " +
                             "   ltj.ltjJurosMax, " +
                             "   ltj.ltjVlrRef ";

        final StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from LimiteTaxaJuros ltj ");
        corpoBuilder.append("inner join ltj.servico svc ");
        corpoBuilder.append("where 1 = 1 ");

        if (!TextHelper.isNull(svcCodigos)) {
            corpoBuilder.append(" and svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigos));
        }

        if ((faixaPrazoInicial!= null) && (faixaPrazoInicial >= 0) && (faixaPrazoFinal!= null) && (faixaPrazoFinal>= faixaPrazoInicial)) {
            corpoBuilder.append(" and ltj.ltjPrazoRef between :faixaPrazoInicial and :faixaPrazoFinal");
        }

        if (!TextHelper.isNull(taxaJuros)) {
            corpoBuilder.append(" and ltj.ltjJurosMax < :taxaJuros");
        }

        corpoBuilder.append(" order by ltj.ltjPrazoRef+0 ASC");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        // Seta os parâmetros na query
        if (!TextHelper.isNull(svcCodigos)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigos, query);
        }

        if ((faixaPrazoInicial!= null) && (faixaPrazoInicial >= 0) && (faixaPrazoFinal!= null) && (faixaPrazoFinal>= faixaPrazoInicial)) {
            defineValorClausulaNomeada("faixaPrazoInicial", faixaPrazoInicial, query);
            defineValorClausulaNomeada("faixaPrazoFinal", faixaPrazoFinal, query);
        }

        if (!TextHelper.isNull(taxaJuros)) {
            defineValorClausulaNomeada("taxaJuros", taxaJuros, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                              Columns.LTJ_CODIGO,
                              Columns.LTJ_SVC_CODIGO,
                              Columns.SVC_DESCRICAO,
                              Columns.LTJ_PRAZO_REF,
                              Columns.LTJ_JUROS_MAX,
                              Columns.LTJ_VLR_REF
        };
    }
}
