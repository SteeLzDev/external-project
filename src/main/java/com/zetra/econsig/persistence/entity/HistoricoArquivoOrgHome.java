package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: HistoricoArquivoOrgHome</p>
 * <p>Description: Classe Home para a entidade HistoricoArquivoOrg</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HistoricoArquivoOrgHome extends AbstractEntityHome {

    public static HistoricoArquivoOrg findByPrimaryKey(HistoricoArquivoOrgId id) throws FindException {
        HistoricoArquivoOrg historicoArquivoOrg = new HistoricoArquivoOrg();
        historicoArquivoOrg.setId(id);
        return find(historicoArquivoOrg, id);
    }
    
    public static HistoricoArquivoOrg create(String orgCodigo, Long harCodigo) throws CreateException {
        HistoricoArquivoOrg bean = new HistoricoArquivoOrg();

        HistoricoArquivoOrgId id = new HistoricoArquivoOrgId();
        id.setOrgCodigo(orgCodigo);
        id.setHarCodigo(harCodigo);
        bean.setId(id);
        
        create(bean);
        return bean;
    }
}
