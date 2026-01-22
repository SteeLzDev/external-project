package com.zetra.econsig.webservice.soap.folha.assembler.operation;

import static com.zetra.econsig.webservice.CamposAPI.CNV_COD_VERBA;
import static com.zetra.econsig.webservice.CamposAPI.CSA_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.EST_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ORG_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.PERIODO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SENHA;
import static com.zetra.econsig.webservice.CamposAPI.SERVICO_CODIGO;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;
import static com.zetra.econsig.webservice.CamposAPI.USUARIO;

import java.util.EnumMap;
import java.util.Map;

import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.assembler.BaseAssembler;
import com.zetra.econsig.webservice.soap.folha.v1.ConsultarMovimentoFinanceiro;

/**
 * <p>Title: ConsultarMovimentoFinanceiroAssembler</p>
 * <p>Description: Assembler para ConsultarMovimentoFinanceiro.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@SuppressWarnings("java:S1192")
public class ConsultarMovimentoFinanceiroAssembler extends BaseAssembler {

    private ConsultarMovimentoFinanceiroAssembler() {
    }

    public static Map<CamposAPI, Object> toMap(ConsultarMovimentoFinanceiro consultarMovimentoFinanceiro) {
        final Map<CamposAPI, Object> parametros = new EnumMap<>(CamposAPI.class);
        parametros.put(USUARIO, consultarMovimentoFinanceiro.getUsuario());
        parametros.put(SENHA, consultarMovimentoFinanceiro.getSenha());
        parametros.put(PERIODO, consultarMovimentoFinanceiro.getPeriodo());
        parametros.put(RSE_MATRICULA, getValue(consultarMovimentoFinanceiro.getMatricula()));
        parametros.put(SER_CPF, getValue(consultarMovimentoFinanceiro.getCpf()));
        parametros.put(ORG_IDENTIFICADOR, getValue(consultarMovimentoFinanceiro.getOrgaoCodigo()));
        parametros.put(EST_IDENTIFICADOR, getValue(consultarMovimentoFinanceiro.getEstabelecimentoCodigo()));
        parametros.put(CSA_IDENTIFICADOR, getValue(consultarMovimentoFinanceiro.getConsignatariaCodigo()));
        parametros.put(SERVICO_CODIGO, getValue(consultarMovimentoFinanceiro.getServicoCodigo()));
        parametros.put(CNV_COD_VERBA, getValue(consultarMovimentoFinanceiro.getCodVerba()));

        return parametros;
    }
}
