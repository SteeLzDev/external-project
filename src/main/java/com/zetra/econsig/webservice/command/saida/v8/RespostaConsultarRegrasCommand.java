package com.zetra.econsig.webservice.command.saida.v8	;

import static com.zetra.econsig.webservice.CamposAPI.REGRA;
import static com.zetra.econsig.webservice.CamposAPI.REGRAS;
import static com.zetra.econsig.webservice.CamposAPI.RCO_CAMPO_NOME;
import static com.zetra.econsig.webservice.CamposAPI.RCO_CAMPO_VALOR;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.saida.RegistroRespostaRequisicaoExterna;
import com.zetra.econsig.webservice.command.saida.RespostaRequisicaoExternaCommand;

/**
 * <p>Title: RespostaOrgaosCommand</p>
 * <p>Description: classe command que gera uma descrição de regras convênio em resposta à requisição externa ao eConsig.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaConsultarRegrasCommand extends RespostaRequisicaoExternaCommand {

	public RespostaConsultarRegrasCommand(AcessoSistema responsavel) {
		super(responsavel);
	}

	@Override
	public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
		List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

		List<TransferObject> regras = (List<TransferObject>) parametros.get(REGRAS);
		for (TransferObject regra : regras) {
			RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
			reg.setNome(REGRA);

			reg.addAtributo(RCO_CAMPO_NOME, regra.getAttribute(Columns.RCO_CAMPO_NOME));
			reg.addAtributo(RCO_CAMPO_VALOR, regra.getAttribute(Columns.RCO_CAMPO_VALOR));

			respostas.add(reg);
		}

		return respostas;
	}
}
