package com.zetra.econsig.persistence.query.convenio;

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
 * <p>Title: ObtemConvenioRelCadTaxasQuery</p>
 * <p>Description: Listagem dos relacionamentos de cadastro de taxas.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ObtemConvenioRelCadTaxasQuery extends HQuery {

    public String svcCodigoOrigem;
    public String csaCodigo;
    public String corCodigo;
    public String orgCodigo;
    public String rseCodigo;
    public List<String> adeCodigosReneg;

    /**
     * A partir do relacionamento de compartilhamento de taxas, retorna o código
     * dos convênio no qual o servidor, representado pelo rseCodigo, ainda não
     * tem nenhum contrato ativo no sistema. No caso de renegociação, a lista
     * adeCodigosReneg deve ser preenchida com os códigos dos contratos a serem
     * renegociados.
     */
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        List<String> sadCodigos = new ArrayList<String>();
        sadCodigos.addAll(CodedValues.SAD_CODIGOS_ATIVOS);
        sadCodigos.addAll(CodedValues.SAD_CODIGOS_AGUARD_LIQ);

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select cnv.cnvCodigo, svc.svcIdentificador ");
        corpoBuilder.append(" from RelacionamentoServico rsv");
        corpoBuilder.append(" inner join rsv.servicoBySvcCodigoDestino svc");
        corpoBuilder.append(" inner join svc.convenioSet cnv");

        if (!TextHelper.isNull(corCodigo)) {
            corpoBuilder.append(" inner join cnv.correspondenteConvenioSet crc with ");
            corpoBuilder.append(" crc.correspondente.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigo)).append(" and ");
            corpoBuilder.append(" crc.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        }

        corpoBuilder.append(" where 1=1 ");
        corpoBuilder.append(" and cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        corpoBuilder.append(" and rsv.tipoNatureza.tntCodigo = '").append(CodedValues.TNT_COMPARTILHA_CADASTRO_TAXAS).append("'");

        if (!TextHelper.isNull(svcCodigoOrigem)) {
            corpoBuilder.append(" and rsv.servicoBySvcCodigoOrigem.svcCodigo ").append(criaClausulaNomeada("svcCodigoOrigem", svcCodigoOrigem));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" and cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        corpoBuilder.append(" and not exists (select 1 from AutDesconto ade2 ");
        corpoBuilder.append(" inner join ade2.verbaConvenio vco2");
        corpoBuilder.append(" inner join vco2.convenio cnv2");
        corpoBuilder.append(" inner join cnv2.servico svc2");
        corpoBuilder.append(" where cnv2.consignataria.csaCodigo = cnv.consignataria.csaCodigo ");
        corpoBuilder.append(" and cnv2.orgao.orgCodigo = cnv.orgao.orgCodigo ");
        corpoBuilder.append(" and ade2.statusAutorizacaoDesconto.sadCodigo").append(" in ('").append(TextHelper.join(sadCodigos, "','")).append("')");
        corpoBuilder.append(" and ade2.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo)).append("");
        if (adeCodigosReneg != null && adeCodigosReneg.size() > 0) {
            List<String> codigos = new ArrayList<String>(adeCodigosReneg);
            codigos.add(CodedValues.NOT_EQUAL_KEY);
            corpoBuilder.append(" and (ade2.adeCodigo ").append(criaClausulaNomeada("adeCodigosReneg", codigos)).append(")");
        }
        // Ou o convênio não possui contratos ou o grupo de serviço não possui contratos
        corpoBuilder.append(" and (cnv2.cnvCodigo = cnv.cnvCodigo");
        corpoBuilder.append("  or (svc.tipoGrupoSvc.tgsCodigo is not null and svc.tipoGrupoSvc.tgsCodigo = svc2.tipoGrupoSvc.tgsCodigo))");

        corpoBuilder.append(")");

        corpoBuilder.append(" ORDER BY svc.svcIdentificador");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(svcCodigoOrigem)) {
            defineValorClausulaNomeada("svcCodigoOrigem", svcCodigoOrigem, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        if (!TextHelper.isNull(corCodigo)) {
            defineValorClausulaNomeada("corCodigo", corCodigo, query);
        }

        if (adeCodigosReneg != null && adeCodigosReneg.size() > 0) {
            defineValorClausulaNomeada("adeCodigosReneg", adeCodigosReneg, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CNV_CODIGO,
                Columns.SVC_IDENTIFICADOR
        };
    }
}
