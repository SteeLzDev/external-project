package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TipoParamConsignatariaHome</p>
 * <p>Description: Classe Home para a entidade TipoParamConsignataria</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoParamConsignatariaHome extends AbstractEntityHome {

    public static TipoParamConsignataria findByPrimaryKey(String tpaCodigo) throws FindException {
        TipoParamConsignataria tipoParamConsignataria = new TipoParamConsignataria();
        tipoParamConsignataria.setTpaCodigo(tpaCodigo);
        return find(tipoParamConsignataria, tpaCodigo);
    }

    public static TipoParamConsignataria create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
