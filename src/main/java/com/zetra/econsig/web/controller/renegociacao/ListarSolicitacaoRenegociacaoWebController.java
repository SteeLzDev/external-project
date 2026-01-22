package com.zetra.econsig.web.controller.renegociacao;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.consignacao.AbstractListarTodasConsignacoesWebController;

/**
 * <p>Title: ListarSolicitacaoRenegociacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso ListarSolicitacaoRenegociacao.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarSolicitacaoRenegociacao" })
public class ListarSolicitacaoRenegociacaoWebController extends AbstractListarTodasConsignacoesWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarSolicitacaoRenegociacaoWebController.class);

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

        return super.iniciar(request, response, session, model);
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.listar.solicitacao.renegociacao.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/listarSolicitacaoRenegociacao");
        model.addAttribute("imageHeader", "i-operacional");
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_AGUARD_CONF);
        return sadCodigos;
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona opção para liquidar consignação
        String link = "../v3/confirmarConsignacao?acao=efetivarAcao";
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.confirmar.abreviado", responsavel);
        String msgAlternativa = "";
        String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.multiplo.confirmar", responsavel);
        String msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("LISTAR_SOLICITACAO_RENEGOCIACAO", CodedValues.FUN_LISTAR_SOLICITACAO_RENEGOCIACAO, descricao, "confirmar_margem.gif", "btnConfirmarReserva", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, "chkConfirmar"));

        // Adiciona o editar consignação
        link = "../v3/listarSolicitacaoRenegociacao?acao=detalharConsignacao";
        descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar.abreviado", responsavel);
        msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.clique.aqui", responsavel);
        msgConfirmacao = "";
        msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("DETALHAR", CodedValues.FUN_CONS_CONSIGNACAO, descricao, "editar.gif", "btnConsultarConsignacao", msgAlternativa, msgConfirmacao, null, link, null));

        return acoes;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "solicitacao_renegociacao");

        criterio.setAttribute(Columns.EST_CODIGO, JspHelper.verificaVarQryStr(request, "EST_CODIGO"));
        criterio.setAttribute(Columns.ORG_CODIGO, JspHelper.verificaVarQryStr(request, "ORG_CODIGO"));
        criterio.setAttribute(Columns.SVC_CODIGO, JspHelper.verificaVarQryStr(request, "SVC_CODIGO"));
        criterio.setAttribute(Columns.CSA_CODIGO, (responsavel.isCsaCor()) ? responsavel.getCsaCodigo() : JspHelper.verificaVarQryStr(request, "CSA_CODIGO"));

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
}
