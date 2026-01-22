package com.zetra.econsig.web.controller.financiamentodivida;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.PropostaPagamentoDividaTO;
import com.zetra.econsig.dto.entidade.SaldoDevedorTransferObject;
import com.zetra.econsig.exception.FinanciamentoDividaControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.financiamentodivida.FinanciamentoDividaController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.saldodevedor.SaldoDevedorController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusPropostaEnum;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: AcompanharFinanciamentoDividaWebController</p>
 * <p>Description: Controlador Web para o caso de uso Acompanhar Financiamento de Dívida.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(value = { "/v3/acompanharFinanciamentoDivida" })
public class AcompanharFinanciamentoDividaWebController extends ControlePaginacaoWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AcompanharFinanciamentoDividaWebController.class);

    @Autowired
    private FinanciamentoDividaController financiamentoDividaController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private SaldoDevedorController saldoDevedorController;

    @Autowired
    private ServicoController servicoController;

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        SynchronizerToken.saveToken(request);

        boolean filtroDataObrigatorio = false;

        String filtro = JspHelper.verificaVarQryStr(request, "filtro");
        boolean pesquisar = JspHelper.verificaVarQryStr(request, "pesquisar").equals("true") && !filtro.isEmpty();

        // Verifica as permissões do usuário para exibição dos ícones
        boolean podeEdtSaldo = responsavel.temPermissao(CodedValues.FUN_EDT_SALDO_DEVEDOR);
        boolean podeEdtProposta = responsavel.temPermissao(CodedValues.FUN_INFORMAR_PROPOSTAS_PGT_DIVIDA);
        boolean podeRenegociar = responsavel.temPermissao(CodedValues.FUN_RENE_CONTRATO);
        boolean podeComprar = responsavel.temPermissao(CodedValues.FUN_COMP_CONTRATO);
        boolean podeConsultarAde = responsavel.temPermissao(CodedValues.FUN_CONS_CONSIGNACAO);

        String filtroDataIni = JspHelper.verificaVarQryStr(request, "periodoIni");
        String filtroDataFim = JspHelper.verificaVarQryStr(request, "periodoFim");

        // Workaround para resolver erro não replicável, que transforma as datas em algo parecido com isso "16%2F09%2F2013"
        filtroDataIni = filtroDataIni.replaceAll("%2F", "/");
        filtroDataFim = filtroDataFim.replaceAll("%2F", "/");

        try {
            if (pesquisar) {
                if ((TextHelper.isNull(filtroDataIni) || TextHelper.isNull(filtroDataFim)) && (filtroDataObrigatorio)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.data.solicitacao", responsavel));
                    pesquisar = false;
                } else if (!TextHelper.isNull(filtroDataIni) && !TextHelper.isNull(filtroDataFim) &&
                        (filtroDataObrigatorio) && DateHelper.dayDiff(DateHelper.parse(filtroDataFim, LocaleHelper.getDatePattern()), DateHelper.parse(filtroDataIni, LocaleHelper.getDatePattern())) > 30) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informacao.limite.periodo", responsavel));
                    pesquisar = false;
                }
            }
        } catch (ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Monta lista de parâmetros através dos parâmetros de request
        Map<String, String[]> parameterMap = new HashMap<>(request.getParameterMap());
        // Ignora os parâmetros abaixo
        parameterMap.remove("offset");
        parameterMap.remove("back");

        List<TransferObject> lstResultado = null;
        if (pesquisar) {
            try {
                TransferObject criteriosPesquisa = new CustomTransferObject();
                criteriosPesquisa.setAttribute("filtro", filtro);

                if (!TextHelper.isNull(filtroDataIni)) {
                    criteriosPesquisa.setAttribute("periodoIni", filtroDataIni);
                }
                if (!TextHelper.isNull(filtroDataFim)) {
                    criteriosPesquisa.setAttribute("periodoFim", filtroDataFim);
                }

                if (!TextHelper.isNull(request.getParameter("ADE_NUMERO"))) {
                    criteriosPesquisa.setAttribute("ADE_NUMERO", Long.valueOf(request.getParameter("ADE_NUMERO")));
                }
                if (!TextHelper.isNull(request.getParameter("RSE_MATRICULA"))) {
                    criteriosPesquisa.setAttribute("RSE_MATRICULA", request.getParameter("RSE_MATRICULA"));
                }
                if (!TextHelper.isNull(request.getParameter("SER_CPF"))) {
                    criteriosPesquisa.setAttribute("SER_CPF", request.getParameter("SER_CPF"));
                }

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
                    // então ele pode selecionar o correspondente.
                    if (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
                        corCodigo = request.getParameter("COR_CODIGO");
                    }
                }

                criteriosPesquisa.setAttribute("CSA_CODIGO", csaCodigo);
                criteriosPesquisa.setAttribute("COR_CODIGO", corCodigo);
                criteriosPesquisa.setAttribute("ORG_CODIGO", orgCodigo);

                int total = financiamentoDividaController.contarFinanciamentoDivida(criteriosPesquisa, responsavel);

                int size = JspHelper.LIMITE;
                int offset = 0;
                try {
                    offset = Integer.parseInt(request.getParameter("offset"));
                } catch (Exception ex) {
                    // Primeiro acesso à página não possui offset
                }

                if (total > 0) {
                    // Realiza a pesquisa
                    lstResultado = financiamentoDividaController.acompanharFinanciamentoDivida(criteriosPesquisa, offset, size, responsavel);
                } else {
                    lstResultado = new ArrayList<>();
                }
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        // Define valores padrão para filtro de data inicial e final, caso seja obrigatório o filtro
        if (filtroDataObrigatorio) {
            Calendar dataAtual = Calendar.getInstance();
            filtroDataFim = (TextHelper.isNull(filtroDataFim) ? DateHelper.toDateString(dataAtual.getTime()) : filtroDataFim);
            dataAtual.add(Calendar.DAY_OF_MONTH, -30);
            filtroDataIni = (TextHelper.isNull(filtroDataIni) ? DateHelper.toDateString(dataAtual.getTime()) : filtroDataIni);
        }

        // Colspan da coluna "Ações"
        int colspan = 0;

        if ((filtro.equals("0") || filtro.equals("1") || filtro.equals("2")) && podeConsultarAde) {
            colspan++;
        }
        if (filtro.equals("0")) {
            colspan += (podeEdtSaldo ? 1 : 0);
        } else if (filtro.equals("1")) {
            colspan += (podeEdtSaldo ? 1 : 0);
            colspan += (podeRenegociar ? 1 : 0);
        } else if (filtro.equals("2")) {
            colspan += (podeRenegociar ? 1 : 0);
        } else if (filtro.equals("3")) {
            colspan += (podeEdtProposta ? 1 : 0);
        } else if (filtro.equals("4")) {
            colspan += (podeEdtProposta ? 1 : 0);
            colspan += (podeComprar ? 1 : 0);
        } else if (filtro.equals("5") && podeComprar) {
            colspan += (podeComprar ? 1 : 0);
        }

        model.addAttribute("filtroDataObrigatorio", filtroDataObrigatorio);
        model.addAttribute("filtro", filtro);
        model.addAttribute("podeEdtSaldo", podeEdtSaldo);
        model.addAttribute("podeEdtProposta", podeEdtProposta);
        model.addAttribute("podeRenegociar", podeRenegociar);
        model.addAttribute("podeComprar", podeComprar);
        model.addAttribute("podeConsultarAde", podeConsultarAde);
        model.addAttribute("filtroDataIni", filtroDataIni);
        model.addAttribute("filtroDataFim", filtroDataFim);
        model.addAttribute("lstResultado", lstResultado);
        model.addAttribute("colspan", colspan);

        return viewRedirect("jsp/acompanharFinanciamentoDivida/acompanharFinanciamentoDivida", request, session, model, responsavel);
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=aprovar" })
    public String aprovar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        ParamSession paramSession = ParamSession.getParamSession(session);

        String adeCodigo = request.getParameter("ADE_CODIGO") != null ? request.getParameter("ADE_CODIGO") : request.getParameter("ade");

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request, true)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        if (!responsavel.isSer() || TextHelper.isNull(adeCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            String ppdCodigo = JspHelper.verificaVarQryStr(request, "ppd");
            financiamentoDividaController.aprovarPropostaPagamento(adeCodigo, ppdCodigo, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.aprovar.proposta.concluido.sucesso", responsavel));
        } catch (FinanciamentoDividaControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=solicitar" })
    public String solicitar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        ParamSession paramSession = ParamSession.getParamSession(session);

        String adeCodigo = request.getParameter("ADE_CODIGO") != null ? request.getParameter("ADE_CODIGO") : request.getParameter("ade");

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request, true)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        if (!responsavel.isSer() || TextHelper.isNull(adeCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            financiamentoDividaController.solicitarPropostaPagamento(adeCodigo, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.solicitar.proposta.concluido.sucesso", responsavel));
        } catch (FinanciamentoDividaControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            String adeCodigo = request.getParameter("ADE_CODIGO") != null ? request.getParameter("ADE_CODIGO") : request.getParameter("ade");

            // Verifica se está habilitado módulo de financiamento de dívida
            if (!ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Verifica se tem solicitação de proposta do servidor
            if (!financiamentoDividaController.temSolicitacaoProposta(adeCodigo, true, responsavel)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Busca os dados do contrato
            TransferObject ade = null;
            try {
                List<String> adeCodigos = new ArrayList<>();
                adeCodigos.add(adeCodigo);
                // validaPermissao = false pois o servidor solicitou à terceiros a informação de propostas
                List<TransferObject> autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigos, false, responsavel);
                if (autdes != null && !autdes.isEmpty()) {
                    ade = autdes.get(0);
                    ade = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) ade, null, responsavel);
                }
            } catch (com.zetra.econsig.exception.AutorizacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Consignatária deverá sempre ser a do usuário, e não a do contrato
            String csaCodigo = responsavel.getCsaCodigo();

            // Serviço e órgão do contrato
            String svcCodigo = ade.getAttribute(Columns.SVC_CODIGO).toString();
            String orgCodigo = ade.getAttribute(Columns.ORG_CODIGO).toString();
            String rseCodigo = ade.getAttribute(Columns.RSE_CODIGO).toString();

            // Código do serviço de financiamento da dívida
            String svcCodigoDestino = null;

            // Busca os parâmetros de serviço necessários
            ParamSvcTO paramSvc = null;
            try {
                paramSvc = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
            } catch (com.zetra.econsig.exception.ParametroControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Verifica exigência de informação de propostas de pagamento do saldo
            int qtdMinPropostas = 0;
            int qtdMaxPropostas = 9;
            if (!TextHelper.isNull(paramSvc.getTpsQtdPropostasPagamentoParcelSaldo())) {
                try {
                    qtdMinPropostas = Integer.valueOf(paramSvc.getTpsQtdPropostasPagamentoParcelSaldo());
                } catch (NumberFormatException ex) {
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.editar.proposta.valor.incorreto.parametro", responsavel, CodedValues.TPS_QTD_PROPOSTAS_PAGAMENTO_PARCEL_SALDO, svcCodigo));
                }
                if (!TextHelper.isNull(paramSvc.getTpsQtdPropostasPagamentoParcelSaldoRef())) {
                    try {
                        qtdMaxPropostas = Integer.valueOf(paramSvc.getTpsQtdPropostasPagamentoParcelSaldoRef());
                    } catch (NumberFormatException ex) {
                        LOG.error(ApplicationResourcesHelper.getMessage("mensagem.editar.proposta.valor.incorreto.referencia", responsavel, CodedValues.TPS_QTD_PROPOSTAS_PAGAMENTO_PARCEL_SALDO, svcCodigo));
                    }
                }
            }
            if (qtdMinPropostas > 0) {
                // Se o serviço requer informação de propostas, verifica se a consignatária possui
                // convênio com o serviço relacionado para financiamento.
                List<String> svcCodigosDestino = servicoController.obtemServicoRelacionadoComConvenioAtivo(svcCodigo, csaCodigo, orgCodigo, CodedValues.TNT_FINANCIAMENTO_DIVIDA, responsavel);
                if (svcCodigosDestino == null || svcCodigosDestino.size() == 0) {
                    qtdMinPropostas = 0;
                } else {
                    svcCodigoDestino = svcCodigosDestino.get(0);
                }
            }

            if (qtdMinPropostas == 0) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Obtém os prazos obrigatórios
            List<Integer> prazosObrigatorios = null;
            try {
                prazosObrigatorios = financiamentoDividaController.calcularPrazosObrigProposta(svcCodigoDestino, rseCodigo, orgCodigo, adeCodigo, true, responsavel);
                LOG.info(ApplicationResourcesHelper.getMessage("mensagem.editar.proposta.prazos.obrigatorios", responsavel, TextHelper.join(prazosObrigatorios, ", ")));
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Busca as propostas de pagamento
            Map<Object, TransferObject> propostas = new HashMap<>();
            try {
                List<TransferObject> lstPropostas = financiamentoDividaController.lstPropostaPagamentoDivida(adeCodigo, csaCodigo, StatusPropostaEnum.AGUARDANDO_APROVACAO.getCodigo(), responsavel);
                if (lstPropostas != null && lstPropostas.size() > 0) {
                    for (TransferObject proposta : lstPropostas) {
                        propostas.put(proposta.getAttribute(Columns.PPD_NUMERO), proposta);
                    }
                }
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            model.addAttribute("qtdMinPropostas", qtdMinPropostas);
            model.addAttribute("qtdMaxPropostas", qtdMaxPropostas);
            model.addAttribute("adeCodigo", adeCodigo);
            model.addAttribute("ade", ade);
            model.addAttribute("prazosObrigatorios", prazosObrigatorios);
            model.addAttribute("propostas", propostas);

            return viewRedirect("jsp/acompanharFinanciamentoDivida/editarProposta", request, session, model, responsavel);

        } catch (FinanciamentoDividaControllerException | ServicoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        ParamSession paramSession = ParamSession.getParamSession(session);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request, true)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            String adeCodigo = request.getParameter("ADE_CODIGO") != null ? request.getParameter("ADE_CODIGO") : request.getParameter("ade");

            // Busca os dados do contrato
            TransferObject ade = null;
            try {
                List<String> adeCodigos = new ArrayList<>();
                adeCodigos.add(adeCodigo);
                // validaPermissao = false pois o servidor solicitou à terceiros a informação de propostas
                List<TransferObject> autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigos, false, responsavel);
                if (autdes != null && !autdes.isEmpty()) {
                    ade = autdes.get(0);
                    ade = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) ade, null, responsavel);
                }
            } catch (com.zetra.econsig.exception.AutorizacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Consignatária deverá sempre ser a do usuário, e não a do contrato
            String csaCodigo = responsavel.getCsaCodigo();

            // Serviço e órgão do contrato
            String svcCodigo = ade.getAttribute(Columns.SVC_CODIGO).toString();
            String orgCodigo = ade.getAttribute(Columns.ORG_CODIGO).toString();
            String rseCodigo = ade.getAttribute(Columns.RSE_CODIGO).toString();

            // Código do serviço de financiamento da dívida
            String svcCodigoDestino = null;

            // Busca as informações do saldo devedor da consignação
            SaldoDevedorTransferObject saldoDevedorTO = null;
            try {
                saldoDevedorTO = saldoDevedorController.getSaldoDevedor(adeCodigo, responsavel);
            } catch (com.zetra.econsig.exception.SaldoDevedorControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Busca os parâmetros de serviço necessários
            ParamSvcTO paramSvc = null;
            try {
                paramSvc = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
            } catch (com.zetra.econsig.exception.ParametroControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Verifica exigência de informação de propostas de pagamento do saldo
            int qtdMinPropostas = 0;
            int qtdMaxPropostas = 9;
            if (!TextHelper.isNull(paramSvc.getTpsQtdPropostasPagamentoParcelSaldo())) {
                try {
                    qtdMinPropostas = Integer.valueOf(paramSvc.getTpsQtdPropostasPagamentoParcelSaldo());
                } catch (NumberFormatException ex) {
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.editar.proposta.valor.incorreto.parametro", responsavel, CodedValues.TPS_QTD_PROPOSTAS_PAGAMENTO_PARCEL_SALDO, svcCodigo));
                }
                if (!TextHelper.isNull(paramSvc.getTpsQtdPropostasPagamentoParcelSaldoRef())) {
                    try {
                        qtdMaxPropostas = Integer.valueOf(paramSvc.getTpsQtdPropostasPagamentoParcelSaldoRef());
                    } catch (NumberFormatException ex) {
                        LOG.error(ApplicationResourcesHelper.getMessage("mensagem.editar.proposta.valor.incorreto.referencia", responsavel, CodedValues.TPS_QTD_PROPOSTAS_PAGAMENTO_PARCEL_SALDO, svcCodigo));
                    }
                }
            }
            if (qtdMinPropostas > 0) {
                // Se o serviço requer informação de propostas, verifica se a consignatária possui
                // convênio com o serviço relacionado para financiamento.
                List<String> svcCodigosDestino = servicoController.obtemServicoRelacionadoComConvenioAtivo(svcCodigo, csaCodigo, orgCodigo, CodedValues.TNT_FINANCIAMENTO_DIVIDA, responsavel);
                if (svcCodigosDestino == null || svcCodigosDestino.size() == 0) {
                    qtdMinPropostas = 0;
                } else {
                    svcCodigoDestino = svcCodigosDestino.get(0);
                }
            }

            if (qtdMinPropostas == 0) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Obtém os prazos obrigatórios
            List<Integer> prazosObrigatorios = null;
            try {
                prazosObrigatorios = financiamentoDividaController.calcularPrazosObrigProposta(svcCodigoDestino, rseCodigo, orgCodigo, adeCodigo, true, responsavel);
                LOG.info(ApplicationResourcesHelper.getMessage("mensagem.editar.proposta.prazos.obrigatorios", responsavel, TextHelper.join(prazosObrigatorios, ", ")));
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Cria lista com as propostas para pagamento do saldo
            List<PropostaPagamentoDividaTO> propostasPgtSaldo = null;
            if (qtdMinPropostas > 0) {
                propostasPgtSaldo = new ArrayList<>();
                for (int i = 1; i <= qtdMaxPropostas; i++) {
                    String cdgProposta = JspHelper.verificaVarQryStr(request, "cdgProposta" + i);
                    String przProposta = JspHelper.verificaVarQryStr(request, "przProposta" + i);
                    String vlrProposta = JspHelper.verificaVarQryStr(request, "vlrProposta" + i);
                    if (!TextHelper.isNull(przProposta) && !TextHelper.isNull(vlrProposta)) {
                        try {
                            PropostaPagamentoDividaTO proposta = new PropostaPagamentoDividaTO();
                            proposta.setPpdCodigo((!TextHelper.isNull(cdgProposta)) ? cdgProposta : null);
                            proposta.setAdeCodigo(adeCodigo);
                            proposta.setCsaCodigo(csaCodigo);
                            proposta.setUsuCodigo(responsavel.getUsuCodigo());
                            proposta.setPpdNumero(i);
                            proposta.setPpdValorDivida(saldoDevedorTO.getSdvValorComDesconto() != null ? saldoDevedorTO.getSdvValorComDesconto() : saldoDevedorTO.getSdvValor());
                            proposta.setPpdValorParcela(new BigDecimal(NumberHelper.reformat(vlrProposta, NumberHelper.getLang(), "en")));
                            proposta.setPpdPrazo(Integer.valueOf(przProposta));
                            propostasPgtSaldo.add(proposta);
                        } catch (Exception ex) {
                            throw new Exception(ApplicationResourcesHelper.getMessage("mensagem.editar.proposta.valor.incorreto.num.proposta", responsavel, String.valueOf(i)));
                        }
                    }
                }
                if (propostasPgtSaldo.size() < qtdMinPropostas) {
                    throw new Exception(ApplicationResourcesHelper.getMessage("mensagem.editar.proposta.qtd.minima.propostas", responsavel, String.valueOf(qtdMinPropostas)));
                } else if (propostasPgtSaldo.size() > qtdMaxPropostas) {
                    throw new Exception(ApplicationResourcesHelper.getMessage("mensagem.editar.proposta.qtd.maxima.propostas", responsavel, String.valueOf(qtdMaxPropostas)));
                }
                if (prazosObrigatorios != null && !prazosObrigatorios.isEmpty()) {
                    for (Integer prazoObrigatorio : prazosObrigatorios) {
                        // Verifica se foi informado proposta para o prazo
                        boolean usouPrazo = false;
                        for (PropostaPagamentoDividaTO proposta : propostasPgtSaldo) {
                            if (proposta.getPpdPrazo().equals(prazoObrigatorio)) {
                                usouPrazo = true;
                                break;
                            }
                        }
                        if (!usouPrazo) {
                            throw new Exception(ApplicationResourcesHelper.getMessage("mensagem.editar.proposta.informe.proposta.num.prestacoes", responsavel, prazoObrigatorio.toString()));
                        }
                    }
                }

                // Salva as propostas para o contrato
                financiamentoDividaController.informarPropostasPgtSdvTerceiros(adeCodigo, propostasPgtSaldo, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.editar.proposta.alteracoes.salvas.sucesso", responsavel));
            }

            // Redireciona para página que chamou
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

}
