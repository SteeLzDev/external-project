package com.zetra.econsig.web.controller.banner;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ManterBoletoWebController</p>
 * <p>Description: Manter upload de boleto em lote</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author: igor.lucas $
 * $Revision: 30120 $
 * $Date: 2020-08-12 14:15:47 -0300 (qua, 12 ago 2020) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/manterBanner" })
public class ManterBannerWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterBannerWebController.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    private List<String> listBannerFiles(AcessoSistema responsavel) {
        return FileHelper.getFilesInDir(ParamSist.getDiretorioRaizArquivos() + "/imagem/banner");
    }

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);

        String separatorString = null;
        int total = 0;
        int size = JspHelper.LIMITE;
        List<String> listFilesOffset = new ArrayList<>();
        List<String> listFiles = listBannerFiles(responsavel);

        // Pega as consignatárias
        List<TransferObject> consignatarias = null;
        try {
            CustomTransferObject criterio = null;
            consignatarias = consignatariaController.lstConsignatarias(criterio, responsavel);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());

            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (!listFiles.isEmpty()) {
            //filtra lista de arquivos para mostrar apenas as imagens
            Iterator<String> fileIt = listFiles.iterator();
            outer: while (fileIt.hasNext()) {
                String fileName = fileIt.next();
                if (!fileName.toLowerCase().endsWith(".jpg") && !fileName.toLowerCase().endsWith(".gif") && !fileName.toLowerCase().endsWith(".inativo")) {
                    fileIt.remove();
                    continue outer;
                }
                boolean arqInDirCorreto = false;
                for (TransferObject element : consignatarias) {
                    String csaCodigo = ((CustomTransferObject) element).getAttribute(Columns.CSA_CODIGO).toString();
                    //Pattern csaDirPtrn = Pattern.compile("/" + csaCodigo + "/");File.pathSeparator
                    separatorString = File.separator.equals("\\") ? "\\" : File.separator;

                    Pattern csaDirPtrn = Pattern.compile(((separatorString.equals("\\")) ? "\\\\" : separatorString) + csaCodigo + ((separatorString.equals("\\")) ? "\\\\" : separatorString));
                    Matcher m = csaDirPtrn.matcher(fileName);
                    if (m.find()) {
                        arqInDirCorreto = true;
                        continue outer;
                    }
                }
                if (!arqInDirCorreto) {
                    fileIt.remove();
                }
            }
            total = listFiles.size();
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }
            int parcial = total - offset;
            if (parcial > size) {
                for (int i = offset; i < offset + size; i++) {
                    listFilesOffset.add(listFiles.get(i));
                }
            } else {
                for (int i = offset; i < offset + parcial; i++) {
                    listFilesOffset.add(listFiles.get(i));
                }
            }
        }

        String linkListagem = "../v3/manterBanner?acao=iniciar";
        configurarPaginador(linkListagem, "rotulo.paginacao.titulo.consignataria", total, size, null, false, request, model);

        model.addAttribute("podeEditarBanner", responsavel.temPermissao(CodedValues.FUN_EDITAR_BANNER_PROPAGANDA));
        model.addAttribute("consignatarias", consignatarias);
        model.addAttribute("listFilesOffset", listFilesOffset);
        model.addAttribute("separatorString", separatorString);

        return viewRedirect("jsp/manterBanner/editarBanner", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        String operacao = JspHelper.verificaVarQryStr(request, "operacao");
        String nomeArqRequest = JspHelper.verificaVarQryStr(request, "arq");

        if (!TextHelper.isNull(operacao) && responsavel.temPermissao(CodedValues.FUN_EDITAR_BANNER_PROPAGANDA)) {
            List<String> listFiles = listBannerFiles(responsavel);
            for (int i = 0; i < listFiles.size(); i++) {
                String fileName = listFiles.get(i);

                if (fileName.equals(nomeArqRequest)) {
                    if (operacao.equals("ativar")) {
                        File toChange = new File(ParamSist.getDiretorioRaizArquivos() + "/imagem/banner/" + fileName);
                        if (toChange.exists()) {
                            toChange.renameTo(new File(ParamSist.getDiretorioRaizArquivos() + "/imagem/banner/" + fileName.substring(0, fileName.indexOf(".inativo"))));
                            listFiles.remove(fileName);
                            fileName = fileName.substring(0, fileName.indexOf(".inativo"));
                            listFiles.add(i, fileName);

                            LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.ACTIVATE_FILE, Log.LOG_INFORMACAO);
                            try {
                                log.add(ApplicationResourcesHelper.getMessage("rotulo.conf.banner.arquivo.log", responsavel) + ": " + fileName);
                                log.write();
                                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.status.conf.banner.desbloqueia.sucesso", responsavel));
                            } catch (Exception ex) {
                                LOG.error(ex.getMessage(), ex);
                                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                            }
                        }
                    } else if (operacao.equals("bloquear")) {
                        File toChange = new File(ParamSist.getDiretorioRaizArquivos() + "/imagem/banner/" + fileName);
                        if (toChange.exists()) {
                            toChange.renameTo(new File(ParamSist.getDiretorioRaizArquivos() + "/imagem/banner/" + fileName + ".inativo"));
                            listFiles.remove(fileName);
                            fileName = fileName + ".inativo";
                            listFiles.add(i, fileName);

                            LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.DEACTIVATE_FILE, Log.LOG_INFORMACAO);
                            try {
                                log.add(ApplicationResourcesHelper.getMessage("rotulo.conf.banner.arquivo.log", responsavel) + ": " + fileName);
                                log.write();
                                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.status.conf.banner.bloqueia.sucesso", responsavel));
                            } catch (Exception ex) {
                                LOG.error(ex.getMessage(), ex);
                                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                            }
                        }
                    }
                }
            }
        }

        ParamSession paramSession = ParamSession.getParamSession(session);
        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }


    @RequestMapping(params = { "acao=upload" })
    public String upload(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        String operacao = JspHelper.verificaVarQryStr(request, "operacao");
        Object tamMaxFile = ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_ARQ_BANNER, responsavel);
        int tamMaxFileInKb = 0;

        if (TextHelper.isNull(operacao)) {
            if (tamMaxFile != null) {
                tamMaxFileInKb = Integer.valueOf((String) tamMaxFile).intValue();
            } else {
                tamMaxFileInKb = 50;
            }
            UploadHelper uploadHelper = new UploadHelper();

            try {
                uploadHelper.processarRequisicao(request.getServletContext(), request, tamMaxFileInKb * 1024);
            } catch (Throwable ex) {
                String msg = ex.getMessage();
                if (!TextHelper.isNull(msg)) {
                    session.setAttribute(CodedValues.MSG_ERRO, msg);
                }
            }

            if (uploadHelper.getValorCampoFormulario("FORM") != null) {
                List<String> listPath = new ArrayList<>();
                String csa_codigo = uploadHelper.getValorCampoFormulario("CSA_CODIGO");

                String path = "imagem" + File.separatorChar + "banner" + File.separatorChar;
                if (csa_codigo != null && !csa_codigo.equals("")) {
                    String[] csa = csa_codigo.split(",");
                    for (String element : csa) {
                        listPath.add(path + "csa" + File.separatorChar + element);
                    }
                } else {
                    listPath.add(path + "cse");
                }

                try {
                    Iterator<String> it = listPath.iterator();
                    boolean copiou = false;
                    while (it.hasNext()) {
                        path = it.next();
                        File uploadedFile = uploadHelper.salvarArquivo(path, UploadHelper.EXTENSOES_PERMITIDAS_UPLOAD_BANNER, null, session);
                        if (uploadedFile != null) {
                            FileHelper.rename(uploadedFile.getAbsolutePath(), uploadedFile.getAbsolutePath() + ".inativo");
                            copiou = true;
                        } else {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.status.conf.banner.copia.erro", responsavel));
                        }
                    }

                    if (copiou) {
                        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.status.conf.banner.copia.sucesso", responsavel));
                    }
                } catch (ZetraException e) {
                    LOG.error(e);
                    session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }
        }

        ParamSession paramSession = ParamSession.getParamSession(session);
        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }
}
