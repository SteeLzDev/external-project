package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TipoParamOrgaoHome</p>
 * <p>Description: Classe Home para a entidade TipoParamOrgao</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoParamOrgaoHome extends AbstractEntityHome {

    public static TipoParamOrgao findByPrimaryKey(String taoCodigo) throws FindException {
        TipoParamOrgao tipoParamOrgao = new TipoParamOrgao();
        tipoParamOrgao.setTaoCodigo(taoCodigo);
        return find(tipoParamOrgao, taoCodigo);
    }

    public static TipoParamOrgao create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
