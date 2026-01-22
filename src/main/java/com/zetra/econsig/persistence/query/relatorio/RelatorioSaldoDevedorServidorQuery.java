package com.zetra.econsig.persistence.query.relatorio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioSaldoDevedorServidorQuery</p>
 * <p>Description: Classe da query do relatorio saldo devedor servidor
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft</p>
 * @author Eduardo Fortes
 */

public class RelatorioSaldoDevedorServidorQuery extends ReportHQuery{
    private String csaCodigo;
    private List<String> rseMatricula;
    private String dataInicio;
    private String dataFim;
    private List<String> serCpf;

    @Override
    public void setCriterios(TransferObject criterio) {
        csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
        rseMatricula = (List<String>) criterio.getAttribute(Columns.RSE_MATRICULA);
        dataInicio = (String) criterio.getAttribute("DATA_INI");
        dataFim = (String) criterio.getAttribute("DATA_FIM");
        serCpf = (List<String>) criterio.getAttribute(Columns.SER_CPF);

    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ");
        corpoBuilder.append("rse.rseMatricula as rseMatricula, ser.serCpf as serCpf, ser.serNome as serNome, org.orgIdentificador as orgIdentificador, sdr.sdrValor as sdrValor, sdr.sdrData as sdrData, csa.csaNome as csaNome ");
        corpoBuilder.append("from SaldoDevedorServidor sdr ");
        corpoBuilder.append("inner join sdr.registroServidor rse ");
        corpoBuilder.append("inner join sdr.consignataria csa ");
        corpoBuilder.append("inner join rse.orgao org ");
        corpoBuilder.append("inner join rse.servidor ser ");
        corpoBuilder.append("where 1 = 1 ");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(serCpf) && TextHelper.isNull(rseMatricula)) {
            corpoBuilder.append(" AND ser.serCpf ").append(criaClausulaNomeada("serCpf", serCpf));
        }

        if (!TextHelper.isNull(rseMatricula) && TextHelper.isNull(serCpf)) {
            corpoBuilder.append(" AND rse.rseMatricula ").append(criaClausulaNomeada("rseMatricula", rseMatricula));
        }

        if (!TextHelper.isNull(rseMatricula) && !TextHelper.isNull(serCpf)) {
            corpoBuilder.append(" AND ( rse.rseMatricula ").append(criaClausulaNomeada("rseMatricula", rseMatricula));
            corpoBuilder.append(" OR ser.serCpf ").append(criaClausulaNomeada("serCpf", serCpf)).append(") ");
        }

        if(!TextHelper.isNull(dataInicio) && TextHelper.isNull(dataFim)) {
            corpoBuilder.append(" AND sdr.sdrData >= :dataIni");
        }

        if(TextHelper.isNull(dataInicio) && !TextHelper.isNull(dataFim)) {
            corpoBuilder.append(" AND sdr.sdrData <= :dataFim");
        }

        if (!TextHelper.isNull(dataInicio) && !TextHelper.isNull(dataFim)) {
            corpoBuilder.append(" AND sdr.sdrData between :dataIni and :dataFim");
        }

        corpoBuilder.append(" order by sdr.sdrData desc, csa.csaNome, org.orgIdentificador");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(serCpf)) {
            defineValorClausulaNomeada("serCpf", serCpf, query);
        }

        if (!TextHelper.isNull(rseMatricula)) {
            defineValorClausulaNomeada("rseMatricula", rseMatricula, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(dataInicio)) {
            defineValorClausulaNomeada("dataIni", parseDateTimeString(dataInicio), query);
        }

        if (!TextHelper.isNull(dataFim)) {
            defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_MATRICULA,
                Columns.SER_CPF,
                Columns.SER_NOME,
                Columns.ORG_IDENTIFICADOR,
                Columns.SDR_VALOR,
                Columns.SDR_DATA,
                Columns.CSA_NOME
        };
    }
}
