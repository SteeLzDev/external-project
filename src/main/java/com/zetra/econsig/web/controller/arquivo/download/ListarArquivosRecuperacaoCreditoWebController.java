package com.zetra.econsig.web.controller.arquivo.download;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ListarArquivosRecuperacaoCreditoWebController.java</p>
 * <p>Description: Controlador Web para o caso de uso Download de arquivos de Recuperação de Crédito</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Date: 2019-12-08 14:29:17 -0200 (seg, 08 dez 2019) $
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarArquivosRecuperacaoCredito" })
public class ListarArquivosRecuperacaoCreditoWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarArquivosRecuperacaoCreditoWebController.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @RequestMapping(params = { "acao=iniciar" })
    public String listarArquivoRecuperacaoCredito(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            SynchronizerToken.saveToken(request);

            String csaCodigo = null;
            List<File> arquivos = new ArrayList<>();
            String absolutePath = ParamSist.getDiretorioRaizArquivos();

            final String tipo = "recuperacao_credito";

            if (responsavel.isSup()) {
                csaCodigo = request.getParameter("CSA_CODIGO");
            } else if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCsaCodigo();
            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String pathCsa = absolutePath + File.separatorChar + tipo + File.separatorChar + "csa" + File.separatorChar + csaCodigo;
            String[] extensaoPermitidas = UploadHelper.EXTENSOES_PERMITIDAS_UPLOAD_RECUPERACAO_CREDITO;

            FileFilter filtro = arq -> {
                String arq_name = arq.getName().toLowerCase();
                for (String element : extensaoPermitidas) {
                    if (arq_name.endsWith(element)) {
                        return true;
                    }
                }
                return false;
            };

            File diretorioCsa = new File(pathCsa);
            if (!diretorioCsa.exists() && !diretorioCsa.mkdirs()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.lst.arq.recuperacao.credito.diretorio", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            boolean permiteListarRecuperacao = responsavel.temPermissao(CodedValues.FUN_ENVIAR_ARQ_RECUPERACAO_CREDITO);

            if (!permiteListarRecuperacao) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            File[] temp = diretorioCsa.listFiles(filtro);
            arquivos.addAll(Arrays.asList(temp));

            int size = JspHelper.LIMITE;
            int offset = 0;
            int total = 0;

            if (!TextHelper.isNull(csaCodigo)) {
                if (arquivos != null) {
                    Collections.sort(arquivos, (f1, f2) -> {
                        Long d1 = f1.lastModified();
                        Long d2 = f2.lastModified();
                        return d2.compareTo(d1);
                    });

                    try {
                        offset = Integer.parseInt(request.getParameter("offset"));
                    } catch (Exception ex) {
                    }
                    total = arquivos.size();
                }
            }

            String linkListagem = "../v3/listarArquivosRecuperacaoCredito?acao=iniciar";
            configurarPaginador(linkListagem, "rotulo.lista.arquivo.recuperacao.credito", total, size, null, false, request, model);

            List<TransferObject> lstConsignatarias = consignatariaController.lstConsignatarias(null, responsavel);

            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("arquivos", arquivos);
            model.addAttribute("lstConsignatarias", lstConsignatarias);
            model.addAttribute("offset", offset);
            model.addAttribute("size", size);
            model.addAttribute("pathCsa", pathCsa);

            return viewRedirect("jsp/listarArquivosRecuperacaoCredito/listarArquivosRecuperacaoCredito", request, session, model, responsavel);

        } catch (ConsignatariaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
