package com.zetra.econsig.web.controller.beneficiario;

import java.io.StringReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.BeneficioControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ContratoBeneficioControllerException;
import com.zetra.econsig.exception.RelacionamentoBeneficioServicoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.margem.MargemDisponivel;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.Beneficio;
import com.zetra.econsig.persistence.entity.BeneficioServico;
import com.zetra.econsig.service.beneficios.BeneficiarioController;
import com.zetra.econsig.service.beneficios.BeneficioController;
import com.zetra.econsig.service.beneficios.CalcularSubsidioBeneficioController;
import com.zetra.econsig.service.beneficios.ContratoBeneficioController;
import com.zetra.econsig.service.beneficios.RelacionamentoBeneficioServicoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusContratoBeneficioEnum;
import com.zetra.econsig.values.TipoBeneficiarioEnum;
import com.zetra.econsig.web.controller.servidor.AbstractConsultarServidorWebController;

/**
 * <p>Title: ReativarBeneficiarioSimulacaoBeneficiosWebController</p>
 * <p>Description: Reativar Beneficiario na Simulação de Beneficios</p>
 * <p>Copyright: Copyright (c) 2020</p>
 * <p>Company: Nostrum Consultoria e Projetos.</p>
 * $Author: marcos.nolasco $
 * $Revision: 28887 $
 * $Date: 2020-03-05 11:50:20 -0300 (qui, 05 mar 2020) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/reativarBeneficiarioSimulacaoBeneficios" })
public class ReativarBeneficiarioSimulacaoBeneficiosWebController extends AbstractConsultarServidorWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ReativarBeneficiarioSimulacaoBeneficiosWebController.class);

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private BeneficiarioController beneficiarioController;

    @Autowired
    private BeneficioController beneficioController;

    @Autowired
    private ContratoBeneficioController contratoBeneficioController;

    @Autowired
    private CalcularSubsidioBeneficioController calcularSubsidioBeneficioController;

    @Autowired
    private RelacionamentoBeneficioServicoController relacionamentoBeneficioServicoController;

    @Override
    protected String continuarOperacao(String rseCodigo, String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException, UsuarioControllerException {
        return carregaTelaSimulacaoPlanoSaude(rseCodigo, adeNumero, request, response, session, model);
    }

    @Override
    protected String definirProximaOperacao(HttpServletRequest request, AcessoSistema responsavel) {
        return "planoSaude";
    }

    @Override
    public void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        String titulo = JspHelper.verificaVarQryStr(request, "titulo");
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.reativar.contrato.beneficio.titulo", responsavel, titulo));
        model.addAttribute("acaoFormulario", "../v3/reativarBeneficiarioSimulacaoBeneficios");
        model.addAttribute("omitirAdeNumero", true);
        model.addAttribute("imageHeader", "i-beneficios");
    }

    /**
     * Carrega a tela para simular o plano de saude
     * @param rseCodigo
     * @param adeNumero
     * @param reiniciarLocalSession
     * @param request
     * @param response
     * @param session
     * @param model
     * @return
     * @throws AutorizacaoControllerException
     * @throws UsuarioControllerException
     */
    @RequestMapping(params = { "acao=planoSaude" })
    private String carregaTelaSimulacaoPlanoSaude(@RequestParam(value = "RSE_CODIGO", required = false) String rseCodigo, @RequestParam(value = "adeNumero", required = false) String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException, UsuarioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        session.removeAttribute("contemPlanoSaude");

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request) && !responsavel.isSer()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        if (responsavel.isSer()) {
            rseCodigo = responsavel.getRseCodigo();
        }

        List<ConsignatariaTransferObject> consignatariasPlanoSaude = new ArrayList<>();
        List<TransferObject> beneficiariosGrupoFamiliar = new ArrayList<>();
        List<String> benCodigos = new ArrayList<>();


        try {
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
            List<String> scbCodigos = new ArrayList<>();
            scbCodigos.add(CodedValues.SCB_CODIGO_ATIVO);
            criterio.setAttribute(Columns.SCB_CODIGO, scbCodigos);
            criterio.setAttribute(Columns.NSE_CODIGO, CodedValues.NSE_PLANO_DE_SAUDE);

            List<TransferObject> contratosBeneficio = contratoBeneficioController.listarContratosBeneficioPorRegistroServidorQuery(criterio, responsavel);

            if (contratosBeneficio == null || contratosBeneficio.size() == 0) {
                scbCodigos.clear();
                scbCodigos.add(CodedValues.SCB_CODIGO_CANCELADO);
                contratosBeneficio = contratoBeneficioController.listarContratosBeneficioPorRegistroServidorQuery(criterio, responsavel);
            }

            if (contratosBeneficio == null || contratosBeneficio.size() == 0) {
                session.setAttribute("contemPlanoSaude", "true");
                return carregaTelaSimulacaoPlanoOdontologico(rseCodigo, adeNumero, request, response, session, model);
            }

            // Buscando os beneficiarios do servidor informado ou logado.
            criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
            criterio.setAttribute(Columns.SCB_CODIGO, scbCodigos);
            for (TransferObject contratoBeneficio : contratosBeneficio) {
                String benCodigo = (String) contratoBeneficio.getAttribute(Columns.BEN_CODIGO);
                criterio.setAttribute(Columns.BEN_CODIGO, benCodigo);
            }
            criterio.setAttribute(Columns.NSE_CODIGO, CodedValues.NSE_PLANO_DE_SAUDE);
            criterio.setAttribute("contratosAtivos", "false");
            criterio.setAttribute("reativar", "true");
            beneficiariosGrupoFamiliar = beneficioController.findRelacaoBeneficioByRseCodigo(criterio, responsavel);

            if (contratosBeneficio == null || contratosBeneficio.size() == 0 || beneficiariosGrupoFamiliar == null || beneficiariosGrupoFamiliar.size() == 0 ) {
                session.setAttribute("contemPlanoSaude", "true");
                return carregaTelaSimulacaoPlanoOdontologico(rseCodigo, adeNumero, request, response, session, model);
            }

            //Validando o status do registro servidor
            RegistroServidorTO registroServidor = null;
            try {
                registroServidor = servidorController.findRegistroServidor(rseCodigo, true, responsavel);

                if (CodedValues.SRS_INATIVOS.contains(registroServidor.getSrsCodigo())) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.selecionar.servidor.erro.nenhum.registro", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

            } catch (ServidorControllerException e) {
                session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Buscando as consignataria e preenchendo benCodigos que serão usados
            for (TransferObject contratoBeneficio : contratosBeneficio) {
                consignatariasPlanoSaude.add(consignatariaController.findConsignataria((String) contratoBeneficio.getAttribute(Columns.CSA_CODIGO), responsavel));
                benCodigos.add((String) contratoBeneficio.getAttribute(Columns.BEN_CODIGO));
            }


        } catch (ConsignatariaControllerException | BeneficioControllerException | ContratoBeneficioControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("consignatariasPlanoSaude", consignatariasPlanoSaude);
        model.addAttribute("beneficiariosGrupoFamiliar", beneficiariosGrupoFamiliar);
        model.addAttribute("rseCodigo", rseCodigo);
        model.addAttribute("benCodigos", benCodigos);
        model.addAttribute("nseCodigo", CodedValues.NSE_PLANO_DE_SAUDE);

        return viewRedirect("jsp/simulacaoBeneficios/reativarBeneficiarioPlanoSaude", request, session, model, responsavel);
    }

    /**
     * Carrega a tela para simular o plano odontologico
     * @param rseCodigo
     * @param adeNumero
     * @param request
     * @param response
     * @param session
     * @param model
     * @return
     * @throws AutorizacaoControllerException
     * @throws UsuarioControllerException
     */
    @RequestMapping(params = { "acao=planoOdontologico" })
    private String carregaTelaSimulacaoPlanoOdontologico(@RequestParam(value = "RSE_CODIGO", required = false) String rseCodigo, @RequestParam(value = "adeNumero", required = false) String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException, UsuarioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        session.removeAttribute("contemPlanoOdontologico");

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request) && session.getAttribute("contemPlanoSaude") == null) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        if (responsavel.isSer()) {
            rseCodigo = responsavel.getRseCodigo();
        }

        List<ConsignatariaTransferObject> consignatariaPlanoOdonto = new ArrayList<>(new HashSet<>());
        List<TransferObject> beneficiariosGrupoFamiliar = new ArrayList<>();
        List<String> benCodigos = new ArrayList<>();


        try {
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
            List<String> scbCodigos = new ArrayList<>();
            scbCodigos.add(CodedValues.SCB_CODIGO_ATIVO);
            criterio.setAttribute(Columns.SCB_CODIGO, scbCodigos);
            criterio.setAttribute(Columns.NSE_CODIGO, CodedValues.NSE_PLANO_ODONTOLOGICO);

            List<TransferObject> contratosBeneficio = contratoBeneficioController.listarContratosBeneficioPorRegistroServidorQuery(criterio, responsavel);

            if (contratosBeneficio == null || contratosBeneficio.size() == 0) {
                scbCodigos.clear();
                scbCodigos.add(CodedValues.SCB_CODIGO_CANCELADO);
                contratosBeneficio = contratoBeneficioController.listarContratosBeneficioPorRegistroServidorQuery(criterio, responsavel);
            }

            if (contratosBeneficio == null || contratosBeneficio.size() == 0) {
                session.setAttribute("contemPlanoOdontologico", "true");
                return simulacaoDetalhes(rseCodigo, request, response, session, model);
            }

            // Buscando os beneficiarios do servidor informado ou logado.
            criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
            criterio.setAttribute(Columns.SCB_CODIGO, scbCodigos);
            // Quando o status do contrato é ativo, preciso buscar somente o benefício deste contrato, pois a reativação só é do próprio beneficío
            for (TransferObject contratoBeneficio : contratosBeneficio) {
                String benCodigo = (String) contratoBeneficio.getAttribute(Columns.BEN_CODIGO);
                criterio.setAttribute(Columns.BEN_CODIGO, benCodigo);
            }
            criterio.setAttribute(Columns.NSE_CODIGO, CodedValues.NSE_PLANO_ODONTOLOGICO);
            criterio.setAttribute("contratosAtivos", "false");
            criterio.setAttribute("reativar", "true");
            beneficiariosGrupoFamiliar = beneficioController.findRelacaoBeneficioByRseCodigo(criterio, responsavel);

            if (beneficiariosGrupoFamiliar == null || beneficiariosGrupoFamiliar.size() == 0) {
                session.setAttribute("contemPlanoOdontologico", "true");
                return simulacaoDetalhes(rseCodigo, request, response, session, model);
            }

            //Validando o status do registro servidor
            RegistroServidorTO registroServidor = null;
            try {
                registroServidor = servidorController.findRegistroServidor(rseCodigo, true, responsavel);

                if (CodedValues.SRS_INATIVOS.contains(registroServidor.getSrsCodigo())) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.selecionar.servidor.erro.nenhum.registro", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

            } catch (ServidorControllerException e) {
                session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Buscando as consignataria e preenchendo benCodigos que serão usados
            for (TransferObject contratoBeneficio : contratosBeneficio) {
                String csaCodigo = (String) contratoBeneficio.getAttribute(Columns.CSA_CODIGO);
                String benCodigo = (String) contratoBeneficio.getAttribute(Columns.BEN_CODIGO);
                if (consignatariaPlanoOdonto.size() > 0) {
                    for (ConsignatariaTransferObject csa : consignatariaPlanoOdonto) {
                        String csaCodigoLista = (String) csa.getAttribute(Columns.CSA_CODIGO);
                        if (!csaCodigo.equals(csaCodigoLista)) {
                            consignatariaPlanoOdonto.add(consignatariaController.findConsignataria((String) contratoBeneficio.getAttribute(Columns.CSA_CODIGO), responsavel));
                        }
                    }
                } else {
                    consignatariaPlanoOdonto.add(consignatariaController.findConsignataria((String) contratoBeneficio.getAttribute(Columns.CSA_CODIGO), responsavel));
                }
                if (benCodigos.size() > 0) {
                    for (String ben : benCodigos) {
                        if (!benCodigo.equals(ben)) {
                            benCodigos.add((String) contratoBeneficio.getAttribute(Columns.BEN_CODIGO));
                        }
                    }
                } else {
                    benCodigos.add((String) contratoBeneficio.getAttribute(Columns.BEN_CODIGO));
                }
            }

        } catch (ConsignatariaControllerException | BeneficioControllerException | ContratoBeneficioControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("consignatariasOdontologico", consignatariaPlanoOdonto);
        model.addAttribute("beneficiariosGrupoFamiliar", beneficiariosGrupoFamiliar);
        model.addAttribute("rseCodigo", rseCodigo);
        model.addAttribute("benCodigos", benCodigos);
        model.addAttribute("fluxoInicial", session.getAttribute("contemPlanoSaude") != null);
        model.addAttribute("nseCodigo", CodedValues.NSE_PLANO_ODONTOLOGICO);

        return viewRedirect("jsp/simulacaoBeneficios/reativarBeneficiarioPlanoOdontologico", request, session, model, responsavel);
    }

    /**
     * Carrega os beneficios de forma async
     * @param csaCodigo
     * @param nseCodigo
     * @param request
     * @return
     */
    @RequestMapping(method = { RequestMethod.POST }, value = { "/carregaBeneficiosAjax" })
    @ResponseBody
    private ResponseEntity<String> carregaBeneficios(@RequestParam(value = "csaCodigo", required = true) String csaCodigo, @RequestParam(value = "nseCodigo", required = true) String nseCodigo, HttpServletRequest request) {
        JsonObjectBuilder raiz = Json.createObjectBuilder();
        JsonArrayBuilder array = Json.createArrayBuilder();
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        try {
            List<Beneficio> beneficios = beneficioController.lstBeneficioByCsaCodigoAndNaturezaServicoAndRelacionamentoServico(csaCodigo, nseCodigo, false, responsavel);

            if (beneficios == null || beneficios.isEmpty()) {
                return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficio.nenhum.beneficio.encontrado", responsavel), HttpStatus.NOT_ACCEPTABLE);
            }

            for (Beneficio beneficio : beneficios) {
                JsonObjectBuilder beneficioJson = Json.createObjectBuilder();
                beneficioJson.add("id", beneficio.getBenCodigo());
                beneficioJson.add("detalhe", TextHelper.forHtmlContent(beneficio.getBenDescricao()));

                array.add(beneficioJson);
            }

            raiz.add("beneficios", array);
        } catch (BeneficioControllerException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(raiz.build().toString(), HttpStatus.OK);
    }

    /**
     * Realiza a simulação de forma async
     * @param bfcCodigoSelecionados
     * @param benCodigo
     * @param rseCodigo
     * @param csaCodigo
     * @param totalADescontarCalculado
     * @param request
     * @return
     */
    @RequestMapping(method = { RequestMethod.POST }, value = { "/simulaAjax" })
    @ResponseBody
    private ResponseEntity<String> simulaAjax(@RequestParam(value = "nseCodigo", required = true) String nseCodigo, @RequestParam(value = "benCodigo", required = true) String benCodigo, @RequestParam(value = "rseCodigo", required = true) String rseCodigo, @RequestParam(value = "csaCodigo", required = true) String csaCodigo, @RequestParam(value = "bfcCodigoSelecionados[]", required = false) List<String> bfcCodigoSelecionados, @RequestParam(value = "dadosSimulacao", required = false) String dadosSimulacaoAtual, HttpServletRequest request) {
        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Analisando se o fluxo é do servidor e garantido que o rseCodigo sejá da pessoa.
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (responsavel.isSer()) {
            rseCodigo = responsavel.getRseCodigo();
        }

        try {
            Map<String, JsonArrayBuilder> cacheArrayBeneficiarioCalculados = new HashMap<>();
            Map<String, JsonArrayBuilder> cacheArrayBeneficiarioSemCalculos = new HashMap<>();
            Map<String, String> cacheBenCodigo = new HashMap<>();
            Map<String, String> cacheCsaCodigo = new HashMap<>();
            Map<String, BigDecimal> cacheTotalMensalidade = new HashMap<>();
            Map<String, BigDecimal> cacheTotalSubsidio = new HashMap<>();

            Map<String, List<String>> dadosSimulacao = new HashMap<>();

            // Recebendo o Json da tela e fazendo o parse nele para recuperar os valores
            if (!dadosSimulacaoAtual.isEmpty()) {
                JsonReader jsonReader = Json.createReader(new StringReader(dadosSimulacaoAtual));
                JsonObject jsonObjectDadosSimulacaoAtual = jsonReader.readObject();

                if (jsonObjectDadosSimulacaoAtual.containsKey("simulacao")) {
                    JsonArray simulacao = jsonObjectDadosSimulacaoAtual.getJsonArray("simulacao");
                    for (int i = 0; i < simulacao.size(); i++) {
                        JsonObject simulacaoNse = simulacao.getJsonObject(i);
                        String benCodigoPassado = simulacaoNse.getString("benCodigo");
                        String nseCodigoPassado = simulacaoNse.getString("nseCodigo");
                        String csaCodigoPassado = simulacaoNse.getString("csaCodigo");

                        // Ignora o NSE passado se for o mesmo NSE que vamos simular nesse momento.
                        if (nseCodigo.equals(nseCodigoPassado)) {
                            continue;
                        }

                        List<String> bfcCodigosNse = new ArrayList<>();

                        JsonArrayBuilder tmp = Json.createArrayBuilder();
                        JsonArray beneficiariosCalculadosNse = simulacaoNse.getJsonArray("beneficiariosCalculados");
                        for (int y = 0; y < beneficiariosCalculadosNse.size(); y++) {
                            JsonObject beneficiario = beneficiariosCalculadosNse.getJsonObject(y);
                            tmp.add(beneficiario);
                            String bfcCodigo = beneficiario.getString("bfcCodigo");
                            bfcCodigosNse.add(bfcCodigo);
                        }
                        cacheArrayBeneficiarioCalculados.put(nseCodigoPassado, tmp);

                        dadosSimulacao.put(benCodigoPassado, bfcCodigosNse);

                        JsonArrayBuilder tmp2 = Json.createArrayBuilder();
                        JsonArray beneficiariosSemCalculosNse = simulacaoNse.getJsonArray("beneficiariosSemCalculos");
                        for (int y = 0; y < beneficiariosSemCalculosNse.size(); y++) {
                            JsonObject beneficiario = beneficiariosSemCalculosNse.getJsonObject(y);
                            tmp2.add(beneficiario);
                        }
                        cacheArrayBeneficiarioSemCalculos.put(nseCodigoPassado, tmp2);

                        cacheBenCodigo.put(nseCodigoPassado, benCodigoPassado);

                        cacheCsaCodigo.put(nseCodigoPassado, csaCodigoPassado);

                        BigDecimal totalMensalidade = new BigDecimal(NumberHelper.reformat(simulacaoNse.getString("totalMensalidade"), NumberHelper.getLang(), Locale.ENGLISH.toString()));
                        BigDecimal totalSubsidio = new BigDecimal(NumberHelper.reformat(simulacaoNse.getString("totalSubsidio"), NumberHelper.getLang(), Locale.ENGLISH.toString()));

                        cacheTotalMensalidade.put(nseCodigoPassado, totalMensalidade);
                        cacheTotalSubsidio.put(nseCodigoPassado, totalSubsidio);
                    }
                }
            }

            JsonArrayBuilder arraySimulacao = Json.createArrayBuilder();
            JsonObjectBuilder raiz = Json.createObjectBuilder();

            cacheTotalMensalidade.put(nseCodigo, new BigDecimal("0.00"));
            cacheTotalSubsidio.put(nseCodigo, new BigDecimal("0.00"));

            cacheArrayBeneficiarioCalculados.put(nseCodigo, null);
            cacheArrayBeneficiarioSemCalculos.put(nseCodigo, null);

            cacheCsaCodigo.put(nseCodigo, csaCodigo);

            raiz.add("rseCodigo", rseCodigo);

            BigDecimal margemRestanteCalculada = new BigDecimal("0.00");
            BigDecimal margemDisponivelBD;

            Set<String> bfcCodigoJaProcessador = new HashSet<>();

            // Buscando o serviço com base no Beneficio
            List<BeneficioServico> servicosTitular = relacionamentoBeneficioServicoController.findByBenCodigoTibCodigo(benCodigo, CodedValues.TIB_TITULAR);
            if (servicosTitular == null || servicosTitular.size() != 1) {
                return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficio.nenhum.ou.mais.servico.entrados", responsavel), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            MargemDisponivel margemDisponivel = new MargemDisponivel(rseCodigo, null, servicosTitular.get(0).getServico().getSvcCodigo(), null, responsavel);

            // Realizando a busca do beneficiario realizando o filtro por serviço para garantir que somente os beneficiario permitidos estejam validos
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
            criterio.setAttribute(Columns.SVC_CODIGO, servicosTitular.get(0).getServico().getSvcCodigo());
            criterio.setAttribute(Columns.SCB_CODIGO, StatusContratoBeneficioEnum.CANCELADO.getCodigo());
            // Quando o status do contrato é ativo, preciso buscar somente o benefício deste contrato, pois a reativação só é do próprio beneficío
            criterio.setAttribute(Columns.BEN_CODIGO, benCodigo);
            criterio.setAttribute(Columns.NSE_CODIGO, nseCodigo);
            List<TransferObject> beneficiariosGrupoFamiliar = beneficioController.findRelacaoBeneficioByRseCodigo(criterio, responsavel);
            Set<String> bfcCodigoValidos = new HashSet<>();

            // Montando a estrutura de dados necessarias para o calculo
            if (bfcCodigoSelecionados != null) {

                // Se estamos simulando limpamos para evitar problemas
                cacheArrayBeneficiarioCalculados.clear();
                cacheTotalMensalidade.clear();
                cacheTotalSubsidio.clear();

                // Analisando se todos os beneficiarios selecionados tem direito a plano e sub
                for (TransferObject beneficiario : beneficiariosGrupoFamiliar) {
                    String bfcCodigo = (String) beneficiario.getAttribute(Columns.BFC_CODIGO);
                    bfcCodigoValidos.add(bfcCodigo);
                }

                // Caso alguem for esperto de tentar "hack" a api aqui estamos removendo os beneficiarios que não tem direito.
                Iterator<String> it = bfcCodigoSelecionados.iterator();
                while (it.hasNext()) {
                    String bfcCodigo = it.next();
                    if (!bfcCodigoValidos.contains(bfcCodigo)) {
                        it.remove();
                    }
                }

                boolean teveTitularSelecionado = false;
                boolean teveAgregado = false;
                boolean permiteAgregadoNoFluxo = ParamSist.paramEquals(CodedValues.TPC_MOD_BENEFICIO_PERMITE_AGREGADO, CodedValues.TPC_SIM, responsavel);
                boolean edicaoSimulacao = (responsavel.isSup() && !permiteAgregadoNoFluxo) || (responsavel.isSer() && permiteAgregadoNoFluxo) ? Boolean.FALSE : Boolean.TRUE;

                // Realizando a simulação
                dadosSimulacao.put(benCodigo, bfcCodigoSelecionados);
                List<TransferObject> resultados = calcularSubsidioBeneficioController.simularCalculoSubsidio(dadosSimulacao, rseCodigo, false, responsavel);

                // Para cadas resultado calculado monto um json com os dados necessarios para desenhar na tela.
                for (TransferObject resultado : resultados) {
                    BigDecimal mensalidade = (BigDecimal) resultado.getAttribute("VALOR_MENSALIDADE");
                    BigDecimal subsidio = (BigDecimal) resultado.getAttribute("VALOR_SUBSIDIO");
                    BigDecimal totalAPagar = mensalidade.subtract(subsidio);
                    String tibCodigo = (String) resultado.getAttribute(Columns.TIB_CODIGO);
                    String nseCodigoSimulacao = (String) resultado.getAttribute(Columns.NSE_CODIGO);
                    String benCodigoSimulacao = (String) resultado.getAttribute(Columns.BEN_CODIGO);

                    JsonArrayBuilder arrayLoop = cacheArrayBeneficiarioCalculados.get(nseCodigoSimulacao) == null ? Json.createArrayBuilder() : cacheArrayBeneficiarioCalculados.get(nseCodigoSimulacao);

                    // Analisando se teve um titular no fluxo
                    if (nseCodigoSimulacao.equals(nseCodigo) && TipoBeneficiarioEnum.TITULAR.equals(tibCodigo)) {
                        teveTitularSelecionado = true;
                    }

                    JsonObjectBuilder resultadoJson = Json.createObjectBuilder();
                    resultadoJson.add("bfcCodigo", (String) resultado.getAttribute(Columns.BFC_CODIGO));
                    resultadoJson.add("mensalidade", NumberHelper.format(mensalidade.doubleValue(), NumberHelper.getLang()));
                    resultadoJson.add("subsidio", NumberHelper.format(subsidio.doubleValue(), NumberHelper.getLang()));
                    resultadoJson.add("totalAPagar", NumberHelper.format(totalAPagar.doubleValue(), NumberHelper.getLang()));

                    if (nseCodigoSimulacao.equals(nseCodigo)) {
                        bfcCodigoJaProcessador.add((String) resultado.getAttribute(Columns.BFC_CODIGO));
                    }

                    boolean contratoVirtual = (boolean) resultado.getAttribute("contratoVirtual");

                    if (contratoVirtual) {
                        // Analisando se foi selecionado um Agreado e não poderia
                        if (TipoBeneficiarioEnum.AGREGADO.equals(tibCodigo) && !edicaoSimulacao ) {
                            teveAgregado = true;
                        }

                        cacheBenCodigo.put(nseCodigoSimulacao, benCodigoSimulacao);

                        BigDecimal totalMensalidade = cacheTotalMensalidade.get(nseCodigoSimulacao) == null ? new BigDecimal("0.00") : cacheTotalMensalidade.get(nseCodigoSimulacao);
                        totalMensalidade = totalMensalidade.add(mensalidade);

                        BigDecimal totalSubsidio = cacheTotalSubsidio.get(nseCodigoSimulacao) == null ? new BigDecimal("0.00") : cacheTotalSubsidio.get(nseCodigoSimulacao);
                        totalSubsidio = totalSubsidio.add(subsidio);

                        cacheTotalMensalidade.put(nseCodigoSimulacao, totalMensalidade);
                        cacheTotalSubsidio.put(nseCodigoSimulacao, totalSubsidio);

                        arrayLoop.add(resultadoJson);

                        cacheArrayBeneficiarioCalculados.put(nseCodigoSimulacao, arrayLoop);
                    }
                }

                // Analiso se teve algum titular selecionado
                if (!teveTitularSelecionado) {
                    return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficio.titular.nao.selecionado", responsavel), HttpStatus.BAD_REQUEST);
                }

                if (teveAgregado) {
                    return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficio.agregado.nao.incluido", responsavel), HttpStatus.TEMPORARY_REDIRECT);
                }
            }

            // Calculando a "nova" margem com base nos calculos dos plano acima
            margemDisponivelBD = margemDisponivel.getMargemRestante();

            // Removemos da margem o valor ja calculado.
            margemRestanteCalculada = margemDisponivelBD;
            BigDecimal totalMensalidade = new BigDecimal("0.00");
            BigDecimal totalSubsidio = new BigDecimal("0.00");

            for (String key : cacheTotalMensalidade.keySet()) {
                BigDecimal tm = cacheTotalMensalidade.get(key);
                BigDecimal ts = cacheTotalSubsidio.get(key);

                totalMensalidade = totalMensalidade.add(tm);
                totalSubsidio = totalSubsidio.add(ts);
            }

            margemRestanteCalculada = margemRestanteCalculada.subtract(totalMensalidade.subtract(totalSubsidio));

            // Para os beneficiarios não selecionados, mas que podem aparecer na tela, fazemos o calculo somente para pegar o valor do plano.
            List<String> beneficiarioNaoSelecionados = new ArrayList<>();
            for (TransferObject beneficiario : beneficiariosGrupoFamiliar) {
                String bfcCodigo = (String) beneficiario.getAttribute(Columns.BFC_CODIGO);
                if (!bfcCodigoJaProcessador.contains(bfcCodigo)) {
                    beneficiarioNaoSelecionados.add(bfcCodigo);
                }
            }

            // Realizando o calculo
            dadosSimulacao.clear();
            dadosSimulacao.put(benCodigo, beneficiarioNaoSelecionados);
            List<TransferObject> resultados = calcularSubsidioBeneficioController.simularCalculoSubsidio(dadosSimulacao, rseCodigo, false, responsavel);

            // Para cadas resultado calculado monto um json com os dados necessarios para desenhar na tela.
            for (TransferObject resultado : resultados) {
                double tmp = 0;
                BigDecimal mensalidade = (BigDecimal) resultado.getAttribute("VALOR_MENSALIDADE");
                String nseCodigoSimulacao = (String) resultado.getAttribute(Columns.NSE_CODIGO);
                String benCodigoSimulacao = (String) resultado.getAttribute(Columns.BEN_CODIGO);

                boolean contratoVirtual = (boolean) resultado.getAttribute("contratoVirtual");

                if (contratoVirtual) {
                    cacheBenCodigo.put(nseCodigoSimulacao, benCodigoSimulacao);

                    JsonArrayBuilder arrayLoop = cacheArrayBeneficiarioSemCalculos.get(nseCodigoSimulacao) == null ? Json.createArrayBuilder() : cacheArrayBeneficiarioSemCalculos.get(nseCodigoSimulacao);

                    JsonObjectBuilder resultadoJson = Json.createObjectBuilder();
                    resultadoJson.add("bfcCodigo", (String) resultado.getAttribute(Columns.BFC_CODIGO));
                    resultadoJson.add("mensalidade", NumberHelper.format(mensalidade.doubleValue(), NumberHelper.getLang()));
                    resultadoJson.add("subsidio", NumberHelper.format(tmp, NumberHelper.getLang()));

                    arrayLoop.add(resultadoJson);

                    cacheArrayBeneficiarioSemCalculos.put(nseCodigoSimulacao, arrayLoop);
                }
            }

            Set<String> todosNseCodigo = new HashSet<>();
            todosNseCodigo.addAll(cacheArrayBeneficiarioCalculados.keySet());
            todosNseCodigo.addAll(cacheArrayBeneficiarioSemCalculos.keySet());
            todosNseCodigo.addAll(cacheBenCodigo.keySet());
            todosNseCodigo.addAll(cacheTotalMensalidade.keySet());
            todosNseCodigo.addAll(cacheTotalSubsidio.keySet());
            todosNseCodigo.addAll(cacheCsaCodigo.keySet());

            for (String key : todosNseCodigo) {
                JsonObjectBuilder dados = Json.createObjectBuilder();
                dados.add("nseCodigo", key);
                dados.add("benCodigo", cacheBenCodigo.get(key));
                dados.add("beneficiariosCalculados", cacheArrayBeneficiarioCalculados.get(key) == null ? Json.createArrayBuilder() : cacheArrayBeneficiarioCalculados.get(key));
                dados.add("beneficiariosSemCalculos", cacheArrayBeneficiarioSemCalculos.get(key) == null ? Json.createArrayBuilder() : cacheArrayBeneficiarioSemCalculos.get(key));

                BigDecimal tm = cacheTotalMensalidade.get(key) == null ? new BigDecimal("0.00") : cacheTotalMensalidade.get(key);
                BigDecimal ts = cacheTotalSubsidio.get(key) == null ? new BigDecimal("0.00") : cacheTotalSubsidio.get(key);

                dados.add("totalMensalidade", NumberHelper.format(tm.doubleValue(), NumberHelper.getLang()));
                dados.add("totalSubsidio", NumberHelper.format(ts.doubleValue(), NumberHelper.getLang()));
                dados.add("totalADesconto", NumberHelper.format(tm.subtract(ts).doubleValue(), NumberHelper.getLang()));

                dados.add("csaCodigo", cacheCsaCodigo.get(key));

                arraySimulacao.add(dados);
            }

            raiz.add("simulacao", arraySimulacao);

            raiz.add("totalSimulacao", NumberHelper.format(totalMensalidade.subtract(totalSubsidio).doubleValue(), NumberHelper.getLang()));
            raiz.add("margemSemPlano", NumberHelper.format(margemDisponivelBD.doubleValue(), NumberHelper.getLang()));
            raiz.add("margemDisponivel", NumberHelper.format(margemRestanteCalculada.doubleValue(), NumberHelper.getLang()));
            return new ResponseEntity<>(raiz.build().toString(), HttpStatus.OK);
        } catch (BeneficioControllerException | RelacionamentoBeneficioServicoControllerException | ViewHelperException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ParseException e) {
            return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.recuperar.dados.operacao", responsavel), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Metodo para desenhar a tela de detalhes para o usuario saber que plano esta simulando.
     * @param rseCodigo
     * @param request
     * @param response
     * @param session
     * @param model
     * @return
     */
    @RequestMapping(params = { "acao=detalhes" })
    public String simulacaoDetalhes(@RequestParam(value = "RSE_CODIGO", required = true) String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request) && session.getAttribute("contemPlanoOdontologico") == null) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        String contemPlanoSaude = session.getAttribute("contemPlanoSaude") == null ? "" : (String) session.getAttribute("contemPlanoSaude");
        String contemPlanoOdontologico = session.getAttribute("contemPlanoOdontologico") == null ? "" : (String) session.getAttribute("contemPlanoOdontologico");

        session.removeAttribute("contemPlanoSaude");
        session.removeAttribute("contemPlanoOdontologico");

        if (!contemPlanoSaude.isEmpty() && !contemPlanoOdontologico.isEmpty()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficiario.ja.contem.beneficio.em.andamento", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (responsavel.isSer()) {
            rseCodigo = responsavel.getRseCodigo();
        }

        List<TransferObject> beneficiariosGrupoFamiliar = new ArrayList<>();

        try {
            // Buscando os beneficiarios do servidor informado ou logado.
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
            beneficiariosGrupoFamiliar = beneficiarioController.listarCountBeneficiosPorBeneficiarios(criterio, responsavel);
        } catch (BeneficioControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String paraOndeVoltar = contemPlanoOdontologico.isEmpty() ? "planoOdontologico" : "planoSaude";

        model.addAttribute("beneficiariosGrupoFamiliar", beneficiariosGrupoFamiliar);
        model.addAttribute("rseCodigo", rseCodigo);
        model.addAttribute("paraOndeVoltar", paraOndeVoltar);

        return viewRedirect("jsp/simulacaoBeneficios/reativarBeneficiosDetalhes", request, session, model, responsavel);
    }

    /**
     * Metodo para gravar no banco a simulação
     * @param request
     * @param response
     * @param session
     * @param model
     * @return
     * @throws InstantiationException
     */
    @RequestMapping(params = { "acao=salvar" })
    public String reativarSalvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        StringTokenizer stn2 = new StringTokenizer(JspHelper.verificaVarQryStr(request, "CBE_CODIGOS"), ";");

      if (TextHelper.isNull(stn2)) {
          session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.reativar.beneficio.nenhum.contrato", responsavel));
          return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
      }

      List<String> cbeCodigos = new ArrayList<>();

        while (stn2.hasMoreTokens()) {
            String cbeCampo = stn2.nextToken();

            String cbeCodigo = JspHelper.verificaVarQryStr(request, cbeCampo);

            if (!TextHelper.isNull(cbeCodigo) && !cbeCodigos.contains(cbeCodigo)) {
                cbeCodigos.add(cbeCodigo);
            }
        }

        if (cbeCodigos.size() > 0) {
            try {
                contratoBeneficioController.reativarContratoBeneficio(cbeCodigos, responsavel);
            } catch (ContratoBeneficioControllerException e) {
                session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
                request.setAttribute("tipo", "principal");
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.reativar.beneficio.nenhum.contrato", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.sucesso.reativar.beneficio.sucesso", responsavel));
        request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
        return "forward:/v3/carregarPrincipal";
    }

    /**
     * Busca as carteirinhas dos beneficiário a serem reativadas de forma async
     * @param csaCodigo
     * @param nseCodigo
     * @param rseCodigo
     * @peram dadosSimulacao
     * @param request
     * @return
     */
    @RequestMapping(method = { RequestMethod.POST }, value = { "/buscaNumCliente" })
    @ResponseBody
    private ResponseEntity<String> buscaNumCliente(@RequestParam(value = "simulacaoDados", required = true) String dadosSimulacaoAtual, HttpServletRequest request) {
        JSONObject postData = new JSONObject();
        HashMap<String, List<String>> hashNumeroClienteOdonto = new HashMap<>();
        HashMap<String, List<String>> hashNumeroClienteSaude = new HashMap<>();
        HashMap<String, HashMap<String, List<String>>> hashNumeroClienteFinal = new HashMap<>();
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Recebendo o Json da tela e fazendo o parse nele para recuperar os valores
        if (!dadosSimulacaoAtual.isEmpty()) {
            JsonReader jsonReader = Json.createReader(new StringReader(dadosSimulacaoAtual));
            JsonObject jsonObjectDadosSimulacaoAtual = jsonReader.readObject();

            if (jsonObjectDadosSimulacaoAtual.containsKey("simulacao")) {
                JsonArray simulacao = jsonObjectDadosSimulacaoAtual.getJsonArray("simulacao");
                for (int i = 0; i < simulacao.size(); i++) {
                    JsonObject simulacaoNse = simulacao.getJsonObject(i);
                    String benCodigoPassado = simulacaoNse.getString("benCodigo");
                    String nseCodigoPassado = simulacaoNse.getString("nseCodigo");
                    String csaCodigoPassado = simulacaoNse.getString("csaCodigo");

                    JsonArray beneficiariosCalculadosNse = simulacaoNse.getJsonArray("beneficiariosCalculados");
                    for (int y = 0; y < beneficiariosCalculadosNse.size(); y++) {
                        JsonObject beneficiario = beneficiariosCalculadosNse.getJsonObject(y);
                        String bfcCodigo = beneficiario.getString("bfcCodigo");

                        CustomTransferObject criterio = new CustomTransferObject();
                        criterio.setAttribute(Columns.BEN_CODIGO, benCodigoPassado);
                        List<String> scbCodigos = new ArrayList<>();
                        scbCodigos.add(CodedValues.SCB_CODIGO_CANCELADO);
                        criterio.setAttribute(Columns.SCB_CODIGO, scbCodigos);
                        criterio.setAttribute(Columns.NSE_CODIGO, nseCodigoPassado);
                        criterio.setAttribute(Columns.BFC_CODIGO, bfcCodigo);
                        criterio.setAttribute(Columns.CSA_CODIGO, csaCodigoPassado);
                        criterio.setAttribute("reativar", "true");
                        try {
                            List<TransferObject> contratosBeneficio = contratoBeneficioController.listarContratosBeneficioPorRegistroServidorQuery(criterio, responsavel);
                            List<String> detalhesContrato = new ArrayList<>();
                            for (TransferObject contratoBeneficio : contratosBeneficio) {
                                String cbeCodigo = (String) contratoBeneficio.getAttribute(Columns.CBE_CODIGO);
                                String cbeNumero = (String) contratoBeneficio.getAttribute(Columns.CBE_NUMERO);
                                detalhesContrato.add(cbeCodigo+";"+cbeNumero+";"+nseCodigoPassado);
                            }

                            if (!contratosBeneficio.isEmpty() && contratosBeneficio != null) {
                                if (nseCodigoPassado.equals(CodedValues.NSE_PLANO_DE_SAUDE)) {
                                    hashNumeroClienteSaude.put(bfcCodigo, detalhesContrato);
                                    hashNumeroClienteFinal.put("saude", hashNumeroClienteSaude);
                                } else if (nseCodigoPassado.equals(CodedValues.NSE_PLANO_ODONTOLOGICO)) {
                                    hashNumeroClienteOdonto.put(bfcCodigo, detalhesContrato);
                                    hashNumeroClienteFinal.put("odonto", hashNumeroClienteOdonto);
                                }
                            }

                        } catch (ContratoBeneficioControllerException e) {
                            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                        }

                    }
                }
            }
        }
        if (!hashNumeroClienteFinal.isEmpty()) {
            postData.putAll(hashNumeroClienteFinal);
        }

        return new ResponseEntity<>(postData.toString(), HttpStatus.OK);
    }
}
