package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: PalavraChaveHome</p>
 * <p>Description: Classe Home para a entidade PalavraChaveHome</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PalavraChaveHome extends AbstractEntityHome {

    public static PalavraChave findByPrimaryKey(Integer pchCodigo) throws FindException {
        PalavraChave palavraChave = new PalavraChave();
        palavraChave.setPchCodigo(pchCodigo);
        return find(palavraChave, pchCodigo);
    }

    public static PalavraChave create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }

}
