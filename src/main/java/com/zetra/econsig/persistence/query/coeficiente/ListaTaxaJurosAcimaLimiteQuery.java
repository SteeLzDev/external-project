package com.zetra.econsig.persistence.query.coeficiente;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaTaxaJurosAcimaLimiteQuery</p>
 * <p>Description: Listagem das Taxas de Juros/CET que estão superiores
 * à tabela de taxas da consignatária limite.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaTaxaJurosAcimaLimiteQuery extends HQuery {

    public String csaCodigo;
    public String csaCodigoLimiteTaxa;
    public String svcCodigo;
    
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "select " +
                       "cft.cftCodigo, " +
                       "cft.cftDia, " +
                       "cft.cftVlr, " +
                       "cft.cftDataIniVig, " +
                       "cft.cftDataFimVig, " +
                       "prz.przVlr ";
        
        StringBuilder corpoBuilder = new StringBuilder(corpo);
       
        corpoBuilder.append(" FROM CoeficienteAtivo cft");
        corpoBuilder.append(" INNER JOIN cft.prazoConsignataria pzc");
        corpoBuilder.append(" INNER JOIN pzc.prazo prz");
        corpoBuilder.append(" INNER JOIN pzc.consignataria csa");
        corpoBuilder.append(" WHERE EXISTS (SELECT 1");
        corpoBuilder.append(" FROM CoeficienteAtivo cftLimite");
        corpoBuilder.append(" INNER JOIN cftLimite.prazoConsignataria pzcLimite");
        corpoBuilder.append(" INNER JOIN pzcLimite.prazo przLimite");
        corpoBuilder.append(" INNER JOIN pzcLimite.consignataria csaLimite ");
        corpoBuilder.append(" WHERE csaLimite.csaCodigo ").append(criaClausulaNomeada("csaCodigoLimiteTaxa", csaCodigoLimiteTaxa));
        corpoBuilder.append(" AND prz.przVlr = przLimite.przVlr ");
        corpoBuilder.append(" AND przLimite.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        corpoBuilder.append(" AND cftLimite.cftDataFimVig IS NULL");
        corpoBuilder.append(" AND cftLimite.cftDataIniVig > current_date()");
        corpoBuilder.append(" AND cftLimite.cftVlr > 0");
        corpoBuilder.append(" AND cft.cftVlr >= cftLimite.cftVlr");
        corpoBuilder.append(" AND pzcLimite.przCsaAtivo = ").append(CodedValues.STS_ATIVO);
        corpoBuilder.append(" AND przLimite.przAtivo = ").append(CodedValues.STS_ATIVO).append(")");
        
        corpoBuilder.append(" AND pzc.przCsaAtivo = ").append(CodedValues.STS_ATIVO);
        corpoBuilder.append(" AND prz.przAtivo = ").append(CodedValues.STS_ATIVO);
        
        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" AND prz.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }
        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        // Buscando as taxas de juros que estão sendo editadas
        corpoBuilder.append(" AND (cft.cftDataFimVig IS NULL)");

        corpoBuilder.append(" ORDER BY prz.przVlr");
        
        // Define os valores para os parâmetros nomeados
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        }
        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (!TextHelper.isNull(csaCodigoLimiteTaxa)) {
            defineValorClausulaNomeada("csaCodigoLimiteTaxa", csaCodigoLimiteTaxa, query);
        }        

        return query;
    }

    @Override
    protected String[] getFields() {        
        return new String[] {
                Columns.CFT_CODIGO,
                Columns.CFT_DIA,
                Columns.CFT_VLR,
                Columns.CFT_DATA_INI_VIG,
                Columns.CFT_DATA_FIM_VIG,
                Columns.PRZ_VLR
        };        
    }
}
