package com.zetra.econsig.persistence.query.servidor;

import java.util.List;

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
 * <p>Title: ListaRegistroServidorQuery</p>
 * <p>Description: Listagem de registro de Servidores</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaRegistroServidorPorCpfEmailQuery extends HQuery {

    public boolean recuperaRseExcluido = true;

    public String serCpf;
    public String serEmail;

    public List<String> orgCodigos;
    public List<String> estCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        if (TextHelper.isNull(serCpf) && TextHelper.isNull(serEmail)) {
            throw new HQueryException("mensagem.usoIncorretoSistema", AcessoSistema.getAcessoUsuarioSistema());
        }

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ");
        corpoBuilder.append("rse.rseCodigo, ");
        corpoBuilder.append("rse.rseMatricula, ");
        corpoBuilder.append("rse.rseTipo, ");
        corpoBuilder.append("rse.rsePrazo, ");
        corpoBuilder.append("rse.rseDataAdmissao, ");
        corpoBuilder.append("rse.rseAgenciaSal2, ");
        corpoBuilder.append("rse.rseSalario, ");
        corpoBuilder.append("rse.rseMunicipioLotacao, ");
        corpoBuilder.append("rse.rseMotivoFaltaMargem, ");
        corpoBuilder.append("ser.serCodigo, ");
        corpoBuilder.append("ser.serNome, ");
        corpoBuilder.append("ser.serNomeMae, ");
        corpoBuilder.append("ser.serDataNasc, ");
        corpoBuilder.append("ser.serCpf, ");
        corpoBuilder.append("ser.serEmail, ");
        corpoBuilder.append("ser.serAcessaHostAHost, ");
        corpoBuilder.append("ser.serEnd, ");
        corpoBuilder.append("ser.serNro, ");
        corpoBuilder.append("ser.serCompl, ");
        corpoBuilder.append("ser.serBairro, ");
        corpoBuilder.append("ser.serCidade, ");
        corpoBuilder.append("ser.serCep, ");
        corpoBuilder.append("ser.serUf, ");
        corpoBuilder.append("ser.serTel, ");
        corpoBuilder.append("ser.serCelular, ");
        corpoBuilder.append("ser.serSexo, ");
        corpoBuilder.append("ser.serNroIdt, ");
        corpoBuilder.append("ser.serDataIdt, ");
        corpoBuilder.append("ser.serNacionalidade, ");
        corpoBuilder.append("ser.serCidNasc, ");
        corpoBuilder.append("ser.serUfNasc, ");
        corpoBuilder.append("ser.serDataValidacaoEmail, ");
        corpoBuilder.append("ser.serPermiteAlterarEmail, ");
        corpoBuilder.append("ser.serDataIdentificacaoPessoal, ");
        corpoBuilder.append("usu.usuCodigo, ");
        corpoBuilder.append("usu.stuCodigo, ");
        corpoBuilder.append("usu.usuLogin, ");
        corpoBuilder.append("usu.usuChaveValidacaoTotp, ");
        corpoBuilder.append("usu.usuOperacoesValidacaoTotp, ");
        corpoBuilder.append("usu.usuDataUltAcesso, ");
        corpoBuilder.append("usu.usuOtpDataCadastro, ");
        corpoBuilder.append("usu.usuOtpCodigo, ");
        corpoBuilder.append("org.orgCodigo, ");
        corpoBuilder.append("org.orgIdentificador, ");
        corpoBuilder.append("org.orgNome, ");
        corpoBuilder.append("org.orgNomeAbrev, ");
        corpoBuilder.append("est.estCodigo, ");
        corpoBuilder.append("est.estIdentificador, ");
        corpoBuilder.append("est.estNome, ");
        corpoBuilder.append("srs.srsCodigo, ");
        corpoBuilder.append("srs.srsDescricao ");

        corpoBuilder.append(" FROM RegistroServidor rse");
        corpoBuilder.append(" INNER JOIN rse.orgao org");
        corpoBuilder.append(" INNER JOIN org.estabelecimento est");

        corpoBuilder.append(" INNER JOIN rse.servidor ser");
        corpoBuilder.append(" INNER JOIN rse.statusRegistroServidor srs");

        corpoBuilder.append(" LEFT OUTER JOIN ser.usuarioSerSet usr ");
        corpoBuilder.append(" LEFT OUTER JOIN usr.usuario usu ");

        if (ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            corpoBuilder.append(" WITH usu.usuLogin = concat(concat(concat(concat(est.estIdentificador, '-'), org.orgIdentificador), '-'), rse.rseMatricula)");
        } else  {
            corpoBuilder.append(" WITH usu.usuLogin = concat(concat(est.estIdentificador, '-'), rse.rseMatricula)");
        }

        // Evita duplicidade na listagem, ou seja o usuCodigo não deve ser nulo ou a ligação ao usuário servidor nem existe
        corpoBuilder.append(" WHERE (usu.usuCodigo IS NOT NULL OR usr.usuCodigo IS NULL)");

        if (!TextHelper.isNull(serCpf)) {
            corpoBuilder.append(" AND ser.serCpf ").append(criaClausulaNomeada("serCpf", serCpf));
        }
        if (!TextHelper.isNull(serEmail)) {
            corpoBuilder.append(" AND ser.serEmail ").append(criaClausulaNomeada("serEmail", serEmail));
        }
        if (estCodigos != null && !estCodigos.isEmpty()) {
            corpoBuilder.append(" AND est.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigos));
        }
        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            corpoBuilder.append(" AND org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        boolean ignoraServExcluidos = ParamSist.paramEquals(CodedValues.TPC_IGNORA_SERVIDORES_EXCLUIDOS, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        if (ignoraServExcluidos || !recuperaRseExcluido) {
            corpoBuilder.append(" AND srs.srsCodigo NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("')");
        }

        corpoBuilder.append(" ORDER BY rse.rseMatricula");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (!TextHelper.isNull(serCpf)) {
            defineValorClausulaNomeada("serCpf", serCpf, query);
        }
        if (!TextHelper.isNull(serEmail)) {
            defineValorClausulaNomeada("serEmail", serEmail, query);
        }
        if (estCodigos != null && !estCodigos.isEmpty()) {
            defineValorClausulaNomeada("estCodigo", estCodigos, query);
        }
        if (orgCodigos != null && !orgCodigos.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.RSE_TIPO,
                Columns.RSE_PRAZO,
                Columns.RSE_DATA_ADMISSAO,
                Columns.RSE_AGENCIA_SAL_2,
                Columns.RSE_SALARIO,
                Columns.RSE_MUNICIPIO_LOTACAO,
                Columns.RSE_MOTIVO_FALTA_MARGEM,
                Columns.SER_CODIGO,
                Columns.SER_NOME,
                Columns.SER_NOME_MAE,
                Columns.SER_DATA_NASC,
                Columns.SER_CPF,
                Columns.SER_EMAIL,
                Columns.SER_ACESSA_HOST_A_HOST,
                Columns.SER_END,
                Columns.SER_NRO,
                Columns.SER_COMPL,
                Columns.SER_BAIRRO,
                Columns.SER_CIDADE,
                Columns.SER_CEP,
                Columns.SER_UF,
                Columns.SER_TEL,
                Columns.SER_CELULAR,
                Columns.SER_SEXO,
                Columns.SER_NRO_IDT,
                Columns.SER_DATA_IDT,
                Columns.SER_NACIONALIDADE,
                Columns.SER_CID_NASC,
                Columns.SER_UF_NASC,
                Columns.SER_DATA_VALIDACAO_EMAIL,
                Columns.SER_PERMITE_ALTERAR_EMAIL,
                Columns.SER_DATA_IDENTIFICACAO_PESSOAL,
                Columns.USU_CODIGO,
                Columns.USU_STU_CODIGO,
                Columns.USU_LOGIN,
                Columns.USU_CHAVE_VALIDACAO_TOTP,
                Columns.USU_OPERACOES_VALIDACAO_TOTP,
                Columns.USU_DATA_ULT_ACESSO,
                Columns.USU_OTP_DATA_CADASTRO,
                Columns.USU_OTP_CODIGO,
                Columns.ORG_CODIGO,
                Columns.ORG_IDENTIFICADOR,
                Columns.ORG_NOME,
                Columns.ORG_NOME_ABREV,
                Columns.EST_CODIGO,
                Columns.EST_IDENTIFICADOR,
                Columns.EST_NOME,
                Columns.SRS_CODIGO,
                Columns.SRS_DESCRICAO,
        };
    }
}
