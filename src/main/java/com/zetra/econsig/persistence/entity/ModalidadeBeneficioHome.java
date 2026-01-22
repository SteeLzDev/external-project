package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ModalidadeBeneficioHome</p>
 * <p>Description: Classe Home para a entidade ModalidadeBeneficioHome</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ModalidadeBeneficioHome extends AbstractEntityHome {

    public static ModalidadeBeneficio findByPrimaryKey(String mbeCodigo) throws FindException {
        ModalidadeBeneficio modalidadeBeneficio = new ModalidadeBeneficio();
        modalidadeBeneficio.setMbeCodigo(mbeCodigo);
        return find(modalidadeBeneficio, mbeCodigo);
    }
    
    public static ModalidadeBeneficio create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }

}
