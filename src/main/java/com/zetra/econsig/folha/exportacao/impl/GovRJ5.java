package com.zetra.econsig.folha.exportacao.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.zetra.econsig.delegate.PeriodoDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
/**
 * <p>Title: GovRJ5</p>
 * <p>Description: Implementações específicas para GovRJ - Governo do Estado do Rio de Janeiro.</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft</p>
 * $
 * $
 * $
 */
public class GovRJ5 extends GovRJ3 {

    /*
     * Os Contratos de cartão que irão movimento, são contratos de lançamento, então estes contratos não incidem na margem.
     * Diante disso, precisamos validar as margens das reservas, e não validando o contrato olhando a lógoica a partir da margem restante,
     * mas a partir da margem folha, pois posso ter lançamentos que caiba na margem e a consignatária não aceite parcial, então deixaremos de enviar o contrato.
     * Ex: Margem Reserva R$ 500,00, Reserva R$ 600,00 , Margem Restante R$ -100,00, Lançamento R$ 500,00.
     *
     * Neste cenário de exemplo acima, o contrato deve ser enviado, pois a margem é o valor exato da margem, porém não seria enviado, pois o valor da Margem restante está negativo.
     */
    protected static final String DATA_INI_MARGEM_ADICIONAL = "2016-02-14 00:00:00";
    protected static final String DATA_FIM_MARGEM_ADICIONAL = "2021-12-14 23:59:59";

