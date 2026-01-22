package com.zetra.econsig.web.controller.cadastrarretorno;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.values.Columns;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParcelaDescontoTO;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.parcela.ParcelaController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: CadastrarRetornoIntegracaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso cadastrar retorno Integração</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = {RequestMethod.POST}, value = {"/v3/cadastrarRetornoIntegracao"})
public class CadastrarRetornoIntegracaoWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CadastrarRetornoIntegracaoWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ParcelaController parcelaController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private SistemaController sistemaController;

    @RequestMapping(params = {"acao=iniciar"})
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);

        List<TransferObject> consignatarias = null;
        List<TransferObject> orgaos = null;
        List<TransferObject> lstTipoOcorrencia = null;
        boolean requerMatriculaCpf = false;
        try {
            orgaos = consignanteController.lstOrgaos(null, responsavel);
            consignatarias = convenioController.getCsaCnvAtivo(null, responsavel.getOrgCodigo(), responsavel);

            List<String> tocCodigos = new ArrayList<>();
            tocCodigos.add(CodedValues.TOC_RETORNO);
            tocCodigos.add(CodedValues.TOC_RETORNO_PARCIAL);
            tocCodigos.add(CodedValues.TOC_RETORNO_FERIAS);
            tocCodigos.add(CodedValues.TOC_RETORNO_PARCIAL_FERIAS);
            tocCodigos.add(CodedValues.TOC_RETORNO_PARCELA_NAO_EXPORTADA);
            tocCodigos.add(CodedValues.TOC_RETORNO_PARCELA_SEM_RETORNO);

            lstTipoOcorrencia = sistemaController.lstTipoOcorrencia(tocCodigos, responsavel);

            requerMatriculaCpf = parametroController.requerMatriculaCpf(responsavel);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            consignatarias = new ArrayList<>();
        }

        model.addAttribute("requerMatriculaCpf", requerMatriculaCpf);
        model.addAttribute("consignatarias", consignatarias);
        model.addAttribute("orgaos", orgaos);
        model.addAttribute("lstTipoOcorrencia", lstTipoOcorrencia);

        return viewRedirect("jsp/cadastrarRetornoIntegracao/pesquisarRetorno", request, session, model, responsavel);
    }

    @RequestMapping(params = {"acao=listarIntegracao"})
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String operacao = JspHelper.verificaVarQryStr(request, "operacao");

        if (TextHelper.isNull(operacao) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        ParamSession paramSession = ParamSession.getParamSession(session);

        if (operacao.equals("R") || operacao.equals("L")) {
            return editar(request, response, session, model);
        } else {
            String tipoEntidade = null;
            if (responsavel.isCseSup()) {
                tipoEntidade = AcessoSistema.ENTIDADE_CSE;
            } else if (responsavel.isOrg()) {
                if (responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                    tipoEntidade = AcessoSistema.ENTIDADE_EST;
                } else {
                    tipoEntidade = AcessoSistema.ENTIDADE_ORG;
                }
            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            model.addAttribute("tipoEntidade", tipoEntidade);

            String adeNumero = JspHelper.verificaVarQryStr(request, "ADE_NUMERO");
            String adeIdentificador = JspHelper.verificaVarQryStr(request, "ADE_IDENTIFICADOR");
            String rseMatricula = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA");
            String serCpf = JspHelper.verificaVarQryStr(request, "SER_CPF");
            String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
            boolean integradas = JspHelper.verificaVarQryStr(request, "PESQUISA").equals("INTEGRADAS");
            String rotuloMatricula = ApplicationResourcesHelper.getMessage("rotulo.servidor.matricula", responsavel);
            String orgCodigo = JspHelper.verificaVarQryStr(request, "ORG_CODIGO");
            String situacao = JspHelper.verificaVarQryStr(request, "SITUACAO");
            String papel = JspHelper.verificaVarQryStr(request, "PAPEL");
            String tocCodigo = JspHelper.verificaVarQryStr(request, "TOC_CODIGO");
            boolean filtros = false;

            boolean camposObrigatoriosOk = validarCamposObrigatorios(adeIdentificador, orgCodigo, tocCodigo, responsavel);
            if (!camposObrigatoriosOk) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.folha.campos.obrigatorios", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (TextHelper.isNull(orgCodigo)) {
                orgCodigo = (responsavel.isOrg()) ? responsavel.getCodigoEntidade() : null;
            }

            String csaNome = JspHelper.verificaVarQryStr(request, "CSA_NOME");

            TransferObject criterio = new CustomTransferObject();
            String periodoIni = JspHelper.verificaVarQryStr(request, "periodoIni");
            try {
                if (!periodoIni.isEmpty()) {
                    Date periodoIniDate = DateHelper.parsePeriodString(periodoIni);
                    criterio.setAttribute("periodoIni", periodoIniDate);
                    model.addAttribute("periodoIni", periodoIni);
                    filtros = true;
                }
            } catch (ParseException ex) {
                LOG.error(ex.getMessage(), ex);
            }
            String periodoFim = JspHelper.verificaVarQryStr(request, "periodoFim");
            try {
                if (!periodoFim.isEmpty()) {
                    Date periodoFimDate = DateHelper.parsePeriodString(periodoFim);
                    criterio.setAttribute("periodoFim", periodoFimDate);
                    model.addAttribute("periodoFim", periodoFim);
                    filtros = true;
                }
            } catch (ParseException ex) {
                LOG.error(ex.getMessage(), ex);
            }
            criterio.setAttribute("adeIdentificador", adeIdentificador);

            if (!adeIdentificador.isEmpty()) {
                model.addAttribute("adeIdentificador", adeIdentificador);
                filtros = true;
            }

            try {
                if (!JspHelper.verificaVarQryStr(request, "operacao").isEmpty()) {
                    String[] codigos = request.getParameterValues("PARCELA");
                    String spdCodigo = (JspHelper.verificaVarQryStr(request, "operacao").equals("liquidar")) ? CodedValues.SPD_LIQUIDADAFOLHA : CodedValues.SPD_REJEITADAFOLHA;
                    if (codigos != null) {
                        for (String codigo : codigos) {
                            String adeCodigo = codigo.split(";")[0];
                            Integer prdCodigo = Integer.valueOf(codigo.split(";")[1]);
                            // String adeCodigo, Integer prdCodigo, BigDecimal prdVlrRealizado, String spdCodigo, String ocpMotivo, AcessoSistema responsavel
                            parcelaController.integrarParcela(adeCodigo, prdCodigo, null, spdCodigo, null, responsavel);
                        }
                        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.folha.concluida.sucesso", responsavel));
                    }
                }
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            List<TransferObject> parcelas = null;
            int offset = 0;
            int size = JspHelper.LIMITE;

            List<String> spdCodigos = new ArrayList<>();
            List<String> papCodigos = new ArrayList<>();
            List<String> tocCodigos = new ArrayList<>();
            if (integradas) {
                //preenche o campo de situacao
                if (TextHelper.isNull(situacao) || situacao.isEmpty()) {
                    spdCodigos.add(CodedValues.SPD_REJEITADAFOLHA);
                    spdCodigos.add(CodedValues.SPD_LIQUIDADAFOLHA);
                    spdCodigos.add(CodedValues.SPD_LIQUIDADAMANUAL);
                } else {
                    spdCodigos.add(situacao);
                    model.addAttribute("situacao", situacao);
                    filtros = true;
                }
                //preenche o campo de papel
                if (TextHelper.isNull(papel) || papel.isEmpty()) {
                    papCodigos.add(CodedValues.PAP_CONSIGNANTE);
                    papCodigos.add(CodedValues.PAP_CONSIGNATARIA);
                    papCodigos.add(CodedValues.PAP_ORGAO);
                    papCodigos.add(CodedValues.PAP_CORRESPONDENTE);
                    papCodigos.add(CodedValues.PAP_SUPORTE);
                } else {
                    papCodigos.add(papel);
                    model.addAttribute("papel", papel);
                    filtros = true;
                }
                //preenche o campo tipo ocorrência
                if (TextHelper.isNull(tocCodigo) || tocCodigo.isEmpty()) {
                    tocCodigos.add(CodedValues.TOC_RETORNO);
                    tocCodigos.add(CodedValues.TOC_RETORNO_PARCIAL);
                    tocCodigos.add(CodedValues.TOC_RETORNO_FERIAS);
                    tocCodigos.add(CodedValues.TOC_RETORNO_PARCIAL_FERIAS);
                    tocCodigos.add(CodedValues.TOC_RETORNO_PARCELA_NAO_EXPORTADA);
                    tocCodigos.add(CodedValues.TOC_RETORNO_PARCELA_SEM_RETORNO);
                } else {
                    tocCodigos.add(tocCodigo);
                    model.addAttribute("tocCodigo", tocCodigo);
                    filtros = true;
                }
                criterio.setAttribute("tocCodigos", tocCodigos);
            } else {
                spdCodigos.add(CodedValues.SPD_EMPROCESSAMENTO);
                spdCodigos.add(CodedValues.SPD_SEM_RETORNO);
                papCodigos = new ArrayList<>();
            }

            if (!adeNumero.isEmpty() || !TextHelper.isNull(adeNumero)) {
                filtros = true;
            }

            if (!rseMatricula.isEmpty() || !TextHelper.isNull(rseMatricula)) {
                filtros = true;
            }

            try {
                int total = parcelaController.countParcelas(tipoEntidade, adeNumero, rseMatricula, serCpf, orgCodigo, csaCodigo, spdCodigos, papCodigos, criterio, responsavel);

                if ((total == 0) && (session.getAttribute(CodedValues.MSG_INFO) == null || session.getAttribute(CodedValues.MSG_INFO).equals(""))) {
                    // Redireciona de volta para a página de pesquisa
                    String msg = ApplicationResourcesHelper.getMessage("mensagem.folha.registro.nao.encontrado", responsavel);

                    if(!csaNome.isEmpty()){
                        msg += ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel) + ": <span class=\"normal\">" + csaNome + "</span>  ";
                    }

                    if(!periodoIni.isEmpty() || !periodoFim.isEmpty()){
                        msg += ApplicationResourcesHelper.getMessage("mensagem.folha.periodo.nao.encontrato", responsavel) + ": <span class=\"normal\">" + periodoIni + " a " + periodoFim + "</span>  ";
                    }

                    if (rseMatricula.isEmpty() && serCpf.isEmpty() && adeNumero.isEmpty() && periodoIni.isEmpty() && periodoFim.isEmpty() && csaNome.isEmpty()) {
                        msg += ApplicationResourcesHelper.getMessage("mensagem.nenhum.selecionado.listar.todos.retorno", responsavel);
                    }

                    if (!adeNumero.isEmpty()) {
                        msg += ApplicationResourcesHelper.getMessage("rotulo.folha.numero.ade", responsavel) + ": <span class=\"normal\">" + adeNumero + "</span>  ";
                    }

                    if (!rseMatricula.isEmpty()) {
                        msg += rotuloMatricula + " : <span class=\"normal\">" + rseMatricula + "</span>  ";
                    }

                    if (!serCpf.isEmpty()) {
                        msg += ApplicationResourcesHelper.getMessage("rotulo.folha.cpf", responsavel) + ": <span class=\"normal\">" + serCpf + "</span>  ";
                    }

                    session.setAttribute(CodedValues.MSG_ERRO, msg);
                    request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                    return "jsp/redirecionador/redirecionar";
                }

                try {
                    offset = Integer.parseInt(request.getParameter("offset"));
                } catch (Exception ex) {
                }

                if (total > 0) {
                    parcelas = parcelaController.getParcelas(tipoEntidade, adeNumero, rseMatricula, serCpf, orgCodigo, csaCodigo, spdCodigos, offset, size, papCodigos, criterio, responsavel);
                } else {
                    parcelas = new ArrayList<>();
                }

                List<String> listParams = Arrays.asList(new String[]{"RSE_MATRICULA", "SER_CPF", "ADE_NUMERO", "PESQUISA", "periodoIni", "periodoFim", "SITUACAO", "PAPEL", "CSA_CODIGO", "ORG_CODIGO", "TOC_CODIGO", "ADE_IDENTIFICADOR"});
                String linkListagem = "../v3/cadastrarRetornoIntegracao?acao=listarIntegracao";
                configurarPaginador(linkListagem, "rotulo.lst.arq.generico.titulo", total, size, listParams, false, request, model);
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                parcelas = new ArrayList<>();
            }
            model.addAttribute("orgCodigo", orgCodigo);
            model.addAttribute("filtros", filtros);
            model.addAttribute("parcelas", parcelas);
            model.addAttribute("offset", offset);
            model.addAttribute("size", size);
        }

        return viewRedirect("jsp/cadastrarRetornoIntegracao/listarIntegracao", request, session, model, responsavel);
    }

    @RequestMapping(params = {"acao=editarIntegracao"})
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String operacao = JspHelper.verificaVarQryStr(request, "operacao");

        if (TextHelper.isNull(operacao) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String tipoEntidade = AcessoSistema.ENTIDADE_CSE;
        String codigoEntidade = responsavel.getCodigoEntidade();
        ParamSession paramSession = ParamSession.getParamSession(session);
        String filtro = request.getParameter("fil");

        boolean isEst = (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO));

        if (isEst) {
            tipoEntidade = AcessoSistema.ENTIDADE_EST;
            codigoEntidade = responsavel.getCodigoEntidadePai();
        } else if (responsavel.isOrg()) {
            tipoEntidade = AcessoSistema.ENTIDADE_ORG;
        }

        String adeCodigo = JspHelper.verificaVarQryStr(request, "ade_codigo");
        Integer prdCodigo = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "prd_codigo")) ? Integer.valueOf(JspHelper.verificaVarQryStr(request, "prd_codigo")) : 0;

        boolean alterarParcela = JspHelper.verificaVarQryStr(request, "alterarParcela").equals("true");

        if (!operacao.isEmpty()) {
            try {
                final List<TransferObject> parcelas;
                final TransferObject criterio = new CustomTransferObject();
                final List<String> spdCodigos = new ArrayList<>();
                final List<String> papCodigos = new ArrayList<>();
                final List<String> tocCodigos = new ArrayList<>();

                final String adeNumero = JspHelper.verificaVarQryStr(request, "ADE_NUMERO");
                final String rseMatricula = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA");
                final String serCpf = JspHelper.verificaVarQryStr(request, "SER_CPF");
                final String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
                final String adeIdentificador = JspHelper.verificaVarQryStr(request, "adeIdentificador");
                final String periodoIni = JspHelper.verificaVarQryStr(request, "periodoIni");
                final String periodoFim = JspHelper.verificaVarQryStr(request, "periodoFim");
                String orgCodigo = JspHelper.verificaVarQryStr(request, "orgCodigo");

                // Integra a parcela
                if (operacao.equals("salvar")) {
                    String valor = JspHelper.verificaVarQryStr(request, "prd_vlr_realizado");
                    if (TextHelper.isNull(valor)) {
                        valor = "0.00";
                    }
                    valor = NumberHelper.reformat(valor, NumberHelper.getLang(), "en");
                    BigDecimal vlrRealizadoManual = new BigDecimal(valor);

                    String status = JspHelper.verificaVarQryStr(request, "status");
                    // Valida valor da parcela
                    if (!status.equals(CodedValues.SPD_REJEITADAFOLHA) && vlrRealizadoManual.signum() <= 0) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.folha.preencha.valor.parcela.ser.realizado", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                    String motivo = JspHelper.verificaVarQryStr(request, "motivo");
                    if (!alterarParcela) {
                        parcelaController.integrarParcela(adeCodigo, prdCodigo, vlrRealizadoManual, status, motivo, responsavel);
                    } else {
                        prdCodigo = parcelaController.desfazIntegracao(adeCodigo, prdCodigo, CodedValues.TOC_DESFEITO, ApplicationResourcesHelper.getMessage("mensagem.folha.intergracao.defeita.operador", responsavel), responsavel);
                        parcelaController.integrarParcela(adeCodigo, prdCodigo, vlrRealizadoManual, status, motivo, responsavel);
                    }
                    // Desfaz a integração
                } else if (operacao.equals("desfazer")) {
                    parcelaController.desfazIntegracao(adeCodigo, prdCodigo, CodedValues.TOC_DESFEITO, ApplicationResourcesHelper.getMessage("mensagem.folha.intergracao.defeita.operador", responsavel), responsavel);

                    // Liquida todas as parcelas
                } else if (operacao.equals("L")) {
                    if (filtro.equals("N")) {
                        parcelaController.liquidarTodasParcelas(tipoEntidade, codigoEntidade, responsavel);
                    } else {

                        if (TextHelper.isNull(orgCodigo) || orgCodigo.isEmpty()) {
                            orgCodigo = (responsavel.isOrg()) ? responsavel.getCodigoEntidade() : null;
                        }

                        if (!periodoIni.isEmpty()) {
                            Date periodoIniDate = DateHelper.parsePeriodString(periodoIni);
                            criterio.setAttribute("periodoIni", periodoIniDate);
                        }

                        if (!periodoFim.isEmpty()) {
                            Date periodoFimDate = DateHelper.parsePeriodString(periodoFim);
                            criterio.setAttribute("periodoFim", periodoFimDate);
                        }

                        spdCodigos.add(CodedValues.SPD_EMPROCESSAMENTO);
                        spdCodigos.add(CodedValues.SPD_SEM_RETORNO);

                        criterio.setAttribute("adeIdentificador", adeIdentificador);
                        criterio.setAttribute("tocCodigos", tocCodigos);

                        parcelas = parcelaController.getParcelas(tipoEntidade, adeNumero, rseMatricula, serCpf, orgCodigo, csaCodigo, spdCodigos, -1, -1, papCodigos, criterio, responsavel);

                        if (parcelas != null) {
                            for (TransferObject parc : parcelas) {
                                String adeCod = (String) parc.getAttribute(Columns.ADE_CODIGO);
                                Integer prdCod = (Integer) parc.getAttribute(Columns.PRD_CODIGO);
                                parcelaController.integrarParcela(adeCod, prdCod, null, CodedValues.SPD_LIQUIDADAFOLHA, null, responsavel);
                            }
                            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.folha.concluida.sucesso", responsavel));
                        }
                        return iniciar(request, response, session, model);
                    }
                    // Rejeita todas as parcelas
                } else if (operacao.equals("R")) {
                    if (filtro.equals("N")) {
                        parcelaController.rejeitarTodasParcelas(tipoEntidade, codigoEntidade, responsavel);
                    } else {

                        if (TextHelper.isNull(orgCodigo) || orgCodigo.isEmpty()) {
                            orgCodigo = (responsavel.isOrg()) ? responsavel.getCodigoEntidade() : null;
                        }

                        if (!periodoIni.isEmpty()) {
                            Date periodoIniDate = DateHelper.parsePeriodString(periodoIni);
                            criterio.setAttribute("periodoIni", periodoIniDate);
                        }

                        if (!periodoFim.isEmpty()) {
                            Date periodoFimDate = DateHelper.parsePeriodString(periodoFim);
                            criterio.setAttribute("periodoFim", periodoFimDate);
                        }

                        spdCodigos.add(CodedValues.SPD_EMPROCESSAMENTO);
                        spdCodigos.add(CodedValues.SPD_SEM_RETORNO);

                        criterio.setAttribute("adeIdentificador", adeIdentificador);
                        criterio.setAttribute("tocCodigos", tocCodigos);

                        parcelas = parcelaController.getParcelas(tipoEntidade, adeNumero, rseMatricula, serCpf, orgCodigo, csaCodigo, spdCodigos, -1, -1, papCodigos, criterio, responsavel);

                        if (parcelas != null) {
                            for (TransferObject parc : parcelas) {
                                String adeCod = (String) parc.getAttribute(Columns.ADE_CODIGO);
                                Integer prdCod = (Integer) parc.getAttribute(Columns.PRD_CODIGO);
                                parcelaController.integrarParcela(adeCod, prdCod, null, CodedValues.SPD_REJEITADAFOLHA, null, responsavel);
                            }
                            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.folha.concluida.sucesso", responsavel));
                        }
                        return iniciar(request, response, session, model);
                    }
                } else {
                    throw new Exception(ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                }
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.folha.concluida.sucesso", responsavel));

            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        }

        CustomTransferObject autdes = null;
        String status;
        String prdVlrPrevisto;
        String prdVlrRealizado;
        try {
            autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);

            // Buscar a parcela para obter valor previsto, realizado e status
            ParcelaDescontoTO parcela = parcelaController.findParcelaByAdeCodigoPrdCodigo(adeCodigo, prdCodigo, responsavel);
            prdVlrPrevisto = NumberHelper.format(parcela.getPrdVlrPrevisto().doubleValue(), NumberHelper.getLang());
            prdVlrRealizado = parcela.getPrdVlrRealizado() != null ? NumberHelper.format(parcela.getPrdVlrRealizado().doubleValue(), NumberHelper.getLang()) : "";
            status = parcela.getSpdCodigo();
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        }

        if (alterarParcela) {
            model.addAttribute("status", status);
            model.addAttribute("prdVlrRealizado", prdVlrRealizado);
        }

        model.addAttribute("isEst", isEst);
        model.addAttribute("tipoEntidade", tipoEntidade);
        model.addAttribute("codigoEntidade", codigoEntidade);
        model.addAttribute("autdes", autdes);
        model.addAttribute("prdVlrPrevisto", prdVlrPrevisto);
        model.addAttribute("alterarParcela", alterarParcela);

        return viewRedirect("jsp/cadastrarRetornoIntegracao/editarRetornoIntegracao", request, session, model, responsavel);
    }

    private boolean validarCamposObrigatorios(String adeIdentificador, String orgCodigo, String tocCodigo, AcessoSistema responsavel) {
        boolean camposObrigatoriosOk = true;
        try {
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CADASTRAR_RETORNO_INTEGRACAO_ADE_IDENTIFICADOR, responsavel) && (TextHelper.isNull(adeIdentificador))) {
                camposObrigatoriosOk = false;
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CADASTRAR_RETORNO_INTEGRACAO_ORG_CODIGO, responsavel) && (TextHelper.isNull(orgCodigo))) {
                camposObrigatoriosOk = false;
            }
            if (ShowFieldHelper.isRequired(FieldKeysConstants.CADASTRAR_RETORNO_INTEGRACAO_TOC_CODIGO, responsavel) && (TextHelper.isNull(tocCodigo))) {
                camposObrigatoriosOk = false;
            }
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return camposObrigatoriosOk;
    }
}
