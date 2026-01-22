package com.zetra.econsig.persistence.query.parametro;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaParamSvcCsaLimiteMargemQuery</p>
 * <p>Description: Lista o parâmetro de serviço de Consignatária que limita a
 * margem folha do servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2016</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaParamSvcCsaLimiteMargemQuery extends HQuery {
    public String csaCodigo;
    public String rseCodigo;
    public Short marCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT min_value(min(to_decimal(substituir(psc.pscVlr, ',', '.'), 13, 8)), 100) / 100.00 ");
        corpoBuilder.append("FROM Servico svc ");
        corpoBuilder.append("INNER JOIN svc.naturezaServico nse ");
        corpoBuilder.append("INNER JOIN svc.paramSvcConsignatariaSet psc ");
        corpoBuilder.append("INNER JOIN svc.paramSvcConsignanteSet pse ");
        corpoBuilder.append("WHERE isnumeric(substituir(psc.pscVlr, ',', '.')) = 1 ");
        corpoBuilder.append("AND svc.svcAtivo = ").append(CodedValues.STS_ATIVO).append(" ");
        corpoBuilder.append("AND psc.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_PERCENTUAL_MARGEM_FOLHA_LIMITE_CSA).append("' ");
        corpoBuilder.append("AND pse.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_INCIDE_MARGEM).append("' ");
        corpoBuilder.append("AND psc.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append("AND to_short(pse.pseVlr) ").append(criaClausulaNomeada("marCodigo", marCodigo));

        // Não existe bloqueio de convênio por registro servidor
        corpoBuilder.append("AND NOT EXISTS (");
        corpoBuilder.append("  SELECT 1 FROM Convenio cnv");
        corpoBuilder.append("  INNER JOIN cnv.paramConvenioRegistroSerSet pcr");
        corpoBuilder.append("  WHERE cnv.consignataria.csaCodigo = psc.consignataria.csaCodigo");
        corpoBuilder.append("    AND cnv.servico.svcCodigo = psc.servico.svcCodigo");
        corpoBuilder.append("    AND pcr.tpsCodigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO).append("'");
        corpoBuilder.append("    AND pcr.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append("    AND coalesce(pcr.pcrVlr, '99') = '0'");
        corpoBuilder.append(") ");

        // Não existe bloqueio de servico por registro servidor
        corpoBuilder.append("AND NOT EXISTS (");
        corpoBuilder.append("  SELECT 1 FROM svc.paramServicoRegistroSerSet psr");
        corpoBuilder.append("  WHERE psr.tpsCodigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_SERVICO).append("'");
        corpoBuilder.append("    AND psr.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append("    AND coalesce(psr.psrVlr, '99') = '0'");
        corpoBuilder.append(") ");

        // Não existe bloqueio de natureza de servico por registro servidor
        corpoBuilder.append("AND NOT EXISTS (");
        corpoBuilder.append("  SELECT 1 FROM nse.paramNseRegistroSerSet pnr");
        corpoBuilder.append("  WHERE pnr.tpsCodigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_NATUREZA_SERVICO).append("'");
        corpoBuilder.append("    AND pnr.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        corpoBuilder.append("    AND coalesce(pnr.pnrVlr, '99') = '0'");
        corpoBuilder.append(") ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("marCodigo", marCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PSC_VLR
        };
    }
}
