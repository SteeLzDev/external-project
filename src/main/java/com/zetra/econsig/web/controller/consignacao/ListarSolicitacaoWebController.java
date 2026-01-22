package com.zetra.econsig.web.controller.consignacao;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.dto.web.ColunaListaConsignacao;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ListarSolicitacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso ListarSolicitacao.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarSolicitacao" })
public class ListarSolicitacaoWebController extends AbstractListarTodasConsignacoesWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarSolicitacaoWebController.class);

    @Autowired
    private SimulacaoController simulacaoController;

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (responsavel.isCseSup()) {
            carregarListaEstabelecimento(request, session, model, responsavel);
            carregarListaOrgao(request, session, model, responsavel);
        }
        if (responsavel.isCseSupOrg()) {
            carregarListaConsignataria(request, session, model, responsavel);
        }
        carregarListaServico(request, session, model, responsavel);

        // Habilita exibição de campo para filtro por data
        model.addAttribute("exibirFiltroDataInclusao", Boolean.TRUE);
        // Habilita opção de listar todos os registros
        model.addAttribute("exibirOpcaoListarTodos", Boolean.TRUE);

        if (ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_ASSINATURA_DIGITAL_CONSIGNACAO, responsavel)
             && responsavel.isCsa() && responsavel.temPermissao(CodedValues.FUN_APROV_REJCT_ANEXO_SOLICITACAO)) {
            // Habilita opção de filtrar solicitações com anexos pendentes de validação
            model.addAttribute("filtrarSolicAnexoPendenteValidacao", Boolean.TRUE);
        }

        return super.iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=aprovarDocumentacao" })
    public String aprovarDocumentacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            String adeCodigo = request.getParameter("ADE_CODIGO");
            List<String> tisCodigos = new ArrayList<>();
            tisCodigos.add(TipoSolicitacaoEnum.SOLICITACAO_CONSIGNACAO_CREDITO_ELETRONICO.getCodigo());
            List<String> ssoCodigos = new ArrayList<>();
            ssoCodigos.add(StatusSolicitacaoEnum.PENDENTE_VALIDACAO_DOCUMENTOS.getCodigo());

            simulacaoController.aprovarAnexosSolicitacaoAutorizacao(adeCodigo, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.simulacao.anexos.aprovados", responsavel));

            String telaEdicao = request.getParameter("telaEdicao");

            ParamSession paramSession = ParamSession.getParamSession(session);
            if (!TextHelper.isNull(telaEdicao) && telaEdicao.equals("true")) {
                paramSession.halfBack();
                return detalharConsignacao(adeCodigo, request, response, session, model);
            } else {
                String rseCodigo = !TextHelper.isNull(request.getParameter("RSE_CODIGO")) ? request.getParameter("RSE_CODIGO") : null;
                String adeNumero = !TextHelper.isNull(request.getParameter("ADE_NUMERO")) ? request.getParameter("ADE_NUMERO") : null;
                paramSession.halfBack();
                String pgResultante = pesquisarConsignacao(rseCodigo, adeNumero, request, response, session, model);

                if (pgResultante.equals("jsp/consultarServidor/pesquisarServidor") || pgResultante.equals("jsp/redirecionador/redirecionar")) {
                    session.removeAttribute(CodedValues.MSG_ERRO);
                }

                return pgResultante;
            }

        } catch (SimulacaoControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=reprovarDocumentacao" })
    public String reprovarDocumentacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            String adeCodigo = request.getParameter("ADE_CODIGO");
            String obsReprovacao = request.getParameter("obs_reprovacao");
            List<String> tisCodigos = new ArrayList<>();
            tisCodigos.add(TipoSolicitacaoEnum.SOLICITACAO_CONSIGNACAO_CREDITO_ELETRONICO.getCodigo());
            List<String> ssoCodigos = new ArrayList<>();
            ssoCodigos.add(StatusSolicitacaoEnum.PENDENTE_VALIDACAO_DOCUMENTOS.getCodigo());

            simulacaoController.reprovarAnexosSolicitacaoAutorizacao(adeCodigo, obsReprovacao, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.simulacao.anexos.reprovados", responsavel));

            String telaEdicao = request.getParameter("telaEdicao");

            ParamSession paramSession = ParamSession.getParamSession(session);
            if (!TextHelper.isNull(telaEdicao) && telaEdicao.equals("true")) {
                paramSession.halfBack();
                return detalharConsignacao(adeCodigo, request, response, session, model);
            } else {
                String rseCodigo = !TextHelper.isNull(request.getParameter("RSE_CODIGO")) ? request.getParameter("RSE_CODIGO") : null;
                String adeNumero = !TextHelper.isNull(request.getParameter("ADE_NUMERO")) ? request.getParameter("ADE_NUMERO") : null;
                paramSession.halfBack();
                String pgResultante = pesquisarConsignacao(rseCodigo, adeNumero, request, response, session, model);

                if (pgResultante.equals("jsp/consultarServidor/pesquisarServidor") || pgResultante.equals("jsp/redirecionador/redirecionar")) {
                    session.removeAttribute(CodedValues.MSG_ERRO);
                }

                return pgResultante;
            }

        } catch (SimulacaoControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.listar.solicitacao.consignacao.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/listarSolicitacao");
        model.addAttribute("imageHeader", "i-operacional");
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_SOLICITADO);
        return sadCodigos;
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        List<AcaoConsignacao> acoes = new ArrayList<>();

        if (responsavel.temPermissao(CodedValues.FUN_CONF_SOLICITACAO)) {
            // Adiciona opção para liquidar consignação
            String link = "../v3/confirmarSolicitacao?acao=iniciar";
            String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.confirmar.abreviado", responsavel);
            String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.confirmar", responsavel);
            String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.confirmar.solicitacao.clique.aqui", responsavel);
            String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.solicitacao", responsavel);
            String msgAdicionalConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.informacao.valor.parcela.maior.margem.tratamento.especial", responsavel);

            acoes.add(new AcaoConsignacao("CONF_SOLICITACAO", CodedValues.FUN_CONF_SOLICITACAO, descricao, descricaoCompleta, "confirmar_margem.gif", "btnConfirmarSolicitacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null, null));
        }

        // Adiciona o editar consignação
        String link = "../v3/listarSolicitacao?acao=detalharConsignacao";
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar.abreviado", responsavel);
        String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar", responsavel);
        String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.clique.aqui", responsavel);
        String msgConfirmacao = "";

        acoes.add(new AcaoConsignacao("DETALHAR", CodedValues.FUN_CONS_CONSIGNACAO, descricao, descricaoCompleta, "editar.gif", "btnConsultarConsignacao", msgAlternativa, msgConfirmacao, null, link, null,null));

        if (ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_ASSINATURA_DIGITAL_CONSIGNACAO, responsavel)
                && responsavel.isCsa() && responsavel.temPermissao(CodedValues.FUN_APROV_REJCT_ANEXO_SOLICITACAO)) {
            Object vlrRequestAnexoPenValid = JspHelper.verificaVarQryStr(request, "temAnexoPendenteValidacao");
            boolean filtroTemAnexoPendenteValidacao = (!TextHelper.isNull(vlrRequestAnexoPenValid) && vlrRequestAnexoPenValid.equals("true"));

            if (filtroTemAnexoPendenteValidacao) {
                link = "../v3/listarSolicitacao?acao=aprovarDocumentacao";
                descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.aprovar.documentacao.abreviado", responsavel);
                msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.aprovar.documentacao.solicitacao.clique.aqui", responsavel);
                msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.aprovar.documentacao.solicitacao.confirma", responsavel);

                acoes.add(new AcaoConsignacao("APROVAR_ANEXO_CONSIGNACAO", CodedValues.FUN_APROV_REJCT_ANEXO_SOLICITACAO, descricao, "attachment_approve.png", "btnAprvAnexosSolicitacao", msgAlternativa, msgConfirmacao, null, link, null));

                link = "../v3/listarSolicitacao?acao=reprovarDocumentacao";
                descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.reprovar.documentacao.abreviado", responsavel);
                msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.reprovar.documentacao.solicitacao.clique.aqui", responsavel);
                msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.reprovar.documentacao.solicitacao.confirma", responsavel);

                acoes.add(new AcaoConsignacao("REPROVAR_ANEXO_CONSIGNACAO", CodedValues.FUN_APROV_REJCT_ANEXO_SOLICITACAO, descricao, "attachment_disapprove.png", "btnReprovarAnexoConsignacao", msgAlternativa, msgConfirmacao, null, link, null));
            }
        }

        return acoes;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "solicitacao");

        criterio.setAttribute(Columns.EST_CODIGO, JspHelper.verificaVarQryStr(request, "EST_CODIGO"));
        criterio.setAttribute(Columns.ORG_CODIGO, JspHelper.verificaVarQryStr(request, "ORG_CODIGO"));
        criterio.setAttribute(Columns.SVC_CODIGO, JspHelper.verificaVarQryStr(request, "SVC_CODIGO"));
        criterio.setAttribute(Columns.CSA_CODIGO, (responsavel.isCsaCor()) ? responsavel.getCsaCodigo() : JspHelper.verificaVarQryStr(request, "CSA_CODIGO"));

        if (ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_ASSINATURA_DIGITAL_CONSIGNACAO, responsavel)
            && responsavel.isCsa() && responsavel.temPermissao(CodedValues.FUN_APROV_REJCT_ANEXO_SOLICITACAO)) {
            Object vlrRequestAnexoPenValid = JspHelper.verificaVarQryStr(request, "temAnexoPendenteValidacao");
            boolean temAnexoPendenteValidacao = (!TextHelper.isNull(vlrRequestAnexoPenValid) && vlrRequestAnexoPenValid.equals("true"));
            criterio.setAttribute("temAnexoPendenteValidacao", temAnexoPendenteValidacao);
        }

        try {
            String periodoIni = JspHelper.verificaVarQryStr(request, "periodoIni");
            if (!periodoIni.equals("") ) {
                periodoIni = DateHelper.reformat(periodoIni, LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
                criterio.setAttribute("periodoIni", periodoIni);
            }
        } catch (ParseException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        try {
            String periodoFim = JspHelper.verificaVarQryStr(request, "periodoFim");
            if (!periodoFim.equals("")) {
                periodoFim = DateHelper.reformat(periodoFim, LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59");
                criterio.setAttribute("periodoFim", periodoFim);
            }
        } catch (ParseException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return criterio;
    }

    @Override
    protected List<ColunaListaConsignacao> definirColunasListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        boolean resultadoMultiplosServidores = (request.getAttribute("resultadoMultiplosServidores") != null);

        List<ColunaListaConsignacao> colunas = new ArrayList<>();

        try {
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_CONSIGNATARIA, responsavel) && !responsavel.isCsaCor()) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_CONSIGNATARIA, ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel)));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_NUMERO, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_NUMERO, ApplicationResourcesHelper.getMessage("rotulo.consignacao.numero", responsavel), ColunaListaConsignacao.TipoValor.NUMERICO));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_SERVICO, responsavel) && responsavel.isCseSupOrg()) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_SERVICO, ApplicationResourcesHelper.getMessage("rotulo.servico.singular", responsavel)));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_SERVIDOR, responsavel) && resultadoMultiplosServidores) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_SERVIDOR, ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel)));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_CPF, responsavel) && responsavel.isCsaCor()) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_CPF, ApplicationResourcesHelper.getMessage("rotulo.servidor.cpf", responsavel)));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_TELEFONE, responsavel) && ParamSist.paramEquals(CodedValues.TPC_REQUER_TEL_SER_SOLIC_EMPRESTIMO, CodedValues.TPC_SIM, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_TELEFONE, ApplicationResourcesHelper.getMessage("rotulo.servidor.telefone", responsavel)));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_DATA_RESERVA, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_DATA_RESERVA, ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.inclusao", responsavel), ColunaListaConsignacao.TipoValor.DATA));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA, ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela.abreviado", responsavel), ColunaListaConsignacao.TipoValor.MONETARIO));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_PRAZO, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_PRAZO, ApplicationResourcesHelper.getMessage("rotulo.consignacao.prazo.abreviado", responsavel), ColunaListaConsignacao.TipoValor.NUMERICO));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_RISCO_CSA, responsavel) && ParamSist.paramEquals(CodedValues.TPC_HABILITA_RISCO_SERVIDOR_CSA, CodedValues.TPC_SIM, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_RISCO_CSA, ApplicationResourcesHelper.getMessage("rotulo.servidor.risco.csa", responsavel)));
            }
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return colunas;
    }

    @Override
    protected String getQueryString(List<String> requestParams, HttpServletRequest request) {
        StringBuilder linkListBuild = new StringBuilder();

        // Concatena os parâmetros de request
        if (requestParams != null && !requestParams.isEmpty()) {
            for (String param: requestParams) {
                // Este parâmetro não deve ser incluídos na query de pesquisa da listagem de solicitação
                if (param.equals("ADE_CODIGO")) {
                    continue;
                }
                String[] paramValues = request.getParameterValues(param);
                if (paramValues != null && paramValues.length > 0) {
                    for (String paramValue : paramValues) {
                        if (!TextHelper.isNull(paramValue)) {
                            linkListBuild.append("&").append(param).append("=").append(TextHelper.forUriComponent(paramValue));
                        }
                    }
                }
            }
        }
        // Remove o primeiro "&"
        if (linkListBuild.length() > 0) {
            linkListBuild.deleteCharAt(0);
        }

        return linkListBuild.toString();
    }

}
