package com.zetra.econsig.persistence.query.usuario;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: ListaFuncoesUsuarioQuery</p>
 * <p>Description: Lista funções de um usuário</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaFuncoesUsuarioQuery extends HQuery {
    public String tipo;
    public String usuCodigo;
    public String papCodigoDestino;
    public String papCodigoOrigem;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        if (tipo.equals(AcessoSistema.ENTIDADE_CSE)) {
            corpoBuilder.append("select funcao.funCodigo");
            corpoBuilder.append(" from Funcao funcao ");
            corpoBuilder.append(" inner join funcao.funcaoPerfilCseSet funcaoPerfilCse ");
            corpoBuilder.append(" where funcaoPerfilCse.usuCodigo = :usuCodigo");
            corpoBuilder.append(" and not exists (select 1 from BloqueioRepasseFuncao bloqRepasseFun");
            corpoBuilder.append(" where bloqRepasseFun.funcao.funCodigo = funcaoPerfilCse.usuCodigo");
            corpoBuilder.append(" and bloqRepasseFun.papelDestino.papCodigo = :papCodigoDestino");
            corpoBuilder.append(" and bloqRepasseFun.papelOrigem.papCodigo = :papCodigoOrigem)");

        } else if (tipo.equals(AcessoSistema.ENTIDADE_CSA)) {
            corpoBuilder.append("select funcao.funCodigo");
            corpoBuilder.append(" from Funcao funcao ");
            corpoBuilder.append(" inner join funcao.funcaoPerfilCsaSet funcaoPerfilCsa ");
            corpoBuilder.append(" where funcaoPerfilCsa.usuCodigo = :usuCodigo");
            corpoBuilder.append(" and not exists (select 1 from BloqueioRepasseFuncao bloqRepasseFun");
            corpoBuilder.append(" where bloqRepasseFun.funcao.funCodigo = funcaoPerfilCsa.usuCodigo");
            corpoBuilder.append(" and bloqRepasseFun.papelDestino.papCodigo = :papCodigoDestino");
            corpoBuilder.append(" and bloqRepasseFun.papelOrigem.papCodigo = :papCodigoOrigem)");

        } else if (tipo.equals(AcessoSistema.ENTIDADE_ORG)) {
            corpoBuilder.append("select funcao.funCodigo");
            corpoBuilder.append(" from Funcao funcao ");
            corpoBuilder.append(" inner join funcao.funcaoPerfilOrgSet funcaoPerfilOrg ");
            corpoBuilder.append(" where funcaoPerfilOrg.usuCodigo = :usuCodigo");
            corpoBuilder.append(" and not exists (select 1 from BloqueioRepasseFuncao bloqRepasseFun");
            corpoBuilder.append(" where bloqRepasseFun.funcao.funCodigo = funcaoPerfilOrg.usuCodigo");
            corpoBuilder.append(" and bloqRepasseFun.papelDestino.papCodigo = :papCodigoDestino");
            corpoBuilder.append(" and bloqRepasseFun.papelOrigem.papCodigo = :papCodigoOrigem)");

        } else if (tipo.equals(AcessoSistema.ENTIDADE_COR)) {
            corpoBuilder.append("select funcao.funCodigo");
            corpoBuilder.append(" from Funcao funcao ");
            corpoBuilder.append(" inner join funcao.funcaoPerfilCorSet funcaoPerfilCor ");
            corpoBuilder.append(" where funcaoPerfilCor.usuCodigo = :usuCodigo");
            corpoBuilder.append(" and not exists (select 1 from BloqueioRepasseFuncao bloqRepasseFun");
            corpoBuilder.append(" where bloqRepasseFun.funcao.funCodigo = funcaoPerfilCor.usuCodigo");
            corpoBuilder.append(" and bloqRepasseFun.papelDestino.papCodigo = :papCodigoDestino");
            corpoBuilder.append(" and bloqRepasseFun.papelOrigem.papCodigo = :papCodigoOrigem)");

        } else if (tipo.equals(AcessoSistema.ENTIDADE_SUP)) {
            corpoBuilder.append("select funcao.funCodigo");
            corpoBuilder.append(" from Funcao funcao ");
            corpoBuilder.append(" inner join funcao.funcaoPerfilSupSet funcaoPerfilSup ");
            corpoBuilder.append(" where funcaoPerfilSup.usuCodigo = :usuCodigo");
            corpoBuilder.append(" and not exists (select 1 from BloqueioRepasseFuncao bloqRepasseFun");
            corpoBuilder.append(" where bloqRepasseFun.funcao.funCodigo = funcaoPerfilSup.usuCodigo");
            corpoBuilder.append(" and bloqRepasseFun.papelDestino.papCodigo = :papCodigoDestino");
            corpoBuilder.append(" and bloqRepasseFun.papelOrigem.papCodigo = :papCodigoOrigem)");

        } else {
            throw new HQueryException("mensagem.erro.sistema.tipo.entidade.invalido", (AcessoSistema) null);
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(usuCodigo)) {
            defineValorClausulaNomeada("usuCodigo", usuCodigo, query);
        }

        if (!TextHelper.isNull(papCodigoDestino)) {
            defineValorClausulaNomeada("papCodigoDestino", papCodigoDestino, query);
        }

        if (!TextHelper.isNull(papCodigoOrigem)) {
            defineValorClausulaNomeada("papCodigoOrigem", papCodigoOrigem, query);
        }

        return query;
    }

}
