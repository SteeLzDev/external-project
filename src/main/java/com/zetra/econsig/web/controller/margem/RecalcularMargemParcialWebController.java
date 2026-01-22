package com.zetra.econsig.web.controller.margem;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaCalculoMargem;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: RecalcularMargemGeralWebController</p>
 * <p>Description: Controlador Web para o caso de uso Recalcular Margem Parcial.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: rodrigo.rosa $
 * $Revision: 29011 $
 * $Date: 2020-03-25 13:25:44 -0300 (qui, 25 mar 2020) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/recalcularMargemParcial" })
public class RecalcularMargemParcialWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RecalcularMargemParcialWebController.class);

    private static final String CHAVE_PROCESSO = "PROCESSO_FOLHA(MARGEM/RETORNO)";

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Como vem da página inicial apenas salva o token
        SynchronizerToken.saveToken(request);

        boolean matriculaApenasNumerica = ParamSist.paramEquals(CodedValues.TPC_MATRICULA_NUMERICA, CodedValues.TPC_SIM, responsavel);

        @SuppressWarnings({ "unchecked", "rawtypes" })
        List<TransferObject> servidores = !TextHelper.isNull(request.getAttribute("servidores")) ? (List) request.getAttribute("servidores") : null;

        // Verifica se existe algum processo rodando para o usuário
        boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(CHAVE_PROCESSO, session);

        // Arquivo a ser processado
        String fileName = JspHelper.verificaVarQryStr(request, "FILE1");
        String direction = JspHelper.verificaVarQryStr(request, "direction");

        model.addAttribute("direction", direction);
        model.addAttribute("matriculaApenasNumerica", matriculaApenasNumerica);
        model.addAttribute("servidores", servidores);
        model.addAttribute("chave", CHAVE_PROCESSO);
        model.addAttribute("temProcessoRodando", temProcessoRodando);
        model.addAttribute("fileName", fileName);

        return viewRedirect("jsp/recalcularMargem/recalcularMargemParcial", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=pesquisar" })
    public String pesquisar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("direction", request.getAttribute( "direction"));

        // Arquivo a ser processado
        String fileName = JspHelper.verificaVarQryStr(request, "FILE1");
        List<TransferObject> servidores = new ArrayList<>();

        try {
            if (!TextHelper.isNull(fileName)) {
                String hashDir = session.getId();
                String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
                String diretorioDestinoUploadHelper = UploadHelper.SUBDIR_ARQUIVOS_TEMPORARIOS + File.separatorChar + "anexo" + File.separatorChar + hashDir;
                String file = diretorioRaizArquivos + File.separator + diretorioDestinoUploadHelper + File.separatorChar + fileName;

                List<String> fileToList = FileHelper.readAllToList(file);
                if (fileToList != null && !fileToList.isEmpty()) {
                    String estIdentificador = null;
                    String orgIdentificador = null;
                    String rseMatricula = null;

                    Iterator<String> iteFileToList = fileToList.iterator();
                    while (iteFileToList.hasNext()) {
                        String linha = iteFileToList.next();
                        try {
                            String[] registro = linha.split(",|;");

                            estIdentificador = registro[0].trim();
                            orgIdentificador = registro[1].trim();
                            rseMatricula = registro[2].trim();
                        } catch (Exception ex) {
                            LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.erro.recalcula.margem.layout.invalido", responsavel));
                            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.erro.recalcula.margem.layout.invalido", responsavel));
                            return iniciar(request, response, session, model);
                        }

                        List<TransferObject> servidor = pesquisarServidorController.pesquisaServidorExato("CSE", CodedValues.CSE_CODIGO_SISTEMA, estIdentificador, orgIdentificador, rseMatricula, null, responsavel);
                        if (servidor != null && !servidor.isEmpty()) {
                            servidores.addAll(servidor);
                            request.setAttribute("servidores", servidores);
                        } else {
                            LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.log.recalcula.margem.servidor.nao.encontrado.arquivo", responsavel, linha));
                            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.erro.recalcula.margem.layout.invalido", responsavel));
                        }
                    }
                }

            } else {
                String[] matriculas = JspHelper.verificaVarQryStr(request, "MATRICULAS").split(";");
                if (matriculas != null && matriculas.length > 0) {
                    servidores = pesquisarServidorController.pesquisaServidorExato("CSE", CodedValues.CSE_CODIGO_SISTEMA, null, null, Arrays.asList(matriculas), responsavel);
                    request.setAttribute("servidores", servidores);
                }
            }
            if (servidores == null || servidores.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.selecionar.servidor.erro.nenhum.registro", responsavel));
            }
        } catch (ServidorControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=confirmar" })
    public String confirmarRecalculoMargemParcial(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("direction", request.getAttribute( "direction"));
        // Verifica se existe algum processo rodando para o usuário
        boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(CHAVE_PROCESSO, session);
        if (!temProcessoRodando) {
            String tipoEntidade = "RSE";
            String[] codigoEntidade = request.getParameterValues("RSE_CODIGO");
            if (codigoEntidade != null && codigoEntidade.length > 0) {
                // Criar processo de recálculo de margem
                ProcessaCalculoMargem processo = new ProcessaCalculoMargem(tipoEntidade, Arrays.asList(codigoEntidade), responsavel);
                processo.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.recalcula.margem.servidores.titulo", responsavel));
                processo.start();
                ControladorProcessos.getInstance().incluir(CHAVE_PROCESSO, processo);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.recalcula.margem.processando", responsavel));
                request.setAttribute("temProcessoRodando", Boolean.TRUE);
            }
        } else {
            // Se algum processo em execução, retorna mensagem de erro ao usuário
            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.erro.recalcula.margem.servidores.processo.em.execucao", responsavel));
        }

        return iniciar(request, response, session, model);
    }
}
