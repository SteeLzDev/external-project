package com.zetra.econsig.persistence.query.log;

import java.util.Date;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaHistArqLogPorPeriodoQuery</p>
 * <p>Description: Listagem do histórico de arquivamento de log filtrado pelo período informado.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaHistArqLogPorPeriodoQuery extends HNativeQuery {

    public Date dataIni;
    public Date dataFim;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpo = new StringBuilder();
        corpo.append("select hal.hal_nome_tabela, hal.hal_data, hal.hal_data_ini_log, hal.hal_data_fim_log, hal.hal_qtd_registros ");
        corpo.append("from tb_historico_arquivamento_log hal ");
        corpo.append("where hal.hal_data_fim_log >= :dataIni ");
        corpo.append("and (hal.hal_data_ini_log <= :dataFim or (hal.hal_data_ini_log <= :dataFim and hal.hal_data_fim_log >= :dataFim)) ");
        corpo.append("union ");
        corpo.append("select 'tb_log', max(log_data), min(log_data), max(log_data), count(*) ");
        corpo.append("from tb_log ");
        corpo.append("where log_data <= :dataFim ");
        corpo.append("having count(*) > 0 ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());
        defineValorClausulaNomeada("dataIni", dataIni, query);
        defineValorClausulaNomeada("dataFim", dataFim, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.HAL_NOME_TABELA,
                Columns.HAL_DATA,
                Columns.HAL_DATA_INI_LOG,
                Columns.HAL_DATA_FIM_LOG,
                Columns.HAL_QTD_REGISTROS
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
