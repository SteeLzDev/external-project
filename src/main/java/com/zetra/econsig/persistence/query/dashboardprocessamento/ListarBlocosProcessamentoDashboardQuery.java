package com.zetra.econsig.persistence.query.dashboardprocessamento;

import java.text.ParseException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarBlocosProcessamentoDashboardQuery</p>
 * <p>Description: Listar o total de blocos de processamento por período, tipo e status</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarBlocosProcessamentoDashboardQuery extends HQuery  {

    public String tipoEntidade;
    public String codigoEntidade;

    public String bprPeriodo;
    public List<String> tbpCodigos;
    public List<String> sbpCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT COUNT(*) AS TOTAL ");
        corpoBuilder.append(", bpr.bprPeriodo ");
        corpoBuilder.append(", bpr.tipoBlocoProcessamento.tbpCodigo ");
        corpoBuilder.append(", bpr.statusBlocoProcessamento.sbpCodigo ");
        corpoBuilder.append("FROM BlocoProcessamento bpr ");
        corpoBuilder.append("WHERE 1=1 ");

        if (!TextHelper.isNull(bprPeriodo)) {
            corpoBuilder.append(" AND bpr.bprPeriodo ").append(criaClausulaNomeada("bprPeriodo", bprPeriodo));
        }
        if (tbpCodigos != null && !tbpCodigos.isEmpty()) {
            corpoBuilder.append(" AND bpr.tipoBlocoProcessamento.tbpCodigo ").append(criaClausulaNomeada("tbpCodigos", tbpCodigos));
        }
        if (sbpCodigos != null && !sbpCodigos.isEmpty()) {
            corpoBuilder.append(" AND bpr.statusBlocoProcessamento.sbpCodigo ").append(criaClausulaNomeada("sbpCodigos", sbpCodigos));
        }

        if (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
            if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                corpoBuilder.append(" AND bpr.orgao.orgCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            } else {
                corpoBuilder.append(" AND bpr.estabelecimento.estCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            }
        }

        corpoBuilder.append(" GROUP BY bpr.bprPeriodo, bpr.tipoBlocoProcessamento.tbpCodigo, bpr.statusBlocoProcessamento.sbpCodigo ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(bprPeriodo)) {
            try {
                defineValorClausulaNomeada("bprPeriodo", DateHelper.parse(bprPeriodo, "yyyy-MM-dd"), query);
            } catch (ParseException ex) {
                throw new HQueryException(ex);
            }
        }
        if (tbpCodigos != null && !tbpCodigos.isEmpty()) {
            defineValorClausulaNomeada("tbpCodigos", tbpCodigos, query);
        }
        if (sbpCodigos != null && !sbpCodigos.isEmpty()) {
            defineValorClausulaNomeada("sbpCodigos", sbpCodigos, query);
        }
        if (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
            defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "TOTAL",
                Columns.BPR_PERIODO,
                Columns.TBP_CODIGO,
                Columns.SBP_CODIGO
        };
    }
}
