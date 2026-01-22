package com.zetra.econsig.web.controller.servidor;

import java.io.File;
import java.io.FileFilter;
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

import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaDesligadoBloqueado;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/importarServidoresBloqueadosDesligados" })
public class ImportarServidoresBloqueadosDesligadosWebController extends ControlePaginacaoWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportarServidoresBloqueadosDesligadosWebController.class);

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            //Valida o token de sessao para evitar a chamada direta a operacao
            if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "operacao")) && !SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            if (!responsavel.isCseSup() && !responsavel.temPermissao(CodedValues.FUN_IMP_SER_DESLIGADO_BLOQUEADO)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String absolutePath = ParamSist.getDiretorioRaizArquivos();

            boolean temProcessoRodando = false;

            // Verifica se existe algum processo rodando para o usuario
            String chave1 = "DESLIGADO" + "|" + responsavel.getUsuCodigo();
            temProcessoRodando = ControladorProcessos.getInstance().verificar(chave1, session);

            absolutePath += File.separatorChar + "desligado" + File.separatorChar + "cse" + File.separatorChar;

            File diretorio = new File(absolutePath);
            if (!diretorio.exists() && !diretorio.mkdirs()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.diretorio.inexistente", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            FileFilter filtro = arq -> {
                String arq_name = arq.getName().toLowerCase();
                return arq.isFile() && (arq_name.endsWith(".txt") || arq_name.endsWith(".zip"));
            };

            List<File> arquivos = null;
            File[] temp = diretorio.listFiles(filtro);
            if (temp != null) {
                arquivos = new ArrayList<>();
                arquivos.addAll(Arrays.asList(temp));
            }

            if (arquivos != null) {
                Collections.sort(arquivos, (f1, f2) -> {
                    Long d1 = f1.lastModified();
                    Long d2 = f2.lastModified();
                    return d2.compareTo(d1);
                });
            }

            // Paginacao
            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }


            int total = arquivos.size();
            String linkListagem = "../v3/importarServidoresBloqueadosDesligados?acao=iniciar";
            configurarPaginador(linkListagem, "rotulo.paginacao.titulo.arquivo.servidor.bloqueado.desligado", total, size, null, false, request, model);

            model.addAttribute("absolutePath", absolutePath);
            model.addAttribute("size", size);
            model.addAttribute("offset", offset);
            model.addAttribute("arquivos", arquivos);
            model.addAttribute("temProcessoRodando", temProcessoRodando);

            return viewRedirect("jsp/importarServidoresBloqueados/importarServidoresBloqueadosDesligados", request, session, model, responsavel);

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params  = { "acao=processar" })
    public String processarArquivo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            boolean temProcessoRodando = false;
            // Verifica se existe algum processo rodando para o usuario
            String chave1 = "DESLIGADO" + "|" + responsavel.getUsuCodigo();
            temProcessoRodando = ControladorProcessos.getInstance().verificar(chave1, session);

            if (!temProcessoRodando) {
                // Se não tem processo rodando para o usuario, e o usuario
                // mandou processar um lote, então ...
                String nomeArquivoEntrada = JspHelper.verificaVarQryStr(request, "arquivo_nome");
                String fileName = ParamSist.getDiretorioRaizArquivos();
                fileName += File.separatorChar + "desligado" + File.separatorChar + "cse" + File.separatorChar + nomeArquivoEntrada;

                // Verifica se algum outro usuario esta processando o arquivo escolhido pelo usuario.
                String chave2 = "DESLIGADO" + "|" + nomeArquivoEntrada;
                temProcessoRodando = ControladorProcessos.getInstance().verificar(chave2, session);

                boolean validar = false;

                if (!temProcessoRodando) {
                    // Se o arquivo escolhido não esta sendo processado
                    // então inicia o processamento.
                    ProcessaDesligadoBloqueado processaLote = new ProcessaDesligadoBloqueado(fileName, validar, responsavel);

                    processaLote.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.lote.arquivo.processar", responsavel) + " '" + nomeArquivoEntrada + "'");

                    processaLote.start();
                    ControladorProcessos.getInstance().incluir(chave1, processaLote);
                    ControladorProcessos.getInstance().incluir(chave2, processaLote);
                } else {
                    // Se o arquivo esta sendo processando por outro usuario,
                    // da mensagem de erro ao usuario e permite que ele escolha
                    // outro arquivo
                    temProcessoRodando = false;
                }
            }

            return iniciar(request, response, session, model);

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }
    @RequestMapping(params  = { "acao=validar" })
    public String validarArquivo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            boolean temProcessoRodando = false;
            // Verifica se existe algum processo rodando para o usuario
            String chave1 = "DESLIGADO" + "|" + responsavel.getUsuCodigo();
            temProcessoRodando = ControladorProcessos.getInstance().verificar(chave1, session);

            if (!temProcessoRodando) {
                // Se não tem processo rodando para o usuario, e o usuario
                // mandou processar um lote, então ...
                String nomeArquivoEntrada = JspHelper.verificaVarQryStr(request, "arquivo_nome");
                String fileName = ParamSist.getDiretorioRaizArquivos();
                fileName += File.separatorChar + "desligado" + File.separatorChar + "cse" + File.separatorChar + nomeArquivoEntrada;

                // Verifica se algum outro usuario esta processando o arquivo escolhido pelo usuario.
                String chave2 = "DESLIGADO" + "|" + nomeArquivoEntrada;
                temProcessoRodando = ControladorProcessos.getInstance().verificar(chave2, session);

                boolean validar = true;

                if (!temProcessoRodando) {
                    // Se o arquivo escolhido não esta sendo processado
                    // então inicia o processamento.
                    ProcessaDesligadoBloqueado processaLote = new ProcessaDesligadoBloqueado(fileName, validar, responsavel);

                    processaLote.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.lote.arquivo.validar", responsavel) + " '" + nomeArquivoEntrada + "'");

                    processaLote.start();
                    ControladorProcessos.getInstance().incluir(chave1, processaLote);
                    ControladorProcessos.getInstance().incluir(chave2, processaLote);
                    session.removeAttribute(CodedValues.MSG_ALERT);
                } else {
                    // Se o arquivo esta sendo processando por outro usuario,
                    // da mensagem de erro ao usuario e permite que ele escolha
                    // outro arquivo
                    temProcessoRodando = false;
                }
            }

            return iniciar(request, response, session, model);

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }

}
