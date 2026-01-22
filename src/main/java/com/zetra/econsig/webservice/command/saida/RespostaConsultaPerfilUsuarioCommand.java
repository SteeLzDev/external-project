package com.zetra.econsig.webservice.command.saida;

import static com.zetra.econsig.webservice.CamposAPI.CONSULTA_PERFIL_USUARIO;
import static com.zetra.econsig.webservice.CamposAPI.PERFIL_USUARIO;
import static com.zetra.econsig.webservice.CamposAPI.PER_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.PER_DATA_EXPIRACAO;
import static com.zetra.econsig.webservice.CamposAPI.PER_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.STATUS;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RespostaConsultaPerfilUsuarioCommand</p>
 * <p>Description: classe command que gera uma lista de perfil de usuários em resposta à requisição externa ao eConsig.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaConsultaPerfilUsuarioCommand extends RespostaRequisicaoExternaCommand {

	public RespostaConsultaPerfilUsuarioCommand(AcessoSistema responsavel) {
		super(responsavel);
	}

	@Override
	public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
		List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

		List<TransferObject> lstPerfil = (List<TransferObject>) parametros.get(CONSULTA_PERFIL_USUARIO);
		for (TransferObject perfil : lstPerfil) {

			RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
			reg.setNome(PERFIL_USUARIO);

			reg.addAtributo(PER_CODIGO, perfil.getAttribute(Columns.PER_CODIGO));
            reg.addAtributo(PER_DESCRICAO, perfil.getAttribute(Columns.PER_DESCRICAO));
            reg.addAtributo(PER_DATA_EXPIRACAO, perfil.getAttribute(Columns.PER_DATA_EXPIRACAO));
            reg.addAtributo(STATUS, perfil.getAttribute("STATUS"));

			respostas.add(reg);
		}

		return respostas;
	}
}
