package com.zetra.econsig.persistence.query.consignataria;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: ObtemConsignatariaUsuarioQuery</p>
 * <p>Description: Retorna a consignatária de um dado usuário</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemConsignatariaUsuarioQuery extends HQuery {

    public String usuCodigo;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        
        String corpo = "select coalesce(usuarioCsa.consignataria.csaCodigo, cor.consignataria.csaCodigo) as csa ";                
        
        StringBuilder corpoBuilder = new StringBuilder(corpo);
        
        corpoBuilder.append("from Usuario usuario " +
                            " left outer join usuario.usuarioCsaSet usuarioCsa" +
                            " left outer join usuario.usuarioCorSet usuarioCor" +
                            " left outer join usuarioCor.correspondente cor  where 1=1 ");
        
        if (!TextHelper.isNull(usuCodigo)) {
            corpoBuilder.append(" and usuario.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo));
        } 

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        
        // Seta os parâmetros na query
        if (!TextHelper.isNull(usuCodigo)) {
            defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        }
        
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "csa"
        };
    }
}
