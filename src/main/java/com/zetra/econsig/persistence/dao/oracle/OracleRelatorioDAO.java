package com.zetra.econsig.persistence.dao.oracle;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.dao.mysql.MySqlRelatorioDAO;

/**
 * <p>Title: OracleRelatorioDAO</p>
 * <p>Description: DAO Oracle para relatorios e subrelatórios editaveis</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */
public class OracleRelatorioDAO extends MySqlRelatorioDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OracleRelatorioDAO.class);

    @Override
	public List<TransferObject> executarQuerySubrelatorio(String sql, AcessoSistema responsavel) throws DAOException {
        Connection conn = null;
        Statement stat = null;
        ResultSet rs = null;

        final List<TransferObject> retornoQuery = new ArrayList<>();
        try {
            conn = DBHelper.makeConnection();
            stat = conn.createStatement();
            rs = stat.executeQuery(sql);

            final int nrCampos = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                final CustomTransferObject linha = new CustomTransferObject();
                for (int i = 1; i <= nrCampos; i++) {
                    linha.setAttribute(rs.getMetaData().getColumnLabel(i), rs.getObject(i));
                }
                retornoQuery.add(linha);
            }
        } catch (final SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } finally {
            DBHelper.closeResultSet(rs);
            DBHelper.closeStatement(stat);
            DBHelper.releaseConnection(conn);
        }
        return retornoQuery;
    }

    @Override
	public String montaQueryRelatorioEstatistico(TransferObject criterio, Map<String, Object> parameters) {
        final StringBuilder query = new StringBuilder();

        final String[] referencias = new String[6];
        final List<String> refList = (List<String>) parameters.get("REFERENCIAS");
        for (int i = 0; i < refList.size(); i++) {
            referencias[i] = refList.get(i);
        }

        final String tableName = (String) parameters.get("TABLE_NAME");

        query.append("SELECT DISTINCT csa_identificador, csa_nome, cnv_cod_verba, svc_descricao, ord.ore_sequencia, trim(cast(ord.ore_codigo as char(2))) as ore_codigo, ord.ore_codigo as ordem, ord.ore_descricao, ");
        query.append("coalesce(tmp01.ree_valor, case when tmp01.ree_ordem in ('14','15') then 0 else 0.00 end) AS valor1, ");
        query.append("coalesce(tmp02.ree_valor, case when tmp02.ree_ordem in ('14','15') then 0 else 0.00 end) AS valor2, ");
        query.append("coalesce(tmp03.ree_valor, case when tmp03.ree_ordem in ('14','15') then 0 else 0.00 end) AS valor3, ");
        query.append("coalesce(tmp04.ree_valor, case when tmp04.ree_ordem in ('14','15') then 0 else 0.00 end) AS valor4, ");
        query.append("coalesce(tmp05.ree_valor, case when tmp05.ree_ordem in ('14','15') then 0 else 0.00 end) AS valor5, ");
        query.append("coalesce(tmp06.ree_valor, case when tmp06.ree_ordem in ('14','15') then 0 else 0.00 end) AS valor6, ");
        query.append("CAST(coalesce(tmp01.ree_qtd, 0) AS NUMBER) AS valor7, ");
        query.append("CAST(coalesce(tmp02.ree_qtd, 0) AS NUMBER) AS valor8, ");
        query.append("CAST(coalesce(tmp03.ree_qtd, 0) AS NUMBER) AS valor9, ");
        query.append("CAST(coalesce(tmp04.ree_qtd, 0) AS NUMBER) AS valor10, ");
        query.append("CAST(coalesce(tmp05.ree_qtd, 0) AS NUMBER) AS valor11, ");
        query.append("CAST(coalesce(tmp06.ree_qtd, 0) AS NUMBER) AS valor12 ");
        query.append("FROM tb_consignataria csa ");
        query.append("INNER JOIN tb_convenio cnv ON (csa.csa_codigo = cnv.csa_codigo) ");
        query.append("INNER JOIN tb_servico svc ON (svc.svc_codigo = cnv.svc_codigo) ");
        query.append("CROSS JOIN tb_ordem_relatorio_estatistico ord ");

        query.append("LEFT OUTER JOIN tb_relatorio_estatistico tmp01a ON (tmp01a.ree_nome = '").append(tableName).append("' AND tmp01a.ree_referencia = '").append(referencias[0]).append("' AND tmp01a.ree_ordem = 1 AND tmp01a.ree_csa = csa.csa_identificador AND tmp01a.ree_VERBA = cnv_cod_verba) ");
        query.append("LEFT OUTER JOIN tb_relatorio_estatistico tmp02a ON (tmp02a.ree_nome = '").append(tableName).append("' AND tmp02a.ree_referencia = '").append(referencias[1]).append("' AND tmp02a.ree_ordem = 1 AND tmp02a.ree_csa = csa.csa_identificador AND tmp02a.ree_VERBA = cnv_cod_verba) ");
        query.append("LEFT OUTER JOIN tb_relatorio_estatistico tmp03a ON (tmp03a.ree_nome = '").append(tableName).append("' AND tmp03a.ree_referencia = '").append(referencias[2]).append("' AND tmp03a.ree_ordem = 1 AND tmp03a.ree_csa = csa.csa_identificador AND tmp03a.ree_VERBA = cnv_cod_verba) ");
        query.append("LEFT OUTER JOIN tb_relatorio_estatistico tmp04a ON (tmp04a.ree_nome = '").append(tableName).append("' AND tmp04a.ree_referencia = '").append(referencias[3]).append("' AND tmp04a.ree_ordem = 1 AND tmp04a.ree_csa = csa.csa_identificador AND tmp04a.ree_VERBA = cnv_cod_verba) ");
        query.append("LEFT OUTER JOIN tb_relatorio_estatistico tmp05a ON (tmp05a.ree_nome = '").append(tableName).append("' AND tmp05a.ree_referencia = '").append(referencias[4]).append("' AND tmp05a.ree_ordem = 1 AND tmp05a.ree_csa = csa.csa_identificador AND tmp05a.ree_VERBA = cnv_cod_verba) ");
        query.append("LEFT OUTER JOIN tb_relatorio_estatistico tmp06a ON (tmp06a.ree_nome = '").append(tableName).append("' AND tmp06a.ree_referencia = '").append(referencias[5]).append("' AND tmp06a.ree_ordem = 1 AND tmp06a.ree_csa = csa.csa_identificador AND tmp06a.ree_VERBA = cnv_cod_verba) ");

        query.append("LEFT OUTER JOIN tb_relatorio_estatistico tmp01 ON (tmp01.ree_nome = '").append(tableName).append("' AND tmp01.ree_referencia = '").append(referencias[0]).append("' AND tmp01.ree_ordem = ord.ore_codigo AND tmp01.ree_csa = csa.csa_identificador AND tmp01.ree_VERBA = cnv_cod_verba) ");
        query.append("LEFT OUTER JOIN tb_relatorio_estatistico tmp02 ON (tmp02.ree_nome = '").append(tableName).append("' AND tmp02.ree_referencia = '").append(referencias[1]).append("' AND tmp02.ree_ordem = ord.ore_codigo AND tmp02.ree_csa = csa.csa_identificador AND tmp02.ree_VERBA = cnv_cod_verba) ");
        query.append("LEFT OUTER JOIN tb_relatorio_estatistico tmp03 ON (tmp03.ree_nome = '").append(tableName).append("' AND tmp03.ree_referencia = '").append(referencias[2]).append("' AND tmp03.ree_ordem = ord.ore_codigo AND tmp03.ree_csa = csa.csa_identificador AND tmp03.ree_VERBA = cnv_cod_verba) ");
        query.append("LEFT OUTER JOIN tb_relatorio_estatistico tmp04 ON (tmp04.ree_nome = '").append(tableName).append("' AND tmp04.ree_referencia = '").append(referencias[3]).append("' AND tmp04.ree_ordem = ord.ore_codigo AND tmp04.ree_csa = csa.csa_identificador AND tmp04.ree_VERBA = cnv_cod_verba) ");
        query.append("LEFT OUTER JOIN tb_relatorio_estatistico tmp05 ON (tmp05.ree_nome = '").append(tableName).append("' AND tmp05.ree_referencia = '").append(referencias[4]).append("' AND tmp05.ree_ordem = ord.ore_codigo AND tmp05.ree_csa = csa.csa_identificador AND tmp05.ree_VERBA = cnv_cod_verba) ");
        query.append("LEFT OUTER JOIN tb_relatorio_estatistico tmp06 ON (tmp06.ree_nome = '").append(tableName).append("' AND tmp06.ree_referencia = '").append(referencias[5]).append("' AND tmp06.ree_ordem = ord.ore_codigo AND tmp06.ree_csa = csa.csa_identificador AND tmp06.ree_VERBA = cnv_cod_verba) ");
        query.append("WHERE ord.ore_codigo <> '2' AND (tmp01a.ree_CSA IS NOT NULL OR tmp02a.ree_CSA IS NOT NULL OR tmp03a.ree_CSA IS NOT NULL OR tmp04a.ree_CSA IS NOT NULL OR tmp05a.ree_CSA IS NOT NULL OR tmp06a.ree_CSA IS NOT NULL) ");
        query.append("AND ord.ore_ativo = 1 ");
        query.append("ORDER BY csa_identificador, csa_nome, cnv_cod_verba, ord.ore_sequencia, ord.ore_codigo");
        LOG.debug(query.toString());

        return query.toString();
    }
}
