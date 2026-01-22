package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.RSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_EMAIL;

import java.util.Map;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: VerificarEmailServidorCommand</p>
 * <p>Description: Classe command que trata requisição externa ao eConsig para verificar se o e-mail
 * informado é válido para cadastro pelo servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2016</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class VerificarEmailServidorCommand extends RequisicaoExternaCommand {

    public VerificarEmailServidorCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        String rseCodigo = (String) parametros.get(RSE_CODIGO);
        String serCpf = (String) parametros.get(SER_CPF);
        String email = (String) parametros.get(SER_EMAIL);

        if(TextHelper.isNull(rseCodigo) && !TextHelper.isNull(parametros.get(RSE_CODIGO))) {
            rseCodigo = (String) parametros.get(RSE_CODIGO);
        }

        if (TextHelper.isNull(rseCodigo)) {
            throw new ZetraException("mensagem.nenhumServidorEncontrado", responsavel);
        }

        if (!TextHelper.isEmailValid(email)) {
            throw new ZetraException("mensagem.erro.email.invalido", responsavel);
        }

        // Verifica se não existe outro servidor com mesmo email
        ServidorDelegate serDelegate = new ServidorDelegate();
        boolean existeEmailCadastrado = serDelegate.existeEmailCadastrado(email, serCpf, responsavel);
        if (existeEmailCadastrado) {
            throw new ServidorControllerException("mensagem.erro.email.informado.em.uso.outro.cpf", responsavel);
        }
    }
}
