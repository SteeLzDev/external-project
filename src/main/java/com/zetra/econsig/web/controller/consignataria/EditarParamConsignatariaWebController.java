package com.zetra.econsig.web.controller.consignataria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ManterConsignatariaWebController</p>
 * <p>Description: Controlador Web para o caso de uso Manutenção de Consignatária.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Date$
 */

@Controller
@RequestMapping(method = {RequestMethod.POST}, value = {"/v3/editarParamConsignataria"})
public class EditarParamConsignatariaWebController extends ControlePaginacaoWebController {

    @Autowired
    private ParametroController parametroController;

    @RequestMapping(params = {"acao=iniciar"})
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException, ParametroControllerException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        return buscarDadosParaTela(request, session, model, responsavel, null);
    }

    @RequestMapping(params = {"acao=salvar"})
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException, ParametroControllerException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String csaCodigo = (responsavel.isCsa() ? responsavel.getCsaCodigo() : JspHelper.verificaVarQryStr(request, "CSA_CODIGO"));

        List<TransferObject> paramSist = buscarParamSist(session, responsavel, csaCodigo);
        List<TransferObject> param = new ArrayList<>();
        int invertCode = 0;

        try {
            for (TransferObject next : paramSist) {
                String tpaCodigo = next.getAttribute(Columns.TPA_CODIGO).toString();
                String oldPcsVlr = next.getAttribute(Columns.PCS_VLR) != null ? next.getAttribute(Columns.PCS_VLR).toString() : "";
                String pcsVlr = "";

                if (tpaCodigo.equals(CodedValues.TPA_FUNCOES_PARA_DEFINICAO_TAXA_JUROS)) {
                    String[] selecao = request.getParameterValues("TPA_" + tpaCodigo);
                    if (selecao != null) {
                        List<String> selecionados = Arrays.asList(selecao);
                        for (int x = 1; x <= selecionados.size(); x++) {
                            if (pcsVlr.isEmpty()) {
                                pcsVlr = selecionados.get(x - 1);
                            } else {
                                pcsVlr = pcsVlr + "," + selecionados.get(x - 1);
                            }
                        }

                        if (pcsVlr.isEmpty()) {
                            pcsVlr = CodedValues.FUN_RES_MARGEM + "," +
                                    CodedValues.FUN_RENE_CONTRATO + "," +
                                    CodedValues.FUN_COMP_CONTRATO + "," +
                                    CodedValues.FUN_SIM_CONSIGNACAO + "," +
                                    CodedValues.FUN_ALT_CONSIGNACAO + "," +
                                    CodedValues.FUN_SIMULAR_RENEGOCIACAO + "," +
                                    CodedValues.FUN_SOLICITAR_PORTABILIDADE;
                        }
                    }
                } else {
                    pcsVlr = JspHelper.verificaVarQryStr(request, "TPA_" + tpaCodigo);
                }

                if ((!oldPcsVlr.equals("") || !pcsVlr.equals("")) && !oldPcsVlr.equals(pcsVlr)) {
                    if(tpaCodigo.equals(CodedValues.TPA_PERCENTUAL_VARIACAO_MENSAL_MARGEM_POR_MATRICULA) && pcsVlr.contains(",")) {
                        pcsVlr = pcsVlr.replace(",", ".");
                    }
                    // Altera o parâmetro no banco
                    CustomTransferObject cto = new CustomTransferObject();
                    cto.setAttribute(Columns.PCS_CSA_CODIGO, csaCodigo);
                    cto.setAttribute(Columns.PCS_TPA_CODIGO, tpaCodigo);
                    cto.setAttribute(Columns.PCS_VLR, pcsVlr);

                    parametroController.updateParamCsa(cto, responsavel);
                    cto.setAttribute(Columns.PCS_VLR, pcsVlr);
                    next.setAttribute(Columns.PCS_VLR, pcsVlr);

                    if (tpaCodigo.equals(CodedValues.TPA_INFO_VINC_BLOQ_PADRAO)) {
                        if (pcsVlr.equals(CodedValues.TPA_SIM)) {
                            invertCode = 1;
                        } else if (pcsVlr.equals(CodedValues.TPA_NAO) && !oldPcsVlr.isEmpty()) {
                            invertCode = 2;
                        } else if (pcsVlr.equals(CodedValues.TPA_NAO) && oldPcsVlr.isEmpty()) {
                            invertCode = 3;
                        }
                    }
                }
                param.add(next);
            }

            if (invertCode == 1 || invertCode == 2) {
                parametroController.invertVinculoParam(invertCode, csaCodigo, responsavel);
            }

            if (invertCode == 1) {
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.alteracoes.vinculo.padrao.bloq", responsavel));
            } else if (invertCode == 2 || invertCode == 3) {
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.alteracoes.vinculo.padrao.desbloq", responsavel));
            }

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alteracoes.salvas.sucesso", responsavel));
        } catch (ParametroControllerException | ConvenioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        return buscarDadosParaTela(request, session, model, responsavel, param);

    }

    private String buscarDadosParaTela(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel, List<TransferObject> param) throws ParametroControllerException {
        String csaCodigo = (responsavel.isCsa() ? responsavel.getCsaCodigo() : JspHelper.verificaVarQryStr(request, "CSA_CODIGO"));
        String titulo = JspHelper.verificaVarQryStr(request, "titulo");
        String parametros = "acao=salvar&titulo=" + titulo + "&" + SynchronizerToken.generateToken4URL(request);

        if (param == null || !param.isEmpty()) {
            param = buscarParamSist(session, responsavel, csaCodigo);
        }

        if (responsavel.isCse() || responsavel.isCsa()) {
            List<TransferObject> reList = new ArrayList<>();
            String paramAuth = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_AUTORIZA_CONFIG_PERCENTUAL_VARIACAO_MARGEM, responsavel);
            boolean tpaAuth = paramAuth == null || Objects.equals(paramAuth, CodedValues.TPC_NAO);

            for (TransferObject tpa : param) {
                if (tpa.getAttribute(Columns.TPA_CODIGO).equals(CodedValues.TPA_PERCENTUAL_VARIACAO_MENSAL_MARGEM_POR_MATRICULA) && tpaAuth) {
                    tpaAuth = false;
                } else {
                    reList.add(tpa);
                }
            }

            param.clear();
            param = reList;
        }

        model.addAttribute("parametros", parametros);
        model.addAttribute("titulo", titulo);
        model.addAttribute("param", param);
        model.addAttribute("csaCodigo", csaCodigo);

        return viewRedirect("jsp/manterConsignataria/editarParamConsignataria", request, session, model, responsavel);
    }

    private List<TransferObject> buscarParamSist(HttpSession session, AcessoSistema responsavel, String csaCodigo) {
        List<TransferObject> paramSist = new ArrayList<>();

        try {
            if (responsavel.isCsa()) {
                paramSist = parametroController.selectParamCsa(csaCodigo, null, "S", null, responsavel);
            } else if (responsavel.isCse()) {
                paramSist = parametroController.selectParamCsa(csaCodigo, "S", null, null, responsavel);
            } else if (responsavel.isSup()) {
                paramSist = parametroController.selectParamCsa(csaCodigo, null, null, "S", responsavel);
            }
        } catch (ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            paramSist = new ArrayList<>();
        }
        return paramSist;
    }
}
