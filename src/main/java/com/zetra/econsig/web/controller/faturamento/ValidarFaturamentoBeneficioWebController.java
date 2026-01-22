package com.zetra.econsig.web.controller.faturamento;

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

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.web.ArquivoDownload;
import com.zetra.econsig.exception.FaturamentoBeneficioControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaPreviaFaturamento;
import com.zetra.econsig.job.process.Processo;
import com.zetra.econsig.service.beneficios.FaturamentoBeneficioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: ValidarFaturamentoBeneficioWebController</p>
 * <p>Description: Validar Faturamento de Benefício</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/validarFaturamento" })
public class ValidarFaturamentoBeneficioWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidarFaturamentoBeneficioWebController.class);

    @Autowired
    private FaturamentoBeneficioController faturamentoBeneficioController;

    private List<ArquivoDownload> listarArquivos(String pathArquivos, String extensao, AcessoSistema responsavel) {
        // Cria filtro para seleção de arquivos pela extensão informada
        FileFilter filtro = new FileFilter() {
            @Override
            public boolean accept(File arq) {
                String arqNome = arq.getName().toLowerCase();
                return (arqNome.endsWith(extensao));
            }
        };
        File diretorioRetorno = new File(pathArquivos);
        File[] temp = diretorioRetorno.listFiles(filtro);

        List<ArquivoDownload> arquivos = null;
        List<File> arquivosCombo = new ArrayList<>();
        if (temp != null) {
            arquivosCombo.addAll(Arrays.asList(temp));

            // Ordena os arquivos baseado na data de modificação
            Collections.sort(arquivosCombo, (f1, f2) -> {
                Long d1 = f1.lastModified();
                Long d2 = f2.lastModified();
                return d2.compareTo(d1);
            });

            arquivos = ArquivoDownload.carregarArquivos(arquivosCombo, pathArquivos, null, responsavel);
        }

        return arquivos;
    }

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws FaturamentoBeneficioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String fatCodigo = JspHelper.verificaVarQryStr(request, "FAT_CODIGO");

            if (TextHelper.isNull(fatCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.FAT_CODIGO, fatCodigo);

            List<TransferObject> lstFaturamento = faturamentoBeneficioController.findFaturamento(criterio, responsavel);

            if (lstFaturamento == null || lstFaturamento.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            TransferObject faturamento = lstFaturamento.get(0);
            String csaCodigo = faturamento.getAttribute(Columns.CSA_CODIGO).toString();
            String absolutePath = ParamSist.getDiretorioRaizArquivos();
            String pathArquivos = absolutePath + File.separatorChar + "beneficio" + File.separatorChar + "fatura" + File.separatorChar + "csa" + File.separatorChar + csaCodigo + File.separatorChar + fatCodigo + File.separatorChar;
            String pathArquivosPrevia = absolutePath + File.separatorChar + "fatura" + File.separatorChar + "previa" + File.separatorChar + "csa" + File.separatorChar + csaCodigo + File.separatorChar;

            List<ArquivoDownload> arquivosCritica = listarArquivos(pathArquivos, ".txt", responsavel);
            List<ArquivoDownload> arquivosPrevia = listarArquivos(pathArquivosPrevia, ".txt", responsavel);

            String chave = "PreviaFaturamento" + "|" + responsavel.getUsuCodigo();
            Processo processoArqFat = ControladorProcessos.getInstance().getProcesso(chave);
            boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chave, session);
            Boolean validarTerminoProcesso = JspHelper.verificaVarQryStr(request, "validarTerminoProcesso").equalsIgnoreCase("true");
            if (validarTerminoProcesso && !temProcessoRodando && (processoArqFat == null || processoArqFat.isSucesso())) {
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.gerado.relatorio.sucesso", responsavel));
            }

            model.addAttribute("faturamentoBeneficio", faturamento);
            model.addAttribute("arquivosCritica", arquivosCritica);
            model.addAttribute("arquivosPrevia", arquivosPrevia);
            model.addAttribute("temProcessoRodando", temProcessoRodando);

            return viewRedirect("jsp/consultarFaturamento/validarFaturamento", request, session, model, responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=validar" })
    public String validar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws FaturamentoBeneficioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String fatCodigo = JspHelper.verificaVarQryStr(request, "FAT_CODIGO");
            String[] chkArquivoPrevia = request.getParameterValues("chkArquivoPrevia");
            List<String> arquivosPrevia = chkArquivoPrevia != null ? Arrays.asList(chkArquivoPrevia) : new ArrayList<>();

            if (TextHelper.isNull(fatCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String chave = "PreviaFaturamento" + "|" + responsavel.getUsuCodigo();
            boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chave, session);

            if (!temProcessoRodando) {
                temProcessoRodando = true;

                ProcessaPreviaFaturamento processaRel = new ProcessaPreviaFaturamento(fatCodigo, arquivosPrevia, responsavel);
                processaRel.start();
                ControladorProcessos.getInstance().incluir(chave, processaRel);
            } else {
                // Se o arquivo está sendo processado por outro usuário,
                // dá mensagem de erro ao usuário e permite que ele escolha outro arquivo
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.processando.arquivo", responsavel));
                temProcessoRodando = false;
            }

            // Repassa o token salvo, pois o método irá revalidar o token
            request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

            return iniciar(request, response, session, model);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

}
