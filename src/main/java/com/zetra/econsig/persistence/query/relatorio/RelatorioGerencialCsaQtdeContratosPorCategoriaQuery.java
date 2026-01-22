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

/**
 * <p>Title: RelatorioGerencialCsaQtdeContratosPorCategoriaQuery</p>
 * <p>Description: Retorna a quantidade de contratos ativos por categoria.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioGerencialCsaQtdeContratosPorCategoriaQuery extends ReportHQuery {
    private int maxResultados = 0;
    private String periodo;

    public boolean count = false;
    public List<String> csaCodigo = null;

    public RelatorioGerencialCsaQtdeContratosPorCategoriaQuery() {
    }

    public RelatorioGerencialCsaQtdeContratosPorCategoriaQuery(boolean count) {
        this.count = count;
    }

    public RelatorioGerencialCsaQtdeContratosPorCategoriaQuery(boolean count, String periodo) {
        this.count = count;
        this.periodo = periodo;
    }

    public RelatorioGerencialCsaQtdeContratosPorCategoriaQuery(int maxResultados) {
        this.maxResultados = maxResultados;
    }

    public RelatorioGerencialCsaQtdeContratosPorCategoriaQuery(int maxResultados, String periodo) {
        this.maxResultados = maxResultados;
        this.periodo = periodo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String srsCodigo = CodedValues.SRS_ATIVO;
        List<String> sadCodigos = new ArrayList<String>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
        sadCodigos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);

        StringBuilder corpo = new StringBuilder();

        if (count) {
            corpo.append("SELECT COUNT(DISTINCT ade.adeNumero) AS TOTAL ");
        } else {
            corpo.append("SELECT coalesce(nullif(trim(rse.rseTipo),''),'"+ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", (AcessoSistema) null)+"') AS RSE_TIPO, ");
            corpo.append("COUNT(DISTINCT ade.adeNumero) AS QUANTIDADE ");
        }

        corpo.append("FROM AutDesconto ade ");
        corpo.append("INNER JOIN ade.registroServidor rse ");

        if (csaCodigo != null && !csaCodigo.isEmpty()) {
            corpo.append("INNER JOIN ade.verbaConvenio vco ");
            corpo.append("INNER JOIN vco.convenio cnv ");
            corpo.append("INNER JOIN cnv.consignataria csa ");
        }

        corpo.append("WHERE rse.statusRegistroServidor.srsCodigo ").append(criaClausulaNomeada("srsCodigo", srsCodigo));
        corpo.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));

        if (csaCodigo != null && !csaCodigo.isEmpty()) {
            corpo.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo)).append(" ");
        }

        if (!TextHelper.isNull(periodo)) {
            corpo.append(" AND ade.adeAnoMesIni ").append(criaClausulaNomeada("periodo", periodo));
        }

        if (!count) {
            corpo.append(" GROUP BY coalesce(nullif(trim(rse.rseTipo),''),'"+ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", (AcessoSistema) null)+"') ");
            corpo.append(" ORDER BY COUNT(DISTINCT ade.adeNumero) DESC ");
        }

        Query<Object[]> query = instanciarQuery(session, corpo.toString());
        if (maxResultados > 0) {
            query.setMaxResults(maxResultados);
        }

        defineValorClausulaNomeada("srsCodigo", srsCodigo, query);
        defineValorClausulaNomeada("sadCodigos", sadCodigos, query);

        if (csaCodigo != null && !csaCodigo.isEmpty()) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(periodo)) {
            defineValorClausulaNomeada("periodo", parseDateString(periodo), query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_TIPO,
                "QUANTIDADE"
        };
    }
}
