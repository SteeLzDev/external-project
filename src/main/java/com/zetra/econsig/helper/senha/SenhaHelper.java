package com.zetra.econsig.helper.senha;

import java.security.KeyPair;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;

import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcCseTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.SenhaExpiradaException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.criptografia.JCrypt;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.ControleLogin;
import com.zetra.econsig.helper.senhaexterna.SenhaExterna;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedNames;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.MetodoSenhaExternaEnum;
import com.zetra.econsig.values.ParamSenhaExternaEnum;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.web.controller.servidor.AutenticarServidorOAuth2WebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: SenhaHelper</p>
 * <p>Description: Helper Class para operações de senha.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class SenhaHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(SenhaHelper.class);

    // Regex para detectar caracteres especiais (ex: "!", "%")
    public static final String specialCharactersRegex = "[!?@#$%()\\[\\]{}<>&*_:;/|\\\\\\-]";

    // Regex para detectar sequência numérica (ex: "123", "987")
    public static final String numericSequencePattern = "(012|123|234|345|456|567|678|789|890|098|987|876|765|654|543|432|321|210)";

    // Regex para detectar sequência alfabética (ex: "abc", "zyx")
    public static final String alphabeticSequencePattern = "(abc|bcd|cde|def|efg|fgh|ghi|hij|ijk|jkl|klm|lmn|mno|nop|opq|pqr|qrs|rst|stu|tuv|uvw|vwx|wxy|xyz|zyx|yxw|xwv|wvu|vut|uts|tsr|srq|rqp|qpo|pon|onm|nml|mlk|lkj|kji|jih|ihg|hgf|gfe|fed|edc|dcb|cba)";

    public static void validarSenha(HttpServletRequest request, String rseCodigo, String svcCodigo, boolean serSenhaObrigatoria, boolean validacaoParaDeferimentoReserva, AcessoSistema responsavel) throws ViewHelperException {
        validarSenha(request, null, rseCodigo, svcCodigo, serSenhaObrigatoria, validacaoParaDeferimentoReserva, false, responsavel);
    }

    public static void validarSenha(HttpServletRequest request, String rseCodigo, String svcCodigo, boolean serSenhaObrigatoria, boolean validacaoParaDeferimentoReserva, boolean consomeSenha, AcessoSistema responsavel) throws ViewHelperException {
        validarSenha(request, null, rseCodigo, svcCodigo, serSenhaObrigatoria, validacaoParaDeferimentoReserva, consomeSenha, responsavel);
    }

    public static void validarSenha(HttpServletRequest request, UploadHelper uploadHelper, String rseCodigo, String svcCodigo, boolean serSenhaObrigatoria, boolean validacaoParaDeferimentoReserva, boolean consomeSenha, AcessoSistema responsavel) throws ViewHelperException {
        final HttpSession session = request.getSession();
        // DESENV-19542 : não remover da sessão, pois caso a operação de compra reporte erro, não irá voltar ao passo anterior da operação.
        // session.removeAttribute("senhaServidorRenegOK");
        session.removeAttribute("senhaServidorOK");

        final boolean senhaExternaOAuth2 =  (ParamSist.paramEquals(CodedValues.TPC_SENHA_EXTERNA, CodedValues.TPC_SIM, responsavel) &&
                MetodoSenhaExternaEnum.OAUTH2.getMetodo().equals(ParamSenhaExternaEnum.METODO.getValor()));

        // DESENV-16627 - Para os sistema que utilizam senha externaOAuth2, porém utilizam senha de autorização do servidor deve ser considerado o
        // a senha de autorização e não a validação de senha externa para as validações das operações com senha.
        final boolean usaSenhaAutorizacaoSer = ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel);

        boolean dispensaValidacaoDigital = false;
        if (ParamSist.getBoolParamSist(CodedValues.TPC_TEM_VALIDACAO_DIGITAL_SERVIDOR, responsavel)) {
            try {
                final ServidorController servidorController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);
                final ServidorTransferObject servidorTO = servidorController.findServidorByRseCodigo(rseCodigo, responsavel);
                dispensaValidacaoDigital = !TextHelper.isNull(servidorTO.getSerDispensaDigital()) && servidorTO.getSerDispensaDigital().equals(CodedValues.TPC_SIM);
            } catch (final ServidorControllerException ex) {
                throw new ViewHelperException(ex);
            }
        }

        final boolean validaDigitais = ParamSist.getBoolParamSist(CodedValues.TPC_TEM_VALIDACAO_DIGITAL_SERVIDOR, responsavel) && !dispensaValidacaoDigital;
        final boolean digitalServidorValidada = (session.getAttribute(CodedNames.ATTR_SESSION_SER_DIGITAL_VALIDA) != null && rseCodigo != null &&
                                           rseCodigo.equals(session.getAttribute(CodedNames.ATTR_SESSION_SER_DIGITAL_VALIDA).toString()));
        if (validaDigitais && digitalServidorValidada) {
            request.setAttribute("senhaServidorOK", "digitalValidada");
            session.removeAttribute("serAutorizacao");

        } else if (senhaExternaOAuth2 && !usaSenhaAutorizacaoSer) {
            try {
                final String token = JspHelper.verificaVarQryStr(request, uploadHelper, "tokenOAuth2");

                if (!TextHelper.isNull(token)) {
                    final ServidorController servidorController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);
                    final ServidorTransferObject servidorTO = servidorController.findServidorByRseCodigo(rseCodigo, responsavel);
                    final String cpf = servidorTO.getSerCpf();

                    // Valida o token recebido
                    final boolean tokenValido = AutenticarServidorOAuth2WebController.isOAuth2TokenValid(cpf, token, responsavel);
                    if (!tokenValido) {
                        throw new ViewHelperException("mensagem.erro.senha.servidor.oauth2.token.invalido", responsavel);
                    }
                    request.setAttribute("senhaServidorOK", token);
                } else if (serSenhaObrigatoria) {
                    throw new ViewHelperException("mensagem.erro.senha.servidor.oauth2.token.ausente", responsavel);
                }
            } catch (final ServidorControllerException ex) {
                throw new ViewHelperException(ex);
            }

        } else {
            String senha = null;
            String loginExterno = null;

            // Obtém a Senha criptografada
            if (session.getAttribute("serAutorizacao") != null) {
                // Se o parâmetro com a Senha está na sessão, então dá preferencia para ele
                senha = (String) session.getAttribute("serAutorizacao");
                loginExterno = (String) session.getAttribute("serLogin");
                // Remove os atributos da sessão
                session.removeAttribute("serAutorizacao");
                session.removeAttribute("serLogin");
            } else {
                // A senha não está na sessão, então pode estar no request
                senha = JspHelper.verificaVarQryStr(request, uploadHelper, "serAutorizacao");
                loginExterno = JspHelper.verificaVarQryStr(request, uploadHelper, "serLogin");
            }

            // Se a senha continua nula, tenta obtê-la através do parâmetro que descreve o nome do campo
            if (TextHelper.isNull(senha)) {
                senha = JspHelper.verificaVarQryStr(request, uploadHelper, JspHelper.verificaVarQryStr(request, uploadHelper, "cryptedPasswordFieldName"));
            }

            if (!TextHelper.isNull(senha)) {
                final KeyPair keyPair = LoginHelper.getRSAKeyPair(request);
                String senhaAberta;

                try {
                    senhaAberta = RSA.decrypt(senha, keyPair.getPrivate());
                } catch (final BadPaddingException e) {
                    // Corresponde a tentativa de decriptografia com chave errada. A sessão pode ter expirado. Tentar novamente.
                    throw new ViewHelperException(validacaoParaDeferimentoReserva ? "mensagem.senha.servidor.autorizacao.invalida" : "mensagem.senha.servidor.consulta.invalida", responsavel);
                }

                // Valida a senha do servidor
                try {
                    validarSenhaServidor(rseCodigo, senhaAberta, JspHelper.getRemoteAddr(request), loginExterno, svcCodigo, validacaoParaDeferimentoReserva, false, responsavel);
                    request.setAttribute("senhaServidorOK", senhaAberta);

                    if (consomeSenha) {
                        String csaCodigo = JspHelper.verificaVarQryStr(request, uploadHelper, "CSA_CODIGO");
                        if (responsavel.isCsaCor()) {
                            csaCodigo = responsavel.getCsaCodigo();
                        }

                        // Se envia senha de autorização OTP para o servidor, a senha deve ser consumida.
                        final AutorizacaoController autorizacaoController = ApplicationContextProvider.getApplicationContext().getBean("autorizacaoController", AutorizacaoController.class);
                        autorizacaoController.consumirSenhaDeAutorizacao(null, null, rseCodigo, svcCodigo, csaCodigo, senhaAberta, true, false, false, true, responsavel);
                    }
                } catch (UsuarioControllerException | AutorizacaoControllerException ex) {
                    throw new ViewHelperException(ex);
                }
            } else if (serSenhaObrigatoria) {
                throw new ViewHelperException("mensagem.informe.ser.senha", responsavel);
            }
        }
    }

    /**
     * Valida a senha do servidor.
     * @param rseCodigo : Código do registro servidor.
     * @param serSenha : Senha a ser validada.
     * @param ip : Endereço IP para validação externa
     * @param loginExterno Login para validação externa
     * @param validacaoParaDeferimentoReserva : Se a validação é para uma operação de deferimento de reserva.
     * @param validacaoParaLogin : Se a validação é para uma operação de login.
     * @param responsavel : Responsável pela operação.
     * @return
     * @throws UsuarioControllerException
     */
    public static void validarSenhaServidor(String rseCodigo, String serSenha, String ip, String loginExterno, String svcCodigo, boolean validacaoParaDeferimentoReserva, boolean validacaoParaLogin, AcessoSistema responsavel) throws UsuarioControllerException {
        validarSenhaServidor(rseCodigo, serSenha, ip, loginExterno, svcCodigo, validacaoParaDeferimentoReserva, validacaoParaLogin, false, responsavel);
    }

    public static void validarSenhaServidor(String rseCodigo, String serSenha, String ip, String loginExterno, String svcCodigo, boolean validacaoParaDeferimentoReserva, boolean validacaoParaLogin, boolean senhaApp, AcessoSistema responsavel) throws UsuarioControllerException {
        try {
            final ServidorDelegate serDelegate = new ServidorDelegate();
            final UsuarioDelegate usuDelegate = new UsuarioDelegate();
            final TransferObject usuario = usuDelegate.getSenhaServidor(rseCodigo, responsavel);

            // Determina se deve ser utilizada a senha de autorização: se o parâmetro TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR
            // define que sim, e é deferimento de contratos ou não é validação de login e TPC_USA_SENHA_AUTORIZACAO_TODAS_OPERACOES = S
            // então será validada a senha de autorização.
            final boolean usaSenhaAutorizacaoSer = ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel);
            final boolean usaSenhaAutorizacaoTodasOpe = ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_TODAS_OPERACOES, CodedValues.TPC_SIM, responsavel);
            final boolean validacaoSenhaAutorizacao = usaSenhaAutorizacaoSer && (validacaoParaDeferimentoReserva || (!validacaoParaLogin && usaSenhaAutorizacaoTodasOpe));
            final boolean habilitaSenhaApp = ParamSist.paramEquals(CodedValues.TPC_HABILITA_SENHA_APP, CodedValues.TPC_SIM, responsavel);

            // Verifica se existe utiliza senha de consulta na reserva de margem caso o sistema utilize senha de autorização mas não utilize para todas as operações
            boolean usaSenhaConsultaReservaMargem = false;
            if (!TextHelper.isNull(svcCodigo) && usaSenhaAutorizacaoSer && !usaSenhaAutorizacaoTodasOpe) {
                try {
                    final ParametroDelegate parDelegate = new ParametroDelegate();
                    final ParamSvcCseTO pse = parDelegate.findParamSvcCse(new ParamSvcCseTO(CodedValues.TPS_USA_SENHA_CONSULTA_RESERVA_MARGEM, CodedValues.CSE_CODIGO_SISTEMA, svcCodigo), responsavel);
                    usaSenhaConsultaReservaMargem = (pse!= null && pse.getPseVlr() != null && pse.getPseVlr().trim().equals("1"));
                } catch (final ParametroControllerException ex) {
                    // Não existe o parâmetro para serviço
                }
            }

            // De acordo com o tipo de senha sendo validada, define as mensagens de erro para validação de senha.
            String msgSenhaInvalida = "mensagem.senha.servidor.consulta.invalida";
            String msgSenhaExpirada = "mensagem.senha.servidor.consulta.expirada";
            String msgSenhaNaoAtualizada = "mensagem.senha.servidor.consulta.nao.atualizada";
            String msgSenhaNaoEncontrada = "mensagem.senha.servidor.consulta.nao.encontrada";

            if (validacaoSenhaAutorizacao && !usaSenhaConsultaReservaMargem) {
                msgSenhaInvalida = "mensagem.senha.servidor.autorizacao.invalida";
                msgSenhaExpirada = "mensagem.senha.servidor.autorizacao.expirada";
                msgSenhaNaoAtualizada = "mensagem.senha.servidor.autorizacao.nao.atualizada";
                msgSenhaNaoEncontrada = "mensagem.senha.servidor.autorizacao.nao.encontrada";
            }

            if (validacaoParaLogin || senhaApp) {
                msgSenhaExpirada = "mensagem.senhaExpirada";
            }

            if (usuario == null) {
                throw new UsuarioControllerException(msgSenhaNaoEncontrada, responsavel);
            }

            // Obtém os dados do usuário servidor e de sua senha
            final String usuCodigo = (String) usuario.getAttribute(Columns.USU_CODIGO);
            String senha = null;
            Date usuDataExpSenha = null;

            // Valida o status do usuário servidor
            final String stuCodigo = usuario.getAttribute(Columns.USU_STU_CODIGO) != null ? usuario.getAttribute(Columns.USU_STU_CODIGO).toString() : CodedValues.STU_ATIVO;
            if (stuCodigo.equals(CodedValues.STU_EXCLUIDO)) {
                throw new UsuarioControllerException("mensagem.usuarioSenhaInvalidos", responsavel);
            } else if (CodedValues.STU_CODIGOS_INATIVOS.contains(stuCodigo)) {
                throw new UsuarioControllerException("mensagem.usuarioBloqueado", responsavel);
            }

            // Se tem máscara de loginExterno é porque é uma validação externa com login diferente da matricula.
            final String mascaraLogin = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MASCARA_LOGIN_EXTERNO_SERVIDOR, responsavel);
            if (!TextHelper.isNull(mascaraLogin)) {
                final CustomTransferObject servidor = serDelegate.buscaServidor(rseCodigo, responsavel);

                // Se deve fazer validação externa de login de servidor, e o login não foi passado
                // deveria retornar erro ao usuário, porém como paliativo, será utilizada a
                // matrícula inst, e caso seja nula a matricula, do servidor no lugar do login.
                // TODO Retornar erro caso o login não tenha sido enviado
                if (TextHelper.isNull(loginExterno)) {
                    if (!TextHelper.isNull(servidor.getAttribute(Columns.RSE_ESTABILIZADO))) {
                        loginExterno = servidor.getAttribute(Columns.RSE_ESTABILIZADO).toString();
                    } else {
                        loginExterno = servidor.getAttribute(Columns.SER_NRO_IDT).toString();
                    }
                }

                final CustomTransferObject result = SenhaExterna.getInstance().validarSenha(loginExterno, serSenha, ip);
                final String senhaOk = (String) result.getAttribute(SenhaExterna.KEY_SENHA);
                String msgErro = (String) result.getAttribute(SenhaExterna.KEY_ERRO);
                if (TextHelper.isNull(msgErro)) {
                    msgErro = msgSenhaInvalida;
                }
                if (senhaOk == null) {
                    throw new UsuarioControllerException(msgErro, responsavel);
                }

                final String resultRG = (String) result.getAttribute(SenhaExterna.KEY_RG);
                final String resultCPF = (String) result.getAttribute(SenhaExterna.KEY_CPF);

                final String cpf = (String) servidor.getAttribute(Columns.SER_CPF);
                final String rg = (String) servidor.getAttribute(Columns.SER_NRO_IDT);

                if ((resultCPF == null || !resultCPF.equals(cpf)) && (resultRG == null || !resultRG.equals(rg))) {
                    throw UsuarioControllerException.byMessage(SenhaExterna.getInstance().getErrorMessage("mensagem.erro.rgCpfNaoConferem"));
                }

                ControleLogin.getInstance().resetTetantivasLogin(usuCodigo);
                // SENHA OK. Retorna dizendo que não expirou.
                return;
            }

            if (validacaoSenhaAutorizacao && !usaSenhaConsultaReservaMargem) {
                if (ParamSist.paramEquals(CodedValues.TPC_USA_MULTIPLAS_SENHAS_AUTORIZACAO_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
                    // Se utiliza múltiplas senhas de autorização, obtém o registro de senha
                    // da tabela apropriada para armazenamento das senhas do servidor.

                    final TransferObject sas = usuDelegate.obtemSenhaAutorizacaoServidor(usuCodigo, serSenha, responsavel);
                    if (sas == null) {
                        // Se não encontrou registro de senha, pode ser porque foi informado errado, ou
                        // porque não foi cadastrado. Pesquisa os demais registros de senhas para definir
                        // a melhor mensagem de erro a ser disparada
                        final List<TransferObject> sasList = usuDelegate.lstSenhaAutorizacaoServidor(usuCodigo, responsavel);
                        if (sasList != null && sasList.size() > 0) {
                            throw new UsuarioControllerException(msgSenhaInvalida, responsavel);
                        }
                    } else {
                        senha = (String) sas.getAttribute(Columns.SAS_SENHA);
                        usuDataExpSenha = (Date) sas.getAttribute(Columns.SAS_DATA_EXPIRACAO);
                    }
                } else {
                    senha = (String) usuario.getAttribute(Columns.USU_SENHA_2);
                    usuDataExpSenha = (Date) usuario.getAttribute(Columns.USU_DATA_EXP_SENHA_2);
                }
            } else if(senhaApp && habilitaSenhaApp) {
                senha = (String) usuario.getAttribute(Columns.USU_SENHA_APP);
                usuDataExpSenha = (Date) usuario.getAttribute(Columns.USU_DATA_EXP_SENHA_APP);
            } else {
                senha = (String) usuario.getAttribute(Columns.USU_SENHA);
                usuDataExpSenha = (Date) usuario.getAttribute(Columns.USU_DATA_EXP_SENHA);
            }

            // Se não encontrou senha ou está cancelada, retorna erro de senha não cadastrada
            if (senha == null || senha.equalsIgnoreCase(CodedValues.USU_SENHA_SERVIDOR_CANCELADA)) {
                throw new UsuarioControllerException(msgSenhaNaoEncontrada, responsavel);
            }

            // Valida somente externamente se existir outro repositório de senhas,
            // somente se não for senha de autorização, pois as senhas de autorização
            // caso utilizadas são validadas localmente.
            if (!validacaoSenhaAutorizacao && !(senhaApp && habilitaSenhaApp) && ParamSist.paramEquals(CodedValues.TPC_SENHA_EXTERNA, CodedValues.TPC_SIM, responsavel)) {
                final CustomTransferObject servidor = serDelegate.buscaServidor(rseCodigo, responsavel);

                final String estIdentificador = servidor.getAttribute(Columns.EST_IDENTIFICADOR).toString();
                final String orgIdentificador = servidor.getAttribute(Columns.ORG_IDENTIFICADOR).toString();
                final String rseMatricula = servidor.getAttribute(Columns.RSE_MATRICULA).toString();
                final String serCpf = servidor.getAttribute(Columns.SER_CPF).toString();
                final String rseMatriculaInst = !TextHelper.isNull(servidor.getAttribute(Columns.RSE_MATRICULA_INST)) ? servidor.getAttribute(Columns.RSE_MATRICULA_INST).toString() : "";
                final String parametros[] = {estIdentificador, rseMatricula, serSenha, orgIdentificador, ip, serCpf, rseMatriculaInst};
                final CustomTransferObject result = SenhaExterna.getInstance().buscarSenha(parametros);
                final String senhaInicial = (String) result.getAttribute(SenhaExterna.KEY_SENHA_INICIAL);
                String senhaExterna = (String) result.getAttribute(SenhaExterna.KEY_SENHA);
                if (TextHelper.isNull(senhaExterna)) {
                    senhaExterna = senhaInicial;
                }

                // Se a senha externa é diferente de null, é porque o usuário com login
                // usu_login existe no outro repositório de dados
                if (senhaExterna != null) {
                    if (((!JCrypt.verificaSenha(serSenha, senhaExterna)
                            && !serSenha.equalsIgnoreCase(senhaExterna))
                            || (senhaExterna == null || senhaExterna.equals("")))
                            && (!senhaExterna.equalsIgnoreCase("true"))) {
                        try {
                            // verifica se já atingiu o num max de tentativas de login definidas em parâmetro de sistema
                            // se sim, bloqueia o usuário
                            if (validacaoParaLogin && usuario.getAttribute(Columns.USU_STU_CODIGO).equals(CodedValues.STU_ATIVO)) {
                                ControleLogin.getInstance().bloqueiaUsuario(usuario, responsavel, msgSenhaInvalida);
                            }
                        } catch (final ZetraException ze) {
                            throw new UsuarioControllerException(ze);
                        }

                        throw new UsuarioControllerException(msgSenhaInvalida, responsavel);
                    }

                    if (senhaExterna.equals(senhaInicial)) {
                        throw new UsuarioControllerException(msgSenhaNaoAtualizada, responsavel);
                    }

                    ControleLogin.getInstance().resetTetantivasLogin(usuCodigo);
                    // Não expira a senha se for externa.
                    return;
                } else {
                    final String mensagem = (String) result.getAttribute(SenhaExterna.KEY_ERRO);
                    if (TextHelper.isNull(mensagem)) {
                        throw new UsuarioControllerException(msgSenhaInvalida, responsavel);
                    }
                    // Se a senha externa é nula é porque o usuário não existe no outro
                    // repositório de dados
                    //TODO verificar uma forma melhor de pegar a mensagem de erro no SenhaExterna.buscarSenha
                    throw new UsuarioControllerException(mensagem, "mensagem.senhaServidorInvalida");
                }
            } else if (!JCrypt.verificaSenha(serSenha, senha)) {
                // Se a senha é incorreta
                try {
                    // verifica se já atingiu o num max de tentativas de login definidas em parâmetro de sistema
                    // se sim, bloqueia o usuário
                    if (validacaoParaLogin && usuario.getAttribute(Columns.USU_STU_CODIGO).equals(CodedValues.STU_ATIVO)) {
                        ControleLogin.getInstance().bloqueiaUsuario(usuario, responsavel, msgSenhaInvalida);
                    }
                } catch (final ZetraException ze) {
                    throw new UsuarioControllerException(ze);
                }
                throw new UsuarioControllerException(msgSenhaInvalida, responsavel);
            } else if ((validacaoParaDeferimentoReserva && ParamSist.paramEquals(CodedValues.TPC_SENHA_EXP_SERVIDOR_RESERVA_MARGEM, CodedValues.TPC_NAO, responsavel)) ||
                       (!validacaoParaLogin  && !validacaoParaDeferimentoReserva && ParamSist.paramEquals(CodedValues.TPC_SENHA_EXP_SERVIDOR_PODE_SER_USADA, CodedValues.TPC_NAO, responsavel)) ||
                       (validacaoParaLogin && ParamSist.paramEquals(CodedValues.TPC_VALIDA_EXP_SENHA_SER_ACESSO_SIST, CodedValues.TPC_SIM, responsavel)) ||
                       (senhaApp && habilitaSenhaApp) ) {
                // Se senha expirada não pode reservar margem, ou
                // se senha expirada não pode consultar margem, verifica a data de validade.
                // se é senhaApp verificar se a usu_data_exp_senha_app
                if (usuDataExpSenha != null && usuDataExpSenha.compareTo(DateHelper.getSystemDatetime()) < 0) {
                    throw new SenhaExpiradaException(msgSenhaExpirada, responsavel);
                }
            }

        } catch (final ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new UsuarioControllerException(ex);
        }
    }


    /**
     * Retorna a matricula do servidor se o usuário e senha informados conferirem.
     * @param loginExterno Login do usuário do servidor a ser validado.
     * @param senha        Senha a ser validada.
     * @param ip           Endereço IP do acesso.
     * @param responsavel  Usuário que está fazendo a chamada.
     * @return A matrícula do servidor.
     * @throws UsuarioControllerException
     */
    public static CustomTransferObject validarSenhaExternaServidor(String loginExterno, String senha, String ip, String matricula, boolean validarExpiracao, AcessoSistema responsavel) throws UsuarioControllerException {
        return SenhaExterna.getInstance().validarSenha(loginExterno, senha, ip);
    }

    /**
     * Criptografa a senha do usuário, de acordo com o modelo padrão utilizado no método
     * que salva a senha do usuáro na alteração: usuário servidor terá senha salva em
     * minúsculo.
     * @param usuLogin
     * @param usuSenha
     * @param usuarioSer
     * @param responsavel
     * @return
     */
    public static String criptografarSenha(String usuLogin, String usuSenha, boolean usuarioSer, AcessoSistema responsavel) {
        return JCrypt.crypt((usuarioSer ? usuSenha.toLowerCase() : usuSenha));
    }

    /**
     * Gera uma senha alteatória para usuários com o tamanho máximo permitido para senha
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    public static String gerarSenhaAleatoria(String tipoEntidade, AcessoSistema responsavel) throws UsuarioControllerException {
        // Define o tamanho da senha ser gerada, caso seja necessário.
        int tamanhoSenha = 8;
        try {
            final Object tamMaxSenhaUsuario = ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_USUARIOS, responsavel);
            tamanhoSenha = !TextHelper.isNull(tamMaxSenhaUsuario) ? Integer.parseInt(tamMaxSenhaUsuario.toString()) : 8;
        } catch (final Exception ex) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.usuario.tamanho.senha", responsavel) + ": " + ex.getMessage());
        }

        return GeradorSenhaUtil.getPassword(tamanhoSenha, tipoEntidade, responsavel);
    }

    /**
     * Método para implementar validação que é feita pela biblioteca JS passwordmeter
     * @param passwd
     * @param nivelConfigurado
     * @param responsavel
     * @return
     * @throws UsuarioControllerException
     */
    public static void validarForcaSenha(String passwd, boolean usuarioSer, AcessoSistema responsavel) throws UsuarioControllerException {
    	int tamMinSenha = 6;
        int tamMaxSenha = 15;

        try {
            if (responsavel.isSer() || usuarioSer) {
                tamMinSenha = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MIN_SENHA_SERVIDOR, responsavel)) ?
                                Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MIN_SENHA_SERVIDOR, responsavel).toString()): 6;
                tamMaxSenha = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel)) ?
                                Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_SERVIDOR, responsavel).toString()): 15;
            } else {
                tamMinSenha = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MIN_SENHA_USUARIOS, responsavel)) ?
                                Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MIN_SENHA_USUARIOS, responsavel).toString()): 6;
                tamMaxSenha = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_USUARIOS, responsavel)) ?
                                Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MAX_SENHA_USUARIOS, responsavel).toString()): 15;
            }
        } catch (final NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
            tamMinSenha = 6;
            tamMaxSenha = 15;
        }

        if (TextHelper.isNull(passwd) || passwd.length() < tamMinSenha) {
        	throw new UsuarioControllerException("mensagem.erro.cadastrar.senha.servidor.minimo", responsavel, String.valueOf(tamMinSenha));
        } else if (passwd.length() > tamMaxSenha) {
        	throw new UsuarioControllerException("mensagem.erro.cadastrar.senha.servidor.maximo", responsavel, String.valueOf(tamMaxSenha));
        }

        // Nível de Severidade da nova senha dos usuários
        String pwdStrength = "3";
        if (responsavel.isSer() || usuarioSer) {
            pwdStrength = ParamSist.getInstance().getParam(CodedValues.TPC_SER_PWD_STRENGTH_LEVEL, responsavel) != null ?
                          ParamSist.getInstance().getParam(CodedValues.TPC_SER_PWD_STRENGTH_LEVEL, responsavel).toString() : "3";
        } else if (responsavel.isCseSupOrg()) {
            pwdStrength = ParamSist.getInstance().getParam(CodedValues.TPC_CSE_ORG_PWD_STRENGTH_LEVEL, responsavel) != null ?
                          ParamSist.getInstance().getParam(CodedValues.TPC_CSE_ORG_PWD_STRENGTH_LEVEL, responsavel).toString() : "3";
        } else if (responsavel.isCsaCor()) {
            pwdStrength = ParamSist.getInstance().getParam(CodedValues.TPC_CSA_COR_PWD_STRENGTH_LEVEL, responsavel) != null ?
                          ParamSist.getInstance().getParam(CodedValues.TPC_CSA_COR_PWD_STRENGTH_LEVEL, responsavel).toString() : "3";
        }

        // Transforma o parâmetro em um número inteiro
        int intpwdStrength;
        try {
        	intpwdStrength = Integer.parseInt(pwdStrength);
        } catch (final NumberFormatException ex) {
        	LOG.error(ex.getMessage(), ex);
        	intpwdStrength = 3;
        }

        int pwdStrengthLevel = 1; // very weak
        String nivel = "muito.baixo";
        if (intpwdStrength == 2) { // weak
        	pwdStrengthLevel = 16;
        	nivel = "baixo";
        } else if (intpwdStrength == 3) { // mediocre
        	pwdStrengthLevel = 25;
        	nivel = "medio";
        } else if (intpwdStrength == 4) { // strong
        	pwdStrengthLevel = 35;
        	nivel = "alto";
        } else if (intpwdStrength >= 5) { // very strong
        	pwdStrengthLevel = 45;
        	nivel = "muito.alto";
        }

        int intScore = 0;

        if (passwd.length() < 5) {
            intScore = (intScore + 3);
        } else if (passwd.length() < 8) {
            intScore = (intScore + 6);
        } else if (passwd.length() < 16) {
            intScore = (intScore + 12);
        } else {
            intScore = (intScore + 18);
        }

        if (passwd.matches(".*[a-z].*")) {
            intScore = (intScore + 1);
        }

        if (passwd.matches(".*[A-Z].*")) {
            intScore = (intScore + 5);
        }

        if (passwd.matches(".*[0-9].*")) {
            intScore = (intScore + 5);
        }

        if (passwd.matches("(.*[0-9].*[0-9].*[0-9].*)")) {
            intScore = (intScore + 5);

            if (intpwdStrength >= 5 && Pattern.compile(numericSequencePattern, Pattern.CASE_INSENSITIVE).matcher(passwd).find()) {
            	// Se for nível muito alto e houver 3 números sequências, remove o score atribuído
            	intScore = (intScore - 5);
            }
        }

        if (passwd.matches(".*" + specialCharactersRegex + ".*")) {
            intScore = (intScore + 5);
        }

        if (passwd.matches("(.*" + specialCharactersRegex + ".*" + specialCharactersRegex + ".*)")) {
            intScore = (intScore + 5);
        }

        if (passwd.matches("(.*[a-z].*[A-Z].*)|(.*[A-Z].*[a-z].*)")) {
            intScore = (intScore + 2);
        }

        if (passwd.matches("(.*[a-zA-Z0-9].*)")) {
            intScore = (intScore + 2);
        }

        if (passwd.matches("(.*[a-zA-Z0-9].*" + specialCharactersRegex + ".*)|(.*" + specialCharactersRegex + ".*[a-zA-Z0-9].*)")) {
            intScore = (intScore + 2);
        }

        final boolean temMinuscula = passwd.matches(".*[a-z].*");
        final boolean temMaiuscula = passwd.matches(".*[A-Z].*");
        final boolean temNumero = passwd.matches(".*[0-9].*");
        final boolean temEspecial = passwd.matches(".*" + specialCharactersRegex + ".*");

        final Pattern repeatedPattern = Pattern.compile("(.)\\1{2}");
        final Pattern numericPattern = Pattern.compile(numericSequencePattern, Pattern.CASE_INSENSITIVE);
        final Pattern alphabeticPattern = Pattern.compile(alphabeticSequencePattern, Pattern.CASE_INSENSITIVE);

        final Matcher repeatedMatcher = repeatedPattern.matcher(passwd);
        final Matcher numericMatcher = numericPattern.matcher(passwd);
        final Matcher alphabeticMatcher = alphabeticPattern.matcher(passwd);
        final int categorias = (temMinuscula ? 1 : 0) + (temMaiuscula ? 1 : 0) + (temNumero ? 1 : 0) + (temEspecial ? 1 : 0);

        if (!repeatedMatcher.find() && !numericMatcher.find() && !alphabeticMatcher.find()) {
            intScore = (intScore + 9);
        }

        if (categorias >= 3) {
            intScore = (intScore + 9);
        }

        if (intScore < pwdStrengthLevel) {
            String chave = "rotulo.ajuda.alteracaoSenha." + nivel + ".geral";
            if (responsavel.isSer() || usuarioSer) {
                chave = "rotulo.ajuda.alteracaoSenha." + nivel + ".servidor";
                if (ParamSist.paramEquals(CodedValues.TPC_SENHA_CONS_SERVIDOR_SOMENTE_NUMERICA, CodedValues.TPC_SIM, responsavel)) {
                    chave += ".numerica";
                }
            }
            throw new UsuarioControllerException(chave, responsavel);
        }
    }

    public static int getPwdStrength(String tipo, AcessoSistema responsavel) {
        if (tipo == null) {
            tipo = String.valueOf(responsavel);
        }
        String pwdStrength = switch (tipo) {
            case AcessoSistema.ENTIDADE_CSE, AcessoSistema.ENTIDADE_ORG, AcessoSistema.ENTIDADE_SUP ->
                    ParamSist.getInstance().getParam(CodedValues.TPC_CSE_ORG_PWD_STRENGTH_LEVEL, responsavel) != null ?
                            ParamSist.getInstance().getParam(CodedValues.TPC_CSE_ORG_PWD_STRENGTH_LEVEL, responsavel).toString() : "3";
            case AcessoSistema.ENTIDADE_CSA, AcessoSistema.ENTIDADE_COR ->
                    ParamSist.getInstance().getParam(CodedValues.TPC_CSA_COR_PWD_STRENGTH_LEVEL, responsavel) != null ?
                            ParamSist.getInstance().getParam(CodedValues.TPC_CSA_COR_PWD_STRENGTH_LEVEL, responsavel).toString() : "3";
            case AcessoSistema.ENTIDADE_SER ->
                    ParamSist.getInstance().getParam(CodedValues.TPC_SER_PWD_STRENGTH_LEVEL, responsavel) != null ?
                            ParamSist.getInstance().getParam(CodedValues.TPC_SER_PWD_STRENGTH_LEVEL, responsavel).toString() : "3";
            default -> "3";
        };

        int intpwdStrength;
        try {
            intpwdStrength = Integer.parseInt(pwdStrength);
        } catch (final NumberFormatException ex) {
            LOG.error(ex.getMessage(), ex);
            intpwdStrength = 3;
        }
        return intpwdStrength;
    }
}