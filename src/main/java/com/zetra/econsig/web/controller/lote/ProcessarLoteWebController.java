package com.zetra.econsig.web.controller.lote;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaLote;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ProcessarLoteWebController</p>
 * <p>Description: Controlador Web base para o caso de uso Importar lote.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/processarLote" })
public class ProcessarLoteWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessarLoteWebController.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PeriodoController periodoController;

	@RequestMapping(params = { "acao=listarConsignataria" })
	public String listarConsignataria(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, InstantiationException, IllegalAccessException {
	    final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

		//Valida o token de sessão para evitar a chamada direta à operação
		if ((!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "FILTRO")) || !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "FILTRO2")))
				&& !SynchronizerToken.isTokenValid(request)) {
			session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}
		SynchronizerToken.saveToken(request);

		if (responsavel.isCsaCor()) {
			if (request.getParameter("back") != null && request.getParameter("back").equals("1")) {
				String link = "../v3/carregarPrincipal";
				model.addAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(link, request)));
				return "jsp/redirecionador/redirecionar";
			} else {
				String link = "../v3/processarLote?acao=listarXml&CSA_CODIGO=" + responsavel.getCsaCodigo();
				model.addAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(link, request)));
				return "jsp/redirecionador/redirecionar";
			}
		}

		List<TransferObject> consignatarias = null;

		String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
		String filtro2 = JspHelper.verificaVarQryStr(request, "FILTRO2");
		int filtro_tipo = -1;
		try {
			filtro_tipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
		} catch (Exception ex1) {
		}

		try {
			CustomTransferObject criterio = new CustomTransferObject();

			// -------------- Seta Criterio da Listagem ------------------
			// Bloqueado
			if (filtro_tipo == 0) {
                List<Short> statusCsa = new ArrayList<>();
                statusCsa.add(CodedValues.STS_INATIVO);
                statusCsa.add(CodedValues.STS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);
                criterio.setAttribute(Columns.CSA_ATIVO, statusCsa);
				// Desbloqueado
			} else if (filtro_tipo == 1) {
				criterio.setAttribute(Columns.CSA_ATIVO, CodedValues.STS_ATIVO);
				// Outros
			} else if (!filtro.equals("") && filtro_tipo != -1) {
				String campo = null;

				switch (filtro_tipo) {
				case 2: campo = Columns.CSA_IDENTIFICADOR; break;
				case 3: campo = Columns.CSA_NOME + CodedValues.OR_KEY + Columns.CSA_NOME_ABREV; break;
				default:
					session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
					return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
				}

				criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);
			}

			if (!filtro2.equals("")) {
				criterio.setAttribute(Columns.CSA_NOME, filtro2 + CodedValues.LIKE_MULTIPLO);
			}
			// ---------------------------------------

			int total = consignatariaController.countConsignatarias(criterio, responsavel);
			int size = JspHelper.LIMITE;
			int offset = 0;
			try {
				offset = Integer.parseInt(request.getParameter("offset"));
			} catch (Exception ex) {}

			consignatarias = consignatariaController.lstConsignatarias(criterio, offset, size, responsavel);

            // Monta lista de parâmetros através dos parâmetros de request
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            // Ignora os parâmetros abaixo
            params.remove("offset");
            params.remove("back");
            params.remove("linkRet");
            params.remove("linkRet64");
            params.remove("eConsig.page.token");
            params.remove("_skip_history_");
            params.remove("pager");
            params.remove("acao");

            List<String> requestParams = new ArrayList<>(params);
			String linkListagem = "../v3/processarLote?acao=listarConsignataria";
			configurarPaginador(linkListagem, "rotulo.processar.lote.titulo", total, size, requestParams, false, request, model);

		} catch (Exception ex) {
			session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
			consignatarias = new ArrayList<>();
		}

		model.addAttribute("filtro", filtro);
		model.addAttribute("filtro_tipo", filtro_tipo);
		model.addAttribute("consignatarias", consignatarias);

		return viewRedirect("jsp/processarLote/listarConsignataria", request, session, model, responsavel);
	}

	@RequestMapping(params = { "acao=listarCorrespondente" })
	public String listarCorrespondente(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, InstantiationException, IllegalAccessException {
	    final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

		//Valida o token de sessão para evitar a chamada direta à operação
		if (!SynchronizerToken.isTokenValid(request)) {
			session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}
		SynchronizerToken.saveToken(request);

		String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
		if (TextHelper.isNull(csaCodigo)) {
			session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}

		List<TransferObject> correspondentes = null;

		String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
		String filtro2 = JspHelper.verificaVarQryStr(request, "FILTRO2");
		int filtro_tipo = -1;
		try {
			filtro_tipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
		} catch (Exception ex1) {
		}

		try {
			CustomTransferObject criterio = new CustomTransferObject();
			criterio.setAttribute(Columns.COR_CSA_CODIGO, csaCodigo);

			List<Short> status = new ArrayList<>();
			status.add(CodedValues.STS_ATIVO);
			status.add(CodedValues.STS_INATIVO);
			status.add(CodedValues.STS_INATIVO_CSE);
			status.add(CodedValues.STS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);
			criterio.setAttribute(Columns.COR_ATIVO, status);

			// -------------- Seta Criterio da Listagem ------------------
			// Bloqueado
			if (filtro_tipo == 0) {
				List<Short> statusCor = new ArrayList<>();
				statusCor.add(CodedValues.STS_INATIVO);
				statusCor.add(CodedValues.STS_INATIVO_CSE);
	            statusCor.add(CodedValues.STS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);
				criterio.setAttribute(Columns.COR_ATIVO, statusCor);
				// Desbloqueado
			} else if (filtro_tipo == 1) {
				criterio.setAttribute(Columns.COR_ATIVO, CodedValues.STS_ATIVO);
				// Outros
			} else if (!filtro.equals("") && filtro_tipo != -1) {
				String campo = null;

				switch (filtro_tipo) {
				case 2: campo = Columns.COR_IDENTIFICADOR; break;
				case 3: campo = Columns.COR_NOME; break;
				default:
					session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
					return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
				}

				criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);
			}

			if (!filtro2.equals("")) {
				criterio.setAttribute(Columns.COR_NOME, filtro2 + CodedValues.LIKE_MULTIPLO);
			}
			// ---------------------------------------

			int total = consignatariaController.countCorrespondentes(criterio, responsavel);
			int size = JspHelper.LIMITE;
			int offset = 0;
			try {
				offset = Integer.parseInt(request.getParameter("offset"));
			} catch (Exception ex) {}

            // Monta lista de parâmetros através dos parâmetros de request
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            // Ignora os parâmetros abaixo
            params.remove("offset");
            params.remove("back");
            params.remove("linkRet");
            params.remove("linkRet64");
            params.remove("eConsig.page.token");
            params.remove("_skip_history_");
            params.remove("pager");
            params.remove("acao");

            List<String> requestParams = new ArrayList<>(params);
			String linkAction = request.getRequestURI() + "?acao=listarCorrespondente&CSA_CODIGO="+csaCodigo;
			configurarPaginador(linkAction, "rotulo.processar.lote.titulo", total, size, requestParams, false, request, model);

			correspondentes = consignatariaController.lstCorrespondentes(criterio, offset, size, responsavel);
		} catch (Exception ex) {
			session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
			correspondentes = new ArrayList<>();
		}

		model.addAttribute("filtro", filtro);
		model.addAttribute("filtro_tipo", filtro_tipo);
		model.addAttribute("csaCodigo", csaCodigo);
		model.addAttribute("correspondentes", correspondentes);

		return viewRedirect("jsp/processarLote/listarCorrespondente", request, session, model, responsavel);
	}

	@RequestMapping(params = { "acao=listarXml" })
	public String listarXml(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ParseException, ServletException, IOException {
		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

		String csa_codigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
		String cor_codigo = JspHelper.verificaVarQryStr(request, "COR_CODIGO");

		//Se csa_codigo existe esta página esta no caminho, se não, ela é inicial
		if (!TextHelper.isNull(csa_codigo) && !SynchronizerToken.isTokenValid(request)) {
			session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}
		SynchronizerToken.saveToken(request);

		if (responsavel.isCsa()) {
			csa_codigo = responsavel.getCodigoEntidade();
		} else if (responsavel.isCor()) {
			csa_codigo = responsavel.getCodigoEntidadePai();
			cor_codigo = responsavel.getCodigoEntidade();
		}

		String absolutePath = ParamSist.getDiretorioRaizArquivos();
		String pathXml = absolutePath + File.separatorChar + "conf" + File.separatorChar + "lote";
		String pathXmlDefault = absolutePath + File.separatorChar + "conf" + File.separatorChar + "lote" + File.separatorChar + "xml";
		if (!TextHelper.isNull(csa_codigo)) {
			pathXml += File.separatorChar + csa_codigo;
		}

		// Cria filtro para seleção de arquivos .xml
		FileFilter filtro = arq -> {
        	String arqNome = arq.getName().toLowerCase();
        	return (arqNome.endsWith("_entrada.xml"));
        };

		// Faz as checagens de diretório
		File diretorioXml = null;
		File diretorioXmlDefault = null;
		if (!TextHelper.isNull(csa_codigo)) {
			diretorioXml = new File(pathXml);
			diretorioXmlDefault = new File(pathXmlDefault);
			if (!diretorioXml.exists() && !diretorioXml.mkdirs()) {
				if (!diretorioXmlDefault.exists() && !diretorioXmlDefault.mkdirs()){
					session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.diretorio.conf.inexistente", responsavel));
					return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
				}
			}
		} else {
			diretorioXmlDefault = new File(pathXmlDefault);
			if (!diretorioXmlDefault.exists() && !diretorioXmlDefault.mkdirs()){
				session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.diretorio.conf.inexistente", responsavel));
				return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
			}
		}

		// Lista os arquivos
		List<File> arquivosXmlEntrada = null;
		File[] temp = null;
		if (diretorioXml != null) {
			temp = diretorioXml.listFiles(filtro);
		} else {
			temp = diretorioXmlDefault.listFiles(filtro);
		}
		if (temp != null) {
			arquivosXmlEntrada = new ArrayList<>();
			arquivosXmlEntrada.addAll(Arrays.asList(temp));
		}

		File arqEntrada = null;
		File arqTradutor = null;

		String nomeTabela = null;

		List<String> nomesTabelas = new ArrayList<>();

		if (arquivosXmlEntrada != null) {
			Iterator<File> it = arquivosXmlEntrada.iterator();
			while (it.hasNext()) {
				arqEntrada = it.next();
				nomeTabela = arqEntrada.getName().substring(0, arqEntrada.getName().indexOf("_entrada.xml"));

				arqTradutor = new File(pathXml + File.separatorChar + nomeTabela + "_tradutor.xml");

				if (arqTradutor.exists()) {
					nomesTabelas.add(nomeTabela);
				}
			}

			// Ordena os arquivos baseado na data de modificação
			Collections.sort(nomesTabelas, String::compareTo);
		}

		// Lista os arquivos para o diretório default caso não encontre o xml no diretório específico
		boolean xmlDefault = false;
		if (nomesTabelas.size() == 0) {
			xmlDefault = true;
			arquivosXmlEntrada = null;
			temp = diretorioXmlDefault.listFiles(filtro);
			if (temp != null) {
				arquivosXmlEntrada = new ArrayList<>();
				arquivosXmlEntrada.addAll(Arrays.asList(temp));
			}

			arqEntrada = null;
			arqTradutor = null;

			nomeTabela = null;

			if (arquivosXmlEntrada != null) {
				Iterator<File> it = arquivosXmlEntrada.iterator();
				while (it.hasNext()) {
					arqEntrada = it.next();
					nomeTabela = arqEntrada.getName().substring(0, arqEntrada.getName().indexOf("_entrada.xml"));

					arqTradutor = new File(pathXmlDefault + File.separatorChar + nomeTabela + "_tradutor.xml");

					if (arqTradutor.exists()) {
						nomesTabelas.add(nomeTabela);
					}
				}

				// Ordena os arquivos baseado na data de modificação
				Collections.sort(nomesTabelas, String::compareTo);
			}
		}


		final ParamSession paramSession = ParamSession.getParamSession(session);
		if (nomesTabelas.size() == 1) {
			if (request.getParameter("back") != null && request.getParameter("back").equals("1")) {
				request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
				return "jsp/redirecionador/redirecionar";
			} else {
				String link = "../v3/processarLote?acao=listarArquivosImportacao&CSA_CODIGO=" + csa_codigo + "&COR_CODIGO=" + cor_codigo + "&XML=" + nomesTabelas.iterator().next().toString();
				model.addAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(link, request)));
				return "jsp/redirecionador/redirecionar";
			}
		} else if (nomesTabelas.size() == 0) {
			session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.erro.layout.conf.ausente", responsavel));
		}

		model.addAttribute("nomesTabelas", nomesTabelas);
		model.addAttribute("xmlDefault", xmlDefault);
		model.addAttribute("pathXmlDefault", pathXmlDefault);
		model.addAttribute("pathXml", pathXml);
		model.addAttribute("csa_codigo", csa_codigo);
		model.addAttribute("cor_codigo", cor_codigo);

		return viewRedirect("jsp/processarLote/listarXml", request, session, model, responsavel);

	}

	@RequestMapping(params = { "acao=listarLoteMultiplo" })
	public String listarXmlLoteMultiplo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ParseException, ServletException, IOException {
	    return listarXml(request, response, session, model);
	}

	@RequestMapping(params = { "acao=listarArquivosImportacao" })
	public String listarArquivosImportacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ParseException, InstantiationException, IllegalAccessException {
		final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

		// Valida o token de sessão para evitar a chamada direta à operação
		if (!SynchronizerToken.isTokenValid(request)) {
			session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}
		SynchronizerToken.saveToken(request);

		final String csaCodigo = responsavel.isCseSupOrg() ? JspHelper.verificaVarQryStr(request, "CSA_CODIGO") : responsavel.isCsa() ? responsavel.getCodigoEntidade() : responsavel.isCor() ? responsavel.getCodigoEntidadePai() : "";
		final String corCodigo = responsavel.isCseSupOrg() ? JspHelper.verificaVarQryStr(request, "COR_CODIGO") : responsavel.isCor() ? responsavel.getCodigoEntidade() : "";
		final String tipoCodigo = (TextHelper.isNull(corCodigo)) ? csaCodigo : corCodigo;
		final String tipoEntidade = (TextHelper.isNull(corCodigo)) ? ((!TextHelper.isNull(csaCodigo)) ? "csa" : "") : "cor";

		if (TextHelper.isNull(csaCodigo) && !responsavel.temPermissao(CodedValues.FUN_PROCESSA_LOTE_MULTIPLAS_CSAS)) {
			session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}

		final String xml = JspHelper.verificaVarQryStr(request, "XML");
		if (TextHelper.isNull(xml)) {
			session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}

		String absolutePath = ParamSist.getDiretorioRaizArquivos();
		final boolean podeProcessarLote = responsavel.temPermissao(CodedValues.FUN_IMPORTACAO_VIA_LOTE);
		final boolean podeValidarLote = responsavel.temPermissao(CodedValues.FUN_VALIDAR_PROCESSAMENTO_VIA_LOTE);
		final boolean podeExcluirArqLote = responsavel.temPermissao(CodedValues.FUN_UPL_ARQUIVOS);
		final List<Date> periodoAgrupado = new ArrayList<>();

		// Verifica se existe algum processo rodando para o usuário
		final String chave1 = "LOTE" + "|" + tipoCodigo;
		final boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chave1, session);

		final String tipo = "lote";
		final String entidade = tipoEntidade;

		if (!TextHelper.isNull(tipoEntidade)) {
			absolutePath += File.separatorChar + tipo + File.separatorChar + tipoEntidade;
			absolutePath += File.separatorChar + tipoCodigo + File.separatorChar;
		} else {
			absolutePath += File.separatorChar + tipo + File.separatorChar + "cse" + File.separatorChar;
		}

		final File diretorio = new File(absolutePath);
		if (!diretorio.exists() && !diretorio.mkdirs()) {
			session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.diretorio.inexistente", responsavel));
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}

		final FileFilter filtro = arq -> {
        	String arq_name = arq.getName().toLowerCase();
        	return arq.isFile() && (arq_name.endsWith(".txt") || arq_name.endsWith(".zip"));
        };

		List<File> arquivos = null;
		final File[] temp = diretorio.listFiles(filtro);
		if (temp != null) {
			arquivos = new ArrayList<>();
			arquivos.addAll(Arrays.asList(temp));
		}

		if (arquivos != null) {
			Collections.sort(arquivos, (o1, o2) -> {
				Long d1 = o1.lastModified();
				Long d2 = o2.lastModified();
				return d2.compareTo(d1);
			});
		}

		// Paginacao
		final int size = JspHelper.LIMITE;
		int offset = 0;
		try {
			offset = Integer.parseInt(request.getParameter("offset"));
		} catch (Exception ex) {}

		String parametros = null;
		if (!TextHelper.isNull(tipoEntidade)) {
			parametros = "CSA_CODIGO=" + csaCodigo +"&" +
					"COR_CODIGO=" + corCodigo +"&" +
					"XML=" + xml;
		} else {
			parametros = "XML=" + xml;
		}

		final int total = arquivos.size();
		final String linkListagem = "../v3/processarLote?acao=listarArquivosImportacao&" + parametros;
		configurarPaginador(linkListagem, "rotulo.processar.lote.titulo", total, size, null, false, request, model);

		// Permitimos forçar o periodo para importação de sistemas permite agrupamento de períodos, permite escolher o período para agrupar e o atual está agrupado.
		if(responsavel.isCsaCor() && ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, responsavel) && ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_ESCOLHER_PERIODO_EM_AGRUPAMENTO, responsavel)) {
		    try {
		        final List<Date> periodos = periodoController.obtemPeriodoAgrupado(null, null, responsavel);
		        final Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(null, responsavel);

                if (periodos.stream().anyMatch(p -> p.compareTo(periodoAtual) == 0)) {
                    periodoAgrupado.addAll(periodos);
                }
            } catch (PeriodoException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
		}

		model.addAttribute("xml", xml);
		model.addAttribute("corCodigo", corCodigo);
		model.addAttribute("parametros", parametros);
		model.addAttribute("tipo", tipo);
		model.addAttribute("tipoCodigo", tipoCodigo);
		model.addAttribute("entidade", entidade);
		model.addAttribute("offset", offset);
		model.addAttribute("csaCodigo", csaCodigo);
		model.addAttribute("temProcessoRodando", temProcessoRodando);
		model.addAttribute("responsavel", responsavel);
		model.addAttribute("podeValidarLote", podeValidarLote);
		model.addAttribute("podeProcessarLote", podeProcessarLote);
		model.addAttribute("podeExcluirArqLote", podeExcluirArqLote);
		model.addAttribute("arquivos", arquivos);
		model.addAttribute("size", size);
		model.addAttribute("absolutePath", absolutePath);
		model.addAttribute("periodoAgrupado", periodoAgrupado);

		return viewRedirect("jsp/processarLote/listarArquivosImportacao", request, session, model, responsavel);
	}

	@RequestMapping(params = { "acao=validar" })
	public String validar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ParseException, InstantiationException, IllegalAccessException, ParametroControllerException {
        return processarArquivo(request, response, session, model, true);
	}

	@RequestMapping(params = { "acao=importar" })
	public String importar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ParseException, InstantiationException, IllegalAccessException, ParametroControllerException {
	    return processarArquivo(request, response, session, model, false);
	}

    private String processarArquivo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, boolean validar) throws ParseException, InstantiationException, IllegalAccessException, ParametroControllerException {
		final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
		final ParamSession paramSession = ParamSession.getParamSession(session);

		final String csaCodigo = responsavel.isCseSupOrg() ? JspHelper.verificaVarQryStr(request, "CSA_CODIGO") : responsavel.isCsa() ? responsavel.getCodigoEntidade() : responsavel.isCor() ? responsavel.getCodigoEntidadePai() : "";
		final String corCodigo = responsavel.isCseSupOrg() ? JspHelper.verificaVarQryStr(request, "COR_CODIGO") : responsavel.isCor() ? responsavel.getCodigoEntidade() : "";
		final String tipoCodigo = (TextHelper.isNull(corCodigo)) ? csaCodigo : corCodigo;
		final String tipoEntidade = (TextHelper.isNull(corCodigo)) ? ((!TextHelper.isNull(csaCodigo)) ? "csa" : "") : "cor";

		if (TextHelper.isNull(csaCodigo) && !responsavel.temPermissao(CodedValues.FUN_PROCESSA_LOTE_MULTIPLAS_CSAS)) {
			session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}

		final String xml = JspHelper.verificaVarQryStr(request, "XML");
		if (TextHelper.isNull(xml)) {
			session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}

		final boolean podeProcessarLote = responsavel.temPermissao(CodedValues.FUN_IMPORTACAO_VIA_LOTE);

		// DESENV-10065: Verifica se existe algum processo de lote executando para a consignatária/correspodente, independente do arquivo ou usuário executando
		final String chave1 = "LOTE" + "|" + tipoCodigo;
		final boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chave1, session);

		if (!temProcessoRodando) {
			// Se não tem processo de lotee rodando para a consignatária/correspodente, então...
		    final String nomeArquivoEntrada = JspHelper.verificaVarQryStr(request, "arquivo_nome");
		    final String nomeArqXmlEntrada = xml + "_entrada.xml";
		    final String nomeArqXmlTradutor = xml + "_tradutor.xml";
			String fileName = ParamSist.getDiretorioRaizArquivos();
			if (!TextHelper.isNull(tipoEntidade)) {
				fileName += File.separatorChar + "lote" + File.separatorChar + tipoEntidade + File.separatorChar + tipoCodigo + File.separatorChar + nomeArquivoEntrada;
			} else {
				fileName += File.separatorChar + "lote" + File.separatorChar + "cse" + File.separatorChar + nomeArquivoEntrada;
			}

			// Se a operação é de validação ou o usuário não tem permissão de processar lote, então é validação
			final boolean podeValidar = validar || !podeProcessarLote;

			final boolean permiteLoteAtrasado = ((responsavel.isCseSup() || responsavel.isCsaCor()) && "true".equals(request.getParameter("permiteLoteAtrasado")));
			final boolean permiteReducaoLancamentoCartao = "true".equals(request.getParameter("permiteReducaoLancamentoCartao"));

	         // Se a consignataria permite importação para servidor excluído
			final String tpaLoteServidorExcluido = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_PERMITE_INC_ADE_RSE_EXCLUIDO_WEB, responsavel);
			final boolean somenteServidorAtivo = !(!TextHelper.isNull(tpaLoteServidorExcluido) && tpaLoteServidorExcluido.equalsIgnoreCase("S"));

            // Se usuário marcou para permitir processamento de lote atrasado, verifica se também
            // preencheu o campo para forçar período manualmente
            Date periodoConfiguravel = null;
            if (permiteLoteAtrasado) {
                String periodo = JspHelper.verificaVarQryStr(request, "periodo");
                if (!TextHelper.isNull(periodo)) {
                    periodoConfiguravel = DateHelper.parsePeriodString(periodo);
                }
            }

			// Se não há arquivo de lote sendo processado para a consignatária/correspodente
			// então inicia o processamento.
            final ProcessaLote processaLote = new ProcessaLote(fileName, nomeArqXmlEntrada, nomeArqXmlTradutor,
			        podeValidar, csaCodigo, corCodigo, permiteLoteAtrasado, permiteReducaoLancamentoCartao, somenteServidorAtivo, periodoConfiguravel, responsavel);
            processaLote.setDescricao(ApplicationResourcesHelper.getMessage(validar ? "rotulo.lote.arquivo.validar" : "rotulo.lote.arquivo.processar", responsavel) + " '" + nomeArquivoEntrada + "'");
			processaLote.start();
			ControladorProcessos.getInstance().incluir(chave1, processaLote);
		} else {
		    // Se algum arquivo de lote já está em processamento para a consignatária/correspodente, retorna mensagem de aviso ao usuário para tentar mais tarde
		    session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage((TextHelper.isNull(corCodigo)) ? "mensagem.erro.arquivo.lote.soap.upload.concorrente.csa" :
                                 "mensagem.erro.arquivo.lote.soap.upload.concorrente.cor", responsavel));
		}

		paramSession.halfBack();
		return listarArquivosImportacao(request, response, session, model);
	}
}