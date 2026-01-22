package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: StatusCompraHome</p>
 * <p>Description: Classe Home para a entidade StatusCompra</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class StatusCompraHome extends AbstractEntityHome {

    public static StatusCompra findByPrimaryKey(String stcCodigo) throws FindException {
        StatusCompra statusCompra = new StatusCompra();
        statusCompra.setStcCodigo(stcCodigo);
        return find(statusCompra, stcCodigo);
    }

    public static StatusLogin create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
