package com.zetra.econsig.webservice.command.saida;

import static com.zetra.econsig.webservice.CamposAPI.CONSIGNATARIA;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNATARIAS;
import static com.zetra.econsig.webservice.CamposAPI.CSA_CNPJ;
import static com.zetra.econsig.webservice.CamposAPI.CSA_EMAIL;
import static com.zetra.econsig.webservice.CamposAPI.CSA_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.CSA_NOME;
import static com.zetra.econsig.webservice.CamposAPI.CSA_NOME_ABREV;
import static com.zetra.econsig.webservice.CamposAPI.CSA_TEL;
import static com.zetra.econsig.webservice.CamposAPI.NCA_CODIGO;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RespostaConsignatariasCommand</p>
 * <p>Description: classe command que gera uma descrição de consignatária em resposta à requisição externa ao eConsig.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaConsignatariasCommand extends RespostaRequisicaoExternaCommand {

	public RespostaConsignatariasCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

	@Override
	public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
		List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

		List<TransferObject> consignatarias = (List<TransferObject>) parametros.get(CONSIGNATARIAS);
        for (TransferObject consignataria : consignatarias) {
            RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
            reg.setNome(CONSIGNATARIA);

            reg.addAtributo(CSA_IDENTIFICADOR, consignataria.getAttribute(Columns.CSA_IDENTIFICADOR));
            reg.addAtributo(CSA_NOME, consignataria.getAttribute(Columns.CSA_NOME));
            reg.addAtributo(CSA_NOME_ABREV, consignataria.getAttribute(Columns.CSA_NOME_ABREV));
            reg.addAtributo(CSA_CNPJ, consignataria.getAttribute(Columns.CSA_CNPJ));
            reg.addAtributo(CSA_TEL, consignataria.getAttribute(Columns.CSA_TEL));
            reg.addAtributo(CSA_EMAIL, consignataria.getAttribute(Columns.CSA_EMAIL));
            reg.addAtributo(NCA_CODIGO, consignataria.getAttribute(Columns.NCA_CODIGO));

            respostas.add(reg);
        }

        return respostas;
	}
}