    @Override
    protected List<TransferObject> listaContratosSemMargemCandidatosv2(List<Short> marCodigos) throws ExportaMovimentoException {
        final MapSqlParameterSource queryParams = new MapSqlParameterSource();

        final StringBuilder query = new StringBuilder();
        query.append("select tmp.rse_codigo, tmp.ade_codigo, tmp.ade_vlr, tmp.ade_data, ");
        query.append("case ");
        query.append("  when tmp.ade_inc_margem = " + MARGEM_CONSIGNACAO_35 + " and (tmp.ade_data < '" + DATA_INI_MUDANCA_MARGEM + "') then " + MARGEM_CONSIGNACAO_40 + " ");
        query.append("  else tmp.ade_inc_margem end AS ade_inc_margem, ");
        query.append(" tmp.autoriza_pgt_parcial, tmp.svc_prioridade, tmp.cnv_prioridade, tmp.ade_ano_mes_ini_ref, tmp.ade_ano_mes_ini, tmp.ade_data_ref, tmp.ade_numero ");
        query.append("from tb_tmp_exportacao tmp ");
        query.append("inner join tb_registro_servidor rse ON (tmp.rse_codigo = rse.rse_codigo) ");
        query.append("inner join tb_periodo_exportacao pex ON (pex.org_codigo = rse.org_codigo) ");
        query.append("where tmp.ade_inc_margem not in ('0','2','3') ");
        query.append("and ((");
        query.append("select mrs_margem_rest + coalesce((").append(queryMargemUsadaPosCorte(queryParams)).append("), 0.00) ");
        query.append("from tb_margem_registro_servidor mrs ");
        query.append("where mrs.rse_codigo = tmp.rse_codigo ");
        query.append("and mrs.mar_codigo = (");
        query.append("case ");
        // DESENV-16229 : Contratos anteriores a 14/02/2016 devem respeitar as margens de 40/50
        query.append("  when tmp.ade_inc_margem = " + MARGEM_CONSIGNACAO_35 + " and (tmp.ade_data < '" + DATA_INI_MUDANCA_MARGEM + "') then " + MARGEM_CONSIGNACAO_40 + " ");
        // Demais casos, valida contra a margem ao qual incide
        query.append("  else tmp.ade_inc_margem end) ");
        query.append(") < 0.00 ");
        query.append(" OR ( tmp.ade_inc_margem = 1 and tmp.rse_margem_rest + COALESCE((").append(queryMargemUsadaPosCorte(CodedValues.INCIDE_MARGEM_SIM, queryParams)).append("), 0.00) < 0.00) ");
        query.append(") ");
        query.append(" UNION ALL ");
        query.append("SELECT tmp.rse_codigo, tmp.ade_codigo, tmp.ade_vlr, ade.ade_data, x.pse_vlr AS ade_inc_margem, ");
        query.append("tmp.autoriza_pgt_parcial, tmp.svc_prioridade, tmp.cnv_prioridade, tmp.ade_ano_mes_ini_ref, tmp.ade_ano_mes_ini, ade.ade_data_ref, tmp.ade_numero ");
        query.append("FROM tb_tmp_exportacao tmp ");
        query.append("INNER JOIN tb_relacionamento_autorizacao rad ON (rad.ade_codigo_destino = tmp.ade_codigo AND rad.tnt_codigo='").append(CodedValues.TNT_CARTAO).append("') ");
        query.append("INNER JOIN tb_aut_desconto ade ON (ade.ade_codigo = rad.ade_codigo_origem) ");
        query.append("INNER JOIN tb_registro_servidor rse ON (tmp.rse_codigo = rse.rse_codigo) ");
        query.append("INNER JOIN ( ");
        query.append("SELECT DISTINCT tps.pse_vlr, rel.svc_codigo_destino AS svc_codigo ");
        query.append("FROM tb_relacionamento_servico rel ");
        query.append("INNER JOIN tb_param_svc_consignante tps ON (tps.tps_codigo='").append(CodedValues.TPS_INCIDE_MARGEM).append("' AND rel.svc_codigo_origem = tps.svc_codigo) ");
        query.append("WHERE tnt_codigo='").append(CodedValues.TNT_CARTAO).append("' ");
        query.append(") AS x ON (x.svc_codigo = tmp.svc_codigo) ");
        query.append("INNER JOIN tb_periodo_exportacao pex ON (pex.org_codigo = rse.org_codigo) ");
        query.append("WHERE tmp.ade_inc_margem = 0 ");
        query.append("AND (x.pse_vlr = 3 AND rse.rse_margem_rest_3 + COALESCE((").append(queryMargemUsadaPosCorte(CodedValues.INCIDE_MARGEM_SIM_3, queryParams)).append("), 0.00) < 0.00 ");
        query.append(") ");
        query.append("ORDER BY rse_codigo, COALESCE(svc_prioridade, 9999999) + 0 DESC, COALESCE(cnv_prioridade, 9999999) + 0 DESC, ade_data DESC, ade_numero DESC ");
        LOG.debug(query.toString());

        List<TransferObject> contratos = new ArrayList<>();
        try {
            final String fieldsNames = "rse_codigo,ade_codigo,ade_data,ade_vlr,ade_inc_margem,autoriza_pgt_parcial";
            contratos = MySqlGenericDAO.getFieldsValuesList(queryParams, query.toString(), fieldsNames, MySqlDAOFactory.SEPARADOR);
        } catch (final DAOException ex) {
            LOG.error(ex.getMessage());
            throw new ExportaMovimentoException("mensagem.erroInternoSistema", (AcessoSistema) null, ex);
        }
        return contratos;
    }

