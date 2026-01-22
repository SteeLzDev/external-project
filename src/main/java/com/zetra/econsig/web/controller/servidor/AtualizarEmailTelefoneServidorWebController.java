package com.zetra.econsig.web.controller.servidor;

import java.security.Key;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.senha.GeradorSenhaUtil;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.web.controller.AbstractWebController;

@Controller
@RequestMapping(value = { "/v3/atualizarEmailTelefone" })
public class AtualizarEmailTelefoneServidorWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AtualizarEmailTelefoneServidorWebController.class);

    @Autowired
    private ServidorController servidorController;

    @RequestMapping(method = { RequestMethod.GET, RequestMethod.POST })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            model.addAttribute("acao", "salvar");

            if (ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_ATUALIZACAO_DADOS_SER_PRIMEIRO_ACESSO, responsavel) &&
                    !ShowFieldHelper.showField(FieldKeysConstants.ATUALIZAR_DADOS_SER_EMAIL, responsavel) &&
                    !ShowFieldHelper.showField(FieldKeysConstants.ATUALIZAR_DADOS_SER_TELEFONE, responsavel) &&
                    !ShowFieldHelper.showField(FieldKeysConstants.ATUALIZAR_DADOS_SER_CELULAR, responsavel)) {

                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.nenhuma.informacao.servidor.ativa", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            int qtdeDiasAcessoSistemaSemValidaEmailHabilitado = TextHelper.isNum(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_ACESSO_SISTEMA_SERVIDOR_SEM_VALIDACAO_EMAIL, responsavel)) ? Integer.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_ACESSO_SISTEMA_SERVIDOR_SEM_VALIDACAO_EMAIL, responsavel).toString()) : 0;
            boolean paramQtdeDiasAcessoSistemaSemValidaEmailHabilitado = qtdeDiasAcessoSistemaSemValidaEmailHabilitado > 0 ? Boolean.TRUE : Boolean.FALSE;
            model.addAttribute("paramQtdeDiasAcessoSistemaSemValidaEmailHabilitado", paramQtdeDiasAcessoSistemaSemValidaEmailHabilitado);

            ServidorTransferObject servidor = servidorController.findServidor(responsavel.getCodigoEntidade(), responsavel);

            String email = (String) servidor.getAttribute(Columns.SER_EMAIL);
            String telefone = (String) servidor.getAttribute(Columns.SER_TEL);
            String celular = (String) servidor.getAttribute(Columns.SER_CELULAR);
            boolean permiteAltEmail = servidor.getSerPermiteAlterarEmail().equalsIgnoreCase("N") ? false : true;

            model.addAttribute("email", email);
            model.addAttribute("telefone", telefone);
            model.addAttribute("celular", celular);
            model.addAttribute("permiteAltEmail", permiteAltEmail);

            session.removeAttribute("cripto");

            return viewRedirect("jsp/atualizarEmailTelefone/atualizarEmailTelefone", request, session, model, responsavel);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("mensagem.atualizacao.email.telefone.titulo", responsavel));
    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=gerarTokenEmail" })
    public String gerarTokenEmail(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String emailIncorreto = JspHelper.verificaVarQryStr(request, "email_Incorreto");
        if (emailIncorreto != null && emailIncorreto.equals("S")) {
            return seguirFluxoEmailIncorreto(request, response, session, model);
        }

        try {


            int qtdeDiasAcessoSistemaSemValidaEmailHabilitado = TextHelper.isNum(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_ACESSO_SISTEMA_SERVIDOR_SEM_VALIDACAO_EMAIL, responsavel)) ? Integer.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_ACESSO_SISTEMA_SERVIDOR_SEM_VALIDACAO_EMAIL, responsavel).toString()) : 0;
            boolean exigeAtualizacaoDados = ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_ATUALIZACAO_DADOS_SER_PRIMEIRO_ACESSO, responsavel);
            boolean paramQtdeDiasAcessoSistemaSemValidaEmailHabilitado = qtdeDiasAcessoSistemaSemValidaEmailHabilitado > 0 ? Boolean.TRUE : Boolean.FALSE;
            model.addAttribute("paramQtdeDiasAcessoSistemaSemValidaEmailHabilitado", paramQtdeDiasAcessoSistemaSemValidaEmailHabilitado);

            if (exigeAtualizacaoDados) {
                model.addAttribute("exigeAtualizacaoDadosSerPrimeiroAcesso", Boolean.TRUE);
            }

            ServidorTransferObject servidor = servidorController.findServidor(responsavel.getCodigoEntidade(), responsavel);

            String telefone = JspHelper.verificaVarQryStr(request, "telefone");
            String ddd = JspHelper.verificaVarQryStr(request, "ddd");
            String email = JspHelper.verificaVarQryStr(request, "email");
            String celular = JspHelper.verificaVarQryStr(request, "celular");
            String dddCel = JspHelper.verificaVarQryStr(request, "dddCel");

            boolean existeEmailCadastrado = servidorController.existeEmailCadastrado(email.trim(), servidor.getSerCpf(), responsavel);
            if (existeEmailCadastrado) {
                throw new ServidorControllerException("mensagem.erro.email.informado.em.uso.outro.cpf", responsavel);
            }

            if (!TextHelper.isEmailValid(email)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.email.invalido", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_CELULAR, responsavel) && exigeAtualizacaoDados && TextHelper.isNull(celular)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ser.celular", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_TELEFONE, responsavel) && exigeAtualizacaoDados && TextHelper.isNull(telefone)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ser.telefone", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_EMAIL, responsavel) && exigeAtualizacaoDados && TextHelper.isNull(email)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ser.email", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            model.addAttribute("telefone", telefone);
            model.addAttribute("ddd", ddd);
            model.addAttribute("celular", celular);
            model.addAttribute("dddCel", dddCel);
            model.addAttribute("email", email);
            model.addAttribute("acao", "salvar");
            model.addAttribute("exibeCodigo", "true");

            //Gera um token e o criptografa para ser enviado no request
            String token = GeradorSenhaUtil.getPassword(6, AcessoSistema.ENTIDADE_SER, responsavel);
            Key privateKeyEConsig = RSA.generatePrivateKey(CodedValues.RSA_MODULUS_ECONSIG, CodedValues.RSA_PRIVATE_KEY_ECONSIG);
            String criptoToken = RSA.encrypt(token, privateKeyEConsig);
            session.setAttribute("cripto", criptoToken);

            EnviaEmailHelper.enviarEmailCodigoVerificacaoEmailServidor(email, token, responsavel);

            return viewRedirect("jsp/atualizarEmailTelefone/atualizarEmailTelefone", request, session, model, responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

    }

    @RequestMapping(method = { RequestMethod.POST }, params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            String email = JspHelper.verificaVarQryStr(request, "email");
            String cripto = (String) session.getAttribute("cripto");

            if (ShowFieldHelper.showField(FieldKeysConstants.ATUALIZAR_DADOS_SER_EMAIL, responsavel) && !TextHelper.isNull(email) && TextHelper.isNull(cripto)) {
                return gerarTokenEmail(request, response, session, model);
            }

            String emailIncorreto = JspHelper.verificaVarQryStr(request, "email_Incorreto");
            if (emailIncorreto != null && emailIncorreto.equals("S")) {
                return seguirFluxoEmailIncorreto(request, response, session, model);
            }

            String token = JspHelper.verificaVarQryStr(request, "tokenEmail");
            String telefone = JspHelper.verificaVarQryStr(request, "telefone");
            String ddd = JspHelper.verificaVarQryStr(request, "ddd");
            String celular = JspHelper.verificaVarQryStr(request, "celular");
            String dddCel = JspHelper.verificaVarQryStr(request, "dddCel");

            boolean exigeAtualizacaoDadosSerPrimeiroAcesso = ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_ATUALIZACAO_DADOS_SER_PRIMEIRO_ACESSO, AcessoSistema.getAcessoUsuarioSistema());

            if (exigeAtualizacaoDadosSerPrimeiroAcesso) {
                model.addAttribute("exigeAtualizacaoDadosSerPrimeiroAcesso", Boolean.TRUE);
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_EMAIL, responsavel) && !TextHelper.isEmailValid(email) && exigeAtualizacaoDadosSerPrimeiroAcesso) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.email.invalido", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_CELULAR, responsavel) && exigeAtualizacaoDadosSerPrimeiroAcesso && TextHelper.isNull(celular)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ser.celular", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_TELEFONE, responsavel) && exigeAtualizacaoDadosSerPrimeiroAcesso && TextHelper.isNull(telefone)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ser.telefone", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_EMAIL, responsavel) && exigeAtualizacaoDadosSerPrimeiroAcesso && TextHelper.isNull(email)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ser.email", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (exigeAtualizacaoDadosSerPrimeiroAcesso && !TextHelper.isNull(email)) {
                Key privateKeyEConsig = RSA.generatePrivateKey(CodedValues.RSA_MODULUS_ECONSIG, CodedValues.RSA_PRIVATE_KEY_ECONSIG);
                String criptoToken = RSA.encrypt(token, privateKeyEConsig);

                if (TextHelper.isNull(token) || TextHelper.isNull(cripto)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.validacao.troca.email", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                if (!cripto.equals(criptoToken)) {
                    model.addAttribute("telefone", telefone);
                    model.addAttribute("ddd", ddd);
                    model.addAttribute("celular", celular);
                    model.addAttribute("dddCel", dddCel);
                    model.addAttribute("email", email);
                    model.addAttribute("acao", "salvar");
                    model.addAttribute("exibeCodigo", "true");
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.token.troca.email", responsavel));
                    return viewRedirect("jsp/atualizarEmailTelefone/atualizarEmailTelefone", request, session, model, responsavel);
                }
            }

            ServidorTransferObject servidor = servidorController.findServidor(responsavel.getCodigoEntidade(), responsavel);

            if (exigeAtualizacaoDadosSerPrimeiroAcesso && ShowFieldHelper.showField(FieldKeysConstants.ATUALIZAR_DADOS_SER_EMAIL, responsavel)) {
                if ((ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_EMAIL, responsavel) && TextHelper.isNull(email)) || (!TextHelper.isNull(email) && !TextHelper.isEmailValid(email))) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.email.invalido", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
                if (!TextHelper.isNull(email) && TextHelper.isEmailValid(email)) {
                    servidor.setAttribute(Columns.SER_EMAIL, email);
                    servidor.setAttribute(Columns.SER_DATA_VALIDACAO_EMAIL, new Timestamp(Calendar.getInstance().getTimeInMillis()));
                }
            }

            if (exigeAtualizacaoDadosSerPrimeiroAcesso && ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_TELEFONE, responsavel)) {
                if (!TextHelper.isNull(ddd) || !TextHelper.isNull(telefone)) {
                    if (TextHelper.isNull(telefone)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.telefone", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }

                    if (!TextHelper.isNull(ddd)) {
                        telefone = ddd + '-' + telefone;
                    }

                    servidor.setAttribute(Columns.SER_TEL, telefone);
                }
            }

            if (exigeAtualizacaoDadosSerPrimeiroAcesso && ShowFieldHelper.isRequired(FieldKeysConstants.ATUALIZAR_DADOS_SER_CELULAR, responsavel)) {
                if (!TextHelper.isNull(dddCel) || !TextHelper.isNull(celular)) {
                    if (TextHelper.isNull(celular)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.servidor.celular", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }

                    if (!TextHelper.isNull(dddCel)) {
                        celular = dddCel + '-' + celular;
                    }

                    servidor.setAttribute(Columns.SER_CELULAR, celular);
                }
            }

            servidorController.updateServidor(servidor, responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.info.atualizacao.email.telefone.sucesso", responsavel));
            session.removeAttribute("ExigeEmailOuTelefone");

            return "redirect:../v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true";

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    public String seguirFluxoEmailIncorreto(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {

            String serCodigo = responsavel.getCodigoEntidade();
            String qtdeDias = ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_ACESSO_SISTEMA_SERVIDOR_SEM_VALIDACAO_EMAIL, responsavel).toString();
            String tmoObs = ApplicationResourcesHelper.getMessage("mensagem.informacao.email.incorreto.ocorrencia", responsavel, qtdeDias);
            String tocCodigo = CodedValues.TOC_DIVERGENCIA_CADASTRO_EMAIL_SERVIDOR;

            List<TransferObject> listaOcorrenciaEmailIncorretoServidor = servidorController.lstDataOcorrenciaServidor(serCodigo, tocCodigo, responsavel);

            if (!listaOcorrenciaEmailIncorretoServidor.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informacao.email.incorreto.prazo.acesso.sistema.expirado", responsavel, responsavel.getUsuNome()));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            //Ocorrencia de e-mail divergente + dias
            servidorController.criaOcorrenciaSER(serCodigo, CodedValues.TOC_DIVERGENCIA_CADASTRO_EMAIL_SERVIDOR, tmoObs, null, responsavel);

            //Mostra mensagem informando o servidor que pode usar o sistema em quantos dias sem validação e procurar o RH
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informacao.email.incorreto.prazo.acesso.sistema", responsavel, responsavel.getUsuNome(), qtdeDias));
            session.removeAttribute("ExigeEmailOuTelefone");

        } catch (ServidorControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        return "redirect:../v3/carregarPrincipal?mostraMensagem=true&limitaMsg=true";
    }
}
