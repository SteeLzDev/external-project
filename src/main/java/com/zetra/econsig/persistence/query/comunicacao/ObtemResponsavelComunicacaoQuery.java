package com.zetra.econsig.persistence.query.comunicacao;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemResponsavelComunicacaoQuery</p>
 * <p>Description: retorna informações sobre usuário gerador da comunicação.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemResponsavelComunicacaoQuery extends HQuery {

    public String cmnCodigo;
        
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";
                        
        corpo = "select " +
                        "usu.usuCodigo, " +                        
                        "usu.usuNome, " +
                        "usu.usuLogin ";
        
        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM Usuario usu");               
        corpoBuilder.append(" INNER JOIN usu.comunicacaoSet cmn ");
        corpoBuilder.append(" WHERE 1=1 ");
        
        if (!TextHelper.isNull(cmnCodigo)) {
            corpoBuilder.append(" AND cmn.cmnCodigo ").append(criaClausulaNomeada("cmnCodigo", cmnCodigo));
        }
        
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (!TextHelper.isNull(cmnCodigo)) {
            defineValorClausulaNomeada("cmnCodigo", cmnCodigo, query);
        }
        
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.USU_CODIGO,
                Columns.USU_NOME,
                Columns.USU_LOGIN
        };
    }

}
