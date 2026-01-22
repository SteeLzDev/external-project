package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: CompMargemHome</p>
 * <p>Description: Classe Home para a entidade CompMargem</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CompMargemHome extends AbstractEntityHome {

    public static CompMargem findByPrimaryKey(String cmaCodigo) throws FindException {
        CompMargem bean = new CompMargem();
        bean.setCmaCodigo(cmaCodigo);
        return find(bean, cmaCodigo);
    }

    public static List<CompMargem> findByRseCodigoAndVctCodigo(String rseCodigo, String vctCodigo) throws FindException {
        String query = "FROM CompMargem cma WHERE cma.registroServidor.rseCodigo = :rseCodigo and cma.vencimento.vctCodigo = :vctCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseCodigo", rseCodigo);
        parameters.put("vctCodigo", vctCodigo);

        List<CompMargem> composicoes = findByQuery(query, parameters);

        if (composicoes == null || composicoes.isEmpty()) {
            throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
        }

        return composicoes;
    }

    public static CompMargem create(String rseCodigo, String vctCodigo, String vrsCodigo, String crsCodigo, BigDecimal cmaValor, String cmaVinculo, Integer cmaQuantidade) throws CreateException {
        Session session = SessionUtil.getSession();

        try {
            CompMargem bean = new CompMargem();

            bean.setCmaCodigo(DBHelper.getNextId());
            if (!TextHelper.isNull(rseCodigo)) {
                bean.setRegistroServidor(session.getReference(RegistroServidor.class, rseCodigo));
            }
            if (!TextHelper.isNull(vctCodigo)) {
                bean.setVencimento(session.getReference(Vencimento.class, vctCodigo));
            }
            if (!TextHelper.isNull(vrsCodigo)) {
                bean.setVinculoRegistroServidor(session.getReference(VinculoRegistroServidor.class, vrsCodigo));
            }
            if (!TextHelper.isNull(crsCodigo)) {
                bean.setCargoRegistroServidor(session.getReference(CargoRegistroServidor.class, crsCodigo));
            }
            if (!TextHelper.isNull(cmaValor)) {
                bean.setCmaVlr(cmaValor);
            }
            if (!TextHelper.isNull(cmaVinculo)) {
                bean.setCmaVinculo(cmaVinculo);
            }
            if (!TextHelper.isNull(cmaQuantidade)) {
                bean.setCmaQuantidade(cmaQuantidade);
            }

            create(bean);
            return bean;
        } catch (final MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }
    }

    public static void removeByRse(String rseCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM CompMargem cma WHERE cma.registroServidor.rseCodigo = :rseCodigo ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            queryUpdate.setParameter("rseCodigo", rseCodigo);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new RemoveException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

}
