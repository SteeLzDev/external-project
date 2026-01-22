package com.zetra.econsig.webservice.command.saida.v3;

import static com.zetra.econsig.webservice.CamposAPI.VALOR_MARGEM_LIMITE;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.saida.RegistroRespostaRequisicaoExterna;

/**
 * <p>Title: RespostaConsultarMargemCommand</p>
 * <p>Description: classe command que gera info margem em resposta à requisição externa ao eConsig.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaConsultarMargemCommand extends com.zetra.econsig.webservice.command.saida.RespostaConsultarMargemCommand {

    public RespostaConsultarMargemCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

        RegistroRespostaRequisicaoExterna registro = respostas.get(0);
        registro.addAtributo(VALOR_MARGEM_LIMITE, parametros.get(VALOR_MARGEM_LIMITE));

        return respostas;
    }

}
