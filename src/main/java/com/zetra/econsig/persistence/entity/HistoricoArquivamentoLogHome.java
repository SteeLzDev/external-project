package com.zetra.econsig.persistence.entity;

import java.util.Date;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: HistoricoArquivamentoLogHome</p>
 * <p>Description: Classe Home para a entidade HistoricoArquivamentoLog</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HistoricoArquivamentoLogHome extends AbstractEntityHome {

    public static HistoricoArquivamentoLog findByPrimaryKey(String halNomeTabela) throws FindException {
        HistoricoArquivamentoLog hal = new HistoricoArquivamentoLog();
        hal.setHalNomeTabela(halNomeTabela);
        return find(hal, halNomeTabela);
    }

    public static HistoricoArquivamentoLog create(String halNomeTabela, Date halData, Date halDataIniLog, Date halDataFimLog, Integer halQtdRegistros) throws CreateException {
        HistoricoArquivamentoLog bean = new HistoricoArquivamentoLog();
        bean.setHalNomeTabela(halNomeTabela);
        bean.setHalData(halData);
        bean.setHalDataIniLog(halDataIniLog);
        bean.setHalDataFimLog(halDataFimLog);
        bean.setHalQtdRegistros(halQtdRegistros);

        return create(bean);
    }

}
