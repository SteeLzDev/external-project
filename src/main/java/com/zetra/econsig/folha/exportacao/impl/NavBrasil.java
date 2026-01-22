package com.zetra.econsig.folha.exportacao.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.ExportaMovimentoException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.folha.exportacao.ExportaMovimentoBase;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.MySqlGenericDAO;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

public class NavBrasil extends ExportaMovimentoBase {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(NavBrasil.class);

    private static final BigDecimal valorInferior = new BigDecimal("10.00");

    @Override
    public void processaTabelaExportacao(ParametrosExportacao parametrosExportacao, AcessoSistema responsavel) throws ExportaMovimentoException {
        // Remove da tabela de exportação as ADE que não cabem na margem
        LOG.debug("NavBrasil.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
        removerContratosSemMargemMovimentoMensalv2(true, null, true);
        LOG.debug("fim - NavBrasil.removerContratosSemMargemMovimentoMensal: " + DateHelper.getSystemDatetime());
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
        query.append("coalesce(tmp.ade_ano_mes_ini_ref, tmp.ade_ano_mes_ini) DESC, coalesce(tmp.ade_data_ref, tmp.ade_data) DESC, tmp.ade_vlr DESC, tmp.ade_numero DESC");
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
            // Cria um responsável do usuário do sistema e define a função FUN_EXP_MOV_FINANCEIRO para
            // consultar as margens independente de convênio ou serviço ativo
            final AcessoSistema responsavel = AcessoSistema.getAcessoUsuarioSistema();
            responsavel.setFunCodigo(CodedValues.FUN_EXP_MOV_FINANCEIRO);

            final boolean margem1CasadaMargem3 = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3, CodedValues.TPC_SIM, responsavel);
            final boolean margem123Casadas = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_2_3_CASADAS, CodedValues.TPC_SIM, responsavel);
            final boolean margem1CasadaMargem3Esq = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_ESQUERDA, CodedValues.TPC_SIM, responsavel);
            final boolean margem123CasadasEsq = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_2_3_CASADAS_PELA_ESQUERDA, CodedValues.TPC_SIM, responsavel);
            final boolean margem1CasadaMargem3Lateral = ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3_LATERAL, CodedValues.TPC_SIM, responsavel);

            final ContratosSemMargem adeImpropria = new ContratosSemMargem();
            final ConsultarMargemController consultarMargemController = ApplicationContextProvider.getApplicationContext().getBean(ConsultarMargemController.class);

            if ((contratos != null) && !contratos.isEmpty()) {
                String rseCodigoAnterior = null;

                String rseCodigo = null;
                Short adeIncMargem = null;
                String adeCodigo = null;
                BigDecimal adeVlr;

                final Map<Short, BigDecimal> margemFolha = new HashMap<>();
                final Map<Short, BigDecimal> margemRestante = new HashMap<>();
                final Map<Short, BigDecimal> margemUsada = new HashMap<>();

                // Parâmetro de sistema para indicar que deve somar à margem restante os contratos que não são exportados para a folha
                final boolean considerarContratosNaoExportados = ParamSist.paramEquals(CodedValues.TPC_CONSIDERAR_CONTRATOS_NAO_EXPORTADOS, CodedValues.TPC_SIM, responsavel);

                for (final TransferObject contrato : contratos) {
                    rseCodigo = contrato.getAttribute("rse_codigo").toString();
                    adeIncMargem = Short.valueOf(contrato.getAttribute("ade_inc_margem").toString());

                    // Se trocou de servidor ou é a primeira vez
                    if (!rseCodigo.equals(rseCodigoAnterior)) {
                        rseCodigoAnterior = rseCodigo;
                        margemFolha.clear();
                        margemRestante.clear();
                        margemUsada.clear();

                        final List<MargemTO> margens = consultarMargemController.consultarMargem(rseCodigo, null, null, null, null, true, false, true, null, responsavel);
                        for (final MargemTO margemTO : margens) {
                            final Short marCodigo = margemTO.getMarCodigo();
                            margemFolha.put(marCodigo, margemTO.getMrsMargem());
                            margemRestante.put(marCodigo, margemTO.getMrsMargemRest());
                            margemUsada.put(marCodigo, margemTO.getMrsMargemUsada());
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

                    // Soma o ade_vlr na margem rest até que este fique positivo
                    // assim o tratamento realizado não precisa ser diferente caso o
                    // sistema trabalhe com margem cheia ou líquida
                    if (margemRestante.get(adeIncMargem).signum() < 0) {
                        margemRestante.put(adeIncMargem, margemRestante.get(adeIncMargem).add(adeVlr));
                        margemUsada.put(adeIncMargem, margemUsada.get(adeIncMargem).subtract(adeVlr));

                        if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                            if (margem1CasadaMargem3Esq || margem123CasadasEsq) {
                                margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_3, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM_3).add(adeVlr));
                                margemUsada.put(CodedValues.INCIDE_MARGEM_SIM_3, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_3).subtract(adeVlr));
                                if (margem123CasadasEsq) {
                                    margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_2, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM_2).add(adeVlr));
                                    margemUsada.put(CodedValues.INCIDE_MARGEM_SIM_2, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_2).subtract(adeVlr));
                                }
                            }

                        } else if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                            if (margem123Casadas || margem123CasadasEsq) {
                                margemRestante.put(CodedValues.INCIDE_MARGEM_SIM, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM).add(adeVlr));
                                margemUsada.put(CodedValues.INCIDE_MARGEM_SIM, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM).subtract(adeVlr));
                                if (margem123CasadasEsq) {
                                    margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_3, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM_3).add(adeVlr));
                                    margemUsada.put(CodedValues.INCIDE_MARGEM_SIM_3, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_3).subtract(adeVlr));
                                }
                            }

                        } else if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                            // Contrato originalmente incidindo na margem 3, ao ser retirado, com casamento de margem
                            // deve ser adicionado às margens 1 e 2, de acordo com o tipo de casamento.
                            if (margem1CasadaMargem3 || margem1CasadaMargem3Esq || margem123Casadas || margem123CasadasEsq) {
                                margemRestante.put(CodedValues.INCIDE_MARGEM_SIM, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM).add(adeVlr));
                                margemUsada.put(CodedValues.INCIDE_MARGEM_SIM, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM).subtract(adeVlr));
                                if (margem123Casadas || margem123CasadasEsq) {
                                    margemRestante.put(CodedValues.INCIDE_MARGEM_SIM_2, margemRestante.get(CodedValues.INCIDE_MARGEM_SIM_2).add(adeVlr));
                                    margemUsada.put(CodedValues.INCIDE_MARGEM_SIM_2, margemUsada.get(CodedValues.INCIDE_MARGEM_SIM_2).subtract(adeVlr));
                                }
                            }
                        } else if (!adeIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) {
                            // Passa o valor negativo, pois está sendo somado à margem
                            atualizaMargemExtraCasada(adeIncMargem, adeVlr.negate(), margemFolha, margemRestante, margemUsada);
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

                        if ((!permiteDescontoParcial || (margemRestante.get(adeIncMargem).signum() <= 0)) || !verificaParamCsaPgParcial) {
                            // Se não permite desconto parcial, ou permite, porém a margem ainda continua negativa
                            // ou zerada inclui o contrato na lista daqueles que não há margem para envio integral
                            adeImpropria.addContratoSemMargem(adeCodigo);
                        } else {
                            if (!TextHelper.isNull(contrato.getAttribute("autoriza_pgt_parcial")) && CodedValues.TPA_SIM.equals(contrato.getAttribute("autoriza_pgt_parcial"))) {
                                // Se permite desconto parcial e a margem já ficou positiva, adiciona o contrato
                                // na lista daqueles que podem pagar parcialmente com o valor restante de margem
                                if (valorInferior.compareTo(margemRestante.get(adeIncMargem)) <= 0) {
                                    adeImpropria.addContratoParcialmenteSemMargem(adeCodigo, margemRestante.get(adeIncMargem));
                                } else {
                                    adeImpropria.addContratoSemMargem(adeCodigo);
                                }
                            } else {
                                // Se a CSA da ade corrente não permitir pagamento parcial, inclui o contrato na
                                // lista dos contratos a serem removidos do movimento
                                adeImpropria.addContratoSemMargem(adeCodigo);
                            }
                        }
                    }
                }
            }
            return adeImpropria;
        } catch (ServidorControllerException | AutorizacaoControllerException | SQLException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaMovimentoException(ex);
        }
    }
}
