package com.zetra.econsig.webservice.command.saida;

import static com.zetra.econsig.webservice.CamposAPI.SERVICO;
import static com.zetra.econsig.webservice.CamposAPI.SERVICOS;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO_CODIGO;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RespostasServicosCommand</p>
 * <p>Description: classe command que gera uma lista de info de serviços em resposta à requisição externa ao eConsig.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostasServicosCommand extends RespostaRequisicaoExternaCommand {

    public RespostasServicosCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

        // Adiciona vários registros com os servicços que possuem
        // mesmo código de verba, informado pelo usuário
        List<TransferObject> servicos = (List<TransferObject>) parametros.get(SERVICOS);

        for (TransferObject servico : servicos) {
            RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
            reg.setNome(SERVICO);

            reg.addAtributo( SERVICO, servico.getAttribute(Columns.SVC_DESCRICAO));
            reg.addAtributo( SERVICO_CODIGO, servico.getAttribute(Columns.SVC_IDENTIFICADOR));

            respostas.add(reg);
        }

        return respostas;
    }
}
