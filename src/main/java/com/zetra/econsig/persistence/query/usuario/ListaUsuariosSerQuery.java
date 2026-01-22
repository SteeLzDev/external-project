package com.zetra.econsig.persistence.query.usuario;

import java.util.Date;

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
 * <p>Title: ListaUsuariosSerQuery</p>
 * <p>Description: pesquisa usuários servidores</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaUsuariosSerQuery extends HQuery {
    public boolean count = false;

    public String rseCodigo;
    public String orgIdentificador;
    public String estIdentificador;
    public String serCpf;
    public String rseMatricula;

    public String serNome;
    public Date serDataNasc;
    public String serNomeMae;
    public String serCelular;
    public boolean somenteAtivos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "";

        if (!count) {
            corpo = "select usu.usuLogin," +
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
        corpoBuilder.append(" inner join ser.usuarioSerSet usuSer ");
        corpoBuilder.append(" inner join usuSer.usuario usu ");
        corpoBuilder.append(" inner join ser.registroServidorSet rse ");
        corpoBuilder.append(" inner join rse.orgao org ");
        corpoBuilder.append(" inner join org.estabelecimento est ");
        corpoBuilder.append(" where 1=1 ");

        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append(" and rse.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        }

        if (!TextHelper.isNull(estIdentificador)) {
            corpoBuilder.append(" and est.estIdentificador ").append(criaClausulaNomeada("estIdentificador", estIdentificador));
        }

        if (!TextHelper.isNull(orgIdentificador)) {
            corpoBuilder.append(" and org.orgIdentificador ").append(criaClausulaNomeada("orgIdentificador", orgIdentificador));
        }

        if (!TextHelper.isNull(serNome)) {
            corpoBuilder.append(" and ser.serNome ").append(criaClausulaNomeada("serNome", serNome));
        }

        if (serDataNasc != null) {
            corpoBuilder.append(" and ser.serDataNasc ").append(criaClausulaNomeada("serDataNasc", serDataNasc));
        }

        if (!TextHelper.isNull(serNomeMae)) {
            corpoBuilder.append(" and ser.serNomeMae ").append(criaClausulaNomeada("serNomeMae", serNomeMae));
        }

        if (!TextHelper.isNull(serCelular)) {
            corpoBuilder.append(" and ser.serCelular ").append(criaClausulaNomeada("serCelular", serCelular));
        }

        // Não utiliza o like da matrícula para evitar que matrículas parecidas gerem problema de localização
        // do usuário servidor, mesmo porque na interface a matrícula exata é informada.
        corpoBuilder.append(ListaServidorQuery.gerarClausulaMatriculaCpf(rseMatricula, serCpf, false));

        // Servidores excluídos não serão listados na pesquisa
        boolean ignoraServExcluidos = ParamSist.paramEquals(CodedValues.TPC_IGNORA_SERVIDORES_EXCLUIDOS, CodedValues.TPC_SIM, null);
        if (ignoraServExcluidos) {
            corpoBuilder.append(" AND rse.statusRegistroServidor.srsCodigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("')");
        }

        if (somenteAtivos) {
            corpoBuilder.append(" AND rse.statusRegistroServidor.srsCodigo IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_ATIVOS, "' , '")).append("')");
        }

        if (ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            corpoBuilder.append(" and usu.usuLogin = concat(concat(concat(concat(est.estIdentificador, '-'), org.orgIdentificador), '-'), rse.rseMatricula)");
        } else  {
            corpoBuilder.append(" and usu.usuLogin = concat(concat(est.estIdentificador, '-'), rse.rseMatricula)");
        }

        if (!count) {
            corpoBuilder.append(" order by usu.usuNome");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        ListaServidorQuery.definirClausulaMatriculaCpf(rseMatricula, serCpf, false, query);

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        if (!TextHelper.isNull(estIdentificador)) {
            defineValorClausulaNomeada("estIdentificador", estIdentificador, query);
        }

        if (!TextHelper.isNull(orgIdentificador)) {
            defineValorClausulaNomeada("orgIdentificador", orgIdentificador, query);
        }

        if (!TextHelper.isNull(serNome)) {
            defineValorClausulaNomeada("serNome", serNome, query);
        }

        if (serDataNasc != null) {
            defineValorClausulaNomeada("serDataNasc", serDataNasc, query);
        }

        if (!TextHelper.isNull(serNomeMae)) {
            defineValorClausulaNomeada("serNomeMae", serNomeMae, query);
        }

        if (!TextHelper.isNull(serCelular)) {
            defineValorClausulaNomeada("serCelular", serCelular, query);
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
