package com.zetra.econsig.webservice.command.saida;

import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.ORGAOS;
import static com.zetra.econsig.webservice.CamposAPI.ORG_CNPJ;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_NOME;
import static com.zetra.econsig.webservice.CamposAPI.ORG_NOME_ABREV;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RespostaOrgaosCommand</p>
 * <p>Description: classe command que gera uma descrição de órgão em resposta à requisição externa ao eConsig.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaOrgaosCommand extends RespostaRequisicaoExternaCommand {

	public RespostaOrgaosCommand(AcessoSistema responsavel) {
		super(responsavel);
	}

	@Override
	public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
		List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

		List<TransferObject> orgaos = (List<TransferObject>) parametros.get(ORGAOS);
		for (TransferObject orgao : orgaos) {
			RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
			reg.setNome(ORGAO);

			reg.addAtributo(ORG_IDENTIFICADOR, orgao.getAttribute(Columns.ORG_IDENTIFICADOR));
			reg.addAtributo(ORG_NOME, orgao.getAttribute(Columns.ORG_NOME));
			reg.addAtributo(ORG_NOME_ABREV, orgao.getAttribute(Columns.ORG_NOME_ABREV));
			reg.addAtributo(ORG_CNPJ, orgao.getAttribute(Columns.ORG_CNPJ));
			reg.addAtributo(EST_IDENTIFICADOR, orgao.getAttribute(Columns.EST_IDENTIFICADOR));

			respostas.add(reg);
		}

		return respostas;
	}
}
