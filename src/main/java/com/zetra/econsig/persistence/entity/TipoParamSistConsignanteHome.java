package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TipoParamSistConsignanteHome</p>
 * <p>Description: Classe Home para a entidade TipoParamSistConsignante</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoParamSistConsignanteHome extends AbstractEntityHome {

    public static TipoParamSistConsignante findByPrimaryKey(String tpcCodigo) throws FindException {
        TipoParamSistConsignante tipoParamSistConsignante = new TipoParamSistConsignante();
        tipoParamSistConsignante.setTpcCodigo(tpcCodigo);
        return find(tipoParamSistConsignante, tpcCodigo);
    }

    public static TipoParamSistConsignante create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
