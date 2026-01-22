package com.zetra.econsig.helper.financeiro;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.consignacao.SimulacaoHelper;
import com.zetra.econsig.helper.margem.MargemDisponivel;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: SimulacaoMetodoBrasileiro</p>
 * <p>Description: Classe auxiliar com metodologia brasileira dos cálculos financeiros para simulação.</p>
 * <p>Copyright: Copyright (c) 2015</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: geovani.morais $
 * $Revision: 19169 $
 * $Date: 2015-05-07 12:44:12 -0300 (Qui, 07 Mai 2015) $
 */
@Component
public class SimulacaoMetodoBrasileiro implements SimuladorCustomizado {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SimulacaoMetodoBrasileiro.class);

    @Autowired
    private SimulacaoController simulacaoController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ParametroController parametroController;

    /**
     * Realiza a simulação de uma consignação, de acordo com as taxas cadastradas no sistema,
     * seja pelo valor da parcela ou pelo valor liberado.
     *
     * @param csaCodigo           : Código da consignatária, caso seja para simular apenas para prazos cadastrados para esta
     * @param svcCodigo           : Código do serviço da simulação
     * @param orgCodigo           : Código do órgão do servidor que está simulando
     * @param rseCodigo           : Código do registro servidor que está simulando
     * @param vlrParcela          : Valor da parcela, caso a simulação seja pelo valor da parcela
     * @param vlrLiberado         : Valor liberado, caso a simulação seja pelo valor liberado
     * @param przVlr              : Prazo escolhido, ou NULL, para realizar simulação por prazos
     * @param adeAnoMesIni        : Data inicial do contrato
     * @param validaBloqSerCnvCsa : TRUE se o bloqueio de consignatária impede que seja exibida no resultado
     * @param utilizaLimiteTaxa   : TRUE se utiliza a taxa limite do serviço para a simulação, ao invés da cadastrada pela CSA
     * @param responsavel         : Responsável pela operação
     * @return
     * @throws SimulacaoControllerException
     */
    @Override
    public List<TransferObject> simularConsignacao(String csaCodigo, String svcCodigo, String orgCodigo, String rseCodigo, BigDecimal vlrParcela, BigDecimal vlrLiberado, short przVlr, Date adeAnoMesIni, boolean validaBloqSerCnvCsa, boolean utilizaLimiteTaxa, String adePeridiocidade, AcessoSistema responsavel) throws SimulacaoControllerException {
        svcCodigo = simulacaoController.getSvcTaxaCompartilhada(svcCodigo, false, responsavel);

        final boolean simulacaoPorTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_POR_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);
        final boolean paramTipoSimuPorOperacao = ParamSist.paramEquals(CodedValues.TPC_SIMULADOR_COM_CET_TIPO_OPERACAO, CodedValues.TPC_SIM, responsavel) && (rseCodigo != null);

        // Pega os coeficientes/taxas/CETs para a simulação
        final List<TransferObject> coeficientes = simulacaoController.getCoeficienteSimulacao(csaCodigo, svcCodigo, orgCodigo, rseCodigo, vlrParcela, vlrLiberado, przVlr, validaBloqSerCnvCsa, utilizaLimiteTaxa, responsavel);

        // Pega as taxas ativas
        final Map<String, BigDecimal> taxas;
        try {
            taxas = getMapTaxas(svcCodigo, null, responsavel);
        } catch (final ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException(ex);
        }

        final boolean calcTacIofCse = calculaTacIofSimulacao(svcCodigo, responsavel);
        final boolean simulaPelaParcela = (vlrParcela != null);

        // Cria cache com parâmetro de serviço por consignatária de carência
        final Map<String, Integer> paramSvcCsaCarenciaMinima = new HashMap<>();
        try {
            final List<String> tpsCodigo = new ArrayList<>();
            tpsCodigo.add(CodedValues.TPS_CARENCIA_MINIMA);
            final List<TransferObject> params = parametroController.selectParamSvcCsa(tpsCodigo, responsavel);
            for (final TransferObject param : params) {
                if (!TextHelper.isNull(param.getAttribute(Columns.PSC_VLR))) {
                    final String csaCodigoParam = param.getAttribute(Columns.PSC_CSA_CODIGO).toString();
                    final String svcCodigoParam = param.getAttribute(Columns.PSC_SVC_CODIGO).toString();
                    final String keyCsaCarencia = "csa_" + csaCodigoParam + "_" + svcCodigoParam + "_carencia";
                    final int carenciaCsa = Integer.parseInt(param.getAttribute(Columns.PSC_VLR).toString());
                    paramSvcCsaCarenciaMinima.put(keyCsaCarencia, carenciaCsa);
                }
            }
        } catch (final NumberFormatException ex) {
            throw new SimulacaoControllerException("mensagem.erro.carencia.configurada.formato.incorreto", responsavel);
        } catch (final ParametroControllerException ex) {
            throw new SimulacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        // Determina a data inicial do contrato utilizada para cálculo de correção de valor presente
        final Map<String, Object> prazoIniDC = new HashMap<>();

        // Se o simulador é agrupado pela natureza de serviço EMPRESTIMO então
        // monta um mapa para contar quantas vezes uma mesma consignatária aparece
        // no ranking para determinar se será exibida a descrição do serviço
        final boolean simuladorAgrupadoPorNaturezaServico = ParamSist.paramEquals(CodedValues.TPC_SIMULADOR_AGRUPADO_NATUREZA_SERVICO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())
                && (rseCodigo != null)
                && (csaCodigo == null);

        final Map<String, Integer> qtdServicoConsignataria = new HashMap<>();
        if (simuladorAgrupadoPorNaturezaServico) {
            for (final TransferObject coeficiente : coeficientes) {
                final String csaCodigoCft = (!TextHelper.isNull(csaCodigo)) ? csaCodigo : coeficiente.getAttribute(Columns.CSA_CODIGO).toString();
                Integer total = qtdServicoConsignataria.get(csaCodigoCft);
                if (total == null) {
                    total = 1;
                } else {
                    total = total + 1;
                }
                qtdServicoConsignataria.put(csaCodigoCft, total);
            }
        }

        final HashMap<String, TransferObject> csaRenegociacao = new HashMap<>();
        final HashMap<String, TransferObject> csaPortabilidade = new HashMap<>();
        if (paramTipoSimuPorOperacao) {
            try {
                List<TransferObject> csaList = consignatariaController.lstConsignatariasComAdeRenegociaveis(rseCodigo, svcCodigo, orgCodigo, responsavel);
                if ((csaList != null) && !csaList.isEmpty()) {
                    for (final TransferObject csa : csaList) {
                        csaRenegociacao.put(csa.getAttribute(Columns.CSA_CODIGO).toString(), csa);
                    }
                }

                csaList = consignatariaController.lstConsignatariaSerTemAde(null, rseCodigo, true, responsavel);
                if ((csaList != null) && !csaList.isEmpty()) {
                    for (final TransferObject csa : csaList) {
                        csaPortabilidade.put(csa.getAttribute(Columns.CSA_CODIGO).toString(), csa);
                    }
                }
            } catch (final ConsignatariaControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        // Parâmetros de serviço
        ParamSvcTO paramSvcCse = null; 
        boolean vlrParcelaForaMargem = false;
        BigDecimal rseMargemRest = null;   
        
        boolean exibeCETMinMax = ParamSist.getBoolParamSist(CodedValues.TPC_CSA_PODE_CADASTRAR_CET_MININO_E_MAXIMO, responsavel);
        
        // Realiza a simulação
        for (final TransferObject coeficiente : coeficientes) {
            final String csaCodigoCft = (!TextHelper.isNull(csaCodigo)) ? csaCodigo : coeficiente.getAttribute(Columns.CSA_CODIGO).toString();
            final String svcCodigoCsa = (!TextHelper.isNull(coeficiente.getAttribute(Columns.SVC_CODIGO))) ? coeficiente.getAttribute(Columns.SVC_CODIGO).toString() : svcCodigo;

            final BigDecimal cftVlr = new BigDecimal(coeficiente.getAttribute(Columns.CFT_VLR).toString());
            final BigDecimal cftVlrMinimo = !TextHelper.isNull(coeficiente.getAttribute(Columns.CFT_VLR_MINIMO)) ? new BigDecimal(coeficiente.getAttribute(Columns.CFT_VLR_MINIMO).toString()) : null;
            final HashMap<String, List<BigDecimal>> cftVlrsPerFunction = coeficiente.getAttribute(Columns.DTJ_TAXA_JUROS) != null ? (HashMap<String, List<BigDecimal>>) coeficiente.getAttribute(Columns.DTJ_TAXA_JUROS) : null;
    
            try {
	            paramSvcCse = parametroController.getParamSvcCseTO(svcCodigoCsa, responsavel);
	        } catch (ParametroControllerException ex) {
	            LOG.error(ex.getMessage(), ex);
	            throw new SimulacaoControllerException(ex);
	        }
            if(exibeCETMinMax) {    	            	
    	        // Verifica se pode mostrar margem
    	        boolean serBloqueadoSimulaSemConcluir = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_SER_BLOQUEADO_SIMULAR_SEM_CONCLUIR, responsavel)) || ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_SER_BLOQUEADO_SIMULAR_SEM_CONCLUIR, responsavel).toString().equals(CodedValues.TPC_SIM);
    	        MargemDisponivel margemDisponivel = null;
    	        try {
    	            Short incMargem = paramSvcCse.getTpsIncideMargem(); // Incide na margem 1, 2 ou 3
    	            margemDisponivel = new MargemDisponivel(rseCodigo, null, svcCodigoCsa, incMargem, !serBloqueadoSimulaSemConcluir, responsavel);
    	        } catch (ViewHelperException ex) {
    	            LOG.error(ex.getMessage(), ex);
    	            throw new SimulacaoControllerException(ex);
    	        }
    	        rseMargemRest = margemDisponivel.getMargemRestante();
            }        
            
            boolean temRene = false;
            boolean permitePortabilidade = false;
            if (paramTipoSimuPorOperacao) {
                temRene = csaRenegociacao.containsKey(csaCodigoCft);

                if ((csaPortabilidade.size() > 1) || ((csaPortabilidade.size() == 1) && (csaPortabilidade.get(csaCodigoCft) == null))) {
                    permitePortabilidade = true;
                }
            }

            coeficiente.setAttribute("RENEGOCIACAO", temRene);
            coeficiente.setAttribute("PERMITE_PORTABILIDADE", permitePortabilidade);

            final BigDecimal taxa = taxas.get(csaCodigoCft) != null ? (BigDecimal) taxas.get(csaCodigoCft) : new BigDecimal("0");

            String csaNome = (String) coeficiente.getAttribute(Columns.CSA_NOME_ABREV);
            if ((csaNome == null) || csaNome.isBlank()) {
                csaNome = (String) coeficiente.getAttribute(Columns.CSA_NOME);
            }

            String titulo = csaNome;
            if (!TextHelper.isNull(titulo) && (qtdServicoConsignataria.get(csaCodigoCft) != null)
                    && (qtdServicoConsignataria.get(csaCodigoCft).intValue() > 1)) {
                titulo += " - " + coeficiente.getAttribute(Columns.SVC_DESCRICAO);
            }

            final String keyCseDC = "cse_" + svcCodigoCsa + "_dc";
            final String keyCsePrazoIni = "cse_" + svcCodigoCsa + "_prazoIni";
            final String keyCseCarencia = "cse_" + svcCodigoCsa + "_carencia";
            final String keyCseCarenciaMaxima = "cse_" + svcCodigoCsa + "_carenciaMaxima";

            if (prazoIniDC.get(keyCseDC) == null) {
                // Se tem carência no serviço, ajusta o prazoIni
                try {

                    if (TextHelper.isNum(paramSvcCse.getTpsCarenciaMinima())) {
                        prazoIniDC.put(keyCseCarencia, Integer.parseInt(paramSvcCse.getTpsCarenciaMinima()));
                    } else {
                        prazoIniDC.put(keyCseCarencia, Integer.valueOf(0));
                    }

                    final String csaCarencia = responsavel.isCsa() ? responsavel.getCodigoEntidade() : responsavel.isCor() ? responsavel.getCodigoEntidadePai() : csaCodigoCft;
                    final int adeCarencia = parametroController.calcularAdeCarenciaDiaCorteCsa((Integer) prazoIniDC.get(keyCseCarencia), csaCarencia, orgCodigo, responsavel);

                    if (TextHelper.isNum(paramSvcCse.getTpsCarenciaMaxima())) {
                        prazoIniDC.put(keyCseCarenciaMaxima, Integer.parseInt(paramSvcCse.getTpsCarenciaMaxima()));
                    } else {
                        prazoIniDC.put(keyCseCarenciaMaxima, Integer.MAX_VALUE);
                    }

                    Date prazoIniGeral = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, adeCarencia, null, responsavel);

                    // Se a reserva possui carência maior do que o mínima do CSE, ajusta o prazoIni.
                    if ((adeAnoMesIni != null) && (adeAnoMesIni.compareTo(prazoIniGeral) > 0)) {
                        prazoIniGeral = adeAnoMesIni;
                    }

                    prazoIniDC.put(keyCseDC, SimulacaoHelper.calculateDC(DateHelper.getSystemDatetime(), prazoIniGeral, orgCodigo, responsavel));
                    prazoIniDC.put(keyCsePrazoIni, prazoIniGeral);
                } catch (final NumberFormatException ex) {
                    throw new SimulacaoControllerException("mensagem.erro.carencia.configurada.formato.incorreto", responsavel);
                } catch (ParametroControllerException | PeriodoException ex) {
                    throw new SimulacaoControllerException(ex);
                }
            }

            final int carencia = (Integer) prazoIniDC.get(keyCseCarencia);
            final int carenciaMaxima = (Integer) prazoIniDC.get(keyCseCarenciaMaxima);
            int dc = (Integer) prazoIniDC.get(keyCseDC);
            Date prazoIni = (Date) prazoIniDC.get(keyCsePrazoIni);
            try {
                final String keyCsaDC = "csa_" + csaCodigoCft + "_" + svcCodigoCsa + "_dc";
                final String keyCsaPrazoIni = "csa_" + csaCodigoCft + "_" + svcCodigoCsa + "_prazoIni";
                final String keyCsaCarencia = "csa_" + csaCodigoCft + "_" + svcCodigoCsa + "_carencia";
                if (prazoIniDC.get(keyCsaDC) != null) {
                    dc = (Integer) prazoIniDC.get(keyCsaDC);
                    prazoIni = (Date) prazoIniDC.get(keyCsaPrazoIni);
                } else {
                    // Pega a carência mínima da CSA e do serviço em questão
                    Integer carenciaCsa = paramSvcCsaCarenciaMinima.get(keyCsaCarencia);
                    if ((carenciaCsa != null) && (carenciaCsa > carencia) && (carenciaCsa <= carenciaMaxima)) {
                        carenciaCsa = parametroController.calcularAdeCarenciaDiaCorteCsa(carenciaCsa, csaCodigoCft, orgCodigo, responsavel);
                        prazoIni = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, carenciaCsa, null, responsavel);
                        dc = SimulacaoHelper.calculateDC(DateHelper.getSystemDatetime(), prazoIni, orgCodigo, responsavel);
                        titulo += "*";
                    }
                    prazoIniDC.put(keyCsaDC, dc);
                    prazoIniDC.put(keyCsaPrazoIni, prazoIni);
                }
            } catch (ParametroControllerException | PeriodoException ex) {
                throw new SimulacaoControllerException(ex);
            }

            // Determina se deve ser calculado IOF para a Consignatária
            final boolean calcIof = calculaIofSimulacao(calcTacIofCse, svcCodigo, csaCodigoCft, responsavel);            
            BigDecimal vlrParcelaMinima = null;
            BigDecimal vlrLiberadoMinimo = null;

            /***********************************
             * Simulação pelo valor da parcela *
             ***********************************/
            //add calculo no minimo aqui
            if (cftVlrsPerFunction != null) {
                for (final Map.Entry<String, List<BigDecimal>> cftVlrPerFunction : cftVlrsPerFunction.entrySet()) {
                    final String function = cftVlrPerFunction.getKey().replaceAll("[^0123456789]", "");
                    final BigDecimal cftVlrFun = cftVlrPerFunction.getValue().get(0);
                    final BigDecimal cftVlrFunMinimo = cftVlrPerFunction.getValue().get(1);
                    if (simulaPelaParcela) {
                        // Faz o cálculo somente se tem coeficiente corretamente cadastrado
                        if (cftVlrFun.signum() <= 0) {
                            vlrLiberado = new BigDecimal("0");
                        } else if (!simulacaoPorTaxaJuros) {
                            // Se simula pelos coeficientes
                            // Valor Liberado = (Valor Prestação / Coeficiente) - (TAC + OP)
                            vlrLiberado = vlrParcela.divide(cftVlrFun, 2, java.math.RoundingMode.DOWN).subtract(taxa);
                            if (vlrLiberado.signum() == -1) {
                                vlrLiberado = new BigDecimal("0");
                            }
                            // Valor Liberado mínimo = (Valor Prestação / Coeficiente mínimo) - (TAC + OP)
                            vlrLiberadoMinimo = !TextHelper.isNull(cftVlrFunMinimo) ? vlrParcela.divide(cftVlrFunMinimo, 2, java.math.RoundingMode.DOWN).subtract(taxa) : null;
                            if (!TextHelper.isNull(vlrLiberadoMinimo) && vlrLiberadoMinimo.signum() == -1) {
                            	vlrLiberadoMinimo = new BigDecimal("0");
                            }
                        } else {
                            try {
                                final BigDecimal[] retorno = calcularValorLiberado(vlrLiberado, vlrParcela, cftVlrFun,
                                        (przVlr > 0) ? przVlr
                                                : (Short) coeficiente.getAttribute(Columns.PRZ_VLR),
                                        dc, prazoIni, svcCodigo, csaCodigoCft, orgCodigo, calcIof, calcTacIofCse,
                                        responsavel);
                                vlrLiberado = retorno[0];
                                final BigDecimal tac = retorno[1];
                                final BigDecimal iof = retorno[2];

                                // Seta os valores de retorno
                                coeficiente.setAttribute("IOF_" + function, iof);
                                coeficiente.setAttribute("TAC_FINANCIADA_" + function, tac);
                                
                                if(!TextHelper.isNull(cftVlrFunMinimo)) {
                                	final BigDecimal[] retornoMinimo = calcularValorLiberado(vlrLiberado, vlrParcela, cftVlrFunMinimo,
                                            (przVlr > 0) ? przVlr
                                                    : (Short) coeficiente.getAttribute(Columns.PRZ_VLR),
                                            dc, prazoIni, svcCodigo, csaCodigoCft, orgCodigo, calcIof, calcTacIofCse,
                                            responsavel);
                                	vlrLiberadoMinimo = retornoMinimo[0];
                                }
                            } catch (final ViewHelperException ex) {
                                LOG.error(ex.getMessage(), ex);
                                vlrLiberado = new BigDecimal("0");
                                throw new SimulacaoControllerException("mensagem.erro.interno.argumento", responsavel,
                                        ex.getMessage());
                            }
                        }
                        if (!TextHelper.isNull(cftVlrFunMinimo) && cftVlrFunMinimo.signum() <= 0) {
                        	vlrLiberadoMinimo = new BigDecimal("0");
                        } 

                        /*********************************
                         * Simulação pelo valor liberado *
                         *********************************/
                    } else // Faz o cálculo somente se tem coeficiente corretamente cadastrado
                    if (cftVlrFun.signum() <= 0) {
                        vlrParcela = new BigDecimal(Double.MAX_VALUE);
                    } else if (!simulacaoPorTaxaJuros) {
                        // Valor Prestação = (Valor Liberado + TAC + OP) x Coeficiente
                        vlrParcela = vlrLiberado.add(taxa).multiply(cftVlrFun);
                        // Valor Prestação mínima = (Valor Liberado + TAC + OP) x Coeficiente minimo
                        vlrParcelaMinima = !TextHelper.isNull(cftVlrFunMinimo) ? vlrLiberado.add(taxa).multiply(cftVlrFunMinimo) : null;
                    } else {
                        try {
                            final BigDecimal[] retorno = calcularValorParcela(vlrLiberado, vlrParcela, cftVlrFun,
                                    (przVlr > 0) ? przVlr
                                            : (Short) coeficiente.getAttribute(Columns.PRZ_VLR),
                                    dc, prazoIni, svcCodigo, csaCodigoCft, orgCodigo, calcIof, calcTacIofCse,
                                    adePeridiocidade, responsavel);
                            vlrParcela = retorno[0];
                            final BigDecimal tac = retorno[1];
                            final BigDecimal iof = retorno[2];

                            coeficiente.setAttribute("TAC_FINANCIADA_" + function, tac);
                            coeficiente.setAttribute("IOF_" + function, iof);
                            
                            if(!TextHelper.isNull(cftVlrFunMinimo)) {
                            	final BigDecimal[] retornoMinimo = calcularValorParcela(vlrLiberado, vlrParcela, cftVlrFunMinimo,
                                        (przVlr > 0) ? przVlr
                                                : (Short) coeficiente.getAttribute(Columns.PRZ_VLR),
                                        dc, prazoIni, svcCodigo, csaCodigoCft, orgCodigo, calcIof, calcTacIofCse,
                                        adePeridiocidade, responsavel);
                            	vlrParcelaMinima = retornoMinimo[0];
                            }
                        } catch (final ViewHelperException ex) {
                            throw new SimulacaoControllerException("mensagem.erro.interno.argumento", responsavel,
                                    ex.getMessage());
                        }
                    }
                    if (!TextHelper.isNull(cftVlrFunMinimo) && cftVlrFunMinimo.signum() <= 0) {
                    	vlrParcelaMinima = new BigDecimal(Double.MIN_VALUE);
                    }                   

                    // Limita a 2 casas decimais tanto o valor liberado quanto o valor da parcela
                    if (vlrParcela.doubleValue() != Double.MAX_VALUE) {
                        vlrParcela = vlrParcela.setScale(2, java.math.RoundingMode.HALF_UP);
                    }
                    if (vlrLiberado.doubleValue() != 0) {
                        vlrLiberado = vlrLiberado.setScale(2, java.math.RoundingMode.HALF_UP);
                    }
                    // Limita a 2 casas decimais tanto o valor liberado mínimo quanto o valor da parcela mínima
                    if (!TextHelper.isNull(vlrParcelaMinima) && vlrParcelaMinima.doubleValue() != Double.MIN_VALUE) {
                    	vlrParcelaMinima = vlrParcelaMinima.setScale(2, java.math.RoundingMode.HALF_UP);
                    }
                    if (!TextHelper.isNull(vlrLiberadoMinimo) &&  vlrLiberadoMinimo.doubleValue() != 0) {
                    	vlrLiberadoMinimo = vlrLiberadoMinimo.setScale(2, java.math.RoundingMode.HALF_UP);
                    }                    
                    coeficiente.setAttribute("CFT_VLR_FUN_" + function, cftVlrFun);                    
                    coeficiente.setAttribute("VLR_PARCELA_" + function, vlrParcela);
                    coeficiente.setAttribute("VLR_LIBERADO_" + function, vlrLiberado);
                    
                    coeficiente.setAttribute("CFT_VLR_MINIMO_FUN_" + function, cftVlrFunMinimo);
                    coeficiente.setAttribute("VLR_PARCELA_MINIMA_" + function, vlrParcelaMinima);
                    coeficiente.setAttribute("VLR_LIBERADO_MINIMO_" + function, vlrLiberadoMinimo);
                    
                    if(exibeCETMinMax && !simulaPelaParcela) {
                	   	boolean parcelaValida = !vlrParcela.equals(new BigDecimal(Double.MAX_VALUE));				    
        				if (parcelaValida) {
        				    vlrParcelaForaMargem = vlrParcela.compareTo(rseMargemRest) > 0;
        				    coeficiente.setAttribute("VLR_PARCELA_FORA_MARGEM_" + function, vlrParcelaForaMargem);
        				} else {
        				    coeficiente.setAttribute("VLR_PARCELA_FORA_MARGEM_" + function, false);
        				}       
                    } else {
                    	coeficiente.setAttribute("VLR_PARCELA_FORA_MARGEM_" + function, false);
        		    }

                    // Define parâmetros para a ordenação
                    if (!TextHelper.isNull(csaNome)) {
                        coeficiente.setAttribute("TITULO_" + function, titulo);
                        coeficiente.setAttribute("CONSIGNATARIA_" + function, csaNome.toUpperCase());
                    }
                    coeficiente.setAttribute("VLR_ORDEM_" + function, (simulaPelaParcela ? vlrLiberado : vlrParcela));
                    coeficiente.setAttribute("SIMULA_POR_PARCELA", simulaPelaParcela);
                }                
            }

            if (simulaPelaParcela) {
                // Faz o cálculo somente se tem coeficiente corretamente cadastrado
                if (cftVlr.signum() <= 0) {
                    vlrLiberado = new BigDecimal("0");
                } else if (!simulacaoPorTaxaJuros) {
                    // Se simula pelos coeficientes
                    // Valor Liberado = (Valor Prestação / Coeficiente) - (TAC + OP)
                    vlrLiberado = vlrParcela.divide(cftVlr, 2, java.math.RoundingMode.DOWN).subtract(taxa);
                    if (vlrLiberado.signum() == -1) {
                        vlrLiberado = new BigDecimal("0");
                    }
                    // Valor Liberado mínimo = (Valor Prestação / Coeficiente mínimo) - (TAC + OP)
                    vlrLiberadoMinimo = !TextHelper.isNull(cftVlrMinimo) ? vlrParcela.divide(cftVlrMinimo, 2, java.math.RoundingMode.DOWN).subtract(taxa) : null;
                    if (!TextHelper.isNull(vlrLiberadoMinimo) && vlrLiberadoMinimo.signum() == -1) {
                    	vlrLiberadoMinimo = new BigDecimal("0");
                    }
                } else {
                    try {
                        final BigDecimal[] retorno = calcularValorLiberado(vlrLiberado, vlrParcela, cftVlr,
                                (przVlr > 0) ? przVlr
                                        : (Short) coeficiente.getAttribute(Columns.PRZ_VLR),
                                dc, prazoIni, svcCodigo, csaCodigoCft, orgCodigo, calcIof, calcTacIofCse,
                                responsavel);
                        vlrLiberado = retorno[0];
                        final BigDecimal tac = retorno[1];
                        final BigDecimal iof = retorno[2];

                        // Seta os valores de retorno
                        coeficiente.setAttribute("IOF", iof);
                        coeficiente.setAttribute("TAC_FINANCIADA", tac);
                        
                        if(!TextHelper.isNull(cftVlrMinimo)) {
                        	final BigDecimal[] retornoMinimo = calcularValorLiberado(vlrLiberado, vlrParcela, cftVlrMinimo,
                                    (przVlr > 0) ? przVlr
                                            : (Short) coeficiente.getAttribute(Columns.PRZ_VLR),
                                    dc, prazoIni, svcCodigo, csaCodigoCft, orgCodigo, calcIof, calcTacIofCse,
                                    responsavel);
                        	vlrLiberadoMinimo = retornoMinimo[0];
                        }
                    } catch (final ViewHelperException ex) {
                        LOG.error(ex.getMessage(), ex);
                        vlrLiberado = new BigDecimal("0");
                        throw new SimulacaoControllerException("mensagem.erro.interno.argumento", responsavel,
                                ex.getMessage());
                    }
                }
            	if (!TextHelper.isNull(cftVlrMinimo) && cftVlrMinimo.signum() <= 0) {
                	vlrLiberadoMinimo = new BigDecimal("0");
                } 

                /*********************************
                 * Simulação pelo valor liberado *
                 *********************************/
            } else // Faz o cálculo somente se tem coeficiente corretamente cadastrado
            if (cftVlr.signum() <= 0) {
                vlrParcela = new BigDecimal(Double.MAX_VALUE);                
            } else if (!simulacaoPorTaxaJuros) {
                // Valor Prestação = (Valor Liberado + TAC + OP) x Coeficiente
                vlrParcela = vlrLiberado.add(taxa).multiply(cftVlr);                
                // Valor Prestação mínima = (Valor Liberado + TAC + OP) x Coeficiente minimo
                vlrParcelaMinima = !TextHelper.isNull(cftVlrMinimo) ? vlrLiberado.add(taxa).multiply(cftVlrMinimo) : null;
            } else {
                try {
                    final BigDecimal[] retorno = calcularValorParcela(vlrLiberado, vlrParcela, cftVlr,
                            (przVlr > 0) ? przVlr
                                    : (Short) coeficiente.getAttribute(Columns.PRZ_VLR),
                            dc, prazoIni, svcCodigo, csaCodigoCft, orgCodigo, calcIof, calcTacIofCse,
                            adePeridiocidade, responsavel);
                    vlrParcela = retorno[0];
                    final BigDecimal tac = retorno[1];
                    final BigDecimal iof = retorno[2];

                    coeficiente.setAttribute("TAC_FINANCIADA", tac);
                    coeficiente.setAttribute("IOF", iof);
                    
                    if(!TextHelper.isNull(cftVlrMinimo)) {
                    	final BigDecimal[] retornoMinimo = calcularValorParcela(vlrLiberado, vlrParcela, cftVlrMinimo,
                                (przVlr > 0) ? przVlr
                                        : (Short) coeficiente.getAttribute(Columns.PRZ_VLR),
                                dc, prazoIni, svcCodigo, csaCodigoCft, orgCodigo, calcIof, calcTacIofCse,
                                adePeridiocidade, responsavel);
                    	vlrParcelaMinima = retornoMinimo[0];
                    }
                } catch (final ViewHelperException ex) {
                    throw new SimulacaoControllerException("mensagem.erro.interno.argumento", responsavel,
                            ex.getMessage());
                }
            }            

            // Limita a 2 casas decimais tanto o valor liberado quanto o valor da parcela
            if (vlrParcela.doubleValue() != Double.MAX_VALUE) {
                vlrParcela = vlrParcela.setScale(2, java.math.RoundingMode.HALF_UP);
            }
            if (vlrLiberado.doubleValue() != 0) {
                vlrLiberado = vlrLiberado.setScale(2, java.math.RoundingMode.HALF_UP);
            }          

            coeficiente.setAttribute("VLR_PARCELA", vlrParcela);            
            coeficiente.setAttribute("VLR_LIBERADO", vlrLiberado);
            
            if(exibeCETMinMax) {
                if (!TextHelper.isNull(cftVlrMinimo) && cftVlrMinimo.signum() <= 0) {
                    vlrParcelaMinima = new BigDecimal(Double.MIN_VALUE);
                }  

            	// Limita a 2 casas decimais tanto o valor liberado mínimo quanto o valor da parcela mínima
                if (!TextHelper.isNull(vlrParcelaMinima) && vlrParcelaMinima.doubleValue() != Double.MIN_VALUE) {
                	vlrParcelaMinima = vlrParcelaMinima.setScale(2, java.math.RoundingMode.HALF_UP);
                }
                if (!TextHelper.isNull(vlrLiberadoMinimo) && vlrLiberadoMinimo.doubleValue() != Double.MIN_VALUE) {
                	vlrLiberadoMinimo = vlrLiberadoMinimo.setScale(2, java.math.RoundingMode.HALF_UP);
                }    
                coeficiente.setAttribute("VLR_PARCELA_MINIMA", vlrParcelaMinima);
                coeficiente.setAttribute("VLR_LIBERADO", !TextHelper.isNull(vlrLiberadoMinimo) ? vlrLiberadoMinimo.max(vlrLiberado) : vlrLiberado);
                coeficiente.setAttribute("VLR_LIBERADO_MINIMO", !TextHelper.isNull(vlrLiberadoMinimo) ? vlrLiberadoMinimo.min(vlrLiberado) : null);
            }
            
            if(exibeCETMinMax && !simulaPelaParcela) {
        	   	boolean parcelaValida = !vlrParcela.equals(new BigDecimal(Double.MAX_VALUE));				    
				if (parcelaValida) {
				    vlrParcelaForaMargem = vlrParcela.compareTo(rseMargemRest) > 0;
				    coeficiente.setAttribute("VLR_PARCELA_FORA_MARGEM", vlrParcelaForaMargem);
				} else {
				    coeficiente.setAttribute("VLR_PARCELA_FORA_MARGEM", false);
				}       
            } else {
            	coeficiente.setAttribute("VLR_PARCELA_FORA_MARGEM", false);
		    } 
            
            // Define parâmetros para a ordenação
            if (!TextHelper.isNull(csaNome)) {
                coeficiente.setAttribute("TITULO", titulo);
                coeficiente.setAttribute("CONSIGNATARIA", csaNome.toUpperCase());
            }
            coeficiente.setAttribute("VLR_ORDEM", (simulaPelaParcela ? vlrLiberado : vlrParcela));
            coeficiente.setAttribute("SIMULA_POR_PARCELA", simulaPelaParcela);
        }

        if (przVlr > 0) {        	
        	// Ordena as consignatárias
        	if (exibeCETMinMax) {
                // Ordena pelo CFT_VLR_MINIMO em ordem crescente
        		coeficientes.sort((o1, o2) -> {
        		    BigDecimal v1 = o1.getAttribute("CFT_VLR_MINIMO_FUN_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? (BigDecimal) o1.getAttribute("CFT_VLR_MINIMO_FUN_" + CodedValues.FUN_SIM_CONSIGNACAO) : (BigDecimal) o1.getAttribute(Columns.CFT_VLR_MINIMO);
        		    BigDecimal v2 = o2.getAttribute("CFT_VLR_MINIMO_FUN_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? (BigDecimal) o2.getAttribute("CFT_VLR_MINIMO_FUN_" + CodedValues.FUN_SIM_CONSIGNACAO) : (BigDecimal) o2.getAttribute(Columns.CFT_VLR_MINIMO);
        		    // Trata valores nulos como maiores (vão para o final)
        		    if (v1 == null) v1 = BigDecimal.valueOf(Double.MAX_VALUE);
        		    if (v2 == null) v2 = BigDecimal.valueOf(Double.MAX_VALUE);

        		    int result = v1.compareTo(v2);
        		    if (result == 0) {
                        result = (o1.getAttribute("TITULO_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? (String) o1.getAttribute("TITULO_" + CodedValues.FUN_SIM_CONSIGNACAO) : o1.getAttribute("TITULO").toString())
                                .compareTo(o2.getAttribute("TITULO_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? (String) o2.getAttribute("TITULO_" + CodedValues.FUN_SIM_CONSIGNACAO) : o2.getAttribute("TITULO").toString());
                    }
        		    return result;
        		});

            } else if (simulaPelaParcela) {
                // Se a simulação é pela parcela, então a ordenação é feita
                // de forma decrescente pelo valor liberado
                coeficientes.sort((o1, o2) -> {
                    int result = ((o2.getAttribute("VLR_ORDEM_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? (BigDecimal) o2.getAttribute("VLR_ORDEM_" + CodedValues.FUN_SIM_CONSIGNACAO) : (BigDecimal) o2.getAttribute("VLR_ORDEM"))
                            .compareTo(o1.getAttribute("VLR_ORDEM_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? (BigDecimal) o1.getAttribute("VLR_ORDEM_" + CodedValues.FUN_SIM_CONSIGNACAO) : (BigDecimal) o1.getAttribute("VLR_ORDEM")));
                    if (result == 0) {
                        result = (o1.getAttribute("TITULO_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? (String) o1.getAttribute("TITULO_" + CodedValues.FUN_SIM_CONSIGNACAO) : o1.getAttribute("TITULO").toString())
                                .compareTo(o2.getAttribute("TITULO_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? (String) o2.getAttribute("TITULO_" + CodedValues.FUN_SIM_CONSIGNACAO) : o2.getAttribute("TITULO").toString());
                    }
                    return result;
                });
            } else {
                // Se a simulação é pelo valor liberado, então a ordenação é feita
                // de forma crescente pela parcela
                coeficientes.sort((o1, o2) -> {
                    int result = (o1.getAttribute("VLR_ORDEM_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? (BigDecimal) o1.getAttribute("VLR_ORDEM_" + CodedValues.FUN_SIM_CONSIGNACAO) : (BigDecimal) o1.getAttribute("VLR_ORDEM"))
                            .compareTo((o2.getAttribute("VLR_ORDEM_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? (BigDecimal) o2.getAttribute("VLR_ORDEM_" + CodedValues.FUN_SIM_CONSIGNACAO) : (BigDecimal) o2.getAttribute("VLR_ORDEM")));
                    if (result == 0) {
                        result = (o1.getAttribute("TITULO_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? (String) o1.getAttribute("TITULO_" + CodedValues.FUN_SIM_CONSIGNACAO) : o1.getAttribute("TITULO").toString())
                                .compareTo(o2.getAttribute("TITULO_" + CodedValues.FUN_SIM_CONSIGNACAO) != null ? (String) o2.getAttribute("TITULO_" + CodedValues.FUN_SIM_CONSIGNACAO) : o2.getAttribute("TITULO").toString());
                    }
                    return result;
                });
            }
        }

        simulacaoController.setaRankingSimulacao(coeficientes, CodedValues.FUN_SIM_CONSIGNACAO, responsavel);

        return coeficientes;
    }

    /**
     * Busca o parâmetro de serviço que define se o IOF e a TAC devem ser calculadas
     * na simulação de consignação
     *
     * @param svcCodigo
     * @param responsavel
     * @return
     * @throws SimulacaoControllerException
     */
    public boolean calculaTacIofSimulacao(String svcCodigo, AcessoSistema responsavel) throws SimulacaoControllerException {
        // Verifica se o sistema está configurado para trabalhar com o CET.
        final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);
        if (temCET) {
            return false;
        }

        final ParamSvcTO paramSvcCse = ParamSvcTO.getParamSvcTO(svcCodigo, responsavel);
        final boolean calcTacIofCse = ((paramSvcCse != null) && paramSvcCse.isTpsCalcTacIofValidaTaxaJuros());
        return calcTacIofCse;
    }

    /**
     * Busca o parâmetro de consignatária / serviço que define se o IOF deve ser
     * calculado na simulação de consignação
     *
     * @param calcIofCse
     * @param svcCodigo
     * @param csaCodigo
     * @param orgCodigo
     * @param responsavel
     * @return
     * @throws SimulacaoControllerException
     */
    public boolean calculaIofSimulacao(boolean calcIofCse, String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws SimulacaoControllerException {
        try {
            // Verifica se o sistema está configurado para trabalhar com o CET.
            final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);
            if (temCET) {
                return false;
            }

            // Usa por padrão o valor da CSE.
            boolean calcIof = calcIofCse;
            // Inclusão da IOF na calculo por CSA
            final List<String> tpsCodigoIOF = new ArrayList<>();
            tpsCodigoIOF.add(CodedValues.TPS_SOMA_IOF_SIMULACAO_RESERVA);

            // Usa o parametro por CSA se ele estiver configurado.
            final List<TransferObject> params = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigoIOF, false, responsavel);
            if ((params != null) && (params.size() == 1)) {
                final TransferObject param = params.get(0);
                if ((param != null) && (param.getAttribute(Columns.PSC_VLR) != null)) {
                    calcIof = "S".equalsIgnoreCase(param.getAttribute(Columns.PSC_VLR).toString());
                }
            }

            return calcIof;
        } catch (final ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException(ex);
        }
    }

    /**
     * Executa calculo do valor liberado, TAC e IOF de acordo com os parâmetros de
     * entrada.
     *
     * @param vlrLiberado
     * @param vlrParcela
     * @param cftVlr
     * @param przVlr
     * @param dc
     * @param prazoIni
     * @param svcCodigo
     * @param csaCodigo
     * @param orgCodigo
     * @param calcIof
     * @param calcTacIofCse
     * @param responsavel
     * @return
     * @throws SimulacaoControllerException
     * @throws ViewHelperException
     */
    public BigDecimal[] calcularValorLiberado(BigDecimal vlrLiberado, BigDecimal vlrParcela, BigDecimal cftVlr,
                                                     int przVlr, int dc, Date prazoIni, String svcCodigo, String csaCodigo, String orgCodigo, boolean calcIof,
                                                     boolean calcTacIofCse, AcessoSistema responsavel) throws SimulacaoControllerException, ViewHelperException {
        BigDecimal iofAntes = new BigDecimal("0");
        BigDecimal iof = null;

        BigDecimal tacAntes = new BigDecimal("0");
        BigDecimal tac = null;

        final BigDecimal valorDescorrigido = SimulacaoHelper.calcularValorLiberado(vlrParcela, przVlr,
                DateHelper.getSystemDatetime(), prazoIni, cftVlr, orgCodigo, responsavel);

        // Valor inicial
        vlrLiberado = valorDescorrigido;

        if (calcIof && (cftVlr.signum() > 0)) {
            final CDCHelper cdcHelper = new CDCHelper(vlrLiberado.doubleValue(), przVlr, cftVlr.doubleValue() / 100.00, dc);
            cdcHelper.calculate();
            iof = new BigDecimal(cdcHelper.getIOFE());
        } else if (!calcIof && calcTacIofCse && (cftVlr.signum() > 0)) {
            // Se a consignatária é isenta de IOF, porém o serviço possui calculo de IOF,
            // verifica se a aliquota adicional de IOF existe e se deve ser utilizada
            iof = new BigDecimal(CDCHelper.calcularIofAdicional(vlrLiberado.doubleValue()));
        } else {
            iof = new BigDecimal("0");
        }

        if (calcIof) {
            for (int i = 0; i < 5; i++) {
                iofAntes = iof;
                vlrLiberado = valorDescorrigido.subtract(iof);
                final CDCHelper cdcHelper = new CDCHelper(vlrLiberado.doubleValue(), przVlr, cftVlr.doubleValue() / 100.00,
                        dc);
                cdcHelper.calculate();
                iof = new BigDecimal(cdcHelper.getIOFE());
                if (Math.abs(iofAntes.doubleValue() - iof.doubleValue()) < 0.0005) {
                    break;
                }
            }
        }

        // Subtrai o IOF do valor liberado
        BigDecimal vlrLiberadoNovo = valorDescorrigido.subtract(iof);
        for (int i = 0; i < 5; i++) {
            try {
                tac = calculaTAC(svcCodigo, csaCodigo, orgCodigo, vlrLiberadoNovo, cftVlr, responsavel);
                if (Math.abs(tacAntes.doubleValue() - tac.doubleValue()) < 0.0005) {
                    break;
                }
                vlrLiberadoNovo = valorDescorrigido.subtract(iof).subtract(tac);
                tacAntes = tac;
            } catch (final SimulacaoControllerException ex) {
                throw new SimulacaoControllerException(ex);
            }
        }

        vlrLiberado = valorDescorrigido.subtract(iof).subtract(tac);

        return new BigDecimal[]{vlrLiberado, tac, iof};
    }

    /**
     * Metodo para retornar o valor da TAC calculada
     *
     * @param svcCodigo
     * @param csaCodigo
     * @param orgCodigo
     * @param vlrLiberado
     * @param cftVlr
     * @param responsavel
     * @return BigDecimal Valor da TAC calculada
     */
    public BigDecimal calculaTAC(String svcCodigo, String csaCodigo, String orgCodigo, BigDecimal vlrLiberado,
                                        BigDecimal cftVlr, AcessoSistema responsavel) throws SimulacaoControllerException {

        // Verifica se o sistema está configurado para trabalhar com o CET.
        final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);
        if (temCET) {
            return new BigDecimal("0.00");
        }

        BigDecimal result;
        BigDecimal tac = new BigDecimal("0.00");
        BigDecimal vlrMinTac = new BigDecimal("0.00");
        BigDecimal vlrMaxTac = new BigDecimal("0.00");
        String tipoTac = "F";
        // Pega as taxas ativas
        List<TransferObject> txs = null;
        try {
            txs = getLstTaxas(svcCodigo, csaCodigo, orgCodigo, responsavel);
        } catch (final ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new SimulacaoControllerException(ex);
        }

        for (final TransferObject cto : txs) {
            if (CodedValues.TPS_TAC_FINANCIADA.equals(cto.getAttribute(Columns.TPS_CODIGO).toString())) {
                tac = !TextHelper.isNull(cto.getAttribute(Columns.PSC_VLR)) ? new BigDecimal(cto.getAttribute(Columns.PSC_VLR).toString()) : new BigDecimal("0.00");
            } else if (CodedValues.TPS_TIPO_TAC.equals(cto.getAttribute(Columns.TPS_CODIGO).toString())) {
                tipoTac = !TextHelper.isNull(cto.getAttribute(Columns.PSC_VLR))
                        ? cto.getAttribute(Columns.PSC_VLR).toString()
                        : "F";

            } else if (CodedValues.TPS_VALOR_MIN_TAC.equals(cto.getAttribute(Columns.TPS_CODIGO).toString())) {
                vlrMinTac = !TextHelper.isNull(cto.getAttribute(Columns.PSC_VLR))
                        ? new BigDecimal(cto.getAttribute(Columns.PSC_VLR).toString())
                        : new BigDecimal("0.00");

            } else if (CodedValues.TPS_VALOR_MAX_TAC.equals(cto.getAttribute(Columns.TPS_CODIGO).toString())) {
                vlrMaxTac = !TextHelper.isNull(cto.getAttribute(Columns.PSC_VLR))
                        ? new BigDecimal(cto.getAttribute(Columns.PSC_VLR).toString())
                        : new BigDecimal("0.00");
            }
        }

        final ParamSvcTO paramSvcCse = ParamSvcTO.getParamSvcTO(svcCodigo, responsavel);

        // Pega limite de valor de TAC cadastrado pelo CSE.
        BigDecimal maxTacCse = null;
        if (!TextHelper.isNull(paramSvcCse.getTpsValorMaxTac())) {
            maxTacCse = new BigDecimal(paramSvcCse.getTpsValorMaxTac());
            if ((vlrMaxTac.compareTo(maxTacCse) > 0) || "F".equals(tipoTac)) {
                vlrMaxTac = maxTacCse;
            }
        }

        if ("F".equals(tipoTac)) {
            if (cftVlr.signum() > 0) {
                if ((maxTacCse != null) && (tac.compareTo(maxTacCse) > 0)) {
                    result = maxTacCse;
                } else {
                    result = tac;
                }
            } else {
                result = new BigDecimal("0");
            }
        } else if (cftVlr.signum() > 0) {
            final BigDecimal tacCalc = vlrLiberado.multiply(tac).divide(new BigDecimal("100.00"), 2,
                    java.math.RoundingMode.HALF_UP);
            if (tacCalc.compareTo(vlrMinTac) < 0) {
                result = vlrMinTac;
            } else if (tacCalc.compareTo(vlrMaxTac) > 0) {
                result = vlrMaxTac;
            } else {
                result = tacCalc;
            }
        } else {
            result = new BigDecimal("0");
        }
        return result;
    }

    /**
     * Executa calculo do valor da parcela, TAC e IOF de acordo com os parâmetros de
     * entrada.
     *
     * @param vlrLiberado
     * @param vlrParcela
     * @param cftVlr
     * @param przVlr
     * @param dc
     * @param prazoIni
     * @param svcCodigo
     * @param csaCodigo
     * @param orgCodigo
     * @param calcIof
     * @param calcTacIofCse
     * @param responsavel
     * @return
     * @throws ViewHelperException
     */
    public BigDecimal[] calcularValorParcela(BigDecimal vlrLiberado, BigDecimal vlrParcela, BigDecimal cftVlr,
                                                    int przVlr, int dc, Date prazoIni, String svcCodigo, String csaCodigo, String orgCodigo, boolean calcIof,
                                                    boolean calcTacIofCse, String adePeridiocidade, AcessoSistema responsavel) throws ViewHelperException {
        BigDecimal tac = new BigDecimal("0.00");
        try {
            tac = calculaTAC(svcCodigo, csaCodigo, orgCodigo, vlrLiberado, cftVlr, responsavel);
        } catch (final SimulacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        BigDecimal iof = new BigDecimal("0.00");
        if (cftVlr.signum() > 0) {
            if (calcIof) {
                // Adiciona o valor da TAC ao valor liberado antes de calcular o IOF
                final BigDecimal vlrLiberadoNovo = vlrLiberado.add(tac);

                final CDCHelper cdcHelper = new CDCHelper(vlrLiberadoNovo.doubleValue(), przVlr,
                        cftVlr.doubleValue() / 100.00, dc);
                cdcHelper.calculate();
                iof = new BigDecimal(cdcHelper.getIOFE());
            } else if (calcTacIofCse) {
                // Se a consignatária é isenta de IOF, porém o serviço possui calculo de IOF,
                // verifica se a aliquota adicional de IOF existe e se deve ser utilizada
                iof = new BigDecimal(CDCHelper.calcularIofAdicional(vlrLiberado.doubleValue()));
            }
        }

        final BigDecimal vlrOperacao = vlrLiberado.add(iof).add(tac);
        vlrParcela = SimulacaoHelper.calcularValorPrestacao(vlrOperacao, przVlr, DateHelper.getSystemDatetime(),
                prazoIni, cftVlr, cftVlr, orgCodigo, adePeridiocidade, responsavel);

        return new BigDecimal[]{vlrParcela, tac, iof};
    }

    private Map<String, BigDecimal> getMapTaxas(String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        final List<TransferObject> result = getLstTaxas(svcCodigo, csaCodigo, null, responsavel);
        final Map<String, BigDecimal> retorno = new HashMap<>();

        for (TransferObject next : result) {
            try {
                String codigoProximo = next.getAttribute(Columns.PSC_CSA_CODIGO).toString();
                BigDecimal valorProximo = new BigDecimal(next.getAttribute(Columns.PSC_VLR).toString());
                BigDecimal valorTemp = retorno.get(codigoProximo);

                valorTemp = (valorTemp == null) ? valorProximo : valorTemp.add(valorProximo);
                retorno.put(codigoProximo, valorTemp);
            } catch (Exception ex2) {
            }
        }

        return retorno;
    }

    private List<TransferObject> getLstTaxas(String svcCodigo, String csaCodigo, String orgCodigo, AcessoSistema responsavel) throws ParametroControllerException {
        boolean simulacaoPorTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_POR_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);

        List<String> tpsCodigos = new ArrayList<>();
        tpsCodigos.add(CodedValues.TPS_TAC_FINANCIADA);

        if (!simulacaoPorTaxaJuros) {
            tpsCodigos.add(CodedValues.TPS_OP_FINANCIADA);
        } else {
            tpsCodigos.add(CodedValues.TPS_TIPO_TAC);
            tpsCodigos.add(CodedValues.TPS_VALOR_MIN_TAC);
            tpsCodigos.add(CodedValues.TPS_VALOR_MAX_TAC);
        }

        return parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigos, !simulacaoPorTaxaJuros, responsavel);
    }
}
