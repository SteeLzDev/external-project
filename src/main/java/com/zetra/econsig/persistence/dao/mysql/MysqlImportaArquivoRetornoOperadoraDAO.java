package com.zetra.econsig.persistence.dao.mysql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.ImportaArquivosBeneficioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.ImportaArquivoRetornoOperadoraDAO;
import com.zetra.econsig.values.CodedValues;


/**
 * <p>Title: MysqlImportaArquivoRetornoOperadoraDAO</p>
 * <p>Description: Mysql DAO para o importação de arquivos retorno da operadora</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MysqlImportaArquivoRetornoOperadoraDAO implements ImportaArquivoRetornoOperadoraDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MysqlImportaArquivoRetornoOperadoraDAO.class);

    private static final String TIPO_MAPEAMENTO_CONTRATO_TITULAR_NAO_ATIVO = "T"; // O contrato do titular não se encontra ativo.

    /**
     * Criar as tabelas necessarias para executar essa rotina.
     */
    @Override
    public void criarTabelaTemporariaArquivoRetorno() throws ImportaArquivosBeneficioControllerException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try {
            // Criando uma conexção com o banco de dados

            // Criando um unico statement que será usado nas proximas rotinas.
            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

            StringBuilder sql = new StringBuilder();
            sql.append("create temporary table tb_tmp_arquivo_operadora (");
            sql.append("    nome_arquivo varchar(255) NOT NULL,");
            sql.append("    id_linha int(11) NOT NULL,");
            sql.append("    operacao varchar(1),");
            sql.append("    csa_codigo varchar(32) not null,");
            sql.append("    ben_codigo_contrato varchar(40) not null,");
            sql.append("    cbe_codigo varchar(32),");
            sql.append("    cbe_numero varchar(40),");
            sql.append("    cbe_data_inicio_vigencia datetime,");
            sql.append("    cbe_data_fim_vigencia datetime,");
            sql.append("    bfc_cpf varchar(19),");
            sql.append("    mapeada char(1) NOT NULL DEFAULT 'N',");
            sql.append("    processada char(1) NOT NULL DEFAULT 'N',");
            sql.append("    linha Text NOT NULL,");
            sql.append("    KEY idx_operacao_io (operacao),");
            sql.append("    KEY idx_csa_codigo_io (csa_codigo),");
            sql.append("    KEY idx_cbe_codigo_io (cbe_codigo),");
            sql.append("    KEY idx_cbe_numero_io (cbe_numero),");
            sql.append("    KEY idx_cbe_data_inicio_vigencia_io (cbe_data_inicio_vigencia),");
            sql.append("    KEY idx_cbe_data_fim_vigencia_io (cbe_data_fim_vigencia),");
            sql.append("    KEY idx_bfc_cpf_io (bfc_cpf),");
            sql.append("    KEY idx_mapeada_codigo_io (mapeada),");
            sql.append("    KEY idx_processada_io (processada)");
            sql.append(") engine=innodb default charset=latin1;");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append("create temporary table tb_tmp_cbe_ade_mapeados (");
            sql.append("    cbe_codigo varchar(32),");
            sql.append("    ade_codigo varchar(32),");
            sql.append("    cbe_numero varchar(40),");
            sql.append("    cbe_data_inicio_vigencia datetime,");
            sql.append("    cbe_data_fim_vigencia datetime,");
            sql.append("    tmo_codigo varchar(32) DEFAULT NULL,");
            sql.append("    cbc_periodo_beneficio date,");
            sql.append("    KEY idx_cbe_codigo_io_tmp (cbe_codigo),");
            sql.append("    KEY idx_ade_codigo_io_tmp (ade_codigo),");
            sql.append("    KEY idx_cbe_numero_io_tmp (cbe_numero),");
            sql.append("    KEY idx_cbe_data_inicio_vigencia_io_tmp (cbe_data_inicio_vigencia),");
            sql.append("    KEY idx_cbe_data_fim_vigencia_io (cbe_data_fim_vigencia),");
            sql.append("    KEY idx_tmo_codigo_io_tmp (tmo_codigo),");
            sql.append("    KEY idx_cbc_periodo_io_tmp (cbc_periodo_beneficio)");
            sql.append(") engine=innodb default charset=latin1;");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append("create temporary table tb_tmp_cbe_ade_mapeados2 (");
            sql.append("    cbe_codigo varchar(32),");
            sql.append("    ade_codigo varchar(32),");
            sql.append("    cbe_numero varchar(40),");
            sql.append("    cbe_data_inicio_vigencia datetime,");
            sql.append("    cbe_data_fim_vigencia datetime,");
            sql.append("    tmo_codigo varchar(32) DEFAULT NULL,");
            sql.append("    cbc_periodo_beneficio date,");
            sql.append("    KEY idx_cbe_codigo_io_tmp (cbe_codigo),");
            sql.append("    KEY idx_ade_codigo_io_tmp (ade_codigo),");
            sql.append("    KEY idx_cbe_numero_io_tmp (cbe_numero),");
            sql.append("    KEY idx_cbe_data_inicio_vigencia_io_tmp (cbe_data_inicio_vigencia),");
            sql.append("    KEY idx_cbe_data_fim_vigencia_io (cbe_data_fim_vigencia),");
            sql.append("    KEY idx_tmo_codigo_io_tmp (tmo_codigo),");
            sql.append("    KEY idx_cbc_periodo_io_tmp (cbc_periodo_beneficio)");
            sql.append(") engine=innodb default charset=latin1;");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);
        } catch (final DataAccessException e) {
            LOG.error(e.getCause(), e);
            throw new ImportaArquivosBeneficioControllerException(e);
        } finally {


        }
    }

    /**
     * Deleta as tabelas necessarias para executar essa rotina.
     */
    @Override
    public void deletaTabelaTemporariaArquivoRetorno() throws ImportaArquivosBeneficioControllerException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try {
            // Criando uma conexção com o banco de dados

            // Criando um unico statement que será usado nas proximas rotinas.
            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

            StringBuilder sql = new StringBuilder();
            sql.append("drop temporary table if exists tb_tmp_arquivo_operadora");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append("drop temporary table if exists tb_tmp_cbe_ade_mapeados");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append("drop temporary table if exists tb_tmp_cbe_ade_mapeados2");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append("drop temporary table if exists tb_tmp_cbe_titular");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);

            sql.append("drop temporary table if exists tb_tmp_cbe_dependente_invalidos");
            LOG.info(sql);
            jdbc.update(sql.toString(), queryParams);
            sql.setLength(0);
        } catch (final DataAccessException e) {
            LOG.error(e.getCause(), e);
            throw new ImportaArquivosBeneficioControllerException(e);
        } finally {


        }
    }

    /**
     * Rotina que faz os inserts das linhas lidas para a tabela temporaria
     */
    @Override
    public void realizarInsertTabelaTemporariaArquivoRetorno(String nomeArquivo, int numeroLinha, String operacao, String csaCodigo, String benCodigoContrato, String cbeNumero, String cbeDataInicioVigencia, String cbeDataFimVigencia, String bfcCpf, String linha) throws ImportaArquivosBeneficioControllerException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        try {
            queryParams.addValue("nomeArquivo", nomeArquivo);
            queryParams.addValue("numeroLinha", numeroLinha);
            queryParams.addValue("operacao", operacao);
            queryParams.addValue("csaCodigo", csaCodigo);
            queryParams.addValue("benCodigoContrato", benCodigoContrato);
            queryParams.addValue("cbeNumero", cbeNumero);
            queryParams.addValue("cbeDataInicioVigencia", cbeDataInicioVigencia);
            queryParams.addValue("cbeDataFimVigencia", cbeDataFimVigencia);
            queryParams.addValue("bfcCpf", bfcCpf);
            queryParams.addValue("linha", linha);

            jdbc.update(SQL_INSERT_TEMP_ARQUIVO_OPERADORA, queryParams);
        } catch (final DataAccessException e) {
            LOG.error(e.getCause(), e);
            throw new ImportaArquivosBeneficioControllerException(e);
        }
    }

    // Metodos responsavel pelo o fluxo de inclusão.

    @Override
    public void realizarMapeamentoContratosBeneficioOperacaoInclusao() throws ImportaArquivosBeneficioControllerException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        List<String> tntCodigos = new ArrayList<>(CodedValues.TNT_BENEFICIO_MENSALIDADE);
        tntCodigos.addAll(CodedValues.TNT_BENEFICIO_SUBSIDIO);

        List<String> sadCodigos = Arrays.asList(CodedValues.SAD_AGUARD_CONF, CodedValues.SAD_SOLICITADO);

        try {
            // Criando uma conexção com o banco de dados

            // Criando um unico statement que será usado nas proximas rotinas.
            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

            StringBuilder sql = new StringBuilder();
            sql.append("update tb_tmp_arquivo_operadora tmp");
            sql.append(" inner join tb_beneficiario bfc on (bfc.bfc_cpf = tmp.bfc_cpf)");
            sql.append(" inner join tb_contrato_beneficio cbe on (cbe.bfc_codigo = bfc.bfc_codigo and trim(cbe.cbe_numero) = '')");
            sql.append(" inner join tb_beneficio ben on (cbe.ben_codigo = ben.ben_codigo and ben.csa_codigo = tmp.csa_codigo and ben.ben_codigo_contrato = tmp.ben_codigo_contrato)");
            sql.append(" inner join tb_aut_desconto ade on (cbe.cbe_codigo = ade.cbe_codigo)");
            sql.append(" inner join tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo)");
            sql.append(" set tmp.mapeada = 'S', tmp.cbe_codigo = cbe.cbe_codigo");
            sql.append(" where 1 = 1");
            sql.append(" and (tmp.cbe_numero is not null or trim(tmp.cbe_numero) <> '')");
            sql.append(" and ade.sad_codigo in (").append(TextHelper.sqlJoin(sadCodigos)).append(")");
            sql.append(" and cbe.scb_codigo in ('").append(CodedValues.SCB_CODIGO_AGUARDANDO_INCLUSAO_OPERADORA).append("')");
            sql.append(" and tla.tnt_codigo in (").append(TextHelper.sqlJoin(tntCodigos)).append(")");
            sql.append(" and (tmp.cbe_data_inicio_vigencia is not null or trim(tmp.cbe_data_inicio_vigencia) <> '' or tmp.cbe_data_inicio_vigencia <> '0000-00-00 00:00:00')");
            sql.append(" and tmp.operacao = 'I';");
            LOG.info(sql);
            int linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            // Desmarcando as operações que dependente esta sendo incluido mas o contrato do titular não esta ativo ou não se encontra no arquivo.
            sql.append("create temporary table tb_tmp_cbe_titular");
            sql.append(" select tmp.id_linha, tmp.cbe_codigo");
            sql.append(" from tb_tmp_arquivo_operadora tmp");
            sql.append(" inner join tb_beneficiario bfcTitular on (bfcTitular.bfc_cpf = tmp.bfc_cpf)");
            sql.append(" inner join tb_contrato_beneficio cbeTitular on (bfcTitular.bfc_codigo = cbeTitular.bfc_codigo and cbeTitular.cbe_codigo = tmp.cbe_codigo)");
            sql.append(" where 1 = 1");
            sql.append(" and bfcTitular.tib_codigo = '").append(CodedValues.TIB_TITULAR).append("'");
            sql.append(" and tmp.operacao = 'I' and tmp.mapeada = 'S'");
            LOG.info(sql);
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            // Analisar se o titular esta no arquivo e se encontra no status esperado.
            sql.append("create temporary table tb_tmp_cbe_dependente_invalidos");
            sql.append(" select tmp.id_linha");
            sql.append(" from tb_tmp_arquivo_operadora tmp");
            sql.append(" inner join tb_beneficiario bfcDependente on (bfcDependente.bfc_cpf = tmp.bfc_cpf and bfcDependente.tib_codigo <> '").append(CodedValues.TIB_TITULAR).append("')");
            sql.append(" inner join tb_contrato_beneficio cbeDependente on (bfcDependente.bfc_codigo = cbeDependente.bfc_codigo and cbeDependente.cbe_codigo = tmp.cbe_codigo)");
            sql.append(" inner join tb_beneficiario bfcTitular on (bfcTitular.ser_codigo = bfcDependente.ser_codigo and bfcTitular.tib_codigo = '1')");
            sql.append(" inner join tb_contrato_beneficio cbeTitular on (cbeTitular.bfc_codigo = bfcTitular.bfc_codigo and cbeTitular.ben_codigo = cbeDependente.ben_codigo)");
            sql.append(" inner join tb_tmp_cbe_titular tmp2 on (cbeTitular.cbe_codigo = tmp2.cbe_codigo)");
            sql.append(" where 1 = 1");
            sql.append(" and cbeTitular.scb_codigo = '").append(CodedValues.SCB_CODIGO_AGUARDANDO_INCLUSAO_OPERADORA).append("'");
            sql.append(" and tmp.operacao = 'I' and tmp.mapeada = 'S'");
            sql.append(" group by tmp.id_linha");
            LOG.info(sql);
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            sql.append("insert into tb_tmp_cbe_dependente_invalidos");
            sql.append(" select tmp.id_linha");
            sql.append(" from tb_tmp_arquivo_operadora tmp");
            sql.append(" inner join tb_beneficiario bfcDependente on (bfcDependente.bfc_cpf = tmp.bfc_cpf and bfcDependente.tib_codigo <> '").append(CodedValues.TIB_TITULAR).append("')");
            sql.append(" inner join tb_contrato_beneficio cbeDependente on (bfcDependente.bfc_codigo = cbeDependente.bfc_codigo and cbeDependente.cbe_codigo = tmp.cbe_codigo)");
            sql.append(" inner join tb_beneficiario bfcTitular on (bfcTitular.ser_codigo = bfcDependente.ser_codigo and bfcTitular.tib_codigo = '1')");
            sql.append(" inner join tb_contrato_beneficio cbeTitular on (cbeTitular.bfc_codigo = bfcTitular.bfc_codigo and cbeTitular.ben_codigo = cbeDependente.ben_codigo)");
            sql.append(" where 1 = 1");
            sql.append(" and cbeTitular.scb_codigo = '").append(CodedValues.SCB_CODIGO_ATIVO).append("'");
            sql.append(" and tmp.operacao = 'I' and tmp.mapeada = 'S'");
            sql.append(" group by tmp.id_linha");
            LOG.info(sql);
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            sql.append("update tb_tmp_arquivo_operadora tmp");
            sql.append(" inner join tb_beneficiario bfcTitular on (bfcTitular.bfc_cpf = tmp.bfc_cpf)");
            sql.append(" inner join tb_contrato_beneficio cbeTitular on (bfcTitular.bfc_codigo = cbeTitular.bfc_codigo and cbeTitular.cbe_codigo = tmp.cbe_codigo)");
            sql.append(" left join tb_tmp_cbe_dependente_invalidos tmp2 on (tmp.id_linha = tmp2.id_linha)");
            sql.append(" set tmp.mapeada = '").append(TIPO_MAPEAMENTO_CONTRATO_TITULAR_NAO_ATIVO).append("'");
            sql.append(" where tmp.operacao = 'I' and tmp.mapeada = 'S' and tmp2.id_linha is null");
            sql.append(" and bfcTitular.tib_codigo <> '").append(CodedValues.TIB_TITULAR).append("'");
            LOG.info(sql);
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            sql.append("insert into tb_tmp_cbe_ade_mapeados (cbe_codigo, ade_codigo, cbe_numero, cbe_data_inicio_vigencia, cbc_periodo_beneficio)");
            sql.append(" select tmp.cbe_codigo, ade.ade_codigo, tmp.cbe_numero, tmp.cbe_data_inicio_vigencia, cbc.cbc_periodo");
            sql.append(" from tb_tmp_arquivo_operadora tmp");
            sql.append(" inner join tb_contrato_beneficio cbe on (tmp.cbe_codigo = cbe.cbe_codigo)");
            sql.append(" inner join tb_beneficio ben on (cbe.ben_codigo = ben.ben_codigo and ben.csa_codigo = tmp.csa_codigo and ben.ben_codigo_contrato = tmp.ben_codigo_contrato)");
            sql.append(" inner join tb_aut_desconto ade on (cbe.cbe_codigo = ade.cbe_codigo)");
            sql.append(" inner join tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo)");
            sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
            sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
            sql.append(" inner join tb_orgao org on (org.org_codigo = cnv.org_codigo)");
            sql.append(" inner join tb_estabelecimento est on (est.est_codigo = org.est_codigo)");
            sql.append(" inner join tb_consignante cse on (cse.cse_codigo = est.cse_codigo)");
            sql.append(" inner join tb_calendario_beneficio_cse cbc on (est.cse_codigo = cbc.cse_codigo)");
            sql.append(" where 1 = 1");
            sql.append(" and ade.sad_codigo in (").append(TextHelper.sqlJoin(sadCodigos)).append(")");
            sql.append(" and tla.tnt_codigo in (").append(TextHelper.sqlJoin(tntCodigos)).append(")");
            sql.append(" and operacao = 'I' and mapeada = 'S'");
            sql.append(" and tmp.cbe_data_inicio_vigencia between cbc.cbc_data_ini and cbc.cbc_data_fim");

            LOG.info(sql);
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);
        } catch (final DataAccessException e) {
            LOG.error(e.getCause(), e);
            throw new ImportaArquivosBeneficioControllerException(e);
        }
    }

    @Override
    public List<String> realizarAlteracaoContratosBeneficioOperacaoInclusao(AcessoSistema responsavel) throws ImportaArquivosBeneficioControllerException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("usuCodigo", responsavel.getUsuCodigo());
        queryParams.addValue("ipUsuario", responsavel.getIpUsuario());
        int linhasAfetadas;

        try {
            StringBuilder sql = new StringBuilder();

            // Inserindo ocorrência de informação da alteração do periodo do contrato por causa da data vigência do contrato de sáude. 
            sql.append("insert into tb_ocorrencia_autorizacao (oca_codigo, toc_codigo, ade_codigo, usu_codigo, oca_data, oca_obs, oca_ip_acesso, oca_periodo)");
            sql.append(" select concat('P',");
            sql.append(" date_format(now(), '%y%m%d%h%i%s'),");
            sql.append(" substring(lpad(ade.ade_numero, 12, '0'), 1, 12),");
            sql.append(" substring(lpad(@rownum := coalesce(@rownum, 0) + 1, 7, '0'), 1, 7)),");
            sql.append(" '").append(CodedValues.TOC_INFORMACAO).append("',");
            sql.append(" ade.ade_codigo,");
            sql.append(" :usuCodigo,");
            sql.append(" now(),");
            sql.append(" concat('").append(ApplicationResourcesHelper.getMessage("mensagem.contrato.beneficio.data.consignacao.alterada", responsavel)).append("',date_format(tmp.cbe_data_inicio_vigencia,'%d/%m/%Y')) ,");
            sql.append(" :ipUsuario,");
            sql.append(" PBE_PERIODO");
            sql.append(" from tb_aut_desconto ade");
            sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
            sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
            sql.append(" inner join tb_periodo_beneficio pbe on (pbe.org_codigo = cnv.org_codigo)");
            sql.append(" inner join tb_tmp_cbe_ade_mapeados tmp on (tmp.ade_codigo = ade.ade_codigo)");
            sql.append(" where ade.ade_ano_mes_ini != tmp.cbc_periodo_beneficio ");
            LOG.info(sql);
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);
            
            // Atualizando autorizacao desconto
            sql.append("update tb_aut_desconto ade");
            sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
            sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
            sql.append(" inner join tb_servico svc on (svc.svc_codigo = cnv.svc_codigo)");
            sql.append(" left join tb_param_svc_consignante pse on (pse.svc_codigo = svc.svc_codigo and pse.tps_codigo = '").append(CodedValues.TPS_INCIDE_MARGEM).append("')");
            sql.append(" inner join tb_tmp_cbe_ade_mapeados tmp on (tmp.ade_codigo = ade.ade_codigo)");
            sql.append(" set ade.sad_codigo = '").append(CodedValues.SAD_DEFERIDA).append("' , ade.ade_inc_margem = coalesce(pse.pse_vlr, '0') , ade.ade_ano_mes_ini=tmp.cbc_periodo_beneficio, ade.ade_ano_mes_ini_ref=tmp.cbc_periodo_beneficio");
            LOG.info(sql);
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            String rownum = "SET @rownum := 0; ";
            LOG.debug(rownum);
            jdbc.update(rownum, queryParams);

            sql.append("insert into tb_ocorrencia_autorizacao (oca_codigo, toc_codigo, ade_codigo, usu_codigo, oca_data, oca_obs, oca_ip_acesso, oca_periodo)");
            sql.append(" select concat('B',");
            sql.append(" date_format(now(), '%y%m%d%h%i%s'),");
            sql.append(" substring(lpad(ade.ade_numero, 12, '0'), 1, 12),");
            sql.append(" substring(lpad(@rownum := coalesce(@rownum, 0) + 1, 7, '0'), 1, 7)),");
            sql.append(" '").append(CodedValues.TOC_ALTERACAO_CONTRATO).append("',");
            sql.append(" ade.ade_codigo,");
            sql.append(" :usuCodigo,");
            sql.append(" now(),");
            sql.append(" '").append(ApplicationResourcesHelper.getMessage("mensagem.informacao.contrato.deferido", responsavel)).append("',");
            sql.append(" :ipUsuario,");
            sql.append(" PBE_PERIODO");
            sql.append(" from tb_aut_desconto ade");
            sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
            sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
            sql.append(" inner join tb_periodo_beneficio pbe on (pbe.org_codigo = cnv.org_codigo)");
            sql.append(" inner join tb_tmp_cbe_ade_mapeados tmp on (tmp.ade_codigo = ade.ade_codigo)");
            LOG.info(sql);
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            // Atualizando contratos beneficios
            sql.append("update tb_contrato_beneficio cbe");
            sql.append(" inner join tb_tmp_cbe_ade_mapeados tmp on (tmp.cbe_codigo = cbe.cbe_codigo)");
            sql.append(" set cbe.scb_codigo = '").append(CodedValues.SCB_CODIGO_ATIVO).append("'");
            sql.append(" ,cbe.cbe_data_inicio_vigencia = tmp.cbe_data_inicio_vigencia");
            sql.append(" ,cbe.cbe_numero = tmp.cbe_numero");
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            LOG.debug(rownum);
            jdbc.update(rownum, queryParams);

            sql.append("insert into tb_ocorrencia_ctt_beneficio (ocb_codigo, toc_codigo, usu_codigo, cbe_codigo, ocb_data, ocb_obs, ocb_ip_acesso)");
            sql.append(" select concat('B',");
            sql.append(" date_format(now(), '%y%m%d%h%i%s'),");
            sql.append(" substring(lpad(cbe.cbe_numero, 12, '0'), 1, 12),");
            sql.append(" substring(lpad(@rownum := coalesce(@rownum, 0) + 1, 7, '0'), 1, 7)),");
            sql.append(" '").append(CodedValues.TOC_ALTERACAO_CONTRATO_BENEFICIO).append("',");
            sql.append(" :usuCodigo,");
            sql.append(" cbe.cbe_codigo,");
            sql.append(" now(),");
            sql.append(" '").append(ApplicationResourcesHelper.getMessage("mensagem.beneficio.ocorrencia.contrato.beneficio.alteracao", responsavel)).append("',");
            sql.append(" :ipUsuario");
            sql.append(" from tb_contrato_beneficio cbe");
            sql.append(" inner join tb_tmp_cbe_ade_mapeados tmp on (tmp.cbe_codigo = cbe.cbe_codigo)");
            sql.append(" group by cbe.cbe_codigo");
            LOG.info(sql);
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            sql.append("update tb_tmp_arquivo_operadora tmpArq");
            sql.append(" inner join tb_tmp_cbe_ade_mapeados tmp on (tmpArq.cbe_codigo = tmp.cbe_codigo)");
            sql.append(" set tmpArq.processada = 'S'");
            LOG.info(sql);
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            sql.append("select rse_codigo from tb_aut_desconto ade");
            sql.append(" inner join tb_tmp_cbe_ade_mapeados tmp on (ade.ade_codigo = tmp.ade_codigo)");
            sql.append(" where 1 = 1");
            sql.append(" group by rse_codigo");
            LOG.info(sql);
            
            final List<String> rseCodigos = new ArrayList<String>();
            final List<Map<String, Object>> resultSet = jdbc.queryForList(sql.toString(), queryParams);

            sql.setLength(0);

            for (Map<String, Object> row : resultSet) {
                String rseCodigo = (String) row.get("rse_codigo");
                rseCodigos.add(rseCodigo);
            }

            return rseCodigos;
        } catch (final DataAccessException e) {
            LOG.error(e.getCause(), e);
            throw new ImportaArquivosBeneficioControllerException(e);
        }
    }

    // Metodos responvel pelo o fluxo de inclusao com migracao

    @Override
    public List<String> realizarMapeamentoContratosBeneficioOperacaoInclusaoMigracao(AcessoSistema responsavel) throws ImportaArquivosBeneficioControllerException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        int linhasAfetadas;

        List<String> tntCodigos = new ArrayList<>(CodedValues.TNT_BENEFICIO_MENSALIDADE);
        tntCodigos.addAll(CodedValues.TNT_BENEFICIO_SUBSIDIO);

        List<String> sadCodigos = Arrays.asList(CodedValues.SAD_DEFERIDA, CodedValues.SAD_EMANDAMENTO, CodedValues.SAD_AGUARD_LIQUIDACAO, CodedValues.SAD_AGUARD_LIQUI_COMPRA);

        try {
            // Pocurando contratos do grupo familiar que estão em migração e fazendo eles incidirem na margem
            StringBuilder sql = new StringBuilder();
            sql.append("delete from tb_tmp_cbe_ade_mapeados2;");
            LOG.info(sql);
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            sql.append("insert into tb_tmp_cbe_ade_mapeados2 (cbe_codigo, ade_codigo, cbe_numero, cbe_data_inicio_vigencia)");
            sql.append(" select cbeNovos.cbe_codigo, adeNovos.ade_codigo, cbeNovos.cbe_numero, now()");
            sql.append(" from tb_aut_desconto adePonte");
            sql.append(" inner join tb_tmp_cbe_ade_mapeados tmp on (tmp.ade_codigo = adePonte.ade_codigo)");
            sql.append(" inner join tb_relacionamento_autorizacao rel on (rel.ade_codigo_destino = tmp.ade_codigo and rel.tnt_codigo = '").append(CodedValues.TNT_CONTROLE_MIGRACAO_BENEFICIO).append("')");
            sql.append(" inner join tb_contrato_beneficio cbePonte on (adePonte.cbe_codigo = cbePonte.cbe_codigo)");
            sql.append(" inner join tb_beneficiario bfcPonte on (cbePonte.bfc_codigo = bfcPonte.bfc_codigo)");
            sql.append(" inner join tb_beneficiario bfcNovos on (bfcPonte.ser_codigo = bfcNovos.ser_codigo)");
            sql.append(" inner join tb_contrato_beneficio cbeNovos on (cbePonte.ben_codigo = cbeNovos.ben_codigo and cbeNovos.bfc_codigo = bfcNovos.bfc_codigo)");
            sql.append(" inner join tb_aut_desconto adeNovos on (cbeNovos.cbe_codigo = adeNovos.cbe_codigo)");
            sql.append(" inner join tb_tipo_lancamento tla on (adeNovos.tla_codigo = tla.tla_codigo)");
            sql.append(" where 1 = 1");
            sql.append(" and tla.tnt_codigo in (").append(TextHelper.sqlJoin(tntCodigos)).append(")");
            LOG.info(sql);
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            sql.append("update tb_aut_desconto ade");
            sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
            sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
            sql.append(" inner join tb_servico svc on (svc.svc_codigo = cnv.svc_codigo)");
            sql.append(" left join tb_param_svc_consignante pse on (pse.svc_codigo = svc.svc_codigo and pse.tps_codigo = '").append(CodedValues.TPS_INCIDE_MARGEM).append("')");
            sql.append(" inner join tb_tmp_cbe_ade_mapeados2 tmp on (tmp.ade_codigo = ade.ade_codigo)");
            sql.append(" set ade.ade_inc_margem = coalesce(pse.pse_vlr, '0')");
            LOG.info(sql);
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            // Procurando os contratos de origem da migração e cancelandos eles.
            sql = new StringBuilder();
            sql.append("delete from tb_tmp_cbe_ade_mapeados2;");
            LOG.info(sql);
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            sql.append("insert into tb_tmp_cbe_ade_mapeados2 (cbe_codigo, ade_codigo, cbe_numero, cbe_data_fim_vigencia)");
            sql.append(" select cbeAntigos.cbe_codigo, adeAntigos.ade_codigo, cbeAntigos.cbe_numero, now()");
            sql.append(" from tb_contrato_beneficio cbeNovos");
            sql.append(" inner join tb_beneficiario bfcNovos on (cbeNovos.bfc_codigo = bfcNovos.bfc_codigo)");
            sql.append(" inner join tb_beneficiario bfcAntigos on (bfcNovos.ser_codigo = bfcAntigos.ser_codigo)");
            sql.append(" inner join tb_aut_desconto adeNovos on (cbeNovos.cbe_codigo = adeNovos.cbe_codigo)");
            sql.append(" inner join tb_tmp_cbe_ade_mapeados tmp on (tmp.ade_codigo = adeNovos.ade_codigo)");
            sql.append(" inner join tb_relacionamento_autorizacao rel on (rel.ade_codigo_destino = adeNovos.ade_codigo and rel.tnt_codigo = '").append(CodedValues.TNT_CONTROLE_MIGRACAO_BENEFICIO).append("')");
            sql.append(" inner join tb_aut_desconto adePonte on (rel.ade_codigo_origem = adePonte.ade_codigo)");
            sql.append(" inner join tb_contrato_beneficio cbePonte on (adePonte.cbe_codigo = cbePonte.cbe_codigo)");
            sql.append(" inner join tb_contrato_beneficio cbeAntigos on (cbeAntigos.bfc_codigo = bfcAntigos.bfc_codigo and cbeAntigos.ben_codigo = cbePonte.ben_codigo)");
            sql.append(" inner join tb_aut_desconto adeAntigos on (adeAntigos.cbe_codigo = cbeAntigos.cbe_codigo)");
            sql.append(" inner join tb_tipo_lancamento tla on (adeAntigos.tla_codigo = tla.tla_codigo)");
            sql.append(" where 1 = 1");
            sql.append(" and adeAntigos.sad_codigo in (").append(TextHelper.sqlJoin(sadCodigos)).append(")");
            sql.append(" and cbeAntigos.scb_codigo in ('").append(CodedValues.SCB_CODIGO_AGUARDANDO_EXCLUSAO_OPERADORA).append("')");
            sql.append(" and tla.tnt_codigo in (").append(TextHelper.sqlJoin(tntCodigos)).append(")");
            LOG.info(sql);
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            sql.append("delete from tb_tmp_cbe_ade_mapeados;");
            LOG.info(sql);
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            sql.append("insert into tb_tmp_cbe_ade_mapeados select * from tb_tmp_cbe_ade_mapeados2");
            LOG.info(sql);
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            return realizarAlteracaoContratosBeneficioOperacaoExclusao(responsavel);
        } catch (final DataAccessException e) {
            LOG.error(e.getCause(), e);
            throw new ImportaArquivosBeneficioControllerException(e);
        }
    }

    // Metodos responsavel pelo o fluxo de exclusão.

    @Override
    public void realizarMapeamentoContratosBeneficioOperacaoExclusao() throws ImportaArquivosBeneficioControllerException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        List<String> tntCodigos = new ArrayList<>(CodedValues.TNT_BENEFICIO_MENSALIDADE);
        tntCodigos.addAll(CodedValues.TNT_BENEFICIO_SUBSIDIO);

        List<String> sadCodigos = Arrays.asList(CodedValues.SAD_DEFERIDA, CodedValues.SAD_EMANDAMENTO, CodedValues.SAD_AGUARD_LIQUIDACAO, CodedValues.SAD_AGUARD_LIQUI_COMPRA);

        try {
            StringBuilder sql = new StringBuilder();
            sql.append("update tb_tmp_arquivo_operadora tmp");
            sql.append(" inner join tb_contrato_beneficio cbe on (cbe.cbe_numero = tmp.cbe_numero)");
            sql.append(" inner join tb_beneficiario bfc on (bfc.bfc_codigo = cbe.bfc_codigo and tmp.bfc_cpf = bfc.bfc_cpf)");
            sql.append(" inner join tb_beneficio ben on (cbe.ben_codigo = ben.ben_codigo and ben.csa_codigo = tmp.csa_codigo and ben.ben_codigo_contrato = tmp.ben_codigo_contrato)");
            sql.append(" inner join tb_aut_desconto ade on (cbe.cbe_codigo = ade.cbe_codigo)");
            sql.append(" inner join tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo)");
            sql.append(" set tmp.mapeada = 'S', tmp.cbe_codigo = cbe.cbe_codigo");
            sql.append(" where 1 = 1");
            sql.append(" and ade.sad_codigo in (").append(TextHelper.sqlJoin(sadCodigos)).append(")");
            sql.append(" and cbe.scb_codigo in ('").append(CodedValues.SCB_CODIGO_AGUARDANDO_EXCLUSAO_OPERADORA).append("')");
            sql.append(" and tla.tnt_codigo in (").append(TextHelper.sqlJoin(tntCodigos)).append(")");
            sql.append(" and (tmp.cbe_data_fim_vigencia is not null or trim(tmp.cbe_data_fim_vigencia) <> '' or tmp.cbe_data_fim_vigencia <> '0000-00-00 00:00:00')");
            sql.append(" and tmp.operacao = 'E';");
            LOG.info(sql);
            int linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            sql.append("delete from tb_tmp_cbe_ade_mapeados;");
            LOG.info(sql);
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            sql.append("delete from tb_tmp_cbe_ade_mapeados2;");
            LOG.info(sql);
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            sql.append("insert into tb_tmp_cbe_ade_mapeados (cbe_codigo, ade_codigo, cbe_numero, cbe_data_fim_vigencia, tmo_codigo)");
            sql.append(" select tmp.cbe_codigo, ade.ade_codigo, tmp.cbe_numero, tmp.cbe_data_fim_vigencia, ocb.tmo_codigo");
            sql.append(" from tb_tmp_arquivo_operadora tmp");
            sql.append(" inner join tb_contrato_beneficio cbe on (tmp.cbe_codigo = cbe.cbe_codigo)");
            sql.append(" inner join tb_beneficio ben on (cbe.ben_codigo = ben.ben_codigo and ben.csa_codigo = tmp.csa_codigo and ben.ben_codigo_contrato = tmp.ben_codigo_contrato)");
            sql.append(" inner join tb_aut_desconto ade on (cbe.cbe_codigo = ade.cbe_codigo)");
            sql.append(" inner join tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo)");
            sql.append(" inner join tb_ocorrencia_ctt_beneficio ocb on (ocb.cbe_codigo = cbe.cbe_codigo) ");
            sql.append(" where 1 = 1");
            sql.append(" and ade.sad_codigo in (").append(TextHelper.sqlJoin(sadCodigos)).append(")");
            sql.append(" and tla.tnt_codigo in (").append(TextHelper.sqlJoin(tntCodigos)).append(")");
            sql.append(" and operacao = 'E' and mapeada = 'S'");
            sql.append(" and ocb.ocb_data = (select max(ocb_data) from tb_ocorrencia_ctt_beneficio ocb1 where ocb1.toc_codigo='").append(CodedValues.TOC_ALTERACAO_STATUS_CONTRATO_BENEFICIO).append("' and ocb1.cbe_codigo = cbe.cbe_codigo)");
            sql.append(" and ocb.toc_codigo = '").append(CodedValues.TOC_ALTERACAO_STATUS_CONTRATO_BENEFICIO).append("'");

            LOG.info(sql);
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            // Procurando se existe algum titular sendo excluido, se sim vamos pegar o grupo familiar do mesmo e colocar no fluxo de exclusão.
            sql.append("insert into tb_tmp_cbe_ade_mapeados2 (cbe_codigo, ade_codigo, cbe_numero, cbe_data_fim_vigencia, tmo_codigo)");
            sql.append("select cbeDependentes.cbe_codigo, ade.ade_codigo, tmp.cbe_numero, tmp.cbe_data_fim_vigencia, ocbDependente.tmo_codigo from tb_tmp_arquivo_operadora tmp");
            sql.append(" inner join tb_contrato_beneficio cbeTitular on (tmp.cbe_codigo = cbeTitular.cbe_codigo)");
            sql.append(" inner join tb_beneficio benTitular on (cbeTitular.ben_codigo = benTitular.ben_codigo and benTitular.csa_codigo = tmp.csa_codigo and benTitular.ben_codigo_contrato = tmp.ben_codigo_contrato)");
            sql.append(" inner join tb_beneficiario bfcTitular on (cbeTitular.bfc_codigo = bfcTitular.bfc_codigo)");
            sql.append(" inner join tb_beneficiario bfcDependentes on (bfcTitular.ser_codigo = bfcDependentes.ser_codigo)");
            sql.append(" inner join tb_contrato_beneficio cbeDependentes on (bfcDependentes.bfc_codigo = cbeDependentes.bfc_codigo)");
            sql.append(" inner join tb_beneficio benDependentes on (benDependentes.ben_codigo = cbeDependentes.ben_codigo and benDependentes.ben_codigo = benTitular.ben_codigo and benDependentes.csa_codigo = tmp.csa_codigo and benDependentes.ben_codigo_contrato = tmp.ben_codigo_contrato)");
            sql.append(" inner join tb_aut_desconto ade on (cbeDependentes.cbe_codigo = ade.cbe_codigo)");
            sql.append(" inner join tb_tipo_lancamento tla on (ade.tla_codigo = tla.tla_codigo)");
            sql.append(" inner join tb_ocorrencia_ctt_beneficio ocbDependente on (ocbDependente.cbe_codigo = cbeDependentes.cbe_codigo) ");
            sql.append(" left join tb_tmp_cbe_ade_mapeados tmpAdeCbe on (tmpAdeCbe.cbe_codigo = cbeDependentes.cbe_codigo)");
            sql.append(" where bfcTitular.tib_codigo = '").append(CodedValues.TIB_TITULAR).append("'");
            sql.append(" and ade.sad_codigo in (").append(TextHelper.sqlJoin(sadCodigos)).append(")");
            sql.append(" and tla.tnt_codigo in (").append(TextHelper.sqlJoin(tntCodigos)).append(")");
            sql.append(" and tmp.operacao = 'E' and tmp.mapeada = 'S'");
            sql.append(" and tmpAdeCbe.cbe_codigo is null");
            sql.append(" and ocbDependente.ocb_data = (select max(ocb_data) from tb_ocorrencia_ctt_beneficio ocb1 where ocb1.toc_codigo='").append(CodedValues.TOC_ALTERACAO_STATUS_CONTRATO_BENEFICIO).append("' and ocb1.cbe_codigo = cbeDependentes.cbe_codigo)");
            sql.append(" and ocbDependente.toc_codigo = '").append(CodedValues.TOC_ALTERACAO_STATUS_CONTRATO_BENEFICIO).append("'");

            LOG.info(sql);
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            sql.append("insert into tb_tmp_cbe_ade_mapeados select * from tb_tmp_cbe_ade_mapeados2");
            LOG.info(sql);
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);
        } catch (final DataAccessException e) {
            LOG.error(e.getCause(), e);
            throw new ImportaArquivosBeneficioControllerException(e);
        }
    }

    @Override
    public List<String> realizarAlteracaoContratosBeneficioOperacaoExclusao(AcessoSistema responsavel) throws ImportaArquivosBeneficioControllerException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("usuCodigo", responsavel.getUsuCodigo());
        queryParams.addValue("ipUsuario", responsavel.getIpUsuario());

        int linhasAfetadas;

        try {
            StringBuilder sql = new StringBuilder();

            // Atualizando autorizacao desconto
            sql.append("update tb_aut_desconto ade");
            sql.append(" inner join tb_tmp_cbe_ade_mapeados tmp on (tmp.ade_codigo = ade.ade_codigo)");
            sql.append(" set ade.sad_codigo = '").append(CodedValues.SAD_LIQUIDADA).append("'");
            sql.append(" ,ade.ade_data_exclusao = tmp.cbe_data_fim_vigencia");
            LOG.info(sql);
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            String rownum = "SET @rownum := 0; ";
            LOG.debug(rownum);
            jdbc.update(rownum, queryParams);

            sql.append("insert into tb_ocorrencia_autorizacao (oca_codigo, toc_codigo, ade_codigo, usu_codigo, oca_data, oca_obs, oca_ip_acesso, oca_periodo)");
            sql.append(" select concat('B',");
            sql.append(" date_format(now(), '%y%m%d%h%i%s'),");
            sql.append(" substring(lpad(ade.ade_numero, 12, '0'), 1, 12),");
            sql.append(" substring(lpad(@rownum := coalesce(@rownum, 0) + 1, 7, '0'), 1, 7)),");
            sql.append(" '").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("',");
            sql.append(" ade.ade_codigo,");
            sql.append(" :usuCodigo,");
            sql.append(" now(),");
            sql.append(" '").append(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.concluido", responsavel)).append("',");
            sql.append(" :ipUsuario,");
            sql.append(" PBE_PERIODO");
            sql.append(" from tb_aut_desconto ade");
            sql.append(" inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo)");
            sql.append(" inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo)");
            sql.append(" inner join tb_periodo_beneficio pbe on (pbe.org_codigo = cnv.org_codigo)");
            sql.append(" inner join tb_tmp_cbe_ade_mapeados tmp on (tmp.ade_codigo = ade.ade_codigo)");
            LOG.info(sql);
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            // Atualizando contratos beneficios que conseguimos atualizar a ade
            sql.append("update tb_contrato_beneficio cbe");
            sql.append(" inner join tb_tmp_cbe_ade_mapeados tmp on (tmp.cbe_codigo = cbe.cbe_codigo)");
            sql.append(" set cbe.scb_codigo = '").append(CodedValues.SCB_CODIGO_CANCELADO).append("'");
            sql.append(" ,cbe.cbe_data_fim_vigencia = tmp.cbe_data_fim_vigencia");
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            LOG.debug(rownum);
            jdbc.update(rownum, queryParams);

            sql.append("insert into tb_ocorrencia_ctt_beneficio (ocb_codigo, toc_codigo, usu_codigo, cbe_codigo, tmo_codigo, ocb_data, ocb_obs, ocb_ip_acesso)");
            sql.append(" select concat('B',");
            sql.append(" date_format(now(), '%y%m%d%h%i%s'),");
            sql.append(" substring(lpad(cbe.cbe_numero, 12, '0'), 1, 12),");
            sql.append(" substring(lpad(@rownum := coalesce(@rownum, 0) + 1, 7, '0'), 1, 7)),");
            sql.append(" '").append(CodedValues.TOC_EXCLUSAO_CONTRATO_BENEFICIO).append("',");
            sql.append(" :usuCodigo,");
            sql.append(" cbe.cbe_codigo,");
            sql.append(" tmp.tmo_codigo,");
            sql.append(" now(),");
            sql.append(" '").append(ApplicationResourcesHelper.getMessage("mensagem.beneficio.ocorrencia.contrato.beneficio.exclusao", responsavel)).append("',");
            sql.append(" :ipUsuario");
            sql.append(" from tb_contrato_beneficio cbe");
            sql.append(" inner join tb_tmp_cbe_ade_mapeados tmp on (tmp.cbe_codigo = cbe.cbe_codigo)");
            sql.append(" group by cbe.cbe_codigo");
            LOG.info(sql);
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            sql.append("update tb_tmp_arquivo_operadora tmpArq");
            sql.append(" inner join tb_tmp_cbe_ade_mapeados tmp on (tmpArq.cbe_codigo = tmp.cbe_codigo)");
            sql.append(" set tmpArq.processada = 'S'");
            LOG.info(sql);
            linhasAfetadas = jdbc.update(sql.toString(), queryParams);
            LOG.info("Linhas afetadas: " + linhasAfetadas);
            sql.setLength(0);

            sql.append("select rse_codigo from tb_aut_desconto ade");
            sql.append(" inner join tb_tmp_cbe_ade_mapeados tmp on (ade.ade_codigo = tmp.ade_codigo)");
            sql.append(" where 1 = 1");
            sql.append(" group by rse_codigo");
            LOG.info(sql);

            final List<String> rseCodigos = new ArrayList<String>();
            final List<Map<String, Object>> resultSet = jdbc.queryForList(sql.toString(), queryParams);

            sql.setLength(0);

            for (Map<String, Object> row : resultSet) {
                String rseCodigo = (String) row.get("rse_codigo");
                rseCodigos.add(rseCodigo);
            }

            return rseCodigos;

        } catch (final DataAccessException e) {
            LOG.error(e.getCause(), e);
            throw new ImportaArquivosBeneficioControllerException(e);
        }
    }

    @Override
    public List<String> geraLinhasNaoMapedasParaCritica(AcessoSistema responsavel) throws ImportaArquivosBeneficioControllerException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        List<String> linhasSemMapeamento = new ArrayList<String>();

        try {
            StringBuilder sql = new StringBuilder();
            sql.append("select linha from tb_tmp_arquivo_operadora where mapeada = 'N'");
            LOG.info(sql);
            final List<Map<String, Object>> resultSet1 = jdbc.queryForList(sql.toString(), queryParams);
            for (Map<String, Object> row : resultSet1) {
                String linha = (String) row.get("linha");
                linhasSemMapeamento.add(linha.concat("|").concat(ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.retorno.operadora.nao.mapeado", responsavel)));
            }

            sql.setLength(0);
            sql.append("select linha from tb_tmp_arquivo_operadora where mapeada = '").append(TIPO_MAPEAMENTO_CONTRATO_TITULAR_NAO_ATIVO).append("'");
            LOG.info(sql);
            final List<Map<String, Object>> resultSet2 = jdbc.queryForList(sql.toString(), queryParams);
            for (Map<String, Object> row : resultSet2) {
                String linha = (String) row.get("linha");
                linhasSemMapeamento.add(linha.concat("|").concat(ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.retorno.operadora.contrato.titular.nao.ativo", responsavel)));
            }
            sql.setLength(0);

        } catch (final DataAccessException e) {
            LOG.error(e.getCause(), e);
            throw new ImportaArquivosBeneficioControllerException(e);
        }

        return linhasSemMapeamento;
    }
}
