package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: FuncaoSensivelCsaHome</p>
 * <p>Description: Classe Home para a entidade FuncaoSensivelCsa </p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class FuncaoSensivelCsaHome extends AbstractEntityHome {

    public static FuncaoSensivelCsa findByPrimaryKey(String csaCodigo, String funCodigo) throws FindException {
        FuncaoSensivelCsaId id = new FuncaoSensivelCsaId(csaCodigo, funCodigo);
        FuncaoSensivelCsa bean = new FuncaoSensivelCsa();
        bean.setId(id);
        return find(bean, id);
    }

    public static FuncaoSensivelCsa create(String csaCodigo, String funCodigo, String fscValor) throws CreateException {
        FuncaoSensivelCsaId id = new FuncaoSensivelCsaId(csaCodigo, funCodigo);
        FuncaoSensivelCsa bean = new FuncaoSensivelCsa();
        bean.setId(id);
        bean.setFscValor(fscValor);
        create(bean);
        return bean;
    }
}
