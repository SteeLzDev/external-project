package com.zetra.econsig.persistence.dao.mysql;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.dao.generic.GenericArquivamentoDAO;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: MySqlArquivamentoDAO</p>
 * <p>Description: Implementação para MySQL do DAO de Arquivamento</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MySqlArquivamentoDAO extends GenericArquivamentoDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MySqlArquivamentoDAO.class);

    @Override
    protected void selecionarConsignacoesArquivamento(int mesesArqCancelados, int mesesArqLiquidados, int mesesArqConcluidos,AcessoSistema responsavel) throws DataAccessException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

        final StringBuilder query = new StringBuilder();

        query.append("drop table if exists tmp_contratos_arquivamento");
        LOG.trace(query.toString());
        jdbc.update(query.toString(), queryParams);
        query.setLength(0);

        query.append("create table tmp_contratos_arquivamento (codigo varchar(32), primary key (codigo))");
        LOG.trace(query.toString());
        jdbc.update(query.toString(), queryParams);
        query.setLength(0);

        query.append("insert into tmp_contratos_arquivamento (codigo) ");

        if (mesesArqCancelados > 0) {
            // CANCELADOS A MAIS DE X MESES QUE NÃO TENHAM PARCELAS NO PERÍODO
            query.append("select ade_codigo from tb_aut_desconto ade ");
            query.append("where sad_codigo = '").append(CodedValues.SAD_CANCELADA).append("' ");
            query.append("and (select max(oca.oca_data) from tb_ocorrencia_autorizacao oca ");
            query.append("where ade.ade_codigo = oca.ade_codigo ");
            query.append("and oca.toc_codigo in ('").append(CodedValues.TOC_TARIF_CANCELAMENTO_CONSIGNACAO).append("','").append(CodedValues.TOC_TARIF_CANCELAMENTO_RESERVA).append("') ");
            query.append(") <= date_sub(curdate(), interval ").append(mesesArqCancelados).append(" month) ");
            query.append("and not exists (select 1 from tb_parcela_desconto_periodo prd where ade.ade_codigo = prd.ade_codigo) ");
        }

        if (mesesArqLiquidados > 0) {
            if (mesesArqCancelados > 0) {
                query.append("union ");
            }
            // LIQUIDADOS A MAIS DE X MESES QUE NÃO TENHAM PARCELAS NO PERÍODO
            query.append("select ade_codigo from tb_aut_desconto ade ");
            query.append("where sad_codigo = '").append(CodedValues.SAD_LIQUIDADA).append("' ");
            query.append("and (select max(oca.oca_data) from tb_ocorrencia_autorizacao oca ");
            query.append("where ade.ade_codigo = oca.ade_codigo ");
            query.append("and oca.toc_codigo = '").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("' ");
            query.append(") <= date_sub(curdate(), interval ").append(mesesArqLiquidados).append(" month) ");
            query.append("and not exists (select 1 from tb_parcela_desconto_periodo prd where ade.ade_codigo = prd.ade_codigo) ");
        }

        if (mesesArqConcluidos > 0) {
            if (mesesArqCancelados > 0 || mesesArqLiquidados > 0) {
                query.append("union ");
            }
            // CONCLUÍDOS A MAIS DE X MESES QUE NÃO TENHAM PARCELAS NO PERÍODO
            query.append("select ade_codigo from tb_aut_desconto ade ");
            query.append("where sad_codigo = '").append(CodedValues.SAD_CONCLUIDO).append("' ");
            query.append("and (select max(oca.oca_data) from tb_ocorrencia_autorizacao oca ");
            query.append("where ade.ade_codigo = oca.ade_codigo ");
            query.append("and oca.toc_codigo in ('").append(CodedValues.TOC_CONCLUSAO_CONTRATO).append("','").append(CodedValues.TOC_CONCLUSAO_SEM_DESCONTO).append("') ");
            query.append(") <= date_sub(curdate(), interval ").append(mesesArqConcluidos).append(" month) ");
            query.append("and not exists (select 1 from tb_parcela_desconto_periodo prd where ade.ade_codigo = prd.ade_codigo) ");
        }
        LOG.trace(query.toString());
        int linhasAfetadas = jdbc.update(query.toString(), queryParams);
        LOG.trace("Linhas afetadas: " + linhasAfetadas);
        query.setLength(0);
    }
}
