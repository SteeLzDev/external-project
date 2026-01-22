package com.zetra.econsig.persistence.query.sdp.plano;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.NaturezaPlanoEnum;

/**
 * <p>Title: ListaPlanosDescontoSemRateioQuery</p>
 * <p>Description: Lista Planos de Desconto Sem Rateio</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaPlanosDescontoSemRateioQuery extends HQuery {

    public String csaCodigo;
    public String svcCodigo;
    public String plaCodigo;
    public String plaDescricao;
    public String plaIdentificador;
    public Short plaAtivo;
    public boolean taxaUso = true;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        corpo =
            "select plano.plaCodigo, " +
            "   svc.svcCodigo, " +
            "   svc.svcDescricao, " +
            "   csa.csaCodigo, " +
            "   csa.csaIdentificador, " +
            "   csa.csaNome, " +
            "   npl.nplCodigo, " +
            "   plano.plaDescricao, " +
            "   plano.plaAtivo, " +
            "   plano.plaIdentificador ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from Plano plano ");
        corpoBuilder.append("inner join plano.servico svc ");
        corpoBuilder.append("inner join plano.naturezaPlano npl ");
        corpoBuilder.append("inner join plano.consignataria csa ");
        corpoBuilder.append("left outer join plano.parametroPlanoSet ppl ");
        corpoBuilder.append("with ppl.tipoParametroPlano.tppCodigo = '").append(CodedValues.TPP_TIPO_RATEIO_PLANO).append("' ");

        corpoBuilder.append("where 1 = 1");

        corpoBuilder.append(" and (ppl.plano.plaCodigo is null or ppl.pplValor = '").append(CodedValues.PLANO_SEM_RATEIO).append("')");


        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" and svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(plaCodigo)) {
            corpoBuilder.append(" and plano.plaCodigo ").append(criaClausulaNomeada("plaCodigo", plaCodigo));
        }

        if (!TextHelper.isNull(plaDescricao)) {
            corpoBuilder.append(" and plano.plaDescricao ").append(criaClausulaNomeada("plaDescricao", plaDescricao));
        }

        if (!TextHelper.isNull(plaIdentificador)) {
            corpoBuilder.append(" and plano.plaIdentificador ").append(criaClausulaNomeada("plaIdentificador", plaIdentificador));
        }

        if (plaAtivo != null) {
            corpoBuilder.append(" and plano.plaAtivo ").append(criaClausulaNomeada("plaAtivo", plaAtivo));
        }

        if (!taxaUso) {
            corpoBuilder.append(" and not npl.nplCodigo ").append(criaClausulaNomeada("nplCodigo", NaturezaPlanoEnum.TAXA_USO.getCodigo()));
        }

        corpoBuilder.append(" order by plano.plaDescricao");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        // Seta os parâmetros na query
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }

        if (!TextHelper.isNull(plaCodigo)) {
            defineValorClausulaNomeada("plaCodigo", plaCodigo, query);
        }

        if (!TextHelper.isNull(plaDescricao)) {
            defineValorClausulaNomeada("plaDescricao", plaDescricao, query);
        }

        if (!TextHelper.isNull(plaIdentificador)) {
            defineValorClausulaNomeada("plaIdentificador", plaIdentificador, query);
        }

        if (plaAtivo != null) {
            defineValorClausulaNomeada("plaAtivo", plaAtivo, query);
        }

        if (!taxaUso) {
            defineValorClausulaNomeada("nplCodigo", NaturezaPlanoEnum.TAXA_USO.getCodigo(), query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PLA_CODIGO,
                Columns.SVC_CODIGO,
                Columns.SVC_DESCRICAO,
                Columns.CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.NPL_CODIGO,
                Columns.PLA_DESCRICAO,
                Columns.PLA_ATIVO,
                Columns.PLA_IDENTIFICADOR
        };
    }
}
