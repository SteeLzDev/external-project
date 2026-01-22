package com.zetra.econsig.persistence.query.margem;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaHistoricoMediaMargemQuery</p>
 * <p>Description: Listagem de histórico da média da margem.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaHistoricoMediaMargemQuery extends HQuery {

    public List<String> estCodigos;
    public List<String> orgCodigos;
    public Date hpmPeriodo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String cseCodigo = null;

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" select hpm.hpmCodigo, hmm.marCodigo, hmm.hmmMediaMargemAntes, hmm.hmmMediaMargemDepois");
        corpoBuilder.append(" from HistoricoMediaMargem hmm");
        corpoBuilder.append(" inner join hmm.historicoProcMargem hpm");
        corpoBuilder.append(" inner join hpm.usuario usu");

        if (estCodigos != null && !estCodigos.isEmpty()) {
            corpoBuilder.append(" inner join hpm.historicoProcMargemEstSet hpe");
        } else if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" inner join hpm.historicoProcMargemOrgSet hpo");
        } else {
            corpoBuilder.append(" inner join hpm.historicoProcMargemCseSet hpc");
        }

        corpoBuilder.append(" where 1 = 1");

        if (estCodigos != null && !estCodigos.isEmpty()) {
            corpoBuilder.append(" and hpe.estCodigo ").append(criaClausulaNomeada("estCodigos", estCodigos));
        } else if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" and hpo.orgCodigo ").append(criaClausulaNomeada("orgCodigos", orgCodigos));
        } else {
            cseCodigo = CodedValues.CSE_CODIGO_SISTEMA;
            corpoBuilder.append(" and hpc.cseCodigo ").append(criaClausulaNomeada("cseCodigo", cseCodigo));
        }

        if (!TextHelper.isNull(hpmPeriodo)) {
            corpoBuilder.append(" and hpm.hpmPeriodo ").append(criaClausulaNomeada("hpmPeriodo", hpmPeriodo));
        }

        corpoBuilder.append(" order by hpm.hpmPeriodo desc, hpm.hpmDataProc desc");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (estCodigos != null && !estCodigos.isEmpty()) {
            defineValorClausulaNomeada("estCodigos", estCodigos, query);
        } else if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigos", orgCodigos, query);
        } else {
            defineValorClausulaNomeada("cseCodigo", cseCodigo, query);
        }

        if (!TextHelper.isNull(hpmPeriodo)) {
            defineValorClausulaNomeada("hpmPeriodo", hpmPeriodo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.HPM_CODIGO,
                Columns.HMM_MAR_CODIGO,
                Columns.HMM_MEDIA_MARGEM_ANTES,
                Columns.HMM_MEDIA_MARGEM_DEPOIS
        };
    }
}
