package com.zetra.econsig.persistence.query.beneficios.contrato;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusContratoBeneficioEnum;

/**
 * <p>Title: ListaOcorrenciaContratosBeneficiosCancelamentoSolicitadoQuery</p>
 * <p>Description: Listagem de Ocorrências de Contrato Beneficios</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision: $
 * $Date: 2020-03-03 09:27:49 -0200 (ter, 03 mar 2020) $
 */
public class ListaOcorrenciaContratosBeneficiosCancelamentoSolicitadoQuery extends HQuery {

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        corpo =  "select distinct cbe.cbeCodigo as CBE_CODIGO ";

        StringBuilder corpoBuilder = null;

        corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" from ContratoBeneficio cbe");
        corpoBuilder.append(" INNER JOIN cbe.ocorrenciaCttBeneficioSet ocb");
        corpoBuilder.append(" where cbe.statusContratoBeneficio.scbCodigo= :scbCodigo");
        corpoBuilder.append(" and ocb.tipoOcorrencia.tocCodigo= :tocCodigo");
        corpoBuilder.append(" and (select count(*) from Calendario where calDiaUtil='S' and calData between to_date(ocb.ocbData) and current_date()) > 3");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("scbCodigo", StatusContratoBeneficioEnum.CANCELAMENTO_SOLICITADO_BENEFICIARIO.getCodigo(), query);
        defineValorClausulaNomeada("tocCodigo", CodedValues.TOC_SOLICITACAO_CANC_CONTRATO_BENEFICIO, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CBE_CODIGO
        };
    }

}
