package com.zetra.econsig.web.controller.servidor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
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

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ArquivoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.arquivo.ArquivoController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.controller.AbstractWebController;

import eu.medsea.mimeutil.MimeType;

/**
 * <p>Title: ListarArquivoDispensaDigitalServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso listar arquivos de dispensa de digital do servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarArquivoDispensaDigitalServidor" })
public class ListarArquivoDispensaDigitalServidorWebController extends AbstractWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarArquivoDispensaDigitalServidorWebController.class);

    @Autowired
    public ArquivoController arquivoController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @RequestMapping(params = { "acao=listarConsultarMargem" })
    public String listarArquivoDispensaDigitalServidorConsultarMargem(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return listarArquivoDispensaDigitalServidor(rseCodigo, request, response, session, model);
    }

    @RequestMapping(params = { "acao=downloadConsultarMargem" })
    public void downloadConsultarMargem(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        download(request, response, session, model);
    }

    @RequestMapping(params = { "acao=listarReservarMargem" })
    public String listarArquivoDispensaDigitalServidorReservarMargem(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return listarArquivoDispensaDigitalServidor(rseCodigo, request, response, session, model);
    }

    @RequestMapping(params = { "acao=downloadReservarMargem" })
    public void downloadReservarMargem(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        download(request, response, session, model);
    }

    @RequestMapping(params = { "acao=listarComprarConsignacao" })
    public String listarArquivoDispensaDigitalServidorComprarConsignacao(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return listarArquivoDispensaDigitalServidor(rseCodigo, request, response, session, model);
    }

    @RequestMapping(params = { "acao=downloadComprarConsignacao" })
    public void downloadComprarConsignacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        download(request, response, session, model);
    }

    @RequestMapping(params = { "acao=listarRenegociarConsignacao" })
    public String listarArquivoDispensaDigitalServidorRenegociarConsignacao(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return listarArquivoDispensaDigitalServidor(rseCodigo, request, response, session, model);
    }

    @RequestMapping(params = { "acao=downloadRenegociarConsignacao" })
    public void downloadRenegociarConsignacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        download(request, response, session, model);
    }

    @RequestMapping(params = { "acao=listarAutorizarConsignacao" })
    public String listarArquivoDispensaDigitalServidorAutorizarConsignacao(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return listarArquivoDispensaDigitalServidor(rseCodigo, request, response, session, model);
    }

    @RequestMapping(params = { "acao=downloadAutorizarConsignacao" })
    public void downloadAutorizarConsignacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        download(request, response, session, model);
    }

    @RequestMapping(params = { "acao=listarCancelarRenegociacao" })
    public String listarArquivoDispensaDigitalServidorCancelarRenegociacao(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return listarArquivoDispensaDigitalServidor(rseCodigo, request, response, session, model);
    }

    @RequestMapping(params = { "acao=downloadCancelarRenegociacao" })
    public void downloadCancelarRenegociacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        download(request, response, session, model);
    }

    @RequestMapping(params = { "acao=listarAlongarConsignacao" })
    public String listarArquivoDispensaDigitalServidorAlongarConsignacao(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return listarArquivoDispensaDigitalServidor(rseCodigo, request, response, session, model);
    }

    @RequestMapping(params = { "acao=downloadAlongarConsignacao" })
    public void downloadAlongarConsignacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        download(request, response, session, model);
    }

    @RequestMapping(params = { "acao=listarAtualizarCompra" })
    public String listarArquivoDispensaDigitalServidorAtualizarCompra(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return listarArquivoDispensaDigitalServidor(rseCodigo, request, response, session, model);
    }

    @RequestMapping(params = { "acao=downloadAtualizarCompra" })
    public void downloadAtualizarCompra(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        download(request, response, session, model);
    }

    private String listarArquivoDispensaDigitalServidor(String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            TransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
            String linkAction = request.getParameter("linkAction");

            if (TextHelper.isNull(linkAction)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            List<String> tarCodigos = new ArrayList<>();
            tarCodigos.add(TipoArquivoEnum.ARQUIVO_DISPENSA_VALIDACAO_DIGITAL_SER.getCodigo());
            List<TransferObject> arquivos = arquivoController.listArquivoServidor(servidor.getAttribute(Columns.SER_CODIGO).toString(), tarCodigos, responsavel);

            model.addAttribute("servidor", servidor);
            model.addAttribute("arquivos", arquivos);
            model.addAttribute("linkAction", linkAction);

        } catch (ArquivoControllerException | ServidorControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/editarServidor/listarArquivoDispensaDigitalServidor", request, session, model, responsavel);
    }

    private void download(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token no dowload mas não salva um novo porque invalida o botão cancelar da interface que ainda utilizará o mesmo token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            }

            String arqCodigo = request.getParameter("arqCodigo");
            String serCodigo = request.getParameter("serCodigo");
            TransferObject arquivoServidor = arquivoController.findArquivoServidor(arqCodigo, serCodigo, responsavel);
            String arqConteudo = arquivoServidor.getAttribute(Columns.ARQ_CONTEUDO).toString();

            // Gera log de download de arquivo
            try {
                LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.DOWNLOAD_FILE, Log.LOG_INFORMACAO);
                log.setArquivo(arqCodigo);
                log.setServidor(serCodigo);
                log.write();
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.compact.erro.interno", responsavel) + ": " + ex.getMessage());
            }

            byte[] conteudoArquivoBase64 = Base64.getDecoder().decode(arqConteudo);

            ParamSist ps = ParamSist.getInstance();
            String path = ParamSist.getDiretorioRaizArquivos() + "anexo" + File.separatorChar + "tmpDispensaDigital" + File.separatorChar + responsavel.getUsuCodigo();
            String fileName = arquivoServidor.getAttribute(Columns.ASE_NOME).toString();

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

            Set<MimeType> mimeCollection = FileHelper.detectContentType(arquivo);
            String contentType = mimeCollection != null && mimeCollection.size() > 0 ? mimeCollection.toArray()[0].toString() : "application/pdf";

            response.setContentType(contentType);
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

        } catch (IOException | ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }
    }

}