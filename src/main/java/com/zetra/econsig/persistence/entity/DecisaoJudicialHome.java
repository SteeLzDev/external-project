package com.zetra.econsig.persistence.entity;

import java.util.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: DecisaoJudicialHome</p>
 * <p>Description: Classe Home para a entidade DecisaoJudicial</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DecisaoJudicialHome extends AbstractEntityHome {

    public static DecisaoJudicial findByPrimaryKey(String djuCodigo) throws FindException {
        DecisaoJudicial decisaoJudicial = new DecisaoJudicial();
        decisaoJudicial.setDjuCodigo(djuCodigo);
        return find(decisaoJudicial, djuCodigo);
    }

    public static DecisaoJudicial create(String ocaCodigo, String tjuCodigo, String cidCodigo, String djuNumProcesso, Date djuData, String djuTexto, Date djuDataRevogacao) throws CreateException {
        Session session = SessionUtil.getSession();
        DecisaoJudicial bean = new DecisaoJudicial();

        try {
            bean.setDjuCodigo(DBHelper.getNextId());
            bean.setOcorrenciaAutorizacao(session.getReference(OcorrenciaAutorizacao.class, ocaCodigo));
            bean.setTipoJustica(session.getReference(TipoJustica.class, tjuCodigo));
            if (!TextHelper.isNull(cidCodigo)) {
                bean.setCidade(session.getReference(Cidade.class, cidCodigo));
            }
            if (!TextHelper.isNull(djuNumProcesso)) {
                bean.setDjuNumProcesso(djuNumProcesso);
            } else {
                bean.setDjuNumProcesso("");
            }
            bean.setDjuData(djuData);
            bean.setDjuTexto(djuTexto);
            if(!TextHelper.isNull(djuDataRevogacao)){
                bean.setDjuDataRevogacao(djuDataRevogacao);
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
