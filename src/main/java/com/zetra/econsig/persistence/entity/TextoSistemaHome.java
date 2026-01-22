package com.zetra.econsig.persistence.entity;

import java.util.Date;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: TextoSistemaHome</p>
 * <p>Description: Classe Home para a entidade TextoSistema</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TextoSistemaHome extends AbstractEntityHome {

    public static TextoSistema findByPrimaryKey(String texChave) throws FindException {
        TextoSistema textoSistema = new TextoSistema();
        textoSistema.setTexChave(texChave);
        return find(textoSistema, texChave);
    }

    public static TextoSistema create(String texChave, String texTexto) throws CreateException {
        TextoSistema textoSistema = new TextoSistema();
        textoSistema.setTexChave(texChave);
        textoSistema.setTexTexto(texTexto);
        create(textoSistema);
        return textoSistema;
    }

    public static TextoSistema create(String texChave, String texTexto, Date texDataAlteracao) throws CreateException {
        TextoSistema textoSistema = new TextoSistema();
        textoSistema.setTexChave(texChave);
        textoSistema.setTexTexto(texTexto);
        textoSistema.setTexDataAlteracao(texDataAlteracao);
        create(textoSistema);
        return textoSistema;
    }
}
