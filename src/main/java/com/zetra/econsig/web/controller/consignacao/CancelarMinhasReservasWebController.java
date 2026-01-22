package com.zetra.econsig.web.controller.consignacao;

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
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: CancelarMinhasReservasWebController</p>
 * <p>Description: Controlador Web para o caso de uso CancelarMinhasReservas.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/cancelarMinhasReservas" })
public class CancelarMinhasReservasWebController extends CancelarReservaWebController {

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.cancelar.minhas.reservas.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/cancelarMinhasReservas");
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_SOLICITADO);
        sadCodigos.add(CodedValues.SAD_AGUARD_CONF);
        sadCodigos.add(CodedValues.SAD_AGUARD_DEFER);
        sadCodigos.add(CodedValues.SAD_AGUARD_MARGEM);
        return sadCodigos;
    }

    @Override
    @RequestMapping(params = { "acao=efetivarAcao" })
    public String efetivarAcao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String urlDestino = "../v3/cancelarMinhasReservas?acao=cancelar";
        String funCodigo = CodedValues.FUN_CANC_MINHAS_RESERVAS;

        if (!super.isExigeMotivoOperacao(funCodigo, responsavel)) {
            // Realiza um forward para passar pelo filtro de segurança e exigir segunda senha, caso habilitado
            return "forward:" + forwardUrl(urlDestino) + "&_skip_history_=true";
        } else {
            return super.informarMotivoOperacao(funCodigo, urlDestino, null, request, response, session, model);
        }
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona opção para liquidar consignação
        String link = "../v3/cancelarMinhasReservas?acao=efetivarAcao&opt=c";
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.cancelar.abreviado", responsavel);
        String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.cancelar.consignacao.clique.aqui", responsavel);
        String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.cancelamento", responsavel);
        String msgAdicionalConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.cancelamento.nao.reverter.renegociacao", responsavel);

        acoes.add(new AcaoConsignacao("CANC_MINHAS_RESERVAS", CodedValues.FUN_CANC_MINHAS_RESERVAS, descricao, "cancelar.gif", "btnCancelarConsignacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null));

        // Adiciona o editar consignação
        link = "../v3/cancelarMinhasReservas?acao=detalharConsignacao";
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
        criterio.setAttribute("TIPO_OPERACAO", "cancelarminhas");
        criterio.setAttribute("adePropria", true);

        return criterio;
    }

    @Override
    @RequestMapping(params = { "acao=cancelar" })
    public String cancelar(HttpServletRequest request, HttpServletResponse response, HttpSession session,Model model) {
        return super.cancelar(request, response, session, model);
    }
}
