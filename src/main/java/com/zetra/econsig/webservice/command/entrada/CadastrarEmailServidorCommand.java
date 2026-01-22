package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.RSE_AGENCIA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_BANCO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CONTA;
import static com.zetra.econsig.webservice.CamposAPI.EXIGE_GRUPO_PERGUNTAS;
import static com.zetra.econsig.webservice.CamposAPI.PROTOCOLO_CADASTRO_EMAIL;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SENHA_USUARIO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_EMAIL;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.Map;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: CadastrarEmailServidorCommand</p>
 * <p>Description: Classe command que trata requisição externa ao eConsig para cadastrar email de servidor/usuario de servidor.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CadastrarEmailServidorCommand extends VerificarEmailServidorCommand {

    public CadastrarEmailServidorCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        // valida email
        super.executaOperacao(parametros);

        // servidor e usuário
        String serCodigo = responsavel.getSerCodigo();
        String rseCodigo = (String) parametros.get(RSE_CODIGO);
        String email = (String) parametros.get(SER_EMAIL);
        // auxiliar totem
        String usuSuporte = (String) parametros.get(USUARIO);
        String usuSuporteSenha = (String) parametros.get(SENHA_USUARIO);
        String pceCodigo = (String) parametros.get(PROTOCOLO_CADASTRO_EMAIL);
        String protocoloCodigo = null;
        if (!TextHelper.isNull(pceCodigo)) {
            protocoloCodigo = pceCodigo.toString() + "_EMAIL";
        }
        // dados bancários
        String bancoNumero = (String) parametros.get(RSE_BANCO);
        String bancoAgencia = (String) parametros.get(RSE_AGENCIA);
        String bancoConta = (String) parametros.get(RSE_CONTA);
        String exigeGrupoPerguntas = (String) parametros.get(EXIGE_GRUPO_PERGUNTAS);

        boolean etapaValidacao = false;
        boolean exigeDadosBancarios = ParamSist.paramEquals(CodedValues.TPC_INF_BANCARIA_OBRIGATORIA_CAD_EMAIL_TOTEM, CodedValues.TPC_SIM, responsavel);

        if (exigeDadosBancarios && TextHelper.isNull(bancoNumero) && TextHelper.isNull(bancoAgencia) && TextHelper.isNull(bancoConta) || !TextHelper.isNull(exigeGrupoPerguntas) && exigeGrupoPerguntas.equalsIgnoreCase(Boolean.TRUE.toString())) {
            etapaValidacao = true;
        }

        UsuarioDelegate usuDelegate = new UsuarioDelegate();
        ServidorDelegate serDelegate = new ServidorDelegate();

        if(TextHelper.isNull(rseCodigo) && !TextHelper.isNull(parametros.get(RSE_CODIGO))) {
            rseCodigo = (String) parametros.get(RSE_CODIGO);
        }

        if(TextHelper.isNull(serCodigo) && !TextHelper.isNull(parametros.get(SER_CODIGO))) {
            serCodigo = (String) parametros.get(SER_CODIGO);
        }

        if (TextHelper.isNull(rseCodigo) || TextHelper.isNull(serCodigo)) {
            throw new ZetraException("mensagem.nenhumServidorEncontrado", responsavel);
        }
        if (TextHelper.isNull(usuSuporte) || TextHelper.isNull(usuSuporteSenha)) {
            throw new ZetraException("mensagem.informe.usuario.senha", responsavel);
        } else {
            autentica(usuSuporte, usuSuporteSenha);
        }

        // usuário suporte responsavel
        CustomTransferObject usuario = usuDelegate.findTipoUsuario(usuSuporte, AcessoSistema.getAcessoUsuarioSistema());
        String usuCodigoSuporte = usuario.getAttribute(Columns.USU_CODIGO).toString();
        AcessoSistema usuarioSuporteResponsavel = new AcessoSistema(usuCodigoSuporte);

        String funAutCadastroEmailServidor = CodedValues.FUN_AUT_CADASTRO_EMAIL_SERVIDOR;
        usuarioSuporteResponsavel.setFunCodigo(funAutCadastroEmailServidor);
        usuarioSuporteResponsavel.setUsuCodigo(usuCodigoSuporte);
        usuarioSuporteResponsavel.setUsuNome(usuario.getAttribute(Columns.USU_NOME).toString());
        usuarioSuporteResponsavel.setCanal(CanalEnum.SOAP);
        usuarioSuporteResponsavel.setTipoEntidade(AcessoSistema.ENTIDADE_SUP);
        usuarioSuporteResponsavel.setCodigoEntidade(usuario.getAttribute(Columns.USP_CSE_CODIGO).toString());
        usuarioSuporteResponsavel.setIpUsuario(responsavel.getIpUsuario());
        usuarioSuporteResponsavel.setPortaLogicaUsuario(responsavel.getPortaLogicaUsuario());
        // verifica se o usuário de suporte tem permissão para autorizar o cadastro de email do servidor
        usuarioTemPermissao(usuarioSuporteResponsavel.getUsuCodigo(), AcessoSistema.ENTIDADE_SUP, usuarioSuporteResponsavel.getCodigoEntidade(), funAutCadastroEmailServidor);

        if (TextHelper.isNull(protocoloCodigo)) {
            throw new ZetraException("mensagem.protocolo.senha.servidor.autorizacao.obrigatorio", responsavel);
        } else {
            // valida protocolo de cadastro de email
            TransferObject protocolo = usuDelegate.getProtocoloSenhaAutorizacao(protocoloCodigo, usuarioSuporteResponsavel);
            if (protocolo != null) {
                throw new ZetraException("mensagem.protocolo.senha.servidor.autorizacao.ja.existe", usuarioSuporteResponsavel);
            }
        }

        if (!etapaValidacao) {
            if (exigeDadosBancarios) {
                // verifica se os dados bancários informados conferem com os dados do servidor
                RegistroServidorTO rseTO = null;
                rseTO = serDelegate.findRegistroServidor(rseCodigo, responsavel);
                validarDadosBancariosServidor(true, true, bancoNumero, bancoAgencia, bancoConta, rseTO);
            }
            // atualiza email do servidor/usuário
            serDelegate.cadastrarEmailServidor(rseCodigo, email, protocoloCodigo, responsavel, usuarioSuporteResponsavel);
        }
    }
}
