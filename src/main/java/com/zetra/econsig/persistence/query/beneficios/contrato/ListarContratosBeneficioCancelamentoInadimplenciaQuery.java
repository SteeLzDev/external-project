package com.zetra.econsig.persistence.query.beneficios.contrato;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarContratosBeneficioCancelamentoInadimplenciaQuery</p>
 * <p>Description: Lista dos contratos beneficios por matricula, bfc para cancelamento por inadimplência</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos.</p>
 * $Author: marcos.nolasco $
 * $Revision: 29273 $
 * $Date: 2020-04-07 13:33:31 -0300 (ter, 07 abr 2020) $
 */
public class ListarContratosBeneficioCancelamentoInadimplenciaQuery extends HQuery {

    public String rseMatricula;
    public String bfcCpf;
    public String benCodigoContrato;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        List<String> scbCodigos =  new ArrayList<>();
        scbCodigos.add(CodedValues.SCB_CODIGO_CANCELADO);
        scbCodigos.add(CodedValues.SCB_CODIGO_CANCELAMENTO_SOLICITADO_BENEFICIARIO);
        scbCodigos.add(CodedValues.SCB_CODIGO_CANCELAMENTO_SOLICITADO);

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT cbe.cbeCodigo ");
        corpoBuilder.append("FROM ContratoBeneficio cbe ");
        corpoBuilder.append("INNER JOIN cbe.autDescontoSet ade ");
        corpoBuilder.append("INNER JOIN ade.registroServidor rse ");
        corpoBuilder.append("INNER JOIN rse.servidor ser ");
        corpoBuilder.append("INNER JOIN cbe.beneficio ben ");
        corpoBuilder.append("INNER JOIN ser.beneficiarioSet bfc ");

        corpoBuilder.append("WHERE 1 = 1 ");

        if (!TextHelper.isNull(rseMatricula)) {
            corpoBuilder.append("AND rse.rseMatricula = :rseMatricula ");
        }

        if (!TextHelper.isNull(bfcCpf)) {
            corpoBuilder.append("AND bfc.bfcCpf = :bfcCpf ");
        }

        if (!TextHelper.isNull(benCodigoContrato)) {
            corpoBuilder.append("AND ben.benCodigoContrato = :benCodigoContrato ");
        }

        corpoBuilder.append("AND cbe.statusContratoBeneficio.scbCodigo NOT IN  (:scbCodigos) ");
        corpoBuilder.append("AND ade.tipoLancamento.tipoNatureza.tntCodigo IN (:tntCodigo) ");
        corpoBuilder.append("AND ade.statusAutorizacaoDesconto.sadCodigo IN (:sadCodigo) ");


        corpoBuilder.append(" GROUP BY cbe.cbeCodigo ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("scbCodigos", scbCodigos, query);
        defineValorClausulaNomeada("tntCodigo", CodedValues.TNT_BENEFICIO_MENSALIDADE, query);
        defineValorClausulaNomeada("sadCodigo", CodedValues.SAD_CODIGOS_INCLUSAO_PARCELA, query);

        if (!TextHelper.isNull(rseMatricula)) {
            defineValorClausulaNomeada("rseMatricula", rseMatricula, query);
        }

        if (!TextHelper.isNull(bfcCpf)) {
            defineValorClausulaNomeada("bfcCpf", bfcCpf, query);
        }

        if (!TextHelper.isNull(benCodigoContrato)) {
            defineValorClausulaNomeada("benCodigoContrato", benCodigoContrato, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CBE_CODIGO
        };
    }
}
