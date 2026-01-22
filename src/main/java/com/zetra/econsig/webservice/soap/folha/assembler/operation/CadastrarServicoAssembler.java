package com.zetra.econsig.webservice.soap.folha.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.dto.entidade.ServicoTransferObject;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.CadastrarServico;
import com.zetra.econsig.webservice.soap.folha.v1.Servico;


/**
 * <p>Title: CadastrarServicoAssembler</p>
 * <p>Description: Assembler para CadastrarServico.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class CadastrarServicoAssembler extends BaseAssembler {

    private CadastrarServicoAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(CadastrarServico cadastrarServico) {
        final ServicoTransferObject svcTO = new ServicoTransferObject();
        final Servico servico = cadastrarServico.getServico();

        svcTO.setSvcIdentificador(servico.getCodigo());
        svcTO.setSvcDescricao(servico.getDescricao());
        svcTO.setSvcObs(getValue(servico.getObservacao()));

        final String prioridade = getValue(servico.getPrioridade());
        if (!TextHelper.isNull(prioridade)) {
            svcTO.setSvcPrioridade(Integer.valueOf(prioridade));
        }

        final Short ativo = getValue(servico.getAtivo());
        if ((ativo == null) || (ativo == Short.MAX_VALUE) || (ativo <= 0)) {
            svcTO.setSvcAtivo(CodedValues.STS_INATIVO);
        } else {
            svcTO.setSvcAtivo(ativo);
        }

        final String naturezaServico = getValue(servico.getCodigoNaturezaServico());
        if (!TextHelper.isNull(naturezaServico)) {
            svcTO.setSvcNseCodigo(naturezaServico);
        } else {
            svcTO.setSvcNseCodigo(CodedValues.NSE_OUTROS);
        }

        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);
        parametros.put(SERVICO, svcTO);
        parametros.put(USUARIO, cadastrarServico.getUsuario());
        parametros.put(SENHA, cadastrarServico.getSenha());

        return parametros;
    }
}
