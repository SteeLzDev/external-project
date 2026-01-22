package com.zetra.econsig.webservice.command.saida;

import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.CSA_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.CSA_NOME;
import static com.zetra.econsig.webservice.CamposAPI.INFO_COMPRAS;
import static com.zetra.econsig.webservice.CamposAPI.RAD_DATA;
import static com.zetra.econsig.webservice.CamposAPI.RAD_DATA_APR_SALDO;
import static com.zetra.econsig.webservice.CamposAPI.RAD_DATA_INF_SALDO;
import static com.zetra.econsig.webservice.CamposAPI.RAD_DATA_PGT_SALDO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SDV_VALOR;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.SER_NOME;
import static com.zetra.econsig.webservice.CamposAPI.STC_DESCRICAO;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RespostaInfoCompraCommand</p>
 * <p>Description: classe command que gera informações da compra de contrato em resposta à requisição externa ao eConsig.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaInfoCompraCommand extends RespostaRequisicaoExternaCommand {

	public RespostaInfoCompraCommand(AcessoSistema responsavel) {
		super(responsavel);
	}

	@Override
	public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
		List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

		List<TransferObject> contratos = (List<TransferObject>) parametros.get(INFO_COMPRAS);
		for (TransferObject contrato : contratos) {
			RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
			reg.setNome(INFO_COMPRAS);

			reg.addAtributo(ADE_NUMERO, contrato.getAttribute(Columns.ADE_NUMERO));
			reg.addAtributo(CSA_NOME, contrato.getAttribute(Columns.CSA_NOME));
			reg.addAtributo(CSA_IDENTIFICADOR, contrato.getAttribute(Columns.CSA_IDENTIFICADOR));
			reg.addAtributo(SER_NOME, contrato.getAttribute(Columns.SER_NOME));
			reg.addAtributo(SER_CPF, contrato.getAttribute(Columns.SER_CPF));
			reg.addAtributo(RSE_MATRICULA, contrato.getAttribute(Columns.RSE_MATRICULA));
			reg.addAtributo(RAD_DATA, contrato.getAttribute(Columns.RAD_DATA));
			reg.addAtributo(RAD_DATA_INF_SALDO, contrato.getAttribute(Columns.RAD_DATA_INF_SALDO));
			reg.addAtributo(SDV_VALOR, contrato.getAttribute(Columns.SDV_VALOR));
			reg.addAtributo(RAD_DATA_APR_SALDO, contrato.getAttribute(Columns.RAD_DATA_APR_SALDO));
			reg.addAtributo(RAD_DATA_PGT_SALDO, contrato.getAttribute(Columns.RAD_DATA_PGT_SALDO));
			reg.addAtributo(STC_DESCRICAO, contrato.getAttribute(Columns.STC_DESCRICAO));

			respostas.add(reg);
		}

		return respostas;
	}
}
