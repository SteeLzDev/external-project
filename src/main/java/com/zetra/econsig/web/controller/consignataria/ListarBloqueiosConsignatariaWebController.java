package com.zetra.econsig.web.controller.consignataria;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.agendamento.AgendamentoController;
import com.zetra.econsig.service.comunicacao.ComunicacaoController;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.mensagem.MensagemController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.values.AgendamentoEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ListarBloqueiosConsignatariaWebController</p>
 * <p>Description: Controlador Web para o caso de uso Lista de Bloqueios de Consignatária.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Date$
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarBloqueiosConsignataria" })
public class ListarBloqueiosConsignatariaWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarBloqueiosConsignatariaWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ComunicacaoController comunicacaoController;

    @Autowired
    private MensagemController mensagemController;

    @Autowired
    private AgendamentoController agendamentoController;

    @Autowired
    private ServicoController servicoController;

    @RequestMapping(params = { "acao=iniciar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            String csaCodigo = null;
            String csaNome = null;

            if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCodigoEntidade();
                csaNome = responsavel.getNomeEntidade();
            } else if (responsavel.isCseSup()) {
                csaCodigo = request.getParameter("CSA_CODIGO");
                if (!TextHelper.isNull(csaCodigo)) {
                    ConsignatariaTransferObject csa = consignatariaController.findConsignataria(csaCodigo, responsavel);
                    csaNome = csa.getCsaNome();
                }
            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            if (!responsavel.isCsa() && !TextHelper.isNull(csaCodigo)) {
                if (!SynchronizerToken.isTokenValid(request)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            } else {
                SynchronizerToken.saveToken(request);
            }

            boolean podeListarBloqueiosCsa = responsavel.temPermissao(CodedValues.FUN_LISTAR_BLOQUEIOS_CSA);
            if (!podeListarBloqueiosCsa) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            List<TransferObject> lstConsignatarias = null;
            try {
                if (responsavel.isCseSup()) {
                    CustomTransferObject criterio = null;
                    lstConsignatarias = consignatariaController.lstConsignatarias(criterio, responsavel);
                }
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                lstConsignatarias = new ArrayList<>();
            }

            // Módulo de compra habilitado?
            boolean temModuloCompra = ParamSist.paramEquals(CodedValues.TPC_PERMITE_COMPRA_CONTRATO, CodedValues.TPC_SIM, responsavel);
            // Módulo de bloqueio de consignatária pela não liquidação de contrato com saldo devedor solicitado e pago pelo servidor
            boolean bloqueiaCsaLiqSaldoPago = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_BLOQ_CSA_N_LIQ_ADE_SALDO_PAGO_SER, responsavel);

            // Informacoes sobre os bloqueios de solicitacao de saldo devedor
            List<TransferObject> servicosCadastroSaldo = null;
            if (!TextHelper.isNull(csaCodigo)) {
                List<String> pseVlrs = new ArrayList<>();
                pseVlrs.add(CodedValues.USUARIO_CADASTRA_SALDO_DEVEDOR);
                pseVlrs.add(CodedValues.SISTEMA_CALCULA_SALDO_DEVEDOR);
                pseVlrs.add(CodedValues.CADASTRA_E_CALCULA_SALDO_DEVEDOR);
                servicosCadastroSaldo = servicoController.selectServicosComParametro(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR, null, AcessoSistema.ENTIDADE_CSA, csaCodigo, pseVlrs, false, false, responsavel);
            }

            List<TransferObject> adesSaldoDev = null;

            int totalSaldoDev = 0;
            int offsetSaldoDev = 0;

            // Monta lista de parâmetros através dos parâmetros de request
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());
            params.remove("offsetSaldoDev");
            List<String> requestParams = new ArrayList<>(params);
            int sizeSaldoDev = JspHelper.LIMITE;
            if (!TextHelper.isNull(csaCodigo) && servicosCadastroSaldo != null && !servicosCadastroSaldo.isEmpty()) {
                List<String> svcCodigos = new ArrayList<>();
                for (TransferObject to : servicosCadastroSaldo) {
                    svcCodigos.add(to.getAttribute(Columns.SVC_CODIGO).toString());
                }

                CustomTransferObject criterioSaldoDev = new CustomTransferObject();
                criterioSaldoDev.setAttribute("infSaldoDevedor", "bloq");
                criterioSaldoDev.setAttribute("diasSolicitacaoSaldo", "0");
                if (bloqueiaCsaLiqSaldoPago) {
                    criterioSaldoDev.setAttribute("diasSolicitacaoSaldoPagaAnexo", "0");
                }

                List<String> sadCodigos = new ArrayList<>();
                sadCodigos.add(CodedValues.SAD_DEFERIDA);
                sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
                sadCodigos.add(CodedValues.SAD_SUSPENSA);
                sadCodigos.add(CodedValues.SAD_SUSPENSA_CSE);
                sadCodigos.add(CodedValues.SAD_ESTOQUE);
                sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
                sadCodigos.add(CodedValues.SAD_ESTOQUE_NAO_LIBERADO);
                sadCodigos.add(CodedValues.SAD_EMCARENCIA);
                sadCodigos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);
                sadCodigos.add(CodedValues.SAD_AGUARD_LIQUIDACAO);

                totalSaldoDev = pesquisarConsignacaoController.countPesquisaAutorizacao("CSA", csaCodigo, null, null, null, sadCodigos, svcCodigos, criterioSaldoDev, responsavel);
                offsetSaldoDev = 0;
                try {
                    offsetSaldoDev = Integer.parseInt(request.getParameter("offsetSaldoDev"));
                } catch (Exception ex) {
                }
                adesSaldoDev = pesquisarConsignacaoController.pesquisaAutorizacao("CSA", csaCodigo, null, null, null, sadCodigos, svcCodigos, offsetSaldoDev, sizeSaldoDev, criterioSaldoDev, responsavel);
            }

            configurarPaginador("SaldoDev", "../v3/listarBloqueiosConsignataria", "rotulo.paginacao.titulo.bloqueio.pendencia.saldo.devedor", totalSaldoDev, sizeSaldoDev, requestParams, false, request, model);

            // Informacoes sobre os bloqueios de comunicacao
            boolean podeLerComunicacao = responsavel.temPermissao(CodedValues.FUN_LER_COMUNICACAO);

            // Monta lista de parâmetros através dos parâmetros de request
            params = new HashSet<>(request.getParameterMap().keySet());
            params.remove("offsetComunicacao");
            requestParams = new ArrayList<>(params);

            int totalComunicacao = 0;
            int offsetComunicacao = 0;

            List<TransferObject> comunicacoes = null;
            int sizeComunicacao = JspHelper.LIMITE;
            if (!TextHelper.isNull(csaCodigo) && podeLerComunicacao) {

                CustomTransferObject criterioComunicacao = new CustomTransferObject();
                criterioComunicacao.setAttribute(Columns.CSA_CODIGO, csaCodigo);
                criterioComunicacao.setAttribute(Columns.CMN_PENDENCIA, Boolean.TRUE);
                criterioComunicacao.setAttribute("APENAS_CMN_PAI", Boolean.TRUE);
                criterioComunicacao.setAttribute("BLOQUEIO_CSA", Boolean.TRUE);

                totalComunicacao = comunicacaoController.countComunicacoes(criterioComunicacao, responsavel);
                offsetComunicacao = 0;
                try {
                    offsetComunicacao = Integer.parseInt(request.getParameter("offsetComunicacao"));
                } catch (Exception ex) {
                }
                comunicacoes = comunicacaoController.listComunicacoes(criterioComunicacao, offsetComunicacao, sizeComunicacao, responsavel);
            }

            configurarPaginador("Comunicacao", "../v3/listarBloqueiosConsignataria", "rotulo.paginacao.titulo.bloqueio.pendencia.comunicacao", totalComunicacao, sizeComunicacao, requestParams, false, request, model);

            // Mensagem
            boolean podeLerMensagem = false;

            List<TransferObject> mensagens = new ArrayList<>();

            int totalMensagem = 0;
            int offsetMensagem = 0;

            // Monta lista de parâmetros através dos parâmetros de request
            params = new HashSet<>(request.getParameterMap().keySet());
            params.remove("offsetMensagem");
            requestParams = new ArrayList<>(params);

            int sizeMensagem = JspHelper.LIMITE;
            if (!TextHelper.isNull(csaCodigo)) {
                CustomTransferObject criterioMensagem = new CustomTransferObject();
                criterioMensagem.setAttribute(Columns.CSA_CODIGO, csaCodigo);

                StringBuilder queryString = new StringBuilder("");
                queryString.append(Columns.getColumnName(Columns.CSA_CODIGO)).append("(").append(csaCodigo);

                // Lista mensagens com pendência de leitura da consignatária
                List<TransferObject> lstMensagemComPendenciaLeitura = mensagemController.lstMensagemCsaBloqueio(csaCodigo, responsavel);
                List<String> menCodigo = new ArrayList<>();
                if (lstMensagemComPendenciaLeitura != null && !lstMensagemComPendenciaLeitura.isEmpty()) {
                    Iterator<TransferObject> iteMsg = lstMensagemComPendenciaLeitura.iterator();
                    while (iteMsg.hasNext()) {
                        menCodigo.add(iteMsg.next().getAttribute(Columns.MEN_CODIGO).toString());
                    }
                }
                criterioMensagem.setAttribute(Columns.MEN_CODIGO, menCodigo);

                if (menCodigo != null && !menCodigo.isEmpty()) {
                    totalMensagem = mensagemController.countMensagem(criterioMensagem, responsavel);
                    podeLerMensagem = totalMensagem > 0;

                    try {
                        offsetMensagem = Integer.parseInt(request.getParameter("offsetMensagem"));
                    } catch (Exception ex) {
                    }
                    mensagens = mensagemController.lstMensagem(criterioMensagem, offsetMensagem, sizeMensagem, responsavel);
                }
            }

            configurarPaginador("Mensagem", "../v3/listarBloqueiosConsignataria", "rotulo.paginacao.titulo.bloqueio.pendencia.confirmacao.leitura.mensagem", totalMensagem, sizeMensagem, requestParams, false, request, model);

            // DESENV-13634 Informacoes sobre os bloqueios de consignações feitas por usuário CSA/COR sem o mínimo de anexos exigidos pelo parâmetro 284
            List<TransferObject> adesSemMinAnexos = null;
            if (ParamSist.getBoolParamSist(CodedValues.TPC_BLOQUEIA_CSA_ADE_SEM_MIN_ANEXOS, responsavel) && !TextHelper.isNull(csaCodigo)) {

                // Monta lista de parâmetros através dos parâmetros de request
                params = new HashSet<>(request.getParameterMap().keySet());
                params.remove("offsetAdeSemMinAnexos");
                requestParams = new ArrayList<>(params);

                int totalAdeSemMinAnexos = 0;
                int offsetAdeSemMinAnexos = 0;
                int sizeAdeSemMinAnexos = JspHelper.LIMITE;
                TransferObject agdAdesSemNumMinAnexos = agendamentoController.findAgendamento(AgendamentoEnum.BLOQUEIO_CSA_ADE_SEM_NUM_ANEXOS_MINIMO.getCodigo(), responsavel);
                Date dataIniVerificacao = (Date) agdAdesSemNumMinAnexos.getAttribute(Columns.AGD_DATA_CADASTRO);

                StringBuilder queryString = new StringBuilder("");
                queryString.append(Columns.getColumnName(Columns.CSA_CODIGO)).append("(").append(csaCodigo);

                totalAdeSemMinAnexos = consignatariaController.countAdesUsuCsaCorSemNumAnexoMin(csaCodigo, dataIniVerificacao, responsavel);
                offsetAdeSemMinAnexos = 0;
                try {
                    offsetAdeSemMinAnexos = Integer.parseInt(request.getParameter("offsetAdeSemMinAnexos"));
                } catch (Exception ex) {
                }
                adesSemMinAnexos = consignatariaController.lstAdesUsuCsaCorSemNumAnexoMin(csaCodigo, dataIniVerificacao, offsetAdeSemMinAnexos, totalAdeSemMinAnexos, responsavel);

                configurarPaginador("AdesSemMinAnexos", "../v3/listarBloqueiosConsignataria", "rotulo.paginacao.titulo.bloqueio.pendencia.comunicacao", totalAdeSemMinAnexos, sizeAdeSemMinAnexos, requestParams, false, request, model);
            }

            // DESENV-18230 : Lista os serviços que estão com cadastro de CET / Taxa de juros expirado
            List<TransferObject> lstSvcCetExpirado = null;
            if (ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_CSA_POR_CET_EXPIRADO, CodedValues.TPC_SIM, responsavel) && !TextHelper.isNull(csaCodigo)) {

                // Monta lista de parâmetros através dos parâmetros de request
                params = new HashSet<>(request.getParameterMap().keySet());
                params.remove("offsetSvcCetExpirado");
                requestParams = new ArrayList<>(params);

                StringBuilder queryString = new StringBuilder("");
                queryString.append(Columns.getColumnName(Columns.CSA_CODIGO)).append("(").append(csaCodigo);

                int sizeSvcCetExpirado = JspHelper.LIMITE;
                int totalSvcCetExpirado = consignatariaController.countServicosCsaCetExpirado(csaCodigo, responsavel);
                int offsetSvcCetExpirado = 0;
                try {
                    offsetSvcCetExpirado = Integer.parseInt(request.getParameter("offsetSvcCetExpirado"));
                } catch (Exception ex) {
                }
                lstSvcCetExpirado = consignatariaController.lstServicosCsaCetExpirado(csaCodigo, offsetSvcCetExpirado, sizeSvcCetExpirado, responsavel);

                configurarPaginador("SvcCetExpirado", "../v3/listarBloqueiosConsignataria", "rotulo.paginacao.titulo.bloqueio.pendencia.comunicacao", totalSvcCetExpirado, sizeSvcCetExpirado, requestParams, false, request, model);
            }

            List<TransferObject> adeRejeitoPgt = null;

            // Monta lista de parâmetros através dos parâmetros de request
            params = new HashSet<>(request.getParameterMap().keySet());
            params.remove("offsetRejPagDev");
            requestParams = new ArrayList<>(params);

            if (!TextHelper.isNull(csaCodigo)) {
                int totalRejPagDev = pesquisarConsignacaoController.countRejeitoPgtSaldo(csaCodigo, responsavel);

                int sizeRejPagDev = JspHelper.LIMITE;
                int offsetRejPagDev = 0;
                try {
                    offsetRejPagDev = Integer.parseInt(request.getParameter("offsetRejPagDev"));
                } catch (Exception ex) {
                }

                // Lista bloqueios por rejeito no pagamento de saldo devedor
                List<String> pseValor = new ArrayList<>();
                pseValor.add("1");
                if (!(servicoController.selectServicosComParametro(CodedValues.TPS_REJEICAO_PGT_SDV_BLOQUEIA_AMBAS_CSAS, null, AcessoSistema.ENTIDADE_CSE, csaCodigo, pseValor, false, false, responsavel).isEmpty())) {
                    adeRejeitoPgt = pesquisarConsignacaoController.pesquisaRejeitoPgtSaldo(csaCodigo, offsetRejPagDev, sizeRejPagDev, responsavel);
                }

                configurarPaginador("RejPagDev", "../v3/listarBloqueiosConsignataria", "rotulo.paginacao.titulo.bloqueio.rejeito.pagamento.saldo.devedor", totalRejPagDev, sizeSaldoDev, requestParams, false, request, model);
            }

            if (!temModuloCompra && (servicosCadastroSaldo == null || servicosCadastroSaldo.isEmpty()) && !podeLerComunicacao && !podeLerMensagem) {
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informacao.nao.existem.bloqueios", responsavel));
            }

            // INÍCIO - DESENV-12092: Listar Bloqueios de Consignatária
            List<TransferObject> adesSolLiqNaoAtendida = null;

            if (podeListarBloqueiosCsa) {
            	// Monta lista de parâmetros através dos parâmetros de request
            	params = new HashSet<>(request.getParameterMap().keySet());
            	params.remove("offsetAdesSolLiqNaoAtendida");
            	requestParams = new ArrayList<>(params);

            	int sizeAdesSolLiqNaoAtendida = JspHelper.LIMITE;
            	if (!TextHelper.isNull(csaCodigo)) {

            		StringBuilder queryString = new StringBuilder("");
            		queryString.append(Columns.getColumnName(Columns.CSA_CODIGO)).append("(").append(csaCodigo);

            		int totalAdesSolLiqNaoAtendida = pesquisarConsignacaoController.countConsignacoesSolicitacoesLiquidacaoNaoAntendida(csaCodigo, responsavel);
//            		int offsetAdesSolLiqNaoAtendida = 0;
//            		try {
//            			offsetAdesSolLiqNaoAtendida = Integer.parseInt(request.getParameter("offsetAdesSolLiqNaoAtendida"));
//            		} catch (Exception ex) {
//            		}
            		// TODO : passar offsetAdesSolLiqNaoAtendida para a pesquisa
            		adesSolLiqNaoAtendida = pesquisarConsignacaoController.pesquisarConsignacoesSolicitacoesLiquidacaoNaoAntendida(csaCodigo, false, responsavel);

            		configurarPaginador("SolicitacaoLiquidacaoNaoAtendida", "../v3/listarBloqueiosConsignataria", "rotulo.paginacao.titulo.bloqueio.pendencia.comunicacao", totalAdesSolLiqNaoAtendida, sizeAdesSolLiqNaoAtendida, requestParams, false, request, model);
            	}
            }

            if (!temModuloCompra && (servicosCadastroSaldo == null || servicosCadastroSaldo.isEmpty()) && !podeLerComunicacao && !podeLerMensagem) {
            	session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informacao.nao.existem.bloqueios", responsavel));
            }
            // FIM - DESENV-12092: Listar Bloqueios de Consignatária

            // Bloco de código que gera link de paginação para a ListaAcompanhamentoCompraTag.
            int size = JspHelper.LIMITE;
            int offsetTag = 0;
            try {
                offsetTag = Integer.parseInt(request.getParameter("offsetTag"));
            } catch (Exception ex) {
            }

            int total = 0;
            try {
                total = Integer.parseInt(request.getParameter("total"));
            } catch (Exception ex) {
            }

            String linkListagemTag = "../v3/listarBloqueiosConsignataria?acao=iniciar";

            configurarPaginador(linkListagemTag, "rotulo.paginacao.acompanhamento.compra", total, size, requestParams, false, request, model);
            String linkPaginacaoTag = linkListagemTag + "&pesquisar=true";
            if (request.getQueryString() != null && !request.getQueryString().equals("")) {
                linkPaginacaoTag += "&" + request.getQueryString();
            }
            linkPaginacaoTag = SynchronizerToken.updateTokenInURL(linkPaginacaoTag, request);

            model.addAttribute("linkPaginacaoTag", linkPaginacaoTag);
            model.addAttribute("offsetTag", offsetTag);
            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("csaNome", csaNome);
            model.addAttribute("lstConsignatarias", lstConsignatarias);
            model.addAttribute("temModuloCompra", temModuloCompra);
            model.addAttribute("adeRejeitoPgt", adeRejeitoPgt);
            model.addAttribute("servicosCadastroSaldo", servicosCadastroSaldo);
            model.addAttribute("bloqueiaCsaLiqSaldoPago", bloqueiaCsaLiqSaldoPago);
            model.addAttribute("adesSaldoDev", adesSaldoDev);
            model.addAttribute("podeLerComunicacao", podeLerComunicacao);
            model.addAttribute("comunicacoes", comunicacoes);
            model.addAttribute("mensagens", mensagens);
            model.addAttribute("adesSemMinAnexos", adesSemMinAnexos);
            model.addAttribute("adesSolLiqNaoAtendida", adesSolLiqNaoAtendida);
            model.addAttribute("lstSvcCetExpirado", lstSvcCetExpirado);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/listarBloqueiosConsignataria/listarBloqueiosConsignataria", request, session, model, responsavel);
    }
}
