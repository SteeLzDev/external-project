package com.zetra.econsig.persistence.query.coeficiente;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaAtivarCoeficienteQuery</p>
 * <p>Description: Listagem de Coeficientes a serem excluídos na ativação.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaAtivarCoeficienteQuery extends HQuery {

    public String tipo;
    public String csaCodigo;
    public String svcCodigo;
    public int prazo;
    public int filtro;
    public Date cftDataIniVig;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String restricao = "";
        if (CodedValues.CFT_DIARIO.equals(tipo)) {
            restricao = " > 0";
        } else if (CodedValues.CFT_MENSAL.equals(tipo)) {
            restricao = " = 0";
        } else {
            throw new HQueryException("mensagem.erro.coeficiente.parametro.invalido", (AcessoSistema) null);
        }

        String fields = "select cft.cftCodigo, cft.cftDia, prz.przVlr, cft.cftVlr, "
                + "cft.cftDataIniVig, cft.cftDataFimVig, "
                + "pzc.przCsaCodigo, cft.cftDataCadastro ";

        StringBuilder corpoBuilder = new StringBuilder(fields);
        corpoBuilder.append(" FROM Prazo prz");
        corpoBuilder.append(" INNER JOIN prz.prazoConsignatariaSet pzc");
        corpoBuilder.append(" INNER JOIN pzc.coeficienteAtivoSet cft");
        corpoBuilder.append(" WHERE pzc.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        corpoBuilder.append(" AND prz.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        if (prazo > 0) {
            corpoBuilder.append(" AND prz.przVlr ").append(criaClausulaNomeada("przVlr", prazo));
        }
        switch (filtro) {
            case 0:
                corpoBuilder.append(" AND cft.cftDia ").append(restricao);
                corpoBuilder.append(" AND cft.cftDataIniVig > current_date() ");
                break;

            case 1:
                corpoBuilder.append(" AND cft.cftDia ").append(restricao);
                corpoBuilder.append(" AND cft.cftDataIniVig IS NULL ");
                break;

            case 2:
                corpoBuilder.append(" AND cft.cftDataIniVig < :cftDataIniVig ");
                corpoBuilder.append(" AND cft.cftDataFimVig IS NULL ");
                break;

            case 3:
                if (CodedValues.CFT_DIARIO.equals(tipo)) {
                    restricao = " = 0";
                } else if (CodedValues.CFT_MENSAL.equals(tipo)) {
                    restricao = " > 0";
                } else {
                    throw new HQueryException("mensagem.erro.coeficiente.parametro.invalido", (AcessoSistema) null);
                }
                corpoBuilder.append(" AND cft.cftDia ").append(restricao);
                corpoBuilder.append(" AND cft.cftDataIniVig = :cftDataIniVig ");
                corpoBuilder.append(" AND cft.cftDataFimVig IS NULL ");
                break;

            default:
                break;
        }

        // Define os valores para os parâmetros nomeados
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (prazo > 0) {
            defineValorClausulaNomeada("przVlr", prazo, query);
        }
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        if (filtro >= 2) {
            defineValorClausulaNomeada("cftDataIniVig", cftDataIniVig, query);
        }
        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CFT_CODIGO,
                Columns.CFT_DIA,
                Columns.PRZ_VLR,
                Columns.CFT_VLR,
                Columns.CFT_DATA_INI_VIG,
                Columns.CFT_DATA_FIM_VIG,
                Columns.CFT_PRZ_CSA_CODIGO,
                Columns.CFT_DATA_CADASTRO
        };
    }
}
