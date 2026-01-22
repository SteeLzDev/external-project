package com.zetra.econsig.persistence.query.reclamacao;

import java.text.ParseException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.persistence.query.servidor.ListaServidorQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaReclamacaoRegistroServidorQuery</p>
 * <p>Description: Lista de reclamações de servidores.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaReclamacaoRegistroServidorQuery extends HQuery {

    public String rrsCodigo;
    public String rseCodigo;
    public String csaCodigo;
    public String serCpf;
    public String serCodigo;
    public String periodoIni;
    public String periodoFim;
    public String rseMatricula;
    public List<String> tmrCodigos;

    public boolean count = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        String corpo = "";

        if (count) {
            corpo =
                "select count(*) as total ";
        } else {
            corpo =
                "select rrs.rrsCodigo, " +
                "   rse.rseCodigo, " +
                "   rse.rseMatricula, " +
                "   ser.serNome, " +
                "   csa.csaNomeAbrev, " +
                "   csa.csaNome, " +
                "   rrs.rrsData, " +
                "   rrs.rrsTexto, " +
                "   rrs.rrsIpAcesso ";
        }
        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append("from ReclamacaoRegistroSer rrs ");
        corpoBuilder.append("inner join rrs.registroServidor rse ");
        corpoBuilder.append("inner join rrs.consignataria csa ");
        corpoBuilder.append("inner join rse.servidor ser ");
        corpoBuilder.append("where 1 = 1 ");
        corpoBuilder.append(ListaServidorQuery.gerarClausulaMatriculaCpf(rseMatricula, serCpf, false));

        if (!TextHelper.isNull(rrsCodigo)) {
            corpoBuilder.append(" and rrs.rrsCodigo ").append(criaClausulaNomeada("rrsCodigo", rrsCodigo));
        }

        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append(" and rse.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(serCodigo)) {
            corpoBuilder.append(" AND ser.serCodigo ").append(criaClausulaNomeada("serCodigo", serCodigo));
        }

        if (tmrCodigos != null && !tmrCodigos.isEmpty()) {
            corpoBuilder.append(" AND exists (select 1 from rrs.reclamacaoMotivoSet rmo ");
            corpoBuilder.append(" WHERE rmo.tmrCodigo ").append(criaClausulaNomeada("tmrCodigos", tmrCodigos));
            corpoBuilder.append(" )");
        }

        if (!TextHelper.isNull(periodoIni)) {
            corpoBuilder.append(" and rrs.rrsData >= :periodoIni");
        }
        if (!TextHelper.isNull(periodoFim)) {
            corpoBuilder.append(" and rrs.rrsData <= :periodoFim");
        }

        if (!count) {
            corpoBuilder.append(" order by rrs.rrsData desc, csa.csaNomeAbrev");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        ListaServidorQuery.definirClausulaMatriculaCpf(rseMatricula, serCpf, false, query);

        // Seta os parâmetros na query
        if (!TextHelper.isNull(rrsCodigo)) {
            defineValorClausulaNomeada("rrsCodigo", rrsCodigo, query);
        }

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(serCodigo)) {
            defineValorClausulaNomeada("serCodigo", serCodigo, query);
        }

        if (tmrCodigos != null && !tmrCodigos.isEmpty()) {
            defineValorClausulaNomeada("tmrCodigos", tmrCodigos, query);
        }

        if (!TextHelper.isNull(periodoIni)) {
            try {
                defineValorClausulaNomeada("periodoIni", DateHelper.parse(periodoIni + " 00:00:00", LocaleHelper.getDateTimePattern()), query);
            } catch (ParseException ex) {
                throw new HQueryException("mensagem.erro.data.inicio.informada.invalida.arg0", (AcessoSistema) null, periodoIni);
            }
        }
        if (!TextHelper.isNull(periodoFim)) {
            try {
                defineValorClausulaNomeada("periodoFim", DateHelper.parse(periodoFim + " 23:59:59", LocaleHelper.getDateTimePattern()), query);
            } catch (ParseException ex) {
                throw new HQueryException("mensagem.erro.data.fim.informada.invalida.arg0", (AcessoSistema) null, periodoFim);
            }
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String [] {
                Columns.RRS_CODIGO,
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.SER_NOME,
                Columns.CSA_NOME_ABREV,
                Columns.CSA_NOME,
                Columns.RRS_DATA,
                Columns.RRS_TEXTO,
                Columns.RRS_IP_ACESSO
        };
    }
}
