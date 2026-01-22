package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: RelatorioGerencialQtdeCorPorCsaQuery</p>
 * <p>Description: Retorna a quantidade de correspondentes ativos por consignatária.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioGerencialQtdeCorPorCsaQuery extends ReportHQuery {
    private int maxResultados = 0;

    public RelatorioGerencialQtdeCorPorCsaQuery() {
    }

    public RelatorioGerencialQtdeCorPorCsaQuery(int maxResultados) {
        this.maxResultados = maxResultados;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        Short corAtivo = CodedValues.STS_ATIVO;

        StringBuilder corpo = new StringBuilder();
        corpo.append("SELECT coalesce(csa.csaNome, '" + ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", (AcessoSistema) null) + "') AS CONSIGNATARIA, ");
        corpo.append("COUNT(DISTINCT cor.corCodigo) AS QUANTIDADE ");
        corpo.append("FROM Correspondente cor ");
        corpo.append("INNER JOIN cor.consignataria csa ");
        corpo.append("WHERE cor.corAtivo ").append(criaClausulaNomeada("corAtivo", corAtivo));
        corpo.append(" GROUP BY csa.csaNome ");
        corpo.append(" ORDER BY COUNT(DISTINCT cor.corCodigo) DESC ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());
        if (maxResultados > 0) {
            query.setMaxResults(maxResultados);
        }

        defineValorClausulaNomeada("corAtivo", corAtivo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "CONSIGNATARIA",
                "QUANTIDADE"
        };
    }
}
