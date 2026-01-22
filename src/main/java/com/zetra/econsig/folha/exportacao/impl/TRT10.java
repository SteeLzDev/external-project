package com.zetra.econsig.folha.exportacao.impl;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: TRT10</p>
 * <p>Description: Implementações específicas para TRT 10 - Tribunal Regional do Trabalho.</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TRT10 extends ExportaMovimentoBase {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(TRT10.class);

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {

        //DESENV-15712 - Ajusta o valor do contrato percentual para o valor realizado antes de fazer a remoção dos contratos.
        LOG.debug("Ajustando o valor dos contratos percentuais para o valor realizado, antes de remover os contratos sem margem");
        ajusteValorContratoPercentual(false);

        // Remove da tabela de exportação as ADE que não cabem na margem
        LOG.debug("TRT10.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
        removerContratosSemMargemMovimentoMensalv2(false, null);
        LOG.debug("fim - TRT10.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());

        LOG.debug("Voltando o valor dos contratos para o original após a remoção dos contratos sem margem");
        ajusteValorContratoPercentual(true);
    }

    private void ajusteValorContratoPercentual(boolean ajustaValorPrevisto) throws ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            final StringBuilder query = new StringBuilder();
            if (!ajustaValorPrevisto) {
                query.append("drop temporary table if exists tb_tmp_ade_vlr_previsto;");
                LOG.info(query);
                jdbc.update(query.toString(), queryParams);
                query.setLength(0);

                query.append(" create temporary table tb_tmp_ade_vlr_previsto");
                query.append(" select ade_codigo, ade_vlr");
                query.append(" from tb_tmp_exportacao");
                query.append(" where ade_tipo_vlr ='").append(CodedValues.TIPO_VLR_PERCENTUAL).append("'");
                LOG.info(query);
                jdbc.update(query.toString(), queryParams);
                query.setLength(0);

                query.append("drop temporary table if exists tb_tmp_ultimo_vlr_realizado;");
                LOG.info(query);
                jdbc.update(query.toString(), queryParams);
                query.setLength(0);

                //Necessário criar uma tabela com o último valor processado pago.
                query.append(" create temporary table tb_tmp_ultimo_vlr_realizado");
                query.append(" SELECT prd.ade_codigo, prd.prd_vlr_realizado ");
                query.append(" FROM (SELECT prd2.ade_codigo, MAX(prd2.prd_data_desconto) as prd_data_desconto FROM tb_parcela_desconto prd2 ");
                query.append(" INNER JOIN tb_tmp_exportacao tmp USING (ade_codigo) ");
                query.append(" WHERE prd2.spd_codigo in ('").append(CodedValues.SPD_LIQUIDADAMANUAL).append("','").append(CodedValues.SPD_LIQUIDADAFOLHA).append("')");
                query.append(" AND tmp.ade_tipo_vlr ='").append(CodedValues.TIPO_VLR_PERCENTUAL).append("'");
                query.append(" GROUP BY prd2.ade_codigo) t ");
                query.append(" JOIN tb_parcela_desconto prd ON (prd.ade_codigo = t.ade_codigo AND prd.prd_data_desconto = t.prd_data_desconto)");
                jdbc.update(query.toString(), queryParams);
                query.setLength(0);

                query.append("UPDATE tb_tmp_exportacao tmp ");
                query.append("LEFT OUTER JOIN tb_tmp_ultimo_vlr_realizado prd ON (prd.ade_codigo = tmp.ade_codigo) ");
                query.append("SET tmp.ade_vlr = IF(prd.ade_codigo IS NULL, tmp.ade_vlr, prd.prd_vlr_realizado) ");
                query.append("WHERE tmp.ade_tipo_vlr ='").append(CodedValues.TIPO_VLR_PERCENTUAL).append("'");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);
            } else {
                query.append("UPDATE tb_tmp_exportacao tmp ");
                query.append("INNER JOIN tb_tmp_ade_vlr_previsto advp ON (advp.ade_codigo = tmp.ade_codigo) ");
                query.append("SET tmp.ade_vlr = advp.ade_vlr ");
                query.append("WHERE tmp.ade_tipo_vlr ='").append(CodedValues.TIPO_VLR_PERCENTUAL).append("'");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                query.setLength(0);
                query.append("drop temporary table if exists tb_tmp_ade_vlr_previsto;");
                LOG.info(query);
                jdbc.update(query.toString(), queryParams);
            }
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
