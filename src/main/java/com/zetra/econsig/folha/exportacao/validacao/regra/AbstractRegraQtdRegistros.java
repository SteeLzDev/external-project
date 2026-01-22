package com.zetra.econsig.folha.exportacao.validacao.regra;

import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: AbstractRegraQtdRegistros</p>
 * <p>Description: Classe abstrata com a implementação MYSQL da regra com a da quantidade de registros gerados.</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class AbstractRegraQtdRegistros extends Regra {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AbstractRegraQtdRegistros.class);

    protected String tipo;

    public AbstractRegraQtdRegistros(String tipo) {
        this.tipo = tipo;
    }

    protected long buscaQtdGerado() {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        long qtd = 0;
        try {
            StringBuilder query = new StringBuilder();
            query.append("SELECT ifnull(sum(hmf_qtd), 0) AS qtd ");
            query.append("FROM tb_historico_mov_fin, tb_resultado_validacao_mov ");
            query.append("LEFT OUTER JOIN ").append(Columns.TB_CONVENIO).append(" ON (").append(Columns.HMF_CNV_CODIGO).append(" = ").append(Columns.CNV_CODIGO).append(") ");
            query.append("LEFT OUTER JOIN ").append(Columns.TB_ORGAO).append(" ON (").append(Columns.CNV_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO).append(") ");
            query.append("WHERE rva_periodo = hmf_periodo AND hmf_operacao = '").append(tipo).append("' ");
            query.append("AND rva_codigo = '").append(rvaCodigo).append("' ");
            if (estCodigos != null && estCodigos.size() > 0) {
                query.append("AND ").append(Columns.ORG_EST_CODIGO).append(" IN (:estCodigos) ");
                queryParams.addValue("estCodigos", estCodigos);
            }
            if (orgCodigos != null && orgCodigos.size() > 0) {
                query.append("AND ").append(Columns.ORG_CODIGO).append(" IN (:orgCodigos) ");
                queryParams.addValue("orgCodigos", orgCodigos);
            }

            LOG.debug(query.toString());

            List<TransferObject> rs = MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), "qtd", MySqlDAOFactory.SEPARADOR);
            if (rs.size() > 0) {
                qtd = Long.parseLong(rs.get(0).getAttribute("qtd").toString());
            }
        } catch (DAOException e) {
            LOG.error(e.getMessage(), e);
        }
        return qtd;
    }

    protected abstract long buscaQtdBase();
}
