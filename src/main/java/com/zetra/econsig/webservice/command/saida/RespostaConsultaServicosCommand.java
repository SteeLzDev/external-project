package com.zetra.econsig.webservice.command.saida;

import static com.zetra.econsig.webservice.CamposAPI.CONSULTA_SERVICO;
import static com.zetra.econsig.webservice.CamposAPI.CONSULTA_SERVICOS;
import static com.zetra.econsig.webservice.CamposAPI.NSE_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.NSE_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_ATIVO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.SVC_OBS;
import static com.zetra.econsig.webservice.CamposAPI.SVC_PRIORIDADE;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RespostaConsultaServicosCommand</p>
 * <p>Description: classe command que gera uma lista de serviços em resposta à requisição externa ao eConsig.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaConsultaServicosCommand extends RespostaRequisicaoExternaCommand {

	public RespostaConsultaServicosCommand(AcessoSistema responsavel) {
		super(responsavel);
	}

	@Override
	public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
		List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

		List<TransferObject> svcList = (List<TransferObject>) parametros.get(CONSULTA_SERVICOS);
		for (TransferObject svc : svcList) {

			RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
			reg.setNome(CONSULTA_SERVICO);

			reg.addAtributo(SVC_IDENTIFICADOR, svc.getAttribute(Columns.SVC_IDENTIFICADOR));
			reg.addAtributo(SVC_DESCRICAO, svc.getAttribute(Columns.SVC_DESCRICAO));
			reg.addAtributo(NSE_CODIGO, svc.getAttribute(Columns.NSE_CODIGO));
			reg.addAtributo(NSE_DESCRICAO, svc.getAttribute(Columns.NSE_DESCRICAO));
			reg.addAtributo(SVC_ATIVO, svc.getAttribute(Columns.SVC_ATIVO));
            reg.addAtributo(SVC_OBS, svc.getAttribute(Columns.SVC_OBS));
            reg.addAtributo(SVC_PRIORIDADE, svc.getAttribute(Columns.SVC_PRIORIDADE));

			respostas.add(reg);
		}

		return respostas;
	}
}
