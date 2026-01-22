package com.zetra.econsig.webservice.command.saida.v3;

import static com.zetra.econsig.webservice.CamposAPI.PARCELA;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.saida.RegistroRespostaRequisicaoExterna;
import com.zetra.econsig.webservice.command.saida.RespostaRequisicaoExternaCommand;

/**
 * <p>Title: RespostaConsultarParcelaCommand</p>
 * <p>Description: classe command que gera info margem em resposta à requisição externa de consultar parcela.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaConsultarParcelaCommand extends RespostaRequisicaoExternaCommand {

    public RespostaConsultarParcelaCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

        RegistroRespostaRequisicaoExterna registro = new RegistroRespostaRequisicaoExterna();
        registro.setNome(PARCELA);

        registro.setAtributos(parametros);

        respostas.add(registro);

        return respostas;
    }
}
