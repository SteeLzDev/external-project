package com.zetra.econsig.web.controller.consignacao;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
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
import com.zetra.econsig.dto.parametros.ConfirmarConsignacaoParametros;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.dto.web.ColunaListaConsignacao;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.GeraTelaSegundaSenhaHelper;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.ConfirmarConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ConfirmarConsignacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso ConfirmarConsignacao.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/confirmarConsignacao" })
public class ConfirmarConsignacaoWebController extends AbstractEfetivarAcaoConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConfirmarConsignacaoWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private ConfirmarConsignacaoController confirmarConsignacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ParametroController parametroController;

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (responsavel.isCseSup()) {
            carregarListaEstabelecimento(request, session, model, responsavel);
            carregarListaOrgao(request, session, model, responsavel);
        }
        if (responsavel.isCseSupOrg()) {
            carregarListaConsignataria(request, session, model, responsavel);
        }
        carregarListaServico(request, session, model, responsavel);

        // Habilita exibição de campo para filtro por data
        model.addAttribute("exibirFiltroDataInclusao", Boolean.TRUE);
        // Habilita opção de listar todos os registros
        model.addAttribute("exibirOpcaoListarTodos", Boolean.TRUE);

        return super.iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=efetivarAcao" })
    public String efetivarAcao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String urlDestino = "../v3/confirmarConsignacao?acao=confirmar";
        String funCodigo = CodedValues.FUN_CONF_RESERVA;
        String[] adeCodigos = request.getParameterValues("chkConfirmar");
        boolean temAnexoConfirmarReserva = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_INCLUSAO_ANEXO_CONFIRMAR_RESERVA, responsavel);

        if (!super.isExigeMotivoOperacao(funCodigo, responsavel) && !temAnexoConfirmarReserva) {
            // Realiza um forward para passar pelo filtro de segurança e exigir segunda senha, caso habilitado
            return "forward:" + forwardUrl(urlDestino) + "&_skip_history_=true";
        } else {
            ParamSvcTO paramSvcCse = null;
            boolean anexoObrigatorio = false;

            String strAdeCodigo = request.getParameter("ADE_CODIGO") != null ? request.getParameter("ADE_CODIGO") : null;
            List<TransferObject> autdesList = new ArrayList<>();
            try {
                if (TextHelper.isNull(strAdeCodigo) && adeCodigos != null) {
                    CustomTransferObject autdes = null;
                    for (String adeCodigo : adeCodigos) {
                        autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
                        autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
                        autdesList.add(autdes);

                        if (!anexoObrigatorio) {
                            try {
                                paramSvcCse = parametroController.getParamSvcCseTO((String) autdes.getAttribute(Columns.SVC_CODIGO), responsavel);
                                anexoObrigatorio = paramSvcCse.isTpsAnexoConfirmarReservaObrigatorio();
                            } catch (ParametroControllerException ex) {
                                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                            }
                        }
                    }
                } else {
                    CustomTransferObject autdes = pesquisarConsignacaoController.buscaAutorizacao(strAdeCodigo, responsavel);
                    autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
                    autdesList.add(autdes);

                    try {
                        paramSvcCse = parametroController.getParamSvcCseTO((String) autdes.getAttribute(Columns.SVC_CODIGO), responsavel);
                        anexoObrigatorio = paramSvcCse.isTpsAnexoConfirmarReservaObrigatorio();
                    } catch (ParametroControllerException ex) {
                        session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                }
            } catch (AutorizacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            model.addAttribute("lstConsignacao", autdesList);
            model.addAttribute("urlDestino", urlDestino);
            model.addAttribute("anexoObrigatorio", anexoObrigatorio);
            model.addAttribute("exibirAnexo", temAnexoConfirmarReserva);
            model.addAttribute("_skip_history_", "true");

            return viewRedirect("jsp/confirmarReserva/confirmarReserva", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=confirmar" })
    public String confirmar(HttpServletRequest request, HttpServletResponse response, HttpSession session,Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        File dirTemporario = null;
        List<File> dirsDestino = null;

        ParamSession paramSession = ParamSession.getParamSession(session);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String msg = "";
        String[] adeCodigos = null;
        String adeCodigoReqParam = null;
        String [] chkConfirmarReqParam = null;
        String tmoCodigoParam = null;
        String adeObsParam = null;
        String aadDescricao = null;
        UploadHelper uploadHelper = null;

        boolean temAnexoConfirmarReserva = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_INCLUSAO_ANEXO_CONFIRMAR_RESERVA, responsavel);

        if (super.isExigeMotivoOperacao(CodedValues.FUN_CONF_RESERVA, responsavel) || temAnexoConfirmarReserva) {
            try {
                String tamMax = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_ARQ_ANEXO_CONTRATO, responsavel);
                uploadHelper = new UploadHelper();
                uploadHelper.processarRequisicao(request.getServletContext(), request, Integer.valueOf(tamMax) * 1024);

                adeCodigoReqParam = uploadHelper.getValorCampoFormulario("ADE_CODIGO");
                chkConfirmarReqParam = JspHelper.obterParametrosRequisicao(request, uploadHelper, new String[] {"chkConfirmar"});
                tmoCodigoParam = uploadHelper.getValorCampoFormulario("TMO_CODIGO");
                adeObsParam = uploadHelper.getValorCampoFormulario("ADE_OBS");
                aadDescricao = uploadHelper.getValorCampoFormulario("AAD_DESCRICAO");
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

        } else {
            adeCodigoReqParam = request.getParameter("ADE_CODIGO");
            chkConfirmarReqParam = request.getParameterValues("chkConfirmar");
            tmoCodigoParam = request.getParameter("TMO_CODIGO");
            aadDescricao = request.getParameter("AAD_DESCRICAO");
            adeObsParam = JspHelper.verificaVarQryStr(request, "ADE_OBS");
        }

        if (TextHelper.isNull(adeCodigoReqParam)) {
            if (TextHelper.isNull(chkConfirmarReqParam)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            adeCodigos = chkConfirmarReqParam;
        } else {
            adeCodigos = new String[1];
            adeCodigos[0] = adeCodigoReqParam;
        }

        List<Map<String, File>> anexos = null;
        File dirDestino = null;
        try {
            // Salva o arquivo

            if (uploadHelper != null) {
                anexos = new ArrayList<>(adeCodigos.length);
                String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
                String diretorioTemporario = diretorioRaizArquivos + File.separator + UploadHelper.SUBDIR_ARQUIVOS_TEMPORARIOS;
                String sessionId = session.getId();

                File arqDestino = null;
                File [] listTempFiles = null;
                String path = "anexo" + File.separatorChar + sessionId;

                dirTemporario = new File(diretorioTemporario + File.separatorChar + path);
                if (dirTemporario.exists()) {
                    listTempFiles = dirTemporario.listFiles();
                    dirsDestino = new ArrayList<>();

                    for (int i = 0; i < adeCodigos.length; i++) {
                        TransferObject ade = pesquisarConsignacaoController.findAutDesconto(adeCodigos[i], responsavel);
                        String dirDestinoPah = diretorioRaizArquivos + File.separatorChar  + "anexo" + File.separatorChar + DateHelper.format((Date) ade.getAttribute(Columns.ADE_DATA), "yyyyMMdd") +  File.separatorChar  + adeCodigos[i];
                        dirDestino = new File(dirDestinoPah);
                        dirsDestino.add(dirDestino);

                        if (listTempFiles != null) {
                            Map<String, File> mapAnexos = new HashMap<>();
                            for (File tempFile: listTempFiles) {
                                if (dirDestino.exists() || dirDestino.mkdirs()) {
                                    try {
                                        arqDestino = new File(dirDestino.getAbsolutePath() + File.separatorChar + tempFile.getName());

                                        FileHelper.copyFile(tempFile, arqDestino);

                                        mapAnexos.put(arqDestino.getName(), arqDestino);
                                        anexos.add(i, mapAnexos);
                                    } catch (Exception e) {
                                        if (dirTemporario.exists()) {
                                            FileHelper.deleteDir(dirTemporario.getPath());
                                        }
                                        if (dirDestino.exists()) {
                                            FileHelper.deleteDir(dirDestino.getPath());
                                        }
                                        throw new ZetraException("mensagem.erro.upload.anexo.confirmar.reserva", responsavel);
                                    }
                                } else {
                                    throw new ZetraException("mensagem.erro.upload.anexo.confirmar.reserva", responsavel);
                                }
                            }
                        }
                    }
                    FileHelper.deleteDir(dirTemporario.getPath());
                }

                if (uploadHelper != null) {
                    // Remove os arquivos carregados pois já foram copiados para as pastas corretas
                    uploadHelper.removerArquivosCarregados(responsavel);
                }
            }
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } finally {
            try {
                if (dirTemporario != null && dirTemporario.exists()) {
                    FileHelper.deleteDir(dirTemporario.getPath());
                }
            } catch (IOException e) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        ConfirmarConsignacaoParametros parametros = new ConfirmarConsignacaoParametros();
        int i = 0;
        for (String adeCodigo : adeCodigos) {
            try {
                CustomTransferObject tmo = null;
                if (!TextHelper.isNull(tmoCodigoParam)) {
                    tmo = new CustomTransferObject();
                    tmo.setAttribute(Columns.ADE_CODIGO, adeCodigo);
                    tmo.setAttribute(Columns.TMO_CODIGO, tmoCodigoParam);
                    tmo.setAttribute(Columns.OCA_OBS, adeObsParam);
                }

                if (anexos != null && !anexos.isEmpty()) {
                    parametros.setAnexos(anexos.get(i).values());
                    if (!TextHelper.isNull(aadDescricao)) {
                        parametros.setAnexoObs(aadDescricao);
                    }
                    i++;
                }

                confirmarConsignacaoController.confirmar(adeCodigo, tmo, parametros, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.confirmar.reserva.concluido.sucesso", responsavel));
                if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                    autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO,
                            (String) session.getAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA),
                            (AcessoSistema) session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA));
                }
            } catch (Exception mae) {
                msg += mae.getMessage() + "<BR>";
                session.removeAttribute(CodedValues.MSG_INFO);

                try {
                    if (dirsDestino != null && !dirsDestino.isEmpty()) {
                        for (File dirDestinoAApagar: dirsDestino) {
                            if (dirDestinoAApagar != null && dirDestinoAApagar.exists()) {
                                FileHelper.deleteDir(dirDestinoAApagar.getPath());
                            }
                        }
                    }
                } catch (IOException e) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                session.setAttribute(CodedValues.MSG_ERRO, mae.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
            session.removeAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA);
            session.removeAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA);
        }

        session.setAttribute(CodedValues.MSG_ERRO, msg);
        if (uploadHelper != null) {
            paramSession.halfBack();
        }
        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        super.configurarPagina(request, session, model, responsavel);
        // Adiciona ao model as informações específicas da operação
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.confirmar.reserva.titulo", responsavel));
        model.addAttribute("msgConfirmacao", ApplicationResourcesHelper.getMessage("mensagem.confirmacao.reserva", responsavel));
        model.addAttribute("acaoFormulario", "../v3/confirmarConsignacao");
        model.addAttribute("adeCodigos", request.getParameterValues("chkConfirmar"));
        model.addAttribute("nomeCampo", "chkConfirmar");
        model.addAttribute("imageHeader", "i-operacional");
    }


    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_AGUARD_CONF);
        return sadCodigos;
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona opção para liquidar consignação
        String link = "../v3/confirmarConsignacao?acao=efetivarAcao";
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.confirmar.abreviado", responsavel);
        String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar", responsavel);
        String msgAlternativa = "";
        String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.multiplo.confirmar", responsavel);
        String msgAdicionalConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.informacao.valor.parcela.maior.margem.tratamento.especial", responsavel);

        acoes.add(new AcaoConsignacao("CONF_RESERVA", CodedValues.FUN_CONF_RESERVA, descricao, descricaoCompleta, "confirmar_margem.gif", "btnConfirmarReserva", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null,"chkConfirmar"));

        // Adiciona o editar consignação
        link = "../v3/confirmarConsignacao?acao=detalharConsignacao";
        descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar.abreviado", responsavel);
        descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar", responsavel);
        msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.clique.aqui", responsavel);
        msgConfirmacao = "";
        msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("DETALHAR", CodedValues.FUN_CONS_CONSIGNACAO, descricao, descricaoCompleta, "editar.gif", "btnConsultarConsignacao", msgAlternativa, msgConfirmacao, null, link, null,null));

        return acoes;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "confirmar");

        criterio.setAttribute(Columns.EST_CODIGO, JspHelper.verificaVarQryStr(request, "EST_CODIGO"));
        criterio.setAttribute(Columns.ORG_CODIGO, JspHelper.verificaVarQryStr(request, "ORG_CODIGO"));
        criterio.setAttribute(Columns.SVC_CODIGO, JspHelper.verificaVarQryStr(request, "SVC_CODIGO"));
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

        return criterio;
    }

    @Override
    protected List<ColunaListaConsignacao> definirColunasListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        boolean resultadoMultiplosServidores = (request.getAttribute("resultadoMultiplosServidores") != null);

        List<ColunaListaConsignacao> colunas = new ArrayList<>();

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
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_STATUS, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_STATUS, ApplicationResourcesHelper.getMessage("rotulo.consignacao.status", responsavel)));
            }
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return colunas;
    }
}
