package com.zetra.econsig.persistence.query.senha;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ListaUsuarioSerSenhaAutExpiradaQuery</p>
 * <p>Description: Listagem de usuários servidores com senhas de autorização (senha 2) expiradas,
 * porém que não possuam consignação aguardando confirmação/deferimento.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaUsuarioSerSenhaAutExpiradaQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT usu.usuCodigo, usu.usuSenha2 ");
        corpoBuilder.append("FROM Usuario usu ");
        corpoBuilder.append("INNER JOIN usu.usuarioSerSet usr ");

        // Usuários servidores com a senha 2 expirada
        corpoBuilder.append("WHERE usu.usuSenha2 IS NOT NULL ");
        corpoBuilder.append("AND usu.usuDataExpSenha2 < current_date() ");

        // E que o servidor não possua consignação ainda não deferida (não cancela pois
        // esta será utilizada no deferimento do contrato)
        corpoBuilder.append("AND NOT EXISTS (SELECT 1 FROM usr.servidor ser ");
        corpoBuilder.append("INNER JOIN ser.registroServidorSet rse ");
        corpoBuilder.append("INNER JOIN rse.orgao org ");
        corpoBuilder.append("INNER JOIN org.estabelecimento est ");
        corpoBuilder.append("INNER JOIN rse.autDescontoSet ade ");
        corpoBuilder.append("WHERE 1=1 ");

        if (ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            corpoBuilder.append(" and usu.usuLogin = concat(concat(concat(concat(est.estIdentificador, '-'), org.orgIdentificador), '-'), rse.rseMatricula)");
        } else  {
            corpoBuilder.append(" and usu.usuLogin = concat(concat(est.estIdentificador, '-'), rse.rseMatricula)");
        }

        corpoBuilder.append("AND ade.statusAutorizacaoDesconto.sadCodigo in ('");
        corpoBuilder.append(CodedValues.SAD_SOLICITADO).append("','");
        corpoBuilder.append(CodedValues.SAD_AGUARD_CONF).append("','");
        corpoBuilder.append(CodedValues.SAD_AGUARD_DEFER).append("')");
        corpoBuilder.append(") ");

        corpoBuilder.append("GROUP BY usu.usuCodigo, usu.usuSenha2 ");

        return instanciarQuery(session, corpoBuilder.toString());
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "USU_CODIGO",
                "SENHA"
        };
    }
}
