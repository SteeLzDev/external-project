package com.zetra.econsig.web.controller.decisaojudicial;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.web.Servico;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.servidor.AbstractConsultarServidorWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ExecutarDecisaoJudicialWebController</p>
 * <p>Description: Web Controller Principal para caso de uso de Decisão Judicial</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/executarDecisaoJudicial" })
public class ExecutarDecisaoJudicialWebController extends AbstractConsultarServidorWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ExecutarDecisaoJudicialWebController.class);

    @Autowired
    private ConvenioController convenioController;

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (responsavel.isCseSupOrg()) {
            carregarListaConsignataria(request, session, model, responsavel);
        }

        final boolean listagemDinamicaDeServicos = responsavel.isCseSupOrg();
        model.addAttribute("listagemDinamicaDeServicos", listagemDinamicaDeServicos);
        if (listagemDinamicaDeServicos) {
            model.addAttribute("lstServico", new ArrayList<>());
        } else {
            carregarListaServico(request, session, model, responsavel);
        }
        return super.iniciar(request, response, session, model);
    }
    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.decisao.judicial.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/executarDecisaoJudicial");
        model.addAttribute("exibirTipoDecisaoJudicial", true);
        model.addAttribute("imageHeader", "i-operacional");

        // DESENV-14216 -> Inclusão de mensagem no módulo de decisão judicial
        session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.decisao.judicial.aviso.usuario", responsavel));
    }

    @Override
    protected String continuarOperacao(String rseCodigo, String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model){
        return redirecionar(rseCodigo, request, response, session, model);
    }

    @Override
    protected String definirProximaOperacao(HttpServletRequest request, AcessoSistema responsavel) {
        return "redirecionar";
    }

    @RequestMapping(params = { "acao=redirecionar" })
    public String redirecionar(@RequestParam(value = "RSE_CODIGO", required = false, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        // Repassa o token salvo, pois o método irá revalidar o token
        request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

        final String param= !TextHelper.isNull(rseCodigo) ? "&RSE_CODIGO=" + rseCodigo : "";

        final String tipoDecisaoJudicial = JspHelper.verificaVarQryStr(request, "tipoDecisaoJudicial");
        if (CodedValues.DECISAO_JUDICIAL_OPCAO_PENSAO_JUDICIAL.equals(tipoDecisaoJudicial)) {
            return "forward:/v3/executarPensaoJudicial?acao=iniciar" + param + "&_skip_history_=true";

        } else if (CodedValues.DECISAO_JUDICIAL_OPCAO_EXCLUIR_CONSIGNACAO.equals(tipoDecisaoJudicial)) {
            return "forward:/v3/executarExclusaoJudicial?acao=pesquisarConsignacao" + param + "&_skip_history_=true";

        } else if (CodedValues.DECISAO_JUDICIAL_OPCAO_ADEQUACAO_MARGEM.equals(tipoDecisaoJudicial)) {
            return "forward:/v3/executarAdequacaoMargem?acao=pesquisarConsignacao" + param + "&_skip_history_=true";

        } else if (CodedValues.DECISAO_JUDICIAL_OPCAO_ALTERAR_CONSIGNACAO.equals(tipoDecisaoJudicial)) {
            return "forward:/v3/executarAlteracaoJudicial?acao=pesquisarConsignacao" + param + "&_skip_history_=true";

        } else if (CodedValues.DECISAO_JUDICIAL_OPCAO_REATIVAR_CONSIGNACAO.equals(tipoDecisaoJudicial)) {
            return "forward:/v3/executarReativacaoJudicial?acao=pesquisarConsignacao" + param + "&_skip_history_=true";

        } else if (CodedValues.DECISAO_JUDICIAL_OPCAO_AUTORIZAR_CONSIGNACAO.equals(tipoDecisaoJudicial)) {
            return "forward:/v3/executarAutorizarDescontoDecisaoJudicial?acao=pesquisarConsignacao" + param + "&_skip_history_=true";

        } else if (CodedValues.DECISAO_JUDICIAL_OPCAO_INCLUIR_CONSIGNACAO.equals(tipoDecisaoJudicial)) {
            return "forward:/v3/executarInclusaoJudicial?acao=reservarMargem" + param + "&inclusaoJudicial=true&_skip_history_=true";
        }

        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
    }

    @Override
    protected String tratarSevidorNaoEncontrado(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        // Retorna para operação de pesquisar de servidor
        return super.iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=listarServicos" })
    public @ResponseBody List<Servico> listarServico(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final List<Servico> lstServico = new ArrayList<>();
        try {
            String codigo = responsavel.getCodigoEntidade();
            String tipo = responsavel.getTipoEntidade();
            final String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
            if (!TextHelper.isNull(csaCodigo)) {
                codigo = csaCodigo;
                tipo = AcessoSistema.ENTIDADE_CSA;
            }
            final List<TransferObject> lstConvenio = convenioController.lstCnvEntidade(codigo, tipo, "reservar", responsavel);
            final List<TransferObject> lstServicoTO = TextHelper.groupConcat(lstConvenio, new String[] { Columns.SVC_DESCRICAO, Columns.SVC_CODIGO }, new String[] { Columns.CNV_COD_VERBA }, ",", true, true);
            for (final TransferObject to : lstServicoTO) {
                final Servico servico = new Servico();
                servico.setSvcCodigo((String) to.getAttribute(Columns.SVC_CODIGO));
                servico.setSvcIdentificador((String) to.getAttribute(Columns.SVC_IDENTIFICADOR));
                servico.setSvcDescricao((String) to.getAttribute(Columns.SVC_DESCRICAO));
                lstServico.add(servico);
            }
        } catch (final ConvenioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return lstServico;
    }
}
