package com.zetra.econsig.helper.usuario;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeansException;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.OcorrenciaUsuarioTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.dto.web.SSOToken;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.SSOException;
import com.zetra.econsig.exception.SenhaExpiradaException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.criptografia.JCrypt;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.ControleLogin;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.Perfil;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webclient.faces.FacesWebServiceClient;
import com.zetra.econsig.webclient.sso.SSOClient;
import com.zetra.econsig.webclient.sso.SSOErrorCodeEnum;

public final class UsuarioHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(UsuarioHelper.class);

    public static final String LAYOUT_V4 = "v4";

    /**
     * busca usuário(s) dado pelo id
     * @param usuId - pode ser o login ou e-mail do usuário
     * @return
     * @throws ViewHelperException
     */
    public static List<TransferObject> localizarUsuario (String usuId, AcessoSistema responsavel) throws ViewHelperException {
        try {
            final UsuarioController usuarioController = ApplicationContextProvider.getApplicationContext().getBean(UsuarioController.class);

            // tenta primeira busca pelo login
            final TransferObject usuario = usuarioController.findTipoUsuarioByLogin(usuId, responsavel);

            if (usuario != null) {
                return List.of(usuario);
            }

            // Busca assumindo usuId como e-mail
            return usuarioController.findUsuarioByEmail(usuId, responsavel);
        } catch (final UsuarioControllerException e) {
            throw new ViewHelperException(e);
        }
    }

    /**
     * verifica se usuário autentica via serviço SSO. a configuração específica do registro do usuário tem precedência sobre a de sistema para o papel
     * @param usuario
     * @param responsavel
     * @return
     */
    public static boolean usuarioAutenticaSso(TransferObject usuario, AcessoSistema responsavel) {
        // Se usuário é nulo ou está no leiaute v2, então não tem SSO
        if (usuario == null) {
            return false;
        }

        // Se a configuração do usuário que informa se autentica via SSO está preenchida, então retorna
        // esta informação, seja ela igual a Sim ou Não
        final String usuAutenticaSso = (String) usuario.getAttribute(Columns.USU_AUTENTICA_SSO);
        if (!TextHelper.isNull(usuAutenticaSso)) {
            return CodedValues.TPC_SIM.equals(usuAutenticaSso);
        }

        // Se a configuração do usuário está nula, verifica o parâmetro de sistema de acordo com o papel do usuário
        boolean sistemaConfiguradoSso = false;

        if (!TextHelper.isNull(usuario.getAttribute(Columns.UCE_CSE_CODIGO)) || !TextHelper.isNull(usuario.getAttribute(Columns.UOR_ORG_CODIGO))) {
            sistemaConfiguradoSso = ParamSist.paramEquals(CodedValues.TPC_AUTENTICACAO_SSO_CSE_ORG, CodedValues.TPC_SIM, responsavel);
        } else if (!TextHelper.isNull(usuario.getAttribute(Columns.UCA_CSA_CODIGO)) || !TextHelper.isNull(usuario.getAttribute(Columns.UCO_COR_CODIGO))) {
            sistemaConfiguradoSso = ParamSist.paramEquals(CodedValues.TPC_AUTENTICACAO_SSO_CSA_COR, CodedValues.TPC_SIM, responsavel);
        } else if (!TextHelper.isNull(usuario.getAttribute(Columns.USP_CSE_CODIGO))) {
            sistemaConfiguradoSso = ParamSist.paramEquals(CodedValues.TPC_AUTENTICACAO_SSO_SUP, CodedValues.TPC_SIM, responsavel);
        } else if (!TextHelper.isNull(usuario.getAttribute(Columns.USE_SER_CODIGO))) {
            sistemaConfiguradoSso = ParamSist.paramEquals(CodedValues.TPC_AUTENTICACAO_SSO_SER, CodedValues.TPC_SIM, responsavel);
        }

        return sistemaConfiguradoSso;
    }

    public static boolean usuarioAutenticaSso(UsuarioTransferObject usuario, String tipoEntidade, AcessoSistema responsavel) {
        // Se usuário é nulo ou está no leiaute v2, então não tem SSO
        if (usuario == null) {
            return false;
        }

        // Se a configuração do usuário que informa se autentica via SSO está preenchida, então retorna
        // esta informação, seja ela igual a Sim ou Não
        final String usuAutenticaSso = usuario.getUsuAutenticaSso();
        if (!TextHelper.isNull(usuAutenticaSso)) {
            return CodedValues.TPC_SIM.equals(usuAutenticaSso);
        }

        // Se a configuração do usuário está nula, verifica o parâmetro de sistema de acordo com o papel do usuário
        boolean sistemaConfiguradoSso = false;

        if (AcessoSistema.ENTIDADE_CSE.equals(tipoEntidade) || AcessoSistema.ENTIDADE_ORG.equals(tipoEntidade)) {
            sistemaConfiguradoSso = ParamSist.paramEquals(CodedValues.TPC_AUTENTICACAO_SSO_CSE_ORG, CodedValues.TPC_SIM, responsavel);
        } else if (AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade) || AcessoSistema.ENTIDADE_COR.equals(tipoEntidade)) {
            sistemaConfiguradoSso = ParamSist.paramEquals(CodedValues.TPC_AUTENTICACAO_SSO_CSA_COR, CodedValues.TPC_SIM, responsavel);
        } else if (AcessoSistema.ENTIDADE_SER.equals(tipoEntidade)) {
            sistemaConfiguradoSso = ParamSist.paramEquals(CodedValues.TPC_AUTENTICACAO_SSO_SUP, CodedValues.TPC_SIM, responsavel);
        } else if (AcessoSistema.ENTIDADE_SUP.equals(tipoEntidade)) {
            sistemaConfiguradoSso = ParamSist.paramEquals(CodedValues.TPC_AUTENTICACAO_SSO_SER, CodedValues.TPC_SIM, responsavel);
        }

        return sistemaConfiguradoSso;
    }

    public static List<TransferObject> autenticarUsuarios(String senha, List<UsuarioTransferObject> usuarios, String token, AcessoSistema responsavel) throws ViewHelperException {
        return validarSenhaUsuarios(senha, usuarios, true, true, token, responsavel);
    }

    /**
     * Valida a senha dos usuários passados no parâmetro usuarios.
     * Retorna uma lista apenas daqueles usuários cuja senha validou corretamente.
     * @param senha - senha aberta a ser validada
     * @param usuarios - usuários que serão validados pela senha
     * @param salvarToken - grava o token de autenticação do serviço de autenticação no AcessoSistema responsavel (apenas para autenticação remota)
     * @param bloqueiaSenhaErrada - bloqueia usuário caso o número de tentativas erradas de login configurado no sistema tenha sido alcançado
     * @param token - Token que deve ser validado no SSO
     * @param responsavel - AcessoSistema que representará os usuários autenticados pela senha
     * @return
     * @throws ViewHelperException
     */
    public static List<TransferObject> validarSenhaUsuarios(String senha, List<UsuarioTransferObject> usuarios, boolean salvarToken, boolean bloqueiaSenhaErrada, String token, AcessoSistema responsavel) throws ViewHelperException {
        List<TransferObject> usuariosValidados = new ArrayList<>();
        if ((usuarios == null) || usuarios.isEmpty()) {
            return usuariosValidados;
        }

        final List<Boolean> autenticaSsoSet = usuarios.stream().map(usu -> usuarioAutenticaSso(usu, responsavel)).collect(Collectors.toList());

        //Se houver apenas um usuário de mesmo e-mail com usu_autentica_sso = S, então esse usuário será validado para acesso.
        if ((autenticaSsoSet.size() > 1) && autenticaSsoSet.contains(Boolean.TRUE)) {
            final List<UsuarioTransferObject> usuarioSSO = new ArrayList<>();
            for (final UsuarioTransferObject usuario : usuarios) {
                if (!TextHelper.isNull(usuario.getAttribute(Columns.USU_AUTENTICA_SSO)) && CodedValues.TPC_SIM.equals(usuario.getAttribute(Columns.USU_AUTENTICA_SSO))) {
                    usuarioSSO.add(usuario);
                }
            }

            usuariosValidados = validaSenhaSSO(senha, usuarioSSO, salvarToken, bloqueiaSenhaErrada, token, responsavel);
        } else if ((autenticaSsoSet.size() == 1) && autenticaSsoSet.contains(Boolean.FALSE)) {
            for (final UsuarioTransferObject usuario : usuarios) {
                final String senhaSalva = usuario.getUsuSenha();
                if (JCrypt.verificaSenha(senha, senhaSalva)) {
                    usuariosValidados.add(usuario);
                }
            }

            // Apenas se nenhum dos usuários tiver a senha válida faz o controle de quantidade de tentativas.
            if (usuariosValidados.isEmpty()) {
                controleNumTentativasAutenticacao(usuarios, bloqueiaSenhaErrada, responsavel);
            }

        } else if ((autenticaSsoSet.size() == 1) && autenticaSsoSet.contains(Boolean.TRUE)) {
            // verifica se todos usuários estão com o e-mail preenchido e se este é único
            final List<UsuarioTransferObject> usuSemEmail = usuarios.stream().filter(usu -> TextHelper.isNull(usu.getUsuEmail())).collect(Collectors.toList());

            if ((usuSemEmail == null) || !usuSemEmail.isEmpty()) {
                throw new ViewHelperException("mensagem.usuarioSenhaInvalidos", responsavel);
            }
            final Set<String> emailList = usuarios.stream().map(UsuarioTransferObject::getUsuEmail).collect(Collectors.toSet());
            // e-mail deve ser único entre usuários
            if (emailList.size() > 1) {
                throw new ViewHelperException("mensagem.usuarioSenhaInvalidos", responsavel);
            }

            usuariosValidados = validaSenhaSSO(senha, usuarios, salvarToken, bloqueiaSenhaErrada, token, responsavel);
        } else {
            verificaConsistenciaConfigSSO(usuarios, responsavel);
        }

        // Se a lista inicial contém multiplos usuários, caso algum não tenha CPF ou os CPFs sejam diferentes retorna mensagem de erro ao usuário
        if (usuarios.size() > 1) {
            final Set<String> cpfSet = usuarios.stream().map(UsuarioTransferObject::getUsuCPF).collect(Collectors.toSet());
            if ((cpfSet == null) || cpfSet.isEmpty() || cpfSet.contains(null) || (cpfSet.size() > 1)) {
                // Se pelo menos algum usuário da lista foi autenticado com sucesso, então retorna mensagem mais descritiva
                if (!usuariosValidados.isEmpty()) {
                    throw new ViewHelperException("mensagem.erro.autenticacao.email.duplicado", responsavel);
                } else {
                    // Caso contrário, retorna erro de usuário e/ou senha inválidos
                    throw new ViewHelperException("mensagem.usuarioSenhaInvalidos", responsavel);
                }
            }
        }

        return usuariosValidados;
    }

    /**
     * verifica se todos os usuários com mesmo email/cpf no sistema possuem a mesma configuração
     * @param usuarios
     * @param responsavel
     * @throws ViewHelperException
     */
	private static void verificaConsistenciaConfigSSO(List<UsuarioTransferObject> usuarios, AcessoSistema responsavel)
			throws ViewHelperException {
		final String identificacaoUsu = !TextHelper.isNull(usuarios.get(0).getUsuEmail()) ? usuarios.get(0).getUsuEmail() : usuarios.get(0).getUsuCPF();

		LOG.error(ApplicationResourcesHelper.getMessage("mensagem.usuario.erro.configuracao.tipo.autenticacao", (AcessoSistema) null, identificacaoUsu));
		final LogDelegate log = new LogDelegate(responsavel, Log.SISTEMA, Log.LOGIN, Log.LOG_LOGIN_ERRO);
		try {
		    log.add(ApplicationResourcesHelper.getMessage("mensagem.usuario.erro.configuracao.tipo.autenticacao", (AcessoSistema) null, identificacaoUsu));
		    log.write();
		} catch (final LogControllerException ex) {
		    LOG.error(ex.getMessage(), ex);
		}

		throw new ViewHelperException("mensagem.usuarioSenhaInvalidos", responsavel);
	}

    /**
     * valida a senha no serviço SSO para os usuários lógicos no sistema para um indivíduo.
     * @param senha - senha a validar
     * @param usuarios - usuários que um indivíduo pode ter no sistema.
     * @param salvarToken - se o token gerado deve ser gravado no acesso recurso
     * @param bloqueiaSenhaErrada - se bloqueia o usuário caso a validação retorne falso.
     * @param token - se faz a validação via um token dado. Se for nulo, valida a senha passada por parâmetro.
     * @param responsavel
     * @return
     * @throws ViewHelperException
     */
	public static List<TransferObject> validaSenhaSSO(String senha, List<UsuarioTransferObject> usuarios, boolean salvarToken, boolean bloqueiaSenhaErrada, String token, AcessoSistema responsavel) throws ViewHelperException {
		if ((usuarios == null) || usuarios.isEmpty()) {
			return new ArrayList<>();
		}

		final List<TransferObject> usuariosValidados = new ArrayList<>();

		final SSOClient ssoClient = ApplicationContextProvider.getApplicationContext().getBean(SSOClient.class);

		try {
		    final String clientId = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_SSO_OAUTH_CLIENT_ID, responsavel)) ? ParamSist.getInstance().getParam(CodedValues.TPC_SSO_OAUTH_CLIENT_ID, responsavel).toString() : "";

		    SSOToken ssoToken = null;
		    if (TextHelper.isNull(token)) {
		    	ssoToken = ssoClient.autenticar(usuarios.get(0).getUsuEmail(), senha);
		    } else if (!ssoClient.isTokenValido(usuarios.get(0).getUsuEmail(), clientId, token)) {
		        throw new ViewHelperException("mensagem.usuarioSenhaInvalidos", responsavel);
		    }

		    // Se validou usuário no SSO, expiração de senha deve ser considerada do SSO
		    if (salvarToken) {
			    responsavel.setSsoToken(ssoToken);
			    if (!usuariosValidados.isEmpty()) {
			        responsavel.setUsuEmail(usuarios.get(0).getUsuEmail());
			    }
			}

		    usuarios = usuarios.stream().map(usu -> {
		        usu.setAttribute("EXPIROU", "0");
		        return usu;
		    }).collect(Collectors.toList());

		    usuariosValidados.addAll(usuarios);
		} catch (final SSOException e) {
		    tratarSSOException(e, usuarios, salvarToken, bloqueiaSenhaErrada, usuariosValidados, responsavel);
		}

		return usuariosValidados;
	}

	private static void tratarSSOException(SSOException e, List<UsuarioTransferObject> usuarios, boolean salvarToken,
			boolean bloqueiaSenhaErrada, List<TransferObject> usuariosValidados, AcessoSistema responsavel) throws ViewHelperException {
		if ("mensagem.usuarioSenhaInvalidos".equals(e.getMessageKey())) {
		    if ((e.getSsoError() == null) || (!SSOErrorCodeEnum.ACCOUNT_PASSWORD_IS_EXPIRED.equals(e.getSsoError()) && !SSOErrorCodeEnum.GENERIC_ERROR.equals(e.getSsoError()))) {
		        controleNumTentativasAutenticacao(usuarios, bloqueiaSenhaErrada, responsavel);
		    } else if (SSOErrorCodeEnum.ACCOUNT_PASSWORD_IS_EXPIRED.equals(e.getSsoError())) {
		        usuarios = usuarios.stream().map(usu -> {
		            usu.setAttribute("EXPIROU", "1");
		            return usu;
		        }).collect(Collectors.toList());

		        usuariosValidados.addAll(usuarios);
		        final SSOToken ssoToken = new SSOToken();
		        ssoToken.error_code = SSOErrorCodeEnum.ACCOUNT_PASSWORD_IS_EXPIRED.getErrorCode();
		        if (salvarToken) {
				    responsavel.setSsoToken(ssoToken);
				    if (!usuariosValidados.isEmpty()) {
				        responsavel.setUsuEmail(usuarios.get(0).getUsuEmail());
				    }
				}
		    }
		} else {
		    throw new ViewHelperException(e);
		}
	}

	private static void controleNumTentativasAutenticacao(List<UsuarioTransferObject> usuarios, boolean bloqueiaSenhaErrada, AcessoSistema responsavel) throws ViewHelperException {
		ViewHelperException numMaxTentativasException = null;

		if (bloqueiaSenhaErrada) {
		    for (final UsuarioTransferObject usuario : usuarios) {
		        try {
                    final AcessoSistema usuAcesso = AcessoSistema.recuperaAcessoSistema(usuario.getUsuCodigo(), responsavel.getIpUsuario(), responsavel.getPortaLogicaUsuario());
		            ControleLogin.getInstance().bloqueiaUsuario(usuario, usuAcesso);
		        } catch (final ZetraException e) {
		            if (numMaxTentativasException == null) {
		                // guarda exceção por enquanto para poder registrar tentativas para todos usuários
		                numMaxTentativasException = new ViewHelperException(e);
		            }
		        }
		    }
		}

		if (numMaxTentativasException != null) {
		    throw numMaxTentativasException;
		}
	}

    public static TransferObject autenticarUsuario(String usuLogin, String usuSenha, AcessoSistema responsavel) throws ViewHelperException {
        return autenticarUsuario(usuLogin, usuSenha, false, false, true, null, responsavel);
    }

    public static TransferObject autenticarUsuario(String usuLogin, String usuSenha, boolean senhaApp, AcessoSistema responsavel) throws ViewHelperException {
        return autenticarUsuario(usuLogin, usuSenha, senhaApp, false, true, null, responsavel);
    }

    public static TransferObject autenticarUsuario(String usuLogin, String usuSenha, boolean senhaApp, boolean permiteAguardAprovacaoCadastro, boolean autenticarUsuario, AcessoSistema responsavel) throws ViewHelperException {
        return autenticarUsuario(usuLogin, usuSenha, senhaApp, permiteAguardAprovacaoCadastro, autenticarUsuario, null, responsavel);
    }

    public static TransferObject autenticarUsuario(String usuLogin, String usuSenha, boolean senhaApp, boolean permiteAguardAprovacaoCadastro, boolean autenticarUsuario, TransferObject servidor, AcessoSistema responsavel) throws ViewHelperException {
        try {
            final UsuarioController usuarioController = ApplicationContextProvider.getApplicationContext().getBean(UsuarioController.class);
            TransferObject usuario = usuarioController.findTipoUsuarioByLogin(usuLogin, AcessoSistema.getAcessoUsuarioSistema());
            if (usuario == null) {
                // Busca pelo usuLogin assumindo que é e-mail
                final List<TransferObject> usuariosListTO = usuarioController.findUsuarioByEmail(usuLogin, responsavel);

                // Recupera o usuário caso seja somente um
                if ((usuariosListTO != null) && !usuariosListTO.isEmpty() && (usuariosListTO.size() == 1)) {
                	usuario = usuariosListTO.get(0);
                }

                // Levanta exceção se usuário ainda não foi localizado
                if (usuario == null) {
                	throw new ViewHelperException("mensagem.usuarioSenhaInvalidos", responsavel);
                }
            }

            // Verifica a licença do eConsig
            final String licenca = (String) usuario.getAttribute(Columns.CSE_LICENCA);
            final String publicKeyCentralizador = (String) usuario.getAttribute(Columns.CSE_RSA_PUBLIC_KEY_CENTRALIZADOR);
            final String modulusCentralizador = (String) usuario.getAttribute(Columns.CSE_RSA_MODULUS_CENTRALIZADOR);
            if (isLicencaExpirada(licenca, publicKeyCentralizador, modulusCentralizador)) {
            	throw new ViewHelperException("mensagem.licencaSistemaInvalida", responsavel);
            }

            // se sistema está configurado para bloquear automaticamente usuário na sua próxima autenticação, faz a verficação de bloqueio.
            boolean bloqueadoPorInatividade = false;
            if(ParamSist.getBoolParamSist(CodedValues.TPC_BLOQUEIA_USU_INATIVIDADE_PROXIMA_AUTENTICACAO, responsavel)) {
            	bloqueadoPorInatividade = UsuarioHelper.bloqueioAutomaticoPorInatividade((String) usuario.getAttribute(Columns.USU_CODIGO), responsavel);
            }

            if (!TextHelper.isNull(usuario.getAttribute(Columns.USE_SER_CODIGO))) {
                // Usuário Servidor, realiza autenticação integrada com senha externa, caso exista
                // Se a validação não for externa e não for login automático, valida a senha do servidor.
                final boolean loginComEstOrg = ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, responsavel);
                final String[] partesLogin = usuLogin.split("-");
                String matricula;
                // As partes são: est[0] -- org[1] -- matricula[2] -- digito matricula[3]
                if (loginComEstOrg && (partesLogin.length > 3)) {
                	matricula = (partesLogin[2] + "-" + partesLogin[3]);
                } else if (loginComEstOrg) {
                	matricula = (partesLogin[2]);
                } else if(partesLogin.length > 2) {
                	matricula = (partesLogin[1] + "-" + partesLogin[2]);
                } else {
                	matricula = (partesLogin[1]);
                }

                final String estIdentificador = partesLogin[0];
                final String orgIdentificador = (loginComEstOrg ? partesLogin[1] : null);


                try {
                    try {
                        if (servidor == null) {
                            final ConsignanteController consignanteController = ApplicationContextProvider.getApplicationContext().getBean(ConsignanteController.class);
                            final EstabelecimentoTransferObject estabelecimento = consignanteController.findEstabelecimentoByIdn(estIdentificador, responsavel);
                            final String estCodigo = estabelecimento.getEstCodigo();
                            String orgCodigo = null;
                            if (loginComEstOrg) {
                                final OrgaoTransferObject orgao = consignanteController.findOrgaoByIdn(orgIdentificador, estCodigo, responsavel);
                                orgCodigo = orgao.getOrgCodigo();
                            }

                            final ServidorController servidorController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);
                            servidor = servidorController.getRegistroServidorPelaMatricula((String) usuario.getAttribute(Columns.USE_SER_CODIGO), orgCodigo, estCodigo, matricula, responsavel);
                            if (servidor == null) {
                                throw new ViewHelperException("mensagem.usuarioSenhaInvalidos", responsavel);
                            }
                        }

                        if (autenticarUsuario) {
                        	if (usuarioAutenticaSso(usuario, responsavel)) {
                        		final SSOClient ssoClient = ApplicationContextProvider.getApplicationContext().getBean(SSOClient.class);
                        		final SSOToken ssoToken = ssoClient.autenticar(usuario.getAttribute(Columns.USU_EMAIL).toString(), usuSenha);
                        		usuario.setAttribute("EXPIROU", "0");
                                usuario.setAttribute("SSO_TOKEN", ssoToken);
                        	} else {
                        		try {
                        			SenhaHelper.validarSenhaServidor(servidor.getAttribute(Columns.RSE_CODIGO).toString(), usuSenha, responsavel.getIpUsuario(), null, null, false, true, senhaApp, responsavel);
                        			usuario.setAttribute("EXPIROU", "0");
                        		} catch (final SenhaExpiradaException ex) {
                        			usuario.setAttribute("EXPIROU", "1");
                        		}
                        	}
                        }
            		} catch (ConsignanteControllerException | ServidorControllerException | UsuarioControllerException | SSOException ex) {
                        LOG.error(ex.getMessage(), ex);
                        throw new ViewHelperException(ex);
                    }
        		} catch (final ViewHelperException ex) {
                    try {
                        final LogDelegate log = new LogDelegate(responsavel, Log.SISTEMA, Log.LOGIN, Log.LOG_LOGIN_ERRO);
                        log.add(ApplicationResourcesHelper.getMessage("mensagem.log.matricula.arg0", (AcessoSistema) null, matricula));
                        log.add(ApplicationResourcesHelper.getMessage("mensagem.log.erro.arg0", (AcessoSistema) null, ex.getMessage()));
                        if (CanalEnum.REST.equals(responsavel.getCanal())) {
                            log.add(ApplicationResourcesHelper.getMessage("rotulo.log.origem.mobile", responsavel));
                        }

                        log.write();
                    } catch (final LogControllerException le) {
                        LOG.error(le);
                    }

                    throw ex;
                }
            } else {
                final String senha = usuario.getAttribute(Columns.USU_SENHA).toString();

                if (autenticarUsuario) {
                	if (usuarioAutenticaSso(usuario, responsavel)) {
                		try {
                			final SSOClient ssoClient = ApplicationContextProvider.getApplicationContext().getBean(SSOClient.class);
                			ssoClient.autenticar(usuario.getAttribute(Columns.USU_EMAIL).toString(), usuSenha);
                			usuario.setAttribute("EXPIROU", "0");
                		} catch (BeansException | SSOException e) {
                			throw new ViewHelperException(e);
                		}
                	} else // Verifica a senha do usuário
                    if (!JCrypt.verificaSenha(usuSenha, senha)) {
                    	try {
                    		// verifica se já atingiu o num max de tentativas de login definidas em parâmetro de sistema
                    		// se sim, bloqueia o usuário
                    		ControleLogin.getInstance().bloqueiaUsuario(usuario, responsavel);
                    	} catch (final ZetraException ze) {
                    		throw new ViewHelperException(ze);
                    	}
                    	throw new ViewHelperException("mensagem.usuarioSenhaInvalidos", responsavel);
                    }
                }
            }

            // se a senha está correta, limpa cache de tentativas de login para este usuário.
            ControleLogin.getInstance().resetTetantivasLogin((String) usuario.getAttribute(Columns.USU_CODIGO));

            // Verifica se a senha já expirou
            final String expirou = usuario.getAttribute("EXPIROU") != null ? usuario.getAttribute("EXPIROU").toString() : "1";
            if ("1".equals(expirou)) {
                boolean senhaExpirada = true;
                throw new ViewHelperException("mensagem.senhaExpirada", senhaExpirada, responsavel);
            }

            // Verifica o status do usuário
            final String stuCodigo = usuario.getAttribute(Columns.USU_STU_CODIGO) != null ? usuario.getAttribute(Columns.USU_STU_CODIGO).toString() : CodedValues.STU_BLOQUEADO;
            if (CodedValues.STU_EXCLUIDO.equals(stuCodigo)) {
                throw new ViewHelperException("mensagem.usuarioSenhaInvalidos", responsavel);
            } else if (bloqueadoPorInatividade || CodedValues.STU_CODIGOS_INATIVOS.contains(stuCodigo)) {
                throw new ViewHelperException("mensagem.usuarioBloqueado", responsavel);
            } else if (CodedValues.STU_AGUARD_APROVACAO_CADASTRO.equals(stuCodigo) && (servidor != null)) {
                try {
                    FacesWebServiceClient.verificarCadastro((String) usuario.getAttribute(Columns.USU_CODIGO), (String) servidor.getAttribute(Columns.SER_CPF), responsavel);
                } catch (final UsuarioControllerException ex) {
                    if (!permiteAguardAprovacaoCadastro) {
                        throw new ViewHelperException(ex);
                    }
                }
            }

            obterTipoEntidade(usuario);
            final String tipo = (String) usuario.getAttribute("TIPO_ENTIDADE");
            final String entidade = (String) usuario.getAttribute("COD_ENTIDADE");

            // Verifica se o perfil do usuário não está bloqueado
            final String perCodigo = (String)usuario.getAttribute(Columns.UPE_PER_CODIGO);
            if ((perCodigo != null) && !"".equals(perCodigo)) {
                Short upeStatus = usuarioController.getStatusPerfil(tipo, entidade, perCodigo, responsavel);
                if ((upeStatus == null) || !upeStatus.equals(CodedValues.STS_ATIVO)) {
                    throw new ViewHelperException("mensagem.usuarioBloqueado", responsavel);
                }
            }

            final String usuCodigo = usuario.getAttribute(Columns.USU_CODIGO) != null ? usuario.getAttribute(Columns.USU_CODIGO).toString() : "";

            final String usuCpf = usuario.getAttribute(Columns.USU_CPF) != null ? usuario.getAttribute(Columns.USU_CPF).toString() : "";
            final String usuEmail = usuario.getAttribute(Columns.USU_EMAIL) != null ? usuario.getAttribute(Columns.USU_EMAIL).toString() : "";
            final String usuIPAcesso = usuario.getAttribute(Columns.USU_IP_ACESSO) != null ? usuario.getAttribute(Columns.USU_IP_ACESSO).toString() : "";
            final String usuDDNSAcesso = usuario.getAttribute(Columns.USU_DDNS_ACESSO) != null ? usuario.getAttribute(Columns.USU_DDNS_ACESSO).toString() : "";
            final String endereco = responsavel.getIpUsuario();

            final String usuCentralizador = (String) usuario.getAttribute(Columns.USU_CENTRALIZADOR);
            if (CodedValues.TPC_SIM.equals(usuCentralizador)) {
                // Verifica usuario de centralizador
                verificarUsuarioCentralizador(endereco, responsavel);
                if (!TextHelper.isNull(responsavel.getIpOrigem())) {
                    responsavel.setIpUsuario(responsavel.getIpOrigem());
                    responsavel.setPortaLogicaUsuario(responsavel.getPortaLogicaUsuario());
                }
            } else {
                // Verifica obrigatoriedade e validade do IP/DDNS de acesso
                verificarIpDDNSAcesso(tipo, entidade, endereco, usuIPAcesso, usuDDNSAcesso, usuCodigo, responsavel);
            }

            // Verifica obrigatoriedade de CPF para acesso ao sistema
            verificarCpfUsuario(tipo, usuCpf, responsavel);

            // Verifica obrigatoriedade de email para acesso ao sistema
            verificarEmailUsuario(tipo, usuEmail, responsavel);

            // Altera a data de último acesso do usuário
            final CanalEnum canal = responsavel.getCanal();
            try {
                final AcessoSistema acesso = new AcessoSistema(usuCodigo);
                acesso.setCanal(canal);
                usuarioController.alteraDataUltimoAcessoSistema(acesso);
            } catch (final UsuarioControllerException e) {
                LOG.error("Não foi possível alterar a data de último acesso ao sistema para o usuário: ['" + usuCodigo + "'].");
            }

            return usuario;

        } catch (final UsuarioControllerException ex) {
            throw new ViewHelperException("mensagem.usuarioSenhaInvalidos", responsavel);
        }
    }

    public static String obterTipoEntidade(TransferObject usuario) {
        String tipo = "";
        String entidade = "";

        final String cseCodigo = usuario.getAttribute(Columns.UCE_CSE_CODIGO) != null ? usuario.getAttribute(Columns.UCE_CSE_CODIGO).toString() : "";
        final String csaCodigo = usuario.getAttribute(Columns.UCA_CSA_CODIGO) != null ? usuario.getAttribute(Columns.UCA_CSA_CODIGO).toString() : "";
        final String corCodigo = usuario.getAttribute(Columns.UCO_COR_CODIGO) != null ? usuario.getAttribute(Columns.UCO_COR_CODIGO).toString() : "";
        final String orgCodigo = usuario.getAttribute(Columns.UOR_ORG_CODIGO) != null ? usuario.getAttribute(Columns.UOR_ORG_CODIGO).toString() : "";
        final String serCodigo = usuario.getAttribute(Columns.USE_SER_CODIGO) != null ? usuario.getAttribute(Columns.USE_SER_CODIGO).toString() : "";
        final String uspCseCodigo = usuario.getAttribute(Columns.USP_CSE_CODIGO) != null ? usuario.getAttribute(Columns.USP_CSE_CODIGO).toString() : "";

        // Determina o tipo da entidade do usuário
        if (!"".equals(cseCodigo)) {
            tipo = AcessoSistema.ENTIDADE_CSE;
            entidade = cseCodigo;
        } else if (!"".equals(csaCodigo)) {
            tipo = AcessoSistema.ENTIDADE_CSA;
            entidade = csaCodigo;
        } else if (!"".equals(corCodigo)) {
            tipo = AcessoSistema.ENTIDADE_COR;
            entidade = corCodigo;
        } else if (!"".equals(orgCodigo)) {
            tipo = AcessoSistema.ENTIDADE_ORG;
            entidade = orgCodigo;
        } else if (!"".equals(serCodigo)) {
            tipo = AcessoSistema.ENTIDADE_SER;
            entidade = serCodigo;
        } else if (!"".equals(uspCseCodigo)) {
            tipo = AcessoSistema.ENTIDADE_SUP;
            entidade = uspCseCodigo;
        }

        usuario.setAttribute("TIPO_ENTIDADE", tipo);
        usuario.setAttribute("COD_ENTIDADE", entidade);

        return tipo;
    }

    /**
     * Verifica se a licença do eConsig está válida.
     * @param licenca
     * @param rsaPublicKeyCentralizador
     * @param rsaModulusCentralizador
     * @return
     */
    public static boolean isLicencaExpirada(String licenca, String rsaPublicKeyCentralizador, String rsaModulusCentralizador) {
        // DESENV-21369 : Nunca foi usado e consome recursos sem ter possibilidade de desabilitar
        return false;
        /*
        if (TextHelper.isNull(licenca)) {
            // Se a licença é nula, é porque não foi possível conectar ao validador de licença e a licença deve ser considerada expirada.
            return true;
        }
        final Key publicKeyCentralizador = RSA.generatePublicKey(rsaModulusCentralizador, rsaPublicKeyCentralizador);
        final Key privateKeyEConsig = RSA.generatePrivateKey(CodedValues.RSA_MODULUS_ECONSIG, CodedValues.RSA_PRIVATE_KEY_ECONSIG);

        // Descriptografa com as chaves do eConsig, e com a chave publica do centralizador
        String message;
        try {
            message = RSA.decrypt(licenca, privateKeyEConsig);
            message = RSA.decrypt(message, publicKeyCentralizador);
        } catch (final BadPaddingException e) {
            // Tentativa de decriptografia com chave inválida.
            LOG.error(e.getMessage(), e);
            message = null;
        }

        final Calendar calendarLicenca = Calendar.getInstance();
        calendarLicenca.setTimeInMillis(Long.parseLong(message));

        return calendarLicenca.before(Calendar.getInstance());
        */
    }

    public static String getPapCodigo(String tipo) {
        tipo = tipo.toUpperCase();
        String papCodigo = null;

        if (AcessoSistema.ENTIDADE_CSE.equalsIgnoreCase(tipo)) { // CONSIGNANTE
            papCodigo = CodedValues.PAP_CONSIGNANTE;
        } else if (AcessoSistema.ENTIDADE_CSA.equalsIgnoreCase(tipo)) { // CONSIGNATARIA
            papCodigo = CodedValues.PAP_CONSIGNATARIA;
        } else if (AcessoSistema.ENTIDADE_EST.equalsIgnoreCase(tipo)) { // ESTABELECIMENTO
            papCodigo = CodedValues.PAP_ESTABELECIMENTO;
        } else if (AcessoSistema.ENTIDADE_ORG.equalsIgnoreCase(tipo)) { // ORGAO
            papCodigo = CodedValues.PAP_ORGAO;
        } else if (AcessoSistema.ENTIDADE_COR.equalsIgnoreCase(tipo)) { // CORRESPONDENTE
            papCodigo = CodedValues.PAP_CORRESPONDENTE;
        } else if (AcessoSistema.ENTIDADE_SER.equalsIgnoreCase(tipo)) { // SERVIDOR
            papCodigo = CodedValues.PAP_SERVIDOR;
        } else if (AcessoSistema.ENTIDADE_SUP.equalsIgnoreCase(tipo)) { // SUPORTE
            papCodigo = CodedValues.PAP_SUPORTE;
        }

        return papCodigo;
    }

    /**
     * Retorna um array de string de duas posições, contendo o IP e o DNS de acesso
     * cadastrado na entidade do usuário responsável.
     * @param responsavel
     * @return
     * @throws ViewHelperException
     */
    public static String[] obtemRestricaoIpDDNSEntidadeUsu(AcessoSistema responsavel) throws ViewHelperException {
        try {
            if (responsavel.isCse()) {
                final ConsignanteController consignanteController = ApplicationContextProvider.getApplicationContext().getBean(ConsignanteController.class);
                final ConsignanteTransferObject cse = consignanteController.findConsignante(responsavel.getCodigoEntidade(), responsavel);
                return new String[] { cse.getCseIPAcesso(), cse.getCseDDNSAcesso() };

            } else if (responsavel.isOrg()) {
                final ConsignanteController consignanteController = ApplicationContextProvider.getApplicationContext().getBean(ConsignanteController.class);
                final OrgaoTransferObject org = consignanteController.findOrgao(responsavel.getCodigoEntidade(), responsavel);
                return new String[] { org.getOrgIPAcesso(), org.getOrgDDNSAcesso() };

            } else if (responsavel.isCsa()) {
                final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
                final ConsignatariaTransferObject csa = consignatariaController.findConsignataria(responsavel.getCodigoEntidade(), responsavel);
                return new String[] { csa.getCsaIPAcesso(), csa.getCsaDDNSAcesso() };

            } else if (responsavel.isCor()) {
                final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
                final CorrespondenteTransferObject cor = consignatariaController.findCorrespondente(responsavel.getCodigoEntidade(), responsavel);
                return new String[] { cor.getCorIPAcesso(), cor.getCorDDNSAcesso() };
            }

            return new String[] { null, null };
        } catch (ConsignanteControllerException | ConsignatariaControllerException ex) {
            throw new ViewHelperException("mensagem.erro.validacao.restricao.ip.entidade", responsavel, ex);
        }
    }

    /**
     * Verifica se as restrições de IP ou DDNS de acesso, do usuário ou de sua entidade, são válidos
     * de acordo com as configurações do sistema. Caso exista restrição e esta esteja inválida, o método
     * retorna uma exceção com a mensagem correspondente.
     * @param tipo
     * @param codigo
     * @param endereco
     * @param usuIPAcesso
     * @param usuDDNSAcesso
     * @param responsavel
     * @return
     * @throws ViewHelperException
     */
    public static void verificarIpDDNSAcesso(String tipo, String entCodigo, String endereco, String usuIPAcesso, String usuDDNSAcesso, String usuCodigo, AcessoSistema responsavel) throws ViewHelperException {
        // Confere se cadastro de IPs de acesso é obrigatório de acordo com o tipo da entidade
        boolean vrfIpAcesso = false;
        boolean passouValidacao = false;
        boolean existeRestricao = false;
        if (AcessoSistema.ENTIDADE_CSE.equals(tipo) || AcessoSistema.ENTIDADE_ORG.equals(tipo) || AcessoSistema.ENTIDADE_SUP.equals(tipo)) {
            // Deve verificar se tem IP cadastrado em usuario, consignante ou órgão.
            vrfIpAcesso = ParamSist.getBoolParamSist(CodedValues.TPC_VERIFICA_CADASTRO_IP_CSE_ORG, responsavel);
        } else if (AcessoSistema.ENTIDADE_CSA.equals(tipo) || AcessoSistema.ENTIDADE_COR.equals(tipo)) {
            // Deve verificar se tem IP cadastrado em usuario, correspondente ou consignataria.
            vrfIpAcesso = ParamSist.getBoolParamSist(CodedValues.TPC_VERIFICA_CADASTRO_IP_CSA_COR, responsavel);
        }

        final boolean paramValidaAcessoIPUsuarioOuEntidade = ParamSist.paramEquals(CodedValues.TPC_VALIDA_ACESSO_IP_POR_USUARIO_OU_ENTIDADE, CodedValues.TPC_SIM, responsavel);

        if (AcessoSistema.ENTIDADE_CSE.equals(tipo) || AcessoSistema.ENTIDADE_ORG.equals(tipo) || AcessoSistema.ENTIDADE_SUP.equals(tipo)) {
            // Verifica se o cadastro de restrição no usuário sobrepõe o geral
            final boolean paramCadUsuSobrepoeGeral = !ParamSist.paramEquals(CodedValues.TPC_ENDERECO_ACESSO_USU_SOBREPOE_CSE_ORG, CodedValues.TPC_NAO, responsavel);
            if ((paramCadUsuSobrepoeGeral || AcessoSistema.ENTIDADE_SUP.equals(tipo)) && (!TextHelper.isNull(usuIPAcesso) || !TextHelper.isNull(usuDDNSAcesso))) {
                try {
                    validaIpDDNS(usuIPAcesso, usuDDNSAcesso, endereco);
                    passouValidacao = true;
                } catch (final ViewHelperException exception) {
                    existeRestricao = true;
                    if (!paramValidaAcessoIPUsuarioOuEntidade) {
                        throw exception;
                    }
                }
            }

            if (!passouValidacao && !paramCadUsuSobrepoeGeral) {
                if (AcessoSistema.ENTIDADE_CSE.equals(tipo)) {
                    // Validacao de IP e DDNS de consignante
                    try {
                        final ConsignanteController consignanteController = ApplicationContextProvider.getApplicationContext().getBean(ConsignanteController.class);
                        final ConsignanteTransferObject cse = consignanteController.findConsignante(entCodigo, responsavel);

                        if (vrfIpAcesso && TextHelper.isNull(cse.getCseIPAcesso()) && TextHelper.isNull(cse.getCseDDNSAcesso())) {
                            throw new ViewHelperException("mensagem.erro.usuario.ou.sua.entidade.deve.possuir.ips.acesso.cadastrados.sistema", responsavel);
                        }

                        validaIpDDNS(cse.getCseIPAcesso(), cse.getCseDDNSAcesso(), endereco);
                        passouValidacao = true;

                    } catch (final ConsignanteControllerException e) {
                        throw new ViewHelperException(e);
                    }

                } else if (AcessoSistema.ENTIDADE_ORG.equals(tipo)) {
                    // Validacao de IP e DDNS de órgão
                    try {
                        final ConsignanteController consignanteController = ApplicationContextProvider.getApplicationContext().getBean(ConsignanteController.class);
                        final OrgaoTransferObject org = consignanteController.findOrgao(entCodigo, responsavel);

                        if (vrfIpAcesso && TextHelper.isNull(org.getOrgIPAcesso()) && TextHelper.isNull(org.getOrgDDNSAcesso())) {
                            throw new ViewHelperException("mensagem.erro.usuario.ou.sua.entidade.deve.possuir.ips.acesso.cadastrados.sistema", responsavel);
                        }

                        validaIpDDNS(org.getOrgIPAcesso(), org.getOrgDDNSAcesso(), endereco);
                        passouValidacao = true;

                    } catch (final ConsignanteControllerException e) {
                        throw new ViewHelperException(e);
                    }
                }
            } else {

                String perIpAcesso = null;
                String perDdnsAcesso = null;
                Perfil perfil = new Perfil();

                try {
                    final UsuarioDelegate usuDelegate = new UsuarioDelegate();

                    perfil = usuDelegate.findPerfilByUsuCodigo(usuCodigo, responsavel);

                    perIpAcesso = perfil.getPerIpAcesso();
                    perDdnsAcesso = perfil.getPerDdnsAcesso();
                } catch (final UsuarioControllerException e) {
                    //Não achar o perfil, não é erro!
                }

                if (!TextHelper.isNull(perIpAcesso) || !TextHelper.isNull(perDdnsAcesso)) {
                    try {
                        validaIpDDNS(perIpAcesso, perDdnsAcesso, endereco);
                        passouValidacao = true;
                    } catch (final ViewHelperException exception) {
                        existeRestricao = true;
                        if (!paramValidaAcessoIPUsuarioOuEntidade) {
                            throw new ViewHelperException(exception);
                        } else if (AcessoSistema.ENTIDADE_CSE.equals(tipo)) {
                            // Validacao de IP e DDNS de consignante
                            try {
                                final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
                                final ConsignanteTransferObject cse = cseDelegate.findConsignante(entCodigo, responsavel);

                                if (vrfIpAcesso && TextHelper.isNull(cse.getCseIPAcesso()) && TextHelper.isNull(cse.getCseDDNSAcesso())) {
                                    throw new ViewHelperException("mensagem.erro.usuario.ou.sua.entidade.deve.possuir.ips.acesso.cadastrados.sistema", responsavel);
                                }

                                if (!TextHelper.isNull(cse.getCseIPAcesso()) || !TextHelper.isNull(cse.getCseDDNSAcesso())) {
                                    validaIpDDNS(cse.getCseIPAcesso(), cse.getCseDDNSAcesso(), endereco);
                                    passouValidacao = true;
                                }

                            } catch (final ConsignanteControllerException e) {
                                throw new ViewHelperException(e);
                            }

                        } else if (AcessoSistema.ENTIDADE_ORG.equals(tipo)) {
                            // Validacao de IP e DDNS de órgão
                            try {
                                final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
                                final OrgaoTransferObject org = cseDelegate.findOrgao(entCodigo, responsavel);

                                if (vrfIpAcesso && TextHelper.isNull(org.getOrgIPAcesso()) && TextHelper.isNull(org.getOrgDDNSAcesso())) {
                                    throw new ViewHelperException("mensagem.erro.usuario.ou.sua.entidade.deve.possuir.ips.acesso.cadastrados.sistema", responsavel);
                                }

                                if (!TextHelper.isNull(org.getOrgIPAcesso()) || !TextHelper.isNull(org.getOrgDDNSAcesso())) {
                                    validaIpDDNS(org.getOrgIPAcesso(), org.getOrgDDNSAcesso(), endereco);
                                    passouValidacao = true;
                                }

                            } catch (final ConsignanteControllerException e) {
                                throw new ViewHelperException(e);
                            }
                        } else {
                            throw new ViewHelperException("rotulo.endereco.acesso.invalido.ip", (AcessoSistema) null, endereco);
                        }
                    }
                } else if (AcessoSistema.ENTIDADE_CSE.equals(tipo)) {
                    // Validacao de IP e DDNS de consignante
                    try {
                        final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
                        final ConsignanteTransferObject cse = cseDelegate.findConsignante(entCodigo, responsavel);

                        if (vrfIpAcesso && TextHelper.isNull(cse.getCseIPAcesso()) && TextHelper.isNull(cse.getCseDDNSAcesso())) {
                            throw new ViewHelperException("mensagem.erro.usuario.ou.sua.entidade.deve.possuir.ips.acesso.cadastrados.sistema", responsavel);
                        }

                        if (!TextHelper.isNull(cse.getCseIPAcesso()) || !TextHelper.isNull(cse.getCseDDNSAcesso())) {
                            validaIpDDNS(cse.getCseIPAcesso(), cse.getCseDDNSAcesso(), endereco);
                            passouValidacao = true;
                        }

                    } catch (final ConsignanteControllerException e) {
                        throw new ViewHelperException(e);
                    }

                } else if (AcessoSistema.ENTIDADE_ORG.equals(tipo)) {
                    // Validacao de IP e DDNS de órgão
                    try {
                        final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
                        final OrgaoTransferObject org = cseDelegate.findOrgao(entCodigo, responsavel);

                        if (vrfIpAcesso && TextHelper.isNull(org.getOrgIPAcesso()) && TextHelper.isNull(org.getOrgDDNSAcesso())) {
                            throw new ViewHelperException("mensagem.erro.usuario.ou.sua.entidade.deve.possuir.ips.acesso.cadastrados.sistema", responsavel);
                        }

                        if (!TextHelper.isNull(org.getOrgIPAcesso()) || !TextHelper.isNull(org.getOrgDDNSAcesso())) {
                            validaIpDDNS(org.getOrgIPAcesso(), org.getOrgDDNSAcesso(), endereco);
                            passouValidacao = true;
                        }

                    } catch (final ConsignanteControllerException e) {
                        throw new ViewHelperException(e);
                    }
                }
            }

            if ((!passouValidacao && existeRestricao) && !responsavel.isNavegadorExclusivo()) {
                throw new ViewHelperException("rotulo.endereco.acesso.invalido.ip", (AcessoSistema) null, endereco);
            }

        } else {
            // Verifica o usuário tem permissão para acessar o sistema a partir de seu IP ou DDNS
            if (!TextHelper.isNull(usuIPAcesso) || !TextHelper.isNull(usuDDNSAcesso)) {
                try {
                    validaIpDDNS(usuIPAcesso, usuDDNSAcesso, endereco);
                    passouValidacao = true;
                } catch (final ViewHelperException exception) {
                    existeRestricao = true;
                    if (!paramValidaAcessoIPUsuarioOuEntidade) {
                        throw new ViewHelperException(exception);
                    }
                }
            }

            if (!passouValidacao) {

                String perIpAcesso = null;
                String perDdnsAcesso = null;
                Perfil perfil = new Perfil();

                try {
                    final UsuarioDelegate usuDelegate = new UsuarioDelegate();

                    perfil = usuDelegate.findPerfilByUsuCodigo(usuCodigo, responsavel);

                    perIpAcesso = perfil.getPerIpAcesso();
                    perDdnsAcesso = perfil.getPerDdnsAcesso();
                } catch (final UsuarioControllerException e) {
                    //Não achar o perfil, não é erro!
                }

                if (!TextHelper.isNull(perIpAcesso) || !TextHelper.isNull(perDdnsAcesso)) {
                    try {
                        validaIpDDNS(perIpAcesso, perDdnsAcesso, endereco);
                        passouValidacao = true;
                    } catch (final ViewHelperException exception) {
                        existeRestricao = true;
                        if (!paramValidaAcessoIPUsuarioOuEntidade) {
                            throw new ViewHelperException(exception);
                        } else if (AcessoSistema.ENTIDADE_CSA.equals(tipo)) {
                            // Validacao de IP e DDNS de consignataria
                            try {
                                final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
                                final ConsignatariaTransferObject csa = consignatariaController.findConsignataria(entCodigo, responsavel);

                                // Se o campo csaExigeEnderecoAcesso estiver preenchido, ele prevalece sobre o parametro 224
                                vrfIpAcesso = (csa.getCsaExigeEnderecoAcesso() != null) ? CodedValues.TPC_SIM.equals(csa.getCsaExigeEnderecoAcesso()) : vrfIpAcesso;

                                if (vrfIpAcesso && TextHelper.isNull(csa.getCsaIPAcesso()) && TextHelper.isNull(csa.getCsaDDNSAcesso())) {
                                    throw new ViewHelperException("mensagem.erro.usuario.ou.sua.entidade.deve.possuir.ips.acesso.cadastrados.sistema", responsavel);
                                }

                                if (!TextHelper.isNull(csa.getCsaIPAcesso()) || !TextHelper.isNull(csa.getCsaDDNSAcesso())) {
                                    validaIpDDNS(csa.getCsaIPAcesso(), csa.getCsaDDNSAcesso(), endereco);
                                    passouValidacao = true;
                                }

                            } catch (final ConsignatariaControllerException e) {
                                throw new ViewHelperException(e);
                            }

                        } else if (AcessoSistema.ENTIDADE_COR.equals(tipo)) {
                            // Validacao de IP e DDNS de correspondente
                            try {
                                final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
                                final CorrespondenteTransferObject cor = consignatariaController.findCorrespondente(entCodigo, responsavel);

                                // Se o campo corExigeEnderecoAcesso estiver preenchido, ele prevalece sobre o parametro 224
                                vrfIpAcesso = (cor.getCorExigeEnderecoAcesso() != null) ? CodedValues.TPC_SIM.equals(cor.getCorExigeEnderecoAcesso()) : vrfIpAcesso;

                                if (vrfIpAcesso && TextHelper.isNull(cor.getCorIPAcesso()) && TextHelper.isNull(cor.getCorDDNSAcesso())) {
                                    throw new ViewHelperException("mensagem.erro.usuario.ou.sua.entidade.deve.possuir.ips.acesso.cadastrados.sistema", responsavel);
                                }

                                if (!TextHelper.isNull(cor.getCorIPAcesso()) || !TextHelper.isNull(cor.getCorDDNSAcesso())) {
                                    validaIpDDNS(cor.getCorIPAcesso(), cor.getCorDDNSAcesso(), endereco);
                                    passouValidacao = true;
                                }

                            } catch (final ConsignatariaControllerException e) {
                                throw new ViewHelperException(e);
                            }
                        }
                    }
                } else if (AcessoSistema.ENTIDADE_CSA.equals(tipo)) {
                    // Validacao de IP e DDNS de consignataria
                    try {
                        final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
                        final ConsignatariaTransferObject csa = consignatariaController.findConsignataria(entCodigo, responsavel);

                        // Se o campo csaExigeEnderecoAcesso estiver preenchido, ele prevalece sobre o parametro 224
                        vrfIpAcesso = (csa.getCsaExigeEnderecoAcesso() != null) ? CodedValues.TPC_SIM.equals(csa.getCsaExigeEnderecoAcesso()) : vrfIpAcesso;

                        if (vrfIpAcesso && TextHelper.isNull(csa.getCsaIPAcesso()) && TextHelper.isNull(csa.getCsaDDNSAcesso())) {
                            throw new ViewHelperException("mensagem.erro.usuario.ou.sua.entidade.deve.possuir.ips.acesso.cadastrados.sistema", responsavel);
                        }

                        if (!TextHelper.isNull(csa.getCsaIPAcesso()) || !TextHelper.isNull(csa.getCsaDDNSAcesso())) {
                            validaIpDDNS(csa.getCsaIPAcesso(), csa.getCsaDDNSAcesso(), endereco);
                            passouValidacao = true;
                        }

                    } catch (final ConsignatariaControllerException e) {
                        throw new ViewHelperException(e);
                    }

                } else if (AcessoSistema.ENTIDADE_COR.equals(tipo)) {
                    // Validacao de IP e DDNS de correspondente
                    try {
                        final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
                        final CorrespondenteTransferObject cor = consignatariaController.findCorrespondente(entCodigo, responsavel);

                        // Se o campo corExigeEnderecoAcesso estiver preenchido, ele prevalece sobre o parametro 224
                        vrfIpAcesso = (cor.getCorExigeEnderecoAcesso() != null) ? CodedValues.TPC_SIM.equals(cor.getCorExigeEnderecoAcesso()) : vrfIpAcesso;

                        if (vrfIpAcesso && TextHelper.isNull(cor.getCorIPAcesso()) && TextHelper.isNull(cor.getCorDDNSAcesso())) {
                            throw new ViewHelperException("mensagem.erro.usuario.ou.sua.entidade.deve.possuir.ips.acesso.cadastrados.sistema", responsavel);
                        }

                        if (!TextHelper.isNull(cor.getCorIPAcesso()) || !TextHelper.isNull(cor.getCorDDNSAcesso())) {
                            validaIpDDNS(cor.getCorIPAcesso(), cor.getCorDDNSAcesso(), endereco);
                            passouValidacao = true;
                        }

                    } catch (final ConsignatariaControllerException e) {
                        throw new ViewHelperException(e);
                    }
                }
            }
            if ((!passouValidacao && existeRestricao) && !responsavel.isNavegadorExclusivo()) {
                throw new ViewHelperException("rotulo.endereco.acesso.invalido.ip", (AcessoSistema) null, endereco);
            }
        }
    }

    private static void validaIpDDNS(String restricaoIpAcesso, String restricaoDDNSAcesso, String endereco) throws ViewHelperException {
        if (!TextHelper.isNull(restricaoIpAcesso)) {
            if (!JspHelper.validaIp(endereco, restricaoIpAcesso)) {
                if (!TextHelper.isNull(restricaoDDNSAcesso)) {
                    if (!JspHelper.validaDDNS(endereco, restricaoDDNSAcesso)) {
                        throw new ViewHelperException("rotulo.endereco.acesso.invalido.ip", (AcessoSistema) null, endereco);
                    }
                } else {
                    throw new ViewHelperException("rotulo.endereco.acesso.invalido.ip", (AcessoSistema) null, endereco);
                }
            }
        } else if (!TextHelper.isNull(restricaoDDNSAcesso) && !JspHelper.validaDDNS(endereco, restricaoDDNSAcesso)) {
            throw new ViewHelperException("rotulo.endereco.acesso.invalido.ip", (AcessoSistema) null, endereco);
        }
    }

    private static void verificarUsuarioCentralizador(String endereco, AcessoSistema responsavel) throws ViewHelperException {
        final Object paramUrlCentralizador = ParamSist.getInstance().getParam(CodedValues.TPC_URL_CENTRALIZADOR, responsavel);
        final String urlCentralizador = (paramUrlCentralizador != null) ? (String) paramUrlCentralizador : "";
        if (!"".equals(urlCentralizador)) {
            final List<String> urls = Arrays.asList(urlCentralizador.split(";"));
            if (!JspHelper.validaUrl(endereco, urls)) {
                throw new ViewHelperException("mensagem.enderecoAcessoCentralizadorInvalido", responsavel);
            }
        } else {
            throw new ViewHelperException("mensagem.urlAcessoNaoCadastradaCentralizador", responsavel);
        }
    }

    /**
     * Verifica os parâmetros de sistema para determinar se o usuário deve possuir
     * CPF cadastrado no sistema para acessá-lo.
     *
     * @param tipoEntidade
     * @param cpf
     * @param responsavel
     * @throws ViewHelperException
     */
    public static void verificarCpfUsuario(String tipoEntidade, String cpf, AcessoSistema responsavel) throws ViewHelperException {
        boolean bloqueiaAcessoUsuSemCpf = false;
        if (!TextHelper.isNull(tipoEntidade)) {
            if (AcessoSistema.ENTIDADE_CSE.equals(tipoEntidade) || AcessoSistema.ENTIDADE_ORG.equals(tipoEntidade) || AcessoSistema.ENTIDADE_SUP.equals(tipoEntidade)) {
                bloqueiaAcessoUsuSemCpf = ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_LOGIN_USU_SEM_CPF_CSE_ORG, CodedValues.TPC_SIM, responsavel);
            } else if (AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade) || AcessoSistema.ENTIDADE_COR.equals(tipoEntidade)) {
                bloqueiaAcessoUsuSemCpf = ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_LOGIN_USU_SEM_CPF_CSA_COR, CodedValues.TPC_SIM, responsavel);
            }
        }

        if (bloqueiaAcessoUsuSemCpf && TextHelper.isNull(cpf)) {
            throw new ViewHelperException("mensagem.cpfUsuarioObrigatorio", responsavel);
        }
    }

    /**
     * Verifica os parâmetros de sistema para determinar se o usuário deve possuir
     * CPF cadastrado no sistema para acessá-lo.
     * @param tipoEntidade
     * @param cpf
     * @param responsavel
     * @throws ViewHelperException
     */
    public static void verificarEmailUsuario(String tipoEntidade, String email, AcessoSistema responsavel) throws ViewHelperException {
        boolean bloqueiaAcessoUsuSemEmail = false;
        if (!TextHelper.isNull(tipoEntidade)) {
            if (AcessoSistema.ENTIDADE_CSE.equals(tipoEntidade) || AcessoSistema.ENTIDADE_ORG.equals(tipoEntidade) || AcessoSistema.ENTIDADE_SUP.equals(tipoEntidade)) {
                bloqueiaAcessoUsuSemEmail = ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_LOGIN_USU_SEM_EMAIL_CSE_ORG_SUP, CodedValues.TPC_SIM, responsavel);
            } else if (AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade) || AcessoSistema.ENTIDADE_COR.equals(tipoEntidade)) {
                bloqueiaAcessoUsuSemEmail = ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_LOGIN_USU_SEM_EMAIL_CSA_COR, CodedValues.TPC_SIM, responsavel);
            }
        }

        if (bloqueiaAcessoUsuSemEmail && TextHelper.isNull(email)) {
            throw new ViewHelperException("mensagem.emailUsuarioObrigatorio", responsavel);
        }
    }

    /**
     * Metodo usado no login do usuario para verificar se ele precisa validar o certificado digital.
     * Tambem é usado nas telas de manutencao do usuario para exibir o valor correto do campo
     * UsuExigeCertificado, o qual deve levar em consideracao os parametros:
     * TPC_EXIGE_CERTIFICADO_DIGITAL_CSE_ORG,
     * TPC_EXIGE_CERTIFICADO_DIGITAL_CSA_COR, TPC_PERMITE_CSA_OPTAR_CERTIF_DIGITAL,
     * TPA_EXIGE_CERTIFICADO_DIGITAL e TPA_PERMITE_USU_OPTAR_CERTIF_DIGITAL.
     * Essa validacao não é feita na autenticacao de usuario via XML ou entrada em lote.
     * @param usuLogin Login do usuario.
     * @param usuExigeCertificado Valor do campo do usuario.
     * @param tipo Tipo da entidade. Se for CSA ou COR, o parametro
     * @param codigo Codigo da entidade. Usado para encontrar a
     * @param responsavel Usuario logado no sistema.
     * @return Verdadeiro ou falso, caso o usuario tenha ou nao que validar o certificado.
     * @throws ViewHelperException Excecao padrao da camada de visao.
     */
    public static boolean isUsuarioCertificadoDigital(String usuLogin, String usuExigeCertificado, String tipo, String codigo, AcessoSistema responsavel) throws ViewHelperException {
        boolean usaCertificado = false;

        if (!TextHelper.isNull(tipo)) {
            if (AcessoSistema.ENTIDADE_CSA.equals(tipo) || AcessoSistema.ENTIDADE_COR.equals(tipo)) {

                // Parametro de sistema que indica se exige certificado digital para CSA/COR
                if (ParamSist.paramEquals(CodedValues.TPC_EXIGE_CERTIFICADO_DIGITAL_CSA_COR, CodedValues.TPC_SIM, responsavel)) {
                    usaCertificado = true;
                }

                // Verifica se a CSA/COR pode optar pelo uso do certificado, o que vai sobrepor
                // o parametro acima
                if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_CSA_OPTAR_CERTIF_DIGITAL, CodedValues.TPC_SIM, responsavel)) {

                    String csaCodigo = null;

                    if (!TextHelper.isNull(codigo)) {
                        if (AcessoSistema.ENTIDADE_CSA.equals(tipo)) {
                            csaCodigo = codigo;
                        } else {
                            try {
                                final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
                                final CorrespondenteTransferObject cor = consignatariaController.findCorrespondente(codigo, responsavel);
                                csaCodigo = cor.getCsaCodigo();
                            } catch (final ConsignatariaControllerException e) {
                                // Se der erro, tenta encontrar o csaCodigo novamente abaixo
                            }
                        }
                    }

                    // Procura o codigo da consignataria atraves do login do usuario
                    if ((csaCodigo == null) && !TextHelper.isNull(usuLogin)) {
                        try {
                            final UsuarioController usuarioController = ApplicationContextProvider.getApplicationContext().getBean(UsuarioController.class);
                            final TransferObject usuario = usuarioController.findTipoUsuarioByLogin(usuLogin, responsavel);
                            final String csa_codigo = !TextHelper.isNull(usuario.getAttribute(Columns.UCA_CSA_CODIGO)) ? usuario.getAttribute(Columns.UCA_CSA_CODIGO).toString() : null;
                            final String cor_codigo = !TextHelper.isNull(usuario.getAttribute(Columns.UCO_COR_CODIGO)) ? usuario.getAttribute(Columns.UCO_COR_CODIGO).toString() : null;
                            if (csa_codigo != null) {
                                csaCodigo = csa_codigo;
                            } else if (cor_codigo != null) {
                                final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
                                final CorrespondenteTransferObject cor = consignatariaController.findCorrespondente(cor_codigo, responsavel);
                                csaCodigo = cor.getCsaCodigo();
                            }
                        } catch (UsuarioControllerException | ConsignatariaControllerException e) {
                            throw new ViewHelperException("mensagem.erro.obter.tipo.usuario", responsavel);
                        }
                    }

                    if (csaCodigo != null) {
                        // Verifica parametro de consignataria que vale para todos os seus usuarios
                        try {
                            final ParametroController parametroController = ApplicationContextProvider.getApplicationContext().getBean(ParametroController.class);
                            final String pcsVlr = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_EXIGE_CERTIFICADO_DIGITAL, responsavel);
                            // Só avalia o parâmetro de CSA se este não for nulo, para não sobrepor a variável neste cenário
                            if (!TextHelper.isNull(pcsVlr)) {
                                usaCertificado = "S".equals(pcsVlr);
                            }
                        } catch (final ParametroControllerException ex) {
                            throw new ViewHelperException("mensagem.erro.obter.parametro.consignataria", responsavel);
                        }

                        // Verifica parametro de consignataria que vale para um usuario especifico e sobrepoe todos
                        if (!TextHelper.isNull(usuExigeCertificado)) {
                            try {
                                final ParametroController parametroController = ApplicationContextProvider.getApplicationContext().getBean(ParametroController.class);
                                final String pcsVlr = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_PERMITE_USU_OPTAR_CERTIF_DIGITAL, responsavel);
                                if ("S".equals(pcsVlr)) {
                                    if (CodedValues.TPC_SIM.equals(usuExigeCertificado)) {
                                        usaCertificado = true;
                                    } else if (CodedValues.TPC_NAO.equals(usuExigeCertificado)) {
                                        usaCertificado = false;
                                    }
                                }
                            } catch (final ParametroControllerException ex) {
                                throw new ViewHelperException("mensagem.erro.obter.parametro.consignataria", responsavel);
                            }
                        }
                    }
                }

            } else if ((AcessoSistema.ENTIDADE_CSE.equals(tipo) || AcessoSistema.ENTIDADE_ORG.equals(tipo)) && ParamSist.paramEquals(CodedValues.TPC_EXIGE_CERTIFICADO_DIGITAL_CSE_ORG, CodedValues.TPC_SIM, responsavel)) {
                // Parametro de sistema que indica se exige certificado digital para CSE/ORG
                usaCertificado = true;

            } else if (!TextHelper.isNull(usuExigeCertificado) && CodedValues.TPC_SIM.equals(usuExigeCertificado)) {
                usaCertificado = true;
            } // Nao precisa verificar o TPC_NAO, pois ja esta false
        }

        return usaCertificado;
    }

    /**
     * Verifica se servidor já acessou o sistema ao menos uma vez com alguma de suas matrículas em algum órgão
     * @param serCpf - cpf do servidor
     * @param lstRegistroServidores - lista de registros do servidor
     * @return
     * @throws HQueryException
     * @throws UsuarioControllerException
     */
    public static boolean primeiroAcessoSistema(String serCpf, List<TransferObject> lstRegistroServidores, AcessoSistema responsavel) throws ViewHelperException {
        try {
            if (TextHelper.isNull(serCpf)) {
                throw new ViewHelperException("mensagem.erro.servidor.nao.encontrado", responsavel);
            }

            final UsuarioController usuarioController = ApplicationContextProvider.getApplicationContext().getBean(UsuarioController.class);

            if ((lstRegistroServidores != null) && !lstRegistroServidores.isEmpty()) {
                for (final TransferObject rse : lstRegistroServidores) {
                    final List<TransferObject> lstUsuResult = usuarioController.lstUsuariosSer(serCpf, (String) rse.getAttribute(Columns.RSE_MATRICULA), (String) rse.getAttribute(Columns.EST_IDENTIFICADOR), (String) rse.getAttribute(Columns.ORG_IDENTIFICADOR), null);

                    for (final TransferObject usuarioSer : lstUsuResult) {
                        if (!TextHelper.isNull(usuarioSer.getAttribute(Columns.USU_DATA_ULT_ACESSO))) {
                            return false;
                        }
                    }
                }
            } else {
                final List<TransferObject> lstUsuResult = usuarioController.lstUsuariosSer(serCpf, null, null, null, null);

                if ((lstUsuResult == null) || lstUsuResult.isEmpty()) {
                    throw new ViewHelperException("mensagem.erro.servidor.nao.encontrado", responsavel);
                }

                for (final TransferObject usuarioSer : lstUsuResult) {
                    if (!TextHelper.isNull(usuarioSer.getAttribute(Columns.USU_DATA_ULT_ACESSO))) {
                        return false;
                    }
                }
            }

        } catch (final UsuarioControllerException e) {
            throw new ViewHelperException(e.getMessageKey(), responsavel);
        }

        return true;
    }

    /**
     * valida token de fluxo de caso de uso de validação de OTP de usuário servidor
     * @param usuCodigo
     * @param token
     * @param responsavel
     * @return
     * @throws ViewHelperException
     */
    public static boolean isTokenOtpPageValido(String usuCodigo, String token, AcessoSistema responsavel) throws ViewHelperException {
        try {
            final UsuarioController usuarioController = ApplicationContextProvider.getApplicationContext().getBean(UsuarioController.class);
            final UsuarioTransferObject usuTO = usuarioController.findUsuario(usuCodigo, responsavel);
            final String tokenSistema = usuTO.getUsuOtpChaveSeguranca();
            return !TextHelper.isNull(tokenSistema) && tokenSistema.equalsIgnoreCase(token);
        } catch (final UsuarioControllerException e) {
            throw new ViewHelperException(e.getMessageKey(), responsavel);
        }

    }

    /**
     * verifica se o usuário autenticou o email cadastrado
     * @param tipoEntidade
     * @param dataValidacaoEmail
     * @param responsavel
     * @return
     * @throws ViewHelperException
     */
    public static boolean usuarioValidouEmail(String tipoEntidade, String dataValidacaoEmail, AcessoSistema responsavel) throws ViewHelperException {
        boolean validarEmailUsuario = false;

        if (!TextHelper.isNull(tipoEntidade)) {
            if (ParamSist.paramEquals(CodedValues.TPC_VALIDA_EMAIL_PAPEL_USUARIO_CSE_ORG_SUP, CodedValues.TPC_SIM, responsavel) && (AcessoSistema.ENTIDADE_CSE.equals(tipoEntidade) || AcessoSistema.ENTIDADE_ORG.equals(tipoEntidade) || AcessoSistema.ENTIDADE_SUP.equals(tipoEntidade))) {
                validarEmailUsuario = true;

            } else if (ParamSist.paramEquals(CodedValues.TPC_VALIDA_EMAIL_PAPEL_USUARIO_CSA_COR, CodedValues.TPC_SIM, responsavel) && (AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade) || AcessoSistema.ENTIDADE_COR.equals(tipoEntidade))) {
                validarEmailUsuario = true;
            }
        }

        return !validarEmailUsuario || !TextHelper.isNull(dataValidacaoEmail);
    }

    /**
     * verifica parâmetros pertinentes ao bloqueio por inatividade e bloqueia usuário(s) caso a data configurada de inatividade tenha sido excedida.
     * @param usuCodigo - código do usuário a verificar bloqueio. Para verificar para todos usuários, informe null.
     * @param responsavel
     * @return retorna true se um ou mais usuários foram bloqueados por inatividade
     * @throws UsuarioControllerException
     */
    public static boolean bloqueioAutomaticoPorInatividade(String usuCodigo, AcessoSistema responsavel) throws UsuarioControllerException {
		final Integer diasSemAcessoCse = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_BLOQ_USU_CSE_SEM_ACESSO, responsavel)) ? Integer.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_BLOQ_USU_CSE_SEM_ACESSO, responsavel).toString()) : 0;
        final Integer diasSemAcessoCsa = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_BLOQ_USU_CSA_SEM_ACESSO, responsavel)) ? Integer.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_BLOQ_USU_CSA_SEM_ACESSO, responsavel).toString()) : 0;
        final Integer diasSemAcessoSer = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_BLOQ_USU_SER_SEM_ACESSO, responsavel)) ? Integer.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_BLOQ_USU_SER_SEM_ACESSO, responsavel).toString()) : 0;

        List<TransferObject> lista = null;
        if ((diasSemAcessoCse > 0) || (diasSemAcessoCsa > 0) || (diasSemAcessoSer > 0)) {
            final Integer qtdeHorasPrazoLoginUsuario = maxHorasParaLogarAposDesbloqueio(responsavel);

            final Calendar data = Calendar.getInstance();
            data.add(Calendar.HOUR, -qtdeHorasPrazoLoginUsuario);

            final UsuarioController usuarioController = ApplicationContextProvider.getApplicationContext().getBean(UsuarioController.class);
            lista = usuarioController.listarBloqueiaUsuariosInativos(usuCodigo, data.getTime(), responsavel);
            if ((lista != null) && !lista.isEmpty()) {
                for (final TransferObject to : lista) {
                    bloqueiaUsuarioAutomaticamente(responsavel, to);
                }
            }
        }

        return (lista != null) && !lista.isEmpty();
	}

    /**
     * Recupera CPF do usuário servidor de acordo com o email informado.
     * @param email
     * @param cpf
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
	public static String getCPFByUsuEmailSer(String email, AcessoSistema responsavel) throws UsuarioControllerException {
        // busca o cpf do servidor a partir do e-mail
        final UsuarioController usuarioController = ApplicationContextProvider.getApplicationContext().getBean(UsuarioController.class);
		final List<UsuarioTransferObject> lstCpf = usuarioController.lstUsuariosSerByEmail(email, responsavel);

		return ((lstCpf != null) && !lstCpf.isEmpty()) ? lstCpf.get(0).getUsuCPF() : null;
	}

	private static Integer maxHorasParaLogarAposDesbloqueio(AcessoSistema responsavel) {
		// DESENV-14804 Quantidade de horas que o usuário tem de prazo para logar no sistema após o desbloqueio no sistema.
		Integer qtdeHorasPrazoLoginUsuario = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_HORAS_USUARIO_LOGAR_APOS_DESBLOQUEIO, responsavel)) ? Integer.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_HORAS_USUARIO_LOGAR_APOS_DESBLOQUEIO, responsavel).toString()) : 48;
		// Prazo máximo permitido será 10 (dez) dias, ou seja 240 horas.
		if (qtdeHorasPrazoLoginUsuario > 240) {
		    qtdeHorasPrazoLoginUsuario = 240;
		}
		return qtdeHorasPrazoLoginUsuario;
	}

	private static void bloqueiaUsuarioAutomaticamente(AcessoSistema responsavel, TransferObject to) {
		final String innerUsuCodigo = to.getAttribute(Columns.USU_CODIGO).toString();

		// Altera status do usuário para bloqueado automaticamente
		final UsuarioTransferObject usuario = new UsuarioTransferObject(innerUsuCodigo);
		usuario.setStuCodigo(CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE);
		usuario.setUsuTipoBloq(ApplicationResourcesHelper.getMessage("mensagem.usuario.bloqueado.inatividade", responsavel));

		// Grava ocorrência de bloqueio automático do usuário por inatividade
		final OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
		ocorrencia.setUsuCodigo(innerUsuCodigo);
		ocorrencia.setTocCodigo(CodedValues.TOC_BLOQUEIO_AUTOMATICO_USUARIO);
		ocorrencia.setOusUsuCodigo((responsavel.getUsuCodigo() != null) ? responsavel.getUsuCodigo() : AcessoSistema.getAcessoUsuarioSistema().getUsuCodigo());
		ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.bloqueio.automatico.usuario.inatividade", responsavel));
		ocorrencia.setOusIpAcesso(responsavel.getIpUsuario());

		try {
            final UsuarioController usuarioController = ApplicationContextProvider.getApplicationContext().getBean(UsuarioController.class);
		    usuarioController.updateUsuario(usuario, ocorrencia, null, null, null, null, null, responsavel);
		} catch (final UsuarioControllerException e) {
		    LOG.debug("Erro na tentativa de bloquear usuário: " + innerUsuCodigo);
		    LOG.debug(e);
		}
	}
}
