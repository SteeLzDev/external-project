package com.zetra.econsig.web.controller.beneficiario;

import java.io.File;
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

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

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
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.BeneficioControllerException;
import com.zetra.econsig.exception.ContratoBeneficioControllerException;
import com.zetra.econsig.exception.RelacionamentoBeneficioServicoControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.margem.MargemDisponivel;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.BeneficioServico;
import com.zetra.econsig.service.beneficios.BeneficiarioController;
import com.zetra.econsig.service.beneficios.CalcularSubsidioBeneficioController;
import com.zetra.econsig.service.beneficios.ContratoBeneficioController;
import com.zetra.econsig.service.beneficios.RelacionamentoBeneficioServicoController;
import com.zetra.econsig.values.CodedNames;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusContratoBeneficioEnum;
import com.zetra.econsig.values.TipoBeneficiarioEnum;
import com.zetra.econsig.web.controller.servidor.AbstractConsultarServidorWebController;

/**
 * <p>Title: IncluirBeneficiarioSimulacaoBeneficiosWebController</p>
 * <p>Description: Inclur Beneficiario na Simulação de Beneficios</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: Nostrum Consultoria e Projetos.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/incluirBeneficiarioSimulacaoBeneficios" })
public class IncluirBeneficiarioSimulacaoBeneficiosWebController extends AbstractConsultarServidorWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(IncluirBeneficiarioSimulacaoBeneficiosWebController.class);

    @Autowired
    private BeneficiarioController beneficiarioController;

    @Autowired
    private ContratoBeneficioController contratoBeneficioController;

    @Autowired
    private CalcularSubsidioBeneficioController calcularSubsidioBeneficioController;

    @Autowired
    private RelacionamentoBeneficioServicoController relacionamentoBeneficioServicoController;

    @Override
    protected String continuarOperacao(String rseCodigo, String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException, UsuarioControllerException {
        return carregaTelaSimulacaoPlanoSaude(rseCodigo, adeNumero, "true", request, response, session, model);
    }

    @Override
    protected String definirProximaOperacao(HttpServletRequest request, AcessoSistema responsavel) {
        return "planoSaude";
    }

    @Override
    public void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        String titulo = JspHelper.verificaVarQryStr(request, "titulo");
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.simulacao.inclusao.vigente.beneficio.titulo", responsavel, titulo));
        model.addAttribute("acaoFormulario", "../v3/incluirBeneficiarioSimulacaoBeneficios");
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
    private String carregaTelaSimulacaoPlanoSaude(@RequestParam(value = "RSE_CODIGO", required = false) String rseCodigo, @RequestParam(value = "adeNumero", required = false) String adeNumero, @RequestParam(name = "reiniciarLocalSession", required = false) String reiniciarLocalSession, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException, UsuarioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Logica para saber se vamos reiniciar o localSession
        reiniciarLocalSession = reiniciarLocalSession == null ? "true" : reiniciarLocalSession;

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request) && !responsavel.isSer()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        if (responsavel.isSer()) {
            rseCodigo = responsavel.getRseCodigo();
        }

        try {
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);

            List<String> scbCodigos = new ArrayList<>();
            scbCodigos.add(StatusContratoBeneficioEnum.ATIVO.getCodigo());
            criterio.setAttribute(Columns.SCB_CODIGO, scbCodigos);
            criterio.setAttribute(Columns.NSE_CODIGO, CodedValues.NSE_PLANO_DE_SAUDE);
            List<TransferObject> contratosBeneficio = contratoBeneficioController.listarContratosBeneficioPorRegistroServidorQuery(criterio, responsavel);

            if (contratosBeneficio == null || contratosBeneficio.isEmpty()) {
                criterio.setAttribute(Columns.NSE_CODIGO, CodedValues.NSE_PLANO_ODONTOLOGICO);
                contratosBeneficio = contratoBeneficioController.listarContratosBeneficioPorRegistroServidorQuery(criterio, responsavel);

                if (contratosBeneficio == null || contratosBeneficio.isEmpty()) {
                    //caso não tenha contrato beneficio ativo tanto de plano de saude como odontologico da mensagem de erro
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficio.beneficiario.nao.possui.contrato.ativo", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                } else {
                    // caso tenha somente contrato beneficio ativo odontologio vai para a tela correspondente
                    request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
                    session.setAttribute("naoContemPlanoSaude", true);
                    return carregaTelaSimulacaoPlanoOdontologico(rseCodigo, adeNumero, null, "true", request, response, session, model);
                }
            }

            TransferObject contratoBeneficio = contratosBeneficio.get(0);
            String svcCodigo = (String) contratoBeneficio.getAttribute(Columns.SVC_CODIGO);

            // Buscando os beneficiarios do servidor informado ou logado.
            criterio = new CustomTransferObject();
            scbCodigos.clear();
            scbCodigos.add(StatusContratoBeneficioEnum.CANCELADO.getCodigo());
            criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
            criterio.setAttribute(Columns.SVC_CODIGO, svcCodigo);
            criterio.setAttribute(Columns.SCB_CODIGO, scbCodigos);
            criterio.setAttribute(Columns.NSE_CODIGO, CodedValues.NSE_PLANO_DE_SAUDE);
            List<TransferObject> beneficiariosGrupoFamiliar = beneficiarioController.listarBeneficiariosFiltradorEOrdenadoSimulador(criterio, responsavel);

            if (beneficiariosGrupoFamiliar == null || beneficiariosGrupoFamiliar.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.selecionar.servidor.erro.nenhum.registro", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Quando o parâmetro permite simulação sem margem de benefício, os contratos podem ser incluídos mesmo sem margem.
            boolean permiteSimulacaoBeneficioSemMargem = ParamSist.paramEquals(CodedValues.TPC_PERMITE_SIMULAR_BENEFICIO_SEM_MARGEM, CodedValues.TPC_SIM, responsavel);

            MargemDisponivel margemDisponivel = new MargemDisponivel(rseCodigo, null, svcCodigo, null, responsavel);

            // Analisando a margem se esta zerada ou negativada
            if (margemDisponivel.getMargemRestante().compareTo(BigDecimal.ZERO) == -1 && !permiteSimulacaoBeneficioSemMargem) {
                String margemFormatada = NumberHelper.format(margemDisponivel.getMargemRestante().doubleValue(), NumberHelper.getLang());
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficio.sem.margem", responsavel, margemFormatada));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            model.addAttribute("beneficiariosGrupoFamiliar", beneficiariosGrupoFamiliar);
            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("reiniciarLocalSession", "true".equalsIgnoreCase(reiniciarLocalSession));
            model.addAttribute("contratoBeneficio", contratoBeneficio);
            model.addAttribute("margemDisponivel", NumberHelper.format(margemDisponivel.getMargemRestante().doubleValue(), NumberHelper.getLang()));
            model.addAttribute("nseCodigo", CodedValues.NSE_PLANO_DE_SAUDE);

        } catch (BeneficioControllerException | ContratoBeneficioControllerException | ViewHelperException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/simulacaoBeneficios/incluirBeneficiarioPlanoSaude", request, session, model, responsavel);
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
    private String carregaTelaSimulacaoPlanoOdontologico(@RequestParam(value = "RSE_CODIGO", required = false) String rseCodigo, @RequestParam(value = "adeNumero", required = false) String adeNumero, @RequestParam(name = "beneficiariosComContratoSaude", required = false) List<String> beneficiariosComContratoSaude, @RequestParam(name = "reiniciarLocalSession", required = false) String reiniciarLocalSession, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException, UsuarioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Logica para saber se vamos reiniciar o localSession
        reiniciarLocalSession = reiniciarLocalSession == null ? "true" : reiniciarLocalSession;

        model.addAttribute("fluxoInicial", session.getAttribute("naoContemPlanoSaude") != null);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request) && !responsavel.isSer()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        if (responsavel.isSer()) {
            rseCodigo = responsavel.getRseCodigo();
        }

        try {
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);

            List<String> scbCodigos = new ArrayList<>();
            scbCodigos.add(StatusContratoBeneficioEnum.ATIVO.getCodigo());
            criterio.setAttribute(Columns.SCB_CODIGO, scbCodigos);
            criterio.setAttribute(Columns.NSE_CODIGO, CodedValues.NSE_PLANO_ODONTOLOGICO);
            List<TransferObject> contratosBeneficio = contratoBeneficioController.listarContratosBeneficioPorRegistroServidorQuery(criterio, responsavel);

            if (contratosBeneficio == null || contratosBeneficio.isEmpty()) {
                request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));
                return simulacaoDetalhes(rseCodigo, beneficiariosComContratoSaude, null, request, response, session, model);
            }

            TransferObject contratoBeneficio = contratosBeneficio.get(0);
            String svcCodigo = (String) contratoBeneficio.getAttribute(Columns.SVC_CODIGO);

            // Buscando os beneficiarios do servidor informado ou logado.
            criterio = new CustomTransferObject();
            scbCodigos.clear();
            scbCodigos.add(StatusContratoBeneficioEnum.CANCELADO.getCodigo());
            criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
            criterio.setAttribute(Columns.SVC_CODIGO, svcCodigo);
            criterio.setAttribute(Columns.SCB_CODIGO, scbCodigos);
            criterio.setAttribute(Columns.NSE_CODIGO, CodedValues.NSE_PLANO_ODONTOLOGICO);
            List<TransferObject> beneficiariosGrupoFamiliar = beneficiarioController.listarBeneficiariosFiltradorEOrdenadoSimulador(criterio, responsavel);

            if (beneficiariosGrupoFamiliar == null || beneficiariosGrupoFamiliar.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.selecionar.servidor.erro.nenhum.registro", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            boolean permiteSimulacaoBeneficioSemMargem = ParamSist.paramEquals(CodedValues.TPC_PERMITE_SIMULAR_BENEFICIO_SEM_MARGEM, CodedValues.TPC_SIM, responsavel);

            MargemDisponivel margemDisponivel = new MargemDisponivel(rseCodigo, null, svcCodigo, null, responsavel);

            // Analisando a margem se esta zerada ou negativada
            if (margemDisponivel.getMargemRestante().compareTo(BigDecimal.ZERO) == -1 && !permiteSimulacaoBeneficioSemMargem) {
                String margemFormatada = NumberHelper.format(margemDisponivel.getMargemRestante().doubleValue(), NumberHelper.getLang());
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficio.sem.margem", responsavel, margemFormatada));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            model.addAttribute("beneficiariosGrupoFamiliar", beneficiariosGrupoFamiliar);
            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("reiniciarLocalSession", "true".equalsIgnoreCase(reiniciarLocalSession));
            model.addAttribute("contratoBeneficio", contratoBeneficio);
            model.addAttribute("beneficiariosComContratoSaude", beneficiariosComContratoSaude);
            model.addAttribute("margemDisponivel", NumberHelper.format(margemDisponivel.getMargemRestante().doubleValue(), NumberHelper.getLang()));
            model.addAttribute("nseCodigo", CodedValues.NSE_PLANO_ODONTOLOGICO);

        } catch (BeneficioControllerException | ContratoBeneficioControllerException | ViewHelperException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return viewRedirect("jsp/simulacaoBeneficios/incluirBeneficiarioPlanoOdontologico", request, session, model, responsavel);
    }

    /**
     * Realiza a simulação de forma async
     * @param bfcCodigoSelecionados
     * @param benCodigo
     * @param rseCodigo
     * @param csaCodigo
     * @param totalADescontoCalculado
     * @param request
     * @return
     */
    @RequestMapping(method = { RequestMethod.POST }, value = { "/simulaAjax" })
    @ResponseBody
    private ResponseEntity<String> simulaAjax(@RequestParam(value = "nseCodigo", required = true) String nseCodigo, @RequestParam(value = "benCodigo", required = true) String benCodigo, @RequestParam(value = "rseCodigo", required = true) String rseCodigo, @RequestParam(value = "csaCodigo", required = true) String csaCodigo, @RequestParam(value = "bfcCodigoSelecionados[]", required = false) List<String> bfcCodigoSelecionados, @RequestParam(value = "dadosSimulacao", required = false) String dadosSimulacaoInclusao, HttpServletRequest request) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel), HttpStatus.UNAUTHORIZED);
        }

        if (bfcCodigoSelecionados == null || bfcCodigoSelecionados.isEmpty()) {
            return new ResponseEntity<>(Json.createObjectBuilder().build().toString(), HttpStatus.OK);
        }

        // Analisando se o fluxo é do servidor e garantido que o rseCodigo seja da pessoa.
        if (responsavel.isSer()) {
            rseCodigo = responsavel.getRseCodigo();
        }

        try {
            // Instanciando os delegates necessarios
            Map<String, JsonArrayBuilder> cacheArrayBeneficiarioCalculados = new HashMap<>();
            Map<String, JsonArrayBuilder> cacheArrayBeneficiarioSemCalculos = new HashMap<>();
            Map<String, String> cacheBenCodigo = new HashMap<>();
            Map<String, String> cacheCsaCodigo = new HashMap<>();
            Map<String, BigDecimal> cacheTotalMensalidade = new HashMap<>();
            Map<String, BigDecimal> cacheTotalSubsidio = new HashMap<>();

            Map<String, List<String>> dadosSimulacao = new HashMap<>();

            // Recebendo o Json da tela e fazendo o parse nele para recuperar os valores
            if (!dadosSimulacaoInclusao.isEmpty()) {
                JsonReader jsonReader = Json.createReader(new StringReader(dadosSimulacaoInclusao));
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

            raiz.add("rseCodigo", rseCodigo);

            BigDecimal margemRestanteCalculada = new BigDecimal("0.00");

            Set<String> bfcCodigoJaProcessador = new HashSet<>();

            // Buscando o serviço com base no Beneficio
            List<BeneficioServico> servicosTitular = relacionamentoBeneficioServicoController.findByBenCodigoTibCodigo(benCodigo, CodedValues.TIB_TITULAR);
            if (servicosTitular == null || servicosTitular.size() != 1) {
                return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficio.nenhum.ou.mais.servico.entrados", responsavel), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            boolean permiteSimulacaoBeneficioSemMargem = ParamSist.paramEquals(CodedValues.TPC_PERMITE_SIMULAR_BENEFICIO_SEM_MARGEM, CodedValues.TPC_SIM, responsavel);

            // Realizando o calculo da margem
            MargemDisponivel margemDisponivel = new MargemDisponivel(rseCodigo, null, servicosTitular.get(0).getServico().getSvcCodigo(), null, responsavel);

            // Analisando a margem se esta zerada ou nagativada
            if (margemDisponivel.getMargemRestante().compareTo(BigDecimal.ZERO) == -1 && !permiteSimulacaoBeneficioSemMargem) {
                String margemFormatada = NumberHelper.format(margemDisponivel.getMargemRestante().doubleValue(), NumberHelper.getLang());
                return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficio.sem.margem", responsavel, margemFormatada), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // Realizando a busca do beneficiario realizando o filtro por serviço para garantir que somente os beneficiario permitidos estejam validos
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
            criterio.setAttribute(Columns.SVC_CODIGO, servicosTitular.get(0).getServico().getSvcCodigo());
            List<TransferObject> beneficiariosGrupoFamiliar = beneficiarioController.listarBeneficiariosFiltradorEOrdenadoSimulador(criterio, responsavel);

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

                // Calculando a "nova" margem com base nos calculos dos plano acima
                margemRestanteCalculada = margemDisponivel.getMargemRestante();

                // Para cadas resultado calculado monto um json com os dados necessarios para desenhar na tela.
                for (TransferObject resultado : resultados) {
                    BigDecimal mensalidade = (BigDecimal) resultado.getAttribute("VALOR_MENSALIDADE");
                    BigDecimal subsidio = (BigDecimal) resultado.getAttribute("VALOR_SUBSIDIO");
                    BigDecimal totalAPagar = mensalidade.subtract(subsidio);
                    String tibCodigo = (String) resultado.getAttribute(Columns.TIB_CODIGO);
                    String nseCodigoSimulacao = (String) resultado.getAttribute(Columns.NSE_CODIGO);
                    String benCodigoSimulacao = (String) resultado.getAttribute(Columns.BEN_CODIGO);
                    String csaCodigoSimulacao = (String) resultado.getAttribute(Columns.CSA_CODIGO);

                    cacheBenCodigo.put(nseCodigoSimulacao, benCodigoSimulacao);
                    cacheCsaCodigo.put(nseCodigoSimulacao, csaCodigoSimulacao);

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

                    BigDecimal totalMensalidade = cacheTotalMensalidade.get(nseCodigoSimulacao) == null ? new BigDecimal("0.00") : cacheTotalMensalidade.get(nseCodigoSimulacao);
                    totalMensalidade = totalMensalidade.add(mensalidade);

                    BigDecimal totalSubsidio = cacheTotalSubsidio.get(nseCodigoSimulacao) == null ? new BigDecimal("0.00") : cacheTotalSubsidio.get(nseCodigoSimulacao);
                    totalSubsidio = totalSubsidio.add(subsidio);

                    cacheTotalMensalidade.put(nseCodigoSimulacao, totalMensalidade);
                    cacheTotalSubsidio.put(nseCodigoSimulacao, totalSubsidio);

                    // Analisando se foi selecionado um Agreado e não poderia
                    if (TipoBeneficiarioEnum.AGREGADO.equals(tibCodigo) && !edicaoSimulacao ) {
                        teveAgregado = true;
                    }

                    margemRestanteCalculada = margemRestanteCalculada.subtract(mensalidade.subtract(subsidio));

                    arrayLoop.add(resultadoJson);

                    cacheArrayBeneficiarioCalculados.put(nseCodigoSimulacao, arrayLoop);
                }

                // Analiso se teve algum titular selecionado
                if (!teveTitularSelecionado) {
                    return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficio.titular.nao.selecionado", responsavel), HttpStatus.INTERNAL_SERVER_ERROR);
                }

                if (teveAgregado) {
                    return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficio.agregado.nao.incluido", responsavel), HttpStatus.TEMPORARY_REDIRECT);
                }
            }

            // Analisamos se zeramos ou negativamos a margem
            if (margemRestanteCalculada.compareTo(BigDecimal.ZERO) == -1 && !permiteSimulacaoBeneficioSemMargem) {
                return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.valor.solicitado.maior.margem.disponivel", responsavel), HttpStatus.INTERNAL_SERVER_ERROR);
            }

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

            BigDecimal totalMensalidade = new BigDecimal("0.00");
            BigDecimal totalSubsidio = new BigDecimal("0.00");

            for (String key : todosNseCodigo) {
                JsonObjectBuilder dados = Json.createObjectBuilder();
                dados.add("nseCodigo", key);
                dados.add("benCodigo", cacheBenCodigo.get(key));
                dados.add("beneficiariosCalculados", cacheArrayBeneficiarioCalculados.get(key) == null ? Json.createArrayBuilder() : cacheArrayBeneficiarioCalculados.get(key));
                dados.add("beneficiariosSemCalculos", cacheArrayBeneficiarioSemCalculos.get(key) == null ? Json.createArrayBuilder() : cacheArrayBeneficiarioSemCalculos.get(key));

                BigDecimal tm = cacheTotalMensalidade.get(key) == null ? new BigDecimal("0.00") : cacheTotalMensalidade.get(key);
                BigDecimal ts = cacheTotalSubsidio.get(key) == null ? new BigDecimal("0.00") : cacheTotalSubsidio.get(key);

                totalMensalidade = totalMensalidade.add(tm);
                totalSubsidio = totalSubsidio.add(ts);

                dados.add("totalMensalidade", NumberHelper.format(tm.doubleValue(), NumberHelper.getLang()));
                dados.add("totalSubsidio", NumberHelper.format(ts.doubleValue(), NumberHelper.getLang()));
                dados.add("totalADesconto", NumberHelper.format(tm.subtract(ts).doubleValue(), NumberHelper.getLang()));

                dados.add("csaCodigo", cacheCsaCodigo.get(key));

                arraySimulacao.add(dados);
            }

            raiz.add("simulacao", arraySimulacao);

            raiz.add("totalSimulacao", NumberHelper.format(totalMensalidade.subtract(totalSubsidio).doubleValue(), NumberHelper.getLang()));
            raiz.add("margemSemPlano", NumberHelper.format(margemDisponivel.getMargemRestante().doubleValue(), NumberHelper.getLang()));
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
    public String simulacaoDetalhes(@RequestParam(value = "RSE_CODIGO", required = true) String rseCodigo, @RequestParam(value = "beneficiariosComContratoSaude", required = false) List<String> beneficiariosComContratoSaude, @RequestParam(value = "beneficiariosComContratoOdonto", required = false) List<String> beneficiariosComContratoOdonto, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        if (responsavel.isSer()) {
            rseCodigo = responsavel.getRseCodigo();
        }

        List<TransferObject> beneficiariosGrupoFamiliar = new ArrayList<>();

        try {
            // Buscando os beneficiarios do servidor informado ou logado.
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
            beneficiariosGrupoFamiliar = beneficiarioController.listarBeneficiariosFiltradorEOrdenadoSimulador(criterio, responsavel);
        } catch (BeneficioControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("beneficiariosGrupoFamiliar", beneficiariosGrupoFamiliar);
        model.addAttribute("reiniciarLocalSession", false);
        model.addAttribute("rseCodigo", rseCodigo);
        model.addAttribute("beneficiariosComContratoSaude", beneficiariosComContratoSaude);
        model.addAttribute("beneficiariosComContratoOdonto", beneficiariosComContratoOdonto);
        // Ao chamar a tela de detalhe, define para qual tela o botão "voltar" deve redirecionar
        String paramLinkVoltar = (beneficiariosComContratoOdonto != null && !beneficiariosComContratoOdonto.isEmpty() ? "planoOdontologico" : "planoSaude");
        model.addAttribute("paramLinkVoltar", paramLinkVoltar);

        return viewRedirect("jsp/simulacaoBeneficios/incluirBeneficiarioDetalhes", request, session, model, responsavel);
    }

    /**
     * Metodo para gravar no banco a simulação
     * @param request
     * @param response
     * @param session
     * @param model
     * @return
     */
    @RequestMapping(params = { "acao=salvar" })
    public String simulacaoSalvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        // Recuperando da tela o beneficio selecionado
        String beneficioPlanoSaudeSelecionado = JspHelper.verificaVarQryStr(request, "beneficioPlanoSaudeSelecionado");
        String beneficioPlanoOdontologicoSelecionado = JspHelper.verificaVarQryStr(request, "beneficioPlanoOdontologicoSelecionado");

        // Recuperando da tela os beneficiarios que já tinham contrato de plano de saude
        String tmpEntradaArray = JspHelper.verificaVarQryStr(request, "beneficiariosComContratoSaude");
        List<String> beneficiarioComContratoSaude = new ArrayList<>();
        String[] tmpArray = tmpEntradaArray.split(",", -1);
        for (String bfcCodigo : tmpArray) {
            if (!bfcCodigo.isEmpty()) {
                beneficiarioComContratoSaude.add(bfcCodigo);
            }
        }

        // Recuperando da tela os beneficiarios que já tinham contrato de plano odontologico
        tmpEntradaArray = JspHelper.verificaVarQryStr(request, "beneficiariosComContratoOdonto");
        List<String> beneficiarioComContratoOdonto = new ArrayList<>();
        tmpArray = tmpEntradaArray.split(",", -1);
        for (String bfcCodigo : tmpArray) {
            if (!bfcCodigo.isEmpty()) {
                beneficiarioComContratoOdonto.add(bfcCodigo);
            }
        }

        // Recuperando da tela os beneficiarios selecionados de plano de saude
        tmpEntradaArray = JspHelper.verificaVarQryStr(request, "beneficiariosPlanoSaudeSelecionado");
        List<String> beneficiariosPlanoSaudeSelecionado = new ArrayList<>();
        tmpArray = tmpEntradaArray.split(";", -1);
        for (String bfcCodigo : tmpArray) {
            if (!bfcCodigo.isEmpty()) {
                beneficiariosPlanoSaudeSelecionado.add(bfcCodigo);
            }
        }

        // Recuperando da tela os beneficiarios selecionados de plano odontologico
        tmpEntradaArray = JspHelper.verificaVarQryStr(request, "beneficiariosPlanoOdontologicoSelecionado");
        List<String> beneficiariosPlanoOdontologicoSelecionado = new ArrayList<>();
        tmpArray = tmpEntradaArray.split(";", -1);
        for (String bfcCodigo : tmpArray) {
            if (!bfcCodigo.isEmpty()) {
                beneficiariosPlanoOdontologicoSelecionado.add(bfcCodigo);
            }
        }

        String rseCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.RSE_CODIGO));
        if (responsavel.isSer()) {
            rseCodigo = responsavel.getRseCodigo();
        }

        // Analisando se teve algum beneficiario informado.
        Map<String, List<String>> dadosSimulacao = new HashMap<>();
        if (beneficiariosPlanoSaudeSelecionado.size() > 0 || !TextHelper.isNull(beneficioPlanoSaudeSelecionado)) {
            dadosSimulacao.put(beneficioPlanoSaudeSelecionado, beneficiariosPlanoSaudeSelecionado);
        }

        if (beneficiariosPlanoOdontologicoSelecionado.size() > 0 && !TextHelper.isNull(beneficioPlanoOdontologicoSelecionado)) {
            dadosSimulacao.put(beneficioPlanoOdontologicoSelecionado, beneficiariosPlanoOdontologicoSelecionado);
        }

        if (dadosSimulacao.size() == 0) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        List<String> contratosBeneficios = new ArrayList<>();
        try {
            contratosBeneficios = contratoBeneficioController.criarReservaDeContratosBeneficios(rseCodigo, dadosSimulacao, beneficiarioComContratoSaude, beneficiarioComContratoOdonto, responsavel);
        } catch (ContratoBeneficioControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            request.setAttribute("tipo", "principal");
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return boletoSimulacao(contratosBeneficios, request, response, session, model);
    }

    /**
     * Gera o "boleto" na tela com os dados informativo.
     * @param contratosBeneficios
     * @param request
     * @param response
     * @param session
     * @param model
     * @return
     */
    public String boletoSimulacao(List<String> contratosBeneficios, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String boleto = CodedNames.TEMPLATE_MENSAGEM_SOLICITACAO_BENEFICIO;
        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        absolutePath += File.separatorChar + "boleto" + File.separatorChar + boleto;

        File arqBoleto = new File(absolutePath);
        String msgBoleto = "";
        if (arqBoleto.exists()) {
            msgBoleto = FileHelper.readAll(absolutePath);
        }

        model.addAttribute("msgBoleto", msgBoleto);

        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.sucesso.simulacao.beneficio", responsavel));

        return viewRedirect("jsp/simulacaoBeneficios/simularBeneficiosBoleto", request, session, model, responsavel);
    }
}
