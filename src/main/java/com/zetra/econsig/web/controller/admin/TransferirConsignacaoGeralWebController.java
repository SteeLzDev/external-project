package com.zetra.econsig.web.controller.admin;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.job.process.ProcessaRelatorioTransfereAde;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.TransferirConsignacaoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: TransferirConsignacaoGeralWebController</p>
 * <p>Description: Controlador Web para o caso de uso Transferência Geral Consignação.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/transferirConsignacaoGeral" })
public class TransferirConsignacaoGeralWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(TransferirConsignacaoGeralWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private TransferirConsignacaoController transferirConsignacaoController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);

        if (!responsavel.isCseSup()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            List<TransferObject> consignatarias = consignatariaController.lstConsignatarias(null, responsavel);
            List<TransferObject> orgaos = consignanteController.lstOrgaos(null, responsavel);
            List<TransferObject> servicos = convenioController.lstServicos(null, responsavel);

            model.addAttribute("consignatarias", consignatarias);
            model.addAttribute("orgaos", orgaos);
            model.addAttribute("servicos", servicos);

            String csaCodigoOrigem = JspHelper.verificaVarQryStr(request, "csaCodigoOrigem");
            String csaCodigoDestino = JspHelper.verificaVarQryStr(request, "csaCodigoDestino");
            String svcCodigoOrigem = JspHelper.verificaVarQryStr(request, "svcCodigoOrigem");
            String svcCodigoDestino = JspHelper.verificaVarQryStr(request, "svcCodigoDestino");
            String orgCodigo = JspHelper.verificaVarQryStr(request, "orgCodigo");
            String rseMatricula = JspHelper.verificaVarQryStr(request, "RSE_MATRICULA");
            String serCpf = JspHelper.verificaVarQryStr(request, "SER_CPF");
            String periodoIni = JspHelper.verificaVarQryStr(request, "periodoIni");
            String periodoFim = JspHelper.verificaVarQryStr(request, "periodoFim");
            List<String> sadCodigos = null;

            if (request.getParameterValues("SAD_CODIGO") != null) {
                sadCodigos = Arrays.asList(request.getParameterValues("SAD_CODIGO"));
            } else {
                sadCodigos = new ArrayList<>();
            }

            // Salva os valores utilizados no model
            model.addAttribute("csaCodigoOrigem", csaCodigoOrigem);
            model.addAttribute("csaCodigoDestino", csaCodigoDestino);
            model.addAttribute("svcCodigoOrigem", svcCodigoOrigem);
            model.addAttribute("svcCodigoDestino", svcCodigoDestino);
            model.addAttribute("orgCodigo", orgCodigo);
            model.addAttribute("rseMatricula", rseMatricula);
            model.addAttribute("serCpf", serCpf);
            model.addAttribute("periodoIni", periodoIni);
            model.addAttribute("periodoFim", periodoFim);
            model.addAttribute("status", sadCodigos);

            if (session.getAttribute("linkArquivo") != null) {
                model.addAttribute("linkArquivo", session.getAttribute("linkArquivo"));
                session.removeAttribute("linkArquivo");
            }

        } catch (ConsignanteControllerException | ConsignatariaControllerException | ConvenioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/transferirConsignacaoGeral/transferirConsignacaoGeral", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=pesquisar" })
    public String pesquisar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        if (!responsavel.isCseSup()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        UploadHelper uploadHelper = new UploadHelper();

        // Tamanho máximo do arquivo
        int maxSize = (TextHelper.isNum(ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSE_ORG, responsavel)) ?
                Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSE_ORG, responsavel).toString()) : 30)  * 1024 * 1024;

        try {
            uploadHelper.processarRequisicao(request.getServletContext(), request, maxSize);
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return iniciar(request, response, session, model);
        }

        List<String> adeNumeroList = null;
        if (uploadHelper.hasArquivosCarregados()) {
            if (!uploadHelper.getFileName(0).toLowerCase().endsWith(".txt")) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.transf.contratos.arquivo.invalido", responsavel));
                return iniciar(request, response, session, model);
            }

            String conteudo = uploadHelper.getFileContent(0);
            uploadHelper.removerArquivosCarregados(responsavel);
            if (!TextHelper.isNull(conteudo)) {
                adeNumeroList = Arrays.asList(conteudo.split("\\r?\\n"));
            }
        } else {
            adeNumeroList = uploadHelper.getValoresCampoFormulario("ADE_NUMERO_LIST");
            if ((adeNumeroList == null || adeNumeroList.isEmpty()) && request.getParameterValues("ADE_NUMERO_LIST") != null) {
                adeNumeroList = Arrays.asList(request.getParameterValues("ADE_NUMERO_LIST"));
            }
        }

        if (adeNumeroList == null || adeNumeroList.isEmpty()) {
            if (!TextHelper.isNull(uploadHelper.getValorCampoFormulario("ADE_NUMERO"))) {
                adeNumeroList = new ArrayList<>();
                adeNumeroList.add(uploadHelper.getValorCampoFormulario("ADE_NUMERO"));
            }
        }

        if (adeNumeroList == null) {
            adeNumeroList = new ArrayList<>();
        }

        List<Long> adeNumeros = null;
        if (!adeNumeroList.isEmpty()) {
            adeNumeros = new ArrayList<>();
            for (String numero : adeNumeroList) {
                if (!TextHelper.isNum(numero)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.transf.contratos.arquivo.invalido", responsavel));
                    return iniciar(request, response, session, model);
                } else {
                    adeNumeros.add(Long.valueOf(numero));
                }
            }
        }

        try {
            String csaCodigoOrigem = JspHelper.verificaVarQryStr(request, uploadHelper, "csaCodigoOrigem");
            String csaCodigoDestino = JspHelper.verificaVarQryStr(request, uploadHelper, "csaCodigoDestino");
            String svcCodigoOrigem = JspHelper.verificaVarQryStr(request, uploadHelper, "svcCodigoOrigem");
            String svcCodigoDestino = JspHelper.verificaVarQryStr(request, uploadHelper, "svcCodigoDestino");
            String orgCodigo = JspHelper.verificaVarQryStr(request, uploadHelper, "orgCodigo");
            String rseMatricula = JspHelper.verificaVarQryStr(request, uploadHelper, "RSE_MATRICULA");
            String serCpf = JspHelper.verificaVarQryStr(request, uploadHelper, "SER_CPF");
            String periodoIni = JspHelper.verificaVarQryStr(request, uploadHelper, "periodoIni");
            String periodoFim = JspHelper.verificaVarQryStr(request, uploadHelper, "periodoFim");
            List<String> sadCodigos = uploadHelper.getValoresCampoFormulario("SAD_CODIGO");

            // Se serviço destino e consignatária destino são nulos ao mesmo tempo, reporta erro pois
            // pelo menos um deles deve ser informado para que a transferência faça sentido (ou transfiro
            // de serviço ou de consignatária)
            if (TextHelper.isNull(svcCodigoDestino) && TextHelper.isNull(csaCodigoDestino)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.transf.contratos.csa.svc.destino.nulos", responsavel));
                return iniciar(request, response, session, model);
            }

            if (sadCodigos == null) {
                if (request.getParameterValues("SAD_CODIGO") != null) {
                    sadCodigos = Arrays.asList(request.getParameterValues("SAD_CODIGO"));
                } else {
                    sadCodigos = new ArrayList<>();
                }
            }

            // Salva os valores utilizados no model
            model.addAttribute("csaCodigoOrigem", csaCodigoOrigem);
            model.addAttribute("csaCodigoDestino", csaCodigoDestino);
            model.addAttribute("svcCodigoOrigem", svcCodigoOrigem);
            model.addAttribute("svcCodigoDestino", svcCodigoDestino);
            model.addAttribute("orgCodigo", orgCodigo);
            model.addAttribute("rseMatricula", rseMatricula);
            model.addAttribute("serCpf", serCpf);
            model.addAttribute("periodoIni", periodoIni);
            model.addAttribute("periodoFim", periodoFim);
            model.addAttribute("status", sadCodigos);

            Date inicio = (!TextHelper.isNull(periodoIni) ? DateHelper.parse(periodoIni + " 00:00:00", LocaleHelper.getDateTimePattern()) : null);
            Date fim = (!TextHelper.isNull(periodoFim) ? DateHelper.parse(periodoFim + " 23:59:59", LocaleHelper.getDateTimePattern()) : null);

            String linkRetorno = getLinkRetorno("iniciar", csaCodigoOrigem, csaCodigoDestino, svcCodigoOrigem, svcCodigoDestino, orgCodigo, rseMatricula, serCpf, periodoIni, periodoFim, adeNumeroList, sadCodigos, responsavel);
            linkRetorno = SynchronizerToken.updateTokenInURL(linkRetorno + "&back=1", request);

            int total = transferirConsignacaoController.countAdeTransferencia(csaCodigoOrigem, csaCodigoDestino, svcCodigoOrigem, svcCodigoDestino, orgCodigo, sadCodigos, inicio, fim, adeNumeros, rseMatricula, serCpf, true, responsavel);

            if (total == 0) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.transf.contratos.nenhuma.consignacao.encontrada", responsavel));
                request.setAttribute("url64", TextHelper.encode64(linkRetorno));
                return "jsp/redirecionador/redirecionar";
            } else if (total < adeNumeroList.size()) {
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.aviso.transf.contratos.qtd.consignacao.encontrada.menor", responsavel));
            }

            int size = JspHelper.LIMITE;
            int offset = (!TextHelper.isNull(request.getParameter("offset")) && TextHelper.isNum(request.getParameter("offset"))) ? Integer.parseInt(request.getParameter("offset")) : 0;

            List<TransferObject> lstConsignacao = transferirConsignacaoController.listarAdeTransferencia(csaCodigoOrigem, csaCodigoDestino, svcCodigoOrigem, svcCodigoDestino, orgCodigo, sadCodigos, inicio, fim, adeNumeros, rseMatricula, serCpf, offset, size, true, responsavel);

            String[] colunas = {
                FieldKeysConstants.LISTA_CONSIGNACAO_CONSIGNATARIA,
                FieldKeysConstants.LISTA_CONSIGNACAO_NUMERO,
                FieldKeysConstants.LISTA_CONSIGNACAO_SERVICO,
                FieldKeysConstants.LISTA_CONSIGNACAO_SERVIDOR,
                FieldKeysConstants.LISTA_CONSIGNACAO_DATA_RESERVA,
                FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA,
                FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_FOLHA,
                FieldKeysConstants.LISTA_CONSIGNACAO_PRAZO,
                FieldKeysConstants.LISTA_CONSIGNACAO_PARCELAS_PAGAS,
                FieldKeysConstants.LISTA_CONSIGNACAO_STATUS
            };
            formatarValoresListaConsignacao(lstConsignacao, colunas, responsavel);
            model.addAttribute("lstConsignacao", lstConsignacao);
            model.addAttribute("adeNumeroList", adeNumeroList);
            model.addAttribute("linkRetorno", linkRetorno);

            if (session.getAttribute("linkArquivo") != null) {
                model.addAttribute("linkArquivo", session.getAttribute("linkArquivo"));
                session.removeAttribute("linkArquivo");
            }

            String linkPaginacao = getLinkRetorno("pesquisar", csaCodigoOrigem, csaCodigoDestino, svcCodigoOrigem, svcCodigoDestino, orgCodigo, rseMatricula, serCpf, periodoIni, periodoFim, adeNumeroList, sadCodigos, responsavel);
            configurarPaginador(linkPaginacao, "rotulo.paginacao.titulo.consignacao", total, size, null, false, request, model);

            return viewRedirect("jsp/transferirConsignacaoGeral/selecionarAdeTransferencia", request, session, model, responsavel);

        } catch (ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.data.invalida", responsavel));
            return iniciar(request, response, session, model);
        } catch (AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return iniciar(request, response, session, model);
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

                } else if (chaveCampo.equals(FieldKeysConstants.LISTA_CONSIGNACAO_SERVIDOR)) {
                    valorCampo = ade.getAttribute(Columns.RSE_MATRICULA) + " - " + ade.getAttribute(Columns.SER_CPF) + " - " + ade.getAttribute(Columns.SER_NOME);

                } else if (chaveCampo.equals(FieldKeysConstants.LISTA_CONSIGNACAO_DATA_RESERVA)) {
                    try {
                        valorCampo = DateHelper.reformat(ade.getAttribute(Columns.ADE_DATA).toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
                    } catch (ParseException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }

                } else if (chaveCampo.equals(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA)) {
                    try {
                        String adeTipoVlr = (!TextHelper.isNull(ade.getAttribute(Columns.ADE_TIPO_VLR)) ? ade.getAttribute(Columns.ADE_TIPO_VLR).toString() : CodedValues.TIPO_VLR_FIXO);
                        valorCampo = ParamSvcTO.getDescricaoTpsTipoVlr(adeTipoVlr) + " " + (!TextHelper.isNull(ade.getAttribute(Columns.ADE_VLR)) ? NumberHelper.reformat(ade.getAttribute(Columns.ADE_VLR).toString(), "en", NumberHelper.getLang()) : "");
                    } catch (ParseException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }

                } else if (chaveCampo.equals(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_FOLHA)) {
                    try {
                        valorCampo = (!TextHelper.isNull(ade.getAttribute(Columns.ADE_VLR_FOLHA)) ? NumberHelper.reformat(ade.getAttribute(Columns.ADE_VLR_FOLHA).toString(), "en", NumberHelper.getLang()) : "");
                    } catch (ParseException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }

                } else if (chaveCampo.equals(FieldKeysConstants.LISTA_CONSIGNACAO_PRAZO)) {
                    if (!TextHelper.isNull(ade.getAttribute(Columns.ADE_PRAZO))) {
                        valorCampo = ade.getAttribute(Columns.ADE_PRAZO).toString();
                    } else {
                        valorCampo = ApplicationResourcesHelper.getMessage("rotulo.indeterminado.abreviado", responsavel);
                    }

                } else if (chaveCampo.equals(FieldKeysConstants.LISTA_CONSIGNACAO_PARCELAS_PAGAS)) {
                    valorCampo = (ade.getAttribute(Columns.ADE_PRD_PAGAS) != null ? ade.getAttribute(Columns.ADE_PRD_PAGAS).toString() : "0");

                } else if (chaveCampo.equals(FieldKeysConstants.LISTA_CONSIGNACAO_STATUS)) {
                    valorCampo = ade.getAttribute(Columns.SAD_DESCRICAO).toString();
                    if (ade.getAttribute(Columns.ADE_DATA_STATUS) != null) {
                        String dataAtualizacao = DateHelper.toDateTimeString((Date) ade.getAttribute(Columns.ADE_DATA_STATUS));
                        valorCampo = String.format("%s (%s)", valorCampo, dataAtualizacao);
                    }
                }

                ade.setAttribute(chaveCampo, valorCampo);
            }
        }

        return lstConsignacao;
    }

    @RequestMapping(params = { "acao=transferir" })
    public String transferir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        if (!responsavel.isCseSup()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String csaCodigoOrigem = request.getParameter("csaCodigoOrigem");
        String csaCodigoDestino = request.getParameter("csaCodigoDestino");
        String svcCodigoOrigem = request.getParameter("svcCodigoOrigem");
        String svcCodigoDestino = request.getParameter("svcCodigoDestino");
        String orgCodigo = request.getParameter("orgCodigo");
        String rseMatricula = request.getParameter("rseMatricula");
        String serCpf = request.getParameter("serCpf");
        String periodoIni = request.getParameter("periodoIni");
        String periodoFim = request.getParameter("periodoFim");

        // Se serviço destino e consignatária destino são nulos ao mesmo tempo, reporta erro pois
        // pelo menos um deles deve ser informado para que a transferência faça sentido (ou transfiro
        // de serviço ou de consignatária)
        if (TextHelper.isNull(svcCodigoDestino) && TextHelper.isNull(csaCodigoDestino)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.transf.contratos.csa.svc.destino.nulos", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String ocaObs = request.getParameter("ocaObs");
        boolean transferirTodos = "S".equals(request.getParameter("transferirTodos"));
        boolean atualizarAdeIncMargem = "S".equals(request.getParameter("atualizarAdeIncMargem"));

        List<String> status = request.getParameterValues("SAD_CODIGO") != null ? Arrays.asList(request.getParameterValues("SAD_CODIGO")) : null;

        List<String> adeNumeroList = null;
        if (transferirTodos) {
            // Se deve transferir todos, recupera os ADE_NUMEROs informados na pesquisa
            adeNumeroList = request.getParameterValues("ADE_NUMERO") != null ? Arrays.asList(request.getParameterValues("ADE_NUMERO")) : null;
        } else {
            // Se deve transferir os selecionados, recupera os ADE_NUMEROs selecionados
            adeNumeroList = request.getParameterValues("ADE_NUMERO_SELECIONADO") != null ? Arrays.asList(request.getParameterValues("ADE_NUMERO_SELECIONADO")) : null;
        }

        if (adeNumeroList == null) {
            adeNumeroList = new ArrayList<>();
        }

        if (!transferirTodos && adeNumeroList.isEmpty()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.transf.contratos.nenhum.registro.selecionado", responsavel));
            return iniciar(request, response, session, model);
        }

        List<Long> adeNumeros = null;
        if (!adeNumeroList.isEmpty()) {
            adeNumeros = new ArrayList<>();
            for (String numero : adeNumeroList) {
                if (!TextHelper.isNull(numero)) {
                    if (!TextHelper.isNum(numero)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.transf.contratos.ade.numero.invalido", responsavel));
                        return iniciar(request, response, session, model);
                    } else {
                        adeNumeros.add(Long.valueOf(numero));
                    }
                }
            }
        }

        try {
            LOG.debug(ApplicationResourcesHelper.getMessage("rotulo.transf.contratos.acao.transferir", responsavel));

            Date inicio = (!TextHelper.isNull(periodoIni) ? DateHelper.parse(periodoIni + " 00:00:00", LocaleHelper.getDateTimePattern()) : null);
            Date fim = (!TextHelper.isNull(periodoFim) ? DateHelper.parse(periodoFim + " 23:59:59", LocaleHelper.getDateTimePattern()) : null);

            // Transfere os contratos
            transferirConsignacaoController.transfereAde(csaCodigoOrigem, csaCodigoDestino, svcCodigoOrigem, svcCodigoDestino, orgCodigo, status, inicio, fim, adeNumeros, rseMatricula, serCpf, ocaObs, atualizarAdeIncMargem, true, responsavel);

        } catch (ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.data.invalida", responsavel));
            return iniciar(request, response, session, model);
        } catch (AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return iniciar(request, response, session, model);
        }

        // Seta mensagem de sucesso na sessão do usuário
        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.transf.contratos.transferir.sucesso", responsavel));
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=gerarRelatorio" })
    public String gerarRelatorio(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        if (!responsavel.isCseSup()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String csaCodigoOrigem = request.getParameter("csaCodigoOrigem");
        String csaCodigoDestino = request.getParameter("csaCodigoDestino");
        String svcCodigoOrigem = request.getParameter("svcCodigoOrigem");
        String svcCodigoDestino = request.getParameter("svcCodigoDestino");
        String orgCodigo = request.getParameter("orgCodigo");
        String rseMatricula = request.getParameter("rseMatricula");
        String serCpf = request.getParameter("serCpf");
        String periodoIni = request.getParameter("periodoIni");
        String periodoFim = request.getParameter("periodoFim");

        String[] sadCodigo = request.getParameterValues("SAD_CODIGO");
        String[] adeNumero = request.getParameterValues("ADE_NUMERO");

        List<String> sadCodigos = null;
        if (sadCodigo != null && sadCodigo.length > 0) {
            sadCodigos = Arrays.asList(sadCodigo);
        }

        List<String> adeNumeroList = null;
        if (adeNumero != null && adeNumero.length > 0) {
            adeNumeroList = Arrays.asList(adeNumero);
        }

        Map<String, String[]> parameterMap = new HashMap<>();
        parameterMap.put("csaCodigoOrigem", new String[] { csaCodigoOrigem });
        parameterMap.put("csaCodigoDestino", new String[] { csaCodigoDestino });
        parameterMap.put("svcCodigoOrigem", new String[] { svcCodigoOrigem });
        parameterMap.put("svcCodigoDestino", new String[] { svcCodigoDestino });
        parameterMap.put("orgCodigo", new String[] { orgCodigo });
        parameterMap.put("periodoIni", new String[] { periodoIni });
        parameterMap.put("periodoFim", new String[] { periodoFim });
        parameterMap.put("rseMatricula", new String[] { rseMatricula });
        parameterMap.put("serCpf", new String[] { serCpf });
        parameterMap.put("adeNumero", adeNumero);
        parameterMap.put("sadCodigo", sadCodigo);

        LOG.debug(ApplicationResourcesHelper.getMessage("rotulo.transf.contratos.acao.listar", responsavel));

        // Gera relatório de transferência de contratos
        ProcessaRelatorioTransfereAde relatorio = new ProcessaRelatorioTransfereAde(parameterMap, session, false, responsavel);
        relatorio.run();

        String linkArquivo = "";

        try {
            String arquivoRelatorio = (String) session.getAttribute("arquivoRelatorio");
            if (!responsavel.temPermissao(CodedValues.FUN_REL_CONSIGNACOES)) {
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.transf.contratos.download.relatorio.consignacoes", responsavel));
            } else if (!TextHelper.isNull(arquivoRelatorio)) {
                session.setAttribute(CodedValues.MSG_INFO, "#econsig:msg#");
                linkArquivo = JspHelper.msgSession(session, true);
                Map<String, String> map = (Map<String, String>) org.json.simple.JSONValue.parse(arquivoRelatorio);
                String msgDownload = ApplicationResourcesHelper.getMessage("mensagem.informacao.arg0.gerado.com.sucesso", responsavel, TextHelper.forHtmlContent(map.get("titulo"))) + "<br/><A CLASS=\"TitTab\" HREF=\"#no-back\" ONCLICK=\"postData('../v3/downloadArquivo?arquivo_nome=" + java.net.URLEncoder.encode(TextHelper.forJavaScriptAttribute(map.get("nome")), "UTF-8") + "&tipo=relatorio&subtipo=consignacoes" + "&"
                        + SynchronizerToken.TRANSACTION_TOKEN_KEY + "=null" + "&skip_history=true','download'); \">" + ApplicationResourcesHelper.getMessage("mensagem.informacao.para.fazer.download.arquivo.clique.aqui", responsavel) + "</A>";
                session.removeAttribute("arquivoRelatorio");
                msgDownload = msgDownload.replaceAll(SynchronizerToken.TRANSACTION_TOKEN_KEY + "=null", SynchronizerToken.generateToken4URL(request));
                linkArquivo = linkArquivo.replaceFirst("#econsig:msg#", msgDownload);
            } else {
                linkArquivo = JspHelper.msgSession(session, true);
                linkArquivo = linkArquivo.replaceAll(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.generateToken4URL(request) + "&old.token");
            }
        } catch (UnsupportedEncodingException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Grava na sessão o resultado do relatório
        session.setAttribute("linkArquivo", linkArquivo);

        String linkRetorno = getLinkRetorno("pesquisar", csaCodigoOrigem, csaCodigoDestino, svcCodigoOrigem, svcCodigoDestino, orgCodigo, rseMatricula, serCpf, periodoIni, periodoFim, adeNumeroList, sadCodigos, responsavel);
        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(linkRetorno + "&back=1", request)));
        return "jsp/redirecionador/redirecionar";
    }

    private String getLinkRetorno(String acao, String csaCodigoOrigem, String csaCodigoDestino, String svcCodigoOrigem, String svcCodigoDestino, String orgCodigo, String rseMatricula, String serCpf, String periodoIni, String periodoFim, List<String> adeNumeroList, List<String> sadCodigos, AcessoSistema responsavel) {
        String linkPaginacao = "../v3/transferirConsignacaoGeral?acao=" + acao
                             + "&csaCodigoOrigem=" + TextHelper.coalesce(csaCodigoOrigem, "")
                             + "&csaCodigoDestino=" + TextHelper.coalesce(csaCodigoDestino, "")
                             + "&svcCodigoOrigem=" + TextHelper.coalesce(svcCodigoOrigem, "")
                             + "&svcCodigoDestino=" + TextHelper.coalesce(svcCodigoDestino, "")
                             + "&orgCodigo=" + TextHelper.coalesce(orgCodigo, "")
                             + "&RSE_MATRICULA=" + TextHelper.coalesce(rseMatricula, "")
                             + "&SER_CPF=" + TextHelper.coalesce(serCpf, "")
                             + "&periodoIni=" + TextHelper.coalesce(periodoIni, "")
                             + "&periodoFim=" + TextHelper.coalesce(periodoFim, "")
                             ;

        if (adeNumeroList != null) {
            for (String adeNumero : adeNumeroList) {
                linkPaginacao += "&ADE_NUMERO_LIST=" + adeNumero;
            }
        }
        if (sadCodigos != null) {
            for (String sadCodigo : sadCodigos) {
                linkPaginacao += "&SAD_CODIGO=" + sadCodigo;
            }
        }

        return linkPaginacao;
    }
}
