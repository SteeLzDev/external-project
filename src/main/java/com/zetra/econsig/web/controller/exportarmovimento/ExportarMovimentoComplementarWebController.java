package com.zetra.econsig.web.controller.exportarmovimento;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.delegate.ExportaMovimentoDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
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
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ExportarMovimentoComplementarWebController</p>
 * <p>Description: Controlador Web para o caso de uso de exportação movimento financeiro complementar.</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/exportarMovimentoComplementar" })
public class ExportarMovimentoComplementarWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ExportarMovimentoComplementarWebController.class);

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        SynchronizerToken.saveToken(request);
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        return viewRedirect("jsp/integrarFolha/iniciarMovimentoComplementar", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=pesquisar" })
    public String pesquisar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

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
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.exportar.movimento.financeiro.complementar.arquivo.invalido", responsavel));
                return iniciar(request, response, session, model);
            }

            String conteudo = uploadHelper.getFileContent(0);
            uploadHelper.removerArquivosCarregados(responsavel);
            if (!TextHelper.isNull(conteudo)) {
                adeNumeroList = Arrays.asList(conteudo.split("\\r?\\n"));

                for (String numero : adeNumeroList) {
                    if (!TextHelper.isNum(numero)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.exportar.movimento.financeiro.complementar.arquivo.invalido", responsavel));
                        return iniciar(request, response, session, model);
                    }
                }
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
            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.ajuda.exportar.movimento.financeiro.complementar", responsavel));
                return iniciar(request, response, session, model);
            }
        }

        try {
            String tipoEntidade = responsavel.getTipoEntidade();
            String codigoEntidade = responsavel.getCodigoEntidade();

            if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                tipoEntidade = AcessoSistema.ENTIDADE_EST;
                codigoEntidade = responsavel.getCodigoEntidadePai();
            }

            int total = pesquisarConsignacaoController.countPesquisaAutorizacao(tipoEntidade, codigoEntidade, null, adeNumeroList, null, null, null, null, responsavel);

            if (total == 0) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.exportar.movimento.financeiro.complementar.nenhuma.consignacao.encontrada", responsavel));
                return iniciar(request, response, session, model);

            } else if (total < adeNumeroList.size()) {
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.aviso.exportar.movimento.financeiro.complementar.qtd.consignacao.encontrada.menor", responsavel));
            }

            int size = JspHelper.LIMITE;
            int offset = (!TextHelper.isNull(request.getParameter("offset")) && TextHelper.isNum(request.getParameter("offset"))) ? Integer.parseInt(request.getParameter("offset")) : 0;

            List<TransferObject> lstConsignacao = pesquisarConsignacaoController.pesquisaAutorizacao(tipoEntidade, codigoEntidade, null, adeNumeroList, null, null, null, offset, size, null, responsavel);

            String[] colunas = {
                FieldKeysConstants.LISTA_CONSIGNACAO_CONSIGNATARIA,
                FieldKeysConstants.LISTA_CONSIGNACAO_RESPONSAVEL,
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

            String linkPaginacao = "../v3/exportarMovimentoComplementar?acao=pesquisar";
            for (String adeNumero : adeNumeroList) {
                linkPaginacao += "&ADE_NUMERO_LIST=" + adeNumero;
            }

            configurarPaginador(linkPaginacao, "rotulo.paginacao.titulo.consignacao", total, size, null, false, request, model);
            return viewRedirect("jsp/integrarFolha/selecionarMovimentoComplementar", request, session, model, responsavel);

        } catch (AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
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

                } else if (chaveCampo.equals(FieldKeysConstants.LISTA_CONSIGNACAO_RESPONSAVEL)) {
                    valorCampo = ade.getAttribute(Columns.USU_LOGIN) != null ? ade.getAttribute(Columns.USU_LOGIN).toString() : "";
                    valorCampo = (valorCampo.equalsIgnoreCase((String) ade.getAttribute(Columns.USU_CODIGO)) && ade.getAttribute(Columns.USU_TIPO_BLOQ) != null) ? (ade.getAttribute(Columns.USU_TIPO_BLOQ).toString() + "(*)") : valorCampo;

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
                        valorCampo = ParamSvcTO.getDescricaoTpsTipoVlr((String) ade.getAttribute(Columns.ADE_TIPO_VLR)) + " " + (!TextHelper.isNull(ade.getAttribute(Columns.ADE_VLR)) ? NumberHelper.reformat(ade.getAttribute(Columns.ADE_VLR).toString(), "en", NumberHelper.getLang()) : "");
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

    @RequestMapping(params = { "acao=exportar" })
    public String exportar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String[] adeNumeros = null;
        if (JspHelper.verificaVarQryStr(request, "exportarTodos").equals("S")) {
            // Pega a lista de ADE_NUMERO usada na pesquisa
            adeNumeros = request.getParameterValues("ADE_NUMERO");
        } else {
            // Pega a lista de ADE_NUMERO selecionada pelo usuário
            adeNumeros = request.getParameterValues("ADE_NUMERO_SELECIONADO");
        }

        if (adeNumeros == null || adeNumeros.length == 0) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.exportar.movimento.financeiro.complementar.nenhum.registro.selecionado", responsavel));
            return iniciar(request, response, session, model);
        }

        List<String> orgCodigos = null;
        List<String> estCodigos = null;

        if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
            estCodigos = Arrays.asList(responsavel.getCodigoEntidadePai());
        } else if (responsavel.isOrg()) {
            orgCodigos = Arrays.asList(responsavel.getCodigoEntidade());
        }

        try {
            // Executa rotina de re-exportação em arquivo único ("1"), filtrando os registros retornados pela exportação
            ExportaMovimentoDelegate expDelegate = new ExportaMovimentoDelegate();
            ParametrosExportacao parametrosExportacao = new ParametrosExportacao();
            parametrosExportacao.setOrgCodigos(orgCodigos)
                                .setEstCodigos(estCodigos)
                                .setVerbas(null)
                                .setAcao(ParametrosExportacao.AcaoEnum.REEXPORTAR.getCodigo())
                                .setOpcao("1")
//                                .setTipoArquivo("1")
                                .setResponsavel(responsavel);
            expDelegate.exportaMovimentoFinanceiro(parametrosExportacao, Arrays.asList(adeNumeros), responsavel);

            // Define mensagem de sucesso e retorna ao início
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.sucesso.exportar.movimento.financeiro.complementar", responsavel));
            return iniciar(request, response, session, model);

        } catch (ConsignanteControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
