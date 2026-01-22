package com.zetra.econsig.webservice.command.saida;

import static com.zetra.econsig.webservice.CamposAPI.PARAMETRO_SET;

import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: RespostaConsultarParametrosCommand</p>
 * <p>Description: classe command que gera conjunto de parâmetros de resposta.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RespostaConsultarParametrosCommand extends RespostaRequisicaoExternaCommand {

    public RespostaConsultarParametrosCommand(AcessoSistema responsavel) {
        super(responsavel);
    }

    @Override
    public List<RegistroRespostaRequisicaoExterna> geraRegistrosResposta(Map<CamposAPI, Object> parametros) throws ZetraException {
        List<RegistroRespostaRequisicaoExterna> respostas = super.geraRegistrosResposta(parametros);

        CustomTransferObject paramSet = (CustomTransferObject) parametros.get(PARAMETRO_SET);

        RegistroRespostaRequisicaoExterna reg = new RegistroRespostaRequisicaoExterna();
        reg.setNome(PARAMETRO_SET);
        reg.addAtributo(PARAMETRO_SET, paramSet);
        /*
        reg.addAtributo(ConsultarParametrosCommand.SVC_DESCRICAO, paramSet.getAttribute(ConsultarParametrosCommand.SVC_DESCRICAO));
        reg.addAtributo(ConsultarParametrosCommand.TAM_MIN_MATR_SRV, paramSet.getAttribute(ConsultarParametrosCommand.TAM_MIN_MATR_SRV));
        reg.addAtributo(ConsultarParametrosCommand.TAMANHO_MATRICULA_MAX, paramSet.getAttribute(ConsultarParametrosCommand.TAMANHO_MATRICULA_MAX));
        reg.addAtributo(ConsultarParametrosCommand.REQUER_MATRICULA_E_CPF, paramSet.getAttribute(ConsultarParametrosCommand.REQUER_MATRICULA_E_CPF));
        reg.addAtributo(ConsultarParametrosCommand.VALIDA_CPF_PESQUISA_SERVIDOR, paramSet.getAttribute(ConsultarParametrosCommand.VALIDA_CPF_PESQUISA_SERVIDOR));
        reg.addAtributo(ConsultarParametrosCommand.REDUZ_VLR_ADE_MARGEM_NEG, paramSet.getAttribute(ConsultarParametrosCommand.REDUZ_VLR_ADE_MARGEM_NEG));
        reg.addAtributo(ConsultarParametrosCommand.EXIGE_SENHA_SERVIDOR_CONS_MARGEM, paramSet.getAttribute(ConsultarParametrosCommand.EXIGE_SENHA_SERVIDOR_CONS_MARGEM));
        reg.addAtributo(ConsultarParametrosCommand.VALIDAR_INF_BANCARIA_NA_RESERVA, paramSet.getAttribute(ConsultarParametrosCommand.VALIDAR_INF_BANCARIA_NA_RESERVA));
        reg.addAtributo(ConsultarParametrosCommand.INFO_BANCARIA_OBRIGATORIA, paramSet.getAttribute(ConsultarParametrosCommand.INFO_BANCARIA_OBRIGATORIA));
        reg.addAtributo(ConsultarParametrosCommand.EXIGE_CADASTRO_VALOR_TAC, paramSet.getAttribute(ConsultarParametrosCommand.EXIGE_CADASTRO_VALOR_TAC));
        reg.addAtributo(ConsultarParametrosCommand.QTD_MAX_PARCELAS, paramSet.getAttribute(ConsultarParametrosCommand.QTD_MAX_PARCELAS));
        reg.addAtributo(ConsultarParametrosCommand.EXIGE_CAD_VLR_MENSALIDADE_VINC, paramSet.getAttribute(ConsultarParametrosCommand.EXIGE_CAD_VLR_MENSALIDADE_VINC));
        reg.addAtributo(ConsultarParametrosCommand.EXIGE_CAD_VLR_LIQUIDO_LIBERADO, paramSet.getAttribute(ConsultarParametrosCommand.EXIGE_CAD_VLR_LIQUIDO_LIBERADO));
        reg.addAtributo(ConsultarParametrosCommand.EXIGE_CAD_VALOR_IOF, paramSet.getAttribute(ConsultarParametrosCommand.EXIGE_CAD_VALOR_IOF));
        reg.addAtributo(ConsultarParametrosCommand.VALIDA_DATA_NASCIMENTO_NA_RESERVA, paramSet.getAttribute(ConsultarParametrosCommand.VALIDA_DATA_NASCIMENTO_NA_RESERVA));
        reg.addAtributo(ConsultarParametrosCommand.SENHA_SERVIDOR_OBRIGATORIA_PARA_CSA, paramSet.getAttribute(ConsultarParametrosCommand.SENHA_SERVIDOR_OBRIGATORIA_PARA_CSA));
        reg.addAtributo(ConsultarParametrosCommand.EXIGE_SENHA_ALTERACAO_CONTRATOS, paramSet.getAttribute(ConsultarParametrosCommand.EXIGE_SENHA_ALTERACAO_CONTRATOS));
        reg.addAtributo(ConsultarParametrosCommand.PERMITE_ALTERACAO_CONTRATOS, paramSet.getAttribute(ConsultarParametrosCommand.PERMITE_ALTERACAO_CONTRATOS));
        reg.addAtributo(ConsultarParametrosCommand.PERMITE_RENEGOCIACAO, paramSet.getAttribute(ConsultarParametrosCommand.PERMITE_RENEGOCIACAO));
        reg.addAtributo(ConsultarParametrosCommand.MINIMO_PRD_PAGAS_PARA_RENEGOCIACAO, paramSet.getAttribute(ConsultarParametrosCommand.MINIMO_PRD_PAGAS_PARA_RENEGOCIACAO));
        reg.addAtributo(ConsultarParametrosCommand.VISUALIZA_MARGEM, paramSet.getAttribute(ConsultarParametrosCommand.VISUALIZA_MARGEM));
        reg.addAtributo(ConsultarParametrosCommand.VISUALIZA_MARGEM_NEGATIVA, paramSet.getAttribute(ConsultarParametrosCommand.VISUALIZA_MARGEM_NEGATIVA));
        reg.addAtributo(ConsultarParametrosCommand.DIA_DE_CORTE, paramSet.getAttribute(ConsultarParametrosCommand.DIA_DE_CORTE));
        reg.addAtributo(ConsultarParametrosCommand.PERIODO_ATUAL, paramSet.getAttribute(ConsultarParametrosCommand.PERIODO_ATUAL));
        reg.addAtributo(ConsultarParametrosCommand.PERMITE_COMPRAR_CONTRATOS, paramSet.getAttribute(ConsultarParametrosCommand.PERMITE_COMPRAR_CONTRATOS));
        reg.addAtributo(ConsultarParametrosCommand.DIAS_INFO_SALDO_DEVEDOR, paramSet.getAttribute(ConsultarParametrosCommand.DIAS_INFO_SALDO_DEVEDOR));
        reg.addAtributo(ConsultarParametrosCommand.DIAS_APRV_SALDO_DEVEDOR, paramSet.getAttribute(ConsultarParametrosCommand.DIAS_APRV_SALDO_DEVEDOR));
        reg.addAtributo(ConsultarParametrosCommand.DIAS_INFO_PG_SALDO_DEVEDOR, paramSet.getAttribute(ConsultarParametrosCommand.DIAS_INFO_PG_SALDO_DEVEDOR));
        reg.addAtributo(ConsultarParametrosCommand.DIAS_PARA_LIQUIDAR_CONTRATO, paramSet.getAttribute(ConsultarParametrosCommand.DIAS_PARA_LIQUIDAR_CONTRATO));
        */
        respostas.add(reg);
        return respostas;
    }

}
