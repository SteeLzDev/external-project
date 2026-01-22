package com.zetra.econsig.web.controller.termogarantiaaluguel;

import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.consignacao.AbstractIncluirConsignacaoWebController;

/**
 * <p>Title: IncluirTermoGarantiaAluguelWebController</p>
 * <p>Description: Controlador Web para o caso de uso termo de garantia de aluguel.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: rodrigo.rosa $
 * $Date: 2018-11-09 15:53:04 -0200 (sex, 09 nov 2018) $
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/incluirTermoGarantiaAluguel" })
public class IncluirTermoGarantiaAluguelWebController extends AbstractIncluirConsignacaoWebController {

    @Autowired
    private ConsignatariaController consignatariaController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        super.configurarPagina(request, session, model, responsavel);

        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.termo.garantia.aluguel.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/incluirTermoGarantiaAluguel");
        model.addAttribute("tipoOperacao", "reservar");
        model.addAttribute("imageHeader", "i-operacional");
    }

    @Override
    protected String getFunCodigo() {
        return CodedValues.FUN_INCLUIR_TERMO_GARANTIA_ALUGUEL;
    }

    @Override
    protected String validarServicoOperacao(String svcCodigo, String rseCodigo, Map<String, String> parametrosPlano, HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        // SERVIÇO DO TERMO DE GARANTIA DE PAGAMENTO DE ALUGUEL
        return "8D808080808080808080808080803A9D";
    }

    @Override
    protected String validarConsignatariaOperacao(String csaCodigo, String rseCodigo, HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ConsignatariaControllerException {
        // CSA_CODIGO DO EXÉRCITO BRASILEIRO
        csaCodigo = "0080808080808080808080808080F88D";
        ConsignatariaTransferObject consignataria = consignatariaController.findConsignataria(csaCodigo, responsavel);

        String csaNome = consignataria.getCsaIdentificador() + " - " + (!TextHelper.isNull(consignataria.getCsaNomeAbreviado()) ? consignataria.getCsaNomeAbreviado() : consignataria.getCsaNome());
        model.addAttribute("csaNome", csaNome);
        return csaCodigo;
    }

    @Override
    protected void carregarListaServico(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        // Método vazio para evitar de buscar lista de serviço na página para pesquisa do servidor
    }

    @Override
    protected void carregarListaConsignataria(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        // Método vazio para evitar de buscar lista de csas na página para pesquisa do servidor

    }

    @Override
    protected boolean possuiVariacaoMargem(AcessoSistema responsavel) {
        // Parâmetro para exibição de variação de margem
        return false;
    }

    @Override
    protected boolean possuiComposicaoMargem(AcessoSistema responsavel) {
        // Parametro que mostra a composição da margem do servidor na reserva.
        return false;
    }

    @Override
    protected boolean temControleCompulsorios(AcessoSistema responsavel) {
        // Parâmetro para controle de compulsórios
        return false;
    }

    @Override
    @RequestMapping(params = { "acao=listarHistLiquidacoesAntecipadas" })
    public String listarHistLiquidacoesAntecipadas(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return super.listarHistLiquidacoesAntecipadas(request, response, session, model);
    }

}
