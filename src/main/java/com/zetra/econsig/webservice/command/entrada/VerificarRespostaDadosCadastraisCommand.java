package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.GRUPO_PERGUNTA;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.PERGUNTA;
import static com.zetra.econsig.webservice.CamposAPI.RESPOSTA_PERGUNTA;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CODIGO;

import java.util.Map;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: VerificarRespostaDadosCadastraisCommand</p>
 * <p>Description: Classe command que trata requisição externa ao eConsig para verificar resposta para pergunta sobre dados cadastrais.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class VerificarRespostaDadosCadastraisCommand extends RequisicaoExternaCommand {

    public VerificarRespostaDadosCadastraisCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        String rseCodigo = (String) parametros.get(RSE_CODIGO);
        Object grupoPergunta = parametros.get(GRUPO_PERGUNTA);
        Object pergunta = parametros.get(PERGUNTA);
        String respostaPergunta = (String) parametros.get(RESPOSTA_PERGUNTA);

        if (TextHelper.isNull(grupoPergunta)) {
            throw new ServidorControllerException("mensagem.grupo.pergunta.dados.cadastrais.obrigatorio", responsavel);
        }

        if (TextHelper.isNull(pergunta)) {
            throw new ServidorControllerException("mensagem.pergunta.dados.cadastrais.obrigatorio", responsavel);
        }

        if (TextHelper.isNull(respostaPergunta)) {
            throw new ServidorControllerException("mensagem.resposta.pergunta.dados.cadastrais.obrigatorio", responsavel);
        }

        if(TextHelper.isNull(rseCodigo)) {
            rseCodigo = (String) parametros.get(RSE_CODIGO);
        }

        Short pdcGrupo = Short.valueOf(grupoPergunta.toString());
        Short pdcNumero = Short.valueOf(pergunta.toString());

        ServidorDelegate serDelegate = new ServidorDelegate();

        boolean respostaValida = serDelegate.validaPerguntaDadosCadastrais(rseCodigo, pdcGrupo, pdcNumero, respostaPergunta, responsavel);

        if (!respostaValida) {
            throw new ZetraException("mensagem.resposta.pergunta.dados.cadastrais.incorreta", responsavel);
        }

        // Caso não haja nenhum impedimento, será enviado o código de sucesso (000)
        parametros.put(MENSAGEM, ApplicationResourcesHelper.getMessage("mensagem.resposta.pergunta.dados.cadastrais.sucesso", responsavel));

    }

}
