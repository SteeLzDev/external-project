package com.zetra.econsig.persistence.dao.oracle;

import java.util.Date;
import java.util.List;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.ValidacaoFaturamentoBeneficioDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.persistence.entity.FaturamentoBeneficio;
import com.zetra.econsig.persistence.entity.FaturamentoBeneficioHome;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.TipoBeneficiarioEnum;

/**
 * <p>Title: OracleValidacaoFaturamentoBeneficioDAO</p>
 * <p>Description: Implementação para Oracle do DAO de ValidacaoFaturamentoBeneficioDAO</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OracleValidacaoFaturamentoBeneficioDAO implements ValidacaoFaturamentoBeneficioDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OracleValidacaoFaturamentoBeneficioDAO.class);

    @Override
    public void apagarPreviaFaturamentoOperadora(String fatCodigo) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            int rows = 0;
            final StringBuilder query = new StringBuilder();

            final FaturamentoBeneficio fat = FaturamentoBeneficioHome.findByPrimaryKey(fatCodigo);
            final String csaCodigo = fat.getCsaCodigo();
            final Date fatPeriodo = fat.getFatPeriodo();

            queryParams.addValue("csaCodigo", csaCodigo);
            queryParams.addValue("fatPeriodo", fatPeriodo);

            // Apaga os dados da tabela de prévia do período de faturamento informado
            query.append("DELETE FROM tb_arquivo_previa_operadora ");
            query.append("WHERE csa_codigo = :csaCodigo ");
            query.append("AND apo_periodo_faturamento = :fatPeriodo ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void copiarCamposLoteFaturamentoBeneficio(String fatCodigo) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("fatCodigo", fatCodigo);
        try {
            int rows = 0;
            final StringBuilder query = new StringBuilder();

            // Atualiza a tabela de arquivo de faturamento de benefício com os campos de lote de acordo com a prévia
            query.append("UPDATE tb_arquivo_faturamento_ben ");
            query.append("JOIN tb_faturamento_beneficio ON (tb_arquivo_faturamento_ben.fat_codigo = tb_faturamento_beneficio.fat_codigo) ");
            query.append("JOIN tb_arquivo_previa_operadora ON (tb_arquivo_previa_operadora.csa_codigo = tb_faturamento_beneficio.csa_codigo) ");
            query.append("SET tb_arquivo_faturamento_ben.afb_numero_lote = tb_arquivo_previa_operadora.apo_numero_lote ");
            query.append(", tb_arquivo_faturamento_ben.afb_item_lote = tb_arquivo_previa_operadora.apo_item_lote ");
            query.append("WHERE tb_faturamento_beneficio.fat_codigo = :fatCodigo ");
            query.append("AND tb_arquivo_previa_operadora.csa_codigo = tb_faturamento_beneficio.csa_codigo ");
            query.append("AND tb_arquivo_previa_operadora.apo_periodo_faturamento = tb_faturamento_beneficio.fat_periodo ");
            query.append("AND tb_arquivo_faturamento_ben.cbe_numero = tb_arquivo_previa_operadora.cbe_numero ");
            query.append("AND tb_arquivo_faturamento_ben.tla_codigo = tb_arquivo_previa_operadora.apo_tipo_lancamento ");
            LOG.trace(query.toString());
            rows = jdbc.update(query.toString(), queryParams);
            LOG.trace("Linhas afetadas: " + rows);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public List<TransferObject> validarFaturamentoBeneficio(String fatCodigo, boolean validarPrevia) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("fatCodigo", fatCodigo);
        try {
            // Parametros de sistema
            final String qtdMaxSubsidiosPorDependente = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MAXIMO_SUBSIDIO_POR_DEPENDENTE, AcessoSistema.getAcessoUsuarioSistema());
            final String qtdMaxSubsidiosPorTitular = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MAXIMO_SUBSIDIO_POR_TITULAR, AcessoSistema.getAcessoUsuarioSistema());
            final boolean validarSalarioZerado = ParamSist.paramEquals(CodedValues.TPC_VALIDAR_REGRA_SALARIO_FATURAMENTO_BENEFICIO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

            final StringBuilder query = new StringBuilder();

            final FaturamentoBeneficio fat = FaturamentoBeneficioHome.findByPrimaryKey(fatCodigo);
            final String csaCodigo = fat.getCsaCodigo().toString();
            final Date fatPeriodo = fat.getFatPeriodo();

            queryParams.addValue("csaCodigo", csaCodigo);
            queryParams.addValue("fatPeriodo", fatPeriodo);

            // Cria tabela temporária para geração do arquivo de críticas
            query.setLength(0);
            query.append("drop temporary table if exists tmp_validacao_faturamento_beneficio");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("create temporary table tmp_validacao_faturamento_beneficio (");
            query.append("csa_identificador varchar(40), ");
            query.append("csa_nome varchar(100), ");
            query.append("fat_periodo date, ");
            query.append("rse_matricula varchar(20), ");
            query.append("cbe_numero varchar(40), ");
            query.append("tla_codigo varchar(32), ");
            query.append("valor_debito decimal(13,2), ");
            query.append("motivo text, ");
            query.append("id_validacao int auto_increment, primary key (id_validacao), key idxvalidacao (id_validacao)) ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Executa regras de validação
            // Contratos presentes no faturamento e ausentes na prévia.
            if (validarPrevia) {
                query.setLength(0);
                query.append("INSERT INTO tmp_validacao_faturamento_beneficio ");
                query.append("SELECT csa.csa_identificador, csa.csa_nome, fat.fat_periodo, afb.rse_matricula, afb.cbe_numero, afb.tla_codigo, afb.prd_vlr_previsto ");
                query.append(", 'Contratos presentes no faturamento e ausentes na prévia' ");
                query.append("FROM tb_arquivo_faturamento_ben afb ");
                query.append("JOIN tb_faturamento_beneficio fat ON (afb.fat_codigo = fat.fat_codigo) ");
                query.append("JOIN tb_consignataria csa ON (fat.csa_codigo = csa.csa_codigo) ");
                query.append("WHERE fat.fat_codigo = :fatCodigo ");
                query.append("AND NOT EXISTS ( ");
                query.append("SELECT 1  ");
                query.append("FROM tb_arquivo_previa_operadora apo ");
                query.append("WHERE apo.csa_codigo = fat.csa_codigo ");
                query.append("AND apo.apo_periodo_faturamento = fat.fat_periodo ");
                query.append("AND afb.cbe_numero = apo.cbe_numero ");
                query.append("AND afb.tla_codigo = apo.apo_tipo_lancamento) ");
                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);
            }

            // Contratos com valores divergentes entre o faturamento e a prévia.
            if (validarPrevia) {
                query.setLength(0);
                query.append("INSERT INTO tmp_validacao_faturamento_beneficio ");
                query.append("SELECT csa.csa_identificador, csa.csa_nome, fat.fat_periodo, afb.rse_matricula, afb.cbe_numero, afb.tla_codigo, afb.prd_vlr_previsto ");
                query.append(", 'Contratos com valores divergentes entre o faturamento e a prévia.' ");
                query.append("FROM tb_arquivo_faturamento_ben afb ");
                query.append("JOIN tb_faturamento_beneficio fat ON (afb.fat_codigo = fat.fat_codigo) ");
                query.append("JOIN tb_consignataria csa ON (fat.csa_codigo = csa.csa_codigo) ");
                query.append("JOIN tb_arquivo_previa_operadora apo ON (fat.csa_codigo = apo.csa_codigo) ");
                query.append("WHERE fat.fat_codigo = :fatCodigo ");
                query.append("AND apo.csa_codigo = fat.csa_codigo ");
                query.append("AND apo.apo_periodo_faturamento = fat.fat_periodo ");
                query.append("AND afb.cbe_numero = apo.cbe_numero ");
                query.append("AND afb.tla_codigo = apo.apo_tipo_lancamento ");
                query.append("AND afb.afb_valor_total != apo_valor_total ");
                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);
            }

            // Desconto de mensalidade do beneficiário duplicado para o mesmo benefício.
            query.setLength(0);
            query.append("INSERT INTO tmp_validacao_faturamento_beneficio ");
            query.append("SELECT csa.csa_identificador, csa.csa_nome, fat.fat_periodo, afb.rse_matricula, afb.cbe_numero, afb.tla_codigo, afb.prd_vlr_previsto ");
            query.append(", 'Desconto de mensalidade do beneficiário duplicado para o mesmo benefício.' ");
            query.append("FROM tb_arquivo_faturamento_ben afb ");
            query.append("JOIN tb_faturamento_beneficio fat ON (afb.fat_codigo = fat.fat_codigo) ");
            query.append("JOIN tb_consignataria csa ON (fat.csa_codigo = csa.csa_codigo) ");
            query.append("JOIN ( ");
            query.append("SELECT COUNT(*) AS qtde, afb.cbe_numero, afb.tla_codigo ");
            query.append("FROM tb_arquivo_faturamento_ben afb2 ");
            query.append("JOIN tb_tipo_lancamento tla ON (afb2.tla_codigo = tla.tla_codigo) ");
            query.append("JOIN tb_tipo_natureza tnt ON (tla.tnt_codigo = tnt.tnt_codigo AND tnt.tnt_codigo IN ('").append(TextHelper.join(CodedValues.TNT_BENEFICIO_MENSALIDADE, "','")).append("')) ");
            query.append("JOIN tb_faturamento_beneficio fat2 ON (afb2.fat_codigo = fat2.fat_codigo) ");
            query.append("WHERE fat2.fat_codigo = :fatCodigo ");
            query.append("GROUP BY afb2.cbe_numero, afb2.tla_codigo ");
            query.append("HAVING COUNT(*) > 1 ");
            query.append(") t ON (t.cbe_numero = afb.cbe_numero and t.tla_codigo = afb.tla_codigo) ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Desconto integral pela folha de contrato que possui subsídio.
            query.setLength(0);
            query.append("INSERT INTO tmp_validacao_faturamento_beneficio ");
            query.append("SELECT csa.csa_identificador, csa.csa_nome, fat.fat_periodo, afb.rse_matricula, afb.cbe_numero, afb.tla_codigo, afb.prd_vlr_previsto ");
            query.append(", 'Desconto integral pela folha de contrato que possui subsídio.' ");
            query.append("FROM tb_arquivo_faturamento_ben afb ");
            query.append("JOIN tb_faturamento_beneficio fat ON (afb.fat_codigo = fat.fat_codigo) ");
            query.append("JOIN tb_consignataria csa ON (fat.csa_codigo = csa.csa_codigo) ");
            query.append("WHERE fat.fat_codigo = :fatCodigo ");
            query.append("AND afb.afb_valor_realizado = afb.afb_valor_total ");
            query.append("AND COALESCE(afb.afb_valor_subsidio,0) > 0 ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Soma do valor desconto e valor do subsídio maior que o valor do benefício.
            query.setLength(0);
            query.append("INSERT INTO tmp_validacao_faturamento_beneficio ");
            query.append("SELECT csa.csa_identificador, csa.csa_nome, fat.fat_periodo, afb.rse_matricula, afb.cbe_numero, afb.tla_codigo, afb.prd_vlr_previsto ");
            query.append(", 'Soma do valor desconto e valor do subsídio maior que o valor do benefício.' ");
            query.append("FROM tb_arquivo_faturamento_ben afb ");
            query.append("JOIN tb_faturamento_beneficio fat ON (afb.fat_codigo = fat.fat_codigo) ");
            query.append("JOIN tb_consignataria csa ON (fat.csa_codigo = csa.csa_codigo) ");
            query.append("WHERE fat.fat_codigo = :fatCodigo ");
            query.append("AND (afb.afb_valor_realizado + COALESCE(afb.afb_valor_subsidio,0)) > afb.afb_valor_total ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Servidores com salário zerado que tiveram desconto.
            if (validarSalarioZerado) {
                query.setLength(0);
                query.append("INSERT INTO tmp_validacao_faturamento_beneficio ");
                query.append("SELECT csa.csa_identificador, csa.csa_nome, fat.fat_periodo, afb.rse_matricula, afb.cbe_numero, afb.tla_codigo, afb.prd_vlr_previsto ");
                query.append(", 'Servidores com salário zerado que tiveram desconto.' ");
                query.append("FROM tb_arquivo_faturamento_ben afb ");
                query.append("JOIN tb_faturamento_beneficio fat ON (afb.fat_codigo = fat.fat_codigo) ");
                query.append("JOIN tb_consignataria csa ON (fat.csa_codigo = csa.csa_codigo) ");
                query.append("JOIN tb_aut_desconto ade ON (afb.ade_codigo = ade.ade_codigo) ");
                query.append("JOIN tb_registro_servidor rse ON (ade.rse_codigo = rse.rse_codigo) ");
                query.append("WHERE fat.fat_codigo = :fatCodigo ");
                query.append("AND COALESCE(afb.afb_valor_realizado,0) > 0 ");
                query.append("AND (rse.rse_salario = 0 OR rse.rse_salario IS NULL) ");
                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);
            }

            // Contratos presentes na prévia e ausentes no faturamento.
            if (validarPrevia) {
                query.setLength(0);
                query.append("INSERT INTO tmp_validacao_faturamento_beneficio ");
                query.append("SELECT csa.csa_identificador, csa.csa_nome, apo.apo_periodo_faturamento, apo.rse_matricula, apo.cbe_numero, apo.apo_tipo_lancamento, apo.apo_valor_debito ");
                query.append(", 'Contratos presentes na prévia e ausentes no faturamento.' ");
                query.append("FROM tb_arquivo_previa_operadora apo ");
                query.append(", tb_consignataria csa ");
                query.append("WHERE csa.csa_codigo = :csaCodigo ");
                query.append("and apo.csa_codigo = :csaCodigo ");
                query.append("AND apo.apo_periodo_faturamento = :fatPeriodo ");
                query.append("AND NOT EXISTS ( ");
                query.append("SELECT 1 ");
                query.append("FROM tb_arquivo_faturamento_ben afb ");
                query.append("JOIN tb_faturamento_beneficio fat ON (afb.fat_codigo = fat.fat_codigo) ");
                query.append("WHERE fat.fat_codigo = :fatCodigo ");
                query.append("AND fat.fat_periodo = apo.apo_periodo_faturamento ");
                query.append("AND afb.cbe_numero = apo.cbe_numero ");
                query.append("AND afb.tla_codigo = apo.apo_tipo_lancamento) ");
                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);
            }

            // Verificar se o beneficiário teve subsídio em mais benefícios que o limite permitido (verificar parâmetro).
            // TITULAR
            query.setLength(0);
            query.append("INSERT INTO tmp_validacao_faturamento_beneficio ");
            query.append("SELECT csa.csa_identificador, csa.csa_nome, fat2.fat_periodo, afb2.rse_matricula, afb2.cbe_numero, afb2.tla_codigo, afb2.prd_vlr_previsto ");
            query.append(", 'Beneficiário teve subsídio em mais benefícios que o limite permitido.' ");
            query.append("FROM tb_arquivo_faturamento_ben afb2 ");
            query.append("JOIN tb_tipo_lancamento tla2 ON (afb2.tla_codigo = tla2.tla_codigo) ");
            query.append("JOIN tb_tipo_natureza tnt2 ON (tla2.tnt_codigo = tnt2.tnt_codigo AND tnt2.tnt_codigo IN ('").append(TextHelper.join(CodedValues.TNT_BENEFICIO_MENSALIDADE, "','")).append("')) ");
            query.append("JOIN tb_faturamento_beneficio fat2 ON (afb2.fat_codigo = fat2.fat_codigo) ");
            query.append("JOIN tb_consignataria csa ON (fat2.csa_codigo = csa.csa_codigo) ");
            query.append("JOIN ( ");
            query.append("SELECT COUNT(*) AS qtde, afb.bfc_cpf, afb.fat_codigo ");
            query.append("FROM tb_arquivo_faturamento_ben afb ");
            query.append("JOIN tb_aut_desconto ade ON (afb.ade_codigo = ade.ade_codigo) ");
            query.append("JOIN tb_contrato_beneficio cbe ON (ade.cbe_codigo = cbe.cbe_codigo) ");
            query.append("JOIN tb_beneficiario bfc ON (cbe.bfc_codigo = bfc.bfc_codigo) ");
            query.append("JOIN tb_tipo_lancamento tla ON (afb.tla_codigo = tla.tla_codigo) ");
            query.append("JOIN tb_tipo_natureza tnt ON (tla.tnt_codigo = tnt.tnt_codigo AND tnt.tnt_codigo IN ('").append(TextHelper.join(CodedValues.TNT_BENEFICIO_MENSALIDADE, "','")).append("')) ");
            query.append("JOIN tb_faturamento_beneficio fat ON (afb.fat_codigo = fat.fat_codigo) ");
            query.append("WHERE fat.fat_codigo = :fatCodigo ");
            query.append("AND COALESCE(afb.afb_valor_subsidio,0) > 0 ");
            query.append("AND bfc.tib_codigo = '").append(TipoBeneficiarioEnum.TITULAR.tibCodigo).append("' ");
            query.append("GROUP BY afb.bfc_cpf, afb.fat_codigo ");
            query.append("HAVING COUNT(*) > :qtdMaxSubsidiosPorTitular ");
            query.append(") t ON (t.fat_codigo = afb2.fat_codigo AND t.bfc_cpf = afb2.bfc_cpf) ");
            LOG.trace(query.toString());
            queryParams.addValue("qtdMaxSubsidiosPorTitular", qtdMaxSubsidiosPorTitular);
            jdbc.update(query.toString(), queryParams);

            // DEPENDENTE/AGREGADO
            query.setLength(0);
            query.append("INSERT INTO tmp_validacao_faturamento_beneficio ");
            query.append("SELECT csa.csa_identificador, csa.csa_nome, fat2.fat_periodo, afb2.rse_matricula, afb2.cbe_numero, afb2.tla_codigo, afb2.prd_vlr_previsto ");
            query.append(", 'Beneficiário teve subsídio em mais benefícios que o limite permitido.' ");
            query.append("FROM tb_arquivo_faturamento_ben afb2 ");
            query.append("JOIN tb_tipo_lancamento tla2 ON (afb2.tla_codigo = tla2.tla_codigo) ");
            query.append("JOIN tb_tipo_natureza tnt2 ON (tla2.tnt_codigo = tnt2.tnt_codigo AND tnt2.tnt_codigo IN ('").append(TextHelper.join(CodedValues.TNT_BENEFICIO_MENSALIDADE, "','")).append("')) ");
            query.append("JOIN tb_faturamento_beneficio fat2 ON (afb2.fat_codigo = fat2.fat_codigo) ");
            query.append("JOIN tb_consignataria csa ON (fat2.csa_codigo = csa.csa_codigo) ");
            query.append("JOIN ( ");
            query.append("SELECT COUNT(*) AS qtde, afb.bfc_cpf, afb.fat_codigo ");
            query.append("FROM tb_arquivo_faturamento_ben afb ");
            query.append("JOIN tb_aut_desconto ade ON (afb.ade_codigo = ade.ade_codigo) ");
            query.append("JOIN tb_contrato_beneficio cbe ON (ade.cbe_codigo = cbe.cbe_codigo) ");
            query.append("JOIN tb_beneficiario bfc ON (cbe.bfc_codigo = bfc.bfc_codigo) ");
            query.append("JOIN tb_tipo_lancamento tla ON (afb.tla_codigo = tla.tla_codigo) ");
            query.append("JOIN tb_tipo_natureza tnt ON (tla.tnt_codigo = tnt.tnt_codigo AND tnt.tnt_codigo IN ('").append(TextHelper.join(CodedValues.TNT_BENEFICIO_MENSALIDADE, "','")).append("')) ");
            query.append("JOIN tb_faturamento_beneficio fat ON (afb.fat_codigo = fat.fat_codigo) ");
            query.append("WHERE fat.fat_codigo = :fatCodigo ");
            query.append("AND COALESCE(afb.afb_valor_subsidio,0) > 0 ");
            query.append("AND bfc.tib_codigo IN ('").append(TipoBeneficiarioEnum.DEPENDENTE.tibCodigo).append("','").append(TipoBeneficiarioEnum.AGREGADO.tibCodigo).append("') ");
            query.append("GROUP BY afb.bfc_cpf, afb.fat_codigo ");
            query.append("HAVING COUNT(*) > :qtdMaxSubsidiosPorDependente ");
            query.append(") t ON (t.fat_codigo = afb2.fat_codigo AND t.bfc_cpf = afb2.bfc_cpf) ");
            LOG.trace(query.toString());
            queryParams.addValue("qtdMaxSubsidiosPorDependente", qtdMaxSubsidiosPorDependente);
            jdbc.update(query.toString(), queryParams);

            // Verificar se o beneficiário do tipo agregado teve subsídio sem ter direito (verificar parâmetro).
            query.setLength(0);
            query.append("INSERT INTO tmp_validacao_faturamento_beneficio ");
            query.append("SELECT csa.csa_identificador, csa.csa_nome, fat.fat_periodo, afb.rse_matricula, afb.cbe_numero, afb.tla_codigo, afb.prd_vlr_previsto ");
            query.append(", 'Beneficiário do tipo agregado teve subsídio sem ter direito.' ");
            query.append("FROM tb_arquivo_faturamento_ben afb ");
            query.append("JOIN tb_aut_desconto ade ON (afb.ade_codigo = ade.ade_codigo) ");
            query.append("JOIN tb_verba_convenio vco ON (ade.vco_codigo = vco.vco_codigo) ");
            query.append("JOIN tb_convenio cnv ON (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("JOIN tb_servico svc ON (cnv.svc_codigo = svc.svc_codigo) ");
            query.append("JOIN tb_contrato_beneficio cbe ON (ade.cbe_codigo = cbe.cbe_codigo) ");
            query.append("JOIN tb_param_svc_consignante pse ON (svc.svc_codigo = pse.svc_codigo AND pse.tps_codigo = '").append(CodedValues.TPS_AGREGADO_PODE_TER_SUBSIDIO).append("' AND pse.pse_vlr = '1') ");
            query.append("JOIN tb_beneficiario bfc ON (cbe.bfc_codigo = bfc.bfc_codigo) ");
            query.append("JOIN tb_tipo_lancamento tla ON (afb.tla_codigo = tla.tla_codigo) ");
            query.append("JOIN tb_tipo_natureza tnt ON (tla.tnt_codigo = tnt.tnt_codigo AND tnt.tnt_codigo IN ('").append(TextHelper.join(CodedValues.TNT_BENEFICIO_MENSALIDADE, "','")).append("')) ");
            query.append("JOIN tb_faturamento_beneficio fat ON (afb.fat_codigo = fat.fat_codigo) ");
            query.append("JOIN tb_consignataria csa ON (fat.csa_codigo = csa.csa_codigo) ");
            query.append("WHERE fat.fat_codigo = :fatCodigo ");
            query.append("AND COALESCE(afb.afb_valor_subsidio,0) > 0 ");
            query.append("AND bfc.tib_codigo = '").append(TipoBeneficiarioEnum.AGREGADO.tibCodigo).append("' ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Desconto de copart do beneficiário duplicado para o mesmo benefício.
            query.setLength(0);
            query.append("INSERT INTO tmp_validacao_faturamento_beneficio ");
            query.append("SELECT csa.csa_identificador, csa.csa_nome, fat2.fat_periodo, afb2.rse_matricula, afb2.cbe_numero, afb2.tla_codigo, afb2.prd_vlr_previsto ");
            query.append(", 'Desconto de copart do beneficiário duplicado para o mesmo benefício.' ");
            query.append("FROM tb_arquivo_faturamento_ben afb2 ");
            query.append("JOIN tb_tipo_lancamento tla2 ON (afb2.tla_codigo = tla2.tla_codigo) ");
            query.append("JOIN tb_tipo_natureza tnt2 ON (tla2.tnt_codigo = tnt2.tnt_codigo AND tnt2.tnt_codigo = '").append(CodedValues.TNT_COPART).append("') ");
            query.append("JOIN tb_faturamento_beneficio fat2 ON (afb2.fat_codigo = fat2.fat_codigo) ");
            query.append("JOIN tb_consignataria csa ON (fat2.csa_codigo = csa.csa_codigo) ");
            query.append("JOIN ( ");
            query.append("SELECT COUNT(*) AS qtde, afb.bfc_cpf, afb.fat_codigo ");
            query.append("FROM tb_arquivo_faturamento_ben afb ");
            query.append("JOIN tb_aut_desconto ade ON (afb.ade_codigo = ade.ade_codigo) ");
            query.append("JOIN tb_contrato_beneficio cbe ON (ade.cbe_codigo = cbe.cbe_codigo) ");
            query.append("JOIN tb_tipo_lancamento tla ON (afb.tla_codigo = tla.tla_codigo) ");
            query.append("JOIN tb_tipo_natureza tnt ON (tla.tnt_codigo = tnt.tnt_codigo AND tnt.tnt_codigo = '").append(CodedValues.TNT_COPART).append("') ");
            query.append("JOIN tb_faturamento_beneficio fat ON (afb.fat_codigo = fat.fat_codigo) ");
            query.append("WHERE fat.fat_codigo = :fatCodigo ");
            query.append("GROUP BY afb.bfc_cpf, afb.fat_codigo ");
            query.append("HAVING COUNT(*) > 1 ");
            query.append(") t ON (t.fat_codigo = afb2.fat_codigo AND t.bfc_cpf = afb2.bfc_cpf) ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Contratos cancelados com subsídio ativo.
            query.setLength(0);
            query.append("INSERT INTO tmp_validacao_faturamento_beneficio ");
            query.append("SELECT csa.csa_identificador, csa.csa_nome, fat.fat_periodo, afb.rse_matricula, afb.cbe_numero, afb.tla_codigo, afb.prd_vlr_previsto ");
            query.append(", 'Contratos cancelados com subsídio ativo.' ");
            query.append("FROM tb_arquivo_faturamento_ben afb ");
            query.append("JOIN tb_aut_desconto ade ON (afb.ade_codigo = ade.ade_codigo) ");
            query.append("JOIN tb_relacionamento_autorizacao rel ON (ade.ade_codigo = rel.ade_codigo_origem AND rel.tnt_codigo IN ('").append(TextHelper.join(CodedValues.TNT_BENEFICIO_SUBSIDIO, "','")).append("')) ");
            query.append("JOIN tb_aut_desconto adeSub ON (adeSub.ade_codigo = rel.ade_codigo_destino) ");
            query.append("JOIN tb_faturamento_beneficio fat ON (afb.fat_codigo = fat.fat_codigo) ");
            query.append("JOIN tb_consignataria csa ON (fat.csa_codigo = csa.csa_codigo) ");
            query.append("WHERE fat.fat_codigo = :fatCodigo ");
            query.append("AND ade.sad_codigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("') ");
            query.append("AND adeSub.sad_codigo NOT IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_INATIVOS, "','")).append("') ");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Contratos com dados de identificação divergentes entre a prévia e o faturamento (Carteirinha, CPF e tipo de débito).
            // Não tem CPF na prévia, a carteirinha é chave na pesquisa, então podemos validar apenas o tipo de débito
            if (validarPrevia) {
                query.setLength(0);
                query.append("INSERT INTO tmp_validacao_faturamento_beneficio ");
                query.append("SELECT csa.csa_identificador, csa.csa_nome, fat.fat_periodo, afb.rse_matricula, afb.cbe_numero, afb.tla_codigo, afb.prd_vlr_previsto ");
                query.append(", 'Desconto de copart do beneficiário duplicado para o mesmo benefício.' ");
                query.append("FROM tb_arquivo_faturamento_ben afb ");
                query.append("JOIN tb_faturamento_beneficio fat ON (afb.fat_codigo = fat.fat_codigo) ");
                query.append("JOIN tb_consignataria csa ON (fat.csa_codigo = csa.csa_codigo) ");
                query.append("JOIN tb_arquivo_previa_operadora apo ON (fat.csa_codigo = apo.csa_codigo) ");
                query.append("WHERE fat.fat_codigo = :fatCodigo ");
                query.append("AND apo.csa_codigo = fat.csa_codigo ");
                query.append("AND apo.apo_periodo_faturamento = fat.fat_periodo ");
                query.append("AND afb.cbe_numero = apo.cbe_numero ");
                query.append("AND afb.afb_valor_total = apo_valor_total ");
                query.append("AND afb.tla_codigo != apo.apo_tipo_lancamento ");
                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);
            }

            // Gera o resultado para o arquivo de críticas a partir da tabela temporária
            String fields = "csa_identificador,csa_nome,fat_periodo,rse_matricula,cbe_numero,tla_codigo,valor_debito,motivo";
            query.setLength(0);
            query.append("SELECT csa_identificador ");
            query.append(",csa_nome ");
            query.append(",fat_periodo ");
            query.append(",rse_matricula ");
            query.append(",cbe_numero ");
            query.append(",tla_codigo ");
            query.append(",valor_debito ");
            query.append(",motivo ");
            query.append("FROM tmp_validacao_faturamento_beneficio ");
            query.append("ORDER BY id_validacao ");
            LOG.trace(query.toString());
            List<TransferObject> resultado = MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fields, MySqlDAOFactory.SEPARADOR);

            // Exclui tabela temporária
            query.setLength(0);
            query.append("drop temporary table if exists tmp_validacao_faturamento_beneficio");
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // retorna o resultado da validação
            return resultado;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
