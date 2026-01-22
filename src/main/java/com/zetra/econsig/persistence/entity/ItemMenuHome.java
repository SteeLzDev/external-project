package com.zetra.econsig.persistence.entity;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: ItemMenuHome</p>
 * <p>Description: Classe Home para a entidade ItemMenu</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ItemMenuHome extends AbstractEntityHome {

    public static ItemMenu findByPrimaryKey(String itmCodigo) throws FindException {
        ItemMenu itemMenu = new ItemMenu();
        itemMenu.setItmCodigo(itmCodigo);
        return find(itemMenu, itmCodigo);
    }

    public static ItemMenu create(String itmCodigo, String mnuCodigo, String itmCodigoPai, String itmDescricao, Short itmAtivo, Short itmSequencia, String itmSeparador, String itmCentralizador, String itmImagem, String texChave) throws CreateException {

        Session session = SessionUtil.getSession();
        ItemMenu bean = new ItemMenu();
        try {
            bean.setItmCodigo(itmCodigo);
            bean.setMenu(session.getReference(Menu.class, mnuCodigo));
            if (!TextHelper.isNull(itmCodigoPai)) {
                bean.setItemMenu(session.getReference(ItemMenu.class, itmCodigoPai));
            }
            if (!TextHelper.isNull(texChave)) {
                bean.setTextoSistema(session.getReference(TextoSistema.class, texChave));
            }
            bean.setItmDescricao(itmDescricao);
            bean.setItmAtivo(itmAtivo);
            bean.setItmSequencia(itmSequencia);
            bean.setItmSeparador(itmSeparador);
            bean.setItmCentralizador(itmCentralizador);
            bean.setItmImagem(itmImagem);
            create(bean, session);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
