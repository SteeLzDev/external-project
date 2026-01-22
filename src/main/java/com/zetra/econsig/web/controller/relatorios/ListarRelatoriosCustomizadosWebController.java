package com.zetra.econsig.web.controller.relatorios;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ListarRelatoriosWebController</p>
 * <p>Description: Controlador Web para o caso de uso Listar Relatórios.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarRelatorioCustomizado" })
public class ListarRelatoriosCustomizadosWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarRelatoriosCustomizadosWebController.class);

    @Autowired
    private ConsignatariaController consignatariaController;

	@RequestMapping(params = { "acao=iniciar" })
	public String listarRelatorio(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
		ParamSession paramSession = ParamSession.getParamSession(session);

		List<TransferObject> consignatarias = null;
		String absolutePath = ParamSist.getDiretorioRaizArquivos();
		String diretorioArquivosCse = absolutePath + java.io.File.separatorChar + "relatorio" + File.separatorChar + "cse" + File.separatorChar + "customizacoes" + java.io.File.separatorChar;
		String diretorioArquivosCsa = absolutePath + java.io.File.separatorChar + "relatorio" + File.separatorChar + "csa" + File.separatorChar + "customizacoes" + java.io.File.separatorChar;

		String consignataria = JspHelper.verificaVarQryStr(request, "consignataria");
		List<ArquivoDownload> arquivosPaginaAtual = null;

		if (responsavel.isSup()) {
			try {
				consignatarias = consignatariaController.lstConsignatarias(null, responsavel);
			} catch (ConsignatariaControllerException ex) {
				LOG.error(ex.getMessage(), ex);
				session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
				request.setAttribute("url64", TextHelper
						.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
				return "jsp/redirecionador/redirecionar";
			}
		}

		if (responsavel.isCsa()) {
			consignataria = responsavel.getCodigoEntidade();
		}

		// Cria filtro para seleção de arquivos .txt, .pdf e .xls.
		FileFilter filtro = arq -> {
			String arqNome = arq.getName().toLowerCase();
			return (arqNome.endsWith(".txt") || arqNome.endsWith(".xls") || arqNome.endsWith(".pdf") || arqNome.endsWith(".csv") || arqNome.endsWith(".doc") || arqNome.endsWith(".docx") || arqNome.endsWith(".xlsx") || arqNome.endsWith(".zip"));
		};

		if (!consignataria.isEmpty()) {
			// Lista os arquivos
			List<File> arquivosCombo = new ArrayList<>();

			// Pega o identificador dos órgão, e os arquivos dos subdiretórios
			String element = consignataria;
			if (element != null) {
				File arq = new File(diretorioArquivosCsa + element);
				if (arq.isDirectory()) {
					arquivosCombo.addAll(Arrays.asList(arq.listFiles(filtro)));
				}
			}

			// Ordena os arquivos baseado na data de modificação
			Collections.sort(arquivosCombo, (f1, f2) -> {
				Long d1 = f1.lastModified();
				Long d2 = f2.lastModified();
				return d2.compareTo(d1);
			});

			arquivosPaginaAtual = ArquivoDownload.carregarArquivos(arquivosCombo, diretorioArquivosCsa, null, responsavel);
		} else if (responsavel.isCse() || responsavel.isSup() && consignataria.isEmpty()){
			List<File> arquivosCombo = new ArrayList<>();

				File arq = new File(diretorioArquivosCse);
				if (arq.isDirectory()) {
					arquivosCombo.addAll(Arrays.asList(Objects.requireNonNull(arq.listFiles(filtro))));
				}

			Collections.sort(arquivosCombo, (f1, f2) -> {
				Long d1 = f1.lastModified();
				Long d2 = f2.lastModified();
				return d2.compareTo(d1);
			});

			arquivosPaginaAtual = ArquivoDownload.carregarArquivos(arquivosCombo, diretorioArquivosCse, null, responsavel);
		}

		model.addAttribute("consignatarias", consignatarias);
		model.addAttribute("consignataria", consignataria);
		model.addAttribute("arquivosCombo", arquivosPaginaAtual);

		return viewRedirect("jsp/listarRelatorios/listarRelatoriosCustomizados", request, session, model, responsavel);
	}

}
