package com.zetra.econsig.persistence.query.coeficiente;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCoeficientesAtivosQuery</p>
 * <p>Description: Listagem de registros ativos da tabela de coeficientes ativos.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCoeficientesAtivosQuery extends HQuery {

    public String csaCodigo = null;
    public String svcCodigo = null;
    public boolean possuiDataFim = false;
    public boolean verificaIniVigPass = false;
    public boolean verificaIniVigFut = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append(" SELECT cfa.cftCodigo, ");
        corpoBuilder.append(" cfa.cftDia, ");
        corpoBuilder.append(" cfa.cftVlr, ");
        corpoBuilder.append(" cfa.cftDataIniVig, ");
        corpoBuilder.append(" cfa.cftDataFimVig, ");
        corpoBuilder.append(" cfa.cftDataCadastro, ");
        corpoBuilder.append(" csa.csaCodigo, ");
        corpoBuilder.append(" pse.pseVlr ");

        corpoBuilder.append(" FROM CoeficienteAtivo cfa");
        corpoBuilder.append(" INNER JOIN cfa.prazoConsignataria pzc");
        corpoBuilder.append(" INNER JOIN pzc.prazo prz");
        corpoBuilder.append(" INNER JOIN pzc.consignataria csa");
        corpoBuilder.append(" INNER JOIN prz.servico svc");
        corpoBuilder.append(" LEFT JOIN svc.paramSvcConsignanteSet pse WITH pse.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_DIAS_VIGENCIA_CET).append("'");
        corpoBuilder.append(" WHERE 1=1 ");
        
        corpoBuilder.append(" AND pzc.przCsaAtivo = ").append(CodedValues.STS_ATIVO);
        corpoBuilder.append(" AND prz.przAtivo = ").append(CodedValues.STS_ATIVO);
        
        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
        if (!TextHelper.isNull(svcCodigo)) {
            corpoBuilder.append(" AND prz.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        }

        if (possuiDataFim) {
            // Taxas com data fim já definida
            corpoBuilder.append(" AND cfa.cftDataFimVig > current_date()");
        } else {
            // Taxas com data fim indefinida
            corpoBuilder.append(" AND (cfa.cftDataFimVig is null or (isnumeric(pse.pseVlr) = 1 and cfa.cftDataFimVig > current_date()))");
        }
        if (verificaIniVigPass) {
            corpoBuilder.append(" AND cfa.cftDataIniVig < current_date()");
        } else if (verificaIniVigFut) {
            corpoBuilder.append(" AND cfa.cftDataIniVig > current_date()");
        }

        // Define os valores para os parâmetros nomeados
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (!TextHelper.isNull(svcCodigo)) {
            defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
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
                Columns.CFT_DATA_CADASTRO,
                Columns.CFT_PRZ_CSA_CODIGO,
                Columns.PSE_VLR
        };
    }
}
