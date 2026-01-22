package com.zetra.econsig.web.controller.estabelecimento;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.entidade.ServicoTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.service.usuario.UsuarioControllerBean;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>
 * Title: ManterEstabelecimentoWebController
 * </p>
 * <p>
 * Description: Controlador Web para manter estabelecimento.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002-2017
 * </p>
 * <p>
 * Company: ZetraSoft
 * </p>
 * $Author$ $Revision$ $Date: 2018-05-17 14:28:30 -0300
 * (Ter, 17 mai 2018) $
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/manterEstabelecimento" })
public class ManterEstabelecimentoWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterEstabelecimentoWebController.class);

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private UsuarioControllerBean usuarioControllerBean;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            SynchronizerToken.saveToken(request);

            final boolean podeExcluirEst = responsavel.temPermissao(CodedValues.FUN_EXCL_ESTABELECIMENTO);
            final boolean podeEditarEst = responsavel.temPermissao(CodedValues.FUN_EDT_ESTABELECIMENTOS);
            final boolean podeConsultarEst = responsavel.temPermissao(CodedValues.FUN_CONS_ESTABELECIMENTOS);

            List<TransferObject> estabelecimentos = null;

            final String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
            int filtro_tipo = -1;
            try {
                filtro_tipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
            } catch (final Exception ex1) {
            }

            try {
                final CustomTransferObject criterio = new CustomTransferObject();

                // -------------- Seta Criterio da Listagem ------------------
                // Bloqueado
                if (filtro_tipo == 0) {
                    criterio.setAttribute(Columns.EST_ATIVO, CodedValues.STS_INATIVO);
                    // Desbloqueado
                } else if (filtro_tipo == 1) {
                    criterio.setAttribute(Columns.EST_ATIVO, CodedValues.STS_ATIVO);
                    // Outros
                } else if (!"".equals(filtro) && (filtro_tipo != -1)) {
                    String campo = null;

                    switch (filtro_tipo) {
                        case 2:
                            campo = Columns.EST_IDENTIFICADOR;
                            break;
                        case 3:
                            campo = Columns.EST_NOME;
                            break;
                        default:
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }

                    criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);
                }
                // ---------------------------------------

                final int total = consignanteController.countEstabelecimentos(criterio, responsavel);
                final int size = JspHelper.LIMITE;
                int offset = 0;
                try {
                    offset = Integer.parseInt(request.getParameter("offset"));
                } catch (final Exception ex) {
                }

                estabelecimentos = consignanteController.lstEstabelecimentos(criterio, offset, size, responsavel);

                // Monta lista de parâmetros através dos parâmetros de request
                final Set<String> params = new HashSet<>(request.getParameterMap().keySet());

                // Ignora os parâmetros abaixo
                params.remove("senha");
                params.remove("serAutorizacao");
                params.remove("cryptedPasswordFieldName");
                params.remove("offset");
                params.remove("back");
                params.remove("linkRet");
                params.remove("linkRet64");
                params.remove("eConsig.page.token");
                params.remove("_skip_history_");
                params.remove("pager");
                params.remove("acao");

                final List<String> requestParams = new ArrayList<>(params);

                final String linkAction = "../v3/manterEstabelecimento?acao=iniciar";
                configurarPaginador(linkAction, "rotulo.paginacao.titulo.estabelecimento", total, size, requestParams, false, request, model);
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                estabelecimentos = new ArrayList<>();
            }

            model.addAttribute("podeEditarEst", podeEditarEst);
            model.addAttribute("filtro_tipo", filtro_tipo);
            model.addAttribute("filtro", filtro);
            model.addAttribute("podeExcluirEst", podeExcluirEst);
            model.addAttribute("podeConsultarEst", podeConsultarEst);
            model.addAttribute("estabelecimentos", estabelecimentos);

            return viewRedirect("jsp/manterEstabelecimento/listarEstabelecimento", request, session, model, responsavel);

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }

    @RequestMapping(params = { "acao=consultar" })
    public String consultar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return editar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            final String est_codigo = request.getParameter("est");

            // Atualiza o estabelecimento
            final boolean podeEditar = responsavel.temPermissao(CodedValues.FUN_EDT_ESTABELECIMENTOS);

            EstabelecimentoTransferObject estabelecimento = null;
            try {
                if (est_codigo != null) {
                    estabelecimento = consignanteController.findEstabelecimento(est_codigo, responsavel);
                }
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final boolean habilitarCodigoFolha = ParamSist.paramEquals(CodedValues.TPC_HABILITAR_EDICAO_CODIGO_FOLHA, CodedValues.TPC_SIM, responsavel);

            model.addAttribute("podeEditar", podeEditar);
            model.addAttribute("habilitarCodigoFolha", habilitarCodigoFolha);

            if (est_codigo != null) {
                model.addAttribute("estabelecimento", estabelecimento);
                model.addAttribute("est_codigo", est_codigo);
                return viewRedirect("jsp/manterEstabelecimento/editarEstabelecimento", request, session, model, responsavel);
            }

            if(ParamSist.paramEquals(CodedValues.TPC_CADASTRO_SIMPLIFICADO_ESTABELECIMENTO, CodedValues.TPC_SIM, responsavel)) {
                return viewRedirect("jsp/manterEstabelecimento/criarEstabelecimentoSimplificado", request, session, model, responsavel);
            }

            return viewRedirect("jsp/manterEstabelecimento/editarEstabelecimento", request, session, model, responsavel);

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        try {

            final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final String reqColumnsStr = "EST_IDENTIFICADOR|EST_NOME|EST_CNPJ";
            final String msgErro = JspHelper.verificaCamposForm(request, session, reqColumnsStr, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel), "100%");

            if (msgErro.length() == 0) {
                final ParamSession paramSession = ParamSession.getParamSession(session);

                String est_codigo = request.getParameter("est");

                EstabelecimentoTransferObject estabelecimento = null;

                if (est_codigo != null) {
                    estabelecimento = new EstabelecimentoTransferObject(est_codigo);
                    estabelecimento.setEstIdentificador(JspHelper.verificaVarQryStr(request, "EST_IDENTIFICADOR"));
                } else {
                    estabelecimento = new EstabelecimentoTransferObject();
                    estabelecimento.setCseCodigo(CodedValues.CSE_CODIGO_SISTEMA);
                    if(ParamSist.paramEquals(CodedValues.TPC_CADASTRO_SIMPLIFICADO_ESTABELECIMENTO, CodedValues.TPC_SIM, responsavel)) {
                        final int estIdentificadorInt = (Integer.parseInt(consignanteController.findEstabelecimento(estabelecimento, responsavel).getEstIdentificador()) + 1);
                        final String estIdentificador = estIdentificadorInt < 10 ? 0 + Integer.toString(estIdentificadorInt) : Integer.toString(estIdentificadorInt);
                        estabelecimento.setEstIdentificador(estIdentificador);
                    } else {
                        estabelecimento.setEstIdentificador(JspHelper.verificaVarQryStr(request, "EST_IDENTIFICADOR"));
                    }

                }

                estabelecimento.setEstNome(JspHelper.verificaVarQryStr(request, "EST_NOME"));

                if ("".equals(JspHelper.verificaVarQryStr(request, "EST_CNPJ"))) {
                    estabelecimento.setEstCnpj(null);
                } else {
                    estabelecimento.setEstCnpj(JspHelper.verificaVarQryStr(request, "EST_CNPJ"));
                }

                estabelecimento.setEstResponsavel(JspHelper.verificaVarQryStr(request, "EST_RESPONSAVEL"));
                estabelecimento.setEstResponsavel2(JspHelper.verificaVarQryStr(request, "EST_RESPONSAVEL_2"));
                estabelecimento.setEstResponsavel3(JspHelper.verificaVarQryStr(request, "EST_RESPONSAVEL_3"));
                estabelecimento.setEstRespCargo(JspHelper.verificaVarQryStr(request, "EST_RESP_CARGO"));
                estabelecimento.setEstRespCargo2(JspHelper.verificaVarQryStr(request, "EST_RESP_CARGO_2"));
                estabelecimento.setEstRespCargo3(JspHelper.verificaVarQryStr(request, "EST_RESP_CARGO_3"));
                estabelecimento.setEstRespTelefone(JspHelper.verificaVarQryStr(request, "EST_RESP_TELEFONE"));
                estabelecimento.setEstRespTelefone2(JspHelper.verificaVarQryStr(request, "EST_RESP_TELEFONE_2"));
                estabelecimento.setEstRespTelefone3(JspHelper.verificaVarQryStr(request, "EST_RESP_TELEFONE_3"));
                estabelecimento.setEstLogradouro(JspHelper.verificaVarQryStr(request, "EST_LOGRADOURO"));

                if (!"".equals(JspHelper.verificaVarQryStr(request, "EST_NRO"))) {
                    estabelecimento.setEstNro(Integer.valueOf(JspHelper.verificaVarQryStr(request, "EST_NRO")));
                } else {
                    estabelecimento.setEstNro(null);
                }

                estabelecimento.setEstCompl(JspHelper.verificaVarQryStr(request, "EST_COMPL"));
                estabelecimento.setEstBairro(JspHelper.verificaVarQryStr(request, "EST_BAIRRO"));
                estabelecimento.setEstCidade(JspHelper.verificaVarQryStr(request, "EST_CIDADE"));
                estabelecimento.setEstUf(JspHelper.verificaVarQryStr(request, "EST_UF"));
                estabelecimento.setEstCep(JspHelper.verificaVarQryStr(request, "EST_CEP"));
                estabelecimento.setEstTel(JspHelper.verificaVarQryStr(request, "EST_TEL"));
                estabelecimento.setEstFax(JspHelper.verificaVarQryStr(request, "EST_FAX"));
                estabelecimento.setEstEmail(JspHelper.verificaVarQryStr(request, "EST_EMAIL"));
                estabelecimento.setEstFolha(JspHelper.verificaVarQryStr(request, "EST_FOLHA"));

                if (est_codigo != null) {
                    consignanteController.updateEstabelecimento(estabelecimento, responsavel);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.estabelecimento.alterado.sucesso", responsavel));
                } else {
                    estabelecimento.setEstAtivo(Short.valueOf("1"));

                    if (ParamSist.paramEquals(CodedValues.TPC_CADASTRO_SIMPLIFICADO_ESTABELECIMENTO, CodedValues.TPC_SIM, responsavel)) {
                        if(TextHelper.isNull(estabelecimento.getEstEmail())) {
                            throw new UsuarioControllerException("mensagem.erro.email.valido.deve.ser.cadastrado.para.atribuir.permissao.auditor.ao.usuario", responsavel);
                        }
                        // Verifica se não existe outro usuário com o mesmo login
                        final UsuarioTransferObject usuario = new UsuarioTransferObject();
                        usuario.setUsuLogin(estabelecimento.getEstEmail());

                        boolean existe = false;
                        try {
                            usuarioControllerBean.findUsuarioByLogin(usuario.getUsuLogin(), responsavel);
                            existe = true;
                        } catch (final UsuarioControllerException ex) {
                            // OK, nenhum usuário encontrato com o login informado
                        }
                        if (existe) {
                            throw new UsuarioControllerException("mensagem.erro.nao.possivel.criar.este.usuario.existe.outro.mesmo.login.cadastrado.sistema", responsavel);
                        }
                        est_codigo = consignanteController.createEstabelecimento(estabelecimento, responsavel);
                        final String usuCpf = JspHelper.verificaVarQryStr(request, "USU_CPF");
                        final String orgCodigo = createOrgaoEstabelecimentoSimplificado(estabelecimento, est_codigo, usuCpf, responsavel);
                        createConveniosOrgao(orgCodigo, responsavel);
                        model.addAttribute("usu_cpf", usuCpf);
                    } else {
                        est_codigo = consignanteController.createEstabelecimento(estabelecimento, responsavel);
                    }
                    // Colocando um endereço no paramSession
                    final Map<String, String[]> parametros = new HashMap<>();
                    parametros.put("est", new String[] { est_codigo });
                    final String link = request.getRequestURI();
                    paramSession.addHistory(link, parametros);
                    model.addAttribute("estabelecimento", estabelecimento);
                    model.addAttribute("est_codigo", est_codigo);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.estabelecimento.criado.sucesso", responsavel));
                }
            }
            model.addAttribute("msgErro", msgErro);

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        return editar(request, response, session, model);

    }

    private String createOrgaoEstabelecimentoSimplificado(EstabelecimentoTransferObject estabelecimento, String estCodigo, String usuCpf, AcessoSistema responsavel) throws ConsignanteControllerException {
        final OrgaoTransferObject orgao = new OrgaoTransferObject();
        orgao.setOrgIdentificador(estabelecimento.getEstIdentificador());
        orgao.setOrgNome(estabelecimento.getEstNome());
        orgao.setOrgEmail(estabelecimento.getEstEmail());
        orgao.setEstCodigo(estCodigo);


        try {
            final String orgCodigo = consignanteController.createOrgao(orgao, responsavel);

            final List<String> funCodigo = new ArrayList<>();

            // Lista as permissões permitidas baseado em natureza, entre outras coisas
            final List<TransferObject> funcoesPermitidas = usuarioController.lstFuncoesPermitidasPerfil(AcessoSistema.ENTIDADE_ORG, orgao.getOrgCodigo(), responsavel);
            final Iterator<TransferObject> it = funcoesPermitidas.iterator();
            CustomTransferObject custom;
            while (it.hasNext()) {
                custom = (CustomTransferObject) it.next();
                funCodigo.add(custom.getAttribute(Columns.FUN_CODIGO).toString());
            }
            //Cria o perfil de usuário master
            final String perfil = usuarioController.createPerfil(AcessoSistema.ENTIDADE_ORG, orgCodigo, "MASTER", null, null, null, null, null, funCodigo, null, null, responsavel);

            //Cria o usuário
            createUsuario(perfil, usuCpf, orgao, orgCodigo, responsavel);

            return orgCodigo;

        } catch (final UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException(ex);
        }

    }

    private String createUsuario(String perfil, String usuCpf, OrgaoTransferObject orgao, String orgCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.YEAR, 2099);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        final java.sql.Date data = new java.sql.Date(cal.getTimeInMillis());

        final String usuSenha = null;

        final UsuarioTransferObject usuarioTransferObject = new UsuarioTransferObject();
        usuarioTransferObject.setStuCodigo(CodedValues.STU_BLOQUEADO);
        final String login = !TextHelper.isNull(orgao.getOrgEmail()) ? orgao.getOrgEmail() : "MASTER" + orgao.getOrgIdentificador();
        usuarioTransferObject.setUsuLogin(login);
        usuarioTransferObject.setUsuSenha(usuSenha);
        usuarioTransferObject.setUsuSenha2(null);
        usuarioTransferObject.setUsuNome(orgao.getOrgNome());
        usuarioTransferObject.setUsuEmail(orgao.getOrgEmail());
        usuarioTransferObject.setUsuTel(null);
        usuarioTransferObject.setUsuDicaSenha(null);
        usuarioTransferObject.setUsuTipoBloq(null);
        usuarioTransferObject.setUsuDataExpSenha(data);
        usuarioTransferObject.setUsuDataExpSenha2(null);
        usuarioTransferObject.setUsuIpAcesso(null);
        usuarioTransferObject.setUsuDDNSAcesso(null);
        usuarioTransferObject.setUsuCPF(usuCpf);
        usuarioTransferObject.setUsuCentralizador("N");
        usuarioTransferObject.setUsuExigeCertificado(null);
        usuarioTransferObject.setUsuMatriculaInst(null);
        usuarioTransferObject.setUsuChaveRecuperarSenha(null);
        usuarioTransferObject.setUsuDataFimVig(null);
        usuarioTransferObject.setUsuDeficienteVisual(null);

        return usuarioController.createUsuario(usuarioTransferObject, perfil, orgCodigo, AcessoSistema.ENTIDADE_ORG, null, false, usuSenha, false, responsavel);

    }

    private void createConveniosOrgao(String orgCodigo, AcessoSistema responsavel) throws ConvenioControllerException {
        final TransferObject criterio = new ServicoTransferObject();
        final List<TransferObject> servicos = convenioController.lstServicos(criterio, responsavel);
        for (final TransferObject to : servicos) {
            final String svcCodigo = to.getAttribute(Columns.SVC_CODIGO).toString();
            convenioController.createConvenio(svcCodigo, null, orgCodigo, null, null, null, responsavel);
        }
    }

    @RequestMapping(params = { "acao=deletar" })
    public String deletar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            try {
                final EstabelecimentoTransferObject estRem = new EstabelecimentoTransferObject(request.getParameter("codigo"));
                consignanteController.removeEstabelecimento(estRem, responsavel);

                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.estabelecimento.excluido.sucesso", responsavel));

            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }

            return iniciar(request, response, session, model);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }

    @RequestMapping(params = { "acao=ativarDesativar" })
    public String ativarDesativar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final ParamSession paramSession = ParamSession.getParamSession(session);
        try {

            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            try {
                String ativo = request.getParameter("status");
                ativo = "1".equals(ativo) ? "0" : "1";
                final EstabelecimentoTransferObject estBloq = new EstabelecimentoTransferObject(request.getParameter("codigo"));
                estBloq.setEstAtivo(Short.valueOf(ativo));
                consignanteController.updateEstabelecimento(estBloq, responsavel);

                // seta mensagem de sucesso
                if ("0".equals(ativo)) {
                    // bloqueado
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.estabelecimento.status.bloqueado", responsavel));
                } else {
                    // desbloqueado
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.estabelecimento.status.desbloqueado", responsavel));
                }
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }
}
