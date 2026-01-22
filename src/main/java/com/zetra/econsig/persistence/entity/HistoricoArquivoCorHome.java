package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: HistoricoArquivoCorHome</p>
 * <p>Description: Classe Home para a entidade HistoricoArquivoCor</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HistoricoArquivoCorHome extends AbstractEntityHome {

    public static HistoricoArquivoCor findByPrimaryKey(HistoricoArquivoCorId id) throws FindException {
        HistoricoArquivoCor historicoArquivoCor = new HistoricoArquivoCor();
        historicoArquivoCor.setId(id);
        return find(historicoArquivoCor, id);
    }
    
    public static HistoricoArquivoCor create(String corCodigo, Long harCodigo) throws CreateException {
        HistoricoArquivoCor bean = new HistoricoArquivoCor();

        HistoricoArquivoCorId id = new HistoricoArquivoCorId();
        id.setCorCodigo(corCodigo);
        id.setHarCodigo(harCodigo);
        bean.setId(id);
        
        create(bean);
        return bean;
    }
}
