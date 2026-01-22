package com.zetra.econsig.web.controller.sdp;

import java.util.ArrayList;
import java.util.List;

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
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.exception.PlanoDescontoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.sdp.PlanoDescontoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.consignacao.AbstractConsultarConsignacaoWebController;

/**
 * <p>Title: ConsultarDespesaIndividualWebController</p>
 * <p>Description: Controlador Web para o caso de uso Consultar Despesa Individual.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/consultarDespesaIndividual" })
public class ConsultarDespesaIndividualWebController extends AbstractConsultarConsignacaoWebController {

    @Autowired
    private PlanoDescontoController planoDescontoController;

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Carregar a lista de consignatárias
        if (responsavel.isCseSupOrg()) {
            carregarListaConsignataria(request, session, model, responsavel);
        }

        // Carregr a lista de planos de desconto
        try {
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.CSA_CODIGO, responsavel.getCsaCodigo());
            criterio.setAttribute(Columns.PLA_ATIVO, CodedValues.STS_ATIVO);
            criterio.setAttribute(Columns.NPL_CODIGO, false); // evita listar planos de taxa de uso
            List<TransferObject> lstPlano = planoDescontoController.lstPlanoDescontoSemRateio(criterio, -1, -1, responsavel);
            model.addAttribute("lstPlano", lstPlano);
        } catch (PlanoDescontoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        return super.iniciar(request, response, session, model);
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.consultar.despesa.individual.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/consultarDespesaIndividual");
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona o editar consignação
        String link = "../v3/consultarDespesaIndividual?acao=detalharConsignacao";
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar.abreviado", responsavel);
        String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.consultar.despesa.clique.aqui", responsavel);
        String msgConfirmacao = "";

        acoes.add(new AcaoConsignacao("DETALHAR", CodedValues.FUN_CONS_CONSIGNACAO, descricao, "editar.gif", "btnConsultarConsignacao", msgAlternativa, msgConfirmacao, null, link, null));

        return acoes;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "cons_despesa_permissionario");
        return criterio;
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        return null;
    }
}
