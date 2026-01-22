package com.zetra.econsig.web.controller.mensagem;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MensagemTO;
import com.zetra.econsig.exception.ArquivoControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.MensagemControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.job.process.ProcessaPushNotificationServidor;
import com.zetra.econsig.persistence.entity.Servico;
import com.zetra.econsig.service.arquivo.ArquivoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.mensagem.MensagemController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import eu.medsea.mimeutil.MimeType;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ManterMensagemWebController</p>
 * <p>Description: Controlador Web responsável por gerenciar todo fluxo de mensagem.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/manterMensagem" })
public class ManterMensagemWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterMensagemWebController.class);

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ServicoController servicoController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private MensagemController mensagemController;

    @Autowired
    private ArquivoController arquivoController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ConsignanteController consignanteController;

    @RequestMapping(params = { "acao=listar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        List<TransferObject> consignatarias = null;
        boolean exibeColunaCsa = false;

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "FILTRO")) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final boolean podeEnviarEmail = responsavel.temPermissao(CodedValues.FUN_ENVIAR_MENSAGEM_POR_EMAIL);

        List<TransferObject> lstMensagens = null;
        /* FILTRO DE MENSAGEM */
        final String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
        final List<String> filtroCsa = !TextHelper.isNull(request.getParameterValues("CSA_CODIGO")) ? Arrays.asList(request.getParameterValues("CSA_CODIGO")) : null;
        int filtro_tipo = -1;
        try {
            if (!JspHelper.verificaVarQryStr(request, "FILTRO_TIPO").isEmpty()) {
                filtro_tipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
            }
        } catch (final Exception ex1) {
            session.setAttribute(CodedValues.MSG_ERRO, ex1.getMessage());
            LOG.error(ex1);
        }
        try {
            consignatarias = consignatariaController.lstConsignatarias(null, responsavel);
            final CustomTransferObject criterio = new CustomTransferObject();

            final String campo = switch (filtro_tipo) {
                case 2 -> Columns.MEN_EXIBE_CSE;
                case 3 -> Columns.MEN_EXIBE_CSA;
                case 4 -> Columns.MEN_EXIBE_COR;
                case 5 -> Columns.MEN_EXIBE_ORG;
                case 6 -> Columns.MEN_EXIBE_SER;
                case 7 -> Columns.MEN_EXIBE_SUP;
                case 8 -> Columns.MEN_TITULO;
                default -> null;
            };
            // Pesquisa feita pelo título, consignatária ou correspondente de csa
            if ((filtro != null) && ((filtro_tipo == 4) || (filtro_tipo == 8))) {
                criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);
                exibeColunaCsa = false;
            } else if ((filtro != null) && (filtro_tipo == 3)) {
                criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);
                criterio.setAttribute(Columns.CSA_CODIGO, filtroCsa);
                exibeColunaCsa = true;
            } else {
                criterio.setAttribute(campo, "");
                exibeColunaCsa = false;
            }
            int total = 0;
            try {
                total = mensagemController.countMensagem(criterio, responsavel);
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
            final int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                if (request.getParameter("offset") != null) {
                    offset = Integer.parseInt(request.getParameter("offset"));
                }
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }

            // Monta lista de parâmetros através dos parâmetros de request
            final Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            // Ignora os parâmetros abaixo
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

            lstMensagens = mensagemController.lstMensagem(criterio, offset, size, responsavel);
            final String linkListagem = "../v3/manterMensagem?acao=listar";
            configurarPaginador(linkListagem, "rotulo.paginacao.titulo.mensagem", total, JspHelper.LIMITE, requestParams, false, request, model);

        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        model.addAttribute("filtro", filtro);
        model.addAttribute("filtro_tipo", filtro_tipo);
        model.addAttribute("filtro_csa", !TextHelper.isNull(filtroCsa) ? TextHelper.join(filtroCsa, ";") : "");
        model.addAttribute("podeEnviarEmail", podeEnviarEmail);
        model.addAttribute("consignatarias", consignatarias);
        model.addAttribute("lstMensagens", lstMensagens);
        model.addAttribute("exibeColunaCsa", exibeColunaCsa);

        return viewRedirect("jsp/manterMensagem/listarMensagens", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServicoControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            //Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final String menCodigo = JspHelper.verificaVarQryStr(request, "menCodigo");
            final String menTitulo = JspHelper.verificaVarQryStr(request, "menTitulo");
            String menTexto = JspHelper.verificaVarQryStr(request, "innerTemp");
            final String funCodigo = JspHelper.verificaVarQryStr(request, "funCodigo");

            final String menSequencia = JspHelper.verificaVarQryStr(request, "menSequencia");
            final String menExibeCse = JspHelper.verificaVarQryStr(request, "menExibeCse");
            final String menExibeCsa = JspHelper.verificaVarQryStr(request, "menExibeCsa");
            final String menExibeCor = JspHelper.verificaVarQryStr(request, "menExibeCor");
            final String menExibeOrg = JspHelper.verificaVarQryStr(request, "menExibeOrg");
            final String menExibeSer = JspHelper.verificaVarQryStr(request, "menExibeSer");
            final String menExibeSup = JspHelper.verificaVarQryStr(request, "menExibeSup");
            String menData = JspHelper.verificaVarQryStr(request, "menData");
            final String menPermiteLerDepois = JspHelper.verificaVarQryStr(request, "menPermiteLerDepois");
            final String menNotificarCseLeitura = JspHelper.verificaVarQryStr(request, "menNotificarCseLeitura");
            final String menBloqCsaSemLeitura = JspHelper.verificaVarQryStr(request, "menBloqCsaSemLeitura");
            final String menPublica = JspHelper.verificaVarQryStr(request, "menPublica");
            final String menLidaIndividualmente = JspHelper.verificaVarQryStr(request, "menLidaIndividualmente");
            final String menPushNotificationSer = JspHelper.verificaVarQryStr(request, "menPushNotificationSer");

            // Busca as naturezas de serviço
            final List<TransferObject> naturezas = servicoController.lstNaturezasServicos(false);
            String linkDinamico = "";

            MensagemTO menTO = new MensagemTO(menCodigo);
            if (!TextHelper.isNull(menCodigo)) {
                // Apenas lista a mensagem existente para edição posterior
                menTO = mensagemController.findMensagem(menTO, responsavel);
                menData = DateHelper.toDateTimeString(menTO.getMenData());
            }

            if ((menSequencia != null) && !"".equals(menSequencia)) {
                final short menSequenciaInt = Short.parseShort(menSequencia);
                menTO.setMenSequencia(menSequenciaInt);
            } else if (TextHelper.isNull(menTO.getMenCodigo())) {
                menTO.setMenSequencia(null);
            }
            if ((menExibeCse != null) && !"".equals(menExibeCse)) {
                menTO.setMenExibeCse(menExibeCse);
            } else if (TextHelper.isNull(menTO.getMenCodigo())) {
                menTO.setMenExibeCse("N");
            }
            if ((menExibeOrg != null) && !"".equals(menExibeOrg)) {
                menTO.setMenExibeOrg(menExibeOrg);
            } else if (TextHelper.isNull(menTO.getMenCodigo())) {
                menTO.setMenExibeOrg("N");
            }
            if ((menExibeCsa != null) && !"".equals(menExibeCsa)) {
                menTO.setMenExibeCsa(menExibeCsa);
            } else if (TextHelper.isNull(menTO.getMenCodigo())) {
                menTO.setMenExibeCsa("N");
            }
            if ((menExibeCor != null) && !"".equals(menExibeCor)) {
                menTO.setMenExibeCor(menExibeCor);
            } else if (TextHelper.isNull(menTO.getMenCodigo())) {
                menTO.setMenExibeCor("N");
            }
            if ((menExibeSer != null) && !"".equals(menExibeSer)) {
                menTO.setMenExibeSer(menExibeSer);
            } else if (TextHelper.isNull(menTO.getMenCodigo())) {
                menTO.setMenExibeSer("N");
            }
            if ((menExibeSup != null) && !"".equals(menExibeSup)) {
                menTO.setMenExibeSup(menExibeSup);
            } else if (TextHelper.isNull(menTO.getMenCodigo())) {
                menTO.setMenExibeSup("N");
            }
            if ((menPublica != null) && !"".equals(menPublica)) {
                menTO.setMenPublica(menPublica);
            } else if (TextHelper.isNull(menTO.getMenPublica())) {
                menTO.setMenPublica("N");
            }

            if (((menLidaIndividualmente != null) && !"".equals(menLidaIndividualmente)) || TextHelper.isNull(menTO.getMenLidaIndividualmente())) {
                menTO.setMenLidaIndividualmente(menLidaIndividualmente);
            }

            menTO.setMenExigeLeitura("S");

            if (!TextHelper.isNull(menPermiteLerDepois)) {
                menTO.setMenPermiteLerDepois(menPermiteLerDepois);
            } else if (TextHelper.isNull(menTO.getMenCodigo())) {
                menTO.setMenPermiteLerDepois(CodedValues.TPC_SIM);
            }
            if (!TextHelper.isNull(menNotificarCseLeitura)) {
                menTO.setMenNotificarCseLeitura(menNotificarCseLeitura);
            } else if (TextHelper.isNull(menTO.getMenCodigo())) {
                menTO.setMenNotificarCseLeitura(CodedValues.TPC_NAO);
            }
            if (!TextHelper.isNull(menBloqCsaSemLeitura)) {
                menTO.setMenBloqCsaSemLeitura(menBloqCsaSemLeitura);
            } else if (TextHelper.isNull(menTO.getMenCodigo())) {
                menTO.setMenBloqCsaSemLeitura(CodedValues.TPC_NAO);
            }
            if (!TextHelper.isNull(menPushNotificationSer)) {
                menTO.setMenPushNotificationSer(menPushNotificationSer);
            } else if (TextHelper.isNull(menTO.getMenCodigo())) {
                menTO.setMenPushNotificationSer(CodedValues.TPC_NAO);
            }
            if (!TextHelper.isNull(funCodigo)) {
                menTO.setFunCodigo(funCodigo);
            }

            if ("".equals(menTexto) && TextHelper.isNull(menTO.getMenCodigo())) {
                menTO.setMenTexto(menTexto);
            } else {
                menTexto = menTO.getMenTexto();
            }

            String titulo = null;
            if (!TextHelper.isNull(menCodigo)) {
                linkDinamico = "../v3/manterMensagem?acao=editar";
                titulo = ApplicationResourcesHelper.getMessage("rotulo.editar.mensagem", responsavel);
            } else {
                linkDinamico = "../v3/manterMensagem?acao=inserir";
                titulo = ApplicationResourcesHelper.getMessage("rotulo.criar.mensagem", responsavel);
            }

            final Object paramMenLidaIndividualmente = ParamSist.getInstance().getParam(CodedValues.TPC_DIAS_PARA_LEITURA_DE_MENSAGEM_INDIVIDUALMENTE, responsavel);
            int paramMenLidaIndividualmenteInt = 0;

            if (TextHelper.isNum(paramMenLidaIndividualmente)) {
                paramMenLidaIndividualmenteInt = Integer.parseInt(paramMenLidaIndividualmente.toString());
            }

            model.addAttribute("paramMenLidaIndividualmenteInt", paramMenLidaIndividualmenteInt);

            List<TransferObject> consignatarias = null;
            try {
                consignatarias = mensagemController.lstConsignatarias(menCodigo, responsavel);
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            List<TransferObject> funcoes = null;
            try {
                funcoes = usuarioController.lstFuncoes(AcessoSistema.ENTIDADE_CSA, responsavel);
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            TransferObject arquivoMensagem;

            try {
                arquivoMensagem = arquivoController.findArquivoMensagem(menCodigo, responsavel);
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final boolean podeEnviarPushNotificationEmMassa = responsavel.temPermissao(CodedValues.FUN_ENVIAR_MENSAGEM_MASSA_PUSH_NOTIFICATION);

            String paramExtensoes = (String) ParamSist.getInstance().getParamOrDefault(CodedValues.TPC_EXTENSOES_PERMITIDAS_UPLOAD_ANEXO, null, responsavel);
            String[] extensoesPermitidas = !TextHelper.isNull(paramExtensoes) ? paramExtensoes.split(",") : null;

            model.addAttribute("linkDinamico", linkDinamico);
            model.addAttribute("titulo", titulo);
            model.addAttribute("menTitulo", menTitulo);
            model.addAttribute("menData", menData);
            model.addAttribute("menTO", menTO);
            model.addAttribute("menSequencia", menSequencia);
            model.addAttribute("menCodigo", menCodigo);
            model.addAttribute("menTexto", menTexto);
            model.addAttribute("funCodigo", funCodigo);
            model.addAttribute("responsavel", responsavel);
            model.addAttribute("consignatarias", consignatarias);
            model.addAttribute("funcoes", funcoes);
            model.addAttribute("naturezas", naturezas);
            model.addAttribute("arquivo", arquivoMensagem);
            model.addAttribute("podeEnviarPushNotificationEmMassa", podeEnviarPushNotificationEmMassa);
            model.addAttribute("extensoesPermitidas", extensoesPermitidas);

            return viewRedirect("jsp/manterMensagem/editarMensagem", request, session, model, responsavel);

        } catch (final MensagemControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=inserir" })
    public String inserir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ZetraException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final UploadHelper uploadHelper = new UploadHelper();

        try {
            final ParamSist ps = ParamSist.getInstance();

            // Tamanho máximo do arquivo
            int maxSize = 0;
            if (responsavel.isCseSupOrg()) {
                maxSize = !TextHelper.isNull(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSE_ORG, responsavel)) ? Integer.parseInt(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSE_ORG, responsavel).toString()) : 30;
            } else if (responsavel.isCsaCor()) {
                maxSize = !TextHelper.isNull(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSA_COR, responsavel)) ? Integer.parseInt(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSA_COR, responsavel).toString()) : 1;
            }
            maxSize = maxSize * 1024 * 1024;

            // Processa a requisição para obter os arquivos e parâmetros
            uploadHelper.processarRequisicao(request.getServletContext(), request, maxSize);
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String menCodigo = JspHelper.verificaVarQryStr(request, uploadHelper, "menCodigo");
        final String menTitulo = JspHelper.verificaVarQryStr(request, uploadHelper, "menTitulo");
        final String menTexto = JspHelper.verificaVarQryStr(request, uploadHelper, "innerTemp");
        final String funCodigo = JspHelper.verificaVarQryStr(request, uploadHelper, "funCodigo");

        final String menSequencia = JspHelper.verificaVarQryStr(request, uploadHelper, "menSequencia");
        final String menExibeCse = JspHelper.verificaVarQryStr(request, uploadHelper, "menExibeCse");
        final String menExibeCsa = JspHelper.verificaVarQryStr(request, uploadHelper, "menExibeCsa");
        final String menExibeCor = JspHelper.verificaVarQryStr(request, uploadHelper, "menExibeCor");
        final String menExibeOrg = JspHelper.verificaVarQryStr(request, uploadHelper, "menExibeOrg");
        final String menExibeSer = JspHelper.verificaVarQryStr(request, uploadHelper, "menExibeSer");
        final String menExibeSup = JspHelper.verificaVarQryStr(request, uploadHelper, "menExibeSup");
        final String menPermiteLerDepois = JspHelper.verificaVarQryStr(request, uploadHelper, "menPermiteLerDepois");
        final String menNotificarCseLeitura = JspHelper.verificaVarQryStr(request, uploadHelper, "menNotificarCseLeitura");
        final String menBloqCsaSemLeitura = JspHelper.verificaVarQryStr(request, uploadHelper, "menBloqCsaSemLeitura");
        final String menPublica = JspHelper.verificaVarQryStr(request, uploadHelper, "menPublica");
        final String menLidaIndividualmente = JspHelper.verificaVarQryStr(request, uploadHelper, "menLidaIndividualmente");
        final String csaCodigos = JspHelper.verificaVarQryStr(request, uploadHelper, "CSA_CODIGO");
        final String nseCodigos = JspHelper.verificaVarQryStr(request, uploadHelper, "NSE_CODIGO");
        final String nomeAnexo = JspHelper.verificaVarQryStr(request, uploadHelper, "FILE1");
        final String menPushNotificationSer = JspHelper.verificaVarQryStr(request, uploadHelper, "menPushNotificationSer");

        MensagemTO menTO = null;
        Date menDate = null;

        if ((!TextHelper.isNull(menPushNotificationSer) && "S".equals(menPushNotificationSer)) && !enviarPushNotificationEmLote(request, response, session, model, responsavel)) {
		    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}

        if (!TextHelper.isNull(menPublica) && CodedValues.TPC_SIM.equals(menPublica) && !TextHelper.isNull(nomeAnexo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            // Inserindo nova mensagem
            menTO = new MensagemTO(menCodigo);
            menTO.setUsuCodigo(responsavel.getUsuCodigo());

            if (!"".equals(menTitulo) && (menTitulo != null)) {
                menTO.setMenTitulo(menTitulo);
            }

            final Calendar hoje = Calendar.getInstance();
            menDate = hoje.getTime();
            menTO.setMenData(menDate);

            if ((menSequencia != null) && !"".equals(menSequencia)) {
                final short menSequenciaInt = Short.parseShort(menSequencia);
                menTO.setMenSequencia(menSequenciaInt);
            } else {
                menTO.setMenSequencia(null);
            }
            if ((menExibeCse != null) && !"".equals(menExibeCse)) {
                menTO.setMenExibeCse(menExibeCse);
            } else {
                menTO.setMenExibeCse("N");
            }
            if ((menExibeOrg != null) && !"".equals(menExibeOrg)) {
                menTO.setMenExibeOrg(menExibeOrg);
            } else {
                menTO.setMenExibeOrg("N");
            }
            if ((menExibeCsa != null) && !"".equals(menExibeCsa)) {
                menTO.setMenExibeCsa(menExibeCsa);
            } else {
                menTO.setMenExibeCsa("N");
                // Remove as entradas que relacionam a mensagem com as consignatarias
                mensagemController.removeMensagemCsa(menTO.getMenCodigo(), responsavel);
            }
            if ((menExibeCor != null) && !"".equals(menExibeCor)) {
                menTO.setMenExibeCor(menExibeCor);
            } else {
                menTO.setMenExibeCor("N");
            }
            if ((menExibeSer != null) && !"".equals(menExibeSer)) {
                menTO.setMenExibeSer(menExibeSer);
            } else {
                menTO.setMenExibeSer("N");
            }
            if ((menExibeSup != null) && !"".equals(menExibeSup)) {
                menTO.setMenExibeSup(menExibeSup);
            } else {
                menTO.setMenExibeSup("N");
            }

            menTO.setMenExigeLeitura("S");

            if (!TextHelper.isNull(menPermiteLerDepois)) {
                menTO.setMenPermiteLerDepois(menPermiteLerDepois);
            } else {
                menTO.setMenPermiteLerDepois(CodedValues.TPC_SIM);
            }
            if (!TextHelper.isNull(menNotificarCseLeitura)) {
                menTO.setMenNotificarCseLeitura(menNotificarCseLeitura);
            } else {
                menTO.setMenNotificarCseLeitura(CodedValues.TPC_NAO);
            }
            if (!TextHelper.isNull(menBloqCsaSemLeitura)) {
                menTO.setMenBloqCsaSemLeitura(menBloqCsaSemLeitura);
            } else {
                menTO.setMenBloqCsaSemLeitura(CodedValues.TPC_NAO);
            }
            if (!TextHelper.isNull(menPushNotificationSer)) {
                menTO.setMenPushNotificationSer(menPushNotificationSer);
            } else {
                menTO.setMenPushNotificationSer(CodedValues.TPC_NAO);
            }
            if (!TextHelper.isNull(funCodigo)) {
                menTO.setFunCodigo(funCodigo);
            }
            if (!TextHelper.isNull(menPublica)) {
                menTO.setMenPublica(menPublica);
            }
            if ((menLidaIndividualmente != null) && !"".equals(menLidaIndividualmente)) {
                menTO.setMenLidaIndividualmente(menLidaIndividualmente);
            }
            if (!TextHelper.isNull(menTexto)) {
                menTO.setMenTexto(menTexto);
            } else {
                // Repassa o token salvo, pois o método irá revalidar o token
                request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
                // Aviso caso a mensagem esteja sendo salva sem um texto
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.escreva.texto.mensagem", responsavel));
                return iniciar(request, response, session, model);
            }

            // Cria a nova mensagem
            if ((menTO.getMenTitulo() != null) && !"".equals(menTO.getMenTitulo()) && (menTO.getMenTexto() != null) && !"".equals(menTO.getMenTexto())) {
                menCodigo = mensagemController.createMensagem(menTO, responsavel);

                if ((nseCodigos != null) && !nseCodigos.isEmpty()) {
                    final Set<String> csaCodigosLista = buscaCsaAtivo(nseCodigos, responsavel);
                    for (final String csaCodigo : csaCodigosLista) {
                        mensagemController.createMensagemCsa(menCodigo, csaCodigo, responsavel);
                    }
                } else {
                    // Cria entradas para cada consignataria e a mensagem no banco de dados
                    String[] vetorCsa;
                    if ((csaCodigos != null) && !"".equals(csaCodigos) && ("S".equals(menTO.getMenExibeCsa()) || "S".equals(menTO.getMenExibeCor()))) {
                        vetorCsa = csaCodigos.split(",");
                        for (final String element : vetorCsa) {
                            // Cria no banco as entradas que relacionam consignatarias com a mensagem
                            mensagemController.createMensagemCsa(menCodigo, element, responsavel);
                        }
                    }
                }

                // verifica anexo

                if (!TextHelper.isNull(nomeAnexo)) {
                    final String idAnexo = session.getId();

                    final File anexo = UploadHelper.retornaArquivoAnexoTemporario(nomeAnexo, idAnexo, responsavel);
                    final byte[] fileContent = FileUtils.readFileToByteArray(anexo);
                    final byte[] conteudoArquivoBase64 = Base64.getEncoder().encode(fileContent);
                    final String arqConteudo = new String(conteudoArquivoBase64);

                    arquivoController.createArquivoMensagem(menCodigo, TipoArquivoEnum.ARQUIVO_MENSAGEM.getCodigo(), arqConteudo, nomeAnexo, responsavel);
                }

                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.confirmacao.mensagem.criada.sucesso", responsavel));
                final String link = "../v3/manterMensagem?acao=listar";
                model.addAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(link, request)));
                return "jsp/redirecionador/redirecionar";
            }

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        return listar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws MensagemControllerException, ServicoControllerException, ConvenioControllerException {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final UploadHelper uploadHelper = new UploadHelper();

        try {
            final ParamSist ps = ParamSist.getInstance();

            // Tamanho máximo do arquivo
            int maxSize = 0;
            if (responsavel.isCseSupOrg()) {
                maxSize = !TextHelper.isNull(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSE_ORG, responsavel)) ? Integer.parseInt(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSE_ORG, responsavel).toString()) : 30;
            } else if (responsavel.isCsaCor()) {
                maxSize = !TextHelper.isNull(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSA_COR, responsavel)) ? Integer.parseInt(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSA_COR, responsavel).toString()) : 1;
            }
            maxSize = maxSize * 1024 * 1024;

            // Processa a requisição para obter os arquivos e parâmetros
            uploadHelper.processarRequisicao(request.getServletContext(), request, maxSize);
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String menCodigo = JspHelper.verificaVarQryStr(request, uploadHelper, "menCodigo");
        final String menTitulo = JspHelper.verificaVarQryStr(request, uploadHelper, "menTitulo");
        String menTexto = JspHelper.verificaVarQryStr(request, uploadHelper, "innerTemp");
        final String csaCodigos = JspHelper.verificaVarQryStr(request, uploadHelper, "CSA_CODIGO");
        final String nseCodigos = JspHelper.verificaVarQryStr(request, uploadHelper, "NSE_CODIGO");
        final String funCodigo = JspHelper.verificaVarQryStr(request, uploadHelper, "funCodigo");

        // Faz as substituições necessárias para que o editor possa ler o que foi salvo no banco de dados
        menTexto = menTexto.replace("&quot;", "\"");

        final String menSequencia = JspHelper.verificaVarQryStr(request, uploadHelper, "menSequencia");
        final String menExibeCse = JspHelper.verificaVarQryStr(request, uploadHelper, "menExibeCse");
        final String menExibeCsa = JspHelper.verificaVarQryStr(request, uploadHelper, "menExibeCsa");
        final String menExibeCor = JspHelper.verificaVarQryStr(request, uploadHelper, "menExibeCor");
        final String menExibeOrg = JspHelper.verificaVarQryStr(request, uploadHelper, "menExibeOrg");
        final String menExibeSer = JspHelper.verificaVarQryStr(request, uploadHelper, "menExibeSer");
        final String menExibeSup = JspHelper.verificaVarQryStr(request, uploadHelper, "menExibeSup");
        final String menPermiteLerDepois = JspHelper.verificaVarQryStr(request, uploadHelper, "menPermiteLerDepois");
        final String menNotificarCseLeitura = JspHelper.verificaVarQryStr(request, uploadHelper, "menNotificarCseLeitura");
        final String menBloqCsaSemLeitura = JspHelper.verificaVarQryStr(request, uploadHelper, "menBloqCsaSemLeitura");
        final String menPublica = JspHelper.verificaVarQryStr(request, uploadHelper, "menPublica");
        final String menLidaIndividualmente = JspHelper.verificaVarQryStr(request, uploadHelper, "menLidaIndividualmente");
        final String nomeAnexo = JspHelper.verificaVarQryStr(request, uploadHelper, "FILE1");
        final String menPushNotificationSer = JspHelper.verificaVarQryStr(request, uploadHelper, "menPushNotificationSer");
        MensagemTO menTO = null;

        if ((!TextHelper.isNull(menPushNotificationSer) && "S".equals(menPushNotificationSer)) && !enviarPushNotificationEmLote(request, response, session, model, responsavel)) {
		    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}

        if (!TextHelper.isNull(menPublica) && CodedValues.TPC_SIM.equals(menPublica) && !TextHelper.isNull(nomeAnexo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Apenas lista a mensagem existente para edição posterior
        try {
            menTO = new MensagemTO(menCodigo);
            menTO = mensagemController.findMensagem(menTO, responsavel);

            if ("S".equals(menTO.getMenHtml())) {
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.informacao.texto.desformatado.novo.padrao", responsavel));
            }
            //Para salvar uma edição ou nova mensagem, seta os novos valores.

            menTO.setUsuCodigo(responsavel.getUsuCodigo());

            if (!"".equals(menTitulo) && (menTitulo != null)) {
                menTO.setMenTitulo(menTitulo);
            }

            if ((menSequencia != null) && !"".equals(menSequencia)) {
                final short menSequenciaInt = Short.parseShort(menSequencia);
                menTO.setMenSequencia(menSequenciaInt);
            } else {
                menTO.setMenSequencia(null);
            }
            if ((menExibeCse != null) && !"".equals(menExibeCse)) {
                menTO.setMenExibeCse(menExibeCse);
            } else {
                menTO.setMenExibeCse("N");
            }
            if ((menExibeOrg != null) && !"".equals(menExibeOrg)) {
                menTO.setMenExibeOrg(menExibeOrg);
            } else {
                menTO.setMenExibeOrg("N");
            }
            if ((menExibeCsa != null) && !"".equals(menExibeCsa)) {
                menTO.setMenExibeCsa(menExibeCsa);
            } else {
                menTO.setMenExibeCsa("N");
            }
            if ((menExibeCor != null) && !"".equals(menExibeCor)) {
                menTO.setMenExibeCor(menExibeCor);
            } else {
                menTO.setMenExibeCor("N");
            }
            if ((menExibeSer != null) && !"".equals(menExibeSer)) {
                menTO.setMenExibeSer(menExibeSer);
            } else {
                menTO.setMenExibeSer("N");
            }
            if ((menExibeSup != null) && !"".equals(menExibeSup)) {
                menTO.setMenExibeSup(menExibeSup);
            } else {
                menTO.setMenExibeSup("N");
            }
            if (!TextHelper.isNull(menPublica)) {
                menTO.setMenPublica(menPublica);
            } else {
                menTO.setMenPublica("N");
            }
            if (!TextHelper.isNull(menLidaIndividualmente)) {
                menTO.setMenLidaIndividualmente(menLidaIndividualmente);
            }

            menTO.setMenExigeLeitura("S");

            if (!TextHelper.isNull(menPermiteLerDepois)) {
                menTO.setMenPermiteLerDepois(menPermiteLerDepois);
            } else {
                menTO.setMenPermiteLerDepois(CodedValues.TPC_SIM);
            }
            if (!TextHelper.isNull(menNotificarCseLeitura)) {
                menTO.setMenNotificarCseLeitura(menNotificarCseLeitura);
            } else {
                menTO.setMenNotificarCseLeitura(CodedValues.TPC_NAO);
            }
            if (!TextHelper.isNull(menBloqCsaSemLeitura)) {
                menTO.setMenBloqCsaSemLeitura(menBloqCsaSemLeitura);
            } else {
                menTO.setMenBloqCsaSemLeitura(CodedValues.TPC_NAO);
            }
            if (!TextHelper.isNull(menPushNotificationSer)) {
                menTO.setMenPushNotificationSer(menPushNotificationSer);
            } else {
                menTO.setMenPushNotificationSer(CodedValues.TPC_NAO);
            }
            if (!TextHelper.isNull(funCodigo)) {
                menTO.setFunCodigo(funCodigo);
            }
            if (!TextHelper.isNull(menTexto)) {
                menTO.setMenTexto(menTexto);
            } else {
                // Aviso caso a mensagem esteja sendo salva sem um texto
                throw new ZetraException("mensagem.erro.escreva.texto.mensagem", responsavel);
            }

            // Faz o update se é edição da mensagem
            mensagemController.updateMensagem(menTO, responsavel);

            // Somente na edição
            if ((menCodigo != null) && !"".equals(menCodigo)) {
                // Remove todas as entradas do banco de dados relacionadas com esta mensagem
                mensagemController.removeMensagemCsa(menCodigo, responsavel);

                if ((nseCodigos != null) && !nseCodigos.isEmpty()) {
                    final Set<String> csaCodigosLista = buscaCsaAtivo(nseCodigos, responsavel);

                    if (!csaCodigosLista.isEmpty() && ("S".equals(menTO.getMenExibeCsa()) || "S".equals(menTO.getMenExibeCor()))) {
                        for (final String csaCodigo : csaCodigosLista) {
                            mensagemController.createMensagemCsa(menCodigo, csaCodigo, responsavel);
                        }
                    }

                } else {
                    // Cria entradas para cada consignataria e a mensagem no banco de dados
                    String[] vetorCsa;
                    if ((csaCodigos != null) && !"".equals(csaCodigos) && ("S".equals(menTO.getMenExibeCsa()) || "S".equals(menTO.getMenExibeCor()))) {
                        vetorCsa = csaCodigos.split(",");
                        for (final String element : vetorCsa) {
                            // Cria no banco as entradas que relacionam consignatarias com a mensagem
                            mensagemController.createMensagemCsa(menCodigo, element, responsavel);
                        }
                    }
                }
            }
            // verifica anexo
            if (!TextHelper.isNull(nomeAnexo)) {
                final String idAnexo = session.getId();

                final File anexo = UploadHelper.retornaArquivoAnexoTemporario(nomeAnexo, idAnexo, responsavel);
                final byte[] fileContent = FileUtils.readFileToByteArray(anexo);
                final byte[] conteudoArquivoBase64 = Base64.getEncoder().encode(fileContent);
                final String arqConteudo = new String(conteudoArquivoBase64);

                arquivoController.createArquivoMensagem(menCodigo, TipoArquivoEnum.ARQUIVO_MENSAGEM.getCodigo(), arqConteudo, nomeAnexo, responsavel);
            }

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alteracoes.salvas.sucesso", responsavel));

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Repassa o token salvo, pois o método irá revalidar o token
        request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

        return listar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=remover" })
    public String remover(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final ParamSession paramSession = ParamSession.getParamSession(session);
        //Valida o token de sessão para evitar a chamada direta à operação
        if ((!TextHelper.isNull(request.getParameter("excluir")) || !TextHelper.isNull(request.getParameter("menCodigo"))) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);
        String link = paramSession.getLastHistory();
        // Exclui o serviço
        if ((request.getParameter("excluir") != null) && (request.getParameter("menCodigo") != null)) {
            try {
                final MensagemTO menTO = new MensagemTO(request.getParameter("menCodigo"));
                link = "../v3/manterMensagem?acao=listar";

                // Remove todas as entradas da tabela men_csa relacionadas com a mensagem
                mensagemController.removeMensagemCsa(menTO.getMenCodigo(), responsavel);
                // Remove a mensagem
                mensagemController.removeMensagem(menTO, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.confirmacao.leitura.mensagem.excluida.sucesso", responsavel));
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }
        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(link, request)));
        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(params = { "acao=enviar" })
    public String enviar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final ParamSession paramSession = ParamSession.getParamSession(session);
        final String menCodigo = JspHelper.verificaVarQryStr(request, "menCodigo");

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!TextHelper.isNull(menCodigo) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        MensagemTO menTO = null;
        if (!TextHelper.isNull(menCodigo)) {
            try {
                menTO = new MensagemTO(menCodigo);
                menTO = mensagemController.findMensagem(menTO, responsavel);
                if (!"S".equals(menTO.getMenExibeCse()) && !"S".equals(menTO.getMenExibeSer()) && !"S".equals(menTO.getMenExibeOrg()) && !"S".equals(menTO.getMenExibeCsa()) && !"S".equals(menTO.getMenExibeCor()) && !"S".equals(menTO.getMenExibeSup())) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.mensagem.cadastrada.sem.destinatario", responsavel));
                    request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                }
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        // Verifica se o responsável possui permissão para enviar mensagem para todos os destinatários selecionados
        List<String> destinatarioPermitido = null;
        try {
            destinatarioPermitido = mensagemController.getDestinatarioMensagemPermitidaEmail(responsavel);
        } catch (final Exception ex) {
            destinatarioPermitido = new ArrayList<>();
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        // Envia a mensagem
        if ((request.getParameter("MM_update") != null) && !TextHelper.isNull(menCodigo)) {
            try {
                final List<String> papeis = new ArrayList<>();

                if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "menExibeCse"))) {
                    papeis.add(CodedValues.PAP_CONSIGNANTE);
                }
                if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "menExibeCor"))) {
                    papeis.add(CodedValues.PAP_CORRESPONDENTE);
                }
                if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "menExibeCsa"))) {
                    papeis.add(CodedValues.PAP_CONSIGNATARIA);
                }
                if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "menExibeSer"))) {
                    papeis.add(CodedValues.PAP_SERVIDOR);
                }
                if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "menExibeOrg"))) {
                    papeis.add(CodedValues.PAP_ORGAO);
                }
                if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "menExibeSup"))) {
                    papeis.add(CodedValues.PAP_SUPORTE);
                }

                if (papeis.isEmpty()) {
                    throw new MensagemControllerException("mensagem.erro.selecione.destinatario", responsavel);
                }

                if (!destinatarioPermitido.containsAll(papeis)) {
                    throw new MensagemControllerException("mensagem.erro.usuario.sem.permissao.enviar", responsavel);
                }

                // Envia a mensagem
                final int retorno = mensagemController.enviaMensagemEmail(menTO, papeis, CodedValues.TPC_SIM.equals(JspHelper.verificaVarQryStr(request, "inclurCsaBloqueda")), responsavel);

                String msg = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.leitura.mensagem.enviada.sucesso", responsavel);
                String tipoMsg = CodedValues.MSG_INFO;

                if (retorno == 0) {
                    tipoMsg = CodedValues.MSG_ALERT;
                    msg = ApplicationResourcesHelper.getMessage("mensagem.erro.mensagem.csa.enviada.parcial", responsavel);
                } else if (retorno < 0) {
                    tipoMsg = CodedValues.MSG_ERRO;
                    msg = ApplicationResourcesHelper.getMessage("mensagem.erro.mensagem.csa.nao.enviada", responsavel);
                }

                session.setAttribute(tipoMsg, msg);
                return listar(request, response, session, model);

            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }
        model.addAttribute("destinatarioPermitido", destinatarioPermitido);
        model.addAttribute("menCodigo", menCodigo);
        model.addAttribute("menTO", menTO);
        return viewRedirect("jsp/manterMensagem/enviarMensagem", request, session, model, responsavel);
    }

    private Set<String> buscaCsaAtivo(String nseCodigos, AcessoSistema responsavel) throws ServicoControllerException, ConvenioControllerException {

        // Busca serviços pelo nse_codigo selecionado na interface pelo usuário
        final List<String> svcCodigos = new ArrayList<>();
        final String[] nse = nseCodigos.split(",");
        for (final String element : nse) {
            final List<Servico> svcResultados = servicoController.findByNseCodigo(element, responsavel);
            svcResultados.forEach(svc -> {
                svcCodigos.add(svc.getSvcCodigo());
            });
        }

        // Busca csa que tem convênio ativo com o svcCodigos encontrados
        final Set<String> csaCodigosLista = new HashSet<>();
        for (final String svcCodigo : svcCodigos) {
            final List<TransferObject> listaCsaAtivas = convenioController.getCsaCnvAtivo(svcCodigo, null, true, responsavel);
            listaCsaAtivas.forEach(csa -> {
                csaCodigosLista.add(csa.getAttribute(Columns.CSA_CODIGO).toString());
            });
        }
        return csaCodigosLista;

    }

    @RequestMapping(params = { "acao=downloadArquivo" })
    public void downloadArquivo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token no dowload mas não salva um novo porque invalida o botão cancelar da interface que ainda utilizará o mesmo token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            }

            final String arqCodigo = request.getParameter("arqCodigo");
            final String menCodigo = request.getParameter("menCodigo");
            final TransferObject arquivoMensagem = arquivoController.findArquivoMensagem(menCodigo, responsavel);
            final String arqConteudo = arquivoMensagem.getAttribute(Columns.ARQ_CONTEUDO).toString();

            // Gera log de download de arquivo
            try {
                final LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.DOWNLOAD_FILE, Log.LOG_INFORMACAO);
                log.setArquivo(arqCodigo);
                log.write();
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.compact.erro.interno", responsavel) + ": " + ex.getMessage());
            }

            final byte[] conteudoArquivoBase64 = Base64.getDecoder().decode(arqConteudo);

            final String path = ParamSist.getDiretorioRaizArquivos() + "anexo" + File.separatorChar + arquivoMensagem.getAttribute(Columns.AMN_NOME) + File.separatorChar + responsavel.getUsuCodigo();

            final File diretorioDefinitivo = new File(path);
            if (!diretorioDefinitivo.exists() && !diretorioDefinitivo.mkdirs()) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.criar.diretorio.arquivo", responsavel));
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.criar.diretorio.arquivo", responsavel));
            }

            final String filepath = path + File.separatorChar + arquivoMensagem.getAttribute(Columns.AMN_NOME);
            final FileOutputStream fos = new FileOutputStream(filepath);
            fos.write(conteudoArquivoBase64);
            fos.close();

            final File arquivo = new File(filepath);
            final long tamanhoArquivoBytes = arquivo.length();

            final Set<MimeType> mimeCollection = FileHelper.detectContentType(arquivo);
            final String contentType = (mimeCollection != null) && (!mimeCollection.isEmpty()) ? mimeCollection.toArray()[0].toString() : "application/pdf";

            response.setContentType(contentType);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + arquivo.getName() + "\"");

            if (tamanhoArquivoBytes > Integer.MAX_VALUE) {
                response.addHeader("Content-Length", Long.toString(tamanhoArquivoBytes));
            } else {
                response.setContentLength((int) tamanhoArquivoBytes);
            }

            final BufferedInputStream entrada = new BufferedInputStream(new FileInputStream(arquivo));
            if (tamanhoArquivoBytes > Integer.MAX_VALUE) {
                org.apache.commons.io.IOUtils.copyLarge(entrada, response.getOutputStream());
            } else {
                org.apache.commons.io.IOUtils.copy(entrada, response.getOutputStream());
            }
            response.flushBuffer();
            entrada.close();

            arquivo.delete();

        } catch (IOException | ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }
    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            final ParamSession paramSession = ParamSession.getParamSession(session);

            // Valida o token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final String arqCodigo = request.getParameter("arqCodigo");
            final String menCodigo = request.getParameter("menCodigo");
            arquivoController.removeArquivoMensagem(arqCodigo, menCodigo, responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.anexo.servidor.removido.sucesso", responsavel));

            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (final ArquivoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    private boolean enviarPushNotificationEmLote(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, AcessoSistema responsavel) {
    	try {
			final int quantidadeMaximaParametro = ParamSist.getIntParamSist(CodedValues.TPC_QUANTIDADE_PUSH_MAXIMO_ATINGIDO_MES, 0, responsavel);
			if (quantidadeMaximaParametro > 0) {
				final TransferObject criterio = new CustomTransferObject();
				criterio.setAttribute(Columns.TOC_CODIGO, CodedValues.TOC_ENVIO_MENSAGEM_SERVIDOR_PUSH_NOTIFICATION);
				criterio.setAttribute(CodedValues.FILTRO_OCE_DATA_INI, DateHelper.parse(DateHelper.format(DateHelper.getSystemDate(), "yyyy-MM-01"), "yyyy-MM-dd"));
				criterio.setAttribute(CodedValues.FILTRO_OCE_DATA_FIM, DateHelper.getLastDayOfMonth(DateHelper.getSystemDate()));

				final int quantidadeEnvioPushNotificationMensal = consignanteController.countOcorrenciaConsignante(criterio, responsavel);

				if (quantidadeMaximaParametro > quantidadeEnvioPushNotificationMensal) {
					final String tituloPush = ApplicationResourcesHelper.getMessage("rotulo.mensagem.push.notification.titulo.ser", responsavel);
					final String textoPush = ApplicationResourcesHelper.getMessage("rotulo.mensagem.push.notification.mensagem.ser", responsavel);
					final ProcessaPushNotificationServidor processo = new ProcessaPushNotificationServidor(tituloPush, textoPush, responsavel);
					processo.start();
				} else {
					session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.quantidade.maxima.parametro.ultrapassada", responsavel, String.valueOf(quantidadeMaximaParametro)));
					request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
					return false;
				}
			}
        } catch (final ZetraException | ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return false;
        }
    	return true;
    }
}
