package com.zetra.econsig.persistence.entity;

import java.util.Date;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: LeituraMensagemUsuarioHome</p>
 * <p>Description: Classe Home para a entidade LeituraMensagemUsuario</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class LeituraMensagemUsuarioHome extends AbstractEntityHome {

    public static LeituraMensagemUsuario findByPrimaryKey(LeituraMensagemUsuarioId pk) throws FindException {
        LeituraMensagemUsuario leituraMensagemUsuario = new LeituraMensagemUsuario();
        leituraMensagemUsuario.setId(pk);
        return find(leituraMensagemUsuario, pk);
    }

    public static LeituraMensagemUsuario create(String menCodigo, String usuCodigo, Date lmuData) throws CreateException {
        LeituraMensagemUsuario bean = new LeituraMensagemUsuario();

        LeituraMensagemUsuarioId id = new LeituraMensagemUsuarioId();
        id.setMenCodigo(menCodigo);
        id.setUsuCodigo(usuCodigo);
        bean.setId(id);
        bean.setLmuData(lmuData);

        create(bean);
        return bean;
    }

}
