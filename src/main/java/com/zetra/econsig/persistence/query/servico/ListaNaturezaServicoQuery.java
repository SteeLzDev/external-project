package com.zetra.econsig.persistence.query.servico;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusConsignatariaEnum;

/**
 * <p>Title: ListaNaturezaServicoQuery</p>
 * <p>Description: Lista Naturezas do Serviço cadastradas.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaNaturezaServicoQuery extends HQuery {

    public boolean orderById;
    public boolean naturezaBeneficio;
    public String nseCodigoPai;
    public String orgCodigo;

    public List<String> nseCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo =  new StringBuilder("SELECT " +
        "nse.nseCodigo, " +
        "nse.nseDescricao, " +
        "nse.nseImagem, " +
        "nse.nseOrdemBeneficio " +
        "from NaturezaServico nse " +
        "where 1=1");

        if (nseCodigo != null && !nseCodigo.isEmpty()) {
            corpo.append(" and nse.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }

        if (naturezaBeneficio) {
            if (TextHelper.isNull(nseCodigoPai)) {
                corpo.append(" and nse.naturezaServicoPai.nseCodigo is null ");
            } else {
                corpo.append(" and nse.naturezaServicoPai.nseCodigo ").append(criaClausulaNomeada("nseCodigoPai", nseCodigoPai));
            }
            corpo.append(" and (exists (select 1 from Beneficio ben");
            corpo.append(" INNER JOIN ben.consignataria csa");
            corpo.append(" where ben.naturezaServico.nseCodigo = nse.nseCodigo ");
            corpo.append(" and csa.csaAtivo = ").append(StatusConsignatariaEnum.ATIVO.getCodigo());
            if (!TextHelper.isNull(orgCodigo)) {
                corpo.append(" and exists (select 1 from Convenio cnv where cnv.consignataria.csaCodigo = csa.csaCodigo and cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
                corpo.append(" and cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
                corpo.append(" and cnv.servico.svcCodigo in (select svc.svcCodigo from Servico svc where svc.naturezaServico.nseCodigo = nse.nseCodigo)");
                corpo.append(")");
            }
            corpo.append(")");
            corpo.append(" or exists (select 1 from Beneficio benInner");
            corpo.append(" INNER JOIN benInner.naturezaServico nseInner");
            corpo.append(" INNER JOIN benInner.consignataria csaInner");
            corpo.append(" where nseInner.naturezaServicoPai.nseCodigo = nse.nseCodigo");
            corpo.append(" and csaInner.csaAtivo = ").append(StatusConsignatariaEnum.ATIVO.getCodigo());
            if (!TextHelper.isNull(orgCodigo)) {
                corpo.append(" and exists (select 1 from Convenio cnvInner where cnvInner.consignataria.csaCodigo = csaInner.csaCodigo and cnvInner.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
                corpo.append(" and cnvInner.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
                corpo.append(" and cnvInner.servico.svcCodigo in (select svcInner.svcCodigo from Servico svcInner where svcInner.naturezaServico.nseCodigo = nseInner.nseCodigo)");
                corpo.append(")");
            }
            corpo.append("))");
        }

        corpo.append(" order by ").append(orderById ? "nse.nseCodigo" : "nse.nseDescricao");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (nseCodigo != null && !nseCodigo.isEmpty()) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }

        if (!TextHelper.isNull(nseCodigoPai)) {
            defineValorClausulaNomeada("nseCodigoPai", nseCodigoPai, query);
        }

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] { Columns.NSE_CODIGO, Columns.NSE_DESCRICAO, Columns.NSE_IMAGEM, Columns.NSE_ORDEM_BENEFICIO };
    }
}
