package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: IndiceHome</p>
 * <p>Description: Classe Home para a entidade Indice</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class IndiceHome extends AbstractEntityHome {

    public static Indice findByPrimaryKey(IndiceId id) throws FindException {
        Indice indice = new Indice();
        indice.setId(id);
        return find(indice, id);
    }

    public static Indice create(String svcCodigo, String csaCodigo, String indCodigo, String indDescricao) throws CreateException {
        Indice bean = new Indice();
        IndiceId id = new IndiceId(svcCodigo, csaCodigo, indCodigo);
        bean.setId(id);
        bean.setIndDescricao(indDescricao);
        
        create(bean);
        return bean;        
    }
}
