package com.zetra.econsig.persistence.query.usuario;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaUsuarioAuditorEntidadeQuery</p>
 * <p>Description: Lista os usuários auditores por entidade.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaUsuarioAuditorEntidadeQuery extends HQuery {

    public boolean count = false;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String stuCodigo = CodedValues.STU_ATIVO;
        String corpo = "";

        if (count) {
            corpo = "select count(distinct usu.usuCodigo) as total ";
        } else {
            corpo = "select usu.usuLogin," +
            "usu.usuCodigo," +
            "usu.usuNome," +
            "usu.usuEmail," +
            "case " +
            "when usuarioCse.cseCodigo is not null then entCse.cseCodigo " +
            "when usuarioCsa.csaCodigo is not null then entCsa.csaCodigo " +
            "when usuarioCor.corCodigo is not null then entCor.corCodigo " +
            "when usuarioOrg.orgCodigo is not null then entOrg.orgCodigo " +
            "when usuarioSup.cseCodigo is not null then entSup.cseCodigo " +
            "end as CODIGO_ENTIDADE, " +
            "case " +
            "when usuarioCse.cseCodigo is not null then 'CSE' " +
            "when usuarioCsa.csaCodigo is not null then 'CSA' " +
            "when usuarioCor.corCodigo is not null then 'COR' " +
            "when usuarioOrg.orgCodigo is not null then 'ORG' " +
            "when usuarioSup.cseCodigo is not null then 'SUP' " +
            "end as TIPO_ENTIDADE, " +
            "case " +
            "when usuarioCse.cseCodigo is not null then entCse.cseNome " +
            "when usuarioCsa.csaCodigo is not null then entCsa.csaNome " +
            "when usuarioCor.corCodigo is not null then entCor.corNome " +
            "when usuarioOrg.orgCodigo is not null then entOrg.orgNome " +
            "when usuarioSup.cseCodigo is not null then entSup.cseNome " +
            "end as ENTIDADE";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from Usuario usu ");
        corpoBuilder.append(" left outer join usu.usuarioCseSet usuarioCse");
        corpoBuilder.append(" left outer join usuarioCse.consignante entCse");
        corpoBuilder.append(" left outer join usu.usuarioCsaSet usuarioCsa");
        corpoBuilder.append(" left outer join usuarioCsa.consignataria entCsa");
        corpoBuilder.append(" left outer join usu.usuarioCorSet usuarioCor");
        corpoBuilder.append(" left outer join usuarioCor.correspondente entCor");
        corpoBuilder.append(" left outer join usu.usuarioOrgSet usuarioOrg");
        corpoBuilder.append(" left outer join usuarioOrg.orgao entOrg");
        corpoBuilder.append(" left outer join usu.usuarioSupSet usuarioSup");
        corpoBuilder.append(" left outer join usuarioSup.consignante entSup");

        corpoBuilder.append(" where 1 = 1 ");

        // CONSIGNANTE
        corpoBuilder.append(" and (");
        corpoBuilder.append(" (exists (select 1 from FuncaoPerfilCse funPer where usu.usuCodigo = funPer.usuCodigo");
        corpoBuilder.append(" and funPer.funCodigo = '").append(CodedValues.FUN_USUARIO_AUDITOR).append("'");
        corpoBuilder.append(")");
        corpoBuilder.append(" or exists (select 1 from PerfilUsuario upe ");
        corpoBuilder.append(" inner join upe.perfil per ");
        corpoBuilder.append(" inner join per.funcaoSet fun ");
        corpoBuilder.append(" inner join per.perfilCseSet perEntidade");
        corpoBuilder.append(" where upe.usuCodigo = usu.usuCodigo and fun.funCodigo = '").append(CodedValues.FUN_USUARIO_AUDITOR).append("'");
        corpoBuilder.append("))");

        // CONSIGNATARIA
        corpoBuilder.append(" or");
        corpoBuilder.append(" (exists (select 1 from FuncaoPerfilCsa funPer where usu.usuCodigo = funPer.usuCodigo");
        corpoBuilder.append(" and funPer.funCodigo = '").append(CodedValues.FUN_USUARIO_AUDITOR).append("'");
        corpoBuilder.append(")");
        corpoBuilder.append(" or exists (select 1 from PerfilUsuario upe ");
        corpoBuilder.append(" inner join upe.perfil per ");
        corpoBuilder.append(" inner join per.funcaoSet fun ");
        corpoBuilder.append(" inner join per.perfilCsaSet perEntidade");
        corpoBuilder.append(" where upe.usuCodigo = usu.usuCodigo and fun.funCodigo = '").append(CodedValues.FUN_USUARIO_AUDITOR).append("'");
        corpoBuilder.append("))");

        // CORRESPONDENTE
        corpoBuilder.append(" or");
        corpoBuilder.append(" (exists (select 1 from FuncaoPerfilCor funPer where usu.usuCodigo = funPer.usuCodigo");
        corpoBuilder.append(" and funPer.funCodigo = '").append(CodedValues.FUN_USUARIO_AUDITOR).append("'");
        corpoBuilder.append(")");
        corpoBuilder.append(" or exists (select 1 from PerfilUsuario upe ");
        corpoBuilder.append(" inner join upe.perfil per ");
        corpoBuilder.append(" inner join per.funcaoSet fun ");
        corpoBuilder.append(" inner join per.perfilCorSet perEntidade");
        corpoBuilder.append(" where upe.usuCodigo = usu.usuCodigo and fun.funCodigo = '").append(CodedValues.FUN_USUARIO_AUDITOR).append("'");
        corpoBuilder.append("))");

        // ÓRGÃO
        corpoBuilder.append(" or");
        corpoBuilder.append(" (exists (select 1 from FuncaoPerfilOrg funPer where usu.usuCodigo = funPer.usuCodigo");
        corpoBuilder.append(" and funPer.funCodigo = '").append(CodedValues.FUN_USUARIO_AUDITOR).append("'");
        corpoBuilder.append(")");
        corpoBuilder.append(" or exists (select 1 from PerfilUsuario upe ");
        corpoBuilder.append(" inner join upe.perfil per ");
        corpoBuilder.append(" inner join per.funcaoSet fun ");
        corpoBuilder.append(" inner join per.perfilOrgSet perEntidade");
        corpoBuilder.append(" where upe.usuCodigo = usu.usuCodigo and fun.funCodigo = '").append(CodedValues.FUN_USUARIO_AUDITOR).append("'");
        corpoBuilder.append("))");

        // SUPORTE
        corpoBuilder.append(" or");
        corpoBuilder.append(" (exists (select 1 from FuncaoPerfilSup funPer where usu.usuCodigo = funPer.usuCodigo");
        corpoBuilder.append(" and funPer.funCodigo = '").append(CodedValues.FUN_USUARIO_AUDITOR).append("'");
        corpoBuilder.append(")");
        corpoBuilder.append(" or exists (select 1 from PerfilUsuario upe ");
        corpoBuilder.append(" inner join upe.perfil per ");
        corpoBuilder.append(" inner join per.funcaoSet fun ");
        corpoBuilder.append(" inner join per.perfilSupSet perEntidade");
        corpoBuilder.append(" where upe.usuCodigo = usu.usuCodigo and fun.funCodigo = '").append(CodedValues.FUN_USUARIO_AUDITOR).append("'");
        corpoBuilder.append("))");
        corpoBuilder.append(") ");

        corpoBuilder.append(" and usu.statusLogin.stuCodigo ").append(criaClausulaNomeada("stuCodigo", stuCodigo));
        corpoBuilder.append(" and nullif(trim(usu.usuEmail), '') is not null");

        if (count) {
            corpoBuilder.append(" order by case when usuarioCse.cseCodigo is not null then entCse.cse_nome");
            corpoBuilder.append(" when usuarioCsa.csaCodigo is not null then entCsa.csa_nome");
            corpoBuilder.append(" when usuarioCor.corCodigo is not null then entCor.cor_nome");
            corpoBuilder.append(" when usuarioOrg.orgCodigo is not null then entOrg.org_nome");
            corpoBuilder.append(" when usuarioSup.cseCodigo is not null then entSup.cse_nome");
            corpoBuilder.append(" end");
        }

        /*if (usuCodigo != null) {
            corpoBuilder.append(" and usu.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo));
        } */

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("stuCodigo", stuCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.USU_LOGIN,
                Columns.USU_CODIGO,
                Columns.USU_NOME,
                Columns.USU_EMAIL,
                "CODIGO_ENTIDADE",
                "TIPO_ENTIDADE",
                "ENTIDADE"
        };
    }

}
