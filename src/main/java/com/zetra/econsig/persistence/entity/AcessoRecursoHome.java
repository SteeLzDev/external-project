package com.zetra.econsig.persistence.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: AcessoRecursoHome</p>
 * <p>Description: Classe Home para a entidade AcessoRecurso</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AcessoRecursoHome extends AbstractEntityHome {

    public static AcessoRecurso findByPrimaryKey(String acrCodigo) throws FindException {
        String query = "FROM AcessoRecurso acr WHERE acr.acrCodigo = :acrCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("acrCodigo", acrCodigo);

        List<AcessoRecurso> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static Collection<AcessoRecurso> findByFunCodigo(String funCodigo) throws FindException {
        String query = "FROM AcessoRecurso acr WHERE acr.funcao.funCodigo = :funCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("funCodigo", funCodigo);

        return findByQuery(query, parameters);
    }

    public static AcessoRecurso findItmCodigoByFunCodigo(String funCodigo) throws FindException {
        String query = "FROM AcessoRecurso acr WHERE acr.funcao.funCodigo = :funCodigo and acr.itemMenu.itmCodigo IS NOT NULL";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("funCodigo", funCodigo);

        List<AcessoRecurso> acessos = findByQuery(query, parameters);
        if (acessos != null && acessos.size() > 0) {
            return acessos.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static AcessoRecurso findByAcrRecurso(String acrRecurso, String papCodigo) throws FindException {
        String query = "FROM AcessoRecurso acr WHERE acr.acrRecurso = :acrRecurso and (acr.papel.papCodigo = :papCodigo OR acr.papel.papCodigo IS NULL)";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("acrRecurso", acrRecurso);
        parameters.put("papCodigo", papCodigo);

        List<AcessoRecurso> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static AcessoRecurso create(String funCodigo, String papCodigo, String acrRecurso, String acrParametro, String acrOperacao,
                                       String acrSessao, String acrBloqueio, Short acrAtivo, String acrFimFluxo, String itmCodigo) throws CreateException {

        Session session = SessionUtil.getSession();
        AcessoRecurso bean = new AcessoRecurso();

        try {
            bean.setAcrCodigo(DBHelper.getNextId());
            bean.setFuncao(session.getReference(Funcao.class, funCodigo));
            bean.setPapel(session.getReference(Papel.class, papCodigo));
            bean.setAcrRecurso(acrRecurso);
            bean.setAcrParametro(acrParametro);
            bean.setAcrOperacao(acrOperacao);
            bean.setAcrSessao(acrSessao);
            bean.setAcrBloqueio(acrBloqueio);
            bean.setAcrAtivo(acrAtivo);
            bean.setAcrFimFluxo(acrFimFluxo);
            bean.setAcrMetodoHttp(CodedValues.METODO_POST);
            if (!TextHelper.isNull(itmCodigo)) {
                bean.setItemMenu(session.getReference(ItemMenu.class, itmCodigo));
            }
            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
