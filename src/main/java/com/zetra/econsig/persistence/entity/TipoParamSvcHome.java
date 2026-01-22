package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TipoParamSvcHome</p>
 * <p>Description: Classe Home para a entidade TipoParamSvc</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoParamSvcHome extends AbstractEntityHome {

    public static TipoParamSvc findByPrimaryKey(String tpsCodigo) throws FindException {
        TipoParamSvc tipoParamSvc = new TipoParamSvc();
        tipoParamSvc.setTpsCodigo(tpsCodigo);
        return find(tipoParamSvc, tpsCodigo);
    }

    public static TipoParamSvc create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
