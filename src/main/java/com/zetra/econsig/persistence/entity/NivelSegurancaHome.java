package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: NivelSegurancaHome</p>
 * <p>Description: Classe Home para a entidade NivelSeguranca</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class NivelSegurancaHome extends AbstractEntityHome {

    public static NivelSeguranca findByPrimaryKey(String nsgCodigo) throws FindException {
        NivelSeguranca nivelSeguranca = new NivelSeguranca();
        nivelSeguranca.setNsgCodigo(nsgCodigo);
        return find(nivelSeguranca, nsgCodigo);
    }

    public static NivelSeguranca create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
