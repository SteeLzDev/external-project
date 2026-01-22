package com.zetra.econsig.folha.exportacao.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.TipoMotivoNaoExportacaoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

public class CaxiasDoSul extends ExportaMovimentoBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CaxiasDoSul.class);

    protected String CSA_IDENTIFICADOR_IPAM = "005";

    @Override
    public void posCriacaoTabelas(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        super.posCriacaoTabelas(parametrosExportacao, responsavel);

        // Precisamos criar duas tabelas:
        // Uma tabela para fazer o cálculo do teto e ir descontando deste teto os valores dos contratos.
        // Uma tabela auxiliar que terá os contratos de saude para setarmos o valor a ser descontado limitando ao teto.

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
            final StringBuilder query = new StringBuilder();

            query.append("DROP TABLE IF EXISTS tmp_rse_teto_ipam");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TABLE tmp_rse_teto_ipam ( ");
            query.append("rse_codigo varchar(32), ");
            query.append("rse_base_calculo decimal(13,2), ");
            query.append("total_divida_ipam decimal(13,2), ");
            query.append("rse_teto_ipam decimal(13,2), ");
            query.append("rse_teto_ipam_rest decimal(13,2), ");
            query.append("primary key (rse_codigo) ");
            query.append(")");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("DROP TABLE IF EXISTS tmp_contratos_ipam");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            query.setLength(0);
            query.append("CREATE TABLE tmp_contratos_ipam ( ");
            query.append("rse_codigo varchar(32), ");
            query.append("ade_codigo varchar(32), ");
            query.append("ade_vlr_analise decimal(13,2), ");
            query.append("ade_vlr decimal(13,2), ");
            query.append("key tmp_contratos_ipam_IDX1 (rse_codigo), ");
            query.append("key tmp_contratos_ipam_IDX2 (ade_codigo) ");
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

        LOG.debug("Ajusta os valores dos contratos do IPAM para descontos: " + DateHelper.getSystemDatetime());
        preparadaTabelaExportacaoIPAM();
        LOG.debug("fim - Ajuste dos contratos do IPAM para descontos: " + DateHelper.getSystemDatetime());

        // Remove da tabela de exportação as ADE que não cabem na margem, envia parcial considerando a escolha da CSA
        LOG.debug("ValidaMargemCsaEscolheExportaParcialOrdemPadrao.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
        removerContratosSemMargemMovimentoMensalv2(true, null, true);
        LOG.debug("fim - ValidaMargemCsaEscolheExportaParcialOrdemPadrao.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
    }

    private void preparadaTabelaExportacaoIPAM() throws ExportaMovimentoException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        MapSqlParameterSource queryParams = new MapSqlParameterSource();
        queryParams.addValue("csaIdentificador", CSA_IDENTIFICADOR_IPAM);
        try {
            final StringBuilder query = new StringBuilder();

            // Inserimos na tabela a soma dos contratos do IPAM por registro servidor
            query.append("INSERT INTO tmp_rse_teto_ipam ");
            query.append("SELECT tmp.rse_codigo, rse.rse_base_calculo, sum(tmp.ade_vlr), '0.00', '0.00' ");
            query.append("FROM tb_tmp_exportacao tmp ");
            query.append("INNER JOIN tb_registro_servidor rse ON (tmp.rse_codigo = rse.rse_codigo) ");
            query.append("WHERE csa_identificador = :csaIdentificador ");
            query.append("GROUP BY rse.rse_codigo ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Calculamos aqui o teto máximo de desconto para o IPAM
            // Total da divida lançando para o IPAM for MENOR que 3x o cadastro da baseCalculo então o teto é de 12% da baseCalculo
            // Total da divida lançando para o IPAM for MAIOR que 3x o cadastro da baseCalculo então o teto é de 20% da baseCalculo
            query.setLength(0);
            query.append("UPDATE tmp_rse_teto_ipam ");
            query.append("SET rse_teto_ipam = CASE WHEN total_divida_ipam < (rse_base_calculo*3) THEN (rse_base_calculo*0.12) ELSE (rse_base_calculo*0.20) END ");
            query.append(", rse_teto_ipam_rest = CASE WHEN total_divida_ipam < (rse_base_calculo*3) THEN (rse_base_calculo*0.12) ELSE (rse_base_calculo*0.20) END ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            // Iremos recuperar a lista de verbas por ordem de prioridade do IPAM e então fazer os cálculos de desconto.
            final List<TransferObject> listVerbasOrdemPrioridadeIPAM = listVerbasOrdemPrioridadeIPAM();
            for (final TransferObject verbaOrdemPrioridadeIPAM : listVerbasOrdemPrioridadeIPAM) {
                queryParams = new MapSqlParameterSource();

                final String cnvCodigo = (String) verbaOrdemPrioridadeIPAM.getAttribute("cnv_codigo");
                queryParams.addValue("cnvCodigo", cnvCodigo);
                queryParams.addValue("mneCodigo", TipoMotivoNaoExportacaoEnum.SERVIDOR_SEM_MARGEM_SUFICIENTE.getCodigo());

                // Limpamos a tabela para conter somente os contratos que serão analisados
                query.setLength(0);
                query.append("DELETE FROM tmp_contratos_ipam ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                // Inserimos na tabela os contratos da verba para ir subtraindo do valor restante para limitar ao Teto
                // Seguimos nessa lógica por ser somente um contrato por verba no sistema
                query.setLength(0);
                query.append("INSERT INTO tmp_contratos_ipam ");
                query.append("SELECT rse_codigo, ade_codigo, ade_vlr, ade_vlr ");
                query.append("FROM tb_tmp_exportacao tmp ");
                query.append("WHERE cnv_codigo = :cnvCodigo ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                // Caso o valor do teto já tenha sido atingido, precisamos remover o contrato do movimento.
                // Atualizamos o mne_codigo (motivo não exportação) e depois iremos excluir
                query.setLength(0);
                query.append("UPDATE tb_aut_desconto ade ");
                query.append("INNER JOIN tmp_contratos_ipam tmp ON (tmp.ade_codigo = ade.ade_codigo) ");
                query.append("INNER JOIN tmp_rse_teto_ipam teto ON (tmp.rse_codigo = teto.rse_codigo) ");
                query.append("SET ade.mne_codigo = :mneCodigo ");
                query.append("WHERE teto.rse_teto_ipam_rest <= 0 ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                query.setLength(0);
                query.append("DELETE tmpExp.* FROM tb_tmp_exportacao tmpExp ");
                query.append("INNER JOIN tmp_contratos_ipam tmp ON (tmp.ade_codigo = tmpExp.ade_codigo) ");
                query.append("INNER JOIN tmp_rse_teto_ipam teto ON (tmp.rse_codigo = teto.rse_codigo) ");
                query.append("WHERE teto.rse_teto_ipam_rest <= 0 ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                // Atualizamos o valor do contrato para o desconto do IPAM
                query.setLength(0);
                query.append("UPDATE tmp_rse_teto_ipam teto ");
                query.append("INNER JOIN tmp_contratos_ipam tmp ON (tmp.rse_codigo = teto.rse_codigo) ");
                query.append("SET ade_vlr = CASE ");
                query.append("  WHEN ade_vlr_analise >= rse_teto_ipam_rest THEN rse_teto_ipam_rest ");
                query.append("  ELSE ade_vlr_analise END ");
                query.append("WHERE rse_teto_ipam_rest > 0 ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                // Atualizamos o restante do servdior para o desconto do IPAM
                query.setLength(0);
                query.append("UPDATE tmp_rse_teto_ipam teto ");
                query.append("INNER JOIN tmp_contratos_ipam tmp ON (tmp.rse_codigo = teto.rse_codigo) ");
                query.append("SET rse_teto_ipam_rest = CASE ");
                query.append(" WHEN ade_vlr_analise >= rse_teto_ipam_rest THEN '0.00' ELSE rse_teto_ipam_rest - ade_vlr_analise END ");
                query.append("WHERE rse_teto_ipam_rest > 0 ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);

                // Atualizamos na exportação o valor da adeVlr calculada
                query.setLength(0);
                query.append("UPDATE tmp_contratos_ipam teto ");
                query.append("INNER JOIN tb_tmp_exportacao tmp ON (tmp.ade_codigo = teto.ade_codigo) ");
                query.append("SET tmp.ade_vlr = teto.ade_vlr ");
                query.append("WHERE tmp.ade_vlr != teto.ade_vlr ");
                LOG.debug(query.toString());
                jdbc.update(query.toString(), queryParams);
            }

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage());
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    private List<TransferObject> listVerbasOrdemPrioridadeIPAM() throws ExportaMovimentoException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final StringBuilder query = new StringBuilder();
        queryParams.addValue("csaIdentificador", CSA_IDENTIFICADOR_IPAM);

        query.append("SELECT DISTINCT cnv_codigo ");
        query.append("from tb_tmp_exportacao tmp ");
        query.append("where csa_identificador = :csaIdentificador ");
        query.append("order by ");
        query.append("coalesce(tmp.svc_prioridade, 9999999) + 0, coalesce(tmp.cnv_prioridade, 9999999) + 0, tmp.ade_data, tmp.ade_numero");
        LOG.debug(query.toString());

        List<TransferObject> contratos = new ArrayList<>();
        try {
            final String fieldsNames = "cnv_codigo";
            contratos = MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fieldsNames, MySqlDAOFactory.SEPARADOR);
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage());
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
        return contratos;
    }

    @Override
    protected List<TransferObject> listaContratosSemMargemCandidatosv2(List<Short> marCodigos) throws ExportaMovimentoException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        final StringBuilder query = new StringBuilder();
        query.append("select tmp.rse_codigo, tmp.ade_codigo, tmp.ade_vlr, tmp.ade_data, ").append(getClausulaIncideMargemNaListaContratosSemMargem(marCodigos)).append(", tmp.autoriza_pgt_parcial ");
        query.append("from tb_tmp_exportacao tmp ");
        query.append("inner join tb_registro_servidor rse ON (tmp.rse_codigo = rse.rse_codigo) ");
        query.append("inner join tb_periodo_exportacao pex ON (pex.org_codigo = rse.org_codigo) ");
        query.append("where ((tmp.ade_inc_margem = 1 and tmp.rse_margem_rest + COALESCE((").append(queryMargemUsadaPosCorte(CodedValues.INCIDE_MARGEM_SIM, queryParams)).append("), 0.00) < 0.00) ");
        query.append("    OR (tmp.ade_inc_margem = 2 and tmp.rse_margem_rest_2 + COALESCE((").append(queryMargemUsadaPosCorte(CodedValues.INCIDE_MARGEM_SIM_2, queryParams)).append("), 0.00) < 0.00) ");
        query.append("    OR (tmp.ade_inc_margem = 3 and tmp.rse_margem_rest_3 + COALESCE((").append(queryMargemUsadaPosCorte(CodedValues.INCIDE_MARGEM_SIM_3, queryParams)).append("), 0.00) < 0.00) ");
        query.append("    OR (tmp.ade_inc_margem not in (0,1,2,3) and (select mrs_margem_rest + COALESCE((").append(queryMargemUsadaPosCorte(queryParams)).append("), 0.00) from tb_margem_registro_servidor mrs where mrs.rse_codigo = rse.rse_codigo and mrs.mar_codigo = tmp.ade_inc_margem) < 0.00)) ");
        if ((marCodigos != null) && !marCodigos.isEmpty()) {
            query.append(" and tmp.ade_inc_margem in (:marCodigos) ");
            queryParams.addValue("marCodigos", marCodigos);
        }
        query.append("order by tmp.rse_codigo, ");
        query.append("coalesce(tmp.svc_prioridade, 9999999) + 0, coalesce(tmp.cnv_prioridade, 9999999) + 0, tmp.ade_data, tmp.ade_numero");
        LOG.debug(query.toString());

        List<TransferObject> contratos = new ArrayList<>();
        try {
            final String fieldsNames = "rse_codigo,ade_codigo,ade_vlr,ade_data,ade_inc_margem,autoriza_pgt_parcial";
            contratos = MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fieldsNames, MySqlDAOFactory.SEPARADOR);
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage());
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
        return contratos;
    }

    @Override
    public ContratosSemMargem obterContratosSemMargemMovimentoMensalv2(List<TransferObject> contratos, boolean permiteDescontoParcial, boolean verificaParamCsaPgParcial) throws ExportaMovimentoException {
        try {
            final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();

            final ConsultarMargemController consultarMargemController = ApplicationContextProvider.getApplicationContext().getBean(ConsultarMargemController.class);
            final ContratosSemMargem adeImpropria = new ContratosSemMargem();

            if ((contratos != null) && !contratos.isEmpty()) {
                String rseCodigoAnterior = null;

                String rseCodigo = null;
                Short adeIncMargem = null;
                String adeCodigo = null;
                BigDecimal adeVlr = BigDecimal.ZERO;

                final Map<Short, BigDecimal> margemFolha = new HashMap<>();

                for (final TransferObject contrato : contratos) {
                    rseCodigo = contrato.getAttribute("rse_codigo").toString();
                    adeIncMargem = Short.valueOf(contrato.getAttribute("ade_inc_margem").toString());

                    // Se trocou de servidor ou é a primeira vez
                    if (!rseCodigo.equals(rseCodigoAnterior)) {
                        rseCodigoAnterior = rseCodigo;
                        margemFolha.clear();

                        final List<MargemTO> margens = consultarMargemController.consultarMargem(rseCodigo, null, null, null, null, true, false, true, null, responsavel);
                        for (final MargemTO margemTO : margens) {
                            margemFolha.put(margemTO.getMarCodigo(), margemTO.getMrsMargem());
                        }
                    }

                    adeCodigo = contrato.getAttribute("ade_codigo").toString();
                    adeVlr = new BigDecimal(contrato.getAttribute("ade_vlr").toString());

                    if (margemFolha.get(adeIncMargem).compareTo(adeVlr) >= 0) {
                        // Se a margem folha é maior ou igual ao ade_vlr, então subtrai o valor da margem e continua
                        margemFolha.put(adeIncMargem, margemFolha.get(adeIncMargem).subtract(adeVlr));
                    } else if (!permiteDescontoParcial || (margemFolha.get(adeIncMargem).signum() <= 0) ) {
                        // Se o valor da ade é maior que a margem, remove do movimento e continua
                        adeImpropria.addContratoSemMargem(adeCodigo);
                    } else {
                        // Se permite desconto parcial e a margem já ficou positiva, adiciona o contrato
                        // na lista daqueles que podem pagar parcialmente com o valor restante de margem
                        adeImpropria.addContratoParcialmenteSemMargem(adeCodigo, margemFolha.get(adeIncMargem));
                        margemFolha.put(adeIncMargem, margemFolha.get(adeIncMargem).subtract(adeVlr));
                    }
                }
            }
            return adeImpropria;
        } catch (final ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException(ex);
        }
    }
}