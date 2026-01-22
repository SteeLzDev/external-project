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
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.BeneficioControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ContratoBeneficioControllerException;
import com.zetra.econsig.exception.RelacionamentoBeneficioServicoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
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
import com.zetra.econsig.persistence.entity.Beneficio;
import com.zetra.econsig.persistence.entity.BeneficioServico;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.service.beneficios.BeneficiarioController;
import com.zetra.econsig.service.beneficios.BeneficioController;
import com.zetra.econsig.service.beneficios.CalcularSubsidioBeneficioController;
import com.zetra.econsig.service.beneficios.ContratoBeneficioController;
import com.zetra.econsig.service.beneficios.RelacionamentoBeneficioServicoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedNames;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoBeneficiarioEnum;
import com.zetra.econsig.web.controller.servidor.AbstractConsultarServidorWebController;

/**
 * <p>Title: SimularBeneficiosWebController</p>
 * <p>Description: Web Controller para realizar a simulação de beneficios.</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: Nostrum Consultoria e Projetos.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/simulacaoBeneficios" })
public class SimularBeneficiosWebController extends AbstractConsultarServidorWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SimularBeneficiosWebController.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private RelacionamentoBeneficioServicoController relacionamentoBeneficioServicoController;

    @Autowired
    private BeneficioController beneficioController;

    @Autowired
    private BeneficiarioController beneficiarioController;

    @Autowired
    private CalcularSubsidioBeneficioController calcularSubsidioBeneficioController;

    @Autowired
    private ContratoBeneficioController contratoBeneficioController;

    @Autowired
    private ServidorController servidorController;

    /**
     * Carrega a tela para simular o plano de saude
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

        List<Consignataria> consignatariasPlanoSaude = new ArrayList<>();
        List<TransferObject> beneficiariosGrupoFamiliar = new ArrayList<>();

        try {
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
            List<String> scbCodigos = new ArrayList<>();
            scbCodigos.add(CodedValues.SCB_CODIGO_SOLICITADO);
            scbCodigos.add(CodedValues.SCB_CODIGO_AGUARDANDO_INCLUSAO_OPERADORA);
            scbCodigos.add(CodedValues.SCB_CODIGO_ATIVO);
            scbCodigos.add(CodedValues.SCB_CODIGO_CANCELAMENTO_SOLICITADO);
            scbCodigos.add(CodedValues.SCB_CODIGO_CANCELAMENTO_SOLICITADO_BENEFICIARIO);
            scbCodigos.add(CodedValues.SCB_CODIGO_AGUARDANDO_EXCLUSAO_OPERADORA);
            criterio.setAttribute(Columns.SCB_CODIGO, scbCodigos);
            criterio.setAttribute(Columns.NSE_CODIGO, CodedValues.NSE_PLANO_DE_SAUDE);
            List<TransferObject> contratosBeneficio = contratoBeneficioController.listarContratosBeneficioPorRegistroServidorQuery(criterio, responsavel);
            if (contratosBeneficio != null && contratosBeneficio.size() > 0) {
                session.setAttribute("contemPlanoSaude", "true");
                return carregaTelaSimulacaoPlanoOdontologico(rseCodigo, adeNumero, request, response, session, model);
            }

            // Buscando os beneficiarios do servidor informado ou logado.
            criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
            beneficiariosGrupoFamiliar = beneficiarioController.listarBeneficiariosFiltradorEOrdenadoSimulador(criterio, responsavel);

            if (beneficiariosGrupoFamiliar == null || beneficiariosGrupoFamiliar.size() == 0) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.selecionar.servidor.erro.nenhum.registro", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
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

            // Buscando as consignataria
            consignatariasPlanoSaude = consignatariaController.lstConsignatariaByNcaCodigoAndStatusConvenioAndNaturezaServico(CodedValues.NCA_CODIGO_OPERADORA_BENEFICIOS, CodedValues.SCV_ATIVO, CodedValues.NSE_PLANO_DE_SAUDE, responsavel);
        } catch (ConsignatariaControllerException | BeneficioControllerException | ContratoBeneficioControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("consignatariasPlanoSaude", consignatariasPlanoSaude);
        model.addAttribute("beneficiariosGrupoFamiliar", beneficiariosGrupoFamiliar);
        model.addAttribute("rseCodigo", rseCodigo);
        model.addAttribute("nseCodigo", CodedValues.NSE_PLANO_DE_SAUDE);

        return viewRedirect("jsp/simulacaoBeneficios/simularBeneficiosPlanoSaude", request, session, model, responsavel);
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

        List<Consignataria> consignatariasOdontologico = new ArrayList<>();
        List<TransferObject> beneficiariosGrupoFamiliar = new ArrayList<>();

        try {
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
            List<String> scbCodigos = new ArrayList<>();
            scbCodigos.add(CodedValues.SCB_CODIGO_SOLICITADO);
            scbCodigos.add(CodedValues.SCB_CODIGO_AGUARDANDO_INCLUSAO_OPERADORA);
            scbCodigos.add(CodedValues.SCB_CODIGO_ATIVO);
            scbCodigos.add(CodedValues.SCB_CODIGO_CANCELAMENTO_SOLICITADO);
            scbCodigos.add(CodedValues.SCB_CODIGO_CANCELAMENTO_SOLICITADO_BENEFICIARIO);
            scbCodigos.add(CodedValues.SCB_CODIGO_AGUARDANDO_EXCLUSAO_OPERADORA);
            criterio.setAttribute(Columns.SCB_CODIGO, scbCodigos);
            criterio.setAttribute(Columns.NSE_CODIGO, CodedValues.NSE_PLANO_ODONTOLOGICO);
            List<TransferObject> contratosBeneficio = contratoBeneficioController.listarContratosBeneficioPorRegistroServidorQuery(criterio, responsavel);
            if (contratosBeneficio != null && contratosBeneficio.size() > 0) {
                session.setAttribute("contemPlanoOdontologico", "true");
                return simulacaoDetalhes(rseCodigo, request, response, session, model);
            }

            // Buscando os beneficiarios do servidor informado ou logado.
            criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
            beneficiariosGrupoFamiliar = beneficiarioController.listarBeneficiariosFiltradorEOrdenadoSimulador(criterio, responsavel);

            consignatariasOdontologico = consignatariaController.lstConsignatariaByNcaCodigoAndStatusConvenioAndNaturezaServico(CodedValues.NCA_CODIGO_OPERADORA_BENEFICIOS, CodedValues.SCV_ATIVO, CodedValues.NSE_PLANO_ODONTOLOGICO, responsavel);

        } catch (ConsignatariaControllerException | BeneficioControllerException | ContratoBeneficioControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("consignatariasOdontologico", consignatariasOdontologico);
        model.addAttribute("beneficiariosGrupoFamiliar", beneficiariosGrupoFamiliar);
        model.addAttribute("rseCodigo", rseCodigo);
        model.addAttribute("fluxoInicial", session.getAttribute("contemPlanoSaude") != null);
        model.addAttribute("nseCodigo", CodedValues.NSE_PLANO_ODONTOLOGICO);

        return viewRedirect("jsp/simulacaoBeneficios/simularBeneficiosPlanoOdontologico", request, session, model, responsavel);
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
            List<Beneficio> beneficios = beneficioController.lstBeneficioByCsaCodigoAndNaturezaServicoAndRelacionamentoServico(csaCodigo, nseCodigo, true, responsavel);

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

            // Quando o parâmetro permite simulação sem margem de benefício, os contratos podem ser incluídos mesmo sem margem.
            boolean permiteSimulacaoBeneficioSemMargem = ParamSist.paramEquals(CodedValues.TPC_PERMITE_SIMULAR_BENEFICIO_SEM_MARGEM, CodedValues.TPC_SIM, responsavel);

            // Realizando o calculo da margem
            MargemDisponivel margemDisponivel = new MargemDisponivel(rseCodigo, null, servicosTitular.get(0).getServico().getSvcCodigo(), null, responsavel);

            // Analisando a margem se esta zerada ou nagativada
            if (margemDisponivel.getMargemRestante().compareTo(BigDecimal.ZERO) == -1 && !permiteSimulacaoBeneficioSemMargem) {
                String margemFormatada = NumberHelper.format(margemDisponivel.getMargemRestante().doubleValue(), NumberHelper.getLang());
                return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficio.sem.margem", responsavel, margemFormatada), HttpStatus.CONFLICT);
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

            // Analisamos se zeramos ou negativamos a margem
            if (margemRestanteCalculada.compareTo(BigDecimal.ZERO) == -1 && !permiteSimulacaoBeneficioSemMargem) {
                return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.valor.solicitado.maior.margem.disponivel", responsavel), HttpStatus.BAD_REQUEST);
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
            beneficiariosGrupoFamiliar = beneficiarioController.listarBeneficiariosFiltradorEOrdenadoSimulador(criterio, responsavel);
        } catch (BeneficioControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String paraOndeVoltar = contemPlanoOdontologico.isEmpty() ? "planoOdontologico" : "planoSaude";

        model.addAttribute("beneficiariosGrupoFamiliar", beneficiariosGrupoFamiliar);
        model.addAttribute("rseCodigo", rseCodigo);
        model.addAttribute("paraOndeVoltar", paraOndeVoltar);

        return viewRedirect("jsp/simulacaoBeneficios/simularBeneficiosDetalhes", request, session, model, responsavel);
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

        // Recuperando da tela os beneficiarios selecionados
        String tmpEntradaArray = JspHelper.verificaVarQryStr(request, "beneficiariosPlanoSaudeSelecionado");
        List<String> beneficiariosPlanoSaudeSelecionado = new ArrayList<>();
        String[] tmpArray = tmpEntradaArray.split(";", -1);
        for (String bfcCodigo : tmpArray) {
            if (!bfcCodigo.isEmpty()) {
                beneficiariosPlanoSaudeSelecionado.add(bfcCodigo);
            }
        }

        // Recuperando da tela os beneficiarios selecionados
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
            contratosBeneficios = contratoBeneficioController.criarReservaDeContratosBeneficios(rseCodigo, dadosSimulacao, responsavel);
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
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.simulacao.beneficio.titulo", responsavel, titulo));
        model.addAttribute("acaoFormulario", "../v3/simulacaoBeneficios");
        model.addAttribute("omitirAdeNumero", true);
        model.addAttribute("imageHeader", "i-beneficios");
    }

}
