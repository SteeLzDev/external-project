package com.zetra.econsig.persistence.query.beneficios.contrato;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarContratosMensalidadeBeneficioQuery</p>
 * <p>Description: Lista contratos de mensalidade de benefícios de uma operadora pela carteirinha do beneficiário e tipo de lançamento</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarContratosMensalidadeBeneficioQuery extends HQuery {

    public String csaCodigo;
    public String cbeNumero;
    public String tlaCodigoMensalidade;
    public List<String> svcCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ade.adeCodigo ");
        corpoBuilder.append(", rse.rseMatricula ");
        corpoBuilder.append(", ser.serCpf ");
        corpoBuilder.append(", cbe.cbeCodigo ");
        corpoBuilder.append(", svc.svcCodigo ");
        corpoBuilder.append(", org.orgIdentificador ");

        corpoBuilder.append(" FROM AutDesconto ade ");
        corpoBuilder.append(" INNER JOIN ade.registroServidor rse ");
        corpoBuilder.append(" INNER JOIN rse.orgao org ");
        corpoBuilder.append(" INNER JOIN rse.servidor ser ");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv ");
        corpoBuilder.append(" INNER JOIN cnv.servico svc ");
        corpoBuilder.append(" INNER JOIN ade.contratoBeneficio cbe ");

        corpoBuilder.append(" WHERE cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append(" AND cbe.cbeNumero ").append(criaClausulaNomeada("cbeNumero", cbeNumero));
        if (!TextHelper.isNull(tlaCodigoMensalidade)) {
            corpoBuilder.append(" AND ade.tipoLancamento.tlaCodigo ").append(criaClausulaNomeada("tlaCodigoMensalidade", tlaCodigoMensalidade));
        }
        if (!TextHelper.isNull(svcCodigos)) {
            corpoBuilder.append(" AND svc.svcCodigo ").append(criaClausulaNomeada("svcCodigos", svcCodigos));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("cbeNumero", cbeNumero, query);
        if (!TextHelper.isNull(tlaCodigoMensalidade)) {
            defineValorClausulaNomeada("tlaCodigoMensalidade", tlaCodigoMensalidade, query);
        }
        if (!TextHelper.isNull(svcCodigos)) {
            defineValorClausulaNomeada("svcCodigos", svcCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.SER_CPF,
                Columns.CBE_CODIGO,
                Columns.SVC_CODIGO,
                Columns.ORG_IDENTIFICADOR
        };
    }

}