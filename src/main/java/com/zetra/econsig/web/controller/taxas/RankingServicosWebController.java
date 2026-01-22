package com.zetra.econsig.web.controller.taxas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.PrazoTransferObject;
import com.zetra.econsig.dto.web.RankServicoDTO;
import com.zetra.econsig.helper.financeiro.CDCHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;


/**
 * <p>
 * Title: RankingServicos
 * </p>
 * <p>
 * Description: Controlador Web para o caso de uso Ranking de Serviços.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002-2018
 * </p>
 * <p>
 * Company: ZetraSoft
 * </p>
 * $Author$ $Revision$ $Date: 2017-09-06 10:39:44 -0300
 * (Qua, 06 Set 2017) $
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/visualizarRankingServico" })
public class RankingServicosWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RankingServicosWebController.class);

    @Autowired
    private SimulacaoController simulacaoController;

    @RequestMapping(params = { "acao=editarTaxa" })
    public String editarTaxa(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=consultarTaxa" })
    public String consultarTaxa(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=consultarCoeficiente" })
    public String consultarCoeficiente(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
       return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=editarCoeficiente" })
    public String editarCoeficiente(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
       return iniciar(request, response, session, model);
    }

    private String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String svc_codigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
            String titulo = JspHelper.verificaVarQryStr(request, "titulo");

            if (svc_codigo.equals("") || titulo.equals("")) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Verifica se o sistema está configurado para trabalhar com o CET.
            boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);

            List<PrazoTransferObject> prazos = null;
            // Seleciona prazos ativos.
            try {
                prazos = simulacaoController.findPrazoAtivoByServico(svc_codigo, responsavel);
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            boolean simulacaoPorTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_POR_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);

            //Verifica se o método de simulação é o Mexicano ou brasileiro
            boolean simulacaoMetodoMexicano = ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_MEXICANO, responsavel);
            boolean simulacaoMetodoBrasileiro = ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_BRASILEIRO, responsavel);

            if (!simulacaoMetodoBrasileiro && (temCET || !simulacaoPorTaxaJuros)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.parametrizacao.taxa.iva", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            Short prz_vlr = null;
            BigDecimal vlr_liberado = new BigDecimal("1000.00");

            List<RankServicoDTO> rankList = new ArrayList<>();

            if (prazos.size() > 0) {
                PrazoTransferObject pto = null;
                List<TransferObject> coeficientes = null;
                Map<Short, List<TransferObject>> rankings = new HashMap<>();
                int maxSizeRanking = 0;
                Iterator<PrazoTransferObject> it = prazos.iterator();
                while (it.hasNext()) {
                    pto = it.next();
                    prz_vlr = pto.getPrzVlr();

                    try {
                        coeficientes = simulacaoController.simularConsignacao(svc_codigo, null, null, null, vlr_liberado, prz_vlr.shortValue(), null, true, CodedValues.PERIODICIDADE_FOLHA_MENSAL, responsavel);
                    } catch (Exception ex) {
                        session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                        coeficientes = new ArrayList<>();
                    }

                    rankings.put(prz_vlr, coeficientes);
                    if (coeficientes.size() > maxSizeRanking) {
                        maxSizeRanking = coeficientes.size();
                    }
                }

                String csa_nome_lst, str_cft_vlr, vlr_parcela, cetAnual;
                BigDecimal cft_vlr;
                String tac = "", iof = "";
                String cat = "", iva = "";
                CustomTransferObject coeficiente = null;

                for (int j = 0; j < maxSizeRanking; j++) {
                    for (int i = 0; i < prazos.size(); i++) {
                        pto = prazos.get(i);
                        prz_vlr = pto.getPrzVlr();
                        coeficientes = rankings.get(prz_vlr);
                        if (j < coeficientes.size()) {
                            coeficiente = (CustomTransferObject) coeficientes.get(j);
                            csa_nome_lst = (String) coeficiente.getAttribute("TITULO");
                            cft_vlr = new BigDecimal(coeficiente.getAttribute(Columns.CFT_VLR).toString());
                            str_cft_vlr = NumberHelper.format(cft_vlr.doubleValue(), NumberHelper.getLang(), 2, 8);
                            cetAnual = CDCHelper.getStrTaxaEquivalenteAnual(str_cft_vlr);

                            if (cft_vlr.signum() > 0) {
                                vlr_parcela = NumberHelper.reformat(coeficiente.getAttribute("VLR_PARCELA").toString(), "en", NumberHelper.getLang(), true);
                            } else {
                                vlr_parcela = "-";
                            }
                            if (!temCET && simulacaoPorTaxaJuros) {
                                if (simulacaoMetodoMexicano) {
                                    cat = NumberHelper.reformat((coeficiente.getAttribute("CAT") != null) ? coeficiente.getAttribute("CAT").toString() : "0.00", "en", NumberHelper.getLang(), true);
                                    iva = NumberHelper.reformat((coeficiente.getAttribute("IVA") != null) ? coeficiente.getAttribute("IVA").toString() : "0.00", "en", NumberHelper.getLang(), true);
                                } else if (simulacaoMetodoBrasileiro) {
                                    tac = NumberHelper.reformat((coeficiente.getAttribute("TAC_FINANCIADA") != null) ? coeficiente.getAttribute("TAC_FINANCIADA").toString() : "0.00", "en", NumberHelper.getLang(), true);
                                    iof = NumberHelper.reformat((coeficiente.getAttribute("IOF") != null) ? coeficiente.getAttribute("IOF").toString() : "0.00", "en", NumberHelper.getLang(), true);
                                }
                            }
                        } else {
                            csa_nome_lst = "";
                            vlr_parcela = "";
                            str_cft_vlr = "";
                            cetAnual = "";
                            tac = "";
                            iof = "";
                        }
                        rankList.add(new RankServicoDTO(csa_nome_lst, str_cft_vlr, cetAnual, vlr_parcela, temCET, simulacaoPorTaxaJuros, simulacaoMetodoMexicano, cat, iva, tac, iof));
                    }
                }

                model.addAttribute("maxSizeRanking", maxSizeRanking);

            }

            //Cria uma lista que separa o rankList pelo prazo
            ArrayList<ArrayList<RankServicoDTO>> listaSeparada = new ArrayList<>(prazos.size());

            for(int i = 0; i < prazos.size(); i++) {
                listaSeparada.add(new ArrayList<RankServicoDTO>());
            }

            for(int i = 0; i < rankList.size();i++) {
                listaSeparada.get(i%prazos.size()).add(rankList.get(i));

            }

            // Exibe Botao que leva ao rodapé
            boolean exibeBotaoRodape = ParamSist.paramEquals(CodedValues.TPC_EXIBE_BOTAO_RESPONSAVEL_PELO_RODAPE_DA_PAGINA, CodedValues.TPC_SIM, responsavel);

            model.addAttribute("exibeBotaoRodape", exibeBotaoRodape);
            model.addAttribute("svc_codigo", svc_codigo);
            model.addAttribute("prazos", prazos);
            model.addAttribute("simulacaoPorTaxaJuros", simulacaoPorTaxaJuros);
            model.addAttribute("temCET", temCET);
            model.addAttribute("simulacaoMetodoMexicano", simulacaoMetodoMexicano);
            model.addAttribute("simulacaoMetodoBrasileiro", simulacaoMetodoBrasileiro);
            model.addAttribute("titulo", titulo);
            model.addAttribute("rankList", rankList);
            model.addAttribute("listaSeparada",listaSeparada);

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        return viewRedirect("jsp/manterTaxas/listarRankingServico", request, session, model, responsavel);
    }

}
