package com.zetra.econsig.web.controller.consignacao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.seguranca.AcessoFuncaoServico;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.GeraTelaSegundaSenhaHelper;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RegistrarOcorrenciaConsignacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso RegistrarOcorrenciaConsignacao.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/registrarOcorrenciaConsignacao" })
public class RegistrarOcorrenciaConsignacaoWebController extends AbstractConsultarConsignacaoWebController {

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private SistemaController sistemaController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.registrar.ocorrencia.consignacao.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/registrarOcorrenciaConsignacao");
        model.addAttribute("imageHeader", "i-operacional");
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        List<String> sadCodigos = new ArrayList<>();
        return sadCodigos;
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona opção para liquidar consignação
        String link = "../v3/registrarOcorrenciaConsignacao?acao=iniciarRegistro";
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.registar.ocorrencia.abreviado", responsavel);
        String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar", responsavel);
        String msgAlternativa = "";
        String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.multiplo.registrar.ocorrencia", responsavel);
        String msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("REGISTRAR_OCO_CONSIGNACAO", CodedValues.FUN_REGISTRAR_OCO_CONSIGNACAO, descricao, descricaoCompleta, "table_row_insert.png", "btnRegistrarOcorrencia", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null,"chkOcorrencia"));

        // Adiciona o editar consignação
        link = "../v3/registrarOcorrenciaConsignacao?acao=detalharConsignacao";
        descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar.abreviado", responsavel);
        descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar", responsavel);
        msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.clique.aqui", responsavel);
        msgConfirmacao = "";
        msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("DETALHAR", CodedValues.FUN_CONS_CONSIGNACAO, descricao, descricaoCompleta, "editar.gif", "btnConsultarConsignacao", msgAlternativa, msgConfirmacao, null, link, null, null));

        return acoes;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "ocorrencia_consignacao");
        return criterio;
    }

    @RequestMapping(params = { "acao=iniciarRegistro" })
    public String iniciarRegistro(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return iniciarRegistroOcorrencia(false, request, response, session, model);
    }

    @RequestMapping(params = { "acao=iniciarRegistroLeilao" })
    public String iniciarRegistroLeilao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return iniciarRegistroOcorrencia(true, request, response, session, model);
    }

    private String iniciarRegistroOcorrencia(boolean isMotivoLeilao, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            String[] adeCodigos = request.getParameterValues("chkOcorrencia");
            if (request.getParameter("ADE_CODIGO") != null && adeCodigos == null) {
                adeCodigos = new String[1];
                adeCodigos[0] = request.getParameter("ADE_CODIGO").toString();
            }

            if (adeCodigos == null) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            CustomTransferObject autdes = null;
            List<CustomTransferObject> autdesList = new ArrayList<>();
            Set<String> svcCodigoSet = new HashSet<>();
            for (String adeCodigo : adeCodigos) {
                autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
                autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
                autdesList.add(autdes);
                svcCodigoSet.add(autdes.getAttribute(Columns.SVC_CODIGO).toString());
            }

            for (String svcCodigo : svcCodigoSet) {
                if (!AcessoFuncaoServico.temAcessoFuncao(request, CodedValues.FUN_REGISTRAR_OCO_CONSIGNACAO, responsavel.getUsuCodigo(), svcCodigo)) {
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            List<String> tocCodigos = new ArrayList<>();
            if (isMotivoLeilao) {
                tocCodigos.add(CodedValues.TOC_MOTIVO_NAO_CONCRETIZACAO_LEILAO);
            } else {
                tocCodigos.add(CodedValues.TOC_AVISO);
                tocCodigos.add(CodedValues.TOC_ERRO);
                tocCodigos.add(CodedValues.TOC_INFORMACAO);
            }

            model.addAttribute("autdesList", autdesList);
            model.addAttribute("isMotivoLeilao", isMotivoLeilao);

            List<TransferObject> tipoOcorrencia = sistemaController.lstTipoOcorrencia(tocCodigos, responsavel);
            model.addAttribute("tipoOcorrencia", tipoOcorrencia);

        } catch (AutorizacaoControllerException | ConsignanteControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/registrarOcorrencia/registrarOcorrencia", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=finalizarRegistro" })
    public String finalizarRegistro(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return finalizarRegistroOcorrencia(false, request, response, session, model);
    }

    @RequestMapping(params = { "acao=finalizarRegistroLeilao" })
    public String finalizarRegistroLeilao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return finalizarRegistroOcorrencia(true, request, response, session, model);
    }

    private String finalizarRegistroOcorrencia(boolean isMotivoLeilao, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            String[] adeCodigos = request.getParameterValues("chkOcorrencia");
            String tocCodigo = JspHelper.verificaVarQryStr(request, "tocCodigo");
            String ocaObs = JspHelper.verificaVarQryStr(request, "ocaObs");
            for (String adeCodigo : adeCodigos) {
                autorizacaoController.criaOcorrenciaADEValidando(adeCodigo, tocCodigo, ocaObs, responsavel);
                if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                    autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO,
                            (String) session.getAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA),
                            (AcessoSistema) session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA));
                }
            }
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.registrar.ocorrencia.consignacao.concluido.sucesso", responsavel));

            if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                session.removeAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA);
                session.removeAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA);
            }

            ParamSession paramSession = ParamSession.getParamSession(session);
            paramSession.halfBack();
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.registrar.ocorrencia.consignacao", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
