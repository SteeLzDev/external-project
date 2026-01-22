package com.zetra.econsig.web.controller.consignacao;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.SaldoDevedorTransferObject;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.dto.web.ColunaListaConsignacao;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.CalendarioControllerException;
import com.zetra.econsig.exception.FinanciamentoDividaControllerException;
import com.zetra.econsig.exception.LeilaoSolicitacaoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ParcelaControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.SaldoDevedorControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.exception.ValidarDocumentoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.consignacao.BoletoHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.servico.NaturezaRelSvc;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacao;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacao;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacao;
import com.zetra.econsig.persistence.entity.StatusSolicitacao;
import com.zetra.econsig.service.calendario.CalendarioController;
import com.zetra.econsig.service.compra.CompraContratoController;
import com.zetra.econsig.service.comunicacao.ComunicacaoController;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.CancelarConsignacaoController;
import com.zetra.econsig.service.consignacao.EditarAnexoConsignacaoController;
import com.zetra.econsig.service.consignacao.InserirSolicitacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignacao.ReimplantarConsignacaoController;
import com.zetra.econsig.service.consignacao.TransferirConsignacaoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.financiamentodivida.FinanciamentoDividaController;
import com.zetra.econsig.service.leilao.LeilaoSolicitacaoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.parcela.ParcelaController;
import com.zetra.econsig.service.saldodevedor.SaldoDevedorController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.service.validardocumento.ValidarDocumentoController;
import com.zetra.econsig.values.CodedNames;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.values.MotivoAdeNaoRenegociavelEnum;
import com.zetra.econsig.values.RiscoRegistroServidorEnum;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;
import com.zetra.econsig.web.controller.servidor.AbstractConsultarServidorWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: AbstractConsultarConsignacaoWebController</p>
 * <p>Description: Controlador Web base para o casos de uso que consultam e listam consignações.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class AbstractConsultarConsignacaoWebController extends AbstractConsultarServidorWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AbstractConsultarConsignacaoWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private CancelarConsignacaoController cancelarConsignacaoController;

    @Autowired
    private CompraContratoController compraContratoController;

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Autowired
    private EditarAnexoConsignacaoController editarAnexoConsignacaoController;

    @Autowired
    private FinanciamentoDividaController financiamentoDividaController;

    @Autowired
    private InserirSolicitacaoController inserirSolicitacaoController;

    @Autowired
    private LeilaoSolicitacaoController leilaoSolicitacaoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private ParcelaController parcelaController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private SaldoDevedorController saldoDevedorController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private SimulacaoController simulacaoController;

    @Autowired
    private TransferirConsignacaoController transferirConsignacaoController;

    @Autowired
    private ReimplantarConsignacaoController reimplantarConsignacaoController;

    @Autowired
    private CalendarioController calendarioController;

    @Autowired
    private ValidarDocumentoController validarDocumentoController;

    @Autowired
    private ComunicacaoController comunicacaoController;

    @Override
    protected String continuarOperacao(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, @RequestParam(value = "ADE_NUMERO", required = true, defaultValue = "") String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return pesquisarConsignacao(rseCodigo, adeNumero, request, response, session, model);
    }

    @Override
    protected String definirProximaOperacao(HttpServletRequest request, AcessoSistema responsavel) {
        return "pesquisarConsignacao";
    }

    @RequestMapping(params = {"acao=pesquisarConsignacao"})
    public String pesquisarConsignacao(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, @RequestParam(value = "ADE_NUMERO", required = true, defaultValue = "") String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final boolean skipTokenConsultarConsignacao = !TextHelper.isNull(request.getParameterValues("skip_consultar_consignacao"));

        if (!SynchronizerToken.isTokenValid(request) && !responsavel.isSer() && !skipTokenConsultarConsignacao) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        // Verifica se o sistema permite deferimento de contratos pela CSA, e é um usuário de CSA que tenha permissão de deferir ou indeferir e esteja executando esta ação
        final boolean deferimentoPelaCsa = ParamSist.paramEquals(CodedValues.TPC_PERMITE_DEFERIMENTO_TERCEIROS_PELA_CSA, CodedValues.TPC_SIM, responsavel) && responsavel.isCsa() && ((responsavel.temPermissao(CodedValues.FUN_DEF_CONSIGNACAO) && CodedValues.FUN_DEF_CONSIGNACAO.equals(responsavel.getFunCodigo())) || (responsavel.temPermissao(CodedValues.FUN_INDF_CONSIGNACAO) && CodedValues.FUN_INDF_CONSIGNACAO.equals(responsavel.getFunCodigo())));

        final boolean listarTodos = request.getAttribute("listarTodos") != null;
        final boolean pesquisaAvancada = request.getAttribute("pesquisaAvancada") != null;
        final String djuAlteracao = JspHelper.verificaVarQryStr(request, "tipoDecisaoJudicial");

        final String[] adeNumeros = request.getParameterValues("ADE_NUMERO_LIST");

        final boolean adeNumerosVazio = TextHelper.isNull(adeNumeros);

        try {
            if (adeNumeros != null) {
                for (final String adeNum : adeNumeros) {
                    if (!adeNum.matches("^[0-9]+$")) {
                        throw new AutorizacaoControllerException("mensagem.erro.ade.numero.invalido.arg0", responsavel, adeNum);
                    }
                }
            }

            if (responsavel.isSer()) {
                rseCodigo = responsavel.getRseCodigo();
            }
            if (TextHelper.isNull(rseCodigo) && TextHelper.isNull(adeNumero) && ((adeNumeros == null) || (adeNumeros.length == 0)) && !listarTodos && !pesquisaAvancada) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.campo", responsavel));
                return iniciar(request, response, session, model);
            }

            String tipoEntidade = responsavel.getTipoEntidade();
            String codigoEntidade = responsavel.isSer() ? responsavel.getUsuCodigo() : responsavel.getCodigoEntidade();

            if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                tipoEntidade = AcessoSistema.ENTIDADE_EST;
                codigoEntidade = responsavel.getCodigoEntidadePai();
            } else if (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
                tipoEntidade = AcessoSistema.ENTIDADE_CSA;
                codigoEntidade = responsavel.getCodigoEntidadePai();
            } else // Se é consignatária fazendo deferimento ou indeferimento, pesquisa contratos de todas as CSAs
                if (responsavel.isCsa() && deferimentoPelaCsa) {
                    tipoEntidade = AcessoSistema.ENTIDADE_CSE;
                    codigoEntidade = CodedValues.CSE_CODIGO_SISTEMA;
                }

            final boolean exibeComboOperacoes = pesquisaAvancada && responsavel.isCseSupOrg() && (responsavel.temPermissao(CodedValues.FUN_SUSP_CONSIGNACAO) || responsavel.temPermissao(CodedValues.FUN_REAT_CONSIGNACAO));
            if (exibeComboOperacoes) {
                request.setAttribute("exibeComboOperacoes", Boolean.TRUE);
            }

            final List<String> adeNumeroList = new ArrayList<>();
            if (!TextHelper.isNull(adeNumero)) {
                adeNumeroList.add(adeNumero);
            }
            if ((adeNumeros != null) && (adeNumeros.length > 0)) {
                adeNumeroList.addAll(Arrays.asList(adeNumeros));
            }

            final List<String> svcCodigos = definirSvcCodigoPesquisa(request, session, responsavel);
            List<String> sadCodigos = definirSadCodigoPesquisa(request, session, responsavel);
            if ((pesquisaAvancada && ((sadCodigos == null) || sadCodigos.isEmpty())) && (request.getParameter("SAD_CODIGO") != null)) {
                sadCodigos = Arrays.asList(request.getParameterValues("SAD_CODIGO"));
            }

            final String adeIdentificador = JspHelper.verificaVarQryStr(request, "ADE_IDENTIFICADOR");

            final CustomTransferObject criterio = new CustomTransferObject();
            final TransferObject criteriosPesqPadrao = recuperarCriteriosPesquisaPadrao(request, responsavel);
            if (criteriosPesqPadrao != null) {
                criterio.setAtributos(criteriosPesqPadrao.getAtributos());

                // TODO Remover quando as páginas das operações forem refatoradas, de modo a ficar independente do parâmetro tipo
                if (criteriosPesqPadrao.getAttribute("TIPO_OPERACAO") != null) {
                    model.addAttribute("tipoOperacao", criteriosPesqPadrao.getAttribute("TIPO_OPERACAO").toString());
                }

                if (ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_NAO_SELECIONADOS_PARTICIPAM_ALT_MULT_CONTRATOS, responsavel) &&
                        !TextHelper.isNull(criterio.getAttribute("marCodigos"))) {
                    final List<Short> marCodigos = (List<Short>) criterio.getAttribute("marCodigos");
                    model.addAttribute("marCodigos", marCodigos.stream().map(String::valueOf).collect(Collectors.joining(";")));
                }
            }
            if (responsavel.temPermissao(CodedValues.FUN_PESQUISA_AVANCADA_CONSIGNACAO)) {
                final TransferObject criteriosPesqAvancada = recuperarCriteriosPesquisaAvancada(request, responsavel);
                if (criteriosPesqAvancada != null) {
                    criterio.setAtributos(criteriosPesqAvancada.getAtributos());
                }
            }

            if (responsavel.isCsaCor()) {
                final String paramCancelaSolicitacaoServidor = parametroController.getParamCsa(responsavel.getCsaCodigo(), CodedValues.TPA_CANCELA_SOLICITACAO_AO_PESQUISAR_SERVIDOR, responsavel);
                final boolean cancelaSolicitacaoServidor = !TextHelper.isNull(paramCancelaSolicitacaoServidor) && CodedValues.TPA_SIM.equals(paramCancelaSolicitacaoServidor);
                if (cancelaSolicitacaoServidor) {
                    if (!TextHelper.isNull(adeNumeroList) && !adeNumeroList.isEmpty()) {
                        final List<TransferObject> consignacoes = pesquisarConsignacaoController.pesquisaAutorizacao(tipoEntidade, codigoEntidade, rseCodigo, adeNumeroList, TextHelper.objectToStringList(adeIdentificador), sadCodigos, svcCodigos, -1, -1, criterio, responsavel);
                        for (final TransferObject consignacao : consignacoes) {
                            cancelarConsignacaoController.cancelarExpiradasCsa((String) consignacao.getAttribute(Columns.RSE_CODIGO), adeNumero, responsavel);
                        }
                    } else {
                        cancelarConsignacaoController.cancelarExpiradasCsa(rseCodigo, adeNumero, responsavel);
                    }
                }
            }

            final int total = pesquisarConsignacaoController.countPesquisaAutorizacao(tipoEntidade, codigoEntidade, rseCodigo, adeNumeroList, TextHelper.objectToStringList(adeIdentificador), sadCodigos, svcCodigos, criterio, responsavel);
            int totalAtivo = 0;
            int totalInativo = 0;
            int size = JspHelper.LIMITE;
            List<TransferObject> lstConsignacao = null;
            List<TransferObject> lstConsignacaoAtivo = new ArrayList<>();
            List<TransferObject> lstConsignacaoInativo = new ArrayList<>();
            final List<AcaoConsignacao> listaAcoes = definirAcoesListaConsignacao(request, responsavel);

            final boolean exibeInativo = !TextHelper.isNull(request.getParameter("exibeInativo"));
            boolean exibeAtivoInativo = CodedValues.FUN_CONS_CONSIGNACAO.equals(responsavel.getFunCodigo());
            final String rseMatricula = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA");
            final String serCpf = JspHelper.verificaVarQryStr(request, "SER_CPF");

            if (total == 0) {
                final StringBuilder msg = new StringBuilder().append(ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.erro.nenhum.registro", responsavel)).append(":<br>");
                if (pesquisaAvancada) {
                    msg.append(ApplicationResourcesHelper.getMessage("rotulo.pesquisa.avancada", responsavel).toUpperCase());
                } else if (listarTodos) {
                    msg.append(ApplicationResourcesHelper.getMessage("rotulo.pesquisa.listar.todos", responsavel).toUpperCase());
                } else if (!adeNumeroList.isEmpty()) {
                    msg.append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.numero", responsavel)).append(": <span class=\"normal\">").append(TextHelper.join(adeNumeroList, ", ")).append("</span>");
                } else {

                    if (!"".equals(rseMatricula)) {
                        msg.append(ApplicationResourcesHelper.getMessage("rotulo.servidor.matricula", responsavel)).append(": <span class=\"normal\">").append(rseMatricula).append("</span> ");
                    }
                    if (!"".equals(serCpf)) {
                        msg.append(ApplicationResourcesHelper.getMessage("rotulo.servidor.cpf", responsavel)).append(": <span class=\"normal\">").append(serCpf).append("</span>");
                    }
                }

                // Se não é o servidor que está listando suas consignações e não é pesquisa avançada,
                // se não encontrou nada, retorna para a página de pesquisa
                if (!responsavel.isSer() && !pesquisaAvancada && TextHelper.isNull(session.getAttribute(CodedValues.MSG_INFO))) {
                    session.setAttribute(CodedValues.MSG_ERRO, msg.toString());

                    if (!TextHelper.isNull(request.getParameter("linkRetHistoricoFluxo"))) {
                        // TODO : Remover tratamento de link de retorno fixo.
                        final String linkRet = request.getParameter("linkRetHistoricoFluxo").replace('$', '?').replace('(', '=').replace('|', '&');
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

                int offset = !TextHelper.isNull(request.getParameter("offset")) && TextHelper.isNum(request.getParameter("offset")) ? Integer.parseInt(request.getParameter("offset")) : 0;
                offset = -1;
                size = -1;

                lstConsignacao = pesquisarConsignacaoController.pesquisaAutorizacao(tipoEntidade, codigoEntidade, rseCodigo, adeNumeroList, TextHelper.objectToStringList(adeIdentificador), sadCodigos, svcCodigos, offset, size, criterio, responsavel);

                if (exibeAtivoInativo && !pesquisaAvancada && adeNumerosVazio) {
                    if (total == 1) {
                        // Se só tem uma consignação, então não separa em abas
                        exibeAtivoInativo = false;

                    } else {
                        // Se tem mais de uma consignação, verifica se precisa separar em abas

                        // Contratos que estão ativos
                        final List<String> sadCodigosAtivos = new ArrayList<>();
                        sadCodigosAtivos.add(CodedValues.NOT_EQUAL_KEY);
                        sadCodigosAtivos.addAll(CodedValues.SAD_CODIGOS_INATIVOS);
                        totalAtivo = pesquisarConsignacaoController.countPesquisaAutorizacao(tipoEntidade, codigoEntidade, rseCodigo, adeNumeroList, TextHelper.objectToStringList(adeIdentificador), sadCodigosAtivos, svcCodigos, criterio, responsavel);

                        // Contratos que estão inativos
                        final List<String> sadCodigosInativos = CodedValues.SAD_CODIGOS_INATIVOS;
                        totalInativo = pesquisarConsignacaoController.countPesquisaAutorizacao(tipoEntidade, codigoEntidade, rseCodigo, adeNumeroList, TextHelper.objectToStringList(adeIdentificador), sadCodigosInativos, svcCodigos, criterio, responsavel);

                        if ((totalAtivo == 0) || (totalInativo == 0)) {
                            // Se só tem ativos ou só tem inativos, não separa em abas
                            exibeAtivoInativo = false;

                        } else {
                            int offsetAtivo = !TextHelper.isNull(request.getParameter("offsetAtivo")) && TextHelper.isNum(request.getParameter("offsetAtivo")) ? Integer.parseInt(request.getParameter("offsetAtivo")) : 0;
                            int offsetInativo = !TextHelper.isNull(request.getParameter("offsetInativo")) && TextHelper.isNum(request.getParameter("offsetInativo")) ? Integer.parseInt(request.getParameter("offsetInativo")) : 0;
                            offsetAtivo = -1;
                            offsetInativo = -1;

                            if (!exibeInativo) {
                                lstConsignacaoAtivo = pesquisarConsignacaoController.pesquisaAutorizacao(tipoEntidade, codigoEntidade, rseCodigo, adeNumeroList, TextHelper.objectToStringList(adeIdentificador), sadCodigosAtivos, svcCodigos, offsetAtivo, size, criterio, responsavel);
                            } else {
                                lstConsignacaoInativo = pesquisarConsignacaoController.pesquisaAutorizacao(tipoEntidade, codigoEntidade, rseCodigo, adeNumeroList, TextHelper.objectToStringList(adeIdentificador), sadCodigosInativos, svcCodigos, offsetInativo, size, criterio, responsavel);
                            }
                        }
                    }
                }

                if (TextHelper.isNull(rseCodigo) && (lstConsignacao.size() > 0)) {
                    boolean resultadoMultiplosServidores = false;
                    String rseCodigoAtual = "";
                    String rseCodigoAnterior = "";
                    for (final TransferObject ade : lstConsignacao) {
                        rseCodigoAtual = ade.getAttribute(Columns.RSE_CODIGO).toString();
                        if (!TextHelper.isNull(rseCodigoAnterior) && !rseCodigoAtual.equals(rseCodigoAnterior)) {
                            resultadoMultiplosServidores = true;
                            break;
                        }
                        rseCodigoAnterior = rseCodigoAtual;
                    }

                    if (resultadoMultiplosServidores) {
                        // SE possuem múltiplos servidores, seta atributo na request
                        request.setAttribute("resultadoMultiplosServidores", Boolean.TRUE);
                    } else {
                        rseCodigo = rseCodigoAtual;
                    }
                }

                if (((adeNumeroList.size() == 1) || !TextHelper.isNull(rseCodigo)) && (lstConsignacao.size() > 0)) {
                    final CustomTransferObject first = (CustomTransferObject) lstConsignacao.get(0);
                    String tituloResultado = null;
                    if (ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                        tituloResultado = first.getAttribute(Columns.RSE_MATRICULA) + " - " + first.getAttribute(Columns.SER_NOME);
                    } else {
                        tituloResultado = first.getAttribute(Columns.RSE_MATRICULA) + " - " + first.getAttribute(Columns.SER_CPF) + " - " + first.getAttribute(Columns.SER_NOME);
                    }

                    final String footerResultado = ApplicationResourcesHelper.getMessage("rotulo.consignacao.listagem.consignacao.servidor", responsavel, (String) first.getAttribute(Columns.SER_NOME));

                    model.addAttribute("footerResultado", footerResultado);
                    model.addAttribute("tituloResultado", tituloResultado);
                }
            }

            // Valida a senha após a pesquisa, pois caso o RSE_CODIGO não tenha sido passado, será obtido da listagem
            final boolean geraSenhaAutOtp = ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_GERA_OTP_SENHA_AUTORIZACAO, CodedValues.TPC_SIM, responsavel);
            boolean exigeSenhaSer = true;
            try {
                final String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
                if (!TextHelper.isNull(svcCodigo)) {
                    exigeSenhaSer = parametroController.verificaAutorizacaoReservaSemSenha(rseCodigo, svcCodigo, exigeSenhaSer, null, responsavel);
                }
            } catch (final ParametroControllerException e) {
                // Qualquer erro ao buscar essa validação por default a senha sempre é true para garantir a segurança do processo.
            }

            if (exigeSenhaSer && !validarSenhaServidor(rseCodigo, geraSenhaAutOtp, request, session, responsavel)) {
                return iniciar(request, response, session, model);
            }

            // Se não encontrou todas as consignações passadas pelos ADE. Números
            // inclui mensagem de alerta para o usuário
            if (adeNumeroList.size() > lstConsignacao.size()) {
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.alerta.pesquisa.consignacao.nao.encontrada", responsavel));
            }

            ordenarListaConsignacao(lstConsignacao);

            model.addAttribute("lstConsignacao", lstConsignacao);

            /**
             * Caso seja compra de contrato, seta os dados da operação após validar a senha do servidor para permitir que
             * o responsável autorizado visualize os detalhes dos contratos disponíveis para compra.
             */
            if (CodedValues.FUN_COMP_CONTRATO.equals(responsavel.getFunCodigo())) {
                final List<String> adeCodigos = new ArrayList<>();
                if (lstConsignacao != null) {
                    for (final TransferObject ade : lstConsignacao) {
                        adeCodigos.add(ade.getAttribute(Columns.ADE_CODIGO).toString());
                    }
                }
                responsavel.setDadosOperacao(rseCodigo, adeCodigos);
            }

            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("rseMatricula", rseMatricula);
            model.addAttribute("serCpf", serCpf);


            // Monta lista de parâmetros através dos parâmetros de request
            final Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            // Caso de uso de notificar consignação
            boolean existeNotificaCse = false;
            boolean checkAllNotificacao = false;
            int coutCheckMarcados = 0;

            if ((criteriosPesqPadrao != null) && "notificar".equals(criteriosPesqPadrao.getAttribute("TIPO_OPERACAO"))) {
                for (final TransferObject ade : lstConsignacao) {
                    if (ade.getAttribute(Columns.ADE_DATA_NOTIFICACAO_CSE) != null) {
                        existeNotificaCse = true;
                        coutCheckMarcados++;
                    }
                }

                if (coutCheckMarcados == lstConsignacao.size()) {
                    checkAllNotificacao = true;
                }
            }

            model.addAttribute("existeCheckBox", existeNotificaCse);
            model.addAttribute("checkAllCheckBox", checkAllNotificacao);

            // Ignora os parâmetros abaixo
            params.remove("exibeInativo");
            if (!exibeInativo) {
                params.remove("offsetAtivo");
            } else {
                params.remove("offsetInativo");
            }
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

            final List<String> requestParams = new ArrayList<>(params);

            // Monta link de paginação
            String linkListagemAde = request.getRequestURI() + "?acao=pesquisarConsignacao";
            String queryStringRseCodigo = "";
            if (TextHelper.isNull(request.getParameter("RSE_CODIGO")) && !TextHelper.isNull(rseCodigo)) {
                // Necessário caso tenha vindo direto sem passar pela seleção de servidor
                linkListagemAde += "&RSE_CODIGO=" + rseCodigo;
                queryStringRseCodigo = "RSE_CODIGO=" + rseCodigo + "&";
            }

            if (exibeAtivoInativo) {
                String linkListagemAdeInativo = linkListagemAde;
                linkListagemAdeInativo += "&exibeInativo=1";
                configurarPaginador("Ativo", linkListagemAde, "rotulo.paginacao.titulo.bloqueio.pendencia.comunicacao", totalAtivo, size, requestParams, false, request, model);
                configurarPaginador("Inativo", linkListagemAdeInativo, "rotulo.paginacao.titulo.bloqueio.pendencia.comunicacao", totalInativo, size, requestParams, false, request, model);

                ordenarListaConsignacao(lstConsignacaoAtivo);
                ordenarListaConsignacao(lstConsignacaoInativo);

                model.addAttribute("lstConsignacaoAtivo", lstConsignacaoAtivo);
                model.addAttribute("lstConsignacaoInativo", lstConsignacaoInativo);
            }

            configurarPaginador(linkListagemAde, "rotulo.paginacao.titulo.consignacao", total, size, requestParams, false, request, model);

            model.addAttribute("queryString", queryStringRseCodigo + getQueryString(requestParams, request));
            model.addAttribute("exibeInativo", exibeInativo);

            // Define lista de ações
            model.addAttribute("listaAcoes", listaAcoes);

            // Define lista de colunas
            final List<ColunaListaConsignacao> lstColunas = definirColunasListaConsignacao(request, responsavel);
            model.addAttribute("listaColunas", lstColunas);

            // Carrega informações acessórias
            carregarInformacoesAcessorias(rseCodigo, adeNumero, lstConsignacao, request, session, model, responsavel);

            // Formata os valores a serem exibidos
            formatarValoresListaConsignacao(lstConsignacao, lstColunas, request, session, responsavel);

            if (exibeAtivoInativo) {
                // Formata os valores a serem exibidos
                formatarValoresListaConsignacao(lstConsignacaoAtivo, lstColunas, request, session, responsavel);

                // Formata os valores a serem exibidos
                formatarValoresListaConsignacao(lstConsignacaoInativo, lstColunas, request, session, responsavel);
            }

            // Define se deve exibir abas de consignações ativas e inativas
            model.addAttribute("exibeAtivoInativo", exibeAtivoInativo);
            model.addAttribute("pesquisaAvancada", pesquisaAvancada);
            model.addAttribute("adeNumerosVazio", adeNumerosVazio);

            // Define se a coluna de CheckBox sera exibida no carregamento da pagina por padrao ou nao
            final boolean ocultarColunaCheckBox = ocultarColunaCheckBox(responsavel);
            model.addAttribute("ocultarColunaCheckBox", ocultarColunaCheckBox);

            // Define se é alteração de contrato de Decisão Judicial
            model.addAttribute("tipoDecisaoJudicial", djuAlteracao);

            // Redireciona para a página de listagem
            return viewRedirect("jsp/consultarConsignacao/listarConsignacao", request, session, model, responsavel);
        } catch (ParametroControllerException | AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (final NumberFormatException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = {"acao=detalharConsignacao"})
    public String detalharConsignacao(@RequestParam(value = "ADE_CODIGO", required = true, defaultValue = "") String adeCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            final boolean usuarioPodeModificarAde = autorizacaoController.usuarioPodeModificarAde(adeCodigo, false, false, responsavel);
            final boolean usuarioPodeConsultarAde = usuarioPodeModificarAde || autorizacaoController.usuarioPodeConsultarAde(adeCodigo, responsavel);
            final boolean arquivado = responsavel.isCseSup() && "S".equals(JspHelper.verificaVarQryStr(request, "arquivado"));
            final boolean mostraTodoHistorico = responsavel.isSup() && "true".equals(JspHelper.verificaVarQryStr(request, "oculto"));
            final boolean exibeAcoes = !"false".equals(JspHelper.verificaVarQryStr(request, "barraAcoes"));

            // Código da autorização de destino usado em pesquisas de contratos comprados
            final String adeDest = request.getParameter("adeDest");
            final String isOrigem = request.getParameter("isOrigem");

            // Faz a pesquisa da consignação a ser executada. O método buscaAutorizacao
            // irá verificar se o usuário atual pode consultar esta consignação.
            CustomTransferObject autdes = null;

            //DESENV-9287: conferindo se veio de fluxo de compra
            final boolean lstHstParcelasAdeAComprar = request.getAttribute("lstHstParcelasAdeAComprar") != null ? (Boolean) request.getAttribute("lstHstParcelasAdeAComprar") : model.containsAttribute("lstHstParcelasAdeAComprar") ? (Boolean) model.asMap().get("lstHstParcelasAdeAComprar") : false;
            final boolean contratoDestinoCompra = pesquisarConsignacaoController.isDestinoRelacionamento(adeCodigo, CodedValues.TNT_CONTROLE_COMPRA);
            final boolean exibeDataCancelamento = !contratoDestinoCompra && ParamSist.paramEquals(CodedValues.TPC_CANC_AUT_DIARIO_CONSIGNACOES, CodedValues.TPC_SIM, responsavel);

            if (!lstHstParcelasAdeAComprar) {
                try {
                    autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, arquivado, responsavel);
                    autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);

                    if (exibeDataCancelamento) {
                        autdes = verificarExibeDataCancelamento(autdes, responsavel);
                    }
                } catch (final AutorizacaoControllerException ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            } else {
                final ArrayList<String> lstAdeCodigo = new ArrayList<>();
                lstAdeCodigo.add(0, adeCodigo);
                final List<TransferObject> lstAdes = pesquisarConsignacaoController.buscaAutorizacao(lstAdeCodigo, false, responsavel);

                if ((lstAdes == null) || lstAdes.isEmpty()) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.sistema.nenhuma.consignacao.encontrada", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                } else {
                    autdes = (CustomTransferObject) lstAdes.get(0);

                    if (exibeDataCancelamento) {
                        autdes = verificarExibeDataCancelamento(autdes, responsavel);
                    }
                }
            }

            if (autdes != null) {
                if (ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE, responsavel)) {
                    try {
                        final SolicitacaoAutorizacao soa = validarDocumentoController.listUltSolicitacaoValidacao(adeCodigo, responsavel);
                        if ((soa != null) && !TextHelper.isNull(soa.getSsoCodigo())) {
                            final StatusSolicitacao statusSolicitacao = autorizacaoController.findStatusSolicitacao(soa.getSsoCodigo(), responsavel);
                            autdes.setAttribute("statusSolicitacao", statusSolicitacao.getSsoDescricao());
                        }
                    } catch (final ValidarDocumentoControllerException ex) {
                        session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                        LOG.error(ex.getMessage(), ex);
                    }
                }
                model.addAttribute("autdes", autdes);
            }

            CustomTransferObject cde = null;
            if (ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_CONSIGNACAO, CodedValues.TPC_SIM, responsavel)) {
                try {
                    cde = simulacaoController.findCdeByAdeCodigo(adeCodigo, arquivado, responsavel);
                    model.addAttribute("cde", cde);
                } catch (final SimulacaoControllerException ex) {
                    // Não há coeficiente cadastrado.
                }
            }

            // Busca o histórico de ocorrências do contrato
            try {
                List<TransferObject> historico = null;
                if ((adeDest != null) && "1".equals(isOrigem) && !responsavel.isCseSupOrg()) {
                    // Novo método para buscar histórico com informação de contrato terceiro que deu origem
                    // ao contrato atual
                    historico = pesquisarConsignacaoController.hstOrigemTerceiro(adeCodigo, adeDest);
                } else if ("0".equals(isOrigem) && !responsavel.isCseSupOrg()) {
                    // Para contratos de terceiro resultantes da compra do registro atual não deve mostrar
                    // histórico
                    historico = new ArrayList<>();
                } else {
                    // Para usuário consignante e orgão mostrar histórico normalmente.
                    historico = pesquisarConsignacaoController.historicoAutorizacao(adeCodigo, mostraTodoHistorico, arquivado, responsavel);
                }
                model.addAttribute("historico", historico);
            } catch (final AutorizacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }

            final boolean exibeParcelaEmAberto = ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_HISTORICO_PARCELA_ABERTO, responsavel) && !TextHelper.isNull(autdes.getAttribute(Columns.ADE_PRAZO));

            // Busca o histórico de ocorrências das parcelas ou  se for ADE de reserva, busca os lancamentos como historico de parcela
            try {

                List<TransferObject> lancamentosCartao = new ArrayList<>();
                boolean isReserva = false;
                if (NaturezaRelSvc.getInstance().exists(CodedValues.TNT_CARTAO)) {
                    lancamentosCartao = pesquisarConsignacaoController.verificaAdeReservaTrazLancamentos(adeCodigo, responsavel);
                    if (!lancamentosCartao.isEmpty()) {
                        isReserva = (boolean) lancamentosCartao.get(0).getAttribute("isReserva");
                    }
                }
                model.addAttribute("isReserva", isReserva);
                if (isReserva) {
                    model.addAttribute("parcelas", lancamentosCartao);
                } else {
                    List<String> tocCodigos = null;
                    if (!mostraTodoHistorico) {
                        tocCodigos = new ArrayList<>(CodedValues.TOC_CODIGOS_RETORNO_PARCELA);
                        if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_EDITAR_FLUXO_PARCELAS_CONSIGNACAO, CodedValues.TPC_SIM, responsavel)) {
                            tocCodigos.add(CodedValues.TOC_EDICAO_FLUXO_PARCELAS);
                        }
                    }
                    final List<TransferObject> parcelas = parcelaController.getHistoricoParcelas(adeCodigo, null, tocCodigos, arquivado, -1, -1, exibeParcelaEmAberto, responsavel);
                    if (exibeParcelaEmAberto && !TextHelper.isNull(autdes.getAttribute(Columns.ADE_SAD_CODIGO)) && CodedValues.SAD_CODIGOS_ATIVOS.contains(autdes.getAttribute(Columns.ADE_SAD_CODIGO))) {
                        final int pagas = autdes.getAttribute(Columns.ADE_PRD_PAGAS) != null ? (int) autdes.getAttribute(Columns.ADE_PRD_PAGAS) : 0;
                        final int prazo = (int) autdes.getAttribute(Columns.ADE_PRAZO);
                        final int prazoRest = prazo - pagas;
                        int parcelasTotais = prazoRest + parcelas.size();
                        // Necessário tirar das parcelas totais a quantidade de parcelas em aberto e em processamento, pois elas estão embutidas
                        // no prazo restante não as removendo a quantidade fica errado.
                        final List<TransferObject> parcelasPeriodo = parcelaController.findParcelasPeriodo(adeCodigo, responsavel);
                        if ((parcelasPeriodo != null) && !parcelasPeriodo.isEmpty()) {
                            parcelasTotais -= parcelasPeriodo.size();
                        }
                        int adePrazoContratoAberto = 1;
                        final List<Integer> prdNumerosExistentes = new ArrayList<>();
                        final List<Date> prdDatasExistentes = new ArrayList<>();
                        final String orgCodigo = (String) autdes.getAttribute(Columns.ORG_CODIGO);
                        final Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);
                        final Date adeAnoMesFim = (Date) autdes.getAttribute(Columns.ADE_ANO_MES_FIM);
                        final String adePeriodicidade = (String) autdes.getAttribute(Columns.ADE_PERIODICIDADE);
                        final boolean sisPreserva = reimplantarConsignacaoController.sistemaPreservaParcela(adeCodigo, arquivado, responsavel);

                        for (final TransferObject parcela : parcelas) {
                            prdNumerosExistentes.add(Integer.valueOf(parcela.getAttribute(Columns.PRD_NUMERO).toString()));
                            prdDatasExistentes.add((Date) parcela.getAttribute(Columns.PRD_DATA_DESCONTO));
                        }

                        for (int prdNumero = 1; prdNumero <= parcelasTotais; prdNumero++) {
                            if (prdNumerosExistentes.contains(prdNumero)) {
                                continue;
                            }

                            final Date prdDataDesconto = PeriodoHelper.getInstance().calcularAdeAnoMesFim(orgCodigo, periodoAtual, adePrazoContratoAberto, adePeriodicidade, responsavel);
                            if (prdDatasExistentes.contains(prdDataDesconto)) {
                                prdNumero--;
                                adePrazoContratoAberto++;
                                continue;
                            }

                            final TransferObject parcelasEmAberto = new CustomTransferObject();
                            parcelasEmAberto.setAttribute(Columns.PRD_NUMERO, prdNumero);
                            parcelasEmAberto.setAttribute(Columns.PRD_DATA_DESCONTO, prdDataDesconto);
                            parcelasEmAberto.setAttribute(Columns.PRD_VLR_PREVISTO, autdes.getAttribute(Columns.ADE_VLR));
                            parcelasEmAberto.setAttribute(Columns.SPD_DESCRICAO, ApplicationResourcesHelper.getMessage("rotulo.em.aberto", responsavel));
                            parcelasEmAberto.setAttribute(Columns.OCP_DATA, null);
                            parcelasEmAberto.setAttribute(Columns.USU_LOGIN, null);
                            parcelasEmAberto.setAttribute(Columns.OCP_OBS, null);

                            if (!sisPreserva && (prdDataDesconto.compareTo(adeAnoMesFim) > 0)) {
                                break;
                            }

                            parcelas.add(parcelasEmAberto);
                            adePrazoContratoAberto++;
                        }
                    }

                    // Ordena o resultado pelo prd_numero das parcelas
                    Collections.sort(parcelas, (o1, o2) -> {
                        final int p1 = Integer.parseInt(o1.getAttribute(Columns.PRD_NUMERO).toString());
                        final int p2 = Integer.parseInt(o2.getAttribute(Columns.PRD_NUMERO).toString());
                        if (p2 > p1) {
                            return -1;
                        } else {
                            return 1;
                        }
                    });


                    model.addAttribute("parcelas", parcelas);
                }
            } catch (ParcelaControllerException | PeriodoException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }

            // Busca os anexos do contrato
            try {
                final CustomTransferObject cto = new CustomTransferObject();
                cto.setAttribute(Columns.AAD_ADE_CODIGO, adeCodigo);
                cto.setAttribute(Columns.AAD_ATIVO, CodedValues.STS_ATIVO);
                cto.setAttribute("arquivado", arquivado ? "S" : "N");

                // Se usuário só pode consultar o contrato, exibe somente anexos referente à compra de contrato
                if (!responsavel.isCseSupOrg() && !usuarioPodeModificarAde && usuarioPodeConsultarAde) {
                    final List<String> tarCodigos = new ArrayList<>();
                    tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_BOLETO.getCodigo());
                    tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_DSD.getCodigo());
                    tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_DOC_ADICIONAL_COMPRA.getCodigo());
                    tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_COMPROVANTE_PAGAMENTO.getCodigo());
                    cto.setAttribute(Columns.AAD_TAR_CODIGO, tarCodigos);
                }

                final List<TransferObject> anexos = editarAnexoConsignacaoController.lstAnexoAutorizacaoDesconto(cto, -1, -1, responsavel);
                model.addAttribute("anexos", anexos);
            } catch (final AutorizacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }

            // Busca as propostas de pagamento
            try {
                if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel)) {
                    final List<TransferObject> propostas = financiamentoDividaController.lstPropostaPagamentoDivida(adeCodigo, responsavel.getCsaCodigo(), null, arquivado, responsavel);
                    model.addAttribute("propostas", propostas);
                }
            } catch (final FinanciamentoDividaControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }

            try {
                if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                    final List<TransferObject> propostasLeilao = leilaoSolicitacaoController.lstPropostaLeilaoSolicitacao(adeCodigo, null, null, arquivado, responsavel);
                    model.addAttribute("propostasLeilao", propostasLeilao);
                }
            } catch (final LeilaoSolicitacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }

            //Busca histórico solicitação autorização (histórico anexos)
            if (ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE, responsavel)) {
                try {
                    final List<TransferObject> historicoSolicitacaoAutorizacao = validarDocumentoController.lstSolicitacaoAutorizacaoValidarDocumentos(adeCodigo, responsavel);
                    model.addAttribute("historicoSolicitacaoAutorizacao", historicoSolicitacaoAutorizacao);
                } catch (final ValidarDocumentoControllerException ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    LOG.error(ex.getMessage(), ex);
                }
            } else if (ParamSist.getBoolParamSist(CodedValues.TPC_ASSINATURA_DIGITAL_CONSIGNACAO_SOMENTE_CERT_DIGITAL, responsavel)) {
                try {
                    final List<String> tisCodigos = new ArrayList<>();
                    tisCodigos.add(TipoSolicitacaoEnum.SOLICITACAO_CONSIGNACAO_CREDITO_ELETRONICO.getCodigo());
                    final List<String> ssoCodigos = new ArrayList<>();
                    ssoCodigos.add(StatusSolicitacaoEnum.PENDENTE_VALIDACAO_DOCUMENTOS.getCodigo());
                    ssoCodigos.add(StatusSolicitacaoEnum.PENDENTE_ASSINATURA_DOCUMENTACAO.getCodigo());
                    ssoCodigos.add(StatusSolicitacaoEnum.PENDENTE_INFORMACAO_DOCUMENTACAO.getCodigo());
                    ssoCodigos.add(StatusSolicitacaoEnum.DOCUMENTACAO_ENVIADA_PARA_ASSINATURA.getCodigo());
                    ssoCodigos.add(StatusSolicitacaoEnum.DOCUMENTACAO_ASSINADA_DIGITALMENTE.getCodigo());

                    final List<TransferObject> historicoSolicitacaoAutorizacao = simulacaoController.lstRegistrosSolicitacao(adeCodigo, tisCodigos, ssoCodigos, responsavel);
                    model.addAttribute("historicoSolicitacaoAutorizacao", historicoSolicitacaoAutorizacao);
                } catch (final SimulacaoControllerException ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    LOG.error(ex.getMessage(), ex);
                }
            }

            try {
                final CustomTransferObject cmnTO = new CustomTransferObject();
                cmnTO.setAttribute(Columns.CMN_ADE_CODIGO, adeCodigo);
                final List<TransferObject> comunicacoes = comunicacaoController.listComunicacoes(cmnTO, true, -1, -1, responsavel);
                model.addAttribute("comunicacoes", comunicacoes);

                for (final TransferObject comunicacao : comunicacoes) {
                    final Boolean cmnPendencia = (Boolean) comunicacao.getAttribute(Columns.CMN_PENDENCIA);
                    final String cmnCodigo = (String) comunicacao.getAttribute(Columns.CMN_CODIGO);

                    if (cmnPendencia) {
                        model.addAttribute("codigoComunicacao", cmnCodigo);
                        break;
                    }
                }
            } catch (final ZetraException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            ParamSvcTO paramSvcCse = null;

            try {
                final String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();
                paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
                model.addAttribute("paramSvcCse", paramSvcCse);

                final String msgInfBancarias = verificarInformacoesBancarias(autdes, paramSvcCse, responsavel);
                if (msgInfBancarias != null) {
                    model.addAttribute("msgInfBancarias", msgInfBancarias);
                }
            } catch (final ParametroControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            boolean informouMatriculaCpf = false;
            final String rseMatricula = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA");
            final String serCpf = JspHelper.verificaVarQryStr(request, "SER_CPF");
            if ((!TextHelper.isNull(rseMatricula) && !TextHelper.isNull(serCpf)) && (autdes.getAttribute(Columns.SER_CPF).toString().substring(0, Math.min(serCpf.length(), autdes.getAttribute(Columns.SER_CPF).toString().length())).equals(serCpf) && autdes.getAttribute(Columns.RSE_MATRICULA).toString().substring(0, Math.min(rseMatricula.length(), autdes.getAttribute(Columns.RSE_MATRICULA).toString().length())).equals(rseMatricula))) {
                informouMatriculaCpf = true;
            }
            final boolean infMatCpfrEdtAde = ParamSist.paramEquals(CodedValues.TPC_INF_MAT_CPF_EDT_CONSIGNACAO, CodedValues.TPC_SIM, responsavel);
            if (!infMatCpfrEdtAde || responsavel.isSer()) {
                informouMatriculaCpf = true;
            }

            final List<AcaoConsignacao> acoes = definirAcoesDetalheConsignacao(autdes, cde, paramSvcCse, arquivado, usuarioPodeModificarAde, informouMatriculaCpf, request, session, responsavel);
            model.addAttribute("listaAcoes", acoes);

            // Determina se será exibido a barra de ações: usuário deve ter permissão de modificar o contrato
            model.addAttribute("usuarioPodeModificarAde", exibeAcoes && usuarioPodeModificarAde);
            model.addAttribute("usuarioPodeConsultarAde", usuarioPodeConsultarAde);
            model.addAttribute("arquivado", arquivado);

            if (ParamSist.paramEquals(CodedValues.TPC_REQUER_TEL_SER_SOLIC_SALDO_DEVEDOR, CodedValues.TPC_SIM, responsavel)) {
                final String sdvTelefone = autorizacaoController.getValorDadoAutDesconto(adeCodigo, CodedValues.TDA_SDV_TEL_SERVIDOR, arquivado, responsavel);
                model.addAttribute("sdvTelefone", sdvTelefone);
            }

            final ParamSession paramSession = ParamSession.getParamSession(session);
            final String srtFiltroTable = "filtroTable";
            final String filtroTable = JspHelper.verificaVarQryStr(request, srtFiltroTable);
            if (!TextHelper.isNull(filtroTable) && !paramSession.getLastHistory().contains(srtFiltroTable)) {
                model.addAttribute("voltar", paramSession.getLastHistory() + "&filtroTable=" + filtroTable);
            }

            // Redireciona para a página de listagem
            return viewRedirect("jsp/consultarConsignacao/detalharConsignacao", request, session, model, responsavel);

        } catch (final AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = {"acao=emitirBoleto"})
    public String emitirBoleto(@RequestParam(value = "ADE_CODIGO", required = true, defaultValue = "") String adeCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        // Busca as informações na ADE para (re)impressão do boleto.
        if (TextHelper.isNull(adeCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            CustomTransferObject cto = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
            cto = TransferObjectHelper.mascararUsuarioHistorico(cto, null, responsavel);

            final String svcCodigo = cto.getAttribute(Columns.SVC_CODIGO).toString();
            final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

            if (paramSvcCse.isTpsBuscaBoletoExterno()) {
                // Repassa o token salvo, pois o método irá revalidar o token
                request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
                return emitirBoletoExterno(adeCodigo, request, response, session, model);
            }

            String csaNome = cto.getAttribute(Columns.CSA_NOME).toString();
            final String csaNomeAbrev = (String) cto.getAttribute(Columns.CSA_NOME_ABREV);
            if ((csaNomeAbrev != null) && (csaNomeAbrev.trim().length() > 0)) {
                csaNome = csaNomeAbrev + " - " + csaNome;
            }
            final String svcDescricao = cto.getAttribute(Columns.SVC_DESCRICAO).toString();
            final String codVerba = cto.getAttribute(Columns.CNV_COD_VERBA) != null ? cto.getAttribute(Columns.CNV_COD_VERBA).toString() : "";

            final BigDecimal adeVlr = cto.getAttribute(Columns.ADE_VLR) != null ? (BigDecimal) cto.getAttribute(Columns.ADE_VLR) : BigDecimal.ZERO;
            final String adePrazo = cto.getAttribute(Columns.ADE_PRAZO) != null ? cto.getAttribute(Columns.ADE_PRAZO).toString() : "";
            final String adeNumero = cto.getAttribute(Columns.ADE_NUMERO).toString();
            final String adeResponsavel = cto.getAttribute(Columns.USU_LOGIN).toString();
            final Date dataSimulacao = (Date) cto.getAttribute(Columns.ADE_DATA);
            final String dataIni = DateHelper.toPeriodString((java.util.Date) cto.getAttribute(Columns.ADE_ANO_MES_INI));
            final String dataFim = DateHelper.toPeriodString((java.util.Date) cto.getAttribute(Columns.ADE_ANO_MES_FIM));

            final CustomTransferObject cde = simulacaoController.findCdeByAdeCodigo(adeCodigo, responsavel);
            final BigDecimal vlrLiberado = cde.getAttribute(Columns.CDE_VLR_LIBERADO) != null ? (BigDecimal) cde.getAttribute(Columns.CDE_VLR_LIBERADO) : BigDecimal.ZERO;
            final String ranking = cde.getAttribute(Columns.CDE_RANKING) != null ? cde.getAttribute(Columns.CDE_RANKING).toString() : "";

            final int numDias = (paramSvcCse.getTpsDiasDesblSolicitacaoNaoConf() != null) && !"".equals(paramSvcCse.getTpsDiasDesblSolicitacaoNaoConf()) ? Integer.parseInt(paramSvcCse.getTpsDiasDesblSolicitacaoNaoConf()) : 2;
            final Date dataValidade = DateHelper.addDays(dataSimulacao, numDias - 1);

            final String rseCodigo = cto.getAttribute(Columns.RSE_CODIGO).toString();
            final CustomTransferObject servidorTO = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
            final String rseMatricula = servidorTO.getAttribute(Columns.RSE_MATRICULA).toString();
            final String rseTipo = servidorTO.getAttribute(Columns.RSE_TIPO) != null ? servidorTO.getAttribute(Columns.RSE_TIPO).toString() : "";
            final String rseDataAdmissao = servidorTO.getAttribute(Columns.RSE_DATA_ADMISSAO) != null ? DateHelper.toDateString((Date) servidorTO.getAttribute(Columns.RSE_DATA_ADMISSAO)) : "";

            final String boleto = CodedNames.TEMPLATE_BOLETO_AUT_DESCONTO;
            String absolutePath = ParamSist.getDiretorioRaizArquivos();
            absolutePath += File.separatorChar + "boleto" + File.separatorChar + svcCodigo + File.separatorChar + boleto;

            File arqBoleto = new File(absolutePath);
            if (!arqBoleto.exists()) {
                absolutePath = ParamSist.getDiretorioRaizArquivos();
                absolutePath += File.separatorChar + "boleto" + File.separatorChar + boleto;
                arqBoleto = new File(absolutePath);
            }

            final String msgBoleto = FileHelper.readAll(absolutePath).replace("<SERVICO>", svcDescricao.toUpperCase()).replace("<CONSIGNATARIA>", csaNome.toUpperCase());
            model.addAttribute("msgBoleto", msgBoleto);

            final String serCodigo = servidorTO.getAttribute(Columns.SER_CODIGO).toString();
            final ServidorTransferObject servidor = servidorController.findServidor(serCodigo, responsavel);
            model.addAttribute("servidor", servidor);

            String serEstCivil = servidorController.getEstCivil(servidor.getSerEstCivil(), responsavel);
            serEstCivil = !TextHelper.isNull(serEstCivil) ? serEstCivil : "";

            final String orgCodigo = servidorTO.getAttribute(Columns.ORG_CODIGO).toString();
            final OrgaoTransferObject orgao = consignanteController.findOrgao(orgCodigo, responsavel);
            model.addAttribute("orgao", orgao);

            if (paramSvcCse.isTpsExigeCodAutorizacaoSolic()) {
                model.addAttribute("exigeCodAutSolicitacao", Boolean.TRUE);

                final String codigoAutorizacaoSolic = autorizacaoController.getValorDadoAutDesconto(adeCodigo, CodedValues.TDA_CODIGO_AUTORIZACAO_SOLICITACAO, responsavel);
                model.addAttribute("codigoAutorizacaoSolic", codigoAutorizacaoSolic);
            }

            final TransferObject dadosConsignacao = new CustomTransferObject();
            dadosConsignacao.setAttribute("rseMatricula", rseMatricula);
            dadosConsignacao.setAttribute("rseTipo", rseTipo);
            dadosConsignacao.setAttribute("rseDataAdmissao", rseDataAdmissao);
            dadosConsignacao.setAttribute("serEstCivil", serEstCivil);
            dadosConsignacao.setAttribute("csaNome", csaNome);
            dadosConsignacao.setAttribute("adeResponsavel", adeResponsavel);
            dadosConsignacao.setAttribute("codVerba", codVerba);
            dadosConsignacao.setAttribute("svcDescricao", svcDescricao.toUpperCase());
            dadosConsignacao.setAttribute("adeNumero", adeNumero);
            dadosConsignacao.setAttribute("ranking", ranking);
            dadosConsignacao.setAttribute("adeVlrLiberado", NumberHelper.format(vlrLiberado.doubleValue(), NumberHelper.getLang(), true));
            dadosConsignacao.setAttribute("adeVlr", NumberHelper.format(adeVlr.doubleValue(), NumberHelper.getLang(), true));
            dadosConsignacao.setAttribute("adePrazo", adePrazo);
            dadosConsignacao.setAttribute("dataIni", dataIni);
            dadosConsignacao.setAttribute("dataFim", dataFim);
            dadosConsignacao.setAttribute("dataValidade", DateHelper.toDateString(dataValidade));
            dadosConsignacao.setAttribute("dataSimulacao", DateHelper.toDateTimeString(dataSimulacao));
            model.addAttribute("dadosConsignacao", dadosConsignacao);

            return viewRedirect("jsp/consultarConsignacao/emitirBoleto", request, session, model, responsavel);

        } catch (final NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = {"acao=emitirBoletoExterno"})
    public String emitirBoletoExterno(@RequestParam(value = "ADE_CODIGO", required = true, defaultValue = "") String adeCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        if (TextHelper.isNull(adeCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            CustomTransferObject cto = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
            cto = TransferObjectHelper.mascararUsuarioHistorico(cto, null, responsavel);

            if (ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_MEXICANO, responsavel)) {
                cto.setAttribute("ade_cat", JspHelper.verificaVarQryStr(request, "ADE_VLR_CAT"));
                cto.setAttribute("ade_iva", JspHelper.verificaVarQryStr(request, "ADE_VLR_IVA"));
            }

            final String svcCodigo = cto.getAttribute(Columns.SVC_CODIGO).toString();
            final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
            final boolean boletoExterno = paramSvcCse.isTpsBuscaBoletoExterno();

            if (boletoExterno) {
                File arqBoleto = null;
                final String sadCodigo = (String) cto.getAttribute(Columns.ADE_SAD_CODIGO);
                String absolutePath = null;
                if (!CodedValues.SAD_SOLICITADO.equals(sadCodigo)) {
                    final String boleto = CodedNames.TEMPLATE_EXTRATO_AUT_DESCONTO;
                    absolutePath = ParamSist.getDiretorioRaizArquivos();
                    absolutePath += File.separatorChar + "boleto" + File.separatorChar + svcCodigo + File.separatorChar + boleto;
                    arqBoleto = new File(absolutePath);
                    if (!arqBoleto.exists()) {
                        absolutePath = ParamSist.getDiretorioRaizArquivos();
                        absolutePath += File.separatorChar + "boleto" + File.separatorChar + boleto;
                        arqBoleto = new File(absolutePath);
                        if (!arqBoleto.exists()) {
                            arqBoleto = null;
                        }
                    }
                }
                if (arqBoleto == null) {
                    final String boleto = CodedNames.TEMPLATE_BOLETO_AUT_DESCONTO;

                    absolutePath = ParamSist.getDiretorioRaizArquivos();
                    absolutePath += File.separatorChar + "boleto" + File.separatorChar + svcCodigo + File.separatorChar + boleto;

                    arqBoleto = new File(absolutePath);
                    if (!arqBoleto.exists()) {
                        absolutePath = ParamSist.getDiretorioRaizArquivos();
                        absolutePath += File.separatorChar + "boleto" + File.separatorChar + boleto;
                        arqBoleto = new File(absolutePath);
                        if (!arqBoleto.exists()) {
                            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.boleto.nao.encontrado", responsavel, absolutePath));
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.boleto.nao.encontrado", responsavel));
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        }
                    }
                }

                // Busca os demais dados necessários para o boleto, que não estão na query principal.
                final String orgCodigo = cto.getAttribute(Columns.ORG_CODIGO).toString();
                final OrgaoTransferObject orgao = consignanteController.findOrgao(orgCodigo, responsavel);
                cto.setAtributos(orgao.getAtributos());

                // Definição da origem do contrato
                final List<String> adeList = new ArrayList<>();
                adeList.add(adeCodigo);
                final List<TransferObject> relList = transferirConsignacaoController.pesquisarConsignacaoRelacionamento(adeList, responsavel);

                if (!relList.isEmpty()) {
                    for (final TransferObject relaciomento : relList) {
                        final String adeDestino = (String) relaciomento.getAttribute(Columns.RAD_ADE_CODIGO_DESTINO);
                        if (!TextHelper.isNull(adeDestino) && adeDestino.equals(adeCodigo)) {
                            final String tntCodigo = (String) relaciomento.getAttribute(Columns.RAD_TNT_CODIGO);

                            if (CodedValues.TNT_CONTROLE_RENEGOCIACAO.equals(tntCodigo)) {
                                cto.setAttribute("origem_ade", ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.renegociacao", responsavel));
                            } else if (CodedValues.TNT_CONTROLE_COMPRA.equals(tntCodigo)) {
                                cto.setAttribute("origem_ade", ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.compra", responsavel));
                            }

                            break;
                        }
                    }

                    if (cto.getAttribute("origem_ade") == null) {
                        cto.setAttribute("origem_ade", ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.novo.contrato", responsavel));
                    }
                } else {
                    cto.setAttribute("origem_ade", ApplicationResourcesHelper.getMessage("rotulo.consignacao.origem.novo.contrato", responsavel));
                }

                final String msgTemplate = FileHelper.readAll(absolutePath);

                final String msgBoleto = BoletoHelper.gerarTextoBoleto(msgTemplate, cto, responsavel);
                model.addAttribute("msgBoleto", msgBoleto);

                if (paramSvcCse.isTpsExigeCodAutorizacaoSolic()) {
                    model.addAttribute("exigeCodAutSolicitacao", Boolean.TRUE);

                    final String codigoAutorizacaoSolic = autorizacaoController.getValorDadoAutDesconto(adeCodigo, CodedValues.TDA_CODIGO_AUTORIZACAO_SOLICITACAO, responsavel);
                    model.addAttribute("codigoAutorizacaoSolic", codigoAutorizacaoSolic);
                }

                return viewRedirect("jsp/consultarConsignacao/emitirBoletoExterno", request, session, model, responsavel);

            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.boleto.nao.permitido", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = {"acao=redirecionarBoletoSdv"})
    public String redirecionarBoletoSdv(@RequestParam(value = "ADE_CODIGO", required = true, defaultValue = "") String adeCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            final SaldoDevedorTransferObject sdvTO = saldoDevedorController.getSaldoDevedor(adeCodigo, responsavel);
            final String linkBoleto = (String) sdvTO.getAttribute(Columns.SDV_LINK_BOLETO_QUITACAO);
            if (!TextHelper.isNull(linkBoleto)) {
                request.setAttribute("url64", TextHelper.encode64(linkBoleto));
                return "jsp/redirecionador/redirecionar";
            }
        } catch (final SaldoDevedorControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
    }

    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        return null;
    }

    protected List<String> definirSvcCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        return null;
    }

    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        return null;
    }

    protected TransferObject recuperarCriteriosPesquisaAvancada(HttpServletRequest request, AcessoSistema responsavel) {
        return null;
    }

    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        return null;
    }

    protected boolean ocultarColunaCheckBox(AcessoSistema responsavel) {
        return true;
    }

    protected List<ColunaListaConsignacao> definirColunasListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        final boolean resultadoMultiplosServidores = request.getAttribute("resultadoMultiplosServidores") != null;

        final List<ColunaListaConsignacao> colunas = new ArrayList<>();

        try {
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_CONSIGNATARIA, responsavel) && !responsavel.isCsaCor()) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_CONSIGNATARIA, ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel)));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_RESPONSAVEL, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_RESPONSAVEL, ApplicationResourcesHelper.getMessage("rotulo.consignacao.responsavel", responsavel)));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_NUMERO, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_NUMERO, ApplicationResourcesHelper.getMessage("rotulo.consignacao.numero", responsavel), ColunaListaConsignacao.TipoValor.NUMERICO));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_IDENTIFICADOR, responsavel) && (responsavel.isCseSup() || responsavel.isCsaCor())) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.consignacao.identificador", responsavel)));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_SERVICO, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_SERVICO, ApplicationResourcesHelper.getMessage("rotulo.servico.singular", responsavel)));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_SERVIDOR, responsavel) && resultadoMultiplosServidores) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_SERVIDOR, ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel)));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_DATA_RESERVA, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_DATA_RESERVA, ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.inclusao", responsavel), ColunaListaConsignacao.TipoValor.DATA));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA, ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela.abreviado", responsavel), ColunaListaConsignacao.TipoValor.MONETARIO));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_PRAZO, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_PRAZO, ApplicationResourcesHelper.getMessage("rotulo.consignacao.prazo.abreviado", responsavel), ColunaListaConsignacao.TipoValor.NUMERICO));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_PARCELAS_PAGAS, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_PARCELAS_PAGAS, ApplicationResourcesHelper.getMessage("rotulo.consignacao.pagas", responsavel), ColunaListaConsignacao.TipoValor.NUMERICO));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_STATUS, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_STATUS, ApplicationResourcesHelper.getMessage("rotulo.consignacao.status", responsavel)));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_DATA_NOTIFICACAO, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_DATA_NOTIFICACAO, ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.notificacao", responsavel)));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_DATA_VALOR_LIBERADO, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_DATA_VALOR_LIBERADO, ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.valor.liberado", responsavel)));
            }
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return colunas;
    }

    protected List<TransferObject> formatarValoresListaConsignacao(List<TransferObject> lstConsignacao, List<ColunaListaConsignacao> colunas, HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        for (TransferObject ade : lstConsignacao) {
            ade = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) ade, null, responsavel);

            for (final ColunaListaConsignacao coluna : colunas) {
                final String chaveCampo = coluna.getChaveCampo();
                String valorCampo = "";

                if (FieldKeysConstants.LISTA_CONSIGNACAO_CONSIGNATARIA.equals(chaveCampo)) {
                    valorCampo = ade.getAttribute(Columns.CSA_IDENTIFICADOR) + " - " + (!TextHelper.isNull(ade.getAttribute(Columns.CSA_NOME_ABREV)) ? ade.getAttribute(Columns.CSA_NOME_ABREV).toString() : ade.getAttribute(Columns.CSA_NOME).toString());
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_RESPONSAVEL.equals(chaveCampo)) {
                    valorCampo = ade.getAttribute(Columns.USU_LOGIN) != null ? ade.getAttribute(Columns.USU_LOGIN).toString() : "";
                    valorCampo = valorCampo.equalsIgnoreCase((String) ade.getAttribute(Columns.USU_CODIGO)) && (ade.getAttribute(Columns.USU_TIPO_BLOQ) != null) ? ade.getAttribute(Columns.USU_TIPO_BLOQ).toString() + "(*)" : valorCampo;
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_NUMERO.equals(chaveCampo)) {
                    valorCampo = ade.getAttribute(Columns.ADE_NUMERO).toString();

                    if (ade.getAttribute(Columns.USU_CODIGO).equals(CodedValues.USU_CODIGO_SISTEMA)) {
                        ade.setAttribute("IMPORTADO", "S");
                    }

                    if (!TextHelper.isNull(ade.getAttribute(Columns.SAD_CODIGO)) && (ade.getAttribute(Columns.SAD_CODIGO).equals(CodedValues.SAD_SUSPENSA) || ade.getAttribute(Columns.SAD_CODIGO).equals(CodedValues.SAD_SUSPENSA_CSE))) {
                        ade.setAttribute("SUSPENSO", "S");
                    }

                    if (pesquisarConsignacaoController.findByOrigemOuDestino(false, ade.getAttribute(Columns.ADE_CODIGO).toString(), CodedValues.TNT_CONTROLE_RENEGOCIACAO, responsavel)) {
                        ade.setAttribute("REFINANCIADO", "S");
                    }

                    if (pesquisarConsignacaoController.findByOrigemOuDestino(false, ade.getAttribute(Columns.ADE_CODIGO).toString(), CodedValues.TNT_CONTROLE_COMPRA, responsavel)) {
                        ade.setAttribute("PORTABILIDADE", "S");
                    }

                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_IDENTIFICADOR.equals(chaveCampo)) {
                    if (responsavel.isCseSup() || (responsavel.isCsaCor() && responsavel.getCsaCodigo().equals(ade.getAttribute(Columns.CSA_CODIGO)))) {
                        valorCampo = ade.getAttribute(Columns.ADE_IDENTIFICADOR) != null ? ade.getAttribute(Columns.ADE_IDENTIFICADOR).toString() : "";
                    } else {
                        valorCampo = "";
                    }
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_SERVICO.equals(chaveCampo)) {
                    final String adeCodReg = (ade.getAttribute(Columns.ADE_COD_REG) != null) && !"".equals(ade.getAttribute(Columns.ADE_COD_REG)) ? ade.getAttribute(Columns.ADE_COD_REG).toString() : CodedValues.COD_REG_DESCONTO;

                    valorCampo = (!TextHelper.isNull(ade.getAttribute(Columns.CNV_COD_VERBA)) ? ade.getAttribute(Columns.CNV_COD_VERBA).toString() : ade.getAttribute(Columns.SVC_IDENTIFICADOR).toString()) + (!TextHelper.isNull(ade.getAttribute(Columns.ADE_INDICE)) ? ade.getAttribute(Columns.ADE_INDICE).toString() : "") + " - " + ade.getAttribute(Columns.SVC_DESCRICAO).toString() + (CodedValues.COD_REG_ESTORNO.equals(adeCodReg) ? " - " + ApplicationResourcesHelper.getMessage("rotulo.consignacao.cod.reg.credito", responsavel) : "");
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_SERVIDOR.equals(chaveCampo)) {
                    valorCampo = ade.getAttribute(Columns.RSE_MATRICULA) + " - " + ade.getAttribute(Columns.SER_CPF) + " - " + ade.getAttribute(Columns.SER_NOME);
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_CPF.equals(chaveCampo)) {
                    valorCampo = ade.getAttribute(Columns.SER_CPF).toString();
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_TELEFONE.equals(chaveCampo)) {
                    valorCampo = ade.getAttribute(Columns.SER_TEL) != null ? ade.getAttribute(Columns.SER_TEL).toString() : "";
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_DATA_RESERVA.equals(chaveCampo)) {
                    try {
                        valorCampo = DateHelper.reformat(ade.getAttribute(Columns.ADE_DATA).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
                    } catch (final ParseException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA.equals(chaveCampo)) {
                    try {
                        valorCampo = !TextHelper.isNull(ade.getAttribute(Columns.ADE_VLR)) ? NumberHelper.reformat(ade.getAttribute(Columns.ADE_VLR).toString(), "en", NumberHelper.getLang()) : "";
                        ade.setAttribute(chaveCampo + "_SIMBOLO", ParamSvcTO.getDescricaoTpsTipoVlr((String) ade.getAttribute(Columns.ADE_TIPO_VLR)));
                    } catch (final ParseException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_FOLHA.equals(chaveCampo)) {
                    try {
                        valorCampo = !TextHelper.isNull(ade.getAttribute(Columns.ADE_VLR_FOLHA)) ? NumberHelper.reformat(ade.getAttribute(Columns.ADE_VLR_FOLHA).toString(), "en", NumberHelper.getLang()) : "";
                    } catch (final ParseException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_RENEGOCIADO.equals(chaveCampo) && (ade.getAttribute("SOMA_VLR_RENEGOCIADO") != null)) {
                    valorCampo = NumberHelper.format(Double.parseDouble(ade.getAttribute("SOMA_VLR_RENEGOCIADO").toString()), NumberHelper.getLang());
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_PRAZO.equals(chaveCampo)) {
                    if (!TextHelper.isNull(ade.getAttribute(Columns.ADE_PRAZO))) {
                        valorCampo = ade.getAttribute(Columns.ADE_PRAZO).toString();
                    } else {
                        valorCampo = ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel);
                        ade.setAttribute(chaveCampo + "_ABREVIATURA", ApplicationResourcesHelper.getMessage("rotulo.indeterminado.abreviado", responsavel));
                    }
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_PARCELAS_PAGAS.equals(chaveCampo)) {
                    valorCampo = ade.getAttribute(Columns.ADE_PRD_PAGAS) != null ? ade.getAttribute(Columns.ADE_PRD_PAGAS).toString() : "0";
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_CAPITAL_DEVIDO.equals(chaveCampo) && ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPITAL_DEVIDO, CodedValues.TPC_SIM, responsavel)) {
                    // Se prazo e valor não são nulos, e o tipo da consignação é de valor Fixo ou de Total de Margem, calcula o capital devido
                    if ((ade.getAttribute(Columns.ADE_PRAZO) != null) && (ade.getAttribute(Columns.ADE_VLR) != null) && (CodedValues.TIPO_VLR_FIXO.equals(ade.getAttribute(Columns.ADE_TIPO_VLR)) || CodedValues.TIPO_VLR_TOTAL_MARGEM.equals(ade.getAttribute(Columns.ADE_TIPO_VLR)))) {
                        final int prazoRestante = (Integer) ade.getAttribute(Columns.ADE_PRAZO) - (ade.getAttribute(Columns.ADE_PRD_PAGAS) != null ? (Integer) ade.getAttribute(Columns.ADE_PRD_PAGAS) : 0);
                        if (prazoRestante >= 0) {
                            final double valorParcela = ((java.math.BigDecimal) (ade.getAttribute(Columns.ADE_VLR_PARCELA_FOLHA) != null ? ade.getAttribute(Columns.ADE_VLR_PARCELA_FOLHA) : ade.getAttribute(Columns.ADE_VLR))).doubleValue();
                            final double valorCapitalDevido = valorParcela * prazoRestante;
                            valorCampo = NumberHelper.format(valorCapitalDevido, NumberHelper.getLang());
                            if (ade.getAttribute(Columns.ADE_VLR_PARCELA_FOLHA) != null) {
                                valorCampo += " (*)";
                            }
                        }
                    }
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_STATUS.equals(chaveCampo)) {
                    valorCampo = ade.getAttribute(Columns.SAD_DESCRICAO).toString();
                    if ((ade.getAttribute(Columns.ADE_DATA_STATUS) != null) && !ParamSist.paramEquals(CodedValues.TPC_EXIBE_ADE_DATA_STATUS_CONSULTAR_CONSIGNACAO, CodedValues.TPC_NAO, responsavel)) {
                        final String dataAtualizacao = DateHelper.toDateTimeString((Date) ade.getAttribute(Columns.ADE_DATA_STATUS));
                        valorCampo = String.format("%s (%s)", valorCampo, dataAtualizacao);
                    }
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_RISCO_CSA.equals(chaveCampo)) {
                    String arrRisco = "";
                    arrRisco = ade.getAttribute(Columns.ARR_RISCO) != null ? ade.getAttribute(Columns.ARR_RISCO).toString() : "";
                    valorCampo = RiscoRegistroServidorEnum.recuperaDescricaoRisco(arrRisco, responsavel);
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_DATA_NOTIFICACAO.equals(chaveCampo)) {
                    try {
                        valorCampo = ade.getAttribute(Columns.ADE_DATA_NOTIFICACAO_CSE) != null ? DateHelper.reformat(ade.getAttribute(Columns.ADE_DATA_NOTIFICACAO_CSE).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern()) : "";
                    } catch (final ParseException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_DATA_VALOR_LIBERADO.equals(chaveCampo)) {
                    try {
                        valorCampo = ade.getAttribute(Columns.ADE_DATA_LIBERACAO_VALOR) != null ? DateHelper.reformat(ade.getAttribute(Columns.ADE_DATA_LIBERACAO_VALOR).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern()) : "";
                    } catch (final ParseException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }

                if (FieldKeysConstants.LISTA_CONSIGNACAO_PRIORIDADE_DESCONTO.equals(chaveCampo)) {
                    valorCampo = ade.getAttribute("PRIORIDADE") != null ? String.valueOf(ade.getAttribute("PRIORIDADE")) : "";
                }

                if (MotivoAdeNaoRenegociavelEnum.CHAVE_MOTIVO_INDISPONIBILIDADE.equals(chaveCampo)) {
                    final MotivoAdeNaoRenegociavelEnum motivo = (MotivoAdeNaoRenegociavelEnum) ade.getAttribute(MotivoAdeNaoRenegociavelEnum.CHAVE_MOTIVO_INDISPONIBILIDADE);
                    if (motivo != null) {
                        valorCampo = motivo.getDescricao(responsavel);
                    }
                }

                ade.setAttribute(chaveCampo, valorCampo);
            }

            if (exibirMensagemAdicionalOperacao(ade, session, responsavel)) {
                ade.setAttribute("EXIBIR_MENSAGEM_ADICIONAL", Boolean.TRUE);
            }
            if (usarLinkAdicionalOperacao(ade, session, responsavel)) {
                ade.setAttribute("USAR_LINK_ADICIONAL", Boolean.TRUE);
            }
        }
        return lstConsignacao;
    }

    private boolean exibirMensagemAdicionalOperacao(TransferObject autdes, HttpSession session, AcessoSistema responsavel) {
        try {
            final String adeCodigo = autdes.getAttribute(Columns.ADE_CODIGO).toString();
            final String sadCodigo = autdes.getAttribute(Columns.SAD_CODIGO).toString();
            final String rseCodigo = autdes.getAttribute(Columns.RSE_CODIGO).toString();
            final String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();
            final String funCodigo = responsavel.getFunCodigo() != null ? responsavel.getFunCodigo() : "";

            if (CodedValues.SAD_DEFERIDA.equals(sadCodigo) && (CodedValues.FUN_CANC_CONSIGNACAO.equals(funCodigo) || CodedValues.FUN_CANC_COMPRA.equals(funCodigo) || CodedValues.FUN_CANC_SOLICITACAO.equals(funCodigo) || CodedValues.FUN_CANC_RESERVA.equals(funCodigo) || CodedValues.FUN_LIQ_CONTRATO.equals(funCodigo))) {

                if (pesquisarConsignacaoController.isDestinoRenegociacao(adeCodigo)) {
                    return true;
                }
            } else if (CodedValues.SAD_AGUARD_DEFER.equals(sadCodigo) && (CodedValues.FUN_DEF_CONSIGNACAO.equals(funCodigo) || CodedValues.FUN_INDF_CONSIGNACAO.equals(funCodigo))) {

                final int qtd = autdes.getAttribute("QTD_ADE_AGUARD_LIQUIDACAO") != null ? Integer.parseInt(autdes.getAttribute("QTD_ADE_AGUARD_LIQUIDACAO").toString()) : 0;
                if (qtd > 0) {
                    return true;
                }

            } else if (responsavel.isCsaCor() && (CodedValues.FUN_AUT_RESERVA.equals(funCodigo) || CodedValues.FUN_CONF_SOLICITACAO.equals(funCodigo) || CodedValues.FUN_CONF_RESERVA.equals(funCodigo))) {

                final boolean possuiServicoTratamentoEspecial = !parametroController.lstParamSvcCse(CodedValues.TPS_SERVICO_TRATAMENTO_ESPECIAL_MARGEM, "1", responsavel).isEmpty();
                if (possuiServicoTratamentoEspecial) {
                    final BigDecimal somaValorContratosTratamentoEspecial = consultarMargemController.somarContratosTratamentoEspecialMargem(rseCodigo, responsavel);
                    if (somaValorContratosTratamentoEspecial.signum() > 0) {
                        final boolean exibirMensagemMargemTratamentoEspecial = !consultarMargemController.servidorTemMargem(rseCodigo, somaValorContratosTratamentoEspecial, svcCodigo, true, responsavel);
                        if (exibirMensagemMargemTratamentoEspecial) {
                            return true;
                        }
                    }
                }
            }
        } catch (AutorizacaoControllerException | ParametroControllerException | ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return false;
    }

    private boolean usarLinkAdicionalOperacao(TransferObject autdes, HttpSession session, AcessoSistema responsavel) {
        final String funCodigo = responsavel.getFunCodigo() != null ? responsavel.getFunCodigo() : "";
        if (CodedValues.FUN_REAT_CONSIGNACAO.equals(funCodigo)) {
            return FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_REAT_CONSIGNACAO, responsavel) || ((autdes.getAttribute("INC_MARGEM_INCONSISTENTE") != null) && "1".equals(autdes.getAttribute("INC_MARGEM_INCONSISTENTE")));
        }
        return false;
    }

    protected void carregarInformacoesAcessorias(String rseCodigo, String adeNumero, List<TransferObject> lstConsignacao, HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws AutorizacaoControllerException {
    }

    protected List<AcaoConsignacao> definirAcoesDetalheConsignacao(CustomTransferObject autdes, CustomTransferObject cde, ParamSvcTO paramSvcCse, boolean arquivado, boolean usuarioPodeModificarAde, boolean infMatCpf, HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        final List<AcaoConsignacao> acoes = new ArrayList<>();
        try {
            if (!arquivado) {
                final String adeCodigo = autdes.getAttribute(Columns.ADE_CODIGO).toString();
                final String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();
                final String csaCodigo = autdes.getAttribute(Columns.CSA_CODIGO).toString();
                final String rseCodigo = autdes.getAttribute(Columns.RSE_CODIGO).toString();
                final String sadCodigo = autdes.getAttribute(Columns.ADE_SAD_CODIGO).toString();
                final Short adeIncMargem = (Short) autdes.getAttribute(Columns.ADE_INC_MARGEM);
                final BigDecimal adeVlr = (BigDecimal) autdes.getAttribute(Columns.ADE_VLR);

                // Contrato em estoque pode ser suspenso por consignante ou suporte
                final boolean suspendeEstoque = (responsavel.isCseSup() || responsavel.isCsa()) && (CodedValues.SAD_ESTOQUE.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_MENSAL.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_NAO_LIBERADO.equals(sadCodigo));
                // Contrato em estoque ou suspenso pode ser alterado por consignante ou suporte
                final boolean cseSupAlteraEstoqueSuspensa = responsavel.isCseSup() && (CodedValues.SAD_ESTOQUE.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_MENSAL.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_NAO_LIBERADO.equals(sadCodigo) || CodedValues.SAD_SUSPENSA.equals(sadCodigo) || CodedValues.SAD_SUSPENSA_CSE.equals(sadCodigo));
                // Contratos aguard. confirmação e aguard. deferimento podem ser alterados conforme parâmetro
                final boolean permiteAlterarAguardConfDef = ParamSist.paramEquals(CodedValues.TPC_PERMITE_ALTERAR_ADE_AGUARD_CONF_E_DEF, CodedValues.TPC_SIM, responsavel) && (CodedValues.SAD_AGUARD_CONF.equals(sadCodigo) || CodedValues.SAD_AGUARD_DEFER.equals(sadCodigo));
                // Contratos em estoque podem ser alterados pela CSA conforme parâmetro
                final boolean permiteCsaAlterarEstoque = responsavel.isCsa() && ParamSist.paramEquals(CodedValues.TPC_PERMITE_CSA_ALTERAR_ADE_ESTOQUE, CodedValues.TPC_SIM, responsavel) && (CodedValues.SAD_ESTOQUE.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_MENSAL.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_NAO_LIBERADO.equals(sadCodigo));
                // Contratos com saldo informado contendo link para emissão de boleto
                final boolean exibirIconeBoletoSdv = saldoDevedorController.exibeLinkBoletoSaldo(adeCodigo, responsavel);

                boolean isOrigemLeilao = false;
                boolean isDestinoLeilao = false;
                if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, CodedValues.TPC_SIM, responsavel) && responsavel.isSer() && CodedValues.SAD_SOLICITADO.equals(sadCodigo)) {
                    isDestinoLeilao = pesquisarConsignacaoController.isDestinoRelacionamento(adeCodigo, CodedValues.TNT_LEILAO_SOLICITACAO);
                    if (!isDestinoLeilao) {
                        isOrigemLeilao = leilaoSolicitacaoController.temSolicitacaoLeilao(adeCodigo, true, responsavel);
                    }
                }

                boolean isDestinoRenegociacao = false;
                if (CodedValues.SAD_DEFERIDA.equals(sadCodigo)) {
                    isDestinoRenegociacao = pesquisarConsignacaoController.isDestinoRenegociacao(adeCodigo);
                }

                boolean informaMotivoNaoConcretizacaoLeilao = (responsavel.isSer() || responsavel.isCsa()) && CodedValues.SAD_CANCELADA.equals(sadCodigo) && responsavel.temPermissao(CodedValues.FUN_MOTIVO_NAO_CONCRETIZACAO_LEILAO) && pesquisarConsignacaoController.isDestinoRelacionamento(adeCodigo, CodedValues.TNT_LEILAO_SOLICITACAO);

                if (responsavel.isSer()) {
                    final Collection<OcorrenciaAutorizacao> oca = autorizacaoController.findOcorrenciaByAdeTocUsuCodigo(adeCodigo, CodedValues.TOC_MOTIVO_NAO_CONCRETIZACAO_LEILAO, responsavel.getUsuCodigo(), responsavel);
                    if ((oca != null) && !oca.isEmpty()) {
                        informaMotivoNaoConcretizacaoLeilao = false;
                    }
                } else if (responsavel.isCsa()) {
                    final Collection<OcorrenciaAutorizacao> oca = autorizacaoController.findOcorrenciaByAdeTocCsaCodigo(adeCodigo, CodedValues.TOC_MOTIVO_NAO_CONCRETIZACAO_LEILAO, responsavel.getCsaCodigo(), responsavel);
                    if ((oca != null) && !oca.isEmpty()) {
                        informaMotivoNaoConcretizacaoLeilao = false;
                    }
                }

                // DESENV-19379 Priorizar parametro de servico de consignataria 126 TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR
                // e caso nao exista, ai sim continuar como o fluxo anterios de buscar o parametro de serviço de consignatnte
                final List<String> tpsCodigo = new ArrayList<>();
                tpsCodigo.add(CodedValues.TPS_PERMITE_CADASTRAR_SALDO_DEVEDOR);
                final List<TransferObject> permiteCadastroSaldoDevedorCsaList = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigo, false, responsavel);
                final TransferObject objectParam = permiteCadastroSaldoDevedorCsaList.isEmpty() ? null : permiteCadastroSaldoDevedorCsaList.get(0);
                final String permiteCadastroSaldoDevedorCsa = objectParam != null ? (String) objectParam.getAttribute(Columns.PSC_VLR) : null;
                String permiteCadastroSaldoDevedor = permiteCadastroSaldoDevedorCsa != null ? permiteCadastroSaldoDevedorCsa : paramSvcCse.getTpsPermiteCadastrarSaldoDevedor();

                final boolean boolTpsPmtAlterar = paramSvcCse.isTpsPermiteAlteracaoContratos();
                boolean boolTpsPmtRenegociar = paramSvcCse.isTpsPermiteRenegociacao();
                final boolean boolTpsPmtCancelar = paramSvcCse.isTpsPermiteCancelarContratos();
                final boolean boolTpsPmtLiquidar = paramSvcCse.isTpsPermiteLiquidarContratos();
                final boolean boolTpsPmtLiquidarParcela = paramSvcCse.isTpsPermiteLiquidarParcela();
                final boolean boletoExterno = paramSvcCse.isTpsBuscaBoletoExterno();
                final boolean permiteSolicitarSaldoBeneficiario = paramSvcCse.isTpsPermiteSolicitarSaldoBeneficiario();
                final boolean servidorPodeLiquidarContrato = paramSvcCse.isTpsServidorLiquidaContrato();
                final boolean servidorPodeAlterarContrato = paramSvcCse.isTpsServidorAlteraContrato();
                final boolean podeLiquidarAdeSuspensa = paramSvcCse.isTpsPermiteLiquidarAdeSuspensa();
                final boolean exibirTabelaPrice = paramSvcCse.isTpsExibeTabelaPrice();
                final Short svcIncMargem = paramSvcCse.getTpsIncideMargem();

                boolean exigeAssinaturaDigital = false;
                if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_ASSINATURA_DIGITAL_CONSIGNACAO, CodedValues.TPC_SIM, responsavel)) {
                    // Recupero o parâmetro de consignatária
                    final List<String> tpsCsaCodigos = new ArrayList<>();
                    tpsCsaCodigos.add(CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES);
                    final List<TransferObject> paramSvcCsa = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCsaCodigos, false, responsavel);

                    for (final TransferObject vo : paramSvcCsa) {
                        if ((vo.getAttribute(Columns.PSC_VLR) != null) && !"".equals(vo.getAttribute(Columns.PSC_VLR))) {
                            String exige = null;
                            exige = vo.getAttribute(Columns.PSC_VLR).toString();
                            exigeAssinaturaDigital = "S".equals(exige);
                        }
                    }
                }

                // Se o serviço permite solicitação de saldo apenas por beneficiários, verifica se o servidor
                // é beneficiário, e caso não seja, não permite solicitação de saldo.
                if (permiteSolicitarSaldoBeneficiario && ((autdes.getAttribute(Columns.RSE_BENEFICIARIO_FINAN_DV_CART) == null) || !"S".equals(autdes.getAttribute(Columns.RSE_BENEFICIARIO_FINAN_DV_CART)))) {
                    permiteCadastroSaldoDevedor = CodedValues.NAO_POSSUI_CADASTRO_SALDO_DEVEDOR;
                }

                if (boolTpsPmtRenegociar) {
                    // Se o serviço da consignação atual não é origem de um relacionamento de renegociação,
                    // então não exibe ícone para renegociação deste contrato
                    final List<TransferObject> relacionamentosRenegociacao = parametroController.getRelacionamentoSvc(CodedValues.TNT_RENEGOCIACAO, svcCodigo, null, responsavel);
                    if ((relacionamentosRenegociacao == null) || (relacionamentosRenegociacao.size() == 0)) {
                        boolTpsPmtRenegociar = false;
                    }
                }

                // Calcula o valor dos contratos de serviço com tratamento especial de margem para exibição de mensagem para servidor.
                // Para que um contrato tenha tratamento especial de margem, não deve incidir sobre nenhuma margem e seu serviço deve ter TPS_CODIGO=224 habilitado.
                final BigDecimal somaValorContratosTratamentoEspecial = responsavel.isCsaCor() ? consultarMargemController.somarContratosTratamentoEspecialMargem((String) autdes.getAttribute(Columns.RSE_CODIGO), responsavel) : new BigDecimal("0");
                boolean exibirMensagemMargemTratamentoEspecial = false;
                try {
                    exibirMensagemMargemTratamentoEspecial = somaValorContratosTratamentoEspecial.signum() > 0 ? !consultarMargemController.servidorTemMargem((String) autdes.getAttribute(Columns.RSE_CODIGO), somaValorContratosTratamentoEspecial, svcCodigo, true, responsavel) : false;
                } catch (final Exception e) {
                    // Servidores excluidos e bloqueados geram exception pois não podem realizar nova reservas.
                }

                // Parâmetro para alertar o usuário caso tenha algum contrato do servidor aguardando liquidação ao deferir/indeferir um contrato
                // pesquisar contratos Aguard. Liquidação
                boolean msgAlertaDeferIndeferManual = false;
                if (ParamSist.paramEquals(CodedValues.TPC_ALERTA_DEFER_INDEFER_MANUAL_CONTRATO, CodedValues.TPC_SIM, responsavel)) {
                    final List<TransferObject> adeCodigos = new ArrayList<>();
                    adeCodigos.add(autdes);
                    final List<TransferObject> adeCodigosAguardLiquidacao = pesquisarConsignacaoController.pesquisarContratosAguardandoLiquidacao(adeCodigos, responsavel);
                    if ((adeCodigosAguardLiquidacao != null) && !adeCodigosAguardLiquidacao.isEmpty()) {
                        msgAlertaDeferIndeferManual = true;
                    }
                }

                // verifica se direciona para confirmação de reativação para caso de inconsistência entre incidência de margem no registro de contrato
                // e o configurado no serviço
                boolean confirmaReativacao = false;

                final boolean permiteRenegociarContratoSuspensoFolha = autorizacaoController.verificaContratoSuspensoPodeRenegociar(adeCodigo, sadCodigo, responsavel);
                //DESENV-13494: A partir desta tarefa, qualquer reativação em que o sistema exige motivo de operação para esta ação abre a tela de confirmação de reativação
                if (((CodedValues.SAD_SUSPENSA_CSE.equals(sadCodigo) || CodedValues.SAD_SUSPENSA.equals(sadCodigo)) && FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_REAT_CONSIGNACAO, responsavel)) || (CodedValues.SAD_SUSPENSA_CSE.equals(sadCodigo) && ((adeIncMargem == null) || (adeIncMargem.shortValue() == 0)) && (svcIncMargem != null) && (svcIncMargem.shortValue() != 0))) {
                    confirmaReativacao = true;
                }

                if (usuarioPodeModificarAde) {
                    if (infMatCpf) {
                        if (responsavel.isSer()) {
                            if (CodedValues.SAD_SOLICITADO.equals(sadCodigo) && responsavel.temPermissao(CodedValues.FUN_CANC_SOLICITACAO) && ((!isOrigemLeilao && !isDestinoLeilao) || (!isOrigemLeilao && isDestinoLeilao && ParamSist.paramEquals(CodedValues.TPC_PERMITE_SERVIDOR_CANCELAR_SOLICITACAO_LEILAO, CodedValues.TPC_SIM, responsavel)))) {

                                final String link = "../v3/cancelarSolicitacao?acao=iniciar&tipo=cancelar&opt=c&isDestinoRenegociacao=" + isDestinoRenegociacao;
                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.cancelar.solicitacao", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.cancelar.solicitacao.clique.aqui", responsavel);
                                final StringBuilder msgConfirmacao = new StringBuilder().append(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.cancelamento", responsavel));
                                if (isDestinoRenegociacao) {
                                    msgConfirmacao.append(" ").append(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.cancelamento.nao.reverter.renegociacao", responsavel));
                                }

                                acoes.add(new AcaoConsignacao("CANC_SOLICITACAO", CodedValues.FUN_CANC_SOLICITACAO, descricao, "cancelar_margem.gif", "btnCancelarConsignacao", msgAlternativa, msgConfirmacao.toString(), null, link, null));

                            } else if (CodedValues.SAD_AGUARD_CONF.equals(sadCodigo) && responsavel.temPermissao(CodedValues.FUN_CANC_COMPRA) && boolTpsPmtCancelar) {
                                final Collection<RelacionamentoAutorizacao> adeCompradas = compraContratoController.recuperarContratosOrigemCompra(adeCodigo);
                                if (adeCompradas.size() > 0) {

                                    final String link = "../v3/cancelarCompra?acao=efetivarAcao&tipo=cancelar_compra&opt=cc&isDestinoRenegociacao=" + isDestinoRenegociacao;
                                    final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.cancelar.compra", responsavel);
                                    final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.cancelar.consignacao.clique.aqui", responsavel);
                                    final StringBuilder msgConfirmacao = new StringBuilder().append(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.cancelamento", responsavel));
                                    if (isDestinoRenegociacao) {
                                        msgConfirmacao.append(" ").append(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.cancelamento.nao.reverter.renegociacao", responsavel));
                                    }

                                    acoes.add(new AcaoConsignacao("CANC_COMPRA", CodedValues.FUN_CANC_COMPRA, descricao, "cancelar_margem.gif", "btnCancelarConsignacao", msgAlternativa, msgConfirmacao.toString(), null, link, null));
                                }
                            }
                            // Liquidar para o papel servidor
                            if ((CodedValues.SAD_SUSPENSA_CSE.equals(sadCodigo) || CodedValues.SAD_SUSPENSA.equals(sadCodigo) || CodedValues.SAD_DEFERIDA.equals(sadCodigo) || CodedValues.SAD_EMANDAMENTO.equals(sadCodigo) || CodedValues.SAD_ESTOQUE.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_MENSAL.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_NAO_LIBERADO.equals(sadCodigo) || CodedValues.SAD_AGUARD_LIQUI_COMPRA.equals(sadCodigo) || CodedValues.SAD_EMCARENCIA.equals(sadCodigo)) && responsavel.temPermissao(CodedValues.FUN_LIQ_CONTRATO) && servidorPodeLiquidarContrato) {

                                final String link = "../v3/liquidarConsignacao?acao=efetivarAcao&opt=l";
                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.liquidar.consignacao", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.liquidar.consignacao.clique.aqui", responsavel);
                                final StringBuilder msgConfirmacao = new StringBuilder().append(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.liquidacao", responsavel));
                                if (isDestinoRenegociacao) {
                                    msgConfirmacao.append(" ").append(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.liquidacao.nao.reverter.renegociacao", responsavel));
                                }

                                acoes.add(new AcaoConsignacao("LIQ_CONTRATO", CodedValues.FUN_LIQ_CONTRATO, descricao, "liquidar_contrato.gif", "btnLiquidarConsignacao", msgAlternativa, msgConfirmacao.toString(), null, link, null));
                            }
                            // Alterar para o papel servidor
                            if ((CodedValues.SAD_DEFERIDA.equals(sadCodigo) || CodedValues.SAD_EMANDAMENTO.equals(sadCodigo)) && responsavel.temPermissao(CodedValues.FUN_ALT_CONSIGNACAO) && servidorPodeAlterarContrato) {

                                final String link = "../v3/alterarConsignacao?acao=editar&flow=start";
                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.alterar.consignacao", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.alterar.consignacao.clique.aqui", responsavel);
                                final String msgConfirmacao = "";

                                acoes.add(new AcaoConsignacao("ALT_CONSIGNACAO", CodedValues.FUN_ALT_CONSIGNACAO, descricao, "editar.gif", "btnAlterarConsignacao", msgAlternativa, msgConfirmacao, null, link, null));
                            }
                            // Autorizar
                            if ((CodedValues.SAD_AGUARD_CONF.equals(sadCodigo) || CodedValues.SAD_AGUARD_DEFER.equals(sadCodigo)) && responsavel.temPermissao(CodedValues.FUN_AUT_RESERVA)) {

                                final String link = "../v3/autorizarConsignacao?acao=confirmarReserva&ADE_CODIGO=" + adeCodigo;
                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.autorizar.reserva", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.autorizar.reserva.clique.aqui", responsavel);
                                String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.autorizacao", responsavel);
                                if (exibirMensagemMargemTratamentoEspecial) {
                                    msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.informacao.valor.parcela.maior.margem.tratamento.especial", responsavel) + "\n" + msgConfirmacao;
                                }

                                acoes.add(new AcaoConsignacao("AUT_RESERVA", CodedValues.FUN_AUT_RESERVA, descricao, "autorizar_reserva.gif", "btnAutorizarReserva", msgAlternativa, msgConfirmacao, null, link, null));
                            }
                            // Solicitar Propostas Pagamento
                            if (((CodedValues.SAD_DEFERIDA.equals(sadCodigo) || CodedValues.SAD_EMANDAMENTO.equals(sadCodigo)) && responsavel.temPermissao(CodedValues.FUN_SOLICITAR_PROPOSTAS_PGT_DIVIDA)) && financiamentoDividaController.exibeLinkSolicitacaoProposta(adeCodigo, responsavel)) {
                                final String link = "../v3/acompanharFinanciamentoDivida?acao=solicitar";
                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.solicitar.proposta.pagamento", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.solicitar.proposta.pagamento.clique.aqui", responsavel);
                                final String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.solicitar.proposta.pagamento", responsavel);

                                acoes.add(new AcaoConsignacao("SOLICITAR_PROPOSTAS_PGT_DIVIDA", CodedValues.FUN_SOLICITAR_PROPOSTAS_PGT_DIVIDA, descricao, "solicitar_propostas.png", "btnSolicitarPropostaPgtDivida", msgAlternativa, msgConfirmacao, null, link, null));
                            }
                            // Informar Pagamento de Contrato com Anexo de Comprovante
                            if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_BLOQ_CSA_N_LIQ_ADE_SALDO_PAGO_SER, CodedValues.TPC_SIM, responsavel)) {
                                if (saldoDevedorController.temSolicitacaoSaldoDevedorLiquidacaoRespondida(adeCodigo, responsavel) && (CodedValues.SAD_DEFERIDA.equals(sadCodigo) || CodedValues.SAD_EMANDAMENTO.equals(sadCodigo) || CodedValues.SAD_ESTOQUE.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_MENSAL.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_NAO_LIBERADO.equals(sadCodigo) || CodedValues.SAD_EMCARENCIA.equals(sadCodigo)) && responsavel.temPermissao(CodedValues.FUN_ANEXAR_COMPROVANTE_PAG_SALDO)) {
                                    final String link = "../v3/anexarPagamentoConsignacao?acao=iniciar";
                                    final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.informar.pagamento.saldo.devedor.anexo", responsavel);
                                    final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.informar.pagamento.saldo.devedor.anexo.clique.aqui", responsavel);
                                    final String msgConfirmacao = "";

                                    acoes.add(new AcaoConsignacao("ANEXAR_COMPROVANTE_PAG_SALDO", CodedValues.FUN_ANEXAR_COMPROVANTE_PAG_SALDO, descricao, "attach.png", "btnAnexoComprovantePgtSaldo", msgAlternativa, msgConfirmacao, null, link, null));
                                }
                            }

                        } else {
                            // Autorizar/Confirmar Solicitacao
                            if ((CodedValues.SAD_AGUARD_CONF.equals(sadCodigo) || CodedValues.SAD_AGUARD_DEFER.equals(sadCodigo)) && responsavel.temPermissao(CodedValues.FUN_AUT_RESERVA)) {

                                final String link = "../v3/autorizarConsignacao?acao=confirmarReserva&ADE_CODIGO=" + adeCodigo;
                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.autorizar.reserva", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.autorizar.reserva.clique.aqui", responsavel);
                                String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.autorizacao", responsavel);
                                if (exibirMensagemMargemTratamentoEspecial) {
                                    msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.informacao.valor.parcela.maior.margem.tratamento.especial", responsavel) + "\n" + msgConfirmacao;
                                }

                                acoes.add(new AcaoConsignacao("AUT_RESERVA", CodedValues.FUN_AUT_RESERVA, descricao, "autorizar_reserva.gif", "btnAutorizarReserva", msgAlternativa, msgConfirmacao, null, link, null));

                            } else if (CodedValues.SAD_SOLICITADO.equals(sadCodigo) && responsavel.temPermissao(CodedValues.FUN_CONF_SOLICITACAO)) {

                                final String link = "../v3/confirmarSolicitacao?acao=iniciar";
                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.confirmar.solicitacao", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.confirmar.solicitacao.clique.aqui", responsavel);
                                String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.solicitacao", responsavel);
                                if (exibirMensagemMargemTratamentoEspecial) {
                                    msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.informacao.valor.parcela.maior.margem.tratamento.especial", responsavel) + "\n" + msgConfirmacao;
                                }

                                acoes.add(new AcaoConsignacao("CONF_SOLICITACAO", CodedValues.FUN_CONF_SOLICITACAO, descricao, "confirmar_margem.gif", "btnConfirmarSolicitacao", msgAlternativa, msgConfirmacao, null, link, null));
                            }
                            // Reativar/Suspender
                            if ((CodedValues.SAD_SUSPENSA.equals(sadCodigo) || (CodedValues.SAD_SUSPENSA_CSE.equals(sadCodigo) && responsavel.isCseSupOrg())) && responsavel.temPermissao(CodedValues.FUN_REAT_CONSIGNACAO)) {

                                String link = "../v3/reativarConsignacao?acao=confirmarReativacao&ADE_CODIGO=" + adeCodigo + "&svc_codigo=" + svcCodigo + "&rse_codigo=" + rseCodigo + "&ade_vlr=" + adeVlr;
                                if (!confirmaReativacao) {
                                    link = "../v3/reativarConsignacao?acao=efetivarAcao";
                                }

                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.reativar.consignacao", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.reativar.consignacao.clique.aqui", responsavel);
                                final String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.reativacao", responsavel);

                                acoes.add(new AcaoConsignacao("REAT_CONSIGNACAO", CodedValues.FUN_REAT_CONSIGNACAO, descricao, "bloqueado.gif", "btnReativarConsignacao", msgAlternativa, msgConfirmacao, null, link, null));

                            } else if ((CodedValues.SAD_DEFERIDA.equals(sadCodigo) || CodedValues.SAD_EMANDAMENTO.equals(sadCodigo) || CodedValues.SAD_EMCARENCIA.equals(sadCodigo) || suspendeEstoque) && responsavel.temPermissao(CodedValues.FUN_SUSP_CONSIGNACAO)) {

                                final String link = "../v3/suspenderConsignacao?acao=confirmarSuspensao";
                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.suspender.consignacao", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.suspender.consignacao.clique.aqui", responsavel);
                                final String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.suspensao", responsavel);

                                acoes.add(new AcaoConsignacao("SUSP_CONSIGNACAO", CodedValues.FUN_SUSP_CONSIGNACAO, descricao, "desbloqueado.gif", "btnSuspenderConsignacao", msgAlternativa, msgConfirmacao, null, link, null));
                            }
                            // Confirmar
                            if (CodedValues.SAD_AGUARD_CONF.equals(sadCodigo) && (responsavel.temPermissao(CodedValues.FUN_CONF_RESERVA) || responsavel.temPermissao(CodedValues.FUN_CONFIRMAR_RENEGOCIACAO))) {
                                final boolean isDestinoRenegociacaoConfirmacao = pesquisarConsignacaoController.isDestinoRelacionamento(adeCodigo, CodedValues.TNT_CONTROLE_RENEGOCIACAO);
                                final boolean isDestinoCompraConfirmacao = pesquisarConsignacaoController.isDestinoRelacionamento(adeCodigo, CodedValues.TNT_CONTROLE_COMPRA);

                                final String link = "../v3/confirmarConsignacao?acao=efetivarAcao";
                                final String descricao = !isDestinoRenegociacaoConfirmacao ? !isDestinoCompraConfirmacao ?
                                        ApplicationResourcesHelper.getMessage("mensagem.acao.confirmar.reserva", responsavel) :
                                        ApplicationResourcesHelper.getMessage("mensagem.acao.confirmar.portabilidade", responsavel) :
                                        ApplicationResourcesHelper.getMessage("mensagem.acao.confirmar.renegociacao", responsavel);
                                final String msgAlternativa = !isDestinoRenegociacaoConfirmacao ? !isDestinoCompraConfirmacao ?
                                        ApplicationResourcesHelper.getMessage("mensagem.confirmar.reserva.clique.aqui", responsavel) :
                                        ApplicationResourcesHelper.getMessage("mensagem.confirmar.portabilidade.clique.aqui", responsavel) :
                                        ApplicationResourcesHelper.getMessage("mensagem.confirmar.renegociacao.clique.aqui", responsavel);
                                String msgConfirmacao = !isDestinoRenegociacaoConfirmacao ? !isDestinoCompraConfirmacao ?
                                        ApplicationResourcesHelper.getMessage("mensagem.confirmacao.reserva", responsavel) :
                                        ApplicationResourcesHelper.getMessage("mensagem.confirmacao.portabilidade", responsavel) :
                                        ApplicationResourcesHelper.getMessage("mensagem.confirmacao.renegociacao", responsavel);
                                if (exibirMensagemMargemTratamentoEspecial) {
                                    msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.informacao.valor.parcela.maior.margem.tratamento.especial", responsavel) + "\n" + msgConfirmacao;
                                }

                                if (responsavel.temPermissao(CodedValues.FUN_CONF_RESERVA) && !isDestinoRenegociacaoConfirmacao && !isDestinoCompraConfirmacao) {
                                    acoes.add(new AcaoConsignacao("CONF_RESERVA", CodedValues.FUN_CONF_RESERVA, descricao, "confirmar_margem.gif", "btnConfirmarReserva", msgAlternativa, msgConfirmacao, null, link, null));
                                } else if (responsavel.temPermissao(CodedValues.FUN_CONFIRMAR_RENEGOCIACAO) && (isDestinoRenegociacaoConfirmacao || isDestinoCompraConfirmacao)) {
                                    acoes.add(new AcaoConsignacao("CONF_RENEGOCIACAO", CodedValues.FUN_CONFIRMAR_RENEGOCIACAO, descricao, "confirmar_margem.gif", "btnConfirmarReserva", msgAlternativa, msgConfirmacao, null, link, null));
                                }
                            }
                            // Deferir
                            if (CodedValues.SAD_AGUARD_DEFER.equals(sadCodigo) && responsavel.isCseSupOrg() && responsavel.temPermissao(CodedValues.FUN_DEF_CONSIGNACAO)) {

                                final String link = "../v3/deferirConsignacao?acao=efetivarAcao";
                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.deferir.consignacao", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.deferir.consignacao.clique.aqui", responsavel);
                                String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.deferimento", responsavel);
                                if (msgAlertaDeferIndeferManual) {
                                    msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.deferimento.liquidacao.pendente", responsavel) + " " + msgConfirmacao;
                                }

                                acoes.add(new AcaoConsignacao("DEF_CONSIGNACAO", CodedValues.FUN_DEF_CONSIGNACAO, descricao, "deferir_consignacao.gif", "btnDeferirConsignacao", msgAlternativa, msgConfirmacao, null, link, null));
                            }
                            // Cancelar Reserva/Cancelar Consignacao
                            if (((CodedValues.SAD_SUSPENSA_CSE.equals(sadCodigo) && responsavel.isCseSupOrg()) || CodedValues.SAD_SUSPENSA.equals(sadCodigo) || CodedValues.SAD_DEFERIDA.equals(sadCodigo) || CodedValues.SAD_EMCARENCIA.equals(sadCodigo)) && responsavel.temPermissao(CodedValues.FUN_CANC_CONSIGNACAO) && boolTpsPmtCancelar) {

                                final String link = "../v3/cancelarConsignacao?acao=efetivarAcao&opt=ca&isDestinoRenegociacao=" + isDestinoRenegociacao;
                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.cancelar.consignacao", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.cancelar.consignacao.clique.aqui", responsavel);
                                final StringBuilder msgConfirmacao = new StringBuilder().append(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.cancelamento", responsavel));
                                if (isDestinoRenegociacao) {
                                    msgConfirmacao.append(" ").append(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.cancelamento.nao.reverter.renegociacao", responsavel));
                                }

                                acoes.add(new AcaoConsignacao("CANC_CONSIGNACAO", CodedValues.FUN_CANC_CONSIGNACAO, descricao, "cancelar_margem.gif", "btnCancelarConsignacao", msgAlternativa, msgConfirmacao.toString(), null, link, null));

                            } else if ((CodedValues.SAD_SOLICITADO.equals(sadCodigo) || CodedValues.SAD_AGUARD_CONF.equals(sadCodigo) || CodedValues.SAD_AGUARD_DEFER.equals(sadCodigo) || CodedValues.SAD_AGUARD_MARGEM.equals(sadCodigo)) && boolTpsPmtCancelar) {
                                if (responsavel.temPermissao(CodedValues.FUN_CANC_RESERVA)) {

                                    final String link = "../v3/cancelarReserva?acao=efetivarAcao&isDestinoRenegociacao=" + isDestinoRenegociacao;
                                    final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.cancelar.reserva", responsavel);
                                    final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.cancelar.reserva.clique.aqui", responsavel);
                                    final StringBuilder msgConfirmacao = new StringBuilder().append(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.cancelamento", responsavel));
                                    if (isDestinoRenegociacao) {
                                        msgConfirmacao.append(" ").append(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.cancelamento.nao.reverter.renegociacao", responsavel));
                                    }

                                    acoes.add(new AcaoConsignacao("CANC_RESERVA", CodedValues.FUN_CANC_RESERVA, descricao, "cancelar_margem.gif", "btnCancelarConsignacao", msgAlternativa, msgConfirmacao.toString(), null, link, null));

                                } else if (responsavel.temPermissao(CodedValues.FUN_CANC_MINHAS_RESERVAS) && autdes.getAttribute(Columns.USU_CODIGO).toString().equals(responsavel.getUsuCodigo())) {

                                    final String link = "../v3/cancelarMinhasReservas?acao=efetivarAcao&tipo=cancelarminhas&opt=cm&isDestinoRenegociacao=" + isDestinoRenegociacao;
                                    final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.cancelar.reserva", responsavel);
                                    final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.cancelar.reserva.clique.aqui", responsavel);
                                    final StringBuilder msgConfirmacao = new StringBuilder().append(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.cancelamento", responsavel));
                                    if (isDestinoRenegociacao) {
                                        msgConfirmacao.append(" ").append(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.cancelamento.nao.reverter.renegociacao", responsavel));
                                    }

                                    acoes.add(new AcaoConsignacao("CANC_MINHAS_RESERVAS", CodedValues.FUN_CANC_MINHAS_RESERVAS, descricao, "cancelar_margem.gif", "btnCancelarConsignacao", msgAlternativa, msgConfirmacao.toString(), null, link, null));
                                }
                            }
                            // Descancelar
                            if (CodedValues.SAD_CANCELADA.equals(sadCodigo) && responsavel.temPermissao(CodedValues.FUN_DESCANCELAR_CONTRATO)) {

                                final String link = "../v3/descancelarConsignacao?acao=efetivarAcao&opt=dc";
                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.descancelar.consignacao", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.descancelar.consignacao.clique.aqui", responsavel);
                                final String msgConfirmacao = "";

                                acoes.add(new AcaoConsignacao("DESCANCELAR_CONTRATO", CodedValues.FUN_DESCANCELAR_CONTRATO, descricao, "liquidar_contrato_des.gif", "btnDescancelarConsignacao", msgAlternativa, msgConfirmacao, null, link, null));
                            }
                            // Indeferir
                            if (CodedValues.SAD_AGUARD_DEFER.equals(sadCodigo) && responsavel.isCseSupOrg() && responsavel.temPermissao(CodedValues.FUN_INDF_CONSIGNACAO)) {

                                final String link = "../v3/indeferirConsignacao?acao=efetivarAcao";
                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.indeferir.consignacao", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.indeferir.consignacao.clique.aqui", responsavel);
                                String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.indeferimento", responsavel);
                                if (msgAlertaDeferIndeferManual) {
                                    msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.deferimento.liquidacao.pendente", responsavel) + " " + msgConfirmacao;
                                }

                                acoes.add(new AcaoConsignacao("INDF_CONSIGNACAO", CodedValues.FUN_INDF_CONSIGNACAO, descricao, "indeferir_margem.gif", "btnIndeferirConsignacao", msgAlternativa, msgConfirmacao, null, link, null));
                            }
                            // Liquidar para os demais papeis
                            if ((CodedValues.SAD_DEFERIDA.equals(sadCodigo) || CodedValues.SAD_EMANDAMENTO.equals(sadCodigo) || CodedValues.SAD_ESTOQUE.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_MENSAL.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_NAO_LIBERADO.equals(sadCodigo) || CodedValues.SAD_AGUARD_LIQUI_COMPRA.equals(sadCodigo) || CodedValues.SAD_EMCARENCIA.equals(sadCodigo) || (podeLiquidarAdeSuspensa && CodedValues.SAD_CODIGOS_SUSPENSOS.contains(sadCodigo))) && responsavel.temPermissao(CodedValues.FUN_LIQ_CONTRATO) && boolTpsPmtLiquidar) {

                                final String link = "../v3/liquidarConsignacao?acao=efetivarAcao&opt=l";
                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.liquidar.consignacao", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.liquidar.consignacao.clique.aqui", responsavel);
                                final StringBuilder msgConfirmacao = new StringBuilder().append(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.liquidacao", responsavel));
                                if (isDestinoRenegociacao) {
                                    msgConfirmacao.append(" ").append(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.liquidacao.nao.reverter.renegociacao", responsavel));
                                }

                                acoes.add(new AcaoConsignacao("LIQ_CONTRATO", CodedValues.FUN_LIQ_CONTRATO, descricao, "liquidar_contrato.gif", "btnLiquidarConsignacao", msgAlternativa, msgConfirmacao.toString(), null, link, null));
                            }
                            // Liquidar Parcela
                            if ((CodedValues.SAD_DEFERIDA.equals(sadCodigo) || CodedValues.SAD_ESTOQUE.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_MENSAL.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_NAO_LIBERADO.equals(sadCodigo) || CodedValues.SAD_EMANDAMENTO.equals(sadCodigo) || CodedValues.SAD_EMCARENCIA.equals(sadCodigo)) && responsavel.temPermissao(CodedValues.FUN_LIQUIDAR_PARCELA) && boolTpsPmtLiquidarParcela) {

                                final String link = "../v3/liquidarParcela?acao=editar";
                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.liquidar.parcela", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.liquidar.parcela.clique.aqui", responsavel);
                                final String msgConfirmacao = "";

                                acoes.add(new AcaoConsignacao("LIQUIDAR_PARCELA", CodedValues.FUN_LIQUIDAR_PARCELA, descricao, "liquidar_parcela.gif", "btnLiquidarParcela", msgAlternativa, msgConfirmacao, null, link, null));
                            }
                            // Desliquidar
                            if (CodedValues.SAD_LIQUIDADA.equals(sadCodigo) && responsavel.temPermissao(CodedValues.FUN_DESLIQ_CONTRATO)) {

                                String link = "../v3/desliquidarConsignacao?acao=efetivarAcao";
                                if (responsavel.temPermissao(CodedValues.FUN_DESLIQUIDACAO_AVANCADA_CONTRATO)) {
                                    link = "../v3/desliquidarConsignacao?acao=confirmarDesliquidacao&ADE_CODIGO=" + adeCodigo;
                                }
                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.desliquidar.consignacao", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.desliquidar.consignacao.clique.aqui", responsavel);
                                final String msgConfirmacao = responsavel.temPermissao(CodedValues.FUN_DESLIQUIDACAO_AVANCADA_CONTRATO) ? "" : ApplicationResourcesHelper.getMessage("mensagem.confirmacao.desliquidacao", responsavel);

                                acoes.add(new AcaoConsignacao("DESLIQ_CONTRATO", CodedValues.FUN_DESLIQ_CONTRATO, descricao, "liquidar_contrato_des.gif", "btnDesliquidarConsignacao", msgAlternativa, msgConfirmacao, null, link, null));
                            }
                            // Renegociar
                            if ((CodedValues.SAD_DEFERIDA.equals(sadCodigo) || CodedValues.SAD_EMANDAMENTO.equals(sadCodigo) || CodedValues.SAD_ESTOQUE.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_MENSAL.equals(sadCodigo) || CodedValues.SAD_EMCARENCIA.equals(sadCodigo) || permiteRenegociarContratoSuspensoFolha)
                                    && responsavel.temPermissao(CodedValues.FUN_RENE_CONTRATO) && boolTpsPmtRenegociar) {

                                final String link = "../v3/renegociarConsignacao?acao=renegociarConsignacao";
                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.renegociar.consignacao", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.renegociar.consignacao.clique.aqui", responsavel);
                                final String msgConfirmacao = "";

                                acoes.add(new AcaoConsignacao("RENE_CONTRATO", CodedValues.FUN_RENE_CONTRATO, descricao, "renegociar_contrato.gif", "btnRenegociarConsignacao", msgAlternativa, msgConfirmacao, null, link, null));
                            }
                            // Inf. Saldo Devedor
                            final Boolean temRelacionamentoCompraByOrigem = compraContratoController.temRelacionamentoCompraByOrigem(adeCodigo);

                            if ((CodedValues.SAD_DEFERIDA.equals(sadCodigo) || CodedValues.SAD_EMANDAMENTO.equals(sadCodigo) || CodedValues.SAD_ESTOQUE.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_MENSAL.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_NAO_LIBERADO.equals(sadCodigo) || CodedValues.SAD_EMCARENCIA.equals(sadCodigo)) && responsavel.temPermissao(CodedValues.FUN_EDT_SALDO_DEVEDOR_SOLICITACAO_SER) && (CodedValues.USUARIO_CADASTRA_SALDO_DEVEDOR.equals(permiteCadastroSaldoDevedor) || CodedValues.CADASTRA_E_CALCULA_SALDO_DEVEDOR.equals(permiteCadastroSaldoDevedor))) {

                                final String link = "../v3/editarSaldoDevedorSolicitacao?acao=iniciar&tipo=solicitacao_saldo";
                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.editar.saldo.devedor", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.editar.saldo.devedor.clique.aqui", responsavel);
                                final String msgConfirmacao = "";

                                final String funEdtSaldoDevedor = responsavel.temPermissao(CodedValues.FUN_EDT_SALDO_DEVEDOR) ? CodedValues.FUN_EDT_SALDO_DEVEDOR : CodedValues.FUN_EDT_SALDO_DEVEDOR_SOLICITACAO_SER;
                                acoes.add(new AcaoConsignacao("EDT_SALDO_DEVEDOR", funEdtSaldoDevedor, descricao, "saldo_devedor.gif", "btnEditarSaldo", msgAlternativa, msgConfirmacao, null, link, null));

                            } else if (temRelacionamentoCompraByOrigem && CodedValues.SAD_AGUARD_LIQUI_COMPRA.equals(sadCodigo) && responsavel.temPermissao(CodedValues.FUN_EDT_SALDO_DEVEDOR)) {

                                final String link = "../v3/editarSaldoDevedor?acao=iniciar&tipo=compra";
                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.editar.saldo.devedor", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.editar.saldo.devedor.clique.aqui", responsavel);
                                final String msgConfirmacao = "";

                                final String funEdtSaldoDevedor = responsavel.temPermissao(CodedValues.FUN_EDT_SALDO_DEVEDOR) ? CodedValues.FUN_EDT_SALDO_DEVEDOR : CodedValues.FUN_EDT_SALDO_DEVEDOR_SOLICITACAO_SER;
                                acoes.add(new AcaoConsignacao("EDT_SALDO_DEVEDOR_COMPRA", funEdtSaldoDevedor, descricao, "saldo_devedor.gif", "btnEditarSaldoCompra", msgAlternativa, msgConfirmacao, null, link, null));
                            }
                            // Liberar Estoque
                            if (CodedValues.SAD_ESTOQUE_NAO_LIBERADO.equals(sadCodigo) && responsavel.temPermissao(CodedValues.FUN_LIBERAR_ESTOQUE)) {

                                final String link = "../v3/liberarConsignacao?acao=liberarConsignacao";
                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.liberar.consignacao", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.liberar.consignacao.clique.aqui", responsavel);
                                final String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.liberacaoEstq", responsavel);

                                acoes.add(new AcaoConsignacao("LIBERAR_ESTOQUE", CodedValues.FUN_LIBERAR_ESTOQUE, descricao, "liberar_estoque.gif", "btnLiberarEstoque", msgAlternativa, msgConfirmacao, null, link, null));
                            }
                            // Alterar
                            if ((CodedValues.SAD_DEFERIDA.equals(sadCodigo) || CodedValues.SAD_EMANDAMENTO.equals(sadCodigo) || cseSupAlteraEstoqueSuspensa || permiteAlterarAguardConfDef || permiteCsaAlterarEstoque) && responsavel.temPermissao(CodedValues.FUN_ALT_CONSIGNACAO) && boolTpsPmtAlterar) {

                                final String link = "../v3/alterarConsignacao?acao=editar&flow=start";
                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.alterar.consignacao", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.alterar.consignacao.clique.aqui", responsavel);
                                final String msgConfirmacao = "";

                                acoes.add(new AcaoConsignacao("ALT_CONSIGNACAO", CodedValues.FUN_ALT_CONSIGNACAO, descricao, "editar.gif", "btnAlterarConsignacao", msgAlternativa, msgConfirmacao, null, link, null));
                            }
                            // Alongar
                            if ((CodedValues.SAD_DEFERIDA.equals(sadCodigo) || CodedValues.SAD_EMANDAMENTO.equals(sadCodigo) || CodedValues.SAD_ESTOQUE.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_MENSAL.equals(sadCodigo) || CodedValues.SAD_EMCARENCIA.equals(sadCodigo)) && responsavel.temPermissao(CodedValues.FUN_ALONGAR_CONTRATO) && ParamSist.paramEquals(CodedValues.TPC_TEM_ALONGAMENTO_CONTRATO, CodedValues.TPC_SIM, responsavel)) {

                                final String link = "../v3/alongarConsignacao?acao=detalhar&ADE_CODIGO=" + adeCodigo;
                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.alongar.consignacao", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.alongar.consignacao.clique.aqui", responsavel);
                                final String msgConfirmacao = "";

                                acoes.add(new AcaoConsignacao("ALONGAR_CONTRATO", CodedValues.FUN_ALONGAR_CONTRATO, descricao, "alongar_contrato.gif", "btnAlongarConsignacao", msgAlternativa, msgConfirmacao, null, link, null));
                            }
                            // Reimplantar
                            if ((CodedValues.SAD_DEFERIDA.equals(sadCodigo) || CodedValues.SAD_EMANDAMENTO.equals(sadCodigo) || CodedValues.SAD_ESTOQUE.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_MENSAL.equals(sadCodigo)) && responsavel.temPermissao(CodedValues.FUN_REIMP_CONSIGNACAO) && ParamSist.paramEquals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, CodedValues.TPC_SIM, responsavel)) {

                                final String link = "../v3/reimplantarConsignacao?acao=iniciar&ADE_CODIGO=" + adeCodigo + "&opt=rei";
                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.reimplantar.consignacao", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.reimplantar.consignacao.clique.aqui", responsavel);
                                final String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.reimplantacao", responsavel);

                                acoes.add(new AcaoConsignacao("REIMP_CONSIGNACAO", CodedValues.FUN_REIMP_CONSIGNACAO, descricao, "reimplantar.png", "btnReimplantarConsignacao", msgAlternativa, msgConfirmacao, null, link, null));
                            }
                            // Editar Anexo
                            if (responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO)) {

                                String link = null;
                                if(!ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE, responsavel)) {
                                    link = "../v3/editarAnexosConsignacao?acao=exibir&ADE_CODIGO=" + adeCodigo;
                                } else {
                                    link = "../v3/editarAnexosConsignacao?acao=exibir&validarDocumentos=true&ADE_CODIGO=" + adeCodigo;
                                }

                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.editar.anexo.consignacao", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.editar.anexo.consignacao.clique.aqui", responsavel);
                                final String msgConfirmacao = "";

                                acoes.add(new AcaoConsignacao("EDITAR_ANEXO_CONSIGNACAO", CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO, descricao, "attach.png", "btnEditarAnexoConsignacao", msgAlternativa, msgConfirmacao, null, link, null));
                            }
                            // Registrar Ocorrência Consignação
                            if (responsavel.temPermissao(CodedValues.FUN_REGISTRAR_OCO_CONSIGNACAO)) {

                                final String link = "../v3/registrarOcorrenciaConsignacao?acao=iniciarRegistro";
                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.registrar.ocorrencia.consignacao", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.registrar.ocorrencia.consignacao.clique.aqui", responsavel);
                                final String msgConfirmacao = "";

                                acoes.add(new AcaoConsignacao("REGISTRAR_OCO_CONSIGNACAO", CodedValues.FUN_REGISTRAR_OCO_CONSIGNACAO, descricao, "table_row_insert.png", "btnRegistrarOcorrencia", msgAlternativa, msgConfirmacao, null, link, null));
                            }
                            // Enviar resumo da consignação
                            if (responsavel.temPermissao(CodedValues.FUN_ENVIAR_RESUMO_ADE_POR_EMAIL_SMS)) {

                                final String link = "../v3/enviarResumoAdeParaServidor?acao=enviar";
                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.enviar.resumo.consignacao", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.acao.enviar.resumo.consignacao.clique.aqui", responsavel);
                                final String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.envio.resumo.consignacao", responsavel);

                                acoes.add(new AcaoConsignacao("ENVIAR_RESUMO_CONSIGNACAO", CodedValues.FUN_ENVIAR_RESUMO_ADE_POR_EMAIL_SMS, descricao, "enviar_email.png", "btnEnviarResumo", msgAlternativa, msgConfirmacao, null, link, null));
                            }
                        }

                        // Opções do Servidor e CseOrg (Solicitar Saldo Devedor)
                        if ((responsavel.isSer() || responsavel.isCseSupOrg()) && (CodedValues.SAD_DEFERIDA.equals(sadCodigo) || CodedValues.SAD_EMANDAMENTO.equals(sadCodigo) || CodedValues.SAD_ESTOQUE.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_MENSAL.equals(sadCodigo) || CodedValues.SAD_ESTOQUE_NAO_LIBERADO.equals(sadCodigo) || CodedValues.SAD_EMCARENCIA.equals(sadCodigo)) && (responsavel.temPermissao(CodedValues.FUN_SOLICITAR_SALDO_DEVEDOR) || responsavel.temPermissao(CodedValues.FUN_SOLICITAR_SALDO_DEVEDOR_PARA_LIQ))
                                && !CodedValues.NAO_POSSUI_CADASTRO_SALDO_DEVEDOR.equals(permiteCadastroSaldoDevedor)) {

                            if (responsavel.temPermissao(CodedValues.FUN_SOLICITAR_SALDO_DEVEDOR)) {
                                if (CodedValues.SISTEMA_CALCULA_SALDO_DEVEDOR.equals(permiteCadastroSaldoDevedor) || CodedValues.CADASTRA_E_CALCULA_SALDO_DEVEDOR.equals(permiteCadastroSaldoDevedor)) {
                                    final String link = "../v3/solicitarSaldoDevedor?acao=consultar";
                                    final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.simular.saldo.devedor", responsavel);
                                    final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.simular.saldo.devedor.clique.aqui", responsavel);
                                    final String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.simular.saldo.devedor", responsavel);

                                    acoes.add(new AcaoConsignacao("CALCULAR_SALDO_DEVEDOR", CodedValues.FUN_SOLICITAR_SALDO_DEVEDOR, descricao, "consultar_saldo_devedor.gif", "btnSimularSaldoDevedor", msgAlternativa, msgConfirmacao, null, link, null));
                                }
                                if (CodedValues.USUARIO_CADASTRA_SALDO_DEVEDOR.equals(permiteCadastroSaldoDevedor) || CodedValues.CADASTRA_E_CALCULA_SALDO_DEVEDOR.equals(permiteCadastroSaldoDevedor)) {
                                    final String link = "../v3/solicitarSaldoDevedor?acao=solicitar";
                                    final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.solicitar.saldo.devedor.info", responsavel);
                                    final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.solicitar.saldo.devedor.info.clique.aqui", responsavel);
                                    final String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.solicitar.saldo.devedor.info", responsavel);

                                    acoes.add(new AcaoConsignacao("SOLICITAR_SALDO_DEVEDOR", CodedValues.FUN_SOLICITAR_SALDO_DEVEDOR, descricao, "saldo_devedor.gif", "btnSolicitarSaldoDevedorInfo", msgAlternativa, msgConfirmacao, null, link, null));
                                }
                            }
                            if ((responsavel.temPermissao(CodedValues.FUN_SOLICITAR_SALDO_DEVEDOR_PARA_LIQ) && !permiteSolicitarSaldoBeneficiario) && (CodedValues.USUARIO_CADASTRA_SALDO_DEVEDOR.equals(permiteCadastroSaldoDevedor) || CodedValues.CADASTRA_E_CALCULA_SALDO_DEVEDOR.equals(permiteCadastroSaldoDevedor))) {
                                final String link = "../v3/solicitarSaldoDevedor?acao=solicitar_liquidacao";
                                final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.solicitar.saldo.devedor.liq", responsavel);
                                final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.solicitar.saldo.devedor.liq.clique.aqui", responsavel);
                                final String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.solicitar.saldo.devedor.liq", responsavel);

                                acoes.add(new AcaoConsignacao("SOLICITAR_SALDO_DEVEDOR_PARA_LIQ", CodedValues.FUN_SOLICITAR_SALDO_DEVEDOR_PARA_LIQ, descricao, "pgt_saldo_devedor.gif", "btnSolicitarSaldoDevedorLiq", msgAlternativa, msgConfirmacao, null, link, null));
                            }
                        }
                    }

                    // Gerar Boleto
                    if (!isOrigemLeilao && ((cde != null) || boletoExterno)) {
                        final String link = request.getRequestURI() + (boletoExterno ? "?acao=emitirBoletoExterno" : "?acao=emitirBoleto");
                        final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.boleto.consignacao", responsavel);
                        final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.boleto.consignacao.clique.aqui", responsavel);
                        final String msgConfirmacao = "";

                        acoes.add(new AcaoConsignacao("BOLETO", null, descricao, "html.gif", "btnVisualizarAutorizacaoDesconto", msgAlternativa, msgConfirmacao, null, link, null));
                    }
                    // Exibir Tabela Price
                    if (exibirTabelaPrice) {
                        final String link = "";
                        final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.exibe.tabela.price", responsavel);
                        final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.exibe.tabela.price.clique.aqui", responsavel);
                        final String msgConfirmacao = "";

                        acoes.add(new AcaoConsignacao("TABELA_PRICE", null, descricao, "table2.png", "btnTabelaPrice", msgAlternativa, msgConfirmacao, null, link, null));
                    }

                    // Editar Anexo de solicitação para assinatura digital
                    if (CodedValues.SAD_SOLICITADO.equals(sadCodigo) && responsavel.isSer() && !isOrigemLeilao && exigeAssinaturaDigital && responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_SOLICITACAO) && inserirSolicitacaoController.temSolicitacaoCreditoEletronicoPendenteDocumentacao(adeCodigo, responsavel)) {

                        final String link = "../v3/editarAnexosSolicitacao?acao=exibir&ADE_CODIGO=" + adeCodigo;
                        final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.editar.anexo.consignacao", responsavel);
                        final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.editar.anexo.consignacao.clique.aqui", responsavel);
                        final String msgConfirmacao = "";

                        acoes.add(new AcaoConsignacao("EDITAR_ANEXO_CONSIGNACAO", CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO, descricao, "attach.png", "btnEditarAnexoConsignacao", msgAlternativa, msgConfirmacao, null, link, null));

                        // Editar Anexo de solicitação comum
                    } else if ((!CodedValues.SAD_INDEFERIDA.equals(sadCodigo) && !CodedValues.SAD_CANCELADA.equals(sadCodigo) && !CodedValues.SAD_LIQUIDADA.equals(sadCodigo) && !CodedValues.SAD_CONCLUIDO.equals(sadCodigo) && !CodedValues.SAD_ENCERRADO.equals(sadCodigo) && !CodedValues.SAD_SUSPENSA.equals(sadCodigo) && !CodedValues.SAD_SUSPENSA_CSE.equals(sadCodigo)) && responsavel.isSer() && responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO)) {

                        String link = null;
                        if (!ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE, responsavel)) {
                            link = "../v3/editarAnexosConsignacao?acao=exibir&ADE_CODIGO=" + adeCodigo;
                        } else {
                            link = "../v3/editarAnexosConsignacao?acao=exibir&validarDocumentos=true&ADE_CODIGO=" + adeCodigo;
                        }
                        final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.editar.anexo.consignacao", responsavel);
                        final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.editar.anexo.consignacao.clique.aqui", responsavel);
                        final String msgConfirmacao = "";

                        acoes.add(new AcaoConsignacao("EDITAR_ANEXO_CONSIGNACAO", CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO, descricao, "attach.png", "btnEditarAnexoConsignacao", msgAlternativa, msgConfirmacao, null, link, null));
                    }

                    // Aprovar Anexo de solicitação
                    if (CodedValues.SAD_SOLICITADO.equals(sadCodigo) && responsavel.temPermissao(CodedValues.FUN_APROV_REJCT_ANEXO_SOLICITACAO) && exigeAssinaturaDigital && responsavel.isCsa() && !isOrigemLeilao) {

                        final List<String> tisCodigos = new ArrayList<>();
                        tisCodigos.add(TipoSolicitacaoEnum.SOLICITACAO_CONSIGNACAO_CREDITO_ELETRONICO.getCodigo());
                        final List<String> ssoCodigos = new ArrayList<>();
                        ssoCodigos.add(StatusSolicitacaoEnum.PENDENTE_VALIDACAO_DOCUMENTOS.getCodigo());

                        final List<TransferObject> solicitacoes = simulacaoController.lstRegistrosSolicitacaoAutorizacao(adeCodigo, tisCodigos, ssoCodigos, responsavel);
                        if ((solicitacoes != null) && !solicitacoes.isEmpty()) {
                            final String link = "../v3/listarSolicitacao?acao=aprovarDocumentacao&telaEdicao=true&ADE_CODIGO=" + adeCodigo;
                            final String descricao = ApplicationResourcesHelper.getMessage("mensagem.aprovar.documentacao.solicitacao", responsavel);
                            final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.aprovar.documentacao.solicitacao.clique.aqui", responsavel);
                            final String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.aprovar.documentacao.solicitacao.confirma", responsavel);

                            acoes.add(new AcaoConsignacao("APROVAR_ANEXO_CONSIGNACAO", CodedValues.FUN_APROV_REJCT_ANEXO_SOLICITACAO, descricao, "attachment_approve.png", "btnAprvAnexoConsignacao", msgAlternativa, msgConfirmacao, null, link, null));
                        }
                    }

                    // Reprovar Anexo de solicitação
                    if (CodedValues.SAD_SOLICITADO.equals(sadCodigo) && responsavel.temPermissao(CodedValues.FUN_APROV_REJCT_ANEXO_SOLICITACAO) && exigeAssinaturaDigital && responsavel.isCsa() && !isOrigemLeilao) {

                        final List<String> tisCodigos = new ArrayList<>();
                        tisCodigos.add(TipoSolicitacaoEnum.SOLICITACAO_CONSIGNACAO_CREDITO_ELETRONICO.getCodigo());
                        final List<String> ssoCodigos = new ArrayList<>();
                        ssoCodigos.add(StatusSolicitacaoEnum.PENDENTE_VALIDACAO_DOCUMENTOS.getCodigo());

                        final List<TransferObject> solicitacoes = simulacaoController.lstRegistrosSolicitacaoAutorizacao(adeCodigo, tisCodigos, ssoCodigos, responsavel);
                        if ((solicitacoes != null) && !solicitacoes.isEmpty()) {
                            final String link = "../v3/listarSolicitacao?acao=reprovarDocumentacao&telaEdicao=true&ADE_CODIGO=" + adeCodigo;
                            final String descricao = ApplicationResourcesHelper.getMessage("mensagem.reprovar.documentacao.solicitacao", responsavel);
                            final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.reprovar.documentacao.solicitacao.clique.aqui", responsavel);
                            final String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.reprovar.documentacao.solicitacao.confirma", responsavel);

                            acoes.add(new AcaoConsignacao("REPROVAR_ANEXO_CONSIGNACAO", CodedValues.FUN_APROV_REJCT_ANEXO_SOLICITACAO, descricao, "attachment_disapprove.png", "btnReprovarAnexoConsignacao", msgAlternativa, msgConfirmacao, null, link, null));
                        }
                    }

                    // Registrar informação após leilão encerrado ou cancelado
                    if (informaMotivoNaoConcretizacaoLeilao) {
                        final String link = "../v3/registrarOcorrenciaConsignacao?acao=iniciarRegistroLeilao";
                        final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.informar.motivo.nao.concretizacao.leilao", responsavel);
                        final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.informar.motivo.nao.concretizacao.leilao.clique.aqui", responsavel);
                        final String msgConfirmacao = "";

                        acoes.add(new AcaoConsignacao("MOTIVO_NAO_CONCRETIZACAO_LEILAO", CodedValues.FUN_MOTIVO_NAO_CONCRETIZACAO_LEILAO, descricao, "tag_blue.png", "btnMotivoNaoConcretizacaoLeilao", msgAlternativa, msgConfirmacao, null, link, null));
                    }
                }

                // Exibir Boleto Quitação Saldo Devedor
                if (exibirIconeBoletoSdv) {
                    final String link = "";
                    final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.boleto.saldo.devedor", responsavel);
                    final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.boleto.saldo.devedor.clique.aqui", responsavel);
                    final String msgConfirmacao = "";

                    acoes.add(new AcaoConsignacao("BOLETO_SALDO_DEVEDOR", null, descricao, "boleto_sdv.gif", "btnBoletoSaldoDevedor", msgAlternativa, msgConfirmacao, null, link, null));
                }

                // Editar fluxo de parcelas de uma consignação
                if (!CodedValues.SAD_CODIGOS_SUSPENSOS.contains(sadCodigo) && !CodedValues.SAD_CODIGOS_INATIVOS.contains(sadCodigo) && responsavel.temPermissao(CodedValues.FUN_EDITAR_FLUXO_PARCELAS) && ParamSist.paramEquals(CodedValues.TPC_PERMITE_EDITAR_FLUXO_PARCELAS_CONSIGNACAO, CodedValues.TPC_SIM, responsavel)) {
                    final String link = "../v3/editarFluxoParcelas?acao=editar";
                    final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.editar.fluxo.parcelas", responsavel);
                    final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.acao.editar.fluxo.parcelas.clique.aqui", responsavel);
                    final String msgConfirmacao = "";

                    acoes.add(new AcaoConsignacao("EDITAR_FLUXO_PARCELAS", CodedValues.FUN_EDITAR_FLUXO_PARCELAS, descricao, "liquidar_parcela.gif", "btnEditarFluxoParcelas", msgAlternativa, msgConfirmacao, null, link, null));

                }

                // Rever leilão não concretizado
                if (CodedValues.SAD_CANCELADA.equals(sadCodigo) && responsavel.isCseSup() && responsavel.temPermissao(CodedValues.FUN_REVER_LEILAO_NAO_CONCRETIZADO) && leilaoSolicitacaoController.podeReverPontuacaoLeilao(adeCodigo, responsavel)) {

                    final String link = "../v3/reverterPontuacaoLeilao?acao=efetivarAcao";
                    final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.rever.leilao.nao.concretizado", responsavel);
                    final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.acao.rever.leilao.nao.concretizado.clique.aqui", responsavel);
                    final String msgConfirmacao = "";

                    acoes.add(new AcaoConsignacao("REVER_LEILAO_NAO_CONCRETIZADO", CodedValues.FUN_REVER_LEILAO_NAO_CONCRETIZADO, descricao, "attach.png", "btnReverLeilaoNaoConcretizado", msgAlternativa, msgConfirmacao, null, link, null));
                }

                // Solicitar Liquidação de Consignação
                if ((CodedValues.SAD_DEFERIDA.equals(sadCodigo) || CodedValues.SAD_EMANDAMENTO.equals(sadCodigo)) && responsavel.isCseSupOrg() && responsavel.temPermissao(CodedValues.FUN_SOLICITAR_LIQUIDAR_CONSIGNACAO)) {

                    final String link = "../v3/solicitarLiquidacao?acao=efetivarAcao";
                    final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.solicitar.liquidacao.consignacao", responsavel);
                    final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.acao.solicitar.liquidacao.consignacao.clique.aqui", responsavel);
                    final String msgConfirmacao = "";

                    acoes.add(new AcaoConsignacao("SOLICITAR_LIQUIDAR_CONSIGNACAO", CodedValues.FUN_SOLICITAR_LIQUIDAR_CONSIGNACAO, descricao, "liquidar_contrato.gif", "btnSolicitarLiquidarConsignacao", msgAlternativa, msgConfirmacao, null, link, null));
                }

                // Cancelar Solicitação Liquidação de Consignação
                final Collection<OcorrenciaAutorizacao> ocaSolicitacaoLiquidacao = autorizacaoController.findByAdeTocCodigo(adeCodigo, CodedValues.TOC_SOLICITAR_LIQUIDACAO_CONSIGNACAO, responsavel);
                if (CodedValues.SAD_AGUARD_LIQUIDACAO.equals(sadCodigo) && (ocaSolicitacaoLiquidacao != null) && !ocaSolicitacaoLiquidacao.isEmpty() && responsavel.temPermissao(CodedValues.FUN_CANC_SOLICITAR_LIQUIDAR_CONSIGNACAO)) {

                    final String link = "../v3/solicitarLiquidacao?acao=efetivarCancelamentoSolicitacao";
                    final String descricao = ApplicationResourcesHelper.getMessage("mensagem.acao.cancelar.solicitar.liquidacao.consignacao", responsavel);
                    final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.acao.cancelar.solicitar.liquidacao.consignacao.clique.aqui", responsavel);
                    final String msgConfirmacao = "";

                    acoes.add(new AcaoConsignacao("CANC_SOLICITACAO_LIQUIDACAO", CodedValues.FUN_CANC_SOLICITAR_LIQUIDAR_CONSIGNACAO, descricao, "cancelar.gif", "btnCancelarSolicitacaoLiquidacao", msgAlternativa, msgConfirmacao, null, link, null));
                }

                if (responsavel.isSer() && responsavel.temPermissao(CodedValues.FUN_EDITAR_COMUNICACAO)) {
                    final String link = "../v3/enviarComunicacao?acao=enviar&RSE_CODIGO=" + rseCodigo + "&CSA_CODIGO=" + csaCodigo + "&ADE_CODIGO=" + adeCodigo;
                    final String descricao = ApplicationResourcesHelper.getMessage("rotulo.acao.criar.comunicacao.consignataria.consignacao", responsavel);
                    final String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.acao.criar.comunicacao.consignataria.consignacao.clique.aqui", responsavel);
                    final String msgConfirmacao = "";

                    acoes.add(new AcaoConsignacao("CRIAR_COMUNICACAO_CONSIGNACAO", CodedValues.FUN_EDITAR_COMUNICACAO, descricao, "", "btnCriarComunicacaoConsignacao", msgAlternativa, msgConfirmacao, null, link, null));
                }


            }
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        return acoes;
    }

    private String verificarInformacoesBancarias(CustomTransferObject autdes, ParamSvcTO paramSvcCse, AcessoSistema responsavel) {
        if (paramSvcCse.isTpsInfBancariaObrigatoria()) {
            final String rseBanco = autdes.getAttribute(Columns.RSE_BANCO_SAL) != null ? TextHelper.formataParaComparacao(autdes.getAttribute(Columns.RSE_BANCO_SAL).toString()) : "";
            final String rseAgencia = autdes.getAttribute(Columns.RSE_AGENCIA_SAL) != null ? TextHelper.formataParaComparacao(autdes.getAttribute(Columns.RSE_AGENCIA_SAL).toString()) : "";
            final String rseConta = autdes.getAttribute(Columns.RSE_CONTA_SAL) != null ? TextHelper.formataParaComparacao(autdes.getAttribute(Columns.RSE_CONTA_SAL).toString()) : "";
            final String rseBancoAlt = autdes.getAttribute(Columns.RSE_BANCO_SAL_2) != null ? TextHelper.formataParaComparacao(autdes.getAttribute(Columns.RSE_BANCO_SAL_2).toString()) : "";
            final String rseAgenciaAlt = autdes.getAttribute(Columns.RSE_AGENCIA_SAL_2) != null ? TextHelper.formataParaComparacao(autdes.getAttribute(Columns.RSE_AGENCIA_SAL_2).toString()) : "";
            final String rseContaAlt = autdes.getAttribute(Columns.RSE_CONTA_SAL_2) != null ? TextHelper.formataParaComparacao(autdes.getAttribute(Columns.RSE_CONTA_SAL_2).toString()) : "";

            // Somente mostra mensagem se houver informações bancárias cadastradas para o (registro) servidor.
            if ((!TextHelper.isNull(rseBanco) && !TextHelper.isNull(rseAgencia) && !TextHelper.isNull(rseConta)) || (!TextHelper.isNull(rseBancoAlt) && !TextHelper.isNull(rseAgenciaAlt) && !TextHelper.isNull(rseContaAlt))) {
                final String adeBanco = autdes.getAttribute(Columns.ADE_BANCO) != null ? TextHelper.formataParaComparacao(autdes.getAttribute(Columns.ADE_BANCO).toString()) : "";
                final String adeAgencia = autdes.getAttribute(Columns.ADE_AGENCIA) != null ? TextHelper.formataParaComparacao(autdes.getAttribute(Columns.ADE_AGENCIA).toString()) : "";
                final String adeConta = autdes.getAttribute(Columns.ADE_CONTA) != null ? TextHelper.formataParaComparacao(autdes.getAttribute(Columns.ADE_CONTA).toString()) : "";

                if ((adeBanco.equals(rseBanco) && adeAgencia.equals(rseAgencia) && adeConta.equals(rseConta)) || (adeBanco.equals(rseBancoAlt) && adeAgencia.equals(rseAgenciaAlt) && adeConta.equals(rseContaAlt))) {
                    return JspHelper.msgGenerica(ApplicationResourcesHelper.getMessage("mensagem.informacaoBancariaCorreta", responsavel), "100%", CodedValues.MSG_INFO);
                } else {
                    return JspHelper.msgGenerica(ApplicationResourcesHelper.getMessage("mensagem.informacaoBancariaIncorreta", responsavel), "100%", CodedValues.MSG_ALERT);
                }
            }
        }
        return null;
    }

    protected String verificarPossibilidadePermitirDuplicidade(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel, String cnvCodigo, String proximaOperacao, AutorizacaoControllerException ex) throws AutorizacaoControllerException {
        if (autorizacaoController.podePermitirDuplicidadeMotivadaUsuario(cnvCodigo, responsavel)) {
            request.setAttribute("proximaOperacao", proximaOperacao);
            return viewRedirect("jsp/reservarMargem/confirmarDuplicidade", request, session, model, responsavel);
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = {"acao=confirmarDuplicidade"})
    public String confirmarDuplicidade(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServidorControllerException, ParametroControllerException {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final String chkConfirmarDuplicidade = request.getParameter("chkConfirmarDuplicidade");
        if (TextHelper.isNull(chkConfirmarDuplicidade)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.alerta.ade.duplicidade.marcar.checkbox", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String tmoCodigo = request.getParameter("TMO_CODIGO");
        if (TextHelper.isNull(tmoCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.alerta.ade.duplicidade.motivo", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String adeObs = request.getParameter("ADE_OBS");
        if (TextHelper.isNull(adeObs)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.alerta.ade.duplicidade.observacao", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return executarFuncaoAposDuplicidade(request, response, session, model);
    }

    protected String executarFuncaoAposDuplicidade(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServidorControllerException, ParametroControllerException {
        return null;
    }

    private CustomTransferObject verificarExibeDataCancelamento(CustomTransferObject autDes, AcessoSistema responsavel) {
        try {
            final String sadCodigo = (String) autDes.getAttribute(Columns.ADE_SAD_CODIGO);
            if (CodedValues.SAD_SOLICITADO.equals(sadCodigo) || CodedValues.SAD_AGUARD_CONF.equals(sadCodigo) || CodedValues.SAD_AGUARD_DEFER.equals(sadCodigo)) {

                final boolean isDiasUteis = ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_CANC_AUTOMATICO_ADE, CodedValues.TPC_SIM, responsavel);
                final String adeCodigo = (String) autDes.getAttribute(Columns.ADE_CODIGO);
                final Date adeData = (Date) autDes.getAttribute(Columns.ADE_DATA);

                Date dataPrevistaCancelamento = null;

                if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                    final String qtdDiasConcretizarLeilaoParam = (String) ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_PARA_SER_CONCRETIZAR_LEILAO, responsavel);
                    final int qtdDiasConcretizarLeilao = TextHelper.isNum(qtdDiasConcretizarLeilaoParam) ? Integer.parseInt(qtdDiasConcretizarLeilaoParam) : 0;

                    if (qtdDiasConcretizarLeilao > 0) {
                        final boolean contratoDestinoLeilao = pesquisarConsignacaoController.isDestinoRelacionamento(adeCodigo, CodedValues.TNT_LEILAO_SOLICITACAO);
                        if (contratoDestinoLeilao) {
                            dataPrevistaCancelamento = isDiasUteis ? calendarioController.findProximoDiaUtil(adeData, qtdDiasConcretizarLeilao) : DateHelper.addDays(adeData, qtdDiasConcretizarLeilao);
                        }
                    }
                }

                if (dataPrevistaCancelamento == null) {
                    final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO((String) autDes.getAttribute(Columns.SVC_CODIGO), responsavel);
                    final String diasReservaNaoConfirmada = paramSvcCse.getTpsDiasDesblResNaoConf();
                    final String diasDesblNaoDef = paramSvcCse.getTpsDiasDesblConsigNaoDef();
                    final String diasSolicitacaoNaoConfirmada = paramSvcCse.getTpsDiasDesblSolicitacaoNaoConf();

                    if (CodedValues.SAD_SOLICITADO.equals(sadCodigo) && !TextHelper.isNull(diasSolicitacaoNaoConfirmada)) {
                        dataPrevistaCancelamento = isDiasUteis ? calendarioController.findProximoDiaUtil(adeData, Integer.valueOf(diasSolicitacaoNaoConfirmada)) : DateHelper.addDays(adeData, Integer.parseInt(diasSolicitacaoNaoConfirmada));
                    } else if (CodedValues.SAD_AGUARD_CONF.equals(sadCodigo) && !TextHelper.isNull(diasReservaNaoConfirmada)) {
                        dataPrevistaCancelamento = isDiasUteis ? calendarioController.findProximoDiaUtil(adeData, Integer.valueOf(diasReservaNaoConfirmada)) : DateHelper.addDays(adeData, Integer.parseInt(diasReservaNaoConfirmada));
                    } else if (CodedValues.SAD_AGUARD_DEFER.equals(sadCodigo) && !TextHelper.isNull(diasDesblNaoDef)) {
                        dataPrevistaCancelamento = isDiasUteis ? calendarioController.findProximoDiaUtil(adeData, Integer.valueOf(diasDesblNaoDef)) : DateHelper.addDays(adeData, Integer.parseInt(diasDesblNaoDef));
                    }
                }

                autDes.setAttribute("dataPrevistaCancelamento", dataPrevistaCancelamento);
            }
        } catch (ParametroControllerException | CalendarioControllerException | AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return autDes;
    }

    protected String tratarConsignacaoNaoEncontrada(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return iniciar(request, response, session, model);
    }

    protected List<ColunaListaConsignacao> definirColunasListaConsignacaoSer(HttpServletRequest request, AcessoSistema responsavel) {
        final List<ColunaListaConsignacao> colunas = new ArrayList<>();

        colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_SER_CONSIGNATARIA, ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel)));

        colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_SER_RESPONSAVEL, ApplicationResourcesHelper.getMessage("rotulo.consignacao.responsavel", responsavel)));

        colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_SER_NUMERO, ApplicationResourcesHelper.getMessage("rotulo.consignacao.numero", responsavel), ColunaListaConsignacao.TipoValor.NUMERICO));

        colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_SER_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.consignacao.identificador", responsavel)));

        colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_SER_SERVICO, ApplicationResourcesHelper.getMessage("rotulo.servico.singular", responsavel)));

        colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_SER_DATA_RESERVA, ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.inclusao", responsavel), ColunaListaConsignacao.TipoValor.DATA));

        colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_SER_VALOR_PARCELA, ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela.abreviado", responsavel), ColunaListaConsignacao.TipoValor.MONETARIO));

        colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_SER_VALOR_FOLHA, ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.folha.abreviado", responsavel), ColunaListaConsignacao.TipoValor.MONETARIO));

        colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_SER_PRAZO, ApplicationResourcesHelper.getMessage("rotulo.consignacao.prazo.abreviado", responsavel), ColunaListaConsignacao.TipoValor.NUMERICO));

        colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_SER_PARCELAS_PAGAS, ApplicationResourcesHelper.getMessage("rotulo.consignacao.pagas", responsavel), ColunaListaConsignacao.TipoValor.NUMERICO));

        colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_SER_CAPITAL_DEVIDO, ApplicationResourcesHelper.getMessage("rotulo.consignacao.capital.devido", responsavel), ColunaListaConsignacao.TipoValor.NUMERICO));

        colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_SER_CARENCIA, ApplicationResourcesHelper.getMessage("rotulo.consignacao.carencia", responsavel), ColunaListaConsignacao.TipoValor.NUMERICO));

        colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_SER_STATUS, ApplicationResourcesHelper.getMessage("rotulo.consignacao.status", responsavel)));


        return colunas;
    }

    protected List<TransferObject> formatarValoresListaConsignacaoSer(List<TransferObject> lstConsignacao, List<ColunaListaConsignacao> colunas, HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        for (TransferObject ade : lstConsignacao) {
            ade = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) ade, null, responsavel);

            for (final ColunaListaConsignacao coluna : colunas) {
                final String chaveCampo = coluna.getChaveCampo();
                String valorCampo = "";

                if (FieldKeysConstants.LISTA_CONSIGNACAO_SER_CONSIGNATARIA.equals(chaveCampo)) {
                    valorCampo = ade.getAttribute(Columns.CSA_IDENTIFICADOR) + " - " + (!TextHelper.isNull(ade.getAttribute(Columns.CSA_NOME_ABREV)) ? ade.getAttribute(Columns.CSA_NOME_ABREV).toString() : ade.getAttribute(Columns.CSA_NOME).toString());
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_SER_RESPONSAVEL.equals(chaveCampo)) {
                    valorCampo = ade.getAttribute(Columns.USU_LOGIN) != null ? ade.getAttribute(Columns.USU_LOGIN).toString() : "";
                    valorCampo = valorCampo.equalsIgnoreCase((String) ade.getAttribute(Columns.USU_CODIGO)) && (ade.getAttribute(Columns.USU_TIPO_BLOQ) != null) ? ade.getAttribute(Columns.USU_TIPO_BLOQ).toString() + "(*)" : valorCampo;
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_SER_NUMERO.equals(chaveCampo)) {
                    valorCampo = ade.getAttribute(Columns.ADE_NUMERO).toString();
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_SER_IDENTIFICADOR.equals(chaveCampo)) {
                    if (responsavel.isCseSup() || (responsavel.isCsaCor() && responsavel.getCsaCodigo().equals(ade.getAttribute(Columns.CSA_CODIGO)))) {
                        valorCampo = ade.getAttribute(Columns.ADE_IDENTIFICADOR) != null ? ade.getAttribute(Columns.ADE_IDENTIFICADOR).toString() : "";
                    } else {
                        valorCampo = "";
                    }
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_SER_SERVICO.equals(chaveCampo)) {
                    final String adeCodReg = (ade.getAttribute(Columns.ADE_COD_REG) != null) && !"".equals(ade.getAttribute(Columns.ADE_COD_REG)) ? ade.getAttribute(Columns.ADE_COD_REG).toString() : CodedValues.COD_REG_DESCONTO;

                    valorCampo = (!TextHelper.isNull(ade.getAttribute(Columns.CNV_COD_VERBA)) ? ade.getAttribute(Columns.CNV_COD_VERBA).toString() : ade.getAttribute(Columns.SVC_IDENTIFICADOR).toString()) + (!TextHelper.isNull(ade.getAttribute(Columns.ADE_INDICE)) ? ade.getAttribute(Columns.ADE_INDICE).toString() : "") + " - " + ade.getAttribute(Columns.SVC_DESCRICAO).toString() + (CodedValues.COD_REG_ESTORNO.equals(adeCodReg) ? " - " + ApplicationResourcesHelper.getMessage("rotulo.consignacao.cod.reg.credito", responsavel) : "");
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_SER_DATA_RESERVA.equals(chaveCampo)) {
                    try {
                        valorCampo = DateHelper.reformat(ade.getAttribute(Columns.ADE_DATA).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
                    } catch (final ParseException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_SER_VALOR_PARCELA.equals(chaveCampo)) {
                    try {
                        valorCampo = !TextHelper.isNull(ade.getAttribute(Columns.ADE_VLR)) ? NumberHelper.reformat(ade.getAttribute(Columns.ADE_VLR).toString(), "en", NumberHelper.getLang()) : "";
                        ade.setAttribute(chaveCampo + "_SIMBOLO", ParamSvcTO.getDescricaoTpsTipoVlr((String) ade.getAttribute(Columns.ADE_TIPO_VLR)));
                    } catch (final ParseException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_SER_VALOR_FOLHA.equals(chaveCampo)) {
                    try {
                        valorCampo = !TextHelper.isNull(ade.getAttribute(Columns.ADE_VLR_FOLHA)) ? NumberHelper.reformat(ade.getAttribute(Columns.ADE_VLR_FOLHA).toString(), "en", NumberHelper.getLang()) : "";
                    } catch (final ParseException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_SER_PRAZO.equals(chaveCampo)) {
                    if (!TextHelper.isNull(ade.getAttribute(Columns.ADE_PRAZO))) {
                        valorCampo = ade.getAttribute(Columns.ADE_PRAZO).toString();
                    } else {
                        valorCampo = ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel);
                        ade.setAttribute(chaveCampo + "_ABREVIATURA", ApplicationResourcesHelper.getMessage("rotulo.indeterminado.abreviado", responsavel));
                    }
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_SER_PARCELAS_PAGAS.equals(chaveCampo)) {
                    valorCampo = ade.getAttribute(Columns.ADE_PRD_PAGAS) != null ? ade.getAttribute(Columns.ADE_PRD_PAGAS).toString() : "0";
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_SER_CAPITAL_DEVIDO.equals(chaveCampo) && ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPITAL_DEVIDO, CodedValues.TPC_SIM, responsavel)) {
                    // Se prazo e valor não são nulos, e o tipo da consignação é de valor Fixo ou de Total de Margem, calcula o capital devido
                    if ((ade.getAttribute(Columns.ADE_PRAZO) != null) && (ade.getAttribute(Columns.ADE_VLR) != null) && (CodedValues.TIPO_VLR_FIXO.equals(ade.getAttribute(Columns.ADE_TIPO_VLR)) || CodedValues.TIPO_VLR_TOTAL_MARGEM.equals(ade.getAttribute(Columns.ADE_TIPO_VLR)))) {
                        final int prazoRestante = (Integer) ade.getAttribute(Columns.ADE_PRAZO) - (ade.getAttribute(Columns.ADE_PRD_PAGAS) != null ? (Integer) ade.getAttribute(Columns.ADE_PRD_PAGAS) : 0);
                        if (prazoRestante >= 0) {
                            final double valorParcela = ((java.math.BigDecimal) (ade.getAttribute(Columns.ADE_VLR_PARCELA_FOLHA) != null ? ade.getAttribute(Columns.ADE_VLR_PARCELA_FOLHA) : ade.getAttribute(Columns.ADE_VLR))).doubleValue();
                            final double valorCapitalDevido = valorParcela * prazoRestante;
                            valorCampo = NumberHelper.format(valorCapitalDevido, NumberHelper.getLang());
                            if (ade.getAttribute(Columns.ADE_VLR_PARCELA_FOLHA) != null) {
                                valorCampo += " (*)";
                            }
                        }
                    }
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_SER_STATUS.equals(chaveCampo)) {
                    valorCampo = ade.getAttribute(Columns.SAD_DESCRICAO).toString();
                    if ((ade.getAttribute(Columns.ADE_DATA_STATUS) != null) && !ParamSist.paramEquals(CodedValues.TPC_EXIBE_ADE_DATA_STATUS_CONSULTAR_CONSIGNACAO, CodedValues.TPC_NAO, responsavel)) {
                        final String dataAtualizacao = DateHelper.toDateTimeString((Date) ade.getAttribute(Columns.ADE_DATA_STATUS));
                        valorCampo = String.format("%s (%s)", valorCampo, dataAtualizacao);
                    }
                }
                if (FieldKeysConstants.LISTA_CONSIGNACAO_SER_CARENCIA.equals(chaveCampo)) {
                    valorCampo = ade.getAttribute(Columns.ADE_CARENCIA) != null ? ade.getAttribute(Columns.ADE_CARENCIA).toString() : "0";
                }

                ade.setAttribute(chaveCampo, valorCampo);
            }
        }
        return lstConsignacao;
    }

    private void ordenarListaConsignacao(List<TransferObject> lstConsignacao) {
        Collections.sort(lstConsignacao, (o1, o2) -> {
            final Integer svcPrioridade1 = (Integer) o1.getAttribute("SVC_PRIORIDADE");
            final Integer cnvPrioridade1 = (Integer) o1.getAttribute("CNV_PRIORIDADE");
            final Date adeAnoMesIniPrioridade1 = (Date) o1.getAttribute("ADE_ANO_MES_INI_PRIORIDADE");
            final Date adeDataPrioridade1 = (Date) o1.getAttribute("ADE_DATA_PRIORIDADE");
            final Long adeNumeroPrioridade1 = (Long) o1.getAttribute("ADE_NUMERO_PRIORIDADE");

            final Integer svcPrioridade2 = (Integer) o2.getAttribute("SVC_PRIORIDADE");
            final Integer cnvPrioridade2 = (Integer) o2.getAttribute("CNV_PRIORIDADE");
            final Date adeAnoMesIniPrioridade2 = (Date) o2.getAttribute("ADE_ANO_MES_INI_PRIORIDADE");
            final Date adeDataPrioridade2 = (Date) o2.getAttribute("ADE_DATA_PRIORIDADE");
            final Long adeNumeroPrioridade2 = (Long) o2.getAttribute("ADE_NUMERO_PRIORIDADE");
            return new CompareToBuilder().append(svcPrioridade1, svcPrioridade2).append(cnvPrioridade1, cnvPrioridade2).append(adeAnoMesIniPrioridade1, adeAnoMesIniPrioridade2)
                    .append(adeDataPrioridade1, adeDataPrioridade2).append(adeNumeroPrioridade1, adeNumeroPrioridade2).toComparison();
        });

        int prioridade = 1;
        for (final TransferObject consignacao : lstConsignacao) {
            consignacao.setAttribute("PRIORIDADE", prioridade++);
        }
    }
}
