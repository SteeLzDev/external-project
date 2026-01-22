package com.zetra.econsig.web.controller.decisaojudicial;

import java.math.BigDecimal;
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

import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: ExecutarPensaoDecisaoJudicialWebController</p>
 * <p>Description: Web Controller Principal para caso de uso de Decisão Judicial - Pensão Judicial</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/executarPensaoJudicial" })
public class ExecutarPensaoDecisaoJudicialWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ExecutarPensaoDecisaoJudicialWebController.class);

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.decisao.judicial.opcao.pensao.judicial", responsavel));
        model.addAttribute("acaoFormulario", "../v3/executarPensaoJudicial");
    }

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciarPensaoJudicial(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            RegistroServidorTO registroServidor = servidorController.findRegistroServidor(rseCodigo, true, responsavel);
            model.addAttribute("registroServidor", registroServidor);

            ServidorTransferObject servidor = servidorController.findServidor(registroServidor.getSerCodigo(), responsavel);
            model.addAttribute("servidor", servidor);

            List<MargemTO> margens = consultarMargemController.consultarMargem(rseCodigo, null, null, null, true, false, responsavel);
            model.addAttribute("margens", margens);

            String tituloResultado = null;
            if (ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                tituloResultado = registroServidor.getRseMatricula() + " - " + servidor.getSerNome();
            } else {
                tituloResultado = registroServidor.getRseMatricula() + " - " + servidor.getSerCpf() + " - " + servidor.getSerNome();
            }

            model.addAttribute("tituloResultado", tituloResultado);

            return viewRedirect("jsp/executarDecisaoJudicial/executarPensaoJudicial", request, session, model, responsavel);
        } catch (ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvarPensaoJudicial(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            List<MargemTO> margens = consultarMargemController.consultarMargem(rseCodigo, null, null, null, true, false, responsavel);
            for (MargemTO margem : margens) {
                String nomeCampo = "margem_" +  margem.getMarCodigo().toString();
                String margemNovaTxt = JspHelper.verificaVarQryStr(request, nomeCampo);
                if (TextHelper.isNull(margemNovaTxt) || NumberHelper.parseDecimal(margemNovaTxt) == null) {
                    throw new ZetraException("mensagem.erro.valor.margem.incorreto", responsavel);
                }
                BigDecimal margemNova = NumberHelper.parseDecimal(margemNovaTxt);
                BigDecimal margemAntiga = margem.getMrsMargem();
                if (margemNova.compareTo(margemAntiga) > 0) {
                    throw new ZetraException("mensagem.erro.valor.margem.maior.atual", responsavel);
                }
                margem.setMrsMargem(margemNova);
            }

            String compulsorio = JspHelper.verificaVarQryStr(request, "compulsorio");
            String tmoCodigo = JspHelper.verificaVarQryStr(request, "TMO_CODIGO");
            String obsMotivoOperacao = JspHelper.verificaVarQryStr(request, "ADE_OBS");

            // Chama rotina para atualização das margens
            servidorController.updateMargensRegistroServidor(rseCodigo, margens,compulsorio, tmoCodigo, obsMotivoOperacao, responsavel);

            // Seta mensagem de suceso
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.decisao.judicial.opcao.pensao.judicial.sucesso", responsavel));

            // Retorna ao passo anterior
            ParamSession paramSession = ParamSession.getParamSession(session);
            paramSession.halfBack();
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
