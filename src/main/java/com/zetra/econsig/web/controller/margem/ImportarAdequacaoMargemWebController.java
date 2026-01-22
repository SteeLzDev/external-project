package com.zetra.econsig.web.controller.margem;

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
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaAdequacao;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ImportarAdequacaoMargemWebController</p>
 * <p>Description: Controlador Web para o caso de uso Importação de Adequação de Margem.</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author: ricardo.magno $
 * $Revision: $
 * $Date: $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/importarAdequacaoMargem" })
public class ImportarAdequacaoMargemWebController extends ControlePaginacaoWebController {

	/** Log object for this class. */
	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportarAdequacaoMargemWebController.class);

	@RequestMapping(params = { "acao=iniciar" })
	public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

		// Verifica se existe algum processo rodando para o usuário
		String chave1 = "ADEQUACAO" + "|" + responsavel.getUsuCodigo();
		boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chave1, session);

		String diretorioArquivos = ParamSist.getDiretorioRaizArquivos()
				+ File.separatorChar + "adequacao"
				+ File.separatorChar + "cse"
				+ File.separatorChar;

		File diretorio = new File(diretorioArquivos);
		if (!diretorio.exists() && !diretorio.mkdirs()) {
			session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.diretorio.inexistente", responsavel));
			return viewRedirect("jsp/importarAdequacaoMargem/importarAdequacaoMargem", request, session, model, responsavel);
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

		int size = JspHelper.LIMITE;
		int offset = 0;
		int total = 0;

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

		// Paginacao
		String linkListagem = "../v3/importarAdequacaoMargem?acao=iniciar";
		configurarPaginador(linkListagem, "rotulo.lista.arquivo.recuperacao.credito", total, size, null, false, request, model);

		model.addAttribute("offset", offset);
		model.addAttribute("size", size);
		model.addAttribute("arquivos", arquivos);
		model.addAttribute("diretorioArquivos", diretorioArquivos);
		model.addAttribute("temProcessoRodando", temProcessoRodando);

		return viewRedirect("jsp/importarAdequacaoMargem/importarAdequacaoMargem", request, session, model, responsavel);
	}

	@RequestMapping(params = { "acao=processar" })
	public String processar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
		try {
			boolean temProcessoRodando = false;
			// Verifica se existe algum processo rodando para o usuário
			String chave1 = "ADEQUACAO" + "|" + responsavel.getUsuCodigo();
			temProcessoRodando = ControladorProcessos.getInstance().verificar(chave1, session);

			if (!temProcessoRodando) {

				String nomeArquivoEntrada = JspHelper.verificaVarQryStr(request, "arquivo_nome");

				String diretorioArquivos = ParamSist.getDiretorioRaizArquivos()
						+ File.separatorChar + "adequacao"
						+ File.separatorChar + "cse"
						+ File.separatorChar;

				File arquivo = new File(diretorioArquivos + nomeArquivoEntrada);

				if (!arquivo.exists()) {
					session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.nenhum.arquivo.encontrado", responsavel));
					return viewRedirect("jsp/importarAdequacaoMargem/importarAdequacaoMargem", request, session, model, responsavel);
				}

				// Verifica se algum outro usuário está processando o arquivo escolhido pelo usuário.
				String chave2 = "ADEQUACAO" + "|" + nomeArquivoEntrada;
				temProcessoRodando = ControladorProcessos.getInstance().verificar(chave2, session);

				boolean validar = false;

				if (!temProcessoRodando) {
					ProcessaAdequacao processo = new ProcessaAdequacao(nomeArquivoEntrada, validar, responsavel);

					processo.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.adequacao.margem.arquivo", responsavel) + " '" + nomeArquivoEntrada + "'");

					processo.start();
					ControladorProcessos.getInstance().incluir(chave1, processo);
					ControladorProcessos.getInstance().incluir(chave2, processo);
					temProcessoRodando = true;
				} else {
					// Se o arquivo está sendo processando por outro usuário, envia mensagem de erro
					// para o usuário e permite que ele escolha outro arquivo
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

	@RequestMapping(params = { "acao=validar" })
	public String validar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
		try {
			boolean temProcessoRodando = false;
			// Verifica se existe algum processo rodando para o usuário
			String chave1 = "ADEQUACAO" + "|" + responsavel.getUsuCodigo();
			temProcessoRodando = ControladorProcessos.getInstance().verificar(chave1, session);

			if (!temProcessoRodando) {

				String nomeArquivoEntrada = JspHelper.verificaVarQryStr(request, "arquivo_nome");
				String diretorioArquivos = ParamSist.getDiretorioRaizArquivos()
						+ File.separatorChar + "adequacao"
						+ File.separatorChar + "cse"
						+ File.separatorChar;

				File arquivo = new File(diretorioArquivos + nomeArquivoEntrada);
				if (!arquivo.exists()) {
					session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.nenhum.arquivo.encontrado", responsavel));
					return viewRedirect("jsp/importarAdequacaoMargem/importarAdequacaoMargem", request, session, model, responsavel);
				}

				// Verifica se algum outro usuário está processando o arquivo escolhido pelo usuário.
				String chave2 = "ADEQUACAO" + "|" + nomeArquivoEntrada;
				temProcessoRodando = ControladorProcessos.getInstance().verificar(chave2, session);

				boolean validar = true;

				if (!temProcessoRodando) {
					ProcessaAdequacao processo = new ProcessaAdequacao(nomeArquivoEntrada, validar, responsavel);

					processo.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.adequacao.margem.arquivo", responsavel) + " '" + nomeArquivoEntrada + "'");

					processo.start();
					ControladorProcessos.getInstance().incluir(chave1, processo);
					ControladorProcessos.getInstance().incluir(chave2, processo);
					temProcessoRodando = true;
				} else {
					// Se o arquivo está sendo processando por outro usuário, envia mensagem de erro
					// para o usuário e permite que ele escolha outro arquivo
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