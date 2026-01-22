package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: CepHome</p>
 * <p>Description: Classe Home para a entidade Cep</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor $
 * $Revision: 17001 $
 * $Date: 2014-05-20 15:35:17 -0300 (Ter, 20 mai 2014) $
 */

public class CepHome extends AbstractEntityHome {
    public static Cep findByPrimaryKey(String cepCodigo) throws FindException {
        Cep cep = new Cep();
        cep.setCepCodigo(cepCodigo);
        return find(cep, cepCodigo);
    }

    public static Cep create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
}
