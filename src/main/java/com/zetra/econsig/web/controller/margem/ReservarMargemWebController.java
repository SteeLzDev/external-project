package com.zetra.econsig.web.controller.margem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.web.Servico;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.consignacao.SolicitacaoServidorHelper;
import com.zetra.econsig.helper.margem.ControleConsulta;
import com.zetra.econsig.helper.margem.TextoMargem;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoFuncaoServico;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.consignacao.AbstractIncluirConsignacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ReservarMargemWebController</p>
 * <p>Description: Controlador Web para o casos de uso de reserva de margem.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/reservarMargem" })
public class ReservarMargemWebController extends AbstractIncluirConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ReservarMargemWebController.class);

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Override
    protected String getFunCodigo() {
        return CodedValues.FUN_RES_MARGEM;
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        if (responsavel.isSer()) {
            model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.solicitar.consignacao.titulo", responsavel));
        } else {
            model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.reservar.margem.titulo", responsavel));
        }
        model.addAttribute("exigeCodAutorizacaoSMS", ParamSist.paramEquals(CodedValues.TPC_ENVIA_SMS_CODIGO_UNICO_AUTORIZACAO_CONSIGNACAO, CodedValues.TPC_SIM, responsavel));
        model.addAttribute("acaoFormulario", "../v3/reservarMargem");
        model.addAttribute("imageHeader", "i-operacional");
        model.addAttribute("tipoOperacao", "reservar");
    }

    @Override
    protected boolean temListagemDinamicaDeServicos(AcessoSistema responsavel) {
        return responsavel.isCseSupOrg();
    }

    @Override
    protected void carregarListaServico(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        try {
            if(model.getAttribute("lstServico") == null) {
                final List<TransferObject> lstConvenio = convenioController.lstCnvEntidade(responsavel.getCodigoEntidade(), responsavel.getTipoEntidade(), "reservar", responsavel);
                final List<TransferObject> lstServico = TextHelper.groupConcat(lstConvenio, new String[]{Columns.SVC_DESCRICAO,Columns.SVC_CODIGO}, new String[]{Columns.CNV_COD_VERBA}, ",", true, true);
                model.addAttribute("lstServico", lstServico);
            }
        } catch (final ConvenioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        // Remove validação de senha realizada anteriormente
        session.removeAttribute("senhaServidorRenegOK");
        session.removeAttribute("senhaServidorOK");
        session.removeAttribute("serAutorizacao");

        final String rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
        model.addAttribute("rseCodigo",rseCodigo);

        if(session.getAttribute("lstServico") != null) {
            model.addAttribute("lstServico", session.getAttribute("lstServico"));
            session.removeAttribute("lstServico");

            model.addAttribute("existeLimiteServico",true);
            model.addAttribute("rseMatricula", JspHelper.verificaVarQryStr(request, "RSE_MATRICULA"));
            model.addAttribute("serCpf", JspHelper.verificaVarQryStr(request, "SER_CPF"));
        }

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

    @RequestMapping(params = { "acao=selecionarCsa" })
    public String selecionarCsa(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!responsavel.isSer() && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String rseCodigo = responsavel.getRseCodigo();
        final String orgCodigo = responsavel.getOrgCodigo();
        final String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");

        if (!AcessoFuncaoServico.temAcessoFuncao(request, CodedValues.FUN_RES_MARGEM, responsavel.getUsuCodigo(), svcCodigo)) {
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            // Busca a lista de consignatárias que tem convênio com o órgão do servidor e serviço selecionado

            final List<TransferObject> consignatarias = convenioController.getCsaCnvAtivo(svcCodigo, orgCodigo, true, true, responsavel);

            model.addAttribute("listaConsignataria", consignatarias);

            final List<String> csaCodigos = new ArrayList<>();
            for(final TransferObject csa : consignatarias) {
                csaCodigos.add((String) csa.getAttribute(Columns.CSA_CODIGO));
            }

            final List<TransferObject> listaCsaPermiteContato = consignatariaController.listaCsaPermiteContato(csaCodigos, responsavel);
            final HashMap<String, TransferObject> hashCsaPermiteContato = new HashMap<>();

            for (final TransferObject csaPermiteContato : listaCsaPermiteContato) {
                hashCsaPermiteContato.put((String) csaPermiteContato.getAttribute(Columns.CSA_CODIGO), csaPermiteContato);
            }

            model.addAttribute("hashCsaPermiteContato", hashCsaPermiteContato);

            final boolean podeConsultarCaptcha = ControleConsulta.getInstance().podeConsultarMargemSemCaptchaSer(responsavel.getUsuCodigo());

            if (podeConsultarCaptcha) {
                // Busca a margem do servidor para o serviço selecionado
                final List<MargemTO> margens = consultarMargemController.consultarMargem(rseCodigo, null, svcCodigo, null, true, true, responsavel);

                final TextoMargem textoMargem = new TextoMargem(null, margens, responsavel, model);
                if (!textoMargem.isVazio()) {
                    session.setAttribute(textoMargem.getTipoMsg(), textoMargem.getTexto());
                }
            }
        } catch (final ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String proximaAcao = definirProximaOperacao(request, responsavel);
        model.addAttribute("proximaAcao", proximaAcao);
        return viewRedirect("jsp/reservarMargem/selecionarCsa", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=selecionarServico" })
    public String selecionarServico(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!responsavel.isSer() && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String orgCodigo = responsavel.getOrgCodigo();
        final boolean temPermissaoSimulacao = responsavel.temPermissao(CodedValues.FUN_SIM_CONSIGNACAO);
        final boolean temPermissaoReserva = responsavel.temPermissao(CodedValues.FUN_RES_MARGEM);
        final boolean temPermissaoSolicitacao = responsavel.temPermissao(CodedValues.FUN_SOL_EMPRESTIMO);
        final String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
        final String nseCodigo = JspHelper.verificaVarQryStr(request, "NSE_CODIGO");
        final String corCodigo = JspHelper.verificaVarQryStr(request, "COR_CODIGO");

        try {
            final List<TransferObject> servicosReserva = SolicitacaoServidorHelper.lstServicos(orgCodigo, null, csaCodigo, temPermissaoSimulacao, temPermissaoReserva, temPermissaoSolicitacao, false, nseCodigo,corCodigo, responsavel);
            final List<TransferObject> servicos = new ArrayList<>();

            for (final TransferObject servico : servicosReserva) {
                if ((boolean) servico.getAttribute("fluxoReservaMargem")) {
                    servicos.add(servico);
                }
            }

            model.addAttribute("listaServico", servicos);
            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("corCodigo", corCodigo);
        } catch (final ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String proximaAcao = definirProximaOperacao(request, responsavel);
        model.addAttribute("proximaAcao", proximaAcao);

        return viewRedirect("jsp/reservarMargem/selecionarServico", request, session, model, responsavel);
    }

    @Override
    @RequestMapping(params = { "acao=listarHistLiquidacoesAntecipadas" })
    public String listarHistLiquidacoesAntecipadas(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return super.listarHistLiquidacoesAntecipadas(request, response, session, model);
    }

    @Override
    protected String validarServicoOperacao(String svcCodigo, String rseCodigo, Map<String, String> parametrosPlano, HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        if (TextHelper.isNull(svcCodigo)) {
            svcCodigo = request.getParameter("SVC_CODIGO");
        }
        if (TextHelper.isNull(svcCodigo)) {
            throw new ViewHelperException("mensagem.erro.servico.nao.informado", responsavel);
        }
        return svcCodigo;
    }
}
