package com.zetra.econsig.persistence.query.dashboardprocessamento;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarHistoricoMediaMargemDashboardQuery</p>
 * <p>Description: Listagem de histórico da média da margem para exibir no 
 * dashboard de processamento da folha.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarHistoricoMediaMargemDashboardQuery extends HQuery {

    public String tipoEntidade;
    public String codigoEntidade;
    public Date periodoIni;
    public Date periodoFim;
    public Short marCodigo;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String cseCodigo = null;

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT hpm.hpmCodigo ");
        corpoBuilder.append(", hpm.hpmPeriodo ");
        corpoBuilder.append(", hmm.marCodigo ");
        corpoBuilder.append(", hmm.hmmMediaMargemAntes ");
        corpoBuilder.append(", hmm.hmmMediaMargemDepois ");
        corpoBuilder.append("FROM HistoricoMediaMargem hmm ");
        corpoBuilder.append("INNER JOIN hmm.historicoProcMargem hpm ");

        if (codigoEntidade != null && tipoEntidade != null && tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
            corpoBuilder.append(" INNER JOIN hpm.historicoProcMargemEstSet hpe");    
        } else if (codigoEntidade != null && tipoEntidade != null && tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
            corpoBuilder.append(" INNER JOIN hpm.historicoProcMargemOrgSet hpo");
        } else {
            corpoBuilder.append(" INNER JOIN hpm.historicoProcMargemCseSet hpc");    
        }

        corpoBuilder.append(" WHERE 1 = 1");
        
        if (codigoEntidade != null && tipoEntidade != null && tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
            corpoBuilder.append(" AND hpe.estCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
        } else if (codigoEntidade != null && tipoEntidade != null && tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
            corpoBuilder.append(" AND hpo.orgCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));    
        } else {
            cseCodigo = CodedValues.CSE_CODIGO_SISTEMA;
            corpoBuilder.append(" AND hpc.cseCodigo ").append(criaClausulaNomeada("cseCodigo", cseCodigo));    
        }

        if (marCodigo != null) {
            corpoBuilder.append(" AND hmm.margem.marCodigo ").append(criaClausulaNomeada("marCodigo", marCodigo));
        }

        if (periodoIni != null && periodoFim != null) {
            corpoBuilder.append(" AND hpm.hpmPeriodo between :periodoIni and :periodoFim");
        }

        corpoBuilder.append(" ORDER BY hpm.hpmPeriodo, hpm.hpmDataProc desc");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (codigoEntidade != null && tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
            defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);
        }

        if (!TextHelper.isNull(cseCodigo)) {
            defineValorClausulaNomeada("cseCodigo", cseCodigo, query);
        }
        
        if (marCodigo != null) {
            defineValorClausulaNomeada("marCodigo", marCodigo, query);
        }
        
        if (periodoIni != null && periodoFim != null) {
            defineValorClausulaNomeada("periodoIni", periodoIni, query);
            defineValorClausulaNomeada("periodoFim", periodoFim, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.HPM_CODIGO,
                Columns.HPM_PERIODO,
                Columns.HMM_MAR_CODIGO,
                Columns.HMM_MEDIA_MARGEM_ANTES,
                Columns.HMM_MEDIA_MARGEM_DEPOIS
        };
    }
}
