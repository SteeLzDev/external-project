package com.zetra.econsig.folha.exportacao.impl;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ModuloBeneficioSaude</p>
 * <p>Description: Implementações específicas para os sistemas que tem o módulo habilitado.</p>
 * <p>Copyright: Copyright (c) 2002-2016</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 24907 $
 * $Date: 2020-09-25 10:34:33 -0300 (Sex, 06 jul 2018) $
 */
public class ModuloBeneficioSaude extends ExportaMovimentoBase {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ModuloBeneficioSaude.class);

    @Override
    public void posCriacaoTabelas(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        super.posCriacaoTabelas(parametrosExportacao, responsavel);

        if (!ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_BENEFICIOS_SAUDE, CodedValues.TPC_SIM, responsavel)) {
            throw new ExportaMovimentoException("mensagem.erro.modulo.beneficio.saude.param.desabilitado", (AcessoSistema) null);
        }

        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder query = new StringBuilder();
        try {
            // Adiciona colunas na tabela tb_tmp_exportacao_ordenada para os campos do módulo de saúde
            query.append("ALTER TABLE tb_tmp_exportacao_ordenada");
            query.append(" ADD COLUMN cbe_numero varchar(40),");
            query.append(" ADD COLUMN bfc_cpf varchar(19),");
            query.append(" ADD COLUMN ben_codigo_contrato varchar(40),");
            query.append(" ADD COLUMN tib_descricao varchar(40),");
            query.append(" ADD COLUMN bfc_nome varchar(255),");
            query.append(" ADD COLUMN bfc_data_nascimento date,");
            query.append(" ADD COLUMN bfc_sexo char(1),");
            query.append(" ADD COLUMN grp_descricao varchar(40)");

            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void preGeraArqLote(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder query = new StringBuilder();
        try {
            // Seta os valores do contrato de beneficio na tabela de exportação ordenada
            query.append("UPDATE tb_tmp_exportacao_ordenada tmp ");
            query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_numero = tmp.ade_numero) ");
            query.append("INNER JOIN tb_contrato_beneficio cbe ON (cbe.cbe_codigo = ade.cbe_codigo) ");
            query.append("INNER JOIN tb_beneficiario bfc ON (bfc.bfc_codigo = cbe.bfc_codigo) ");
            query.append("INNER JOIN tb_tipo_beneficiario tib ON (tib.tib_codigo = bfc.tib_codigo) ");
            query.append("LEFT JOIN tb_grau_parentesco grp ON (grp.grp_codigo = bfc.grp_codigo) ");
            query.append("INNER JOIN tb_beneficio ben ON (ben.ben_codigo = cbe.ben_codigo) ");
            query.append("SET tmp.cbe_numero= cbe.cbe_numero, ");
            query.append("tmp.bfc_cpf= bfc.bfc_cpf, ");
            query.append("tmp.ben_codigo_contrato = ben.ben_codigo_contrato, ");
            query.append("tmp.tib_descricao = tib.tib_descricao, ");
            query.append("tmp.bfc_nome = bfc.bfc_nome, ");
            query.append("tmp.bfc_data_nascimento = bfc.bfc_data_nascimento, ");
            query.append("tmp.bfc_sexo = bfc.bfc_sexo, ");
            query.append("tmp.grp_descricao = grp.grp_descricao");

            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}