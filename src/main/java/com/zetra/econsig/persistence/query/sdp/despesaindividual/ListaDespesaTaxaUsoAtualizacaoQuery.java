package com.zetra.econsig.persistence.query.sdp.despesaindividual;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.NaturezaPlanoEnum;

/**
 * <p>Title: ListaDespesaTaxaUsoAtualizacaoQuery</p>
 * <p>Description: Lista as despeas individuais de taxa de uso que devem
 * sofrer atualização caso o posto seja alterado.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaDespesaTaxaUsoAtualizacaoQuery extends HQuery {

    public String posCodigo;

    public String prmCodigo;

    public String rseCodigo;

    public Boolean echCondominio;

    public String echCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        corpo = "SELECT" +
                " ade.adeCodigo, " +
                " ade.adeVlr, " +
                " ade.adeIdentificador, " +
                " ade.adeIndice, " +
                " ech.echCodigo, " +
                " rse.rseCodigo ";

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from RegistroServidor rse ");
        corpoBuilder.append("inner join rse.autDescontoSet ade ");
        corpoBuilder.append("inner join ade.despesaIndividualSet dei ");
        corpoBuilder.append("inner join dei.plano pla  ");
        corpoBuilder.append("inner join dei.permissionario prm ");
        corpoBuilder.append("inner join prm.enderecoConjHabitacional ech ");

        corpoBuilder.append("where prm.registroServidor.rseCodigo = rse.rseCodigo ");
        corpoBuilder.append("and pla.naturezaPlano.nplCodigo = '").append(NaturezaPlanoEnum.TAXA_USO.getCodigo()).append("' ");

        if (echCondominio != null) {
            // Busca depesas de endereço seja de condomínio ou não, de acordo com o valor alterado no posto
            if (echCondominio) {
                corpoBuilder.append(" and ech.echCondominio = 'S' ");
            } else {
                corpoBuilder.append(" and ech.echCondominio = 'N' ");
            }
        }

        if (!TextHelper.isNull(posCodigo)) {
            corpoBuilder.append(" and rse.postoRegistroServidor.posCodigo ").append(criaClausulaNomeada("posCodigo", posCodigo));
        }
        if (!TextHelper.isNull(prmCodigo)) {
            corpoBuilder.append(" and prm.prmCodigo ").append(criaClausulaNomeada("prmCodigo", prmCodigo));
        }
        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append(" and rse.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        }
        if (!TextHelper.isNull(echCodigo)){
            corpoBuilder.append(" and ech.echCodigo ").append(criaClausulaNomeada("echCodigo", echCodigo));
        }

        // Permissionário e servidor ativo
        corpoBuilder.append(" and rse.statusRegistroServidor.srsCodigo = '").append(CodedValues.SRS_ATIVO).append("'");
        corpoBuilder.append(" and prm.prmAtivo = ").append(CodedValues.STS_ATIVO);

        // Lista de status que podem ser alterados
        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
        corpoBuilder.append(" and ade.statusAutorizacaoDesconto.sadCodigo in ('").append(TextHelper.join(sadCodigos, "','")).append("') ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        if (!TextHelper.isNull(posCodigo)) {
            defineValorClausulaNomeada("posCodigo", posCodigo, query);
        }
        if (!TextHelper.isNull(prmCodigo)) {
            defineValorClausulaNomeada("prmCodigo", prmCodigo, query);
        }
        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }
        if (!TextHelper.isNull(echCodigo)) {
            defineValorClausulaNomeada("echCodigo", echCodigo, query);
        }
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.ADE_VLR,
                Columns.ADE_IDENTIFICADOR,
                Columns.ADE_INDICE,
                Columns.ECH_CODIGO,
                Columns.RSE_CODIGO
                };
    }
}
