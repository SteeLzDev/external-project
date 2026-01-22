package com.zetra.econsig.persistence.query.correspondente;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaAssociacaoEmpresaCorrespondenteQuery</p>
 * <p>Description: Listagem de empresas correspondentes</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaAssociacaoEmpresaCorrespondenteQuery extends HQuery {
    
    public boolean count = false;    
    public String ecoCodigo;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";
        
        if (!count) {
            corpo = " select csa.csaCodigo, " + 
            " csa.csaIdentificador, " +            
            " csa.csaNome, " +            
            " csa.csaCnpj, " + 
            " csa.csaAtivo, " + 
            " eco.ecoCodigo, " + 
            " eco.ecoIdentificador, " + 
            " eco.ecoNome, " + 
            " cor.corCodigo, " + 
            " cor.corIdentificador, " + 
            " cor.corNome, " + 
            " cor.corCnpj, " + 
            " cor.corAtivo ";             
        } else {
            corpo = "select count(*) as total ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);        
        corpoBuilder.append(" from Correspondente cor "); 
        corpoBuilder.append(" inner join cor.consignataria csa "); 
        corpoBuilder.append(" inner join cor.empresaCorrespondente eco "); 
        corpoBuilder.append(" where 1 = 1 "); 
        corpoBuilder.append(" and cor.corAtivo != ").append(CodedValues.STS_INDISP);

        if (!TextHelper.isNull(ecoCodigo)) {
            corpoBuilder.append(" and cor.empresaCorrespondente.ecoCodigo ").append(criaClausulaNomeada("ecoCodigo", ecoCodigo));            
        }
        
        if (!count) {
            corpoBuilder.append(" order by csa.csaNome");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(ecoCodigo)) {
            defineValorClausulaNomeada("ecoCodigo", ecoCodigo, query);
        }
        
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.CSA_CNPJ,
                Columns.CSA_ATIVO,
                Columns.ECO_CODIGO,
                Columns.ECO_IDENTIFICADOR,
                Columns.ECO_NOME,
                Columns.COR_CODIGO,
                Columns.COR_IDENTIFICADOR,
                Columns.COR_NOME,
                Columns.COR_CNPJ,
                Columns.COR_ATIVO
        };
    }

}
