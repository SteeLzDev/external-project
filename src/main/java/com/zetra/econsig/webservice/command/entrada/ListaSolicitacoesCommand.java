package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CSA_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SOLICITACOES;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ListaSolicitacoesCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de lista de solicitações</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaSolicitacoesCommand extends RequisicaoExternaCommand {

    public ListaSolicitacoesCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        String csaCodigo = (String) parametros.get(CSA_CODIGO);
        String rseCodigo = (String) parametros.get(RSE_CODIGO);
        Object adeNumero = parametros.get(ADE_NUMERO);
        Object adeIdentificador = parametros.get(ADE_IDENTIFICADOR);

        AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();

        try {
            sadCodigos = new ArrayList<>();
            sadCodigos.add(CodedValues.SAD_SOLICITADO);
            List<TransferObject> solicitacoes = adeDelegate.pesquisaAutorizacao("CSA", csaCodigo, rseCodigo, TextHelper.objectToStringList(adeNumero), TextHelper.objectToStringList(adeIdentificador), sadCodigos, null, null, responsavel);
            if (solicitacoes.size() > 0) {
                parametros.put(SOLICITACOES, solicitacoes);
            } else {
                throw new ZetraException("mensagem.erro.nenhuma.solicitacao.encontrada", responsavel);
            }
        } catch (AutorizacaoControllerException ex) {
            throw ex;
        }
    }
}
