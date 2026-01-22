package com.zetra.econsig.persistence.dao.generic;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.ArquivamentoDAO;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: MySqlArquivamentoDAO</p>
 * <p>Description: Implementação Base do DAO de Arquivamento</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class GenericArquivamentoDAO implements ArquivamentoDAO {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GenericArquivamentoDAO.class);

    @Override
    public void arquivarConsignacoesFinalizadas(AcessoSistema responsavel) throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

            int linhasAfetadas = 0;
            String nomesColunas = null;
            StringBuilder query = new StringBuilder();

            // 1) DEFINE QUAIS CONTRATOS SERÃO ARQUIVADOS

            Object paramCancelados = ParamSist.getInstance().getParam(CodedValues.TPC_QTD_MESES_ARQUIVAMENTO_ADE_CANCELADAS, responsavel);
            Object paramLiquidados = ParamSist.getInstance().getParam(CodedValues.TPC_QTD_MESES_ARQUIVAMENTO_ADE_LIQUIDADAS, responsavel);
            Object paramConcluidos = ParamSist.getInstance().getParam(CodedValues.TPC_QTD_MESES_ARQUIVAMENTO_ADE_CONCLUIDAS, responsavel);
            int mesesArqCancelados  = (TextHelper.isNum(paramCancelados) ? Integer.parseInt(paramCancelados.toString()) : 0);
            int mesesArqLiquidados  = (TextHelper.isNum(paramLiquidados) ? Integer.parseInt(paramLiquidados.toString()) : 0);
            int mesesArqConcluidos  = (TextHelper.isNum(paramConcluidos) ? Integer.parseInt(paramConcluidos.toString()) : 0);

            if (mesesArqCancelados <= 0 && mesesArqLiquidados <= 0 && mesesArqConcluidos <= 0) {
                return;
            }

            selecionarConsignacoesArquivamento(mesesArqCancelados, mesesArqLiquidados, mesesArqConcluidos,responsavel);

            // 2) REALIZA O ARQUIVAMENTO

            // ht_aut_desconto ----------------> histórico da tb_aut_desconto
            nomesColunas = TextHelper.join(Columns.getColumnsOfTable(Columns.TB_AUTORIZACAO_DESCONTO), ", ");
            query.append("insert into ht_aut_desconto (").append(nomesColunas).append(") ");
            query.append("select ").append(nomesColunas).append(" from tb_aut_desconto ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // ht_ocorrencia_dados_ade ----------------> histórico da tb_ocorrencia_dados_ade
            nomesColunas = TextHelper.join(Columns.getColumnsOfTable(Columns.TB_OCORRENCIA_DADOS_ADE), ", ");
            query.append("insert into ht_ocorrencia_dados_ade (").append(nomesColunas).append(") ");
            query.append("select ").append(nomesColunas).append(" from tb_ocorrencia_dados_ade ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // ht_ocorrencia_autorizacao ------> histórico da tb_ocorrencia_autorizacao
            nomesColunas = TextHelper.join(Columns.getColumnsOfTable(Columns.TB_OCORRENCIA_AUTORIZACAO), ", ");
            query.append("insert into ht_ocorrencia_autorizacao (").append(nomesColunas).append(") ");
            query.append("select ").append(nomesColunas).append(" from tb_ocorrencia_autorizacao ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // ht_historico_ocorrencia_autorizacao_ade --------> historico da tb_historico_ocorrencia_ade
            nomesColunas = TextHelper.join(Columns.getColumnsOfTable(Columns.TB_HISTORICO_OCORRENCIA_ADE), ", ");
            query.append("insert into ht_historico_ocorrencia_ade (").append(nomesColunas).append(") ");
            query.append("select ").append("hoa.hoa_codigo, hoa.oca_codigo, hoa.usu_codigo, hoa.hoa_data, hoa.hoa_ip_acesso, hoa.hoa_obs").append(" from tb_historico_ocorrencia_ade hoa ");
            query.append(" where exists (select 1 from tmp_contratos_arquivamento inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = codigo) and (oca.oca_codigo = hoa.oca_codigo)) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // ht_parcela_desconto ------------> histórico da tb_parcela_desconto
            nomesColunas = TextHelper.join(Columns.getColumnsOfTable(Columns.TB_PARCELA_DESCONTO, Columns.PRD_CODIGO), ", ");
            query.append("insert into ht_parcela_desconto (").append(nomesColunas).append(") ");
            query.append("select ").append(nomesColunas).append(" from tb_parcela_desconto ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // ht_ocorrencia_parcela ----------> histórico da tb_ocorrencia_parcela
            nomesColunas = TextHelper.join(Columns.getColumnsOfTable(Columns.TB_OCORRENCIA_PARCELA, Columns.OCP_PRD_CODIGO), ", ");
            query.append("insert into ht_ocorrencia_parcela (").append(nomesColunas).append(", prd_codigo) ");
            query.append("select ").append(nomesColunas).append(", ht_parcela_desconto.prd_codigo from tb_ocorrencia_parcela ");
            query.append("inner join tb_parcela_desconto on (tb_ocorrencia_parcela.prd_codigo = tb_parcela_desconto.prd_codigo) ");
            query.append("inner join ht_parcela_desconto on (tb_parcela_desconto.ade_codigo = ht_parcela_desconto.ade_codigo  ");
            query.append(" and tb_parcela_desconto.prd_numero = ht_parcela_desconto.prd_numero  ");
            query.append(" and tb_parcela_desconto.prd_data_desconto = ht_parcela_desconto.prd_data_desconto ");
            query.append(") ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where tb_parcela_desconto.ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // ht_despesa_individual ----------> histórico da tb_despesa_individual
            nomesColunas = TextHelper.join(Columns.getColumnsOfTable(Columns.TB_DESPESA_INDIVIDUAL), ", ");
            query.append("insert into ht_despesa_individual (").append(nomesColunas).append(") ");
            query.append("select ").append(nomesColunas).append(" from tb_despesa_individual ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // ht_ocorrencia_desp_individual --> histórico da tb_ocorrencia_desp_individual
            nomesColunas = TextHelper.join(Columns.getColumnsOfTable(Columns.TB_OCORRENCIA_DESP_INDIVIDUAL), ", ");
            query.append("insert into ht_ocorrencia_desp_individual (").append(nomesColunas).append(") ");
            query.append("select ").append(nomesColunas).append(" from tb_ocorrencia_desp_individual ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // ht_anexo_autorizacao_desconto --> histórico da tb_anexo_autorizacao_desconto
            nomesColunas = TextHelper.join(Columns.getColumnsOfTable(Columns.TB_ANEXO_AUTORIZACAO_DESCONTO), ", ");
            query.append("insert into ht_anexo_autorizacao_desconto (").append(nomesColunas).append(") ");
            query.append("select ").append(nomesColunas).append(" from tb_anexo_autorizacao_desconto ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // ht_coeficiente_desconto --------> histórico da tb_coeficiente_desconto
            nomesColunas = TextHelper.join(Columns.getColumnsOfTable(Columns.TB_COEFICIENTE_DESCONTO), ", ");
            query.append("insert into ht_coeficiente_desconto (").append(nomesColunas).append(") ");
            query.append("select ").append(nomesColunas).append(" from tb_coeficiente_desconto ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // ht_dados_autorizacao_desconto --> histórico da tb_dados_autorizacao_desconto
            nomesColunas = TextHelper.join(Columns.getColumnsOfTable(Columns.TB_DADOS_AUTORIZACAO_DESCONTO), ", ");
            query.append("insert into ht_dados_autorizacao_desconto (").append(nomesColunas).append(") ");
            query.append("select ").append(nomesColunas).append(" from tb_dados_autorizacao_desconto ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // ht_param_servico_autorizacao ---> histórico da tb_param_servico_autorizacao
            nomesColunas = TextHelper.join(Columns.getColumnsOfTable(Columns.TB_PARAM_SERVICO_AUTORIZACAO_DESCONTO), ", ");
            query.append("insert into ht_param_servico_autorizacao (").append(nomesColunas).append(") ");
            query.append("select ").append(nomesColunas).append(" from tb_param_servico_autorizacao ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // ht_saldo_devedor ---------------> histórico da tb_saldo_devedor
            nomesColunas = TextHelper.join(Columns.getColumnsOfTable(Columns.TB_SALDO_DEVEDOR), ", ");
            query.append("insert into ht_saldo_devedor (").append(nomesColunas).append(") ");
            query.append("select ").append(nomesColunas).append(" from tb_saldo_devedor ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // ht_solicitacao_autorizacao -----> histórico da tb_solicitacao_autorizacao
            nomesColunas = TextHelper.join(Columns.getColumnsOfTable(Columns.TB_SOLICITACAO_AUTORIZACAO), ", ");
            query.append("insert into ht_solicitacao_autorizacao (").append(nomesColunas).append(") ");
            query.append("select ").append(nomesColunas).append(" from tb_solicitacao_autorizacao ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // ht_proposta_pagamento_divida ---> histórico da tb_proposta_pagamento_divida
            nomesColunas = TextHelper.join(Columns.getColumnsOfTable(Columns.TB_PROPOSTA_PAGAMENTO_DIVIDA), ", ");
            query.append("insert into ht_proposta_pagamento_divida (").append(nomesColunas).append(") ");
            query.append("select ").append(nomesColunas).append(" from tb_proposta_pagamento_divida ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // ht_proposta_leilao_solicitacao ---> histórico da tb_proposta_leilao_solicitacao
            nomesColunas = TextHelper.join(Columns.getColumnsOfTable(Columns.TB_PROPOSTA_LEILAO_SOLICITACAO), ", ");
            query.append("insert into ht_proposta_leilao_solicitacao (").append(nomesColunas).append(") ");
            query.append("select ").append(nomesColunas).append(" from tb_proposta_leilao_solicitacao ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // ht_decisao_judicial ---> histórico da tb_decisao_judicial
            nomesColunas = TextHelper.join(Columns.getColumnsOfTable(Columns.TB_DECISAO_JUDICIAL), ", ");
            query.append("insert into ht_decisao_judicial (").append(nomesColunas).append(") ");
            query.append("select ").append(nomesColunas).append(" from tb_decisao_judicial ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento ");
            query.append("inner join tb_ocorrencia_autorizacao on (ade_codigo = codigo) ");
            query.append("where tb_ocorrencia_autorizacao.oca_codigo = tb_decisao_judicial.oca_codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // ht_relacionamento_autorizacao --> histórico da tb_relacionamento_autorizacao, onde a origem e destino estão na ht_aut_desconto
            nomesColunas = TextHelper.join(Columns.getColumnsOfTable(Columns.TB_RELACIONAMENTO_AUTORIZACAO), ", ");
            query.append("insert into ht_relacionamento_autorizacao (").append(nomesColunas).append(") ");
            query.append("select ").append(nomesColunas).append(" from tb_relacionamento_autorizacao ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo_origem = codigo) ");
            query.append("and exists (select 1 from tmp_contratos_arquivamento where ade_codigo_destino = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // ht_relacionamento_autorizacao --> histórico da ht_relacionamento_ade_origem, onde o destino estão na ht_aut_desconto (a origem já estará)
            nomesColunas = TextHelper.join(Columns.getColumnsOfTable(Columns.TB_RELACIONAMENTO_AUTORIZACAO), ", ");
            query.append("insert into ht_relacionamento_autorizacao (").append(nomesColunas).append(") ");
            query.append("select ").append(nomesColunas).append(" from ht_relacionamento_ade_origem ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo_destino = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // ht_relacionamento_autorizacao --> histórico da ht_relacionamento_ade_destino, onde a origem estão na ht_aut_desconto (o destino já estará)
            nomesColunas = TextHelper.join(Columns.getColumnsOfTable(Columns.TB_RELACIONAMENTO_AUTORIZACAO), ", ");
            query.append("insert into ht_relacionamento_autorizacao (").append(nomesColunas).append(") ");
            query.append("select ").append(nomesColunas).append(" from ht_relacionamento_ade_destino ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo_origem = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // ht_relacionamento_ade_origem  --> histórico da tb_relacionamento_autorizacao, onde a origem está na ht_aut_desconto, e o destino na tb_aut_desconto
            nomesColunas = TextHelper.join(Columns.getColumnsOfTable(Columns.TB_RELACIONAMENTO_AUTORIZACAO), ", ");
            query.append("insert into ht_relacionamento_ade_origem (").append(nomesColunas).append(") ");
            query.append("select ").append(nomesColunas).append(" from tb_relacionamento_autorizacao ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo_origem = codigo) ");
            query.append("and not exists (select 1 from tmp_contratos_arquivamento where ade_codigo_destino = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // ht_relacionamento_ade_destino --> histórico da tb_relacionamento_autorizacao, onde a origem está na tb_aut_desconto, e o destino na ht_aut_desconto
            nomesColunas = TextHelper.join(Columns.getColumnsOfTable(Columns.TB_RELACIONAMENTO_AUTORIZACAO), ", ");
            query.append("insert into ht_relacionamento_ade_destino (").append(nomesColunas).append(") ");
            query.append("select ").append(nomesColunas).append(" from tb_relacionamento_autorizacao ");
            query.append("where not exists (select 1 from tmp_contratos_arquivamento where ade_codigo_origem = codigo) ");
            query.append("and exists (select 1 from tmp_contratos_arquivamento where ade_codigo_destino = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);


            // 3) APAGA/ATUALIZA OS DADOS NA TABELA NÃO ARQUIVADAS

            // tb_historico_margem_rse
            query.append("update tb_historico_margem_rse set oca_codigo_ht = oca_codigo, oca_codigo = null ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento ");
            query.append("inner join tb_ocorrencia_autorizacao on (ade_codigo = codigo) ");
            query.append("where tb_historico_margem_rse.oca_codigo = tb_ocorrencia_autorizacao.oca_codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // tb_operacao_libera_margem
            query.append("update tb_operacao_libera_margem set ade_codigo_ht = ade_codigo, ade_codigo = null ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // tb_arquivo_retorno_parcela
            query.append("delete from tb_arquivo_retorno_parcela ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // tb_ignora_inconsistencia_ade
            query.append("delete from tb_ignora_inconsistencia_ade ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);


            // 4) APAGA OS DADOS NA TABELA ARQUIVADAS

            // ht_relacionamento_ade_origem
            query.append("delete from ht_relacionamento_ade_origem ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo_destino = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // ht_relacionamento_ade_destino
            query.append("delete from ht_relacionamento_ade_destino ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo_origem = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // tb_historico_status_ade
            query.append("delete from tb_historico_status_ade ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // tb_relacionamento_autorizacao
            query.append("delete from tb_relacionamento_autorizacao ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo_origem = codigo) ");
            query.append("or exists (select 1 from tmp_contratos_arquivamento where ade_codigo_destino = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // tb_decisao_judicial
            query.append("delete from tb_decisao_judicial ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento ");
            query.append("inner join tb_ocorrencia_autorizacao on (ade_codigo = codigo) ");
            query.append("where tb_ocorrencia_autorizacao.oca_codigo = tb_decisao_judicial.oca_codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // tb_proposta_pagamento_divida
            query.append("delete from tb_proposta_pagamento_divida ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // tb_proposta_leilao_solicitacao
            query.append("delete from tb_proposta_leilao_solicitacao ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // tb_solicitacao_autorizacao
            query.append("delete from tb_solicitacao_autorizacao ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // tb_saldo_devedor
            query.append("delete from tb_saldo_devedor ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // tb_param_servico_autorizacao
            query.append("delete from tb_param_servico_autorizacao ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // tb_ocorrencia_dados_ade
            query.append("delete from tb_ocorrencia_dados_ade ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // tb_dados_autorizacao_desconto
            query.append("delete from tb_dados_autorizacao_desconto ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // tb_coeficiente_desconto
            query.append("delete from tb_coeficiente_desconto ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // tb_anexo_autorizacao_desconto
            query.append("delete from tb_anexo_autorizacao_desconto ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // tb_ocorrencia_desp_individual
            query.append("delete from tb_ocorrencia_desp_individual ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // tb_despesa_individual
            query.append("delete from tb_despesa_individual ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // tb_ocorrencia_parcela
            query.append("delete from tb_ocorrencia_parcela ");
            query.append("where exists ( ");
            query.append("  select 1 from tmp_contratos_arquivamento ");
            query.append("  inner join tb_parcela_desconto on (ade_codigo = codigo) ");
            query.append("  where tb_ocorrencia_parcela.prd_codigo = tb_parcela_desconto.prd_codigo ");
            query.append(")");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // tb_parcela_desconto
            query.append("delete from tb_parcela_desconto ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // tb_historico_ocorrencia_ade
            query.append("delete from tb_historico_ocorrencia_ade hoa " );
            query.append("where exists (select 1 from tmp_contratos_arquivamento inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = codigo) where (oca.oca_codigo = hoa.oca_codigo) ) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // tb_ocorrencia_autorizacao
            query.append("delete from tb_ocorrencia_autorizacao ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

            // tb_aut_desconto
            query.append("delete from tb_aut_desconto ");
            query.append("where exists (select 1 from tmp_contratos_arquivamento where ade_codigo = codigo) ");
            LOG.trace(query.toString());
            linhasAfetadas = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + linhasAfetadas);
            query.setLength(0);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
        } finally {


        }
    }

    protected abstract void selecionarConsignacoesArquivamento(int mesesArqCancelados, int mesesArqLiquidados, int mesesArqConcluidos,AcessoSistema responsavel) throws DataAccessException;
}
