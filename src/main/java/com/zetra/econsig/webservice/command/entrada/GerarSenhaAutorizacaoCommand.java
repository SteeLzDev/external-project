package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.RSE_TIPO;
import static com.zetra.econsig.webservice.CamposAPI.DATA_NASCIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.ESTABELECIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.EST_NOME;
import static com.zetra.econsig.webservice.CamposAPI.GERAR_LOG;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_NOME;
import static com.zetra.econsig.webservice.CamposAPI.RSE_PRAZO;
import static com.zetra.econsig.webservice.CamposAPI.PROTOCOLO_SENHA_AUTORIZACAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_DATA_ADMISSAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA_AUTORIZACAO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA_USUARIO;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.Map;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.entidade.OcorrenciaUsuarioTransferObject;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusProtocoloSenhaAutorizacaoEnum;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: GerarSenhaAutorizacaoCommand</p>
 * <p>Description: Classe command que trata requisição externa ao eConsig para gerar senha de autorização para servidor.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class GerarSenhaAutorizacaoCommand extends VerificarLimitesSenhaAutorizacaoCommand {

    public GerarSenhaAutorizacaoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {

        // Não gerar log de validação do protocolo de senha de autorização
        parametros.put(GERAR_LOG, Boolean.FALSE);
        // Realiza as validações da verificação dos limites para geração de senha de autorização
        super.executaOperacao(parametros);

        String rseCodigo = (String) parametros.get(RSE_CODIGO);
        String usuSuporte = (String) parametros.get(USUARIO);
        String usuSuporteSenha = (String) parametros.get(SENHA_USUARIO);
        String psaCodigo = (String) parametros.get(PROTOCOLO_SENHA_AUTORIZACAO);
        String usuCodigo = responsavel.getUsuCodigo();
        String serCodigo = responsavel.getSerCodigo();

        UsuarioDelegate usuDelegate = new UsuarioDelegate();
        ServidorDelegate serDelegate = new ServidorDelegate();

        boolean exigeProtocoloHostaHost = ParamSist.paramEquals(CodedValues.TPC_EXIGE_PROTOCOLO_GERA_SENHA_AUT_HOST_HOST, CodedValues.TPC_SIM, responsavel);

        if (exigeProtocoloHostaHost) {
            if (TextHelper.isNull(usuSuporte) || TextHelper.isNull(usuSuporteSenha)) {
                throw new ZetraException("mensagem.informe.usuario.senha", responsavel);
            }

            if (TextHelper.isNull(psaCodigo)) {
                throw new ZetraException("mensagem.protocolo.senha.servidor.autorizacao.obrigatorio", responsavel);
            }
        }

        // Gera senha de autorização
        String novaSenhaPlana = usuDelegate.gerarSenhaAutorizacao(usuCodigo, true, responsavel);

        if (exigeProtocoloHostaHost) {
            // Cria usuário suporte responsavel
            CustomTransferObject usuario = usuDelegate.findTipoUsuario(usuSuporte, AcessoSistema.getAcessoUsuarioSistema());
            String usuCodigoSuporte = usuario.getAttribute(Columns.USU_CODIGO).toString();
            AcessoSistema usuarioSuporteResponsavel = new AcessoSistema(usuCodigoSuporte);
            String funAutEmissaoSenhaAutorizacao = CodedValues.FUN_AUT_EMISSAO_SENHA_AUTORIZACAO;
            usuarioSuporteResponsavel.setFunCodigo(funAutEmissaoSenhaAutorizacao);
            usuarioSuporteResponsavel.setUsuCodigo(usuCodigoSuporte);
            usuarioSuporteResponsavel.setUsuNome(usuario.getAttribute(Columns.USU_NOME).toString());
            usuarioSuporteResponsavel.setCanal(CanalEnum.SOAP);
            usuarioSuporteResponsavel.setTipoEntidade(AcessoSistema.ENTIDADE_SUP);
            usuarioSuporteResponsavel.setCodigoEntidade(usuario.getAttribute(Columns.USP_CSE_CODIGO).toString());
            usuarioSuporteResponsavel.setIpUsuario(responsavel.getIpUsuario());
            usuarioSuporteResponsavel.setPortaLogicaUsuario(responsavel.getPortaLogicaUsuario());

            // Grava protocolo de senha de autorização
            usuDelegate.createProtocoloSenhaAutorizacao(psaCodigo, responsavel.getUsuCodigo(), usuarioSuporteResponsavel);

            // Gera log de protocolo de senha de autorização consumido
            LogDelegate logDelegate = new LogDelegate(usuarioSuporteResponsavel, Log.PROTOCOLO_SENHA_AUTORIZACAO, Log.CREATE, Log.LOG_INFORMACAO);
            logDelegate.setProtocoloSenhaAutorizacao(psaCodigo);
            logDelegate.setRegistroServidor(rseCodigo);
            logDelegate.setUsuario(usuCodigo);
            StatusProtocoloSenhaAutorizacaoEnum consumido = StatusProtocoloSenhaAutorizacaoEnum.CONSUMIDO;
            logDelegate.setStatusProtocoloSenhaAutorizacao(consumido.getCodigo());
            logDelegate.add(ApplicationResourcesHelper.getMessage("rotulo.protocolo.senha.autorizacao.singular", usuarioSuporteResponsavel) + ": \"" + psaCodigo + "\" " + consumido.getDescricao());
            logDelegate.write();

            // Cria ocorrência de usuário de autorização de senha
            OcorrenciaUsuarioTransferObject ocorrencia = new OcorrenciaUsuarioTransferObject();
            ocorrencia.setUsuCodigo(usuCodigo);
            ocorrencia.setTocCodigo(CodedValues.TOC_AUT_GERAR_SENHA_AUTORIZACAO_TOTEM);
            ocorrencia.setOusUsuCodigo(usuCodigoSuporte);
            ocorrencia.setOusObs(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ous.obs.autorizacao.senha.autorizacao.host.a.host", usuarioSuporteResponsavel));
            ocorrencia.setOusIpAcesso(usuarioSuporteResponsavel.getIpUsuario());

            usuDelegate.createOcorrenciaUsuario(ocorrencia, usuarioSuporteResponsavel);
        }

        // Verifica o modo de entrega da senha de autorização: ESCOLHA[1=E-mail;2=Tela;3=E-mail/Tela]
        String modoEntrega = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, responsavel)) ? ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, responsavel).toString() : CodedValues.ALTERACAO_SENHA_AUT_SER_ENVIA_EMAIL;
        // Verifica se sempre entrega para a senha
        boolean sempreEnviaSenha = ParamSist.paramEquals(CodedValues.TPC_SOAP_ENVIA_SENHA_AUT_SERVIDOR, CodedValues.TPC_SIM, responsavel);

        try {
            String serEmail = null;
            String serCel = null;
            if ((modoEntrega.equals(CodedValues.ALTERACAO_SENHA_AUT_SER_ENVIA_EMAIL) || modoEntrega.equals(CodedValues.ALTERACAO_SENHA_AUT_SER_SMS) || modoEntrega.equals(CodedValues.ALTERACAO_SENHA_AUT_SER_SMS_E_EMAIL)) && !sempreEnviaSenha) {
                // Busca os dados do servidor para obter o endereço de e-mail, caso exista
                ServidorTransferObject servidor = null;
                if (responsavel.isSer()) {
                    servidor = serDelegate.findServidor(responsavel.getSerCodigo(), responsavel);
                } else {
                    servidor = serDelegate.findServidor(serCodigo, responsavel);
                }
                serEmail = servidor.getSerEmail();
                serCel = servidor.getSerCelular();
            }


//            if (modoEntrega.equals(CodedValues.ALTERACAO_SENHA_AUT_SER_ENVIA_EMAIL) ||
//                    (modoEntrega.equals(CodedValues.ALTERACAO_SENHA_AUT_SER_EMAIL_OU_TELA) && !TextHelper.isNull(serEmail)) ||
//                    (modoEntrega.equals(CodedValues.ALTERACAO_SENHA_AUT_SER_SMS_E_EMAIL) && TextHelper.isNull(serCel) && !TextHelper.isNull(serEmail))) {
//                     session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.sucesso.envia.email", responsavel));
//                 } else if (modoEntrega.equals(CodedValues.ALTERACAO_SENHA_AUT_SER_SMS) ||
//                           (modoEntrega.equals(CodedValues.ALTERACAO_SENHA_AUT_SER_SMS_E_EMAIL) && TextHelper.isNull(serEmail) && !TextHelper.isNull(serCel))) {
//                     session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.sucesso.envia.sms", responsavel));
//                 } else if (modoEntrega.equals(CodedValues.ALTERACAO_SENHA_AUT_SER_EXIBE_TELA) ||
//                           (modoEntrega.equals(CodedValues.ALTERACAO_SENHA_AUT_SER_EMAIL_OU_TELA) && TextHelper.isNull(serEmail))) {
//                     session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.sucesso.exibe.tela", responsavel) + " <font class=\"novaSenha\">" + novaSenhaPlana + "</font>");
//                 } else if (modoEntrega.equals(CodedValues.ALTERACAO_SENHA_AUT_SER_SMS_E_EMAIL) &&
//                            !TextHelper.isNull(serCel) &&
//                            !TextHelper.isNull(serEmail)) {
//                   session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.sucesso.envia.email.sms", responsavel));
//                 } else {
//                     session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
//                 }


            if (responsavel.isSer()) {
                // Verifica se a senha pode ser exibida na tela para o usuário, ou apenas para envio por e-mail
                if (sempreEnviaSenha){
                    // Neste caso ignora o parâmetro do modo de entrega
                    parametros.put(MENSAGEM, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.sucesso.exibe.tela", responsavel) + " <font class=\"novaSenha\">" + novaSenhaPlana + "</font>");
                    parametros.put(SENHA_AUTORIZACAO, novaSenhaPlana);
                } else if (TextHelper.isNull(serEmail) && modoEntrega.equals(CodedValues.ALTERACAO_SENHA_AUT_SER_ENVIA_EMAIL)){
                    // Se só entrega as senhas de autorização por e-mail, e o servidor
                    // não possui endereço de e-mail cadastrado, então não deixa gerar
                    parametros.put(MENSAGEM, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.erro.email.invalido", responsavel));
                    throw new ZetraException("mensagem.senha.servidor.autorizacao.erro.email.invalido", responsavel);
                } else if (TextHelper.isNull(serCel) && modoEntrega.equals(CodedValues.ALTERACAO_SENHA_AUT_SER_SMS)){
                    // Se só entrega as senhas de autorização por SMS, e o servidor
                    // não possui celular cadastrado, então não deixa gerar
                    parametros.put(MENSAGEM, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.erro.celular.invalido", responsavel));
                    throw new ZetraException("mensagem.senha.servidor.autorizacao.erro.celular.invalido", responsavel);
                } else if (TextHelper.isNull(serCel) && TextHelper.isNull(serEmail) && modoEntrega.equals(CodedValues.ALTERACAO_SENHA_AUT_SER_SMS_E_EMAIL)){
                    // Entrega as senhas de autorização por email/SMS, e o servidor
                    // não possui celular ou e-mail cadastrado, então não deixa gerar
                    parametros.put(MENSAGEM, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.erro.email.celular.invalido", responsavel));
                    throw new ZetraException("mensagem.senha.servidor.autorizacao.erro.email.celular.invalido", responsavel);
                } else if (modoEntrega.equals(CodedValues.ALTERACAO_SENHA_AUT_SER_EXIBE_TELA) ||
                        (modoEntrega.equals(CodedValues.ALTERACAO_SENHA_AUT_SER_EMAIL_OU_TELA))) {
                    // Se pode exibir na tela, informa a senha em qualquer situação
                    parametros.put(MENSAGEM, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.sucesso.exibe.tela", responsavel) + " <font class=\"novaSenha\">" + novaSenhaPlana + "</font>");
                    parametros.put(SENHA_AUTORIZACAO, novaSenhaPlana);
                } else if (modoEntrega.equals(CodedValues.ALTERACAO_SENHA_AUT_SER_ENVIA_EMAIL)) {
                    parametros.put(MENSAGEM, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.sucesso.envia.email", responsavel));
                } else if (modoEntrega.equals(CodedValues.ALTERACAO_SENHA_AUT_SER_SMS)) {
                    parametros.put(MENSAGEM, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.sucesso.envia.sms", responsavel));
                } else if (modoEntrega.equals(CodedValues.ALTERACAO_SENHA_AUT_SER_SMS_E_EMAIL)) {
                    parametros.put(MENSAGEM, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.sucesso.envia.email.sms", responsavel));
                } else if (modoEntrega.equals(CodedValues.ALTERACAO_SENHA_AUT_SER_EMAIL_E_TELA)) {
                    parametros.put(MENSAGEM, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.sucesso.envia.email", responsavel));
                    parametros.put(SENHA_AUTORIZACAO, novaSenhaPlana);
                } else {
                    throw new ZetraException("mensagem.erroInternoSistema", responsavel);
                }
            }
        } catch (UsuarioControllerException ex) {
            throw ex;
        }

        if (rseCodigo != null && !rseCodigo.equals("")) {
            CustomTransferObject servidor = serDelegate.buscaServidor(rseCodigo, responsavel);
            parametros.put(ORGAO, servidor.getAttribute(Columns.ORG_IDENTIFICADOR));
            parametros.put(ORG_NOME, servidor.getAttribute(Columns.ORG_NOME));
            parametros.put(ESTABELECIMENTO, servidor.getAttribute(Columns.EST_IDENTIFICADOR));
            parametros.put(EST_NOME, servidor.getAttribute(Columns.EST_NOME));
            parametros.put(RSE_TIPO, servidor.getAttribute(Columns.RSE_TIPO));
            parametros.put(SERVIDOR, servidor.getAttribute(Columns.SER_NOME));
            parametros.put(SER_CPF, servidor.getAttribute(Columns.SER_CPF));
            parametros.put(DATA_NASCIMENTO, servidor.getAttribute(Columns.SER_DATA_NASC));
            parametros.put(RSE_MATRICULA, servidor.getAttribute(Columns.RSE_MATRICULA));
            parametros.put(RSE_DATA_ADMISSAO, servidor.getAttribute(Columns.RSE_DATA_ADMISSAO));
            parametros.put(RSE_PRAZO, servidor.getAttribute(Columns.RSE_PRAZO));
        } else {
            throw new ZetraException("mensagem.nenhumServidorEncontrado", responsavel);
        }
    }
}