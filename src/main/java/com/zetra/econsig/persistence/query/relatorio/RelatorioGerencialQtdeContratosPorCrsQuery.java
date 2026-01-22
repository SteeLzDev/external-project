package com.zetra.econsig.persistence.query.relatorio;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

public class RelatorioGerencialQtdeContratosPorCrsQuery extends ReportHQuery {
    private int maxResultados = 0;
    private String periodo;

    public boolean count = false;

    public RelatorioGerencialQtdeContratosPorCrsQuery() {
    }

    public RelatorioGerencialQtdeContratosPorCrsQuery(boolean count) {
        this.count = count;
    }

    public RelatorioGerencialQtdeContratosPorCrsQuery(boolean count, String periodo) {
        this.count = count;
        this.periodo = periodo;
    }

    public RelatorioGerencialQtdeContratosPorCrsQuery(int maxResultados) {
        this.maxResultados = maxResultados;
    }

    public RelatorioGerencialQtdeContratosPorCrsQuery(int maxResultados, String periodo) {
        this.maxResultados = maxResultados;
        this.periodo = periodo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        List<String> situacaoServidor = new ArrayList<>();
        situacaoServidor.addAll(CodedValues.SRS_ATIVOS);

        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);

        StringBuilder corpo = new StringBuilder();

        if (count) {
            corpo.append("SELECT COUNT(DISTINCT ade.adeNumero) AS TOTAL ");
        } else {
            corpo.append("SELECT coalesce(crs.crsDescricao, '"+ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", (AcessoSistema) null)+"') AS CRS_DESCRICAO, ");
            corpo.append("COUNT(DISTINCT ade.adeNumero) AS QUANTIDADE ");
        }

        corpo.append("FROM AutDesconto ade ");
        corpo.append("INNER JOIN ade.registroServidor rse ");
        corpo.append("INNER JOIN rse.cargoRegistroServidor crs ");
        corpo.append("WHERE rse.statusRegistroServidor.srsCodigo ").append(criaClausulaNomeada("srsCodigo", situacaoServidor));
        corpo.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        if(!count) {
            corpo.append(" AND (crs.crsDescricao IS NOT NULL OR trim(crs.crsDescricao) != '')");
        }

        if (!TextHelper.isNull(periodo)) {
            corpo.append(" AND ade.adeAnoMesIni ").append(criaClausulaNomeada("periodo", periodo));
        }

        if (!count) {
            corpo.append(" GROUP BY coalesce(crs.crsDescricao, '"+ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", (AcessoSistema) null)+"') ");
            corpo.append(" ORDER BY COUNT(DISTINCT ade.adeNumero) DESC ");
        }

        Query<Object[]> query = instanciarQuery(session, corpo.toString());
        if (maxResultados > 0) {
            query.setMaxResults(maxResultados);
        }

        defineValorClausulaNomeada("srsCodigo", situacaoServidor, query);
        defineValorClausulaNomeada("sadCodigos", sadCodigos, query);

        if (!TextHelper.isNull(periodo)) {
            defineValorClausulaNomeada("periodo", parseDateString(periodo), query);
        }

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