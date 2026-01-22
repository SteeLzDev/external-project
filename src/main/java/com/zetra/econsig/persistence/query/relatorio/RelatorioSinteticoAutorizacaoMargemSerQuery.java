package com.zetra.econsig.persistence.query.relatorio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;

public class RelatorioSinteticoAutorizacaoMargemSerQuery extends ReportHQuery {

    private List<String> csaCodigos;

    @Override
    public void setCriterios(TransferObject criterio) {
        csaCodigos = (List<String>) criterio.getAttribute("CSA_CODIGO");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final StringBuilder hql = new StringBuilder();
        hql.append("SELECT ");
        hql.append("cmss.cssDataIni AS dataIni, ");
        hql.append("cmss.cssDataFim AS dataFim, ");
        hql.append("rse.servidor.serNome AS nomeServidor, ");
        hql.append("rse.servidor.serCpf AS cpfServidor, ");
        hql.append("rse.rseMatricula AS matriculaServidor, ");
        hql.append("concatenar(concatenar(csa.csaIdentificador, ' - '), csa.csaNome) AS nomeConsignataria ");
        hql.append("FROM ConsultaMargemSemSenha cmss ");

        hql.append("INNER JOIN cmss.registroServidor rse ");
        hql.append("INNER JOIN cmss.consignataria csa ");
        hql.append("WHERE cmss.cssDataFim >= CURRENT_DATE() ");
        hql.append("AND cmss.cssDataRevogacaoSer is NULL AND cmss.cssDataRevogacaoSup is NULL ");
        if ((csaCodigos != null) && !csaCodigos.isEmpty()) {
            hql.append("AND cmss.csaCodigo ").append(criaClausulaNomeada("csaCodigos", csaCodigos));
        }
        hql.append(" ORDER BY csa.csaNome");
        final Query<Object[]> query = instanciarQuery(session, hql.toString());
        if ((csaCodigos != null) && !csaCodigos.isEmpty()) {
            defineValorClausulaNomeada("csaCodigos", csaCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                              "dataIni",
                              "dataFim",
                              "nomeServidor",
                              "cpfServidor",
                              "matriculaServidor",
                              "nomeConsignataria"
        };
    }
}
