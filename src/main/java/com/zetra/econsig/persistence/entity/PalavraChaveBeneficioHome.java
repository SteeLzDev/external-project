package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: PalavraChaveBeneficioHome</p>
 * <p>Description: Classe Home para a entidade PalavraChaveBeneficio</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PalavraChaveBeneficioHome extends AbstractEntityHome {

    public static PalavraChaveBeneficio findByPrimaryKey(PalavraChaveBeneficioId id) throws FindException {
        PalavraChaveBeneficio palavraChaveBeneficio = new PalavraChaveBeneficio();
        palavraChaveBeneficio.setId(id);
        return find(palavraChaveBeneficio, id);
    }
    
    public static PalavraChaveBeneficio create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
    
}
