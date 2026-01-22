package com.zetra.econsig.persistence.query.usuario;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ObtemUsuarioTipoQuery</p>
 * <p>Description: Retorna os dados do usuário e a qual entidade ele pertence.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemUsuarioTipoQuery extends HQuery {

    public String usuCodigo;
    public String usuLogin;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select ");
        corpoBuilder.append("usuario.usuCodigo, usuario.statusLogin.stuCodigo, usuario.usuDataCad, ");
        corpoBuilder.append("case ").append("when usuario.statusLogin.stuCodigo = '").append(CodedValues.STU_EXCLUIDO);
        corpoBuilder.append("' then coalesce(nullif(concat(usuario.usuTipoBloq, '(*)'), ''), usuario.usuLogin) ");
        corpoBuilder.append("else usuario.usuLogin end AS USU_LOGIN, ");
        corpoBuilder.append("usuario.usuSenha, usuario.usuNome, usuario.usuEmail, usuario.usuTel, usuario.usuDicaSenha, usuario.usuTipoBloq, ");
        corpoBuilder.append("usuario.usuDataExpSenha, usuario.usuQtdConsultasMargem, usuario.usuCpf, ");

        corpoBuilder.append("case ");
        corpoBuilder.append("when (usuarioCse.usuCodigo is not null) then '").append(AcessoSistema.ENTIDADE_CSE).append("' ");
        corpoBuilder.append("when (usuarioCsa.usuCodigo is not null) then '").append(AcessoSistema.ENTIDADE_CSA).append("' ");
        corpoBuilder.append("when (usuarioCor.usuCodigo is not null) then '").append(AcessoSistema.ENTIDADE_COR).append("' ");
        corpoBuilder.append("when (usuarioOrg.usuCodigo is not null) then '").append(AcessoSistema.ENTIDADE_ORG).append("' ");
        corpoBuilder.append("when (usuarioSer.usuCodigo is not null) then '").append(AcessoSistema.ENTIDADE_SER).append("' ");
        corpoBuilder.append("when (usuarioSup.usuCodigo is not null) then '").append(AcessoSistema.ENTIDADE_SUP).append("' ");
        corpoBuilder.append("end AS TIPO, ");

        corpoBuilder.append("case ");
        corpoBuilder.append("when (usuarioCse.usuCodigo is not null) then consignante.cseCodigo ");
        corpoBuilder.append("when (usuarioCsa.usuCodigo is not null) then consignataria.csaCodigo ");
        corpoBuilder.append("when (usuarioCor.usuCodigo is not null) then correspondente.corCodigo ");
        corpoBuilder.append("when (usuarioOrg.usuCodigo is not null) then orgao.orgCodigo ");
        corpoBuilder.append("when (usuarioSer.usuCodigo is not null) then servidor.serCodigo ");
        corpoBuilder.append("when (usuarioSup.usuCodigo is not null) then consignanteSup.cseCodigo ");
        corpoBuilder.append("end AS CODIGO, ");

        corpoBuilder.append("case ");
        corpoBuilder.append("when (usuarioCse.usuCodigo is not null) then consignante.cseIdentificador ");
        corpoBuilder.append("when (usuarioCsa.usuCodigo is not null) then consignataria.csaIdentificador ");
        corpoBuilder.append("when (usuarioCor.usuCodigo is not null) then correspondente.corIdentificador ");
        corpoBuilder.append("when (usuarioOrg.usuCodigo is not null) then orgao.orgIdentificador ");
        corpoBuilder.append("when (usuarioSer.usuCodigo is not null) then null ");
        corpoBuilder.append("when (usuarioSup.usuCodigo is not null) then consignanteSup.cseIdentificador ");
        corpoBuilder.append("end AS IDENTIFICADOR, ");

        corpoBuilder.append("case ");
        corpoBuilder.append("when (usuarioCse.usuCodigo is not null) then consignante.cseNome ");
        corpoBuilder.append("when (usuarioCsa.usuCodigo is not null) then consignataria.csaNome ");
        corpoBuilder.append("when (usuarioCor.usuCodigo is not null) then concat(concat(consignatariaCor.csaNome,' - '), correspondente.corNome) ");
        corpoBuilder.append("when (usuarioOrg.usuCodigo is not null) then orgao.orgNome ");
        corpoBuilder.append("when (usuarioSer.usuCodigo is not null) then servidor.serNome ");
        corpoBuilder.append("when (usuarioSup.usuCodigo is not null) then '"+ ApplicationResourcesHelper.getMessage("rotulo.suporte.singular", (AcessoSistema) null).toUpperCase() +"' ");
        corpoBuilder.append("end AS ENTIDADE ");

        corpoBuilder.append("from Usuario usuario ");
        corpoBuilder.append("left outer join usuario.usuarioCseSet usuarioCse ");
        corpoBuilder.append("left outer join usuarioCse.consignante consignante ");
        corpoBuilder.append("left outer join usuario.usuarioCsaSet usuarioCsa ");
        corpoBuilder.append("left outer join usuarioCsa.consignataria consignataria ");
        corpoBuilder.append("left outer join usuario.usuarioCorSet usuarioCor ");
        corpoBuilder.append("left outer join usuarioCor.correspondente correspondente ");
        corpoBuilder.append("left outer join correspondente.consignataria consignatariaCor ");
        corpoBuilder.append("left outer join usuario.usuarioOrgSet usuarioOrg ");
        corpoBuilder.append("left outer join usuarioOrg.orgao orgao ");
        corpoBuilder.append("left outer join usuario.usuarioSerSet usuarioSer ");
        corpoBuilder.append("left outer join usuarioSer.servidor servidor ");
        corpoBuilder.append("left outer join usuario.usuarioSupSet usuarioSup ");
        corpoBuilder.append("left outer join usuarioSup.consignante consignanteSup ");

        corpoBuilder.append(" where 1 = 1 ");

        if (!TextHelper.isNull(usuCodigo)) {
            corpoBuilder.append(" and usuario.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo));
        }

        if (!TextHelper.isNull(usuLogin)) {
            corpoBuilder.append(" and usuario.usuLogin ").append(criaClausulaNomeada("usuLogin", usuLogin));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(usuCodigo)) {
            defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        }

        if (!TextHelper.isNull(usuLogin)) {
            defineValorClausulaNomeada("usuLogin", usuLogin, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.USU_CODIGO,
                Columns.USU_STU_CODIGO,
                Columns.USU_DATA_CAD,
                Columns.USU_LOGIN,
                Columns.USU_SENHA,
                Columns.USU_NOME,
                Columns.USU_EMAIL,
                Columns.USU_TEL,
                Columns.USU_DICA_SENHA,
                Columns.USU_TIPO_BLOQ,
                Columns.USU_DATA_EXP_SENHA,
                Columns.USU_QTD_CONSULTAS_MARGEM,
                Columns.USU_CPF,
                "TIPO",
                "CODIGO",
                "IDENTIFICADOR",
                "ENTIDADE"
        };
    }
}
