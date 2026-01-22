package com.zetra.econsig.web.controller.sistema;

import static com.zetra.econsig.web.filter.UrlCryptFilter.AES_INIT_VECTOR_SESSION_ATTRIBUTE;
import static com.zetra.econsig.web.filter.UrlCryptFilter.AES_KEY_SESSION_ATTRIBUTE;

import java.security.KeyPair;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.crypto.BadPaddingException;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.criptografia.AES;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.margem.ControleConsulta;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoRecursoHelper;
import com.zetra.econsig.helper.seguranca.AcessoRecursoHelper.AcessoRecurso;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.GeraTelaSegundaSenhaHelper;
import com.zetra.econsig.helper.seguranca.GoogleAuthenticatorHelper;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.OperacaoNaoConfirmada;
import com.zetra.econsig.service.seguranca.SegurancaController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;
import com.zetra.econsig.web.servlet.AudioCaptchaServlet;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * <p>Title: AutorizarOperacaoWebController</p>
 * <p>Description: REST Controller para autorização de operação sensível.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@RestController
public class AutorizarOperacaoWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AutorizarOperacaoWebController.class);

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private SistemaController sistemaController;

    @Autowired
    private SegurancaController segurancaController;

    @RequestMapping("/v3/verificarOperacao")
    @Produces({ MediaType.APPLICATION_JSON })
    public Map<String, String> verificarOperacao(@RequestParam(value="uri") String uri, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final Map<String, String> result = new HashMap<>();
        try {
            final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
            final AcessoRecurso recursoAcessado = obterRecursoAcessado(uri, responsavel, request);

            result.put("requerAutorizacao", verificarExigenciaAutorizacao(recursoAcessado, responsavel));

            if (verificarExigenciaCaptcha(recursoAcessado, responsavel)) {
                boolean exibeCaptcha = false;
                boolean exibeCaptchaAvancado = false;
                boolean exibeCaptchaDeficiente = false;

                final boolean defVisual = responsavel.isDeficienteVisual();
                if (!defVisual) {
                    exibeCaptcha = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                    exibeCaptchaAvancado = exibeCaptcha ? false : ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                } else {
                    exibeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
                }
                // O captcha default deve ser solicitado mesmo que não tenha nenhum parâmetro de captcha configurado
                if (!exibeCaptcha && !exibeCaptchaAvancado && !exibeCaptchaDeficiente) {
                    exibeCaptcha = true;
                }
                result.put("requerCaptcha", exibeCaptcha ? "S" : "N");
                result.put("requerCaptchaAvancado", exibeCaptchaAvancado ? "S" : "N");
                result.put("requerCaptchaDeficiente", exibeCaptchaDeficiente ? "S" : "N");
            }
        } catch (final Exception ex) {
            // DESENV-21330 : força pedir senha para não correr o risco de algum erro permitir a inclusão indevidamente
            result.put("requerAutorizacao", CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_SIM);
            LOG.error(ex.getMessage(), ex);
        }
        return result;
    }

    @RequestMapping("/v3/autorizarOperacao")
    public Map<String, String> autorizarOperacao(@RequestParam(value="uri") String uri, @RequestParam(value = "username") String username, @RequestParam(value = "password") String password, @RequestParam(value = "validarSenha") boolean validarSenha, @RequestParam(value = "validarCaptcha") boolean validarCaptcha, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final Map<String, String> result = new HashMap<>();
        try {
            final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
            final AcessoRecurso recursoAcessado = obterRecursoAcessado(uri, responsavel, request);
            String exigeAutorizacaoOperacao = CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_NAO;
            boolean exigeCaptchaOperacao = false;
            if (validarSenha) {
                exigeAutorizacaoOperacao = verificarExigenciaAutorizacao(recursoAcessado, responsavel);
            }
            if (validarCaptcha) {
                exigeCaptchaOperacao = verificarExigenciaCaptcha(recursoAcessado, responsavel);
            }

            result.put("prosseguir", "S");

            if (!CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_NAO.equals(exigeAutorizacaoOperacao)) {
                final String mensagemErro = validarAutorizacao(exigeAutorizacaoOperacao, username, password, recursoAcessado, responsavel, request, session);
                if (!TextHelper.isNull(mensagemErro)) {
                    result.put("prosseguir", "N");
                    result.put("mensagem", mensagemErro);
                    return result;
                }
            }

            if (exigeCaptchaOperacao) {
                final String mensagemErro = validarCaptchaOperacoesLiberacaoMargem(request, response, session, model, responsavel);
                if (!TextHelper.isNull(mensagemErro)) {
                    result.put("prosseguir", "N");
                    result.put("mensagem", mensagemErro);
                }
            }
        } catch (final Exception ex) {
            // DESENV-21330 : força o bloqueio para não correr o risco de algum erro permitir a inclusão indevidamente
            result.put("prosseguir", "N");
            result.put("mensagem", ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }

        return result;
    }

    @RequestMapping("/v3/verDetalheOperacao")
    public Map<String, String> verDetalheOperacao(@RequestParam(value="oncCodigo") String oncCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final Map<String, String> result = new HashMap<>();
        try {
            final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

            if (!SynchronizerToken.isTokenValid(request)) {
                result.put("mensagem", ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return result;
            }

            final OperacaoNaoConfirmada opNaoConfirmada = sistemaController.findOperacaoNaoConfirmada(oncCodigo, responsavel);

            result.put("oncDetalhe", opNaoConfirmada.getOncDetalhe());
        } catch (final Exception ex) {
            result.put("mensagem", ex.getMessage());
            LOG.warn(ex.getMessage(), ex);
        }

        return result;
    }

    private AcessoRecurso obterRecursoAcessado(String uri, AcessoSistema responsavel, HttpServletRequest request) {
        if (responsavel.isSessaoValida()) {
            return AcessoRecursoHelper.identificarAcessoRecurso(getURIFromURL(uri, request), request.getParameterMap(), responsavel);
        }
        return null;
    }

    private String verificarExigenciaAutorizacao(AcessoRecurso recursoAcessado, AcessoSistema responsavel) {
        String exigeSenhaAutorizacao = CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_NAO;
        if (recursoAcessado != null) {
            boolean fimFluxo = recursoAcessado.isAcrFimFluxo();

            if (recursoAcessado.exigeSenhaOpeSensiveis(responsavel)) {
                // Se não é o fim do fluxo, porém é um recurso "acao=efetivarAcao", então verifica se a função acessada
                // exige motivo de operação, pois caso não exija, é feito um redirecionamento para o recurso do fim fluxo
                if ((!fimFluxo && "acao".equals(recursoAcessado.getAcrParametro()) && "efetivarAcao".equals(recursoAcessado.getAcrOperacao())) && !isExigeMotivoOperacao(recursoAcessado.getFunCodigo(), responsavel)) {
                    fimFluxo = true;
                }

                if (fimFluxo) {
                    if (recursoAcessado.funcaoExigeSegundaSenha(responsavel)) {
                        exigeSenhaAutorizacao = CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_SIM;
                    } else if (recursoAcessado.funcaoExigePropriaSenha(responsavel)) {
                        exigeSenhaAutorizacao = CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_PROPRIA;
                    } else if (recursoAcessado.adicionarFuncaoFilaAutorizacao(responsavel)) {
                        exigeSenhaAutorizacao = CodedValues.OPERACAO_ADICIONA_FILA_SEGUNDA_SENHA;
                    }
                }
            }

            // Verifica se deve solicitar segunda senha por motivo de segurança para funções que podem liberar margem
            if (fimFluxo && !CodedValues.OPERACAO_ADICIONA_FILA_SEGUNDA_SENHA.equals(exigeSenhaAutorizacao) && segurancaController.exigirSegundaSenhaOperacoesLiberacaoMargem(responsavel)) {
                exigeSenhaAutorizacao = CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_SIM;
            }
        }
        return exigeSenhaAutorizacao;
    }

    private boolean verificarExigenciaCaptcha(AcessoRecurso recursoAcessado, AcessoSistema responsavel) {
        if (recursoAcessado != null) {
            final boolean fimFluxo = recursoAcessado.isAcrFimFluxo();
            if (fimFluxo && segurancaController.exigirCaptchaOperacoesLiberacaoMargem(responsavel)) {
                return true;
            }
        }
        return false;
    }

    private String validarAutorizacao(String exigeAutorizacaoOperacao, String username, String password, AcessoRecurso recursoAcessado, AcessoSistema responsavel, HttpServletRequest request, HttpSession session) {
        boolean exigePropriaSenha = false;
        if (CodedValues.OPERACAO_EXIGE_SEGUNDA_SENHA_PROPRIA.equals(exigeAutorizacaoOperacao)) {
            username = responsavel.getUsuLogin();
            exigePropriaSenha = true;
        } else if (TextHelper.isNull(username)) {
            return ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel);
        }

        try {
            final CustomTransferObject usuario = new CustomTransferObject();
            List<TransferObject> usuarios = UsuarioHelper.localizarUsuario(username, responsavel);

            if ((usuarios == null)  || usuarios.isEmpty()) {
                return ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.senha.invalida", responsavel);
            }

            String funCodigo = responsavel.getFunCodigo();
            if (TextHelper.isNull(funCodigo) && recursoAcessado != null) {
                funCodigo = recursoAcessado.getFunCodigo();
            }

            if (!exigePropriaSenha) {
                final List<TransferObject> usuarioProprio = usuarios.stream().filter(usu -> usu.getAttribute(Columns.USU_CODIGO).equals(responsavel.getUsuCodigo())).collect(Collectors.toList());

                if ((usuarioProprio != null)  && !usuarioProprio.isEmpty() && !responsavel.isValidaTotp(true)) {
                    return ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.diferente.autorizar.operacao", responsavel);
                }

                if (responsavel.isCseSup()) {
                    usuarios = usuarios.stream().filter(usu -> !TextHelper.isNull(usu.getAttribute(Columns.UCE_CSE_CODIGO)) || !TextHelper.isNull(usu.getAttribute(Columns.USP_CSE_CODIGO))).collect(Collectors.toList());

                    if ((usuarios == null)  || usuarios.isEmpty()) {
                        return ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.nao.pode.autorizar.operacao", responsavel);
                    }
                } else if (responsavel.isOrg()) {
                    usuarios = usuarios.stream().filter(usu -> (!TextHelper.isNull(usu.getAttribute(Columns.UCE_CSE_CODIGO)) || !TextHelper.isNull(usu.getAttribute(Columns.USP_CSE_CODIGO)) || (!TextHelper.isNull(usu.getAttribute(Columns.UOR_ORG_CODIGO)) && responsavel.getOrgCodigo().equals(usu.getAttribute(Columns.UOR_ORG_CODIGO))))).collect(Collectors.toList());

                    if ((usuarios == null)  || usuarios.isEmpty()) {
                        return ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.nao.pode.autorizar.operacao", responsavel);
                    }
                } else if (responsavel.isCsa()) {
                    List<TransferObject> usuariosTemp = usuarios.stream().filter(usu -> !TextHelper.isNull(usu.getAttribute(Columns.UCA_CSA_CODIGO))).collect(Collectors.toList());

                    if ((usuariosTemp == null)  || usuariosTemp.isEmpty()) {
                        return ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.nao.pode.autorizar.operacao", responsavel);
                    } else {
                        usuariosTemp = usuariosTemp.stream().filter(usu -> responsavel.getCsaCodigo().equals(usu.getAttribute(Columns.UCA_CSA_CODIGO))).collect(Collectors.toList());

                        if ((usuariosTemp == null)  || usuariosTemp.isEmpty()) {
                            return ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.nao.pode.autorizar.operacao", responsavel);
                        }
                    }

                    usuarios = usuariosTemp;
                } else if (responsavel.isCor()) {
                    usuarios = usuarios.stream().filter(usu -> (((!TextHelper.isNull(usu.getAttribute(Columns.UCA_CSA_CODIGO)) && responsavel.getCsaCodigo().equals(usu.getAttribute(Columns.UCA_CSA_CODIGO))) || (!TextHelper.isNull(usu.getAttribute(Columns.UCO_COR_CODIGO)) && responsavel.getCorCodigo().equals(usu.getAttribute(Columns.UCO_COR_CODIGO)))))).collect(Collectors.toList());
                    if ((usuarios == null)  || usuarios.isEmpty()) {
                        return ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.nao.pode.autorizar.operacao", responsavel);
                    }
                }

                // Verifica se o usuário está bloqueado ou cancelado (removido)
                final List<TransferObject> usuariosExcluidos = usuarios.stream().filter(usu -> !TextHelper.isNull(usu.getAttribute(Columns.USU_STU_CODIGO)) && CodedValues.STU_EXCLUIDO.equals(usu.getAttribute(Columns.USU_STU_CODIGO))).collect(Collectors.toList());

                if ((usuariosExcluidos != null)  && !usuariosExcluidos.isEmpty()) {
                    return ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.senha.invalida", responsavel);
                }

                final List<TransferObject> usuariosBloqueados = usuarios.stream().filter(usu -> !TextHelper.isNull(usu.getAttribute(Columns.USU_STU_CODIGO)) && CodedValues.STU_CODIGOS_INATIVOS.contains(usu.getAttribute(Columns.USU_STU_CODIGO))).collect(Collectors.toList());
                if ((usuariosBloqueados != null)  && !usuariosBloqueados.isEmpty()) {
                    return ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.bloqueado.autorizar.operacao", responsavel);
                }
            }

            // Decriptografa a senha informada
            final KeyPair keyPair = LoginHelper.getRSAKeyPair(request);
            String senhaAberta = null;
            AcessoSistema usuAutentica = null;
            try {
                senhaAberta = RSA.decrypt(password, keyPair.getPrivate());
            } catch (final BadPaddingException ex) {
                return ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.senha.invalida", responsavel);
            }

            if (responsavel.isValidaTotp(true)) {
                usuario.setAtributos(usuarios.get(0).getAtributos());

                try {
                    final String usuChaveValidacaoTotp = !TextHelper.isNull(usuario.getAttribute(Columns.USU_CHAVE_VALIDACAO_TOTP)) ? usuario.getAttribute(Columns.USU_CHAVE_VALIDACAO_TOTP).toString() : null;

                    final GoogleAuthenticatorHelper authenticator = new GoogleAuthenticatorHelper();
                    long timeInMilliseconds = 0;
                    try {
                        final String strTimeInMilliseconds = JspHelper.verificaVarQryStr(request, "timeInMilliseconds");
                        timeInMilliseconds = !TextHelper.isNull(strTimeInMilliseconds) ? Long.parseLong(strTimeInMilliseconds) : 0;
                    } catch (final Exception ex) {
                        return ApplicationResourcesHelper.getMessage("mensagem.totp.codigo.invalido", responsavel);
                    }

                    if (!authenticator.checkCode(usuChaveValidacaoTotp, Long.parseLong(senhaAberta), timeInMilliseconds)) {
                        return ApplicationResourcesHelper.getMessage("mensagem.totp.codigo.invalido", responsavel);
                    }
                } catch (final Exception ex) {
                    return ApplicationResourcesHelper.getMessage("mensagem.totp.codigo.invalido", responsavel);
                }
            } else {
                final List<UsuarioTransferObject> usuarioTOs = usuarios.stream().map(usu -> {final UsuarioTransferObject usuTransf = new UsuarioTransferObject(); usuTransf.setAtributos(usu.getAtributos()); return usuTransf;}).collect(Collectors.toList());
                // configura AcessoSistema para o primeiro usuário temporáriamente. Após autenticação, seta este para do usuário correto.
                usuAutentica = new AcessoSistema(usuarioTOs.get(0).getUsuCodigo());
                usuAutentica.setIpUsuario((String) usuarioTOs.get(0).getAttribute(Columns.USU_IP_ACESSO));
                usuarios = UsuarioHelper.validarSenhaUsuarios(senhaAberta, usuarioTOs, false, false, null, usuAutentica);
                if ((usuarios == null) || usuarios.isEmpty()) {
                    return ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.senha.invalida", responsavel);
                }
            }

            usuario.setAtributos(usuarios.get(0).getAtributos());
            if (usuAutentica != null) {
                usuAutentica.setUsuCodigo((String) usuario.getAttribute(Columns.USU_CODIGO));
                usuAutentica.setIpUsuario((String) usuario.getAttribute(Columns.USU_IP_ACESSO));
            }

            // se sistema está configurado para bloquear automaticamente usuário na sua próxima autenticação, faz a verficação de bloqueio.
            if(!exigePropriaSenha && ParamSist.getBoolParamSist(CodedValues.TPC_BLOQUEIA_USU_INATIVIDADE_PROXIMA_AUTENTICACAO, responsavel) &&
            		UsuarioHelper.bloqueioAutomaticoPorInatividade((String) usuario.getAttribute(Columns.USU_CODIGO), responsavel)) {
            	return ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.bloqueado.autorizar.operacao", responsavel);
            }

            final String usuCodigo = usuario.getAttribute(Columns.USU_CODIGO).toString();
            final String usuIpAcesso = (usuario.getAttribute(Columns.USU_IP_ACESSO) != null ? usuario.getAttribute(Columns.USU_IP_ACESSO).toString() : "");
            final String usuDdnsAcesso = (usuario.getAttribute(Columns.USU_DDNS_ACESSO) != null ? usuario.getAttribute(Columns.USU_DDNS_ACESSO).toString() : "");

            String expirou = usuario.getAttribute("EXPIROU") != null ? usuario.getAttribute("EXPIROU").toString() : "1";

            // Se validou usuário no SSO, expiração de senha deve ser considerada do SSO
            if (!TextHelper.isNull(usuario.getAttribute(Columns.USU_AUTENTICA_SSO)) && CodedValues.TPC_SIM.equals(usuario.getAttribute(Columns.USU_AUTENTICA_SSO))) {
                expirou = "0";
            }

            if ("1".equals(expirou)) {
                return ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.senha.expirada.autorizar.operacao", responsavel);
            }

            UsuarioHelper.obterTipoEntidade(usuario);
            final String tipo = (String) usuario.getAttribute("TIPO_ENTIDADE");
            final String entidade = (String) usuario.getAttribute("COD_ENTIDADE");

            if (tipo == null) {
                return ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.invalido.autorizar.operacao", responsavel);
            }

            if (!usuarioController.usuarioTemPermissao(usuCodigo, funCodigo, tipo, responsavel)) {
                return ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.sem.permissao.autorizar.operacao", responsavel);
            }

            // Verifica se o perfil do usuário não está bloqueado
            final String perCodigo = (String)usuario.getAttribute(Columns.UPE_PER_CODIGO);
            if ((perCodigo != null) && !"".equals(perCodigo)) {
                final Short upeStatus = usuarioController.getStatusPerfil(tipo, entidade, perCodigo, responsavel);
                if ((upeStatus == null) || !upeStatus.equals(CodedValues.STS_ATIVO)) {
                    return ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.bloqueado.autorizar.operacao", responsavel);
                }
            }

            try {
                // Verifica obrigatoriedade e validade do IP/DDNS de acesso
                UsuarioHelper.verificarIpDDNSAcesso(tipo, entidade, JspHelper.getRemoteAddr(request), usuIpAcesso, usuDdnsAcesso, usuCodigo, responsavel);
            } catch (final ViewHelperException vex) {
                return ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.acesso.invalido.autorizar.operacao", responsavel);
            }

            // Cria o responsável pela confirmação com a segunda senha
            final AcessoSistema segundoResponsavel = new AcessoSistema(usuCodigo, JspHelper.getRemoteAddr(request), JspHelper.getRemotePort(request));
            segundoResponsavel.setTipoEntidade(tipo);
            segundoResponsavel.setCodigoEntidade(entidade);
            segundoResponsavel.setUsuNome(usuario.getAttribute(Columns.USU_NOME).toString());
            segundoResponsavel.setUsuLogin(usuario.getAttribute(Columns.USU_LOGIN).toString());
            segundoResponsavel.setFunCodigo(funCodigo);
            session.setAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA, segundoResponsavel);

            // Define observação para gravação de ocorrência de autorização com senha: será consumido pelas operações
            final StringBuilder ocaObs = new StringBuilder().append(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.operacao.sensivel", responsavel));
            if (responsavel.isValidaTotp(true)) {
                ocaObs.append(" ").append(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.operacao.com.codigo.seguranca", responsavel));
            } else if (exigePropriaSenha) {
                ocaObs.append(" ").append(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.operacao.com.propria.senha", responsavel));
            } else {
                ocaObs.append(" ").append(ApplicationResourcesHelper.getMessage("mensagem.confirmacao.operacao.com.segunda.senha", responsavel));
            }
            ocaObs.append(" ").append(recursoAcessado.getFunDescricao());
            session.setAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA, ocaObs.toString());

            // Grava log de autorização de operação sensível
            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.SISTEMA, Log.AUTORIZA_OP_2A_SENHA, Log.LOG_INFORMACAO);
            String logObs = "";
            if (responsavel.isValidaTotp(true)) {
                logObs = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.operacao.com.codigo.seguranca", responsavel);
            } else if (exigePropriaSenha) {
                logObs = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.operacao.com.propria.senha", responsavel);
            } else {
                logObs = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.operacao.com.segunda.senha", responsavel);
            }
            logDelegate.setResponsavel(segundoResponsavel);
            logDelegate.add(logObs);
            logDelegate.write();

            // Pula o filtro de segurança
            session.setAttribute(AcessoSistema.SEGUNDA_SENHA_AUTENTICADA, Boolean.TRUE);

        } catch (final ZetraException ex) {
            if ((ex.getMessageKey() == null) || !"mensagem.erro.usuario.senha.invalida".equals(ex.getMessageKey())) {
                // Grava no console de erro exceção que não é de senha inválida para que possa ser analisada
                LOG.error(ex.getMessage(), ex);
            }
            return ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.senha.invalida", responsavel);
        }

        return null;
    }

    private static String getURIFromURL(String url, HttpServletRequest request) {
    	final String context = request.getContextPath();
        final HttpSession session = request.getSession();
        final byte[] key = (byte[]) session.getAttribute(AES_KEY_SESSION_ATTRIBUTE);
        final byte[] iv = (byte[]) session.getAttribute(AES_INIT_VECTOR_SESSION_ATTRIBUTE);

        if (key != null && iv != null) {
        	try {
				final String uri = new String(Base64.getDecoder().decode(JspHelper.verificaVarQryStr(request, "xyz")));
				String plainUrl = StringEscapeUtils.unescapeHtml4(AES.decryptText(key, iv, uri));

				if (!TextHelper.isNull(plainUrl)) {
					while (plainUrl.charAt(0) == '.') {
						plainUrl = plainUrl.substring(1);
					}

					return plainUrl;
				}
			} catch (final Exception e) {
				// Não conseguiu realizar o decode, deve ser ignorado
			}
        }

        return url.substring(url.indexOf(context) + context.length(), (url.indexOf('?') > 0 ? url.indexOf('?') : url.length()));
    }

    protected String validarCaptchaOperacoesLiberacaoMargem(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model, AcessoSistema responsavel) {
        String msgRetorno = "";
        final String usuCodigo = responsavel.getUsuCodigo();

        final boolean defVisual = responsavel.isDeficienteVisual();
        if (!defVisual) {
            if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, responsavel)) {
                final String remoteAddr = request.getRemoteAddr();

                if (!isValidCaptcha(request.getParameter("g-recaptcha-response"), remoteAddr, responsavel)) {
                    msgRetorno = ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel);
                }
            } else {
                if (ImageCaptchaServlet.armazenaCaptcha(session.getId(), (String) session.getAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY))
                        && !ImageCaptchaServlet.validaCaptcha(session.getId(), request.getParameter("captcha"))) {
                    msgRetorno = ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel);
                }
                session.removeAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);
            }
        } else {
            final boolean exigeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
            if (exigeCaptchaDeficiente) {
                final String captchaAnswer = request.getParameter("captcha");

                if (captchaAnswer == null) {
                    msgRetorno = ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel);
                }

                final String captchaCode   = (String) session.getAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                if ((captchaCode == null) || !captchaCode.equalsIgnoreCase(captchaAnswer)) {
                    msgRetorno = ApplicationResourcesHelper.getMessage("mensagem.erro.captcha.invalido", responsavel);
                }
                session.removeAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
            }
        }

        if (TextHelper.isNull(msgRetorno)) {
            ControleConsulta.getInstance().somarValorCaptcha(usuCodigo);
        }
        return msgRetorno;
    }
}
