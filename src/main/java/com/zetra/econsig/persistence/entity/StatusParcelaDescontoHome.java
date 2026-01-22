package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: StatusParcelaDescontoHome</p>
 * <p>Description: Classe Home para a entidade StatusParcelaDesconto</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class StatusParcelaDescontoHome extends AbstractEntityHome {

    public static StatusParcelaDesconto findByPrimaryKey(String spdCodigo) throws FindException {
        StatusParcelaDesconto statusParcelaDesconto = new StatusParcelaDesconto();
        statusParcelaDesconto.setSpdCodigo(spdCodigo);
        return find(statusParcelaDesconto, spdCodigo);
    }

    public static StatusParcelaDesconto create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
