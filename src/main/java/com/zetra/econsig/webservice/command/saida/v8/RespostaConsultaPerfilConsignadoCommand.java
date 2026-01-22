package com.zetra.econsig.webservice.command.saida.v8;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.command.saida.RegistroRespostaRequisicaoExterna;
import com.zetra.econsig.webservice.command.saida.RespostaRequisicaoExternaCommand;

import java.util.List;
import java.util.Map;

public class RespostaConsultaPerfilConsignadoCommand extends RespostaRequisicaoExternaCommand {
    public RespostaConsultaPerfilConsignadoCommand(AcessoSistema responsavel) { super(responsavel); }


    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);
        TransferObject servidor = (TransferObject) parametros.get(CamposAPI.PERFIL_CONSIGNADO);
        RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
        reg.setNome(CamposAPI.PERFIL_CONSIGNADO);
        reg.addAtributo(CamposAPI.SERVIDOR, servidor.getAttribute(Columns.SER_NOME));
        reg.addAtributo(CamposAPI.SER_CPF, servidor.getAttribute(Columns.SER_CPF));
        reg.addAtributo(CamposAPI.RSE_MATRICULA, servidor.getAttribute(Columns.RSE_MATRICULA));
        reg.addAtributo(CamposAPI.ORG_CODIGO, servidor.getAttribute(Columns.ORG_IDENTIFICADOR));
        reg.addAtributo(CamposAPI.ORG_NOME, servidor.getAttribute(Columns.ORG_NOME));
        reg.addAtributo(CamposAPI.EST_NOME, servidor.getAttribute(Columns.EST_NOME));
        reg.addAtributo(CamposAPI.EST_CODIGO, servidor.getAttribute(Columns.EST_IDENTIFICADOR));
        reg.addAtributo(CamposAPI.PERFIL_CONSIGNADO, servidor.getAttribute(Columns.RSE_PONTUACAO));
        respostas.add(reg);

        return respostas;
    }
}
