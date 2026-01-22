package com.zetra.econsig.web.controller.arquivo.download;

import java.io.File;
import java.io.FileFilter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.web.ArquivoDownload;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignacao.DownloadAnexoContratoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: DownloadAnexosContratoWebController</p>
 * <p>Description: Controlador Web para realização de download de anexos de contrato..</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author: ricardo.magno $
 * $Revision: $
 * $Date: $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/downloadAnexosContrato" })
public class DownloadAnexosContratoWebController extends ControlePaginacaoWebController{

	@Autowired
	private ConsignanteController consignanteController;

	@Autowired
	private ConsignatariaController consignatariaController;

	@Autowired
	private ConvenioController convenioController;

	@Autowired
	private DownloadAnexoContratoController downloadAnexoContratoController;

	@RequestMapping(params = { "acao=iniciar" })
	public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConsignatariaControllerException, ConvenioControllerException, ConsignanteControllerException {
		SynchronizerToken.saveToken(request);
		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

		List<TransferObject> consignatarias = null;
		List<TransferObject> orgaos = null;
		List<TransferObject> servicos = null;

		consignatarias = consignatariaController.lstConsignatarias(null, responsavel);
		orgaos = consignanteController.lstOrgaos(null, responsavel);
		servicos = convenioController.lstServicos(null, responsavel);

		model.addAttribute("consignatarias", consignatarias);
		model.addAttribute("orgaos", orgaos);
		model.addAttribute("servicos", servicos);


		if (!responsavel.isCseSupOrg()) {
			session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}

		String absolutePath = ParamSist.getDiretorioRaizArquivos();

		String tipo = "anexos_contrato";

		absolutePath += File.separatorChar + tipo + "/cse";

		FileFilter filtro = arq -> {
        	String arqNome = arq.getName().toLowerCase();
        	return (arqNome.endsWith(".zip"));
        };

		File diretorio = new File(absolutePath);
		if (!diretorio.exists() && !diretorio.mkdirs()) {
			session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.listar.arquivos.download.rescisao.criacao.diretorio", responsavel));
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}

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

		// Monta a paginação
		int size = JspHelper.LIMITE;
		int offset = 0;
		try {
			offset = Integer.parseInt(request.getParameter("offset"));
		} catch (Exception ex) {
		}

		int total = arquivos.size();

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

		String linkListagem = "../v3/listarAnexosContrato?acao=iniciar";
		configurarPaginador(linkListagem, "rotulo.listar.arquivos.download.rescisao.titulo.paginacao", total, size, requestParams, false, request, model);

		// Retorna apenas os arquivos da página
		List<ArquivoDownload> arquivosPaginaAtual = ArquivoDownload.carregarArquivos(arquivos, absolutePath, null, offset, size, responsavel);

		model.addAttribute("tipo", tipo);
		model.addAttribute("arquivos", arquivosPaginaAtual);

		return viewRedirect("jsp/listarAnexosContrato/listarAnexosContrato", request, session, model, responsavel);
	}

	@RequestMapping(params = { "acao=anexoContratoZip" })
	public String anexoContratoZip(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ConsignatariaControllerException, ConvenioControllerException, ConsignanteControllerException, ParseException {

		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
		Date periodoIniDate = null;
		Date periodoFimDate = null;

		List<String> csaCodigos = (!TextHelper.isNull(request.getParameter("csaCodigo")) ? Arrays.asList(request.getParameterValues("csaCodigo")) : null);
		List<String> svcCodigos = (!TextHelper.isNull(request.getParameter("svcCodigo")) ? Arrays.asList(request.getParameterValues("svcCodigo")) : null);
		List<String> sadCodigos = (!TextHelper.isNull(request.getParameter("SAD_CODIGO")) ? Arrays.asList(request.getParameterValues("SAD_CODIGO")) : null);

		if(!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "periodoIni")) && !JspHelper.verificaVarQryStr(request, "periodoIni").isEmpty()
				&& !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "periodoFim")) && !JspHelper.verificaVarQryStr(request, "periodoFim").isEmpty()) {
			periodoIniDate = DateHelper.toSQLDate(DateHelper.parse(JspHelper.verificaVarQryStr(request, "periodoIni"), LocaleHelper.getDatePattern()));
			periodoFimDate = DateHelper.toSQLDate(DateHelper.parse(JspHelper.verificaVarQryStr(request, "periodoFim"), LocaleHelper.getDatePattern()));
		}

		String anexoContratoResult = downloadAnexoContratoController.compactarAnexosAdePeriodo(csaCodigos, svcCodigos, sadCodigos, periodoIniDate, periodoFimDate, ".zip", responsavel);

		if (TextHelper.isNull(anexoContratoResult)) {
			session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.anexo.erro.nenhum.registro", responsavel));
		} else {
			session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.download.anexo.gerado.sucesso", responsavel));
		}


		return iniciar(request, response, session, model);

	}


}
