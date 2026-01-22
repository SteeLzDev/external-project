package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: BloqueioRepasseFuncaoHome</p>
 * <p>Description: Classe Home para a entidade BloqueioRepasseFuncao</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class BloqueioRepasseFuncaoHome extends AbstractEntityHome {


    public static BloqueioRepasseFuncao findByPrimaryKey(com.zetra.econsig.persistence.entity.BloqueioRepasseFuncaoId id) throws FindException {
        throw new FindException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }

    public static BloqueioRepasseFuncao create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }


}
