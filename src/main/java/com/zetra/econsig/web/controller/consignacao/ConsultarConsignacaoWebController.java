package com.zetra.econsig.web.controller.consignacao;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.zetra.econsig.service.consignacao.OcorrenciaAutorizacaoController;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.dto.web.ColunaListaConsignacao;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.consignacao.PriceHelper.TabelaPrice;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.margem.ControleConsulta;
import com.zetra.econsig.helper.margem.TextoMargem;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.pdf.PDFHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.CampoUsuario;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.values.TipoMotivoBloqueioEnum;
import com.zetra.econsig.web.servlet.AudioCaptchaServlet;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;
import com.zetra.econsig.web.tag.v4.TabelaPriceTag;

/**
 * <p>Title: ConsultarConsignacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso Consultar Consignação.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/consultarConsignacao" })
public class ConsultarConsignacaoWebController extends AbstractConsultarConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConsultarConsignacaoWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private MargemController margemController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private OcorrenciaAutorizacaoController ocorrenciaAutorizacaoController;

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
    	AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);
        model.addAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, session.getAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY));
        definirAcaoRetorno(request, response, session, model);

        // Remove captcha consulta de consignação
        session.removeAttribute("isValidCaptcha");

        try {
            // Parâmetro de obrigatoriedade de CPF e Matrícula
            boolean requerMatriculaCpf = false;
            if (ParamSist.paramEquals(CodedValues.TPC_INF_MAT_CPF_EDT_CONSIGNACAO, CodedValues.TPC_SIM, responsavel)) {
                requerMatriculaCpf = false;
            } else {
                requerMatriculaCpf = parametroController.requerMatriculaCpf(responsavel);
            }
            if (requerMatriculaCpf) {
                model.addAttribute("requerMatriculaCpf", Boolean.TRUE);
            }

            // Parâmetro de obrigatoriedade de data de nascimento
	        if (parametroController.requerDataNascimento(responsavel)) {
	            model.addAttribute("requerDataNascimento", Boolean.TRUE);
	        }
        } catch (ParametroControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }


		if (responsavel.temPermissao(CodedValues.FUN_PESQUISA_AVANCADA_CONSIGNACAO)) {
			carregarPesquisaAvancada(request, session, model, responsavel);
	        return viewRedirect("jsp/consultarConsignacao/filtrarConsignacao", request, session, model, responsavel);
		} else {
			return viewRedirect("jsp/consultarServidor/pesquisarServidor", request, session, model, responsavel);
		}

    }

    @RequestMapping(params = { "acao=fixarCampos" })
    public String fixarCampos(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws UsuarioControllerException {
    	AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
    	String campoUsuarioJSON = JspHelper.verificaVarQryStr(request, "campoUsuario");
    	ObjectMapper objectMapper = new ObjectMapper();

    	try {
			List<CampoUsuario> lstCampoUsuario = objectMapper.readValue(campoUsuarioJSON, new TypeReference<List<CampoUsuario>>(){});
			usuarioController.fixarCamposPesquisaAvancada(lstCampoUsuario, responsavel);
		} catch (JsonProcessingException | UsuarioControllerException ex) {
			LOG.error(ex.getMessage(), ex);
          throw new UsuarioControllerException("mensagem.erroInternoSistema", responsavel, ex);
		}

		carregarPesquisaAvancada(request, session, model, responsavel);

    	return viewRedirect("jsp/consultarConsignacao/filtrarConsignacao", request, session, model, responsavel);

    }


    @RequestMapping(params = { "acao=filtrar" })
    public String filtrar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ZetraException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        // Remove captcha consulta de consignação
        session.removeAttribute("isValidCaptcha");

        carregarPesquisaAvancada(request, session, model, responsavel);
        model.addAttribute("pesquisaAvancada", Boolean.TRUE);

        return viewRedirect("jsp/consultarConsignacao/filtrarConsignacao", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=pesquisarConsignacao", "TIPO_LISTA=pesquisa_avancada" })
    public String pesquisarConsignacaoAvancada(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, InstantiationException, IllegalAccessException, ParametroControllerException, ServidorControllerException, AutorizacaoControllerException, ParseException, ZetraException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        request.setAttribute("pesquisaAvancada",  Boolean.TRUE);
        request.setAttribute("tituloResultado", ApplicationResourcesHelper.getMessage("rotulo.pesquisa.avancada", responsavel).toUpperCase());

        Object paramValidarVezesCaptcha = ParamSist.getInstance().getParam(CodedValues.TPC_VALIDAR_QTDE_VEZES_CAPTCHA_CONSULTAR_CONSIGNACAO, null);
        if (responsavel.isCsaCor() && !TextHelper.isNull(paramValidarVezesCaptcha) && Integer.parseInt(paramValidarVezesCaptcha.toString()) >= 0) {
        	boolean isValid = validarCaptcha(request, response, session, model, responsavel);
        	if (!isValid) {
        		return filtrar(request, response, session, model);
        	}
        }

        String adeNumero = request.getParameter("ADE_NUMERO");
        return pesquisarConsignacao(null, adeNumero, request, response, session, model);
    }

    private void carregarPesquisaAvancada(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        try {

			try {
				Map<String, String> mapCampoUsuario =
						usuarioController.buscarCamposPesquisaAvancada(responsavel).stream()
						.collect(Collectors.toMap(CampoUsuario::getCauChave, CampoUsuario::getCauValor));
					model.addAttribute("mapCampoUsuario", mapCampoUsuario);
				} catch (UsuarioControllerException ex) {
					LOG.error(ex.getMessage(), ex);
				}

			if (responsavel.isCseSupOrg()) {
                try {
                    List<TransferObject> lstGrupoConsignataria = consignatariaController.lstGrupoConsignataria(null, responsavel);
                    model.addAttribute("lstGrupoConsignataria", lstGrupoConsignataria);
                } catch (ConsignatariaControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }

            if (responsavel.isCseSupOrg() || responsavel.isSer()) {
                try {
                    String orgCodigo = (responsavel.isOrg() ? responsavel.getCodigoEntidade() : null);
                    String rseCodigo = (responsavel.isSer() ? responsavel.getRseCodigo() : JspHelper.verificaVarQryStr(request, "RSE_CODIGO"));
                    if (!TextHelper.isNull(rseCodigo)) {
                        RegistroServidorTO servidor = servidorController.findRegistroServidor(rseCodigo, responsavel);
                        if (servidor != null) {
                            orgCodigo = servidor.getOrgCodigo();
                        }
                    }

                    List<TransferObject> lstConsignataria = convenioController.getCsaCnvAtivo(null, orgCodigo, responsavel);
                    model.addAttribute("lstConsignataria", lstConsignataria);
                } catch (ConvenioControllerException | ServidorControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }

            if (responsavel.isCsa()) {
                try {
                    String csaCodigo = responsavel.getCodigoEntidade();
                    TransferObject criterio = new CustomTransferObject();
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
                } catch (ConsignatariaControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }

            try {
                List<TransferObject> lstConvenio = convenioController.lstCnvEntidade(responsavel.getCodigoEntidade(), responsavel.getTipoEntidade(), "consultar", responsavel);
                List<TransferObject> lstNaturezas = new ArrayList<>();
                List<String> nseDescricao = new ArrayList<>();

                for (int i = 0; lstConvenio.size() > i ; i++ ) {
	                if (!nseDescricao.contains(lstConvenio.get(i).getAttribute(Columns.NSE_DESCRICAO))) {
	                	lstNaturezas.add(lstConvenio.get(i)) ;
	                	nseDescricao.add((String) lstConvenio.get(i).getAttribute(Columns.NSE_DESCRICAO));
	                }
                }
                model.addAttribute("lstConvenio", lstConvenio);
                model.addAttribute("lstNaturezas", lstNaturezas);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }

            if (responsavel.isCseSupOrg() || responsavel.isSer()) {
                try {
                    List<TransferObject> lstGrupoServico = convenioController.lstGrupoServicos(false, responsavel);
                    model.addAttribute("lstGrupoServico", lstGrupoServico);
                } catch (ConvenioControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }

            if (responsavel.isCseSup() || responsavel.isCsaCor()) {
                try {
                    List<TransferObject> lstOrgao = null;
                    TransferObject criterio = null;
                    if (responsavel.isCsaCor()) {
                        String corCodigo = (responsavel.isCor() && !responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) ? responsavel.getCodigoEntidade() : null;
                        String csaCodigo = (responsavel.isCsa()) ? responsavel.getCodigoEntidade() : ((responsavel.isCor() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) ? responsavel.getCsaCodigo() : null);
                        lstOrgao = convenioController.getOrgCnvAtivo(csaCodigo, corCodigo, responsavel);
                    } else {
                        lstOrgao = consignanteController.lstOrgaos(criterio, responsavel);
                    }
                    model.addAttribute("lstOrgao", lstOrgao);
                } catch (ConvenioControllerException | ConsignanteControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }

            try {
                List<MargemTO> lstMargem = margemController.lstMargemRaiz(responsavel);
                model.addAttribute("lstMargem", lstMargem);
            } catch (MargemControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            try {
                List<TransferObject> lstMotivoOperacaoConsignacao = tipoMotivoOperacaoController.lstMotivoOperacaoConsignacao(null, responsavel);
                model.addAttribute("lstMotivoOperacaoConsignacao", lstMotivoOperacaoConsignacao);
            } catch (TipoMotivoOperacaoControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            // Verifica se o sistema permite cadastro de indice e parametros relacionados.
            boolean permiteCadIndice = ParamSist.paramEquals(CodedValues.TPC_PERMITE_CAD_INDICE, CodedValues.TPC_SIM, responsavel);
            if (permiteCadIndice) {
                boolean indiceNumerico = ParamSist.paramEquals(CodedValues.TPC_INDICE_NUMERICO, CodedValues.TPC_SIM, responsavel);
                int limiteIndice = (ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel) != null && !ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel).equals("")) ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel).toString()) : 99;
                String mascaraIndice = (indiceNumerico ? "#D" : "#A") + String.valueOf(limiteIndice).length();
                model.addAttribute("mascaraIndice", mascaraIndice);
            }

            //Verifica se exige senha do servidor para mostrar o valor da margem
            boolean exigeSenhaConsMargem = false;
            try {
                exigeSenhaConsMargem = parametroController.senhaServidorObrigatoriaConsultaMargem(null, responsavel);
            } catch (ParametroControllerException e) {
                LOG.error(e.getMessage(), e);
            }
            model.addAttribute("exigeSenhaConsMargem", exigeSenhaConsMargem);

            // Parâmetro de obrigatoriedade de CPF e Matrícula
            boolean requerMatriculaCpf = false;
            try {
                if (ParamSist.paramEquals(CodedValues.TPC_INF_MAT_CPF_EDT_CONSIGNACAO, CodedValues.TPC_SIM, responsavel)) {
                    requerMatriculaCpf = false;
                } else {
                    requerMatriculaCpf = parametroController.requerMatriculaCpf(responsavel);
                }

                // Parâmetro de obrigatoriedade de data de nascimento
    	        if (parametroController.requerDataNascimento(responsavel)) {
    	            model.addAttribute("requerDataNascimento", Boolean.TRUE);
    	        }
            } catch (ParametroControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }
            model.addAttribute("requerMatriculaCpf", requerMatriculaCpf);

            String rotuloCampoTodos = ApplicationResourcesHelper.getMessage("rotulo.campo.todos", responsavel);
            model.addAttribute("rotuloCampoTodos", rotuloCampoTodos);

            String ordenacao = JspHelper.verificaVarQryStr(request, "ORDENACAO");
            String ordenacaoAux = JspHelper.verificaVarQryStr(request, "ORDENACAO_AUX");
            List<String> lstOrdenacaoAux = new ArrayList<>();
            if (!TextHelper.isNull(ordenacaoAux)) {
                lstOrdenacaoAux = Arrays.asList(ordenacaoAux.split(","));
            }

            String ordemAdeData = JspHelper.verificaVarQryStr(request, "ORDEM_DATA");
            String ordemSerCpf = JspHelper.verificaVarQryStr(request, "ORDEM_CPF");
            String ordemRseMatricula = JspHelper.verificaVarQryStr(request, "ORDEM_MATRICULA");
            String ordemSerNome = JspHelper.verificaVarQryStr(request, "ORDEM_NOME");

            model.addAttribute("ordenacao", ordenacao);
            model.addAttribute("lstOrdenacaoAux", lstOrdenacaoAux);
            model.addAttribute("ordemAdeData", ordemAdeData);
            model.addAttribute("ordemSerCpf", ordemSerCpf);
            model.addAttribute("ordemRseMatricula", ordemRseMatricula);
            model.addAttribute("ordemSerNome", ordemSerNome);

        } catch (NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private void consultarMargem(String rseCodigo, List<TransferObject> lstConsignacao, HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws AutorizacaoControllerException {
        // Exibe as margens do servidor, caso seja consulta de consignação e a pesquisa tenha resultados
        if ((lstConsignacao != null && lstConsignacao.size() > 0) || !TextHelper.isNull(rseCodigo)) {
            // Se o resultado não é vazio e pertence ao mesmo servidor, então obtém primeiro contrato
            // da lista para recuperar informações do servidor
            TransferObject ade = null;
            if (request.getAttribute("resultadoMultiplosServidores") == null && lstConsignacao != null && !lstConsignacao.isEmpty()) {
                ade = lstConsignacao.get(0);
            }
            if (TextHelper.isNull(rseCodigo) && ade != null) {
                // Se a pesquisa não foi por rseCodigo, obtém o código da primeira consignação
                // para realizar a consulta da margem.
                rseCodigo = ade.getAttribute(Columns.RSE_CODIGO).toString();
            }

            if (!TextHelper.isNull(rseCodigo)) {
                try {
                    // Não exibir a margem do servidor caso a consigntária esteja bloqueada manualmente
                    // A mesma restrição deve ser feita para usuários de correspondente caso a CSA esteja bloqueada manualmente ou o correspondente esteja bloqueado.
                    try {
                        if (responsavel.isCsa()) {
                            ConsignatariaTransferObject csa = consignatariaController.findConsignataria(responsavel.getCsaCodigo(), responsavel);
                            // Não permite exibir margem caso
                            if (csa != null &&
                                    (csa.getCsaAtivo().equals(CodedValues.STS_INATIVO) && (TextHelper.isNull(csa.getTmbCodigo()) || TipoMotivoBloqueioEnum.BLOQUEIO_MANUAL.getCodigo().equals(csa.getTmbCodigo())))) {
                                return;
                            }
                        } else if (responsavel.isCor()) {
                            ConsignatariaTransferObject csa = consignatariaController.findConsignataria(responsavel.getCsaCodigo(), responsavel);
                            CorrespondenteTransferObject cor = consignatariaController.findCorrespondente(responsavel.getCorCodigo(), responsavel);
                            if ((csa != null &&
                                    (csa.getCsaAtivo().equals(CodedValues.STS_INATIVO) && (TextHelper.isNull(csa.getTmbCodigo()) || TipoMotivoBloqueioEnum.BLOQUEIO_MANUAL.getCodigo().equals(csa.getTmbCodigo())))) ||
                                (cor != null && cor.getCorAtivo().equals(CodedValues.STS_INATIVO))) {
                                return;
                            }
                        }
                    } catch (ConsignatariaControllerException ex) {
                        LOG.error(ex.getMessage(), ex);
                        throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
                    }

                    // Verifica se a senha foi digitada e validada corretamente
                    boolean senhaServidorOK = (request.getAttribute("senhaServidorOK") != null);
                    boolean exibeCaptcha = false;
                    boolean exigeCaptcha = false;
                    boolean exibeCaptchaAvancado = false;
                    boolean exibeCaptchaDeficiente = false;
                    List<MargemTO> margens = new ArrayList<>();
                    if (responsavel.isSer()) {
                        String validaRecaptcha = JspHelper.verificaVarQryStr(request, "validaCaptchaConsultar").equals("S") && !JspHelper.verificaVarQryStr(request, "validaCaptchaTopo").equals("S") ? JspHelper.verificaVarQryStr(request, "validaCaptchaConsultar") : "N";
                        boolean podeConsultar = true;
                        podeConsultar = ControleConsulta.getInstance().podeConsultarMargemSemCaptchaSer(responsavel.getUsuCodigo());

                        boolean defVisual = responsavel.isDeficienteVisual();
                        if (!defVisual) {
                            exibeCaptchaAvancado = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                            exibeCaptcha = !exibeCaptchaAvancado;
                        } else {
                            exibeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                        }
                        if (!podeConsultar && validaRecaptcha.equals("S")) {
                            if (!defVisual) {
                                if (exibeCaptcha) {
                                    if (ImageCaptchaServlet.armazenaCaptcha(session.getId(), (String) session.getAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY))
                                            && !ImageCaptchaServlet.validaCaptcha(session.getId(), JspHelper.verificaVarQryStr(request, "codigoCapConsultar"))) {
                                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                                        exigeCaptcha = true;
                                    } else {
                                        session.removeAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);
                                        margens = consultarMargemController.consultarMargem(rseCodigo, null, null, responsavel.getCsaCodigo(), senhaServidorOK, (String) request.getAttribute("senhaServidorOK"), true, null, responsavel);
                                        ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                                    }
                                } else if (exibeCaptchaAvancado) {
                                    String remoteAddr = request.getRemoteAddr();

                                    if (!isValidCaptcha(request.getParameter("g-recaptcha-response_consultar"), remoteAddr, responsavel)) {
                                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                                        exigeCaptcha = true;
                                    } else {
                                        margens = consultarMargemController.consultarMargem(rseCodigo, null, null, responsavel.getCsaCodigo(), senhaServidorOK, (String) request.getAttribute("senhaServidorOK"), true, null, responsavel);
                                        ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                                    }
                                }
                            } else {
                                boolean exigeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                                if (exigeCaptchaDeficiente) {
                                    String captchaAnswer = JspHelper.verificaVarQryStr(request, "codigoCapConsultar");

                                    if (captchaAnswer == null) {
                                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                                        exigeCaptcha = true;
                                    }

                                    String captchaCode = (String) session.getAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                                    if (captchaCode == null || !captchaCode.equalsIgnoreCase(captchaAnswer)) {
                                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel));
                                        exigeCaptcha = true;
                                    } else {
                                        session.removeAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                                        margens = consultarMargemController.consultarMargem(rseCodigo, null, null, responsavel.getCsaCodigo(), senhaServidorOK, (String) request.getAttribute("senhaServidorOK"), true, null, responsavel);
                                        ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                                    }
                                }
                            }
                        } else if (podeConsultar) {
                            margens = consultarMargemController.consultarMargem(rseCodigo, null, null, responsavel.getCsaCodigo(), senhaServidorOK, (String) request.getAttribute("senhaServidorOK"), true, null, responsavel);
                            ControleConsulta.getInstance().somarValorCaptchaSer(responsavel.getUsuCodigo());
                        } else {
                            exigeCaptcha = true;
                        }
                    } else {
                        margens = consultarMargemController.consultarMargem(rseCodigo, null, null, responsavel.getCsaCodigo(), senhaServidorOK, (String) request.getAttribute("senhaServidorOK"), true, null, responsavel);
                    }
                    model.addAttribute("exigeCaptcha", exigeCaptcha);
                    model.addAttribute("exibeCaptcha", exibeCaptcha);
                    model.addAttribute("exibeCaptchaAvancado", exibeCaptchaAvancado);
                    model.addAttribute("exibeCaptchaDeficientee", exibeCaptchaDeficiente);

                    // Paramêtro para margem limite por consignatária
                    Short codMargemLimitePorCsa = (ParamSist.getInstance().getParam(CodedValues.TPC_MARGEM_LIMITE_CONTRATOS_POR_CSA, responsavel) != null && !ParamSist.getInstance().getParam(CodedValues.TPC_MARGEM_LIMITE_CONTRATOS_POR_CSA, responsavel).equals("")) ? Short.parseShort(ParamSist.getInstance().getParam(CodedValues.TPC_MARGEM_LIMITE_CONTRATOS_POR_CSA, responsavel).toString()) : 0;
                    // Verifica se pode mostrar margem limite por csa
                    if (codMargemLimitePorCsa != null && !codMargemLimitePorCsa.equals(CodedValues.INCIDE_MARGEM_NAO) && responsavel.isCsaCor()) {
                        MargemTO margemLimiteDisponivel = consultarMargemController.consultarMargemLimitePorCsa(rseCodigo, responsavel.getCsaCodigo(), codMargemLimitePorCsa, null, responsavel);
                        if (margemLimiteDisponivel != null) {
                            margemLimiteDisponivel.setMarDescricao(ApplicationResourcesHelper.getMessage("rotulo.reservar.margem.margem.limite.por.csa.disponivel", responsavel));
                            margens.add(margemLimiteDisponivel);
                        }
                    }
                    
                    TextoMargem textoMargem = new TextoMargem(ade, margens, responsavel, model);
                    if (!textoMargem.isVazio()) {
                        model.addAttribute("margensServidor", margens);
                        model.addAttribute("mensagemDataMargem", TextoMargem.getMensagemDataMargem(ade, responsavel));
                    }
                } catch (ServidorControllerException ex) {
                    // Não exibe mensagem de consultas excedidas, ou que o servidor foi excluido
                }
            }
        }
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        request.setAttribute("sizeDataTable", "true");
        if (responsavel.isSer() && JspHelper.verificaVarQryStr(request, "subtipo").equals("financiamento")) {
            return CodedValues.SAD_CODIGOS_ATIVOS;
        }
        return null;
    }

    @Override
    protected List<String> definirSvcCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        if (responsavel.isSer() && JspHelper.verificaVarQryStr(request, "subtipo").equals("financiamento")) {
            return Arrays.asList(request.getParameterValues("SVC_CODIGO"));
        }
        return null;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        TransferObject criterio = new CustomTransferObject();

        String arquivado = (responsavel.isCseSup() ? request.getParameter("arquivado") : "");

        criterio.setAttribute("TIPO_OPERACAO", (responsavel.isSer() ? "servidor" : "consultar"));
        criterio.setAttribute("arquivado", arquivado);

        return criterio;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaAvancada(HttpServletRequest request, AcessoSistema responsavel) {
        TransferObject criterio = new CustomTransferObject();

        try {
            String adeAnoMesIni = JspHelper.verificaVarQryStr(request, "ADE_ANO_MES_INI");
            if (!adeAnoMesIni.equals("")) {
                adeAnoMesIni = DateHelper.format(DateHelper.parsePeriodString(adeAnoMesIni), "yyyy-MM-dd");
                criterio.setAttribute(Columns.ADE_ANO_MES_INI, adeAnoMesIni);
            }

            String tipoOcorrenciaPeriodo = JspHelper.verificaVarQryStr(request, "tipoOcorrenciaPeriodo");
            if (!tipoOcorrenciaPeriodo.equals("")) {
                criterio.setAttribute("tipoOcorrenciaPeriodo", tipoOcorrenciaPeriodo);
            }

            String periodoIni = JspHelper.verificaVarQryStr(request, "periodoIni");
            if (!periodoIni.equals("")) {
                periodoIni = DateHelper.reformat(periodoIni, LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
                criterio.setAttribute("periodoIni", periodoIni);
            }
            String periodoFim = JspHelper.verificaVarQryStr(request, "periodoFim");
            if (!periodoFim.equals("")) {
                periodoFim = DateHelper.reformat(periodoFim, LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59");
                criterio.setAttribute("periodoFim", periodoFim);
            }
            String adeIntFolha = JspHelper.verificaVarQryStr(request, "ADE_INT_FOLHA");
            if (!adeIntFolha.equals("")) {
                criterio.setAttribute(Columns.ADE_INT_FOLHA, adeIntFolha);
            }
            String adeIndice = JspHelper.verificaVarQryStr(request, "ADE_INDICE");
            if (!adeIndice.equals("")) {
                criterio.setAttribute(Columns.ADE_INDICE, adeIndice);
            }
            String[] srsCodigo = request.getParameterValues("SRS_CODIGO");
            List<String> rseSrsCodigo = new ArrayList<>();
            if (srsCodigo != null) {
                for (String element : srsCodigo) {
                    String[] aux = element.split(";");
                    rseSrsCodigo.add(aux[0]);
                }
                if (rseSrsCodigo.size() > 0) {
                    criterio.setAttribute(Columns.RSE_SRS_CODIGO, rseSrsCodigo);
                }
            }

            criterio.setAttribute(Columns.TMO_CODIGO, JspHelper.verificaVarQryStr(request, "TMO_CODIGO"));
            criterio.setAttribute(Columns.TGC_CODIGO, JspHelper.verificaVarQryStr(request, "TGC_CODIGO"));
            criterio.setAttribute(Columns.CSA_CODIGO, JspHelper.verificaVarQryStr(request, "CSA_CODIGO"));
            criterio.setAttribute(Columns.COR_CODIGO, JspHelper.verificaVarQryStr(request, "COR_CODIGO"));
            criterio.setAttribute(Columns.ORG_CODIGO, JspHelper.verificaVarQryStr(request, "ORG_CODIGO"));
            criterio.setAttribute(Columns.TGS_CODIGO, JspHelper.verificaVarQryStr(request, "TGS_CODIGO"));
            criterio.setAttribute(Columns.SVC_CODIGO, JspHelper.verificaVarQryStr(request, "SVC_CODIGO"));
            if (request.getParameterValues("NSE_CODIGO") != null && !request.getParameterValues("NSE_CODIGO")[0].equals("")) {
                criterio.setAttribute("nseCodigos", Arrays.asList(request.getParameterValues("NSE_CODIGO")));
            }
            if (request.getParameterValues("ADE_INC_MARGEM") != null && !request.getParameterValues("ADE_INC_MARGEM")[0].equals("")) {
                criterio.setAttribute("marCodigos", Arrays.asList(request.getParameterValues("ADE_INC_MARGEM")).stream().map(Short::parseShort).collect(Collectors.toList()));
            }
            criterio.setAttribute(Columns.CNV_COD_VERBA, JspHelper.verificaVarQryStr(request, "CNV_COD_VERBA"));
            criterio.setAttribute(Columns.SER_CPF, JspHelper.verificaVarQryStr(request, "SER_CPF"));
            criterio.setAttribute(Columns.RSE_MATRICULA, JspHelper.verificaVarQryStr(request, "RSE_MATRICULA"));

            if (request.getParameter("infSaldoDevedor") != null) {
                // Busca pela informação de saldo devedor
                criterio.setAttribute("infSaldoDevedor", request.getParameter("infSaldoDevedor"));
            }

            criterio.setAttribute("TIPO_ORDENACAO", request.getParameter("tipoOrdenacao"));
            if (request.getParameter("ORDENACAO_AUX") != null) {
                criterio.setAttribute("ORDENACAO", request.getParameter("ORDENACAO_AUX"));
            }

            if (responsavel.isCsaCor() && !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "adePropria"))) {
                criterio.setAttribute("adePropria", true);
            }
        } catch (ParseException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return criterio;
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.consultar.consignacao.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/consultarConsignacao");
        model.addAttribute("imageHeader", "i-operacional");

        boolean exibeCaptcha = false;
        boolean exibeCaptchaAvancado = false;
        boolean exibeCaptchaDeficiente = false;
        Object paramValidarVezesCaptcha = ParamSist.getInstance().getParam(CodedValues.TPC_VALIDAR_QTDE_VEZES_CAPTCHA_CONSULTAR_CONSIGNACAO, null);

        // DESENV-13988 - Verifica se aparecerá o captcha
        if (responsavel.isCsaCor() && !TextHelper.isNull(paramValidarVezesCaptcha) && Integer.parseInt(paramValidarVezesCaptcha.toString()) >= 0) {

            boolean podeConsultar = ControleConsulta.getInstance().podeConsultarConsignacaoSemCaptcha(responsavel.getUsuCodigo());
            if (!podeConsultar) {
                boolean defVisual = responsavel.isDeficienteVisual();
                if (!defVisual) {
                    exibeCaptcha = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                    exibeCaptchaAvancado = exibeCaptcha ? false : ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                } else {
                    exibeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                }
            }
        }

        if (responsavel.temPermissao(CodedValues.FUN_PESQUISA_AVANCADA_CONSIGNACAO)) {
            model.addAttribute("exibirPesquisaAvancada", Boolean.TRUE);
        }

        try {
            if (parametroController.senhaServidorObrigatoriaConsultaMargem(null, responsavel)) {
                model.addAttribute("exibirCampoSenha", Boolean.TRUE);
            }
        } catch (ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, ex);
        }

        model.addAttribute("exibeCaptcha", exibeCaptcha);
        model.addAttribute("exibeCaptchaAvancado", exibeCaptchaAvancado);
        model.addAttribute("exibeCaptchaDeficiente", exibeCaptchaDeficiente);
        model.addAttribute("proximaOperacao", "pesquisarServidor");

    }

    @Override
    protected List<ColunaListaConsignacao> definirColunasListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        boolean resultadoMultiplosServidores = (request.getAttribute("resultadoMultiplosServidores") != null);

        List<ColunaListaConsignacao> colunas = new ArrayList<>();

        try {
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_CONSIGNATARIA, responsavel) && !responsavel.isCsaCor()) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_CONSIGNATARIA, ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel)));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_RESPONSAVEL, responsavel) && !responsavel.isSer()) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_RESPONSAVEL, ApplicationResourcesHelper.getMessage("rotulo.consignacao.responsavel", responsavel)));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_NUMERO, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_NUMERO, ApplicationResourcesHelper.getMessage("rotulo.consignacao.numero", responsavel), ColunaListaConsignacao.TipoValor.NUMERICO));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_IDENTIFICADOR, responsavel) && (responsavel.isCseSup() || responsavel.isCsaCor())) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_IDENTIFICADOR, ApplicationResourcesHelper.getMessage("rotulo.consignacao.identificador", responsavel)));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_SERVICO, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_SERVICO, ApplicationResourcesHelper.getMessage("rotulo.servico.singular", responsavel)));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_SERVIDOR, responsavel) && resultadoMultiplosServidores) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_SERVIDOR, ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel)));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_DATA_RESERVA, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_DATA_RESERVA, ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.inclusao", responsavel), ColunaListaConsignacao.TipoValor.DATA));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_PARCELA, ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela.abreviado", responsavel), ColunaListaConsignacao.TipoValor.MONETARIO));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_FOLHA, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_VALOR_FOLHA, ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.folha.abreviado", responsavel), ColunaListaConsignacao.TipoValor.MONETARIO));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_PRAZO, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_PRAZO, ApplicationResourcesHelper.getMessage("rotulo.consignacao.prazo.abreviado", responsavel), ColunaListaConsignacao.TipoValor.NUMERICO));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_PARCELAS_PAGAS, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_PARCELAS_PAGAS, ApplicationResourcesHelper.getMessage("rotulo.consignacao.pagas", responsavel), ColunaListaConsignacao.TipoValor.NUMERICO));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_CAPITAL_DEVIDO, responsavel) && ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPITAL_DEVIDO, CodedValues.TPC_SIM, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_CAPITAL_DEVIDO, ApplicationResourcesHelper.getMessage("rotulo.consignacao.capital.devido", responsavel), ColunaListaConsignacao.TipoValor.MONETARIO));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_STATUS, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_STATUS, ApplicationResourcesHelper.getMessage("rotulo.consignacao.status", responsavel)));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_DATA_NOTIFICACAO, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_DATA_NOTIFICACAO, ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.notificacao", responsavel)));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_DATA_VALOR_LIBERADO, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_DATA_VALOR_LIBERADO, ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.valor.liberado", responsavel)));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.LISTA_CONSIGNACAO_PRIORIDADE_DESCONTO, responsavel)) {
                colunas.add(new ColunaListaConsignacao(FieldKeysConstants.LISTA_CONSIGNACAO_PRIORIDADE_DESCONTO, ApplicationResourcesHelper.getMessage("rotulo.consignacao.prioridade.desconto", responsavel)));
            }
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        return colunas;
    }

    @Override
    protected void carregarInformacoesAcessorias(String rseCodigo, String adeNumero, List<TransferObject> lstConsignacao, HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws AutorizacaoControllerException {
        consultarMargem(rseCodigo, lstConsignacao, request, session, model, responsavel);
        carregarPesquisaAvancada(request, session, model, responsavel);
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        List<AcaoConsignacao> acoes = new ArrayList<>();

        boolean exibeComboOperacoes = (request.getAttribute("exibeComboOperacoes") != null);
        if (exibeComboOperacoes) {
            String link = "";
            String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar.abreviado", responsavel);
            String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar", responsavel);
            String msgAlternativa = "";
            String msgConfirmacao = "";

            acoes.add(new AcaoConsignacao("SELECIONAR", CodedValues.FUN_CONS_CONSIGNACAO, descricao, descricaoCompleta, "editar.gif", "btnConsultarConsignacao", msgAlternativa, msgConfirmacao, null, link, null, "chkADE"));
        }

        String link = "../v3/consultarConsignacao?acao=detalharConsignacao";
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar.abreviado", responsavel);
        String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.visualizar", responsavel);
        String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.clique.aqui", responsavel);
        String msgConfirmacao = "";

        acoes.add(new AcaoConsignacao("DETALHAR", CodedValues.FUN_CONS_CONSIGNACAO, descricao, descricaoCompleta, "editar.gif", "btnConsultarConsignacao", msgAlternativa, msgConfirmacao, null, link, null, null));

        return acoes;
    }

    @RequestMapping(params = {"acao=exibirTabelaPrice"})
    public String exibirTabelaPrice(@RequestParam(value = "ADE_CODIGO", required = true, defaultValue = "") String adeCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        // Apenas valida o token, e não salva, visto que a janela é aberta em pop-up
        // SynchronizerToken.saveToken(request);

        try {
            CustomTransferObject autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
            autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
            model.addAttribute("autdes", autdes);

            // Gera os dados da tabela price
            TabelaPrice tabelaPrice = autorizacaoController.calcularTabelaPrice(autdes, responsavel);
            model.addAttribute("tabelaPrice", tabelaPrice);
            model.addAttribute("ADE_CODIGO", adeCodigo);

            // Redireciona para a página de listagem
            return viewRedirect("jsp/consultarConsignacao/exibirTabelaPrice", request, session, model, responsavel);

        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = {"acao=editarOcorrenciaAde"})
    public String editarOcorrenciaAde(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ZetraException  {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String adeCodigo = JspHelper.verificaVarQryStr(request, "adeCodigo");
        String ocaObs = JspHelper.verificaVarQryStr(request, "observacao");
        String ocaCodigo = JspHelper.verificaVarQryStr(request, "ocaCodigo");

        try {
            ocorrenciaAutorizacaoController.updateOcorrenciaAutorizacaoAde(ocaCodigo, ocaObs, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("rotulo.historico.ocorrencia.sucesso.edicao", responsavel));
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        return detalharConsignacao(adeCodigo, request, response, session, model);
    }

    @RequestMapping(params = { "acao=gerarPdfTabelaPrice" }, method = RequestMethod.POST, consumes = {MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> gerarPdfTabelaPrice(@RequestBody(required = true) Map<String,Object> corpo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws IOException, InstantiationException, IllegalAccessException, ServidorControllerException, ParametroControllerException, MargemControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        final String adeCodigo = String.valueOf(corpo.get("adeCodigo"));

        if (TextHelper.isNull(adeCodigo)) {
            return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        CustomTransferObject autdes = null;
        final Document document = new Document();
        OutputStream file = null;
        PdfWriter writer = null;
        String arquivoConsultaMargem = "";
        String nomeArquivoDestino = "";

        try {
            autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
            autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);

            // Gera os dados da tabela price
            TabelaPrice tabelaPrice = autorizacaoController.calcularTabelaPrice(autdes, responsavel);
            final StringBuilder htmlBuilder = new StringBuilder();
            htmlBuilder.append(corpo.get("html"));

            try {
                TabelaPriceTag tag = new TabelaPriceTag();
                htmlBuilder.append(tag.getParcelas(autdes, tabelaPrice, responsavel));
            } catch (ParseException ex) {
                LOG.error(ex.getMessage(), ex);
                return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.interno.sistema.motivo.arg0", responsavel, ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            String html = htmlBuilder.toString();
            html = html.replace("<dl", "<div");
            html = html.replace("<dt class=\"col-6\">", "<h3><strong>");
            html = html.replace("<dd", "<div");
            html = html.replace("</dl>", "</div>");
            html = html.replace("</dt>", "</strong></h3>");
            html = html.replace("</dd>", "</div><br>");
            html = html.replace("</h2>", "</h2><br>");
    
            // Remove imagem da geração do relatório
            final String pattern1 = "<img ";
            final String pattern2 = ">";
            final StringBuilder textoSubstituir = new StringBuilder(pattern1);
    
            final Pattern p = Pattern.compile(Pattern.quote(pattern1) + "(.*?)" + Pattern.quote(pattern2));
            final Matcher m = p.matcher(html);
            while (m.find()) {
                textoSubstituir.append(m.group(1));
            }
            textoSubstituir.append(pattern2);
    
            // Adiciona logo do sistema
            final String urlSistema = (String) ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel);
            final StringBuilder logoSistema = new StringBuilder();
            if (!TextHelper.isNull(urlSistema)) {
                String versaoLeiaute = TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel)) ? "v4" : (String) ParamSist.getInstance().getParam(CodedValues.TPC_SUFIXO_VERSAO_LEIAUTE_WEB, responsavel);
                String imgLogo = "v4".equals(versaoLeiaute) ? "img/logo_sistema.png" : "img/logo_sistema_v5.png";
                final String urlLogoSistema = urlSistema + (urlSistema.endsWith("/") ? "" : "/") + imgLogo;
                logoSistema.append("<div>");
                logoSistema.append("<img src=\"").append(urlLogoSistema).append("\" align=\"left\" title=\"logo\" alt=\"titulo.logo\">");
                logoSistema.append("</div><br>");
            }
    
            final StringBuilder rodape = new StringBuilder();
    
            // Exibir no relatório data da consulta
            final String dataGeracao = DateHelper.toDateTimeString(DateHelper.getSystemDatetime());
            rodape.append("<h3><strong>").append(ApplicationResourcesHelper.getMessage("rotulo.consultar.margem.data.consulta", responsavel)).append(":</strong></h3>\n");
            rodape.append("<div class=\"col-6\">").append(dataGeracao).append("</div><br>");
    
            // Exibir no relatório url do sistema
            if (!TextHelper.isNull(urlSistema)) {
                rodape.append("<h3><strong>").append(ApplicationResourcesHelper.getMessage("rotulo.link.acesso", responsavel)).append(":</strong></h3>\n");
                rodape.append("<div class=\"col-6\">").append(urlSistema).append("</div><br>");
            }
    
            // Exibir no relatório informações do bloqueio
            final String temBloqueio = "span name=\"bloqueio\"";
            final boolean temDecisaoJudicial = html.contains(temBloqueio);
            if (temDecisaoJudicial) {
                final String regex = "<span\\s+([^>]*)name=\"bloqueio\"([^>]*)>(.*?)</span>";
                final Pattern pattern = Pattern.compile(regex);
                final Matcher matcher = pattern.matcher(html);
                String tagSpan = "";
                rodape.append("<h3><strong>")
                        .append(ApplicationResourcesHelper.getMessage("rotulo.informacoes.pdf", responsavel))
                        .append(":</strong></h3>\n");
                while (matcher.find()) {
                    tagSpan = matcher.group(0);
                    rodape.append("<div class=\"col-6\">").append(tagSpan).append("</div><br>");
                    html = html.replace(tagSpan, "");
                }
            }
    
            html = logoSistema.toString() + html.replaceAll(textoSubstituir.toString(), "") + rodape.toString();
    
            final String hoje = DateHelper.format(DateHelper.getSystemDate(), "yyyyMMdd");
            final String dataHora = DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss");
            final String dirRaiz = ParamSist.getDiretorioRaizArquivos();
            final Paragraph paragrafo = new Paragraph(" ");
    
            final File hojeDir = new File(dirRaiz + File.separatorChar + "temp" + File.separatorChar + "consignacao" + File.separatorChar + adeCodigo  + File.separatorChar + hoje);
            if (!hojeDir.exists() && !hojeDir.mkdirs()) {
                return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.criacao.diretorio", responsavel), HttpStatus.INTERNAL_SERVER_ERROR);
            }
    
            document.setPageSize(PageSize.A4.rotate());
    
            final String adeNumero = autdes.getAttribute(Columns.ADE_NUMERO).toString();
            nomeArquivoDestino = adeNumero + "_" + dataHora + ".pdf";
            arquivoConsultaMargem = hojeDir.getAbsolutePath() + File.separatorChar + nomeArquivoDestino;
    
            file = new FileOutputStream(new File(arquivoConsultaMargem));

            writer = PdfWriter.getInstance(document, file);
            document.open();
            document.add(paragrafo);

            PDFHelper.addHTMLToPDF(document, html);

        } catch (final AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.nao.encontrado", responsavel), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (final FileNotFoundException ex) {
            LOG.error(ex.getMessage(), ex);
            return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.arquivo.nao.encontrado", responsavel), HttpStatus.CONFLICT);
        } catch (DocumentException | IOException ex) {
            LOG.error(ex.getMessage(), ex);
            return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.interno.sistema.motivo.arg0", responsavel, ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            document.close();

            if (writer != null) {
                writer.flush();
                writer.close();
            }
            if (file != null) {
                try {
                    file.close();
                } catch (final IOException ex) {
                    LOG.error(ex.getMessage(), ex);
                    return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.interno.sistema.motivo.arg0", responsavel, ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
                }
            }
        }

        final File arquivo = new File(arquivoConsultaMargem);

        // Gera log de download de arquivo
        try {
            final LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.DOWNLOAD_FILE, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage("rotulo.download.arquivo.log", responsavel) + ": " + arquivo.getAbsolutePath());
            log.write();
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.compact.erro.interno", responsavel) + ": " + ex.getMessage());
        }

        final byte[] contents = org.apache.commons.io.IOUtils.toByteArray(new FileInputStream(arquivo));

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData(nomeArquivoDestino, nomeArquivoDestino);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(contents, headers, HttpStatus.OK);
    }
}
