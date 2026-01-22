package com.zetra.econsig.web.controller.consignacao;

import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.FaturamentoBeneficio;
import com.zetra.econsig.persistence.entity.FaturamentoBeneficioNf;
import com.zetra.econsig.persistence.entity.TipoNotaFiscal;
import com.zetra.econsig.service.beneficios.FaturamentoBeneficioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ManterNotaFiscalBeneficioWebController</p>
 * <p>Description: Controlador Web para o caso de uso Manter Nota Fiscal Beneficio.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/manterNotaFiscalBeneficio" })
public class ManterNotaFiscalBeneficioWebController extends ControlePaginacaoWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterNotaFiscalBeneficioWebController.class);

    @Autowired
    private FaturamentoBeneficioController faturamentoBeneficioController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            String fatCodigo = JspHelper.verificaVarQryStr(request, "faturamentoCodigo");
            if (TextHelper.isNull(fatCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            List<FaturamentoBeneficioNf> nfList = faturamentoBeneficioController.listarFaturamentoBeneficioNfPorIdFaturamentoBeneficio(fatCodigo, responsavel);

            model.addAttribute("nfList", nfList);

            return viewRedirect("jsp/manterNotaFiscalBeneficio/listarNotaFiscalBeneficio", request, session, model, responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessao para evitar a chamada direta da operacao
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            String fnfCodigo = JspHelper.verificaVarQryStr(request, "notaFiscalCodigo");
            faturamentoBeneficioController.excluirFaturamentoBeneficioNf(fnfCodigo, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.notafiscal.faturamento.beneficio.excluida.sucesso", responsavel));
            return iniciar(request, response, session, model);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessao para evitar a chamada direta da operacao
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {

            String notaFiscalCodigo = JspHelper.verificaVarQryStr(request, "notaFiscalCodigo");
            String fnfCodigo = JspHelper.verificaVarQryStr(request, "fnfCodigo");
            if (!TextHelper.isNull(notaFiscalCodigo) && TextHelper.isNull(fnfCodigo)) {

                FaturamentoBeneficioNf faturamentoBeneficioNf = faturamentoBeneficioController.findFaturamentoBeneficioNf(notaFiscalCodigo, responsavel);

                model.addAttribute("fnfCodigo", faturamentoBeneficioNf.getFnfCodigo());
                model.addAttribute("faturamentoCodigo", faturamentoBeneficioNf.getFaturamentoBeneficio().getFatCodigo());
                model.addAttribute("tipoNotaFiscal", faturamentoBeneficioNf.getTipoNotaFiscal().getTnfCodigo());
                model.addAttribute("codigoContrato", faturamentoBeneficioNf.getFnfCodigoContrato());
                model.addAttribute("numeroNf", faturamentoBeneficioNf.getFnfNumeroNf());
                model.addAttribute("numeroTitulo", faturamentoBeneficioNf.getFnfNumeroTitulo());
                model.addAttribute("valorIss", faturamentoBeneficioNf.getFnfValorIss() != null ? NumberHelper.reformat(faturamentoBeneficioNf.getFnfValorIss().toString(), "en", NumberHelper.getLang()) : "");
                model.addAttribute("valorIr", faturamentoBeneficioNf.getFnfValorIr() != null ? NumberHelper.reformat(faturamentoBeneficioNf.getFnfValorIr().toString(), "en", NumberHelper.getLang()) : "");
                model.addAttribute("pisCofins", faturamentoBeneficioNf.getFnfValorPisCofins() != null ? NumberHelper.reformat(faturamentoBeneficioNf.getFnfValorPisCofins().toString(), "en", NumberHelper.getLang()) : "");
                model.addAttribute("valorBruto", faturamentoBeneficioNf.getFnfValorBruto() != null ? NumberHelper.reformat(faturamentoBeneficioNf.getFnfValorBruto().toString(), "en", NumberHelper.getLang()) : "");
                model.addAttribute("valorLiquido", faturamentoBeneficioNf.getFnfValorLiquido() != null ? NumberHelper.reformat(faturamentoBeneficioNf.getFnfValorLiquido().toString(), "en", NumberHelper.getLang()) : "");
                model.addAttribute("dataVencimento", faturamentoBeneficioNf.getFnfDataVencimento() != null ? DateHelper.toDateString(faturamentoBeneficioNf.getFnfDataVencimento()) : "");

            }

            return viewRedirect("jsp/manterNotaFiscalBeneficio/editarNotaFiscalBeneficio", request, session, model, responsavel);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessao para evitar a chamada direta da operacao
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {

            String fnfCodigo = JspHelper.verificaVarQryStr(request, "fnfCodigo");
            String fatCodigo = JspHelper.verificaVarQryStr(request, "faturamentoCodigo");
            String tipoNotaFiscal = JspHelper.verificaVarQryStr(request, "tipoNotaFiscal");
            String codigoContrato = JspHelper.verificaVarQryStr(request, "codigoContrato");
            String numeroNf = JspHelper.verificaVarQryStr(request, "numeroNf");
            String numeroTitulo = JspHelper.verificaVarQryStr(request, "numeroTitulo");
            String valorIss = JspHelper.verificaVarQryStr(request, "valorIss");
            String valorIr = JspHelper.verificaVarQryStr(request, "valorIr");
            String pisCofins = JspHelper.verificaVarQryStr(request, "pisCofins");
            String valorBruto = JspHelper.verificaVarQryStr(request, "valorBruto");
            String valorLiquido = JspHelper.verificaVarQryStr(request, "valorLiquido");
            String dataVencimento = JspHelper.verificaVarQryStr(request, "dataVencimento");

            String validarCampos = validarCampos(fatCodigo, tipoNotaFiscal, codigoContrato, numeroNf, numeroTitulo, valorIss, valorIr,
                    pisCofins, valorBruto, valorLiquido, dataVencimento);
            if (!TextHelper.isNull(validarCampos)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(validarCampos, responsavel));
                return editar(request, response, session, model);
            } else {
                SynchronizerToken.saveToken(request);
            }

            FaturamentoBeneficio fat = new FaturamentoBeneficio(fatCodigo);

            FaturamentoBeneficioNf nf = new FaturamentoBeneficioNf();
            nf.setFnfCodigo(fnfCodigo);
            nf.setFaturamentoBeneficio(fat);
            nf.setTipoNotaFiscal(new TipoNotaFiscal(tipoNotaFiscal));
            nf.setFnfCodigoContrato(codigoContrato);

            nf.setFnfNumeroNf(numeroNf);
            nf.setFnfNumeroTitulo(numeroTitulo);

            nf.setFnfValorIss(NumberHelper.objectToBigDecimal(valorIss));
            nf.setFnfValorIr(NumberHelper.objectToBigDecimal(valorIr));
            nf.setFnfValorPisCofins(NumberHelper.objectToBigDecimal(pisCofins));
            nf.setFnfValorBruto(NumberHelper.objectToBigDecimal(valorBruto));
            nf.setFnfValorLiquido(NumberHelper.objectToBigDecimal(valorLiquido));

            nf.setFnfDataGeracao(new Date());
            nf.setFnfDataVencimento(DateHelper.objectToDate(dataVencimento));

            faturamentoBeneficioController.salvarFaturamentoBeneficioNf(nf, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.notafiscal.faturamento.beneficio.salvo.sucesso", responsavel));
            return iniciar(request, response, session, model);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    private String validarCampos(String fatCodigo, String tipoNotaFiscal, String codigoContrato, String numeroNf, String numeroTitulo, String valorIss, String valorIr, String pisCofins, String valorBruto, String valorLiquido, String dataVencimento) {

        if (TextHelper.isNull(fatCodigo)) {
            return "mensagem.erro.interno.contate.administrador";
        } else if (TextHelper.isNull(tipoNotaFiscal)) {
            return "mensagem.notafiscal.faturamento.beneficio.tipo.nota.fiscal.obrigatorio";
        } else if (TextHelper.isNull(codigoContrato)) {
            return "mensagem.notafiscal.faturamento.beneficio.codigo.contrato.obrigatorio";
        } else if (TextHelper.isNull(numeroNf)) {
            return "mensagem.notafiscal.faturamento.beneficio.numero.nf.obrigatorio";
        } else if (TextHelper.isNull(numeroTitulo)) {
            return "mensagem.notafiscal.faturamento.beneficio.numero.titulo.obrigatorio";
        } else if (TextHelper.isNull(dataVencimento)) {
            return "mensagem.notafiscal.faturamento.beneficio.data.vencimento.obrigatorio";
        } else if (TextHelper.isNull(valorIss)) {
            return "mensagem.notafiscal.faturamento.beneficio.valor.iss.obrigatorio";
        } else if (TextHelper.isNull(valorIr)) {
            return "mensagem.notafiscal.faturamento.beneficio.valor.ir.obrigatorio";
        } else if (TextHelper.isNull(pisCofins)) {
            return "mensagem.notafiscal.faturamento.beneficio.valor.pis.cofins.obrigatorio";
        } else if (TextHelper.isNull(valorBruto)) {
            return "mensagem.notafiscal.faturamento.beneficio.valor.bruto.obrigatorio";
        } else if (TextHelper.isNull(valorLiquido)) {
            return "mensagem.notafiscal.faturamento.beneficio.valor.liquido.obrigatorio";
        }

        return null;

    }

}
