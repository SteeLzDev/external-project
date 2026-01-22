package com.zetra.econsig.web.controller.indice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
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
import com.zetra.econsig.exception.IndiceControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.indice.IndiceController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ManterIndiceWebController</p>
 * <p>Description: Manter índices</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: igor.lucas $
 * $Revision: 30120 $
 * $Date: 2020-08-12 14:15:47 -0300 (qua, 12 ago 2020) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/manterIndice" })
public class ManterIndiceWebController extends ControlePaginacaoWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterIndiceWebController.class);

    @Autowired
    private IndiceController indiceController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        String svcCodigo = JspHelper.verificaVarQryStr(request, "svcCodigo");
        String csaCodigo = JspHelper.verificaVarQryStr(request, "csaCodigo");
        String parametros = "|svcCodigo(" + svcCodigo + "|csaCodigo(" + csaCodigo;
        String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");

        String linkRet = ("../v3/manterIndice?acao=iniciar&" + SynchronizerToken.generateToken4URL(request)).replace('?', '$').replace('=', '(').replace('&', '|');
        linkRet = linkRet + parametros;

        String linkBtnNovo = "../v3/manterIndice?acao=editar&csaCodigo=" + csaCodigo + "&svcCodigo=" + svcCodigo + "&" + SynchronizerToken.generateToken4URL(request);
        String linkAction = "../v3/manterIndice?acao=iniciar&csaCodigo=" + csaCodigo + "&svcCodigo=" + svcCodigo + "&" + SynchronizerToken.generateToken4URL(request);

        List<TransferObject> indices = null;

        //--------------  FILTRO ------------------

        int filtro_tipo = -1;
        try {
            filtro_tipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
        } catch (Exception ex1) {
        }

        try {
            CustomTransferObject criterio = new CustomTransferObject();

            criterio.setAttribute(Columns.IND_SVC_CODIGO, svcCodigo);
            criterio.setAttribute(Columns.IND_CSA_CODIGO, csaCodigo);

            String campo = null;

            if (!filtro.equals("") && filtro_tipo != -1) {

                switch (filtro_tipo) {
                    case 2:
                        campo = Columns.IND_CODIGO;
                        break;
                    case 3:
                        campo = Columns.IND_DESCRICAO;
                        break;
                    default:
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
                criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);
            }

            //-------------- PAGINACAO ------------------

            int total = indiceController.countIndices(criterio, responsavel);
            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            indices = indiceController.selectIndices(size, offset, criterio, responsavel);

            configurarPaginador("../v3/manterIndice?acao=iniciar", "rotulo.indice.singular", total, size, null, false, request, model);

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            indices = new ArrayList<>();
        }

        model.addAttribute("svcCodigo", svcCodigo);
        model.addAttribute("csaCodigo", csaCodigo);
        model.addAttribute("parametros", parametros);
        model.addAttribute("filtro", filtro);
        model.addAttribute("linkRet", linkRet);
        model.addAttribute("linkBtnNovo", linkBtnNovo);
        model.addAttribute("linkAction", linkAction);
        model.addAttribute("filtro_tipo", filtro_tipo);
        model.addAttribute("indices", indices);

        return viewRedirect("jsp/manterIndice/listarIndice", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=modificar" })
    public String modificar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        CustomTransferObject originalIndice = new CustomTransferObject();
        CustomTransferObject novoIndice = new CustomTransferObject();

        // valores originais
        String csaCodigo = (responsavel.isCsa() ? responsavel.getCodigoEntidade() : request.getParameter("csaCodigo"));
        String svcCodigo = request.getParameter("svcCodigo");
        String indCodigoOrig = request.getParameter("indCodigoOrig");
        String indDescricaoOrig = request.getParameter("indDescricaoOrig");
        String indCodigoNovo = request.getParameter("indCodigo");
        String indDescricaoNovo = request.getParameter("indDescricao");
        String reqColumnsStr = "indCodigo|indDescricao";
        String msgErro = JspHelper.verificaCamposForm(request, session, reqColumnsStr, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel), "100%");

        //-------------- Parametros ------------------

        //Define se o indice eh numero ou nao (true numero) se !existe =null
        boolean indiceNumerico = ParamSist.getInstance().getParam(CodedValues.TPC_INDICE_NUMERICO, responsavel) != null && ParamSist.getInstance().getParam(CodedValues.TPC_INDICE_NUMERICO, responsavel).toString().equals(CodedValues.TPC_SIM);

        //Limite numérico do indice
        int limiteIndice = (ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel) != null && !ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel).equals("")) ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel).toString()) : 99;

        String maskNum = "#D" + (String.valueOf(limiteIndice).length());
        String maskNaoNum = "#A" + (String.valueOf(limiteIndice).length());

        //----------------------------------------------------
        String operacao = request.getParameter("operacao");

        novoIndice.setAttribute(Columns.IND_CODIGO, indCodigoNovo);
        novoIndice.setAttribute(Columns.IND_DESCRICAO, indDescricaoNovo);
        novoIndice.setAttribute(Columns.IND_CSA_CODIGO, csaCodigo);
        novoIndice.setAttribute(Columns.IND_SVC_CODIGO, svcCodigo);

        if (operacao.equals("modificar")) {
            try {
                originalIndice.setAttribute(Columns.IND_CODIGO, indCodigoOrig);
                originalIndice.setAttribute(Columns.IND_DESCRICAO, indDescricaoOrig);
                originalIndice.setAttribute(Columns.IND_CSA_CODIGO, csaCodigo);
                originalIndice.setAttribute(Columns.IND_SVC_CODIGO, svcCodigo);
                indiceController.updateIndice(novoIndice, originalIndice, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.indice.alterado.sucesso", responsavel));

            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        } else if (operacao.equals("inserir")) {
            try {
                indiceController.createIndice(novoIndice, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alteracoes.salvas.sucesso", responsavel));

            } catch (IndiceControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }

        ParamSession paramSession = ParamSession.getParamSession(session);
        paramSession.halfBack();

        // Atualiza o original como sendo o novo anterior
        indCodigoOrig = indCodigoNovo;
        indDescricaoOrig = indDescricaoNovo;

        model.addAttribute("csaCodigo", csaCodigo);
        model.addAttribute("svcCodigo", svcCodigo);
        model.addAttribute("indCodigoOrig", indCodigoOrig);
        model.addAttribute("indDescricaoOrig", indDescricaoOrig);
        model.addAttribute("indDescricaoNovo", indDescricaoNovo);
        model.addAttribute("indCodigoNovo", indCodigoNovo);
        model.addAttribute("reqColumnsStr", reqColumnsStr);
        model.addAttribute("msgErro", msgErro);
        model.addAttribute("indiceNumerico", indiceNumerico);
        model.addAttribute("maskNum", maskNum);
        model.addAttribute("maskNaoNum", maskNaoNum);

        return viewRedirect("jsp/manterIndice/editarIndice", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        // valores originais
        String csaCodigo = (responsavel.isCsa() ? responsavel.getCodigoEntidade() : request.getParameter("csaCodigo"));
        String svcCodigo = request.getParameter("svcCodigo");
        String indCodigoOrig = request.getParameter("indCodigoOrig");
        String indDescricaoOrig = request.getParameter("indDescricaoOrig");
        String indCodigoNovo = request.getParameter("indCodigo");
        String indDescricaoNovo = request.getParameter("indDescricao");
        String reqColumnsStr = "indCodigo|indDescricao";
        String msgErro = JspHelper.verificaCamposForm(request, session, reqColumnsStr, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel), "100%");

        //-------------- Parametros ------------------

        //Define se o indice eh numero ou nao (true numero) se !existe =null
        boolean indiceNumerico = ParamSist.getInstance().getParam(CodedValues.TPC_INDICE_NUMERICO, responsavel) != null && ParamSist.getInstance().getParam(CodedValues.TPC_INDICE_NUMERICO, responsavel).toString().equals(CodedValues.TPC_SIM);

        //Limite numérico do indice
        int limiteIndice = (ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel) != null && !ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel).equals("")) ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel).toString()) : 99;

        String maskNum = "#D" + (String.valueOf(limiteIndice).length());
        String maskNaoNum = "#A" + (String.valueOf(limiteIndice).length());

        model.addAttribute("csaCodigo", csaCodigo);
        model.addAttribute("svcCodigo", svcCodigo);
        model.addAttribute("indCodigoOrig", indCodigoOrig);
        model.addAttribute("indDescricaoOrig", indDescricaoOrig);
        model.addAttribute("indDescricaoNovo", indDescricaoNovo);
        model.addAttribute("indCodigoNovo", indCodigoNovo);
        model.addAttribute("reqColumnsStr", reqColumnsStr);
        model.addAttribute("msgErro", msgErro);
        model.addAttribute("indiceNumerico", indiceNumerico);
        model.addAttribute("maskNum", maskNum);
        model.addAttribute("maskNaoNum", maskNaoNum);

        return viewRedirect("jsp/manterIndice/editarIndice", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        ParamSession paramSession = ParamSession.getParamSession(session);

        CustomTransferObject criterio = new CustomTransferObject();

        boolean podeExcluirIndices = responsavel.temPermissao(CodedValues.FUN_EXCL_INDICES);

        criterio.setAttribute(Columns.IND_CODIGO, request.getParameter("indCodigo"));
        criterio.setAttribute(Columns.IND_DESCRICAO, request.getParameter("indDescricao"));
        criterio.setAttribute(Columns.IND_CSA_CODIGO, request.getParameter("csaCodigo"));
        criterio.setAttribute(Columns.IND_SVC_CODIGO, request.getParameter("svcCodigo"));

        // Exclui o indice
        if (podeExcluirIndices) {
            try {
                indiceController.removeIndice(criterio, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.indice.excluido.sucesso", responsavel));
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        }

        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }
}
