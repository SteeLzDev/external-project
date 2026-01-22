package com.zetra.econsig.persistence.query.funcao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: FuncoesPersonalizadasQuery</p>
 * <p>Description: Lista as funções de um usuário com perfil personalizado</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FuncoesPersonalizadasQuery extends HQuery {
    public String usuCodigo = null;
    public String entidade = null;
    public String tipo = null;
    public String funCodigo = null;
    public String funDescricao = null;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select funcao.funCodigo, funcao.funDescricao, '', '' from Funcao funcao";

        if (!TextHelper.isNull(usuCodigo)) {
            String funcaoPerfilSet, campoEntidade;
            corpo = "select funcao.funCodigo, funcao.funDescricao, eaf.eafIpAcesso, eaf.eafDdnsAcesso from Funcao funcao";

            if (tipo.equals(AcessoSistema.ENTIDADE_CSE)) { // CONSIGNANTE
                funcaoPerfilSet = "funcaoPerfilCseSet";
                campoEntidade = "cseCodigo";

            } else if (tipo.equals(AcessoSistema.ENTIDADE_CSA)) { // CONSIGNATARIA
                funcaoPerfilSet = "funcaoPerfilCsaSet";
                campoEntidade = "csaCodigo";

            } else if (tipo.equals(AcessoSistema.ENTIDADE_ORG)) { // ORGAO
                funcaoPerfilSet = "funcaoPerfilOrgSet";
                campoEntidade = "orgCodigo";

            } else if (tipo.equals(AcessoSistema.ENTIDADE_COR)) { // CORRESPONDENTE
                funcaoPerfilSet = "funcaoPerfilCorSet";
                campoEntidade = "corCodigo";

            } else if (tipo.equals(AcessoSistema.ENTIDADE_SUP)) { // SUPORTE
                funcaoPerfilSet = "funcaoPerfilSupSet";
                campoEntidade = "cseCodigo";

            } else {
                throw new HQueryException("mensagem.usoIncorretoSistema", (AcessoSistema) null);
            }

            corpo += " join funcao." + funcaoPerfilSet + " funcaoPerfil "
                    + " left join funcao.enderecoAcessoFuncaoSet eaf with (eaf.usuCodigo = :usuCodigo) "
                    + " where funcaoPerfil." + campoEntidade + " = :campoEntidade"
                    + " and funcaoPerfil.usuCodigo = :usuCodigo"
                    + (TextHelper.isNull(funCodigo) ? "" : " and funcao.funCodigo " + criaClausulaNomeada("funCodigo", funCodigo))
                    + (TextHelper.isNull(funDescricao) ? "" : " and funcao.funDescricao " + criaClausulaNomeada("funDescricao", funDescricao))
                    + " order by funcao.funDescricao ";
        }

        Query<Object[]> query = instanciarQuery(session, corpo);
        if (usuCodigo != null && usuCodigo.length() > 0) {
            query.setParameter("usuCodigo", usuCodigo);
            query.setParameter("campoEntidade", entidade);
            if (!TextHelper.isNull(funCodigo)) {
                defineValorClausulaNomeada("funCodigo", funCodigo, query);
            }
            if (!TextHelper.isNull(funDescricao)) {
                defineValorClausulaNomeada("funDescricao", funDescricao, query);
            }
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.FUN_CODIGO,
                Columns.FUN_DESCRICAO,
                Columns.EAF_IP_ACESSO,
                Columns.EAF_DDNS_ACESSO};
    }
}
