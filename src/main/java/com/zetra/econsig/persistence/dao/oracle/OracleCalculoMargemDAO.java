package com.zetra.econsig.persistence.dao.oracle;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import java.util.List;

import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.servico.NaturezaRelSvc;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.generic.GenericCalculoMargemDAO;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: OracleCalculoMargemDAO</p>
 * <p>Description: Implementacao do DAO de cálculo de margem para o Oracle</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OracleCalculoMargemDAO extends GenericCalculoMargemDAO {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(OracleCalculoMargemDAO.class);

    /**
     * Recalcula os valores utilizados das margens de acordo com os contratos
     * que incidem diretamente nestas margens, sejam contratos abertos que estarão
     * consumindo, seja contratos liquidados que estarão liberando margem.
     *
     * @param tipoEntidade : CSE/ EST / ORG / RSE
     * @param entCodigos : Códigos dos estabelecimentos / órgãos / registros servidores
     * @param controlaMargem : Indica se o sistema controla margem
     * @throws DAOException
     */
    @Override
    public void calcularMargemExtraUsada(String tipoEntidade, List<String> entCodigos, boolean controlaMargem) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            final String complementoJoinEnt = getComplementoJoin(tipoEntidade, entCodigos, "ade");
            final String complementoJoinPex = getComplementoJoinPeriodoExp(tipoEntidade, entCodigos, "ade");
            final String complementoWhere   = getComplementoWhere(tipoEntidade, entCodigos, queryParams);

            String query = "update tb_margem_registro_servidor mrs "
                         + "set mrs_margem_usada = 0 "
                         + "where 1=1 "
                         + getComplementoWhereExists(tipoEntidade, entCodigos, queryParams);
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "CALL dropTableIfExists('tmp_margem_extra_usada')";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "CALL createTemporaryTable('tmp_margem_extra_usada "
                  + "(rse_codigo varchar2(32), mar_codigo number(5,0), margem_usada number(13,2), primary key (rse_codigo, mar_codigo)) "
                  + "')";
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
                query = "insert into tmp_margem_extra_usada (rse_codigo, mar_codigo, margem_usada) "
                      + "select ade.rse_codigo, ade.ade_inc_margem as mar_codigo, "
                      + "sum(ade_vlr) as margem_usada "
                      + "from tb_aut_desconto ade "
                      + complementoJoinEnt
                      + "where (sad_codigo in ('" + TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_INCIDEM_MARGEM_LIQUIDA, "','") + "') "
                      + "or (sad_codigo in ('" + TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_INCIDEM_MARGEM_LIQ_SE_NAO_PAGOS, "','") + "') and coalesce(ade_paga, 'N') <> 'S' and ade_vlr_folha is null) "
                      + (prendeVlrEmAndNaoPago ? "or (sad_codigo = '" + CodedValues.SAD_EMANDAMENTO + "' and coalesce(ade_paga, 'N') <> 'S' and ade_vlr_folha is null))" : ")")
                      + "and ade_inc_margem <> 0 "
                      + complementoWhere
                      + "group by ade.rse_codigo, ade.ade_inc_margem";
            } else {
                /* --------------------------------------------------------------------------------------------------
                 * PASSO 1.2) Se controla margem, a margem usada inicial terá todos os contratos abertos, pois
                 * a folha envia a margem bruta e o sistema deve subtrair desta os contratos abertos.
                 */
                query = "insert into tmp_margem_extra_usada (rse_codigo, mar_codigo, margem_usada) "
                      + "select ade.rse_codigo, ade.ade_inc_margem as mar_codigo, "
                      + "sum(ade_vlr) as margem_usada "
                      + "from tb_aut_desconto ade "
                      + complementoJoinEnt
                      + "where sad_codigo in ('" + TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_INCIDEM_MARGEM_BRUTA, "','") + "') "
                      + "and ade_inc_margem <> 0 "
                      + complementoWhere
                      + "group by ade.rse_codigo, ade.ade_inc_margem";
            }
            LOG.trace(query);
            jdbc.update(query, queryParams);

            // DESENV-16566 - Adiciona os contratos em carência, porém já exportados para serem liberados da margem
            if (!controlaMargem && ParamSist.getBoolParamSist(CodedValues.TPC_LIBERA_MARGEM_ENVIADA_PELA_FOLHA_CONSIG_CARENCIA, AcessoSistema.getAcessoUsuarioSistema())
                    && ParamSist.getBoolParamSist(CodedValues.TPC_ENVIA_CONTRATOS_CARENCIA_MOV_FIN, AcessoSistema.getAcessoUsuarioSistema())) {
                throw new UnsupportedOperationException();
            }

            // Os contratos deferidos ou em andamento que tenham a ocorrência de margem preza, precisam ser considerados com o valor total da renegocição e não o valor atual na margem usada.
            // Por este motivo precisamos adicionar a diferença à margem usada. Pois o valor do contrato novo ja está sendo considerado.
            if (ParamSist.getBoolParamSist(CodedValues.TPC_PRENDER_MARGEM_RENEG_CONTRATRO_NOVO_MENOR_ANTIGOS, AcessoSistema.getAcessoUsuarioSistema())) {
                query = "CALL dropTableIfExists('tmp_mar_extra_reneg_diff')";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "CALL createTemporaryTable('tmp_mar_extra_reneg_diff "
                        + "(rse_codigo varchar2(32), mar_codigo number(5,0), margem_usada number(13,2), primary key (rse_codigo, mar_codigo)) "
                        + "')";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "insert into tmp_mar_extra_reneg_diff (rse_codigo, mar_codigo, margem_usada)"
                        + "select ade.rse_codigo, ade.ade_inc_margem, "
                        + "sum((oca.oca_ade_vlr_ant - oca.oca_ade_vlr_novo)) "
                        + "from tb_aut_desconto ade "
                        + "inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) "
                        + "where sad_codigo in ('" + TextHelper.join(CodedValues.SAD_CODIGOS_DEFERIDAS_OU_ANDAMENTO, "','") + "') "
                        + "and oca.toc_codigo = '" + CodedValues.TOC_RETENCAO_MARGEM_DENTRO_PRAZO_RENEGOCIACAO + "' "
                        + " AND NOT EXISTS (SELECT 1 FROM tb_ocorrencia_autorizacao oca1 WHERE oca1.ade_codigo = ade.ade_codigo "
                        + " AND oca1.toc_codigo ='" + CodedValues.TOC_LIBERACAO_MARGEM_APOS_PRAZO_RENEGOCIACAO + "' "
                        + " ) "
                        + "and ade_inc_margem != '0' "
                        + "group by ade.rse_codigo, ade.ade_inc_margem";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "update tmp_margem_extra_usada tmp "
                        + "set tmp.margem_usada = tmp.margem_usada + (select ren.margem_usada "
                        + "from tmp_mar_extra_reneg_diff ren where tmp.rse_codigo = ren.rse_codigo and tmp.mar_codigo = ren.mar_codigo) "
                        + "where exists (select 1 "
                        + "from tmp_mar_extra_reneg_diff ren where tmp.rse_codigo = ren.rse_codigo and tmp.mar_codigo = ren.mar_codigo and ren.margem_usada > 0.00)";
                LOG.trace(query);
                jdbc.update(query, queryParams);
            }

            /* --------------------------------------------------------------------------------------------------
             * PASSO 2) Adiciona à margem usada a diferença positiva entre contratos renegociados/comprados, ou
             * seja caso uma renegociação/compra seja com valor maior que o anterior, a diferença deve ser presa.
             */
            query = "CALL dropTableIfExists('tmp_margem_extra_usada_reneg')";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "CALL createTemporaryTable('tmp_margem_extra_usada_reneg "
                  + "(rse_codigo varchar2(32), mar_codigo number(5,0), ade_codigo varchar2(32), ade_vlr number(13,2), usada number(13,2), primary key (ade_codigo)) "
                  + "')";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "CALL createIndexOnTemporaryTable('tmp_margem_extra_usada_r_idx1', 'tmp_margem_extra_usada_reneg', 'rse_codigo, mar_codigo')";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "insert into tmp_margem_extra_usada_reneg (rse_codigo, mar_codigo, ade_codigo, ade_vlr, usada)"
                  + " select ade.rse_codigo, ade11.ade_inc_margem as mar_codigo, ade.ade_codigo,"
                  + " ade.ade_vlr, sum(ade11.ade_vlr) as usada"
                  + " from tb_aut_desconto ade11"
                  + " inner join tb_relacionamento_autorizacao rad11 on (ade11.ade_codigo = rad11.ade_codigo_origem)"
                  + " inner join tb_aut_desconto ade on (ade.ade_codigo = rad11.ade_codigo_destino)"
                  + complementoJoinEnt
                  + " where ade11.sad_codigo in ('" + TextHelper.join(CodedValues.SAD_CODIGOS_LIQUIDACAO, "','") + "') "
                  + " and ade.sad_codigo in ('" + CodedValues.SAD_AGUARD_CONF + "', '" + CodedValues.SAD_AGUARD_DEFER + "') "
                  + " and tnt_codigo in ('" + CodedValues.TNT_CONTROLE_RENEGOCIACAO + "', '" + CodedValues.TNT_CONTROLE_COMPRA + "') "
                  + " and ade11.ade_inc_margem <> 0 "
                  + " and ade.ade_inc_margem = 0 "
                  + complementoWhere
                  + " group by ade.rse_codigo, ade11.ade_inc_margem, ade.ade_codigo, ade.ade_vlr";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "CALL dropTableIfExists('tmp_margem_extra_reneg')";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "CALL createTemporaryTable('tmp_margem_extra_reneg "
                  + "(rse_codigo varchar2(32), mar_codigo number(5,0), margem_usada number(13,2), primary key (rse_codigo, mar_codigo)) "
                  + "')";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "insert into tmp_margem_extra_reneg (rse_codigo, mar_codigo, margem_usada) "
                  + " select rse_codigo, mar_codigo, "
                  + " sum(case when ade_vlr > usada then ade_vlr - usada else 0.00 end) margem_usada"
                  + " from tmp_margem_extra_usada_reneg"
                  + " group by rse_codigo, mar_codigo";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "update tmp_margem_extra_usada tmp "
                  + "set tmp.margem_usada = tmp.margem_usada + (select ren.margem_usada "
                  + "from tmp_margem_extra_reneg ren where tmp.rse_codigo = ren.rse_codigo and tmp.mar_codigo = ren.mar_codigo) "
                  + "where exists (select 1 "
                  + "from tmp_margem_extra_reneg ren where tmp.rse_codigo = ren.rse_codigo and tmp.mar_codigo = ren.mar_codigo and ren.margem_usada > 0.00)";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "delete from tmp_margem_extra_reneg where exists ("
                  + "select 1 from tmp_margem_extra_usada tmp where tmp.rse_codigo = tmp_margem_extra_reneg.rse_codigo and tmp.mar_codigo = tmp_margem_extra_reneg.mar_codigo)";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "insert into tmp_margem_extra_usada (rse_codigo, mar_codigo, margem_usada) "
                  + "select ren.rse_codigo, ren.mar_codigo, ren.margem_usada "
                  + "from tmp_margem_extra_reneg ren "
                  + "where ren.margem_usada > 0.00";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            /* --------------------------------------------------------------------------------------------------
             * PASSO 3) Atualiza a tabela de margem registro servidor, armazenando o valor da margem usada inicial
             * calculada pelas rotinas acima. Os próximos cálculos será feitos diretamente sobre a própria
             * tabela de margem registro servidor.
             */
            query = "update tb_margem_registro_servidor mrs "
                  + "set mrs_margem_usada = (select margem_usada "
                  + "from tmp_margem_extra_usada tmp where tmp.rse_codigo = mrs.rse_codigo and tmp.mar_codigo = mrs.mar_codigo) "
                  + "where exists (select 1 from tmp_margem_extra_usada tmp where tmp.rse_codigo = mrs.rse_codigo and tmp.mar_codigo = mrs.mar_codigo)";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            /* --------------------------------------------------------------------------------------------------
             * PASSO 4) Adiciona à margem usada as autorizações liquidadas neste período, ou seja aquelas
             * que ainda não foram para a folha e que fazem parte de renegociação/compra não concluída,
             * pois margem de compra/renegociação só deve ser liberada após a conclusão do processo.
             */
            query = "CALL dropTableIfExists('tmp_margem_extra_liq_reneg')";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "CALL createTemporaryTable('tmp_margem_extra_liq_reneg "
                  + "(rse_codigo varchar2(32), mar_codigo number(5,0), margem_liquidada number(13,2), primary key (rse_codigo, mar_codigo)) "
                  + "')";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "insert into tmp_margem_extra_liq_reneg (rse_codigo, mar_codigo, margem_liquidada) "
                  + "select ade.rse_codigo, ade8.ade_inc_margem as mar_codigo, "
                  + "sum(case when ade8.ade_tipo_vlr = 'P' then coalesce(ade8.ade_vlr_folha, ade8.ade_vlr) else ade8.ade_vlr end) as margem_liquidada "
                  + "from tb_aut_desconto ade8 "
                  + "inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade8.ade_codigo) "
                  + "inner join tb_relacionamento_autorizacao rad8 on (ade8.ade_codigo = rad8.ade_codigo_origem) "
                  + "inner join tb_aut_desconto ade on (ade.ade_codigo = rad8.ade_codigo_destino) "
                  + complementoJoinEnt
                  + "where ade8.ade_inc_margem <> 0 "
                  + "and ade8.sad_codigo = '" + CodedValues.SAD_LIQUIDADA + "' "
                  + "and toc_codigo = '" + CodedValues.TOC_TARIF_LIQUIDACAO + "' "
                  + "and oca_data > rad_data "
                  + "and tnt_codigo in ('" + CodedValues.TNT_CONTROLE_RENEGOCIACAO + "', '" + CodedValues.TNT_CONTROLE_COMPRA + "') "
                  + "and ade.sad_codigo in ('" + CodedValues.SAD_AGUARD_CONF + "', '" + CodedValues.SAD_AGUARD_DEFER + "') "
                  + "and ade.ade_inc_margem = 0 "
                  + complementoWhere
                  + "group by ade.rse_codigo, ade8.ade_inc_margem";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "update tb_margem_registro_servidor mrs "
                  + "set mrs_margem_usada = mrs_margem_usada + (select margem_liquidada "
                  + "from tmp_margem_extra_liq_reneg tmp where tmp.rse_codigo = mrs.rse_codigo and tmp.mar_codigo = mrs.mar_codigo) "
                  + "where exists (select 1 from tmp_margem_extra_liq_reneg tmp where tmp.rse_codigo = mrs.rse_codigo and tmp.mar_codigo = mrs.mar_codigo)";
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
                query = "CALL dropTableIfExists('tmp_margem_extra_liquidada')";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "CALL createTemporaryTable('tmp_margem_extra_liquidada "
                      + "(rse_codigo varchar2(32), mar_codigo number(5,0), margem_liquidada number(13,2), primary key (rse_codigo, mar_codigo)) "
                      + "')";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "insert into tmp_margem_extra_liquidada (rse_codigo, mar_codigo, margem_liquidada) "
                      + "select ade.rse_codigo, ade.ade_inc_margem as mar_codigo, "
                      + "sum(case when ade_tipo_vlr = 'P' then coalesce(ade_vlr_folha, ade_vlr) else ade_vlr end) as margem_liquidada "
                      + "from tb_aut_desconto ade "
                      + "inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) "
                      + complementoJoinEnt
                      + complementoJoinPex
                      + "where ade.ade_inc_margem <> 0 "
                      + "and ade_prd_pagas > 0 "
                      + "and sad_codigo = '" + CodedValues.SAD_LIQUIDADA + "' "
                      + "and toc_codigo = '" + CodedValues.TOC_TARIF_LIQUIDACAO + "' "
                      + "and oca_data > pex_data_fim "
                      + (!liberaMargemContratoLiqNaoPago ? "and coalesce(ade_vlr_folha, 0.00) > 0 " : "")
                      + (!liberaMargemConclusaoContrato ? "and (coalesce(ade_prazo, 999999999) > coalesce(ade_prd_pagas, 0) "
                      + " or add_months(ade_ano_mes_fim, coalesce(ade_carencia_final, 0) + " + carenciaFolha + ") > pex_periodo) " : "")
                      + complementoWhere
                      + "group by ade.rse_codigo, ade.ade_inc_margem";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "update tb_margem_registro_servidor mrs "
                      + "set mrs_margem_usada = mrs_margem_usada - (select margem_liquidada "
                      + "from tmp_margem_extra_liquidada tmp where tmp.rse_codigo = mrs.rse_codigo and tmp.mar_codigo = mrs.mar_codigo) "
                      + "where exists (select 1 from tmp_margem_extra_liquidada tmp where tmp.rse_codigo = mrs.rse_codigo and tmp.mar_codigo = mrs.mar_codigo)";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                if (liberaMargemConclusaoContrato) {
                    /* --------------------------------------------------------------------------------------------------
                     * PASSO 6) Se não controla margem, subtrai da margem usada as autorizações concluidas neste período,
                     * ou seja aquelas que a folha ainda abateu na margem por terem sido concluidas no mês atual.
                     */
                    query = "CALL dropTableIfExists('tmp_margem_extra_concluida')";
                    LOG.trace(query);
                    jdbc.update(query, queryParams);

                    query = "CALL createTemporaryTable('tmp_margem_extra_concluida "
                          + "(rse_codigo varchar2(32), mar_codigo number(5,0), margem_concluida number(13,2), primary key (rse_codigo, mar_codigo)) "
                          + "')";
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

                    query = "insert into tmp_margem_extra_concluida (rse_codigo, mar_codigo, margem_concluida) "
                          + "select ade.rse_codigo, ade.ade_inc_margem as mar_codigo, sum(ade_vlr) as margem_concluida "
                          + "from tb_ocorrencia_autorizacao oca "
                          + "inner join tb_aut_desconto ade on (oca.ade_codigo = ade.ade_codigo) "
                          + complementoJoinEnt
                          + complementoJoinPex
                          + "where ade.ade_inc_margem <> 0 "
                          + "and sad_codigo = '" + CodedValues.SAD_CONCLUIDO + "' "
                          + "and toc_codigo = '" + CodedValues.TOC_CONCLUSAO_CONTRATO + "' "
                          + "and oca_data > PEX_DATA_FIM "
                          + clausulaConclusaoFerias
                          + clausulaSaldoParcelas
                          + complementoWhere
                          + "group by ade.rse_codigo, ade.ade_inc_margem";
                    LOG.trace(query);
                    jdbc.update(query, queryParams);

                    query = "update tb_margem_registro_servidor mrs "
                          + "set mrs_margem_usada = mrs_margem_usada - (select margem_concluida "
                          + "from tmp_margem_extra_concluida tmp where tmp.rse_codigo = mrs.rse_codigo and tmp.mar_codigo = mrs.mar_codigo) "
                          + "where exists (select 1 from tmp_margem_extra_concluida tmp where tmp.rse_codigo = mrs.rse_codigo and tmp.mar_codigo = mrs.mar_codigo)";
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

                query = "CALL dropTableIfExists('tmp_margem_extra_alterada')";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "CALL createTemporaryTable('tmp_margem_extra_alterada "
                      + "(rse_codigo varchar2(32), mar_codigo number(5,0), margem_alterada number(13,2), primary key (rse_codigo, mar_codigo)) "
                      + "')";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "insert into tmp_margem_extra_alterada (rse_codigo, mar_codigo, margem_alterada) "
                      + "select ade.rse_codigo, ade.ade_inc_margem as mar_codigo, sum(coalesce(ade_vlr_parcela_folha, ade_vlr) - ade_vlr_folha) as margem_alterada "
                      + "from tb_aut_desconto ade "
                      + complementoJoinEnt
                      + complementoJoinPex
                      + "where sad_codigo = '" + CodedValues.SAD_EMANDAMENTO + "' " // CONTRATO EM ANDAMENTO
                      + "and ade_tipo_vlr = 'F' "              // COM VALOR FIXO
                      + "and ade_inc_margem <> 0 "             // QUE INCIDE NA MARGEM
                      + "and coalesce(ade_paga, 'N') = 'S' "   // ONDE A ADE FOI PAGA
                      + "and ade_vlr_folha IS NOT NULL "       // COM VALOR FOLHA

                      + (subtraiPagamentoParcial
                      ? "and ((ade_vlr_folha < coalesce(ade_vlr_parcela_folha, ade_vlr)) "      // VLR PAGO MENOR DO QUE ADE_VLR OU
                      + "  or (ade_vlr_folha > coalesce(ade_vlr_parcela_folha, ade_vlr) "       // VLR PAGO MAIOR DO QUE ADE_VLR E TEM ALTERACAO
                      + "and EXISTS (select 1 from tb_ocorrencia_autorizacao oca where ade.ade_codigo = oca.ade_codigo and toc_codigo = '" + CodedValues.TOC_ALTERACAO_CONTRATO + "' and oca_data > PEX_DATA_FIM))) "

                      : "and (ade_vlr_folha <> coalesce(ade_vlr_parcela_folha, ade_vlr)) "      // VLR PAGO DIFERENTE DO QUE ADE_VLR E TEM ALTERACAO
                      + "and EXISTS (select 1 from tb_ocorrencia_autorizacao oca where ade.ade_codigo = oca.ade_codigo and toc_codigo = '" + CodedValues.TOC_ALTERACAO_CONTRATO + "' and oca_data > PEX_DATA_FIM) "
                      )

                      + complementoWhere
                      + "group by ade.rse_codigo, ade.ade_inc_margem";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "update tb_margem_registro_servidor mrs "
                      + "set mrs_margem_usada = mrs_margem_usada + (select margem_alterada "
                      + "from tmp_margem_extra_alterada tmp where tmp.rse_codigo = mrs.rse_codigo and tmp.mar_codigo = mrs.mar_codigo) "
                      + "where exists (select 1 from tmp_margem_extra_alterada tmp where tmp.rse_codigo = mrs.rse_codigo and tmp.mar_codigo = mrs.mar_codigo)";
                LOG.trace(query);
                jdbc.update(query, queryParams);

            } else {
                /* --------------------------------------------------------------------------------------------------
                 * PASSO 8) Se controla margem, adiciona na margem usada o valor descontado das parcelas de
                 * consignações de tipo percentual, subtraido do valor nominal das consignaçãoes que já foi
                 * adicionado na margem usada.
                 */
                if (ParamSist.paramEquals(CodedValues.TPC_SUBTRAIR_VALOR_PAGO_ADE_PERCENTUAL_INDEPENDENTE_PERIODO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                    throw new UnsupportedOperationException();
                }

                query = "CALL dropTableIfExists('tmp_margem_extra_percentual')";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "CALL createTemporaryTable('tmp_margem_extra_percentual "
                      + "(rse_codigo varchar2(32), mar_codigo number(5,0), margem_usada number(13,2), primary key (rse_codigo, mar_codigo)) "
                      + "')";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "insert into tmp_margem_extra_percentual (rse_codigo, mar_codigo, margem_usada) "
                      + "select ade.rse_codigo, ade.ade_inc_margem as mar_codigo, "
                      + "sum(coalesce(prd_vlr_realizado, ade_vlr_folha) - ade_vlr) as margem_usada "
                      + "from tb_aut_desconto ade "
                      + complementoJoinEnt
                      + complementoJoinPex
                      + "left outer join tb_parcela_desconto prd on (ade.ade_codigo = prd.ade_codigo "
                      + "and spd_codigo = '" + CodedValues.SPD_LIQUIDADAFOLHA + "' " // PARCELA DESCONTADA
                      + "and prd_data_desconto = pex_periodo) " // DO ULTIMO PERIODO DE RETORNO
                      + "where sad_codigo in ('" + TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_INCIDEM_MARGEM_BRUTA, "','") + "') " // CONTRATO ATIVO
                      + "and ade_tipo_vlr = 'P' " // PERCENTUAL
                      + "and ade_inc_margem <> 0 " // INCIDE NA MARGEM
                      + "and (ade_vlr_folha is not null or prd_vlr_realizado is not null) " // TENHA SIDO PAGO
                      + complementoWhere
                      + "group by ade.rse_codigo, ade.ade_inc_margem";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                query = "update tb_margem_registro_servidor mrs "
                      + "set mrs_margem_usada = mrs_margem_usada + (select margem_usada "
                      + "from tmp_margem_extra_percentual tmp where tmp.rse_codigo = mrs.rse_codigo and tmp.mar_codigo = mrs.mar_codigo) "
                      + "where exists (select 1 from tmp_margem_extra_percentual tmp where tmp.rse_codigo = mrs.rse_codigo and tmp.mar_codigo = mrs.mar_codigo)";
                LOG.trace(query);
                jdbc.update(query, queryParams);

                final boolean liberaMargemConclusaoContrato = ParamSist.paramEquals(CodedValues.TPC_LIB_MARGEM_CONCLUSAO_CONTRATO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()); // Default: Não
                final boolean liberaMargemConclusaoFerias = !ParamSist.paramEquals(CodedValues.TPC_LIB_MARGEM_CONCLUSAO_CONTRATO_FERIAS, CodedValues.TPC_NAO, AcessoSistema.getAcessoUsuarioSistema()); // Default: Sim
                if (!liberaMargemConclusaoContrato || !liberaMargemConclusaoFerias) {
                    /* --------------------------------------------------------------------------------------------------
                     * PASSO 9) Se controla margem, Adiciona na margem usada as autorizações concluidas por desconto
                     * de férias neste período.
                     */
                    query = "CALL dropTableIfExists('tmp_margem_extra_concluida')";
                    LOG.trace(query);
                    jdbc.update(query, queryParams);

                    query = "CALL createTemporaryTable('tmp_margem_extra_concluida "
                          + "(rse_codigo varchar2(32), mar_codigo number(5,0), margem_concluida number(13,2), primary key (rse_codigo, mar_codigo)) "
                          + "')";
                    LOG.trace(query);
                    jdbc.update(query, queryParams);

                    query = "insert into tmp_margem_extra_concluida (rse_codigo, mar_codigo, margem_concluida) "
                          + "select ade.rse_codigo, ade.ade_inc_margem as mar_codigo, sum(ade_vlr) as margem_concluida "
                          + "from tb_ocorrencia_autorizacao oca "
                          + "inner join tb_aut_desconto ade on (oca.ade_codigo = ade.ade_codigo) "
                          + complementoJoinEnt
                          + complementoJoinPex
                          + "where ade_inc_margem <> 0 "
                          + "and sad_codigo = '" + CodedValues.SAD_CONCLUIDO + "' "
                          + "and toc_codigo = '" + CodedValues.TOC_CONCLUSAO_CONTRATO + "' "
                          + "and oca_data > pex_data_fim "
                          + (!liberaMargemConclusaoContrato ? ""
                          : "and exists(select 1 from tb_parcela_desconto prd "
                          + "           where prd.ade_codigo = ade.ade_codigo "
                          + "           and prd.spd_codigo = '" + CodedValues.SPD_LIQUIDADAFOLHA + "' "
                          + "           and prd.prd_data_desconto > pex.pex_periodo) ")
                          + complementoWhere
                          + "group by ade.rse_codigo, ade.ade_inc_margem";
                    LOG.trace(query);
                    jdbc.update(query, queryParams);

                    query = "update tb_margem_registro_servidor mrs "
                          + "set mrs_margem_usada = mrs_margem_usada + (select margem_concluida "
                          + "from tmp_margem_extra_concluida tmp where tmp.rse_codigo = mrs.rse_codigo and tmp.mar_codigo = mrs.mar_codigo) "
                          + "where exists (select 1 from tmp_margem_extra_concluida tmp where tmp.rse_codigo = mrs.rse_codigo and tmp.mar_codigo = mrs.mar_codigo)";
                    LOG.trace(query);
                    jdbc.update(query, queryParams);

                }
            }

            /* --------------------------------------------------------------------------------------------------
             * PASSO 10) Adiciona à margem usada as autorizações de prazo indeterminado liquidadas associadas
             * a serviço que esteja configurado para só liberar a margem na próxima carga de margem.
             */
            query = "CALL dropTableIfExists('tmp_margem_extra_liq_prz_indet')";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "CALL createTemporaryTable('tmp_margem_extra_liq_prz_indet "
                  + "(rse_codigo varchar2(32), mar_codigo number(5,0), margem_liquidada number(13,2), primary key (rse_codigo, mar_codigo)) "
                  + "')";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "insert into tmp_margem_extra_liq_prz_indet (rse_codigo, mar_codigo, margem_liquidada) "
                  + "select rse.rse_codigo, ade.ade_inc_margem, "
                  + "sum(case when ade.ade_tipo_vlr = 'P' then coalesce(ade.ade_vlr_folha, ade.ade_vlr) else ade.ade_vlr end) as margem_liquidada "
                  + "from tb_aut_desconto ade "
                  + "inner join tb_ocorrencia_autorizacao oca on (oca.ade_codigo = ade.ade_codigo) "
                  + "inner join tb_verba_convenio vco on (vco.vco_codigo = ade.vco_codigo) "
                  + "inner join tb_convenio cnv on (cnv.cnv_codigo = vco.cnv_codigo) "
                  + "inner join tb_param_svc_consignante pse on (cnv.svc_codigo = pse.svc_codigo) "
                  + (complementoJoinEnt.isEmpty() ? "inner join tb_registro_servidor rse on (ade.rse_codigo = rse.rse_codigo) " : "")
                  + complementoJoinEnt
                  + "where ade.ade_inc_margem <> 0 "
                  + "and ade.ade_prazo is null "
                  + "and ade.sad_codigo = '" + CodedValues.SAD_LIQUIDADA + "' "
                  + "and oca.toc_codigo = '" + CodedValues.TOC_TARIF_LIQUIDACAO + "' "
                  + "and pse.tps_codigo = '" + CodedValues.TPS_PRENDE_MARGEM_LIQ_ADE_PRZ_INDET_ATE_CARGA_MARGEM + "' "
                  + "and pse.pse_vlr = '" + CodedValues.PSE_BOOLEANO_SIM + "' "
                  + "and oca.oca_data > rse.rse_data_carga "
                  + complementoWhere
                  + "group by rse.rse_codigo, ade.ade_inc_margem";
            LOG.trace(query);
            jdbc.update(query, queryParams);

            query = "update tb_margem_registro_servidor mrs "
                  + "set mrs_margem_usada = mrs_margem_usada + (select margem_liquidada "
                  + "from tmp_margem_extra_liq_prz_indet tmp where tmp.rse_codigo = mrs.rse_codigo and tmp.mar_codigo = mrs.mar_codigo) "
                  + "where exists (select 1 from tmp_margem_extra_liq_prz_indet tmp where tmp.rse_codigo = mrs.rse_codigo and tmp.mar_codigo = mrs.mar_codigo)";
            LOG.trace(query);
            jdbc.update(query, queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Recalcula o valor restante das margens, de acordo com o valor utilizado
     * e o valor enviado pela folha.
     *
     * @param tipoEntidade : CSE/ EST / ORG / RSE
     * @param entCodigos : Códigos dos estabelecimentos / órgãos / registros servidores
     * @throws DAOException
     */
    @Override
    public void calcularMargemExtraRestante(String tipoEntidade, List<String> entCodigos) throws DAOException {

        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {

            final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();

            final String query = "update tb_margem_registro_servidor mrs "
                         + " set mrs.mrs_margem_rest = mrs.mrs_margem - mrs.mrs_margem_usada "
                         + " where 1=1 "
                         + getComplementoWhereExists(tipoEntidade, entCodigos, queryParams)
                         ;
            LOG.trace(query);
            jdbc.update(query, queryParams);

        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Atualiza o valor das margens de acordo com o casamento de margem pela direita.
     * Diagrama:
     *
     *  +----------------------------------------------+
     *  |                    A                         |
     *  +---------------------------------+------------+
     *  |              B                  |            ^
     *  +----------------+----------------+            |
     *  |       C        |                ^
     *  +----------------+                |
     *                   ^
     *                   |
     *
     * Cálculos:
     *
     *   usada A = usada A + usada B
     *   usada A = usada A + usada C
     *   usada B = usada B + usada C
     *
     *   rest A = margem A - usada A
     *   rest B = min(rest A, margem B - usada B)
     *   rest C = min(rest B, margem C - usada C)
     *
     * @param tipoEntidade : CSE/ EST / ORG / RSE
     * @param entCodigos : Códigos dos estabelecimentos / órgãos / registros servidores
     * @param marCodigos : Códigos das margens que estão sendo casadas, ordenada na sequência do casamento
     * @throws DAOException
     */
    @Override
    public void calcularMargemExtraCasadaDireita(String tipoEntidade, List<String> entCodigos, List<Short> marCodigos) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            final StringBuilder query = new StringBuilder();

            for (int i = 0; i < marCodigos.size(); i++) {
                for (int j = i+1; j < marCodigos.size(); j++) {
                	query.setLength(0);
                    query.append("update tb_margem_registro_servidor mrs").append(marCodigos.get(i));
                    query.append(" set mrs").append(marCodigos.get(i)).append(".mrs_margem_usada");
                    query.append("  = mrs").append(marCodigos.get(i)).append(".mrs_margem_usada");
                    query.append("  + (select mrs").append(marCodigos.get(j)).append(".mrs_margem_usada");
                    query.append(" from tb_margem_registro_servidor mrs").append(marCodigos.get(j));
                    query.append(getComplementoJoin(tipoEntidade, entCodigos, "mrs" + marCodigos.get(j)));
                    query.append(" where 1 = 1 ");
                    query.append(" and mrs").append(marCodigos.get(i)).append(".rse_codigo = mrs").append(marCodigos.get(j)).append(".rse_codigo");
                    query.append(" and mrs").append(marCodigos.get(j)).append(".mar_codigo = ").append(marCodigos.get(j));
                    query.append(getComplementoWhere(tipoEntidade, entCodigos, queryParams));
                    query.append(") ");
                    query.append(" where 1=1");
                    query.append(" and mrs").append(marCodigos.get(i)).append(".mar_codigo = ").append(marCodigos.get(i));
                    LOG.trace(query);
                    jdbc.update(query.toString(), queryParams);
                }
            }

            for (int i = 0; i < marCodigos.size(); i++) {
                if (i == 0) {
                    query.setLength(0);
                    query.append("update tb_margem_registro_servidor mrs").append(marCodigos.get(i));
                    query.append(" set mrs").append(marCodigos.get(i)).append(".mrs_margem_rest");
                    query.append("   = mrs").append(marCodigos.get(i)).append(".mrs_margem - mrs").append(marCodigos.get(i)).append(".mrs_margem_usada");
                    query.append(" where 1=1");
                    query.append(" and mrs").append(marCodigos.get(i)).append(".mar_codigo = ").append(marCodigos.get(i));
                    query.append(" and exists (select 1 ");
                    query.append(" from tb_registro_servidor rse");
                    query.append(" inner join tb_margem_registro_servidor mrs on (rse.rse_codigo = mrs.rse_codigo)");
                    query.append(getComplementoJoin(tipoEntidade, entCodigos, "mrs" + marCodigos.get(i)));
                    query.append(" where rse.rse_codigo = mrs").append(marCodigos.get(i)).append(".rse_codigo");
                    query.append(getComplementoWhereExists(tipoEntidade, entCodigos, queryParams));
                    query.append(" ) ");
                    LOG.trace(query);
                    jdbc.update(query.toString(), queryParams);
                } else {
                    query.setLength(0);
                    query.append("update tb_margem_registro_servidor mrs").append(marCodigos.get(i));
                    query.append(" set mrs").append(marCodigos.get(i)).append(".mrs_margem_rest");
                    query.append("  = least((select mrs").append(marCodigos.get(i-1)).append(".mrs_margem_rest ");
                    query.append(" from tb_margem_registro_servidor mrs").append(marCodigos.get(i-1));
                    query.append(getComplementoJoin(tipoEntidade, entCodigos, "mrs" + marCodigos.get(i-1)));
                    query.append(" where 1=1");
                    query.append(" and mrs").append(marCodigos.get(i)).append(".rse_codigo = mrs").append(marCodigos.get(i-1)).append(".rse_codigo");
                    query.append(" and mrs").append(marCodigos.get(i-1)).append(".mar_codigo = ").append(marCodigos.get(i-1));
                    query.append(getComplementoWhere(tipoEntidade, entCodigos, queryParams));
                    query.append(") ");
                    query.append("          ,mrs").append(marCodigos.get(i)).append(".mrs_margem - mrs").append(marCodigos.get(i)).append(".mrs_margem_usada");
                    query.append(" )");
                    query.append(" where 1=1");
                    query.append(" and mrs").append(marCodigos.get(i)).append(".mar_codigo = ").append(marCodigos.get(i));
                    LOG.trace(query);
                    jdbc.update(query.toString(), queryParams);
                }
            }
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Atualiza o valor das margens de acordo com o casamento de margem pela esquerda.
     * Diagrama:
     *
     * +----------------------------------------------+
     * |                    A                         |
     * +---------------------------------+------------+
     * |              B                  |
     * +----------------+----------------+
     * |       C        |
     * +----------------+
     * ^
     * |
     *
     * Cálculos:
     *
     *  usada A = usada A + usada B
     *  usada A = usada A + usada C
     *  usada B = usada A
     *  usada C = usada A
     *
     *  rest A = margem A - usada A
     *  rest B = margem B - usada B
     *  rest C = margem C - usada C
     *
     * @param tipoEntidade : CSE/ EST / ORG / RSE
     * @param entCodigos : Códigos dos estabelecimentos / órgãos / registros servidores
     * @param marCodigos : Códigos das margens que estão sendo casadas, ordenada na sequência do casamento
     * @throws DAOException
     */
    @Override
    public void calcularMargemExtraCasadaEsquerda(String tipoEntidade, List<String> entCodigos, List<Short> marCodigos) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            final StringBuilder query = new StringBuilder();

            for (int i = 0; i < marCodigos.size(); i++) {
                if (i == 0) {
                    for (int j = i+1; j < marCodigos.size(); j++) {
                    	query.setLength(0);
                        query.append("update tb_margem_registro_servidor mrs").append(marCodigos.get(i));
                        query.append(" set mrs").append(marCodigos.get(i)).append(".mrs_margem_usada");
                        query.append("  = mrs").append(marCodigos.get(i)).append(".mrs_margem_usada");
                        query.append("  + (select mrs").append(marCodigos.get(j)).append(".mrs_margem_usada");
                        query.append(" from tb_margem_registro_servidor mrs").append(marCodigos.get(j));
                        query.append(getComplementoJoin(tipoEntidade, entCodigos, "mrs" + marCodigos.get(j)));
                        query.append(" where 1 = 1 ");
                        query.append(" and mrs").append(marCodigos.get(i)).append(".rse_codigo = mrs").append(marCodigos.get(j)).append(".rse_codigo");
                        query.append(" and mrs").append(marCodigos.get(j)).append(".mar_codigo = ").append(marCodigos.get(j));
                        query.append(getComplementoWhere(tipoEntidade, entCodigos, queryParams));
                        query.append(") ");
                        query.append(" where 1=1");
                        query.append(" and mrs").append(marCodigos.get(i)).append(".mar_codigo = ").append(marCodigos.get(i));
                        LOG.trace(query);
                        jdbc.update(query.toString(), queryParams);
                    }
                } else {
                    query.setLength(0);
                    query.append("update tb_margem_registro_servidor mrs").append(marCodigos.get(i));
                    query.append(" set mrs").append(marCodigos.get(i)).append(".mrs_margem_usada");
                    query.append(" = (select mrs").append(marCodigos.get(0)).append(".mrs_margem_usada");
                    query.append(" from tb_margem_registro_servidor mrs").append(marCodigos.get(0));
                    query.append(getComplementoJoin(tipoEntidade, entCodigos, "mrs" + marCodigos.get(0)));
                    query.append(" where 1=1");
                    query.append(" and mrs").append(marCodigos.get(i)).append(".rse_codigo = mrs").append(marCodigos.get(0)).append(".rse_codigo");
                    query.append(" and mrs").append(marCodigos.get(0)).append(".mar_codigo = ").append(marCodigos.get(0));
                    query.append(getComplementoWhere(tipoEntidade, entCodigos, queryParams));
                    query.append(") ");
                    query.append(" where 1=1");
                    query.append(" and mrs").append(marCodigos.get(i)).append(".mar_codigo = ").append(marCodigos.get(i));
                    LOG.trace(query);
                    jdbc.update(query.toString(), queryParams);
                }
            }

            for (final Short element : marCodigos) {
                query.setLength(0);
                query.append("update tb_margem_registro_servidor mrs").append(element);
                query.append(" set mrs").append(element).append(".mrs_margem_rest");
                query.append("   = mrs").append(element).append(".mrs_margem - mrs").append(element).append(".mrs_margem_usada");
                query.append(" where 1=1");
                query.append(" and mrs").append(element).append(".mar_codigo = ").append(element);
                query.append(getComplementoWhereExists(tipoEntidade, entCodigos, queryParams));
                LOG.trace(query);
                jdbc.update(query.toString(), queryParams);
            }
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Atualiza o valor das margens de acordo com o casamento de margem lateralmente.
     * Diagrama:
     *
     * +--------------------------------+----------------+----------+
     * |             A                  |        B       |     C    |
     * +--------------------------------+----------------+----------+
     * ^                                ^                ^
     * |                                |                |
     *
     * Cálculos:
     *
     *  rest B = margem B - usada B + min(0, rest A)
     *  rest C = margem C - usada C + min(0, rest B)
     *
     * @param tipoEntidade : CSE/ EST / ORG / RSE
     * @param entCodigos : Códigos dos estabelecimentos / órgãos / registros servidores
     * @param marCodigos : Códigos das margens que estão sendo casadas, ordenada na sequência do casamento
     * @throws DAOException
     */
    @Override
    public void calcularMargemExtraCasadaLateral(String tipoEntidade, List<String> entCodigos, List<Short> marCodigos) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            final StringBuilder query = new StringBuilder();
            for (int i = 1; i < marCodigos.size(); i++) {
                query.setLength(0);
                query.append("update tb_margem_registro_servidor mrs").append(marCodigos.get(i));
                query.append(" set mrs").append(marCodigos.get(i)).append(".mrs_margem_rest");
                query.append("  = mrs").append(marCodigos.get(i)).append(".mrs_margem");
                query.append("  - mrs").append(marCodigos.get(i)).append(".mrs_margem_usada");
                query.append("  + least(0, (select mrs").append(marCodigos.get(i-1)).append(".mrs_margem_rest");
                query.append(" from tb_margem_registro_servidor mrs").append(marCodigos.get(i-1));
                query.append(getComplementoJoin(tipoEntidade, entCodigos, "mrs" + marCodigos.get(i-1)));
                query.append(" where 1=1");
                query.append(" and mrs").append(marCodigos.get(i)).append(".rse_codigo = mrs").append(marCodigos.get(i-1)).append(".rse_codigo");
                query.append(" and mrs").append(marCodigos.get(i-1)).append(".mar_codigo = ").append(marCodigos.get(i-1));
                query.append(getComplementoWhere(tipoEntidade, entCodigos, queryParams));
                query.append(")) ");
                query.append(" where 1=1");
                query.append(" and mrs").append(marCodigos.get(i)).append(".mar_codigo = ").append(marCodigos.get(i));
                LOG.trace(query);
                jdbc.update(query.toString(), queryParams);
            }
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Atualiza o valor das margens restantes de acordo com o casamento de margem limitado ao mínimo.
     * Neste tipo de casamento, as margens usadas não são afetadas, apenas a margem restante é limitada
     * ao mínimo da margem restante anterior.
     *
     * @param tipoEntidade : CSE/ EST / ORG / RSE
     * @param entCodigos : Códigos dos estabelecimentos / órgãos / registros servidores
     * @param marCodigos : Códigos das margens que estão sendo casadas, ordenada na sequência do casamento
     * @throws DAOException
     */
    @Override
    public void calcularMargemExtraCasadaMinimo(String tipoEntidade, List<String> entCodigos, List<Short> marCodigos) throws DAOException {
        final NamedParameterJdbcTemplate jdbc = DBHelper.getNamedParameterJdbcTemplate();
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();
        try {
            final StringBuilder query = new StringBuilder();

            for (int i = 1; i < marCodigos.size(); i++) {
                query.setLength(0);
                query.append("update tb_margem_registro_servidor mrs").append(marCodigos.get(i));
                query.append(" set mrs").append(marCodigos.get(i)).append(".mrs_margem_rest");
                query.append("  = least((select mrs").append(marCodigos.get(i-1)).append(".mrs_margem_rest ");
                query.append(" from tb_margem_registro_servidor mrs").append(marCodigos.get(i-1));
                query.append(getComplementoJoin(tipoEntidade, entCodigos, "mrs" + marCodigos.get(i-1)));
                query.append(" where 1=1");
                query.append(" and mrs").append(marCodigos.get(i)).append(".rse_codigo = mrs").append(marCodigos.get(i-1)).append(".rse_codigo");
                query.append(" and mrs").append(marCodigos.get(i-1)).append(".mar_codigo = ").append(marCodigos.get(i-1));
                query.append(getComplementoWhere(tipoEntidade, entCodigos, queryParams));
                query.append(") ");
                query.append("          ,mrs").append(marCodigos.get(i)).append(".mrs_margem - mrs").append(marCodigos.get(i)).append(".mrs_margem_usada");
                query.append(" )");
                query.append(" where 1=1");
                query.append(" and mrs").append(marCodigos.get(i)).append(".mar_codigo = ").append(marCodigos.get(i));
                LOG.trace(query);
                jdbc.update(query.toString(), queryParams);
            }
        } catch (final DataAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DAOException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
    }

    /**
     * Cálcula o valor médio das margens dos últimos períodos de acordo com o parâmetro de sistema 873
     *
     * @param tipoEntidade : CSE/ EST / ORG / RSE
     * @param entCodigos : Códigos dos estabelecimentos / órgãos / registros servidores
     * @param marCodigos : Códigos das margens que estão sendo casadas, ordenada na sequência do casamento
     * @throws DAOException
     */
    @Override
    public void calcularMediaMargem(int periodoMediaMargem) throws DAOException {
        throw new UnsupportedOperationException();
    }
}
