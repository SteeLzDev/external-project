package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: HistoricoArquivoCseHome</p>
 * <p>Description: Classe Home para a entidade HistoricoArquivoCse</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HistoricoArquivoCseHome extends AbstractEntityHome {

    public static HistoricoArquivoCse findByPrimaryKey(HistoricoArquivoCseId id) throws FindException {
        HistoricoArquivoCse historicoArquivoCse = new HistoricoArquivoCse();
        historicoArquivoCse.setId(id);
        return find(historicoArquivoCse, id);
    }
    
    public static HistoricoArquivoCse create(String cseCodigo, Long harCodigo) throws CreateException {
        HistoricoArquivoCse bean = new HistoricoArquivoCse();

        HistoricoArquivoCseId id = new HistoricoArquivoCseId();
        id.setCseCodigo(cseCodigo);
        id.setHarCodigo(harCodigo);
        bean.setId(id);
        
        create(bean);
        return bean;
    }
}
