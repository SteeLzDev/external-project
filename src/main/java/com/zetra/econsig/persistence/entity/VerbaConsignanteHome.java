package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: VerbaConsignanteHome</p>
 * <p>Description: Classe Home para a entidade VerbaConsignante</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class VerbaConsignanteHome extends AbstractEntityHome {

    public static VerbaConsignante findByPrimaryKey(String vceCodigo) throws FindException {
        VerbaConsignante verbaConsignante = new VerbaConsignante();
        verbaConsignante.setVceCodigo(vceCodigo);
        return find(verbaConsignante, vceCodigo);
    }

    public static VerbaConsignante create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
