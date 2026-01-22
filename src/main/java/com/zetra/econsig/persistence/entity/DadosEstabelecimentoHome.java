package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: DadosEstabelecimentoHome</p>
 * <p>Description: Classe Home para a entidade DadosEstabelecimento</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DadosEstabelecimentoHome extends AbstractEntityHome {

    public static DadosEstabelecimento findByPrimaryKey(DadosEstabelecimentoId pk) throws FindException {
        DadosEstabelecimento dadosAutDesconto = new DadosEstabelecimento();
        dadosAutDesconto.setId(pk);
        return find(dadosAutDesconto, pk);
    }

    public static List<DadosEstabelecimento> findByEstCodigo(String estCodigo) throws FindException {
        String query = "FROM DadosEstabelecimento dae WHERE dae.estabelecimento.estCodigo = :estCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("estCodigo", estCodigo);

        return findByQuery(query, parameters);
    }

    public static DadosEstabelecimento create(String estCodigo, String tdaCodigo, String daeValor) throws CreateException {
        DadosEstabelecimento bean = new DadosEstabelecimento();

        DadosEstabelecimentoId id = new DadosEstabelecimentoId();
        id.setEstCodigo(estCodigo);
        id.setTdaCodigo(tdaCodigo);
        bean.setId(id);
        bean.setDaeValor(daeValor);

        create(bean);
        return bean;
    }

    public static void removeByEst(String estCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM DadosEstabelecimento dae WHERE dae.estabelecimento.estCodigo = :estCodigo ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            queryUpdate.setParameter("estCodigo", estCodigo);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new RemoveException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }
}
