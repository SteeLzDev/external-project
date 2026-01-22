package com.zetra.econsig.webservice.command.saida;

import static com.zetra.econsig.webservice.CamposAPI.DADOS_CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.DAD_VALOR;
import static com.zetra.econsig.webservice.CamposAPI.TDA_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.TDA_DESCRICAO;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RespostaDadosConsignacaoCommand</p>
 * <p>Description: classe command que gera um boleto em resposta à requisição externa ao eConsig.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaDadosConsignacaoCommand extends RespostaRequisicaoExternaCommand {

    public RespostaDadosConsignacaoCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);
        List<TransferObject> dadosConsignacao = (List<TransferObject>) parametros.get(DADOS_CONSIGNACAO);
        if (dadosConsignacao != null) {
            for (TransferObject dadoConsignacao : dadosConsignacao) {
                RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
                reg.setNome(DADOS_CONSIGNACAO);

                reg.addAtributo(TDA_CODIGO, dadoConsignacao.getAttribute(Columns.TDA_CODIGO));
                reg.addAtributo(TDA_DESCRICAO, dadoConsignacao.getAttribute(Columns.TDA_DESCRICAO));
                reg.addAtributo(DAD_VALOR, dadoConsignacao.getAttribute(Columns.DAD_VALOR));

                respostas.add(reg);
            }
        }
        return respostas;
    }
}
