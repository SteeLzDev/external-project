package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

public class BannerPublicidadeHome extends AbstractEntityHome {

    public static BannerPublicidade findByPrimaryKey(String bpuCodigo) throws FindException {
        BannerPublicidade bannerPublicidade = new BannerPublicidade();
        bannerPublicidade.setBpuCodigo(bpuCodigo);
        return find(bannerPublicidade, bpuCodigo);
    }

    public static List<BannerPublicidade> findByNseCodigo(String nseCodigo) throws FindException {
        String query = "FROM BannerPublicidade bpu WHERE bpu.naturezaServico.nseCodigo = :nseCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("nseCodigo", nseCodigo);

        return findByQuery(query, parameters);
    }

    public static BannerPublicidade create(String arqCodigo, String nseCodigo, String bpuDescricao, String bpuUrlSaida, Short bpuOrdem, String bpuExibeMobile) throws CreateException {
        Session session = SessionUtil.getSession();

        try {
            BannerPublicidade bean = new BannerPublicidade();

            bean.setBpuCodigo(DBHelper.getNextId());
            if (!TextHelper.isNull(arqCodigo)) {
                bean.setArquivo(session.getReference(Arquivo.class, arqCodigo));
            }
            if (!TextHelper.isNull(nseCodigo)) {
                bean.setNaturezaServico(session.getReference(NaturezaServico.class, nseCodigo));
            }
            if (!TextHelper.isNull(bpuDescricao)) {
                bean.setBpuDescricao(bpuDescricao);
            }
            if (!TextHelper.isNull(bpuUrlSaida)) {
                bean.setBpuDescricao(bpuUrlSaida);
            }
            bean.setBpuOrdem(bpuOrdem);
            bean.setBpuExibeMobile(bpuExibeMobile);
            bean.setBpuData(DateHelper.getSystemDatetime());

            create(bean, session);
            return bean;
        } catch (final MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }
    }

    public static void removeByNse(String nseCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM BannerPublicidade bos WHERE bpu.naturezaServico.nseCodigo = :nseCodigo ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            queryUpdate.setParameter("nseCodigo", nseCodigo);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new RemoveException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }
}
