package com.zetra.econsig.persistence.query.relatorio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.Columns;

public class RelatorioEstatisticoProcessamentoPeriodosQuery extends ReportHQuery {

    private final short QNT_RESULTADOS = 12;

    public List<String> funCodigos;
    public List<String> tarCodigos;
    public AcessoSistema responsavel;

    public RelatorioEstatisticoProcessamentoPeriodosQuery() {
    }

    public RelatorioEstatisticoProcessamentoPeriodosQuery(List<String> funCodigos, List<String> tarCodigos, AcessoSistema responsavel) {
        this.funCodigos = funCodigos;
        this.tarCodigos = tarCodigos;
        this.responsavel = responsavel;
    }

    @Override
    public void setCriterios(TransferObject criterio) {
        if (criterio != null) {
            funCodigos = (List<String>) criterio.getAttribute("funCodigos");
            tarCodigos = (List<String>) criterio.getAttribute("tarCodigos");
        }
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" SELECT har.harPeriodo ");
        corpoBuilder.append(" from HistoricoArquivo as har ");

        corpoBuilder.append(" WHERE 1 = 1 ");

        if (funCodigos != null && !funCodigos.isEmpty()) {
            corpoBuilder.append(" AND har.funcao.funCodigo ").append(criaClausulaNomeada("funCodigos", funCodigos));
        }

        if (tarCodigos != null && !tarCodigos.isEmpty()) {
            corpoBuilder.append(" AND har.tipoArquivo.tarCodigo ").append(criaClausulaNomeada("tarCodigos", tarCodigos));
        }

        corpoBuilder.append(" GROUP BY har.harPeriodo");
        corpoBuilder.append(" ORDER BY har.harPeriodo DESC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (funCodigos != null && !funCodigos.isEmpty()) {
            defineValorClausulaNomeada("funCodigos", funCodigos, query);
        }

        if (tarCodigos != null && !tarCodigos.isEmpty()) {
            defineValorClausulaNomeada("tarCodigos", tarCodigos, query);
        }

        query.setMaxResults(QNT_RESULTADOS);
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.HAR_PERIODO
        };
    }
}