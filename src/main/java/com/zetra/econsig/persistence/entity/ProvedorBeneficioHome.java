package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: PalavraChaveHome</p>
 * <p>Description: Classe Home para a entidade ProvedorBeneficio</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProvedorBeneficioHome extends AbstractEntityHome {

    public static ProvedorBeneficio findByPrimaryKey(String proCodigo) throws FindException {
        ProvedorBeneficio provedorBeneficio = new ProvedorBeneficio();
        provedorBeneficio.setProCodigo(proCodigo);
        return find(provedorBeneficio, proCodigo);
    }
    
    public static ProvedorBeneficio create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }
    
}
