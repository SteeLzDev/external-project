package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: StatusLoginHome</p>
 * <p>Description: Classe Home para a entidade StatusLogin</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class StatusLoginHome extends AbstractEntityHome {

    public static StatusLogin findByPrimaryKey(String stuCodigo) throws FindException {
        StatusLogin statusLogin = new StatusLogin();
        statusLogin.setStuCodigo(stuCodigo);
        return find(statusLogin, stuCodigo);
    }

    public static StatusLogin create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
