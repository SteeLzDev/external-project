package com.zetra.econsig.persistence.query.beneficios.contrato;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarContratosBeneficioPorRegistroServidorQuery</p>
 * <p>Description: Lista dos contratos beneficios por natureza, status e registro servidor</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarContratosBeneficioPorRegistroServidorQuery extends HQuery {

    public List<String> scbCodigos;
    public String rseCodigo;
    public String nseCodigo;
    public List<String> tibCodigo;
    public String bfcCodigo;
    public List<String> tntCodigo;
    public String csaCodigo;
    public boolean reativar;
    public boolean reservaSemRegrasModulo;
    public String benCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT cbe.cbeCodigo, csa.csaNome, ben.benDescricao, svc.svcCodigo, ");
        corpoBuilder.append(" cbe.cbeValorTotal as valorTotal, cbe.cbeValorSubsidio as valorSubsidio, ");
        corpoBuilder.append(" ben.benCodigo, csa.csaCodigo, cbe.cbeDataInicioVigencia, ben.benCategoria, ade.adeCodigo, bfc.bfcSubsidioConcedido, ade.adeVlr, bfc.tipoBeneficiario.tibCodigo , cbe.cbeNumero ");
        corpoBuilder.append("FROM ContratoBeneficio cbe ");
        corpoBuilder.append("INNER JOIN cbe.autDescontoSet ade ");
        corpoBuilder.append("INNER JOIN ade.registroServidor rse ");
        corpoBuilder.append("INNER JOIN cbe.beneficio ben ");
        corpoBuilder.append("INNER JOIN ben.consignataria csa ");
        corpoBuilder.append("INNER JOIN ben.naturezaServico nse ");
        corpoBuilder.append("INNER JOIN cbe.beneficiario bfc ");
        corpoBuilder.append("INNER JOIN ade.verbaConvenio vco ");
        corpoBuilder.append("INNER JOIN vco.convenio cnv ");
        corpoBuilder.append("INNER JOIN cnv.servico svc ");

        corpoBuilder.append("WHERE 1 = 1 ");
        corpoBuilder.append("AND cbe.statusContratoBeneficio.scbCodigo IN  (:scbCodigos) ");
        corpoBuilder.append("AND nse.nseCodigo = :nseCodigo ");

        if (!reativar) {
            corpoBuilder.append("AND bfc.tipoBeneficiario.tibCodigo in (:tibCodigo) ");
            corpoBuilder.append("AND rse.rseCodigo = :rseCodigo ");
        } else {
            corpoBuilder.append("AND NULLIF(TRIM(cbe.cbeNumero), '') IS NOT NULL ");
        }

        if (!reservaSemRegrasModulo) {
            corpoBuilder.append("AND ade.tipoLancamento.tipoNatureza.tntCodigo in (:tntCodigo) ");
        }
        if (!TextHelper.isNull(benCodigo)) {
            corpoBuilder.append("AND ben.benCodigo = :benCodigo ");
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append("AND csa.csaCodigo = :csaCodigo ");
        }

        if (!TextHelper.isNull(bfcCodigo)) {
            corpoBuilder.append(" AND bfc.bfcCodigo = :bfcCodigo ");
        }

        corpoBuilder.append(" GROUP BY cbe.cbeCodigo, csa.csaNome, ben.benDescricao, ade.verbaConvenio.convenio.servico.svcCodigo ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("scbCodigos", scbCodigos, query);
        defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        if (!reativar) {
            defineValorClausulaNomeada("tibCodigo", tibCodigo, query);
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }
        if (!reservaSemRegrasModulo) {
            defineValorClausulaNomeada("tntCodigo", tntCodigo, query);
        }

        if (!TextHelper.isNull(bfcCodigo)) {
            defineValorClausulaNomeada("bfcCodigo", bfcCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(benCodigo)) {
            defineValorClausulaNomeada("benCodigo", benCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CBE_CODIGO,
                Columns.CSA_NOME,
                Columns.BEN_DESCRICAO,
                Columns.SVC_CODIGO,
                "valorTotal",
                "valorSubsidio",
                Columns.BEN_CODIGO,
                Columns.CSA_CODIGO,
                Columns.CBE_DATA_INICIO_VIGENCIA,
                Columns.BEN_CATEGORIA,
                Columns.ADE_CODIGO,
                Columns.BFC_SUBSIDIO_CONCEDIDO,
                Columns.ADE_VLR,
                Columns.TIB_CODIGO,
                Columns.CBE_NUMERO
        };
    }
}
