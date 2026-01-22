package com.zetra.econsig.persistence.query.funcao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaFuncoesBloqueaveisQuery</p>
 * <p>Description: Lista as funções que podem ser bloqueadas</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaFuncoesBloqueaveisQuery extends HQuery {
    public String tipo;
    
   /**
    * Bloqueio de funções por usuário e serviço.
    */
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String papCodigo = UsuarioHelper.getPapCodigo(tipo);
        String corpo = 
            "select " +
            "fun.funCodigo, " +
            "fun.funDescricao ";            
            
        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from Funcao fun");        
        corpoBuilder.append(" inner join fun.papelFuncaoSet pf");
        corpoBuilder.append(" inner join fun.grupoFuncao grf");
        
        corpoBuilder.append(" where 1=1 ");                
    
        if (!TextHelper.isNull(papCodigo)) {
            corpoBuilder.append(" AND pf.papCodigo ").append(criaClausulaNomeada("papCodigo",papCodigo));
        }
        corpoBuilder.append(" AND fun.funPermiteBloqueio ").append(" = '").append(CodedValues.TPC_SIM).append("'");        
        corpoBuilder.append(" ORDER BY fun.funDescricao");
                
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        
        if (!TextHelper.isNull(papCodigo)) {
            defineValorClausulaNomeada("papCodigo", papCodigo, query);
        }
        
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.FUN_CODIGO,
                Columns.FUN_DESCRICAO                
        };
    } 

}
