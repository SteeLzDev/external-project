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
import com.zetra.econsig.dto.entidade.ParamNseRseTO;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ListarNaturezaServicoServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Listar Natureza de Serviço do Servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarNaturezaServicoServidor" })
public class ListarNaturezaServicoServidorWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarNaturezaServicoServidorWebController.class);

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private ServicoController servicoController;

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
                        try {
                            String svcCodigo = to.getAttribute(Columns.PSE_SVC_CODIGO).toString();
                            TransferObject svc = servicoController.findNaturezaServico(svcCodigo, responsavel);
                            listaSvcBloqueaveisServidor.add(svc.getAttribute(Columns.NSE_CODIGO));
                        } catch (ServicoControllerException e) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        }
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

            // Obtem os valores dos bloqueios por natureza de serviço
            List<TransferObject> naturezaServicoBloqueados = null;
            try {
                naturezaServicoBloqueados = parametroController.lstBloqueioNseRegistroServidor(rseCodigo, null, responsavel);
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
            model.addAttribute("naturezaServicoBloqueados", naturezaServicoBloqueados);
            model.addAttribute("listaSvcBloqueaveisServidor", listaSvcBloqueaveisServidor);
            model.addAttribute("readOnly", readOnly);

            return viewRedirect("jsp/editarServidor/listarNaturezaServicoServidor", request, session, model, responsavel);

        } catch (ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
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

            if (JspHelper.verificaVarQryStr(request, "naturezaServicoBloqueados") != null) {
                List<ParamNseRseTO> bloqueios = new ArrayList<>();

                StringTokenizer stn = new StringTokenizer(JspHelper.verificaVarQryStr(request, "naturezaServicoBloqueados"), ",");
                while (stn.hasMoreTokens()) {
                    String nseCodigo = stn.nextToken();
                    String pnrVlr = JspHelper.verificaVarQryStr(request, "nse_" + nseCodigo);
                    String pnrObs = JspHelper.verificaVarQryStr(request, "nse2_" + nseCodigo);

                    ParamNseRseTO bloqueio = new ParamNseRseTO();
                    bloqueio.setTpsCodigo(CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO);
                    bloqueio.setRseCodigo(rseCodigo);
                    bloqueio.setNseCodigo(nseCodigo);
                    bloqueio.setPnrVlr(pnrVlr);
                    bloqueio.setPnrObs(pnrObs);
                    bloqueio.setPnrAlteradoPeloServidor(listaSvcBloqueaveisServidor.isEmpty() ? "N" : "S");
                    bloqueios.add(bloqueio);
                }

                CustomTransferObject tmoObject = new CustomTransferObject();
                tmoObject.setAttribute("tmoCodigo", tmoCodigo);
                tmoObject.setAttribute("orsObs", orsObs);

                // Salva os bloqueios de servidor
                parametroController.setBloqueioNseRegistroServidor(bloqueios,tmoObject, responsavel);

                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.servidor.natureza.servico.alterado.sucesso", responsavel));
            }

            // Repassa o token salvo, pois o método irá revalidar o token
            request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

            return iniciar(request, response, session, model);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }
}
