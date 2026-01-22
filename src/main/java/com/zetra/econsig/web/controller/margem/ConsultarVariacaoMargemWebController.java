package com.zetra.econsig.web.controller.margem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.margem.ExibeMargem;
import com.zetra.econsig.helper.margem.MargemHelper;
import com.zetra.econsig.helper.margem.variacao.VariacaoMargemGraficoDataSet;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.servidor.AbstractConsultarServidorWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ConsultarVariacaoMargemWebController</p>
 * <p>Description: Controlador Web para o caso de uso Consultar Variação da Margem.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/consultarVariacaoMargem" })
public class ConsultarVariacaoMargemWebController extends AbstractConsultarServidorWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsultarVariacaoMargemWebController.class);

    @Autowired
    private ParametroController parametroController;

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        model.addAttribute("exibirCampoSenhaAutorizacao", true);
        model.addAttribute("omitirAdeNumero", true);

        return super.iniciar(request, response, session, model);
    }

    @Override
    protected void definirAcaoRetorno(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final ParamSession paramSession = ParamSession.getParamSession(session);
        model.addAttribute("acaoRetorno", SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request));
    }

    @RequestMapping(params = { "acao=iniciarMargem" })
    public String iniciarMargem(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token de sessão para evitar a chamada direta à operação
            // Esta página não pode ser início de curso pois usa o RSE_CODIGO que deve ser repassado por outra,
            // mas abre em outra janela, assim não pode recriar token
            if (!SynchronizerToken.isTokenValid(request) && !responsavel.isSer()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String rseCodigo = null;

            if (responsavel.isSer()) {
                rseCodigo = responsavel.getRseCodigo();
            } else if (model.asMap().containsKey("rseCodigo")) {
                rseCodigo = (String) model.asMap().get("rseCodigo");
            } else {
                rseCodigo = JspHelper.verificaVarQryStr(request, "RSE_CODIGO");
            }

            if (TextHelper.isNull(rseCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Valida a senha após a pesquisa, pois caso o RSE_CODIGO não tenha sido passado, será obtido da listagem
            if (!validarSenhaServidor(rseCodigo, false, request, session, responsavel)) {
                return iniciar(request, response, session, model);
            }

            // Se alguma das margens não puder ser exibida, então solicita o valor da parcela.
            final String csaCodigo = responsavel.getCsaCodigo();
            final String orgCodigo = responsavel.getOrgCodigo();

            final List<Short> marCodigos = new ArrayList<>();
            final List<MargemTO> margensIncidentes = parametroController.lstMargensIncidentes(null, csaCodigo, orgCodigo, rseCodigo, null, responsavel);

            boolean exibeAlgumaMargem = false;
            final Map<Short, ExibeMargem> exibeMargens = new HashMap<>();
            for (final MargemTO margem : margensIncidentes) {
                final Short marCodigo = margem.getMarCodigo();
                if (!marCodigo.equals(CodedValues.INCIDE_MARGEM_NAO) && (margem.getMarCodigoPai() == null)) {
                    // Ignora quando não incide e quando é margem dependente (aparentemente a rotina não está pronta)
                    final ExibeMargem exibeMargem = new ExibeMargem(margem, responsavel);
                    exibeMargens.put(marCodigo, exibeMargem);
                    marCodigos.add(marCodigo);
                    exibeAlgumaMargem |= exibeMargem.isExibeValor();
                }
            }

            if (!exibeAlgumaMargem) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.variacao.margem.erro", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Parâmetro que mostra a composição da margem do servidor. Para papel de servidor não
            // precisa de parâmetro pois é habilitado via função de permissão exclusiva.
            final boolean possuiVariacaoMargem = (responsavel.isCseSupOrg() && ParamSist.getBoolParamSist(CodedValues.TPC_MOSTRA_VARIACAO_MARGEM_CSE_ORG, responsavel)) || (responsavel.isCsaCor() && ParamSist.getBoolParamSist(CodedValues.TPC_MOSTRA_VARIACAO_MARGEM_CSA_COR, responsavel)) || (responsavel.isSer());

            // Inicia a Lógica de preenchimento para o gráfico de margem líquida
            VariacaoMargemGraficoDataSet variacaoMargemDS = null;
            Map<Date, Map<Short, Double>> variacaoMargem = null;

            if (possuiVariacaoMargem) {
                try {
                    if ((rseCodigo != null) && (rseCodigo.length() > 0)) {
                        variacaoMargemDS = new VariacaoMargemGraficoDataSet(rseCodigo, margensIncidentes, responsavel);
                    }
                    if (variacaoMargemDS != null) {
                        variacaoMargem = variacaoMargemDS.recuperarVariacaoMargem();
                    }
                } catch (final Exception ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    LOG.error(ex.getMessage(), ex);
                    variacaoMargemDS = null;
                    variacaoMargem = null;
                }
            }

            final List<String> chartLegends = new ArrayList<>();
            final List<String> chartLabels = new ArrayList<>();
            final List<String> chartValues = new ArrayList<>();
            if ((variacaoMargemDS != null) && (variacaoMargem != null) && (variacaoMargem.size() > 0)) {
                final Map<Short, List<Double>> values = new HashMap<>();
                final List<Date> datasVariacaoMargem = variacaoMargemDS.recuperarDatasVariacaoMargem();
                for (final Date dataEvento : datasVariacaoMargem) {
                    // Substitui barra por espaços na data para que o texto possa ser quebrado
                    final String data = DateHelper.toDateString(dataEvento).replace('/', ' ');
                    chartLabels.add(data);

                    final Map<Short, Double> dadosHistoricoMargem = variacaoMargem.get(dataEvento);
                    for (final Short marCodigo : marCodigos) {
                        if ((exibeMargens.get(marCodigo) != null) && exibeMargens.get(marCodigo).isExibeValor()) {
                            if (values.get(marCodigo) == null) {
                                values.put(marCodigo, new ArrayList<>());
                            }
                            final double margem = dadosHistoricoMargem.get(marCodigo) != null ? dadosHistoricoMargem.get(marCodigo) : 0.0;
                            values.get(marCodigo).add(margem);
                        }
                    }
                }

                Collections.reverse(chartLabels);
                for (final Short marCodigo : marCodigos) {
                    if ((exibeMargens.get(marCodigo) != null) && exibeMargens.get(marCodigo).isExibeValor()) {
                        final List<Double> lineValues = values.get(marCodigo);
                        Collections.reverse(lineValues);
                        chartLegends.add(TextHelper.forJavaScript(MargemHelper.getInstance().getMarDescricao(marCodigo, responsavel)));
                        chartValues.add("[" + TextHelper.join(lineValues, ",") + "]");
                    }
                }
            }

            // Inicia a Lógica de preenchimento para o gráfico de margem bruta
            VariacaoMargemGraficoDataSet variacaoMargemBrutaDS = null;
            Map<Date, Map<Short, Double>> variacaoMargemBruta = null;

            if (possuiVariacaoMargem) {
                try {
                    if ((rseCodigo != null) && (rseCodigo.length() > 0)) {
                        variacaoMargemBrutaDS = new VariacaoMargemGraficoDataSet(rseCodigo, margensIncidentes, responsavel);
                    }
                    if (variacaoMargemBrutaDS != null) {
                        variacaoMargemBruta = variacaoMargemBrutaDS.recuperarVariacaoMargemBruta();
                    }
                } catch (final Exception ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    LOG.error(ex.getMessage(), ex);
                    variacaoMargemBrutaDS = null;
                    variacaoMargemBruta = null;
                }
            }

            final List<String> chartLegendsBruta = new ArrayList<>();
            final List<String> chartLabelsBruta = new ArrayList<>();
            final List<String> chartValuesBruta = new ArrayList<>();
            if ((variacaoMargemBrutaDS != null) && (variacaoMargemBruta != null) && (variacaoMargemBruta.size() > 0)) {
                final Map<Short, List<Double>> values = new HashMap<>();
                final List<Date> datasVariacaoMargem = variacaoMargemBrutaDS.recuperarDatasVariacaoMargemBruta();
                for (final Date dataEvento : datasVariacaoMargem) {
                    // Substitui barra por espaços na data para que o texto possa ser quebrado
                    final String data = DateHelper.toDateString(dataEvento).replace('/', ' ');
                    chartLabelsBruta.add(data);

                    final Map<Short, Double> dadosHistoricoMargem = variacaoMargemBruta.get(dataEvento);
                    for (final Short marCodigo : marCodigos) {
                        if ((exibeMargens.get(marCodigo) != null) && exibeMargens.get(marCodigo).isExibeValor()) {
                            if (values.get(marCodigo) == null) {
                                values.put(marCodigo, new ArrayList<>());
                            }
                            final double margem = dadosHistoricoMargem.get(marCodigo) != null ? dadosHistoricoMargem.get(marCodigo) : 0.0;
                            values.get(marCodigo).add(margem);
                        }
                    }
                }

                Collections.reverse(chartLabelsBruta);
                for (final Short marCodigo : marCodigos) {
                    if ((exibeMargens.get(marCodigo) != null) && exibeMargens.get(marCodigo).isExibeValor()) {
                        final List<Double> lineValues = values.get(marCodigo);
                        Collections.reverse(lineValues);
                        chartLegendsBruta.add(TextHelper.forJavaScript(MargemHelper.getInstance().getMarDescricao(marCodigo, responsavel)));
                        chartValuesBruta.add("[" + TextHelper.join(lineValues, ",") + "]");
                    }
                }
            }

            final ParamSession paramSession = ParamSession.getParamSession(session);
            String destinoBotaoVoltar = null;
            if (responsavel.isSer()) {
                destinoBotaoVoltar = "../v3/carregarPrincipal";
            } else {
                destinoBotaoVoltar = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory() + "&RSE_CODIGO=" + rseCodigo, request);
                if (destinoBotaoVoltar.contains("consultarVariacaoMargem")) {
                    destinoBotaoVoltar = destinoBotaoVoltar.replace("&acao=pesquisarServidor", "&acao=iniciar");
                } else if (destinoBotaoVoltar.contains("consultarMargem")) {
                    destinoBotaoVoltar = destinoBotaoVoltar.replace("&acao=pesquisarServidor", "&acao=consultar");
                } else {
                    destinoBotaoVoltar = destinoBotaoVoltar.replace("&acao=pesquisarServidor", "&acao=reservarMargem");
                }
            }

            model.addAttribute("responsavel", responsavel);
            model.addAttribute("exibeMargens", exibeMargens);
            model.addAttribute("marCodigos", marCodigos);
            model.addAttribute("variacaoMargem", variacaoMargem);
            model.addAttribute("variacaoMargemDS", variacaoMargemDS);
            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("destinoBotaoVoltar", destinoBotaoVoltar);
            model.addAttribute("chartLegends", chartLegends);
            model.addAttribute("chartLabels", chartLabels);
            model.addAttribute("chartValues", chartValues);
            model.addAttribute("chartLegendsBruta", chartLegendsBruta);
            model.addAttribute("chartLabelsBruta", chartLabelsBruta);
            model.addAttribute("chartValuesBruta", chartValuesBruta);

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        return viewRedirect("jsp/consultarVariacaoMargem/consultarVariacaoMargem", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=comprar" })
    public String comprar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return iniciarMargem(request, response, session, model);
    }

    @RequestMapping(params = { "acao=renegociar" })
    public String renegociar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return iniciarMargem(request, response, session, model);
    }

    @RequestMapping(params = { "acao=reservar" })
    public String reservar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return iniciarMargem(request, response, session, model);
    }

    @RequestMapping(params = { "acao=despesa_individual" })
    public String visualizar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return iniciarMargem(request, response, session, model);
    }

    @Override
    protected String continuarOperacao(String rseCodigo, String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException, UsuarioControllerException {
        model.addAttribute("rseCodigo", rseCodigo);
        model.addAttribute("adeNumero", adeNumero);
        return iniciarMargem(request, response, session, model);
    }

    @Override
    protected String definirProximaOperacao(HttpServletRequest request, AcessoSistema responsavel) {
        return "iniciarMargem";
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws com.zetra.econsig.exception.ViewHelperException {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.variacao.margem.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/consultarVariacaoMargem");
    }

}
