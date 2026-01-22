package com.zetra.econsig.web.controller.consignacao;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.parametros.AlterarMultiplasConsignacoesParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignacao.AlterarConsignacaoController;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.web.controller.servidor.AbstractConsultarServidorWebController;

/**
 * <p>Title: AjustarConsignacoesMargemWebController</p>
 * <p>Description: Controlador Web para o caso de uso AjustarConsignacoesMargemWebController.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/ajustarConsignacoesMargem" })
public class AjustarConsignacoesMargemWebController extends AbstractConsultarServidorWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AjustarConsignacoesMargemWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private AlterarConsignacaoController alterarConsignacaoController;

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private SistemaController sistemaController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.ajustar.consignacao.margem.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/ajustarConsignacoesMargem");
        model.addAttribute("omitirAdeNumero", true);
        model.addAttribute("exibirFiltroDataInclusao", true);
    }

    @Override
    protected void configurarCampoSistema(Model model, AcessoSistema responsavel) throws ViewHelperException {
        try {
            model.addAttribute("exibirTipoJustica", ShowFieldHelper.showField(FieldKeysConstants.AJUSTAR_CONSIGNACOES_MARGEM_TIPO_JUSTICA, responsavel));
            model.addAttribute("exibirComarcaJustica", ShowFieldHelper.showField(FieldKeysConstants.AJUSTAR_CONSIGNACOES_MARGEM_COMARCA_JUSTICA, responsavel));
            model.addAttribute("exibirNumeroProcesso", ShowFieldHelper.showField(FieldKeysConstants.AJUSTAR_CONSIGNACOES_MARGEM_NUMERO_PROCESSO, responsavel));
            model.addAttribute("exibirDataDecisao", ShowFieldHelper.showField(FieldKeysConstants.AJUSTAR_CONSIGNACOES_MARGEM_DATA_DECISAO, responsavel));
            model.addAttribute("exibirTextoDecisao", ShowFieldHelper.showField(FieldKeysConstants.AJUSTAR_CONSIGNACOES_MARGEM_TEXTO_DECISAO, responsavel));

            model.addAttribute("tipoJusticaObrigatorio", isTipoJusticaObrigatorio(responsavel));
            model.addAttribute("comarcaJusticaObrigatorio", isComarcaJusticaObrigatorio(responsavel));
            model.addAttribute("numeroProcessoObrigatorio", isNumeroProcessoObrigatorio(responsavel));
            model.addAttribute("dataDecisaoObrigatorio", isDataDecisaoObrigatorio(responsavel));
            model.addAttribute("textoDecisaoObrigatorio", isTextoDecisaoObrigatorio(responsavel));
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erro.permissao.nao.encontrado", responsavel, ex.getMessage());
        }
    }

    @Override
    protected boolean isTipoJusticaObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.AJUSTAR_CONSIGNACOES_MARGEM_TIPO_JUSTICA, responsavel);
    }

    @Override
    protected boolean isComarcaJusticaObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.AJUSTAR_CONSIGNACOES_MARGEM_COMARCA_JUSTICA, responsavel);
    }

    @Override
    protected boolean isNumeroProcessoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.AJUSTAR_CONSIGNACOES_MARGEM_NUMERO_PROCESSO, responsavel);
    }

    @Override
    protected boolean isDataDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.AJUSTAR_CONSIGNACOES_MARGEM_DATA_DECISAO, responsavel);
    }

    @Override
    protected boolean isTextoDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
        return ShowFieldHelper.isRequired(FieldKeysConstants.AJUSTAR_CONSIGNACOES_MARGEM_TEXTO_DECISAO, responsavel);
    }

    @RequestMapping(params = { "acao=iniciarAjustarConsignacoesMargem" })
    public String iniciarAjustarConsignacoesMargem(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException, UsuarioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String tipoEntidade = responsavel.getTipoEntidade();
        String codigoEntidade = responsavel.getCodigoEntidade();

        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.addAll(CodedValues.SAD_CODIGOS_ATIVOS);

        CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "ajustar_consignacoes_margem");
        criterio.setAttribute(Columns.CSA_CODIGO, (responsavel.isCsaCor()) ? responsavel.getCsaCodigo() : JspHelper.verificaVarQryStr(request, "CSA_CODIGO"));

        try {
            String periodoIni = JspHelper.verificaVarQryStr(request, "periodoIni");
            if (!periodoIni.equals("") ) {
                periodoIni = DateHelper.reformat(periodoIni, LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
                criterio.setAttribute("periodoIni", periodoIni);
            }
        } catch (ParseException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        try {
            String periodoFim = JspHelper.verificaVarQryStr(request, "periodoFim");
            if (!periodoFim.equals("")) {
                periodoFim = DateHelper.reformat(periodoFim, LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59");
                criterio.setAttribute("periodoFim", periodoFim);
            }
        } catch (ParseException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        model.addAttribute("tipoOperacao", criterio.getAttribute("TIPO_OPERACAO").toString());

        int size = JspHelper.LIMITE;
        int offset = (!TextHelper.isNull(request.getParameter("offset")) && TextHelper.isNum(request.getParameter("offset"))) ? Integer.parseInt(request.getParameter("offset")) : 0;
        offset = -1;
        size = -1;

        // Só posso exibir as consingações que são da CSA por isso essa lista é para exibir as consignções dela.
        List<TransferObject> lstConsignacaoCsa = new ArrayList<>();
        lstConsignacaoCsa = pesquisarConsignacaoController.pesquisaAutorizacao(tipoEntidade, codigoEntidade, rseCodigo, null, null, sadCodigos, null, offset, size, criterio, responsavel);

        // Essas consignações são para calcular o valor da margem em que o contrato será ajustado.
        criterio.remove(Columns.CSA_CODIGO);
        List<TransferObject> lstConsignacao = new ArrayList<>();
        lstConsignacao = pesquisarConsignacaoController.pesquisaAutorizacao(responsavel.getTipoEntidade(), null, rseCodigo, null, null, sadCodigos, null, -1, -1, criterio, responsavel);
        List<String> adeCodigos = new ArrayList<>();

        BigDecimal totalAtual = BigDecimal.ZERO;
        boolean permiteReverterValor = false;

        for (TransferObject contrato : lstConsignacao) {
            String adeCodigo = contrato.getAttribute(Columns.ADE_CODIGO).toString();
            BigDecimal adeVlr = (BigDecimal) contrato.getAttribute(Columns.ADE_VLR);
            totalAtual = totalAtual.add(adeVlr);
            adeCodigos.add(adeCodigo);

            // Busca o valor anterior da última operação
            String dadValor = autorizacaoController.getValorDadoAutDesconto(adeCodigo, CodedValues.TDA_VALOR_PARCELA_ANTERIOR_ALT_MULTIPLA, responsavel);
            if (TextHelper.isDecimalNum(dadValor)) {
                permiteReverterValor = true;
                BigDecimal adeVlrAnt = new BigDecimal(dadValor);
                if (alterarConsignacaoController.verificarAlteracaoPosterior(adeCodigo, adeVlr, adeVlrAnt, responsavel)) {
                    // verifica qual a decisão jucial associada a consignação
                    List<TransferObject> historicoOcorrencia = pesquisarConsignacaoController.historicoAutorizacao(adeCodigo, false, false, responsavel);
                    for(TransferObject historico : historicoOcorrencia) {
                        String djuProcesso = (String) historico.getAttribute(Columns.DJU_NUM_PROCESSO);
                        if(!TextHelper.isNull(djuProcesso)) {
                            String tjuDescricao = (String) historico.getAttribute(Columns.TJU_DESCRICAO);
                            model.addAttribute("processoExistente", ApplicationResourcesHelper.getMessage("mensagem.ajustar.consignacoes.a.margem.existe.processo.judicial", responsavel, djuProcesso,tjuDescricao));
                            break;
                        }
                    }
                }
            }
        }

        if (lstConsignacaoCsa == null || lstConsignacaoCsa.isEmpty()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.transf.contratos.nenhuma.consignacao.encontrada", responsavel));
            return super.iniciar(request, response, session, model);
        }

        // Consulta e valida se existe margens negativas para montar o combo de margem
        List<MargemTO> margens = new ArrayList<>();
        try {
            margens = consultarMargemController.consultarMargem(rseCodigo, null, null, responsavel.getCsaCodigo(), false, null, false, null, responsavel);
        } catch (ServidorControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        Map <Short, BigDecimal> somatorioPorIncidencia = new HashMap<>();
        for (MargemTO margem : margens) {
            BigDecimal somatorio = somatorioPorIncidencia.get(margem.getMarCodigo());

            // Recupera total incidencia
            if (adeCodigos != null && !adeCodigos.isEmpty()) {
                if (TextHelper.isNull(somatorio)) {
                    somatorio = pesquisarConsignacaoController.obterTotalAdeVlrPorPeriodoInclusao(rseCodigo, null, null, margem.getMarCodigo(), adeCodigos, responsavel);
                    if (TextHelper.isNull(somatorio)) {
                        somatorio = BigDecimal.ZERO;
                    }
                    somatorioPorIncidencia.put(margem.getMarCodigo(), somatorio);
                }

                if (margem != null && !TextHelper.isNull(margem.getMrsMargem())) {
                    margem.setMrsMargem(margem.getMrsMargem().subtract(somatorio));
                } else {
                    margem.setMrsMargem(somatorio.negate());
                }
            }
        }


        List<MargemTO> margensNegativas = new ArrayList<>();
        for (MargemTO margem : margens) {
            if(!TextHelper.isNull(margem.getMrsMargemRest()) && margem.getMrsMargemRest().compareTo(BigDecimal.ZERO) < 0) {
                margensNegativas.add(margem);
            }
        }
        if (margensNegativas == null || margensNegativas.isEmpty()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.ajustar.consignacao.a.margem.negativa.nao.existe", responsavel));
            return super.iniciar(request, response, session, model);
        }

        model.addAttribute("margens", margensNegativas);
        model.addAttribute("autdesList", lstConsignacaoCsa);
        model.addAttribute("adeCodigos", adeCodigos);
        model.addAttribute("totalAtual", totalAtual);
        model.addAttribute("rseCodigo", rseCodigo);
        model.addAttribute("periodoIni", criterio.getAttribute("periodoIni"));
        model.addAttribute("periodoFim", criterio.getAttribute("periodoFim"));
        model.addAttribute("permiteReverterValor", permiteReverterValor);

        return viewRedirect("jsp/alterarConsignacao/ajustarConsignacoesMargem", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=validar" })
    public String validar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        //A operação de ajuste afeta todas as consignações do servidor e não somente as da consignatária que está efetuando a operação
        //por este motivo é necessário buscar todas as consignações deste servidor para o devido ajuste.
        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.addAll(CodedValues.SAD_CODIGOS_ATIVOS);

        CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "ajustar_consignacoes_margem");

        String rseCodigo = request.getParameter("rseCodigo");
        String periodoIni = request.getParameter("periodoIni");
        String periodoFim = request.getParameter("periodoFim");
        if(!TextHelper.isNull(periodoIni)) {
            criterio.setAttribute("periodoIni", periodoIni);
        }
        if(!TextHelper.isNull(periodoFim)) {
            criterio.setAttribute("periodoFim", periodoFim);
        }

        List<TransferObject> lstConsignacao = new ArrayList<>();
        lstConsignacao = pesquisarConsignacaoController.pesquisaAutorizacao(responsavel.getTipoEntidade(), null, rseCodigo, null, null, sadCodigos, null, -1, -1, criterio, responsavel);

        List<String> adeCodigos = new ArrayList<>();
        boolean restaurarValor = "true".equals(JspHelper.verificaVarQryStr(request, "restaurarValor"));

        for (TransferObject consignacao : lstConsignacao) {
            String adeCodigo = consignacao.getAttribute(Columns.ADE_CODIGO).toString();
            adeCodigos.add(adeCodigo);
        }

        try {
            AlterarMultiplasConsignacoesParametros parametros = new AlterarMultiplasConsignacoesParametros();
            parametros.setAdeCodigos(adeCodigos);
            parametros.setIgnorarAltPosterior(true);
            parametros.setAlterarPrazo(true);
            parametros.setRestaurarValor(restaurarValor);
            parametros.setAjustaConsignacoesMargem(true);

            String margem = request.getParameter("ajustarMargem");
            if(TextHelper.isNull(margem) && !restaurarValor) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.ajustar.consignacao.a.margem.selecione.margem", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            BigDecimal vlrTotalNovo = null;
            String marCodigo = null;

            if(!restaurarValor) {
                String[] margemArray = margem.split(";");
                marCodigo = margemArray[0];
                BigDecimal margemRest = NumberHelper.parseDecimal(margemArray[1]).abs();

                parametros.setMarCodigo(Short.valueOf(marCodigo));
                vlrTotalNovo = margemRest;
            }

            if (!parametros.isRestaurarValor() && (vlrTotalNovo == null || vlrTotalNovo.signum() <= 0)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.ajustar.consignacao.a.margem.selecione.margem", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            parametros.setVlrTotalNovo(vlrTotalNovo);
            parametros.setAlterarIncidencia(true);

            // Realiza a alteração múltipla transacional
            List<TransferObject> autdesList = alterarConsignacaoController.validarAlteracaoMultiplosAdes(parametros, responsavel);

            List<Map<String, Object>> adeMapList = autdesList.stream().map((to) -> {return to.getAtributos();}).collect(Collectors.toList());
            ObjectMapper mapperAdes = new ObjectMapper();

            model.addAttribute("autdesListRaw", new String(Base64.getEncoder().encode(mapperAdes.writeValueAsString(adeMapList).replaceAll(" ", "").getBytes())));
            model.addAttribute("parametros", new String(Base64.getEncoder().encode(mapperAdes.writeValueAsString(parametros).replaceAll(" ", "").getBytes())));

            // Dados da tela de validação: CPF, Matrícula, Nome, CSA, Verba, Serviço, Valor Antigo, Valor Novo, Prazo Antigo, Prazo Novo e Última Parcela.
            String[] colunas = {
                    FieldKeysConstants.LISTA_CONSIGNACAO_CONSIGNATARIA,
                    FieldKeysConstants.LISTA_CONSIGNACAO_SERVICO,
                    FieldKeysConstants.LISTA_CONSIGNACAO_NUMERO,
                    FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA,
                    FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA_NOVO,
                    FieldKeysConstants.LISTA_CONSIGNACAO_PRAZO,
                    FieldKeysConstants.LISTA_CONSIGNACAO_PRAZO_NOVO,
                    FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_ULTIMA_PARCELA
            };
            formatarValoresListaConsignacao(autdesList, colunas, responsavel);

            // Como é uma consignatária que efetua a operação e afeta a todas, só posso mostrar os contratos pertinentes a essa consginatária
            List<TransferObject> consignacaoCsa = new ArrayList<>();
            for (TransferObject autDes : autdesList) {
                String csaCodigo = autDes.getAttribute(Columns.CSA_CODIGO).toString();
                if(csaCodigo.equals(responsavel.getCodigoEntidade())) {
                    consignacaoCsa.add(autDes);
                }
            }

            model.addAttribute("autdesList", consignacaoCsa);

            if (consignacaoCsa != null && !consignacaoCsa.isEmpty()) {
                model.addAttribute("servidor", consignacaoCsa.get(0));

                BigDecimal totalAtual = BigDecimal.ZERO;
                BigDecimal totalNovo = BigDecimal.ZERO;
                for (TransferObject ade : consignacaoCsa) {
                    totalAtual = totalAtual.add((BigDecimal) ade.getAttribute(Columns.ADE_VLR));
                    totalNovo = totalNovo.add((BigDecimal) ade.getAttribute("adeVlrNovo"));
                }
                model.addAttribute("totalAtual", totalAtual);
                model.addAttribute("totalNovo", totalNovo);
            }

            List<TransferObject> lstMtvOperacao = tipoMotivoOperacaoController.lstMotivoOperacaoConsignacao(CodedValues.STS_ATIVO, responsavel);
            model.addAttribute("lstMtvOperacao", lstMtvOperacao);

            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel)) {
                List<TransferObject> lstTipoJustica = sistemaController.lstTipoJustica(responsavel);
                model.addAttribute("lstTipoJustica", lstTipoJustica);
            }

            return viewRedirect("jsp/alterarConsignacao/validarAjusteConsignacoesMargem", request, session, model, responsavel);
        } catch (ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (JsonProcessingException e) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        List<Map<String, Object>> autdesListRaw = null;
        Map<String, Object> parametrosMap = null;

        if (request.getParameter("autdesListRaw") != null && request.getParameter("parametros") != null) {
            Gson gson = new Gson();
            autdesListRaw = gson.fromJson(new String(Base64.getDecoder().decode(request.getParameter("autdesListRaw"))), List.class);
            parametrosMap = gson.fromJson(new String(Base64.getDecoder().decode(request.getParameter("parametros"))), Map.class);
        }

        List<TransferObject> autdesList = null;

        if (autdesListRaw != null && !autdesListRaw.isEmpty()) {
            for (Map<String, Object> adeMap: autdesListRaw) {
                if (autdesList ==  null) {
                    autdesList = new ArrayList<>();
                }

                CustomTransferObject newTO = new CustomTransferObject();
                newTO.setAtributos(adeMap);
                if (newTO.getAttribute(Columns.ADE_INC_MARGEM) != null) {
                    newTO.setAttribute(Columns.ADE_INC_MARGEM, ((Double) newTO.getAttribute(Columns.ADE_INC_MARGEM)).shortValue());
                }
                if (newTO.getAttribute(Columns.ADE_DATA) != null) {
                    newTO.setAttribute(Columns.ADE_DATA, new Date(((Double) newTO.getAttribute(Columns.ADE_DATA)).longValue()));
                }
                if (newTO.getAttribute(Columns.ADE_ANO_MES_INI) != null) {
                    newTO.setAttribute(Columns.ADE_ANO_MES_INI, new Date(((Double) newTO.getAttribute(Columns.ADE_ANO_MES_INI)).longValue()));
                }
                if (newTO.getAttribute(Columns.ADE_ANO_MES_FIM) != null) {
                    newTO.setAttribute(Columns.ADE_ANO_MES_FIM, new Date(((Double) newTO.getAttribute(Columns.ADE_ANO_MES_FIM)).longValue()));
                }
                if (newTO.getAttribute(Columns.ADE_INT_FOLHA) != null) {
                    newTO.setAttribute(Columns.ADE_INT_FOLHA, ((Double) newTO.getAttribute(Columns.ADE_INT_FOLHA)).shortValue());
                }
                if (newTO.getAttribute("adeVlrNovo") != null) {
                    newTO.setAttribute("adeVlrNovo", new BigDecimal(((Double) newTO.getAttribute("adeVlrNovo"))));
                }
                if (newTO.getAttribute("adePrazoNovo") != null) {
                    newTO.setAttribute("adePrazoNovo", ((Double) newTO.getAttribute("adePrazoNovo")).intValue());
                }
                if (TextHelper.isNull(newTO.getAttribute("ajusteConsignacoesMargem"))) {
                    newTO.setAttribute("ajusteConsignacoesMargem", true);
                }
                autdesList.add(newTO);
            }
        }

        try {
            AlterarMultiplasConsignacoesParametros parametros = null;
            if (parametrosMap != null) {
                parametros = new AlterarMultiplasConsignacoesParametros();
                parametros.setarCampos(parametrosMap);
            }

            if (parametros == null || autdesList == null || autdesList.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.alterar.multiplo.consignacao.nenhum.registro.selecionado", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            parametros.setTmoCodigo(JspHelper.verificaVarQryStr(request, "tmoCodigo"));
            parametros.setOcaObs(JspHelper.verificaVarQryStr(request, "adeObs"));

            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel)) {
                // Dados de decisão judicial
                String tjuCodigo = JspHelper.verificaVarQryStr(request, "tjuCodigo");
                String cidCodigo = JspHelper.verificaVarQryStr(request, "cidCodigo");
                String djuNumProcesso = JspHelper.verificaVarQryStr(request, "djuNumProcesso");
                String djuData = JspHelper.verificaVarQryStr(request, "djuData");
                String djuTexto = JspHelper.verificaVarQryStr(request, "djuTexto");

                if ((isTipoJusticaObrigatorio(responsavel) && TextHelper.isNull(tjuCodigo)) ||
                        (isComarcaJusticaObrigatorio(responsavel) && TextHelper.isNull(cidCodigo)) ||
                        (isNumeroProcessoObrigatorio(responsavel) && TextHelper.isNull(djuNumProcesso)) ||
                        (isDataDecisaoObrigatorio(responsavel) && TextHelper.isNull(djuData)) ||
                        (isTextoDecisaoObrigatorio(responsavel) && TextHelper.isNull(djuTexto))) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.alteracao.avancada.decisao.judicial.dados.minimos", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                if (!TextHelper.isNull(tjuCodigo) && !TextHelper.isNull(djuTexto) && !TextHelper.isNull(djuData)) {
                    // Se informado, pelo menos tipo de justiça, texto e data devem ser informados. Os demais são opcionais.
                    parametros.setTjuCodigo(tjuCodigo);
                    parametros.setCidCodigo(cidCodigo);
                    parametros.setDjuNumProcesso(djuNumProcesso);
                    parametros.setDjuData(DateHelper.parse(djuData, LocaleHelper.getDatePattern()));
                    parametros.setDjuTexto(djuTexto);
                }
            }

            // Realiza a alteração múltipla transacional
            alterarConsignacaoController.alterarMultiplosAdes(autdesList, parametros, responsavel);

            consignatariaController.enviarEmailNotificacaoConsignacaoAjustadoMargem(autdesList, parametros, responsavel);

            // Define mensagem de sucesso e retorna para o usuário
            if(!parametros.isRestaurarValor()) {
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.ajustar.consignacoes.a.margem.sucesso", responsavel));
            } else {
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.ajustar.consignacoes.a.margem.retaurar.sucesso", responsavel));
            }
            return iniciar(request, response, session, model);

        } catch (ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (ParseException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    private List<TransferObject> formatarValoresListaConsignacao(List<TransferObject> lstConsignacao, String[] colunas, AcessoSistema responsavel) {
        for (TransferObject ade : lstConsignacao) {
            ade = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) ade, null, responsavel);

            for (String chaveCampo : colunas) {
                String valorCampo = "";

                if (chaveCampo.equals(FieldKeysConstants.LISTA_CONSIGNACAO_CONSIGNATARIA)) {
                    valorCampo = ade.getAttribute(Columns.CSA_IDENTIFICADOR) + " - " + (!TextHelper.isNull(ade.getAttribute(Columns.CSA_NOME_ABREV)) ? ade.getAttribute(Columns.CSA_NOME_ABREV).toString() : ade.getAttribute(Columns.CSA_NOME).toString());

                } else if (chaveCampo.equals(FieldKeysConstants.LISTA_CONSIGNACAO_NUMERO)) {
                    valorCampo = ade.getAttribute(Columns.ADE_NUMERO).toString();

                } else if (chaveCampo.equals(FieldKeysConstants.LISTA_CONSIGNACAO_SERVICO)) {
                    String adeCodReg = ((ade.getAttribute(Columns.ADE_COD_REG) != null && !ade.getAttribute(Columns.ADE_COD_REG).equals("")) ? ade.getAttribute(Columns.ADE_COD_REG).toString() : CodedValues.COD_REG_DESCONTO);
                    valorCampo = (!TextHelper.isNull(ade.getAttribute(Columns.CNV_COD_VERBA)) ? ade.getAttribute(Columns.CNV_COD_VERBA).toString() : ade.getAttribute(Columns.SVC_IDENTIFICADOR).toString()) + (!TextHelper.isNull(ade.getAttribute(Columns.ADE_INDICE)) ? ade.getAttribute(Columns.ADE_INDICE).toString() : "") + " - " + (ade.getAttribute(Columns.SVC_DESCRICAO).toString()) + (adeCodReg.equals(CodedValues.COD_REG_ESTORNO) ? " - " + ApplicationResourcesHelper.getMessage("rotulo.consignacao.cod.reg.credito", responsavel) : "");

                } else if (chaveCampo.equals(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA)) {
                    try {
                        valorCampo = ParamSvcTO.getDescricaoTpsTipoVlr((String) ade.getAttribute(Columns.ADE_TIPO_VLR)) + " " + (!TextHelper.isNull(ade.getAttribute("adeVlrAtual")) ? NumberHelper.reformat(ade.getAttribute("adeVlrAtual").toString(), "en", NumberHelper.getLang()) : "");
                    } catch (ParseException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }

                } else if (chaveCampo.equals(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA_NOVO)) {
                    try {
                        valorCampo = ParamSvcTO.getDescricaoTpsTipoVlr((String) ade.getAttribute(Columns.ADE_TIPO_VLR)) + " " + (!TextHelper.isNull(ade.getAttribute("adeVlrNovo")) ? NumberHelper.reformat(ade.getAttribute("adeVlrNovo").toString(), "en", NumberHelper.getLang()) : "");
                    } catch (ParseException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }

                } else if (chaveCampo.equals(FieldKeysConstants.LISTA_CONSIGNACAO_PRAZO)) {
                    if (!TextHelper.isNull(ade.getAttribute("adePrazoAtual"))) {
                        valorCampo = ade.getAttribute("adePrazoAtual").toString();
                    } else {
                        valorCampo = ApplicationResourcesHelper.getMessage("rotulo.indeterminado.abreviado", responsavel);
                    }

                } else if (chaveCampo.equals(FieldKeysConstants.LISTA_CONSIGNACAO_PRAZO_NOVO)) {
                    if (!TextHelper.isNull(ade.getAttribute("adePrazoNovo"))) {
                        valorCampo = ade.getAttribute("adePrazoNovo").toString();
                    } else {
                        valorCampo = ApplicationResourcesHelper.getMessage("rotulo.indeterminado.abreviado", responsavel);
                    }

                } else if (chaveCampo.equals(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_ULTIMA_PARCELA)) {
                    try {
                        if (ade.getAttribute("vlrParcelaExtra") != null) {
                            valorCampo = ParamSvcTO.getDescricaoTpsTipoVlr((String) ade.getAttribute(Columns.ADE_TIPO_VLR)) + " " + (!TextHelper.isNull(ade.getAttribute("vlrParcelaExtra")) ? NumberHelper.reformat(ade.getAttribute("vlrParcelaExtra").toString(), "en", NumberHelper.getLang()) : "");
                        } else {
                            valorCampo = ParamSvcTO.getDescricaoTpsTipoVlr((String) ade.getAttribute(Columns.ADE_TIPO_VLR)) + " " + (!TextHelper.isNull(ade.getAttribute("adeVlrNovo")) ? NumberHelper.reformat(ade.getAttribute("adeVlrNovo").toString(), "en", NumberHelper.getLang()) : "");
                        }
                    } catch (ParseException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }

                ade.setAttribute(chaveCampo, valorCampo);
            }
        }

        return lstConsignacao;
    }

    @Override
    protected String definirProximaOperacao(HttpServletRequest request, AcessoSistema responsavel) {
        return "iniciarAjustarConsignacoesMargem";
    }

    @Override
    protected String continuarOperacao(String rseCodigo, String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException, UsuarioControllerException {
        return iniciarAjustarConsignacoesMargem(rseCodigo, request, response, session, model);
    }
}
