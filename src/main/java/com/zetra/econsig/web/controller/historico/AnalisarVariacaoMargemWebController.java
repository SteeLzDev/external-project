package com.zetra.econsig.web.controller.historico;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.persistence.entity.HistoricoMargemFolha;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: AnalisarVariacaoMargemWebController</p>
 * <p>Description: Controlador Web para o caso de uso analisar variação margem.</p>
 * <p>Copyright: Copyright (c) 2002-2025</p>
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/analisarVariacaoMargem" })
public class AnalisarVariacaoMargemWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AnalisarVariacaoMargemWebController.class);

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @RequestMapping(params = {"acao=iniciar"})
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ParseException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final ParamSession paramSession = ParamSession.getParamSession(session);
        String rseCodigo = "";

        if (!responsavel.isSer()) {
            // Valida o token de sessão para evitar a chamada direta da operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);
            rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
        } else {
            rseCodigo = responsavel.getRseCodigo();
        }

        if (TextHelper.isNull(rseCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String periodoIniStr = JspHelper.verificaVarQryStr(request, "periodoIni");
        final String periodoFimStr = JspHelper.verificaVarQryStr(request, "periodoFim");
        final String marCodigoStr = JspHelper.verificaVarQryStr(request, "marCodigo");
        final String variacaoIniStr = JspHelper.verificaVarQryStr(request, "variacaoIni");
        final String variacaoFimStr = JspHelper.verificaVarQryStr(request, "variacaoFim");
        Date periodoIni = null;
        Date periodoFim = null;
        Short marCodigo = null;
        BigDecimal variacaoIni = null;
        BigDecimal variacaoFim = null;

        if (!TextHelper.isNull(periodoIniStr)) {
            periodoIni = DateHelper.parse(periodoIniStr, LocaleHelper.getDatePattern());
        }

        if (!TextHelper.isNull(periodoFimStr)) {
            periodoFim = DateHelper.parse(periodoFimStr, LocaleHelper.getDatePattern());
        }

        if (!TextHelper.isNull(marCodigoStr)) {
            marCodigo = Short.valueOf(marCodigoStr);
        }

        if (!TextHelper.isNull(variacaoIniStr)) {
            variacaoIni = new BigDecimal(NumberHelper.reformat(variacaoIniStr, NumberHelper.getLang(), "en"));
        }

        if (!TextHelper.isNull(variacaoFimStr)) {
            variacaoFim = new BigDecimal(NumberHelper.reformat(variacaoFimStr, NumberHelper.getLang(), "en"));
        }

        // Busca os dados e as margens do servidor
        CustomTransferObject servidor = null;
        List<MargemTO> margensServidor = null;
        final List<MargemTO> margensServidorFilter = new ArrayList<>();
        try {
            // Busca os dados do servidor
            servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
            // Busca as margens do servidor
            margensServidor = consultarMargemController.consultarMargem(rseCodigo, null, null, null, true, true, responsavel);
        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }
        margensServidorFilter.addAll(margensServidor);
        model.addAttribute("margensServidor", margensServidorFilter);

        if (marCodigo != null) {
            final List<MargemTO> margensServidorList = new ArrayList<>();
            for (final MargemTO margem : margensServidor) {
                if (margem.getMarCodigo().equals(marCodigo)) {
                    margensServidorList.add(margem);
                }
            }
            margensServidor.clear();
            margensServidor.addAll(margensServidorList);
        }

        // Inicio lógica do histórico de margem, onde iremos pegar todos os históricos para calcular a variação com o período anterior
        // Além disso, agrupar por marCodigo para então poder usar esse hash no front para tratar os resultados com agrupamentos de margem.
        final Map<Short, List<CustomTransferObject>> lstHistoricoVariacaoMap = new HashMap<>();
        try {
            // Pela regra desta página, só devemos trabalhar com os últimos 12 períodos de cada margem.
            // Por isso é preciso filtrar os resultados por margem com o limite de 12, ou seja, cada margem com até 12 registros.
            final Map<Short, List<HistoricoMargemFolha>> historicoFiltrado = new HashMap<>();
            final List<HistoricoMargemFolha> lstHistoricoMargemFolhaTemp = servidorController.lstHistoricoMargemFolhaRseFiltro(rseCodigo, periodoIni, periodoFim, marCodigo, responsavel);

            final Map<Short, List<HistoricoMargemFolha>> historicoAgrupadoMargem = lstHistoricoMargemFolhaTemp.stream().collect(Collectors.groupingBy(HistoricoMargemFolha::getMarCodigo));

            for (final Map.Entry<Short, List<HistoricoMargemFolha>> entry : historicoAgrupadoMargem.entrySet()) {
                final List<HistoricoMargemFolha> historicoLista = entry.getValue();

                final List<HistoricoMargemFolha> ultimos12 = historicoLista.size() > 12
                        ? historicoLista.subList(historicoLista.size() - 12, historicoLista.size())
                        : historicoLista;

                historicoFiltrado.put(entry.getKey(), ultimos12);
            }

            for (final Map.Entry<Short, List<HistoricoMargemFolha>> mapHistorico : historicoFiltrado.entrySet()) {

                BigDecimal margemAnterior = null;
                final List<CustomTransferObject> listHistoricoVariacao = new ArrayList<>();

                for (final HistoricoMargemFolha historicoMargemFolha : mapHistorico.getValue()) {
                    final CustomTransferObject historicoVariacao = new CustomTransferObject();
                    BigDecimal variacao = BigDecimal.ZERO;

                    if ((margemAnterior != null) && (margemAnterior.compareTo(BigDecimal.ZERO) != 0)) {
                        // Cálculo da variação
                        variacao = historicoMargemFolha.getHmaMargemFolha().subtract(margemAnterior)
                                                       .divide(margemAnterior, 4, RoundingMode.HALF_UP)
                                                       .multiply(BigDecimal.valueOf(100));
                    }

                    if ((variacaoIni != null) && (variacaoFim != null) && ((variacao.compareTo(variacaoIni) < 0) || (variacao.compareTo(variacaoFim) >= 0))) {
                        continue;
                    }
                    historicoVariacao.setAttribute("historicoMargemFolha", historicoMargemFolha);
                    historicoVariacao.setAttribute("variacao", variacao);

                    margemAnterior = historicoMargemFolha.getHmaMargemFolha();

                    listHistoricoVariacao.add(historicoVariacao);
                }

                if (!listHistoricoVariacao.isEmpty()) {
                    lstHistoricoVariacaoMap.put(mapHistorico.getKey(), listHistoricoVariacao);
                }
            }

        } catch (final ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("servidor", servidor);
        model.addAttribute("margensTab", margensServidor);
        model.addAttribute("lstHistoricoVariacaoMap", lstHistoricoVariacaoMap);

        String destinoBotaoVoltar = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory() + "&RSE_CODIGO=" + rseCodigo, request);
        if (destinoBotaoVoltar.contains("consultarMargem")) {
            destinoBotaoVoltar = destinoBotaoVoltar.replace("&acao=pesquisarServidor", "&acao=consultar");
        } else {
            destinoBotaoVoltar = destinoBotaoVoltar.replace("&acao=pesquisarServidor", "&acao=reservarMargem");
        }

        model.addAttribute("rseCodigo", rseCodigo);
        model.addAttribute("paramSession", paramSession);
        model.addAttribute("destinoBotaoVoltar", destinoBotaoVoltar);

        return viewRedirect("jsp/analisarVariacaoMargem/analisarVariacaoMargem", request, session, model, responsavel);
    }
}
