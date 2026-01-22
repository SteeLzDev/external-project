package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TipoLogHome</p>
 * <p>Description: Classe Home para a entidade TipoLog</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoLogHome extends AbstractEntityHome {

    public static TipoLog findByPrimaryKey(String tloCodigo) throws FindException {
        TipoLog tipoLog = new TipoLog();
        tipoLog.setTloCodigo(tloCodigo);
        return find(tipoLog, tloCodigo);
    }

    public static TipoLog create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
