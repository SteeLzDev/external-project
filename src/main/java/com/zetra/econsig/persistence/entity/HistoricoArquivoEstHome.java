package com.zetra.econsig.persistence.entity;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: HistoricoArquivoEstHome</p>
 * <p>Description: Classe Home para a entidade HistoricoArquivoEst</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HistoricoArquivoEstHome extends AbstractEntityHome {

    public static HistoricoArquivoEst findByPrimaryKey(HistoricoArquivoEstId id) throws FindException {
        HistoricoArquivoEst historicoArquivoEst = new HistoricoArquivoEst();
        historicoArquivoEst.setId(id);
        return find(historicoArquivoEst, id);
    }

    public static HistoricoArquivoEst create(String estCodigo, Long harCodigo) throws CreateException {
        HistoricoArquivoEst bean = new HistoricoArquivoEst();

        HistoricoArquivoEstId id = new HistoricoArquivoEstId();
        id.setEstCodigo(estCodigo);
        id.setHarCodigo(harCodigo);
        bean.setId(id);

        create(bean);
        return bean;
    }
}
