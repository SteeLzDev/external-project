package com.zetra.econsig.persistence.query.arquivo;

import java.util.Date;

import com.zetra.econsig.values.CodedValues;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;

public class ListarHistoricoArquivoDashboardQuery extends HQuery {

    final String csaCodigo;
    final Date filterDate;

    public ListarHistoricoArquivoDashboardQuery(String csaCodigo, Date filterDate) {
        this.csaCodigo = csaCodigo;
        this.filterDate = filterDate;
    }

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        final String queryString = "SELECT har.harQtdLinhas," +
                "har.harResultadoProc," +
                "har.harDataProc, " +
                "har.harNomeArquivo";

        final StringBuilder corpo = new StringBuilder(queryString);
        corpo.append(" FROM HistoricoArquivo har");
        corpo.append(" INNER JOIN har.historicoArquivoCsaSet hcsa ON hcsa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpo.append(" WHERE har.tarCodigo ").append(criaClausulaNomeada("tarCodigo", TipoArquivoEnum.ARQUIVO_LOTE.getCodigo()));
        corpo.append(" AND har.funCodigo = '").append(CodedValues.FUN_IMPORTACAO_VIA_LOTE).append("' ");
        if (!TextHelper.isNull(filterDate)) {
            corpo.append(" AND to_year_month(har.harPeriodo) = to_year_month(:filterDate)");
        }

        final Query<Object[]> query = instanciarQuery(session, corpo.toString());

        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("tarCodigo", TipoArquivoEnum.ARQUIVO_LOTE.getCodigo(), query);
        if (!TextHelper.isNull(filterDate)) {
            defineValorClausulaNomeada("filterDate", filterDate, query);
        }
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[]{
                Columns.HAR_QTD_LINHAS,
                Columns.HAR_RESULTADO_PROC,
                Columns.HAR_DATA_PROC,
                Columns.HAR_NOME_ARQUIVO
        };
    }
}
