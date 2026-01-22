package com.zetra.econsig.webservice.command.saida;

import static com.zetra.econsig.webservice.CamposAPI.ESTABELECIMENTO;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORGAO;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDOR;
import static com.zetra.econsig.webservice.CamposAPI.SERVIDORES;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RespostaServidoresCommand</p>
 * <p>Description: classe command que gera uma lista de info de servidores em resposta à requisição externa ao eConsig.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaServidoresCommand extends RespostaRequisicaoExternaCommand {

    public RespostaServidoresCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

        // Adiciona vários registros com os servidores encontrados na pesquisa,
        // para que o usuário possa escolher um
        List<TransferObject> servidores = (List<TransferObject>) parametros.get(SERVIDORES);
        for (TransferObject servidor : servidores) {
            RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
            reg.setNome(SERVIDOR);

            reg.addAtributo( SERVIDOR, servidor.getAttribute(Columns.SER_NOME));
            reg.addAtributo( SER_CPF, servidor.getAttribute(Columns.SER_CPF));
            reg.addAtributo( RSE_MATRICULA, servidor.getAttribute(Columns.RSE_MATRICULA));
            reg.addAtributo( EST_IDENTIFICADOR, servidor.getAttribute(Columns.EST_IDENTIFICADOR));
            reg.addAtributo( ESTABELECIMENTO, servidor.getAttribute(Columns.EST_NOME));
            reg.addAtributo( ORG_IDENTIFICADOR, servidor.getAttribute(Columns.ORG_IDENTIFICADOR));
            reg.addAtributo( ORGAO, servidor.getAttribute(Columns.ORG_NOME));

            respostas.add(reg);
        }

        return respostas;
    }
}
