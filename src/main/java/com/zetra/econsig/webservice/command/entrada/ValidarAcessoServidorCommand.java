package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.CSA_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SER_LOGIN;
import static com.zetra.econsig.webservice.CamposAPI.SER_SENHA;
import static com.zetra.econsig.webservice.CamposAPI.TOKEN;
import static com.zetra.econsig.webservice.CamposAPI.USU_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.USU_NOME;

import java.util.Map;

import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ValidarAcessoServidorCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de validar acesso do servidor</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ValidarAcessoServidorCommand extends RequisicaoExternaCommand {

    public ValidarAcessoServidorCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
        validarFiltrosServidor(parametros);
        validaLoginSenha(parametros);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        String rseCodigo = (String) parametros.get(RSE_CODIGO);
        String serSenha = (String)parametros.get(SER_SENHA);
        String loginExterno = (String) parametros.get(SER_LOGIN);
        String csaCodigo = (String) parametros.get(CSA_CODIGO);
        String token = (String) parametros.get(TOKEN);

        validarSenhaServidor(rseCodigo, serSenha, false, loginExterno, csaCodigo, token, responsavel);
    }

    private void validarFiltrosServidor(Map<CamposAPI, Object> parametros) throws ZetraException {
        if (parametros.get(ORG_IDENTIFICADOR) == null || parametros.get(ORG_IDENTIFICADOR).toString().equals("")) {
            throw new ZetraException("mensagem.informe.ser.orgao", responsavel);
        }
        if (parametros.get(EST_IDENTIFICADOR) == null || parametros.get(EST_IDENTIFICADOR).toString().equals("")) {
            throw new ZetraException("mensagem.informe.ser.estabelecimento", responsavel);
        }
        if ((parametros.get(SER_SENHA) == null || parametros.get(SER_SENHA).toString().equals("")) && TextHelper.isNull(parametros.get(TOKEN))) {
            throw new ZetraException("mensagem.informe.ser.senha.ou.token", responsavel);
        }
        if (parametros.get(RSE_MATRICULA) == null || parametros.get(RSE_MATRICULA).toString().equals("")) {
            throw new ZetraException("mensagem.informe.matricula", responsavel);
        }
    }

    private void validaLoginSenha(Map<CamposAPI, Object> parametros) throws ZetraException {
        Object rseMatricula = parametros.get(RSE_MATRICULA);
        String login = "";

        UsuarioDelegate usuDelegate = new UsuarioDelegate();

        if (ParamSist.paramEquals(CodedValues.TPC_MATRICULA_NUMERICA, CodedValues.TPC_SIM, responsavel)) {
            login = parametros.get(EST_IDENTIFICADOR) + "-" + String.valueOf(Long.parseLong(rseMatricula.toString()));
        } else {
            login = parametros.get(EST_IDENTIFICADOR) + "-" + rseMatricula.toString();
        }
        CustomTransferObject usuario = null;
        try {
            usuario = usuDelegate.findTipoUsuario(login, null);
        } catch (UsuarioControllerException ex) {
            throw new ZetraException(ex);
        }
        if (usuario == null) {
            throw new ZetraException("mensagem.usuarioSenhaInvalidos", responsavel);
        }
        parametros.put(USU_CODIGO, usuario.getAttribute(Columns.USU_CODIGO));
        parametros.put(USU_NOME, usuario.getAttribute(Columns.USU_NOME));
        responsavel.setUsuCodigo(usuario.getAttribute(Columns.USU_CODIGO).toString());
        responsavel.setTipoEntidade("SER");
        responsavel.setCodigoEntidade((String) usuario.getAttribute(Columns.USE_SER_CODIGO));
    }
}
