package com.zetra.econsig.folha.exportacao.impl;

import java.util.Date;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.delegate.PeriodoDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: Seg</p>
 * <p>Description: Implementações específicas para a exportação do sistema eConsig SEG </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Seg extends Quinzenal {
	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(Seg.class);

    @Override
    public void posCriacaoTabelas(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        super.posCriacaoTabelas(parametrosExportacao, responsavel);
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try {
            // Não tem o DROP da tabela pois ela não deve ser recriada a cada exportação, visto que armazena
            // os dados de uma exportação até a próxima para a análise do prazo folha
            StringBuilder query = new StringBuilder();
            query.append("CREATE TABLE IF NOT EXISTS tb_dados_exportacao ( ");
            query.append(" ade_codigo VARCHAR(32) NOT NULL, ");
            query.append(" periodo DATE NOT NULL, ");
            query.append(" sad_codigo VARCHAR(32) NOT NULL, ");
            query.append(" ade_numero INT(11) NOT NULL, ");
            query.append(" ade_prazo INT(11) NULL, ");
            query.append(" ade_vlr DECIMAL(13,2) NULL, ");
            query.append(" PRIMARY KEY (ade_codigo, periodo), ");
            query.append(" KEY ix_ade_codigo (ade_codigo), ");
            query.append(" KEY ix_periodo (periodo), ");
            query.append(" CONSTRAINT FK_TB_DADOS_EXPORTACAO_ADE_CODIGO FOREIGN KEY (ade_codigo) REFERENCES tb_aut_desconto (ade_codigo), ");
            query.append(" CONSTRAINT FK_TB_DADOS_EXPORTACAO_SAD_CODIGO FOREIGN KEY (sad_codigo) REFERENCES tb_status_autorizacao_desconto (sad_codigo) ");
            query.append(") ENGINE=InnoDB DEFAULT CHARSET=latin1 ; ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

	@Override
    public void preGeraArqLote(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        super.preGeraArqLote(parametrosExportacao, responsavel);
		try {
			LOG.debug("inicio - SEG.updadeDadValor24 : " + DateHelper.getSystemDatetime());
			updateDadValor24();
			LOG.debug("fim - SEG.updadeDadValor24 : " + DateHelper.getSystemDatetime());
			// atualiza prazo folha
			LOG.debug("inicio - SEG.updadeDadValor24 : " + DateHelper.getSystemDatetime());
			updatePrazoFolha();
			LOG.debug("fim - SEG.updadeDadValor24 : " + DateHelper.getSystemDatetime());
		} catch (ExportaMovimentoException ex) {
			LOG.error(ex.getMessage(), ex);
			throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
		}
	}

	@Override
    public void posProcessaAutorizacoes(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        List<String> orgCodigos = parametrosExportacao.getOrgCodigos();
        List<String> estCodigos = parametrosExportacao.getEstCodigos();

        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try {
            // Obtém o período atual
            PeriodoDelegate perDelegate = new PeriodoDelegate();
            TransferObject periodoExportacao = perDelegate.obtemPeriodoExportacaoDistinto(orgCodigos, estCodigos, responsavel);
            Date periodoAtual = (java.util.Date) periodoExportacao.getAttribute(Columns.PEX_PERIODO);
            String periodo = DateHelper.format(periodoAtual, "yyyy-MM-dd");
            LOG.debug("periodoAtual=" + periodo);

            // Salva dados do contrato no período de exportação para serem usados como referência em exportações futuras
            StringBuilder query = new StringBuilder();
            query.append("INSERT IGNORE INTO tb_dados_exportacao (ade_codigo, periodo, sad_codigo, ade_numero, ade_prazo, ade_vlr) ");
            query.append("SELECT DISTINCT ade.ade_codigo, prd.prd_data_desconto, ade.sad_codigo, ade.ade_numero, ade.ade_prazo, ade.ade_vlr ");
            query.append("FROM tb_aut_desconto ade ");
            query.append("JOIN tb_parcela_desconto_periodo prd ON (ade.ade_codigo = prd.ade_codigo) ");
            query.append("WHERE prd.spd_codigo = '").append(CodedValues.SPD_EMPROCESSAMENTO).append("' ");
            query.append("AND prd.prd_data_desconto = '").append(periodo).append("' ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        } catch (PeriodoException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

	private void updateDadValor24() throws ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
		try {
			String query = "update tb_tmp_exportacao_ordenada set dad_valor_24 = 'S' where ade_numero in ("
					+ "select ade.ade_numero "
			        + "from tb_aut_desconto ade "
					+ "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
					+ "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
					+ "inner join tb_servico svc on (svc.svc_codigo = cnv.svc_codigo) "
					+ "inner join tb_orgao org on (org.org_codigo = cnv.org_codigo) "
					+ "inner join tb_periodo_exportacao pex on (pex.org_codigo = org.org_codigo) "
					+ "inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) "
					+ "left outer join tb_ocorrencia_autorizacao oca on (ade.ade_codigo = oca.ade_codigo and oca.toc_codigo = '"
					+ CodedValues.TOC_RELANCAMENTO
					+ "' and oca.oca_data between pex.pex_data_ini and pex.pex_data_fim) "
					// seleciona contratos com ocorrência de relançamento para o período
					+ "where oca.oca_codigo is not null"
					// não seleciona contratos de servidores bloquados ou excluídos
					+ " and rse.srs_codigo = '" + CodedValues.SRS_ATIVO + "' "
					+ " group by ade.ade_numero)";
			LOG.debug(query);
			jdbc.update(query, queryParams);

		} catch (final DataAccessException ex) {
			LOG.error(ex.getMessage(), ex);
			throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
		}
	}

    private void updatePrazoFolha() throws ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try {
            final StringBuilder query = new StringBuilder();
            // exclui registros com período menor que o período anterior que já não são mais necessários
            query.setLength(0);
            query.append("DELETE dad.* FROM tb_dados_exportacao dad ");
            query.append("JOIN tb_aut_desconto ade ON (dad.ade_codigo = ade.ade_codigo) ");
            query.append("JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
            query.append("JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("JOIN tb_orgao org ON (org.org_codigo = cnv.org_codigo) ");
            query.append("JOIN tb_periodo_exportacao pex ON (pex.org_codigo = org.org_codigo) ");
            query.append("WHERE dad.periodo < pex.pex_periodo_ant ");
            LOG.debug(query);
            jdbc.update(query.toString(), queryParams);

            // atualiza o prazo folha da tabela de exportação
            query.setLength(0);
            query.append("UPDATE tb_tmp_exportacao_ordenada tmp ");
            query.append("JOIN tb_aut_desconto ade ON (ade.ade_numero = tmp.ade_numero) ");
            query.append("JOIN tb_verba_convenio vco ON (vco.vco_codigo = ade.vco_codigo) ");
            query.append("JOIN tb_convenio cnv ON (cnv.cnv_codigo = vco.cnv_codigo) ");
            query.append("JOIN tb_orgao org ON (org.org_codigo = cnv.org_codigo) ");
            query.append("JOIN tb_periodo_exportacao pex ON (pex.org_codigo = org.org_codigo) ");
            query.append("JOIN tb_dados_exportacao dad ON (dad.ade_codigo = ade.ade_codigo) ");
            query.append("SET tmp.ade_prazo_folha = dad.ade_prazo ");
            query.append("WHERE dad.periodo = pex.pex_periodo_ant ");
            query.append("AND dad.ade_prazo IS NOT NULL ");
            LOG.debug(query);
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
