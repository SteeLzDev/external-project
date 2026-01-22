package com.zetra.econsig.web.controller.boleto;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.BoletoServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.boleto.ProcessaBoletoServidor;
import com.zetra.econsig.service.boleto.BoletoServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;
import com.zetra.econsig.web.servlet.AudioCaptchaServlet;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;

/**
 * <p>Title: ManterBoletoWebController</p>
 * <p>Description: Manter upload de boleto em lote</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/manterBoleto" })
public class ManterBoletoWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterBoletoWebController.class);

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private BoletoServidorController boletoServidorController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            String chave = "BoletoServidor" + "|" + responsavel.getUsuCodigo();
            boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chave, session);

            String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
            int filtroTipo = -1;
            try {
                filtroTipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
            } catch (Exception ex1) {
            }

            TransferObject criterio = new CustomTransferObject();

            // -------------- Seta Criterio da Listagem ------------------
            if (!TextHelper.isNull(filtro) && filtroTipo != -1) {
                String campo = null;

                switch (filtroTipo) {
                    case 2:
                        campo = Columns.SER_NOME;
                        break;
                    case 3:
                        campo = Columns.SER_CPF;
                        break;
                    default:
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);
            }
            // ---------------------------------------

            int total = boletoServidorController.countBoletoServidor(criterio, responsavel);
            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            List<TransferObject> boletoServidor = boletoServidorController.listarBoletoServidor(criterio, offset, size, responsavel);

            // Monta lista de parâmetros através dos parâmetros de request
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            // Ignora os parâmetros abaixo
            params.remove("offset");

            List<String> requestParams = new ArrayList<>(params);
            configurarPaginador("../v3/manterBoleto?acao=iniciar", "rotulo.correspondente.singular", total, size, requestParams, false, request, model);

            //Parametros de captcha
            boolean exibeCaptcha = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            boolean exibeCaptchaAvancado = exibeCaptcha ? false : ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            boolean exibeCaptchaDeficiente = false;
            UsuarioTransferObject usuarioResp = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel);

            if (usuarioResp != null && usuarioResp.getUsuDeficienteVisual() != null && usuarioResp.getUsuDeficienteVisual().equals("S")) {
                exibeCaptcha = false;
                exibeCaptchaAvancado = false;
                exibeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            } else if (!exibeCaptcha && !exibeCaptchaAvancado) {
                //Caso não tenha nenhum captcha habilitado mostra o padrão que é captcha simples
                exibeCaptcha = true;
            }

            model.addAttribute("exibeCaptcha", exibeCaptcha);
            model.addAttribute("exibeCaptchaAvancado", exibeCaptchaAvancado);
            model.addAttribute("exibeCaptchaDeficiente", exibeCaptchaDeficiente);
            model.addAttribute("boletoServidor", boletoServidor);
            model.addAttribute("temProcessoRodando", temProcessoRodando);
            model.addAttribute("responsavel", responsavel);
            model.addAttribute("filtroTipo", filtroTipo);
            model.addAttribute("filtro", filtro);

            return viewRedirect("jsp/manterBoleto/listarBoletoServidor", request, session, model, responsavel);

        } catch (BoletoServidorControllerException | UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=upload" })
    public String processar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        UploadHelper uploadHelper = new UploadHelper();
        ParamSession paramSession = ParamSession.getParamSession(session);

        try {
            ParamSist ps = ParamSist.getInstance();
            String path = ParamSist.getDiretorioRaizArquivos();

            String chave = "BoletoServidor" + "|" + responsavel.getUsuCodigo();
            boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chave, session);

            if (!temProcessoRodando) {
                try {
                    int maxSize = !TextHelper.isNull(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSA_COR, responsavel)) ? Integer.parseInt(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSA_COR, responsavel).toString()) : 1;
                    maxSize = maxSize * 1024 * 1024;

                    try {
                        uploadHelper.processarRequisicao(request.getServletContext(), request, maxSize);
                    } catch (Throwable ex) {
                        LOG.error(ex.getMessage(), ex);
                        String msg = ex.getMessage();
                        if (!TextHelper.isNull(msg)) {
                            session.setAttribute(CodedValues.MSG_ERRO, msg);
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        }
                    }
                } catch (NumberFormatException ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                if (uploadHelper.getValorCampoFormulario("FORM") != null && !SynchronizerToken.isTokenValid(request)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
                SynchronizerToken.saveToken(request);

                //Parametros de captcha
                boolean exibeCaptcha = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                boolean exibeCaptchaAvancado = exibeCaptcha ? false : ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                boolean exibeCaptchaDeficiente = false;
                UsuarioTransferObject usuarioResp = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel);

                if (usuarioResp != null && usuarioResp.getUsuDeficienteVisual() != null && usuarioResp.getUsuDeficienteVisual().equals("S")) {
                    exibeCaptcha = false;
                    exibeCaptchaAvancado = false;
                    exibeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                } else if (!exibeCaptcha && !exibeCaptchaAvancado) {
                    //Caso não tenha nenhum captcha habilitado mostra o padrão que é captcha simples
                    exibeCaptcha = true;
                }

                //Validação captcha
                if (usuarioResp.getUsuDeficienteVisual() == null || usuarioResp.getUsuDeficienteVisual().equals("N")) {
                    if (exibeCaptcha) {
                        if (ImageCaptchaServlet.armazenaCaptcha(session.getId(), (String) session.getAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY))
                                    && !ImageCaptchaServlet.validaCaptcha(session.getId(), JspHelper.verificaVarQryStr(request, uploadHelper, "captcha"))) {
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
                    String captchaAnswer = JspHelper.verificaVarQryStr(request, uploadHelper, "captcha");
                    String captchaCode   = (String) session.getAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                    if (captchaCode == null || !captchaCode.equalsIgnoreCase(captchaAnswer)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                        return "jsp/redirecionador/redirecionar";
                    }
                    session.removeAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                }

                File zipCarregado = null;
                try {
                    String outputPath = "anexo" + File.separatorChar + "tmpBoletosEmLote" + File.separatorChar + responsavel.getUsuCodigo();

                    //Remove diretório e dependências antigo caso tenha acontecido erro inesperado
                    try {
                        FileUtils.deleteDirectory(new File(path + File.separatorChar + outputPath));
                    } catch (IOException e) {
                        LOG.error("Não foi possível remover o diretório.", e);
                    }

                    String fileName = uploadHelper.getFileName(0);
                    if (!fileName.toLowerCase().endsWith(".zip")) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.copia.impossivel.arquivos.permitidos", responsavel, TextHelper.join(UploadHelper.EXTENSOES_PERMITIDAS_BOLETO_SERVIDOR, ", ")));
                        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                        return "jsp/redirecionador/redirecionar";
                    }
                    zipCarregado = uploadHelper.salvarArquivo(outputPath, UploadHelper.EXTENSOES_PERMITIDAS_BOLETO_SERVIDOR_CONTEUDO, null, session);

                } catch (ZetraException ex) {
                    LOG.error(ex.getMessage(), ex);
                    if (!TextHelper.isNull(ex.getMessageKey()) && ex.getMessageKey().equals("mensagem.erro.copia.impossivel.arquivos.permitidos")) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.upload.generico.erro", responsavel) + " " + ApplicationResourcesHelper.getMessage("mensagem.upload.em.lote.extensoes.validas.no.zip", responsavel, TextHelper.join(UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CONTRATO, ", ")));
                    } else if (ex.getMessage() != null) {
                        session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    } else {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
                    }
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                try {
                    ProcessaBoletoServidor processaComandoExterno = new ProcessaBoletoServidor(zipCarregado, boletoServidorController, responsavel);
                    processaComandoExterno.start();
                    ControladorProcessos.getInstance().incluir(chave, processaComandoExterno);

                } catch (Exception ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

            } else {
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.upload.aguarde.processamento", responsavel));
            }

            return iniciar(request, response, session, model);

        } catch (UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token
        if (!responsavel.isCsa() && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            String bosCodigo = request.getParameter("bosCodigo");
            boletoServidorController.removeBoleto(bosCodigo, responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.boleto.servidor.removido.sucesso", responsavel));

            return iniciar(request, response, session, model);
        } catch (BoletoServidorControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=download" })
    public void download(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token
            if ((!responsavel.isCsa() || !responsavel.isSer()) && !SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            }
            SynchronizerToken.saveToken(request);

            String bosCodigo = request.getParameter("bosCodigo");
            TransferObject boletoServidor = boletoServidorController.findBoletoServidor(bosCodigo, responsavel);
            String arqConteudo = boletoServidor.getAttribute(Columns.ARQ_CONTEUDO).toString();

            // Gera log de download de arquivo
            try {
                LogDelegate log = new LogDelegate(responsavel, Log.BOLETO_SERVIDOR, Log.DOWNLOAD_FILE, Log.LOG_INFORMACAO);
                log.setBoletoServidor(bosCodigo);
                log.write();
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.compact.erro.interno", responsavel) + ": " + ex.getMessage());
            }

            byte[] conteudoArquivoBase64 = Base64.getDecoder().decode(arqConteudo);

            String path = ParamSist.getDiretorioRaizArquivos() + "anexo" + File.separatorChar + "tmpBoletosEmLote" + File.separatorChar + responsavel.getUsuCodigo();
            String fileName = bosCodigo + ".pdf";

            File diretorioDefinitivo = new File(path);
            if (!diretorioDefinitivo.exists() && !diretorioDefinitivo.mkdirs()) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.criar.diretorio.arquivo", responsavel));
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.criar.diretorio.arquivo", responsavel));
            }

            String filepath = path + File.separatorChar + fileName;
            FileOutputStream fos = new FileOutputStream(filepath);
            fos.write(conteudoArquivoBase64);
            fos.close();

            File arquivo = new File(filepath);
            long tamanhoArquivoBytes = arquivo.length();
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + arquivo.getName() + "\"");

            if (tamanhoArquivoBytes > Integer.MAX_VALUE) {
                response.addHeader("Content-Length", Long.toString(tamanhoArquivoBytes));
            } else {
                response.setContentLength((int) tamanhoArquivoBytes);
            }

            BufferedInputStream entrada = new BufferedInputStream(new FileInputStream(arquivo));
            if (tamanhoArquivoBytes > Integer.MAX_VALUE) {
                org.apache.commons.io.IOUtils.copyLarge(entrada, response.getOutputStream());
            } else {
                org.apache.commons.io.IOUtils.copy(entrada, response.getOutputStream());
            }
            response.flushBuffer();
            entrada.close();

            arquivo.delete();

        } catch (BoletoServidorControllerException | IOException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

    }

}
