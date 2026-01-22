package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: UfHome</p>
 * <p>Description: Classe Home para a entidade Uf</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class UfHome extends AbstractEntityHome {

    public static Uf findByPrimaryKey(String ufCod) throws FindException {
        Uf uf = new Uf();
        uf.setUfCod(ufCod);
        return find(uf, ufCod);
    }

    public static Uf create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
