package com.zetra.econsig.persistence.query.relatorio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.servidor.ListaServidorQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioBeneficiarioDataNascimentoQuery</p>
 * <p>Description: Query para relatório de beneficiario por data nascimento.</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioBeneficiarioDataNascimentoQuery extends ReportHQuery {

    public AcessoSistema responsavel;

    private String periodoIni;

    private String periodoFim;

    private String rseMatricula;

    private String cpf;

    private String serNome;

    private List<String> nseCodigo;

    private String benCodigo;

    private String csaCodigo;

    private List<String> scbCodigo;

    @Override
    public void setCriterios(TransferObject criterio) {
        if (criterio != null) {
            responsavel = (AcessoSistema) criterio.getAttribute("responsavel");
            periodoIni = (String) criterio.getAttribute("periodoIni");
            periodoFim = (String) criterio.getAttribute("periodoFim");
            rseMatricula = (String) criterio.getAttribute("rseMatricula");
            cpf = (String) criterio.getAttribute("cpf");
            serNome = (String) criterio.getAttribute("serNome");
            nseCodigo = (List<String>) criterio.getAttribute("nseCodigo");
            benCodigo = (String) criterio.getAttribute("benCodigo");
            csaCodigo = (String) criterio.getAttribute("csaCodigo");
            scbCodigo = (List<String>) criterio.getAttribute("scbCodigo");
        }
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" SELECT rse.rseMatricula as RSE_MATRICULA, ser.serNome as SER_NOME, ser.serCpf as SER_CPF, ");
        corpoBuilder.append(" bfc.bfcNome as BFC_NOME, bfc.bfcCpf as BFC_CPF, bfc.bfcDataNascimento as BFC_DATA_NASCIMENTO, ");
        corpoBuilder.append(" bfc.bfcNomeMae as BFC_NOME_MAE, ben.benDescricao as BEN_DESCRICAO, csa.csaNome as CSA_NOME, ");
        corpoBuilder.append(" cbe.cbeNumero as CBE_NUMERO, scb.scbDescricao as SCB_DESCRICAO ");

        corpoBuilder.append(" FROM Beneficiario bfc ");
        corpoBuilder.append(" INNER JOIN bfc.servidor ser ");
        corpoBuilder.append(" INNER JOIN ser.registroServidorSet rse");
        corpoBuilder.append(" LEFT JOIN bfc.contratoBeneficioSet cbe ");
        corpoBuilder.append(" LEFT JOIN cbe.beneficio ben ");
        corpoBuilder.append(" LEFT JOIN ben.naturezaServico nse ");
        corpoBuilder.append(" LEFT JOIN cbe.statusContratoBeneficio scb ");
        corpoBuilder.append(" LEFT JOIN ben.consignataria csa ");

        corpoBuilder.append(" WHERE ");

        corpoBuilder.append(" bfc.bfcDataNascimento >= :periodoIni");
        corpoBuilder.append(" AND bfc.bfcDataNascimento <= :periodoFim");

        corpoBuilder.append(ListaServidorQuery.gerarClausulaMatriculaCpf(rseMatricula, cpf, false));

        if (!TextHelper.isNull(serNome)) {
            corpoBuilder.append(" AND ser.serNome ").append(criaClausulaNomeada("serNome", serNome));
        }

        if (nseCodigo != null && !nseCodigo.isEmpty()) {
            corpoBuilder.append(" AND nse.nseCodigo ").append(criaClausulaNomeada("nseCodigo", nseCodigo));
        }

        if (!TextHelper.isNull(benCodigo)) {
            corpoBuilder.append(" AND ben.benCodigo ").append(criaClausulaNomeada("benCodigo", benCodigo));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (scbCodigo != null && !scbCodigo.isEmpty()) {
            corpoBuilder.append(" AND scb.scbCodigo ").append(criaClausulaNomeada("scbCodigo", scbCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("periodoIni", parseDateString(periodoIni), query);
        defineValorClausulaNomeada("periodoFim", parseDateString(periodoFim), query);

        ListaServidorQuery.definirClausulaMatriculaCpf(rseMatricula, cpf, false, query);

        if (!TextHelper.isNull(serNome)) {
            defineValorClausulaNomeada("serNome", serNome, query);
        }

        if (nseCodigo != null && !nseCodigo.isEmpty()) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }

        if (!TextHelper.isNull(benCodigo)) {
            defineValorClausulaNomeada("benCodigo", benCodigo, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (scbCodigo != null && !scbCodigo.isEmpty()) {
            defineValorClausulaNomeada("scbCodigo", scbCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.RSE_MATRICULA,
                Columns.SER_NOME,
                Columns.SER_CPF,
                Columns.BFC_NOME,
                Columns.BFC_CPF,
                Columns.BFC_DATA_NASCIMENTO,
                Columns.BFC_NOME_MAE,
                Columns.BEN_DESCRICAO,
                Columns.CSA_NOME,
                Columns.CBE_NUMERO,
                Columns.SCB_DESCRICAO
        };
    }
}
