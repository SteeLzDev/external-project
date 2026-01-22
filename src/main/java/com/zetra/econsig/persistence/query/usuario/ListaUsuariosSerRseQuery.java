package com.zetra.econsig.persistence.query.usuario;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

public class ListaUsuariosSerRseQuery extends HQuery {
    public boolean count = false;

    public List<String> rseCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (!count) {
            corpo = "select distinct usu.usuLogin," +
            "usu.usuNome," +
            "usu.usuCodigo," +
            "usu.usuDataUltAcesso," +
            "usu.usuDataCad," +
            "usu.usuNovaSenha," +
            "ser.serCodigo," +
            "ser.serCpf," +
            "ser.serNome," +
            "org.orgCodigo," +
            "org.orgNome," +
            "org.orgIdentificador," +
            "est.estIdentificador," +
            "rse.rseCodigo," +
            "rse.rseMatricula," +
            "usu.statusLogin.stuCodigo," +
            "ser.serCelular," +
            "usu.usuOtpCodigo," +
            "usu.usuOtpDataCadastro,"+
            "ser.serNomeMae," +
            "est.estCodigo";
        } else {
            corpo = "select count(distinct(usu.usuLogin)) as total ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from Servidor ser ");
        corpoBuilder.append(" inner join ser.registroServidorSet rse ");
        corpoBuilder.append(" inner join rse.orgao org ");
        corpoBuilder.append(" inner join org.estabelecimento est ");
        corpoBuilder.append(" left join ser.usuarioSerSet usuSer ");
        corpoBuilder.append(" left join usuSer.usuario usu with ");

        if (ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            corpoBuilder.append(" usu.usuLogin = concat(concat(concat(concat(est.estIdentificador, '-'), org.orgIdentificador), '-'), rse.rseMatricula)");
        } else  {
            corpoBuilder.append(" usu.usuLogin = concat(concat(est.estIdentificador, '-'), rse.rseMatricula)");
        }

        corpoBuilder.append(" where 1=1 ");

        // Verifica se os usuários não retornados realmente não existem
        // DESENV-21104 : Usa o usuLogin ao invés de usuCodigo na cláusula abaixo por um erro do hibernate que monta a consulta final
        // referenciando o campo da tb_usuario_ser ao invés da tb_usuario. O login é um campo não nulo portanto é seguro a substituição.
        // Na branch DESENV-21058, com atualização do springboot e do hibernate, o erro não mais ocorre.
        corpoBuilder.append(" and (usu.usuLogin IS NOT NULL OR (usu.usuLogin IS NULL and not exists (");
        corpoBuilder.append(" select 1 from Usuario usu2 ");
        if (ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            corpoBuilder.append(" where usu2.usuLogin = concat(concat(concat(concat(est.estIdentificador, '-'), org.orgIdentificador), '-'), rse.rseMatricula)");
        } else  {
            corpoBuilder.append(" where usu2.usuLogin = concat(concat(est.estIdentificador, '-'), rse.rseMatricula)");
        }
        corpoBuilder.append(")))");

        if (rseCodigos != null && !rseCodigos.isEmpty()) {
            corpoBuilder.append(" and rse.rseCodigo ").append(criaClausulaNomeada("rseCodigos", rseCodigos));
        }

        if (!count) {
            corpoBuilder.append(" order by usu.usuNome");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (rseCodigos != null && !rseCodigos.isEmpty()) {
            defineValorClausulaNomeada("rseCodigos", rseCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.USU_LOGIN,
                Columns.USU_NOME,
                Columns.USU_CODIGO,
                Columns.USU_DATA_ULT_ACESSO,
                Columns.USU_DATA_CAD,
                Columns.USU_NOVA_SENHA,
                Columns.SER_CODIGO,
                Columns.SER_CPF,
                Columns.SER_NOME,
                Columns.ORG_CODIGO,
                Columns.ORG_NOME,
                Columns.ORG_IDENTIFICADOR,
                Columns.EST_IDENTIFICADOR,
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.USU_STU_CODIGO,
                Columns.SER_CELULAR,
                Columns.USU_OTP_CODIGO,
                Columns.USU_OTP_DATA_CADASTRO,
                Columns.SER_NOME_MAE,
                Columns.EST_CODIGO
        };
    }
}
