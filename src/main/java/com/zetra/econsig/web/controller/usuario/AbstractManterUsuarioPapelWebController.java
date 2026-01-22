package com.zetra.econsig.web.controller.usuario;

import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.EnderecoFuncaoTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.senha.GeradorSenhaUtil;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webclient.sso.SSOClient;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: AbstractManterUsuarioPapelWebController</p>
 * <p>Description: Controlador Web base para o caso de uso Manter Usuário por papel.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class AbstractManterUsuarioPapelWebController extends AbstractEfetivarAcaoUsuarioWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AbstractManterUsuarioPapelWebController.class);

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ServicoController servicoController;
    
    @Autowired
    private ParametroController parametroController;
    
    @Lazy(true)
    @Autowired
    private SSOClient ssoClient;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final boolean cpfObrigatorio = ParamSist.getBoolParamSist(CodedValues.TPC_CADASTRO_CPF_OBRIGATORIO_USUARIO, responsavel);

            // Exige tipo de motivo da operacao
            final boolean exigeMotivoOperacaoUsu = ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_TIPO_MOTIVO_OPERACAO_USUARIO, responsavel);
            final boolean exibeMotivoOperacao = (exigeMotivoOperacaoUsu && FuncaoExigeMotivo.getInstance().exists(responsavel.getFunCodigo(), responsavel));

            boolean podeEditarUsuDeficienteVisual = false;
            if (responsavel.isSup()) {
                podeEditarUsuDeficienteVisual = true;
            }

            boolean bloqueiaEdicaoEmail = ParamSist.getBoolParamSist(CodedValues.TPC_BLOQUEIA_EDICAO_EMAIL, responsavel);
            if (responsavel.isSup()) {
                bloqueiaEdicaoEmail = false;
            }

            final String codigo = getCodigoEntidade(request);
            final String tipo = getTipoEntidade();
            final String titulo = getTituloPagina(request, responsavel);
            final String usuCodigo = JspHelper.verificaVarQryStr(request, "usu_codigo");

            try {
                validaTipoEntidade(request, responsavel);
            } catch (final ZetraException e) {
                // Redireciona para página de erro
                session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (!TextHelper.isNull(usuCodigo)) {
                // Validação para a escalação de usuário
                final List<TransferObject> usuarios = usuarioController.listUsuarios(tipo, codigo, null, -1, -1, responsavel);
                boolean usuarioExiste = false;
                for (final TransferObject validaUsuario : usuarios) {
                    if (usuCodigo.equals(validaUsuario.getAttribute(Columns.USU_CODIGO))) {
                        usuarioExiste = true;
                        break;
                    }
                }

                if (!usuarioExiste) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            String arr_p = "";
            Map<String, Object> funcoesSession = new HashMap<>();
            try {
                if (request.getParameterValues("funcao") != null) {
                    final List<String> list = Arrays.asList(request.getParameterValues("funcao"));
                    for (final String e : list) {
                        funcoesSession.put(e, e);
                    }
                } else {
                    funcoesSession = new HashMap<>(usuarioController.selectFuncoes(usuCodigo, codigo, tipo, responsavel));
                }
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            if (funcoesSession.size() != 0) {
                arr_p = "'" + TextHelper.join(funcoesSession.keySet().toArray(), "','") + "'";
            } else {
                arr_p = "";
            }

            List<String> usuFunCodigos = null;
            List<TransferObject> funcoes = null;
            List<TransferObject> perfis = null;

            String stu_codigo = "";
            String usu_dica_senha = "";
            String usu_email = "";
            String usu_tel = "";
            String usu_login = "";
            String usu_nome = "";
            String usu_ip_acesso = "";
            String usu_ddns_acesso = "";
            String usu_cpf = "";
            String usu_centralizador = "";
            String usu_autentica_sso = "";
            String usu_visivel = "";
            String usu_exige_certificado = "";
            String usu_matricula_inst = "";
            String usu_deficiente_visual = "";
            String perfil = "";
            String usu_permite_validacao_totp = "";
            Integer usu_qtd_consultas_margem = null;
            Date usu_data_fim_vig = null;

            boolean podeEditarUsuExigeCertificado = false;
            try {
                podeEditarUsuExigeCertificado = (UsuarioHelper.isUsuarioCertificadoDigital(usu_login, "S", tipo, codigo, responsavel) != UsuarioHelper.isUsuarioCertificadoDigital(usu_login, "N", tipo, codigo, responsavel));
            } catch (final ViewHelperException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            try {
                // Inclusão não precisa pesquisar usuário
                if (!TextHelper.isNull(usuCodigo)) {
                    usuFunCodigos = usuarioController.getUsuarioFuncoes(usuCodigo, tipo, responsavel);
                    final UsuarioTransferObject usuario = usuarioController.findUsuario(usuCodigo, responsavel);
                    stu_codigo = usuario.getStuCodigo();
                    usu_login = CodedValues.STU_EXCLUIDO.equals(usuario.getStuCodigo()) ? usuario.getUsuTipoBloq() + "(*)" : usuario.getUsuLogin();
                    usu_nome = usuario.getUsuNome();
                    usu_cpf = usuario.getUsuCPF();
                    usu_email = usuario.getUsuEmail() == null ? "" : usuario.getUsuEmail();
                    usu_tel = usuario.getUsuTel() == null ? "" : usuario.getUsuTel();
                    usu_dica_senha = usuario.getUsuDicaSenha() == null ? "" : usuario.getUsuDicaSenha();
                    usu_ip_acesso = (usuario.getUsuIpAcesso() != null ? usuario.getUsuIpAcesso() : "");
                    usu_ddns_acesso = (usuario.getUsuDDNSAcesso() != null ? usuario.getUsuDDNSAcesso() : "");
                    usu_centralizador = (usuario.getUsuCentralizador() != null ? (String) usuario.getUsuCentralizador() : "");
                    usu_autentica_sso = (usuario.getUsuAutenticaSso() != null ? (String) usuario.getUsuAutenticaSso() : "");
                    usu_visivel = (usuario.getUsuVisivel() != null ? (String) usuario.getUsuVisivel() : "");
                    usu_exige_certificado = podeEditarUsuExigeCertificado ? usuario.getUsuExigeCertificado() : CodedValues.TPC_NAO;
                    usu_matricula_inst = usuario.getUsuMatriculaInst() == null ? "" : usuario.getUsuMatriculaInst();
                    perfil = usuarioController.findUsuarioPerfil(usuCodigo, responsavel);
                    usu_data_fim_vig = usuario.getUsuDataFimVig();
                    usu_deficiente_visual = usuario.getUsuDeficienteVisual() != null ? usuario.getUsuDeficienteVisual() : "";
                    usu_permite_validacao_totp = usuario.getUsuPermiteValidacaoTotp() != null ? usuario.getUsuPermiteValidacaoTotp() : "";
                    usu_qtd_consultas_margem = usuario.getUsuQtdConsultasMargem();

                    if (CodedValues.TPC_SIM.equals(usu_centralizador) && !responsavel.isSup()) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.centralizador.edicao.somente.gestor", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                }

                // Recupera alterações do formulário para criação ou edição de usuário
                if (!"".equals(JspHelper.verificaVarQryStr(request, "USU_LOGIN"))) {
                    usu_login = JspHelper.verificaVarQryStr(request, "USU_LOGIN");
                    usu_nome = JspHelper.verificaVarQryStr(request, "USU_NOME");
                    usu_cpf = JspHelper.verificaVarQryStr(request, "USU_CPF");
                    usu_email = JspHelper.verificaVarQryStr(request, "USU_EMAIL");
                    usu_tel = JspHelper.verificaVarQryStr(request, "USU_TEL");
                    usu_dica_senha = JspHelper.verificaVarQryStr(request, "USU_DICA_SENHA");
                    usu_ip_acesso = JspHelper.verificaVarQryStr(request, "usu_ip_acesso");
                    usu_ddns_acesso = JspHelper.verificaVarQryStr(request, "usu_ddns_acesso");
                    usu_centralizador = JspHelper.verificaVarQryStr(request, "USU_CENTRALIZADOR");
                    usu_autentica_sso = JspHelper.verificaVarQryStr(request, "USU_AUTENTICA_SSO");
                    usu_exige_certificado = JspHelper.verificaVarQryStr(request, "USU_EXIGE_CERTIFICADO");
                    usu_matricula_inst = JspHelper.verificaVarQryStr(request, "USU_MATRICULA_INST");
                    perfil = JspHelper.verificaVarQryStr(request, "perfil");
                    usuFunCodigos = request.getParameterValues("funcao") != null ? Arrays.asList(request.getParameterValues("funcao")) : null;
                    usu_deficiente_visual = JspHelper.verificaVarQryStr(request, "USU_DEFICIENTE_VISUAL");
                    usu_permite_validacao_totp = JspHelper.verificaVarQryStr(request, "USU_PERMITE_VALIDACAO_TOTP");
                    usu_data_fim_vig = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "USU_DATA_FIM_VIG")) ? Date.valueOf(DateHelper.reformat(JspHelper.verificaVarQryStr(request, "USU_DATA_FIM_VIG"), LocaleHelper.getDatePattern(), "yyyy-MM-dd")) : null;
                    usu_qtd_consultas_margem = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "usuQtdConsultasMargem")) ? Integer.parseInt(JspHelper.verificaVarQryStr(request, "usuQtdConsultasMargem")) : null;
                }

                funcoes = usuarioController.lstFuncoesPermitidasUsuario(tipo, codigo, responsavel);
                perfis = usuarioController.lstPerfilSemBloqueioRepasse(tipo, codigo, usuCodigo, responsavel);

            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                perfis = funcoes = new ArrayList<>();
            }

            List<String> perFunCodigos = null;
            int cont;
            String arrPerfis = "", arrFuncoes = "";

            for (final TransferObject next : perfis) {
                final String per_codigo = next.getAttribute(Columns.PER_CODIGO).toString();
                //String per_descricao = next.getAttribute(Columns.PER_DESCRICAO).toString();

                perFunCodigos = usuarioController.getFuncaoPerfil(tipo, codigo, per_codigo, responsavel);
                cont = 0;
                while (cont < perFunCodigos.size()) {
                    arrPerfis += "'" + per_codigo + "',";
                    arrFuncoes += "'" + perFunCodigos.get(cont) + "',";
                    cont++;
                }
            }

            if (arrPerfis.endsWith(",")) {
                arrPerfis = arrPerfis.substring(0, arrPerfis.length() - 1);
            }

            if (arrFuncoes.endsWith(",")) {
                arrFuncoes = arrFuncoes.substring(0, arrFuncoes.length() - 1);
            }

            String icnBloquearFuncoes = "desbloqueado.gif";
            List<TransferObject> bloqueadas = new ArrayList<>();
            if (!TextHelper.isNull(usuCodigo)) {
                bloqueadas = usuarioController.selectFuncoesBloqueadas(usuCodigo, responsavel);
            }

            if (bloqueadas.size() > 0) {
                icnBloquearFuncoes = "pdesbloqueado.gif";
            }

            // Se usuário está excluído não pode ser editado
            boolean readOnly = false;
            if (CodedValues.STU_EXCLUIDO.equals(stu_codigo)) {
                readOnly = true;
            }

            // Se o usuário não tem permissão para editar usuários
            if (!podeEditarUsuario(responsavel)) {
                readOnly = true;
            }

            // Cadastro de TOTP só pode ser realizado por usuário de suporte para usuário de suporte
            boolean podeEditarValidacaoTotp = false;
            if (responsavel.isSup() && !responsavel.getUsuCodigo().equals(usuCodigo) && AcessoSistema.ENTIDADE_SUP.equals(tipo)) {
                podeEditarValidacaoTotp = true;
            }

            String msgAlertaCriacaoUsuarioGestor = "";
            if (AcessoSistema.ENTIDADE_CSE.equals(tipo) || AcessoSistema.ENTIDADE_ORG.equals(tipo)) {
                msgAlertaCriacaoUsuarioGestor = ApplicationResourcesHelper.getMessage("mensagem.alerta.criacao.usuario.gestor", responsavel);
            }

            // Exibe Botao que leva ao rodapé
            final boolean exibeBotaoRodape = ParamSist.paramEquals(CodedValues.TPC_EXIBE_BOTAO_RESPONSAVEL_PELO_RODAPE_DA_PAGINA, CodedValues.TPC_SIM, responsavel);
            model.addAttribute("exibeBotaoRodape", exibeBotaoRodape);
            
            boolean exibeCampoUsuAutenticaSso = false;
            try {
    			String pcsVlr = parametroController.getParamCsa(codigo, CodedValues.TPA_USUARIO_AUTENTICA_SSO, responsavel);
    			final boolean tpaUsuAutenticaSso = !TextHelper.isNull(pcsVlr) && "S".equalsIgnoreCase(pcsVlr);
    			exibeCampoUsuAutenticaSso = responsavel.isCsa() && tpaUsuAutenticaSso && (!TextHelper.isNull(responsavel.getUsuAutenticaSso()) && responsavel.getUsuAutenticaSso().equals("S"));
    		} catch (ParametroControllerException e) {
                session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());                
    		}            

            // Dados do usuário
            model.addAttribute("stu_codigo", stu_codigo);
            model.addAttribute("usu_dica_senha", usu_dica_senha);
            model.addAttribute("usu_email", usu_email);
            model.addAttribute("usu_tel", usu_tel);
            model.addAttribute("usu_login", usu_login);
            model.addAttribute("usu_nome", usu_nome);
            model.addAttribute("usu_ip_acesso", usu_ip_acesso);
            model.addAttribute("usu_ddns_acesso", usu_ddns_acesso);
            model.addAttribute("usu_cpf", usu_cpf);
            model.addAttribute("usu_centralizador", usu_centralizador);
            model.addAttribute("usu_autentica_sso", usu_autentica_sso);
            model.addAttribute("usu_visivel", usu_visivel);
            model.addAttribute("usu_exige_certificado", usu_exige_certificado);
            model.addAttribute("usu_matricula_inst", usu_matricula_inst);
            model.addAttribute("usu_deficiente_visual", usu_deficiente_visual);
            model.addAttribute("perfil", perfil);
            model.addAttribute("usu_permite_validacao_totp", usu_permite_validacao_totp);
            model.addAttribute("usu_data_fim_vig", usu_data_fim_vig);

            model.addAttribute("cpfObrigatorio", cpfObrigatorio);
            model.addAttribute("exigeMotivoOperacaoUsu", exigeMotivoOperacaoUsu);
            model.addAttribute("exibeMotivoOperacao", exibeMotivoOperacao);
            model.addAttribute("podeEditarUsuDeficienteVisual", podeEditarUsuDeficienteVisual);
            model.addAttribute("bloqueiaEdicaoEmail", bloqueiaEdicaoEmail);
            model.addAttribute("codigo", codigo);
            model.addAttribute("tipo", tipo);
            model.addAttribute("titulo", titulo);
            model.addAttribute("usu_codigo", usuCodigo);
            model.addAttribute("arr_p", arr_p);
            model.addAttribute("usuFunCodigos", usuFunCodigos);
            model.addAttribute("funcoes", funcoes);
            model.addAttribute("perfis", perfis);
            model.addAttribute("arrPerfis", arrPerfis);
            model.addAttribute("arrFuncoes", arrFuncoes);
            model.addAttribute("icnBloquearFuncoes", icnBloquearFuncoes);
            model.addAttribute("podeEditarUsuExigeCertificado", podeEditarUsuExigeCertificado);
            model.addAttribute("podeEditarValidacaoTotp", podeEditarValidacaoTotp);
            model.addAttribute("msgAlertaCriacaoUsuarioGestor", msgAlertaCriacaoUsuarioGestor);
            model.addAttribute("usuQtdConsultasMargem", usu_qtd_consultas_margem);
            model.addAttribute("readOnly", readOnly);
            model.addAttribute("exibeCampoUsuAutenticaSso", exibeCampoUsuAutenticaSso);

            return viewRedirect("jsp/manterUsuario/editarUsuarioPapel", request, session, model, responsavel);

        } catch (final UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=inserir" })
    public String inserir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            // Valida o token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final String tipo = getTipoEntidade();
            String codigo = getCodigoEntidade(request);
            final String perCodigo = JspHelper.verificaVarQryStr(request, "perfil");
            final String usu_ip_acesso = JspHelper.verificaVarQryStr(request, "usu_ip_acesso");
            final Object usu_ddns_acesso = JspHelper.verificaVarQryStr(request, "usu_ddns_acesso");

            // Valida o tipo de entidade, com a entidade do usuário que está criando novo usuário
            try {
                validaTipoEntidade(request, responsavel);
            } catch (final ZetraException e) {
                // Redireciona para página de erro
                session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Caso não permita inclusão de usuário sem perfil associado
            if ((TextHelper.isNull(perCodigo) || "0".equals(perCodigo)) && ParamSist.paramEquals(CodedValues.TPC_PERMITE_PERFIL_PERSONALIZADO, CodedValues.TPC_NAO, responsavel)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.perfil.usuario", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String link = getLinkInserirUsuario() + "?acao=iniciar&codigo=" + codigo + "&titulo=" + getTituloPaginaBase64(request, responsavel) + "&back=1";

            if (TextHelper.isNull(codigo)) {
                codigo = responsavel.getCodigoEntidade();
            }

            // Validar ip de acesso
            String msgErro = validaIpAcesso(request, session, tipo, codigo, usu_ip_acesso, usu_ddns_acesso);

            //Exige tipo de motivo da operacao
            final boolean exigeMotivoOperacaoUsu = ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_TIPO_MOTIVO_OPERACAO_USUARIO, responsavel);
            final boolean exibeMotivoOperacao = (exigeMotivoOperacaoUsu && FuncaoExigeMotivo.getInstance().exists(responsavel.getFunCodigo(), responsavel));

            if (exigeMotivoOperacaoUsu && exibeMotivoOperacao && "".equals(JspHelper.verificaVarQryStr(request, "TMO_CODIGO"))) {
                msgErro = ApplicationResourcesHelper.getMessage("mensagem.motivo.operacao.obrigatorio", responsavel);
            }

            if (msgErro.length() > 0) {
                session.setAttribute(CodedValues.MSG_ERRO, msgErro);
                request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(link, request)));
                return "jsp/redirecionador/redirecionar";
            }

            // Define o tamanho da senha ser gerada, deve respeitar o parâmetro de sistema caso cadastrado
            int tamanhoSenha = 8;
            try {
                final Object tamMinSenhaUsuario = ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_USUARIOS, responsavel);
                tamanhoSenha = (!TextHelper.isNull(tamMinSenhaUsuario)) ? Integer.parseInt(tamMinSenhaUsuario.toString()) : 8;
            } catch (final Exception ex) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.tamanho.senha", responsavel) + ": " + ex.getMessage());
            }

            final String usuSenha = GeradorSenhaUtil.getPassword(tamanhoSenha, tipo, responsavel);

            try {
                String usuCodigo = "";
                final UsuarioTransferObject usuario = new UsuarioTransferObject();
                usuario.setUsuDicaSenha(JspHelper.verificaVarQryStr(request, "USU_DICA_SENHA"));
                usuario.setUsuEmail(JspHelper.verificaVarQryStr(request, "USU_EMAIL"));
                usuario.setUsuTel(JspHelper.verificaVarQryStr(request, "USU_TEL"));
                usuario.setUsuLogin(JspHelper.verificaVarQryStr(request, "USU_LOGIN"));
                usuario.setUsuNome(JspHelper.verificaVarQryStr(request, "USU_NOME"));
                usuario.setUsuIpAcesso(!TextHelper.isNull(usu_ip_acesso) ? usu_ip_acesso : null);
                usuario.setUsuDDNSAcesso(!"".equals(JspHelper.verificaVarQryStr(request, "usu_ddns_acesso")) ? JspHelper.verificaVarQryStr(request, "usu_ddns_acesso") : null);
                usuario.setUsuCPF(JspHelper.verificaVarQryStr(request, "USU_CPF"));
                usuario.setUsuSenha(SenhaHelper.criptografarSenha(usuario.getUsuLogin(), usuSenha, false, responsavel));
                usuario.setStuCodigo(CodedValues.STU_ATIVO);
                if (responsavel.isSup() && !"".equals(JspHelper.verificaVarQryStr(request, "USU_CENTRALIZADOR"))) {
                    usuario.setUsuCentralizador(JspHelper.verificaVarQryStr(request, "USU_CENTRALIZADOR").toString());
                }
                if (responsavel.isSup() && !"".equals(JspHelper.verificaVarQryStr(request, "USU_VISIVEL"))) {
                    usuario.setUsuVisivel(JspHelper.verificaVarQryStr(request, "USU_VISIVEL").toString());
                }
                final String usuLogin = JspHelper.verificaVarQryStr(request, "USU_LOGIN");

                boolean podeEditarUsuExigeCertificado = false;
                try {
                    podeEditarUsuExigeCertificado = (UsuarioHelper.isUsuarioCertificadoDigital(usuLogin, "S", tipo, codigo, responsavel) != UsuarioHelper.isUsuarioCertificadoDigital(usuLogin, "N", tipo, codigo, responsavel));
                } catch (final ViewHelperException ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                }
                if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "USU_EXIGE_CERTIFICADO")) && podeEditarUsuExigeCertificado) {
                    usuario.setUsuExigeCertificado(JspHelper.verificaVarQryStr(request, "USU_EXIGE_CERTIFICADO").toString());
                }
                if ((JspHelper.verificaVarQryStr(request, "USU_DEFICIENTE_VISUAL") != null) && !"".equals(JspHelper.verificaVarQryStr(request, "USU_DEFICIENTE_VISUAL"))) {
                    usuario.setUsuDeficienteVisual(JspHelper.verificaVarQryStr(request, "USU_DEFICIENTE_VISUAL").toString());
                }
                if ((JspHelper.verificaVarQryStr(request, "USU_PERMITE_VALIDACAO_TOTP") != null) && !"".equals(JspHelper.verificaVarQryStr(request, "USU_PERMITE_VALIDACAO_TOTP")) && responsavel.isSup() && AcessoSistema.ENTIDADE_SUP.equals(tipo)) {
                    usuario.setUsuPermiteValidacaoTotp(JspHelper.verificaVarQryStr(request, "USU_PERMITE_VALIDACAO_TOTP").toString());
                }
                if (responsavel.isSup() && !TextHelper.isNull(request.getParameter("usuQtdConsultasMargem"))) {
                    usuario.setUsuQtdConsultasMargem(Integer.parseInt(request.getParameter("usuQtdConsultasMargem")));
                }

                usuario.setUsuMatriculaInst(JspHelper.verificaVarQryStr(request, "USU_MATRICULA_INST"));

                usuario.setUsuDataFimVig(!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "USU_DATA_FIM_VIG")) ? Date.valueOf(DateHelper.reformat(JspHelper.verificaVarQryStr(request, "USU_DATA_FIM_VIG"), LocaleHelper.getDatePattern(), "yyyy-MM-dd")) : null);

                boolean exibeCampoUsuAutenticaSso = false;
                try {
        			String pcsVlr = parametroController.getParamCsa(codigo, CodedValues.TPA_USUARIO_AUTENTICA_SSO, responsavel);
        			final boolean tpaUsuAutenticaSso = !TextHelper.isNull(pcsVlr) && "S".equalsIgnoreCase(pcsVlr);
        			exibeCampoUsuAutenticaSso = responsavel.isCsa() && tpaUsuAutenticaSso && (!TextHelper.isNull(responsavel.getUsuAutenticaSso()) && responsavel.getUsuAutenticaSso().equals("S"));
        		} catch (ParametroControllerException e) {
        			session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());    
        		}
                if (exibeCampoUsuAutenticaSso && !"".equals(JspHelper.verificaVarQryStr(request, "USU_AUTENTICA_SSO"))) {
                    usuario.setUsuAutentiaSso(JspHelper.verificaVarQryStr(request, "USU_AUTENTICA_SSO").toString());
                }
                
                // informa o motivo da operação para ser gravado junto com a ocorrência de usuário
                CustomTransferObject tmo = null;
                if (request.getParameter("TMO_CODIGO") != null) {
                    tmo = new CustomTransferObject();
                    tmo.setAttribute(Columns.USU_CODIGO, usuCodigo);
                    tmo.setAttribute(Columns.TMO_CODIGO, JspHelper.verificaVarQryStr(request, "TMO_CODIGO"));
                    tmo.setAttribute(Columns.OUS_OBS, JspHelper.verificaVarQryStr(request, "ADE_OBS"));
                }

                //DESENV-10463: Verifica se sistema envia e-mail de inicialização de senha na criação do usuário do tipo em questão
                final boolean enviaEmailInicializacaoSenha = (((AcessoSistema.ENTIDADE_CSE.equals(tipo) || AcessoSistema.ENTIDADE_ORG.equals(tipo)) &&
                                                        ParamSist.paramEquals(CodedValues.TPC_ENVIA_EMAIL_CRIACAO_SENHA_NOVO_USU_CSE_ORG, CodedValues.TPC_SIM, responsavel))
                                                        || ((AcessoSistema.ENTIDADE_CSA.equals(tipo) || AcessoSistema.ENTIDADE_COR.equals(tipo)) &&
                                                            ParamSist.paramEquals(CodedValues.TPC_ENVIA_EMAIL_CRIACAO_SENHA_NOVO_USU_CSA_COR, CodedValues.TPC_SIM, responsavel))
                                                        || ((AcessoSistema.ENTIDADE_SUP.equals(tipo)) &&
                                                            ParamSist.paramEquals(CodedValues.TPC_ENVIA_EMAIL_CRIACAO_SENHA_NOVO_USU_SUP, CodedValues.TPC_SIM, responsavel)));

                String msgSenha = "";

                if (enviaEmailInicializacaoSenha) {
                    String linkReinicializacao = request.getRequestURL().substring(0, request.getRequestURL().indexOf("/v3")).toString() + "/v3/recuperarSenhaUsuario";

                    linkReinicializacao += "?acao=iniciarUsuario&enti=" + getTipoEntidade();
                    linkReinicializacao = SynchronizerToken.updateTokenInURL(linkReinicializacao, request);
                    usuario.setLinkRecuperarSenha(linkReinicializacao);

                    msgSenha = "<br><font class=\"novaSenha\"> " + ApplicationResourcesHelper.getMessage("mensagem.info.novo.usuario.acessar.email.iniciar.senha", responsavel);
                } else if (ParamSist.getBoolParamSist(CodedValues.TPC_ENVIA_SENHA_EMAIL_CRIACAO_SENHA_NOVO_USU, responsavel)) {
                    msgSenha = "<br><font class=\"novaSenha\"> " + ApplicationResourcesHelper.getMessage("mensagem.usuario.senha.usuario", responsavel) + ": " + usuSenha + "</font>";
                    final String emailUsuario = JspHelper.verificaVarQryStr(request, "USU_EMAIL");

                    if (!TextHelper.isNull(emailUsuario)) {
                        EnviaEmailHelper.enviarEmailSenhaNovoUsuario(usuario, usuSenha, responsavel);
                        session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.usuario.senha.usuario.enviada.email", responsavel));
                    } else {
                        session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.usuario.senha.usuario.nao.enviada.email", responsavel));
                    }
                } else {
                    msgSenha = "<br><font class=\"novaSenha\"> " + ApplicationResourcesHelper.getMessage("mensagem.usuario.senha.usuario", responsavel) + ": " + usuSenha + "</font>";
                }

                if ("0".equals(perCodigo) && !ParamSist.paramEquals(CodedValues.TPC_PERMITE_PERFIL_PERSONALIZADO, CodedValues.TPC_NAO, responsavel)) {
                    final String[] funcao = request.getParameterValues("funcao");
                    final List<String> funcoes = funcao != null ? Arrays.asList(funcao) : new ArrayList<>();
                    usuCodigo = usuarioController.createUsuario(usuario, funcoes, codigo, tipo, tmo, true, usuSenha, responsavel);
                } else {
                    usuCodigo = usuarioController.createUsuario(usuario, perCodigo, codigo, tipo, tmo, true, usuSenha, true,responsavel);
                }
                
                if (podeEditarUsuario(responsavel)) {
                    // Se tem permissão de edição, redireciona para página para editar o usuário criado
                    link = getLinkEditarUsuario() + "?acao=iniciar&codigo=" + codigo + "&titulo=" + getTituloPaginaBase64(request, responsavel) + "&usu_codigo=" + usuCodigo + "&back=1";
                } else {
                    // Se não tem permissão de edição, redireciona para página para consultar o usuário criado
                    link = getLinkConsultarUsuario() + "?acao=iniciar&codigo=" + codigo + "&titulo=" + getTituloPaginaBase64(request, responsavel) + "&usu_codigo=" + usuCodigo + "&back=1";
                }

                final String emailAlertaCriacaoUsuCseOrg = (String) ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_ALERTA_CRIACAO_NOVO_USUARIO_CSE_ORG, responsavel);
                if ((AcessoSistema.ENTIDADE_CSE.equals(tipo) || AcessoSistema.ENTIDADE_ORG.equals(tipo)) && !TextHelper.isNull(emailAlertaCriacaoUsuCseOrg)) {
                    //DESENV-16952: Notificação por e-mail ao ocorrer a criação de um usuário do papel Consignante
                    EnviaEmailHelper.enviarEmailAlertaCriaNovoUsuCse(usuCodigo, responsavel);
                }
               
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.inserir.usuario.sucesso", responsavel) + msgSenha);

            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }

            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(link, request)));
            return "jsp/redirecionador/redirecionar";

        } catch (InstantiationException | IllegalAccessException | UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            // Valida o token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final ParamSession paramSession = ParamSession.getParamSession(session);

            final String usu_codigo = JspHelper.verificaVarQryStr(request, "USU_CODIGO");
            final UsuarioTransferObject usuario = new UsuarioTransferObject(usu_codigo);
            String msg = "";
            final String novoLogin = JspHelper.verificaVarQryStr(request, "USU_LOGIN");
            final UsuarioTransferObject usuOld = usuarioController.findUsuario(usu_codigo, responsavel);

            //String operacao = JspHelper.verificaVarQryStr(request, "operacao");
            final String tipo = getTipoEntidade();
            final String vrfTipo = JspHelper.verificaVarQryStr(request, "vrfTipo");
            String codigo = getCodigoEntidade(request);
            String perCodigo = JspHelper.verificaVarQryStr(request, "perfil");
            final String usu_ip_acesso = JspHelper.verificaVarQryStr(request, "usu_ip_acesso");
            final Object usu_ddns_acesso = JspHelper.verificaVarQryStr(request, "usu_ddns_acesso");

            final String perCodigoOld = request.getParameter("perCodigoOld");

            // Valida o tipo de entidade, com a entidade do usuário que está criando novo usuário
            try {
                validaTipoEntidade(request, responsavel);
            } catch (final ZetraException e) {
                // Redireciona para página de erro
                session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final String link = TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request));

            if (TextHelper.isNull(codigo)) {
                codigo = responsavel.getCodigoEntidade();
            }

            // Validar ip de acesso
            String msgErro = validaIpAcesso(request, session, tipo, codigo, usu_ip_acesso, usu_ddns_acesso);

            if (TextHelper.isNull(usu_codigo)) {
                msgErro = ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.inexistente", responsavel);
            }

            //Exige tipo de motivo da operacao
            final boolean exigeMotivoOperacaoUsu = ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_TIPO_MOTIVO_OPERACAO_USUARIO, responsavel);
            final boolean exibeMotivoOperacao = (exigeMotivoOperacaoUsu && FuncaoExigeMotivo.getInstance().exists(responsavel.getFunCodigo(), responsavel));

            if (exigeMotivoOperacaoUsu && exibeMotivoOperacao && "".equals(JspHelper.verificaVarQryStr(request, "TMO_CODIGO"))) {
                msgErro = ApplicationResourcesHelper.getMessage("mensagem.motivo.operacao.obrigatorio", responsavel);
            }

            if (msgErro.length() > 0) {
                session.setAttribute(CodedValues.MSG_ERRO, msgErro);
                request.setAttribute("url64", link);
                return "jsp/redirecionador/redirecionar";
            }

            final String usuCentralizador = (usuOld.getUsuCentralizador() != null ? (String) usuOld.getUsuCentralizador() : "");

            // verifica se usuário a ser editado é de origem do centralizador.
            // Caso sim, só pode ser editado por gestor ou via centralizador.
            if (CodedValues.TPC_SIM.equals(usuCentralizador) && !responsavel.isSup()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.centralizador.edicao.somente.gestor", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            try {
                if (!usuOld.getUsuLogin().equals(novoLogin)) {
                    // Define o tamanho da senha ser gerada, deve respeitar o parâmetro de sistema caso cadastrado
                    int tamanhoSenha = 8;
                    try {
                        final Object tamMaxSenhaUsuario = ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_USUARIOS, responsavel);
                        tamanhoSenha = (!TextHelper.isNull(tamMaxSenhaUsuario)) ? Integer.parseInt(tamMaxSenhaUsuario.toString()) : 8;
                    } catch (final Exception ex) {
                        LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.tamanho.senha", responsavel) + ": " + ex.getMessage());
                    }

                    final String usuSenha = GeradorSenhaUtil.getPassword(tamanhoSenha, tipo, responsavel);
                    usuario.setUsuDataExpSenha(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
                    usuario.setUsuSenha(SenhaHelper.criptografarSenha(usuario.getUsuLogin(), usuSenha, false, responsavel));
                    msg = "<BR><font class=\"novaSenha\">" + ApplicationResourcesHelper.getMessage("mensagem.usuario.nova.senha.novo.usuario", responsavel) + ": " + usuSenha + "</font>";
                }
                usuario.setUsuDicaSenha(JspHelper.verificaVarQryStr(request, "USU_DICA_SENHA"));
                usuario.setUsuEmail(JspHelper.verificaVarQryStr(request, "USU_EMAIL"));
                usuario.setUsuTel(JspHelper.verificaVarQryStr(request, "USU_TEL"));
                usuario.setUsuLogin(JspHelper.verificaVarQryStr(request, "USU_LOGIN"));
                usuario.setUsuNome(JspHelper.verificaVarQryStr(request, "USU_NOME"));
                usuario.setUsuCPF(JspHelper.verificaVarQryStr(request, "USU_CPF"));

                if (request.getParameter("usu_ip_acesso") != null) {
                    usuario.setUsuIpAcesso(!TextHelper.isNull(usu_ip_acesso) ? usu_ip_acesso : null);
                }
                if (request.getParameter("usu_ddns_acesso") != null) {
                    usuario.setUsuDDNSAcesso(!"".equals(JspHelper.verificaVarQryStr(request, "usu_ddns_acesso")) ? JspHelper.verificaVarQryStr(request, "usu_ddns_acesso") : null);
                }
                if (responsavel.isSup() && !"".equals(JspHelper.verificaVarQryStr(request, "USU_CENTRALIZADOR"))) {
                    usuario.setUsuCentralizador(JspHelper.verificaVarQryStr(request, "USU_CENTRALIZADOR").toString());
                }
                if (responsavel.isSup() && !"".equals(JspHelper.verificaVarQryStr(request, "USU_VISIVEL"))) {
                    usuario.setUsuVisivel(JspHelper.verificaVarQryStr(request, "USU_VISIVEL").toString());
                }
                
                boolean exibeCampoUsuAutenticaSso = false;
                try {
        			String pcsVlr = parametroController.getParamCsa(codigo, CodedValues.TPA_USUARIO_AUTENTICA_SSO, responsavel);
        			final boolean tpaUsuAutenticaSso = !TextHelper.isNull(pcsVlr) && "S".equalsIgnoreCase(pcsVlr);
        			exibeCampoUsuAutenticaSso = responsavel.isCsa() && tpaUsuAutenticaSso && (!TextHelper.isNull(responsavel.getUsuAutenticaSso()) && responsavel.getUsuAutenticaSso().equals("S"));
        		} catch (ParametroControllerException e) {
        			session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());    
        		}
                if (exibeCampoUsuAutenticaSso && !"".equals(JspHelper.verificaVarQryStr(request, "USU_AUTENTICA_SSO"))) {
                    usuario.setUsuAutentiaSso(JspHelper.verificaVarQryStr(request, "USU_AUTENTICA_SSO").toString());
                }

                boolean podeEditarUsuExigeCertificado = false;
                try {
                    podeEditarUsuExigeCertificado = (UsuarioHelper.isUsuarioCertificadoDigital(usuario.getUsuLogin(), "S", tipo, codigo, responsavel) != UsuarioHelper.isUsuarioCertificadoDigital(usuario.getUsuLogin(), "N", tipo, codigo, responsavel));
                } catch (final ViewHelperException ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                }

                final String usu_exige_certificado_old = JspHelper.verificaVarQryStr(request, "USU_EXIGE_CERTIFICADO_OLD");
                final String usu_exige_certificado = JspHelper.verificaVarQryStr(request, "USU_EXIGE_CERTIFICADO");
                if (!TextHelper.isNull(usu_exige_certificado_old) && !TextHelper.isNull(usu_exige_certificado) && !usu_exige_certificado_old.equals(usu_exige_certificado) && podeEditarUsuExigeCertificado) {
                    usuario.setUsuExigeCertificado(JspHelper.verificaVarQryStr(request, "USU_EXIGE_CERTIFICADO").toString());
                }
                usuario.setUsuMatriculaInst(JspHelper.verificaVarQryStr(request, "USU_MATRICULA_INST"));
                usuario.setUsuDataFimVig(!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "USU_DATA_FIM_VIG")) ? Date.valueOf(DateHelper.reformat(JspHelper.verificaVarQryStr(request, "USU_DATA_FIM_VIG"), LocaleHelper.getDatePattern(), "yyyy-MM-dd")) : null);
                if ((JspHelper.verificaVarQryStr(request, "USU_DEFICIENTE_VISUAL") != null) && !"".equals(JspHelper.verificaVarQryStr(request, "USU_DEFICIENTE_VISUAL"))) {
                    usuario.setUsuDeficienteVisual(JspHelper.verificaVarQryStr(request, "USU_DEFICIENTE_VISUAL").toString());
                }

                if ((JspHelper.verificaVarQryStr(request, "USU_PERMITE_VALIDACAO_TOTP") != null) && !"".equals(JspHelper.verificaVarQryStr(request, "USU_PERMITE_VALIDACAO_TOTP"))) {
                    usuario.setUsuPermiteValidacaoTotp(JspHelper.verificaVarQryStr(request, "USU_PERMITE_VALIDACAO_TOTP").toString());
                }

                if (responsavel.isSup()) {
                    usuario.setUsuQtdConsultasMargem((!TextHelper.isNull(request.getParameter("usuQtdConsultasMargem")) ? Integer.parseInt(request.getParameter("usuQtdConsultasMargem")) : null));
                }

                List<String> funcoes = null;
                if (!TextHelper.isNull(perCodigoOld) && !perCodigoOld.equals(perCodigo)) {
                    final Map<String, EnderecoFuncaoTransferObject> funcMap = usuarioController.selectFuncoes(usu_codigo, codigo, tipo, responsavel);
                    final Set<String> funSet = funcMap.keySet();
                    final String[] funcao = request.getParameterValues("funcao");
                    funcoes = (funcao != null) && (funcao.length > 0) ? Arrays.asList(funcao) : usuarioController.getFuncaoPerfil(tipo, codigo, perCodigo, responsavel);

                    if ((funSet.contains(CodedValues.FUN_USUARIO_AUDITOR) && !funcoes.contains(CodedValues.FUN_USUARIO_AUDITOR)) && !usuarioController.podeRemoverFuncAuditoria(usu_codigo, codigo, tipo, responsavel)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.permissao.auditoria.nao.pode.remover", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                }

                // informa o motivo da operação para ser gravado junto com a ocorrência de usuário
                CustomTransferObject tmo = null;
                if (request.getParameter("TMO_CODIGO") != null) {
                    tmo = new CustomTransferObject();
                    tmo.setAttribute(Columns.USU_CODIGO, usu_codigo);
                    tmo.setAttribute(Columns.TMO_CODIGO, JspHelper.verificaVarQryStr(request, "TMO_CODIGO"));
                    tmo.setAttribute(Columns.OUS_OBS, JspHelper.verificaVarQryStr(request, "ADE_OBS"));
                }

                if (TextHelper.isNull(vrfTipo) && !"".equals(tipo)) {
                    if ("0".equals(perCodigo)) {
                        perCodigo = null;
                        final String[] funcao = request.getParameterValues("funcao");
                        funcoes = funcao != null ? Arrays.asList(funcao) : new ArrayList<>();
                    } else {
                        funcoes = null;
                    }
                }

                usuarioController.updateUsuario(usuario, null, funcoes, perCodigo, tipo, codigo, tmo, responsavel);

                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alterar.usuario.sucesso", responsavel) + msg);
            } catch (final ParseException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }

            request.setAttribute("url64", link);
            return "jsp/redirecionador/redirecionar";

        } catch (InstantiationException | IllegalAccessException | UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            // Valida o token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final ParamSession paramSession = ParamSession.getParamSession(session);

            final String link = TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request));

            // Valida o tipo de entidade, com a entidade do usuário que está criando novo usuário
            try {
                validaTipoEntidade(request, responsavel);
            } catch (final ZetraException e) {
                // Redireciona para página de erro
                session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final String usu_codigo = JspHelper.verificaVarQryStr(request, "USU_CODIGO");
            final String codigoEntidade = getCodigoEntidade(request);
            final String tipo = getTipoEntidade();

            final UsuarioTransferObject criterio = new UsuarioTransferObject(usu_codigo);
            criterio.setAttribute(getColunaCodigoEntidade(), codigoEntidade);

            //Exige tipo de motivo da operacao
            final boolean exigeMotivoOperacaoUsu = ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_TIPO_MOTIVO_OPERACAO_USUARIO, responsavel);
            final boolean exibeMotivoOperacao = (exigeMotivoOperacaoUsu && FuncaoExigeMotivo.getInstance().exists(responsavel.getFunCodigo(), responsavel));

            String msgErro = "";
            if (exigeMotivoOperacaoUsu && exibeMotivoOperacao && "".equals(JspHelper.verificaVarQryStr(request, "TMO_CODIGO"))) {
                msgErro = ApplicationResourcesHelper.getMessage("mensagem.motivo.operacao.obrigatorio", responsavel);
            }

            if (msgErro.length() > 0) {
                session.setAttribute(CodedValues.MSG_ERRO, msgErro);
                request.setAttribute("url64", link);
                return "jsp/redirecionador/redirecionar";
            }

            // informa o motivo da operação para ser gravado junto com a ocorrência de usuário
            CustomTransferObject tmo = null;
            if (request.getParameter("TMO_CODIGO") != null) {
                tmo = new CustomTransferObject();
                tmo.setAttribute(Columns.USU_CODIGO, usu_codigo);
                tmo.setAttribute(Columns.TMO_CODIGO, JspHelper.verificaVarQryStr(request, "TMO_CODIGO"));
                tmo.setAttribute(Columns.OUS_OBS, JspHelper.verificaVarQryStr(request, "ADE_OBS"));
            }

            //testa se usuário centralizador. Se sim, só poderá ser removido por usuário
            //gestor do eConsig ou via centralizador
            final UsuarioTransferObject usuTrans = usuarioController.findUsuario(usu_codigo, responsavel);
            final String usuCentralizador = usuTrans.getUsuCentralizador();
            if (((usuTrans != null) && (CodedValues.TPC_SIM.equals(usuCentralizador))) && !responsavel.isSup()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.centralizador.exclusao.somente.gestor", responsavel));
            } else {
                usuarioController.removeUsuario(criterio, tipo, tmo, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.remover.usuario.sucesso", responsavel));
            }

            request.setAttribute("url64", link);
            return "jsp/redirecionador/redirecionar";

        } catch (final UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=bloquear" })
    public String bloquear(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            // Valida o token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final ParamSession paramSession = ParamSession.getParamSession(session);

            String link = TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request));

            // Valida o tipo de entidade, com a entidade do usuário que está criando novo usuário
            try {
                validaTipoEntidade(request, responsavel);
            } catch (final ZetraException e) {
                // Redireciona para página de erro
                session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            //Exige tipo de motivo da operacao
            final boolean exigeMotivoOperacaoUsu = ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_TIPO_MOTIVO_OPERACAO_USUARIO, responsavel);
            final boolean exibeMotivoOperacao = (exigeMotivoOperacaoUsu && FuncaoExigeMotivo.getInstance().exists(responsavel.getFunCodigo(), responsavel));

            String msgErro = "";
            if (exigeMotivoOperacaoUsu && exibeMotivoOperacao && "".equals(JspHelper.verificaVarQryStr(request, "TMO_CODIGO"))) {
                msgErro = ApplicationResourcesHelper.getMessage("mensagem.motivo.operacao.obrigatorio", responsavel);
            }

            if (msgErro.length() > 0) {
                session.setAttribute(CodedValues.MSG_ERRO, msgErro);
                request.setAttribute("url64", link);
                return "jsp/redirecionador/redirecionar";
            }

            final String usu_codigo = JspHelper.verificaVarQryStr(request, "USU_CODIGO");
            final String tipo = getTipoEntidade();
            String status = JspHelper.verificaVarQryStr(request, "STATUS");
            final String tmoCodigo = JspHelper.verificaVarQryStr(request, "TMO_CODIGO");
            final String ousObs = JspHelper.verificaVarQryStr(request, "ADE_OBS");

            final String msgRet = (CodedValues.STU_ATIVO.equals(status) ? ApplicationResourcesHelper.getMessage("mensagem.usuario.bloqueado.sucesso", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.usuario.desbloqueado.sucesso", responsavel));
            status = (CodedValues.STU_ATIVO.equals(status) ? CodedValues.STU_BLOQUEADO : CodedValues.STU_ATIVO);

            usuarioController.bloquearDesbloquearUsuario(usu_codigo, status, tipo, tmoCodigo, ousObs, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, msgRet);

            // Redireciona para a página de listagem de usuários criados pelo usuário bloqueado
            if (!CodedValues.STU_ATIVO.equals(status) && ("CSE".equalsIgnoreCase(tipo) || "ORG".equalsIgnoreCase(tipo) || "SUP".equalsIgnoreCase(tipo))) {
                link = getLinkBloquearUsuario() + "?acao=listarBloqueioUsuarioRecursivo&USU_CODIGO=" + usu_codigo + "&STATUS=" + status + "&tipo=" + tipo + "&TMO_CODIGO=" + tmoCodigo + "&ADE_OBS=" + ousObs + "&_skip_history_=true";
                link = TextHelper.encode64(SynchronizerToken.updateTokenInURL(link, request));
            }

            request.setAttribute("url64", link);
            return "jsp/redirecionador/redirecionar";

        } catch (final UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=reinicializar" })
    public String reinicializar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            // Valida o token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final ParamSession paramSession = ParamSession.getParamSession(session);
            final String link = TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request));
            final String usuCodigo = JspHelper.verificaVarQryStr(request, "USU_CODIGO");

            // Valida o tipo de entidade, com a entidade do usuário que está criando novo usuário
            try {
                validaTipoEntidade(request, responsavel);
            } catch (final ZetraException e) {
                // Redireciona para página de erro
                session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Exige tipo de motivo da operacao
            final boolean exigeMotivoOperacaoUsu = ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_TIPO_MOTIVO_OPERACAO_USUARIO, responsavel) && FuncaoExigeMotivo.getInstance().exists(responsavel.getFunCodigo(), responsavel);
            final String tmoCodigo = JspHelper.verificaVarQryStr(request, "TMO_CODIGO");
            if (exigeMotivoOperacaoUsu && TextHelper.isNull(tmoCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.motivo.operacao.obrigatorio", responsavel));
                request.setAttribute("url64", link);
                return "jsp/redirecionador/redirecionar";
            }

            // informa o motivo da operação para ser gravado junto com a ocorrência de usuário
            CustomTransferObject tmo = null;
            if (!TextHelper.isNull(tmoCodigo)) {
                tmo = new CustomTransferObject();
                tmo.setAttribute(Columns.USU_CODIGO, usuCodigo);
                tmo.setAttribute(Columns.TMO_CODIGO, tmoCodigo);
                tmo.setAttribute(Columns.OUS_OBS, JspHelper.verificaVarQryStr(request, "ADE_OBS"));
            }

            // Define o tamanho da senha ser gerada, deve respeitar o parâmetro de sistema caso cadastrado
            int tamanhoSenha = 8;
            try {
                final Object tamMaxSenhaUsuario = ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_USUARIOS, responsavel);
                tamanhoSenha = (!TextHelper.isNull(tamMaxSenhaUsuario)) ? Integer.parseInt(tamMaxSenhaUsuario.toString()) : 8;
            } catch (final Exception ex) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.tamanho.senha", responsavel) + ": " + ex.getMessage());
            }

            final TransferObject usuario = usuarioController.findTipoUsuarioByCodigo(usuCodigo, responsavel);
            final String tipoEntidade = UsuarioHelper.obterTipoEntidade(usuario);

            if (!ParamSist.paramEquals(CodedValues.TPC_EMAIL_REINICIALIZACAO_SENHA, CodedValues.TPC_SIM, responsavel)) {
                final String usuSenha = GeradorSenhaUtil.getPassword(tamanhoSenha, tipoEntidade, responsavel);
                usuarioController.alterarSenha(usuCodigo, usuSenha, null, true, true, false, tmo, null, responsavel);

                final String msgSucesso = ApplicationResourcesHelper.getMessage("mensagem.senha.usuario.reinicializada", responsavel);
                final String msgSenha = "<br><font class=\"novaSenha\"> " + ApplicationResourcesHelper.getMessage("rotulo.usuario.nova.senha", responsavel) + ": " + usuSenha + "</font>";
                session.setAttribute(CodedValues.MSG_INFO, msgSucesso + msgSenha);

            } else {
                final UsuarioTransferObject dadosUsuario = usuarioController.findUsuario(usuCodigo, responsavel);

                final String stuCodigo = (String) dadosUsuario.getAttribute(Columns.USU_STU_CODIGO);

                // Usuário deve estar na situação ativo
                if (!CodedValues.STU_ATIVO.equals(stuCodigo)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usuario.bloqueado", responsavel));
                } else {
                    // Encontrou usuário, verifica se possui e-mail cadastrado
                    final String usuCpf = dadosUsuario.getUsuCPF();
                    final String usuLogin = dadosUsuario.getUsuLogin();
                    final String usuEmail = dadosUsuario.getUsuEmail();
                    final boolean cpfObrigatorio = ParamSist.paramEquals(CodedValues.TPC_CADASTRO_CPF_OBRIGATORIO_USUARIO, CodedValues.TPC_SIM, responsavel);

                    if (TextHelper.isNull(usuEmail)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.email.nao.cadastrado", responsavel));
                    } else if (cpfObrigatorio && TextHelper.isNull(usuCpf)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.cpf.nao.cadastrado", responsavel));
                    } else {
                        if (!TextHelper.isNull(tipoEntidade) && !AcessoSistema.ENTIDADE_SUP.equals(tipoEntidade) && !AcessoSistema.ENTIDADE_SER.equals(tipoEntidade)) {
                            final boolean usuEmailRepeat = usuarioController.findEmailExistenteCsaCseOrgCor(usuEmail, dadosUsuario.getUsuCodigo(), responsavel);
                            if (usuEmailRepeat) {
                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.email.usuario.csa.cse.org.cor.repetido", responsavel));
                                request.setAttribute("url64", link);
                                return "jsp/redirecionador/redirecionar";
                            }
                        }
                        try {
                            // O usuario possui e-mail, então envia email com link para alterar senha
                            String linkReinicializacao = request.getRequestURL().toString();
                            linkReinicializacao = linkReinicializacao.replace(getLinkReinicializarSenhaUsuario().replace(".", ""), "/v3/recuperarSenhaUsuario");
                            linkReinicializacao += "?acao=iniciarUsuario" + "&enti=" + getTipoEntidade();
                            linkReinicializacao = SynchronizerToken.updateTokenInURL(linkReinicializacao, request);
                            // Gera uma nova codigo de recuparação de senha
                            final String cod_Senha = SynchronizerToken.generateToken();
                            // Atualiza o codigo de recuperação de senha do usuário
                            usuarioController.alteraChaveRecupSenha(usuCodigo, cod_Senha, responsavel);
                            // Envia e-mail com link para recuperação de senha
                            usuarioController.enviaLinkReinicializarSenhaUsu(usuCodigo, usuLogin, linkReinicializacao, cod_Senha, responsavel);
                            // Inválida senha do usuário
                            usuarioController.alterarSenha(usuCodigo, GeradorSenhaUtil.getPassword(tamanhoSenha, tipoEntidade, responsavel), null, false, true, true, tmo, null, responsavel);
                            // Retorna mensagem de sucesso para o usuário
                            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.reinicializar.senha.usuario.sucesso", responsavel, TextHelper.escondeEmail(dadosUsuario.getUsuEmail())));
                        } catch (final UsuarioControllerException ex) {
                            LOG.error(ex.getMessage(), ex);
                            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                        }
                    }
                }
            }

            request.setAttribute("url64", link);
            return "jsp/redirecionador/redirecionar";

        } catch (final UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=iniciarBloqueioUsuarioFuncaoServico" })
    public String iniciarBloqueioUsuarioFuncaoServico(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final String titulo = getTituloPagina(request, responsavel);
            final String usu_codigo = JspHelper.verificaVarQryStr(request, "USU_CODIGO");

            if (TextHelper.isNull(usu_codigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final UsuarioTransferObject usu = usuarioController.findUsuario(usu_codigo, responsavel);
            final String usu_nome = usu.getUsuNome();
            final String usu_login = usu.getUsuLogin();

            final CustomTransferObject usuario = usuarioController.findTipoUsuarioByLogin(usu_login, responsavel);

            if (TextHelper.isNull(usuario)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final String tipo = getTipoEntidade();
            final String codigo = getCodigoEntidade(request);
            final String funBloqUsuFunSvc = getFuncaoEdicaoBloqueioFuncaoServidor();

            List<TransferObject> funcoes;
            List<TransferObject> servicos;
            Map<String, List<String>> bloqueios;

            try {
                funcoes = usuarioController.lstFuncoesBloqueaveis(tipo, responsavel);

                if ((tipo == AcessoSistema.ENTIDADE_CSE) || (tipo == AcessoSistema.ENTIDADE_SUP)) {
                    final CustomTransferObject criterio = new CustomTransferObject();
                    criterio.setAttribute(Columns.SVC_ATIVO, CodedValues.STS_ATIVO);
                    servicos = convenioController.lstServicos(criterio, responsavel);
                } else if (tipo == AcessoSistema.ENTIDADE_CSA) {
                    servicos = servicoController.selectServicosCsa(codigo, responsavel);
                } else if (tipo == AcessoSistema.ENTIDADE_COR) {
                    servicos = servicoController.selectServicosCorrespondente(codigo, responsavel);
                } else if (tipo == AcessoSistema.ENTIDADE_ORG) {
                    servicos = servicoController.selectServicosOrgao(codigo, responsavel);
                } else {
                    servicos = new ArrayList<>();
                }

                final List<TransferObject> bloqueadas = usuarioController.selectFuncoesBloqueadas(usu_codigo, getTipoEntidade(), responsavel);

                // Monta HashMap dos bloqueios existentes, tendo serviço como chave e List de funções como valor.
                bloqueios = new HashMap<>();

                for (final TransferObject bloq : bloqueadas) {
                    if (!bloqueios.containsKey(bloq.getAttribute(Columns.BUF_FUN_CODIGO).toString())) {
                        bloqueios.put(bloq.getAttribute(Columns.BUF_FUN_CODIGO).toString(), new ArrayList<>());
                    }
                    bloqueios.get(bloq.getAttribute(Columns.BUF_FUN_CODIGO).toString()).add(bloq.getAttribute(Columns.BUF_SVC_CODIGO).toString());
                }

            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                funcoes = new ArrayList<>();
                servicos = new ArrayList<>();
                bloqueios = new HashMap<>();
            }

            String msgErro = "";
            if ((funcoes.size() == 0) && (servicos.size() == 0)) {
                msgErro = ApplicationResourcesHelper.getMessage("mensagem.erro.nenhuma.fun.nenhum.svc.pode.ser.bloqueado", responsavel);
            } else if (funcoes.size() == 0) {
                msgErro = ApplicationResourcesHelper.getMessage("mensagem.erro.nenhuma.fun.pode.ser.bloqueada", responsavel);
            } else if (servicos.size() == 0) {
                msgErro = ApplicationResourcesHelper.getMessage("mensagem.erro.nenhum.svc.pode.ser.bloqueado", responsavel);
            }

            boolean readOnly = false;
            if (!responsavel.temPermissao(funBloqUsuFunSvc)) {
                readOnly = true;
            }

            model.addAttribute("titulo", titulo);
            model.addAttribute("codigo", codigo);
            model.addAttribute("usu_codigo", usu_codigo);
            model.addAttribute("usu_nome", usu_nome);
            model.addAttribute("msgErro", msgErro);
            model.addAttribute("funcoes", funcoes);
            model.addAttribute("servicos", servicos);
            model.addAttribute("bloqueios", bloqueios);
            model.addAttribute("readOnly", readOnly);

            return viewRedirect("jsp/manterUsuario/editarBloqueioUsuarioFuncaoServico", request, session, model, responsavel);

        } catch (final UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=editarBloqueioUsuarioFuncaoServico" })
    public String editarBloqueioUsuarioFuncaoServico(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final String usu_codigo = JspHelper.verificaVarQryStr(request, "USU_CODIGO");

            if (TextHelper.isNull(usu_codigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final UsuarioTransferObject usu = usuarioController.findUsuario(usu_codigo, responsavel);
            if (usu == null) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final String[] selecao = request.getParameterValues("funcao_servico");
            final List<String> selecionados = selecao != null ? Arrays.asList(selecao) : new ArrayList<>();

            // List de CustomTransferObjects a serem inseridos.
            final List<TransferObject> bloquear = new ArrayList<>();

            for (final String selecionado : selecionados) {
                final String fun_codigo = selecionado.split("_")[0];
                final String svc_codigo = selecionado.split("_")[1];

                final CustomTransferObject cto = new CustomTransferObject();

                cto.setAttribute(Columns.BUF_FUN_CODIGO, fun_codigo);
                cto.setAttribute(Columns.BUF_SVC_CODIGO, svc_codigo);

                bloquear.add(cto);
            }

            // Insere os bloqueios selecionados.
            usuarioController.insereBloqueiosFuncoes(usu_codigo, bloquear, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.bloquear.funcao.usuario.sucesso", responsavel));

            // Repassa o token salvo, pois o método irá revalidar o token
            request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

            return iniciarBloqueioUsuarioFuncaoServico(request, response, session, model);

        } catch (final UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=listarBloqueioUsuarioRecursivo" })
    public String listarBloqueioUsuarioRecursivo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final ParamSession paramSession = ParamSession.getParamSession(session);

        final String usuCodigo = JspHelper.verificaVarQryStr(request, "USU_CODIGO");
        final String tipo = getTipoEntidade();
        final String status = JspHelper.verificaVarQryStr(request, "STATUS");
        final String tmoCodigo = JspHelper.verificaVarQryStr(request, "TMO_CODIGO");
        final String ousObs = JspHelper.verificaVarQryStr(request, "ADE_OBS");
        final String link = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request);
        final boolean lst_tudo = "listar_tudo".equals(JspHelper.verificaVarQryStr(request, "LISTAR_TODOS"));

        if (TextHelper.isNull(usuCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String mensagem = ApplicationResourcesHelper.getMessage("mensagem.informacao.usuario.bloqueado.cadastro.outros.usuarios", responsavel);

        List<TransferObject> usuarios = null;
        if (AcessoSistema.ENTIDADE_CSE.equalsIgnoreCase(tipo) || AcessoSistema.ENTIDADE_ORG.equalsIgnoreCase(tipo) || AcessoSistema.ENTIDADE_SUP.equalsIgnoreCase(tipo)) {
            try {
                final List<String> usuCodigos = new ArrayList<>();
                usuCodigos.add(usuCodigo);
                if (lst_tudo) {
                    usuarios = usuarioController.lstUsuarioCriadoRecursivoPorResponsavel(usuCodigos, responsavel);
                } else {
                    usuarios = usuarioController.lstUsuarioCriadoPorResponsavel(usuCodigos, responsavel);
                }
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }

        if ((usuarios == null) || usuarios.isEmpty()) {
            // Se não existe usuário criado pelo usuário passado redireciona para a ultima tela válida
            request.setAttribute("url64", TextHelper.encode64(link));
            return "jsp/redirecionador/redirecionar";
        }

        model.addAttribute("usuCodigo", usuCodigo);
        model.addAttribute("tipo", tipo);
        model.addAttribute("status", status);
        model.addAttribute("tmoCodigo", tmoCodigo);
        model.addAttribute("ousObs", ousObs);
        model.addAttribute("link", link);
        model.addAttribute("lst_tudo", lst_tudo);
        model.addAttribute("mensagem", mensagem);
        model.addAttribute("usuarios", usuarios);

        return viewRedirect("jsp/manterUsuario/listarBloqueioUsuarioRecursivo", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=bloquearUsuarioRecursivo" })
    public String bloquearUsuarioRecursivo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final String usuCodigo = JspHelper.verificaVarQryStr(request, "USU_CODIGO");
        final String tmoCodigo = JspHelper.verificaVarQryStr(request, "TMO_CODIGO");
        final String ousObs = JspHelper.verificaVarQryStr(request, "ADE_OBS");

        if (TextHelper.isNull(usuCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            final String[] codigos = request.getParameterValues("USUARIO");
            final List<String> usuarios = codigos != null ? Arrays.asList(codigos) : new ArrayList<>();

            final List<TransferObject> usuCodigos = new ArrayList<>();
            TransferObject to = null;

            final Iterator<String> iteUsuarios = usuarios.iterator();
            while (iteUsuarios.hasNext()) {
                final String[] valores = iteUsuarios.next().toString().split(";");
                to = new CustomTransferObject();
                to.setAttribute(Columns.USU_CODIGO, valores[0]);
                to.setAttribute("TIPO", valores[1]);
                usuCodigos.add(to);
            }

            usuarioController.bloquearDesbloquearUsuario(usuCodigos, CodedValues.STU_BLOQUEADO, tmoCodigo, ousObs, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.usuario.bloqueado.sucesso", responsavel));

        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }

        // Repassa o token salvo, pois o método irá revalidar o token
        request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

        return listarBloqueioUsuarioRecursivo(request, response, session, model);
    }

    protected String validaIpAcesso(HttpServletRequest request, HttpSession session, String tipo, String codigo, String usu_ip_acesso, Object usu_ddns_acesso) throws InstantiationException, IllegalAccessException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String msgErro = "";
        if ("".equals(JspHelper.verificaVarQryStr(request, "USU_NOME"))) {
            msgErro = ApplicationResourcesHelper.getMessage("mensagem.informe.usu.nome", responsavel);
        }
        if ("".equals(JspHelper.verificaVarQryStr(request, "USU_LOGIN"))) {
            msgErro = ApplicationResourcesHelper.getMessage("mensagem.informe.usu.login", responsavel);
        } else if (JspHelper.verificaVarQryStr(request, "USU_LOGIN").length() < 2) {
            msgErro = ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.login.minimo", responsavel);
        }

        final String usuLogin = JspHelper.verificaVarQryStr(request, "USU_LOGIN");

        try {
            //Confere se cadastro de IPs (ou DDNS) de acesso é obrigatório de acordo com o tipo da entidade
            usuarioController.validaIpAcessoResponsavel(tipo, usuLogin, codigo, usu_ip_acesso, (String) usu_ddns_acesso, responsavel);
        } catch (final UsuarioControllerException uex) {
            msgErro = uex.getMessage();
        }
        return msgErro;
    }

    protected abstract String getColunaCodigoEntidade();

    protected abstract boolean podeEditarUsuario(AcessoSistema responsavel);

    protected abstract String getFuncaoEdicaoBloqueioFuncaoServidor();
}
