package com.zetra.econsig.persistence.query.relatorio;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

public class RelatorioGerencialQtdeServidorPorCrsQuery extends ReportHQuery {

    private int maxResultados = 0;

    public RelatorioGerencialQtdeServidorPorCrsQuery() {
    }

    public RelatorioGerencialQtdeServidorPorCrsQuery(int maxResultados) {
        this.maxResultados = maxResultados;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        List<String> situacaoServidor = new ArrayList<>();
        situacaoServidor.addAll(CodedValues.SRS_ATIVOS);

        StringBuilder corpo = new StringBuilder();
        corpo.append(" select ");
        corpo.append(" coalesce(crs.crsDescricao, '"+ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", (AcessoSistema) null)+"') AS CRS_DESCRICAO, ");
        corpo.append(" COUNT(DISTINCT rse.rseCodigo) AS QUANTIDADE ");
        corpo.append(" from RegistroServidor rse ");
        corpo.append(" INNER JOIN rse.cargoRegistroServidor crs ");
        corpo.append(" WHERE rse.statusRegistroServidor.srsCodigo ").append(criaClausulaNomeada("srsAtivo", situacaoServidor));
        corpo.append(" GROUP BY crs.crsDescricao ");
        corpo.append(" ORDER BY COUNT(DISTINCT rse.rseCodigo) DESC");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());
        if (maxResultados > 0) {
            query.setMaxResults(maxResultados);
        }
        query.setFirstResult(0);

        defineValorClausulaNomeada("srsAtivo", situacaoServidor, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CRS_DESCRICAO,
                "QUANTIDADE"
        };
    }
}