package com.zetra.econsig.persistence.query.senha;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaSenhaAutorizacaoServidorQuery</p>
 * <p>Description: Retorna a senhas de autorização dos usuários servidores.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaSenhaAutorizacaoServidorQuery extends HQuery {

    public String usuCodigo;
    public String sasSenha;
    public boolean senhasValidas = true;
    public boolean count = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = null;

        if (count) {
            corpo = "SELECT COUNT(*)";
        } else {
            corpo = "SELECT "
                  + "sas.usuCodigo, "
                  + "sas.sasDataCriacao, "
                  + "sas.sasDataExpiracao, "
                  + "sas.sasSenha, "
                  + "sas.sasQtdOperacoes";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" FROM SenhaAutorizacaoServidor sas ");
        corpoBuilder.append(" WHERE sas.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo));

        if (senhasValidas) {
            corpoBuilder.append(" AND sas.sasDataExpiracao > current_date()");
            corpoBuilder.append(" AND sas.sasQtdOperacoes > 0");
        }

        if (!TextHelper.isNull(sasSenha)) {
            corpoBuilder.append(" AND sas.sasSenha ").append(criaClausulaNomeada("sasSenha", sasSenha));
        }

        if (!count) {
            corpoBuilder.append(" ORDER BY sas.sasDataExpiracao ASC, sas.sasDataCriacao ASC ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("usuCodigo", usuCodigo, query);

        if (!TextHelper.isNull(sasSenha)) {
            defineValorClausulaNomeada("sasSenha", sasSenha, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SAS_USU_CODIGO,
                Columns.SAS_DATA_CRIACAO,
                Columns.SAS_DATA_EXPIRACAO,
                Columns.SAS_SENHA,
                Columns.SAS_QTD_OPERACOES
        };
    }
}
