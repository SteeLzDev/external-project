package com.zetra.econsig.webservice.command.saida;

import static com.zetra.econsig.webservice.CamposAPI.MOVIMENTO_FINANCEIRO;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RespostaMovimentoFinanceiroCommand</p>
 * <p>Description: classe command que gera uma lista de registros de movimento financeiro em resposta à requisição externa ao eConsig.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaMovimentoFinanceiroCommand extends RespostaRequisicaoExternaCommand {

    public RespostaMovimentoFinanceiroCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

        List<TransferObject> movimentos = (List<TransferObject>) parametros.get(MOVIMENTO_FINANCEIRO);
        if (movimentos != null && !movimentos.isEmpty()) {
            for (TransferObject movimento : movimentos) {
                RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
                reg.setNome(MOVIMENTO_FINANCEIRO);
                reg.addAtributo(MOVIMENTO_FINANCEIRO, movimento);
                respostas.add(reg);
            }
        }

        return respostas;
    }
}
