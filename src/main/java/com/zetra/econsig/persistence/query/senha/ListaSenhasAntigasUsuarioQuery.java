package com.zetra.econsig.persistence.query.senha;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaSenhasAntigasUsuarioQuery</p>
 * <p>Description: Retorna a senhas anteriores utilizadas pelo usuário.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaSenhasAntigasUsuarioQuery extends HQuery {

    public String usuCodigo;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT " +
                "usuario.usuCodigo, " +
                "senha.seaSenha, " +
                "senha.seaData";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" FROM SenhaAnterior senha ");
        corpoBuilder.append(" INNER JOIN senha.usuario usuario ");
        corpoBuilder.append(" WHERE usuario.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo));
        
        corpoBuilder.append(" ORDER BY senha.seaData ASC ");
        
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());        
        defineValorClausulaNomeada("usuCodigo", usuCodigo, query);

        return query;
    }
    
    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SEA_USU_CODIGO,
                Columns.SEA_SENHA,
                Columns.SEA_DATA
        };
    }
}
