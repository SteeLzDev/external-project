package com.zetra.econsig.web.controller.consignacao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.persistence.entity.DadosAutorizacaoDesconto;
import com.zetra.econsig.persistence.entity.DadosServidor;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: AutorizarConsignacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso AutorizarConsignacao.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST, RequestMethod.GET }, value = { "/v3/autorizarConsignacaoPorVerba" })
public class AutorizarConsignacaoPorVerbaWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AutorizarConsignacaoPorVerbaWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.autorizar.ade.por.verba.titulo", responsavel));
    }

    @RequestMapping(params = { "acao=iniciar" })
    public String pesquisarConsignacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            final String rseCodigo = responsavel.getRseCodigo();
            final TransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
            final String confirmouAutorizacao = autorizacaoController.getValorDadoServidor(responsavel.getSerCodigo(), CodedValues.TDA_CONFIRMOU_AUTORIZACAO_DESCONTO, responsavel);

            final List<TransferObject> lstConsignacao = buscaAdes(responsavel);
            if (lstConsignacao == null || lstConsignacao.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.contrato.autorizacao.ade.por.verba.nao.encontrado", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            model.addAttribute("servidor", servidor);
            model.addAttribute("lstConsignacao", lstConsignacao);
            

            if (TextHelper.isNull(confirmouAutorizacao)) {
                model.addAttribute("confirmouAutorizacao", Boolean.FALSE);
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.info.instrucoes.confirmar.autorizacao", responsavel));
               
            } else {
                model.addAttribute("confirmouAutorizacao", Boolean.TRUE);
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.info.instrucoes.revisar.autorizacao", responsavel));
            }

            // Redireciona para a página de listagem
            return viewRedirect("jsp/autorizarConsignacaoPorVerba/autorizarConsignacaoPorVerba", request, session, model, responsavel);
        } catch (AutorizacaoControllerException | ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @PostMapping(params = { "acao=autorizar" })
    public String autorizarConsignacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String aceiteTermo = JspHelper.verificaVarQryStr(request, "aceiteTermo");
        if (TextHelper.isNull(aceiteTermo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            final String rseCodigo = responsavel.getRseCodigo();
            final String serCodigo = responsavel.getSerCodigo();

            final DadosServidor dadosServidor = new DadosServidor();
            dadosServidor.setDasValor(CodedValues.TDA_SIM);
            dadosServidor.setSerCodigo(serCodigo);
            dadosServidor.setTdaCodigo(CodedValues.TDA_CONFIRMOU_AUTORIZACAO_DESCONTO);

            final List<TransferObject> lstConsignacao = buscaAdes(responsavel);
            final List<DadosAutorizacaoDesconto> listaDadosAde = new ArrayList<>();

            for (final TransferObject ade : lstConsignacao) {
                final String adeCodigo = ade.getAttribute(Columns.ADE_CODIGO).toString();
                final String radioValue = JspHelper.verificaVarQryStr(request, "rdb_" + adeCodigo);

                final DadosAutorizacaoDesconto dadosAutorizacaoDesconto = new DadosAutorizacaoDesconto();
                dadosAutorizacaoDesconto.setAdeCodigo(adeCodigo);
                dadosAutorizacaoDesconto.setTdaCodigo(CodedValues.TDA_AUTORIZA_DESCONTO);
                dadosAutorizacaoDesconto.setDadValor(radioValue);

                listaDadosAde.add(dadosAutorizacaoDesconto);
            }

            // Salva os dados adicionais de autorização das consignações conforme marcação dada pelo servidor
            autorizacaoController.salvarDadosAutorizacaoConsignacao(dadosServidor, listaDadosAde, responsavel);

            final TransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
            final String rseMatricula = (String) servidor.getAttribute(Columns.RSE_MATRICULA);
            final String serNome = (String) servidor.getAttribute(Columns.SER_NOME);
            final String serCpf = (String) servidor.getAttribute(Columns.SER_CPF);
            final String orgIdentificador = (String) servidor.getAttribute(Columns.ORG_IDENTIFICADOR);
            final String orgNome = (String) servidor.getAttribute(Columns.ORG_NOME);
            final String estIdentificador = (String) servidor.getAttribute(Columns.EST_IDENTIFICADOR);
            final String estNome = (String) servidor.getAttribute(Columns.EST_NOME);

            // Envia email para o cse (CSE_EMAIL_FOLHA) com as escolhas feitas pelo servidor
            EnviaEmailHelper.enviarEmailAutorizarConsignacao(rseMatricula + " - " + serNome, estIdentificador + " - " + estNome ,
                    orgIdentificador + " - " + orgNome, serCpf, geraHtml(responsavel), responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.info.autorizar.nao.esqueca.impressao", responsavel));
        } catch (AutorizacaoControllerException | ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return pesquisarConsignacao(request, response, session, model);
    }

    private String geraHtml(AcessoSistema responsavel) throws AutorizacaoControllerException {
        final List<TransferObject> lstConsignacao = buscaAdes(responsavel);

        // Inicia geração do código HTML
        final StringBuilder code = new StringBuilder();

        code.append("<table cellpadding=\"2\" cellspacing=\"1\">");
        code.append("<thead>");
        code.append("<tr>");
        code.append("<th align=\"center\">").append(ApplicationResourcesHelper.getMessage("rotulo.tabela.autorizar.consignacao.aceito", responsavel, "")).append("</td>");
        code.append("<th align=\"center\">").append(ApplicationResourcesHelper.getMessage("rotulo.tabela.autorizar.consignacao.nao.aceito", responsavel, "")).append("</td>");
        code.append("<th align=\"center\">").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.numero.ade.abreviado", responsavel)).append("</td>");
        code.append("<th align=\"left\">").append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel)).append("</td>");
        code.append("<th align=\"center\">").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela", responsavel)).append("</td>");
        code.append("</tr>");
        code.append("</thead>");

        code.append("<tbody>");
        for (final TransferObject ade : lstConsignacao) {
            final String adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
            final String csaNome = ade.getAttribute(Columns.CSA_NOME).toString();
            final String adeValor = NumberHelper.format(Double.parseDouble(ade.getAttribute(Columns.ADE_VLR).toString()), NumberHelper.getLang());
            final String dadValor = (String) ade.getAttribute(CodedValues.TDA_AUTORIZA_DESCONTO);

            code.append("<tr>");

            code.append("<td align=\"center\">");
            code.append("<input type=\"radio\" disabled").append(CodedValues.TDA_SIM.equals(dadValor) ? " checked" : "").append(">");
            code.append("</td>");

            code.append("<td align=\"center\">");
            code.append("<input type=\"radio\" disabled").append(CodedValues.TDA_NAO.equals(dadValor) ? " checked" : "").append(">");
            code.append("</td>");

            code.append("<td>").append(TextHelper.forHtmlContent(adeNumero)).append("</td>");
            code.append("<td>").append(TextHelper.forHtmlContent(csaNome)).append("</td>");
            code.append("<td align=\"right\">").append(ParamSvcTO.getDescricaoTpsTipoVlr((String) ade.getAttribute(Columns.ADE_TIPO_VLR))).append(" ").append(TextHelper.forHtmlContent(adeValor)).append("</td>");
            code.append("</tr>");

        }
        code.append("</tbody>");
        code.append("</table>");

        return code.toString();
    }

    private List<TransferObject> buscaAdes(AcessoSistema responsavel) throws AutorizacaoControllerException {
        return pesquisarConsignacaoController.lstConsignacaoParaAutorizacaoDoServidor(responsavel.getRseCodigo(), responsavel);
    }
}
