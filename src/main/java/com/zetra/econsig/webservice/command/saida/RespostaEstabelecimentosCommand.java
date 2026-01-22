package com.zetra.econsig.webservice.command.saida;

import static com.zetra.econsig.webservice.CamposAPI.ESTABELECIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.ESTABELECIMENTOS;
import static com.zetra.econsig.webservice.CamposAPI.EST_ATIVO;
import static com.zetra.econsig.webservice.CamposAPI.EST_CNPJ;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.EST_NOME;
import static com.zetra.econsig.webservice.CamposAPI.EST_NOME_ABREV;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RespostaEstabelecimentosCommand</p>
 * <p>Description: classe command que gera uma descrição de estabelecimento em resposta à requisição externa ao eConsig.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaEstabelecimentosCommand extends RespostaRequisicaoExternaCommand {

	public RespostaEstabelecimentosCommand(AcessoSistema responsavel) {
		super(responsavel);
	}

	@Override
	public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
		List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

		List<TransferObject> estabelecimentos = (List<TransferObject>) parametros.get(ESTABELECIMENTOS);
		for (TransferObject estabelecimento : estabelecimentos) {
			RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
			reg.setNome(ESTABELECIMENTO);

			reg.addAtributo(EST_IDENTIFICADOR, estabelecimento.getAttribute(Columns.EST_IDENTIFICADOR));
			reg.addAtributo(EST_NOME, estabelecimento.getAttribute(Columns.EST_NOME));
			reg.addAtributo(EST_NOME_ABREV, estabelecimento.getAttribute(Columns.EST_NOME_ABREV));
			reg.addAtributo(EST_CNPJ, estabelecimento.getAttribute(Columns.EST_CNPJ));
			reg.addAtributo(EST_ATIVO, estabelecimento.getAttribute(Columns.EST_ATIVO));

			respostas.add(reg);
		}

		return respostas;
	}
}
