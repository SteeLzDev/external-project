package com.zetra.econsig.persistence.query.convenio;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConvenioSvcQuery</p>
 * <p>Description: Listagem de Convênios de um Serviço</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConvenioSvcQuery extends HQuery {
    
    public boolean count = false;
    public String svcCodigo;
    public String cnvCodVerba;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";
        
        if (!count) {
            corpo = "select " +
            "cnv.cnvCodVerba, " +
            "cnv.cnvPrioridade, " +
            "csa.csaNome, " +
            "csa.csaIdentificador, " +
            "csa.csaNomeAbrev ";
            
        } else {
            corpo = "select count(distinct cnv.cnvCodVerba) as total ";
        }
        
        StringBuilder corpoBuilder = new StringBuilder(corpo);
        
        corpoBuilder.append(" from Convenio cnv ");
        corpoBuilder.append(" inner join cnv.consignataria csa ");        
        corpoBuilder.append(" where cnv.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        
        if (!TextHelper.isNull(cnvCodVerba)) {
            corpoBuilder.append(" and cnv.cnvCodVerba ").append(criaClausulaNomeada("cnvCodVerba", cnvCodVerba));
        }
        
        if (!count) {
            corpoBuilder.append(" group by cnv.cnvCodVerba, cnv.cnvPrioridade, csa.csaNome, csa.csaIdentificador, csa.csaNomeAbrev ");
            corpoBuilder.append(" order by csa.csaNome ");            
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());        
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);

        if (!TextHelper.isNull(cnvCodVerba)) {
            defineValorClausulaNomeada("cnvCodVerba", cnvCodVerba, query);
        }
        
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CNV_COD_VERBA,
                Columns.CNV_PRIORIDADE,
                Columns.CSA_NOME,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME_ABREV
        };
    }
}
