package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: HistoricoArquivoCsaHome</p>
 * <p>Description: Classe Home para a entidade HistoricoArquivoCsa</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HistoricoArquivoCsaHome extends AbstractEntityHome {

    public static HistoricoArquivoCsa findByPrimaryKey(HistoricoArquivoCsaId id) throws FindException {
        HistoricoArquivoCsa historicoArquivoCsa = new HistoricoArquivoCsa();
        historicoArquivoCsa.setId(id);
        return find(historicoArquivoCsa, id);
    }
    
    public static HistoricoArquivoCsa create(String csaCodigo, Long harCodigo) throws CreateException {
        HistoricoArquivoCsa bean = new HistoricoArquivoCsa();

        HistoricoArquivoCsaId id = new HistoricoArquivoCsaId();
        id.setCsaCodigo(csaCodigo);
        id.setHarCodigo(harCodigo);
        bean.setId(id);
        
        create(bean);
        return bean;
    }
}
