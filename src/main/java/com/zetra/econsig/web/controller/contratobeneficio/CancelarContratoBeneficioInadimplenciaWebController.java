package com.zetra.econsig.web.controller.contratobeneficio;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.web.ArquivoDownload;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaCancelamentoInadimplenciaBeneficio;
import com.zetra.econsig.job.process.Processo;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;
import com.zetra.econsig.web.controller.arquivo.DeleteWebController;

/**
 * <p>Title: CancelarContratoBeneficioInadimplenciaWebController</p>
 * <p>Description: Controlador Web para o caso de uso Processar Cancelamento de contrato benefício por inadimplência.</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision: 30345 $
 * $Date: 2020-12-16 17:23:30 -0300 (qua, 16 dez 2020) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/cancelarContratoBeneficioInadimplencia" })
public class CancelarContratoBeneficioInadimplenciaWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CancelarContratoBeneficioInadimplenciaWebController.class);

    private static final String CHAVE_PROCESSO = "PROCESSO_CONTRATOBENEFICIOINADIMPLENCIA";

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
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            SynchronizerToken.saveToken(request);

            String absolutePath = ParamSist.getDiretorioRaizArquivos();
            String pathArquivos = absolutePath + File.separatorChar + "cancelamentoporinadimplencia" + File.separatorChar + "cse" + File.separatorChar;

            List<ArquivoDownload> arquivosCancelamento = listarArquivos(pathArquivos, ".txt", responsavel);
            List<ArquivoDownload> arquivosCritica = listarArquivos(pathArquivos, ".zip", responsavel);

            Processo processoCanc = ControladorProcessos.getInstance().getProcesso(CHAVE_PROCESSO);
            boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(CHAVE_PROCESSO, session);
            Boolean validarTerminoProcesso = JspHelper.verificaVarQryStr(request, "validarTerminoProcesso").equalsIgnoreCase("true");
            if (validarTerminoProcesso && !temProcessoRodando && (processoCanc == null || processoCanc.isSucesso())) {
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.cancelar.beneficio.inadimplente.sucesso", responsavel));
            }

            model.addAttribute("arquivosCancelamento", arquivosCancelamento);
            model.addAttribute("arquivosCritica", arquivosCritica);
            model.addAttribute("temProcessoRodando", temProcessoRodando);

            return viewRedirect("jsp/cancelarBeneficiosInadimplencia/cancelarBeneficiosInadimplencia", request, session, model, responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=processar" })
    public String processar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String nomeArquivo = JspHelper.verificaVarQryStr(request,"nomeArquivo");
            if(TextHelper.isNull(nomeArquivo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.cancelar.beneficio.inadimplente.arquivo.nao.encontrado", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            } else if (!TextHelper.isNull(nomeArquivo) && !nomeArquivo.matches(".*\\.txt")) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.processamento.cancelar.beneficio.inadimplente.extensao", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String arquivoLote = nomeArquivo.replaceAll("\\.txt", "");

            boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(CHAVE_PROCESSO, session);

            if (!temProcessoRodando) {
                temProcessoRodando = true;

                ProcessaCancelamentoInadimplenciaBeneficio processaCancBenInad = new ProcessaCancelamentoInadimplenciaBeneficio(arquivoLote, responsavel);
                processaCancBenInad.start();
                ControladorProcessos.getInstance().incluir(CHAVE_PROCESSO, processaCancBenInad);
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

    /**
    *
    * @param request
    * @param response
    * @param session
    * @param model
    * @param tipo
    * @return
     * @throws IOException
     * @throws IllegalAccessException
     * @throws InstantiationException
    */
    @RequestMapping(params = { "acao=excluirArquivo" })
    public String excluirArquivo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, IOException {
        return new DeleteWebController().excluirArquivo(request, response, session, model);
    }
}
