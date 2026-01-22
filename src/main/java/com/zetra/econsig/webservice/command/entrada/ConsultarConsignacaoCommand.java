package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.webservice.CamposAPI.ADE_IDENTIFICADOR;
import static com.zetra.econsig.webservice.CamposAPI.ADE_NUMERO;
import static com.zetra.econsig.webservice.CamposAPI.OPERACAO;
import static com.zetra.econsig.webservice.CamposAPI.RSE_MATRICULA;
import static com.zetra.econsig.webservice.CamposAPI.SER_CPF;

import java.util.Map;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.webservice.CamposAPI;

/**
 * <p>Title: ConsultarConsignacaoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de consultar consignação</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsultarConsignacaoCommand extends RequisicaoExternaCommand {

    public ConsultarConsignacaoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {

    }

    @Override
    protected void validaEntrada(Map<CamposAPI, Object> parametros) throws ZetraException {
        super.validaEntrada(parametros);
        if (CodedValues.OPERACOES_DETALHAR_ADE.contains(parametros.get(OPERACAO))) {
            pesquisaServico(parametros);
            pesquisaConsignataria(parametros);
        }
        verificaFiltrosConsulta(parametros);
    }

    private void verificaFiltrosConsulta(Map<CamposAPI, Object> parametros) throws ZetraException {
        Object adeNumero = parametros.get(ADE_NUMERO);
        Object adeIdentificador = parametros.get(ADE_IDENTIFICADOR);
        Object serCpf = parametros.get(SER_CPF);
        Object rseMatricula = parametros.get(RSE_MATRICULA);

        // Verifica se foi passado algum parâmetro para a consulta de consignação
        if ((adeNumero == null || adeNumero.equals("")) &&
                (adeIdentificador == null || adeIdentificador.equals("")) &&
                (serCpf == null || serCpf.equals("")) &&
                (rseMatricula == null || rseMatricula.equals(""))) {
            throw new ZetraException("mensagem.informe.ade.nro.ade.ident.ser.cpf.ser.mat", responsavel);
        }
    }
}
