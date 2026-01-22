package com.zetra.econsig.web.controller.folha;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.folha.exportacao.ParametrosExportacao;
import com.zetra.econsig.folha.importacao.ValidaImportacao;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.Processo;
import com.zetra.econsig.job.process.integracao.orientada.ProcessaHistorico;
import com.zetra.econsig.job.process.integracao.orientada.ProcessaMargem;
import com.zetra.econsig.job.process.integracao.orientada.ProcessaMovimento;
import com.zetra.econsig.job.process.integracao.orientada.ProcessaRetorno;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.folha.ValidaImportacaoController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.arquivo.upload.UploadArquivoWebController;
import com.zetra.econsig.web.servlet.AudioCaptchaServlet;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;

import br.com.nostrum.simpletl.Validator;
import br.com.nostrum.simpletl.exception.InterpreterException;
import br.com.nostrum.simpletl.reader.TextFileReader;
import br.com.nostrum.simpletl.validation.Record;

/**
 * <p>Title: IntegrarFolhaWebController</p>
 * <p>Description: Controlador Web para o caso de uso de liberar movimento financeiro.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/integrarFolha" })
public class IntegrarFolhaWebController extends UploadArquivoWebController {
	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(IntegrarFolhaWebController.class);

	public static final String KEY_ARQUIVO_AGUARDANDO_PROCESSAMENTO = "_arquivo_aguardando_processamento";

	public static final String KEY_ARQUIVO_CRITICA_NAME = "arquivoCritica_name";

	public static final String KEY_ARQUIVO_CRITICA_PATH = "arquivoCritica_absolutePath";

	public static final String CHAVE_PROCESSAMENTO = "PROCESSO_FOLHA_ORIENTADA(MARGEM/RETORNO)";

	@Autowired
	private ConsignanteController consignanteController;

	@Autowired
	private ValidaImportacaoController validaImportacaoController;

	@Autowired
	private PesquisarConsignacaoController pesquisarConsignacaoController;

	@Autowired
	private UsuarioController usuarioController;

	/**
	 *
	 */
	@Override
	protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
		super.configurarPagina(request, session, model, responsavel);
		// Adiciona ao model as informações específicas da operação
		model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.integracao.orientada.titulo", responsavel));
	}

	/**
	 *
	 */
	@Override
	@RequestMapping(params = { "acao=iniciar" })
	public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
	    AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
		try {
            verificarCargaMargens(request, session, model, responsavel);

            SynchronizerToken.saveToken(request);

            boolean habilitaAmbienteDeTestes = ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_AMBIENTE_DE_TESTES, responsavel);
            model.addAttribute("habilitaAmbienteDeTestes", habilitaAmbienteDeTestes);
            if (habilitaAmbienteDeTestes) {
            	verificarHistorico(request, response, session, model);
            }

            boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(CHAVE_PROCESSAMENTO, session);

            boolean exibeCaptcha = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            boolean exibeCaptchaAvancado = exibeCaptcha ? false : ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            boolean exibeCaptchaDeficiente = false;
            UsuarioTransferObject usuarioResp = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel);

            if (usuarioResp != null && usuarioResp.getUsuDeficienteVisual() != null && usuarioResp.getUsuDeficienteVisual().equals("S")) {
                exibeCaptcha = false;
                exibeCaptchaAvancado = false;
                exibeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            } else if (!exibeCaptcha && !exibeCaptchaAvancado) {
                //caso não tenha nenhum captcha habilitado mostra o padrão que é captcha simples
                exibeCaptcha = true;
            }

            model.addAttribute("temProcessoRodando", temProcessoRodando);

            verificarArquivos("margem", request, response, session, model);
            verificarArquivos("retorno", request, response, session, model);

            tutorialHistoricoContratos(model, session, responsavel);

            model.addAttribute("arqManual", ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_MANUAL_INTEGRACAO_ORIENTADA, responsavel));


            model.addAttribute("exibeCaptcha", exibeCaptcha);
            model.addAttribute("exibeCaptchaAvancado", exibeCaptchaAvancado);
            model.addAttribute("exibeCaptchaDeficiente", exibeCaptchaDeficiente);

            return viewRedirect("jsp/integrarFolha/dashboard", request, session, model, responsavel);
        } catch (UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
	}

	/**
	 * DESENV-9655 - euConsigoMais - Tutorial sobre histórico de contratos no eConsig
	 */
	private void tutorialHistoricoContratos (Model model, HttpSession session, AcessoSistema responsavel) {
		if (ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_TUTORIAL_PRIMEIRO_ACESSO, responsavel))  {
			try {
				int total = 0;

				if (responsavel.isOrg()) {
					TransferObject criterio = new CustomTransferObject();
					criterio.setAttribute(Columns.ORG_CODIGO, responsavel.getCodigoEntidade());
					criterio.setAttribute(Columns.TOC_CODIGO, CodedValues.TOC_IMPORTACAO_MARGEM);
					total = consignanteController.countOcorrenciaOrgao(criterio, responsavel);

				} else {
					TransferObject criterio = new CustomTransferObject();
					criterio.setAttribute(Columns.CSE_CODIGO, responsavel.getCodigoEntidade());
					criterio.setAttribute(Columns.TOC_CODIGO, CodedValues.TOC_IMPORTACAO_MARGEM);
					total = consignanteController.countOcorrenciaConsignante(criterio, responsavel);
				}

				// tutorial só deve aparecer se não houver ocorrencia de importacao de margem
				if (total <= 0) {
					List<String> tutorialList = FileHelper.getFilesInDir(ParamSist.getDiretorioRaizArquivos() + "/imagem/historicocontratos/tutorial");

					if (!tutorialList.isEmpty()) {
						Collections.sort(tutorialList);
						model.addAttribute("tutorialList", tutorialList);
					}
				}
			} catch (Exception e) {
				LOG.error(e.getLocalizedMessage(), e);
			}
		}
	}

	/****************************************************************************************************************
	 *                                             MARGEM/RETORNO
	 ****************************************************************************************************************/

	/**
	 *
	 * @param request
	 * @param response
	 * @param session
	 * @param model
	 * @param tipo
	 * @return
	 */
	@RequestMapping(params = { "acao=upload" })
	public String upload(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, @RequestParam("tipo") String tipo) {
		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
		boolean selecionaEstOrgUploadMargemRetorno = false; // TODO: responsavel.isCseSup() && ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_SELEC_ENT_UPL_ARQ_MARGEM_RETORNO, responsavel);
		boolean selecionaEstOrgUploadContracheque = false;
		model.addAttribute("tipo", tipo);

		try {
			upload(tipo, false, selecionaEstOrgUploadMargemRetorno, selecionaEstOrgUploadContracheque, request, response, session, model);
		} catch (ZetraException ex) {
			session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}

		// remove mensagem de sucesso do método da classe mãe
		session.removeAttribute(CodedValues.MSG_INFO);
		String path = (String) model.asMap().get(KEY_ARQUIVO_SALVO_PATH);
		if (TextHelper.isNull(path)) {
			if (session.getAttribute(CodedValues.MSG_ERRO) == null) {
				// FIXME: Trocar para uma mensagem mais informativa que o upload não foi bem sucedido?
				session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
			}
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}
		String nomePadronizado = getNomeArquivoPadronizado(tipo, responsavel);
		File arquivoSalvo;
		if (!TextHelper.isNull(nomePadronizado)) {
			arquivoSalvo = new File((new File(path)).getParentFile(), nomePadronizado);
			FileHelper.rename(path, arquivoSalvo.getAbsolutePath());
			model.addAttribute(KEY_ARQUIVO_SALVO_NAME, arquivoSalvo.getName());
			model.addAttribute(KEY_ARQUIVO_SALVO_PATH, arquivoSalvo.getAbsolutePath());
		} else {
			arquivoSalvo = new File(path);
		}
		validarArquivo(tipo, arquivoSalvo.getAbsolutePath(), request, response, session, model);
		return viewRedirect("jsp/integrarFolha/resultadoUpload", request, session, model, responsavel);
	}

	/**
	 *
	 * @param request
	 * @param response
	 * @param session
	 * @param model
	 * @param tipo
	 * @return
	 * @throws UsuarioControllerException
	 */
	@RequestMapping(params = { "acao=selecionarArquivo" })
	public String selecionarArquivo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, @RequestParam("tipo") String tipo, @RequestParam("nomeArquivo") String nomeArquivo) throws UsuarioControllerException {
		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
		model.addAttribute("tipo", tipo);

		if (TextHelper.isNull(nomeArquivo)) {
			if (session.getAttribute(CodedValues.MSG_ERRO) == null) {
				// FIXME: Trocar para uma mensagem mais informativa que o upload não foi bem sucedido?
				session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
			}
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}

		boolean temPermissaoEst = false;
		if (responsavel.isOrg()) {
			temPermissaoEst = responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO);
		}

		//parametros de captcha
		boolean exibeCaptcha = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
		boolean exibeCaptchaAvancado = exibeCaptcha ? false : ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
		boolean exibeCaptchaDeficiente = false;
		UsuarioTransferObject usuarioResp = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel);
		ParamSession paramSession = ParamSession.getParamSession(session);

		if (usuarioResp != null && usuarioResp.getUsuDeficienteVisual() != null && usuarioResp.getUsuDeficienteVisual().equals("S")) {
			exibeCaptcha = false;
			exibeCaptchaAvancado = false;
			exibeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
		} else if (!exibeCaptcha && !exibeCaptchaAvancado) {
			//caso não tenha nenhum captcha habilitado mostra o padrão que é captcha simples
			exibeCaptcha = true;
		}

		ParamSist ps = ParamSist.getInstance();
		int maxSize = !TextHelper.isNull(ps.getParam(CodedValues.TPC_TAM_MAX_ARQ_UPLOAD_EM_LOTE_ANEXO, responsavel)) ? Integer.parseInt(ps.getParam(CodedValues.TPC_TAM_MAX_ARQ_UPLOAD_EM_LOTE_ANEXO, responsavel).toString()) : 20;
		maxSize = maxSize*1024*1024;

		UploadHelper uploadHelper = new UploadHelper();

		try {
			uploadHelper.processarRequisicao(request.getServletContext(), request, maxSize);
		} catch (Throwable ex) {
			LOG.error(ex.getMessage(), ex);
			String msg = ex.getMessage();
			if (!TextHelper.isNull(msg)) {
				session.setAttribute(CodedValues.MSG_ERRO, msg);
				return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
			}
		}

		//Validação captcha
		if (usuarioResp.getUsuDeficienteVisual() == null || usuarioResp.getUsuDeficienteVisual().equals("N")) {
			if (exibeCaptcha) {
				if (ImageCaptchaServlet.armazenaCaptcha(session.getId(), (String) session.getAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY))
                        && !ImageCaptchaServlet.validaCaptcha(session.getId(), uploadHelper.getValorCampoFormulario("captcha"))) {
					session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
					request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
					return "jsp/redirecionador/redirecionar";
				}
				session.removeAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);
			} else if (exibeCaptchaAvancado) {
				String remoteAddr = request.getRemoteAddr();

				if (!isValidCaptcha(uploadHelper.getValorCampoFormulario("g-recaptcha-response"), remoteAddr, responsavel)) {
					session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
					request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
					return "jsp/redirecionador/redirecionar";
				}
			}
		} else if (exibeCaptchaDeficiente) {
			String captchaAnswer = uploadHelper.getValorCampoFormulario("captcha");
			String captchaCode   = (String) session.getAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
			if (captchaCode == null || !captchaCode.equalsIgnoreCase(captchaAnswer)) {
				session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
				request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
				return "jsp/redirecionador/redirecionar";
			}
			session.removeAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
		}

		List<String> listPath = null;
		try {
			listPath = recuperarPath(tipo, false, false, model, responsavel.getPapCodigo(), responsavel.getOrgCodigo(), responsavel.getEstCodigo(), responsavel.getCsaCodigo(), responsavel.getCorCodigo(), temPermissaoEst, responsavel);
		} catch (ZetraException e) {
			LOG.error(e.getMessage(), e);
			session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.folha.arquivo.nao.encontrado", responsavel));
		}

		if (listPath != null && !listPath.isEmpty()) {

			String pathDir = ParamSist.getDiretorioRaizArquivos() + File.separatorChar + listPath.get(0);

			File arquivoSalvo = new File(pathDir, nomeArquivo);
			if (!arquivoSalvo.exists()) {
				session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.nao.encontrado", responsavel));
				return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
			}
			model.addAttribute(KEY_ARQUIVO_SALVO_NAME, arquivoSalvo.getName());
			model.addAttribute(KEY_ARQUIVO_SALVO_PATH, arquivoSalvo.getAbsolutePath());

			validarArquivo(tipo, arquivoSalvo.getAbsolutePath(), request, response, session, model);
			return viewRedirect("jsp/integrarFolha/resultadoUpload", request, session, model, responsavel);
		} else {
			session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.nao.encontrado", responsavel));
			return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
		}
	}

	/**
	 *
	 * @param request
	 * @param response
	 * @param session
	 * @param model
	 * @return
	 */
	@RequestMapping(params = { "acao=processar" })
	public String processar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, @RequestParam("tipo") String tipo) {
		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

		boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(CHAVE_PROCESSAMENTO, session);
		model.addAttribute("temProcessoRodando_" + tipo, temProcessoRodando);
		if (!temProcessoRodando) {
			String tipoEntidade = responsavel.getTipoEntidade();
			String codigoEntidade = responsavel.getCodigoEntidade();
			if (responsavel.isSup()) {
				tipoEntidade = "CSE";
			}
			try {
				LOG.debug("tipoEntidade = " + tipoEntidade);
				LOG.debug("codigoEntidade = " + codigoEntidade);

				String estCodigo = null;
				String orgCodigo = null;

				String nomeArquivo = null;
				String fileName = null;
				List<String> listPath = null;

				if (tipo.equalsIgnoreCase("margem")| tipo.equalsIgnoreCase("retorno")) {
					nomeArquivo = java.net.URLDecoder.decode(request.getParameter("arquivo_nome"), "UTF-8");

					if (nomeArquivo.indexOf("..") != -1) {
						session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.folha.impossibilidade.importacao.margens", responsavel));
						return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
					}

					boolean temPermissaoEst = false;
					if (responsavel.isOrg()) {
						temPermissaoEst = responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO);
						if (temPermissaoEst) {
							estCodigo = responsavel.getCodigoEntidadePai();
						} else {
							orgCodigo = codigoEntidade;
						}
					}

					try {
						listPath = recuperarPath(tipo, false, false, model, responsavel.getPapCodigo(), responsavel.getOrgCodigo(), responsavel.getEstCodigo(), responsavel.getCsaCodigo(), responsavel.getCorCodigo(), temPermissaoEst, responsavel);
					} catch (ZetraException e) {
						LOG.error(e.getMessage(), e);
						session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.folha.arquivo.nao.encontrado", responsavel));
						return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
					}

					if (listPath == null || listPath.isEmpty()) {
						session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.folha.arquivo.nao.encontrado", responsavel));
						return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
					}

					fileName = ParamSist.getDiretorioRaizArquivos()
							+ File.separatorChar + listPath.get(0) + File.separatorChar + nomeArquivo;

					File arqImportacao = new File(fileName);
					if (!arqImportacao.exists()) {
						session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.folha.arquivo.nao.encontrado", responsavel));
						return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
					}
				}

				Processo processo = null;
				switch (tipo.toLowerCase()) {
				case "margem":
					processo = new ProcessaMargem(fileName, tipoEntidade, codigoEntidade, true, false, responsavel);
					processo.setDescricao(ApplicationResourcesHelper.getMessage("mensagem.integracao.orientada.arquivo.margem.nome", responsavel, nomeArquivo));
					break;

				case "historico":
					String resetParam = JspHelper.verificaVarQryStr(request, "reset");
					boolean apagarAntigos = "all".intern().equals(resetParam);
					processo = new ProcessaHistorico(50, 2, true, null, apagarAntigos, responsavel);
					processo.setDescricao(ApplicationResourcesHelper.getMessage("mensagem.integracao.orientada.geracao.historico", responsavel, nomeArquivo));
					break;

				case "movimento":
					processo = new ProcessaMovimento(getEstabelecimentos(responsavel), null, null, ParametrosExportacao.AcaoEnum.EXPORTAR.getCodigo(), "1", responsavel);
					processo.setDescricao(ApplicationResourcesHelper.getMessage("mensagem.integracao.orientada.exportacao.movimento", responsavel, nomeArquivo));
					break;

				case "retorno":
					processo = new ProcessaRetorno(nomeArquivo, orgCodigo, estCodigo, tipo, responsavel);
					processo.setDescricao(ApplicationResourcesHelper.getMessage("mensagem.integracao.orientada.arquivo.retorno.nome", responsavel, nomeArquivo));
					break;

				default:
					session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
					return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
				}
				processo.start();
				ControladorProcessos.getInstance().incluir(CHAVE_PROCESSAMENTO, processo);
				session.removeAttribute(tipo + KEY_ARQUIVO_AGUARDANDO_PROCESSAMENTO);

			} catch (Exception ex) {
				LOG.error(ex.getMessage(), ex);
				session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.folha.falha.importacao.margem", responsavel, ex.getMessage()));
				return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
			}
		}
		return "forward:/v3/integrarFolha?acao=iniciar";
	}

	/****************************************************************************************************************
	 *                                             Métodos auxiliares
	 ****************************************************************************************************************/

	/**
	 *
	 * @param responsavel
	 * @return
	 * @throws ConsignanteControllerException
	 */
	private List<String> getEstabelecimentos(AcessoSistema responsavel) throws ConsignanteControllerException {
		List<TransferObject> estabelecimentos = consignanteController.lstEstabelecimentos(null, responsavel);
		Iterator<TransferObject> it = estabelecimentos.iterator();
		List<String> codigos = new ArrayList<>();
		while (it.hasNext()) {
			codigos.add((String) it.next().getAttribute(Columns.EST_CODIGO));
		}
		return codigos;
	}

	/**
	 *
	 * @param tipoArquivo
	 * @param responsavel
	 * @return
	 */
	private String getNomeArquivoPadronizado(String tipoArquivo, AcessoSistema responsavel) {
		String nomePadronizado = null;
		String tipoEntidade = responsavel.getTipoEntidade();
		String codigoEntidade = responsavel.getCodigoEntidade();
		try {
			Map<String, String> paramValidacaoArq = validaImportacaoController.lstParamValidacaoArq(tipoEntidade, codigoEntidade, null, null, responsavel);

			String padraoNomeArquivoFinal = paramValidacaoArq.get(tipoArquivo + ".padraoNomeArquivoFinal");
			nomePadronizado = ValidaImportacao.substituirPadroesNomeArquivoFinal(padraoNomeArquivoFinal, tipoEntidade, codigoEntidade, responsavel);
		} catch (ZetraException ex) {
			LOG.debug(ex);
		}
		return nomePadronizado;
	}

	/**
	 *
	 * @param tipo
	 * @param request
	 * @param response
	 * @param session
	 * @param model
	 * @return
	 */
	private void verificarArquivos(String tipo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		Set<String> arquivosPendentes = new HashSet<>();
		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
		String path = (String) session.getAttribute(tipo + KEY_ARQUIVO_AGUARDANDO_PROCESSAMENTO);
		if (!TextHelper.isNull(path)) {
			File aguardando = new File(path);
			if (aguardando.exists()) {
				arquivosPendentes.add(aguardando.getName());
			}
		}

		boolean temPermissaoEst = false;
		if (responsavel.isOrg()) {
			temPermissaoEst = responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO);
		}

		List<String> listPath = null;
		try {
			listPath = recuperarPath(tipo, false, false, model, responsavel.getPapCodigo(), responsavel.getOrgCodigo(), responsavel.getEstCodigo(), responsavel.getCsaCodigo(), responsavel.getCorCodigo(), temPermissaoEst, responsavel);
		} catch (ZetraException e) {
			LOG.error(e.getMessage(), e);
			session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.folha.arquivo.nao.encontrado", responsavel));
		}

		if (listPath != null && !listPath.isEmpty()) {
			String diretorioRaiz = ParamSist.getDiretorioRaizArquivos();
			String pathDir = diretorioRaiz + File.separatorChar + listPath.get(0);

			String prefixo = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.critica.prefixo", responsavel);
			FileFilter filtro = arq -> {
            	String arqName = arq.getName().toLowerCase();
            	return arq.isFile() && !arqName.startsWith(prefixo) && (arqName.endsWith(".txt") || arqName.endsWith(".zip"));
            };
			File dir = new File(pathDir);
			File[] arquivos = dir.listFiles(filtro);
			boolean possuiArquivos = arquivos != null && arquivos.length > 0 && arquivos[0] != null;
			if (possuiArquivos) {
				model.addAttribute(tipo + "_arquivoPendente", arquivos[0].getName());
			}
		}
	}

	/**
	 *
	 * @param request
	 * @param response
	 * @param session
	 * @param model
	 */
	private void verificarHistorico(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
		boolean temHistorico = false;
		try {
			int count = pesquisarConsignacaoController.countPesquisaAutorizacao(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), null, null, null, null, null, null, responsavel);
			temHistorico = (count > 0);
		} catch (AutorizacaoControllerException ex) {
			LOG.error(ex.getMessage(), ex);
			session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
		}
		model.addAttribute("temHistorico", temHistorico);
	}

	/**
	 *
	 * @param tipo
	 * @param path
	 * @param request
	 * @param response
	 * @param session
	 * @param model
	 */
	private void validarArquivo(String tipo, String path, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
		AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
		String configurationFileName;
		String diretorioRaiz = ParamSist.getDiretorioRaizArquivos();
		switch (tipo) {
		case "margem":
			configurationFileName = diretorioRaiz + File.separatorChar + "conf" + File.separatorChar
			+ (String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_INTEGRACAO_ORIENTADA_MARGEM, responsavel);
			break;

		case "retorno":
			configurationFileName = diretorioRaiz + File.separatorChar + "conf" + File.separatorChar
			+ (String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_INTEGRACAO_ORIENTADA_RETORNO, responsavel);
			break;

		default:
			// FIXME: Trocar para uma mensagem mais informativa dizendo que a configuração não foi efetuada?
			session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
			return;
		}
		if (!new File(configurationFileName).exists()) {
			session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
			return;
		}
		model.addAttribute("dataValidacaoArquivo", DateHelper.toDateTimeString(DateHelper.getSystemDatetime()));
		TextFileReader reader = new TextFileReader(configurationFileName);
		try {
			File inputFile = new File(path);
			reader.setSource(inputFile);
			Validator validator = new Validator(reader, configurationFileName, null);
			validator.validate(true);

			String prefixo = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.critica.prefixo", responsavel);
			File resultFile = new File(inputFile.getParent(), prefixo + inputFile.getName());
			BufferedReader in = null;
			PrintWriter out = null;
			try {
				String msgSucesso = ApplicationResourcesHelper.getMessage("mensagem.informacao.validacao.realizada.sucesso", responsavel);
				in = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
				out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultFile))));
				String linha = null;
				Iterator<Record> it = validator.getRecords().iterator();
				while ((linha = in.readLine()) != null) {
					out.print(linha);
					if (!linha.endsWith(";")) {
						out.print(";");
					}
					if (it.hasNext()) {
						Record record = it.next();
						out.println(!TextHelper.isNull(record.getMessage()) ? record.getMessage().replaceAll("\n", " ") : msgSucesso);
					}
				}
			} catch (IOException ex) {
				LOG.error(ex.getMessage());
			} finally {
				if (in != null) {
					try {
						in.close();
					} catch (IOException e) {
						// just ignore
					}
				}
				if (out != null) {
					out.close();
				}
			}
			model.addAttribute(KEY_ARQUIVO_CRITICA_NAME, resultFile.getName());
			model.addAttribute(KEY_ARQUIVO_CRITICA_PATH, resultFile.getAbsolutePath());

			int maxSize = 10;
			List<Record> lista = new ArrayList<>();
			for (Record record : validator.getRecords()) {
				if (lista.size() >= maxSize) {
					break;
				}
				if (record.getState() != null && record.getState().intValue() == -1) {
					lista.add(record);
				}
			}
			if (lista.size() < maxSize) {
				for (Record record : validator.getRecords()) {
					if (lista.size() >= maxSize) {
						break;
					}
					if (record.getState() != null && record.getState().intValue() == 0) {
						lista.add(record.getIndex() - 1, record);
					}
				}
			}
			model.addAttribute("validacao_arquivo_listagem", lista);
			model.addAttribute("validacao_arquivo_sucesso", !validator.hasError());
			if (validator.hasError()) {
				session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.integracao.orientada.validacao.com.erro", responsavel));
			} else {
				session.setAttribute(tipo + KEY_ARQUIVO_AGUARDANDO_PROCESSAMENTO, inputFile.getAbsolutePath());
				session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.integracao.orientada.validacao.sem.erro", responsavel));
			}
		} catch (InterpreterException ex) {
			session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.integracao.orientada.validacao.impossivel", responsavel));
		}
	}

	@Override
	protected List<String> recuperarPath(String tipo, boolean selecionaEstOrgUploadMargemRetorno, boolean selecionaEstOrgUploadContracheque, Model model, String papCodigo, String orgCodigo, String estCodigo, String csaCodigo, String corCodigo, boolean temPermissaoEst, AcessoSistema responsavel) throws ZetraException {
		String path = tipo + java.io.File.separatorChar;
		List<String> listPath = new ArrayList<>();

		if (responsavel.isOrg() && (((!selecionaEstOrgUploadMargemRetorno || !selecionaEstOrgUploadContracheque) && temPermissaoEst) || ((selecionaEstOrgUploadMargemRetorno || selecionaEstOrgUploadContracheque) && papCodigo.equals(AcessoSistema.ENTIDADE_EST)))) {
			listPath.add(path + "est" + java.io.File.separatorChar + responsavel.getCodigoEntidadePai());
		} else if (responsavel.isOrg()) {
			listPath.add(path + "cse" + java.io.File.separatorChar + responsavel.getCodigoEntidade());
		} else if (responsavel.isCseSup()) {
			if ((selecionaEstOrgUploadMargemRetorno || selecionaEstOrgUploadContracheque) && !TextHelper.isNull(papCodigo)) {
				if (papCodigo.equals(AcessoSistema.ENTIDADE_ORG)) {
					if (TextHelper.isNull(orgCodigo)) {
						throw new ZetraException("mensagem.usoIncorretoSistema", responsavel);
					}
					listPath.add(path + "cse" + java.io.File.separatorChar + orgCodigo);
				} else if (papCodigo.equals(AcessoSistema.ENTIDADE_EST)) {
					if (TextHelper.isNull(estCodigo)) {
						throw new ZetraException("mensagem.usoIncorretoSistema", responsavel);
					}
					listPath.add(path + "est" + java.io.File.separatorChar + estCodigo);
				} else if (papCodigo.equals(AcessoSistema.ENTIDADE_CSE)) {
					listPath.add(path + "cse");
				}
			} else {
				listPath.add(path + "cse");
			}
		}
		return listPath;
	}

	@Override
	protected boolean getExibeCaptchaDefault() {
		return true;
	}
}
