package com.zetra.econsig.web.controller.margem;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.CompraContratoControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.compra.CompraContratoController;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.saldodevedor.SaldoDevedorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.consignacao.AbstractConsultarConsignacaoWebController;

/**
 * <p>Title: AcompanharPortabilidadeWebController</p>
 * <p>Description: Controlador Web para o caso de uso Acompanhar Portabilidade de Contrato de Terceiros.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/acompanharPortabilidade" })
public class AcompanharPortabilidadeWebController extends AbstractConsultarConsignacaoWebController  {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AbstractConsultarConsignacaoWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private SaldoDevedorController saldoDevedorController;

    @Autowired
    private CompraContratoController compraContratoController;

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            boolean utilizaDiasUteis = ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_CONTROLE_COMPRA, CodedValues.TPC_SIM, responsavel);
            boolean temEtapaAprovacaoSaldo = ParamSist.paramEquals(CodedValues.TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA, CodedValues.TPC_SIM, responsavel);
            boolean filtroDataObrigatorio = true;

            String csaCodigo = null;
            String corCodigo = null;
            String orgCodigo = null;
            String pesquisar = null;
            String filtroConfiguravel = null;
            String tipoPeriodo = request.getParameter("tipoPeriodo");

            if (responsavel.isCseSup()) {
                csaCodigo = request.getParameter("CSA_CODIGO");
            } else if (responsavel.isOrg()) {
                orgCodigo = responsavel.getCodigoEntidade();
                csaCodigo = request.getParameter("CSA_CODIGO");
            } else if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCodigoEntidade();
                corCodigo = request.getParameter("COR_CODIGO");
            } else if (responsavel.isCor()) {
                csaCodigo = responsavel.getCodigoEntidadePai();
                corCodigo = responsavel.getCodigoEntidade();
                // Se o usuário de correspondente tem permissão de acessar os contratos da consignatária
                // então ele pode selecionar o correspondente.
                if (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
                    corCodigo = request.getParameter("COR_CODIGO");
                }
            }
            pesquisar = request.getParameter("pesquisar");
            //Valida o token de sessão para evitar a chamada direta à operação
            //Quando chamada inicialmente não possui o parâmetro pesquisar
            if (!TextHelper.isNull(pesquisar) && !SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            filtroConfiguravel = request.getParameter("filtroConfiguravel");

            String filtroDataIni = JspHelper.verificaVarQryStr(request, "periodoIni");
            String filtroDataFim = JspHelper.verificaVarQryStr(request, "periodoFim");

            // Workaround para resolver erro não replicável, que transforma as datas em algo parecido com isso "16%2F09%2F2013"
            filtroDataIni = filtroDataIni.replaceAll("%2F", "/");
            filtroDataFim = filtroDataFim.replaceAll("%2F", "/");

            CustomTransferObject criteriosPesquisa = new CustomTransferObject();

            if (pesquisar != null && pesquisar.equals("true")) {
                if (!TextHelper.isNull(request.getParameter("origem"))) {
                    criteriosPesquisa.setAttribute("origem", request.getParameter("origem"));
                }
                if (!TextHelper.isNull(request.getParameter("temSaldoDevedor"))) {
                    criteriosPesquisa.setAttribute("temSaldoDevedor", request.getParameter("temSaldoDevedor"));
                }
                if (!TextHelper.isNull(request.getParameter("saldoDevedorAprovado"))) {
                    criteriosPesquisa.setAttribute("saldoDevedorAprovado", request.getParameter("saldoDevedorAprovado"));
                }
                if (!TextHelper.isNull(request.getParameter("saldoDevedorPago"))) {
                    criteriosPesquisa.setAttribute("saldoDevedorPago", request.getParameter("saldoDevedorPago"));
                }
                if (!TextHelper.isNull(request.getParameter("liquidado"))) {
                    criteriosPesquisa.setAttribute("liquidado", request.getParameter("liquidado"));
                }
                if (!TextHelper.isNull(request.getParameter("diasSemSaldoDevedor"))) {
                    criteriosPesquisa.setAttribute("diasSemSaldoDevedor", request.getParameter("diasSemSaldoDevedor"));
                }
                if (!TextHelper.isNull(request.getParameter("diasSemAprovacaoSaldoDevedor"))) {
                    criteriosPesquisa.setAttribute("diasSemAprovacaoSaldoDevedor", request.getParameter("diasSemAprovacaoSaldoDevedor"));
                }
                if (!TextHelper.isNull(request.getParameter("diasSemPagamentoSaldoDevedor"))) {
                    criteriosPesquisa.setAttribute("diasSemPagamentoSaldoDevedor", request.getParameter("diasSemPagamentoSaldoDevedor"));
                }
                if (!TextHelper.isNull(request.getParameter("diasSemLiquidacao"))) {
                    criteriosPesquisa.setAttribute("diasSemLiquidacao", request.getParameter("diasSemLiquidacao"));
                }
                if (!TextHelper.isNull(request.getParameter("diasBloqueio"))) {
                    criteriosPesquisa.setAttribute("diasBloqueio", request.getParameter("diasBloqueio"));
                }
                if (!TextHelper.isNull(filtroDataIni)) {
                    criteriosPesquisa.setAttribute("periodoIni", filtroDataIni);
                }
                if (!TextHelper.isNull(filtroDataFim)) {
                    criteriosPesquisa.setAttribute("periodoFim", filtroDataFim);
                }
                if (!TextHelper.isNull(request.getParameter("tipoPeriodo"))) {
                    criteriosPesquisa.setAttribute("tipoPeriodo", request.getParameter("tipoPeriodo"));
                }
                if (!TextHelper.isNull(request.getParameter("ADE_NUMERO"))) {
                    criteriosPesquisa.setAttribute("ADE_NUMERO", request.getParameter("ADE_NUMERO"));
                }
                if (!TextHelper.isNull(request.getParameter("RSE_MATRICULA"))) {
                    criteriosPesquisa.setAttribute("RSE_MATRICULA", request.getParameter("RSE_MATRICULA"));
                }
                if (!TextHelper.isNull(request.getParameter("SER_CPF"))) {
                    criteriosPesquisa.setAttribute("SER_CPF", request.getParameter("SER_CPF"));
                }
                if (responsavel.isCseSupOrg() && !TextHelper.isNull(request.getParameter("CSA_CODIGO"))) {
                    criteriosPesquisa.setAttribute("CSA_CODIGO", csaCodigo);
                }
                if (responsavel.isCsaCor() && !TextHelper.isNull(request.getParameter("COR_CODIGO"))) {
                    criteriosPesquisa.setAttribute("COR_CODIGO", corCodigo);
                }

                if ((TextHelper.isNull(filtroDataIni) || TextHelper.isNull(filtroDataFim)) && (filtroDataObrigatorio)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.data", responsavel));
                    pesquisar = "false";
                } else if (!TextHelper.isNull(filtroDataIni) && !TextHelper.isNull(filtroDataFim) && (filtroDataObrigatorio) &&
                        DateHelper.dayDiff(DateHelper.parse(filtroDataFim, LocaleHelper.getDatePattern()), DateHelper.parse(filtroDataIni, LocaleHelper.getDatePattern())) > 30) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informacao.limite.periodo", responsavel));
                    pesquisar = "false";
                }
            }

            // Define valores padrão para filtro de data inicial e final, caso seja obrigatório o filtro
            if (filtroDataObrigatorio) {
                Calendar dataAtual = Calendar.getInstance();
                filtroDataFim = (TextHelper.isNull(filtroDataFim) ? DateHelper.toDateString(dataAtual.getTime()) : filtroDataFim);
                dataAtual.add(Calendar.DAY_OF_MONTH, -30);
                filtroDataIni = (TextHelper.isNull(filtroDataIni) ? DateHelper.toDateString(dataAtual.getTime()) : filtroDataIni);
            }

            List<TransferObject> consignatarias = null;
            if (responsavel.isCseSupOrg()) {
                //mostra combo para selecao de consignataria
                consignatarias = convenioController.getCsaCnvAtivo(null, orgCodigo, responsavel);
            }

            List<TransferObject> correspondentes = null;
            if (responsavel.isCsa() || (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA))) {
                CustomTransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.COR_CSA_CODIGO, csaCodigo);
                // Lista somente os correspondentes ativos ou bloqueados
                List<Short> statusCor = new ArrayList<>();
                statusCor.add(CodedValues.STS_ATIVO);
                statusCor.add(CodedValues.STS_INATIVO);
                statusCor.add(CodedValues.STS_INATIVO_CSE);
                statusCor.add(CodedValues.STS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);
                criterio.setAttribute(Columns.COR_ATIVO, statusCor);
                correspondentes = consignatariaController.lstCorrespondentes(criterio, responsavel);
            }

            // Paginação do resultado da pesquisa
            int size = JspHelper.LIMITE;

            int offset2 = 0;
            try {
                offset2 = Integer.parseInt(request.getParameter("offset2"));
            } catch (Exception ex) {
            }

            int total = 0;
            try {
                total = Integer.parseInt(request.getParameter("total"));
            } catch (Exception ex) {
            }

            // Monta lista de parâmetros através dos parâmetros de request
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            // Ignora os parâmetros abaixo
            params.remove("offset");
            params.remove("back");
            params.remove("linkRet");
            params.remove("linkRet64");
            params.remove("eConsig.page.token");
            params.remove("_skip_history_");
            params.remove("pager");
            params.remove("acao");

            List<String> requestParams = new ArrayList<>(params);
            String linkListagem = "../v3/acompanharPortabilidade?acao=iniciar";
            configurarPaginador(linkListagem, "rotulo.paginacao.acompanhamento.compra", total, size, requestParams, false, request, model);

            String linkPaginacao = linkListagem + "&pesquisar=true";
            if (request.getQueryString() != null && !request.getQueryString().equals("")) {
                linkPaginacao += "&" + request.getQueryString();
            }
            linkPaginacao = SynchronizerToken.updateTokenInURL(linkPaginacao, request);

            model.addAttribute("linkPaginacao", linkPaginacao);
            model.addAttribute("offset2", offset2);

            model.addAttribute("responsavel", responsavel);
            model.addAttribute("utilizaDiasUteis", utilizaDiasUteis);
            model.addAttribute("temEtapaAprovacaoSaldo", temEtapaAprovacaoSaldo);
            model.addAttribute("filtroDataObrigatorio", filtroDataObrigatorio);
            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("corCodigo", corCodigo);
            model.addAttribute("orgCodigo", orgCodigo);
            model.addAttribute("pesquisar", pesquisar);
            model.addAttribute("filtroDataIni", filtroDataIni);
            model.addAttribute("filtroDataFim", filtroDataFim);
            model.addAttribute("criteriosPesquisa", criteriosPesquisa);
            model.addAttribute("filtroConfiguravel",filtroConfiguravel);
            model.addAttribute("tipoPeriodo", tipoPeriodo);
            model.addAttribute("consignatarias", consignatarias);
            model.addAttribute("correspondentes", correspondentes);

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        return viewRedirect("jsp/acompanharPortabilidade/acompanharPortabilidade", request, session, model, responsavel);
    }

    @Override
    @RequestMapping(params = { "acao=emitirBoleto" })
    public String emitirBoleto(@RequestParam(value = "ADE_CODIGO", required = true, defaultValue = "") String adeCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return super.emitirBoleto(adeCodigo, request, response, session, model);
    }

    @Override
    @RequestMapping(params = { "acao=emitirBoletoExterno" })
    public String emitirBoletoExterno(@RequestParam(value = "ADE_CODIGO", required = true, defaultValue = "") String adeCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return super.emitirBoletoExterno(adeCodigo, request, response, session, model);
    }

    @RequestMapping(params = { "acao=editarMsgCsaPortabilidade" })
    public String editarMsgCsaPortabilidade(@RequestParam(value = "ADE_CODIGO_MSG", required = true, defaultValue = "") String adeCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String tituloPagina = ApplicationResourcesHelper.getMessage("rotulo.criar.email.csa.portabilidade.titulo", responsavel);
        ParamSession paramSession = ParamSession.getParamSession(session);
        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        int tamMaxMsg = 65000; // Como o texto é concatenado na tb_log (LOG_OBS), não pode usar 65535
        boolean podeIncluirAnexo = responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO);

        model.addAttribute("responsavel", responsavel);
        model.addAttribute("podeIncluirAnexo", podeIncluirAnexo);
        model.addAttribute("tamMaxMsg", tamMaxMsg);
        model.addAttribute("tituloPagina", tituloPagina);
        model.addAttribute("adeCodigo", adeCodigo);
        model.addAttribute("paramSession", paramSession);


        return viewRedirect("jsp/acompanharPortabilidade/enviarMensagemCsaPortabilidade", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=enviarMsgCsaPortabilidade" })
    public String enviarMsgCsaPortabilidade(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
    	AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

    	ParamSession paramSession = ParamSession.getParamSession(session);

    	//Realiza o upload do arquivo de anexo do email.
    	String paramTamMaxArqAnexo = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_ARQ_ANEXO_CONTRATO, responsavel);
    	int tamMaxArqAnexo = (!TextHelper.isNull(paramTamMaxArqAnexo) ? (Integer.valueOf(paramTamMaxArqAnexo)).intValue() : 200);

    	UploadHelper uploadHelper = new UploadHelper();
    	String adeCodigo = "";

		try {
			uploadHelper.processarRequisicao(request.getServletContext(), request, tamMaxArqAnexo * 1024);
			adeCodigo =  uploadHelper.getValorCampoFormulario("adeCodigo");
		} catch (Throwable ex) {
			LOG.error(ex.getMessage(), ex);
			String msg = ex.getMessage();
			if (!TextHelper.isNull(msg)) {
				session.setAttribute(CodedValues.MSG_ERRO, msg);
			}
		}

		try {
			compraContratoController.enviarMsgCsaPortabilidade(uploadHelper, responsavel);
			session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.email.enviado.sucesso", responsavel));
		} catch (CompraContratoControllerException ex) {
			session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
			return editarMsgCsaPortabilidade(adeCodigo, request, response, session, model);
		}

    	request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";

    }

    @Override
    @RequestMapping(params = { "acao=detalharConsignacao" })
    public String detalharConsignacao(@RequestParam(value = "ade", required = true, defaultValue = "") String adeCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String isOrigem = request.getParameter("isOrigem");
        //Em listagem de contratos para portabilidade, verifica se compradora pode ver histórico de parcelas de ADEs a comprar.
        boolean lstHstParcelasAdeAComprar = ParamSist.paramEquals(CodedValues.TPC_LST_HIST_PARCELA_ADE_TERCEIRO_COMPRA, CodedValues.TPC_SIM, responsavel) && ((responsavel.isCsaCor() && isOrigem != null && isOrigem.equals("1")));
        model.addAttribute("lstHstParcelasAdeAComprar", lstHstParcelasAdeAComprar);

        return super.detalharConsignacao(!TextHelper.isNull(adeCodigo) ? adeCodigo : request.getParameter("ADE_CODIGO"), request, response, session, model);
    }

    @RequestMapping(params = { "acao=acompanhar" })
    public String acompanhar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        model.addAttribute("responsavel", responsavel);

        return viewRedirect("jsp/acompanharPortabilidade/acompanharPortabilidadeSer", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=informarPgtSdv" })
    public String informarPagamentoSaldoDevedor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String adeCodigo = request.getParameter("ADE_CODIGO") != null ? request.getParameter("ADE_CODIGO") : request.getParameter("ade");

        //Valida o token de sessão para evitar a chamada direta à operação
        if (adeCodigo != null && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String obs = request.getParameter("obs");

        ParamSession paramSession = ParamSession.getParamSession(session);
        try {
            saldoDevedorController.informarPagamentoSaldoDevedor(adeCodigo, obs, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.saldo.devedor.sucesso", responsavel));

            // Verifica se a consignatária pode ser desbloqueada automaticamente
            if (responsavel.isCsaCor()) {
                String csaCodigo = (responsavel.isCor() ? responsavel.getCodigoEntidadePai() : responsavel.getCodigoEntidade());
                if (consignatariaController.verificarDesbloqueioAutomaticoConsignataria(csaCodigo, responsavel)) {
                    session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.informacao.csa.desbloqueada.automaticamente", responsavel));
                }
            }
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(params = { "acao=solicitarRecalcSdv" })
    public String solicitarRecalculoSaldoDevor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String adeCodigo = request.getParameter("ADE_CODIGO") != null ? request.getParameter("ADE_CODIGO") : request.getParameter("ade");

        //Valida o token de sessão para evitar a chamada direta à operação
        if (adeCodigo != null && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String obs = request.getParameter("obs");

        ParamSession paramSession = ParamSession.getParamSession(session);
        try {
            saldoDevedorController.solicitarRecalculoSaldoDevedor(adeCodigo, obs, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.saldo.devedor.sucesso", responsavel));

            // Verifica se a consignatária pode ser desbloqueada automaticamente
            if (responsavel.isCsaCor()) {
                String csaCodigo = (responsavel.isCor() ? responsavel.getCodigoEntidadePai() : responsavel.getCodigoEntidade());
                if (consignatariaController.verificarDesbloqueioAutomaticoConsignataria(csaCodigo, responsavel)) {
                    session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.informacao.csa.desbloqueada.automaticamente", responsavel));
                }
            }
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(params = { "acao=rejeitarPgtoSdv" })
    public String rejeitarPagamentoSaldoDevor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String adeCodigo = request.getParameter("ADE_CODIGO") != null ? request.getParameter("ADE_CODIGO") : request.getParameter("ade");

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String obs = request.getParameter("obs");

        ParamSession paramSession = ParamSession.getParamSession(session);
        try {
            saldoDevedorController.rejeitarPagamentoSaldoDevedor(adeCodigo, obs, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.saldo.devedor.sucesso", responsavel));

            // Verifica se a consignatária pode ser desbloqueada automaticamente
            if (responsavel.isCsaCor()) {
                String csaCodigo = (responsavel.isCor() ? responsavel.getCodigoEntidadePai() : responsavel.getCodigoEntidade());
                if (consignatariaController.verificarDesbloqueioAutomaticoConsignataria(csaCodigo, responsavel)) {
                    session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.informacao.csa.desbloqueada.automaticamente", responsavel));
                }
            }
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(params = { "acao=aprovarSaldoDevedor" })
    public String aprovarSaldoDevedor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String adeCodigo = request.getParameter("ADE_CODIGO") != null ? request.getParameter("ADE_CODIGO") : request.getParameter("ade");

        //Valida o token de sessão para evitar a chamada direta à operação
        if (adeCodigo != null && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String obs = request.getParameter("obs");
        boolean aprovado = (request.getParameter("aprovado") != null && request.getParameter("aprovado").equals("S"));

        ParamSession paramSession = ParamSession.getParamSession(session);
        try {
            if (responsavel.isSer()) {
                saldoDevedorController.aprovarSaldoDevedor(adeCodigo, aprovado, obs, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.saldo.devedor.sucesso", responsavel));
            }
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(params = { "acao=detalharPesquisa" })
    public String detalharPesquisa(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        // Parâmetros de sistema necessários
        boolean possuiEtapaAprovacaoSaldo = ParamSist.paramEquals(CodedValues.TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA, CodedValues.TPC_SIM, responsavel);
        boolean filtroDataObrigatorio = !JspHelper.verificaVarQryStr(request, "filtroConfiguravel").equals("2");

        List<TransferObject> listaContratos = null;

        String filtroDataIni = JspHelper.verificaVarQryStr(request, "periodoIni");
        String filtroDataFim = JspHelper.verificaVarQryStr(request, "periodoFim");

        // Workaround para resolver erro não replicável, que transforma as datas em algo parecido com isso "16%2F09%2F2013"
        filtroDataIni = filtroDataIni.replaceAll("%2F", "/");
        filtroDataFim = filtroDataFim.replaceAll("%2F", "/");

        // Faz a pesquisa dos contratos em situação de compra
        if (request.getParameter("pesquisar") != null && request.getParameter("pesquisar").equals("true")) {

            String csaCodigo = null;
            String corCodigo = null;
            String orgCodigo = null;

            if (responsavel.isCseSup()) {
                csaCodigo = request.getParameter("CSA_CODIGO");
            } else if (responsavel.isOrg()) {
                orgCodigo = responsavel.getCodigoEntidade();
                csaCodigo = request.getParameter("CSA_CODIGO");
            } else if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCodigoEntidade();
                corCodigo = request.getParameter("COR_CODIGO");
            } else if (responsavel.isCor()) {
                csaCodigo = responsavel.getCodigoEntidadePai();
                corCodigo = responsavel.getCodigoEntidade();
                // Se o usuário de correspondente tem permissão de acessar os contratos da consignatária
                //  passa o código do correspondente como nulo, não fazendo o filtro pelo correspondente
                if (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
                    corCodigo = request.getParameter("COR_CODIGO");
                }
            }

            // Obtém da request os parâmetros necessários
            CustomTransferObject criterios = new CustomTransferObject();
            if (!TextHelper.isNull(request.getParameter("origem"))) {
                criterios.setAttribute("origem", request.getParameter("origem"));
            }
            if (!TextHelper.isNull(request.getParameter("temSaldoDevedor"))) {
                criterios.setAttribute("temSaldoDevedor", request.getParameter("temSaldoDevedor"));
            }
            if (!TextHelper.isNull(request.getParameter("saldoDevedorAprovado"))) {
                criterios.setAttribute("saldoDevedorAprovado", request.getParameter("saldoDevedorAprovado"));
            }
            if (!TextHelper.isNull(request.getParameter("saldoDevedorPago"))) {
                criterios.setAttribute("saldoDevedorPago", request.getParameter("saldoDevedorPago"));
            }
            if (!TextHelper.isNull(request.getParameter("liquidado"))) {
                criterios.setAttribute("liquidado", request.getParameter("liquidado"));
            }
            if (!TextHelper.isNull(request.getParameter("diasSemSaldoDevedor"))) {
                criterios.setAttribute("diasSemSaldoDevedor", request.getParameter("diasSemSaldoDevedor"));
            }
            if (!TextHelper.isNull(request.getParameter("diasSemAprovacaoSaldoDevedor"))) {
                criterios.setAttribute("diasSemAprovacaoSaldoDevedor", request.getParameter("diasSemAprovacaoSaldoDevedor"));
            }
            if (!TextHelper.isNull(request.getParameter("diasSemPagamentoSaldoDevedor"))) {
                criterios.setAttribute("diasSemPagamentoSaldoDevedor", request.getParameter("diasSemPagamentoSaldoDevedor"));
            }
            if (!TextHelper.isNull(request.getParameter("diasSemLiquidacao"))) {
                criterios.setAttribute("diasSemLiquidacao", request.getParameter("diasSemLiquidacao"));
            }
            if (!TextHelper.isNull(request.getParameter("diasBloqueio"))) {
                criterios.setAttribute("diasBloqueio", request.getParameter("diasBloqueio"));
            }
            if (!TextHelper.isNull(filtroDataIni)) {
                criterios.setAttribute("periodoIni", filtroDataIni);
            }
            if (!TextHelper.isNull(filtroDataFim)) {
                criterios.setAttribute("periodoFim", filtroDataFim);
            }
            if (!TextHelper.isNull(request.getParameter("tipoPeriodo"))) {
                criterios.setAttribute("tipoPeriodo", request.getParameter("tipoPeriodo"));
            }
            if (!TextHelper.isNull(request.getParameter("ADE_NUMERO"))) {
                criterios.setAttribute("ADE_NUMERO", request.getParameter("ADE_NUMERO"));
            }
            if (!TextHelper.isNull(request.getParameter("RSE_MATRICULA"))) {
                criterios.setAttribute("RSE_MATRICULA", request.getParameter("RSE_MATRICULA"));
            }
            if (!TextHelper.isNull(request.getParameter("SER_CPF"))) {
                criterios.setAttribute("SER_CPF", request.getParameter("SER_CPF"));
            }
            if (responsavel.isCseSupOrg() && !TextHelper.isNull(request.getParameter("CSA_CODIGO"))) {
                criterios.setAttribute("CSA_CODIGO", csaCodigo);
            }
            if (responsavel.isCsaCor() && !TextHelper.isNull(request.getParameter("COR_CODIGO"))) {
                criterios.setAttribute("COR_CODIGO", corCodigo);
            }

            try {
                if ((TextHelper.isNull(filtroDataIni) || TextHelper.isNull(filtroDataFim)) && (filtroDataObrigatorio)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.data.compra", responsavel));
                    listaContratos = new ArrayList<>();
                } else if (!TextHelper.isNull(filtroDataIni) && !TextHelper.isNull(filtroDataFim) && (filtroDataObrigatorio) &&
                        DateHelper.dayDiff(DateHelper.parse(filtroDataFim, LocaleHelper.getDatePattern()), DateHelper.parse(filtroDataIni, LocaleHelper.getDatePattern())) > 30) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informacao.limite.periodo", responsavel));
                    listaContratos = new ArrayList<>();
                } else {
                    try {
                        listaContratos = pesquisarConsignacaoController.pesquisarCompraContratos(criterios, csaCodigo, corCodigo, orgCodigo,  responsavel);
                    } catch (Exception ex) {
                        session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                        LOG.error(ex.getMessage(), ex);
                        listaContratos = new ArrayList<>();
                    }
                }

            } catch (ParseException pex) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
                LOG.error(pex.getMessage(), pex);
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        model.addAttribute("listaContratos", listaContratos);
        model.addAttribute("possuiEtapaAprovacaoSaldo", possuiEtapaAprovacaoSaldo);
        model.addAttribute("filtroDataObrigatorio", filtroDataObrigatorio);
        model.addAttribute("responsavel", responsavel);

        return viewRedirect("jsp/acompanharPortabilidade/detalharPesquisa", request, session, model, responsavel);
    }

    @Override
    @RequestMapping(params = { "acao=listarHistLiquidacoesAntecipadas" })
    public String listarHistLiquidacoesAntecipadas(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return super.listarHistLiquidacoesAntecipadas(request, response, session, model);
    }


}
