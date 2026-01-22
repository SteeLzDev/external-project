package com.zetra.econsig.persistence.query.convenio;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConvenioConsolidaDescontosQuery</p>
 * <p>Description: Lista o parâmetro de consolidação de descontos para o convênio.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConvenioConsolidaDescontosQuery extends HQuery {
    
    public String svcCodigo;
    public String orgCodigo;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select distinct " +
                       "svc.svcDescricao, " +
                       "svc.svcIdentificador, " +
                       "org.orgCodigo, " +
                       "org.orgNome, " +
                       "org.orgIdentificador, " +
                       "cnv.cnvCodVerba, " +
                       "cnv.cnvConsolidaDescontos ";
        
        StringBuilder corpoBuilder = new StringBuilder(corpo);
        
        corpoBuilder.append(" from Convenio cnv ");
        corpoBuilder.append(" inner join cnv.servico svc ");
        corpoBuilder.append(" inner join cnv.orgao org ");
        corpoBuilder.append(" where 1=1");
        
        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" and svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }
        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }
        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SVC_DESCRICAO,
                Columns.SVC_IDENTIFICADOR,
                Columns.ORG_CODIGO,
                Columns.ORG_NOME,
                Columns.ORG_IDENTIFICADOR,
                Columns.CNV_COD_VERBA,
                Columns.CNV_CONSOLIDA_DESCONTOS        
        };
    }
}
