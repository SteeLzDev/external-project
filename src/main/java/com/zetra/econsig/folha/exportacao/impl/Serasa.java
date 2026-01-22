package com.zetra.econsig.folha.exportacao.impl;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: Globo</p>
 * <p>Description: Implementações específicas para a Globo</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Serasa extends ExportaMovimentoBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Serasa.class);

    public static final String LINHAS_AFETADAS = "Linhas afetadas: ";
    
    @Override
    public void posCriacaoTabelas(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        super.posCriacaoTabelas(parametrosExportacao, responsavel);

        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            final StringBuilder query = new StringBuilder();

            query.append("DROP TABLE IF EXISTS tmp_arquivo_movimento");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TEMPORARY TABLE tmp_arquivo_movimento ( ");
            query.append("  ARM_SITUACAO char(2) NOT NULL, ");
            query.append("  PEX_PERIODO date NOT NULL, ");
            query.append("  PEX_PERIODO_ANT date NOT NULL, ");
            query.append("  PEX_PERIODO_POS date NOT NULL, ");
            query.append("  SER_CPF varchar(19) DEFAULT NULL, ");
            query.append("  RSE_MATRICULA varchar(20) DEFAULT NULL, ");
            query.append("  ORG_IDENTIFICADOR varchar(40) DEFAULT NULL, ");
            query.append("  EST_IDENTIFICADOR varchar(40) DEFAULT NULL, ");
            query.append("  CSA_IDENTIFICADOR varchar(40) DEFAULT NULL, ");
            query.append("  SVC_IDENTIFICADOR varchar(40) DEFAULT NULL, ");
            query.append("  CNV_COD_VERBA varchar(32) DEFAULT NULL, ");
            query.append("  ADE_NUMERO bigint DEFAULT NULL, ");
            query.append("  ADE_INDICE varchar(32) DEFAULT NULL, ");
            query.append("  ADE_DATA datetime DEFAULT NULL, ");
            query.append("  ADE_ANO_MES_INI date DEFAULT NULL, ");
            query.append("  ADE_ANO_MES_FIM date DEFAULT NULL, ");
            query.append("  ADE_PRAZO int DEFAULT NULL, ");
            query.append("  ADE_VLR decimal(13,2) DEFAULT NULL, ");
            query.append("  KEY ARQUIVO_MOVIMENTO_IDX0_ade (ADE_NUMERO), ");
            query.append("  KEY ARQUIVO_MOVIMENTO_IDX1_mul (PEX_PERIODO,RSE_MATRICULA,CNV_COD_VERBA), ");
            query.append("  KEY ARQUIVO_MOVIMENTO_IDX2_mul1 (PEX_PERIODO,SER_CPF,CNV_COD_VERBA) ");
            query.append(")");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void preGeraArqLote(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        StringBuilder query = new StringBuilder();
        try {
            int rows = 0;

            query.append("DELETE tmp.* FROM tb_tmp_exportacao_ordenada tmp ");
            query.append("INNER JOIN tb_arquivo_movimento arq ON (tmp.pex_periodo = arq.pex_periodo AND arq.ade_numero = tmp.ade_numero AND arq.arm_situacao = tmp.situacao) ");
            LOG.debug(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.debug(LINHAS_AFETADAS + rows);
            query.setLength(0);
            
            query.append("INSERT INTO tmp_arquivo_movimento ");
            query.append("SELECT * FROM tb_arquivo_movimento ");
            LOG.debug(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.debug(LINHAS_AFETADAS + rows);
            query.setLength(0);
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
    
    @Override
    public String posProcessaArqLote(String nomeArqLote, ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
         final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
         final MapSqlParameterSource queryParams = new MapSqlParameterSource();
         StringBuilder query = new StringBuilder();
         try {
             int rows = 0;

             query.append("DELETE FROM tb_arquivo_movimento ");
             LOG.debug(query.toString());
             rows = jdbc.update(query.toString(), queryParams);
             LOG.debug(LINHAS_AFETADAS + rows);
             query.setLength(0);
             
             query.append("INSERT INTO tb_arquivo_movimento ");
             query.append("SELECT * FROM tmp_arquivo_movimento ");
             LOG.debug(query.toString());
             rows = jdbc.update(query.toString(), queryParams);
             LOG.debug(LINHAS_AFETADAS + rows);
             query.setLength(0);
             
             limparTabelaExportacao(parametrosExportacao.getOrgCodigos(),
                     parametrosExportacao.getEstCodigos(),
                     parametrosExportacao.getVerbas(),
                     parametrosExportacao.getResponsavel());
             
             gravarTabelaExportacao();
             
         } catch (final DataAccessException | DAOException ex) {
             LOG.error(ex.getMessage(), ex);
             throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
         }
        return nomeArqLote;
    }
    
    private void limparTabelaExportacao(List<String> orgCodigos, List<String> estCodigos, List<String> verbas, AcessoSistema responsavel) throws DAOException {
        final Object paramQtdPeriodosTbExportacao = ParamSist.getInstance().getParam(CodedValues.TPC_QTD_PERIODOS_MANTIDOS_NA_TABELA_MOVIMENTO, responsavel);
        final int qtdPeriodos = (!TextHelper.isNull(paramQtdPeriodosTbExportacao) ? Integer.parseInt(paramQtdPeriodosTbExportacao.toString()) : 0);
        if (qtdPeriodos > 0) {

            final MapSqlParameterSource queryParams = new MapSqlParameterSource();
            try {

                final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
                String query = "delete from tb_arquivo_movimento where 1 = 1";

                if ((orgCodigos != null) && !orgCodigos.isEmpty()) {
                    query += " and org_identificador in (select org_identificador from tb_orgao where org_codigo in (:orgCodigos)) ";
                    queryParams.addValue("orgCodigos", orgCodigos);
                }
                if ((estCodigos != null) && !estCodigos.isEmpty()) {
                    query += " and est_identificador in (select est_identificador from tb_estabelecimento where est_codigo in (:estCodigos)) ";
                    queryParams.addValue("estCodigos", estCodigos);
                }
                if ((verbas != null) && !verbas.isEmpty()) {
                    query += " and cnv_cod_verba in (:verbas) ";
                    queryParams.addValue("verbas", verbas);
                }
                if (!PeriodoHelper.folhaMensal(responsavel)) {
                    query += " and ((select count(distinct hie.hie_periodo) from tb_historico_exportacao hie inner join tb_orgao org on (hie.org_codigo = org.org_codigo) where org.org_identificador = tb_arquivo_movimento.org_identificador and hie.hie_data_ini <> hie.hie_data_fim and hie.hie_periodo > tb_arquivo_movimento.pex_periodo) >= " + qtdPeriodos
                           + "   ) ";
                } else {
                    query += " and (pex_periodo <= date_sub((select min(pex.pex_periodo) from tb_periodo_exportacao pex inner join tb_orgao org on (pex.org_codigo = org.org_codigo) where org.org_identificador = tb_arquivo_movimento.org_identificador), interval " + qtdPeriodos + " month) "
                           + "   ) ";
                }

                LOG.trace(query);
                final int linhasAfetadas = jdbc.update(query, queryParams);
                LOG.trace(LINHAS_AFETADAS + linhasAfetadas);

            } catch (final DataAccessException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new DAOException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }
    
    private void gravarTabelaExportacao() throws DAOException {
        Object paramQtdPeriodosTbExportacao = ParamSist.getInstance().getParam(CodedValues.TPC_QTD_PERIODOS_MANTIDOS_NA_TABELA_MOVIMENTO, AcessoSistema.getAcessoUsuarioSistema());
        int qtdPeriodos = (!TextHelper.isNull(paramQtdPeriodosTbExportacao) ? Integer.parseInt(paramQtdPeriodosTbExportacao.toString()) : 0);
        if (qtdPeriodos > 0) {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final MapSqlParameterSource queryParams = new MapSqlParameterSource();
            try {
                String query = "INSERT INTO tb_arquivo_movimento (ARM_SITUACAO, PEX_PERIODO, PEX_PERIODO_ANT, PEX_PERIODO_POS, SER_CPF, RSE_MATRICULA, ORG_IDENTIFICADOR, EST_IDENTIFICADOR, CSA_IDENTIFICADOR, SVC_IDENTIFICADOR, CNV_COD_VERBA, ADE_NUMERO, ADE_INDICE, ADE_DATA, ADE_ANO_MES_INI, ADE_ANO_MES_FIM, ADE_PRAZO, ADE_VLR) "
                             + "SELECT SITUACAO, PEX_PERIODO, PEX_PERIODO_ANT, PEX_PERIODO_POS, SER_CPF, RSE_MATRICULA, ORG_IDENTIFICADOR, EST_IDENTIFICADOR, CSA_IDENTIFICADOR, SVC_IDENTIFICADOR, CNV_COD_VERBA, ADE_NUMERO, ADE_INDICE, ADE_DATA, DATA_INI_CONTRATO, DATA_FIM_CONTRATO, NRO_PARCELAS, VALOR_DESCONTO "
                             + "FROM tb_tmp_exportacao_ordenada ";
                LOG.trace(query);
                jdbc.update(query, queryParams);

            } catch (final DataAccessException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
            }
        }
    }
}
