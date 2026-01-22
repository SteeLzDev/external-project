package com.zetra.econsig.web.controller.compra;

import java.io.File;
import java.math.BigDecimal;
import java.security.KeyPair;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.parametros.RenegociarConsignacaoParametros;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.helper.consignacao.AutorizacaoHelper;
import com.zetra.econsig.helper.criptografia.JCryptOld;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.margem.ControleConsulta;
import com.zetra.econsig.helper.processareserva.ProcessaReservaMargem;
import com.zetra.econsig.helper.processareserva.ProcessaReservaMargemFactory;
import com.zetra.econsig.helper.seguranca.AcessoFuncaoServico;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.service.consignacao.RenegociarConsignacaoController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.web.servlet.AudioCaptchaServlet;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ParcelaControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.consignacao.ReservaMargemHelper;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.margem.MargemDisponivel;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.ParcelaDescontoPeriodo;
import com.zetra.econsig.persistence.entity.RelacionamentoServico;
import com.zetra.econsig.persistence.entity.RelacionamentoServicoHome;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.parcela.ParcelaController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.CodedNames;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;
import com.zetra.econsig.web.controller.leilao.SolicitarLeilaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(method = {RequestMethod.POST}, value = {"/v3/solicitarPortabilidade"})
public class SolicitarPortabilidadeWebController extends SolicitarLeilaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SolicitarPortabilidadeWebController.class);

    private static final String DATATABLE_COLUMN_TITLE = "{ title: '%s' },";
    private static final String DATATABLE_LINE_VALUES = "[ '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s' ],";
    private static final String DATATABLE_LINE_SELECTION = "<div class=\"actions\"><a class=\"ico-action\" href=\"#\" onclick=\"postData(\\'%s\\')\" role=\"button\"><div class=\"form-inline\"><span class=\"mr-1\" title=\"\" aria-label=\"%s\"><svg><use xlink:href=\"#i-confirmar\"></use></svg></span> %s</div></a></div>";

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    @Qualifier("renegociarConsignacaoController")
    private RenegociarConsignacaoController renegociarConsignacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private SimulacaoController simulacaoController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Autowired
    private ParcelaController parcelaController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        super.configurarPagina(request, session, model, responsavel);

        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.menu.solicitar.portabilidade", responsavel));
        model.addAttribute("acaoFormulario", "../v3/solicitarPortabilidade");
        if (!ParamSist.paramEquals(CodedValues.TPC_SOLICITAR_PORTABILIDADE_COM_RANKING_CONSIGNATARIA, CodedValues.TPC_SIM, responsavel)) {
            model.addAttribute("leilaoReverso", Boolean.TRUE);
        } else {
            model.addAttribute("leilaoReverso", Boolean.FALSE);
        }
    }

    @Override
    @RequestMapping(params = {"acao=iniciar"})
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);

        CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "solicitar_portabilidade");

        String rseCodigo = responsavel.getRseCodigo();
        List<TransferObject> lstConsignacao = null;

        try {
            lstConsignacao = pesquisarConsignacaoController.pesquisaAutorizacao(responsavel.getTipoEntidade(), responsavel.getUsuCodigo(), rseCodigo, null, null, CodedValues.SAD_CODIGOS_PORTABILIDADE, null, -1, -1, criterio, responsavel);
            lstConsignacao = parametroController.filtraAdeRestringePortabilidade(lstConsignacao, rseCodigo, null, responsavel);
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (lstConsignacao == null || lstConsignacao.isEmpty()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.solicitar.portabilidade.nenhuma.consignacao.encontrada", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Se a margem está negativa, e não permite portabilidade com margem negativa, retorna erro ao usuário
        boolean permitePortabilidadeMargemNegativa = ParamSist.paramEquals(CodedValues.TPC_PERMITE_PORTABILIDADE_MARGEM_NEGATIVA, CodedValues.TPC_SIM, responsavel);
        if (!permitePortabilidadeMargemNegativa) {
            // Valida as margens que as consignações incidem
            try {
                boolean algumaMargemNegativa = false;

                // Consulta margem passando valor ZERADO, pois só importa se a margem não está negativa, e com isso
                // a configuração de exibição de margem para papel de servidor não importa
                List<MargemTO> lstMargens = consultarMargemController.consultarMargem(rseCodigo, BigDecimal.ZERO, null, null, true, true, responsavel);

                // Cria um Mapa com código da margem e se está negativa
                Map<Short, Boolean> margemNegativa = new HashMap<>();
                for (MargemTO margem : lstMargens) {
                    margemNegativa.put(margem.getMarCodigo(), !margem.temMargemDisponivel());
                    algumaMargemNegativa |= !margem.temMargemDisponivel();
                }

                if (algumaMargemNegativa) {
                    List<TransferObject> lstConsignacaoPermitia = new ArrayList<>(lstConsignacao.size());
                    for (TransferObject consignacao : lstConsignacao) {
                        Short adeIncMargem = consignacao.getAttribute(Columns.ADE_INC_MARGEM) != null ? (Short) consignacao.getAttribute(Columns.ADE_INC_MARGEM) : CodedValues.INCIDE_MARGEM_SIM;
                        if (margemNegativa.containsKey(adeIncMargem) && !margemNegativa.get(adeIncMargem)) {
                            lstConsignacaoPermitia.add(consignacao);
                        }
                    }

                    if (lstConsignacaoPermitia.isEmpty()) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.solicitar.portabilidade.margem.negativa.nao.permitida", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }

                    lstConsignacao = lstConsignacaoPermitia;
                }
            } catch (ServidorControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        // Link para seleção da consignação
        String linkSelecionar = SynchronizerToken.updateTokenInURL("../v3/solicitarPortabilidade?acao=simular&ade=%s&nse=%s", request);

        model.addAttribute("tituloColunas", gerarTituloColunas(responsavel));
        model.addAttribute("conteudoLinhas", gerarConteudoLinhas(lstConsignacao, linkSelecionar, responsavel));

        // Passa mensagem de instrução sobre o processo somente se o parametro abaixo não estiver marcado como S
        if (!ParamSist.paramEquals(CodedValues.TPC_SOLICITAR_PORTABILIDADE_COM_RANKING_CONSIGNATARIA, CodedValues.TPC_SIM, responsavel)) {
            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.solicitar.portabilidade.instrucoes", responsavel));
        }
        // Redireciona para a página de listagem
        return viewRedirect("jsp/solicitarPortabilidade/listarConsignacaoParaPortabilidade", request, session, model, responsavel);
    }

    private String gerarTituloColunas(AcessoSistema responsavel) {
        StringBuilder conteudo = new StringBuilder();

        conteudo.append(String.format(DATATABLE_COLUMN_TITLE, ApplicationResourcesHelper.getMessage("rotulo.consignacao.numero", responsavel)));
        conteudo.append(String.format(DATATABLE_COLUMN_TITLE, ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel)));
        conteudo.append(String.format(DATATABLE_COLUMN_TITLE, ApplicationResourcesHelper.getMessage("rotulo.servico.singular", responsavel)));
        conteudo.append(String.format(DATATABLE_COLUMN_TITLE, ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.inclusao", responsavel)));
        conteudo.append(String.format(DATATABLE_COLUMN_TITLE, ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela.abreviado", responsavel)));
        conteudo.append(String.format(DATATABLE_COLUMN_TITLE, ApplicationResourcesHelper.getMessage("rotulo.consignacao.prazo.abreviado", responsavel)));
        conteudo.append(String.format(DATATABLE_COLUMN_TITLE, ApplicationResourcesHelper.getMessage("rotulo.consignacao.pagas", responsavel)));
        conteudo.append(String.format(DATATABLE_COLUMN_TITLE, ApplicationResourcesHelper.getMessage("rotulo.consignacao.status", responsavel)));
        conteudo.append(String.format(DATATABLE_COLUMN_TITLE, ApplicationResourcesHelper.getMessage("rotulo.acoes", responsavel)));

        return conteudo.toString();
    }

    private String gerarConteudoLinhas(List<TransferObject> lstConsignacao, String linkSelecionar, AcessoSistema responsavel) {
        StringBuilder conteudo = new StringBuilder();

        final String rotuloSelecionar = ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar", responsavel);
        final String rotuloPrazoIndeterminado = ApplicationResourcesHelper.getMessage("rotulo.indeterminado.abreviado", responsavel);
        final String rotuloCodRegCredito = ApplicationResourcesHelper.getMessage("rotulo.consignacao.cod.reg.credito", responsavel);

        for (TransferObject ade : lstConsignacao) {
            String adeCodigo = ade.getAttribute(Columns.ADE_CODIGO).toString();
            String adeCodReg = ((ade.getAttribute(Columns.ADE_COD_REG) != null && !ade.getAttribute(Columns.ADE_COD_REG).equals("")) ? ade.getAttribute(Columns.ADE_COD_REG).toString() : CodedValues.COD_REG_DESCONTO);
            String consignataria = ade.getAttribute(Columns.CSA_IDENTIFICADOR) + " - " + (!TextHelper.isNull(ade.getAttribute(Columns.CSA_NOME_ABREV)) ? ade.getAttribute(Columns.CSA_NOME_ABREV).toString() : ade.getAttribute(Columns.CSA_NOME).toString());
            String servico = (!TextHelper.isNull(ade.getAttribute(Columns.CNV_COD_VERBA)) ? ade.getAttribute(Columns.CNV_COD_VERBA).toString() : ade.getAttribute(Columns.SVC_IDENTIFICADOR).toString()) + (!TextHelper.isNull(ade.getAttribute(Columns.ADE_INDICE)) ? ade.getAttribute(Columns.ADE_INDICE).toString() : "") + " - " + (ade.getAttribute(Columns.SVC_DESCRICAO).toString()) + (adeCodReg.equals(CodedValues.COD_REG_ESTORNO) ? " - " + rotuloCodRegCredito : "");
            String adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
            String adeTipoVlr = ParamSvcTO.getDescricaoTpsTipoVlr((String) ade.getAttribute(Columns.ADE_TIPO_VLR));
            String adePrazo = (ade.getAttribute(Columns.ADE_PRAZO) != null ? ade.getAttribute(Columns.ADE_PRAZO).toString() : rotuloPrazoIndeterminado);
            String adePrdPagas = (ade.getAttribute(Columns.ADE_PRD_PAGAS) != null ? ade.getAttribute(Columns.ADE_PRD_PAGAS).toString() : "0");
            String nseCodigo = (ade.getAttribute(Columns.SVC_NSE_CODIGO) != null ? ade.getAttribute(Columns.SVC_NSE_CODIGO).toString() : "0");

            String adeData = null;
            try {
                adeData = DateHelper.reformat(ade.getAttribute(Columns.ADE_DATA).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
            } catch (ParseException ex) {
                LOG.error(ex.getMessage(), ex);
                adeData = "";
            }

            String adeVlr = null;
            try {
                adeVlr = (!TextHelper.isNull(ade.getAttribute(Columns.ADE_VLR)) ? NumberHelper.reformat(ade.getAttribute(Columns.ADE_VLR).toString(), "en", NumberHelper.getLang()) : "");
                adeVlr = String.format("%s %s", adeTipoVlr, adeVlr);
            } catch (ParseException ex) {
                LOG.error(ex.getMessage(), ex);
                adeVlr = "";
            }

            String status = ade.getAttribute(Columns.SAD_DESCRICAO).toString();
            if (ade.getAttribute(Columns.ADE_DATA_STATUS) != null && !ParamSist.paramEquals(CodedValues.TPC_EXIBE_ADE_DATA_STATUS_CONSULTAR_CONSIGNACAO, CodedValues.TPC_NAO, responsavel)) {
                String dataAtualizacao = DateHelper.toDateTimeString((Date) ade.getAttribute(Columns.ADE_DATA_STATUS));
                status = String.format("%s (%s)", status, dataAtualizacao);
            }

            conteudo.append(String.format(DATATABLE_LINE_VALUES,
                    TextHelper.forJavaScriptBlock(adeNumero),
                    TextHelper.forJavaScriptBlock(consignataria),
                    TextHelper.forJavaScriptBlock(servico),
                    TextHelper.forJavaScriptBlock(adeData),
                    TextHelper.forJavaScriptBlock(adeVlr),
                    TextHelper.forJavaScriptBlock(adePrazo),
                    TextHelper.forJavaScriptBlock(adePrdPagas),
                    TextHelper.forJavaScriptBlock(status),
                    String.format(DATATABLE_LINE_SELECTION, String.format(linkSelecionar, adeCodigo, nseCodigo.equals(CodedValues.NSE_CARTAO) ? "S" : "N"), rotuloSelecionar, rotuloSelecionar)));
        }

        return conteudo.toString();
    }

    @Override
    @RequestMapping(params = {"acao=simular"})
    public String simular(@RequestParam(value = "ade", required = true, defaultValue = "") String adeCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        TransferObject autdes = null;
        TransferObject primeiroResultadoValido = null;
        String svcCodigoOrigem = null;
        try {
            autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
        } catch (AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (autdes != null) {
            boolean portabilidadeCartao = request.getParameter("nse") != null && request.getParameter("nse").equals("S");

            String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();
            String svcDescricao = autdes.getAttribute(Columns.SVC_DESCRICAO).toString();
            String orgCodigo = autdes.getAttribute(Columns.ORG_CODIGO).toString();
            String rseCodigo = autdes.getAttribute(Columns.RSE_CODIGO).toString();
            String rseMatricula = autdes.getAttribute(Columns.RSE_MATRICULA).toString();
            if (portabilidadeCartao) {
                return listaCsaPortabilidadeCartao(adeCodigo, (String) autdes.getAttribute(Columns.CSA_CODIGO), svcCodigo, rseCodigo, orgCodigo, rseMatricula, request, response, session, model);
            }
            BigDecimal adeVlr = (autdes.getAttribute(Columns.ADE_VLR) != null ? (BigDecimal) autdes.getAttribute(Columns.ADE_VLR) : BigDecimal.ZERO);
            Integer pagas = (autdes.getAttribute(Columns.ADE_PRD_PAGAS) != null ? (Integer) autdes.getAttribute(Columns.ADE_PRD_PAGAS) : 0);
            Integer prazo = (Integer) autdes.getAttribute(Columns.ADE_PRAZO);

            Integer qtdPrdEmProcessamento = 0;
            if (ParamSist.paramEquals(CodedValues.TPC_SUBTRAIR_PARCELAS_AGUARD_PROCESSAMENTO_SOL_PORTABILIDADE, CodedValues.TPC_SIM, responsavel)) {
                try {
                    List<ParcelaDescontoPeriodo> parcelasEmProcessamento = parcelaController.findByAutDescontoStatus(adeCodigo, CodedValues.SPD_EMPROCESSAMENTO, responsavel);
                    qtdPrdEmProcessamento = parcelasEmProcessamento != null ? parcelasEmProcessamento.size() : 0;
                } catch (ParcelaControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            Integer prazoRest = prazo - pagas - qtdPrdEmProcessamento;
            String adePeriodicidade = PeriodoHelper.getPeriodicidadeFolha(responsavel);

            // Define o svcCodigoOrigem como sendo o serviço da consignação a ser transferida
            svcCodigoOrigem = svcCodigo;

            try {
                if (ParamSist.paramEquals(CodedValues.TPC_SOLICITAR_PORTABILIDADE_COM_RANKING_CONSIGNATARIA, CodedValues.TPC_SIM, responsavel)) {
                    String link = "../v3/simularConsignacao?acao=iniciarSimulacao&SVC_CODIGO=" + svcCodigo + "&RSE_CODIGO=" + rseCodigo + "&titulo=" + TextHelper.encode64(svcDescricao) + "&tpcSolicitarPortabilidadeRanking=true&ADE_CODIGO=" + adeCodigo + "&_skip_history_=true";
                    request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(link, request)));
                    return "jsp/redirecionador/redirecionar";
                } else {
                    primeiroResultadoValido = simularPortabilidade(svcCodigoOrigem, orgCodigo, rseCodigo, adeVlr, prazoRest, adePeriodicidade, responsavel);
                }

                if (primeiroResultadoValido == null) {
                    // Se não há resultado de simulação no serviço da consignação selecionada, então tenta
                    // nos demais serviços que permitem solicitação de leilão
                    List<TransferObject> lstServicos = convenioController.lstCnvEntidade(responsavel.getCodigoEntidade(), responsavel.getTipoEntidade(), "leilao", responsavel);
                    for (TransferObject servico : lstServicos) {
                        svcCodigoOrigem = servico.getAttribute(Columns.SVC_CODIGO).toString();
                        primeiroResultadoValido = simularPortabilidade(svcCodigoOrigem, orgCodigo, rseCodigo, adeVlr, prazoRest, adePeriodicidade, responsavel);
                        if (primeiroResultadoValido != null) {
                            break;
                        }
                    }
                }
            } catch (ZetraException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        if (primeiroResultadoValido != null) {
            boolean simulacaoPorTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_POR_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);
            boolean simulacaoMetodoMexicano = ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_MEXICANO, responsavel);
            boolean simulacaoMetodoBrasileiro = ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_BRASILEIRO, responsavel);

            String tac = "", iof = "";
            String cat = "", iva = ""; // simulacaoMetodoMexicano

            String csaCodigo = (String) primeiroResultadoValido.getAttribute(Columns.CSA_CODIGO);
            String csaNome = (String) primeiroResultadoValido.getAttribute("TITULO");
            String svcCodigo = (String) primeiroResultadoValido.getAttribute(Columns.SVC_CODIGO);
            String cftCodigo = (String) primeiroResultadoValido.getAttribute(Columns.CFT_CODIGO);
            String dtjCodigo = (String) primeiroResultadoValido.getAttribute(Columns.DTJ_CODIGO);
            String vlrParcela = primeiroResultadoValido.getAttribute("VLR_PARCELA").toString();
            String vlrLiberado = primeiroResultadoValido.getAttribute("VLR_LIBERADO").toString();
            String numParcelas = primeiroResultadoValido.getAttribute(Columns.PRZ_VLR).toString();
            String ranking = (String) primeiroResultadoValido.getAttribute("RANKING");

            try {
                if (simulacaoPorTaxaJuros) {
                    if (simulacaoMetodoMexicano) {
                        cat = NumberHelper.reformat((primeiroResultadoValido.getAttribute("CAT") != null) ? primeiroResultadoValido.getAttribute("CAT").toString() : "0.00", "en", NumberHelper.getLang(), true);
                        iva = NumberHelper.reformat((primeiroResultadoValido.getAttribute("IVA") != null) ? primeiroResultadoValido.getAttribute("IVA").toString() : "0.00", "en", NumberHelper.getLang(), true);
                    } else if (simulacaoMetodoBrasileiro) {
                        tac = NumberHelper.reformat((primeiroResultadoValido.getAttribute("TAC_FINANCIADA") != null) ? primeiroResultadoValido.getAttribute("TAC_FINANCIADA").toString() : "0.00", "en", NumberHelper.getLang(), true);
                        iof = NumberHelper.reformat((primeiroResultadoValido.getAttribute("IOF") != null) ? primeiroResultadoValido.getAttribute("IOF").toString() : "0.00", "en", NumberHelper.getLang(), true);
                    }
                }
            } catch (ParseException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            model.addAttribute("CSA_CODIGO", csaCodigo);
            model.addAttribute("CSA_NOME", csaNome);
            model.addAttribute("CFT_CODIGO", cftCodigo);
            model.addAttribute("ADE_VLR", vlrParcela);
            model.addAttribute("VLR_LIBERADO", vlrLiberado);
            model.addAttribute("RANKING", ranking);
            model.addAttribute("SVC_CODIGO", svcCodigo);
            if (simulacaoMetodoMexicano) {
                model.addAttribute("ADE_VLR_CAT", cat);
                model.addAttribute("ADE_VLR_IVA", iva);
            } else if (simulacaoMetodoBrasileiro) {
                model.addAttribute("ADE_VLR_TAC", tac);
                model.addAttribute("ADE_VLR_IOF", iof);
            }
            model.addAttribute("SIMULACAO_POR_ADE_VLR", Boolean.TRUE);

            // Repassa a ADE de portabilidade para exibição dos seus dados
            request.setAttribute("adePortabilidade", autdes);

            // Repassa o token salvo, pois o método irá revalidar o token
            request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

            // Gera chave criptografada com os dados resultado da simulação, que é validado no método abaixo
            KeyPair keyPair = LoginHelper.getRSAKeyPair(request);
            String chaveSeguranca = RSA.encrypt(vlrParcela + "|" + vlrLiberado + "|" + numParcelas, keyPair.getPublic());
            request.setAttribute("chaveSeguranca", chaveSeguranca);

            return confirmar(svcCodigo, svcCodigoOrigem, csaCodigo, vlrParcela, tac, iof, cat, iva, cftCodigo, dtjCodigo, vlrLiberado, numParcelas, false, request, response, session, model);

        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.solicitar.portabilidade.simulacao.sem.resultado", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = {"acao=confirmarSimulacaoPortabilidade"})
    public String confirmarSimulacaoPortabilidade(@RequestParam(value = "SVC_CODIGO", required = true, defaultValue = "") String svcCodigo, @RequestParam(value = "CSA_CODIGO", required = true, defaultValue = "") String csaCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!responsavel.temPermissao(CodedValues.FUN_SOLICITAR_PORTABILIDADE)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String adeVlr = request.getParameter("ADE_VLR");
        String adeVlrTac = request.getParameter("ADE_VLR_TAC");
        String adeVlrIof = request.getParameter("ADE_VLR_IOF");
        String ade_vlr_cat = request.getParameter("ADE_VLR_CAT"); // simulacaoMetodoMexicano
        String adeVlrIva = request.getParameter("ADE_VLR_IVA"); // simulacaoMetodoMexicano
        String cftCodigo = request.getParameter("CFT_CODIGO");
        String dtjCodigo = request.getParameter("DTJ_CODIGO");
        String vlrLiberado = request.getParameter("VLR_LIBERADO");
        String przVlr = request.getParameter("PRZ_VLR");
        String svcCodigoOrigem = request.getParameter("SVC_CODIGO_ORIGEM");
        String adeCodigo = request.getParameter("ADE_CODIGO");
        boolean simulacaoPorAdeVlr = Boolean.parseBoolean(request.getParameter("SIMULACAO_POR_ADE_VLR"));

        TransferObject autdes = null;
        try {
            autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
            model.addAttribute("adePortabilidade", autdes);
        } catch (AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (!TextHelper.isNull(adeVlr) && adeVlr.lastIndexOf(",") != -1) {
            try {
                adeVlr = NumberHelper.reformat(adeVlr, NumberHelper.getLang(), "en");
            } catch (java.text.ParseException ex) {
                adeVlr = "";
            }
        }

        if (!TextHelper.isNull(vlrLiberado) && vlrLiberado.lastIndexOf(",") != -1) {
            try {
                vlrLiberado = NumberHelper.reformat(vlrLiberado, NumberHelper.getLang(), "en");
            } catch (java.text.ParseException ex) {
                vlrLiberado = "";
            }
        }

        model.addAttribute("SIMULACAO_POR_ADE_VLR", simulacaoPorAdeVlr);
        model.addAttribute("tpcSolicitarPortabilidadeRanking", true);

        return confirmar(svcCodigo, svcCodigoOrigem, csaCodigo, adeVlr, adeVlrTac, adeVlrIof, ade_vlr_cat, adeVlrIva, cftCodigo, dtjCodigo, vlrLiberado, przVlr, false, request, response, session, model);
    }

    private TransferObject simularPortabilidade(String svcCodigo, String orgCodigo, String rseCodigo, BigDecimal adeVlr, Integer prazoRest, String adePeriodicidade, AcessoSistema responsavel) throws SimulacaoControllerException, ParametroControllerException, ViewHelperException {
        TransferObject primeiroResultadoValido = null;

        // Caso o servidor tenha margem negativa o parâmetro 852 esteja habilitado, o valor deverá ser 1 centavo menor que o valor da parcela da consignação selecionada.
        boolean permitePortabilidadeMargemNegativa = ParamSist.paramEquals(CodedValues.TPC_PERMITE_PORTABILIDADE_MARGEM_NEGATIVA, CodedValues.TPC_SIM, responsavel);
        BigDecimal margemConsignavel = new MargemDisponivel(rseCodigo, null, svcCodigo, null, responsavel).getMargemRestante();
        if (margemConsignavel.signum() < 0 && permitePortabilidadeMargemNegativa) {
            adeVlr = adeVlr.subtract(BigDecimal.valueOf(0.01));
        }

        // Caso o parâmetro de serviço 303, Valor percentual máximo da parcela de portabilidade, esteja preenchido, o valor deverá ser limitado conforme a parametrização.
        CustomTransferObject paramSvcCondicionaPortabilidade = parametroController.getParamSvcCse(svcCodigo, CodedValues.TPS_VLR_PERC_MAXIMO_PARCELA_PORTABILIDADE, responsavel);
        BigDecimal perMaxParcPortabilidade = (paramSvcCondicionaPortabilidade != null && !TextHelper.isNull(paramSvcCondicionaPortabilidade.getAttribute(Columns.PSE_VLR))) ? new BigDecimal((String) paramSvcCondicionaPortabilidade.getAttribute(Columns.PSE_VLR)) : null;
        if (perMaxParcPortabilidade != null) {
            adeVlr = adeVlr.multiply(perMaxParcPortabilidade).divide(new BigDecimal(100));
        }

        // Faz uma simulação com o valor de parcela da consignação selecionada e o prazo sendo o restante
        List<TransferObject> listaSimulacao = simulacaoController.simularConsignacao(svcCodigo, orgCodigo, rseCodigo, adeVlr, null, prazoRest.shortValue(), null, true, adePeriodicidade, responsavel);

        // Faz a seleção das linhas de simulação passando como valor de margem o adeVlr visto que é uma portabilidade, ou seja irá manter o valor
        listaSimulacao = simulacaoController.selecionarLinhasSimulacao(listaSimulacao, rseCodigo, adeVlr, Integer.MAX_VALUE, false, true, responsavel);

        // Navega no resultado e verifica se tem algum registro válido
        for (TransferObject simulacao : listaSimulacao) {
            if (simulacao.getAttribute("OK") != null && (Boolean) simulacao.getAttribute("OK")) {
                primeiroResultadoValido = simulacao;
                break;
            }
        }

        return primeiroResultadoValido;
    }

    @RequestMapping(params = "acao=portabilidadeSemLeilao")
    public String portabilidadeSemLeilao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ZetraException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String rseCodigo = responsavel.getRseCodigo();

        CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "solicitar_portabilidade");
        List<TransferObject> lstConsignacao = null;
        try {
            lstConsignacao = pesquisarConsignacaoController.pesquisaAutorizacao(responsavel.getTipoEntidade(), responsavel.getUsuCodigo(), rseCodigo, null, null, CodedValues.SAD_CODIGOS_PORTABILIDADE, null, -1, -1, criterio, responsavel);
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Se a margem está negativa, e não permite portabilidade com margem negativa, retorna erro ao usuário
        boolean permitePortabilidadeMargemNegativa = ParamSist.paramEquals(CodedValues.TPC_PERMITE_PORTABILIDADE_MARGEM_NEGATIVA, CodedValues.TPC_SIM, responsavel);
        if (!permitePortabilidadeMargemNegativa) {
            // Valida as margens que as consignações incidem
            try {
                boolean algumaMargemNegativa = false;

                // Consulta margem passando valor ZERADO, pois só importa se a margem não está negativa, e com isso
                // a configuração de exibição de margem para papel de servidor não importa
                List<MargemTO> lstMargens = consultarMargemController.consultarMargem(rseCodigo, BigDecimal.ZERO, null, null, true, true, responsavel);

                // Cria um Mapa com código da margem e se está negativa
                Map<Short, Boolean> margemNegativa = new HashMap<>();
                for (MargemTO margem : lstMargens) {
                    margemNegativa.put(margem.getMarCodigo(), !margem.temMargemDisponivel());
                    algumaMargemNegativa |= !margem.temMargemDisponivel();
                }

                if (algumaMargemNegativa) {
                    List<TransferObject> lstConsignacaoPermitia = new ArrayList<>(lstConsignacao.size());
                    for (TransferObject consignacao : lstConsignacao) {
                        Short adeIncMargem = consignacao.getAttribute(Columns.ADE_INC_MARGEM) != null ? (Short) consignacao.getAttribute(Columns.ADE_INC_MARGEM) : CodedValues.INCIDE_MARGEM_SIM;
                        if (margemNegativa.containsKey(adeIncMargem) && !margemNegativa.get(adeIncMargem)) {
                            lstConsignacaoPermitia.add(consignacao);
                        }
                    }

                    if (lstConsignacaoPermitia.isEmpty()) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.solicitar.portabilidade.margem.negativa.nao.permitida", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }

                    lstConsignacao = lstConsignacaoPermitia;
                }
            } catch (ServidorControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        String csaCodigoDestino = request.getParameter("CSA_CODIGO");
        String svcCodigoDestino = request.getParameter("SVC_CODIGO");
        String vlrAde = request.getParameter("ADE_VLR");
        String prazo = request.getParameter("PRZ_VLR");
        String vlrLiberado = request.getParameter("VLR_LIBERADO");        
        boolean vlrLiberadoOk = Boolean.parseBoolean(request.getParameter("vlrLiberadoOk"));
        String cftCodigo = request.getParameter("CFT_CODIGO");
        String dtjCodigo = request.getParameter("DTJ_CODIGO");
        String rank = request.getParameter("RANK");
        String isParcela = request.getParameter("IS_PARCELA");
        String adePeriodicidade = request.getParameter("ADE_PERIODICIDADE");
        
        if(!vlrLiberadoOk) {
        	boolean serBloqueadoSimulaSemConcluir = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_SER_BLOQUEADO_SIMULAR_SEM_CONCLUIR, responsavel)) || ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_SER_BLOQUEADO_SIMULAR_SEM_CONCLUIR, responsavel).toString().equals(CodedValues.TPC_SIM);
            MargemDisponivel margemDisponivel = null;
            try {
            	ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigoDestino, responsavel);
                Short incMargem = paramSvcCse.getTpsIncideMargem(); // Incide na margem 1, 2 ou 3
                margemDisponivel = new MargemDisponivel(rseCodigo, null, svcCodigoDestino, incMargem, !serBloqueadoSimulaSemConcluir, responsavel);
                BigDecimal rseMargemRest = margemDisponivel.getMargemRestante();
                CustomTransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
                String orgCodigo = servidor.getAttribute(Columns.ORG_CODIGO).toString();
                List<TransferObject> linhaSimulacao = simulacaoController.simularConsignacao(csaCodigoDestino, svcCodigoDestino, orgCodigo, rseCodigo, rseMargemRest, null, Short.parseShort(prazo), null, true, false, adePeriodicidade, responsavel);
                for (TransferObject simu : linhaSimulacao) {
                	vlrLiberado = simu.getAttribute("VLR_LIBERADO").toString();
                	vlrAde = simu.getAttribute("VLR_PARCELA").toString();
                }
            } catch (ParametroControllerException | ViewHelperException | NumberFormatException | SimulacaoControllerException | ServidorControllerException ex) {
            	LOG.error(ex.getMessage(), ex);
                throw new ZetraException(ex);
            }
        }

        if (vlrLiberadoOk && !validarDadosSimulacao(request, null, vlrAde, vlrLiberado, prazo, responsavel)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Link para seleção da consignação
        String linkSelecionar = SynchronizerToken.updateTokenInURL("../v3/solicitarPortabilidade?acao=detalhesPortabilidade&CSA_CODIGO=" + csaCodigoDestino + "&SVC_CODIGO=" + svcCodigoDestino + "&ADE_VLR=" + vlrAde + "&PRZ_VLR=" + prazo + "&VLR_LIBERADO=" + vlrLiberado + "&CFT_CODIGO=" + cftCodigo + "&DTJ_CODIGO=" + dtjCodigo + "&RANK=" + rank + "&IS_PARCELA=" + isParcela + "&ade=%s" + "&vlrLiberadoOk=" + vlrLiberadoOk, request);

        model.addAttribute("tituloColunas", gerarTituloColunas(responsavel));
        model.addAttribute("conteudoLinhas", gerarConteudoLinhas(lstConsignacao, linkSelecionar, responsavel));
        model.addAttribute("portabilidadeSemLeilao", true);
        return viewRedirect("jsp/solicitarPortabilidade/listarConsignacaoParaPortabilidade", request, session, model, responsavel);
    }

    @RequestMapping(params = "acao=detalhesPortabilidade")
    public String detalhesPortabilidade(@RequestParam(value = "ade", required = true, defaultValue = "") String adeCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws UsuarioControllerException, ServidorControllerException, ParametroControllerException, AutorizacaoControllerException, ConsignatariaControllerException, SimulacaoControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        //busca dados da simulação//
        String csaCodigoDestino = request.getParameter("CSA_CODIGO");
        String svcCodigoDestino = request.getParameter("SVC_CODIGO");
        String vlrAdeDestino = request.getParameter("ADE_VLR").replace(',', '.');
        String prazoDestino = request.getParameter("PRZ_VLR");
        String vlrLiberadoDestino = request.getParameter("VLR_LIBERADO");
        boolean vlrLiberadoOk = Boolean.parseBoolean(request.getParameter("vlrLiberadoOk"));
        String cftCodigo = request.getParameter("CFT_CODIGO");
        String dtjCodigo = request.getParameter("DTJ_CODIGO");
        String rank = request.getParameter("RANK");
        boolean isParcela = request.getParameter("IS_PARCELA").equals("S");

        TransferObject autdes = null;
        String svcCodigoOrigem = null;
        try {
            autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
        } catch (AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (autdes != null) {
            svcCodigoOrigem = autdes.getAttribute(Columns.SVC_CODIGO).toString();
            request.setAttribute("adePortabilidade", autdes);
        }

        //verificar relacionamento de portabilidade entre serviços
        try {
            List<RelacionamentoServico> relacionamentosServico = RelacionamentoServicoHome.findBySvcCodigoDestinoECodigoOrigem(svcCodigoDestino, svcCodigoOrigem);
            if (relacionamentosServico.isEmpty()) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.servico.nao.tem.relacionamento.para.portabilidade", responsavel));
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.servico.nao.tem.relacionamento.para.portabilidade", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        } catch (FindException e) {
            throw new RuntimeException(e);
        }

        String svcIdentificador = request.getParameter("SVC_IDENTIFICADOR");
        String adePeriodicidade = request.getParameter("ADE_PERIODICIDADE"); // simulacaoMetodoMexicano
        String tipo = request.getParameter("tipo");

        if (TextHelper.isNull(csaCodigoDestino) || TextHelper.isNull(svcCodigoDestino)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String rseCodigo = responsavel.getRseCodigo();
        String serCodigo = responsavel.getSerCodigo();
        String orgCodigo = responsavel.getOrgCodigo();

        ServidorTransferObject servidor = null;
        try {
            servidor = servidorController.findServidor(serCodigo, responsavel);
            model.addAttribute("servidor", servidor);
        } catch (ServidorControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Seleciona texto de instrução para o servidor cadastrado pela consignatária
        ConsignatariaTransferObject consignataria = consignatariaController.findConsignataria(csaCodigoDestino, responsavel);
        String csaIdentificador = consignataria.getCsaIdentificador();

        Integer prazo = (!TextHelper.isNull(prazoDestino) ? Integer.valueOf(prazoDestino) : null);
        BigDecimal valor = null;
        BigDecimal liberado = null;
        try {
            valor = (!TextHelper.isNull(vlrAdeDestino) ? new BigDecimal(NumberHelper.parse(vlrAdeDestino, "en")) : null);
            liberado = (!TextHelper.isNull(vlrLiberadoDestino) ? new BigDecimal(NumberHelper.parse(vlrLiberadoDestino, "en")) : null);
        } catch (ParseException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Cria lista com ade_codigo para portabilidade para que no podeReservarMargem, seja considerada a operação de portabilidade
        List<String> adeCodigosRenegociacao = null;
        if (request.getAttribute("adePortabilidade") != null) {
            TransferObject adePortabilidade = (TransferObject) request.getAttribute("adePortabilidade");
            adeCodigosRenegociacao = new ArrayList<>(1);
            adeCodigosRenegociacao.add(adePortabilidade.getAttribute(Columns.ADE_CODIGO).toString());
        }

        CustomTransferObject convenio = null;
        String cnvCodigo = null;
        try {
            // Busca os dados do convênio
            convenio = convenioController.getParamCnv(csaCodigoDestino, orgCodigo, svcCodigoDestino, responsavel);
            cnvCodigo = convenio.getAttribute(Columns.CNV_CODIGO).toString();
            String acao = (tipo != null && tipo.equals("simula_renegociacao") ? "RENEGOCIAR" : "RESERVAR");

            boolean telaConfirmacaoDuplicidade = "S".equals(request.getParameter("telaConfirmacaoDuplicidade"));
            // Verifica se as entidades não estão bloqueadas
            autorizacaoController.podeReservarMargem(cnvCodigo, null, rseCodigo, true, true, true, adeCodigosRenegociacao, valor, liberado, prazo, 0, adePeriodicidade, null, null, acao, true, telaConfirmacaoDuplicidade, responsavel);

        } catch (AutorizacaoControllerException ex) {
            String messageKey = ex.getMessageKey();
            if ("mensagem.erro.ade.duplicidade.bloqueada.ate.data.limite".equals(messageKey)) {
                return verificarPossibilidadePermitirDuplicidade(request, session, model, responsavel, cnvCodigo, "confirmar", ex);
            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String svcDescricao = (String) convenio.getAttribute(Columns.SVC_DESCRICAO);
        String csaNome = (String) convenio.getAttribute(Columns.CSA_NOME);

        /****************************************************************************************************************/
        // Busca os parâmetros do serviço
        List<String> tpsCsaCodigos = new ArrayList<>();
        tpsCsaCodigos.add(CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES);
        tpsCsaCodigos.add(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO);

        ParamSvcTO paramSvcCse = null;
        List<TransferObject> paramSvcCsa = null;
        try {
            paramSvcCse = parametroController.getParamSvcCseTO(svcCodigoDestino, responsavel);
            paramSvcCsa = parametroController.selectParamSvcCsa(svcCodigoDestino, csaCodigoDestino, tpsCsaCodigos, false, responsavel);
        } catch (ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        ParamSvcTO paramSvcCseOrigem = null;
        try {
            paramSvcCseOrigem = parametroController.getParamSvcCseTO(svcCodigoOrigem, responsavel);
        } catch (ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        boolean tpsExigenciaConfirmacaoLeituraServidor = paramSvcCseOrigem.isTpsExigenciaConfirmacaoLeituraServidor();
        if (tpsExigenciaConfirmacaoLeituraServidor) {
            String exigenciaConfirmacao = request.getParameter("exigenciaConfirmacaoLeitura");
            model.addAttribute("exigenciaConfirmacaoLeitura", exigenciaConfirmacao);
            if (!"true".equals(exigenciaConfirmacao)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informacao.simulacao.informar.confirmacao.leitura", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        int carenciaMinCse = (paramSvcCse.getTpsCarenciaMinima() != null && !paramSvcCse.getTpsCarenciaMinima().equals("")) ? Integer.parseInt(paramSvcCse.getTpsCarenciaMinima()) : 0;
        int carenciaMaxCse = (paramSvcCse.getTpsCarenciaMaxima() != null && !paramSvcCse.getTpsCarenciaMaxima().equals("")) ? Integer.parseInt(paramSvcCse.getTpsCarenciaMaxima()) : 99;
        boolean exigeCodAutSolicitacao = paramSvcCse.isTpsExigeCodAutorizacaoSolic();
        boolean exibirTabelaPrice = paramSvcCse.isTpsExibeTabelaPrice();
        String paramExibeCampoCidade = paramSvcCse.getTpsExibeCidadeConfirmacaoSolicitacao();
        if (TextHelper.isNull(paramExibeCampoCidade)) {
            paramExibeCampoCidade = CodedValues.NAO_EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO;
        }
        boolean campoCidadeObrigatorio = (paramExibeCampoCidade.equals(CodedValues.EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO_OBRIGATORIO));

        // Parâmetros de convênio
        int carenciaMinima = (convenio.getAttribute("CARENCIA_MINIMA") != null && !convenio.getAttribute("CARENCIA_MINIMA").equals("")) ? Integer.parseInt(convenio.getAttribute("CARENCIA_MINIMA").toString()) : 0;
        int carenciaMaxima = (convenio.getAttribute("CARENCIA_MAXIMA") != null && !convenio.getAttribute("CARENCIA_MAXIMA").equals("")) ? Integer.parseInt(convenio.getAttribute("CARENCIA_MAXIMA").toString()) : 99;

        // Define os valores de carência mínimo e máximo
        int[] carenciaPermitida = ReservaMargemHelper.getCarenciaPermitida(carenciaMinima, carenciaMaxima, carenciaMinCse, carenciaMaxCse);
        int carenciaMinPermitida = carenciaPermitida[0];

        boolean simulacaoMetodoMexicano = ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_MEXICANO, responsavel);
        boolean simulacaoMetodoBrasileiro = ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_BRASILEIRO, responsavel);
        boolean quinzenal = simulacaoMetodoMexicano && adePeriodicidade.equals(CodedValues.PERIODICIDADE_FOLHA_QUINZENAL);

        boolean exigeAssinaturaDigital = false;
        for (TransferObject vo : paramSvcCsa) {
            if (vo.getAttribute(Columns.TPS_CODIGO).equals(CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES) && !TextHelper.isNull(vo.getAttribute(Columns.PSC_VLR))) {
                exigeAssinaturaDigital = vo.getAttribute(Columns.PSC_VLR).equals("S");
            }
            // DESENV-17017 - Por decisão do setor de segurança (LGPD) essa implementação não deve ser liberada, por isso os valores são setados como false
            CustomTransferObject param = (CustomTransferObject) vo;
            if (param != null && param.getAttribute(Columns.PSC_VLR) != null) {
                if (param.getAttribute(Columns.TPS_CODIGO).equals(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO)) {
                    String pscVlr = (!param.getAttribute(Columns.PSC_VLR).toString().isEmpty()) ? param.getAttribute(Columns.PSC_VLR).toString() : "";
                    if (pscVlr.equals("E")) {
                        model.addAttribute("enderecoObrigatorio", true);
                    } else if (pscVlr.equals("C")) {
                        model.addAttribute("celularObrigatorio", true);
                    } else if (pscVlr.equals("EC")) {
                        model.addAttribute("enderecoCelularObrigatorio", true);
                    }
                }
            }
        }

        // Calcula a data inicial e final do contrato
        Date adeAnoMesIni = null;
        Date adeAnoMesFim = null;

        carenciaMinPermitida = parametroController.calcularAdeCarenciaDiaCorteCsa(carenciaMinPermitida, csaCodigoDestino, orgCodigo, responsavel);
        try {
            adeAnoMesIni = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, carenciaMinPermitida, adePeriodicidade, responsavel);

            java.sql.Date dataInicioFimAde = autorizacaoController.calcularDataIniFimMargemExtra(rseCodigo, new java.sql.Date(adeAnoMesIni.getTime()), paramSvcCse.getTpsIncideMargem(), true, false, responsavel);
            boolean mensagemAlertaAlteracaoDataInicio = false;
            if (dataInicioFimAde != null && dataInicioFimAde.compareTo(adeAnoMesIni) > 0) {
                adeAnoMesIni = dataInicioFimAde;
                carenciaMinPermitida = 0;
                mensagemAlertaAlteracaoDataInicio = true;
            }

            adeAnoMesFim = PeriodoHelper.getInstance().calcularAdeAnoMesFim(orgCodigo, adeAnoMesIni, Integer.valueOf(prazoDestino), adePeriodicidade, responsavel);
            autorizacaoController.calcularDataIniFimMargemExtra(rseCodigo, new java.sql.Date(adeAnoMesFim.getTime()), paramSvcCse.getTpsIncideMargem(), false, true, responsavel);

            if (mensagemAlertaAlteracaoDataInicio) {
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.info.calcular.data.inicio.fim.margem.extra.rse.data.margem.maior.web", responsavel));
            }
        } catch (PeriodoException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());

            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String dataIni = (adeAnoMesIni != null ? DateHelper.toPeriodString(adeAnoMesIni) : "");
        String dataFim = (adeAnoMesFim != null ? DateHelper.toPeriodString(adeAnoMesFim) : "");

        boolean exigeTelefone = ParamSist.paramEquals(CodedValues.TPC_REQUER_TEL_SER_SOLIC_EMPRESTIMO, CodedValues.TPC_SIM, responsavel);
        boolean exigeMunicipioLotacao = ParamSist.paramEquals(CodedValues.TPC_REQUER_MUN_LOTACAO_SER_SOLIC_EMPRESTIMO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

        // Verifica se permite servidor escolher correspondentes
        List<TransferObject> lstCorrespondentes = null;
        String permiteEscolherCorresp;
        try {
            permiteEscolherCorresp = parametroController.getParamCsa(csaCodigoDestino, CodedValues.TPA_PERMITE_SERVIDOR_ESCOLHER_COR_SIMULACAO, responsavel);
            if (!TextHelper.isNull(permiteEscolherCorresp) && permiteEscolherCorresp.equalsIgnoreCase("S")) {
                CorrespondenteTransferObject cor = new CorrespondenteTransferObject();
                cor.setCsaCodigo(csaCodigoDestino);
                cor.setCorAtivo(CodedValues.STS_ATIVO);
                lstCorrespondentes = consignatariaController.lstCorrespondentes(cor, responsavel);
            }
        } catch (ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        TransferObject cft = null;
        String cftVlr = null;
        if (!TextHelper.isNull(dtjCodigo)) {
            try {
                cft = simulacaoController.getDefinicaoTaxaJuros(dtjCodigo);
                cftVlr = NumberHelper.format(((BigDecimal) cft.getAttribute(Columns.CFT_VLR)).doubleValue(), LocaleHelper.getLanguage());
            } catch (SimulacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        } else if (!TextHelper.isNull(cftCodigo)) {
            try {
                cft = simulacaoController.getCoeficienteAtivo(cftCodigo);
                cftVlr = NumberHelper.format(((BigDecimal) cft.getAttribute(Columns.CFT_VLR)).doubleValue(), LocaleHelper.getLanguage());
            } catch (SimulacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        if (exibirTabelaPrice && !TextHelper.isNull(liberado) && !TextHelper.isNull(valor) && !TextHelper.isNull(prazo) && cft != null) {
            CustomTransferObject autdess = new CustomTransferObject();
            autdess.setAttribute(Columns.ADE_CODIGO, "");
            autdess.setAttribute(Columns.CSA_CODIGO, csaCodigoDestino);
            autdess.setAttribute(Columns.ORG_CODIGO, orgCodigo);
            autdess.setAttribute(Columns.RSE_CODIGO, rseCodigo);
            autdess.setAttribute(Columns.SVC_CODIGO, svcCodigoDestino);
            autdess.setAttribute(Columns.CFT_CODIGO, cftCodigo);
            autdess.setAttribute(Columns.DTJ_CODIGO, dtjCodigo);
            autdess.setAttribute(Columns.ADE_VLR, valor);
            autdess.setAttribute(Columns.ADE_VLR_LIQUIDO, liberado);
            autdess.setAttribute(Columns.ADE_PRAZO, prazo);
            autdess.setAttribute(Columns.ADE_DATA, Calendar.getInstance().getTime());
            autdess.setAttribute(Columns.ADE_ANO_MES_INI, adeAnoMesIni);
            autdess.setAttribute(Columns.ADE_ANO_MES_FIM, adeAnoMesFim);
            autdess.setAttribute(Columns.CFT_VLR, cft.getAttribute(Columns.CFT_VLR));

            model.addAttribute("autdes", autdess);
        }

        if (responsavel.isSer() && ParamSist.paramEquals(CodedValues.TPC_SIMULADOR_AGRUPADO_NATUREZA_SERVICO, CodedValues.TPC_SIM, responsavel)) {
            int dia = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
            TransferObject paramMensagemSolicitacao = simulacaoController.getParamSvcCsaMensagemSolicitacaoOutroSvc(svcCodigoDestino, csaCodigoDestino, prazo.shortValue(), (short) dia, responsavel);
            if (paramMensagemSolicitacao != null) {
                model.addAttribute("mensagemSolicitacaoOutroSvc", paramMensagemSolicitacao.getAttribute(Columns.PSC_VLR));
                model.addAttribute("nomeOutroSvc", paramMensagemSolicitacao.getAttribute(Columns.SVC_DESCRICAO));
                model.addAttribute("novoCftCodigo", paramMensagemSolicitacao.getAttribute(Columns.CFT_CODIGO));
                model.addAttribute("keyPair", LoginHelper.getRSAKeyPair(request));
            }
        }

        if (responsavel.isSer() && ParamSist.paramEquals(CodedValues.TPC_EXIGE_CONFIRMACAO_TERMO_CONSENTIMENTO_DADOS_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
            model.addAttribute("termoConsentimentoDadosServidor", montarTermoConsentimentoDadosServidor(responsavel));
        }

        try {
            List<TransferObject> tdaList = autorizacaoController.lstTipoDadoAdicional(AcaoTipoDadoAdicionalEnum.ALTERA, VisibilidadeTipoDadoAdicionalEnum.WEB, svcCodigoDestino, csaCodigoDestino, responsavel);
            model.addAttribute("tdaList", tdaList);

            if (tdaList != null && !tdaList.isEmpty()) {
                Map<String, String> dadosAutorizacao = new HashMap<>();
                for (TransferObject tda : tdaList) {
                    String tdaCodigo = (String) tda.getAttribute(Columns.TDA_CODIGO);
                    String tdaValor = autorizacaoController.getValorDadoServidor(serCodigo, tdaCodigo, responsavel);
                    dadosAutorizacao.put(tdaCodigo, tdaValor);
                }
                model.addAttribute("dadosAutorizacao", dadosAutorizacao);
            }
        } catch (AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        int maxQtdArquivos;

        try {
            maxQtdArquivos = Integer.parseInt((String) ParamSist.getInstance().getParam(CodedValues.TPC_QTE_MAX_ARQ_ANEXO_CONTRATO_ASSINATURA_DIGITAL, responsavel));
        } catch (NumberFormatException e) {
            maxQtdArquivos = 15;
        }

        String usuToken = usuarioController.gerarChaveSessaoUsuario(responsavel.getUsuCodigo(), responsavel); // UsuarioChaveSessaoHome.findByPrimaryKey(responsavel.getUsuCodigo());
        boolean serSenhaObrigatoria = parametroController.senhaServidorObrigatoriaReserva(rseCodigo, svcCodigoDestino, csaCodigoDestino, responsavel);

        model.addAttribute("usuToken", usuToken);
        model.addAttribute("exigeTelefone", exigeTelefone);
        model.addAttribute("exigeMunicipioLotacao", exigeMunicipioLotacao);
        model.addAttribute("campoCidadeObrigatorio", campoCidadeObrigatorio);
        model.addAttribute("exigeCodAutSolicitacao", exigeCodAutSolicitacao);
        model.addAttribute("tipo", tipo);
        model.addAttribute("exigeAssinaturaDigital", exigeAssinaturaDigital);
        model.addAttribute("csaNome", csaNome);
        model.addAttribute("vlrLiberado", vlrLiberadoDestino);
        model.addAttribute("vlrLiberadoOk", vlrLiberadoOk);
        model.addAttribute("adeVlr", vlrAdeDestino);
        model.addAttribute("przVlr", prazoDestino);
        model.addAttribute("cftVlr", cftVlr);
        model.addAttribute("carenciaMinPermitida", carenciaMinPermitida);
        model.addAttribute("dataIni", dataIni);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("svcDescricao", svcDescricao);
        model.addAttribute("simulacaoMetodoMexicano", simulacaoMetodoMexicano);
        model.addAttribute("simulacaoMetodoBrasileiro", simulacaoMetodoBrasileiro);
        model.addAttribute("quinzenal", quinzenal);
        model.addAttribute("lstCorrespondentes", lstCorrespondentes);
        model.addAttribute("serCodigo", serCodigo);
        model.addAttribute("rseCodigo", rseCodigo);
        model.addAttribute("csaCodigo", csaCodigoDestino);
        model.addAttribute("svcCodigo", svcCodigoDestino);
        model.addAttribute("orgCodigo", orgCodigo);
        model.addAttribute("svcIdentificador", svcIdentificador);
        model.addAttribute("csaIdentificador", csaIdentificador);
        model.addAttribute("cftCodigo", cftCodigo);
        model.addAttribute("dtjCodigo", dtjCodigo);
        model.addAttribute("isParcela", isParcela);
        model.addAttribute("RANKING", rank);
        model.addAttribute("adePeriodicidade", adePeriodicidade);
        model.addAttribute("qtdMaximaArquivos", maxQtdArquivos);
        model.addAttribute("svcCodigoOrigem", svcCodigoOrigem);
        model.addAttribute("serSenhaObrigatoria", serSenhaObrigatoria);

        String chaveSeguranca = JspHelper.verificaVarQryStr(request, "chaveSeguranca");
        if (!TextHelper.isNull(chaveSeguranca)) {
            model.addAttribute("chaveSeguranca", chaveSeguranca);
        }

        //Envia o código de autorização enviado por SMS ao Servidor.
        boolean exigeCodAutorizacaoSMS = ParamSist.paramEquals(CodedValues.TPC_ENVIA_SMS_CODIGO_UNICO_AUTORIZACAO_CONSIGNACAO, CodedValues.TPC_SIM, responsavel);
        if (responsavel.isSer() && exigeCodAutorizacaoSMS) {
            usuarioController.enviarCodigoAutorizacaoSms(rseCodigo, responsavel);
        }


        try {
            if (parametroController.isExigeReconhecimentoFacialServidor(svcCodigoDestino, responsavel) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "reconhecimentoFacialServidorSimulacao"))
                    && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "exigeReconhecimentoFacil"))) {
                model.addAttribute("exigeReconhecimentoFacil", "true");
            }
        } catch (ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }


        return viewRedirect("jsp/solicitarPortabilidade/confimarPortabilidadeSemLeilao", request, session, model, responsavel);
    }

    private String montarTermoConsentimentoDadosServidor(AcessoSistema responsavel) {
        String termoConsentimento = "";
        if (ParamSist.paramEquals(CodedValues.TPC_EXIGE_CONFIRMACAO_TERMO_CONSENTIMENTO_DADOS_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
            String absolutePath = ParamSist.getDiretorioRaizArquivos();
            absolutePath += File.separatorChar + "termo_de_uso" + File.separatorChar;
            absolutePath += CodedNames.TEMPLATE_TERMO_CONSENTIMENTO_DADOS_SERVIDOR;

            File file = new File(absolutePath);
            if (file != null && file.isFile() && file.exists()) {
                termoConsentimento = FileHelper.readAll(absolutePath).replaceAll("\\r\\n|\\r|\\n", "");
            }
        }
        return termoConsentimento;
    }

    private boolean validarDadosSimulacao(HttpServletRequest request, UploadHelper uploadHelper, String adeVlr, String adeVlrLiberado, String adePrazo, AcessoSistema responsavel) {
        KeyPair keyPair = LoginHelper.getRSAKeyPair(request);
        String chaveSegurancaAberta = null;
        try {
            String chaveSeguranca = (String) request.getAttribute("chaveSeguranca");
            if (TextHelper.isNull(chaveSeguranca)) {
                chaveSeguranca = JspHelper.verificaVarQryStr(request, uploadHelper, "chaveSeguranca");
            }
            chaveSegurancaAberta = RSA.decrypt(chaveSeguranca, keyPair.getPrivate());
        } catch (BadPaddingException | NullPointerException ex) {
            LOG.warn(ex);
        }
        if (chaveSegurancaAberta != null) {
            String[] dadosSimulacao = chaveSegurancaAberta.split("\\|");
            if (dadosSimulacao.length == 3 &&
                    !TextHelper.isNull(adeVlr) && (adeVlr.equals(dadosSimulacao[0]) || TextHelper.isNull(dadosSimulacao[0])) &&
                    !TextHelper.isNull(adeVlrLiberado) && (adeVlrLiberado.equals(dadosSimulacao[1]) || TextHelper.isNull(dadosSimulacao[1])) &&
                    !TextHelper.isNull(adePrazo) && adePrazo.equals(dadosSimulacao[2])) {
                return true;
            }
        }
        try {
            // Gerar log de auditoria de erro de segurança
            LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.CREATE, Log.LOG_ERRO_SEGURANCA);
            log.add(ApplicationResourcesHelper.getMessage("rotulo.erro.arg0", responsavel, ApplicationResourcesHelper.getMessage("mensagem.erro.seguranca.alteracao.valores.simulacao", responsavel)));
            log.write();
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return false;
    }

    private String listaCsaPortabilidadeCartao(String adeCodigo, String csaCodigo, String svcCodigo, String rseCodigo, String orgCodigo, String rseMatricula, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        List<TransferObject> csas;
        try {
            csas = consignatariaController.listaCsaPortabilidadeCartao(csaCodigo, responsavel);
            csas = simulacaoController.buscarTaxasParaConsignatarias(responsavel, rseCodigo, orgCodigo, svcCodigo, csas.toArray(TransferObject[]::new));
            csas.sort((TransferObject obj1, TransferObject obj2) -> ((BigDecimal) obj1.getAttribute(Columns.CFT_VLR)).compareTo((BigDecimal) obj2.getAttribute(Columns.CFT_VLR)));
        } catch (final ConsignatariaControllerException | SimulacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("svcCodigo", svcCodigo);
        model.addAttribute("rseCodigo", rseCodigo);
        model.addAttribute("rseMatricula", rseMatricula);
        model.addAttribute("adeCodigo", adeCodigo);
        model.addAttribute("csas", csas);
        model.addAttribute("temCET", ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel));

        return viewRedirect("jsp/solicitarPortabilidade/listaConsignatariasPortabilidadeCartao", request, session, model, responsavel);
    }

    @RequestMapping(params = "acao=solicitarPortabilidadeCartao")
    public String solicitarPortabilidadeCartao(@RequestParam(value = "SVC_CODIGO") String svcCodigo, @RequestParam(value = "RSE_CODIGO") String rseCodigo, @RequestParam(value = "CSA_CODIGO") String csaCodigo, @RequestParam(value = "ADE_CODIGO") String adeCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            final CustomTransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
            if (servidor == null) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            model.addAttribute("servidor", servidor);

            // Obtém os parâmetros de plano de desconto com base no plano/serviço selecionado
            final Map<String, String> parametrosPlano = new HashMap<>();

            final String rseMatricula = servidor.getAttribute(Columns.RSE_MATRICULA).toString();
            final String orgCodigo = servidor.getAttribute(Columns.ORG_CODIGO).toString();
            final String serCodigo = servidor.getAttribute(Columns.SER_CODIGO).toString();

            if (!AcessoFuncaoServico.temAcessoFuncao(request, CodedValues.FUN_SOLICITAR_PORTABILIDADE, responsavel.getUsuCodigo(), svcCodigo)) {
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final ConsignatariaTransferObject consignatariaDestino = consignatariaController.findConsignataria(csaCodigo, responsavel);
            String csaNomeDestino = consignatariaDestino.getCsaIdentificador() + " - " + (!TextHelper.isNull(consignatariaDestino.getCsaNomeAbreviado()) ? consignatariaDestino.getCsaNomeAbreviado() : consignatariaDestino.getCsaNome());
            model.addAttribute("csaCodigoDestino", csaCodigo);
            model.addAttribute("csaNome", csaNomeDestino);

            CustomTransferObject convenio = null;
            try {
                // verfica se o servico e consignataria escolhida pode reservar uma margem para este servidor
                convenio = convenioController.getParamCnv(csaCodigo, orgCodigo, svcCodigo, true, true, responsavel);
            } catch (final ConvenioControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
            if (convenio == null) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.convenio.inexistente.ser", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            final String cnvCodigo = convenio.getAttribute(Columns.CNV_CODIGO).toString();

            // Verifica quantidade de contratos por grupo de serviço e numero de consignatarias
            try {
                final boolean telaConfirmacaoDuplicidade = "S".equals(request.getParameter("telaConfirmacaoDuplicidade"));
                autorizacaoController.podeReservarMargem(cnvCodigo, null, rseCodigo, true, true, true, null, null, null, null, 0, null, null, null, "RESERVAR", true, telaConfirmacaoDuplicidade, responsavel);
            } catch (final AutorizacaoControllerException ex) {
                final String messageKey = ex.getMessageKey();
                if ("mensagem.erro.ade.duplicidade.bloqueada.ate.data.limite".equals(messageKey)) {
                    return verificarPossibilidadePermitirDuplicidade(request, session, model, responsavel, cnvCodigo, "reservarMargem", ex);
                }
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Seleciona os vínculos que não podem reservar margem para este csa e svc
            autorizacaoController.verificaBloqueioVinculoCnvAlertaSessao(session, csaCodigo, svcCodigo, (String) servidor.getAttribute(Columns.RSE_VRS_CODIGO), responsavel);

            final String numBanco = JCryptOld.crypt("IB", servidor.getAttribute(Columns.RSE_BANCO_SAL) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_BANCO_SAL).toString()) : "");
            final String numAgencia = JCryptOld.crypt("IB", servidor.getAttribute(Columns.RSE_AGENCIA_SAL) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_AGENCIA_SAL).toString()) : "");
            final String numConta = servidor.getAttribute(Columns.RSE_CONTA_SAL) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_CONTA_SAL).toString()) : "";

            final boolean rseTemInfBancaria = (!TextHelper.isNull(servidor.getAttribute(Columns.RSE_BANCO_SAL)) && !TextHelper.isNull(servidor.getAttribute(Columns.RSE_AGENCIA_SAL)) && !TextHelper.isNull(servidor.getAttribute(Columns.RSE_CONTA_SAL))) || (!TextHelper.isNull(servidor.getAttribute(Columns.RSE_BANCO_SAL_2)) && !TextHelper.isNull(servidor.getAttribute(Columns.RSE_AGENCIA_SAL_2)) && !TextHelper.isNull(servidor.getAttribute(Columns.RSE_CONTA_SAL_2)));
            if (!rseTemInfBancaria) {
                model.addAttribute("rseNaoTemInfBancaria", Boolean.TRUE);
            }

            final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

            // Parâmetro de SVC/CSA que determina se permite valor negativo de contrato
            final boolean permiteVlrNegativo = parametroController.permiteContratoValorNegativo(csaCodigo, svcCodigo, responsavel);

            //***********************************************/
            // Parâmetros de Serviço

            Short incMargem = paramSvcCse.getTpsIncideMargem(); // Incide na margem 1, 2 ou 3
            final Short intFolha = paramSvcCse.getTpsIntegraFolha(); // Integra folha sim ou não
            final String tipoVlr = paramSvcCse.getTpsTipoVlr(); // Tipo do valor: F (Fixo) / P (Percentual) / T (Total da Margem)
            boolean alteraAdeVlr = paramSvcCse.isTpsAlteraAdeVlr(); // Habilita ou nao campo de valor da reserva, campo ja vem preenchido
            if (alteraAdeVlr && parametrosPlano.containsKey(CodedValues.TPP_VLR_FIXO_PLANO)) {
                alteraAdeVlr = CodedValues.PLANO_VALOR_ALTERAVEL.equals(parametrosPlano.get(CodedValues.TPP_VLR_FIXO_PLANO)); // Habilita ou nao campo de valor da reserva dependendo da configuração do plano
            }

            final String vlrLimite = (paramSvcCse.getTpsVlrLimiteAdeSemMargem() != null) && !"".equals(paramSvcCse.getTpsVlrLimiteAdeSemMargem()) ? paramSvcCse.getTpsVlrLimiteAdeSemMargem() : "0";

            //***********************************************/
            // Parâmetros de Convênio
            final String svcIdentificador = convenio.getAttribute(Columns.SVC_IDENTIFICADOR) != null ? convenio.getAttribute(Columns.SVC_IDENTIFICADOR).toString() : "";
            final String svcDescricao = convenio.getAttribute(Columns.SVC_DESCRICAO) != null ? convenio.getAttribute(Columns.SVC_DESCRICAO).toString() : "";
            final String cnvCodVerba = convenio.getAttribute(Columns.CNV_COD_VERBA) != null ? convenio.getAttribute(Columns.CNV_COD_VERBA).toString() : "";
            final String cnvDescricao = (cnvCodVerba.length() > 0 ? cnvCodVerba : svcIdentificador) + " - " + svcDescricao;
            model.addAttribute("cnvDescricao", cnvDescricao);

            //***********************************************/
            // Verifica se pode mostrar margem
            boolean exigeCaptcha = false;
            MargemDisponivel margemDisponivel = null;
            try {
                boolean exibeCaptcha = false;
                boolean exibeCaptchaAvancado = false;
                boolean exibeCaptchaDeficiente = false;
                final String validaRecaptcha = "S".equals(JspHelper.verificaVarQryStr(request, "validaCaptchaPortabilidadeCartao")) && !"S".equals(JspHelper.verificaVarQryStr(request, "validaCaptchaTopo")) ? JspHelper.verificaVarQryStr(request, "validaCaptchaPortabilidadeCartao") : "N";
                final boolean podeConsultar = ControleConsulta.getInstance().podeConsultarMargemSemCaptchaSer(responsavel.getUsuCodigo());

                final boolean defVisual = responsavel.isDeficienteVisual();
                if (!defVisual) {
                    exibeCaptchaAvancado = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                    exibeCaptcha = !exibeCaptchaAvancado;
                } else {
                    exibeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                }
                if (!podeConsultar && "S".equals(validaRecaptcha)) {
                    if (!defVisual) {
                        if (exibeCaptcha) {
                            if (ImageCaptchaServlet.armazenaCaptcha(session.getId(), (String) session.getAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY))
                                    && !ImageCaptchaServlet.validaCaptcha(session.getId(), JspHelper.verificaVarQryStr(request, "codigoCapPortabilidadeCartao"))) {
                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                                exigeCaptcha = true;
                            } else {
                                session.removeAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);
                                ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                            }
                        } else if (exibeCaptchaAvancado) {
                            final String remoteAddr = request.getRemoteAddr();

                            if (!isValidCaptcha(request.getParameter("g-recaptcha-response_portabilidade_cartao"), remoteAddr, responsavel)) {
                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                                exigeCaptcha = true;
                            } else {
                                ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                            }
                        }
                    } else {
                        final boolean exigeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                        if (exigeCaptchaDeficiente) {
                            final String captchaAnswer = JspHelper.verificaVarQryStr(request, "codigoCapPortabilidadeCartao");

                            if (captchaAnswer == null) {
                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                                exigeCaptcha = true;
                            }

                            final String captchaCode = (String) session.getAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                            if ((captchaCode == null) || !captchaCode.equalsIgnoreCase(captchaAnswer)) {
                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                                exigeCaptcha = true;
                            } else {
                                session.removeAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                                ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                            }
                        }
                    }
                } else if (podeConsultar) {
                    ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                } else {
                    exigeCaptcha = true;
                }
                margemDisponivel = new MargemDisponivel(rseCodigo, csaCodigo, svcCodigo, incMargem, responsavel);
                model.addAttribute("exigeCaptcha", exigeCaptcha);
                model.addAttribute("exibeCaptcha", exibeCaptcha);
                model.addAttribute("exibeCaptchaAvancado", exibeCaptchaAvancado);
                model.addAttribute("exibeCaptchaDeficiente", exibeCaptchaDeficiente);
            } catch (final ViewHelperException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            final boolean podeMostrarMargem = margemDisponivel.getExibeMargem().isExibeValor();

            // Mostra a Margem
            if (!exigeCaptcha && podeMostrarMargem) {
                model.addAttribute("exibirValorMargem", Boolean.TRUE);
            }

            BigDecimal margemConsignavel = margemDisponivel.getMargemRestante();

            // Calcula o valor dos contratos de serviço com tratamento especial de margem para exibição de mensagem para servidor.
            // Para que um contrato tenha tratamento especial de margem, não deve incidir sobre nenhuma margem e seu serviço deve ter TPS_CODIGO=224 habilitado.
            final BigDecimal somaValorContratosTratamentoEspecial = new BigDecimal("0");

            // Se for o servidor fazendo solicitação, verifica se ele possui margem
            // disponível para o serviço selecionado
            // Se a margem restante não é positiva
            if (margemConsignavel.signum() != 1) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.margem.insuficiente.svc", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            //DESENV-17017: Verifica a obrigatoriedade de informações do servidor.
            final List<String> tpsCodigos = new ArrayList<>();
            tpsCodigos.add(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO);
            tpsCodigos.add(CodedValues.TPS_VALOR_SVC_FIXO_POSTO);

            final List<TransferObject> paramSvcCsa = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigos, false, responsavel);
            boolean enderecoObrigatorio = false;
            boolean celularObrigatorio = false;
            boolean enderecoCelularObrigatorio = false;
            for (final TransferObject param2 : paramSvcCsa) {
                final CustomTransferObject param = (CustomTransferObject) param2;
                if ((param != null) && (param.getAttribute(Columns.PSC_VLR) != null)) {
                    if (CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO.equals(param.getAttribute(Columns.TPS_CODIGO))) {
                        final String pscVlr = !param.getAttribute(Columns.PSC_VLR).toString().isEmpty() ? param.getAttribute(Columns.PSC_VLR).toString() : "";
                        // DESENV-17017 - Por decisão do setor de segurança (LGPD) essa implementação não deve ser liberada, por isso os valores são setados como false
                        if ("E".equals(pscVlr)) {
                            enderecoObrigatorio = true;
                            model.addAttribute("enderecoObrigatorio", enderecoObrigatorio);
                        } else if ("C".equals(pscVlr)) {
                            celularObrigatorio = true;
                            model.addAttribute("celularObrigatorio", celularObrigatorio);
                        } else if ("EC".equals(pscVlr)) {
                            enderecoCelularObrigatorio = true;
                            model.addAttribute("enderecoCelularObrigatorio", enderecoCelularObrigatorio);
                        }
                    }

                }
            }

            if (!TextHelper.isNull(paramSvcCse.getTpsMsgExibirSolicitacaoServidor())) {
                // Se é o servidor que está solicitando, então exibe a mensagem do parâmetro
                JspHelper.addMsgSession(session, CodedValues.MSG_ALERT, paramSvcCse.getTpsMsgExibirSolicitacaoServidor());
            }

            final List<TransferObject> tdaList = autorizacaoController.lstTipoDadoAdicional(AcaoTipoDadoAdicionalEnum.ALTERA, VisibilidadeTipoDadoAdicionalEnum.WEB, svcCodigo, csaCodigo, responsavel);
            if ((tdaList != null) && !tdaList.isEmpty()) {
                model.addAttribute("lstTipoDadoAdicional", tdaList);

                final Map<String, String> dadosAutorizacao = new HashMap<>();
                for (final TransferObject tda : tdaList) {
                    final String tdaCodigo = (String) tda.getAttribute(Columns.TDA_CODIGO);
                    final String tdaValor = autorizacaoController.getValorDadoServidor(serCodigo, tdaCodigo, responsavel);
                    dadosAutorizacao.put(tdaCodigo, tdaValor);
                }
                model.addAttribute("dadosAutorizacao", dadosAutorizacao);
            }

            // DESENV-14337: Necessário exibir o termo de consentimento também para serviços que não fazem parte de simulação.
            if (ParamSist.paramEquals(CodedValues.TPC_EXIGE_CONFIRMACAO_TERMO_CONSENTIMENTO_DADOS_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                model.addAttribute("termoConsentimentoDadosServidor", montarTermoConsentimentoDadosServidor(responsavel));
            }

            TransferObject autdes = null;
            try {
                autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
                model.addAttribute("adePortabilidade", autdes);
            } catch (AutorizacaoControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String txtExplicativo = TextHelper.isNull(paramSvcCse.getTpsExibeTxtExplicativoValorPrestacao()) ? "" : paramSvcCse.getTpsExibeTxtExplicativoValorPrestacao();

            final String nomeArqFotoServidor = JspHelper.getPhoto(servidor.getAttribute(Columns.SER_CPF).toString(), rseCodigo, responsavel);
            if (!TextHelper.isNull(nomeArqFotoServidor)) {
                model.addAttribute("nomeArqFotoServidor", nomeArqFotoServidor);
            }

            model.addAttribute("margemDisponivel", margemDisponivel);
            model.addAttribute("margemConsignavel", margemConsignavel);
            model.addAttribute("somaValorContratosTratamentoEspecial", somaValorContratosTratamentoEspecial);
            model.addAttribute("serCodigo", serCodigo);
            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("cnvCodigo", cnvCodigo);
            model.addAttribute("svcCodigo", svcCodigo);
            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("adeCodigo", adeCodigo);
            model.addAttribute("txtExplicativo", txtExplicativo);

            // Parâmetros
            model.addAttribute("intFolha", intFolha);
            model.addAttribute("incMargem", incMargem);
            model.addAttribute("tipoVlr", tipoVlr);
            model.addAttribute("alteraAdeVlr", alteraAdeVlr);
            model.addAttribute("permiteVlrNegativo", permiteVlrNegativo);
            model.addAttribute("vlrLimite", vlrLimite);

            // Dados de servidor e registro servidor necessários
            model.addAttribute("rseMatricula", rseMatricula);
            model.addAttribute("numBanco", numBanco);
            model.addAttribute("numAgencia", numAgencia);
            model.addAttribute("numConta", numConta);

            // Opções de inclusão avançada
            model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.menu.solicitar.portabilidade", responsavel));

            // Verificamos quais Consignatárias permitem ser contactadas
            final List<String> csaCodigos = new ArrayList<>();
            csaCodigos.add(csaCodigo);

            BigDecimal coeficienteValor = buscarCoeficienteValor(svcCodigo, rseCodigo, csaCodigo, responsavel, orgCodigo);

            final List<TransferObject> listaCsaPermiteContato = consignatariaController.listaCsaPermiteContato(csaCodigos, responsavel);
            final HashMap<String, TransferObject> hashCsaPermiteContato = new HashMap<>();

            for (final TransferObject csaPermiteContato : listaCsaPermiteContato) {
                hashCsaPermiteContato.put((String) csaPermiteContato.getAttribute(Columns.CSA_CODIGO), csaPermiteContato);
            }
            model.addAttribute("hashCsaPermiteContato", hashCsaPermiteContato);
            model.addAttribute("anexoObrigatorio", parametroController.isObrigatorioAnexoInclusao(svcCodigo, responsavel));
            model.addAttribute("qtdeMinAnexos", paramSvcCse.getTpsQuantidadeMinimaInclusaoContratos());
            model.addAttribute("coeficienteValor", coeficienteValor);
            model.addAttribute("temCET", ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel));

            model.addAttribute("exibeAlertaMsgPertenceCategoria", false);

        } catch (NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (final ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        return viewRedirect("jsp/solicitarPortabilidade/solicitarPortabilidadeCartao", request, session, model, responsavel);
    }

    private BigDecimal buscarCoeficienteValor(String svcCodigo, String rseCodigo, String csaCodigo,
            final AcessoSistema responsavel, final String orgCodigo) throws SimulacaoControllerException {
        
        ConsignatariaTransferObject csa = new ConsignatariaTransferObject(csaCodigo);
        List<TransferObject> csaList = simulacaoController.buscarTaxasParaConsignatarias(responsavel, rseCodigo, orgCodigo, svcCodigo, csa);
        BigDecimal coeficienteValor = (BigDecimal) csaList.getFirst().getAttribute(Columns.CFT_VLR);

        return coeficienteValor;

    }

    @RequestMapping(params = "acao=confirmarCartao")
    public String confirmarPortabilidadeCartao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            final String rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
            final String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
            final String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
            final String adeCodigo = JspHelper.verificaVarQryStr(request, "ADE_CODIGO");

            model.addAttribute("CSA_NOME_ORIGEM", request.getParameter("CSA_NOME_ORIGEM"));
            model.addAttribute("CSA_NOME_DESTINO", request.getParameter("CSA_NOME_DESTINO"));

            String corNome = "";
            String corCodigo = "";
            if (!"".equals(JspHelper.verificaVarQryStr(request, "PORTAL_BENEFICIO")) && !"".equals(JspHelper.verificaVarQryStr(request, "COR_CODIGO"))) {
                corCodigo = JspHelper.verificaVarQryStr(request, "COR_CODIGO");
                final CorrespondenteTransferObject correspondente = consignatariaController.findCorrespondente(corCodigo, responsavel);
                corNome = !TextHelper.isNull(correspondente) ? correspondente.getCorNome() : corNome;

                model.addAttribute("portalBeneficio", true);
                model.addAttribute("COR_CODIGO", corCodigo);
            }
            if (!TextHelper.isNull(corCodigo)) {
                model.addAttribute("corCodigo", corCodigo);
                model.addAttribute("corNome", corNome);
            }

            final List<String> csaCodigos = new ArrayList<>();
            csaCodigos.add(csaCodigo);

            final List<TransferObject> listaCsaPermiteContato = consignatariaController.listaCsaPermiteContato(csaCodigos, responsavel);
            final HashMap<String, TransferObject> hashCsaPermiteContato = new HashMap<>();

            for (final TransferObject csaPermiteContato : listaCsaPermiteContato) {
                hashCsaPermiteContato.put((String) csaPermiteContato.getAttribute(Columns.CSA_CODIGO), csaPermiteContato);
            }
            model.addAttribute("hashCsaPermiteContato", hashCsaPermiteContato);

            final String cnvCodigo = JspHelper.verificaVarQryStr(request, "CNV_CODIGO");
            final String adePrazo = JspHelper.verificaVarQryStr(request, "adePrazo");
            final String adeVlrLiquido = JspHelper.verificaVarQryStr(request, "adeVlrLiquido");
            String adeVlr = JspHelper.verificaVarQryStr(request, "adeVlr");
            final char separadorDecimal = LocaleHelper.getDecimalFormat().getDecimalFormatSymbols().getDecimalSeparator();
            if (adeVlr.indexOf(separadorDecimal) == -1) {
                adeVlr += separadorDecimal + "00";
            }

            final java.sql.Date ocaPeriodo = !TextHelper.isNull(request.getParameter("ocaPeriodo")) ? DateHelper.toSQLDate(DateHelper.parse(request.getParameter("ocaPeriodo"), "yyyy-MM-dd")) : null;
            Integer adeCarencia = TextHelper.isNum(JspHelper.verificaVarQryStr(request, "adeCarencia")) ? Integer.valueOf(JspHelper.verificaVarQryStr(request, "adeCarencia")) : 0;
            final String adeResponsavel = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel).getUsuLogin();
            final String adePeriodicidade = JspHelper.verificaVarQryStr(request, "adePeriodicidade");
            //Verifica se permite a escolha de periodicidade da folha diferente da que está configurada no sistema
            final boolean permiteEscolherPeriodicidade = ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODICIDADE_FOLHA, CodedValues.TPC_SIM, responsavel);
            if (permiteEscolherPeriodicidade && !PeriodoHelper.folhaMensal(responsavel)) {
                model.addAttribute("exibirCampoPeriodicidade", Boolean.TRUE);
            }

            final Integer prazo = !TextHelper.isNull(adePrazo) ? Integer.valueOf(adePrazo) : null;
            final BigDecimal valor = !TextHelper.isNull(adeVlr) ? new BigDecimal(String.valueOf(NumberHelper.parse(adeVlr, NumberHelper.getLang()))) : null;
            final BigDecimal liberado = !TextHelper.isNull(adeVlrLiquido) ? new BigDecimal(String.valueOf(NumberHelper.parse(adeVlrLiquido, NumberHelper.getLang()))) : null;

            // Verifica se as entidades não estão bloqueadas
            try {
                final boolean telaConfirmacaoDuplicidade = "S".equals(request.getParameter("telaConfirmacaoDuplicidade"));
                autorizacaoController.podeReservarMargem(cnvCodigo, corCodigo, rseCodigo, false, true, true, null, valor, liberado, prazo, adeCarencia, adePeriodicidade, null, null, "RESERVAR", false, telaConfirmacaoDuplicidade, responsavel);
            } catch (final AutorizacaoControllerException ex) {
                final String messageKey = ex.getMessageKey();
                if ("mensagem.erro.ade.duplicidade.bloqueada.ate.data.limite".equals(messageKey)) {
                    return verificarPossibilidadePermitirDuplicidade(request, session, model, responsavel, cnvCodigo, "autorizarReserva", ex);
                } else {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            // Busca o Convênio
            CustomTransferObject convenio = null;
            try {
                convenio = convenioController.getParamCnv(cnvCodigo, true, true, responsavel);
            } catch (final ConvenioControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            if (convenio == null) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.convenio.inexistente.ser", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final String svcDescricao = convenio.getAttribute(Columns.SVC_DESCRICAO).toString();
            final String orgCodigo = convenio.getAttribute(Columns.CNV_ORG_CODIGO).toString();

            model.addAttribute("svcDescricao", svcDescricao);

            java.sql.Date adeAnoMesIni = null;
            java.sql.Date adeAnoMesFim = null;

            adeCarencia = parametroController.calcularAdeCarenciaDiaCorteCsa(adeCarencia, csaCodigo, orgCodigo, responsavel);
            final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

            boolean exigeCodAutSolicitacao = paramSvcCse.isTpsExigeCodAutorizacaoSolic();
            String paramExibeCampoCidade = paramSvcCse.getTpsExibeCidadeConfirmacaoSolicitacao();
            if (TextHelper.isNull(paramExibeCampoCidade)) {
                paramExibeCampoCidade = CodedValues.NAO_EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO;
            }
            boolean campoCidadeObrigatorio = (paramExibeCampoCidade.equals(CodedValues.EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO_OBRIGATORIO));

            try {
                adeAnoMesIni = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, ocaPeriodo, adeCarencia, adePeriodicidade, responsavel);

                if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_PERIODO_ACEITA_APENAS_REDUCOES, CodedValues.TPC_SIM, responsavel)) {
                    final java.sql.Date adeAnoMesIniValido = PeriodoHelper.getInstance().validarAdeAnoMesIni(orgCodigo, adeAnoMesIni, responsavel);
                    if (!adeAnoMesIniValido.equals(adeAnoMesIni)) {
                        session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.alerta.reservar.margem.data.inicial.ajustada.periodo.apenas.reducoes", responsavel));
                        adeAnoMesIni = adeAnoMesIniValido;
                    }
                }

                adeAnoMesFim = PeriodoHelper.getInstance().calcularAdeAnoMesFim(orgCodigo, adeAnoMesIni, prazo, adePeriodicidade, responsavel);

            } catch (final PeriodoException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Busca o servidor
            final CustomTransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);

            servidor.getAttribute(Columns.ORG_CODIGO).toString();
            if (servidor == null) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            model.addAttribute("servidor", servidor);
            final String nomeArqFotoServidor = JspHelper.getPhoto(servidor.getAttribute(Columns.SER_CPF).toString(), rseCodigo, responsavel);
            if (!TextHelper.isNull(nomeArqFotoServidor)) {
                model.addAttribute("nomeArqFotoServidor", nomeArqFotoServidor);
            }

            final String serDataNasc = servidor.getAttribute(Columns.SER_DATA_NASC) != null ? DateHelper.reformat(servidor.getAttribute(Columns.SER_DATA_NASC).toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern()) : "";
            final String serCodigo = servidor.getAttribute(Columns.SER_CODIGO) != null ? servidor.getAttribute(Columns.SER_CODIGO).toString() : "";

            final String adeDataIni = adeAnoMesIni != null ? DateHelper.toPeriodString(adeAnoMesIni) : "";
            final String adeDataFim = adeAnoMesFim != null ? DateHelper.toPeriodString(adeAnoMesFim) : "";

            final String tipoVlr = paramSvcCse.getTpsTipoVlr();
            final String labelTipoValor = ParamSvcTO.getDescricaoTpsTipoVlr(tipoVlr);
            final String labelAdePrazo = ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel);
            final String mascaraAdeIdentificador = paramSvcCse.getTpsMascaraIdentificadorAde();

            try {
                List<TransferObject> tdaList = autorizacaoController.lstTipoDadoAdicional(AcaoTipoDadoAdicionalEnum.ALTERA, VisibilidadeTipoDadoAdicionalEnum.WEB, svcCodigo, csaCodigo, responsavel);
                model.addAttribute("tdaList", tdaList);

                if (tdaList != null && !tdaList.isEmpty()) {
                    Map<String, String> dadosAutorizacao = new HashMap<>();
                    for (TransferObject tda : tdaList) {
                        String tdaCodigo = (String) tda.getAttribute(Columns.TDA_CODIGO);
                        String tdaValor = autorizacaoController.getValorDadoServidor(serCodigo, tdaCodigo, responsavel);
                        dadosAutorizacao.put(tdaCodigo, tdaValor);
                    }
                    model.addAttribute("dadosAutorizacao", dadosAutorizacao);
                }
            } catch (AutorizacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            List<String> tpsCsaCodigos = new ArrayList<>();
            tpsCsaCodigos.add(CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES);
            tpsCsaCodigos.add(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO);

            List<TransferObject> paramSvcCsa = null;
            try {
                paramSvcCsa = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCsaCodigos, false, responsavel);
            } catch (ParametroControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            boolean exigeAssinaturaDigital = false;
            for (TransferObject vo : paramSvcCsa) {
                if (vo.getAttribute(Columns.TPS_CODIGO).equals(CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES) && !TextHelper.isNull(vo.getAttribute(Columns.PSC_VLR))) {
                    exigeAssinaturaDigital = vo.getAttribute(Columns.PSC_VLR).equals("S");
                }
                // DESENV-17017 - Por decisão do setor de segurança (LGPD) essa implementação não deve ser liberada, por isso os valores são setados como false
                CustomTransferObject param = (CustomTransferObject) vo;
                if (param != null && param.getAttribute(Columns.PSC_VLR) != null) {
                    if (param.getAttribute(Columns.TPS_CODIGO).equals(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO)) {
                        String pscVlr = (!param.getAttribute(Columns.PSC_VLR).toString().isEmpty()) ? param.getAttribute(Columns.PSC_VLR).toString() : "";
                        if (pscVlr.equals("E")) {
                            model.addAttribute("enderecoObrigatorio", true);
                        } else if (pscVlr.equals("C")) {
                            model.addAttribute("celularObrigatorio", true);
                        } else if (pscVlr.equals("EC")) {
                            model.addAttribute("enderecoCelularObrigatorio", true);
                        }
                    }
                }
            }


            if (ParamSist.paramEquals(CodedValues.TPC_EXIGE_CONFIRMACAO_TERMO_CONSENTIMENTO_DADOS_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                model.addAttribute("termoConsentimentoDadosServidor", montarTermoConsentimentoDadosServidor(responsavel));
            }

            int maxQtdArquivos;

            try {
                maxQtdArquivos = Integer.parseInt((String) ParamSist.getInstance().getParam(CodedValues.TPC_QTE_MAX_ARQ_ANEXO_CONTRATO_ASSINATURA_DIGITAL, responsavel));
            } catch (NumberFormatException e) {
                maxQtdArquivos = 15;
            }

            TransferObject autdes = null;
            try {
                autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
                model.addAttribute("adePortabilidade", autdes);
            } catch (AutorizacaoControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final ConsignatariaTransferObject consignatariaDestino = consignatariaController.findConsignataria(csaCodigo, responsavel);
            String csaNomeDestino = consignatariaDestino.getCsaIdentificador() + " - " + (!TextHelper.isNull(consignatariaDestino.getCsaNomeAbreviado()) ? consignatariaDestino.getCsaNomeAbreviado() : consignatariaDestino.getCsaNome());

            BigDecimal coeficienteValor = buscarCoeficienteValor(svcCodigo, rseCodigo, csaCodigo, responsavel, orgCodigo);

            model.addAttribute("temCET", ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel));

            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("csaNome", csaNomeDestino);
            model.addAttribute("coeficienteValor", coeficienteValor);

            // Adiciona ao modelo acessível ao JSP as configurações necessárias
            model.addAttribute("paramSvcCse", paramSvcCse);
            model.addAttribute("mascaraAdeIdentificador", mascaraAdeIdentificador);
            model.addAttribute("labelTipoValor", labelTipoValor);
            model.addAttribute("labelAdePrazo", labelAdePrazo);
            model.addAttribute("qtdMaximaArquivos", maxQtdArquivos);
            model.addAttribute("exigeAssinaturaDigital", exigeAssinaturaDigital);

            // Demais valores necessários
            model.addAttribute("cnvCodigo", cnvCodigo);
            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("serCodigo", serCodigo);
            model.addAttribute("orgCodigo", orgCodigo);
            model.addAttribute("svcCodigo", svcCodigo);
            model.addAttribute("serDataNasc", serDataNasc);
            model.addAttribute("adePeriodicidade", adePeriodicidade);
            model.addAttribute("adeVlr", adeVlr);
            model.addAttribute("adePrazo", adePrazo);
            model.addAttribute("adeCarencia", adeCarencia);
            model.addAttribute("adeResponsavel", adeResponsavel);
            model.addAttribute("adeDataIni", adeDataIni);
            model.addAttribute("adeDataFim", adeDataFim);
            model.addAttribute("ocaPeriodo", ocaPeriodo);
            model.addAttribute("campoCidadeObrigatorio", campoCidadeObrigatorio);
            model.addAttribute("exigeCodAutSolicitacao", exigeCodAutSolicitacao);

            //Envia o código de autorização enviado por SMS ao Servidor.
            final boolean exigeCodAutorizacaoSMS = ParamSist.paramEquals(CodedValues.TPC_ENVIA_SMS_CODIGO_UNICO_AUTORIZACAO_CONSIGNACAO, CodedValues.TPC_SIM, responsavel);
            if (exigeCodAutorizacaoSMS) {
                usuarioController.enviarCodigoAutorizacaoSms(rseCodigo, responsavel);
            }
            model.addAttribute("exigeCodAutorizacaoSMS", exigeCodAutorizacaoSMS);

            // Valida a quantidade máxima de anexos permitidos por contrato
            final String paramQtdeMaxArqAnexo = (String) ParamSist.getInstance().getParam(CodedValues.TPC_QTE_MAX_ARQ_ANEXO_CONTRATO, responsavel);
            final int qtdeMaxArqAnexo = !TextHelper.isNull(paramQtdeMaxArqAnexo) ? Integer.parseInt(paramQtdeMaxArqAnexo) : 10;
            final String nomeAnexo = JspHelper.verificaVarQryStr(request, "FILE1");
            final int totalAnexos = nomeAnexo.split(";").length;
            if (totalAnexos > qtdeMaxArqAnexo) {
                throw new AutorizacaoControllerException("mensagem.erro.quantidade.maxima.anexos.por.contrato.atingida", responsavel);
            }

            if (parametroController.isExigeReconhecimentoFacialServidor(svcCodigo, responsavel)) {
                model.addAttribute("reconhecimentoFacialServidorCartao", "true");
            }
        } catch (NumberFormatException | ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (final ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/solicitarPortabilidade/confirmarPortabilidadeCartao", request, session, model, responsavel);
    }

    @RequestMapping(params = {"acao=incluirReserva"})
    public String incluirReserva(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.reservar.margem.reserva.ja.inserida", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            // Sincroniza a sessão do usuário para evitar duplo request
            synchronized (session) {
                String corCodigo = null;
                if (!"".equals(JspHelper.verificaVarQryStr(request, "PORTAL_BENEFICIO")) && !"".equals(JspHelper.verificaVarQryStr(request, "COR_CODIGO"))) {
                    corCodigo = JspHelper.verificaVarQryStr(request, "COR_CODIGO");
                }

                String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");

                String rseCodigo = responsavel.getRseCodigo();
                String orgCodigo = responsavel.getOrgCodigo();

                String cnvCodigo = JspHelper.verificaVarQryStr(request, "CNV_CODIGO");
                final String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
                Object adeValor = JspHelper.verificaVarQryStr(request, "adeVlr");

                Boolean adeSemPrazo = !TextHelper.isNull(request.getParameter("adeSemPrazo"));
                
                // recupera a permissão corrente do caso de uso
                String permissaoCorrente = CodedValues.FUN_SOLICITAR_PORTABILIDADE;

                if (!AcessoFuncaoServico.temAcessoFuncao(request, permissaoCorrente, responsavel.getUsuCodigo(), svcCodigo)) {
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                if ("".equals(cnvCodigo) || "".equals(csaCodigo) || "".equals(rseCodigo) || "".equals(svcCodigo) || "".equals(orgCodigo) || "".equals(adeValor)) {
                    throw new ViewHelperException("mensagem.erro.interno.contate.administrador", responsavel);
                }

                if (((request.getParameter("adePrazo") == null) && (request.getParameter("adeSemPrazo") == null)) || (request.getParameter("adeCarencia") == null)) {
                    throw new ViewHelperException("mensagem.erro.interno.contate.administrador", responsavel);
                }

                validaInformacoesServidorObrigatorias(request, svcCodigo, csaCodigo, responsavel);

                // Valida a reserva de Margem
                if (!TextHelper.isNull(adeValor)) {
                    try {
                        final String valor = NumberHelper.reformat(adeValor.toString(), NumberHelper.getLang(), "en");
                        final BigDecimal adeVlr = new BigDecimal(valor);
                        AutorizacaoHelper.validarValorAutorizacao(adeVlr, svcCodigo, csaCodigo, responsavel);
                    } catch (final ViewHelperException ex) {
                        session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                }

                Integer adePrazo = null;
                Integer adeCarencia = null;

                final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
                final String classeProcReserva = paramSvcCse.getTpsClasseJavaProcEspecificoReserva();

                final String ppdCodigo = request.getParameter("ppd");

                // Informações Financeiras
                Object adeVlrLiquido = !"".equals(JspHelper.verificaVarQryStr(request, "adeVlrLiquido")) ? JspHelper.verificaVarQryStr(request, "adeVlrLiquido") : null;

                // Dados da consignação
                final List<TransferObject> tdaList = autorizacaoController.lstTipoDadoAdicional(AcaoTipoDadoAdicionalEnum.ALTERA, VisibilidadeTipoDadoAdicionalEnum.WEB, svcCodigo, csaCodigo, responsavel);
                final Map<String, String> dadosAutorizacao = new HashMap<>();
                for (final TransferObject tda : tdaList) {
                    final String tdaCodigo = (String) tda.getAttribute(Columns.TDA_CODIGO);
                    dadosAutorizacao.put(tdaCodigo, JspHelper.parseValor(request, null, "TDA_" + tdaCodigo, (String) tda.getAttribute(Columns.TDA_DOMINIO)));
                }

                // Se o serviço possui processamento específico de reserva,
                // cria a classe de execução
                ProcessaReservaMargem processador = null;
                if (classeProcReserva != null) {
                    try {
                        processador = ProcessaReservaMargemFactory.getProcessador(classeProcReserva);
                    } catch (final ViewHelperException ex) {
                        session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    }
                }
                // Executa validação do passo 2
                if (processador != null) {
                    try {
                        processador.validarPasso2(request);
                    } catch (final ViewHelperException ex) {
                        session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                }

                try {
                    if (!TextHelper.isNull(adeValor)) {
                        adeValor = NumberHelper.reformat(adeValor.toString(), NumberHelper.getLang(), "en");
                        adeValor = new BigDecimal(adeValor.toString());
                    }

                    if (!TextHelper.isNull(request.getParameter("adeCarencia"))) {
                        adeCarencia = Integer.valueOf(request.getParameter("adeCarencia"));
                    }

                    if (!"".equals(JspHelper.verificaVarQryStr(request, "adePrazo"))) {
                        adePrazo = Integer.valueOf(JspHelper.verificaVarQryStr(request, "adePrazo"));
                    }

                } catch (ParseException | NumberFormatException ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                // Identificador da solicitação
                String adeIdentificador = ApplicationResourcesHelper.getMessage("rotulo.consignacao.identificador.solicitacao", responsavel);

                // Atualiza os dados do servidor
                final String serCodigo = responsavel.getCodigoEntidade();
                final ServidorTransferObject servidorUpd = new ServidorTransferObject(serCodigo);
                final java.sql.Date dataNascimento = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SER_DATA_NASC")) ? DateHelper.toSQLDate(DateHelper.parse(JspHelper.verificaVarQryStr(request, "SER_DATA_NASC"), LocaleHelper.getDatePattern())) : null;
                final java.sql.Date dataEmissaoIdt = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SER_DATA_IDT")) ? DateHelper.toSQLDate(DateHelper.parse(JspHelper.verificaVarQryStr(request, "SER_DATA_IDT"), LocaleHelper.getDatePattern())) : null;

                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO, responsavel)) {
                    servidorUpd.setSerEnd(JspHelper.verificaVarQryStr(request, "SER_END"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_COMPLEMENTO, responsavel)) {
                    servidorUpd.setSerCompl(JspHelper.verificaVarQryStr(request, "SER_COMPL"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_BAIRRO, responsavel)) {
                    servidorUpd.setSerBairro(JspHelper.verificaVarQryStr(request, "SER_BAIRRO"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CIDADE, responsavel)) {
                    servidorUpd.setSerCidade(JspHelper.verificaVarQryStr(request, "SER_CIDADE"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF, responsavel)) {
                    servidorUpd.setSerUf(JspHelper.verificaVarQryStr(request, "SER_UF"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CEP, responsavel)) {
                    servidorUpd.setSerCep(JspHelper.verificaVarQryStr(request, "SER_CEP"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_DATA_NASCIMENTO, responsavel)) {
                    servidorUpd.setSerDataNasc(dataNascimento);
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_SEXO, responsavel)) {
                    servidorUpd.setSerSexo(JspHelper.verificaVarQryStr(request, "SER_SEXO"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO_IDT, responsavel)) {
                    servidorUpd.setSerNroIdt(JspHelper.verificaVarQryStr(request, "SER_NRO_IDT"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_DATA_IDT, responsavel)) {
                    servidorUpd.setSerDataIdt(dataEmissaoIdt);
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CELULAR, responsavel)) {
                    servidorUpd.setSerCelular(JspHelper.verificaVarQryStr(request, "SER_CEL"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NACIONALIDADE, responsavel)) {
                    servidorUpd.setSerNacionalidade(JspHelper.verificaVarQryStr(request, "SER_NACIONALIDADE"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NATURALIDADE, responsavel)) {
                    servidorUpd.setSerCidNasc(JspHelper.verificaVarQryStr(request, "SER_NATURALIDADE"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF_NASCIMENTO, responsavel)) {
                    servidorUpd.setSerUfNasc(JspHelper.verificaVarQryStr(request, "SER_UF_NASCIMENTO"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel)) {
                    servidorUpd.setSerTel(JspHelper.verificaVarQryStr(request, "SER_TEL"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO, responsavel)) {
                    servidorUpd.setSerNro(JspHelper.verificaVarQryStr(request, "SER_NRO"));
                } else {
                    servidorUpd.setSerNro(null);
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_EMAIL, responsavel)) {
                    servidorUpd.setSerEmail(JspHelper.verificaVarQryStr(request, "SER_EMAIL"));
                }
                servidorController.updateServidor(servidorUpd, responsavel);

                // registro servidor
                final RegistroServidorTO registroServidorUpd = new RegistroServidorTO(rseCodigo);
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_IBAN, responsavel)) {
                    registroServidorUpd.setRseAgenciaSalAlternativa(JspHelper.verificaVarQryStr(request, "SER_IBAN"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_SALARIO, responsavel)) {
                    registroServidorUpd.setRseSalario(new BigDecimal(!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SER_SALARIO")) ? NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "SER_SALARIO").toString(), NumberHelper.getLang(), "en") : "0.00"));
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_DATA_ADMISSAO, responsavel)) {
                    final java.sql.Date dataAdmissaoSql = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SER_DATA_ADMISSAO")) ? DateHelper.toSQLDate(DateHelper.parse(JspHelper.verificaVarQryStr(request, "SER_DATA_ADMISSAO"), LocaleHelper.getDatePattern())) : null;
                    Timestamp dataAdmissao = null;
                    if (dataAdmissaoSql != null) {
                        dataAdmissao = new Timestamp(dataAdmissaoSql.getTime());
                        registroServidorUpd.setRseDataAdmissao(dataAdmissao);
                    }
                }
                if (ShowFieldHelper.canEdit(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_MUNICIPIO_LOTACAO, responsavel)) {
                    registroServidorUpd.setRseMunicipioLotacao(JspHelper.verificaVarQryStr(request, "RSE_MUNICIPIO_LOTACAO"));
                }
                if (registroServidorUpd != null) {
                    servidorController.updateRegistroServidor(registroServidorUpd, false, false, false, responsavel);
                }

                // verifica anexo obrigatorio
                final String nomeAnexo = JspHelper.verificaVarQryStr(request, "FILE1");
                final String idAnexo = session.getId();
                final String aadDescricao = JspHelper.verificaVarQryStr(request, "AAD_DESCRICAO");

                if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_ANEXO_INCLUSAO_CONTRATOS, CodedValues.TPC_SIM, responsavel) && responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO) && parametroController.isObrigatorioAnexoInclusao(svcCodigo, responsavel) && TextHelper.isNull(nomeAnexo)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.anexo.obrigatorio.svc", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                String adeCodigoNovo = null;

                List<String> adeCodigos = new ArrayList<>();
                adeCodigos.add(request.getParameter("ADE_CODIGO_PORTABILIDADE"));
                final List<String> adeCodigosAntigos = new ArrayList<>(adeCodigos);

                final RenegociarConsignacaoParametros renegociarParam = new RenegociarConsignacaoParametros();
                renegociarParam.setTipo(responsavel.getTipoEntidade());
                renegociarParam.setAdeCodigosRenegociacao(adeCodigosAntigos);
                renegociarParam.setRseCodigo(rseCodigo);
                renegociarParam.setAdeVlr((BigDecimal) adeValor);
                renegociarParam.setCorCodigo(corCodigo);
                renegociarParam.setAdePrazo(adePrazo);
                renegociarParam.setValidaPrazo(!adeSemPrazo);
                renegociarParam.setAdeCarencia(adeCarencia);
                renegociarParam.setAdeIdentificador(adeIdentificador);
                renegociarParam.setCnvCodigo(cnvCodigo);
                renegociarParam.setComSerSenha(true);
                renegociarParam.setAdeIndice(null);
                renegociarParam.setAdeVlrTac(null);
                renegociarParam.setAdeVlrIof(null);
                renegociarParam.setAdeVlrLiquido((BigDecimal) adeVlrLiquido);
                renegociarParam.setAdeVlrMensVinc(null);
                renegociarParam.setAdeTaxaJuros(null);
                renegociarParam.setAdeVlrSegPrestamista(null);
                renegociarParam.setAdeDtHrOcorrencia(null);
                renegociarParam.setAdeCodigosRenegociacao(adeCodigos);
                renegociarParam.setCdeTxtContato("");
                renegociarParam.setAdeBanco(null);
                renegociarParam.setAdeAgencia(null);
                renegociarParam.setAdeConta(null);
                renegociarParam.setPpdCodigo(ppdCodigo);
                renegociarParam.setAdePeriodicidade(null);
                renegociarParam.setNomeAnexo(nomeAnexo);
                renegociarParam.setIdAnexo(idAnexo);
                renegociarParam.setAadDescricao(aadDescricao);
                renegociarParam.setCompraContrato(true);
                renegociarParam.setPortabilidadeCartao(true);

                // Seta os dados genéricos que o responsável tem permissão de alterar
                renegociarParam.setDadosAutorizacaoMap(dadosAutorizacao);

                renegociarParam.setTelaConfirmacaoDuplicidade("S".equals(request.getParameter("telaConfirmacaoDuplicidade")));
                renegociarParam.setChkConfirmarDuplicidade(!TextHelper.isNull(request.getParameter("chkConfirmarDuplicidade")));
                renegociarParam.setMotivoOperacaoCodigoDuplicidade(request.getParameter("TMO_CODIGO"));
                renegociarParam.setMotivoOperacaoObsDuplicidade(request.getParameter("ADE_OBS"));


                renegociarParam.setCompraContrato(Boolean.TRUE);
                renegociarParam.setSerEmail(JspHelper.verificaVarQryStr(request, "serEmail"));
                adeCodigoNovo = renegociarConsignacaoController.renegociar(renegociarParam, responsavel);

                // Remove chave de senha da sessão
                session.removeAttribute("senhaServidorRenegOK");

                if (adeCodigoNovo != null) {
                    if (processador != null) {
                        try {
                            processador.finalizar(request, adeCodigoNovo);
                        } catch (final ViewHelperException ex) {
                            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        }
                    }

                    session.removeAttribute(CodedValues.MSG_ERRO);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informa.portabilidade.sucesso.cartao", responsavel));

                    // Repassa o token salvo, pois o método irá revalidar o token
                    request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

                    String link = "../v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true";
                    link = TextHelper.encode64(SynchronizerToken.updateTokenInURL(link, request));

                    request.setAttribute("url64", link);
                    return "jsp/redirecionador/redirecionar";
                }
            }
        } catch (NumberFormatException | ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (final ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
    }

    private void validaInformacoesServidorObrigatorias(HttpServletRequest request, String svcCodigo, String csaCodigo, AcessoSistema responsavel) throws ZetraException {
        boolean celularObrigatorio = false;
        boolean enderecoObrigatorio = false;
        boolean enderecoCelularObrigatorio = false;

        final List<String> tpsCodigos = new ArrayList<>();
        tpsCodigos.add(CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO);

        // DESENV-17017 - Por decisão do setor de segurança (LGPD) essa implementação não deve ser liberado, por isso os valores são setados como false
        List<TransferObject> paramSvcCsa;
        try {
            paramSvcCsa = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigos, false, responsavel);
            for (final TransferObject param2 : paramSvcCsa) {
                final CustomTransferObject param = (CustomTransferObject) param2;
                if (((param != null) && (param.getAttribute(Columns.PSC_VLR) != null)) && CodedValues.TPS_OBRIGA_INFORMACOES_SERVIDOR_SOLICITACAO.equals(param.getAttribute(Columns.TPS_CODIGO))) {
                    final String pscVlr = !param.getAttribute(Columns.PSC_VLR).toString().isEmpty() ? param.getAttribute(Columns.PSC_VLR).toString() : "";
                    if ("E".equals(pscVlr)) {
                        enderecoObrigatorio = true;
                    } else if ("C".equals(pscVlr)) {
                        celularObrigatorio = true;
                    } else if ("EC".equals(pscVlr)) {
                        enderecoCelularObrigatorio = true;
                    }
                }
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel) || (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_TELEFONE, responsavel) && (celularObrigatorio || enderecoCelularObrigatorio) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SER_TEL")))) {
                throw new ViewHelperException("mensagem.informe.servidor.telefone", responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO, responsavel) || (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_LOGRADOURO, responsavel) && (enderecoObrigatorio || enderecoCelularObrigatorio) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SER_END")))) {
                throw new ViewHelperException("mensagem.informe.servidor.logradouro", responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO, responsavel) || (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_NRO, responsavel) && (enderecoObrigatorio || enderecoCelularObrigatorio) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SER_NRO")))) {
                throw new ViewHelperException("mensagem.informe.servidor.numero", responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_COMPLEMENTO, responsavel) || (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_COMPLEMENTO, responsavel) && (enderecoObrigatorio || enderecoCelularObrigatorio) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SER_COMPL")))) {
                throw new ViewHelperException("mensagem.informe.servidor.complemento", responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_BAIRRO, responsavel) || (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_BAIRRO, responsavel) && (enderecoObrigatorio || enderecoCelularObrigatorio) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SER_BAIRRO")))) {
                throw new ViewHelperException("mensagem.informe.servidor.bairro", responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CIDADE, responsavel) || (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CIDADE, responsavel) && (enderecoObrigatorio || enderecoCelularObrigatorio) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SER_CIDADE")))) {
                throw new ViewHelperException("mensagem.informe.servidor.cidade", responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CEP, responsavel) || (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CEP, responsavel) && (enderecoObrigatorio || enderecoCelularObrigatorio) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SER_CEP")))) {
                throw new ViewHelperException("mensagem.informe.servidor.cep", responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF, responsavel) || (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_UF, responsavel) && (enderecoObrigatorio || enderecoCelularObrigatorio) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SER_UF")))) {
                throw new ViewHelperException("mensagem.informe.servidor.estado", responsavel);
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CELULAR, responsavel) || (ShowFieldHelper.showField(FieldKeysConstants.CONFIRMACAO_DADOS_SERVIDOR_CELULAR, responsavel) && (celularObrigatorio || enderecoCelularObrigatorio) && TextHelper.isNull(JspHelper.verificaVarQryStr(request, "SER_CEL")))) {
                throw new ViewHelperException("mensagem.informe.servidor.celular", responsavel);
            }
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException(ex.getMessageKey(), responsavel);
        }
    }

}
