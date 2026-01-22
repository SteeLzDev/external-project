package com.zetra.econsig.web.controller;

import java.beans.PropertyEditor;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.MenuTO;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.ItemMenuEnum;
import com.zetra.econsig.web.DuplicateParameterReducingPropertyEditor;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: AbstractWebController</p>
 * <p>Description: Controlador Web base para implementações dos casos de uso.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class AbstractWebController {
	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AbstractWebController.class);

	public static final String LAYOUT_V4 = "v4";
	public static final String LAYOUT_V3 = "v3";
	public static final String MODO_INTEGRAR_FOLHA = "modoIntegrarFolha";

	@Autowired
	private ConvenioController convenioController;

	@Autowired
	private ConsignanteController consignanteController;

	@Autowired
	private ConsignatariaController consignatariaController;

	@Autowired
	private MargemController margemController;

	@Autowired
	private ServicoController servicoController;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		PropertyEditor stringEditor = new DuplicateParameterReducingPropertyEditor();
		binder.registerCustomEditor(String.class, stringEditor);
	}

	protected String forwardUrl(String url) {
		return url.replaceAll("^..", "");
	}

	protected String viewRedirect(String viewName, HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
		return viewRedirect(viewName, request, session, model, responsavel, true);
	}

	protected String viewRedirectNoSuffix(String viewName, HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
		return viewRedirect(viewName, request, session, model, responsavel, false);
	}

	private String viewRedirect(String viewName, HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel, boolean sufixo) {
		// Repassa responsável para poder ser reutilizado através de EL
		model.addAttribute("responsavel", responsavel);

		String sufixoLeiaute = "v4";// ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel).toString();

		try {
			configurarPagina(request, session, model, responsavel);
			configurarCampoSistema(model, responsavel);
		} catch (ViewHelperException ex) {
			session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
			return "jsp/visualizarPaginaErro/visualizarMensagem" + "_" + sufixoLeiaute;
		}

		if (sufixo) {
			return viewName + "_" + sufixoLeiaute;
		} else {
			return viewName;
		}
		// Verifica se o JSP existe
		//Resource resource1 = resourceLoader.getResource("/WEB-INF/" + newViewName + ".jsp");
		//Resource resource2 = resourceLoader.getResource(ResourceLoader.CLASSPATH_URL_PREFIX + "/WEB-INF/" + newViewName + ".jsp");
		//if (resource1.exists() || resource2.exists()) {
		//    return newViewName;
		//}
		//return viewName;
	}

	protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
	}

	protected void configurarCampoSistema(Model model, AcessoSistema responsavel) throws ViewHelperException {
	}

	protected void carregarListaEstabelecimento(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
		try {
			CustomTransferObject criterio = new CustomTransferObject();
			if (responsavel.isOrg()) {
				criterio.setAttribute(Columns.EST_CODIGO, responsavel.getEstCodigo());
			}
			List<TransferObject> lstEstabelecimento = consignanteController.lstEstabelecimentos(criterio, responsavel);
			model.addAttribute("lstEstabelecimento", lstEstabelecimento);
		} catch (ConsignanteControllerException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	protected void carregarListaOrgao(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
		try {
			List<TransferObject> lstOrgao = null;

			if (responsavel.isCseSupOrg()) {
				CustomTransferObject criterio = new CustomTransferObject();
				if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
					criterio.setAttribute(Columns.ORG_EST_CODIGO, responsavel.getEstCodigo());
				} else if (responsavel.isOrg()) {
					criterio.setAttribute(Columns.ORG_CODIGO, responsavel.getOrgCodigo());
				}
				lstOrgao = consignanteController.lstOrgaos(criterio, responsavel);

			} else if (responsavel.isCsaCor()) {
				String corCodigo = (responsavel.isCor() && !responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) ? responsavel.getCodigoEntidade() : null;
				String csaCodigo = (responsavel.isCsa()) ? responsavel.getCodigoEntidade() : ((responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) ? responsavel.getCodigoEntidadePai() : null);
				lstOrgao = convenioController.getOrgCnvAtivo(csaCodigo, corCodigo, responsavel);
			}
			model.addAttribute("lstOrgao", lstOrgao);

		} catch (ConsignanteControllerException | ConvenioControllerException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	protected void carregarListaConsignataria(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
		try {
			List<TransferObject> lstConsignataria = convenioController.getCsaCnvAtivo(null, responsavel.getOrgCodigo(), responsavel);
			model.addAttribute("lstConsignataria", lstConsignataria);
		} catch (ConvenioControllerException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	protected void carregarListaCorrespondente(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
		try {
			if (responsavel.isCsa() || (responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA))) {
				String csaCodigo = responsavel.getCodigoEntidade();
				CustomTransferObject criterio = new CustomTransferObject();
				criterio.setAttribute(Columns.COR_CSA_CODIGO, csaCodigo);
				// Lista somente os correspondentes ativos ou bloqueados
				List<Short> statusCor = new ArrayList<>();
				statusCor.add(CodedValues.STS_ATIVO);
				statusCor.add(CodedValues.STS_INATIVO);
				statusCor.add(CodedValues.STS_INATIVO_CSE);
				statusCor.add(CodedValues.STS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);
				criterio.setAttribute(Columns.COR_ATIVO, statusCor);
				List<TransferObject> lstCorrespondente = consignatariaController.lstCorrespondentes(criterio, responsavel);
				model.addAttribute("lstCorrespondente", lstCorrespondente);
			}
		} catch (ConsignatariaControllerException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	protected void carregarListaServico(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
		try {
			List<TransferObject> lstConvenio = convenioController.lstCnvEntidade(responsavel.getCodigoEntidade(), responsavel.getTipoEntidade(), null, responsavel);
			List<TransferObject> lstServico = TextHelper.groupConcat(lstConvenio, new String[]{Columns.SVC_DESCRICAO,Columns.SVC_CODIGO}, new String[]{Columns.CNV_COD_VERBA}, ",", true, true);
			model.addAttribute("lstServico", lstServico);
		} catch (ConvenioControllerException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	protected void carregarListaNaturezaServico(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
		try {
			List<TransferObject> lstNaturezaSvc = servicoController.lstNaturezasServicos(false);
			model.addAttribute("lstNaturezaSvc", lstNaturezaSvc);
		} catch (ServicoControllerException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	protected void carregarListaMargem(boolean inserirOpcaoQualquerMargem, HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
		try {
			List<MargemTO> lstMargens = margemController.lstMargemRaiz(responsavel);
			if (inserirOpcaoQualquerMargem) {
				// Adiciona no index 1, logo após a opção "NÃO INCIDE"
				lstMargens.add(1, new MargemTO((short) CodedValues.INCIDE_MARGEM_QQ, ApplicationResourcesHelper.getMessage("rotulo.margem.incide.qualquer", responsavel)));
			}

			model.addAttribute("lstMargens", lstMargens);
		} catch (MargemControllerException ex) {
			LOG.error(ex.getMessage(), ex);
		}
	}

	/**
     * Método para validar resposta do captcha avançado.
     *
     * TODO: Método incluído como público e estático porque existem arquivos que ainda não foram refatorados (back-end) que precisam validar
     * o captcha avançado. Após realizar toda refatoração, alterar o método para protected não estático.
     *
     * @param response
     * @param remoteip
     * @param responsavel
     * @return
     */
    public static Boolean isValidCaptcha(String response, String remoteip, AcessoSistema responsavel) {
        String chavePrivada = (String) ParamSist.getInstance().getParam(CodedValues.TPC_CAPTCHA_AVANCADO_CHAVE_PRIVADA, responsavel);

        if (TextHelper.isNull(chavePrivada) || TextHelper.isNull(response)) {
            return false;
        }

        JsonObject jsonObject = null;
        URLConnection connection = null;
        InputStream is = null;
        String charset = java.nio.charset.StandardCharsets.UTF_8.name();

        String url = "https://www.google.com/recaptcha/api/siteverify";
        try {
            String query = String.format("secret=%s&response=%s&remoteip=%s",
            URLEncoder.encode(chavePrivada, charset),
            URLEncoder.encode(response, charset),
            URLEncoder.encode(remoteip, charset));

            connection = URI.create(url + "?" + query).toURL().openConnection();
            is = connection.getInputStream();
            JsonReader rdr = Json.createReader(is);
            jsonObject = rdr.readObject();

        } catch (IOException ex) {
            LOG.debug(ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    LOG.debug(ex);
                }
            }
        }

        return !TextHelper.isNull(jsonObject) && jsonObject.getBoolean("success");
    }

    /**
     * Método para validar resposta do Hcaptcha.
     *
     * TODO: Método incluído como público e estático porque existem arquivos que ainda não foram refatorados (back-end) que precisam validar
     * o captcha avançado. Após realizar toda refatoração, alterar o método para protected não estático.
     *
     * @param response
     * @param remoteip
     * @param responsavel
     * @return
     */
    public static Boolean isValidHcaptcha(String response, String remoteip, AcessoSistema responsavel) {
        String chavePrivada = (String) ParamSist.getInstance().getParam(CodedValues.TPC_H_CAPTCHA_CHAVE_PRIVADA, responsavel);

        if (TextHelper.isNull(chavePrivada) || TextHelper.isNull(response)) {
            return false;
        }

        JsonObject jsonObject = null;
        URLConnection connection = null;
        InputStream is = null;
        String charset = java.nio.charset.StandardCharsets.UTF_8.name();

        String url = "https://api.hcaptcha.com/siteverify";
        try {
            String query = String.format("secret=%s&response=%s&remoteip=%s",
            URLEncoder.encode(chavePrivada, charset),
            URLEncoder.encode(response, charset),
            URLEncoder.encode(remoteip, charset));

            connection = URI.create(url + "?" + query).toURL().openConnection();
            is = connection.getInputStream();
            JsonReader rdr = Json.createReader(is);
            jsonObject = rdr.readObject();

        } catch (IOException ex) {
            LOG.debug(ex);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex) {
                    LOG.debug(ex);
                }
            }
        }

        return !TextHelper.isNull(jsonObject) && jsonObject.getBoolean("success");
    }

	/**
	 * Verifica itens para menu drop down quando acessado via centralizador
	 */
	protected void verificarAcessoMenuDropDown(HttpSession session, AcessoSistema usuAcesso, List<MenuTO> mnuLst) {

		session.setAttribute(ItemMenuEnum.ALTERAR_SENHA.getDescricao(), false);
		session.setAttribute(ItemMenuEnum.SOBRE.getDescricao(), false);
		session.setAttribute(ItemMenuEnum.TERMO_DE_USO.getDescricao(), false);
		session.setAttribute(ItemMenuEnum.TERMO_DE_ADESAO.getDescricao(), false);
		session.setAttribute(ItemMenuEnum.SAIR_DO_SISTEMA.getDescricao(), false);

		if (!"S".equals(usuAcesso.getUsuCentralizador())) {


			// Alterar senha - 103
			if (mnuLst.stream().anyMatch(mnu -> mnu.getItens().stream().anyMatch(itmMnu -> itmMnu.getItmCodigo().equals(String.valueOf(ItemMenuEnum.ALTERAR_SENHA.getCodigo()))))) {
				session.setAttribute(ItemMenuEnum.ALTERAR_SENHA.getDescricao(), true);
			}

			// Visualizar sobre - 105
			if (mnuLst.stream().anyMatch(mnu -> mnu.getItens().stream().anyMatch(itmMnu -> itmMnu.getItmCodigo().equals(String.valueOf(ItemMenuEnum.SOBRE.getCodigo()))))) {
				session.setAttribute(ItemMenuEnum.SOBRE.getDescricao(), true);
			}

			// Visualizar Termo Uso - 176
			if (mnuLst.stream().anyMatch(mnu -> mnu.getItens().stream().anyMatch(itmMnu -> itmMnu.getItmCodigo().equals(String.valueOf(ItemMenuEnum.TERMO_DE_USO.getCodigo()))))) {
				session.setAttribute(ItemMenuEnum.TERMO_DE_USO.getDescricao(), true);
			}

			// Visualizar Termo Adesão - 289
			if (mnuLst.stream().anyMatch(mnu -> mnu.getItens().stream().anyMatch(itmMnu -> itmMnu.getItmCodigo().equals(String.valueOf(ItemMenuEnum.TERMO_DE_ADESAO.getCodigo()))))) {
				session.setAttribute(ItemMenuEnum.TERMO_DE_ADESAO.getDescricao(), true);
			}

			// Sair Sistema - 102
			if (mnuLst.stream().anyMatch(mnu -> mnu.getItens().stream().anyMatch(itmMnu -> itmMnu.getItmCodigo().equals(String.valueOf(ItemMenuEnum.SAIR_DO_SISTEMA.getCodigo()))))) {
				session.setAttribute(ItemMenuEnum.SAIR_DO_SISTEMA.getDescricao(), true);
			}
		}
	}

	/**
	 * Verifica que já foi efetuada uma carga de margem no sistema.
	 * @param request
	 * @param session
	 * @param model
	 * @param responsavel
	 */
	protected void verificarCargaMargens(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
		boolean margensCarregadas = false;
		try {
			if (responsavel.isOrg()) {
				TransferObject criterio = new CustomTransferObject();
				criterio.setAttribute(Columns.ORG_CODIGO, responsavel.getCodigoEntidade());
				criterio.setAttribute(Columns.TOC_CODIGO, CodedValues.TOC_IMPORTACAO_MARGEM);
				margensCarregadas = consignanteController.countOcorrenciaOrgao(criterio, responsavel) > 0;

			} else {
				TransferObject criterio = new CustomTransferObject();
				criterio.setAttribute(Columns.CSE_CODIGO, CodedValues.CSE_CODIGO_SISTEMA);
				criterio.setAttribute(Columns.TOC_CODIGO, CodedValues.TOC_IMPORTACAO_MARGEM);
				margensCarregadas = consignanteController.countOcorrenciaConsignante(criterio, responsavel) > 0;
			}
		} catch (ConsignanteControllerException ex) {
			LOG.error(ex.getMessage(), ex);
		}

		if (margensCarregadas) {
			model.addAttribute(MODO_INTEGRAR_FOLHA, "completo");
		} else {
			model.addAttribute(MODO_INTEGRAR_FOLHA, "acessoInicial");
		}
	}

	/**
	 * Verifica se a função exige motivo de operação, conforme parâmetro de sistema
	 * e configuração da tabela de função.
	 * @param funCodigo
	 * @param responsavel
	 * @return
	 */
	protected boolean isExigeMotivoOperacao(String funCodigo, AcessoSistema responsavel) {
		// Verifica se a função exige tipo de motivo da operação
		Boolean exigeTipoMotivoOperacao = FuncaoExigeMotivo.getInstance().exists(funCodigo, responsavel);

		// Busca atributos quanto a exigencia de Tipo de motivo da operacao
		if (!ParamSist.paramEquals(CodedValues.TPC_EXIGE_TIPO_MOTIVO_CANC, CodedValues.TPC_SIM, responsavel) || !exigeTipoMotivoOperacao) {
			return false;
		}
		return true;
	}

	/**
	 * Verifica se tipo justiça é obrigatório
	 * e configuração da tabela de função.
	 * @param responsavel
	 * @return
	 */
	protected boolean isTipoJusticaObrigatorio(AcessoSistema responsavel) throws ZetraException {
		return false;
	}

	/**
	 * Verifica se comarca justiça é obrigatório
	 * e configuração da tabela de função.
	 * @param responsavel
	 * @return
	 */
	protected boolean isComarcaJusticaObrigatorio(AcessoSistema responsavel) throws ZetraException {
		return false;
	}

	/**
	 * Verifica se tipo justiça é obrigatório
	 * e configuração da tabela de função.
	 * @param responsavel
	 * @return
	 */
	protected boolean isNumeroProcessoObrigatorio(AcessoSistema responsavel) throws ZetraException {
		return false;
	}

	/**
	 * Verifica se data decisão é obrigatório
	 * e configuração da tabela de função.
	 * @param responsavel
	 * @return
	 */
	protected boolean isDataDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
		return false;
	}

	/**
	 * Verifica se texto decisão é obrigatório
	 * e configuração da tabela de função.
	 * @param responsavel
	 * @return
	 */
	protected boolean isTextoDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
		return false;
	}

	/**
	 * Verifica se anexo é obrigatório
	 * e configuração da tabela de função.
	 * @param responsavel
	 * @return
	 */
	protected boolean isAnexoDecisaoObrigatorio(AcessoSistema responsavel) throws ZetraException {
		return false;
	}

	/**
	 * Retorna a resposta se a combianação de usuário responsável x entidade é válida ou não
	 * para consulta ou manutenção de usuários e perfis de usuários
	 * @param tipo
	 * @param codEntidade
	 * @param responsavel
	 * @throws ConsignatariaControllerException
	 */
	protected boolean isTipoEntidadeInvalido(String tipo, String codEntidade, AcessoSistema responsavel) throws ConsignatariaControllerException {
		String codEntidadePai = null;
		if (responsavel.isCsa() && tipo.equals(AcessoSistema.ENTIDADE_COR)) {
			CorrespondenteTransferObject cor = consignatariaController.findCorrespondente(codEntidade, responsavel);
			if (cor != null) {
				codEntidadePai = cor.getCsaCodigo();
			}
		}

		return ((!tipo.equals(AcessoSistema.ENTIDADE_CSE) && !tipo.equals(AcessoSistema.ENTIDADE_SUP) && !tipo.equals(AcessoSistema.ENTIDADE_ORG) && !tipo.equals(AcessoSistema.ENTIDADE_CSA) && !tipo.equals(AcessoSistema.ENTIDADE_COR) && !tipo.equals(AcessoSistema.ENTIDADE_SER)) ||
				(responsavel.isCsa() && !tipo.equals(AcessoSistema.ENTIDADE_CSA) && !tipo.equals(AcessoSistema.ENTIDADE_COR)) ||
				(responsavel.isCsa() && tipo.equals(AcessoSistema.ENTIDADE_CSA) && !responsavel.getCodigoEntidade().equals(codEntidade)) ||
				(responsavel.isCsa() && tipo.equals(AcessoSistema.ENTIDADE_COR) && !responsavel.getCodigoEntidade().equals(codEntidadePai)) ||
				(responsavel.isCor() && !tipo.equals(AcessoSistema.ENTIDADE_COR)) ||
				(responsavel.isCor() && tipo.equals(AcessoSistema.ENTIDADE_COR) && !responsavel.getCodigoEntidade().equals(codEntidade))) ||
				(responsavel.isOrg() && !tipo.equals(AcessoSistema.ENTIDADE_ORG) && !tipo.equals(AcessoSistema.ENTIDADE_SER) && !tipo.equals(AcessoSistema.ENTIDADE_CSA)) ||
				(responsavel.isOrg() && tipo.equals(AcessoSistema.ENTIDADE_ORG) && !responsavel.getCodigoEntidade().equals(codEntidade));
	}
}
