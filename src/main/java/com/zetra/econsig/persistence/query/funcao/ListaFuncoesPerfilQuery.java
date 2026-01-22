package com.zetra.econsig.persistence.query.funcao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaFuncoesPerfilQuery</p>
 * <p>Description: Retorna as funções associadas a um perfil, que não são repassáveis do papel origem 
 * para o papel de destino.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaFuncoesPerfilQuery extends HQuery {
    public String perCodigo;
    public String papCodigoOrigem;
    public String papCodigoDestino;
    
    /**
     * Retorna as funções associadas a um perfil, que são repassáveis do papel origem 
     * para o papel de destino.
     */
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        
        String corpo = 
            "select " +
            "fun.funCodigo ";
        
        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from Funcao fun");
        corpoBuilder.append(" inner join fun.funcaoPerfilSet per");
                
        corpoBuilder.append(" where 1=1 ");                
    
        if (!TextHelper.isNull(perCodigo)) {
            corpoBuilder.append(" AND per.perCodigo ").append(criaClausulaNomeada("perCodigo", perCodigo));
        }
        
        corpoBuilder.append(" AND NOT EXISTS (SELECT 1 FROM BloqueioRepasseFuncao brf WHERE ");
        corpoBuilder.append("brf.funcao.funCodigo = fun.funCodigo AND ");        
        corpoBuilder.append("brf.papelDestino.papCodigo ").append(criaClausulaNomeada("papCodigoDestino", papCodigoDestino)).append(" AND ");        
        corpoBuilder.append("brf.papelOrigem.papCodigo ").append(criaClausulaNomeada("papCodigoOrigem", papCodigoOrigem)).append(")");
                        
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        
        if (!TextHelper.isNull(perCodigo)) {
            defineValorClausulaNomeada("perCodigo", perCodigo, query);
        }
        
        if (!TextHelper.isNull(papCodigoDestino)) {
            defineValorClausulaNomeada("papCodigoDestino", papCodigoDestino, query);
        }
        
        if (!TextHelper.isNull(papCodigoOrigem)) {
            defineValorClausulaNomeada("papCodigoOrigem", papCodigoOrigem, query);
        }
        
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.FP_FUN_CODIGO                
        };
    }
}
