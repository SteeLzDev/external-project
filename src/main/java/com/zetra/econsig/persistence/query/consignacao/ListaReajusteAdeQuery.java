package com.zetra.econsig.persistence.query.consignacao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaReajusteAdeQuery</p>
 * <p>Description: Listagem de </p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaReajusteAdeQuery extends HQuery  {

    private final String csaCodigo;
    private final String vlrIgual;
    private final String vlrMaiorIgual;
    private final String vlrMenorIgual;
    private final String verba;
    private final String servico;
    private String padraoVerba;
    private String padraoIndice;

    public ListaReajusteAdeQuery(CustomTransferObject regras) {
        csaCodigo     = (String) regras.getAttribute("CSA_CODIGO");
        vlrIgual      = (String) regras.getAttribute("vlr_igual");
        vlrMenorIgual = (String) regras.getAttribute("vlr_menor_igual");
        vlrMaiorIgual = (String) regras.getAttribute("vlr_maior_igual");
        verba         = (String) regras.getAttribute("verba");
        padraoVerba   = (String) regras.getAttribute("padrao_verba");
        padraoIndice  = (String) regras.getAttribute("padrao_indice");
        servico       = (String) regras.getAttribute("servico");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        //Status das ADEs para reajuste
        List<String> adeStatus = new ArrayList<>();
        adeStatus.add(CodedValues.SAD_DEFERIDA);
        adeStatus.add(CodedValues.SAD_EMANDAMENTO);

        //Status dos convenios para reajuste
        List<String> cnvStatus = new ArrayList<>();
        cnvStatus.add(CodedValues.SCV_ATIVO);

        //Status das parcelas para reajuste
        List<String> prdStatus = new ArrayList<>();
        prdStatus.add(CodedValues.SPD_EMABERTO);
        prdStatus.add(CodedValues.SPD_REJEITADAFOLHA);

        String fields = "ade.adeCodigo, ade.adeNumero, ade.adeIdentificador, ade.adeVlr, ade.adePrazo, ade.adePrdPagas, rse.rseMatricula, " +
                        "ser.serNome, ser.serCpf, ade.adeIndice, ade.adeVlrTac, ade.adeVlrIof, ade.adeVlrLiquido, ade.adeVlrMensVinc, ade.adePeriodicidade ";

        // Seleciona ADEs e servidores para alteração
        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ");
        corpoBuilder.append(fields);
        corpoBuilder.append(" FROM AutDesconto ade");
        corpoBuilder.append(" INNER JOIN ade.verbaConvenio vco");
        corpoBuilder.append(" INNER JOIN vco.convenio cnv");
        corpoBuilder.append(" INNER JOIN ade.registroServidor rse");
        corpoBuilder.append(" INNER JOIN rse.servidor ser");
        corpoBuilder.append(" WHERE ");

        corpoBuilder.append(" cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));

        if (adeStatus != null && adeStatus.size() > 0) {
            corpoBuilder.append(" AND ade.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("adeStatus", adeStatus));
        }

        if (cnvStatus.size() > 0) {
            corpoBuilder.append(" AND cnv.statusConvenio.scvCodigo ").append(criaClausulaNomeada("cnvStatus", cnvStatus));
        }

        if (!TextHelper.isNull(vlrIgual)) {
            corpoBuilder.append(" AND ade.adeVlr").append(criaClausulaNomeada("vlrIgual", vlrIgual));
        }

        if (!TextHelper.isNull(vlrMaiorIgual)) {
            corpoBuilder.append(" AND ade.adeVlr >= ").append(vlrMaiorIgual);
        }

        if (!TextHelper.isNull(vlrMenorIgual)) {
            corpoBuilder.append(" AND ade.adeVlr <= ").append(vlrMenorIgual);
        }

        if (!TextHelper.isNull(verba)) {
            corpoBuilder.append(" AND cnv.cnvCodVerba ").append(criaClausulaNomeada("verba", verba));
        }

        if (!TextHelper.isNull(servico)) {
            corpoBuilder.append(" AND cnv.servico.svcCodigo ").append(criaClausulaNomeada("servico", servico));
        }

        if (!TextHelper.isNull(padraoVerba)) {
            padraoVerba = padraoVerba.replaceAll("\\?", CodedValues.LIKE_UNICO);
            corpoBuilder.append(" AND ").append(criaClausulaNomeada("cnv.cnvCodVerba", "padraoVerba", padraoVerba));
        }

        if (!TextHelper.isNull(padraoIndice)) {
            padraoIndice = padraoIndice.replaceAll("\\?", CodedValues.LIKE_UNICO);
            corpoBuilder.append(" AND ").append(criaClausulaNomeada("ade.adeIndice", "padraoIndice", padraoIndice));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);

        if (adeStatus != null && adeStatus.size() > 0) {
            defineValorClausulaNomeada("adeStatus", adeStatus, query);
        }

        if (cnvStatus.size() > 0) {
            defineValorClausulaNomeada("cnvStatus", cnvStatus, query);
        }

        if (!TextHelper.isNull(vlrIgual)) {
           defineValorClausulaNomeada("vlrIgual", new BigDecimal(vlrIgual), query);
        }

        if (!TextHelper.isNull(verba)) {
            defineValorClausulaNomeada("verba", verba, query);
        }

        if (!TextHelper.isNull(servico)) {
            defineValorClausulaNomeada("servico", servico, query);
        }

        if (!TextHelper.isNull(padraoVerba)) {
            defineValorClausulaNomeada("padraoVerba", padraoVerba, query);
        }

        if (!TextHelper.isNull(padraoIndice)) {
            defineValorClausulaNomeada("padraoIndice", padraoIndice, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[]{
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.ADE_IDENTIFICADOR,
                Columns.ADE_VLR,
                Columns.ADE_PRAZO,
                Columns.ADE_PRD_PAGAS,
                Columns.RSE_MATRICULA,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.ADE_INDICE,
                Columns.ADE_VLR_TAC,
                Columns.ADE_VLR_IOF,
                Columns.ADE_VLR_LIQUIDO,
                Columns.ADE_VLR_MENS_VINC,
                Columns.ADE_PERIODICIDADE
        };
    }
}
