package com.zetra.econsig.persistence.query.funcao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaFuncoesBloqueadasQuery</p>
 * <p>Description: Lista as funções bloqueadas para um usuário</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaFuncoesBloqueadasQuery extends HQuery {
    public String usuCodigo;
    public String tipoEntidade;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = 
            "select " +
            "buf.usuCodigo, " +
            "buf.funCodigo, " +
            "buf.servico.svcCodigo ";
        
        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from Funcao fun");
        corpoBuilder.append(" inner join fun.bloqueioUsuFunSvcSet buf ");
        corpoBuilder.append(" inner join fun.papelFuncaoSet pf ");
                
        corpoBuilder.append(" where 1=1 ");                
    
        if (!TextHelper.isNull(usuCodigo)) {
            corpoBuilder.append(" AND buf.usuCodigo ").append(criaClausulaNomeada("usuCodigo",usuCodigo));
        }
        
        if (!TextHelper.isNull(tipoEntidade)) {
            corpoBuilder.append(" AND pf.papCodigo ").append(criaClausulaNomeada("papCodigo", UsuarioHelper.getPapCodigo(tipoEntidade)));
        }
                        
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        
        if (!TextHelper.isNull(usuCodigo)) {
            defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        }

        if (!TextHelper.isNull(tipoEntidade)) {
            defineValorClausulaNomeada("papCodigo", UsuarioHelper.getPapCodigo(tipoEntidade), query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.BUF_USU_CODIGO,
                Columns.BUF_FUN_CODIGO,
                Columns.BUF_SVC_CODIGO
        };
    }   
    
}
