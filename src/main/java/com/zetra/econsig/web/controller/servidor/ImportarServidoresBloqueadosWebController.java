package com.zetra.econsig.web.controller.servidor;

import java.io.File;
import java.io.FileFilter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.web.ArquivoDTO;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaBloqueioServidor;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ImportarServidoresBloqueadosWebController</p>
 * <p>Description: Controlador Web para o caso de uso de Importar Servidores Bloqueados.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Date$
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/importarServidoresBloqueados" })
public class ImportarServidoresBloqueadosWebController extends ControlePaginacaoWebController {

    @RequestMapping(params = { "acao=iniciar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model)
            throws InstantiationException, IllegalAccessException, UnsupportedEncodingException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Verifica apenas quando um arquivo foi selecionado
        if (!SynchronizerToken.isTokenValid(request) && !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "arquivo_nome"))) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);
        String entidade;
        if (responsavel.isCsa()) {
            entidade = "csa";
        } else {
            entidade = "cse";
        }

        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        boolean podeProcessarBloqSer = responsavel.temPermissao(CodedValues.FUN_IMP_BLOQUEIO_SERVIDOR);
        boolean podeExcluirArqBloqSer = podeProcessarBloqSer && responsavel.temPermissao(CodedValues.FUN_UPL_ARQUIVOS);
        boolean temProcessoRodando = false;

        // Verifica se existe algum processo rodando para o usuário
        String chave1 = "BLOQUEIO_SER" + "|" + responsavel.getUsuCodigo();
        temProcessoRodando = ControladorProcessos.getInstance().verificar(chave1, session);

        String nomeArquivoEntrada = JspHelper.verificaVarQryStr(request, "arquivo_nome");
        if (!temProcessoRodando && !nomeArquivoEntrada.equals("")) {
            // Verifica se algum outro usuário da consignatária está processando
            // o arquivo escolhido pelo usuário.
            String chave2 = "BLOQUEIO_SER" + "|" + nomeArquivoEntrada;
            temProcessoRodando = ControladorProcessos.getInstance().verificar(chave2, session);

            if (!temProcessoRodando) {
                // Se o arquivo escolhido não está sendo processado então inicia o processamento.
                ProcessaBloqueioServidor processaBloqueio = new ProcessaBloqueioServidor(nomeArquivoEntrada, responsavel);
                processaBloqueio.setDescricao(ApplicationResourcesHelper.getMessage("mensagem.importar.bloqueio.servidor.arq.entrada", responsavel, nomeArquivoEntrada));
                processaBloqueio.start();
                ControladorProcessos.getInstance().incluir(chave1, processaBloqueio);
                ControladorProcessos.getInstance().incluir(chave2, processaBloqueio);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.importar.bloqueio.servidor.arq.processado", responsavel, nomeArquivoEntrada));
                temProcessoRodando = true;
            } else {
                // Se o arquivo está sendo processando por outro usuário,
                // dá mensagem de erro ao usuário e permite que ele escolha
                // outro arquivo
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.importar.bloqueio.servidor.arq.em.processamento", responsavel, nomeArquivoEntrada));
                temProcessoRodando = false;
            }
        }

        final String tipo = "bloqueio_ser";

        if (responsavel.isCsa()) {
            absolutePath += File.separatorChar + tipo + File.separatorChar + entidade + File.separatorChar + responsavel.getCsaCodigo();
        } else {
            absolutePath += File.separatorChar + tipo + File.separatorChar + entidade + File.separatorChar;

        }

        File diretorio = new File(absolutePath);
        if (!diretorio.exists() && !diretorio.mkdirs()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.importar.bloqueio.servidor.diretorio.nao.existe", responsavel));
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
        } catch (Exception ex) {}

        String parametros = "CSA_CODIGO(" +
                "|XML(";

        int total = arquivos.size();

        Set<String> params = new HashSet<>(request.getParameterMap().keySet());

        // Ignora os parâmetros abaixo
        params.remove("offset");
        params.remove("back");
        params.remove("linkRet");
        params.remove("linkRet64");
        params.remove("_skip_history_");
        params.remove("pager");
        params.remove("acao");

		List<String> requestParams = new ArrayList<>(params);
        String linkListagem = "../v3/importarServidoresBloqueados?acao=iniciar";
		configurarPaginador(linkListagem, "rotulo.lst.arq.generico.titulo", total, size, requestParams, false, request, model);

        List<ArquivoDTO> arquivosDTO = new ArrayList<>();

        if (arquivos != null && !arquivos.isEmpty()){

            int j = offset == -1 ? ((arquivos.size() % size) == 0 ? (arquivos.size() - size) : arquivos.size() - (arquivos.size() % size)) : offset;
            int i = JspHelper.LIMITE + offset;

            while (arquivos.size() > j) {
                if (offset <= j && i > j) {
                    File arquivo = arquivos.get(j);
                    String tam = "";
                    if (arquivo.length() > 1024.00) {
                        tam = Math.round(arquivo.length() / 1024.00) + " " + ApplicationResourcesHelper.getMessage("rotulo.kilobyte.abreviado", responsavel);
                    } else {
                        tam = arquivo.length() + " " + ApplicationResourcesHelper.getMessage("rotulo.byte.abreviado", responsavel);
                    }
                    String data = DateHelper.toDateTimeString(new java.sql.Date(arquivo.lastModified()));
                    String nome = arquivo.getPath().substring(absolutePath.length()).replace("/", "");
                    String formato = (nome.toLowerCase().endsWith(".zip")? "zip.gif" : "text.gif");

                    arquivosDTO.add(new ArquivoDTO(arquivo.getName(), nome, tam, data, formato));
                }
                j++;
            }
        }

        model.addAttribute("tipo", tipo);
        model.addAttribute("entidade", entidade);
        model.addAttribute("temProcessoRodando", temProcessoRodando);
        model.addAttribute("podeProcessarBloqSer", podeProcessarBloqSer);
        model.addAttribute("podeExcluirArqBloqSer", podeExcluirArqBloqSer);
        model.addAttribute("arquivosDTO", arquivosDTO);
        model.addAttribute("parametros", parametros);

        return viewRedirect("jsp/importarServidoresBloqueados/importarServidoresBloqueados", request, session, model, responsavel);
    }
}
