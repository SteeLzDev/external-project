package com.zetra.econsig.web.controller.servidor;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcRseTO;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ListarServicoServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Listar Serviço do Servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarServicoServidor" })
public class ListarServicoServidorWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarServicoServidorWebController.class);

    @Autowired
    private ParametroController parametroController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String readOnly = "false";
            List<Object> listaSvcBloqueaveisServidor = new ArrayList<>();

            if (responsavel.isCseSupOrg() && !responsavel.temPermissao(CodedValues.FUN_EDT_CNV_REG_SERVIDOR)) {
                readOnly = "true";
            } else if (responsavel.isSer()) {
                // Se responsavel for servidor, recupera a lista de servicos bloqueaveis por ele
                List<TransferObject> lista = parametroController.lstParamSvcCse(CodedValues.TPS_PERMITE_SER_BLOQUEAR_SERVICO_VERBA, "1", responsavel);
                if (!lista.isEmpty()) {
                    readOnly = "false";
                    for (TransferObject to : lista) {
                        listaSvcBloqueaveisServidor.add(to.getAttribute(Columns.PSE_SVC_CODIGO));
                    }
                }
            }

            String rseCodigo = responsavel.isSer() ? responsavel.getRseCodigo() : JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
            String rseMatricula = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA");
            String serNomeCodificado = JspHelper.verificaVarQryStr(request, "SER_NOME");
            String serNome = TextHelper.isNull(serNomeCodificado) ? serNomeCodificado : TextHelper.decode64(JspHelper.verificaVarQryStr(request, "SER_NOME"));

            if (rseCodigo == null || rseCodigo.equals("")) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Obtem os valores dos bloqueios por serviços
            List<TransferObject> servicosBloqueados = null;
            try {
                servicosBloqueados = parametroController.lstBloqueioSvcRegistroServidor(rseCodigo, null, responsavel);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Se for servidor, não exige motivo para operação
            Boolean exigeMotivo = Boolean.valueOf(responsavel.isSer() ? false : FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_EDT_CNV_REG_SERVIDOR, responsavel));

            model.addAttribute("exigeMotivo", exigeMotivo);
            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("rseMatricula", rseMatricula);
            model.addAttribute("serNomeCodificado", serNomeCodificado);
            model.addAttribute("serNome", serNome);
            model.addAttribute("servicosBloqueados", servicosBloqueados);
            model.addAttribute("listaSvcBloqueaveisServidor", listaSvcBloqueaveisServidor);
            model.addAttribute("readOnly", readOnly);

            return viewRedirect("jsp/editarServidor/listarServicoServidor", request, session, model, responsavel);

        } catch (ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=listarSvcSerSobrepoeParam" })
    public String listarSvcSerSobrepoeParam(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);


        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String rseCodigo = responsavel.isSer() ? responsavel.getRseCodigo() : JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
        String rseMatricula = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA");
        String serNomeCodificado = JspHelper.verificaVarQryStr(request, "SER_NOME");
        String serNome = TextHelper.isNull(serNomeCodificado) ? serNomeCodificado : TextHelper.decode64(JspHelper.verificaVarQryStr(request, "SER_NOME"));

        if (rseCodigo == null || rseCodigo.equals("")) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        List<TransferObject> servicosServidorTotal = null;
        try {
            servicosServidorTotal = parametroController.lstServicoServidor(rseCodigo, null, true, responsavel);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        int total = servicosServidorTotal.size();
        int size = JspHelper.LIMITE;
        int offset = 0;
        try {
          offset = Integer.parseInt(request.getParameter("offset"));
        } catch (Exception ex) {}

        List<TransferObject> servicosServidor = null;
        try {
            servicosServidor = parametroController.lstServicoServidor(rseCodigo, null, null, true, offset, size, responsavel);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String linkListagem = request.getRequestURI() + "?acao=listarSvcSerSobrepoeParam&RSE_CODIGO=" + rseCodigo + "&RSE_MATRICULA=" + rseMatricula + "&SER_NOME=" + serNomeCodificado;
        configurarPaginador(linkListagem, "rotulo.convenio.manutencao.titulo", total, size, null, false, request, model);

        model.addAttribute("rseCodigo", rseCodigo);
        model.addAttribute("rseMatricula", rseMatricula);
        model.addAttribute("serNomeCodificado", serNomeCodificado);
        model.addAttribute("serNome", serNome);
        model.addAttribute("servicosServidor", servicosServidor);

        return viewRedirect("jsp/editarServidor/listarServicoServidorParam", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String tmoCodigo = request.getParameter("TMO_CODIGO");
            String orsObs = request.getParameter("ADE_OBS");

            // Se for servidor, não exige motivo para operação
            if (!responsavel.isSer() && TextHelper.isNull(tmoCodigo) && FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_EDT_CNV_REG_SERVIDOR, responsavel)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.motivo.operacao.obrigatorio", responsavel));
                // Repassa o token salvo, pois o método irá revalidar o token
                request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
                return iniciar(request, response, session, model);
            }

            String rseCodigo = responsavel.isSer() ? responsavel.getRseCodigo() : JspHelper.verificaVarQryStr(request, "RSE_CODIGO");

            if (rseCodigo == null || rseCodigo.equals("")) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            List<Object> listaSvcBloqueaveisServidor = new ArrayList<>();

            // Se responsavel for servidor, recupera a lista de servicos bloqueaveis por ele
            if (responsavel.isSer()) {
                List<TransferObject> lista = parametroController.lstParamSvcCse(CodedValues.TPS_PERMITE_SER_BLOQUEAR_SERVICO_VERBA, "1", responsavel);
                if (!lista.isEmpty()) {
                    for (TransferObject to : lista) {
                        listaSvcBloqueaveisServidor.add(to.getAttribute(Columns.PSE_SVC_CODIGO));
                    }
                }
            }

            if (JspHelper.verificaVarQryStr(request, "servicosBloqueados") != null) {
                List<ParamSvcRseTO> bloqueios = new ArrayList<>();

                StringTokenizer stn = new StringTokenizer(JspHelper.verificaVarQryStr(request, "servicosBloqueados"), ",");
                while (stn.hasMoreTokens()) {
                    String svcCodigo = stn.nextToken();
                    String psrVlr = JspHelper.verificaVarQryStr(request, "svc_" + svcCodigo);
                    String psrObs = JspHelper.verificaVarQryStr(request, "svc2_" + svcCodigo);

                    ParamSvcRseTO bloqueio = new ParamSvcRseTO();
                    bloqueio.setTpsCodigo(CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO);
                    bloqueio.setRseCodigo(rseCodigo);
                    bloqueio.setSvcCodigo(svcCodigo);
                    bloqueio.setPsrVlr(psrVlr);
                    bloqueio.setPsrObs(psrObs);
                    bloqueio.setPsrAlteradoPeloServidor(listaSvcBloqueaveisServidor.isEmpty() ? "N" : "S");
                    bloqueios.add(bloqueio);
                }
                // Salva os bloqueios de servidor

                CustomTransferObject tmoObject = new CustomTransferObject();
                tmoObject.setAttribute("tmoCodigo", tmoCodigo);
                tmoObject.setAttribute("orsObs", orsObs);

                parametroController.setBloqueioSvcRegistroServidor(bloqueios, tmoObject, responsavel);

                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.servidor.servico.alterado.sucesso", responsavel));
            }

            // Repassa o token salvo, pois o método irá revalidar o token
            request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

            ParamSession paramSession = ParamSession.getParamSession(session);
            paramSession.halfBack();

            return iniciar(request, response, session, model);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }
}
