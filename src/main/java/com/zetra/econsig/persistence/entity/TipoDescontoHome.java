package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TipoDescontoHome</p>
 * <p>Description: Classe Home para a entidade TipoDesconto</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoDescontoHome extends AbstractEntityHome {

    public static TipoDesconto findByPrimaryKey(String tdeCodigo) throws FindException {
        TipoDesconto tipoDesconto = new TipoDesconto();
        tipoDesconto.setTdeCodigo(tdeCodigo);
        return find(tipoDesconto, tdeCodigo);
    }

    public static TipoDesconto create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
