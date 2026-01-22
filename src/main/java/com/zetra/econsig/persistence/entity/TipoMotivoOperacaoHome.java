package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: TipoMotivoOperacaoHome</p>
 * <p>Description: Classe Home para a entidade TipoMotivoOperacao</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoMotivoOperacaoHome extends AbstractEntityHome {

    public static TipoMotivoOperacao findByPrimaryKey(String tmoCodigo) throws FindException {
        TipoMotivoOperacao tipoMotivoOperacao = new TipoMotivoOperacao();
        tipoMotivoOperacao.setTmoCodigo(tmoCodigo);
        return find(tipoMotivoOperacao, tmoCodigo);
    }

    public static TipoMotivoOperacao findByIdn(String tmoIdentificador) throws FindException {
        String query = "FROM TipoMotivoOperacao tmo WHERE tmo.tmoIdentificador = :tmoIdentificador";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("tmoIdentificador", tmoIdentificador);

        List<TipoMotivoOperacao> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static List<TipoMotivoOperacao> findByTmoExigeObsObrigatorio() throws FindException {
        String query = "FROM TipoMotivoOperacao tmo WHERE tmo.tmoExigeObs = 'S'";
        List<TipoMotivoOperacao> lista = findByQuery(query, null);

        return lista;
    }

    public static TipoMotivoOperacao create(String tmoDescricao, String tmoIdentificador, String tenCodigo, Short tmoAtivo, String tmoExigeObs, String tmoDecisaoJudicial) throws CreateException {

        Session session = SessionUtil.getSession();
        TipoMotivoOperacao bean = new TipoMotivoOperacao();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setTmoCodigo(objectId);
            bean.setTmoDescricao(tmoDescricao);
            bean.setTmoIdentificador(tmoIdentificador);
            bean.setTipoEntidade(session.getReference(TipoEntidade.class, tenCodigo));
            bean.setTmoAtivo(tmoAtivo);
            bean.setTmoExigeObs(tmoExigeObs);
            bean.setTmoDecisaoJudicial(tmoDecisaoJudicial);
            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static TipoMotivoOperacao findByAcao(String acaCodigo, AcessoSistema responsavel) throws FindException {
        String query = "SELECT DISTINCT tmo FROM TipoMotivoOperacao tmo " + " INNER JOIN tmo.acao aca " + " WHERE aca.acaCodigo = :acaCodigo ";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("acaCodigo", acaCodigo);

        List<TipoMotivoOperacao> tipoMotivoOperacao = findByQuery(query, parameters);
        if (tipoMotivoOperacao == null || tipoMotivoOperacao.size() == 0) {
            return null;
        } else if (tipoMotivoOperacao.size() == 1) {
            return tipoMotivoOperacao.get(0);
        } else {
            throw new FindException("mensagem.erro.mais.de.um.resultado.encontrado", responsavel);
        }
    }
}
