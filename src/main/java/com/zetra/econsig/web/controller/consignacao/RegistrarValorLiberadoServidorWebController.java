package com.zetra.econsig.web.controller.consignacao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.dto.web.ColunaListaConsignacao;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.DeferirConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RegistrarValorLiberadoServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Registrar Valor Liberado.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/registrarValorLiberadoServidor" })
public class RegistrarValorLiberadoServidorWebController extends AbstractListarTodasConsignacoesWebController {

    @Autowired
    private DeferirConsignacaoController deferirConsignacaoController;

    @Autowired
    private AutorizacaoController autorizacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        model.addAttribute("exibirOpcaoListarTodos", Boolean.TRUE);

        return super.iniciar(request, response, session, model);
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.registrar.valor.liberado.servidor", responsavel));
        model.addAttribute("acaoFormulario", "../v3/registrarValorLiberadoServidor");
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_AGUARD_DEFER);

        return sadCodigos;
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona opção para liquidar consignação
        String link = "../v3/registrarValorLiberadoServidor?acao=confirmarRegistro";
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.registrar.abreviado", responsavel);
        String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar", responsavel);
        String msgAlternativa = "";
        String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.multiplo.registra.consignacao", responsavel);
        String msgAdicionalConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.nao.selecionado.registra.consignacao", responsavel);

        acoes.add(new AcaoConsignacao("REGISTRAR_VALOR_CONSIGANCAO", CodedValues.FUN_DATA_VALOR_CONSIGNACAO_LIBERADO_SER, descricao, descricaoCompleta, "desbloqueado.gif", "btnRegistrarValorLiberadoServidor", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null, "chkNotificar"));

        return acoes;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "registrarValorLiberado");

        if (responsavel.isCor()) {
            criterio.setAttribute(Columns.COR_CODIGO, responsavel.getCodigoEntidade());
        }

        if (responsavel.isCsa()) {
            criterio.setAttribute(Columns.CSA_CODIGO, responsavel.getCsaCodigo());
        }

        if (responsavel.isOrg()) {
            criterio.setAttribute(Columns.ORG_CODIGO, responsavel.getOrgCodigo());
        }

        return criterio;
    }

    @RequestMapping(params = { "acao=confirmarRegistro" })
    public String confirmarRegistro(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        try {
            SynchronizerToken.saveToken(request);

            List<String> adesCodigosIncluir = Arrays.asList(JspHelper.verificaVarQryStr(request, "adesCodigosIncluir").split(","));
            List<String> adesCodigosRemover = Arrays.asList(JspHelper.verificaVarQryStr(request, "adesCodigosRemover").split(","));

            for (String ade : adesCodigosIncluir) {
                if (ade != null && !ade.isEmpty()) {
                    TransferObject consignacao = pesquisarConsignacaoController.findAutDesconto(ade, responsavel);

                    if (TextHelper.isNull(consignacao.getAttribute(Columns.ADE_DATA_LIBERACAO_VALOR))) {
                        deferirConsignacaoController.deferir(ade, null, responsavel);
                    }
                }
            }

            autorizacaoController.registraValorLiberadoConsignacao(adesCodigosIncluir, adesCodigosRemover, responsavel);

            String msg = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.registro.sucesso", responsavel);
            session.setAttribute(CodedValues.MSG_INFO, msg);

            ParamSession paramSession = ParamSession.getParamSession(session);

            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        } catch (AutorizacaoControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }

    @Override
    @RequestMapping(params = { "acao=pesquisarConsignacao" })
    public String pesquisarConsignacao(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, @RequestParam(value = "ADE_NUMERO", required = true, defaultValue = "") String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request) && !responsavel.isSer()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        boolean listarTodos = (request.getAttribute("listarTodos") != null);

        String[] adeNumeros = request.getParameterValues("ADE_NUMERO_LIST");

        boolean adeNumerosVazio = TextHelper.isNull(adeNumeros);

        try {
            if (adeNumeros != null) {
                for (String adeNum : adeNumeros) {
                    if (!adeNum.matches("^[0-9]+$")) {
                        throw new AutorizacaoControllerException("mensagem.erro.ade.numero.invalido.arg0", responsavel, adeNum);
                    }
                }
            }

            if (TextHelper.isNull(rseCodigo) && TextHelper.isNull(adeNumero) && (adeNumeros == null || adeNumeros.length == 0) && !listarTodos) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.campo", responsavel));
                return iniciar(request, response, session, model);
            }

            boolean exibeComboOperacoes = (responsavel.isCseSupOrg() && (responsavel.temPermissao(CodedValues.FUN_SUSP_CONSIGNACAO) || responsavel.temPermissao(CodedValues.FUN_REAT_CONSIGNACAO)));
            if (exibeComboOperacoes) {
                request.setAttribute("exibeComboOperacoes", Boolean.TRUE);
            }

            List<String> adeNumeroList = new ArrayList<>();
            if (!TextHelper.isNull(adeNumero)) {
                adeNumeroList.add(adeNumero);
            }
            if (adeNumeros != null && adeNumeros.length > 0) {
                adeNumeroList.addAll(Arrays.asList(adeNumeros));
            }

            CustomTransferObject criterio = new CustomTransferObject();
            TransferObject criteriosPesqPadrao = recuperarCriteriosPesquisaPadrao(request, responsavel);
            if (criteriosPesqPadrao != null) {
                criterio.setAtributos(criteriosPesqPadrao.getAtributos());

                // TODO Remover quando as páginas das operações forem refatoradas, de modo a ficar independente do parâmetro tipo
                if (criteriosPesqPadrao.getAttribute("TIPO_OPERACAO") != null) {
                    model.addAttribute("tipoOperacao", criteriosPesqPadrao.getAttribute("TIPO_OPERACAO").toString());
                }
            }

            int total = pesquisarConsignacaoController.countPesquisaAutorizacaoSemParcela(adeNumeroList, rseCodigo, criterio, responsavel);
            int size = JspHelper.LIMITE;
            List<TransferObject> lstConsignacao = null;
            List<AcaoConsignacao> listaAcoes = definirAcoesListaConsignacao(request, responsavel);

            String rseMatricula = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA");
            String serCpf = JspHelper.verificaVarQryStr(request, "SER_CPF");

            if (total == 0) {
                String msg = ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.erro.nenhum.registro", responsavel) + ":<br>";
                if (listarTodos) {
                    msg += ApplicationResourcesHelper.getMessage("rotulo.pesquisa.listar.todos", responsavel).toUpperCase();
                } else if (!adeNumeroList.isEmpty()) {
                    msg += ApplicationResourcesHelper.getMessage("rotulo.consignacao.numero", responsavel) + ": <span class=\"normal\">" + TextHelper.join(adeNumeroList, ", ") + "</span>";
                } else {

                    if (!rseMatricula.equals("")) {
                        msg += ApplicationResourcesHelper.getMessage("rotulo.servidor.matricula", responsavel) + ": <span class=\"normal\">" + rseMatricula + "</span> ";
                    }
                    if (!serCpf.equals("")) {
                        msg += ApplicationResourcesHelper.getMessage("rotulo.servidor.cpf", responsavel) + ": <span class=\"normal\">" + serCpf + "</span>";
                    }
                }

                // Se não é o servidor que está listando suas consignações e não é pesquisa avançada,
                // se não encontrou nada, retorna para a página de pesquisa
                if (!responsavel.isSer() && TextHelper.isNull(session.getAttribute(CodedValues.MSG_INFO))) {
                    session.setAttribute(CodedValues.MSG_ERRO, msg);

                    if (!TextHelper.isNull(request.getParameter("linkRetHistoricoFluxo"))) {
                        // TODO : Remover tratamento de link de retorno fixo.
                        String linkRet = request.getParameter("linkRetHistoricoFluxo").replace('$', '?').replace('(', '=').replace('|', '&');
                        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(linkRet, request)));
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.nenhumaConsignacaoEncontrada", responsavel));
                        return "jsp/redirecionador/redirecionar";
                    } else {
                        return tratarConsignacaoNaoEncontrada(request, response, session, model);
                    }
                }

                lstConsignacao = new ArrayList<>();

            } else {
                if (CodedValues.FUN_RENE_CONTRATO.equals(responsavel.getFunCodigo()) || CodedValues.FUN_COMP_CONTRATO.equals(responsavel.getFunCodigo()) || CodedValues.FUN_SIMULAR_RENEGOCIACAO.equals(responsavel.getFunCodigo())) {
                    // Se compra ou renegociação, aumenta a quantidade de registros por página
                    // para não exibir ícones de paginação, visto que a query irá retornar todos.
                    size = 1000;
                    model.addAttribute("ocultarPaginacao", Boolean.TRUE);
                }

                lstConsignacao = pesquisarConsignacaoController.pesquisaAutorizacaoSemParcela(adeNumeroList, rseCodigo, criterio, responsavel);

                if ((adeNumeroList.size() == 1 || !TextHelper.isNull(rseCodigo)) && lstConsignacao.size() > 0) {
                    CustomTransferObject first = (CustomTransferObject) lstConsignacao.get(0);
                    String tituloResultado = null;
                    if (ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                        tituloResultado = first.getAttribute(Columns.RSE_MATRICULA) + " - " + first.getAttribute(Columns.SER_NOME);
                    } else {
                        tituloResultado = first.getAttribute(Columns.RSE_MATRICULA) + " - " + first.getAttribute(Columns.SER_CPF) + " - " + first.getAttribute(Columns.SER_NOME);
                    }

                    String footerResultado = ApplicationResourcesHelper.getMessage("rotulo.consignacao.listagem.consignacao.servidor", responsavel, (String) first.getAttribute(Columns.SER_NOME));

                    model.addAttribute("footerResultado", footerResultado);
                    model.addAttribute("tituloResultado", tituloResultado);
                }
            }

            // Valida a senha após a pesquisa, pois caso o RSE_CODIGO não tenha sido passado, será obtido da listagem
            boolean geraSenhaAutOtp = ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_GERA_OTP_SENHA_AUTORIZACAO, CodedValues.TPC_SIM, responsavel);
            if (!validarSenhaServidor(rseCodigo, geraSenhaAutOtp, request, session, responsavel)) {
                return iniciar(request, response, session, model);
            }

            // Se não encontrou todas as consignações passadas pelos ADE. Números
            // inclui mensagem de alerta para o usuário
            if (adeNumeroList.size() > lstConsignacao.size()) {
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.alerta.pesquisa.consignacao.nao.encontrada", responsavel));
            }

            model.addAttribute("lstConsignacao", lstConsignacao);

            model.addAttribute("rseCodigo", rseCodigo);

            // Monta lista de parâmetros através dos parâmetros de request
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            // Logica para exibir e marcar checkbox
            boolean existeRegistro = false;
            boolean checkAllRegistro = false;
            int coutCheckMarcados = 0;

            if (criteriosPesqPadrao.getAttribute("TIPO_OPERACAO").equals("registrarValorLiberado")) {

                for (TransferObject ade : lstConsignacao) {
                    if (ade.getAttribute(Columns.ADE_DATA_LIBERACAO_VALOR) != null) {
                        existeRegistro = true;
                        coutCheckMarcados++;
                    }
                }

                if (coutCheckMarcados == lstConsignacao.size()) {
                    checkAllRegistro = true;
                }
            }

            model.addAttribute("existeCheckBox", existeRegistro);
            model.addAttribute("checkAllCheckBox", checkAllRegistro);

            // Ignora os parâmetros abaixo
            params.remove("exibeInativo");
            params.remove("senha");
            params.remove("serAutorizacao");
            params.remove("cryptedPasswordFieldName");
            params.remove("offset");
            params.remove("back");
            params.remove("linkRet");
            params.remove("linkRet64");
            params.remove("eConsig.page.token");
            params.remove("_skip_history_");
            params.remove("pager");
            params.remove("acao");

            List<String> requestParams = new ArrayList<>(params);

            // Monta link de paginação
            String linkListagemAde = request.getRequestURI() + "?acao=pesquisarConsignacao";
            String queryStringRseCodigo = "";
            if (TextHelper.isNull(request.getParameter("RSE_CODIGO")) && !TextHelper.isNull(rseCodigo)) {
                // Necessário caso tenha vindo direto sem passar pela seleção de servidor
                linkListagemAde += "&RSE_CODIGO=" + rseCodigo;
                queryStringRseCodigo = "RSE_CODIGO=" + rseCodigo + "&";
            }

            configurarPaginador(linkListagemAde, "rotulo.paginacao.titulo.consignacao", total, size, requestParams, false, request, model);

            model.addAttribute("queryString", queryStringRseCodigo + getQueryString(requestParams, request));

            // Define lista de ações
            model.addAttribute("listaAcoes", listaAcoes);

            // Define lista de colunas
            List<ColunaListaConsignacao> lstColunas = definirColunasListaConsignacao(request, responsavel);
            model.addAttribute("listaColunas", lstColunas);

            // Carrega informações acessórias
            carregarInformacoesAcessorias(rseCodigo, adeNumero, lstConsignacao, request, session, model, responsavel);

            // Formata os valores a serem exibidos
            formatarValoresListaConsignacao(lstConsignacao, lstColunas, request, session, responsavel);

            model.addAttribute("exibeAtivoInativo", false);
            model.addAttribute("pesquisaAvancada", false);
            model.addAttribute("adeNumerosVazio", adeNumerosVazio);

            // Define se a coluna de CheckBox sera exibida no carregamento da pagina por padrao ou nao
            boolean ocultarColunaCheckBox = ocultarColunaCheckBox(responsavel);
            model.addAttribute("ocultarColunaCheckBox", ocultarColunaCheckBox);

            // Redireciona para a página de listagem
            return viewRedirect("jsp/consultarConsignacao/listarConsignacao", request, session, model, responsavel);
        } catch (AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (NumberFormatException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }
}
