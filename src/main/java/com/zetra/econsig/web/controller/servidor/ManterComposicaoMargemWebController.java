package com.zetra.econsig.web.controller.servidor;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ManterComposicaoMargemWebController</p>
 * <p>Description: Controlador Web para o caso de uso manutenção da composição de margem do servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/manterComposicaoMargemServidor" })
public class ManterComposicaoMargemWebController extends ControlePaginacaoWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterComposicaoMargemWebController.class);

    @Autowired
    private ServidorController servidorController;

    @RequestMapping(params = { "acao=listar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, @RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            if (TextHelper.isNull(rseCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            TransferObject composicaoMargem = new CustomTransferObject();
            composicaoMargem.setAttribute(Columns.RSE_CODIGO, rseCodigo);

            ServidorTransferObject servidor = servidorController.findServidorByRseCodigo(rseCodigo, responsavel);
            RegistroServidorTO registroServidor = servidorController.findRegistroServidor(rseCodigo, responsavel);

            List<TransferObject> listaComposicaoMargem = servidorController.listarComposicaoMargem(composicaoMargem, responsavel);

            model.addAttribute("composicoes", listaComposicaoMargem);
            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("servidor", servidor);
            model.addAttribute("registroServidor", registroServidor);
            model.addAttribute("linkPaginacao", "");

            int total = listaComposicaoMargem != null ? listaComposicaoMargem.size() : 0;
            int size = JspHelper.LIMITE;

            List<String> requestParams = Arrays.asList(new String[] { "RSE_CODIGO" });
            configurarPaginador("../v3/manterComposicaoMargemServidor?acao=listar", "rotulo.paginacao.titulo.operacao.fila.autorizacao", total, size, requestParams, false, request, model);

            return viewRedirect("jsp/editarServidor/listarComposicaoMargemServidor", request, session, model, responsavel);

        } catch (ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String cmaCodigo = JspHelper.verificaVarQryStr(request, "CMA_CODIGO");
            String rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");

            if (TextHelper.isNull(rseCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            ServidorTransferObject servidor = servidorController.findServidorByRseCodigo(rseCodigo, responsavel);
            RegistroServidorTO registroServidor = servidorController.findRegistroServidor(rseCodigo, responsavel);

            String vctCodigo = "";
            String cmaVlr = "";
            String cmaVinculo = "";
            String cmaQuantidade = "1";
            String vrsCodigo = "";
            String crsCodigo = "";
            String desconto = "";

            if (!TextHelper.isNull(cmaCodigo)) {
                TransferObject composicao = servidorController.findComposicaoMargem(cmaCodigo, responsavel);

                vctCodigo = !TextHelper.isNull(composicao.getAttribute(Columns.CMA_VCT_CODIGO)) ? composicao.getAttribute(Columns.CMA_VCT_CODIGO).toString() : "";
                cmaVlr = !TextHelper.isNull(composicao.getAttribute(Columns.CMA_VLR)) ? NumberHelper.format(((BigDecimal) composicao.getAttribute(Columns.CMA_VLR)).doubleValue(), NumberHelper.getLang()) : "";
                cmaVinculo = !TextHelper.isNull(composicao.getAttribute(Columns.CMA_VINCULO)) ? composicao.getAttribute(Columns.CMA_VINCULO).toString() : "";
                cmaQuantidade = !TextHelper.isNull(composicao.getAttribute(Columns.CMA_QUANTIDADE)) ? composicao.getAttribute(Columns.CMA_QUANTIDADE).toString() : "";
                vrsCodigo = !TextHelper.isNull(composicao.getAttribute(Columns.CMA_VRS_CODIGO)) ? composicao.getAttribute(Columns.CMA_VRS_CODIGO).toString() : "";
                crsCodigo = !TextHelper.isNull(composicao.getAttribute(Columns.CMA_CRS_CODIGO)) ? composicao.getAttribute(Columns.CMA_CRS_CODIGO).toString() : "";
                desconto = !TextHelper.isNull(composicao.getAttribute(Columns.CMA_VLR)) && ((BigDecimal) composicao.getAttribute(Columns.CMA_VLR)).compareTo(BigDecimal.ZERO) < 0 ? "1" : "";
            }
            List<TransferObject> vencimentos = servidorController.findVencimento(null, responsavel);
            List<TransferObject> vinculos = servidorController.selectVincRegistroServidor(false, responsavel);
            List<TransferObject> cargos = servidorController.lstCargo(responsavel);

            model.addAttribute("servidor", servidor);
            model.addAttribute("registroServidor", registroServidor);
            model.addAttribute("RSE_CODIGO", rseCodigo);
            model.addAttribute("CMA_CODIGO", cmaCodigo);
            model.addAttribute("vctCodigo", vctCodigo);
            model.addAttribute("cmaVlr", cmaVlr);
            model.addAttribute("cmaVinculo", cmaVinculo);
            model.addAttribute("cmaQuantidade", cmaQuantidade);
            model.addAttribute("vrsCodigo", vrsCodigo);
            model.addAttribute("crsCodigo", crsCodigo);
            model.addAttribute("desconto", desconto);
            model.addAttribute("vencimentos", vencimentos);
            model.addAttribute("vinculos", vinculos);
            model.addAttribute("cargos", cargos);

            return viewRedirect("jsp/editarServidor/editarComposicaoMargemServidor", request, session, model, responsavel);

        } catch (ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
            String cmaCodigo = JspHelper.verificaVarQryStr(request, "CMA_CODIGO");
            String vctCodigo = JspHelper.verificaVarQryStr(request, "vctCodigo");

            if (TextHelper.isNull(rseCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (TextHelper.isNull(vctCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.editar.composicao.margem.vencimento.obrigatorio", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (TextHelper.isNull(JspHelper.verificaVarQryStr(request, "cmaVlr"))) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.editar.composicao.margem.valor.obrigatorio", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            BigDecimal cmaVlr = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "cmaVlr")) ? new BigDecimal(String.valueOf(NumberHelper.parse(JspHelper.verificaVarQryStr(request, "cmaVlr"), NumberHelper.getLang()))) : BigDecimal.ZERO;

            // Se for desconto, o valor é cadastrado negativo
            String desconto = JspHelper.verificaVarQryStr(request, "desconto");
            if (desconto.equals("1") && cmaVlr.compareTo(BigDecimal.ZERO) > 0) {
                cmaVlr = cmaVlr.multiply(new BigDecimal(-1));
            }

            Integer cmaQuantidade = TextHelper.isNum(JspHelper.verificaVarQryStr(request, "cmaQuantidade")) ? Integer.valueOf(JspHelper.verificaVarQryStr(request, "cmaQuantidade")) : 1;

            TransferObject composicaoMargem = new CustomTransferObject();
            composicaoMargem.setAttribute(Columns.CMA_CODIGO, cmaCodigo);
            composicaoMargem.setAttribute(Columns.CMA_RSE_CODIGO, rseCodigo);
            composicaoMargem.setAttribute(Columns.CMA_VCT_CODIGO, vctCodigo);
            composicaoMargem.setAttribute(Columns.CMA_VRS_CODIGO, JspHelper.verificaVarQryStr(request, "vrsCodigo"));
            composicaoMargem.setAttribute(Columns.CMA_CRS_CODIGO, JspHelper.verificaVarQryStr(request, "crsCodigo"));
            composicaoMargem.setAttribute(Columns.CMA_VLR, cmaVlr);
            composicaoMargem.setAttribute(Columns.CMA_VINCULO, JspHelper.verificaVarQryStr(request, "cmaVinculo"));
            composicaoMargem.setAttribute(Columns.CMA_QUANTIDADE, cmaQuantidade);

            servidorController.editarComposicaoMargem(composicaoMargem, responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.editar.composicao.margem.alterado.sucesso", responsavel));

            // Repassa o token salvo, pois o método irá revalidar o token
            request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

            // Volta um elemento no topo da pilha para que ao voltar, o usuário caia no início da operação, caso usuário tenha selecionado servidor na listagem de servidores
            ParamSession paramSession = ParamSession.getParamSession(session);
            paramSession.back();

            return listar(request, response, session, model, rseCodigo);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
            String cmaCodigo = JspHelper.verificaVarQryStr(request, "CMA_CODIGO");

            if (TextHelper.isNull(rseCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (TextHelper.isNull(cmaCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            servidorController.excluirComposicaoMargem(cmaCodigo, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.editar.composicao.margem.removido.sucesso", responsavel));

            // Repassa o token salvo, pois o método irá revalidar o token
            request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

            // Volta um elemento no topo da pilha para que ao voltar, o usuário caia no início da operação, caso usuário tenha selecionado servidor na listagem de servidores
            ParamSession paramSession = ParamSession.getParamSession(session);
            paramSession.halfBack();

            return listar(request, response, session, model, rseCodigo);

        } catch (ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
