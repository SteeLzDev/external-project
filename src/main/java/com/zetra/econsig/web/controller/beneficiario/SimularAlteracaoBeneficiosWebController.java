package com.zetra.econsig.web.controller.beneficiario;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
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
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ContratoBeneficioControllerException;
import com.zetra.econsig.exception.NaturezaServicoControllerException;
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
import com.zetra.econsig.persistence.entity.Beneficio;
import com.zetra.econsig.persistence.entity.BeneficioServico;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.persistence.entity.NaturezaServico;
import com.zetra.econsig.service.beneficios.BeneficiarioController;
import com.zetra.econsig.service.beneficios.BeneficioController;
import com.zetra.econsig.service.beneficios.CalcularSubsidioBeneficioController;
import com.zetra.econsig.service.beneficios.ContratoBeneficioController;
import com.zetra.econsig.service.beneficios.RelacionamentoBeneficioServicoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.servico.NaturezaServicoController;
import com.zetra.econsig.values.CodedNames;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusContratoBeneficioEnum;
import com.zetra.econsig.values.TipoBeneficiarioEnum;
import com.zetra.econsig.web.controller.servidor.AbstractConsultarServidorWebController;

/**
 * <p>Title: SimularAlteracaoBeneficiosWebController</p>
 * <p>Description: Web Controller para realizar a simulação de alteração beneficios.</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: Nostrum Consultoria e Projetos.</p>
 * $Author$
 * $Revision$
 * $Date$
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/simulacaoAlteracaoBeneficios" })
public class SimularAlteracaoBeneficiosWebController extends AbstractConsultarServidorWebController {
    private static final String chaveSessionStore = "simulacaoAlteracaoBeneficios";

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private RelacionamentoBeneficioServicoController relacionamentoBeneficioServicoController;

    @Autowired
    private NaturezaServicoController naturezaServicoController;

    @Autowired
    private BeneficioController beneficioController;

    @Autowired
    private BeneficiarioController beneficiarioController;

    @Autowired
    private CalcularSubsidioBeneficioController calcularSubsidioBeneficioController;

    @Autowired
    private ContratoBeneficioController contratoBeneficioController;

    /**
     * Carrega a tela para selecionar o beneficio que deseja alterar.
     * @param rseCodigo
     * @param adeNumero
     * @param request
     * @param response
     * @param session
     * @param model
     * @return
     */
    @RequestMapping(params = { "acao=selecionarBeneficio" })
    public String carregaTelaSelecionaBeneficio(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, @RequestParam(value = "adeNumero", required = false) String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request) && !responsavel.isSer()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        if (responsavel.isSer()) {
            rseCodigo = responsavel.getRseCodigo();
        }

        boolean temContratoEmAndamento = false;

        List<CustomTransferObject> naturezasServico = new ArrayList<>();
        try {
            List<NaturezaServico> tmp = naturezaServicoController.listaNaturezasByNseCodigo(Arrays.asList(CodedValues.NSE_PLANO_DE_SAUDE, CodedValues.NSE_PLANO_ODONTOLOGICO), responsavel);

            for (NaturezaServico naturezaServico : tmp) {
                CustomTransferObject cto = new CustomTransferObject();
                cto.setAttribute(Columns.NSE_CODIGO, naturezaServico.getNseCodigo());
                cto.setAttribute(Columns.NSE_DESCRICAO, naturezaServico.getNseDescricao());

                List<TransferObject> contratosBeneficio = listarContratosBeneficioPorRegistroServidor(rseCodigo, naturezaServico.getNseCodigo(), null, null, responsavel);

                List<String> sbcCodigo = new ArrayList<>();
                sbcCodigo.add(StatusContratoBeneficioEnum.SOLICITADO.getCodigo());
                sbcCodigo.add(StatusContratoBeneficioEnum.AGUARD_INCLUSAO_OPERADORA.getCodigo());
                List<String> tibsCodigo = Arrays.asList(CodedValues.TIB_TITULAR, CodedValues.TIB_AGREGADO, CodedValues.TIB_DEPENDENTE);
                List<TransferObject> contratosBeneficioEmAndamento = listarContratosBeneficioPorRegistroServidor(rseCodigo, naturezaServico.getNseCodigo(), tibsCodigo, sbcCodigo, responsavel);
                if (contratosBeneficio.isEmpty() || !contratosBeneficioEmAndamento.isEmpty()) {
                    cto.setAttribute("ativo", false);
                } else {
                    cto.setAttribute("ativo", true);
                    temContratoEmAndamento = true;
                }

                naturezasServico.add(cto);
            }

        } catch (NaturezaServicoControllerException | ContratoBeneficioControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("rseCodigo", rseCodigo);
        model.addAttribute("naturezasServico", naturezasServico);
        model.addAttribute("temContratoEmAndamento", temContratoEmAndamento);

        return viewRedirect("jsp/simulacaoAlteracaoBeneficios/simulacaoAlteracaoBeneficiosSelecionarBeneficio", request, session, model, responsavel);
    }

    /**
     * Carrega a tela de realiza a simulção de migração
     * @param rseCodigo
     * @param nseCodigo
     * @param adeNumero
     * @param request
     * @param response
     * @param session
     * @param model
     * @return
     */
    @RequestMapping(params = { "acao=simular" })
    public String carregaTelaSimulacao(@RequestParam(value = "rseCodigo", required = true) String rseCodigo, @RequestParam(value = "nseCodigo", required = true) String nseCodigo, @RequestParam(value = "adeNumero", required = false) String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
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

        List<Consignataria> consignatariasPlanoSaude = new ArrayList<>();
        List<TransferObject> beneficiariosGrupoFamiliar = new ArrayList<>();
        NaturezaServico naturezaServico = null;

        try {
            // Analisando se o beneficiario tem algum contrato ativado para poder solicitar a migração
            List<TransferObject> contratosBeneficio = listarContratosBeneficioPorRegistroServidor(rseCodigo, nseCodigo, null, null, responsavel);
            if (contratosBeneficio.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficio.beneficiario.nao.possui.contrato.ativo", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Analisando se o beneficiario tem algum contrato andamento para aprovação
            List<String> sbcCodigo = new ArrayList<>();
            sbcCodigo.add(StatusContratoBeneficioEnum.SOLICITADO.getCodigo());
            sbcCodigo.add(StatusContratoBeneficioEnum.AGUARD_INCLUSAO_OPERADORA.getCodigo());
            List<String> tibsCodigo = Arrays.asList(CodedValues.TIB_TITULAR, CodedValues.TIB_AGREGADO, CodedValues.TIB_DEPENDENTE);
            contratosBeneficio = listarContratosBeneficioPorRegistroServidor(rseCodigo, nseCodigo, tibsCodigo, sbcCodigo, responsavel);
            if (!contratosBeneficio.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficio.beneficiario.possui.contrato.simulado", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (responsavel.isSer()) {
                sbcCodigo.add(StatusContratoBeneficioEnum.ATIVO.getCodigo());
                sbcCodigo.add(StatusContratoBeneficioEnum.CANCELAMENTO_SOLICITADO.getCodigo());
                sbcCodigo.add(StatusContratoBeneficioEnum.CANCELAMENTO_SOLICITADO_BENEFICIARIO.getCodigo());
                sbcCodigo.add(StatusContratoBeneficioEnum.AGUARD_EXCLUSAO_OPERADORA.getCodigo());
                contratosBeneficio = listarContratosBeneficioPorRegistroServidor(rseCodigo, nseCodigo, tibsCodigo, sbcCodigo, responsavel);
                for (TransferObject contrato : contratosBeneficio) {
                    String bfcSubsidioConcedido = contrato.getAttribute(Columns.BFC_SUBSIDIO_CONCEDIDO) == null ? CodedValues.TPC_NAO : contrato.getAttribute(Columns.BFC_SUBSIDIO_CONCEDIDO).toString();

                    if (CodedValues.TPC_SIM.equals(bfcSubsidioConcedido)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficio.existe.beneficiario.com.subcidio.concedido", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }

                }
            }

            // Buscando os beneficiarios do servidor informado ou logado.
            CustomTransferObject criterio = new CustomTransferObject();
            criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
            beneficiariosGrupoFamiliar = beneficiarioController.listarBeneficiariosFiltradorEOrdenadoSimulador(criterio, responsavel);

            if (beneficiariosGrupoFamiliar == null || beneficiariosGrupoFamiliar.size() == 0) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.selecionar.servidor.erro.nenhum.registro", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Buscando as consignataria
            consignatariasPlanoSaude = consignatariaController.lstConsignatariaByNcaCodigoAndStatusConvenioAndNaturezaServico(CodedValues.NCA_CODIGO_OPERADORA_BENEFICIOS, CodedValues.SCV_ATIVO, nseCodigo, responsavel);

            // Buscando a natureza para exibir na tela
            List<NaturezaServico> tmpNatureza = naturezaServicoController.listaNaturezasByNseCodigo(Arrays.asList(nseCodigo), responsavel);
            if (tmpNatureza == null || tmpNatureza.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficio.nenhum.natureza.encontrada ", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            naturezaServico = tmpNatureza.get(0);
        } catch (ConsignatariaControllerException | BeneficioControllerException | ContratoBeneficioControllerException | NaturezaServicoControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("chaveSessionStore", chaveSessionStore.concat(rseCodigo).concat(nseCodigo));
        model.addAttribute("naturezaServico", naturezaServico);
        model.addAttribute("consignatariasPlanoSaude", consignatariasPlanoSaude);
        model.addAttribute("beneficiariosGrupoFamiliar", beneficiariosGrupoFamiliar);
        model.addAttribute("rseCodigo", rseCodigo);

        return viewRedirect("jsp/simulacaoAlteracaoBeneficios/simulacaoAlteracaoBeneficios", request, session, model, responsavel);
    }

    /**
     * Carrega a tela mostrando os detalhes da simulção para o usuario
     * @param rseCodigo
     * @param nseCodigo
     * @param request
     * @param response
     * @param session
     * @param model
     * @return
     */
    @RequestMapping(params = { "acao=detalhes" })
    public String simulacaoAlteracaoDetalhes(@RequestParam(value = "rseCodigo", required = true) String rseCodigo, @RequestParam(value = "nseCodigo", required = true) String nseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request) && session.getAttribute("contemPlanoOdontologico") == null) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        SynchronizerToken.saveToken(request);

        if (responsavel.isSer()) {
            rseCodigo = responsavel.getRseCodigo();
        }

        List<TransferObject> beneficiariosGrupoFamiliar = new ArrayList<>();
        NaturezaServico naturezaServico = null;

        try {
            // Buscando os beneficiarios do servidor informado ou logado.
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
            beneficiariosGrupoFamiliar = beneficiarioController.listarBeneficiariosFiltradorEOrdenadoSimulador(criterio, responsavel);

            // Buscando a natureza para exibir na tela
            List<NaturezaServico> tmpNatureza = naturezaServicoController.listaNaturezasByNseCodigo(Arrays.asList(nseCodigo), responsavel);
            if (tmpNatureza == null || tmpNatureza.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficio.nenhum.natureza.encontrada ", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            naturezaServico = tmpNatureza.get(0);
        } catch (BeneficioControllerException | NaturezaServicoControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("chaveSessionStore", chaveSessionStore.concat(rseCodigo).concat(nseCodigo));
        model.addAttribute("naturezaServico", naturezaServico);
        model.addAttribute("beneficiariosGrupoFamiliar", beneficiariosGrupoFamiliar);
        model.addAttribute("rseCodigo", rseCodigo);

        return viewRedirect("jsp/simulacaoAlteracaoBeneficios/simulacaoAlteracaoBeneficiosDetalhes", request, session, model, responsavel);
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
        String beneficioPlanoSelecionado = JspHelper.verificaVarQryStr(request, "beneficioPlanoSelecionado");

        // Recuperando da tela os beneficiarios selecionados
        String tmpEntradaArray = JspHelper.verificaVarQryStr(request, "beneficiariosPlanoSelecionado");
        List<String> beneficiariosPlanoSelecionado = new ArrayList<>();
        String[] tmpArray = tmpEntradaArray.split(";", -1);
        for (String bfcCodigo : tmpArray) {
            if (!bfcCodigo.isEmpty()) {
                beneficiariosPlanoSelecionado.add(bfcCodigo);
            }
        }

        String rseCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.RSE_CODIGO));
        if (responsavel.isSer()) {
            rseCodigo = responsavel.getRseCodigo();
        }

        // Analisando se teve algum beneficiario informado.
        Map<String, List<String>> dadosSimulacao = new HashMap<>();
        if (beneficiariosPlanoSelecionado.size() > 0 || !TextHelper.isNull(beneficioPlanoSelecionado)) {
            dadosSimulacao.put(beneficioPlanoSelecionado, beneficiariosPlanoSelecionado);
        }

        if (dadosSimulacao.size() == 0) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        List<String> contratosBeneficios = new ArrayList<>();
        try {
            contratosBeneficios = contratoBeneficioController.criarReservaDeContratosBeneficiosMigracao(rseCodigo, dadosSimulacao, responsavel);
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

    /**
     * Cria um Json com os beneficios para o usuario selecionar
     * @param csaCodigo
     * @param nseCodigo
     * @param rseCodigo
     * @param request
     * @return
     */
    @RequestMapping(method = { RequestMethod.POST }, value = { "/carregaBeneficiosAjax" })
    @ResponseBody
    public ResponseEntity<String> carregaBeneficios(@RequestParam(value = "csaCodigo", required = true) String csaCodigo, @RequestParam(value = "nseCodigo", required = true) String nseCodigo, @RequestParam(value = "rseCodigo", required = true) String rseCodigo, HttpServletRequest request) {
        JsonObjectBuilder raiz = Json.createObjectBuilder();
        JsonArrayBuilder array = Json.createArrayBuilder();
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        if (responsavel.isSer()) {
            rseCodigo = responsavel.getRseCodigo();
        }

        try {
            List<Beneficio> beneficios = beneficioController.lstBeneficioByCsaCodigoAndNaturezaServicoAndRelacionamentoServico(csaCodigo, nseCodigo, true, responsavel);
            if (beneficios == null || beneficios.isEmpty()) {
                return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficio.nenhum.beneficio.encontrado", responsavel), HttpStatus.NOT_ACCEPTABLE);
            }

            // Buscando os beneficios já existentes para esse contrato para removermos ele da listagem
            List<TransferObject> contratosBeneficio = listarContratosBeneficioPorRegistroServidor(rseCodigo, nseCodigo, null, null, responsavel);
            if (contratosBeneficio.isEmpty()) {
                return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficio.nenhum.beneficio.encontrado", responsavel), HttpStatus.NOT_ACCEPTABLE);
            }

            Set<String> controleBenContratosAtivos = new HashSet<>();
            for (TransferObject contratoBeneficio : contratosBeneficio) {
                String benCodigo = contratoBeneficio.getAttribute(Columns.BEN_CODIGO).toString();
                controleBenContratosAtivos.add(benCodigo);
            }

            for (Beneficio beneficio : beneficios) {
                JsonObjectBuilder beneficioJson = Json.createObjectBuilder();

                if (!controleBenContratosAtivos.contains(beneficio.getBenCodigo())) {
                    beneficioJson.add("id", beneficio.getBenCodigo());
                    beneficioJson.add("detalhe", TextHelper.forHtmlContent(beneficio.getBenDescricao()));

                    array.add(beneficioJson);
                }
            }

            raiz.add("beneficios", array);
        } catch (BeneficioControllerException | ContratoBeneficioControllerException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(raiz.build().toString(), HttpStatus.OK);
    }

    /**
     * Cria um JSon com os dados da simulação
     * @param bfcCodigoSelecionados
     * @param benCodigo
     * @param rseCodigo
     * @param csaCodigo
     * @param nseCodigo
     * @param request
     * @return
     */
    @RequestMapping(method = { RequestMethod.POST }, value = { "/simulaAjax" })
    @ResponseBody
    public ResponseEntity<String> simulaAjax(@RequestParam(value = "bfcCodigoSelecionados[]", required = false) List<String> bfcCodigoSelecionados, @RequestParam(value = "benCodigo", required = true) String benCodigo, @RequestParam(value = "rseCodigo", required = true) String rseCodigo, @RequestParam(value = "csaCodigo", required = true) String csaCodigo, @RequestParam(value = "nseCodigo", required = true) String nseCodigo, HttpServletRequest request) {
        JsonObjectBuilder raiz = Json.createObjectBuilder();
        JsonArrayBuilder arrayBeneficiarioCalculados = Json.createArrayBuilder();
        JsonArrayBuilder arrayBeneficiarioSemCalculos = Json.createArrayBuilder();

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Analisando se o fluxo é do servidor e garantido que o rseCodigo sejá da pessoa.
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (responsavel.isSer()) {
            rseCodigo = responsavel.getRseCodigo();
        }

        raiz.add("rseCodigo", rseCodigo);
        raiz.add("benCodigo", benCodigo);
        raiz.add("csaCodigo", csaCodigo);

        try {
            BigDecimal totalMensalidadeNovaSimulacao = new BigDecimal("0.00");
            BigDecimal totalSubsidioNovaSimulacao = new BigDecimal("0.00");
            BigDecimal totalMensalidadeAtual = new BigDecimal("0.00");
            BigDecimal totalSubsidioAtual = new BigDecimal("0.00");
            BigDecimal margemRestanteCalculada = new BigDecimal("0.00");

            Set<String> bfcCodigoJaProcessador = new HashSet<>();

            // Buscando o serviço com base no Beneficio
            List<BeneficioServico> servicosTitular = relacionamentoBeneficioServicoController.findByBenCodigoTibCodigo(benCodigo, CodedValues.TIB_TITULAR);
            if (servicosTitular == null || servicosTitular.size() != 1) {
                return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficio.nenhum.ou.mais.servico.entrados", responsavel), HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // Realizando o calculo da margem
            MargemDisponivel margemDisponivel = new MargemDisponivel(rseCodigo, null, servicosTitular.get(0).getServico().getSvcCodigo(), null, responsavel);
            margemRestanteCalculada = margemDisponivel.getMargemRestante();

            boolean contratoAtivoAgregado = Boolean.FALSE;

            // Buscando todos os contratos ativos para assim recuperar o valor total e add na margem
            // E validar se em algum dos contratos ativo esta no periodo de carencia.
            List<String> tibsCodigo = Arrays.asList(CodedValues.TIB_TITULAR, CodedValues.TIB_AGREGADO, CodedValues.TIB_DEPENDENTE);
            List<TransferObject> contratosAtivos = listarContratosBeneficioPorRegistroServidor(rseCodigo, nseCodigo, tibsCodigo, null, responsavel);
            for (TransferObject contratoAtivo : contratosAtivos) {
                String valorTotal = contratoAtivo.getAttribute("valorTotal").toString();
                String valorSubsidio = contratoAtivo.getAttribute("valorSubsidio").toString();

                BigDecimal tmp = new BigDecimal(valorTotal);
                totalMensalidadeAtual = totalMensalidadeAtual.add(tmp);

                tmp = new BigDecimal(valorSubsidio);
                totalSubsidioAtual = totalSubsidioAtual.add(tmp);

                if(TipoBeneficiarioEnum.AGREGADO.tibCodigo.equals(contratoAtivo.getAttribute(Columns.TIB_CODIGO))) {
                    contratoAtivoAgregado = Boolean.TRUE;
                }

                try {
                    contratoBeneficioController.validaCarenciaMigracaoContratoBeneficio(contratoAtivo, csaCodigo, benCodigo, responsavel);
                } catch (ContratoBeneficioControllerException e) {
                    return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
                }
            }

            boolean permiteSimulacaoBeneficioSemMargem = ParamSist.paramEquals(CodedValues.TPC_PERMITE_SIMULAR_BENEFICIO_SEM_MARGEM, CodedValues.TPC_SIM, responsavel);

            margemRestanteCalculada = margemRestanteCalculada.add(totalMensalidadeAtual.subtract(totalSubsidioAtual));

            // Analisando a margem se esta zerada ou nagativada
            if (margemRestanteCalculada.compareTo(BigDecimal.ZERO) == -1 && !permiteSimulacaoBeneficioSemMargem) {
                String margemFormatada = NumberHelper.format(margemRestanteCalculada.doubleValue(), NumberHelper.getLang());
                return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficio.sem.margem", responsavel, margemFormatada), HttpStatus.CONFLICT);
            }

            // Realizando a busca do beneficiario realizando o filtro por serviço para garantir que somente os beneficiario permitidos estejam validos
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
            criterio.setAttribute(Columns.SVC_CODIGO, servicosTitular.get(0).getServico().getSvcCodigo());
            List<TransferObject> beneficiariosGrupoFamiliar = beneficiarioController.listarBeneficiariosFiltradorEOrdenadoSimulador(criterio, responsavel);
            Set<String> bfcCodigoValidos = new HashSet<>();

            //É preciso analisar se na lista de beneficiários existe algum agregado e se existe é preciso verificar se este tem algum contrato ativo
            //Caso ele não tenha nenhum contrato ativo da natureza em questão ele não pode aparecer no fluxo de alteração DESENV-11557
            if (!contratoAtivoAgregado) {
                for (int i = 0; i < beneficiariosGrupoFamiliar.size(); i++) {
                    if (TipoBeneficiarioEnum.AGREGADO.tibCodigo.equals(beneficiariosGrupoFamiliar.get(i).getAttribute(Columns.TIB_CODIGO))) {
                        beneficiariosGrupoFamiliar.remove(i);
                    }
                }
            }

            // Montando a estrutura de dados necessarias para o calculo
            Map<String, List<String>> dadosSimulacao = new HashMap<>();
            if (bfcCodigoSelecionados != null) {
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

                // Realizando a simulação
                dadosSimulacao.put(benCodigo, bfcCodigoSelecionados);
                List<TransferObject> resultados = calcularSubsidioBeneficioController.simularCalculoSubsidio(dadosSimulacao, rseCodigo, true, responsavel);

                // Para cadas resultado calculado monto um json com os dados necessarios para desenhar na tela.
                for (TransferObject resultado : resultados) {
                    BigDecimal mensalidade = (BigDecimal) resultado.getAttribute("VALOR_MENSALIDADE");
                    BigDecimal subsidio = (BigDecimal) resultado.getAttribute("VALOR_SUBSIDIO");
                    BigDecimal totalAPagar = mensalidade.subtract(subsidio);
                    String tibCodigo = (String) resultado.getAttribute(Columns.TIB_CODIGO);
                    String nseCodigoSimulacao = (String) resultado.getAttribute(Columns.NSE_CODIGO);
                    String bfcCodigo = (String) resultado.getAttribute(Columns.BFC_CODIGO);

                    // Analisando se teve um titular no fluxo
                    if (nseCodigoSimulacao.equals(nseCodigo) && TipoBeneficiarioEnum.TITULAR.equals(tibCodigo)) {
                        teveTitularSelecionado = true;
                    }

                    JsonObjectBuilder resultadoJson = Json.createObjectBuilder();
                    resultadoJson.add("bfcCodigo", bfcCodigo);
                    resultadoJson.add("mensalidade", NumberHelper.format(mensalidade.doubleValue(), NumberHelper.getLang()));
                    resultadoJson.add("subsidio", NumberHelper.format(subsidio.doubleValue(), NumberHelper.getLang()));
                    resultadoJson.add("totalAPagar", NumberHelper.format(totalAPagar.doubleValue(), NumberHelper.getLang()));

                    if (bfcCodigoSelecionados.contains(bfcCodigo) && nseCodigoSimulacao.equals(nseCodigo)) {
                        bfcCodigoJaProcessador.add((String) resultado.getAttribute(Columns.BFC_CODIGO));

                        totalMensalidadeNovaSimulacao = totalMensalidadeNovaSimulacao.add(mensalidade);
                        totalSubsidioNovaSimulacao = totalSubsidioNovaSimulacao.add(subsidio);

                        // Salvando no array
                        arrayBeneficiarioCalculados.add(resultadoJson);
                    }
                }

                // Analiso se teve algum titular selecionado
                if (!teveTitularSelecionado) {
                    return new ResponseEntity<>(ApplicationResourcesHelper.getMessage("mensagem.erro.simulacao.beneficio.titular.nao.selecionado", responsavel), HttpStatus.BAD_REQUEST);
                }
            }

            BigDecimal margemSemPlanoAlterandoAtual = margemRestanteCalculada;
            margemRestanteCalculada = margemRestanteCalculada.subtract(totalMensalidadeNovaSimulacao.subtract(totalSubsidioNovaSimulacao));

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
            List<TransferObject> resultados = calcularSubsidioBeneficioController.simularCalculoSubsidio(dadosSimulacao, rseCodigo, true, responsavel);

            // Para cadas resultado calculado monto um json com os dados necessarios para desenhar na tela.
            for (TransferObject resultado : resultados) {
                double tmp = 0;
                BigDecimal mensalidade = (BigDecimal) resultado.getAttribute("VALOR_MENSALIDADE");
                String bfcCodigo = (String) resultado.getAttribute(Columns.BFC_CODIGO);
                String nseCodigoSimulacao = (String) resultado.getAttribute(Columns.NSE_CODIGO);

                JsonObjectBuilder resultadoJson = Json.createObjectBuilder();
                resultadoJson.add("bfcCodigo", bfcCodigo);
                resultadoJson.add("mensalidade", NumberHelper.format(mensalidade.doubleValue(), NumberHelper.getLang()));
                resultadoJson.add("subsidio", NumberHelper.format(tmp, NumberHelper.getLang()));

                if (beneficiarioNaoSelecionados.contains(bfcCodigo) && nseCodigoSimulacao.equals(nseCodigo)) {
                    arrayBeneficiarioSemCalculos.add(resultadoJson);
                }
            }

            raiz.add("margemSemPlano", NumberHelper.format(margemSemPlanoAlterandoAtual.doubleValue(), NumberHelper.getLang()));
            raiz.add("margemDisponivel", NumberHelper.format(margemRestanteCalculada.doubleValue(), NumberHelper.getLang()));
            raiz.add("totalMensalidade", NumberHelper.format(totalMensalidadeNovaSimulacao.doubleValue(), NumberHelper.getLang()));
            raiz.add("totalSubsidio", NumberHelper.format(totalSubsidioNovaSimulacao.doubleValue(), NumberHelper.getLang()));
            raiz.add("totalADesconto", NumberHelper.format(totalMensalidadeNovaSimulacao.subtract(totalSubsidioNovaSimulacao).doubleValue(), NumberHelper.getLang()));
        } catch (BeneficioControllerException | RelacionamentoBeneficioServicoControllerException | ViewHelperException | ContratoBeneficioControllerException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        raiz.add("beneficiariosCalculados", arrayBeneficiarioCalculados);
        raiz.add("beneficiariosSemCalculos", arrayBeneficiarioSemCalculos);

        return new ResponseEntity<>(raiz.build().toString(), HttpStatus.OK);
    }

    /**
     * Lista os contratos do beneficiario dependendo do filtro informado
     * @param rseCodigo
     * @param nseCodigo
     * @param tibCodigo
     * @param scbCodigos
     * @param responsavel
     * @return
     * @throws ContratoBeneficioControllerException
     */
    private List<TransferObject> listarContratosBeneficioPorRegistroServidor(String rseCodigo, String nseCodigo, List<String> tibCodigo, List<String> scbCodigos, AcessoSistema responsavel) throws ContratoBeneficioControllerException {
        CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);

        if (scbCodigos == null) {
            scbCodigos = new ArrayList<>();
            scbCodigos.add(StatusContratoBeneficioEnum.ATIVO.getCodigo());
        }

        criterio.setAttribute(Columns.SCB_CODIGO, scbCodigos);
        criterio.setAttribute(Columns.NSE_CODIGO, nseCodigo);
        criterio.setAttribute(Columns.TIB_CODIGO, tibCodigo);

        return contratoBeneficioController.listarContratosBeneficioPorRegistroServidorQuery(criterio, responsavel);
    }

    @Override
    protected String continuarOperacao(String rseCodigo, String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException, UsuarioControllerException {
        return carregaTelaSelecionaBeneficio(rseCodigo, adeNumero, request, response, session, model);
    }

    @Override
    protected String definirProximaOperacao(HttpServletRequest request, AcessoSistema responsavel) {
        return "selecionarBeneficio";
    }

    @Override
    public void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        String titulo = JspHelper.verificaVarQryStr(request, "titulo");
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.simulacao.alteracao.beneficio.titulo", responsavel, titulo));
        model.addAttribute("acaoFormulario", "../v3/simulacaoAlteracaoBeneficios");
        model.addAttribute("omitirAdeNumero", true);
        model.addAttribute("imageHeader", "i-beneficios");
    }

}
