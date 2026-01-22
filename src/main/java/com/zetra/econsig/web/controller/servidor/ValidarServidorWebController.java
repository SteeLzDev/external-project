package com.zetra.econsig.web.controller.servidor;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.notificacao.NotificacaoUsuarioController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;

/**
 * <p>Title: ValidarServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Validar Servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
public class ValidarServidorWebController extends AbstractServidorWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidarServidorWebController.class);

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private NotificacaoUsuarioController notificacaoUsuarioController;

    @Autowired
    private MargemController margemController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/validarServidor" }, params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        SynchronizerToken.saveToken(request);
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        int offset = (!TextHelper.isNull(request.getParameter("offset")) && TextHelper.isNum(request.getParameter("offset"))) ?  Integer.parseInt(request.getParameter("offset")) : 0;
        int size = JspHelper.LIMITE;
        int total = 0;

        boolean omiteMatriculaServidor = ParamSist.paramEquals(CodedValues.TPC_SERVIDOR_POSSUI_MATRICULA, CodedValues.TPC_NAO, responsavel);
        boolean omiteCpfServidor = ParamSist.paramEquals(CodedValues.TPC_OMITE_CPF_SERVIDOR, CodedValues.TPC_SIM, responsavel);
        model.addAttribute("omiteMatriculaServidor", omiteMatriculaServidor);
        model.addAttribute("omiteCpfServidor", omiteCpfServidor);

        try {
            String serCpf = request.getParameter("SER_CPF");
            String serNome = request.getParameter("serNome");
            String serSobrenome = request.getParameter("serSobrenome");
            String serDataNasc = request.getParameter("serDataNasc");
            String estCodigo = request.getParameter("EST_CODIGO");
            String orgCodigo = request.getParameter("ORG_CODIGO");
            String rseMatricula = request.getParameter("RSE_MATRICULA");

            if(verificarSePossuiParametro(serCpf, serNome, serSobrenome, serDataNasc, estCodigo, orgCodigo, rseMatricula)) {
                request.setAttribute("possuiParametroPesquisa", true);
            }

            TransferObject criterioSer = new CustomTransferObject();
            criterioSer.setAttribute(Columns.SER_CPF, serCpf);
            criterioSer.setAttribute(Columns.SER_NOME, serNome);
            criterioSer.setAttribute(Columns.SER_ULTIMO_NOME, serSobrenome);
            criterioSer.setAttribute(Columns.SER_DATA_NASC, serDataNasc);
            criterioSer.setAttribute(Columns.EST_CODIGO, estCodigo);
            criterioSer.setAttribute(Columns.ORG_CODIGO, orgCodigo);
            criterioSer.setAttribute(Columns.RSE_MATRICULA, rseMatricula);

            total = pesquisarServidorController.contarServidorPendente(criterioSer, responsavel);

            if (total > 0) {
                List<TransferObject> lstServidor = pesquisarServidorController.pesquisarServidorPendente(criterioSer, offset, size, responsavel);
                model.addAttribute("lstServidor", lstServidor);
            }

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

            String linkListagem = "../v3/validarServidor?acao=iniciar";
            configurarPaginador(linkListagem, "rotulo.paginacao.titulo.download.arq.integracao", total, size, requestParams, false, request, model);

            TransferObject criterio = new CustomTransferObject();
            if (responsavel.isOrg()) {
                criterio.setAttribute(Columns.EST_CODIGO, responsavel.getEstCodigo());
                if (!responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                    criterio.setAttribute(Columns.ORG_CODIGO, responsavel.getOrgCodigo());
                }
            }

            List<TransferObject> lstEstabelecimentos = consignanteController.lstEstabelecimentos(criterio, responsavel);
            model.addAttribute("lstEstabelecimentos", lstEstabelecimentos);

            List<TransferObject> lstOrgaos = consignanteController.lstOrgaos(criterio, responsavel);
            model.addAttribute("lstOrgaos", lstOrgaos);

        } catch (ServidorControllerException | ConsignanteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        SynchronizerToken.saveToken(request);
        model.addAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, session.getAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY));
        model.addAttribute("_skip_history_", Boolean.TRUE);

        return viewRedirect("jsp/validarServidor/listarServidor", request, session, model, responsavel);
    }

    private boolean verificarSePossuiParametro(String serCpf, String serNome, String serSobrenome, String serDataNasc, String estCodigo, String orgCodigo, String rseMatricula) {

        if (StringUtils.isNotBlank(serCpf) ||
                StringUtils.isNotBlank(serNome) ||
                StringUtils.isNotBlank(serSobrenome) ||
                StringUtils.isNotBlank(serDataNasc) ||
                StringUtils.isNotBlank(estCodigo) ||
                StringUtils.isNotBlank(orgCodigo) ||
                StringUtils.isNotBlank(rseMatricula)) {
            return true;
        }

        return false;
    }

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/validarServidor" }, params = { "acao=aprovarRejeitarCadServidor" })
    public String aprovarRejeitarCadServidor(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, ZetraException, IllegalAccessException, ParametroControllerException, ServidorControllerException, ServletException, IOException, ParseException {

    	String rseCodigosAprovar = request.getParameter("checkAprovar");
    	String[] rseCodigosAprovarList = rseCodigosAprovar.split(",");

    	String rseCodigosRejeitar = request.getParameter("checkRejeitar");
    	String[] rseCodigosRejeitarList = rseCodigosRejeitar.split(",");

    	if (!TextHelper.isNull(rseCodigosAprovarList)) {
    			try {
    				aprovarMultiplos(rseCodigosAprovarList, request, response, session, model);
    			} catch (ZetraException ex) {
    			    LOG.error(ex.getMessage(), ex);
    				session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
    			}

    	}

    	if (!TextHelper.isNull(rseCodigosRejeitarList)) {
    			try {
    				return informarMotivoMultiplo(rseCodigosRejeitarList, request, response, session, model);
    			} catch (ZetraException ex) {
    			    LOG.error(ex.getMessage(), ex);
    				session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
    			}

    	}
		return iniciar(request, response, session, model);
    }

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/validarServidor" }, params = { "acao=aprovar" })
    public String aprovar(@RequestParam(value = "rseCodigo", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ZetraException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            RegistroServidorTO rseTO = servidorController.findRegistroServidor(rseCodigo, responsavel);
            ServidorTransferObject serTO = servidorController.findServidor(rseTO.getSerCodigo(), responsavel);

            if (validarDadosServidor(serTO, request, responsavel) &&
                    validarDadosRegistroServidor(rseTO, request, responsavel)) {
                servidorController.aprovarCadastroServidor(rseTO, null, true, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.sucesso", responsavel));
            }
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        // Retorna à listagem de servidores pendentes
        return iniciar(request, response, session, model);
    }

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/validarServidor" }, params = { "acao=aprovarMultiplos" })
    public String aprovarMultiplos(@RequestParam(value = "rseCodigos", required = true, defaultValue = "") String[] rseCodigos, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ZetraException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            for (String rseCodigo : rseCodigos) {
            	RegistroServidorTO rseTO = servidorController.findRegistroServidor(rseCodigo, responsavel);
                ServidorTransferObject serTO = servidorController.findServidor(rseTO.getSerCodigo(), responsavel);

                if (validarDadosServidor(serTO, request, responsavel) &&
                        validarDadosRegistroServidor(rseTO, request, responsavel)) {
                    servidorController.aprovarCadastroServidor(rseTO, null, true, responsavel);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.sucesso", responsavel));
                }
			}
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        // Retorna à listagem de servidores pendentes
        return iniciar(request, response, session, model);
    }

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/validarServidor" }, params = { "acao=informarMotivo" })
    public String informarMotivo(@RequestParam(value = "rseCodigo", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ZetraException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            TransferObject rseTO = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);

            model.addAttribute("servidor", rseTO);
        } catch (ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        model.addAttribute("_skip_history_", Boolean.TRUE);
        return viewRedirect("jsp/validarServidor/informarMotivo", request, session, model, responsavel);
    }

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/validarServidor" }, params = { "acao=informarMotivoMultiplo" })
    public String informarMotivoMultiplo(@RequestParam(value = "rseCodigos", required = true, defaultValue = "") String[] rseCodigos, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ZetraException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
        	ArrayList<TransferObject> rseCodigosList = new ArrayList<>();
            for (String rseCodigo : rseCodigos) {
            	TransferObject rseTO = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);

                rseCodigosList.add(rseTO);
			}

            model.addAttribute("servidores", rseCodigosList);
        } catch (ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        model.addAttribute("_skip_history_", Boolean.TRUE);
        return viewRedirect("jsp/validarServidor/informarMotivo", request, session, model, responsavel);
    }

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/validarServidor" }, params = { "acao=rejeitar" })
    public String rejeitar(@RequestParam(value = "rseCodigo", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            RegistroServidorTO rseTO = new RegistroServidorTO(rseCodigo);

            String tmoCodigo = JspHelper.verificaVarQryStr(request, "TMO_CODIGO");
            rseTO.setTipoMotivo(tmoCodigo);
            String orsObs = JspHelper.verificaVarQryStr(request, "ADE_OBS");
            rseTO.setOrsObs(orsObs);

            servidorController.aprovarCadastroServidor(rseTO, null, false, responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.sucesso", responsavel));
        } catch (ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        ParamSession paramSession = ParamSession.getParamSession(session);
        paramSession.halfBack();

        // Retorna à listagem de servidores pendentes
        return iniciar(request, response, session, model);
    }

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/validarServidor" }, params = { "acao=rejeitarMultiplos" })
    public String rejeitarMultiplos(@RequestParam(value = "rseCodigos", required = true, defaultValue = "") String rseCodigosList, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String[] rseCodigos = TextHelper.split(rseCodigosList, ";");

        try {
            for(String rseCodigo : rseCodigos) {
            	RegistroServidorTO rseTO = new RegistroServidorTO(rseCodigo);

                String tmoCodigo = JspHelper.verificaVarQryStr(request, "TMO_CODIGO");
                rseTO.setTipoMotivo(tmoCodigo);
                String orsObs = JspHelper.verificaVarQryStr(request, "ADE_OBS");
                rseTO.setOrsObs(orsObs);

                servidorController.aprovarCadastroServidor(rseTO, null, false, responsavel);

                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.sucesso", responsavel));
            }
        } catch (ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        ParamSession paramSession = ParamSession.getParamSession(session);
        paramSession.halfBack();

        // Retorna à listagem de servidores pendentes
        return iniciar(request, response, session, model);
    }

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/validarServidor" }, params = { "acao=editar" })
    public String editar(@RequestParam(value = "rseCodigo", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            // Carrega as informações necessárias
            RegistroServidorTO registroServidor = servidorController.findRegistroServidor(rseCodigo, true, responsavel);
            model.addAttribute("registroServidor", registroServidor);

            ServidorTransferObject servidor = servidorController.findServidor(registroServidor.getSerCodigo(), responsavel);
            model.addAttribute("servidor", servidor);

            if (ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_ESTADO_CIVIL, responsavel)) {
                model.addAttribute("listEstadoCivil", servidorController.getEstCivil(responsavel));
            }

            if(ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_NIVEL_ESCOLARIDADE, responsavel)) {
                model.addAttribute("listNivelEscolaridade", servidorController.getNivelEscolaridade(responsavel));
            }

            if(ShowFieldHelper.showField(FieldKeysConstants.EDT_SERVIDOR_TIPO_HABITACAO, responsavel)) {
                model.addAttribute("listTipoHabitacao", servidorController.getTipoHabitacao(responsavel));
            }

            //DESENV-8327: Se campo estiver visível no sistema, lista para ser configurado para o servidor em qual margem
            //             todas consignações irão incidir
            if (ShowFieldHelper.showField(FieldKeysConstants.EDT_REGISTRO_SERVIDOR_MARGEM_LIMITE_DESCONTO_FOLHA, responsavel)) {
                List<MargemTO> margensRaiz = margemController.lstMargemRaiz(responsavel);
                model.addAttribute("margensRaiz", margensRaiz);
            }

            // Carrega as entidades adicionais para a tela de edição
            recuperarDadosAdicionaisRseServidor(servidor, request, responsavel);

        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        model.addAttribute("_skip_history_", Boolean.TRUE);
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.validar.servidor.titulo", responsavel));

        return viewRedirect("jsp/validarServidor/editarServidor", request, session, model, responsavel);
    }

    @RequestMapping(method = { RequestMethod.POST }, value = { "/v3/validarServidor" }, params = { "acao=salvar" })
    public String salvar(@RequestParam(value = "rseCodigo", required = true, defaultValue = "") String rseCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            // Carrega as informações necessárias
            RegistroServidorTO registroServidor = servidorController.findRegistroServidor(rseCodigo, true, responsavel);

            ServidorTransferObject servidor = servidorController.findServidor(registroServidor.getSerCodigo(), responsavel);

            // Recupera os dados do cadastro de servidor e registro servidor atualizados pelo usuário
            recuperarDadosServidor(servidor, request, notificacaoUsuarioController, responsavel);
            recuperarDadosRegistroServidor(registroServidor, request, responsavel);

            if (validarDadosServidor(servidor, request, responsavel) &&
                    validarDadosRegistroServidor(registroServidor, request, responsavel)) {

                // Verifica se deve aprovar ou rejeitar. Só aprova se o status não é campo editável, ou
                // é editável, e não foi alterado, continuando como PENDENTE ou foi manualmente alterado para ATIVO
                boolean aprovar = (TextHelper.isNull(registroServidor.getSrsCodigo()) ||
                        registroServidor.getSrsCodigo().equals(CodedValues.SRS_ATIVO) ||
                        registroServidor.getSrsCodigo().equals(CodedValues.SRS_PENDENTE));

                // Realiza a alteração e aprovação dos dados
                servidorController.aprovarCadastroServidor(registroServidor, servidor, aprovar, responsavel);

                // Seta mensagem de sucesso
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.sucesso", responsavel));
            }

        } catch (ZetraException | ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        } catch (InstantiationException | IllegalAccessException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
        }

        ParamSession paramSession = ParamSession.getParamSession(session);
        paramSession.halfBack();

        // Retorna à listagem de servidores pendentes
        return iniciar(request, response, session, model);
    }
}
