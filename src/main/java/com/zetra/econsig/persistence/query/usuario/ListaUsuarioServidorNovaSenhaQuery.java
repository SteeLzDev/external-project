package com.zetra.econsig.persistence.query.usuario;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaUsuarioServidorNovaSenhaQuery</p>
 * <p>Description: Recupera usuários servidores que terão senhas geradas automaticamente.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaUsuarioServidorNovaSenhaQuery extends HQuery {

    public boolean todosUsuAtivos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ser.serNome, ser.serCpf, rse.rseMatricula, org.orgNome, org.orgIdentificador, est.estNome, est.estIdentificador, usu.usuCodigo, usu.usuLogin");
        corpoBuilder.append(" from UsuarioSer usr ");
        corpoBuilder.append(" inner join usr.usuario usu");
        corpoBuilder.append(" inner join usr.servidor ser");
        corpoBuilder.append(" inner join ser.registroServidorSet rse");
        corpoBuilder.append(" inner join rse.orgao org");
        corpoBuilder.append(" inner join org.estabelecimento est");

        if (todosUsuAtivos) {
            corpoBuilder.append(" where rse.statusRegistroServidor.srsCodigo = '").append(CodedValues.SRS_ATIVO).append("'");
        } else {
            corpoBuilder.append(" where usu.usuSenha = '").append(CodedValues.USU_SENHA_SERVIDOR_INICIAL).append("'");
        }

        if (ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            corpoBuilder.append(" and usu.usuLogin = concat(concat(concat(concat(est.estIdentificador, '-'), org.orgIdentificador), '-'), rse.rseMatricula)");
        } else  {
            corpoBuilder.append(" and usu.usuLogin = concat(concat(est.estIdentificador, '-'), rse.rseMatricula)");
        }

        return instanciarQuery(session, corpoBuilder.toString());
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "SER_NOME",
                "SER_CPF",
                "RSE_MATRICULA",
                "ORG_NOME",
                "ORG_IDENTIFICADOR",
                "EST_NOME",
                "EST_IDENTIFICADOR",
                "USU_CODIGO",
                Columns.USU_LOGIN
       };
    }
}