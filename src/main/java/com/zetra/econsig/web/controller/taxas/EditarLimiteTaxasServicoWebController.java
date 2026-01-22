package com.zetra.econsig.web.controller.taxas;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

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
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.LimiteTaxaJurosControllerException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.juros.LimiteTaxaJurosController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: EditarLimiteTaxasServicoWebController</p>
 * <p>Description: Controlador Web para o caso de uso de edição de limite de taxas de juros.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: fagner.luiz $
 * $Revision: 28114 $
 * $Date: 2019-10-31 09:33:57 -0300 (qui, 31 out 2019) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarLimiteTaxas" })
public class EditarLimiteTaxasServicoWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EditarLimiteTaxasServicoWebController.class);

    @Autowired
    private LimiteTaxaJurosController limiteTaxaJurosController;

    @Autowired
    private SimulacaoController simulacaoController;

    @RequestMapping(params = { "acao=iniciar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException {
        SynchronizerToken.saveToken(request);

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);
        boolean temLimiteTaxaJurosComposicaoCET = temCET && ParamSist.paramEquals(CodedValues.TPC_HABILITA_CADASTRO_TAXA_JUROS_EDITAR_CET_MANUTENCAO_CSA, CodedValues.TPC_SIM, responsavel);
        String titulo = (temCET ? ApplicationResourcesHelper.getMessage("rotulo.consultar.limite.cet.titulo", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.consultar.limite.taxa.juros.titulo", responsavel));
        String rotuloLimiteJurosMax = (temCET ? ApplicationResourcesHelper.getMessage("rotulo.limite.cet.juros.max", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.limite.taxa.juros.juros.max", responsavel));
        String rotuloLimitePrazoRef = (temCET ? ApplicationResourcesHelper.getMessage("rotulo.limite.cet.prazo.ref", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.limite.taxa.juros.prazo.ref", responsavel));

        String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");

        if (TextHelper.isNull(svcCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String svcDescricao = JspHelper.verificaVarQryStr(request, "titulo");

        String msgNovoLimiteCliqueAqui = "";
        String msgEditarLimiteCliqueAqui = "";
        String msgExcluirLimiteCliqueAqui = "";
        if (temCET) {
            msgNovoLimiteCliqueAqui = ApplicationResourcesHelper.getMessage("mensagem.criar.limite.cet.clique.aqui", responsavel);
            msgEditarLimiteCliqueAqui = ApplicationResourcesHelper.getMessage("mensagem.editar.limite.cet.clique.aqui", responsavel);
            msgExcluirLimiteCliqueAqui = ApplicationResourcesHelper.getMessage("mensagem.excluir.limite.cet.clique.aqui", responsavel);
        } else {
            msgNovoLimiteCliqueAqui = ApplicationResourcesHelper.getMessage("mensagem.criar.limite.taxa.juros.clique.aqui", responsavel);
            msgEditarLimiteCliqueAqui = ApplicationResourcesHelper.getMessage("mensagem.editar.limite.taxa.juros.clique.aqui", responsavel);
            msgExcluirLimiteCliqueAqui = ApplicationResourcesHelper.getMessage("mensagem.excluir.limite.taxa.juros.clique.aqui", responsavel);
        }

        List<TransferObject> limites = null;

        CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAttribute(Columns.LTJ_SVC_CODIGO, svcCodigo);

        try {
            limites = limiteTaxaJurosController.listaLimiteTaxaJuros(criterio, -1, -1, responsavel);
        } catch (LimiteTaxaJurosControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            limites = new ArrayList<>();
        }


        model.addAttribute("titulo", titulo);
        model.addAttribute("rotuloLimitePrazoRef", rotuloLimitePrazoRef);
        model.addAttribute("rotuloLimiteJurosMax", rotuloLimiteJurosMax);
        model.addAttribute("limites", limites);
        model.addAttribute("svcCodigo", svcCodigo);
        model.addAttribute("svcDescricao", svcDescricao);
        model.addAttribute("msgNovoLimiteCliqueAqui", msgNovoLimiteCliqueAqui);
        model.addAttribute("msgEditarLimiteCliqueAqui", msgEditarLimiteCliqueAqui);
        model.addAttribute("msgExcluirLimiteCliqueAqui", msgExcluirLimiteCliqueAqui);
        model.addAttribute("rotuloLimitePrazoRef", rotuloLimitePrazoRef);
        model.addAttribute("temLimiteTaxaJurosComposicaoCET", temLimiteTaxaJurosComposicaoCET);

        return viewRedirect("jsp/manterTaxas/listarLimiteTaxaJuros", request, session, model, responsavel);

    }

    @RequestMapping(params = { "acao=incluir" })
    public String incluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);
        boolean temLimiteTaxaJurosComposicaoCET = temCET && ParamSist.paramEquals(CodedValues.TPC_HABILITA_CADASTRO_TAXA_JUROS_EDITAR_CET_MANUTENCAO_CSA, CodedValues.TPC_SIM, responsavel);
        boolean aplicarRegraCETTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_APLICAR_REGRAS_LIMITE_VARIACAO_CET_PARA_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);

        String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
        if (TextHelper.isNull(svcCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String ltjCodigo = null;
        if (request.getParameter("LTJ_CODIGO") != null) {
            ltjCodigo = JspHelper.verificaVarQryStr(request, "LTJ_CODIGO");
        }

        String svcDescricao = JspHelper.verificaVarQryStr(request, "SVC_DESCRICAO");

        TransferObject limite = null;
        String ltjPrazoRef = null;
        BigDecimal ltjJurosMax = null;
        BigDecimal ltjVlrRef = null;
        try {
            if (ltjCodigo != null && TextHelper.isNull(session.getAttribute(CodedValues.MSG_ERRO))) {
                CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.LTJ_CODIGO, ltjCodigo);
                limite = limiteTaxaJurosController.findLimiteTaxaJuros(criterio, responsavel);
                ltjPrazoRef = limite.getAttribute(Columns.LTJ_PRAZO_REF).toString();
                ltjJurosMax = (BigDecimal) limite.getAttribute(Columns.LTJ_JUROS_MAX);
                ltjVlrRef = (BigDecimal) limite.getAttribute(Columns.LTJ_VLR_REF);
            }
        } catch (LimiteTaxaJurosControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        String titulo = "";
        if (limite != null) {
            titulo = (temCET ? ApplicationResourcesHelper.getMessage("rotulo.editar.limite.cet.titulo", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.editar.limite.taxa.juros.titulo", responsavel));
        } else {
            titulo = (temCET ? ApplicationResourcesHelper.getMessage("rotulo.criar.limite.cet.titulo", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.criar.limite.taxa.juros.titulo", responsavel));
        }

        String rotuloLimiteJurosMax = (temCET ? ApplicationResourcesHelper.getMessage("rotulo.limite.cet.juros.max", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.limite.taxa.juros.juros.max", responsavel));
        String rotuloLimitePrazoRef = (temCET ? ApplicationResourcesHelper.getMessage("rotulo.limite.cet.prazo.ref", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.limite.taxa.juros.prazo.ref", responsavel));

        model.addAttribute("titulo", titulo);
        model.addAttribute("limite", limite);
        model.addAttribute("ltjPrazoRef", ltjPrazoRef);
        model.addAttribute("temCET", temCET);
        model.addAttribute("podeEditar", responsavel.temPermissao(CodedValues.FUN_EDT_LIMITE_TAXA));
        model.addAttribute("rotuloLimiteJurosMax", rotuloLimiteJurosMax);
        model.addAttribute("rotuloLimitePrazoRef", rotuloLimitePrazoRef);
        model.addAttribute("rotuloDataFimVigencia", ApplicationResourcesHelper.getMessage("rotulo.taxa.juros.data.fim.vigencia", responsavel));
        model.addAttribute("ltjJurosMax", ltjJurosMax);
        model.addAttribute("ltjVlrRef", ltjVlrRef);
        model.addAttribute("temLimiteTaxaJurosComposicaoCET", temLimiteTaxaJurosComposicaoCET);
        model.addAttribute("svcCodigo", svcCodigo);
        model.addAttribute("LTJ_CODIGO", ltjCodigo);
        model.addAttribute("svcDescricao", svcDescricao);
        if (!model.containsAttribute("lstTaxaSuperior")) {
            model.addAttribute("lstTaxaSuperior", new ArrayList<TransferObject>());
        }
        
        if(!model.containsAttribute("lstRegraJurosSuperior") && aplicarRegraCETTaxaJuros) {
            model.addAttribute("lstRegraJurosSuperior", new ArrayList<TransferObject>());
        }

        return viewRedirect("jsp/manterTaxas/editarLimiteTaxaJuros", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);
        boolean aplicarRegraCETTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_APLICAR_REGRAS_LIMITE_VARIACAO_CET_PARA_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);

        try {
            String reqColumnsStr = "LTJ_PRAZO_REF|LTJ_JUROS_MAX";
            String msgErro = JspHelper.verificaCamposForm(request, reqColumnsStr, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel), "100%");

            model.addAttribute("msgErro", msgErro);

            if (!TextHelper.isNull(msgErro)) {
                return incluir(request, response, session, model);
            }

            Short prazo = Short.valueOf(JspHelper.verificaVarQryStr(request, "LTJ_PRAZO_REF"));
            BigDecimal juros = (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "LTJ_JUROS_MAX")) ? new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "LTJ_JUROS_MAX"), NumberHelper.getLang(), "en", 8, 8)) : null);
            BigDecimal taxaCET = (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "LTJ_VLR_REF")) ? new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "LTJ_VLR_REF"), NumberHelper.getLang(), "en", 8, 8)) : null);

            // valida campo juros
            BigDecimal maiorValorPermitido = new BigDecimal("99.99999999");
            if ((juros != null && juros.compareTo(maiorValorPermitido) > 0) || (taxaCET != null && taxaCET.compareTo(maiorValorPermitido) > 0)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.limite.taxa.juros.valor.maximo", responsavel, NumberHelper.format(maiorValorPermitido.doubleValue(), NumberHelper.getLang(), 2, 8)));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (taxaCET != null && taxaCET.compareTo(juros) > 0) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.limite.taxa.juros.composicao.cet.valor.maximo", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
            if (TextHelper.isNull(svcCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String ltjCodigo = JspHelper.verificaVarQryStr(request, "LTJ_CODIGO");
            List<TransferObject> lstTaxaSuperior = new ArrayList<>();
            List<TransferObject> lstRegraJurosSuperior = new ArrayList<>();

            String finalizarEdicao = JspHelper.verificaVarQryStr(request, "finalizarEdicao");
            if (!TextHelper.isNull(finalizarEdicao) && finalizarEdicao.equals("false")) {

                // Recupera taxas de juros superiores ao limite definido
                lstTaxaSuperior = simulacaoController.getTaxaSuperiorTaxaLimite(svcCodigo, prazo, juros, responsavel);

                if(aplicarRegraCETTaxaJuros) {
                    lstRegraJurosSuperior = simulacaoController.getRegraJurosTaxaLimite(svcCodigo, prazo, juros, responsavel);    
                }
            }
            String cftDataFimVig = JspHelper.verificaVarQryStr(request, "CFT_DATA_FIM_VIG");

            if(!aplicarRegraCETTaxaJuros) {
                if (lstTaxaSuperior == null || lstTaxaSuperior.isEmpty()) {
                    CustomTransferObject to = new CustomTransferObject();
                    to.setAttribute(Columns.LTJ_SVC_CODIGO, svcCodigo);
                    to.setAttribute(Columns.LTJ_PRAZO_REF, prazo);
                    to.setAttribute(Columns.LTJ_JUROS_MAX, juros);
                    to.setAttribute(Columns.LTJ_VLR_REF, taxaCET);
                    to.setAttribute(Columns.CFT_DATA_FIM_VIG, cftDataFimVig);

                    boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);
                    if (!TextHelper.isNull(ltjCodigo)) {
                        to.setAttribute(Columns.LTJ_CODIGO, ltjCodigo);
                        limiteTaxaJurosController.updateLimiteTaxaJuros(to, responsavel);
                        session.setAttribute(CodedValues.MSG_INFO, (temCET ? ApplicationResourcesHelper.getMessage("mensagem.limite.cet.alterado.sucesso", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.limite.taxa.juros.alterado.sucesso", responsavel)));
                    } else {
                        ltjCodigo = limiteTaxaJurosController.createLimiteTaxaJuros(to, responsavel);
                        session.setAttribute(CodedValues.MSG_INFO, (temCET ? ApplicationResourcesHelper.getMessage("mensagem.limite.cet.criado.sucesso", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.limite.taxa.juros.criado.sucesso", responsavel)));
                    }
                } else {
                    request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
                    model.addAttribute("lstTaxaSuperior", lstTaxaSuperior);
                    return incluir(request, response, session, model);
                }
            }else {
                if((lstTaxaSuperior == null || lstTaxaSuperior.isEmpty()) && (lstRegraJurosSuperior == null || lstRegraJurosSuperior.isEmpty())) {
                    CustomTransferObject to = new CustomTransferObject();
                    to.setAttribute(Columns.LTJ_SVC_CODIGO, svcCodigo);
                    to.setAttribute(Columns.LTJ_PRAZO_REF, prazo);
                    to.setAttribute(Columns.LTJ_JUROS_MAX, juros);
                    to.setAttribute(Columns.LTJ_VLR_REF, taxaCET);
                    to.setAttribute(Columns.CFT_DATA_FIM_VIG, cftDataFimVig);
                    to.setAttribute("PARAM_TPC_CET_REGRA_TAXA", aplicarRegraCETTaxaJuros);

                    boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);
                    if (!TextHelper.isNull(ltjCodigo)) {
                        to.setAttribute(Columns.LTJ_CODIGO, ltjCodigo);
                        limiteTaxaJurosController.updateLimiteTaxaJuros(to, responsavel);
                        session.setAttribute(CodedValues.MSG_INFO, (temCET ? ApplicationResourcesHelper.getMessage("mensagem.limite.cet.alterado.sucesso", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.limite.taxa.juros.alterado.sucesso", responsavel)));
                    } else {
                        ltjCodigo = limiteTaxaJurosController.createLimiteTaxaJuros(to, responsavel);
                        session.setAttribute(CodedValues.MSG_INFO, (temCET ? ApplicationResourcesHelper.getMessage("mensagem.limite.cet.criado.sucesso", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.limite.taxa.juros.criado.sucesso", responsavel)));
                    }
                }else {
                    request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
                    model.addAttribute("lstTaxaSuperior", lstTaxaSuperior);
                    model.addAttribute("lstRegraJurosSuperior", lstRegraJurosSuperior);
                    return incluir(request, response, session, model);
                }
            }
            
        model.addAttribute("lstTaxaSuperior", lstTaxaSuperior);

        } catch (SimulacaoControllerException | LimiteTaxaJurosControllerException | ParseException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }

        return listar(request, response, session, model);

    }

    @RequestMapping(params = ("acao=excluir"))
    public String excluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConsignatariaControllerException, ConsignanteControllerException, IllegalAccessException, InstantiationException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);

        //Exclui limite de taxa de juros
        if (request.getParameter("ltjCodigo") != null) {
            String codigo = JspHelper.verificaVarQryStr(request, "ltjCodigo");
            try {
                CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.LTJ_CODIGO, codigo);

                limiteTaxaJurosController.removeLimiteTaxaJuros(criterio, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, (temCET ? ApplicationResourcesHelper.getMessage("mensagem.limite.cet.excluido.sucesso", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.limite.taxa.juros.excluido.sucesso", responsavel)));
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }

        return listar(request, response, session, model);
    }
}
