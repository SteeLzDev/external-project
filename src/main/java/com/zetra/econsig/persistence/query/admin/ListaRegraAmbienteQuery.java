package com.zetra.econsig.persistence.query.admin;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaRegraAmbienteQuery</p>
 * <p>Description: Listagem de regras de validação do ambiente.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaRegraAmbienteQuery extends HQuery {
    
    public Short reaAtivo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        
        StringBuilder corpoBuilder = new StringBuilder(); 
        corpoBuilder.append(" select ");
        corpoBuilder.append(" rea.reaCodigo, ");
        corpoBuilder.append(" rea.reaDescricao, ");
        corpoBuilder.append(" rea.reaAtivo, ");
        corpoBuilder.append(" rea.reaDataCadastro, ");
        corpoBuilder.append(" rea.reaJavaClassName, ");
        corpoBuilder.append(" rea.reaSequencia, ");
        corpoBuilder.append(" rea.reaBloqueiaSistema ");
        corpoBuilder.append(" from RegraValidacaoAmbiente rea ");
        corpoBuilder.append(" where 1=1 ");

        if (reaAtivo != null) {
            corpoBuilder.append(" and rea.reaAtivo ").append(criaClausulaNomeada("reaAtivo", reaAtivo));
        }

        corpoBuilder.append(" order by rea.reaSequencia ASC");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        
        if (reaAtivo != null) {
            defineValorClausulaNomeada("reaAtivo", reaAtivo, query);
        }
        
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.REA_CODIGO,
                Columns.REA_DESCRICAO,
                Columns.REA_ATIVO,
                Columns.REA_DATA_CADASTRO,
                Columns.REA_JAVA_CLASS_NAME,
                Columns.REA_SEQUENCIA,
                Columns.REA_BLOQUEIA_SISTEMA
                };
    }
}
