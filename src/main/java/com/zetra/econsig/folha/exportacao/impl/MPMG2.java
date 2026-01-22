package com.zetra.econsig.folha.exportacao.impl;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;

/**
 * <p>Title: MPMG</p>
 * <p>Description: Implementações específicas para MPMG - Ministério Público de Minas Gerais.</p>
 * <p>Copyright: Copyright (c) 2002-2023</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco$
 * $Revision$
 * $Date$
 */
public class MPMG2 extends MPMG {
    protected static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MPMG2.class);

    @Override
    public void preGeraArqLote(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        StringBuilder query = new StringBuilder();
        try {
            query.append("UPDATE tb_tmp_exportacao_ordenada tmp  ");
            query.append("SET tmp.capital_devido = ( ");
            query.append("  SELECT SUM(ade.ade_vlr) ");
            query.append("  FROM tb_aut_desconto ade ");
            query.append("  INNER JOIN tb_tmp_exportacao exp ON (ade.ade_codigo = exp.ade_codigo) ");
            query.append("  WHERE tmp.rse_matricula = exp.rse_matricula ");
            query.append("    AND tmp.org_identificador = exp.org_identificador ");
            query.append("    AND tmp.est_identificador = exp.est_identificador ");
            query.append("    AND tmp.csa_identificador = exp.csa_identificador ");
            query.append("    AND tmp.cnv_cod_verba = exp.cnv_cod_verba ");
            query.append(") ");
            query.append("WHERE EXISTS ( ");
            query.append("  SELECT 1 ");
            query.append("  FROM tb_aut_desconto ade ");
            query.append("  INNER JOIN tb_tmp_exportacao exp ON (ade.ade_codigo = exp.ade_codigo) ");
            query.append("  WHERE tmp.rse_matricula = exp.rse_matricula ");
            query.append("    AND tmp.org_identificador = exp.org_identificador ");
            query.append("    AND tmp.est_identificador = exp.est_identificador ");
            query.append("    AND tmp.csa_identificador = exp.csa_identificador ");
            query.append("    AND tmp.cnv_cod_verba = exp.cnv_cod_verba ");
            query.append(")");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        // Grava a ADE_DATA no campo ADE_ANO_MES_INI da tabela de resultado da exportação
        sobreporAdeAnoMesIni(parametrosExportacao, responsavel);
        // Executa a rotina de remoção das consignações sem margem
        super.processaTabelaExportacao(parametrosExportacao, responsavel);
    }

    /**
     * DESENV-20852 : Usar o ADE_DATA em todas as condições da classe no lugar do ADE_ANO_MES_INI.
     * Deve ser considerada a ADE_DATA como mês/ano e dia 01.
     * @param parametrosExportacao
     * @param responsavel
     * @throws ExportaMovimentoException
     */
    private void sobreporAdeAnoMesIni(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        StringBuilder query = new StringBuilder();
        try {
            query.append("UPDATE tb_tmp_exportacao tmp ");
            query.append("SET tmp.ade_ano_mes_ini = date_format(tmp.ade_data, '%Y-%m-01') ");
            query.append("WHERE tmp.ade_ano_mes_ini != tmp.pex_periodo ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    protected String getCampoReferencia() {
        return "date_format(ade.ade_data, '%Y-%m-01')";
    }
}
