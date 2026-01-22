package com.zetra.econsig.folha.margem.impl;

import static com.zetra.econsig.values.CodedValues.IIA_ITEM_INCONSISTENCIA_INCIDENCIA_MARGEM;
import static com.zetra.econsig.values.CodedValues.SAD_CODIGOS_INATIVOS;
import static com.zetra.econsig.values.CodedValues.TPS_INCIDE_MARGEM;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.ImportaMargemException;
import com.zetra.econsig.folha.exportacao.impl.Exercito;
import com.zetra.econsig.folha.margem.ImportaMargemBase;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.BatchManager;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.MargemDAO;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.LiquidarConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ImportaMargemExercito</p>
 * <p>Description: Implementação específica para o sistema do Exército para
 * rotinas relacionadas a alteração do posto do registro servidor.</p>
 *
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ImportaMargemExercito extends ImportaMargemBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportaMargemExercito.class);
    private static final String OBS_IIA_ITEM_INC_MARGEM = "ImportaMargemExercito";

    private boolean aplicarRotinas(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) {
        if ("EST".equalsIgnoreCase(tipoEntidade) && (entCodigos != null) && !entCodigos.isEmpty() && entCodigos.contains(Exercito.EST_CODIGO_NAO_APLICADO)) {
            // DESENV-18011 : não executar as rotinas caso o estabelecimento seja o indicado acima
            return false;
        }
        return true;
    }

    @Override
    public void posImportacaoMargem(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ImportaMargemException {
        // DESENV-18603 : Executa rotina de remoção de incidência de margem mesmo para o EST_CODIGO_NAO_APLICADO
        ajustarIncMargemMilitarExterior(responsavel);

        if (aplicarRotinas(tipoEntidade, entCodigos, responsavel)) {
            // DESENV-16294 : Liquida contratos de servidores que não pertencem mais a determinados postos (12,13,14,15,16 e 28)
            liquidarAdePorPosto(responsavel);
        }
    }

    private void liquidarAdePorPosto(AcessoSistema responsavel) {
        LOG.debug("INICIANDO LIQUIDAÇÃO DE CONTRATOS QUE O POS_CODIGO NÃO SÃO MAIS 12, 13, 14, 15, 16, 28, 33 e 34");

        /**
         * 1) Códigos de verbas a se levar em consideração: ZQ6,ZB4,ZS2,ZD2,ZN5 e ZGV
         * 2) Caso os servidores mudem para um pos_codigo diferentes do 12, 13, 14, 15, 16, 28, 33 e 34, os contratos dessas verbas
         * deverão ser liquidados. A liquidação é somente para os contratos dessas verbas e que foram inseridos quando eram do
         * pos_codigo 12, 13, 14, 15, 16, 28, 33 e 34.
         * 3)no exército é gerado dois movimentos: 1 movimento normal (mensal) e outro, mais próximo do fechamento da folha,
         * onde recebemos uma margem atualizada, onde geramos uma espécie de movimento inicial, utilizando o reexporta,
         * para ajustar os contratos na folha, de acordo com a nova margem.
         * Caso ele mude de pós código no segundo movimento, o contrato liquidado deixará de ir apenas no próximo movimento.
         * Sendo assim liquidamos o contrato sempre com o período atual.
         * 4) Os tratamentos acima são apenas para os contratos averbados após 01/08/2021 (inclusive)
         * 5)Remover dessa lógica contratos que são pertinentes a servidores com matrículas iniciadas em 96 (inativos) e 98 (pensionistas)
        */

        final MargemDAO margemDAO = DAOFactory.getDAOFactory().getMargemDAO();
        final PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
        final AutorizacaoController autorizacaoController = ApplicationContextProvider.getApplicationContext().getBean("autorizacaoController", AutorizacaoController.class);
        final LiquidarConsignacaoController liquidarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(LiquidarConsignacaoController.class);

        final List<String> cnvCodVerba = new ArrayList<>();
        cnvCodVerba.add("ZQ6");
        cnvCodVerba.add("ZB4");
        cnvCodVerba.add("ZS2");
        cnvCodVerba.add("ZD2");
        cnvCodVerba.add("ZN5");
        cnvCodVerba.add("ZGV");

        final List<String> sadCodigos = new ArrayList<>(CodedValues.SAD_CODIGOS_ABERTOS_EXPORTACAO);
        final List<String> posCodigosLiquidacao = new ArrayList<>();
        posCodigosLiquidacao.add("12");
        posCodigosLiquidacao.add("13");
        posCodigosLiquidacao.add("14");
        posCodigosLiquidacao.add("15");
        posCodigosLiquidacao.add("16");
        posCodigosLiquidacao.add("28");
        posCodigosLiquidacao.add("33");
        posCodigosLiquidacao.add("34");

        try {
            final Date dataAplicaRegra = DateHelper.parse("2021-07-30 23:59:59", "yyyy-MM-dd HH:mm:ss");

            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_SDP, CodedValues.TPC_SIM, responsavel)) {
                final List<TransferObject> rseAlterados = margemDAO.lstTabelaPostoTipoRseAlterados(null, null);
                if ((rseAlterados != null) && !rseAlterados.isEmpty()) {
                    final BatchManager batman = new BatchManager(SessionUtil.getSession());

                    for (final TransferObject rseAlterado : rseAlterados) {
                        final String rseCodigo = rseAlterado.getAttribute(Columns.RSE_CODIGO).toString();
                        final String rseMatricula = (String) rseAlterado.getAttribute(Columns.RSE_MATRICULA);
                        final String posCodigo = (String) rseAlterado.getAttribute(Columns.POS_CODIGO);
                        final String posCodigoOld = (String) rseAlterado.getAttribute("pos_codigo_old");

                        // 5) Remover dessa lógica contratos que são pertinentes a servidores com matrículas iniciadas em 96 (inativos) e 98 (pensionistas)
                        if (!TextHelper.isNull(rseMatricula) && (rseMatricula.startsWith("96") || rseMatricula.startsWith("98"))) {
                            continue;
                        }

                        if (!TextHelper.isNull(posCodigo) && !TextHelper.isNull(posCodigoOld) && posCodigosLiquidacao.contains(posCodigoOld) && !posCodigosLiquidacao.contains(posCodigo)) {
                            final List<TransferObject> contratosPorVerba = pesquisarConsignacaoController.pesquisaAutorizacaoPorVerba(rseCodigo, cnvCodVerba, sadCodigos, responsavel);
                            if ((contratosPorVerba != null) && !contratosPorVerba.isEmpty()) {
                                for (final TransferObject contrato : contratosPorVerba) {
                                    final String adeCodigo = (String) contrato.getAttribute(Columns.ADE_CODIGO);
                                    final Date adeData = (Date) contrato.getAttribute(Columns.ADE_DATA);
                                    if (adeData.compareTo(dataAplicaRegra) <= 0) {
                                        continue;
                                    }
                                    liquidarConsignacaoController.liquidar(adeCodigo, null, null, responsavel);
                                    autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.informacao.liquidacao.mudanca.posto", responsavel), responsavel);
                                }
                            }
                        }

                        batman.iterate();
                    }

                    batman.finish();
                }
            }
        } catch (DAOException | AutorizacaoControllerException | ParseException ex) {
            LOG.debug(ex.getMessage());
        }
        LOG.debug("FIM LIQUIDAÇÃO DE CONTRATOS QUE O POS_CODIGO NÃO SÃO MAIS 12, 13, 14, 15, 16, 28, 33 e 34");
    }

    private void ajustarIncMargemMilitarExterior(AcessoSistema responsavel) {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            // DESENV-18603 : Inclui registro para ignorar inconsistência de incidência de margem que será alterada
            final StringBuilder query = new StringBuilder();
            query.append("REPLACE INTO tb_ignora_inconsistencia_ade (ADE_CODIGO, IIA_ITEM, IIA_DATA, IIA_OBS, IIA_USUARIO, IIA_PERMANENTE) ");
            query.append("SELECT ade.ade_codigo, ").append(IIA_ITEM_INCONSISTENCIA_INCIDENCIA_MARGEM).append(", now(), '").append(OBS_IIA_ITEM_INC_MARGEM).append("', 'eConsig', 1 ");
            query.append("FROM tb_aut_desconto ade ");
            query.append("INNER JOIN tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("INNER JOIN tb_orgao org on (cnv.org_codigo = org.org_codigo) ");
            query.append("WHERE ade.ade_inc_margem <> 0 ");
            query.append("  AND ade.sad_codigo NOT IN ('").append(TextHelper.join(SAD_CODIGOS_INATIVOS, "','")).append("') ");
            query.append("  AND org.est_codigo = '").append(Exercito.EST_CODIGO_NAO_APLICADO).append("' ");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            // DESENV-18603 : Remove a incidência de margem das consignações abertas de servidores que pertença ao estabelecimento SISTEMA DE RETRIBUIÇÃO DO EXTERIOR
            query.setLength(0);
            query.append("UPDATE tb_aut_desconto ade ");
            query.append("INNER JOIN tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("INNER JOIN tb_orgao org on (cnv.org_codigo = org.org_codigo) ");
            query.append("SET ade.ade_inc_margem = 0 ");
            query.append("WHERE ade.ade_inc_margem <> 0 ");
            query.append("  AND ade.sad_codigo NOT IN ('").append(TextHelper.join(SAD_CODIGOS_INATIVOS, "','")).append("') ");
            query.append("  AND org.est_codigo = '").append(Exercito.EST_CODIGO_NAO_APLICADO).append("' ");
            query.append("  AND EXISTS (");
            query.append("   SELECT 1 FROM tb_ignora_inconsistencia_ade iia ");
            query.append("   WHERE ade.ade_codigo = iia.ade_codigo");
            query.append("     AND iia.iia_item = ").append(IIA_ITEM_INCONSISTENCIA_INCIDENCIA_MARGEM);
            query.append("     AND iia.iia_obs = '").append(OBS_IIA_ITEM_INC_MARGEM).append("'");
            query.append("  )");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            // DESENV-18603 : Restaura a incidência de margem das consignações de servidores que não pertençam ao estabelecimento SISTEMA DE RETRIBUIÇÃO DO EXTERIOR
            query.setLength(0);
            query.append("UPDATE tb_aut_desconto ade ");
            query.append("INNER JOIN tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("INNER JOIN tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("INNER JOIN tb_orgao org on (cnv.org_codigo = org.org_codigo) ");
            query.append("SET ade.ade_inc_margem = ( ");
            query.append("  SELECT pse.pse_vlr ");
            query.append("  FROM tb_param_svc_consignante pse ");
            query.append("  WHERE pse.svc_codigo = cnv.svc_codigo ");
            query.append("    AND pse.tps_codigo = '").append(TPS_INCIDE_MARGEM).append("'");
            query.append(") ");
            query.append("WHERE ade.ade_inc_margem = 0 ");
            query.append("  AND ade.sad_codigo NOT IN ('").append(TextHelper.join(SAD_CODIGOS_INATIVOS, "','")).append("')");
            query.append("  AND org.est_codigo <> '").append(Exercito.EST_CODIGO_NAO_APLICADO).append("'");
            query.append("  AND EXISTS (");
            query.append("   SELECT 1 FROM tb_ignora_inconsistencia_ade iia ");
            query.append("   WHERE ade.ade_codigo = iia.ade_codigo");
            query.append("     AND iia.iia_item = ").append(IIA_ITEM_INCONSISTENCIA_INCIDENCIA_MARGEM);
            query.append("     AND iia.iia_obs = '").append(OBS_IIA_ITEM_INC_MARGEM).append("'");
            query.append("  )");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

            // DESENV-18603 : Remove a regra de desconsiderar a validação da incidência de margem para aqueles que não estão mais no EXTERIOR
            query.setLength(0);
            query.append("DELETE FROM tb_ignora_inconsistencia_ade iia ");
            query.append("WHERE iia.iia_item = ").append(IIA_ITEM_INCONSISTENCIA_INCIDENCIA_MARGEM);
            query.append("  AND iia.iia_obs = '").append(OBS_IIA_ITEM_INC_MARGEM).append("'");
            query.append("  AND EXISTS (");
            query.append("    SELECT 1 FROM tb_aut_desconto ade ");
            query.append("    INNER JOIN tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
            query.append("    INNER JOIN tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
            query.append("    INNER JOIN tb_orgao org on (cnv.org_codigo = org.org_codigo) ");
            query.append("    WHERE ade.ade_codigo = iia.ade_codigo");
            query.append("      AND ade.ade_inc_margem <> 0");
            query.append("      AND ade.sad_codigo NOT IN ('").append(TextHelper.join(SAD_CODIGOS_INATIVOS, "','")).append("')");
            query.append("      AND org.est_codigo <> '").append(Exercito.EST_CODIGO_NAO_APLICADO).append("'");
            query.append(")");
            LOG.debug(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }
}
