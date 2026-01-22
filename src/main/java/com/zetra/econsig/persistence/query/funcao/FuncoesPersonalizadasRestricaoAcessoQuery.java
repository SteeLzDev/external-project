package com.zetra.econsig.persistence.query.funcao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;

/**
 * <p>Title: FuncoesPersonalizadasQuery</p>
 * <p>Description: Lista as funções de um usuário com perfil personalizado</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FuncoesPersonalizadasRestricaoAcessoQuery extends HQuery {
    public String usuCodigo = null;
    public String entidade = null;
    public String papel = null;
    public String tipo = null;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String funcaoPerfil = null;
        String campoEntidade = null;
        String corpo = "select funcao.funCodigo, funcao.funDescricao from AcessoRecurso acr";

        if (usuCodigo != null && usuCodigo.length() > 0) {

            if (tipo.equals(AcessoSistema.ENTIDADE_CSE)) { // CONSIGNANTE
                funcaoPerfil = "funcaoPerfilCseSet";
                campoEntidade = "cseCodigo";
                papel = "1";

            } else if (tipo.equals(AcessoSistema.ENTIDADE_CSA)) { // CONSIGNATARIA
                funcaoPerfil = "funcaoPerfilCsaSet";
                campoEntidade = "csaCodigo";
                papel = "2";

            } else if (tipo.equals(AcessoSistema.ENTIDADE_ORG)) { // ORGAO
                funcaoPerfil = "funcaoPerfilOrgSet";
                campoEntidade = "orgCodigo";
                papel = "3";

            } else if (tipo.equals(AcessoSistema.ENTIDADE_COR)) { // CORRESPONDENTE
                funcaoPerfil = "funcaoPerfilCorSet";
                campoEntidade = "corCodigo";
                papel = "4";

            } else if (tipo.equals(AcessoSistema.ENTIDADE_SUP)) { // SUPORTE
                funcaoPerfil = "funcaoPerfilSupSet";
                campoEntidade = "cseCodigo";
                papel = "7";

            } else {
                throw new HQueryException("mensagem.erro.sistema.tipo.entidade.invalido", (AcessoSistema) null);
            }
        }

        corpo += " join acr.funcao funcao ";

        if (funcaoPerfil != null && funcaoPerfil.length() > 0) {
            corpo += " join funcao." + funcaoPerfil + " funcaoPerfil ";
        }

        corpo += " join funcao.papelFuncaoSet papCod " + " where 1=1 ";
        if (campoEntidade != null && campoEntidade.length() > 0) {
            corpo += " and funcaoPerfil." + campoEntidade + " = :campoEntidade";
        }
        if (campoEntidade != null && campoEntidade.length() > 0) {
            corpo += " and funcaoPerfil.usuCodigo = :usuCodigo";
        }

        corpo += " and papCod.papCodigo = :papel";
        corpo += " group by funcao.funCodigo, funcao.funDescricao";

        Query<Object[]> query = instanciarQuery(session, corpo);

        if (usuCodigo != null && usuCodigo.length() > 0) {
            query.setParameter("usuCodigo", usuCodigo);
        }
        if (campoEntidade != null && campoEntidade.length() > 0) {
            query.setParameter("campoEntidade", entidade);
        }

        query.setParameter("papel", papel);

        return query;
    }
}
