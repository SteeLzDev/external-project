package com.zetra.econsig.web.controller.arquivo.upload;

import java.util.ArrayList;
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

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.UploadControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.persistence.entity.Servico;
import com.zetra.econsig.persistence.entity.TipoArquivo;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.upload.UploadController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.controller.AbstractWebController;
import com.zetra.econsig.web.servlet.AudioCaptchaServlet;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;

/**
 * <p>Title: UploadArquivoGenericoWebController</p>
 * <p>Description: Controlador Web para o caso de uso de Upload de Arquivos Genericos</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/uploadArquivoGenerico" })
public class UploadArquivoGenericoWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(UploadArquivoGenericoWebController.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ServicoController servicoController;

    @Autowired
    private UploadController uploadController;

    @Autowired
    private UsuarioController usuarioController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServicoControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            //parametros de captcha
            boolean exibeCaptcha = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            boolean exibeCaptchaAvancado = exibeCaptcha ? false : ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            boolean exibeCaptchaDeficiente = false;
            UsuarioTransferObject usuarioResp = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel);

            if (usuarioResp != null && usuarioResp.getUsuDeficienteVisual() != null && usuarioResp.getUsuDeficienteVisual().equals("S")) {
                exibeCaptcha = false;
                exibeCaptchaAvancado = false;
                exibeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            } else if (!exibeCaptcha && !exibeCaptchaAvancado) {
                //caso não tenha nenhum captcha habilitado mostra o padrão que é captcha simples
                exibeCaptcha = true;
            }

            SynchronizerToken.saveToken(request);

            // Pega as consignatárias
            CustomTransferObject criterio = null;
            List<TransferObject> consignatarias = consignatariaController.lstConsignatarias(criterio, responsavel);

            // Busca as naturezas de serviço
            List<TransferObject> naturezas = new ArrayList<>();
            naturezas = servicoController.lstNaturezasServicos(false);

            model.addAttribute("consignatarias", consignatarias);
            model.addAttribute("exibeCaptcha", exibeCaptcha);
            model.addAttribute("exibeCaptchaAvancado", exibeCaptchaAvancado);
            model.addAttribute("exibeCaptchaDeficiente", exibeCaptchaDeficiente);
            model.addAttribute("naturezas", naturezas);

        } catch (UsuarioControllerException | ConsignatariaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/uploadArquivoGenerico/uploadArquivoGenerico", request, session, model, responsavel);

    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=upload" })
    public String uploadArquivo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws UsuarioControllerException, ServicoControllerException, ConvenioControllerException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        ParamSist ps = ParamSist.getInstance();
        int maxSize = !TextHelper.isNull(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSE_ORG, responsavel)) ? Integer.parseInt(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSE_ORG, responsavel).toString()) : 30;
        maxSize = maxSize * 1024 * 1024;

        UploadHelper uploadHelper = new UploadHelper();

        try {
            uploadHelper.processarRequisicao(request.getServletContext(), request, maxSize);
        } catch (Throwable ex) {
            LOG.error(ex.getMessage(), ex);
            String msg = ex.getMessage();
            if (!TextHelper.isNull(msg)) {
                session.setAttribute(CodedValues.MSG_ERRO, msg);
            }
        }

        if (uploadHelper.getValorCampoFormulario("FORM") != null && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        //parametros de captcha
        boolean exibeCaptcha = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        boolean exibeCaptchaAvancado = exibeCaptcha ? false : ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        boolean exibeCaptchaDeficiente = false;
        UsuarioTransferObject usuarioResp = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel);

        if (usuarioResp != null && usuarioResp.getUsuDeficienteVisual() != null && usuarioResp.getUsuDeficienteVisual().equals("S")) {
            exibeCaptcha = false;
            exibeCaptchaAvancado = false;
            exibeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        } else if (!exibeCaptcha && !exibeCaptchaAvancado) {
            //caso não tenha nenhum captcha habilitado mostra o padrão que é captcha simples
            exibeCaptcha = true;
        }

        ParamSession paramSession = ParamSession.getParamSession(session);
        if (uploadHelper.getValorCampoFormulario("FORM") != null) {

            //Validação captcha
            if (usuarioResp.getUsuDeficienteVisual() == null || usuarioResp.getUsuDeficienteVisual().equals("N")) {
                if (exibeCaptcha) {
                    if (ImageCaptchaServlet.armazenaCaptcha(session.getId(), (String) session.getAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY))
                                    && !ImageCaptchaServlet.validaCaptcha(session.getId(), uploadHelper.getValorCampoFormulario("captcha"))) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                        return "jsp/redirecionador/redirecionar";
                    }
                    session.removeAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);
                } else if (exibeCaptchaAvancado) {
                    String remoteAddr = request.getRemoteAddr();

                    if (!isValidCaptcha(uploadHelper.getValorCampoFormulario("g-recaptcha-response"), remoteAddr, responsavel)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                        return "jsp/redirecionador/redirecionar";
                    }
                }
            } else if (exibeCaptchaDeficiente) {
                String captchaAnswer = uploadHelper.getValorCampoFormulario("captcha");
                String captchaCode   = (String) session.getAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                if (captchaCode == null || !captchaCode.equalsIgnoreCase(captchaAnswer)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                    request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                    return "jsp/redirecionador/redirecionar";
                }
                session.removeAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
            }

            String tipo = "generico";

            List<String> listPath = new ArrayList<>();
            String csa_codigo = uploadHelper.getValorCampoFormulario("CSA_CODIGO");
            String nse_codigo = uploadHelper.getValorCampoFormulario("NSE_CODIGO");

            String path = tipo + java.io.File.separatorChar;
            if (!TextHelper.isNull(nse_codigo) && !nse_codigo.isEmpty()) {
                // DESENV-14086 > disponibilizar este arquivo para todas as CSA's' ativas que possuem convênios ativos em serviços ativos das naturezas de serviço selecionadas.

                // Busca serviços pelo nse_codigo selecionado na interface pelo usuário
                List<String> svcCodigos = new ArrayList<>();
                String[] nse = nse_codigo.split(",");
                for (String element : nse) {
                    List<Servico> svcResultados = servicoController.findByNseCodigo(element, responsavel);
                    svcResultados.forEach(svc -> {
                        svcCodigos.add(svc.getSvcCodigo());
                    });
                }

                // Busca csa que tem convênio ativo com o svcCodigos encontrados
                Set<String> csaCodigos = new HashSet<>();
                for (String svcCodigo : svcCodigos) {
                    List<TransferObject> listaCsaAtivas = convenioController.getCsaCnvAtivo(svcCodigo, null, true, responsavel);
                    for (TransferObject csa : listaCsaAtivas) {
                        csaCodigos.add(csa.getAttribute(Columns.CSA_CODIGO).toString());
                    }
                }

                if (!csaCodigos.isEmpty()) {
                    for (String csaCodigo : csaCodigos) {
                        listPath.add(path + "csa" + java.io.File.separatorChar + csaCodigo);
                    }
                } else {
                    listPath.add(path + "cse");
                }

            } else if (csa_codigo != null && !csa_codigo.equals("")) {
                String[] csa = csa_codigo.split(",");
                for (String element : csa) {
                    listPath.add(path + "csa" + java.io.File.separatorChar + element);
                }
            } else {
                listPath.add(path + "cse");
            }

            try {
                // Salva o arquivo enviado
                uploadHelper.salvarArquivo(listPath, UploadHelper.EXTENSOES_PERMITIDAS_UPLOAD_GENERICO, null, session);

                // Envia notificação de recebimento do arquivo
                enviarEmailRecebimentoArquivo(uploadHelper.getFileName(0), responsavel);

                // Define mensagem de sucesso na sessão
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.upload.generico.sucesso", responsavel));
            } catch (ZetraException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.upload.generico.erro", responsavel) + " " + ex.getMessage());
            }
        }

        return iniciar(request, response, session, model);
    }

    private void enviarEmailRecebimentoArquivo(String nomeArquivo, AcessoSistema responsavel) throws ViewHelperException {
        try {
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_AMBIENTE_DE_TESTES, CodedValues.TPC_SIM, responsavel)) {
                // Se for ambiente de testes, não envia e-mail de recebimento de arquivos
                return;
            }

            // Busca o tipo de arquivo, e verifica se este envia notificação no upload
            TipoArquivo tar = uploadController.buscaTipoArquivoByPrimaryKey(TipoArquivoEnum.ARQUIVO_GENERICO.getCodigo(), responsavel);

            if (!tar.getTarNotificacaoUpload().equalsIgnoreCase("S")) {
                // Se o tipo de arquivo não envia notificação, então finaliza o método
                return;
            }

            // Verifica tabela de destinatarios de e-mail para determinar se é enviado ou não o e-mail
            List<String> papeisDestinatarios = uploadController.listarPapeisEnvioEmailUpload(responsavel);
            if (papeisDestinatarios == null || papeisDestinatarios.isEmpty()) {
                // Se não há configuração para destinatários de recebimento de e-mail de notificação de upload, então finaliza o método
                return;
            }

            boolean enviaEmailCSE = papeisDestinatarios.contains(CodedValues.PAP_CONSIGNANTE);
            boolean enviaEmailORG = papeisDestinatarios.contains(CodedValues.PAP_ORGAO);
            boolean enviaEmailCSA = papeisDestinatarios.contains(CodedValues.PAP_CONSIGNATARIA);
            boolean enviaEmailCOR = papeisDestinatarios.contains(CodedValues.PAP_CORRESPONDENTE);

            String tipoEmail = ApplicationResourcesHelper.getMessage("rotulo.upload.generico.titulo.email", responsavel).toString().toLowerCase();

            if (enviaEmailCSE || enviaEmailORG) {
                // Envia notificação de recebimento para papel de CSE/ORG
                EnviaEmailHelper.enviarEmailRecebimentoArquivo(tipoEmail, nomeArquivo, enviaEmailCSE, enviaEmailORG, null, null, responsavel);
            }

            // Envia notificação de recebimento para papel de CSA/COR somente se o parâmetro 545 estiver habilitado
            if ((enviaEmailCSA || enviaEmailCOR) && ParamSist.getBoolParamSist(CodedValues.TPC_ENVIA_EMAIL_UPLOAD_ARQ_CSE_PARA_CSA, responsavel)) {
                EnviaEmailHelper.enviarEmailUploadArquivoCsa(tipoEmail, nomeArquivo, enviaEmailCSA, enviaEmailCOR, null, responsavel);
            }
        } catch (UploadControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException(ex);
        }
    }
}
