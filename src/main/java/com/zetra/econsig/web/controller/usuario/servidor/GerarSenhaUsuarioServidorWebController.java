package com.zetra.econsig.web.controller.usuario.servidor;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaManutencaoSenhaServidor;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: GerarSenhaUsuarioServidorWebController.java</p>
 * <p>Description: Controlador Web para o caso de uso Gerar Novas Senhas para Usuários Servidores.</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author: ricardo.magno $
 * $Revision: $
 * $Date: $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/gerarSenhaUsuarioServidor" })
public class GerarSenhaUsuarioServidorWebController extends AbstractWebController {
	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(GerarSenhaUsuarioServidorWebController.class);

	@RequestMapping(params = { "acao=iniciar" })
	public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
		SynchronizerToken.saveToken(request);

		boolean podeGerarSenhas = responsavel.temPermissao(CodedValues.FUN_GERAR_NOVAS_SENHAS_USU_SER);

		//Verifica se existe algum processo rodando para o usuário
		String chaveGeracao = "PROCESSO_GERAR_SENHAS";
		boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chaveGeracao, session);

		//Path para arquivo com senhas
		String absolutePath = ParamSist.getDiretorioRaizArquivos() + File.separatorChar + "senhaservidores";

		//Cria filtro para seleção de arquivos .zip
		FileFilter filtro = new FileFilter() {
			@Override
            public boolean accept(File arq) {
				String arqNome = arq.getName().toLowerCase();
				return (arqNome.endsWith(".zip"));
			}
		};

		//Faz as checagens de diretório
		File diretorioSenhas = new File(absolutePath);

		if ((!diretorioSenhas.exists() && !diretorioSenhas.mkdirs())) {
			session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}

		//Lista os arquivos
		List<File> arquivosRetorno = null;
		File[] temp = diretorioSenhas.listFiles(filtro);
		if (temp != null) {
			arquivosRetorno = new ArrayList<>();
			arquivosRetorno.addAll(Arrays.asList(temp));
		}

		model.addAttribute("temProcessoRodando", temProcessoRodando);
		model.addAttribute("arquivosRetorno", arquivosRetorno);
		model.addAttribute("podeGerarSenhas", podeGerarSenhas);
		model.addAttribute("absolutePath", absolutePath);

		return viewRedirect("jsp/gerarSenhaUsuarioServidor/gerarSenha", request, session, model, responsavel);

	}

	@RequestMapping(params = { "acao=gerarSenha" })
	public String gerarSenha(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

		boolean podeGerarSenhas = responsavel.temPermissao(CodedValues.FUN_GERAR_NOVAS_SENHAS_USU_SER);

		//Verifica se existe algum processo rodando para o usuário
		String chaveGeracao = "PROCESSO_GERAR_SENHAS";
		boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chaveGeracao, session);

		//Thread para gerar novas senhas dos usuários.
		try {

			if (!temProcessoRodando) {
				ProcessaManutencaoSenhaServidor processo = new ProcessaManutencaoSenhaServidor(ProcessaManutencaoSenhaServidor.GERAR_SENHA, responsavel);
				processo.setDescricao(ApplicationResourcesHelper.getMessage("mensagem.informacao.usuario.gerar.senha.servidores", responsavel));
				processo.start();
				ControladorProcessos.getInstance().incluir(chaveGeracao, processo);
				session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informacao.usuario.gerar.senha.gerando", responsavel));
				temProcessoRodando = true;
			}

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}

		model.addAttribute("temProcessoRodando", temProcessoRodando);
		model.addAttribute("podeGerarSenhas", podeGerarSenhas);

		return viewRedirect("jsp/gerarSenhaUsuarioServidor/gerarSenha", request, session, model, responsavel);
	}

	@RequestMapping(params = { "acao=ativarSenha" })
	public String ativarSenha(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

		if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

		boolean podeGerarSenhas = responsavel.temPermissao(CodedValues.FUN_GERAR_NOVAS_SENHAS_USU_SER);

		//Verifica se existe algum processo rodando para o usuário
		String chaveGeracao = "PROCESSO_GERAR_SENHAS";
		boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chaveGeracao, session);

		//Thread para gerar novas senhas dos usuários.
		try {

			if (!temProcessoRodando) {
				ProcessaManutencaoSenhaServidor processo = new ProcessaManutencaoSenhaServidor(ProcessaManutencaoSenhaServidor.ATIVAR_SENHA, responsavel);
				processo.setDescricao(ApplicationResourcesHelper.getMessage("mensagem.informacao.usuario.gerar.senha.ativacao", responsavel));
				processo.start();
				ControladorProcessos.getInstance().incluir(chaveGeracao, processo);
				session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informacao.usuario.gerar.senha.ativando", responsavel));
				temProcessoRodando = true;
			}

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}

		model.addAttribute("temProcessoRodando", temProcessoRodando);
		model.addAttribute("podeGerarSenhas", podeGerarSenhas);

		return viewRedirect("jsp/gerarSenhaUsuarioServidor/gerarSenha", request, session, model, responsavel);
	}

}

