package com.zetra.econsig.persistence.query.margem;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;
import org.hibernate.Session;
import org.hibernate.query.Query;

import static com.zetra.econsig.persistence.query.servidor.ListaServidorQuery.definirClausulaMatriculaCpf;
import static com.zetra.econsig.persistence.query.servidor.ListaServidorQuery.gerarClausulaMatriculaCpf;

public class ObtemValidacaoPdfMargemQuery extends HQuery {
    public String cpf;
    public String chave;
    public String matricula;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String bean = "SELECT cdm.rseCodigo, " +
                "cdm.cdmLocalArquivo, " +
                "cdm.cdmData, " +
                "ser.serNome ";

        StringBuilder build = new StringBuilder(bean);
        build.append("FROM ControleDocumentoMargem cdm ");
        build.append("INNER JOIN cdm.registroServidor rse ");
        build.append("INNER JOIN rse.servidor ser ");
        build.append("WHERE 1=1 ");
        build.append(" AND cdm.cdmCodigoAuth ").append(criaClausulaNomeada("chave", chave));

        build.append(gerarClausulaMatriculaCpf(matricula, cpf, false));

        final Query<Object[]> query = instanciarQuery(session, build.toString());

        definirClausulaMatriculaCpf(matricula, cpf, false, query);
        defineValorClausulaNomeada("chave", chave, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[]{
                Columns.CDM_RSECODIGO,
                Columns.CDM_LOCAL_ARQUIVO,
                Columns.CDM_DATA,
                Columns.SER_NOME
        };
    }
}
