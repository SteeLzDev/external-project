package com.zetra.econsig.persistence.dao.mysql;

import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.servico.NaturezaRelSvc;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.generic.GenericServidorDAO;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: MySqlServidorDAO</p>
 * <p>Description: Implementacao do DAO de servidor para o MySql</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class MySqlServidorDAO extends GenericServidorDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(MySqlServidorDAO.class);

    /**
     * Atualiza a margem restante do servidor de acordo com a margem atualizada pela
     * folha e pela margem usada
     *
     * @param tipoEntidade
     * @param entCodigos
     * @throws DAOException
     */
    @Override
    public void calculaMargemRestante(String tipoEntidade, List<String> entCodigos) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        try {
            final StringBuilder query = new StringBuilder();
            final StringBuilder tabelas = new StringBuilder();
            final StringBuilder filtro = new StringBuilder();
            if ((entCodigos != null) && (entCodigos.size() > 0)) {
                if ("EST".equalsIgnoreCase(tipoEntidade)) {
                    tabelas.append(", ").append(Columns.TB_ORGAO);
                    filtro.append(" WHERE ").append(Columns.RSE_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO);
                    filtro.append(" AND ").append(Columns.ORG_EST_CODIGO).append(" IN (:entCodigos)");
                    queryParams.addValue("entCodigos", entCodigos);
                } else if ("ORG".equalsIgnoreCase(tipoEntidade)) {
                    filtro.append(" WHERE ").append(Columns.RSE_ORG_CODIGO).append(" IN (:entCodigos)");
                    queryParams.addValue("entCodigos", entCodigos);
                } else if ("RSE".equalsIgnoreCase(tipoEntidade)) {
                    filtro.append(" WHERE ").append(Columns.RSE_CODIGO).append(" IN (:entCodigos)");
                    queryParams.addValue("entCodigos", entCodigos);
                }
            }
            query.append("UPDATE ").append(Columns.TB_REGISTRO_SERVIDOR).append(tabelas).append(" SET ");
            query.append(Columns.RSE_MARGEM_REST).append(" = ").append(Columns.RSE_MARGEM).append(" - ").append(Columns.RSE_MARGEM_USADA).append(MySqlDAOFactory.SEPARADOR);
            query.append(Columns.RSE_MARGEM_REST_2).append(" = ").append(Columns.RSE_MARGEM_2).append(" - ").append(Columns.RSE_MARGEM_USADA_2).append(MySqlDAOFactory.SEPARADOR);
            query.append(Columns.RSE_MARGEM_REST_3).append(" = ").append(Columns.RSE_MARGEM_3).append(" - ").append(Columns.RSE_MARGEM_USADA_3);
            query.append(filtro);

            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Calcula a margem usada do servidor baseado nos contratos
     * existentes na base de dados
     *
     * @param tipoEntidade
     * @param entCodigos
     * @throws DAOException
     */
    @Override
    public void calculaMargemUsada(String tipoEntidade, List<String> entCodigos, boolean controlaMargem) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        try {
            String complemento = "";
            String tabelas = "";
            if ((entCodigos != null) && (entCodigos.size() > 0)) {
                if ("EST".equalsIgnoreCase(tipoEntidade)) {
                    tabelas = "inner join tb_orgao org on (rse.org_codigo = org.org_codigo) ";
                    complemento = " and org.est_codigo in (:entCodigos) ";
                    queryParams.addValue("entCodigos", entCodigos);
                } else if ("ORG".equalsIgnoreCase(tipoEntidade)) {
                    complemento = " and rse.org_codigo in (:entCodigos) ";
                    queryParams.addValue("entCodigos", entCodigos);
                } else if ("RSE".equalsIgnoreCase(tipoEntidade)) {
                    complemento = " and rse.rse_codigo in (:entCodigos) ";
                    queryParams.addValue("entCodigos", entCodigos);
                }
            }

            String query = "update tb_registro_servidor rse " + tabelas + " set rse_margem_usada = 0, rse_margem_usada_2 = 0, rse_margem_usada_3 = 0 where 1=1 " + complemento;
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "drop temporary table if exists tmp_margem_usada";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            /* --------------------------------------------------------------------------------------------------
             * PASSO 1) Calcula a margem usada inicial, de acordo com o parâmetro de sistema sobre
             * o controle ou não da margem pelo econsig:
             */
            if (!controlaMargem) {
                // Verifica se o valor dos contratos em andamento não pagos devem ser abatidas da margem enviada pela folha
                final boolean prendeVlrEmAndNaoPago = !ParamSist.paramEquals(CodedValues.TPC_RETEM_MARGEM_ADE_EM_ANDAMENTO_NAO_PAGO, CodedValues.TPC_NAO, AcessoSistema.getAcessoUsuarioSistema());

                /* --------------------------------------------------------------------------------------------------
                 * PASSO 1.1) Se não controla margem, a margem usada inicial terá apenas os contratos abertos que a
                 * folha ainda não possui, ou seja, aqueles onde a margem líquida enviada não está contemplando.
                 */
                query = "create temporary table tmp_margem_usada "
                        + "(rse_codigo varchar(32), margem_usada_1 decimal(13,2), margem_usada_2 decimal(13,2), margem_usada_3 decimal(13,2), KEY `tmpidx_rse` (`rse_codigo`)) "
                        + "select rse.rse_codigo, "
                        + "sum(if(ade_inc_margem = '1', ade_vlr, 0.00)) as margem_usada_1, "
                        + "sum(if(ade_inc_margem = '2', ade_vlr, 0.00)) as margem_usada_2, "
                        + "sum(if(ade_inc_margem = '3', ade_vlr, 0.00)) as margem_usada_3 "
                        + "from tb_aut_desconto ade "
                        + "inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) "
                        + tabelas
                        + "where (sad_codigo in ('" + TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_INCIDEM_MARGEM_LIQUIDA, "','") + "') "
                        + "or (sad_codigo in ('" + TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_INCIDEM_MARGEM_LIQ_SE_NAO_PAGOS, "','") + "') and coalesce(ade_paga, 'N') <> 'S' and ade_vlr_folha is null) "
                        + (prendeVlrEmAndNaoPago ? "or (sad_codigo = '5' and coalesce(ade_paga, 'N') <> 'S' and ade_vlr_folha is null))" : ")")
                        + "and ade_inc_margem in ('1','2','3') "
                        + complemento
                        + "group by rse.rse_codigo";
            } else {
                /* --------------------------------------------------------------------------------------------------
                 * PASSO 1.2) Se controla margem, a margem usada inicial terá todos os contratos abertos, pois
                 * a folha envia a margem bruta e o sistema deve subtrair desta os contratos abertos.
                 */
                query = "create temporary table tmp_margem_usada "
                        + "(rse_codigo varchar(32), margem_usada_1 decimal(13,2), margem_usada_2 decimal(13,2), margem_usada_3 decimal(13,2), KEY `tmpidx_rse` (`rse_codigo`)) "
                        + "select rse.rse_codigo, "
                        + "sum(if(ade_inc_margem = '1', ade_vlr, 0.00)) as margem_usada_1, "
                        + "sum(if(ade_inc_margem = '2', ade_vlr, 0.00)) as margem_usada_2, "
                        + "sum(if(ade_inc_margem = '3', ade_vlr, 0.00)) as margem_usada_3 "
                        + "from tb_aut_desconto ade "
                        + "inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) "
                        + tabelas
                        + "where sad_codigo in ('" + TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_INCIDEM_MARGEM_BRUTA, "','") + "') "
                        + "and ade_inc_margem in ('1','2','3') "
                        + complemento
                        + "group by rse.rse_codigo";
            }
            LOG.trace(query);
            jdbc.update(query, queryParams);

            // Os contratos deferidos ou em andamento que tenham a ocorrência de margem preza, precisam ser considerados com o valor total da renegocição e não o valor atual na margem usada.
            // Por este motivo precisamos adicionar a diferença à margem usada. Pois o valor do contrato novo ja está sendo considerado.
            if (ParamSist.getBoolParamSist(CodedValues.TPC_PRENDER_MARGEM_RENEG_CONTRATRO_NOVO_MENOR_ANTIGOS, AcessoSistema.getAcessoUsuarioSistema())) {
                query = "drop temporary table if exists tmp_margem_reneg_diferenca";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "create temporary table tmp_margem_reneg_diferenca "
                        + "(rse_codigo varchar(32), margem_usada_1 decimal(13,2), margem_usada_2 decimal(13,2), margem_usada_3 decimal(13,2), KEY `tmpDiferencaidx_rse` (`rse_codigo`)) "
                        + "select ade.rse_codigo, "
                        + "sum(if(ade_inc_margem = '1', (oca.oca_ade_vlr_ant - oca.oca_ade_vlr_novo), 0.00)) as margem_usada_1, "
                        + "sum(if(ade_inc_margem = '2', (oca.oca_ade_vlr_ant - oca.oca_ade_vlr_novo), 0.00)) as margem_usada_2, "
                        + "sum(if(ade_inc_margem = '3', (oca.oca_ade_vlr_ant - oca.oca_ade_vlr_novo), 0.00)) as margem_usada_3 "
                        + "from tb_aut_desconto ade "
                        + "inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) "
                        + "where sad_codigo in ('" + TextHelper.join(CodedValues.SAD_CODIGOS_DEFERIDAS_OU_ANDAMENTO, "','") + "') "
                        + "and oca.toc_codigo = '" + CodedValues.TOC_RETENCAO_MARGEM_DENTRO_PRAZO_RENEGOCIACAO + "' "
                        + " AND NOT EXISTS (SELECT 1 FROM tb_ocorrencia_autorizacao oca1 WHERE oca1.ade_codigo = ade.ade_codigo "
                        + " AND oca1.toc_codigo ='" + CodedValues.TOC_LIBERACAO_MARGEM_APOS_PRAZO_RENEGOCIACAO + "' "
                        + " ) "
                        + "and ade_inc_margem in ('1','2','3') "
                        + "group by ade.rse_codigo";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "update tmp_margem_usada tmp "
                        + "inner join tmp_margem_reneg_diferenca ren on (tmp.rse_codigo = ren.rse_codigo) "
                        + "set tmp.margem_usada_1 = tmp.margem_usada_1 + ren.margem_usada_1 "
                        + "where ren.margem_usada_1 > 0.00";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "update tmp_margem_usada tmp "
                        + "inner join tmp_margem_reneg_diferenca ren on (tmp.rse_codigo = ren.rse_codigo) "
                        + "set tmp.margem_usada_2 = tmp.margem_usada_2 + ren.margem_usada_2 "
                        + "where ren.margem_usada_2 > 0.00";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "update tmp_margem_usada tmp "
                        + "inner join tmp_margem_reneg_diferenca ren on (tmp.rse_codigo = ren.rse_codigo) "
                        + "set tmp.margem_usada_3 = tmp.margem_usada_3 + ren.margem_usada_3 "
                        + "where ren.margem_usada_3 > 0.00";
                LOG.trace(query);
                jdbc.update(query, queryParams);
            }

            // DESENV-16566 - Adiciona os contratos em carência, porém já exportados para serem liberados da margem
            if(!controlaMargem && ParamSist.getBoolParamSist(CodedValues.TPC_LIBERA_MARGEM_ENVIADA_PELA_FOLHA_CONSIG_CARENCIA, AcessoSistema.getAcessoUsuarioSistema())
                    && ParamSist.getBoolParamSist(CodedValues.TPC_ENVIA_CONTRATOS_CARENCIA_MOV_FIN, AcessoSistema.getAcessoUsuarioSistema())) {
                query = "drop temporary table if exists tmp_margem_carencia_usada";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "drop temporary table if exists tmp_apoio_total_contratos_carencia_usada";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "drop temporary table if exists tmp_margem_contratos_pagos_carencia_usada";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "create temporary table tmp_apoio_total_contratos_carencia_usada "
                        + "(rse_codigo varchar(32), total int, KEY `tmpApoioTotalidx_rse` (`rse_codigo`)) "
                        + "select ade.rse_codigo, count(*) "
                        + "from tb_aut_desconto ade "
                        + "inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) "
                        + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                        + "inner join tb_periodo_exportacao pex on (cnv.org_codigo = pex.org_codigo) "
                        + "inner join tb_parcela_desconto prd on (ade.ade_codigo = prd.ade_codigo) "
                        + "where prd.prd_data_desconto = pex.pex_periodo "
                        + "group by ade.rse_codigo ";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "create temporary table tmp_margem_contratos_pagos_carencia_usada "
                        + "(rse_codigo varchar(32), total int, KEY `tmpApoioPagasidx_rse` (`rse_codigo`)) "
                        + "select ade.rse_codigo, count(*) "
                        + "from tb_aut_desconto ade "
                        + "inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) "
                        + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                        + "inner join tb_periodo_exportacao pex on (cnv.org_codigo = pex.org_codigo) "
                        + "inner join tb_parcela_desconto prd on (ade.ade_codigo = prd.ade_codigo) "
                        + "where prd.prd_data_desconto = pex.pex_periodo "
                        + "and prd.spd_codigo in ('"+CodedValues.SPD_LIQUIDADAFOLHA+"','"+CodedValues.SPD_LIQUIDADAMANUAL+"') "
                        + "group by ade.rse_codigo ";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "delete tmpTotal.* from tmp_apoio_total_contratos_carencia_usada tmpTotal "
                        + "inner join tmp_margem_contratos_pagos_carencia_usada tmpPagos on (tmpTotal.rse_codigo = tmpPagos.rse_codigo) "
                        + "where tmpTotal.total != tmpPagos.total";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "create temporary table tmp_margem_carencia_usada "
                        + "(rse_codigo varchar(32), margem_usada_1 decimal(13,2), margem_usada_2 decimal(13,2), margem_usada_3 decimal(13,2), KEY `tmpCarenciaidx_rse` (`rse_codigo`)) "
                        + "select rse.rse_codigo, "
                        + "sum(if(ade_inc_margem = '1', ade_vlr, 0.00)) as margem_usada_1, "
                        + "sum(if(ade_inc_margem = '2', ade_vlr, 0.00)) as margem_usada_2, "
                        + "sum(if(ade_inc_margem = '3', ade_vlr, 0.00)) as margem_usada_3 "
                        + "from tb_aut_desconto ade "
                        + "inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) "
                        + "inner join tmp_apoio_total_contratos_carencia_usada tmpApoio on (rse.rse_codigo = tmpApoio.rse_codigo) "
                        + "inner join tb_periodo_exportacao pex on (rse.org_codigo = pex.org_codigo) "
                        + tabelas
                        + "where sad_codigo in ('" + TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_INCIDEM_MARGEM_LIQUIDA, "','") + "') "
                        + "and ade.ade_ult_periodo_exportacao = pex.pex_periodo "
                        + "and ade.ade_ano_mes_ini > ade.ade_ult_periodo_exportacao "
                        + "and ade.ade_ano_mes_ini > pex.pex_periodo "
                        + "and ade_inc_margem in ('1','2','3') "
                        + complemento
                        + "group by rse.rse_codigo";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "update tmp_margem_usada tmpUsada "
                        + "inner join tmp_margem_carencia_usada tmpCarencia on (tmpCarencia.rse_codigo = tmpUsada.rse_codigo) "
                        + "set tmpusada.margem_usada_1 = tmpusada.margem_usada_1-tmpCarencia.margem_usada_1 "
                        + ", tmpusada.margem_usada_2 = tmpusada.margem_usada_1-tmpCarencia.margem_usada_2 "
                        + ", tmpusada.margem_usada_3 = tmpusada.margem_usada_1-tmpCarencia.margem_usada_3 ";
                LOG.trace(query);
                jdbc.update(query, queryParams);
            }

            /* --------------------------------------------------------------------------------------------------
             * PASSO 2) Adiciona à margem usada a diferença positiva entre contratos renegociados/comprados, ou
             * seja caso uma renegociação/compra seja com valor maior que o anterior, a diferença deve ser presa.
             */
            query = "drop temporary table if exists tmp_margem_usada_reneg";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "create temporary table tmp_margem_usada_reneg "
                  + " (rse_codigo varchar(32), ade_codigo varchar(32), ade_vlr_1 decimal(13,2), ade_vlr_2 decimal(13,2), ade_vlr_3 decimal(13,2), "
                  + "  usada_1 decimal(13,2), usada_2 decimal(13,2), usada_3 decimal(13,2), KEY `tmpidx_rse` (`rse_codigo`))"
                  + " select ade.rse_codigo, ade.ade_codigo,"
                  + " if(ade11.ade_inc_margem = '1', ade.ade_vlr, 0.00) ade_vlr_1,"
                  + " if(ade11.ade_inc_margem = '2', ade.ade_vlr, 0.00) ade_vlr_2,"
                  + " if(ade11.ade_inc_margem = '3', ade.ade_vlr, 0.00) ade_vlr_3,"
                  + " sum(if(ade11.ade_inc_margem = '1', ade11.ade_vlr, 0.00)) usada_1,"
                  + " sum(if(ade11.ade_inc_margem = '2', ade11.ade_vlr, 0.00)) usada_2,"
                  + " sum(if(ade11.ade_inc_margem = '3', ade11.ade_vlr, 0.00)) usada_3 "
                  + " from tb_aut_desconto ade11"
                  + " inner join tb_relacionamento_autorizacao rad11 on (ade11.ade_codigo = rad11.ade_codigo_origem)"
                  + " inner join tb_aut_desconto ade on (ade.ade_codigo = rad11.ade_codigo_destino)"
                  + " inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo)"
                  + tabelas
                  + " where ade11.sad_codigo in ('" + TextHelper.join(CodedValues.SAD_CODIGOS_LIQUIDACAO, "','") + "') "
                  + " and ade.sad_codigo in ('1', '2') "
                  + " and tnt_codigo in ('6', '7') "
                  + " and ade11.ade_inc_margem in ('1', '2', '3') "
                  + " and ade.ade_inc_margem = '0' "
                  + complemento
                  + " group by ade.rse_codigo, ade.ade_codigo";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            final boolean habilitaModuloBeneficio = ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_BENEFICIOS_SAUDE, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            if (habilitaModuloBeneficio) {
                /*
                 * Caso o módulo de benefícios esteja habilitado, adiciona à margem usada a diferença positiva entre contratos
                 * migrados, ou seja, caso uma migração de benefícios seja realizada com valor maior que o anterior, a diferença
                 * deve ser presa.
                 * A diferença é calculada utilizando a soma dos valores das autorizações de desconto de mensalidade do grupo familiar.
                 */
                query = "insert into tmp_margem_usada_reneg (rse_codigo, ade_vlr_1, ade_vlr_2, ade_vlr_3, usada_1, usada_2, usada_3) "
                        + "select rse.rse_codigo, "
                        + "if(adeOri.ade_inc_margem = '1', adeDes.ade_vlr, 0.00) ade_vlr_1, "
                        + "if(adeOri.ade_inc_margem = '2', adeDes.ade_vlr, 0.00) ade_vlr_2, "
                        + "if(adeOri.ade_inc_margem = '3', adeDes.ade_vlr, 0.00) ade_vlr_3, "
                        + "sum(if(adeOri.ade_inc_margem = '1', adeOri.ade_vlr, 0.00)) usada_1, "
                        + "sum(if(adeOri.ade_inc_margem = '2', adeOri.ade_vlr, 0.00)) usada_2, "
                        + "sum(if(adeOri.ade_inc_margem = '3', adeOri.ade_vlr, 0.00)) usada_3 "
                        + "from tb_registro_servidor rse "
                        + "inner join ( "
                        + "select rse.rse_codigo, adeGF.ade_inc_margem, sum(coalesce(adeGF.ade_vlr,0)) as ade_vlr "
                        + "from tb_aut_desconto adeOri "
                        // relacionamento de controle de migração de benefício (tnt_codigo=53)
                        + "inner join tb_relacionamento_autorizacao rad on (adeOri.ade_codigo = rad.ade_codigo_origem and rad.tnt_codigo in ('53')) "
                        + "inner join tb_aut_desconto adeDes on (adeDes.ade_codigo = rad.ade_codigo_destino) "
                        + "inner join tb_registro_servidor rse on (adeDes.rse_codigo = rse.rse_codigo) "
                        + "inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo) "
                        + "inner join tb_tipo_lancamento tlaTit on (adeOri.tla_codigo = tlaTit.tla_codigo) "
                        + "inner join tb_tipo_natureza tntTit on (tlaTit.tnt_codigo = tntTit.tnt_codigo) "
                        + "inner join tb_contrato_beneficio cbeTit on (adeOri.cbe_codigo = cbeTit.cbe_codigo) "
                        // garante que está buscando o relacionamento apenas do titular para evitar duplicidade
                        + "inner join tb_beneficiario bfcTit on (cbeTit.bfc_codigo = bfcTit.bfc_codigo and bfcTit.tib_codigo = '1') "
                        + "inner join tb_beneficiario bfcGF on (ser.ser_codigo = bfcGF.ser_codigo) "
                        + "inner join tb_contrato_beneficio cbeGF on (bfcGF.bfc_codigo = cbeGF.bfc_codigo) "
                        + "inner join tb_aut_desconto adeGF on (cbeGF.cbe_codigo = adeGF.cbe_codigo) "
                        + "inner join tb_tipo_lancamento tlaGF on (adeGF.tla_codigo = tlaGF.tla_codigo) "
                        + "inner join tb_tipo_natureza tntGF on (tlaGF.tnt_codigo = tntGF.tnt_codigo) "
                        + "left outer join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = adeOri.ade_codigo and oca.toc_codigo = '6') "
                        + "left outer join tb_ocorrencia_autorizacao ocaGF on (ocaGF.ade_codigo = adeGF.ade_codigo and ocaGF.toc_codigo = '6') "
                        + tabelas
                        + "where 1=1  "
                        + complemento
                        + "and cbeTit.ben_codigo = cbeGF.ben_codigo "
                        + "and adeGF.ade_inc_margem in ('1','2','3') "
                        // caso as ADEs já estejam liquidadas, garante a soma apenas das que foram liquidados na mesma operação,
                        // evitando somar o valor de um beneficiário que foi excluído a mais tempo
                        + "and (adeGF.sad_codigo in ('5','11','15') or (adeGF.sad_codigo = '8' and ocaGF.oca_data >= oca.oca_data)) "
                        // tipos de lançamentos de natureza de mensalidade (tnt_codigo in ('25','26'))
                        + "and tntTit.tnt_codigo in ('25','26') "
                        + "and tntGF.tnt_codigo in ('25','26') "
                        // contratos de benefícios de origem com status: Ativo, Cancelamento Solicitado; Aguard. Exclusão Operadora; Cancelado;
                        + "and cbeGF.scb_codigo in ('3','4','5','6') "
                        + "group by rse.rse_codigo, adeGF.ade_inc_margem "
                        + ") adeOri on (adeOri.rse_codigo = rse.rse_codigo) "
                        + "inner join ( "
                        + "select rse.rse_codigo, adeGF.ade_inc_margem, sum(coalesce(adeGF.ade_vlr,0)) as ade_vlr "
                        + "from tb_aut_desconto adeOri "
                        // relacionamento de controle de migração de benefício (tnt_codigo=53)
                        + "inner join tb_relacionamento_autorizacao rad on (adeOri.ade_codigo = rad.ade_codigo_origem and rad.tnt_codigo in ('53')) "
                        + "inner join tb_aut_desconto adeDes on (adeDes.ade_codigo = rad.ade_codigo_destino) "
                        + "inner join tb_registro_servidor rse on (adeDes.rse_codigo = rse.rse_codigo) "
                        + "inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo) "
                        + "inner join tb_tipo_lancamento tlaTit on (adeOri.tla_codigo = tlaTit.tla_codigo) "
                        + "inner join tb_tipo_natureza tntTit on (tlaTit.tnt_codigo = tntTit.tnt_codigo) "
                        + "inner join tb_contrato_beneficio cbeTit on (adeDes.cbe_codigo = cbeTit.cbe_codigo) "
                        // garante que está buscando o relacionamento apenas do titular para evitar duplicidade
                        + "inner join tb_beneficiario bfcTit on (cbeTit.bfc_codigo = bfcTit.bfc_codigo and bfcTit.tib_codigo = '1') "
                        + "inner join tb_beneficiario bfcGF on (ser.ser_codigo = bfcGF.ser_codigo) "
                        + "inner join tb_contrato_beneficio cbeGF on (bfcGF.bfc_codigo = cbeGF.bfc_codigo) "
                        + "inner join tb_aut_desconto adeGF on (cbeGF.cbe_codigo = adeGF.cbe_codigo) "
                        + "inner join tb_tipo_lancamento tlaGF on (adeGF.tla_codigo = tlaGF.tla_codigo) "
                        + "inner join tb_tipo_natureza tntGF on (tlaGF.tnt_codigo = tntGF.tnt_codigo) "
                        + tabelas
                        + "where 1=1 "
                        + complemento
                        + "and adeGF.ade_inc_margem in ('0') "
                        + "and adeGF.sad_codigo in ('0','1','2') "
                        + "and cbeTit.ben_codigo = cbeGF.ben_codigo "
                        // tipos de lançamentos de natureza de mensalidade (tnt_codigo in ('25','26'))
                        + "and tntTit.tnt_codigo in ('25','26') "
                        + "and tntGF.tnt_codigo in ('25','26') "
                        // contratos de benefícios de destino com status: Solicitado; Aguard. Inclusão Operadora;
                        + "and cbeGF.scb_codigo in ('1','2') "
                        + "group by rse.rse_codigo, adeGF.ade_inc_margem "
                        + ") adeDes on (adeDes.rse_codigo = rse.rse_codigo) "
                        + "group by rse.rse_codigo ";

                LOG.trace(query);
                jdbc.update(query, queryParams);
            }

            query = "drop temporary table if exists tmp_margem_reneg";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "create temporary table tmp_margem_reneg "
                  + " (rse_codigo varchar(32), margem_usada_1 decimal(13,2), margem_usada_2 decimal(13,2), margem_usada_3 decimal(13,2), KEY `tmpidx_rse` (`rse_codigo`)) "
                  + " select rse_codigo, "
                  + " sum(if(ade_vlr_1 > usada_1, (ade_vlr_1 - usada_1), 0.00)) margem_usada_1,"
                  + " sum(if(ade_vlr_2 > usada_2, (ade_vlr_2 - usada_2), 0.00)) margem_usada_2,"
                  + " sum(if(ade_vlr_3 > usada_3, (ade_vlr_3 - usada_3), 0.00)) margem_usada_3 "
                  + " from tmp_margem_usada_reneg"
                  + " group by rse_codigo";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "update tmp_margem_usada tmp "
                  + "inner join tmp_margem_reneg ren on (tmp.rse_codigo = ren.rse_codigo) "
                  + "set tmp.margem_usada_1 = tmp.margem_usada_1 + ren.margem_usada_1 "
                  + "where ren.margem_usada_1 > 0.00";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "update tmp_margem_usada tmp "
                  + "inner join tmp_margem_reneg ren on (tmp.rse_codigo = ren.rse_codigo) "
                  + "set tmp.margem_usada_2 = tmp.margem_usada_2 + ren.margem_usada_2 "
                  + "where ren.margem_usada_2 > 0.00";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "update tmp_margem_usada tmp "
                  + "inner join tmp_margem_reneg ren on (tmp.rse_codigo = ren.rse_codigo) "
                  + "set tmp.margem_usada_3 = tmp.margem_usada_3 + ren.margem_usada_3 "
                  + "where ren.margem_usada_3 > 0.00";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "delete from tmp_margem_reneg "
                  + "using tmp_margem_reneg "
                  + "inner join tmp_margem_usada tmp using (rse_codigo)";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "insert into tmp_margem_usada "
                  + "select ren.rse_codigo, "
                  + " if(ren.margem_usada_1 > 0.00, ren.margem_usada_1, 0.00) as margem_usada_1, "
                  + " if(ren.margem_usada_2 > 0.00, ren.margem_usada_2, 0.00) as margem_usada_2, "
                  + " if(ren.margem_usada_3 > 0.00, ren.margem_usada_3, 0.00) as margem_usada_3 "
                  + "from tmp_margem_reneg ren "
                  + "where ren.margem_usada_1 > 0.00 or ren.margem_usada_2 > 0.00 or ren.margem_usada_3 > 0.00";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            /* --------------------------------------------------------------------------------------------------
             * PASSO 3) Atualiza a tabela de registro servidor, armazenando o valor da margem usada inicial
             * calculada pelas rotinas acima. Os próximos cálculos será feitos diretamente sobre a própria
             * tabela de registro servidor.
             */
            query = "update tb_registro_servidor rse "
                  + "inner join tmp_margem_usada tmp on (tmp.rse_codigo = rse.rse_codigo) "
                  + "set rse_margem_usada   = margem_usada_1,"
                  + "    rse_margem_usada_2 = margem_usada_2,"
                  + "    rse_margem_usada_3 = margem_usada_3";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            /* --------------------------------------------------------------------------------------------------
             * PASSO 4) Adiciona à margem usada as autorizações liquidadas neste período, ou seja aquelas
             * que ainda não foram para a folha e que fazem parte de renegociação/compra não concluída,
             * pois margem de compra/renegociação só deve ser liberada após a conclusão do processo.
             */
            query = "drop temporary table if exists tmp_margem_liquidada_reneg";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "create temporary table tmp_margem_liquidada_reneg "
                  + "(rse_codigo varchar(32), margem_liquidada_1 decimal(13,2), margem_liquidada_2 decimal(13,2), margem_liquidada_3 decimal(13,2), KEY `tmpidx_rse` (`rse_codigo`)) "
                  + "select rse.rse_codigo, "
                  + "sum(if(ade8.ade_inc_margem = '1', if(ade8.ade_tipo_vlr = 'P', coalesce(ade8.ade_vlr_folha, ade8.ade_vlr), ade8.ade_vlr), 0.00)) as margem_liquidada_1, "
                  + "sum(if(ade8.ade_inc_margem = '2', if(ade8.ade_tipo_vlr = 'P', coalesce(ade8.ade_vlr_folha, ade8.ade_vlr), ade8.ade_vlr), 0.00)) as margem_liquidada_2, "
                  + "sum(if(ade8.ade_inc_margem = '3', if(ade8.ade_tipo_vlr = 'P', coalesce(ade8.ade_vlr_folha, ade8.ade_vlr), ade8.ade_vlr), 0.00)) as margem_liquidada_3 "
                  + "from tb_aut_desconto ade8 "
                  + "inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade8.ade_codigo) "
                  + "inner join tb_relacionamento_autorizacao rad8 on (ade8.ade_codigo = rad8.ade_codigo_origem) "
                  + "inner join tb_aut_desconto ade on (ade.ade_codigo = rad8.ade_codigo_destino) "
                  + "inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) "
                  + tabelas
                  + "where ade8.ade_inc_margem in ('1', '2', '3') "
                  + "and ade8.sad_codigo = '8' "
                  + "and toc_codigo = '6' "
                  + "and oca_data > rad_data "
                  + "and tnt_codigo in ('6', '7') "
                  + "and ade.sad_codigo in ('1', '2') "
                  + "and ade.ade_inc_margem = '0' "
                  + complemento
                  + "group by rse.rse_codigo";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            if (habilitaModuloBeneficio) {
                /*
                 * Adiciona à margem usada as autorizações liquidadas neste período, ou seja aquelas
                 * que ainda não foram para a folha e que fazem parte de migração de benefícios não concluída,
                 * pois a margem de migração de benefícios só deve ser liberada após a conclusão do processo.
                 * A diferença é calculada utilizando a soma dos valores das autorizações de desconto de mensalidade do grupo familiar.
                 */
                query = "insert into tmp_margem_liquidada_reneg(rse_codigo, margem_liquidada_1, margem_liquidada_2, margem_liquidada_3) "
                      + "select rse.rse_codigo, "
                      + "sum(if(adeOri.ade_inc_margem = '1', if(adeOri.ade_tipo_vlr = 'P', coalesce(adeOri.ade_vlr_folha, adeOri.ade_vlr), adeOri.ade_vlr), 0.00)) as margem_liquidada_1, "
                      + "sum(if(adeOri.ade_inc_margem = '2', if(adeOri.ade_tipo_vlr = 'P', coalesce(adeOri.ade_vlr_folha, adeOri.ade_vlr), adeOri.ade_vlr), 0.00)) as margem_liquidada_2, "
                      + "sum(if(adeOri.ade_inc_margem = '3', if(adeOri.ade_tipo_vlr = 'P', coalesce(adeOri.ade_vlr_folha, adeOri.ade_vlr), adeOri.ade_vlr), 0.00)) as margem_liquidada_3 "
                      + "from tb_registro_servidor rse "
                      + "join ( "
                      + "select rse.rse_codigo, adeGF.ade_inc_margem, adeGF.ade_tipo_vlr, sum(coalesce(adeGF.ade_vlr,0)) as ade_vlr, sum(coalesce(adeGF.ade_vlr_folha,0)) as ade_vlr_folha "
                      + "from tb_aut_desconto adeOri "
                      + "inner join tb_relacionamento_autorizacao rad on (adeOri.ade_codigo = rad.ade_codigo_origem and rad.tnt_codigo in ('53')) "
                      + "inner join tb_aut_desconto adeDes on (adeDes.ade_codigo = rad.ade_codigo_destino) "
                      + "inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = adeOri.ade_codigo) "
                      + "inner join tb_registro_servidor rse on (adeDes.rse_codigo = rse.rse_codigo) "
                      + "inner join tb_servidor ser on (rse.ser_codigo = ser.ser_codigo) "
                      + "inner join tb_tipo_lancamento tlaTit on (adeOri.tla_codigo = tlaTit.tla_codigo) "
                      + "inner join tb_tipo_natureza tntTit on (tlaTit.tnt_codigo = tntTit.tnt_codigo) "
                      + "inner join tb_contrato_beneficio cbeTit on (adeOri.cbe_codigo = cbeTit.cbe_codigo) "
                      // garante que está buscando o relacionamento apenas do titular para evitar duplicidade
                      + "inner join tb_beneficiario bfcTit on (cbeTit.bfc_codigo = bfcTit.bfc_codigo and bfcTit.tib_codigo = '1') "
                      + "inner join tb_beneficiario bfcGF on (ser.ser_codigo = bfcGF.ser_codigo) "
                      + "inner join tb_contrato_beneficio cbeGF on (bfcGF.bfc_codigo = cbeGF.bfc_codigo) "
                      + "inner join tb_aut_desconto adeGF on (cbeGF.cbe_codigo = adeGF.cbe_codigo) "
                      + "inner join tb_tipo_lancamento tlaGF on (adeGF.tla_codigo = tlaGF.tla_codigo) "
                      + "inner join tb_tipo_natureza tntGF on (tlaGF.tnt_codigo = tntGF.tnt_codigo) "
                      + "inner join tb_ocorrencia_autorizacao ocaGF on (ocaGF.ade_codigo = adeGF.ade_codigo) "
                      + tabelas
                      + "where 1=1  "
                      + complemento
                      + "and adeOri.ade_inc_margem in ('1','2','3') "
                      + "and adeDes.ade_inc_margem in ('0') "
                      + "and adeOri.sad_codigo = '8' "
                      + "and oca.toc_codigo = '6' "
                      + "and oca.oca_data >= rad.rad_data "
                      // garante a soma apenas das que foram liquidados na mesma operação, evitando somar o valor de um beneficiário
                      // que foi excluído a mais tempo
                      + "and ocaGF.oca_data >= oca.oca_data "
                      + "and cbeTit.ben_codigo = cbeGF.ben_codigo "
                      // tipos de lançamentos de natureza de mensalidade (tnt_codigo in ('25','26'))
                      + "and tntTit.tnt_codigo in ('25','26') "
                      + "and tntGF.tnt_codigo in ('25','26') "
                      + "group by rse.rse_codigo, adeGF.ade_inc_margem, adeGF.ade_tipo_vlr "
                      + ") adeOri on (adeOri.rse_codigo = rse.rse_codigo) "
                      + "group by rse.rse_codigo ";

                LOG.trace(query);
                jdbc.update(query, queryParams);
            }

            query = "update tb_registro_servidor rse "
                  + "inner join tmp_margem_liquidada_reneg tmp on (tmp.rse_codigo = rse.rse_codigo) "
                  + "set rse_margem_usada   = rse_margem_usada + margem_liquidada_1,"
                  + "    rse_margem_usada_2 = rse_margem_usada_2 + margem_liquidada_2,"
                  + "    rse_margem_usada_3 = rse_margem_usada_3 + margem_liquidada_3";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            if (!controlaMargem) {
                // Busca parâmetros de sistema adicionais
                final boolean liberaMargemConclusaoContrato = ParamSist.paramEquals(CodedValues.TPC_LIB_MARGEM_CONCLUSAO_CONTRATO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                final boolean liberaMargemContratoLiqNaoPago = ParamSist.paramEquals(CodedValues.TPC_LIB_MARGEM_LIQ_CONTRATO_NAO_PAGO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                final Object paramCarenciaFolha = ParamSist.getInstance().getParam(CodedValues.TPC_CARENCIA_CONCLUSAO_FOLHA, AcessoSistema.getAcessoUsuarioSistema());
                int carenciaFolha = 0;
                if (!TextHelper.isNull(paramCarenciaFolha)) {
                    carenciaFolha = Integer.parseInt(paramCarenciaFolha.toString());
                }

                /* --------------------------------------------------------------------------------------------------
                 * PASSO 5) Se não controla margem, subtrai da margem usada as autorizações liquidadas neste período,
                 * ou seja aquelas que ainda não foram para a folha, pois a margem líquida não terá este valor.
                 */
                query = "drop temporary table if exists tmp_margem_liquidada";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "create temporary table tmp_margem_liquidada "
                      + "(rse_codigo varchar(32), margem_liquidada_1 decimal(13,2), margem_liquidada_2 decimal(13,2), margem_liquidada_3 decimal(13,2), KEY `tmpidx_rse` (`rse_codigo`)) "
                      + "select rse.rse_codigo, "
                      + "sum(if(ade_inc_margem = '1', if(ade_tipo_vlr = 'P', coalesce(ade_vlr_folha, ade_vlr), ade_vlr), 0.00)) as margem_liquidada_1, "
                      + "sum(if(ade_inc_margem = '2', if(ade_tipo_vlr = 'P', coalesce(ade_vlr_folha, ade_vlr), ade_vlr), 0.00)) as margem_liquidada_2, "
                      + "sum(if(ade_inc_margem = '3', if(ade_tipo_vlr = 'P', coalesce(ade_vlr_folha, ade_vlr), ade_vlr), 0.00)) as margem_liquidada_3 "
                      + "from tb_aut_desconto ade "
                      + "inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) "
                      + "inner join tb_periodo_exportacao pex on (rse.org_codigo = pex.org_codigo) "
                      + tabelas
                      + "where ade_inc_margem in ('1', '2', '3') "
                      + "and ade_prd_pagas > 0 "
                      + "and sad_codigo = '8' "
                      + "and exists ( "
                      + "     select 1 from tb_ocorrencia_autorizacao oca where oca.ade_codigo = ade.ade_codigo "
                      + "     and toc_codigo = '6' "
                      + "     and oca_data > pex.pex_data_fim "
                      + ") "
                      + (!liberaMargemContratoLiqNaoPago ? "and coalesce(ade_vlr_folha, 0.00) > 0 " : "")
                      + (!liberaMargemConclusaoContrato ? "and (coalesce(ade_prazo, 999999999) > coalesce(ade_prd_pagas, 0) "
                      + " or date_add(ade_ano_mes_fim, interval coalesce(ade_carencia_final, 0) + " + carenciaFolha + " month) > (select pex_periodo from tb_periodo_exportacao pex where rse.org_codigo = pex.org_codigo)) " : "")
                      + complemento
                      + "group by rse.rse_codigo";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "update tb_registro_servidor rse "
                      + "inner join tmp_margem_liquidada tmp on (tmp.rse_codigo = rse.rse_codigo) "
                      + "set rse_margem_usada   = rse_margem_usada - margem_liquidada_1,"
                      + "    rse_margem_usada_2 = rse_margem_usada_2 - margem_liquidada_2,"
                      + "    rse_margem_usada_3 = rse_margem_usada_3 - margem_liquidada_3";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                if (liberaMargemConclusaoContrato) {
                    /* --------------------------------------------------------------------------------------------------
                     * PASSO 6) Se não controla margem, subtrai da margem usada as autorizações concluidas neste período,
                     * ou seja aquelas que a folha ainda abateu na margem por terem sido concluidas no mês atual.
                     */
                    query = "drop temporary table if exists tmp_margem_concluida";
                    LOG.trace(query);
                    jdbc.update(query, queryParams);

                    /*
                     * O padrão (mesmo que não exista o parâmetro no banco de dados) é que a margem seja
                     * liberada para os contratos concluídos por desconto de férias. Para que a margem não
                     * seja liberada é preciso que se inclua o parâmetro no banco de dados com valor NAO.
                     */
                    String clausulaConclusaoFerias = "";
                    if (ParamSist.paramEquals(CodedValues.TPC_LIB_MARGEM_CONCLUSAO_CONTRATO_FERIAS, CodedValues.TPC_NAO, AcessoSistema.getAcessoUsuarioSistema())) {
                        clausulaConclusaoFerias = "and not exists (select 1 from tb_parcela_desconto prd "
                                                + "where prd.ade_codigo = ade.ade_codigo "
                                                + "and prd.spd_codigo = '6' "
                                                + "and prd.prd_data_desconto > pex.pex_periodo) ";
                    }

                    /*
                     * Se existir relacionamento de saldo de parcelas, então só deve liberar margem
                     * de contratos concluídos que não tenham saldo de parcela aberto. Estes só terão
                     * a margem restituída na conclusão ou liquidação do saldo de parcela.
                     */
                    String clausulaSaldoParcelas = "";
                    if (NaturezaRelSvc.getInstance().exists(CodedValues.TNT_SALDO_PARCELAS)) {
                        clausulaSaldoParcelas = "and not exists (select 1 from tb_verba_convenio vco "
                                              + "inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) "
                                              + "inner join tb_relacionamento_servico rsv on (cnv.svc_codigo = rsv.svc_codigo_origem and rsv.tnt_codigo = '" + CodedValues.TNT_SALDO_PARCELAS + "') "
                                              + "inner join tb_convenio cnvSaldo on (cnvSaldo.svc_codigo = rsv.svc_codigo_destino and cnv.csa_codigo = cnvSaldo.csa_codigo and cnv.org_codigo = cnvSaldo.org_codigo) "
                                              + "inner join tb_verba_convenio vcoSaldo on (vcoSaldo.cnv_codigo = cnvSaldo.cnv_codigo) "
                                              + "inner join tb_aut_desconto adeSaldo on (adeSaldo.vco_codigo = vcoSaldo.vco_codigo) "
                                              + "where vco.vco_codigo = ade.vco_codigo "
                                              + "and adeSaldo.rse_codigo = ade.rse_codigo "
                                              + "and adeSaldo.ade_indice = ade.ade_indice "
                                              + "and adeSaldo.sad_codigo in ('" + TextHelper.join(CodedValues.SAD_CODIGOS_ALTERACAO_EM_ESTOQUE, "','") + "') "
                                              + ") ";
                    }

                    query = "create temporary table tmp_margem_concluida "
                            + "(rse_codigo varchar(32), margem_concluida_1 decimal(13,2), margem_concluida_2 decimal(13,2), margem_concluida_3 decimal(13,2), KEY `tmpidx_rse` (`rse_codigo`)) "
                            + "select rse.rse_codigo, "
                            + "sum(if(ade_inc_margem = '1', ade_vlr, 0.00)) as margem_concluida_1, "
                            + "sum(if(ade_inc_margem = '2', ade_vlr, 0.00)) as margem_concluida_2, "
                            + "sum(if(ade_inc_margem = '3', ade_vlr, 0.00)) as margem_concluida_3 "
                            + "from tb_ocorrencia_autorizacao oca "
                            + "inner join tb_aut_desconto ade on (oca.ade_codigo = ade.ade_codigo) "
                            + "inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) "
                            + "inner join tb_periodo_exportacao pex on (rse.org_codigo = pex.org_codigo) "
                            + tabelas
                            + "where ade_inc_margem in ('1', '2', '3') "
                            /*
                             * Devido a conclusao considerar a data de conclusao do contrato
                             * pode haver contratos com ade_prd_pagas <> de ade_prazo
                             */
                            // + "and ade_prd_pagas = ade_prazo "
                            + "and sad_codigo = '9' "
                            + "and toc_codigo = '15' "
                            + "and oca_data > PEX_DATA_FIM "
                            + clausulaConclusaoFerias
                            + clausulaSaldoParcelas
                            + complemento
                            + "group by rse.rse_codigo";
                    LOG.trace(query);
                    jdbc.update(query, queryParams);

                    query = "update tb_registro_servidor rse "
                          + "inner join tmp_margem_concluida tmp on (tmp.rse_codigo = rse.rse_codigo) "
                          + "set rse_margem_usada   = rse_margem_usada - margem_concluida_1,"
                          + "    rse_margem_usada_2 = rse_margem_usada_2 - margem_concluida_2,"
                          + "    rse_margem_usada_3 = rse_margem_usada_3 - margem_concluida_3";
                    LOG.trace(query);
                    jdbc.update(query, queryParams);
                }

                /* --------------------------------------------------------------------------------------------------
                 * PASSO 7) Se não controla margem, Adiciona na margem usada a diferença entre o valor do contrato
                 * e o valor realmente pago pela folha. É necessário para não perder as alterações de contratos e
                 * para prender a diferença no caso de pagamento parcial da folha.
                 * - Se tiver que prender margem, é para todos os casos.
                 * - Se tiver que liberar margem, é somente para os contratos que sofreram alteração.
                 * - Se o parâmetro de sistema diz que não subtrai o pgt parcial, então só o faz
                 *   mediante a presença da ocorrência de alteração.
                 */
                final boolean subtraiPagamentoParcial = !ParamSist.paramEquals(CodedValues.TPC_SUBTRAI_PAGAMENTO_PARCIAL_MARGEM, CodedValues.TPC_NAO, AcessoSistema.getAcessoUsuarioSistema());

                query = "drop temporary table if exists tmp_margem_alterada";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "create temporary table tmp_margem_alterada "
                        + "(rse_codigo varchar(32), margem_alterada_1 decimal(13,2), margem_alterada_2 decimal(13,2), margem_alterada_3 decimal(13,2), KEY `tmpidx_rse` (`rse_codigo`)) "
                        + "select rse.rse_codigo, "
                        + "sum(case when ade_inc_margem = '1' then (coalesce(ade_vlr_parcela_folha, ade_vlr) - ade_vlr_folha) else 0.00 end) as margem_alterada_1, "
                        + "sum(case when ade_inc_margem = '2' then (coalesce(ade_vlr_parcela_folha, ade_vlr) - ade_vlr_folha) else 0.00 end) as margem_alterada_2, "
                        + "sum(case when ade_inc_margem = '3' then (coalesce(ade_vlr_parcela_folha, ade_vlr) - ade_vlr_folha) else 0.00 end) as margem_alterada_3 "
                        + "from tb_aut_desconto ade "
                        + "inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) "
                        + "inner join tb_periodo_exportacao pex on (rse.org_codigo = pex.org_codigo) "
                        + tabelas
                        + "where sad_codigo = '5' "              // CONTRATO EM ANDAMENTO
                        + "and ade_tipo_vlr = 'F' "              // COM VALOR FIXO
                        + "and ade_inc_margem in ('1','2','3') " // QUE INCIDE NA MARGEM
                        + "and coalesce(ade_paga, 'N') = 'S' "   // ONDE A ADE FOI PAGA
                        + "and ade_vlr_folha IS NOT NULL "       // COM VALOR FOLHA

                        + (subtraiPagamentoParcial
                        ? "and ((ade_vlr_folha < coalesce(ade_vlr_parcela_folha, ade_vlr)) "      // VLR PAGO MENOR DO QUE ADE_VLR OU
                        + "  or (ade_vlr_folha > coalesce(ade_vlr_parcela_folha, ade_vlr) "       // VLR PAGO MAIOR DO QUE ADE_VLR E TEM ALTERACAO
                        + "and EXISTS (select 1 from tb_ocorrencia_autorizacao oca where ade.ade_codigo = oca.ade_codigo and toc_codigo = '14' and oca_data > PEX_DATA_FIM))) "

                        : "and (ade_vlr_folha <> coalesce(ade_vlr_parcela_folha, ade_vlr)) "      // VLR PAGO DIFERENTE DO QUE ADE_VLR E TEM ALTERACAO
                        + "and EXISTS (select 1 from tb_ocorrencia_autorizacao oca where ade.ade_codigo = oca.ade_codigo and toc_codigo = '14' and oca_data > PEX_DATA_FIM) "
                        )

                        + complemento
                        + "group by rse.rse_codigo";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "update tb_registro_servidor rse "
                      + "inner join tmp_margem_alterada tmp on (tmp.rse_codigo = rse.rse_codigo) "
                      + "set rse_margem_usada   = rse_margem_usada + margem_alterada_1,"
                      + "    rse_margem_usada_2 = rse_margem_usada_2 + margem_alterada_2,"
                      + "    rse_margem_usada_3 = rse_margem_usada_3 + margem_alterada_3";
                LOG.trace(query);
                jdbc.update(query, queryParams);

            } else {
                /* --------------------------------------------------------------------------------------------------
                 * PASSO 8) Se controla margem, adiciona na margem usada o valor descontado das parcelas de
                 * consignações de tipo percentual, subtraido do valor nominal das consignaçãoes que já foi
                 * adicionado na margem usada.
                 */
                final boolean subtrairValorPagoQQPeriodo = ParamSist.paramEquals(CodedValues.TPC_SUBTRAIR_VALOR_PAGO_ADE_PERCENTUAL_INDEPENDENTE_PERIODO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

                query = "drop temporary table if exists tmp_valor_pago_percentual";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "create temporary table tmp_valor_pago_percentual (ade_codigo varchar(32), valor_pago decimal(13,2), primary key (ade_codigo))";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "insert into tmp_valor_pago_percentual (ade_codigo, valor_pago) "
                        + "select prd.ade_codigo, max(prd.prd_vlr_realizado) "
                        + "from tb_parcela_desconto prd "
                        + "inner join tb_aut_desconto ade on (prd.ade_codigo = ade.ade_codigo) "
                        + "inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) "
                        + "inner join tb_periodo_exportacao pex on (rse.org_codigo = pex.org_codigo) "
                        + tabelas
                        + "where ade.sad_codigo in ('" + TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_INCIDEM_MARGEM_BRUTA, "','") + "') " // CONTRATO ATIVO
                        + "and ade.ade_tipo_vlr = '" + CodedValues.TIPO_VLR_PERCENTUAL + "' " // PERCENTUAL
                        + "and ade.ade_inc_margem in ('1','2','3') "                          // INCIDE NA MARGEM
                        + "and prd.spd_codigo = '" + CodedValues.SPD_LIQUIDADAFOLHA + "' "    // PARCELA DESCONTADA
                        + (!subtrairValorPagoQQPeriodo                                        // DO ULTIMO PERIODO DE RETORNO OU A ÚLTIMA PAGA
                        ? "and prd.prd_data_desconto = pex.pex_periodo "
                        : "and not exists (select 1 from tb_parcela_desconto prd2 where prd.ade_codigo = prd2.ade_codigo and prd2.spd_codigo = '" + CodedValues.SPD_LIQUIDADAFOLHA + "' and prd2.prd_data_desconto > prd.prd_data_desconto) ")
                        + complemento
                        + "group by prd.ade_codigo";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "drop temporary table if exists tmp_margem_percentual";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "create temporary table tmp_margem_percentual (rse_codigo varchar(32), margem_usada_1 decimal(13,2), margem_usada_2 decimal(13,2), margem_usada_3 decimal(13,2), primary key (rse_codigo))";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "insert into tmp_margem_percentual (rse_codigo, margem_usada_1, margem_usada_2, margem_usada_3) "
                        + "select rse.rse_codigo, "
                        + "sum(if(ade_inc_margem = '1', coalesce(valor_pago, ade_vlr_folha) - ade_vlr, 0.00)) as margem_usada_1, "
                        + "sum(if(ade_inc_margem = '2', coalesce(valor_pago, ade_vlr_folha) - ade_vlr, 0.00)) as margem_usada_2, "
                        + "sum(if(ade_inc_margem = '3', coalesce(valor_pago, ade_vlr_folha) - ade_vlr, 0.00)) as margem_usada_3 "
                        + "from tb_aut_desconto ade "
                        + "inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) "
                        + tabelas
                        + "left outer join tmp_valor_pago_percentual tmp on (ade.ade_codigo = tmp.ade_codigo) "
                        + "where ade.sad_codigo in ('" + TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_INCIDEM_MARGEM_BRUTA, "','") + "') " // CONTRATO ATIVO
                        + "and ade.ade_tipo_vlr = '" + CodedValues.TIPO_VLR_PERCENTUAL + "' "  // PERCENTUAL
                        + "and ade.ade_inc_margem in ('1','2','3') "                           // INCIDE NA MARGEM
                        + "and (coalesce(coalesce(valor_pago, ade_vlr_folha), 0.00) > 0.00) "  // TENHA SIDO PAGO
                        + complemento
                        + "group by rse.rse_codigo";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "update tb_registro_servidor rse "
                      + "inner join tmp_margem_percentual tmp on (tmp.rse_codigo = rse.rse_codigo) "
                      + "set rse_margem_usada   = rse_margem_usada + margem_usada_1, "
                      + "    rse_margem_usada_2 = rse_margem_usada_2 + margem_usada_2, "
                      + "    rse_margem_usada_3 = rse_margem_usada_3 + margem_usada_3";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                final boolean liberaMargemConclusaoContrato = ParamSist.paramEquals(CodedValues.TPC_LIB_MARGEM_CONCLUSAO_CONTRATO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()); // Default: Não
                final boolean liberaMargemConclusaoFerias = !ParamSist.paramEquals(CodedValues.TPC_LIB_MARGEM_CONCLUSAO_CONTRATO_FERIAS, CodedValues.TPC_NAO, AcessoSistema.getAcessoUsuarioSistema()); // Default: Sim
                if (!liberaMargemConclusaoContrato || !liberaMargemConclusaoFerias) {
                    /* --------------------------------------------------------------------------------------------------
                     * PASSO 9) Se controla margem, Adiciona na margem usada as autorizações concluidas por desconto
                     * de férias neste período.
                     */
                    query = "drop temporary table if exists tmp_margem_concluida";
                    LOG.trace(query);
                    jdbc.update(query, queryParams);

                    query = "create temporary table tmp_margem_concluida "
                          + "(rse_codigo varchar(32), margem_concluida_1 decimal(13,2), margem_concluida_2 decimal(13,2), margem_concluida_3 decimal(13,2), KEY `tmpidx_rse` (`rse_codigo`)) "
                          + "select rse.rse_codigo, "
                          + "sum(if(ade_inc_margem = '1', ade_vlr, 0.00)) as margem_concluida_1, "
                          + "sum(if(ade_inc_margem = '2', ade_vlr, 0.00)) as margem_concluida_2, "
                          + "sum(if(ade_inc_margem = '3', ade_vlr, 0.00)) as margem_concluida_3 "
                          + "from tb_ocorrencia_autorizacao oca "
                          + "inner join tb_aut_desconto ade on (oca.ade_codigo = ade.ade_codigo) "
                          + "inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) "
                          + "inner join tb_periodo_exportacao pex on (rse.org_codigo = pex.org_codigo) "
                          + tabelas
                          + "where ade_inc_margem in ('1', '2', '3') "
                          + "and sad_codigo = '9' "
                          + "and toc_codigo = '15' "
                          + "and oca_data > PEX_DATA_FIM "
                          + (!liberaMargemConclusaoContrato ? ""
                          : "and exists(select 1 from tb_parcela_desconto prd "
                          + "           where prd.ade_codigo = ade.ade_codigo "
                          + "           and prd.spd_codigo = '6' "
                          + "           and prd.prd_data_desconto > pex.pex_periodo) ")
                          + complemento
                          + "group by rse.rse_codigo";
                    LOG.trace(query);
                    jdbc.update(query, queryParams);

                    query = "update tb_registro_servidor rse "
                          + "inner join tmp_margem_concluida tmp on (tmp.rse_codigo = rse.rse_codigo) "
                          + "set rse_margem_usada   = rse_margem_usada + margem_concluida_1,"
                          + "    rse_margem_usada_2 = rse_margem_usada_2 + margem_concluida_2,"
                          + "    rse_margem_usada_3 = rse_margem_usada_3 + margem_concluida_3";
                    LOG.trace(query);
                    jdbc.update(query, queryParams);
                }
            }

            /* --------------------------------------------------------------------------------------------------
             * PASSO 10) Adiciona à margem usada as autorizações de prazo indeterminado liquidadas associadas
             * a serviço que esteja configurado para só liberar a margem na próxima carga de margem.
             */
            query = "drop temporary table if exists tmp_margem_liquidada_prz_indet";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "create temporary table tmp_margem_liquidada_prz_indet "
                  + "(rse_codigo varchar(32), margem_liquidada_1 decimal(13,2), margem_liquidada_2 decimal(13,2), margem_liquidada_3 decimal(13,2), KEY `tmpidx_rse` (`rse_codigo`)) "
                    + "select rse.rse_codigo, "
                  + "sum(if(ade.ade_inc_margem = '1', if(ade.ade_tipo_vlr = 'P', coalesce(ade.ade_vlr_folha, ade.ade_vlr), ade.ade_vlr), 0.00)) as margem_liquidada_1, "
                  + "sum(if(ade.ade_inc_margem = '2', if(ade.ade_tipo_vlr = 'P', coalesce(ade.ade_vlr_folha, ade.ade_vlr), ade.ade_vlr), 0.00)) as margem_liquidada_2, "
                  + "sum(if(ade.ade_inc_margem = '3', if(ade.ade_tipo_vlr = 'P', coalesce(ade.ade_vlr_folha, ade.ade_vlr), ade.ade_vlr), 0.00)) as margem_liquidada_3 "
                  + "from tb_aut_desconto ade "
                  + "inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) "
                  + "inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) "
                  + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
                  + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                  + "inner join tb_param_svc_consignante pse on (cnv.svc_codigo = pse.svc_codigo) "
                  + tabelas
                  + "where ade.ade_inc_margem in ('1', '2', '3') "
                  + "and ade.ade_prazo is null "
                  + "and ade.sad_codigo = '" + CodedValues.SAD_LIQUIDADA + "' "
                  + "and oca.toc_codigo = '" + CodedValues.TOC_TARIF_LIQUIDACAO + "' "
                  + "and pse.tps_codigo = '" + CodedValues.TPS_PRENDE_MARGEM_LIQ_ADE_PRZ_INDET_ATE_CARGA_MARGEM + "' "
                  + "and pse.pse_vlr = '" + CodedValues.PSE_BOOLEANO_SIM + "' "
                  + "and oca.oca_data > rse.rse_data_carga "
                  + complemento
                    + "group by rse.rse_codigo";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "update tb_registro_servidor rse "
                  + "inner join tmp_margem_liquidada_prz_indet tmp on (tmp.rse_codigo = rse.rse_codigo) "
                  + "set rse_margem_usada   = rse_margem_usada   + margem_liquidada_1,"
                  + "    rse_margem_usada_2 = rse_margem_usada_2 + margem_liquidada_2,"
                  + "    rse_margem_usada_3 = rse_margem_usada_3 + margem_liquidada_3";
            LOG.trace(query);
            jdbc.update(query, queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Atualiza os valores usados e restantes das margens 1 e 3, de acordo com
     * o relacionamento entre essas duas margens.
     *
     * @param tipoEntidade : EST / ORG
     * @param entCodigos : códigos dos órgãos/estabelecimentos
     * @throws DAOException
     */
    @Override
    public void calculaMargem1CasadaMargem3(String tipoEntidade, List<String> entCodigos) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        try {
            final StringBuilder query = new StringBuilder();
            final StringBuilder tabelas = new StringBuilder();
            final StringBuilder filtro = new StringBuilder();
            if ((entCodigos != null) && (entCodigos.size() > 0) &&
                    ("EST".equalsIgnoreCase(tipoEntidade) || "ORG".equalsIgnoreCase(tipoEntidade) || "RSE".equalsIgnoreCase(tipoEntidade))) {
                if ("EST".equalsIgnoreCase(tipoEntidade)) {
                    tabelas.append(", ").append(Columns.TB_ORGAO);
                    filtro.append(" WHERE ").append(Columns.RSE_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO);
                    filtro.append(" AND ").append(Columns.ORG_EST_CODIGO).append(" IN (:entCodigos)");
                    queryParams.addValue("entCodigos", entCodigos);
                } else if ("ORG".equalsIgnoreCase(tipoEntidade)) {
                    filtro.append(" WHERE ").append(Columns.RSE_ORG_CODIGO).append(" IN (:entCodigos)");
                    queryParams.addValue("entCodigos", entCodigos);
                } else if ("RSE".equalsIgnoreCase(tipoEntidade)) {
                    filtro.append(" WHERE ").append(Columns.RSE_CODIGO).append(" IN (:entCodigos)");
                    queryParams.addValue("entCodigos", entCodigos);
                }
            } else {
                filtro.append(" WHERE '1' = '1'");
            }
            // set RSE_MARGEM_USADA = RSE_MARGEM_USADA + RSE_MARGEM_USADA_3
            query.append("UPDATE ").append(Columns.TB_REGISTRO_SERVIDOR).append(tabelas).append(" SET ");
            query.append(Columns.RSE_MARGEM_USADA).append(" = ").append(Columns.RSE_MARGEM_USADA).append(" + ").append(Columns.RSE_MARGEM_USADA_3);
            query.append(filtro);

            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // set RSE_MARGEM_REST = RSE_MARGEM - RSE_MARGEM_USADA
            query.setLength(0);
            query.append("UPDATE ").append(Columns.TB_REGISTRO_SERVIDOR).append(tabelas).append(" SET ");
            query.append(Columns.RSE_MARGEM_REST).append(" = ").append(Columns.RSE_MARGEM).append(" - ").append(Columns.RSE_MARGEM_USADA);
            query.append(filtro);

            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // set RSE_MARGEM_REST_3 = RSE_MARGEM_REST
            // where RSE_MARGEM_REST <= RSE_MARGEM_3 - RSE_MARGEM_USADA_3
            query.setLength(0);
            query.append("UPDATE ").append(Columns.TB_REGISTRO_SERVIDOR).append(tabelas).append(" SET ");
            query.append(Columns.RSE_MARGEM_REST_3).append(" = ").append(Columns.RSE_MARGEM_REST);
            query.append(filtro).append(" AND ").append(Columns.RSE_MARGEM_REST).append(" <= ");
            query.append(Columns.RSE_MARGEM_3).append(" - ").append(Columns.RSE_MARGEM_USADA_3);

            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // set RSE_MARGEM_REST_3 = RSE_MARGEM_3 - RSE_MARGEM_USADA_3
            // where RSE_MARGEM_REST > RSE_MARGEM_3 - RSE_MARGEM_USADA_3
            query.setLength(0);
            query.append("UPDATE ").append(Columns.TB_REGISTRO_SERVIDOR).append(tabelas).append(" SET ");
            query.append(Columns.RSE_MARGEM_REST_3).append(" = ").append(Columns.RSE_MARGEM_3).append(" - ").append(Columns.RSE_MARGEM_USADA_3);
            query.append(filtro).append(" AND ").append(Columns.RSE_MARGEM_REST).append(" > ");
            query.append(Columns.RSE_MARGEM_3).append(" - ").append(Columns.RSE_MARGEM_USADA_3);

            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Atualiza os valores usados e restantes das margens 1 e 3, de acordo com
     * o relacionamento entre essas duas margens, casando pela Esquerda.
     *
     * @param tipoEntidade : EST / ORG
     * @param entCodigos : códigos dos órgãos/estabelecimentos
     * @throws DAOException
     */
    @Override
    public void calculaMargem1CasadaMargem3Esq(String tipoEntidade, List<String> entCodigos) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        try {
            final StringBuilder query = new StringBuilder();
            final StringBuilder tabelas = new StringBuilder();
            final StringBuilder filtro = new StringBuilder();
            if ((entCodigos != null) && (entCodigos.size() > 0) &&
                    ("EST".equalsIgnoreCase(tipoEntidade) || "ORG".equalsIgnoreCase(tipoEntidade) || "RSE".equalsIgnoreCase(tipoEntidade))) {
                if ("EST".equalsIgnoreCase(tipoEntidade)) {
                    tabelas.append(", ").append(Columns.TB_ORGAO);
                    filtro.append(" WHERE ").append(Columns.RSE_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO);
                    filtro.append(" AND ").append(Columns.ORG_EST_CODIGO).append(" IN (:entCodigos)");
                    queryParams.addValue("entCodigos", entCodigos);
                } else if ("ORG".equalsIgnoreCase(tipoEntidade)) {
                    filtro.append(" WHERE ").append(Columns.RSE_ORG_CODIGO).append(" IN (:entCodigos)");
                    queryParams.addValue("entCodigos", entCodigos);
                } else if ("RSE".equalsIgnoreCase(tipoEntidade)) {
                    filtro.append(" WHERE ").append(Columns.RSE_CODIGO).append(" IN (:entCodigos)");
                    queryParams.addValue("entCodigos", entCodigos);
                }
            } else {
                filtro.append(" WHERE '1' = '1'");
            }

            // set RSE_MARGEM_USADA = RSE_MARGEM_USADA + RSE_MARGEM_USADA_3
            query.append("UPDATE ").append(Columns.TB_REGISTRO_SERVIDOR).append(tabelas).append(" SET ");
            query.append(Columns.RSE_MARGEM_USADA).append(" = ").append(Columns.RSE_MARGEM_USADA).append(" + ").append(Columns.RSE_MARGEM_USADA_3);
            query.append(filtro);
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // set RSE_MARGEM_REST    = RSE_MARGEM - RSE_MARGEM_USADA,
            //     RSE_MARGEM_USADA_3 = RSE_MARGEM_USADA,
            //     RSE_MARGEM_REST_3  = RSE_MARGEM_3 - RSE_MARGEM_USADA
            query.setLength(0);
            query.append("UPDATE ").append(Columns.TB_REGISTRO_SERVIDOR).append(tabelas).append(" SET ");
            query.append(Columns.RSE_MARGEM_REST).append(" = ").append(Columns.RSE_MARGEM).append(" - ").append(Columns.RSE_MARGEM_USADA).append(", ");
            query.append(Columns.RSE_MARGEM_USADA_3).append(" = ").append(Columns.RSE_MARGEM_USADA).append(", ");
            query.append(Columns.RSE_MARGEM_REST_3).append(" = ").append(Columns.RSE_MARGEM_3).append(" - ").append(Columns.RSE_MARGEM_USADA);
            query.append(filtro);
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Atualiza o valor restante da margens 3, de acordo com
     * o relacionamento entre essas duas margens, casando pela Lateralmente.
     *
     * @param tipoEntidade : EST / ORG / RSE
     * @param entCodigos : códigos dos órgãos/estabelecimentos/registros servidores
     * @throws DAOException
     */
    @Override
    public void calculaMargem1CasadaMargem3Lateral(String tipoEntidade, List<String> entCodigos) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        try {
            final StringBuilder query = new StringBuilder();
            final StringBuilder tabelas = new StringBuilder();
            final StringBuilder filtro = new StringBuilder();
            if ((entCodigos != null) && (entCodigos.size() > 0) &&
                    ("EST".equalsIgnoreCase(tipoEntidade) || "ORG".equalsIgnoreCase(tipoEntidade) || "RSE".equalsIgnoreCase(tipoEntidade))) {
                if ("EST".equalsIgnoreCase(tipoEntidade)) {
                    tabelas.append(", ").append(Columns.TB_ORGAO);
                    filtro.append(" WHERE ").append(Columns.RSE_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO);
                    filtro.append(" AND ").append(Columns.ORG_EST_CODIGO).append(" IN (:entCodigos)");
                    queryParams.addValue("entCodigos", entCodigos);
                } else if ("ORG".equalsIgnoreCase(tipoEntidade)) {
                    filtro.append(" WHERE ").append(Columns.RSE_ORG_CODIGO).append(" IN (:entCodigos)");
                    queryParams.addValue("entCodigos", entCodigos);
                } else if ("RSE".equalsIgnoreCase(tipoEntidade)) {
                    filtro.append(" WHERE ").append(Columns.RSE_CODIGO).append(" IN (:entCodigos)");
                    queryParams.addValue("entCodigos", entCodigos);
                }
            } else {
                filtro.append(" WHERE '1' = '1'");
            }

            // set RSE_MARGEM_REST_3 = RSE_MARGEM_3 - RSE_MARGEM_USADA_3 + MIN(0, RSE_MARGEM_REST)
            query.append("UPDATE ").append(Columns.TB_REGISTRO_SERVIDOR).append(tabelas).append(" SET ");
            query.append(Columns.RSE_MARGEM_REST_3).append(" = ").append(Columns.RSE_MARGEM_3).append(" - ").append(Columns.RSE_MARGEM_USADA_3);
            query.append(" + LEAST(0, ").append(Columns.RSE_MARGEM_REST).append(")");
            query.append(filtro);
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Atualiza os valores usados e restantes das margens 1, 2 e 3, de acordo com
     * o relacionamento entre essas duas margens.
     *
     * @param tipoEntidade : EST / ORG
     * @param entCodigos : códigos dos órgãos/estabelecimentos
     * @throws DAOException
     */
    @Override
    public void calculaMargens123Casadas(String tipoEntidade, List<String> entCodigos) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        try {
            final StringBuilder query = new StringBuilder();
            final StringBuilder tabelas = new StringBuilder();
            final StringBuilder filtro = new StringBuilder();
            if ((entCodigos != null) && (entCodigos.size() > 0) &&
                    ("EST".equalsIgnoreCase(tipoEntidade) || "ORG".equalsIgnoreCase(tipoEntidade) || "RSE".equalsIgnoreCase(tipoEntidade))) {
                if ("EST".equalsIgnoreCase(tipoEntidade)) {
                    tabelas.append(", ").append(Columns.TB_ORGAO);
                    filtro.append(" WHERE ").append(Columns.RSE_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO);
                    filtro.append(" AND ").append(Columns.ORG_EST_CODIGO).append(" IN (:entCodigos)");
                    queryParams.addValue("entCodigos", entCodigos);
                } else if ("ORG".equalsIgnoreCase(tipoEntidade)) {
                    filtro.append(" WHERE ").append(Columns.RSE_ORG_CODIGO).append(" IN (:entCodigos)");
                    queryParams.addValue("entCodigos", entCodigos);
                } else if ("RSE".equalsIgnoreCase(tipoEntidade)) {
                    filtro.append(" WHERE ").append(Columns.RSE_CODIGO).append(" IN (:entCodigos)");
                    queryParams.addValue("entCodigos", entCodigos);
                }
            } else {
                filtro.append(" WHERE '1' = '1'");
            }

            // set RSE_MARGEM_USADA   = RSE_MARGEM_USADA + RSE_MARGEM_USADA_2 + RSE_MARGEM_USADA_3
            //     RSE_MARGEM_USADA_2 = RSE_MARGEM_USADA_2 + RSE_MARGEM_USADA_3
            query.append("UPDATE ").append(Columns.TB_REGISTRO_SERVIDOR).append(tabelas).append(" SET ");
            query.append(Columns.RSE_MARGEM_USADA).append(" = ").append(Columns.RSE_MARGEM_USADA).append(" + ").append(Columns.RSE_MARGEM_USADA_2);
            query.append(" + ").append(Columns.RSE_MARGEM_USADA_3).append(MySqlDAOFactory.SEPARADOR);
            query.append(Columns.RSE_MARGEM_USADA_2).append(" = ").append(Columns.RSE_MARGEM_USADA_2).append(" + ").append(Columns.RSE_MARGEM_USADA_3);
            query.append(filtro);

            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // OBS: O valor atualizado de margem_rest não pode ser utilizado na query para determinar o valor
            // das próximas margens, por isso a query é feita toda utilizando os campos margem e margem_usada:
            // set RSE_MARGEM_REST   = RSE_MARGEM - RSE_MARGEM_USADA
            //     RSE_MARGEM_REST_2 = IF(RSE_MARGEM_REST <= RSE_MARGEM_2 - RSE_MARGEM_USADA_2, RSE_MARGEM_REST, RSE_MARGEM_2 - RSE_MARGEM_USADA_2)
            //     RSE_MARGEM_REST_3 = IF(RSE_MARGEM_REST_2 <= RSE_MARGEM_3 - RSE_MARGEM_USADA_3, RSE_MARGEM_REST_2, RSE_MARGEM_3 - RSE_MARGEM_USADA_3)
            query.setLength(0);
            query.append("UPDATE ").append(Columns.TB_REGISTRO_SERVIDOR).append(tabelas).append(" SET ");
            // Margem 1
            query.append(Columns.RSE_MARGEM_REST).append(" = ").append(Columns.RSE_MARGEM).append(" - ").append(Columns.RSE_MARGEM_USADA);
            query.append(MySqlDAOFactory.SEPARADOR);
            // Margem 2
            query.append(Columns.RSE_MARGEM_REST_2).append(" = CASE WHEN ").append(Columns.RSE_MARGEM).append(" - ").append(Columns.RSE_MARGEM_USADA).append(" <= ");
            query.append(Columns.RSE_MARGEM_2).append(" - ").append(Columns.RSE_MARGEM_USADA_2).append(" THEN ");
            query.append(Columns.RSE_MARGEM).append(" - ").append(Columns.RSE_MARGEM_USADA).append(" ELSE ");
            query.append(Columns.RSE_MARGEM_2).append(" - ").append(Columns.RSE_MARGEM_USADA_2).append(" END ");
            query.append(MySqlDAOFactory.SEPARADOR);
            // Margem 3
            query.append(Columns.RSE_MARGEM_REST_3).append(" = CASE WHEN CASE WHEN ").append(Columns.RSE_MARGEM).append(" - ").append(Columns.RSE_MARGEM_USADA).append(" <= ");
            query.append(Columns.RSE_MARGEM_2).append(" - ").append(Columns.RSE_MARGEM_USADA_2).append(" THEN ");
            query.append(Columns.RSE_MARGEM).append(" - ").append(Columns.RSE_MARGEM_USADA).append(" ELSE ");
            query.append(Columns.RSE_MARGEM_2).append(" - ").append(Columns.RSE_MARGEM_USADA_2).append(" END ");
            query.append(" <= ").append(Columns.RSE_MARGEM_3).append(" - ").append(Columns.RSE_MARGEM_USADA_3).append(" THEN ");
            query.append("CASE WHEN ").append(Columns.RSE_MARGEM).append(" - ").append(Columns.RSE_MARGEM_USADA).append(" <= ");
            query.append(Columns.RSE_MARGEM_2).append(" - ").append(Columns.RSE_MARGEM_USADA_2).append(" THEN ");
            query.append(Columns.RSE_MARGEM).append(" - ").append(Columns.RSE_MARGEM_USADA).append(" ELSE ");
            query.append(Columns.RSE_MARGEM_2).append(" - ").append(Columns.RSE_MARGEM_USADA_2).append(" END ").append(" ELSE ");
            query.append(Columns.RSE_MARGEM_3).append(" - ").append(Columns.RSE_MARGEM_USADA_3).append(" END ");
            query.append(filtro);

            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Atualiza os valores usados e restantes das margens 1, 2 e 3, de acordo com
     * o relacionamento entre essas duas margens, casando pela Esquerda.
     *
     * @param tipoEntidade : EST / ORG
     * @param entCodigos : códigos dos órgãos/estabelecimentos
     * @throws DAOException
     */
    @Override
    public void calculaMargens123CasadasEsq(String tipoEntidade, List<String> entCodigos) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        try {
            final StringBuilder query = new StringBuilder();
            final StringBuilder tabelas = new StringBuilder();
            final StringBuilder filtro = new StringBuilder();
            if ((entCodigos != null) && (entCodigos.size() > 0) &&
                    ("EST".equalsIgnoreCase(tipoEntidade) || "ORG".equalsIgnoreCase(tipoEntidade) || "RSE".equalsIgnoreCase(tipoEntidade))) {
                if ("EST".equalsIgnoreCase(tipoEntidade)) {
                    tabelas.append(", ").append(Columns.TB_ORGAO);
                    filtro.append(" WHERE ").append(Columns.RSE_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO);
                    filtro.append(" AND ").append(Columns.ORG_EST_CODIGO).append(" IN (:entCodigos)");
                    queryParams.addValue("entCodigos", entCodigos);
                } else if ("ORG".equalsIgnoreCase(tipoEntidade)) {
                    filtro.append(" WHERE ").append(Columns.RSE_ORG_CODIGO).append(" IN (:entCodigos)");
                    queryParams.addValue("entCodigos", entCodigos);
                } else if ("RSE".equalsIgnoreCase(tipoEntidade)) {
                    filtro.append(" WHERE ").append(Columns.RSE_CODIGO).append(" IN (:entCodigos)");
                    queryParams.addValue("entCodigos", entCodigos);
                }
            } else {
                filtro.append(" WHERE '1' = '1'");
            }

            // set RSE_MARGEM_USADA = RSE_MARGEM_USADA + RSE_MARGEM_USADA_2 + RSE_MARGEM_USADA_3
            query.append("UPDATE ").append(Columns.TB_REGISTRO_SERVIDOR).append(tabelas).append(" SET ");
            query.append(Columns.RSE_MARGEM_USADA).append(" = ").append(Columns.RSE_MARGEM_USADA);
            query.append(" + ").append(Columns.RSE_MARGEM_USADA_2);
            query.append(" + ").append(Columns.RSE_MARGEM_USADA_3);
            query.append(filtro);
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

            // set RSE_MARGEM_REST    = RSE_MARGEM - RSE_MARGEM_USADA,
            //     RSE_MARGEM_USADA_2 = RSE_MARGEM_USADA,
            //     RSE_MARGEM_REST_2  = RSE_MARGEM_2 - RSE_MARGEM_USADA
            //     RSE_MARGEM_USADA_3 = RSE_MARGEM_USADA,
            //     RSE_MARGEM_REST_3  = RSE_MARGEM_3 - RSE_MARGEM_USADA
            query.setLength(0);
            query.append("UPDATE ").append(Columns.TB_REGISTRO_SERVIDOR).append(tabelas).append(" SET ");
            query.append(Columns.RSE_MARGEM_REST).append(" = ").append(Columns.RSE_MARGEM).append(" - ").append(Columns.RSE_MARGEM_USADA).append(", ");
            query.append(Columns.RSE_MARGEM_USADA_2).append(" = ").append(Columns.RSE_MARGEM_USADA).append(", ");
            query.append(Columns.RSE_MARGEM_REST_2).append(" = ").append(Columns.RSE_MARGEM_2).append(" - ").append(Columns.RSE_MARGEM_USADA).append(", ");
            query.append(Columns.RSE_MARGEM_USADA_3).append(" = ").append(Columns.RSE_MARGEM_USADA).append(", ");
            query.append(Columns.RSE_MARGEM_REST_3).append(" = ").append(Columns.RSE_MARGEM_3).append(" - ").append(Columns.RSE_MARGEM_USADA);
            query.append(filtro);
            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Seta a margem dos servidores para MARGEM_SERVIDOR_PRESTES_A_SER_EXCLUIDO, pois após o processo de importação
     * de margem, aqueles servidores com margem igual a MARGEM_SERVIDOR_PRESTES_A_SER_EXCLUIDO devem ser excluídos.
     *
     * @param tipoEntidade
     * @param entCodigos
     * @throws DAOException
     */
    @Override
    public void zeraMargem(String tipoEntidade, List<String> entCodigos, boolean zerarMargemExclusao) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        try {
            final StringBuilder query = new StringBuilder();
            final StringBuilder tables = new StringBuilder();
            final StringBuilder filters = new StringBuilder();

            query.append("UPDATE ").append(Columns.TB_REGISTRO_SERVIDOR);

            //preenche os stringBuffers com as tabelas e filtros necessários
            if ((entCodigos != null) && (entCodigos.size() > 0)) {
                if ("EST".equalsIgnoreCase(tipoEntidade)) {
                    tables.append(MySqlDAOFactory.SEPARADOR).append(Columns.TB_ORGAO);
                    filters.append(" WHERE ").append(Columns.RSE_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO);
                    filters.append(" AND ").append(Columns.ORG_EST_CODIGO).append(" IN (:entCodigos)");
                    queryParams.addValue("entCodigos", entCodigos);
                } else if ("ORG".equalsIgnoreCase(tipoEntidade)) {
                    filters.append(" WHERE ").append(Columns.RSE_ORG_CODIGO);
                    filters.append(" IN (:entCodigos)");
                    queryParams.addValue("entCodigos", entCodigos);
                }
            }

            if (zerarMargemExclusao) {
                query.append(tables.toString()).append(" SET ").append(Columns.RSE_MARGEM).append(" = ").append(CodedValues.MARGEM_SERVIDOR_PRESTES_A_SER_EXCLUIDO);
            } else {
                query.append(tables.toString()).append(" SET ").append(Columns.RSE_DATA_CARGA).append(" = NULL");
            }

            query.append(filters.toString());

            LOG.trace(query.toString());
            jdbc.update(query.toString(), queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Exclui os servidores que não foram enviados pela folha, ou seja, que estão
     * com a margem igual a MARGEM_SERVIDOR_PRESTES_A_SER_EXCLUIDO. O seu status, representado pelo campo SRS_CODIGO, na
     * tabela registro_servidor é alterado para SRS_EXCLUIDO.
     *
     * @param tipoEntidade
     * @param entCodigos
     * @throws DAOException
     */
    @Override
    public void excluiServidores(String tipoEntidade, List<String> entCodigos, boolean geraTransferidos, boolean zerarMargemExclusao) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        try {
            final StringBuilder query = new StringBuilder();
            final StringBuilder tables = new StringBuilder();
            final StringBuilder filters = new StringBuilder();

            if ((entCodigos != null) && (entCodigos.size() > 0)) {
                if ("EST".equalsIgnoreCase(tipoEntidade)) {
                    tables.append(MySqlDAOFactory.SEPARADOR).append(Columns.TB_ORGAO);
                    filters.append(" AND ").append(Columns.RSE_ORG_CODIGO).append(" = ").append(Columns.ORG_CODIGO);
                    filters.append(" AND ").append(Columns.ORG_EST_CODIGO).append(" IN (:entCodigos)");
                    queryParams.addValue("entCodigos", entCodigos);
                } else if ("ORG".equalsIgnoreCase(tipoEntidade)) {
                    filters.append(" AND ").append(Columns.RSE_ORG_CODIGO).append(" IN (:entCodigos)");
                    queryParams.addValue("entCodigos", entCodigos);
                }
            }

            if (zerarMargemExclusao) {
                // Seta o prazo so para os que nao estao excluidos ainda.
                if (geraTransferidos) {
                    query.append("UPDATE ").append(Columns.TB_REGISTRO_SERVIDOR);
                    query.append(tables.toString()).append(" SET ").append(Columns.RSE_DATA_CARGA).append(" = null");
                    query.append("  WHERE ").append(Columns.RSE_MARGEM).append(" = ").append(CodedValues.MARGEM_SERVIDOR_PRESTES_A_SER_EXCLUIDO);
                    query.append(filters.toString()).append(" AND (").append(Columns.RSE_SRS_CODIGO).append(" NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("')").append(")");

                    LOG.trace(query.toString());
                    jdbc.update(query.toString(), queryParams);
                }

                query.setLength(0);
                query.append("UPDATE ").append(Columns.TB_REGISTRO_SERVIDOR).append(tables.toString());
                query.append(" SET ");
                query.append(Columns.RSE_SRS_CODIGO).append(" = '").append(CodedValues.SRS_EXCLUIDO).append("', ");
                query.append(Columns.RSE_MARGEM).append(" = '0', ");
                query.append(Columns.RSE_MARGEM_2).append(" = '0', ");
                query.append(Columns.RSE_MARGEM_3).append(" = '0' ");
                query.append(" WHERE ").append(Columns.RSE_MARGEM).append(" = ").append(CodedValues.MARGEM_SERVIDOR_PRESTES_A_SER_EXCLUIDO);
                query.append(" AND (").append(Columns.RSE_SRS_CODIGO).append(" NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("')").append(")");
                query.append(filters.toString());

                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);

            } else {
                query.setLength(0);
                query.append("UPDATE ").append(Columns.TB_REGISTRO_SERVIDOR).append(tables.toString());
                query.append(" SET ");
                query.append(Columns.RSE_SRS_CODIGO).append(" = '").append(CodedValues.SRS_EXCLUIDO).append("' ");
                query.append(" WHERE ").append(Columns.RSE_DATA_CARGA).append(" IS NULL ");
                query.append(" AND (").append(Columns.RSE_SRS_CODIGO).append(" NOT IN ('").append(TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '")).append("')").append(")");
                query.append(filters.toString());

                LOG.trace(query.toString());
                jdbc.update(query.toString(), queryParams);
            }

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Preenche a tabela de servidores transferidos baseado nos tipos de servidores
     * existentes na base de dados
     *
     * @throws DAOException
     */
    @Override
    public String obtemServidoresTransferidos(String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws DAOException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        try {
            String filtros = "";

            // Verifica se o sistema preserva a matrícula e/ou estabelecimento do servidor na transferência (Default: MATRICULA)
            String preservaEstMatrTrans = (String) ParamSist.getInstance().getParam(CodedValues.TPC_PRESERVA_EST_MATR_TRANSFER, AcessoSistema.getAcessoUsuarioSistema());
            if ((preservaEstMatrTrans == null) || "".equals(preservaEstMatrTrans)) {
                preservaEstMatrTrans = "MATRICULA";
            }
            LOG.debug("PRESERVA MATRICULA E/OU ESTABELECIMENTO: " + preservaEstMatrTrans);

            // Verifica se mesmo com processamento por ORG/EST, é permitido gerar transferências
            // para outros órgãos que não seja aquele do usuário atual
            if (!ParamSist.paramEquals(CodedValues.TPC_PERMITE_TRANSFERENCIA_PARA_OUTROS_ORGAOS, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()) && (tipoEntidade != null)) {
                if ("ORG".equalsIgnoreCase(tipoEntidade)) {
                    filtros = " and rse.org_codigo = :codigoEntidade ";
                    queryParams.addValue("codigoEntidade", codigoEntidade);
                } else if ("EST".equalsIgnoreCase(tipoEntidade)) {
                    filtros = " and org.est_codigo = :codigoEntidade ";
                    queryParams.addValue("codigoEntidade", codigoEntidade);
                }
            }

            // Exclui a tabela de servidores excluidos
            String query = "drop temporary table if exists tb_ser_excluidos";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            // Cria a tabela temporaria de excluidos, que podem estar em quaisquer órgãos
            // ou estabelecimentos, por este motivo o filtro pelo tipo do usuário não pode ser aplicado
            query = "create temporary table tb_ser_excluidos ("
                    + " ser_codigo varchar(32),"
                    + " rse_codigo_excluido varchar(32),"
                    + " rse_codigo_ativo varchar(32),"
                    + " rse_matricula varchar(20),"
                    + " rse_data_admissao datetime,"
                    + " org_codigo varchar(32),"
                    + " est_codigo varchar(32),"
                    + " KEY `tmpidx_ser` (`ser_codigo`),"
                    + " KEY `tmpidx_org` (`org_codigo`),"
                    + " KEY `tmpidx_est` (`est_codigo`),"
                    + " KEY `tmpidx_mat` (`rse_matricula`))"
                    + " select ser_codigo, rse_codigo as rse_codigo_excluido, null as rse_codigo_ativo, rse_matricula, rse_data_admissao, org.org_codigo, org.est_codigo"
                    + " from tb_registro_servidor rse"
                    + " inner join tb_orgao org on (rse.org_codigo = org.org_codigo)"
                    + " where rse.srs_codigo IN ('" + TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '") + "')"
                    + " and rse.rse_data_carga is null "
                    ;

            LOG.trace(query);
            jdbc.update(query, queryParams);

            // Exclui a tabela de servidores transferidos
            query = "drop temporary table if exists tb_ser_transferidos";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            // Cria a tabela temporaria de transferidos a partir da comparacao dos excluidos obtidos anteriormente
            query = "create temporary table tb_ser_transferidos ("
                    + " ser_codigo varchar(32),"
                    + " rse_codigo_excluido varchar(32),"
                    + " rse_codigo_ativo varchar(32), "
                    + " KEY `tmpidx_ser` (`ser_codigo`),"
                    + " KEY `tmpidx_exc` (`rse_codigo_excluido`),"
                    + " KEY `tmpidx_ati` (`rse_codigo_ativo`))"
                    + " select exc.ser_codigo, exc.rse_codigo_excluido, rse.rse_codigo as rse_codigo_ativo"
                    + " from tb_ser_excluidos exc, tb_registro_servidor rse, tb_orgao orgE, tb_orgao org, tb_servidor serE, tb_servidor serT"
                    + " where serE.ser_cpf = serT.ser_cpf"
                    + " and rse.srs_codigo NOT IN ('" + TextHelper.joinWithEscapeSql(CodedValues.SRS_INATIVOS, "' , '") + "')"
                    + " and rse.org_codigo = org.org_codigo"
                    + " and orgE.org_codigo = exc.org_codigo"
                    + " and serE.ser_codigo = exc.ser_codigo"
                    + " and serT.ser_codigo = rse.ser_codigo"
                    + filtros;

            // Adiciona os demais filtros baseado no parâmetro de transferência
            query += adicionaFiltroTransferencia(preservaEstMatrTrans, queryParams);

            LOG.trace(query);
            jdbc.update(query, queryParams);

            // Verifica parâmetro de sistema que define ordem na geração dos transferidos quando há duplicidade
            // de destino para a mesma matrícula de origem
            final String clausulaOrdemTransf = (String) ParamSist.getInstance().getParam(CodedValues.TPC_ORDEM_PRIORIDADE_GERACAO_TRANSFERIDOS, AcessoSistema.getAcessoUsuarioSistema());
            if (!TextHelper.isNull(clausulaOrdemTransf)) {
                // Cria tabela temporária com os registros em duplicidade
                query = "drop temporary table if exists tb_ser_transferidos_dup";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "create temporary table tb_ser_transferidos_dup select t.rse_codigo_excluido from tb_ser_transferidos t group by t.rse_codigo_excluido having count(*) > 1";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                // Executa rotina de remoção dos duplicados
                removerDuplicadosTransferencia(clausulaOrdemTransf, "tb_ser_transferidos", "tb_ser_transferidos_dup");
            }

            // Texto a ser incluído na linha indicando a transferência do servidor
            final String textoTransferido = ApplicationResourcesHelper.getMessage("rotulo.servidor.rse.tipo.transferido", responsavel);

            query = "drop temporary table if exists tb_arq_transferidos";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "create temporary table tb_arq_transferidos ("
                    + "RSE_CODIGO_EXCLUIDO varchar(32) NOT NULL, "
                    + "RSE_CODIGO_ATIVO varchar(32) NOT NULL, "
                    + "ACAO varchar(40) NOT NULL, "
                    + "EST_IDENTIFICADOR varchar(40) NOT NULL, "
                    + "ORG_IDENTIFICADOR varchar(40) NOT NULL, "
                    + "RSE_MATRICULA varchar(20) NOT NULL, "
                    + "SER_NOME varchar(100) NOT NULL, "
                    + "NOVO_ESTABELECIMENTO varchar(40) NOT NULL, "
                    + "NOVO_ORGAO varchar(40) NOT NULL, "
                    + "NOVA_MATRICULA varchar(20) NOT NULL, "
                    + "DATA_MUDANCA datetime NOT NULL, "
                    + "KEY tmpidx_exc (rse_codigo_excluido), "
                    + "KEY tmpidx_ati (rse_codigo_ativo))"
                    ;
            LOG.trace(query);
            jdbc.update(query, queryParams);

            // Gera uma tabela igual ao arquivo de transferencia de servidores
            query = "insert into tb_arq_transferidos (RSE_CODIGO_EXCLUIDO, RSE_CODIGO_ATIVO, ACAO, EST_IDENTIFICADOR, ORG_IDENTIFICADOR, RSE_MATRICULA, SER_NOME, NOVO_ESTABELECIMENTO, NOVO_ORGAO, NOVA_MATRICULA, DATA_MUDANCA)"
                    + " select tra.rse_codigo_excluido, tra.rse_codigo_ativo, '" + textoTransferido + "', est.est_identificador, org.org_identificador, rse.rse_matricula, ser.ser_nome, est2.est_identificador, org2.org_identificador, rse2.rse_matricula, now()"
                    + " from tb_ser_transferidos tra, tb_registro_servidor rse, tb_registro_servidor rse2, tb_servidor ser, tb_servidor ser2, tb_orgao org, tb_orgao org2, tb_estabelecimento est, tb_estabelecimento est2"
                    + " where tra.rse_codigo_excluido = rse.rse_codigo"
                    + " and rse.ser_codigo = ser.ser_codigo"
                    + " and rse.org_codigo = org.org_codigo"
                    + " and org.est_codigo = est.est_codigo"
                    + " and tra.rse_codigo_ativo = rse2.rse_codigo"
                    + " and rse2.ser_codigo = ser2.ser_codigo"
                    + " and rse2.org_codigo = org2.org_codigo"
                    + " and org2.est_codigo = est2.est_codigo";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "SELECT COUNT(*) AS CONTADOR FROM tb_arq_transferidos";
            final Integer contador = jdbc.queryForObject(query, queryParams, Integer.class);

            query = null;
            if (contador != null && contador > 0) {
                query = "SELECT ACAO, EST_IDENTIFICADOR, ORG_IDENTIFICADOR, RSE_MATRICULA, SER_NOME, NOVO_ESTABELECIMENTO, NOVO_ORGAO, NOVA_MATRICULA, DATA_MUDANCA FROM tb_arq_transferidos";
            }

            return query;
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    @Override
    public void createRelRegistroServidor(String rseCodigoOrigem, String rseCodigoDestino, String tntCodigo, AcessoSistema responsavel) throws DAOException {
        final String query = "REPLACE INTO tb_relacionamento_registro_ser (RSE_CODIGO_ORIGEM, RSE_CODIGO_DESTINO, TNT_CODIGO, USU_CODIGO, RRE_DATA) "
                     + "VALUES (:rseCodigoOrigem , :rseCodigoDestino, :tntCodigo, :usuCodigo, NOW())";

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        try {
        	queryParams.addValue("rseCodigoOrigem", rseCodigoOrigem);
        	queryParams.addValue("rseCodigoDestino", rseCodigoDestino);
        	queryParams.addValue("tntCodigo", tntCodigo);
        	queryParams.addValue("usuCodigo", responsavel.getUsuCodigo());
            LOG.trace(query);
            jdbc.update(query, queryParams);
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }
}
