package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: MenuHome</p>
 * <p>Description: Classe Home para a entidade Menu</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MenuHome extends AbstractEntityHome {

    public static Menu findByPrimaryKey(String mnuCodigo) throws FindException {
        Menu menu = new Menu();
        menu.setMnuCodigo(mnuCodigo);
        return find(menu, mnuCodigo);
    }

    public static Menu create(String mnuCodigo, String mnuDescricao, Short mnuAtivo, Short mnuSequencia, String mnuImagem) throws CreateException {
        Menu menu = new Menu();
        menu.setMnuCodigo(mnuCodigo);
        menu.setMnuDescricao(mnuDescricao);
        menu.setMnuAtivo(mnuAtivo);
        menu.setMnuSequencia(mnuSequencia);
        menu.setMnuImagem(mnuImagem);
        create(menu);
        return menu;
    }
}
