package com.zetra.econsig.persistence.query.relatorio;

import java.sql.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.Columns;

public class RelatorioEstatisticoProcessamentoQuery extends ReportHQuery {

    private final short QNT_RESULTADOS = 12;

    public String funCodigo;
    public String tarCodigo;
    public List<Date> harPeriodos;
    public AcessoSistema responsavel;

    public RelatorioEstatisticoProcessamentoQuery() {
    }

    public RelatorioEstatisticoProcessamentoQuery(String funCodigo, String tarCodigo, List<Date> harPeriodos, AcessoSistema responsavel) {
        this.funCodigo = funCodigo;
        this.tarCodigo = tarCodigo;
        this.harPeriodos = harPeriodos;
        this.responsavel = responsavel;
    }

    @Override
    public void setCriterios(TransferObject criterio) {
        if (criterio != null) {
            funCodigo = (String) criterio.getAttribute(Columns.HAR_FUN_CODIGO);
            tarCodigo = (String) criterio.getAttribute(Columns.TAR_CODIGO);
            harPeriodos = (List<Date>) criterio.getAttribute("HAR_PERIODOS");
        }
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" SELECT har.harPeriodo, MAX(har.harQtdLinhas) AS har_max_qnt_linhas ");
        corpoBuilder.append(" FROM HistoricoArquivo AS har ");

        corpoBuilder.append(" WHERE 1 = 1 ");

        if (funCodigo != null) {
            corpoBuilder.append(" AND har.funcao.funCodigo ").append(criaClausulaNomeada("funCodigo", funCodigo));
        }

        if (tarCodigo != null) {
            corpoBuilder.append(" AND har.tipoArquivo.tarCodigo ").append(criaClausulaNomeada("tarCodigo", tarCodigo));
        }

        if (harPeriodos != null && !harPeriodos.isEmpty()) {
            corpoBuilder.append(" AND har.harPeriodo ").append(criaClausulaNomeada("harPeriodos", harPeriodos));
        }

        corpoBuilder.append(" GROUP BY har.harPeriodo");
        corpoBuilder.append(" ORDER BY har.harPeriodo DESC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (funCodigo != null) {
            defineValorClausulaNomeada("funCodigo", funCodigo, query);
        }

        if (tarCodigo != null) {
            defineValorClausulaNomeada("tarCodigo", tarCodigo, query);
        }

        if (harPeriodos != null && !harPeriodos.isEmpty()) {
            defineValorClausulaNomeada("harPeriodos", harPeriodos, query);
        }

        query.setMaxResults(QNT_RESULTADOS);
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.HAR_PERIODO,
                "har_max_qnt_linhas"
        };
    }
}
