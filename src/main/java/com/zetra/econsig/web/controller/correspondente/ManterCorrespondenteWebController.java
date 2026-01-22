package com.zetra.econsig.web.controller.correspondente;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.TipoMotivoOperacaoTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p> * Title: ManterCorrespondenteWebController * </p>
 * <p> * Description: Controlador Web para manter corresposndente. * </p>
 * <p> * Copyright: Copyright (c) 2002-2017 * </p>
 * <p> * Company: ZetraSoft * </p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/manterCorrespondente" })
public class ManterCorrespondenteWebController extends ControlePaginacaoWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterCorrespondenteWebController.class);

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        ParamSession paramSession = ParamSession.getParamSession(session);

        if (!responsavel.isCsaCor() && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);
        if (responsavel.isCor()) {
            // Hack necessário pois no link do menu não tem o token. E este entrypoint está sendo usado por todos os papéis.
            request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, request.getSession().getAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY));
            return consultar(request, response, session, model);
        }

        String csa = request.getParameter("csa");

        /* usuario */
        boolean podeCriarUsu = responsavel.temPermissao(CodedValues.FUN_CRIAR_USUARIOS_COR);
        boolean podeConsultarUsu = responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_COR);
        /* perfil usuario */
        boolean podeConsultarPerfilUsu = responsavel.temPermissao(CodedValues.FUN_CONS_PERFIL_COR);
        /* servico */
        boolean podeConsultarCnvCor = responsavel.temPermissao(CodedValues.FUN_CONS_CONV_CORRESPONDENTE);
        boolean podeEditarCnvCor = responsavel.temPermissao(CodedValues.FUN_EDT_CONV_CORRESPONDENTE);
        /* correspondente */
        boolean podeConsultarCor = responsavel.temPermissao(CodedValues.FUN_CONS_CORRESPONDENTES);
        boolean podeEditarCor = responsavel.temPermissao(CodedValues.FUN_EDT_CORRESPONDENTES);
        boolean podeExcluirCor = responsavel.temPermissao(CodedValues.FUN_EXCL_CORRESPONDENTE);
        boolean podeEditarEnderecoAcesso = responsavel.temPermissao(CodedValues.FUN_EDT_IP_ACESSO_COR);
        boolean podeEditarEnderecosCor = responsavel.temPermissao(CodedValues.FUN_EDITAR_ENDERECOS_CORRESPONDENTE);

        String csaCodigo = responsavel.getCodigoEntidade();
        String titulo = ApplicationResourcesHelper.getMessage("rotulo.lista.correspondentes.titulo", responsavel);
        String editaCorr = "../v3/manterCorrespondente?acao=consultar&" + SynchronizerToken.generateToken4URL(request);
        String canc = "../v3/carregarPrincipal";
        String linkRet = "../v3/manterCorrespondente$acao(iniciar";

        boolean cadastraEmpCor = ParamSist.paramEquals(CodedValues.TPC_CADASTRO_EMPRESA_CORRESPONDENTE, CodedValues.TPC_SIM, responsavel);
        String novoCorr = "../v3/manterCorrespondente?acao=consultar&" + SynchronizerToken.generateToken4URL(request);
        // se possui cadastro de empresa correspondente, exigir CNPJ da empresa correspondente ao cadastrar correspondete.
        if (cadastraEmpCor) {
            novoCorr = "../v3/manterCorrespondente?acao=pesquisarEmpCorrespondente&tipo=consultar&" + SynchronizerToken.generateToken4URL(request);
        }

        // Se o usuário não é da consignatária
        if (!responsavel.isCsa()) {
            csaCodigo = JspHelper.verificaVarQryStr(request, "csa");
            String subTitulo = JspHelper.verificaVarQryStr(request, "titulo");

            if (!subTitulo.equals("")) {
                titulo += " - " + subTitulo;
            }

            editaCorr = "../v3/manterCorrespondente?acao=consultar&csa=" + csaCodigo + "&titulo=" + subTitulo + "&" + SynchronizerToken.generateToken4URL(request);
            novoCorr += "&csa=" + csaCodigo + "&titulo=" + subTitulo + "&novo=novo";
            canc = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request);

            try {
                linkRet = "../v3/manterCorrespondente$acao(iniciar|csa(" + csaCodigo + "|titulo(" + java.net.URLEncoder.encode(subTitulo.replaceAll("\"", ""), "ISO-8859-1");
            } catch (UnsupportedEncodingException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        String linkEdit = linkRet.replace('$', '?').replace('(', '=').replace('|', '&');
        linkEdit = SynchronizerToken.updateTokenInURL(linkEdit, request);

        List<TransferObject> correspondentes = null;
        String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
        int filtro_tipo = -1;

        try {
            filtro_tipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
        } catch (Exception ex1) {
        }

        try {
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.COR_CSA_CODIGO, csaCodigo);

            // -------------- Seta Criterio da Listagem ------------------
            if (filtro_tipo == 0) {
                // Bloqueado
                List<Short> statusCor = new ArrayList<>();
                statusCor.add(CodedValues.STS_INATIVO);
                statusCor.add(CodedValues.STS_INATIVO_CSE);
                statusCor.add(CodedValues.STS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);
                criterio.setAttribute(Columns.COR_ATIVO, statusCor);
            } else if (filtro_tipo == 1) {
                // Desbloqueado
                criterio.setAttribute(Columns.COR_ATIVO, CodedValues.STS_ATIVO);
            } else if (!filtro.equals("") && filtro_tipo != -1) {
                // Outros
                String campo = null;

                switch (filtro_tipo) {
                    case 2:
                        campo = Columns.COR_IDENTIFICADOR;
                        break;
                    case 3:
                        campo = Columns.COR_NOME;
                        break;
                    default:
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);
            }
            if (filtro_tipo != 0 && filtro_tipo != 1) {
                List<Short> statusCor = new ArrayList<>();
                statusCor.add(CodedValues.STS_ATIVO);
                statusCor.add(CodedValues.STS_INATIVO);
                statusCor.add(CodedValues.STS_INATIVO_CSE);
                statusCor.add(CodedValues.STS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);
                criterio.setAttribute(Columns.COR_ATIVO, statusCor);
            }
            // ---------------------------------------

            int total = consignatariaController.countCorrespondentes(criterio, responsavel);
            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            correspondentes = consignatariaController.lstCorrespondentes(criterio, offset, size, responsavel);

            configurarPaginador("../v3/manterCorrespondente?acao=iniciar&csa=" + csaCodigo, "rotulo.correspondente.singular", total, size, null, false, request, model);

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            correspondentes = new ArrayList<>();
        }

        model.addAttribute("csa", csa);
        model.addAttribute("titulo", titulo);
        model.addAttribute("podeCriarUsu", podeCriarUsu);
        model.addAttribute("podeConsultarUsu", podeConsultarUsu);
        model.addAttribute("podeConsultarPerfilUsu", podeConsultarPerfilUsu);
        model.addAttribute("podeConsultarCnvCor", podeConsultarCnvCor);
        model.addAttribute("podeEditarCnvCor", podeEditarCnvCor);
        model.addAttribute("podeConsultarCor", podeConsultarCor);
        model.addAttribute("podeEditarCor", podeEditarCor);
        model.addAttribute("podeExcluirCor", podeExcluirCor);
        model.addAttribute("podeEditarEnderecoAcesso", podeEditarEnderecoAcesso);
        model.addAttribute("csa_codigo", csaCodigo);
        model.addAttribute("titulo", titulo);
        model.addAttribute("novoCorr", novoCorr);
        model.addAttribute("linkRet", linkRet);
        model.addAttribute("editaCorr", editaCorr);
        model.addAttribute("canc", canc);
        model.addAttribute("cadastraEmpCor", cadastraEmpCor);
        model.addAttribute("linkEdit", linkEdit);
        model.addAttribute("correspondentes", correspondentes);
        model.addAttribute("filtro", filtro);
        model.addAttribute("filtro_tipo", filtro_tipo);
        model.addAttribute("podeEditarEnderecosCor", podeEditarEnderecosCor);

        return viewRedirect("jsp/manterCorrespondente/listarCorrespondente", request, session, model, responsavel);

    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            CorrespondenteTransferObject corRem = new CorrespondenteTransferObject(request.getParameter("codigo"));
            ConsignatariaTransferObject validacaoCor = consignatariaController.findConsignatariaByCorrespondente(corRem.getCorCodigo(), responsavel);

            List<TransferObject> usuariosCor = usuarioController.listUsuarios("COR", request.getParameter("codigo"), null, -1, -1, responsavel);
            Iterator<TransferObject> it = usuariosCor.iterator();

            //Validaçao de segurança cenário CSA bloqueando/desbloqueando um Correspondente que não a pertence.
            if (responsavel.isCsa() && !validacaoCor.getCsaCodigo().equals(responsavel.getCodigoEntidade())) {
            	session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.correspondente.alteracoes.erro", responsavel));
            	return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
                while (it.hasNext()) {
                    CustomTransferObject next = (CustomTransferObject) it.next();
                    UsuarioTransferObject user = new UsuarioTransferObject(next.getAttribute(Columns.USU_CODIGO).toString());
                    usuarioController.removeUsuario(user, "COR", null, responsavel);
                }
                consignatariaController.removeCorrespondente(corRem, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.correspondente.excluido.sucesso", responsavel));

                ParamSession paramSession = ParamSession.getParamSession(session);
                request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                return "jsp/redirecionador/redirecionar";

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }

    @RequestMapping(params = { "acao=bloquear" })
    public String bloquear(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String ativo = request.getParameter("status");
            ativo = ativo.equals(CodedValues.STS_ATIVO.toString()) ? CodedValues.STS_INATIVO.toString() : CodedValues.STS_ATIVO.toString();
            String corCodigo = request.getParameter("codigo");
            CorrespondenteTransferObject corBloq = new CorrespondenteTransferObject(corCodigo);

            //Validaçao de segurança cenário CSA bloqueando/desbloqueando um Correspondente que não a pertence.
            ConsignatariaTransferObject validacaoCor = consignatariaController.findConsignatariaByCorrespondente(corBloq.getCorCodigo(), responsavel);
            if (responsavel.isCsa() && !validacaoCor.getCsaCodigo().equals(responsavel.getCodigoEntidade())) {
            	session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.correspondente.alteracoes.erro", responsavel));
            	return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            ParamSession paramSession = ParamSession.getParamSession(session);
            String linkVoltar = JspHelper.verificaVarQryStr(request, "link_voltar");
            String link = linkVoltar.equals("") ? paramSession.getLastHistory() : linkVoltar;
            link = SynchronizerToken.updateTokenInURL(link, request);

            boolean exigeMotivoOperacaoBloquearDesbloquear = isExigeMotivoOperacao(CodedValues.FUN_EDT_CORRESPONDENTES, responsavel);

            if (exigeMotivoOperacaoBloquearDesbloquear) {
                request.setAttribute("link_voltar", link);
                model.addAttribute("status",ativo);
                request.setAttribute("corCodigo",corCodigo);
                return inserirObsBloqueio(request, response, session, model);
            } else {
                corBloq.setCorAtivo(Short.valueOf(ativo));
                consignatariaController.updateCorrespondente(corBloq,true,null,null,responsavel);
            }


            session.setAttribute(CodedValues.MSG_INFO, (ativo.equals("1") ? ApplicationResourcesHelper.getMessage("mensagem.correspondente.desbloqueado.sucesso", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.correspondente.bloqueado.sucesso", responsavel)));
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=pesquisarEmpCorrespondente" })
    public String pesquisarEmpCorrespondente(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            SynchronizerToken.saveToken(request);

            String tipo = JspHelper.verificaVarQryStr(request, "tipo");
            String csa = JspHelper.verificaVarQryStr(request, "csa");
            String titulo = JspHelper.verificaVarQryStr(request, "titulo");
            String novo = JspHelper.verificaVarQryStr(request, "novo");

            model.addAttribute("tipo", tipo);
            model.addAttribute("csa", csa);
            model.addAttribute("titulo", titulo);
            model.addAttribute("podeConsultarUsu", novo);

            return viewRedirect("jsp/manterCorrespondente/pesquisarEmpCorrespondente", request, session, model, responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }

    @RequestMapping(params = { "acao=consultar" })
    public String consultar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            SynchronizerToken.saveToken(request);

            /* usuario */
            boolean podeCriarUsu = responsavel.temPermissao(CodedValues.FUN_CRIAR_USUARIOS_COR);
            boolean podeConsultarUsu = responsavel.temPermissao(CodedValues.FUN_CONS_USUARIOS_COR);
            /* correspondente */
            boolean podeConsultarCor = responsavel.temPermissao(CodedValues.FUN_CONS_CORRESPONDENTES);
            boolean podeEditarCor = responsavel.temPermissao(responsavel.isCor() ? CodedValues.FUN_EDT_CORRESPONDENTE : CodedValues.FUN_EDT_CORRESPONDENTES);
            boolean podeEditarCorBackup = podeEditarCor;
            boolean podeExcluirCor = responsavel.temPermissao(CodedValues.FUN_EXCL_CORRESPONDENTE);
            boolean podeEditarCnpj = true;
            boolean podeEditarEnderecosCor = responsavel.temPermissao(CodedValues.FUN_EDITAR_ENDERECOS_CORRESPONDENTE);

            /* perfil usuario */
            boolean podeConsultarPerfilUsu = responsavel.temPermissao(CodedValues.FUN_CONS_PERFIL_COR);

            // Verifica parâmetro de sistema se permite cadastro de ip interno
            boolean permiteCadIpInternoCsaCor = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_CAD_IP_REDE_INTERNA_CSA_COR, responsavel);

            boolean podeEditarEnderecoAcesso = responsavel.temPermissao(CodedValues.FUN_EDT_IP_ACESSO_COR);
            String periodoEnvioEmailAudit = (String) ParamSist.getInstance().getParam(CodedValues.TPC_PERIODO_ENVIO_EMAIL_AUDITORIA_CSA_COR, responsavel);
            String cor_codigo = null;
            if (responsavel.isCor()) {
                cor_codigo = responsavel.getCodigoEntidade();
            } else if (request.getParameter("cor") != null) {
                cor_codigo = JspHelper.verificaVarQryStr(request, "cor");
            }

            String titulo = ApplicationResourcesHelper.getMessage("rotulo.manutencao.correspondente.titulo", responsavel);
            String parametros = "acao=" + (podeEditarCor ? "editar" : (podeEditarEnderecoAcesso ? "editarIP" : "consultar")) + "&" + SynchronizerToken.generateToken4URL(request);
            String csa_codigo = responsavel.getCodigoEntidade();
            String codigoEntidade = JspHelper.verificaVarQryStr(request, "codigoEntidade");

            // Se o usuário não é da consignatária e nem correspondente
            if (!responsavel.isCor() && !responsavel.isCsa()) {
                csa_codigo = JspHelper.verificaVarQryStr(request, "csa");
                String subTitulo = JspHelper.verificaVarQryStr(request, "titulo");

                if (!subTitulo.equals("")) {
                    titulo += " - " + subTitulo.toUpperCase();
                }
            }

            if (TextHelper.isNull(codigoEntidade)) {
            	codigoEntidade = cor_codigo;
            }

            if (!TextHelper.isNull(codigoEntidade)) {
            	//Validaçao de segurança cenário CSA bloqueando/desbloqueando um Correspondente que não a pertence.
                ConsignatariaTransferObject validacaoCor = consignatariaController.findConsignatariaByCorrespondente(codigoEntidade, responsavel);
                if (responsavel.isCsa() && !validacaoCor.getCsaCodigo().equals(responsavel.getCodigoEntidade())) {
                	session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.correspondente.alteracoes.erro", responsavel));
                	return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            Object paramVrfIpAcesso = ParamSist.getInstance().getParam(CodedValues.TPC_VERIFICA_CADASTRO_IP_CSA_COR, responsavel);
            boolean cadastraEmpCorrespondente = ParamSist.getBoolParamSist(CodedValues.TPC_CADASTRO_EMPRESA_CORRESPONDENTE, responsavel);

            ParamSession paramSession = ParamSession.getParamSession(session);

            // Atualiza o correspondente
            String reqColumnsStr = "COR_IDENTIFICADOR|COR_NOME";
            String msgErro = JspHelper.verificaCamposForm(request, session, reqColumnsStr, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel), "100%");
            if (request.getParameter("MM_update") != null && msgErro.length() == 0) {
                String corIpAcesso = JspHelper.verificaVarQryStr(request, "cor_ip_acesso");
                try {
                    // Valida a lista de IPs.
                    if (!TextHelper.isNull(corIpAcesso)) {
                        List<String> ipsAcesso = Arrays.asList(corIpAcesso.split(";"));
                        if (!JspHelper.validaListaIps(ipsAcesso)) {
                            throw new ViewHelperException("mensagem.erro.ip.invalido", responsavel);
                        }
                    }

                    CorrespondenteTransferObject correspondente = null;
                    if (cor_codigo != null) {
                        correspondente = new CorrespondenteTransferObject(cor_codigo);
                    } else {
                        correspondente = new CorrespondenteTransferObject();
                        correspondente.setCsaCodigo(csa_codigo);
                        correspondente.setCorAtivo(CodedValues.STS_ATIVO);
                    }
                    correspondente.setCorNome(JspHelper.verificaVarQryStr(request, "COR_NOME"));
                    correspondente.setCorCnpj(JspHelper.verificaVarQryStr(request, "COR_CNPJ"));
                    correspondente.setCorEmail(JspHelper.verificaVarQryStr(request, "COR_EMAIL"));
                    correspondente.setCorResponsavel(JspHelper.verificaVarQryStr(request, "COR_RESPONSAVEL"));
                    correspondente.setCorResponsavel2(JspHelper.verificaVarQryStr(request, "COR_RESPONSAVEL_2"));
                    correspondente.setCorResponsavel3(JspHelper.verificaVarQryStr(request, "COR_RESPONSAVEL_3"));
                    correspondente.setCorRespCargo(JspHelper.verificaVarQryStr(request, "COR_RESP_CARGO"));
                    correspondente.setCorRespCargo2(JspHelper.verificaVarQryStr(request, "COR_RESP_CARGO_2"));
                    correspondente.setCorRespCargo3(JspHelper.verificaVarQryStr(request, "COR_RESP_CARGO_3"));
                    correspondente.setCorRespTelefone(JspHelper.verificaVarQryStr(request, "COR_RESP_TELEFONE"));
                    correspondente.setCorRespTelefone2(JspHelper.verificaVarQryStr(request, "COR_RESP_TELEFONE_2"));
                    correspondente.setCorRespTelefone3(JspHelper.verificaVarQryStr(request, "COR_RESP_TELEFONE_3"));
                    correspondente.setCorLogradouro(JspHelper.verificaVarQryStr(request, "COR_LOGRADOURO"));
                    if (!JspHelper.verificaVarQryStr(request, "COR_NRO").equals("")) {
                        correspondente.setCorNro(Integer.valueOf(JspHelper.verificaVarQryStr(request, "COR_NRO")));
                    } else {
                        correspondente.setCorNro(null);
                    }
                    correspondente.setCorCompl(JspHelper.verificaVarQryStr(request, "COR_COMPL"));
                    correspondente.setCorBairro(JspHelper.verificaVarQryStr(request, "COR_BAIRRO"));
                    correspondente.setCorCidade(JspHelper.verificaVarQryStr(request, "COR_CIDADE"));
                    correspondente.setCorUf(JspHelper.verificaVarQryStr(request, "COR_UF"));
                    correspondente.setCorCep(JspHelper.verificaVarQryStr(request, "COR_CEP"));
                    correspondente.setCorTel(JspHelper.verificaVarQryStr(request, "COR_TEL"));
                    correspondente.setCorFax(JspHelper.verificaVarQryStr(request, "COR_FAX"));
                    correspondente.setCorIdentificador(JspHelper.verificaVarQryStr(request, "COR_IDENTIFICADOR"));
                    if (podeEditarEnderecoAcesso) {
                        correspondente.setCorIPAcesso(corIpAcesso);
                        correspondente.setCorDDNSAcesso(JspHelper.verificaVarQryStr(request, "cor_ddns_acesso"));
                    }
                    String exigeEnderecoNew = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "cor_exige_endereco_acesso")) ? JspHelper.verificaVarQryStr(request, "cor_exige_endereco_acesso") : null;
                    String exigeEnderecoOld = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "cor_exige_endereco_acesso_old")) ? JspHelper.verificaVarQryStr(request, "cor_exige_endereco_acesso_old") : null;
                    if (exigeEnderecoNew != null) {
                        // Se o campo estava vazio e o parametro tambem esta vazio
                        // OU Se o campo estava vazio, mas o parametro esta preenchido com um valor diferente
                        // OU Se o campo estava preenchido com um valor diferente do novo
                        if ((exigeEnderecoOld == null && paramVrfIpAcesso == null) || (exigeEnderecoOld == null && paramVrfIpAcesso != null && !exigeEnderecoNew.equals(paramVrfIpAcesso)) || (exigeEnderecoOld != null && !exigeEnderecoNew.equals(exigeEnderecoOld))) {
                            correspondente.setCorExigeEnderecoAcesso(exigeEnderecoNew);
                        }
                    }

                    if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "ECO_CODIGO"))) {
                        correspondente.setEcoCodigo(JspHelper.verificaVarQryStr(request, "ECO_CODIGO"));
                    }

                    if (cor_codigo != null) {
                        CorrespondenteTransferObject correspondenteValidacao = consignatariaController.findCorrespondente(cor_codigo, responsavel) ;

                        // Validação de Segurança verificando se o correspondente é nulo e se ele pertence aquela CSA e se esta alterando de outros correspondentes
                        if(!TextHelper.isNull(correspondenteValidacao) && !correspondenteValidacao.getCsaCodigo().equals(csa_codigo) && !correspondenteValidacao.getCorCodigo().equals(cor_codigo)){

                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.correspondente.alteracoes.erro", responsavel));
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        }

                        consignatariaController.updateCorrespondente(correspondente, responsavel);
                    } else {
                        cor_codigo = consignatariaController.createCorrespondente(correspondente, responsavel);
                        // Colocando um endereço no paramSession, para caso o usuário entre nas ações do novo
                        // correspondente ele possa voltar ao lugar correto
                        Map<String, String[]> parameterMap = new HashMap<>();
                        parameterMap.put("csa", new String[] { csa_codigo });
                        parameterMap.put("cor", new String[] { cor_codigo });
                        parameterMap.put("titulo", new String[] { titulo });
                        parameterMap.put("acao", new String[] { "consultar" });
                        String link = request.getRequestURI();
                        paramSession.halfBack();
                        paramSession.addHistory(link, parameterMap);
                    }
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.correspondente.alteracoes.sucesso", responsavel));
                } catch (Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                }
            }

            String ecoCodigo = null;
            String cor_ip_acesso = "";
            String cor_ddns_acesso = "";
            String ecoCnpj = request.getParameter("ECO_CNPJ");
            CorrespondenteTransferObject correspondente = null;
            try {
                if (cor_codigo != null) {
                    correspondente = consignatariaController.findCorrespondente(cor_codigo, responsavel);
                    cor_ip_acesso = (correspondente.getCorIPAcesso() != null ? correspondente.getCorIPAcesso() : "");
                    cor_ddns_acesso = (correspondente.getCorDDNSAcesso() != null ? correspondente.getCorDDNSAcesso() : "");

                    if (!TextHelper.isNull(correspondente.getEcoCodigo()) && podeEditarCor) {
                        // podeEditarCor = !cadastraEmpCorrespondente;
                        // apenas campo CNPJ ficará não editável
                        podeEditarCnpj = !cadastraEmpCorrespondente;
                    }
                } else if (cadastraEmpCorrespondente && !TextHelper.isNull(ecoCnpj)) {
                    ecoCnpj = request.getParameter("ECO_CNPJ");

                    CustomTransferObject ecoTO = new CustomTransferObject();
                    ecoTO.setAttribute(Columns.ECO_CNPJ, ecoCnpj);

                    TransferObject empCor = consignatariaController.findEmpresaCorrespondente(ecoTO, responsavel);

                    if (empCor != null) {
                        correspondente = new CorrespondenteTransferObject();
                        correspondente.setCorAtivo((Short) empCor.getAttribute(Columns.ECO_ATIVO));
                        correspondente.setCorBairro((String) empCor.getAttribute(Columns.ECO_BAIRRO));
                        correspondente.setCorCep((String) empCor.getAttribute(Columns.ECO_CEP));
                        correspondente.setCorCidade((String) empCor.getAttribute(Columns.ECO_CIDADE));
                        correspondente.setCorCnpj((String) empCor.getAttribute(Columns.ECO_CNPJ));
                        correspondente.setCorCompl((String) empCor.getAttribute(Columns.ECO_COMPL));
                        correspondente.setCorEmail((String) empCor.getAttribute(Columns.ECO_EMAIL));
                        correspondente.setCorFax((String) empCor.getAttribute(Columns.ECO_FAX));
                        correspondente.setCorLogradouro((String) empCor.getAttribute(Columns.ECO_LOGRADOURO));
                        correspondente.setCorNome((String) empCor.getAttribute(Columns.ECO_NOME));
                        correspondente.setCorNro((Integer) empCor.getAttribute(Columns.ECO_NRO));
                        correspondente.setCorRespCargo((String) empCor.getAttribute(Columns.ECO_RESP_CARGO));
                        correspondente.setCorRespCargo2((String) empCor.getAttribute(Columns.ECO_RESP_CARGO_2));
                        correspondente.setCorRespCargo3((String) empCor.getAttribute(Columns.ECO_RESP_CARGO_3));
                        correspondente.setCorResponsavel((String) empCor.getAttribute(Columns.ECO_RESPONSAVEL));
                        correspondente.setCorResponsavel2((String) empCor.getAttribute(Columns.ECO_RESPONSAVEL_2));
                        correspondente.setCorResponsavel3((String) empCor.getAttribute(Columns.ECO_RESPONSAVEL_3));
                        correspondente.setCorRespTelefone((String) empCor.getAttribute(Columns.ECO_RESP_TELEFONE));
                        correspondente.setCorRespTelefone2((String) empCor.getAttribute(Columns.ECO_RESP_TELEFONE_2));
                        correspondente.setCorRespTelefone3((String) empCor.getAttribute(Columns.ECO_RESP_TELEFONE_3));
                        correspondente.setCorTel((String) empCor.getAttribute(Columns.ECO_TEL));
                        correspondente.setCorUf((String) empCor.getAttribute(Columns.ECO_UF));
                        correspondente.setCorIdentificador((String) empCor.getAttribute(Columns.ECO_IDENTIFICADOR));
                        correspondente.setEcoCodigo((String) empCor.getAttribute(Columns.ECO_CODIGO));

                        ecoCodigo = (String) empCor.getAttribute(Columns.ECO_CODIGO);

                        // se já houver empresa correspondente para o CNPJ pesquisado, seus dados são copiados para os
                        // campos do correspondente e estes se tornam não editáveis
                        // podeEditarCor = false;

                        // apenas campo CNPJ ficará não editável
                        podeEditarCnpj = false;
                    } else {
                        correspondente = new CorrespondenteTransferObject();
                        correspondente.setCorCnpj(ecoCnpj);
                        // apenas campo CNPJ ficará não editável
                        podeEditarCnpj = false;
                    }
                }
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String cor_ativo = (correspondente !=  null && correspondente.getCorAtivo() != null) ? correspondente.getCorAtivo().toString() : CodedValues.STS_ATIVO.toString();
            String cor_nome = (correspondente !=  null) ? correspondente.getCorNome() : "";

            model.addAttribute("podeCriarUsu", podeCriarUsu);
            model.addAttribute("podeConsultarUsu", podeConsultarUsu);
            model.addAttribute("podeConsultarCor", podeConsultarCor);
            model.addAttribute("podeEditarCor", podeEditarCor);
            model.addAttribute("podeEditarCorBackup", podeEditarCorBackup);
            model.addAttribute("podeExcluirCor", podeExcluirCor);
            model.addAttribute("podeEditarCnpj", podeEditarCnpj);
            model.addAttribute("podeConsultarPerfilUsu", podeConsultarPerfilUsu);
            model.addAttribute("permiteCadIpInternoCsaCor", permiteCadIpInternoCsaCor);
            model.addAttribute("podeEditarEnderecoAcesso", podeEditarEnderecoAcesso);
            model.addAttribute("cor_codigo", cor_codigo);
            model.addAttribute("titulo", titulo);
            model.addAttribute("parametros", parametros);
            model.addAttribute("csa_codigo", csa_codigo);
            model.addAttribute("paramVrfIpAcesso", paramVrfIpAcesso);
            model.addAttribute("msgErro", msgErro);
            model.addAttribute("ecoCodigo", ecoCodigo);
            model.addAttribute("cor_ip_acesso", cor_ip_acesso);
            model.addAttribute("cor_ddns_acesso", cor_ddns_acesso);
            model.addAttribute("ecoCnpj", cor_ddns_acesso);
            model.addAttribute("correspondente", correspondente);
            model.addAttribute("periodoEnvioEmailAudit", periodoEnvioEmailAudit);
            model.addAttribute("cor_ativo", cor_ativo);
            model.addAttribute("cor_nome", cor_nome);
            model.addAttribute("podeEditarEnderecosCor", podeEditarEnderecosCor);

            return viewRedirect("jsp/manterCorrespondente/editarCorrespondente", request, session, model, responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return consultar(request, response, session, model);

    }

    @RequestMapping(params = { "acao=editarIP" })
    public String editarIp(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return consultar(request, response, session, model);

    }

    @RequestMapping(params = { "acao=editarServico" })
    public String editarServico(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, Exception, ServicoControllerException, ParametroControllerException, SimulacaoControllerException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String csaCodigo = "";

        if (responsavel.isCseSup()) {
            csaCodigo = JspHelper.verificaVarQryStr(request, "csa_codigo");
        } else if (responsavel.isCsa()) {
            csaCodigo = responsavel.getCodigoEntidade();
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        CorrespondenteTransferObject corRem = new CorrespondenteTransferObject(request.getParameter("codigo"));

        String corCodigo = (responsavel.isCsa() ? corRem.getCorCodigo() : JspHelper.verificaVarQryStr(request, "cor_codigo"));
        String svcCodigo = JspHelper.verificaVarQryStr(request, "svc");
        String svcIdentificador = JspHelper.verificaVarQryStr(request, "SVC_IDENTIFICADOR");
        String svcDescricao = JspHelper.verificaVarQryStr(request, "SVC_DESCRICAO");

        // Busca os tipos de parâmetro de serviço disponíveis.
        List<TransferObject> tiposParams = parametroController.lstTipoParamSvc(responsavel);
        HashMap<Object, Boolean> parametrosSvc = new HashMap<>();
        Iterator<TransferObject> itParam = tiposParams.iterator();
        while (itParam.hasNext()) {
            CustomTransferObject paramSvc = (CustomTransferObject) itParam.next();
            if (responsavel.isCse()) {
                parametrosSvc.put(paramSvc.getAttribute(Columns.TPS_CODIGO), Boolean.valueOf(paramSvc.getAttribute(Columns.TPS_CSE_ALTERA) == null || paramSvc.getAttribute(Columns.TPS_CSE_ALTERA).equals("") || paramSvc.getAttribute(Columns.TPS_CSE_ALTERA).equals(CodedValues.TPC_SIM)));
            } else if (responsavel.isSup()) {
                parametrosSvc.put(paramSvc.getAttribute(Columns.TPS_CODIGO), Boolean.valueOf(paramSvc.getAttribute(Columns.TPS_SUP_ALTERA) == null || paramSvc.getAttribute(Columns.TPS_SUP_ALTERA).equals("") || paramSvc.getAttribute(Columns.TPS_SUP_ALTERA).equals(CodedValues.TPC_SIM)));
            } else if (responsavel.isCsa()) {
                parametrosSvc.put(paramSvc.getAttribute(Columns.TPS_CODIGO), Boolean.valueOf(paramSvc.getAttribute(Columns.TPS_CSA_ALTERA) != null && paramSvc.getAttribute(Columns.TPS_CSA_ALTERA).equals(CodedValues.TPC_SIM)));
            }
        }
        List<String> tpsCodigos = new ArrayList<>();
        if (parametrosSvc.containsKey(CodedValues.TPS_VLR_INTERVENIENCIA) && parametrosSvc.get(CodedValues.TPS_VLR_INTERVENIENCIA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_VLR_INTERVENIENCIA);
        }

        String valorInterveniencia = "", valorIntervenienciaRef = "", valorIntervenienciaPadrao = "", valorIntervenienciaRefPadrao = "";
        boolean temVlrIntervenienciaCor = false;

        // Parâmetro de interveniência CSE (default)
        List<TransferObject> paramTarif = parametroController.selectParamTarifCse(svcCodigo, responsavel);
        Iterator<TransferObject> itTarif = paramTarif.iterator();
        CustomTransferObject tarifa = null;
        while (itTarif.hasNext()) {
            tarifa = (CustomTransferObject) itTarif.next();
            if (tarifa.getAttribute(Columns.TPT_CODIGO).equals(CodedValues.TPT_VLR_INTERVENIENCIA) && tarifa.getAttribute(Columns.PCV_VLR) != null) {
                valorIntervenienciaPadrao = tarifa.getAttribute(Columns.PCV_VLR).toString();
                valorIntervenienciaRefPadrao = tarifa.getAttribute(Columns.PCV_FORMA_CALC).toString();
            }
        }

        // Parâmetro de interveniência CSA (sobrepoe a configuração para CSE)
        try {
            List<TransferObject> parametros = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigos, false, responsavel);
            Iterator<TransferObject> it2 = parametros.iterator();
            CustomTransferObject next = null;
            while (it2.hasNext()) {
                next = (CustomTransferObject) it2.next();
                if (next.getAttribute(Columns.TPS_CODIGO).equals(CodedValues.TPS_VLR_INTERVENIENCIA)) {
                    valorIntervenienciaPadrao = next.getAttribute(Columns.PSC_VLR).toString();
                    valorIntervenienciaRefPadrao = TextHelper.isNull(next.getAttribute(Columns.PSC_VLR_REF)) ? "1" : next.getAttribute(Columns.PSC_VLR_REF).toString();
                }
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        // Parâmetro de interveniência COR (sobrepoe a configuração para CSA e CSE)
        try {
            List<TransferObject> parametros = parametroController.selectParamSvcCor(svcCodigo, corCodigo, tpsCodigos, false, responsavel);
            Iterator<TransferObject> it2 = parametros.iterator();
            CustomTransferObject next = null;
            while (it2.hasNext()) {
                next = (CustomTransferObject) it2.next();
                if (next.getAttribute(Columns.TPS_CODIGO).equals(CodedValues.TPS_VLR_INTERVENIENCIA)) {
                    valorInterveniencia = next.getAttribute(Columns.PSO_VLR).toString();
                    valorIntervenienciaRef = TextHelper.isNull(next.getAttribute(Columns.PSO_VLR_REF)) ? "1" : next.getAttribute(Columns.PSO_VLR_REF).toString();
                    if (!valorInterveniencia.equals("")) {
                        temVlrIntervenienciaCor = true;
                    }
                }
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        ParamSession paramSession = ParamSession.getParamSession(session);
        String voltar = TextHelper.forJavaScriptAttribute(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request));
        model.addAttribute("voltar", voltar);

        model.addAttribute("csa_codigo", csaCodigo);
        model.addAttribute("cor_codigo", corCodigo);
        model.addAttribute("svc_codigo", svcCodigo);
        model.addAttribute("svc_identificador", svcIdentificador);
        model.addAttribute("svc_descricao", svcDescricao);
        model.addAttribute("parametrosSvc", parametrosSvc);
        model.addAttribute("valorInterveniencia", valorInterveniencia);
        model.addAttribute("valorIntervenienciaRef", valorIntervenienciaRef);
        model.addAttribute("valorIntervenienciaPadrao", valorIntervenienciaPadrao);
        model.addAttribute("valorIntervenienciaRefPadrao", valorIntervenienciaRefPadrao);
        model.addAttribute("TEM_VLR_INTERVENIENCIA_COR", temVlrIntervenienciaCor);
        model.addAttribute("tpsCodigos", tpsCodigos);

        if (tpsCodigos == null || tpsCodigos.isEmpty()) {
            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.manutencao.servico.cor.informacao.nenhum.parametro.encontrado", responsavel));
        }
        return viewRedirect("jsp/manterCorrespondente/editarServico", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvarServico" })
    public String salvarServico(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException, ServicoControllerException, ParametroControllerException, SimulacaoControllerException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String csaCodigo = "";

        if (responsavel.isCseSup()) {
            csaCodigo = JspHelper.verificaVarQryStr(request, "csa_codigo");
        } else if (responsavel.isCsa()) {
            csaCodigo = responsavel.getCodigoEntidade();
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        CorrespondenteTransferObject corRem = new CorrespondenteTransferObject(request.getParameter("codigo"));

        String corCodigo = (responsavel.isCsa() ? corRem.getCorCodigo() : JspHelper.verificaVarQryStr(request, "cor_codigo"));
        String svcCodigo = JspHelper.verificaVarQryStr(request, "svc");
        String svcIdentificador = JspHelper.verificaVarQryStr(request, "SVC_IDENTIFICADOR");
        String svcDescricao = JspHelper.verificaVarQryStr(request, "SVC_DESCRICAO");

        // Busca os tipos de parâmetro de serviço disponíveis.
        List<TransferObject> tiposParams = parametroController.lstTipoParamSvc(responsavel);
        HashMap<Object, Boolean> parametrosSvc = new HashMap<>();
        Iterator<TransferObject> itParam = tiposParams.iterator();
        while (itParam.hasNext()) {
            CustomTransferObject paramSvc = (CustomTransferObject) itParam.next();
            if (responsavel.isCse()) {
                parametrosSvc.put(paramSvc.getAttribute(Columns.TPS_CODIGO), Boolean.valueOf(paramSvc.getAttribute(Columns.TPS_CSE_ALTERA) == null || paramSvc.getAttribute(Columns.TPS_CSE_ALTERA).equals("") || paramSvc.getAttribute(Columns.TPS_CSE_ALTERA).equals(CodedValues.TPC_SIM)));
            } else if (responsavel.isSup()) {
                parametrosSvc.put(paramSvc.getAttribute(Columns.TPS_CODIGO), Boolean.valueOf(paramSvc.getAttribute(Columns.TPS_SUP_ALTERA) == null || paramSvc.getAttribute(Columns.TPS_SUP_ALTERA).equals("") || paramSvc.getAttribute(Columns.TPS_SUP_ALTERA).equals(CodedValues.TPC_SIM)));
            } else if (responsavel.isCsa()) {
                parametrosSvc.put(paramSvc.getAttribute(Columns.TPS_CODIGO), Boolean.valueOf(paramSvc.getAttribute(Columns.TPS_CSA_ALTERA) != null && paramSvc.getAttribute(Columns.TPS_CSA_ALTERA).equals(CodedValues.TPC_SIM)));
            }
        }
        List<String> tpsCodigos = new ArrayList<>();
        if (parametrosSvc.containsKey(CodedValues.TPS_VLR_INTERVENIENCIA) && parametrosSvc.get(CodedValues.TPS_VLR_INTERVENIENCIA).booleanValue()) {
            tpsCodigos.add(CodedValues.TPS_VLR_INTERVENIENCIA);
        }

        if (!svcCodigo.equals("")) {
            // Salva os parâmetros do serviço
            try {
                List<TransferObject> parametros = new ArrayList<>();
                List<TransferObject> tpsCodigosIgualCsa = new ArrayList<>();
                for (int i = 0; i < tpsCodigos.size(); i++) {
                    CustomTransferObject cto = new CustomTransferObject();
                    String psc_vlr = JspHelper.verificaVarQryStr(request, "tps_" + tpsCodigos.get(i));
                    String psc_vlr_ref = "";
                    if (tpsCodigos.get(i).equals(CodedValues.TPS_VLR_INTERVENIENCIA)) {
                        if (!psc_vlr.equals("")) {
                            psc_vlr = NumberHelper.reformat(psc_vlr, NumberHelper.getLang(), "en");
                            psc_vlr_ref = JspHelper.verificaVarQryStr(request, "tps_" + tpsCodigos.get(i) + "_REF");
                        }
                    }
                    cto.setAttribute(Columns.PSO_TPS_CODIGO, tpsCodigos.get(i));
                    cto.setAttribute(Columns.PSO_SVC_CODIGO, svcCodigo);
                    cto.setAttribute(Columns.PSO_COR_CODIGO, corCodigo);
                    cto.setAttribute(Columns.PSO_VLR_REF, psc_vlr_ref);
                    cto.setAttribute(Columns.PSO_VLR, !psc_vlr.equals("") ? psc_vlr : "");
                    parametros.add(cto);

                    if (JspHelper.verificaVarQryStr(request, "check_" + tpsCodigos.get(i)).equals("1") || cto.getAttribute(Columns.PSO_VLR).equals("")) {
                        tpsCodigosIgualCsa.add(cto);
                    }
                }

                parametroController.updateParamSvcCor(parametros, responsavel);
                parametroController.deleteParamIgualCsa(tpsCodigosIgualCsa, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alteracoes.salvas.sucesso", responsavel));
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }

        String permanecerNaPaginaEditarServico = "../v3/manterCorrespondente?acao=editarServico&svc=" + svcCodigo + "&SVC_IDENTIFICADOR=" + svcIdentificador + "&SVC_DESCRICAO=" + svcDescricao + "&cor_codigo=" + corCodigo +  "&csa_codigo=" + csaCodigo;
        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(permanecerNaPaginaEditarServico, request)));
        return "jsp/redirecionador/redirecionar";
    }

    @RequestMapping(params = { "acao=inserirObsBloqueio" })
    public String inserirObsBloqueio(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ConsignanteControllerException, ConsignatariaControllerException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        ParamSession paramSession = ParamSession.getParamSession(session);
        String linkVoltar = JspHelper.verificaVarQryStr(request, "link_voltar");
        linkVoltar = linkVoltar.equals("") ? paramSession.getLastHistory() : linkVoltar;
        linkVoltar = SynchronizerToken.updateTokenInURL(linkVoltar, request);
        boolean bloqueado = !request.getParameter("status").equals(CodedValues.STS_ATIVO.toString());

        List<TransferObject> tiposMotivoOperacao = null;
        List<String> tenCodigos = new ArrayList<>();
        tenCodigos.add(Log.GERAL);
        tenCodigos.add(Log.CORRESPONDENTE);

        try {
            tiposMotivoOperacao = tipoMotivoOperacaoController.lstMotivoOperacao(tenCodigos, null, responsavel);
        } catch (TipoMotivoOperacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }

        model.addAttribute("bloqueado", bloqueado);
        model.addAttribute("linkVoltar", linkVoltar);
        model.addAttribute("corCodigo", request.getAttribute("corCodigo"));
        model.addAttribute("tiposMotivoOperacao", tiposMotivoOperacao);

        return viewRedirect("jsp/manterCorrespondente/inserirObservacaoBloqueioCor", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvarMotivoBloqueioDesbloqueio" })
    public String salvarMotivoBloqueioDesbloqueio(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model){

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String corCodigo = JspHelper.verificaVarQryStr(request, "cor_codigo");
        String[] tmoCodigoSplit = JspHelper.verificaVarQryStr(request, "tmoCodigo").split(";");
        String tmoCodigo = tmoCodigoSplit[0];
        String ocrObs = JspHelper.verificaVarQryStr(request, "OCR_OBS");
        String status = JspHelper.verificaVarQryStr(request, "status");

        try {
            TipoMotivoOperacaoTransferObject tipoMotivoOperacaoTransferObject = new TipoMotivoOperacaoTransferObject(tmoCodigo);
            tipoMotivoOperacaoTransferObject = tipoMotivoOperacaoController.findMotivoOperacao(tipoMotivoOperacaoTransferObject, responsavel);

            if(tipoMotivoOperacaoTransferObject.getTmoExigeObs().equals("S") && TextHelper.isNull(ocrObs)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.obrigatoriedade.observacao.motivo.operacao", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            CorrespondenteTransferObject corBloq = new CorrespondenteTransferObject(corCodigo);
            corBloq.setCorAtivo(Short.valueOf(status));
            consignatariaController.updateCorrespondente(corBloq,true,tmoCodigo,ocrObs,responsavel);

        } catch (TipoMotivoOperacaoControllerException | ConsignatariaControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }

        session.setAttribute(CodedValues.MSG_INFO, (status.equals("1") ? ApplicationResourcesHelper.getMessage("mensagem.correspondente.desbloqueado.sucesso", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.correspondente.bloqueado.sucesso", responsavel)));
        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(JspHelper.verificaVarQryStr(request, "link_voltar"), request)));
        return "jsp/redirecionador/redirecionar";    }
}
