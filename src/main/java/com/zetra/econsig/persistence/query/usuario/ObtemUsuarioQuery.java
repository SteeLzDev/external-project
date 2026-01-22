package com.zetra.econsig.persistence.query.usuario;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: FindUsuarioQuery</p>
 * <p>Description: Busca usuário para login</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemUsuarioQuery extends HQuery {
    public String usuLogin = null;
    public String usuCodigo = null;
    public String usuEmail = null;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        if (TextHelper.isNull(usuCodigo) && TextHelper.isNull(usuLogin) && TextHelper.isNull(usuEmail)) {
            throw new HQueryException("mensagem.erro.interno.contate.administrador", AcessoSistema.getAcessoUsuarioSistema());
        }

        String corpo = "SELECT usuario.usuNome, " +
                "   usuario.usuLogin, " +
                "   usuario.usuSenha, " +
                "   usuario.usuSenhaApp, " +
                "   usuario.usuEmail, " +
                "   usuario.usuTel, " +
                "   usuario.statusLogin.stuCodigo, " +
                "   usuario.usuCodigo, " +
                "   usuario.usuDataUltAcesso, " +
                "   usuario.usuDataExpSenha, " +
                "   usuario.usuIpAcesso, " +
                "   usuario.usuDdnsAcesso, " +
                "   usuario.usuDataCad, " +
                "   usuario.usuCentralizador, " +
                "   usuario.usuExigeCertificado, " +
                "   usuario.usuCpf, " +
                "   usuario.usuOtpCodigo, " +
                "   usuario.usuChaveRecuperarSenha, " +
                "   usuario.usuChaveValidacaoTotp, " +
                "   usuario.usuPermiteValidacaoTotp, " +
                "   usuario.usuOperacoesValidacaoTotp, " +
                "   usuario.usuQtdConsultasMargem, " +
                "   usuario.usuChaveValidacaoEmail, " +
                "   usuario.usuDataValidacaoEmail, " +
                "   usuario.usuDeficienteVisual, " +
                "   usuario.usuAutenticaSso, " +
                "   usuarioSer.servidor.serDeficienteVisual, " +
                "   perfilUsuario.perfil.perCodigo, " +
                "   per.perDescricao, " +
                "   usuarioCsa.csaCodigo, " +
                "   usuarioCse.cseCodigo, " +
                "   usuarioCor.corCodigo, " +
                "   usuarioOrg.orgCodigo, " +
                "   usuarioSer.serCodigo, " +
                "   usuarioSup.cseCodigo, " +
                "   csa.csaNome, " +
                "   cse.cseNome, " +
                "   cor.corNome, " +
                "   org.orgNome, " +
                "   ser.serNome, " +
                "   sup.cseNome, " +
                "   consignante.cseLicenca, " +
                "   '" + CodedValues.RSA_PUBLIC_KEY_CENTRALIZADOR + "', " +
                "   '" + CodedValues.RSA_MODULUS_CENTRALIZADOR + "', " +
                "   consignante.cseCertificadoCentralizador, " +
                "   case when (coalesce(usuario.usuDataExpSenha, current_date()) <= current_date()) then '1' else '0' end " +
                "FROM Consignante consignante, " +
                "     Usuario usuario " +
                "LEFT JOIN usuario.perfilUsuarioSet perfilUsuario " +
                "LEFT JOIN perfilUsuario.perfil per " +
                "LEFT JOIN usuario.usuarioCsaSet usuarioCsa " +
                "LEFT JOIN usuarioCsa.consignataria csa " +
                "LEFT JOIN usuario.usuarioCseSet usuarioCse " +
                "LEFT JOIN usuarioCse.consignante cse " +
                "LEFT JOIN usuario.usuarioCorSet usuarioCor " +
                "LEFT JOIN usuarioCor.correspondente cor " +
                "LEFT JOIN usuario.usuarioOrgSet usuarioOrg " +
                "LEFT JOIN usuarioOrg.orgao org " +
                "LEFT JOIN usuario.usuarioSerSet usuarioSer " +
                "LEFT JOIN usuarioSer.servidor ser " +
                "LEFT JOIN usuario.usuarioSupSet usuarioSup " +
                "LEFT JOIN usuarioSup.consignante sup " +
                "LEFT JOIN usuarioSer.servidor.registroServidorSet registroServidor " +
                "WHERE " +
                "    (" +
                "        usuarioSer.serCodigo IS NULL " +
                "        OR registroServidor.statusRegistroServidor.srsCodigo NOT IN ('" + TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '") + "') " +
                "    )";

        if (!TextHelper.isNull(usuCodigo)) {
            StringBuilder sqlBuilder = new StringBuilder(corpo);
            sqlBuilder.append(" AND usuario.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo));
            corpo = sqlBuilder.toString();
        }

        if (!TextHelper.isNull(usuLogin)) {
            StringBuilder sqlBuilder = new StringBuilder(corpo);
            sqlBuilder.append(" AND usuario.usuLogin ").append(criaClausulaNomeada("usuLogin", usuLogin));
            corpo = sqlBuilder.toString();
        }

        if (!TextHelper.isNull(usuEmail)) {
            StringBuilder sqlBuilder = new StringBuilder(corpo);
            sqlBuilder.append(" AND usuario.usuEmail ").append(criaClausulaNomeada("usuEmail", usuEmail));
            corpo = sqlBuilder.toString();
        }

        Query<Object[]> query = instanciarQuery(session, corpo);

        if (!TextHelper.isNull(usuCodigo)) {
            defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        }

        if (!TextHelper.isNull(usuLogin)) {
            defineValorClausulaNomeada("usuLogin", usuLogin, query);
        }

        if (!TextHelper.isNull(usuEmail)) {
            defineValorClausulaNomeada("usuEmail", usuEmail, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.USU_NOME,
                Columns.USU_LOGIN,
                Columns.USU_SENHA,
                Columns.USU_SENHA_APP,
                Columns.USU_EMAIL,
                Columns.USU_TEL,
                Columns.USU_STU_CODIGO,
                Columns.USU_CODIGO,
                Columns.USU_DATA_ULT_ACESSO,
                Columns.USU_DATA_EXP_SENHA,
                Columns.USU_IP_ACESSO,
                Columns.USU_DDNS_ACESSO,
                Columns.USU_DATA_CAD,
                Columns.USU_CENTRALIZADOR,
                Columns.USU_EXIGE_CERTIFICADO,
                Columns.USU_CPF,
                Columns.USU_OTP_CODIGO,
                Columns.USU_CHAVE_RECUPERAR_SENHA,
                Columns.USU_CHAVE_VALIDACAO_TOTP,
                Columns.USU_PERMITE_VALIDACAO_TOTP,
                Columns.USU_OPERACOES_VALIDACAO_TOTP,
                Columns.USU_QTD_CONSULTAS_MARGEM,
                Columns.USU_CHAVE_VALIDACAO_EMAIL,
                Columns.USU_DATA_VALIDACAO_EMAIL,
                Columns.USU_DEFICIENTE_VISUAL,
                Columns.USU_AUTENTICA_SSO,
                Columns.SER_DEFICIENTE_VISUAL,
                Columns.UPE_PER_CODIGO,
                Columns.PER_DESCRICAO,
                Columns.UCA_CSA_CODIGO,
                Columns.UCE_CSE_CODIGO,
                Columns.UCO_COR_CODIGO,
                Columns.UOR_ORG_CODIGO,
                Columns.USE_SER_CODIGO,
                Columns.USP_CSE_CODIGO,
                Columns.CSA_NOME,
                Columns.CSE_NOME,
                Columns.COR_NOME,
                Columns.ORG_NOME,
                Columns.SER_NOME,
                "SUP_NOME",
                Columns.CSE_LICENCA,
                Columns.CSE_RSA_PUBLIC_KEY_CENTRALIZADOR,
                Columns.CSE_RSA_MODULUS_CENTRALIZADOR,
                Columns.CSE_CERTIFICADO_CENTRALIZADOR,
                "EXPIROU"
        };
    }
}
