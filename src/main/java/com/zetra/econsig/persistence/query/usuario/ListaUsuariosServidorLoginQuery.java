package com.zetra.econsig.persistence.query.usuario;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.persistence.query.servidor.ListaServidorQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaUsuariosServidorLoginQuery</p>
 * <p>Description: Lista usuario para o fluxo de autenticação com CPF</p>
 * <p>Copyright: Nostrum (c) 2019</p>
 * <p>Company: Nostrum</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaUsuariosServidorLoginQuery extends HQuery {

    public String usuLogin;
    public String estIdentificador;
    public String orgIdentificador;
    public String rseMatricula;
    public String serCpf;
    public boolean serSomenteAtivo = true;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select usu.usuLogin," +
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
                "est.estCodigo," +
                "rse.rseCodigo," +
                "rse.rseMatricula," +
                "rse.statusRegistroServidor.srsCodigo," +
                "usu.statusLogin.stuCodigo," +
                "ser.serCelular," +
                "usu.usuOtpCodigo," +
                "usu.usuOtpDataCadastro,"+
                "ser.serNomeMae";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from Servidor ser ");
        corpoBuilder.append(" inner join ser.usuarioSerSet usuSer ");
        corpoBuilder.append(" inner join usuSer.usuario usu ");
        corpoBuilder.append(" inner join ser.registroServidorSet rse ");
        corpoBuilder.append(" inner join rse.orgao org ");
        corpoBuilder.append(" inner join org.estabelecimento est ");
        corpoBuilder.append(" where 1=1 ");

        if (!TextHelper.isNull(estIdentificador)) {
            corpoBuilder.append(" and est.estIdentificador ").append(criaClausulaNomeada("estIdentificador", estIdentificador));
        }

        if (!TextHelper.isNull(orgIdentificador)) {
            corpoBuilder.append(" and org.orgIdentificador ").append(criaClausulaNomeada("orgIdentificador", orgIdentificador));
        }

        corpoBuilder.append(ListaServidorQuery.gerarClausulaMatriculaCpf(rseMatricula, serCpf, false));

        if (serSomenteAtivo) {
            corpoBuilder.append(" AND rse.statusRegistroServidor.srsCodigo IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_ATIVOS, "' , '")).append("' , '").append(CodedValues.SRS_PENDENTE).append("')");
        }

        boolean loginComEstOrg = ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

        if (loginComEstOrg) {
            corpoBuilder.append(" and usu.usuLogin = concat(concat(concat(concat(est.estIdentificador, '-'), org.orgIdentificador), '-'), rse.rseMatricula) ");
        } else  {
            corpoBuilder.append(" and usu.usuLogin = concat(concat(est.estIdentificador, '-'), rse.rseMatricula) ");
        }

        if (!TextHelper.isNull(usuLogin)) {
            corpoBuilder.append(" and :usuLogin = usu.usuLogin ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        ListaServidorQuery.definirClausulaMatriculaCpf(rseMatricula, serCpf, false, query);

        if (!TextHelper.isNull(estIdentificador)) {
            defineValorClausulaNomeada("estIdentificador", estIdentificador, query);
        }

        if (!TextHelper.isNull(orgIdentificador)) {
            defineValorClausulaNomeada("orgIdentificador", orgIdentificador, query);
        }

        if (!TextHelper.isNull(usuLogin)) {
            defineValorClausulaNomeada("usuLogin", usuLogin, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.USU_LOGIN,
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
                Columns.EST_CODIGO,
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.SRS_CODIGO,
                Columns.USU_STU_CODIGO,
                Columns.SER_CELULAR,
                Columns.USU_OTP_CODIGO,
                Columns.USU_OTP_DATA_CADASTRO,
                Columns.SER_NOME_MAE
        };
    }

}
