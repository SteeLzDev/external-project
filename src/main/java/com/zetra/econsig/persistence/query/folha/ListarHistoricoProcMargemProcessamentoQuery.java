package com.zetra.econsig.persistence.query.folha;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarHistoricoProcMargemProcessamentoQuery</p>
 * <p>Description: Listar o histórico de processamento de margem de um período de processamento folha.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarHistoricoProcMargemProcessamentoQuery extends HQuery {

    public List<String> estCodigos;
    public List<String> orgCodigos;
    public String cseCodigo;
    public Date hpmPeriodo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select hpm.hpmCodigo, hpm.hpmPeriodo, hpm.hpmDataProc, hpm.hpmQtdServidoresAntes, ");
        corpoBuilder.append("hpm.hpmQtdServidoresDepois ");
        corpoBuilder.append("from HistoricoProcMargem hpm ");

        if (estCodigos != null && !estCodigos.isEmpty()) {
            corpoBuilder.append(" inner join hpm.historicoProcMargemEstSet hpe");
        } else if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" inner join hpm.historicoProcMargemOrgSet hpo");
        } else if (!TextHelper.isNull(cseCodigo)) {
            corpoBuilder.append(" inner join hpm.historicoProcMargemCseSet hpc");
        }

        corpoBuilder.append(" where hpm.hpmPeriodo ").append(criaClausulaNomeada("hpmPeriodo", hpmPeriodo));

        if (estCodigos != null && !estCodigos.isEmpty()) {
            corpoBuilder.append(" and hpe.estCodigo ").append(criaClausulaNomeada("estCodigos", estCodigos));
        } else if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" and hpo.orgCodigo ").append(criaClausulaNomeada("orgCodigos", orgCodigos));
        } else if (!TextHelper.isNull(cseCodigo)) {
            corpoBuilder.append(" and hpc.cseCodigo ").append(criaClausulaNomeada("cseCodigo", cseCodigo));
        }

        corpoBuilder.append(" order by hpm.hpmPeriodo desc, hpm.hpmDataProc desc");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("hpmPeriodo", hpmPeriodo, query);

        if (estCodigos != null && !estCodigos.isEmpty()) {
            defineValorClausulaNomeada("estCodigos", estCodigos, query);
        } else if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigos", orgCodigos, query);
        } else if (!TextHelper.isNull(cseCodigo)) {
            defineValorClausulaNomeada("cseCodigo", cseCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.HPM_CODIGO,
                Columns.HPM_PERIODO,
                Columns.HPM_DATA_PROC,
                Columns.HPM_QTD_SERVIDORES_ANTES,
                Columns.HPM_QTD_SERVIDORES_DEPOIS
        };
    }
}
