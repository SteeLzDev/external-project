package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.GRUPO_PERGUNTA;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.NUMERO_PERGUNTA;
import static com.zetra.econsig.webservice.CamposAPI.TEXTO_PERGUNTA;

import java.util.Map;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RecuperarPerguntaDadosCadastraisCommand</p>
 * <p>Description: Classe command que trata requisição externa ao eConsig para recuperar pergunta de dados cadastrais.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RecuperarPerguntaDadosCadastraisCommand extends RequisicaoExternaCommand {

    public RecuperarPerguntaDadosCadastraisCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        Object objGrupo = parametros.get(GRUPO_PERGUNTA);

        if (TextHelper.isNull(objGrupo)) {
            throw new ZetraException("mensagem.grupo.pergunta.dados.cadastrais.obrigatorio", responsavel);
        }

        ServidorDelegate serDelegate = new ServidorDelegate();
        Short grupoPergunta = Short.valueOf(objGrupo.toString());
        TransferObject pergunta = serDelegate.sorteiaPerguntaDadosCadastrais(grupoPergunta, responsavel);

        if (pergunta == null) {
            throw new ZetraException("mensagem.pergunta.dados.cadastrais.nao.existe", responsavel);
        }

        // Caso não haja nenhum impedimento, será enviado o código de sucesso (000)
        parametros.put(MENSAGEM, ApplicationResourcesHelper.getMessage("mensagem.pergunta.dados.cadastrais.recuperada.sucesso", responsavel));
        parametros.put(NUMERO_PERGUNTA, pergunta.getAttribute(Columns.PDC_NUMERO));
        parametros.put(TEXTO_PERGUNTA, pergunta.getAttribute(Columns.PDC_TEXTO));
    }

}
