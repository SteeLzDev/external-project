package com.zetra.econsig.persistence.query.consignacao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaDuplicaParcelaQuery</p>
 * <p>Description: Listagem de duplicação de parcela.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaDuplicaParcelaQuery extends HQuery {

    public String csaCodigo;
    public String cnvCodVerba;
    public String adeIndice;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        //Status das ADEs para reajuste
        List<String> adeStatus = new ArrayList<String>();
        adeStatus.add(CodedValues.SAD_DEFERIDA);
        adeStatus.add(CodedValues.SAD_EMANDAMENTO);

        //Status dos convenios para reajuste
        List<String> cnvStatus = new ArrayList<String>();
        cnvStatus.add(CodedValues.SCV_ATIVO);

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ");
        corpoBuilder.append("ade.adeCodigo, ade.adeNumero, ade.adeIdentificador, ade.adeVlr, ade.adePrazo, ");
        corpoBuilder.append("ade.correspondente.corCodigo, ade.adeTipoVlr, ade.adeIntFolha, ade.adeIncMargem, ade.adePeriodicidade, ");
        corpoBuilder.append("rse.rseCodigo, rse.rseMatricula, ser.serNome, ser.serCpf, cnv.consignataria.csaCodigo, ");
        corpoBuilder.append("cnv.cnvCodigo ");
        corpoBuilder.append(" FROM AutDesconto ade ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("inner join ade.registroServidor rse ");
        corpoBuilder.append("inner join rse.servidor ser ");
        corpoBuilder.append(" WHERE cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));

        if (!TextHelper.isNull(cnvCodVerba)) {
            corpoBuilder.append(" AND cnv.cnvCodVerba ").append(criaClausulaNomeada("cnvCodVerba", cnvCodVerba));
        }
        if (!TextHelper.isNull(adeIndice)) {
            corpoBuilder.append(" AND ade.adeIndice ").append(criaClausulaNomeada("adeIndice", adeIndice));
        }
        if (adeStatus != null && !adeStatus.isEmpty()) {
            corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("adeStatus", adeStatus));
        }
        if (cnvStatus != null && !cnvStatus.isEmpty()) {
            corpoBuilder.append(" AND cnv.statusConvenio.scvCodigo ").append(criaClausulaNomeada("cnvStatus", cnvStatus));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        if (!TextHelper.isNull(cnvCodVerba)) {
            defineValorClausulaNomeada("cnvCodVerba", cnvCodVerba, query);
        }
        if (!TextHelper.isNull(adeIndice)) {
            defineValorClausulaNomeada("adeIndice", adeIndice, query);
        }
        if (adeStatus != null && !adeStatus.isEmpty()) {
            defineValorClausulaNomeada("adeStatus", adeStatus, query);
        }
        if (cnvStatus != null && !cnvStatus.isEmpty()) {
            defineValorClausulaNomeada("cnvStatus", cnvStatus, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_IDENTIFICADOR,
                Columns.ADE_VLR,
                Columns.ADE_PRAZO,
                Columns.ADE_COR_CODIGO,
                Columns.ADE_TIPO_VLR,
                Columns.ADE_INT_FOLHA,
                Columns.ADE_INC_MARGEM,
                Columns.ADE_PERIODICIDADE,
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.CNV_CSA_CODIGO,
                Columns.CNV_CODIGO
        };
    }

}
