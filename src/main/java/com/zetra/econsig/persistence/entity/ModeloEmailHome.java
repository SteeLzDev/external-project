package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: ModeloEmailHome</p>
 * <p>Description: Classe Home para a entidade ModeloEmail</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ModeloEmailHome extends AbstractEntityHome {

    public static ModeloEmail findByPrimaryKey(String memCodigo) throws FindException {
        ModeloEmail modeloEmail = new ModeloEmail();
        modeloEmail.setMemCodigo(memCodigo);
        return find(modeloEmail, memCodigo);
    }

    public static ModeloEmail create(String memCodigo, String memTitulo, String memTexto) throws CreateException {
        ModeloEmail modeloEmail = new ModeloEmail();
        modeloEmail.setMemCodigo(memCodigo);
        modeloEmail.setMemTitulo(memTitulo);
        modeloEmail.setMemTexto(memTexto);
        create(modeloEmail);
        return modeloEmail;
    }
}