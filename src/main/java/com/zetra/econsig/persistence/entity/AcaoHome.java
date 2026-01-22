package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: AcaoHome</p>
 * <p>Description: Classe Home para a entidade Acao</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AcaoHome extends AbstractEntityHome {


    public static Acao findByPrimaryKey(String acaCodigo) throws FindException {
        throw new FindException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }

    public static Acao create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }


}
