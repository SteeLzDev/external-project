package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: ParamServicoAutorizacaoDescontoHome</p>
 * <p>Description: Classe Home para a entidade ParamServicoAutorizacao</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParamServicoAutorizacaoDescontoHome extends AbstractEntityHome {
    
    public static ParamServicoAutorizacao findByPrimaryKey(ParamServicoAutorizacaoId id) throws FindException {
        ParamServicoAutorizacao paramSvcAde = new ParamServicoAutorizacao();
        paramSvcAde.setId(id);
        return find(paramSvcAde, id);
    }
    
    public static ParamServicoAutorizacao create(String adeCodigo, String pscCodigo) throws CreateException {
        ParamServicoAutorizacao bean = new ParamServicoAutorizacao();

        ParamServicoAutorizacaoId id = new ParamServicoAutorizacaoId();
        id.setAdeCodigo(adeCodigo);
        id.setPscCodigo(pscCodigo);
        bean.setId(id);

        create(bean);
        return bean;
    }
}
