package com.zetra.econsig.persistence.entity;

import java.util.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.TipoArquivoEnum;

/**
 * <p>Title: AnexoComunicacaoHome</p>
 * <p>Description: Classe Home para a entidade AnexoComunicacaoHome</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 */

public class AnexoComunicacaoHome extends AbstractEntityHome {

    public static AnexoComunicacao findByPrimaryKey(AnexoComunicacaoId pk) throws FindException {
        AnexoComunicacao anexoComunicacao = new AnexoComunicacao();
        anexoComunicacao.setId(pk);
        return find(anexoComunicacao, pk);
    }

    public static AnexoComunicacao create(String cmnCodigo, String acmNome, String acmDescricao, String usuCodigo, Short acmAtivo, Date acmData, TipoArquivoEnum tipoArquivo) throws CreateException {

        Session session = SessionUtil.getSession();
        AnexoComunicacao bean = new AnexoComunicacao();
        try {
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            bean.setTipoArquivo(session.getReference(TipoArquivo.class, tipoArquivo.getCodigo()));
            bean.setAcmAtivo(acmAtivo);
            bean.setAcmData(acmData);
            bean.setAcmDescricao(acmDescricao);
            AnexoComunicacaoId id = new AnexoComunicacaoId();
            id.setCmnCodigo(cmnCodigo);
            id.setAcmNome(acmNome);
            bean.setId(id);

            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

}
