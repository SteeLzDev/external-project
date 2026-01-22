package com.zetra.econsig.webservice.command.saida;

import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA_FERIAS;
import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA_REF;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIO;
import static com.zetra.econsig.webservice.CamposAPI.CONVENIOS;
import static com.zetra.econsig.webservice.CamposAPI.CSA_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.CSA_NOME;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_NOME;
import static com.zetra.econsig.webservice.CamposAPI.SVC_DESCRICAO;
import static com.zetra.econsig.webservice.CamposAPI.SVC_IDENTIFICADOR;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RespostaConveniosCommand</p>
 * <p>Description: classe command que gera uma descrição de convênio em resposta à requisição externa ao eConsig.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaConveniosCommand extends RespostaRequisicaoExternaCommand {

	public RespostaConveniosCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

	@Override
	public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
		List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);
		List<TransferObject> convenios = (List<TransferObject>) parametros.get(CONVENIOS);
        for (TransferObject convenio : convenios) {
            RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
            reg.setNome(CONVENIO);

            reg.addAtributo(CSA_IDENTIFICADOR, convenio.getAttribute(Columns.CSA_IDENTIFICADOR));
            reg.addAtributo(CSA_NOME, convenio.getAttribute(Columns.CSA_NOME));
            reg.addAtributo(ORG_IDENTIFICADOR, convenio.getAttribute(Columns.ORG_IDENTIFICADOR));
            reg.addAtributo(ORG_NOME, convenio.getAttribute(Columns.ORG_NOME));
            reg.addAtributo(SVC_IDENTIFICADOR, convenio.getAttribute(Columns.SVC_IDENTIFICADOR));
            reg.addAtributo(SVC_DESCRICAO, convenio.getAttribute(Columns.SVC_DESCRICAO));
            reg.addAtributo(CNV_COD_VERBA, convenio.getAttribute(Columns.CNV_COD_VERBA));
            reg.addAtributo(CNV_COD_VERBA_REF, convenio.getAttribute(Columns.CNV_COD_VERBA_REF));
            reg.addAtributo(CNV_COD_VERBA_FERIAS, convenio.getAttribute(Columns.CNV_COD_VERBA_FERIAS));
            reg.addAtributo(EST_IDENTIFICADOR, convenio.getAttribute(Columns.EST_IDENTIFICADOR));

            respostas.add(reg);
        }

        return respostas;
	}
}
