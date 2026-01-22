package com.zetra.econsig.webservice.command.saida.v8;

import static com.zetra.econsig.webservice.CamposAPI.SERVIDORES;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.saida.RegistroRespostaRequisicaoExterna;

/**
 * <p>Title: RespostaConsultarMargemCommand</p>
 * <p>Description: classe command que gera info margem em resposta à requisição externa ao eConsig versão 7.0.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaConsultarMargemCommand extends com.zetra.econsig.webservice.command.saida.v7.RespostaServidoresCommand {

    public RespostaConsultarMargemCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = null;

        List<TransferObject> servidores = (parametros.get(SERVIDORES) != null) ? (List<TransferObject>) parametros.get(SERVIDORES) : null;

        if (servidores != null && !servidores.isEmpty()) {
            RespostaServidoresCommand respostaServidoresCommand = new RespostaServidoresCommand(responsavel);
            respostas = respostaServidoresCommand.geraRegistrosResposta(parametros);
        } else {
            RespostaServidorCommand respostaServidorCommand = new RespostaServidorCommand(responsavel);
            respostas = respostaServidorCommand.geraRegistrosResposta(parametros);
        }

        return respostas;
    }

}
