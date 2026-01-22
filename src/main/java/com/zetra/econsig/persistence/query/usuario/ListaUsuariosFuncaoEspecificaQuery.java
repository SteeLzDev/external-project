package com.zetra.econsig.persistence.query.usuario;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaUsuariosFuncaoEspecificaQuery</p>
 * <p>Description: Lista os usuários com função específica</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaUsuariosFuncaoEspecificaQuery extends HQuery {

    public String tipo;
    public String codigoEntidade;
    public Object perCodigo;
    public Object stuCodigo;
    public List<String> usuCodigo;
    public String funCodigo;

    public boolean count = false;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (TextHelper.isNull(tipo)) {
            throw new HQueryException("mensagem.informe.tipo.entidade.consulta.usuarios.auditores", (AcessoSistema) null);
        }

        if (count) {
            corpo = "select count(distinct usu.usuCodigo) as total ";
        } else {
            corpo = "select usu.usuLogin," +
            "usu.usuCodigo," +
            "usu.usuNome," +
            "usu.usuEmail";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from Usuario usu ");
        corpoBuilder.append(" where ");

        if (tipo.equals(AcessoSistema.ENTIDADE_CSE)) {
            corpoBuilder.append(" exists (select 1 from FuncaoPerfilCse funPer where usu.usuCodigo = funPer.usuCodigo");
            corpoBuilder.append(" and funPer.cseCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            corpoBuilder.append(" and funPer.funCodigo = :funCodigo");
            if (usuCodigo != null) {
                corpoBuilder.append(" and usu.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo)).append(")");
            } else {
                corpoBuilder.append(")");
            }
            corpoBuilder.append(" or exists (select 1 from PerfilUsuario upe ");
            corpoBuilder.append(" inner join upe.perfil per ");
            corpoBuilder.append(" inner join per.funcaoSet fun ");
            corpoBuilder.append(" inner join per.perfilCseSet perEntidade");
            corpoBuilder.append(" where upe.usuCodigo = usu.usuCodigo and fun.funCodigo = :funCodigo");
            corpoBuilder.append(" and perEntidade.cseCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            if (!TextHelper.isNull(perCodigo)) {
                corpoBuilder.append(" and per.perCodigo ").append(criaClausulaNomeada("perCodigo", perCodigo));
            }
            if (usuCodigo != null) {
                usuCodigo.add(0, CodedValues.NOT_EQUAL_KEY);
                corpoBuilder.append(" and usu.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo));
            }
            corpoBuilder.append(")");

        } else if (tipo.equals(AcessoSistema.ENTIDADE_CSA)) {
            corpoBuilder.append(" exists (select 1 from FuncaoPerfilCsa funPer where usu.usuCodigo = funPer.usuCodigo");
            corpoBuilder.append(" and funPer.csaCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            corpoBuilder.append(" and funPer.funCodigo = :funCodigo");
            if (usuCodigo != null) {
                corpoBuilder.append(" and usu.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo)).append(")");
            } else {
                corpoBuilder.append(")");
            }
            corpoBuilder.append(" or exists (select 1 from PerfilUsuario upe ");
            corpoBuilder.append(" inner join upe.perfil per ");
            corpoBuilder.append(" inner join per.funcaoSet fun ");
            corpoBuilder.append(" inner join per.perfilCsaSet perEntidade");
            corpoBuilder.append(" where upe.usuCodigo = usu.usuCodigo and fun.funCodigo = :funCodigo");
            corpoBuilder.append(" and perEntidade.csaCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            if (!TextHelper.isNull(perCodigo)) {
                corpoBuilder.append(" and per.perCodigo ").append(criaClausulaNomeada("perCodigo", perCodigo));
            }
            if (usuCodigo != null) {
                usuCodigo.add(0, CodedValues.NOT_EQUAL_KEY);
                corpoBuilder.append(" and usu.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo));
            }
            corpoBuilder.append(")");
        } else if (tipo.equals(AcessoSistema.ENTIDADE_COR)) {
            corpoBuilder.append(" exists (select 1 from FuncaoPerfilCor funPer where usu.usuCodigo = funPer.usuCodigo");
            corpoBuilder.append(" and funPer.corCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            corpoBuilder.append(" and funPer.funCodigo = :funCodigo");
            if (usuCodigo != null) {
                corpoBuilder.append(" and usu.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo)).append(")");
            } else {
                corpoBuilder.append(")");
            }
            corpoBuilder.append(" or exists (select 1 from PerfilUsuario upe ");
            corpoBuilder.append(" inner join upe.perfil per ");
            corpoBuilder.append(" inner join per.funcaoSet fun ");
            corpoBuilder.append(" inner join per.perfilCorSet perEntidade");
            corpoBuilder.append(" where upe.usuCodigo = usu.usuCodigo and fun.funCodigo = :funCodigo");
            corpoBuilder.append(" and perEntidade.corCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            if (!TextHelper.isNull(perCodigo)) {
                corpoBuilder.append(" and per.perCodigo ").append(criaClausulaNomeada("perCodigo", perCodigo));
            }
            if (usuCodigo != null) {
                usuCodigo.add(0, CodedValues.NOT_EQUAL_KEY);
                corpoBuilder.append(" and usu.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo));
            }
            corpoBuilder.append(")");
        } else if (tipo.equals(AcessoSistema.ENTIDADE_ORG)) {
            corpoBuilder.append(" exists (select 1 from FuncaoPerfilOrg funPer where usu.usuCodigo = funPer.usuCodigo");
            corpoBuilder.append(" and funPer.orgCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            corpoBuilder.append(" and funPer.funCodigo = :funCodigo");
            if (usuCodigo != null) {
                corpoBuilder.append(" and usu.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo)).append(")");
            } else {
                corpoBuilder.append(")");
            }
            corpoBuilder.append(" or exists (select 1 from PerfilUsuario upe ");
            corpoBuilder.append(" inner join upe.perfil per ");
            corpoBuilder.append(" inner join per.funcaoSet fun ");
            corpoBuilder.append(" inner join per.perfilOrgSet perEntidade");
            corpoBuilder.append(" where upe.usuCodigo = usu.usuCodigo and fun.funCodigo = :funCodigo");
            corpoBuilder.append(" and perEntidade.orgCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            if (!TextHelper.isNull(perCodigo)) {
                corpoBuilder.append(" and per.perCodigo ").append(criaClausulaNomeada("perCodigo", perCodigo));
            }
            if (usuCodigo != null) {
                usuCodigo.add(0, CodedValues.NOT_EQUAL_KEY);
                corpoBuilder.append(" and usu.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo));
            }
            corpoBuilder.append(")");
        }else if (tipo.equals(AcessoSistema.ENTIDADE_SUP)) {
            corpoBuilder.append(" exists (select 1 from FuncaoPerfilSup funPer where usu.usuCodigo = funPer.usuCodigo");
            corpoBuilder.append(" and funPer.cseCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            corpoBuilder.append(" and funPer.funCodigo = :funCodigo");
            if (usuCodigo != null) {
                corpoBuilder.append(" and usu.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo)).append(")");
            } else {
                corpoBuilder.append(")");
            }
            corpoBuilder.append(" or exists (select 1 from PerfilUsuario upe ");
            corpoBuilder.append(" inner join upe.perfil per ");
            corpoBuilder.append(" inner join per.funcaoSet fun ");
            corpoBuilder.append(" inner join per.perfilSupSet perEntidade");
            corpoBuilder.append(" where upe.usuCodigo = usu.usuCodigo and fun.funCodigo = :funCodigo");
            corpoBuilder.append(" and perEntidade.cseCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            if (!TextHelper.isNull(perCodigo)) {
                corpoBuilder.append(" and per.perCodigo ").append(criaClausulaNomeada("perCodigo", perCodigo));
            }
            if (usuCodigo != null) {
                usuCodigo.add(0, CodedValues.NOT_EQUAL_KEY);
                corpoBuilder.append(" and usu.usuCodigo ").append(criaClausulaNomeada("usuCodigo", usuCodigo));
            }
            corpoBuilder.append(")");
        }

        if (stuCodigo != null) {
            corpoBuilder.append(" and usu.statusLogin.stuCodigo ").append(criaClausulaNomeada("stuCodigo", stuCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(perCodigo)) {
            defineValorClausulaNomeada("perCodigo", perCodigo, query);
        }

        if (!TextHelper.isNull(codigoEntidade)) {
            defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);
        }

        if (stuCodigo != null) {
            defineValorClausulaNomeada("stuCodigo", stuCodigo, query);
        }

        if (usuCodigo != null) {
            defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        }

        if (!TextHelper.isNull(codigoEntidade)) {
            defineValorClausulaNomeada("funCodigo", funCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.USU_LOGIN,
                Columns.USU_CODIGO,
                Columns.USU_NOME,
                Columns.USU_EMAIL
        };
    }
}
