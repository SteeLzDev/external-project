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
 * <p>Title: ObtemPapelUsuarioQuery</p>
 * <p>Description: Busca o papel do usuário</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemPapelUsuarioQuery extends HQuery {
    public String usuCodigo = null;
    public String usuLogin = null;
    public String usuChaveRecuperarSenha = null;
    public String usuChaveValidacaoEmail = null;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        if (TextHelper.isNull(usuCodigo) && TextHelper.isNull(usuLogin) && TextHelper.isNull(usuChaveRecuperarSenha) && TextHelper.isNull(usuChaveValidacaoEmail)) {
            throw new HQueryException("mensagem.erro.parametros.ausentes", (AcessoSistema) null);
        }

        final StringBuilder sql = new StringBuilder();

        sql.append("SELECT ");
        sql.append("   usuario.usuCodigo, ");
        sql.append("   usuario.usuLogin, ");
        sql.append("   usuario.usuNome, ");
        sql.append("   usuarioCse.cseCodigo, ");
        sql.append("   usuarioOrg.orgCodigo, ");
        sql.append("   usuarioCsa.csaCodigo, ");
        sql.append("   usuarioCor.corCodigo, ");
        sql.append("   usuarioSer.serCodigo, ");
        sql.append("   usuarioSup.cseCodigo  ");
        sql.append("FROM Usuario usuario ");
        sql.append("LEFT JOIN usuario.usuarioCseSet usuarioCse ");
        sql.append("LEFT JOIN usuario.usuarioOrgSet usuarioOrg ");
        sql.append("LEFT JOIN usuario.usuarioCsaSet usuarioCsa ");
        sql.append("LEFT JOIN usuario.usuarioCorSet usuarioCor ");
        sql.append("LEFT JOIN usuario.usuarioSerSet usuarioSer ");
        sql.append("LEFT JOIN usuario.usuarioSupSet usuarioSup ");
        if (!TextHelper.isNull(usuChaveValidacaoEmail)) {
            sql.append("INNER JOIN usuario.ocorrenciaUsuarioByOusUsuCodigoSet ocaUsuario with ocaUsuario.tipoOcorrencia.tocCodigo = '" + CodedValues.TOC_VALIDACAO_EMAIL_USUARIO + "' ");
        }
        sql.append(" WHERE 1=1 ");

        if (!TextHelper.isNull(usuCodigo)) {
            sql.append("AND usuario.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo));
        }
        if (!TextHelper.isNull(usuLogin)) {
            sql.append("AND usuario.usuLogin ").append(criaClausulaNomeada("usuLogin", usuLogin));
        }
        if (!TextHelper.isNull(usuChaveRecuperarSenha)) {
            sql.append("AND usuario.usuChaveRecuperarSenha ").append(criaClausulaNomeada("usuChaveRecuperarSenha", usuChaveRecuperarSenha));
        }

        if (!TextHelper.isNull(usuChaveValidacaoEmail)) {
            sql.append("AND usuario.usuChaveValidacaoEmail ").append(criaClausulaNomeada("usuChaveValidacaoEmail", usuChaveValidacaoEmail));
            sql.append("AND ocaUsuario.ousData >= add_day(data_corrente(), -1) ");
            sql.append("ORDER BY ocaUsuario.ousData DESC ");
        }

        final Query<Object[]> query = instanciarQuery(session, sql.toString());

        if (!TextHelper.isNull(usuCodigo)) {
            defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        }
        if (!TextHelper.isNull(usuLogin)) {
            defineValorClausulaNomeada("usuLogin", usuLogin, query);
        }
        if (!TextHelper.isNull(usuChaveRecuperarSenha)) {
            defineValorClausulaNomeada("usuChaveRecuperarSenha", usuChaveRecuperarSenha, query);
        }
        if (!TextHelper.isNull(usuChaveValidacaoEmail)) {
            defineValorClausulaNomeada("usuChaveValidacaoEmail", usuChaveValidacaoEmail, query);
        }

        if (!TextHelper.isNull(usuChaveValidacaoEmail)) {
        query.setMaxResults(1);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.USU_CODIGO,
                Columns.USU_LOGIN,
                Columns.USU_NOME,
                Columns.UCE_CSE_CODIGO,
                Columns.UOR_ORG_CODIGO,
                Columns.UCA_CSA_CODIGO,
                Columns.UCO_COR_CODIGO,
                Columns.USE_SER_CODIGO,
                Columns.USP_CSE_CODIGO
        };
    }
}
