package com.zetra.econsig.webservice.command.saida.v3;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.saida.RegistroRespostaRequisicaoExterna;
import com.zetra.econsig.webservice.command.saida.RespostaRequisicaoExternaCommand;

/**
 * <p>Title: CadastrarTaxaJurosCommand</p>
 * <p>Description: classe command que gera resposta à requisição externa de cadastrar taxa de juros.</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaCadastrarTaxaJurosCommand extends RespostaRequisicaoExternaCommand {

    public RespostaCadastrarTaxaJurosCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

        return respostas;
    }
}
