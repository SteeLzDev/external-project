package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: TipoDadoAdicionalHome</p>
 * <p>Description: Classe Home para a entidade TipoDadoAdicional</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TipoDadoAdicionalHome extends AbstractEntityHome {

    public static TipoDadoAdicional findByPrimaryKey(String tdaCodigo) throws FindException {
        TipoDadoAdicional tipoDadoAdicional = new TipoDadoAdicional();
        tipoDadoAdicional.setTdaCodigo(tdaCodigo);
        return find(tipoDadoAdicional, tdaCodigo);
    }

    public static TipoDadoAdicional create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
