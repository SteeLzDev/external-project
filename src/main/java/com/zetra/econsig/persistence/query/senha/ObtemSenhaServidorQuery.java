package com.zetra.econsig.persistence.query.senha;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemSenhaServidorQuery</p>
 * <p>Description: Retorna a senha de um usuário servidor.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemSenhaServidorQuery extends HQuery {

    public String rseCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "SELECT " +
                "usu.usuCodigo, " +
                "usu.usuLogin, " +
                "usu.usuNome, " +
                "usu.usuSenha, " +
                "usu.usuDataExpSenha, " +
                "usu.usuSenha2, " +
                "usu.usuDataExpSenha2, " +
                "usu.usuOperacoesSenha2, " +
                "usu.usuSenhaApp, " +
                "usu.statusLogin.stuCodigo, " +
                "usu.usuDataExpSenhaApp ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" FROM Usuario usu ");
        corpoBuilder.append(" INNER JOIN usu.usuarioSerSet usr ");
        corpoBuilder.append(" INNER JOIN usr.servidor ser ");
        corpoBuilder.append(" INNER JOIN ser.registroServidorSet rse ");
        corpoBuilder.append(" INNER JOIN rse.orgao org ");
        corpoBuilder.append(" INNER JOIN org.estabelecimento est ");
        corpoBuilder.append(" WHERE rse.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));

        if (ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            corpoBuilder.append(" and usu.usuLogin = concat(concat(concat(concat(est.estIdentificador, '-'), org.orgIdentificador), '-'), rse.rseMatricula)");
        } else  {
            corpoBuilder.append(" and usu.usuLogin = concat(concat(est.estIdentificador, '-'), rse.rseMatricula)");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.USU_CODIGO,
                Columns.USU_LOGIN,
                Columns.USU_NOME,
                Columns.USU_SENHA,
                Columns.USU_DATA_EXP_SENHA,
                Columns.USU_SENHA_2,
                Columns.USU_DATA_EXP_SENHA_2,
                Columns.USU_OPERACOES_SENHA_2,
                Columns.USU_SENHA_APP,
                Columns.USU_STU_CODIGO,
                Columns.USU_DATA_EXP_SENHA_APP
        };
    }
}
