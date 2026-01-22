package com.zetra.econsig.persistence.query.coeficiente;

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
 * <p>Title: ListaServicoPrazoAtivoQuery</p>
 * <p>Description: Listagem de Serviços que tem prazos cadastrados e ativos.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaServicoPrazoAtivoQuery extends HQuery {

    public String csaCodigo;
    public String svcCodigo;
    public String prazoInicial;
    public String prazoFinal;
    public String prazoOrdenacao;
    public String prazosInformados;
    public List<Short> prazosInformadosList = new ArrayList<>();
    public boolean prazo;
    public boolean prazoMultiploDoze;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final Short stsAtivo = CodedValues.STS_ATIVO;

        final StringBuilder corpoBuilder = new StringBuilder();

        if (prazo) {
            if (!TextHelper.isNull(csaCodigo) && !TextHelper.isNull(svcCodigo)) {
                corpoBuilder.append("select distinct prz.przVlr, pzc.przCsaCodigo ");
            } else {
                corpoBuilder.append("select distinct prz.przVlr ");
            }
        } else {
            corpoBuilder.append("select distinct svc.svcCodigo, svc.svcDescricao, svc.svcIdentificador ");
        }

        corpoBuilder.append(" FROM Servico svc");
        corpoBuilder.append(" INNER JOIN svc.prazoSet prz");
        corpoBuilder.append(" INNER JOIN prz.prazoConsignatariaSet pzc");
        corpoBuilder.append(" WHERE prz.przAtivo ").append(criaClausulaNomeada("przAtivo", stsAtivo));
        corpoBuilder.append(" AND pzc.przCsaAtivo ").append(criaClausulaNomeada("przCsaAtivo", stsAtivo));

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND pzc.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" AND prz.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }
        if (!TextHelper.isNull(prazoInicial) && !TextHelper.isNull(prazoFinal)) {
            corpoBuilder.append(" AND ((prz.przVlr >= :prazoInicial");
            corpoBuilder.append("       AND prz.przVlr <= :prazoFinal)");

            if (!TextHelper.isNull(prazoOrdenacao)) {
                corpoBuilder.append(" OR prz.przVlr ").append(criaClausulaNomeada("prazoOrdenacao", prazoOrdenacao));
            }
            corpoBuilder.append("     )");
        }
        if (prazoMultiploDoze) {
            corpoBuilder.append(" AND MOD(prz.przVlr,12) = 0 ");
        }
        if (!TextHelper.isNull(prazosInformados)) {
            for(final String prazo: prazosInformados.split(",")){
                if(!TextHelper.isNotNumeric(prazo)) {
                    prazosInformadosList.add(Short.parseShort(prazo));
                }
            }
            corpoBuilder.append(" AND prz.przVlr ").append(criaClausulaNomeada("prazosInformadosList", prazosInformadosList));
        }

        if (prazo) {
            corpoBuilder.append(" ORDER BY prz.przVlr");
        } else {
            corpoBuilder.append(" ORDER BY svc.svcDescricao");
        }

        // Define os valores para os parâmetros nomeados
        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("przAtivo", stsAtivo, query);
        defineValorClausulaNomeada("przCsaAtivo", stsAtivo, query);

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }
        if (!TextHelper.isNull(prazosInformados)) {
            defineValorClausulaNomeada("prazosInformadosList", prazosInformadosList, query);
        }

        if (!TextHelper.isNull(prazoInicial) && !TextHelper.isNull(prazoFinal)) {
            defineValorClausulaNomeada("prazoInicial", Short.parseShort(prazoInicial), query);
            defineValorClausulaNomeada("prazoFinal", Short.parseShort(prazoFinal), query);

            if (!TextHelper.isNull(prazoOrdenacao)) {
                defineValorClausulaNomeada("prazoOrdenacao", prazoOrdenacao, query);
            }
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        if (prazo) {
            if (!TextHelper.isNull(csaCodigo) && !TextHelper.isNull(svcCodigo)) {
                return new String[] {
                        Columns.PRZ_VLR,
                        Columns.PZC_CSA_CODIGO
                };
            } else {
                return new String[] {
                        Columns.PRZ_VLR
                };
            }
        } else {
            return new String[] {
                    Columns.SVC_CODIGO,
                    Columns.SVC_DESCRICAO,
                    Columns.SVC_IDENTIFICADOR
            };
        }
    }
}
