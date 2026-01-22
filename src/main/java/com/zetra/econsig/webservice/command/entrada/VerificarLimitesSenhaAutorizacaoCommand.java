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
import static com.zetra.econsig.webservice.CamposAPI.SENHA_USUARIO;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.Map;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
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
 * <p>Title: VerificarLimitesSenhaAutorizacaoCommand</p>
 * <p>Description: Classe command que trata requisição externa ao eConsig para verificar limites para geração de senha de autorização.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class VerificarLimitesSenhaAutorizacaoCommand extends RequisicaoExternaCommand {

    public VerificarLimitesSenhaAutorizacaoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        String rseCodigo = (String) parametros.get(RSE_CODIGO);
        String usuSuporte = (String) parametros.get(USUARIO);
        String usuSuporteSenha = (String) parametros.get(SENHA_USUARIO);
        String psaCodigo = (String) parametros.get(PROTOCOLO_SENHA_AUTORIZACAO);
        boolean gerarLogValidacaoProtocoloSenhaAut = !TextHelper.isNull(parametros.get(GERAR_LOG)) ? Boolean.valueOf(parametros.get(GERAR_LOG).toString()) : true;
        String usuCodigo = responsavel.getUsuCodigo();

        UsuarioDelegate usuDelegate = new UsuarioDelegate();
        ServidorDelegate serDelegate = new ServidorDelegate();

        // Senhas múltiplas de autorização servidor habilitada
        boolean usaMultiplasSenhasAut = ParamSist.paramEquals(CodedValues.TPC_USA_MULTIPLAS_SENHAS_AUTORIZACAO_SERVIDOR, CodedValues.TPC_SIM, responsavel);
        if (!usaMultiplasSenhasAut) {
            throw new ZetraException("mensagem.erro.ser.senha.multipla.desabilitada", responsavel);
        }

        // Quantidade de senhas de autorização diárias que podem ser geradas via totem
        Object objMaxSenhasAutorizacaoTotem = ParamSist.getInstance().getParam(CodedValues.TPC_QTD_MAX_SENHAS_AUTORIZACAO_VIA_TOTEM, responsavel);
        int qtdMaxSenhasAutorizacaoTotem = 0;
        try {
            qtdMaxSenhasAutorizacaoTotem = !TextHelper.isNull(objMaxSenhasAutorizacaoTotem) ? Integer.parseInt(objMaxSenhasAutorizacaoTotem.toString()) : 0;
        } catch (NumberFormatException e) {
        }

        if (qtdMaxSenhasAutorizacaoTotem > 0) {
            // Quantidade de senhas geradas via totem e comparar com o parâmetro de sistema
            int qtdeSenhaAutorizacaoUsuSerDiaHostAHost = usuDelegate.qtdeSenhaAutorizacaoUsuSerDiaHostAHost(usuCodigo, responsavel);

            if (qtdeSenhaAutorizacaoUsuSerDiaHostAHost >= qtdMaxSenhasAutorizacaoTotem) {
                throw new ZetraException("mensagem.senha.servidor.autorizacao.qtd.excedida.host.a.host", responsavel);
            }
        }

        // Quantidade máxima de senhas de autorização múltiplas que podem ser geradas para um servidor
        usuDelegate.validaQtdeSenhaAutorizacao(usuCodigo, responsavel);

        // Verifica o modo de entrega da senha de autorização: ESCOLHA[1=E-mail;2=Tela;3=E-mail/Tela]
        String modoEntrega = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, responsavel)) ? ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, responsavel).toString() : CodedValues.ALTERACAO_SENHA_AUT_SER_ENVIA_EMAIL;
        // Verifica se sempre entrega para a senha
        boolean enviaSempreSenhaHostaHost = ParamSist.paramEquals(CodedValues.TPC_SOAP_ENVIA_SENHA_AUT_SERVIDOR, CodedValues.TPC_SIM, responsavel);

        // Verifica se a senha só pode ser enviada por e-mail para o usuário
        if (!enviaSempreSenhaHostaHost && modoEntrega.equals(CodedValues.ALTERACAO_SENHA_AUT_SER_ENVIA_EMAIL)) {
            throw new ZetraException("mensagem.senha.servidor.autorizacao.somente.email", responsavel);
        }

        if (!TextHelper.isNull(usuSuporte)) {
            // A. verificar se o login/senha do usuário servidor são válidos e este está habilitado para acessar o módulo do servidor do host-a-host
            TransferObject usuarioSuporte = autentica(usuSuporte, usuSuporteSenha);

            String usuCodigoSup = (String) usuarioSuporte.getAttribute(Columns.USU_CODIGO);
            String tipoSup = (String) usuarioSuporte.getAttribute("TIPO_ENTIDADE");
            String entidadeSup = (String) usuarioSuporte.getAttribute("COD_ENTIDADE");

            // B. verificar se o login/senha do usuário são válidos e este usuário possui permissão para autorizar emissão de código único.
            String funAutEmissaoSenhaAutorizacao = CodedValues.FUN_AUT_EMISSAO_SENHA_AUTORIZACAO;
            usuarioTemPermissao(usuCodigoSup, tipoSup, entidadeSup, funAutEmissaoSenhaAutorizacao);

            // Cria usuário suporte responsavel
            CustomTransferObject usuario = usuDelegate.findTipoUsuario(usuSuporte, AcessoSistema.getAcessoUsuarioSistema());
            String usuCodigoSuporte = usuario.getAttribute(Columns.USU_CODIGO).toString();
            AcessoSistema usuarioSuporteResponsavel = new AcessoSistema(usuCodigoSuporte);
            usuarioSuporteResponsavel.setFunCodigo(funAutEmissaoSenhaAutorizacao);
            usuarioSuporteResponsavel.setUsuCodigo(usuCodigoSuporte);
            usuarioSuporteResponsavel.setUsuNome(usuario.getAttribute(Columns.USU_NOME).toString());
            usuarioSuporteResponsavel.setCanal(CanalEnum.SOAP);
            usuarioSuporteResponsavel.setTipoEntidade(AcessoSistema.ENTIDADE_SUP);
            usuarioSuporteResponsavel.setCodigoEntidade(usuario.getAttribute(Columns.USP_CSE_CODIGO).toString());
            usuarioSuporteResponsavel.setIpUsuario(responsavel.getIpUsuario());
            usuarioSuporteResponsavel.setPortaLogicaUsuario(responsavel.getPortaLogicaUsuario());

            // C. Verificar se o código de autorização ainda não foi usado. Caso uma das situações acima falhe, informar código de erro correspondente. Criar novos códigos e mensagens de erro caso seja necessário.
            if (!TextHelper.isNull(psaCodigo)) {
                TransferObject protocolo = usuDelegate.getProtocoloSenhaAutorizacao(psaCodigo, usuarioSuporteResponsavel);
                if (protocolo != null) {
                    // Gera log de protocolo de senha de autorização inválido
                    LogDelegate logDelegate = new LogDelegate(usuarioSuporteResponsavel, Log.PROTOCOLO_SENHA_AUTORIZACAO, Log.FIND, Log.LOG_INFORMACAO);
                    logDelegate.setProtocoloSenhaAutorizacao(psaCodigo);
                    logDelegate.setRegistroServidor(rseCodigo);
                    logDelegate.setUsuario(usuCodigo);
                    StatusProtocoloSenhaAutorizacaoEnum invalido = StatusProtocoloSenhaAutorizacaoEnum.INVALIDO;
                    logDelegate.setStatusProtocoloSenhaAutorizacao(invalido.getCodigo());
                    logDelegate.add(ApplicationResourcesHelper.getMessage("rotulo.protocolo.senha.autorizacao.singular", usuarioSuporteResponsavel) + ": \"" + psaCodigo + "\" " + invalido.getDescricao());
                    logDelegate.write();

                    throw new ZetraException("mensagem.protocolo.senha.servidor.autorizacao.ja.existe", usuarioSuporteResponsavel);
                }

                if (gerarLogValidacaoProtocoloSenhaAut) {
                    // Gera log de protocolo de senha de autorização válido
                    LogDelegate logDelegate = new LogDelegate(usuarioSuporteResponsavel, Log.PROTOCOLO_SENHA_AUTORIZACAO, Log.FIND, Log.LOG_INFORMACAO);
                    logDelegate.setProtocoloSenhaAutorizacao(psaCodigo);
                    logDelegate.setRegistroServidor(rseCodigo);
                    logDelegate.setUsuario(usuCodigo);
                    StatusProtocoloSenhaAutorizacaoEnum valido = StatusProtocoloSenhaAutorizacaoEnum.VALIDO;
                    logDelegate.setStatusProtocoloSenhaAutorizacao(valido.getCodigo());
                    logDelegate.add(ApplicationResourcesHelper.getMessage("rotulo.protocolo.senha.autorizacao.singular", usuarioSuporteResponsavel) + ": \"" + psaCodigo + "\" " + valido.getDescricao());
                    logDelegate.write();
                }
            }
        }

        // Caso não haja nenhum impedimento, será enviado o código de sucesso (000)
        parametros.put(MENSAGEM, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.pode.gerar.sucesso", responsavel));

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