    /**
     * Navega na lista de contratos passada e verifica se este pode
     * ser exportado de acordo com a margem restante do servidor
     * @param contratos
     * @param permiteDescontoParcial
     * @param verificaParamCsaPgParcial - determina se verifica o parâmetro de CSA que permite ou não pagamento parcial
     * @return
     * @throws ExportaMovimentoException
     */
    @Override
    public ContratosSemMargem obterContratosSemMargemMovimentoMensalv2(List<TransferObject> contratos, boolean permiteDescontoParcial, boolean verificaParamCsaPgParcial) throws ExportaMovimentoException {
        try {
            // Cria um responsável do usuário do sistema e define a função FUN_EXP_MOV_FINANCEIRO para
            // consultar as margens independente de convênio ou serviço ativo
            final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
            responsavel.setFunCodigo(CodedValues.FUN_EXP_MOV_FINANCEIRO);

            final PeriodoDelegate perDelegate = new PeriodoDelegate();
            final TransferObject periodoExportacao = perDelegate.obtemPeriodoExportacaoDistinto(null, null, responsavel);
            final java.util.Date periodoDate = (java.util.Date) periodoExportacao.getAttribute(Columns.PEX_PERIODO);
            final java.sql.Date periodoAtual = new java.sql.Date(periodoDate.getTime());

            final ContratosSemMargem adeImpropria = new ContratosSemMargem();
            final ConsultarMargemController consultarMargemController = ApplicationContextProvider.getApplicationContext().getBean(ConsultarMargemController.class);
            final ConvenioController convenioController = ApplicationContextProvider.getApplicationContext().getBean(ConvenioController.class);
            final PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);

            // Parâmetro de sistema para indicar que deve somar à margem restante os contratos que não são exportados para a folha
            final boolean considerarContratosNaoExportados = ParamSist.paramEquals(CodedValues.TPC_CONSIDERAR_CONTRATOS_NAO_EXPORTADOS, CodedValues.TPC_SIM, responsavel);

            // Precisamos identificar quais são os reais valores para validar na margem Cartão, pois precisamos considerar o valor da reserva e não do margem rest
            // pois posso ter reserva maior que a margem atual, negativando os contratos e os mesmos precisam ser enviados no movimento cabendo na margem correta de cartão
            final List<String> cnvCodigosReserva = new ArrayList<>();
            final List<String> cnvCodigosLancamentos = new ArrayList<>();
            final List<TransferObject> lstCnvCodigosCartaoReserva3 = convenioController.ListaConveniosIncMargemCartaoReservaLancamento(CodedValues.INCIDE_MARGEM_SIM_3, true, responsavel);
            final List<TransferObject> lstCnvCodigosCartaoLancamento = convenioController.ListaConveniosIncMargemCartaoReservaLancamento(CodedValues.INCIDE_MARGEM_SIM_3, false, responsavel);

            for (final TransferObject cnv : lstCnvCodigosCartaoReserva3) {
                cnvCodigosReserva.add((String) cnv.getAttribute(Columns.CNV_CODIGO));
            }

            for (final TransferObject cnv : lstCnvCodigosCartaoLancamento) {
                cnvCodigosLancamentos.add((String) cnv.getAttribute(Columns.CNV_CODIGO));
            }

            if ((contratos != null) && !contratos.isEmpty()) {
                String rseCodigoAnterior = null;

                String rseCodigo = null;
                Short adeIncMargem = null;
                String adeCodigo = null;
                BigDecimal adeVlr;

                final Map<Short, BigDecimal> margemFolha = new HashMap<>();
                final Map<Short, BigDecimal> margemRestante = new HashMap<>();
                final Map<Short, BigDecimal> margemUsada = new HashMap<>();


                for (final TransferObject contrato : contratos) {
                    rseCodigo = contrato.getAttribute("rse_codigo").toString();
                    adeIncMargem = Short.valueOf(contrato.getAttribute("ade_inc_margem").toString());

                    if (!rseCodigo.equals(rseCodigoAnterior)) {
                        rseCodigoAnterior = rseCodigo;
                        margemFolha.clear();
                        margemRestante.clear();
                        margemUsada.clear();

                        final List<MargemTO> margens = consultarMargemController.consultarMargem(rseCodigo, null, null, null, null, true, false, true, null, responsavel);
                        for (final MargemTO margemTO : margens) {
                            final Short marCodigo = margemTO.getMarCodigo();
                            if (CodedValues.INCIDE_MARGEM_SIM_3.equals(marCodigo)) {

                                final BigDecimal totalReservasCartao = pesquisarConsignacaoController.ObtemTotalValorConsignacaoPorRseCnv(rseCodigo, cnvCodigosReserva, null, responsavel);
                                final BigDecimal totalLancamentosCartao = pesquisarConsignacaoController.ObtemTotalValorConsignacaoPorRseCnv(rseCodigo, cnvCodigosLancamentos, periodoAtual, responsavel);

                                if (!TextHelper.isNull(totalReservasCartao) && (totalReservasCartao.compareTo(BigDecimal.ZERO) > 0)
                                && !TextHelper.isNull(totalLancamentosCartao) && (totalLancamentosCartao.compareTo(BigDecimal.ZERO) > 0)) {
                                    margemFolha.put(marCodigo, totalReservasCartao.add(margemTO.getMrsMargemRest()));
                                    margemUsada.put(marCodigo, totalLancamentosCartao);
                                    margemRestante.put(marCodigo, totalReservasCartao.add(margemTO.getMrsMargemRest()).subtract(totalLancamentosCartao));
                                } else {
                                    margemFolha.put(marCodigo, margemTO.getMrsMargem());
                                    margemRestante.put(marCodigo, margemTO.getMrsMargemRest());
                                    margemUsada.put(marCodigo, margemTO.getMrsMargemUsada());
                                }
                            } else {
                                margemFolha.put(marCodigo, margemTO.getMrsMargem());
                                margemRestante.put(marCodigo, margemTO.getMrsMargemRest());
                                margemUsada.put(marCodigo, margemTO.getMrsMargemUsada());
                            }
                            final BigDecimal margemUsadaAposCorte = calcularMargemUsadaPosCorte(rseCodigo, marCodigo, responsavel);
                            margemRestante.put(marCodigo, margemRestante.get(marCodigo).add(margemUsadaAposCorte));
                            margemUsada.put(marCodigo, margemUsada.get(marCodigo).subtract(margemUsadaAposCorte));
                        }
                        if (considerarContratosNaoExportados) {
                            // Buscar margem usada por contratos que não são exportados.
                            adicionarMargemUsadaNaoExportavel(rseCodigo, margens, margemFolha, margemRestante, margemUsada, responsavel);
                        }
                    }

                    adeCodigo = contrato.getAttribute("ade_codigo").toString();
                    adeVlr = new BigDecimal(contrato.getAttribute("ade_vlr").toString());

                    if (margemRestante.get(adeIncMargem).signum() < 0) {
                        margemRestante.put(adeIncMargem, margemRestante.get(adeIncMargem).add(adeVlr));
                        margemUsada.put(adeIncMargem, margemUsada.get(adeIncMargem).subtract(adeVlr));

                        // Passa o valor negativo, pois está sendo somado à margem
                        atualizaMargemExtraCasada(adeIncMargem, adeVlr.negate(), margemFolha, margemRestante, margemUsada);

                        if ((!permiteDescontoParcial || (margemRestante.get(adeIncMargem).signum() <= 0)) || !verificaParamCsaPgParcial) {
                            // Se não permite desconto parcial, ou permite, porém a margem ainda continua negativa
                            // ou zerada inclui o contrato na lista daqueles que não há margem para envio integral
                            adeImpropria.addContratoSemMargem(adeCodigo);
                        } else if (!TextHelper.isNull(contrato.getAttribute("autoriza_pgt_parcial")) && CodedValues.TPA_SIM.equals(contrato.getAttribute("autoriza_pgt_parcial"))) {
						    // Se permite desconto parcial e a margem já ficou positiva, adiciona o contrato
						    // na lista daqueles que podem pagar parcialmente com o valor restante de margem
						    adeImpropria.addContratoParcialmenteSemMargem(adeCodigo, margemRestante.get(adeIncMargem));
						} else {
						    // Se a CSA da ade corrente não permitir pagamento parcial, inclui o contrato na
						    // lista dos contratos a serem removidos do movimento
						    adeImpropria.addContratoSemMargem(adeCodigo);
						}
                    }
                }
            }
            return adeImpropria;
        } catch (ServidorControllerException | AutorizacaoControllerException | SQLException | PeriodoException | ConvenioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException(ex);
        }
    }

    @Override
    protected void adicionarMargemUsadaNaoExportavel(String rseCodigo, List<MargemTO> margens,
            Map<Short, BigDecimal> margemFolha, Map<Short, BigDecimal> margemRestante, Map<Short, BigDecimal> margemUsada,
            AcessoSistema responsavel) throws AutorizacaoControllerException, SQLException {
    	final boolean margem1CasadaMargem3 = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3, CodedValues.TPC_SIM, responsavel);
    	final boolean margem123Casadas = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_2_3_CASADAS, CodedValues.TPC_SIM, responsavel);
    	final boolean margem1CasadaMargem3Esq = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_ESQUERDA, CodedValues.TPC_SIM, responsavel);
    	final boolean margem123CasadasEsq = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_2_3_CASADAS_PELA_ESQUERDA, CodedValues.TPC_SIM, responsavel);
    	final boolean margem1CasadaMargem3Lateral = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_LATERAL, CodedValues.TPC_SIM, responsavel);

    	for (final MargemTO margemTO : margens) {
    		final Short marCodigo = margemTO.getMarCodigo();
    		if (CodedValues.INCIDE_MARGEM_SIM_3.equals(marCodigo)) {
    			continue;
    		}
    		// Buscar margem usada por contratos que não são exportados.
    		final BigDecimal margemUsadaApenasEConsig = calcularMargemUsadaNaoExportavel(rseCodigo, marCodigo, responsavel);
    		if (margemUsadaApenasEConsig.compareTo(BigDecimal.valueOf(0)) != 0) {
    			margemRestante.put(marCodigo, margemRestante.get(marCodigo).add(margemUsadaApenasEConsig));
    			margemUsada.put(marCodigo, margemUsada.get(marCodigo).subtract(margemUsadaApenasEConsig));

    			if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM)) {
    				if (margem1CasadaMargem3Esq || margem123CasadasEsq) {
    					margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_3, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM_3).add(margemUsadaApenasEConsig));
    					margemUsada.put(CodedValues.INCIDE_MARGEM_SIM_3, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_3).subtract(margemUsadaApenasEConsig));
    					if (margem123CasadasEsq) {
    						margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_2, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM_2).add(margemUsadaApenasEConsig));
    						margemUsada.put(CodedValues.INCIDE_MARGEM_SIM_2, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_2).subtract(margemUsadaApenasEConsig));
    					}
    				}

    			} else if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
    				if (margem123Casadas || margem123CasadasEsq) {
    					margemRestante.put(CodedValues.INCIDE_MARGEM_SIM, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM).add(margemUsadaApenasEConsig));
    					margemUsada.put(CodedValues.INCIDE_MARGEM_SIM, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM).subtract(margemUsadaApenasEConsig));
    					if (margem123CasadasEsq) {
    						margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_3, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM_3).add(margemUsadaApenasEConsig));
    						margemUsada.put(CodedValues.INCIDE_MARGEM_SIM_3, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_3).subtract(margemUsadaApenasEConsig));
    					}
    				}

    			} else if (marCodigo.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
    				// Contrato originalmente incidindo na margem 3, ao ser retirado, com casamento de margem
    				// deve ser adicionado às margens 1 e 2, de acordo com o tipo de casamento.
    				if (margem1CasadaMargem3 || margem1CasadaMargem3Esq || margem123Casadas || margem123CasadasEsq) {
    					margemRestante.put(CodedValues.INCIDE_MARGEM_SIM, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM).add(margemUsadaApenasEConsig));
    					margemUsada.put(CodedValues.INCIDE_MARGEM_SIM, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM).subtract(margemUsadaApenasEConsig));
    					if (margem123Casadas || margem123CasadasEsq) {
    						margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_2, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM_2).add(margemUsadaApenasEConsig));
    						margemUsada.put(CodedValues.INCIDE_MARGEM_SIM_2, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_2).subtract(margemUsadaApenasEConsig));
    					}
    				}
    			} else if (!marCodigo.equals(CodedValues.INCIDE_MARGEM_NAO)) {
    				// Passa o valor negativo, pois está sendo somado à margem
    				atualizaMargemExtraCasada(marCodigo, margemUsadaApenasEConsig.negate(), margemFolha, margemRestante, margemUsada);
    			}

    			if (margem1CasadaMargem3 || margem1CasadaMargem3Esq) {
    				margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_3, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM).min(margemFolha.get(CodedValues.INCIDE_MARGEM_SIM_3).subtract(margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_3))));
    			} else if (margem123Casadas || margem123CasadasEsq) {
    				// Realiza o acerto da margem, de acordo com o real restante e o limite das superiores
    				margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_2, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM).min(margemFolha.get(CodedValues.INCIDE_MARGEM_SIM_2).subtract(margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_2))));
    				margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_3, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM_2).min(margemFolha.get(CodedValues.INCIDE_MARGEM_SIM_3).subtract(margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_3))));
    			} else if (margem1CasadaMargem3Lateral) {
    				margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_3, margemFolha.get(CodedValues.INCIDE_MARGEM_SIM_3).subtract(margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_3)).add(margemRestante.get(CodedValues.INCIDE_MARGEM_SIM).min(BigDecimal.ZERO)));
    			}
    		}
    	}
    }
}