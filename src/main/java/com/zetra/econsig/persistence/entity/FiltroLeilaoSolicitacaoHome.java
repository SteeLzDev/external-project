package com.zetra.econsig.persistence.entity;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: FiltroLeilaoSolicitacaoHome</p>
 * <p>Description: Classe Home para a entidade FiltroLeilaoSolicitacao</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FiltroLeilaoSolicitacaoHome extends AbstractEntityHome {

    public static FiltroLeilaoSolicitacao findByPrimaryKey(String flsCodigo) throws FindException {
        FiltroLeilaoSolicitacao filtro = new FiltroLeilaoSolicitacao();
        filtro.setFlsCodigo(flsCodigo);
        return find(filtro, flsCodigo);
    }

    public static FiltroLeilaoSolicitacao findByPrimaryKeyAndUsuCodigo(String flsCodigo, String usuCodigo) throws FindException {
        String query = "FROM FiltroLeilaoSolicitacao fls WHERE fls.flsCodigo = :flsCodigo and fls.usuario.usuCodigo = :usuCodigo";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("flsCodigo", flsCodigo);
        parameters.put("usuCodigo", usuCodigo);
        Collection<FiltroLeilaoSolicitacao> res = findByQuery(query, parameters);
        if (res != null && res.size() == 1) {
            return res.iterator().next();
        }
        return null;
    }

    public static List<FiltroLeilaoSolicitacao> findByUsuCodigo(String usuCodigo) throws FindException {
        String query = "FROM FiltroLeilaoSolicitacao fls WHERE fls.usuario.usuCodigo = :usuCodigo";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("usuCodigo", usuCodigo);
        return findByQuery(query, parameters);
    }

    public static FiltroLeilaoSolicitacao create(String cidCodigo, String usuCodigo, String posCodigo, String flsDescricao, Date flsData, String flsEmailNotificacao, Date flsDataAberturaInicial, Date flsDataAberturaFinal, Short flsHorasEncerramento, Integer flsPontuacaoMinima, String flsAnaliseRisco, Integer flsMargemLivreMax, String flsTipoPesquisa, String flsMatricula, String flsCpf) throws CreateException {

        Session session = SessionUtil.getSession();

        FiltroLeilaoSolicitacao bean = new FiltroLeilaoSolicitacao();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setFlsCodigo(objectId);
            if (!TextHelper.isNull(cidCodigo)) {
                bean.setCidade(session.getReference(Cidade.class, cidCodigo));
            }
            if (!TextHelper.isNull(usuCodigo)) {
                bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            }
            if (!TextHelper.isNull(posCodigo)) {
                bean.setPostoRegistroServidor(session.getReference(PostoRegistroServidor.class, posCodigo));
            }
            bean.setFlsDescricao(flsDescricao);
            bean.setFlsData(new Date());
            bean.setFlsEmailNotificacao(flsEmailNotificacao);
            bean.setFlsDataAberturaIni(flsDataAberturaInicial);
            bean.setFlsDataAberturaFim(flsDataAberturaFinal);
            bean.setFlsHorasEncerramento(flsHorasEncerramento);
            bean.setFlsPontuacaoMin(flsPontuacaoMinima);
            bean.setFlsAnaliseRisco(flsAnaliseRisco);
            bean.setFlsMargemLivreMax(flsMargemLivreMax);
            bean.setFlsTipoPesquisa(flsTipoPesquisa);
            bean.setFlsMatricula(flsMatricula);
            bean.setFlsCpf(flsCpf);

            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;

    }

}
