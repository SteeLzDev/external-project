package com.zetra.econsig.persistence.query.usuario;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemUsuarioServidorQuery</p>
 * <p>Description: Retornar informações a respeito de um servidor a partir
 * de dados de seu usuário.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemUsuarioServidorQuery extends HQuery {

    public String usuCodigo;
    public String usuLogin;
    public String rseMatricula;
    public String orgIdentificador;
    public String estIdentificador;
    public String serCodigo;
    public boolean permiteExcluidoFalecido = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select usu.usuCodigo, usu.usuLogin, usu.usuChaveValidacaoTotp, usu.usuOperacoesValidacaoTotp, usu.usuDataUltAcesso, rse.rseCodigo,");
        corpoBuilder.append(" rse.rseMatricula, ser.serCodigo, ser.serNome, ser.serEmail, ser.serCpf, org.orgCodigo, org.orgIdentificador,");
        corpoBuilder.append(" est.estCodigo, est.estIdentificador, ser.serAcessaHostAHost, ser.serEnd, ser.serNro, ser.serCompl, ser.serBairro,");
        corpoBuilder.append(" ser.serCidade, ser.serCep, ser.serUf,");
        corpoBuilder.append(" rse.rsePrazo, ser.serTel, ser.serCelular, usu.usuOtpDataCadastro, usu.usuOtpCodigo, rse.rseAgenciaSal2, rse.rseSalario,");
        corpoBuilder.append(" rse.rseDataAdmissao, rse.rseTipo, rse.statusRegistroServidor.srsCodigo, usu.stuCodigo ");

        corpoBuilder.append(" from UsuarioSer usu_ser ");
        corpoBuilder.append(" inner join usu_ser.usuario usu");
        corpoBuilder.append(" inner join usu_ser.servidor ser");
        corpoBuilder.append(" inner join ser.registroServidorSet rse");
        corpoBuilder.append(" inner join rse.orgao org");
        corpoBuilder.append(" inner join org.estabelecimento est");
        corpoBuilder.append(" where 1=1");

        if (!permiteExcluidoFalecido) {
            corpoBuilder.append(" AND rse.statusRegistroServidor.srsCodigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("')");
        }

        if (!TextHelper.isNull(usuCodigo)) {
            corpoBuilder.append(" and usu.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo));
        }

        if (!TextHelper.isNull(usuLogin)) {
            corpoBuilder.append(" and usu.usuLogin ").append(criaClausulaNomeada("usuLogin", usuLogin));
        }

        if (!TextHelper.isNull(rseMatricula)) {
            corpoBuilder.append(" and rse.rseMatricula ").append(criaClausulaNomeada("rseMatricula", rseMatricula));
        }

        if (!TextHelper.isNull(orgIdentificador)) {
            corpoBuilder.append(" and org.orgIdentificador ").append(criaClausulaNomeada("orgIdentificador", orgIdentificador));
        }

        if (!TextHelper.isNull(estIdentificador)) {
            corpoBuilder.append(" and est.estIdentificador ").append(criaClausulaNomeada("estIdentificador", estIdentificador));
        }

        if (!TextHelper.isNull(serCodigo)) {
            corpoBuilder.append(" and ser.serCodigo ").append(criaClausulaNomeada("serCodigo", serCodigo));
        }

        if (ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            corpoBuilder.append(" and usu.usuLogin = concat(concat(concat(concat(est.estIdentificador, '-'), org.orgIdentificador), '-'), rse.rseMatricula)");
        } else  {
            corpoBuilder.append(" and usu.usuLogin = concat(concat(est.estIdentificador, '-'), rse.rseMatricula)");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(usuCodigo)) {
            defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        }
        if (!TextHelper.isNull(usuLogin)) {
            defineValorClausulaNomeada("usuLogin", usuLogin, query);
        }
        if (!TextHelper.isNull(rseMatricula)) {
            defineValorClausulaNomeada("rseMatricula", rseMatricula, query);
        }
        if (!TextHelper.isNull(orgIdentificador)) {
            defineValorClausulaNomeada("orgIdentificador", orgIdentificador, query);
        }
        if (!TextHelper.isNull(estIdentificador)) {
            defineValorClausulaNomeada("estIdentificador", estIdentificador, query);
        }
        if (!TextHelper.isNull(serCodigo)) {
            defineValorClausulaNomeada("serCodigo", serCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.USU_CODIGO,
                Columns.USU_LOGIN,
                Columns.USU_CHAVE_VALIDACAO_TOTP,
                Columns.USU_OPERACOES_VALIDACAO_TOTP,
                Columns.USU_DATA_ULT_ACESSO,
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.SER_CODIGO,
                Columns.SER_NOME,
                Columns.SER_EMAIL,
                Columns.SER_CPF,
                Columns.ORG_CODIGO,
                Columns.ORG_IDENTIFICADOR,
                Columns.EST_CODIGO,
                Columns.EST_IDENTIFICADOR,
                Columns.SER_ACESSA_HOST_A_HOST,
                Columns.SER_END,
                Columns.SER_NRO,
                Columns.SER_COMPL,
                Columns.SER_BAIRRO,
                Columns.SER_CIDADE,
                Columns.SER_CEP,
                Columns.SER_UF,
                Columns.RSE_PRAZO,
                Columns.SER_TEL,
                Columns.SER_CELULAR,
                Columns.USU_OTP_DATA_CADASTRO,
                Columns.USU_OTP_CODIGO,
                Columns.RSE_AGENCIA_SAL_2,
                Columns.RSE_SALARIO,
                Columns.RSE_DATA_ADMISSAO,
                Columns.RSE_TIPO,
                Columns.SRS_CODIGO,
                Columns.USU_STU_CODIGO
        };
    }
}