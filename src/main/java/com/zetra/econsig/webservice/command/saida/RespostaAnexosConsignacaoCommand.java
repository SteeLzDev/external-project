package com.zetra.econsig.webservice.command.saida;

import static com.zetra.econsig.webservice.CamposAPI.ANEXOS_CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.ANEXO_CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.ARQUIVO;
import static com.zetra.econsig.webservice.CamposAPI.DESCRICAO_TIPO_ARQUIVO;
import static com.zetra.econsig.webservice.CamposAPI.NOME_ARQUIVO;
import static com.zetra.econsig.webservice.CamposAPI.TIPO_ARQUIVO;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RespostaAnexosConsignacaoCommand</p>
 * <p>Description: classe command que gera info de múltipos anexos de consignação em resposta à requisição externa ao eConsig.</p>
 * <p>Copyright: Copyright (c) 2002-2022</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaAnexosConsignacaoCommand extends RespostaRequisicaoExternaCommand {

    public RespostaAnexosConsignacaoCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

        List<TransferObject> anexos = (List<TransferObject>) parametros.get(ANEXOS_CONSIGNACAO);
        if (anexos != null && !anexos.isEmpty()) {
            for (TransferObject anexo : anexos) {
                RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
                reg.setNome(ANEXO_CONSIGNACAO);

                reg.addAtributo(NOME_ARQUIVO, anexo.getAttribute(Columns.AAD_NOME));
                reg.addAtributo(TIPO_ARQUIVO, anexo.getAttribute(Columns.TAR_CODIGO));
                reg.addAtributo(DESCRICAO_TIPO_ARQUIVO, anexo.getAttribute(Columns.TAR_DESCRICAO));
                reg.addAtributo(ARQUIVO, anexo.getAttribute("ARQUIVO"));

                respostas.add(reg);
            }
        }

        return respostas;
    }
}
