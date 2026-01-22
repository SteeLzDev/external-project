package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: NaturezaPlanoHome</p>
 * <p>Description: Classe Home para a entidade Natureza Plano</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class NaturezaPlanoHome extends AbstractEntityHome {

    public static NaturezaPlano findByPrimaryKey(String nplCodigo) throws FindException {
        NaturezaPlano naturezaPlano = new NaturezaPlano();
        naturezaPlano.setNplCodigo(nplCodigo);
        return find(naturezaPlano, nplCodigo);
    }

    public static NaturezaPlano create(String nplCodigo, String nplDescricao) throws CreateException {
        NaturezaPlano bean = new NaturezaPlano();
        bean.setNplCodigo(nplCodigo);
        bean.setNplDescricao(nplDescricao);
        create(bean);
        return bean;
    }
}
