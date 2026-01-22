package com.zetra.econsig.persistence.query.sdp.permissionario;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaPermissionarioQuery</p>
 * <p>Description: Listagem de permissionários</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaPermissionarioQuery extends HQuery {

    public boolean count = false;

    public boolean retornaPrmExcluido = false;

    public String prmCodigo;
    public String rseCodigo;
    public String rseMatricula;
    public String serCpf;
    public String serNome;
    public String csaCodigo;
    public String echCodigo;
    public String echDescricao;
    public String endereco;
    public String posCodigo;
    public Date decDataRetroativa;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        List<Short> status = new ArrayList<>();
        status.add(CodedValues.STS_ATIVO);
        status.add(CodedValues.STS_INATIVO);
        if (retornaPrmExcluido || !TextHelper.isNull(decDataRetroativa)) {
            status.add(CodedValues.STS_INDISP);
        }

        String corpo = "";
        if (count) {
            corpo = "SELECT COUNT(DISTINCT prm.prmCodigo) AS TOTAL ";
        } else {
            corpo = "select " +
                    "prm.prmCodigo, " +
                    "rse.rseCodigo, " +
                    "rse.rseMatricula, " +
                    "org.orgCodigo, " +
                    "ser.serCpf, " +
                    "ser.serNome, " +
                    "srs.srsCodigo, " +
                    "srs.srsDescricao, " +
                    "trs.trsCodigo, " +
                    "trs.trsDescricao, " +
                    "pos.posCodigo, " +
                    "pos.posDescricao, " +
                    "csa.csaCodigo, " +
                    "csa.csaNome, " +
                    "ech.echCodigo, " +
                    "ech.echDescricao, " +
                    "prm.prmTelefone, " +
                    "prm.prmEmail, " +
                    "prm.prmComplEndereco, " +
                    "prm.prmDataCadastro, " +
                    "prm.prmDataOcupacao, " +
                    "prm.prmDataDesocupacao, " +
                    "prm.prmEmTransferencia, " +
                    "prm.prmAtivo";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" FROM Permissionario prm");
        corpoBuilder.append(" INNER JOIN prm.consignataria csa");
        corpoBuilder.append(" INNER JOIN prm.enderecoConjHabitacional ech");
        corpoBuilder.append(" INNER JOIN prm.registroServidor rse");
        corpoBuilder.append(" INNER JOIN rse.orgao org");
        corpoBuilder.append(" INNER JOIN org.estabelecimento est");
        corpoBuilder.append(" INNER JOIN rse.servidor ser");
        corpoBuilder.append(" INNER JOIN rse.statusRegistroServidor srs");
        corpoBuilder.append(" LEFT OUTER JOIN rse.tipoRegistroServidor trs");
        corpoBuilder.append(" LEFT OUTER JOIN rse.postoRegistroServidor pos");
        corpoBuilder.append(" WHERE 1 = 1");

        corpoBuilder.append(" AND prm.prmAtivo ").append(criaClausulaNomeada("status", status));

        if (!TextHelper.isNull(decDataRetroativa)) {
            corpoBuilder.append("AND prm.prmDataOcupacao <= :dataOcupacao ");
            corpoBuilder.append("AND (prm.prmDataDesocupacao IS NULL ");
            corpoBuilder.append("OR (prm.prmDataDesocupacao > prm.prmDataOcupacao ");
            corpoBuilder.append("AND :dataOcupacao BETWEEN prm.prmDataOcupacao AND add_day(prm.prmDataDesocupacao,-1)) ");
            corpoBuilder.append(") ");
        }

        if (!TextHelper.isNull(prmCodigo)) {
            corpoBuilder.append(" AND prm.prmCodigo ").append(criaClausulaNomeada("prmCodigo", prmCodigo));
        }

        if (!TextHelper.isNull(posCodigo)) {
            corpoBuilder.append(" AND pos.posCodigo ").append(criaClausulaNomeada("posCodigo", posCodigo));
        }

        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append(" AND rse.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        }

        if (!TextHelper.isNull(rseMatricula)) {
            corpoBuilder.append(" AND ").append(criaClausulaNomeada("rse.rseMatricula", "rseMatricula", rseMatricula));
        }

        if (!TextHelper.isNull(serCpf)) {
            corpoBuilder.append(" AND ").append(criaClausulaNomeada("ser.serCpf", "serCpf", serCpf));
        }

        if (!TextHelper.isNull(serNome)) {
            corpoBuilder.append(" AND ").append(criaClausulaNomeada("ser.serNome", "serNome", serNome));
        }

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(echCodigo)) {
            corpoBuilder.append(" AND ech.echCodigo ").append(criaClausulaNomeada("echCodigo", echCodigo));
        }

        if (!TextHelper.isNull(echDescricao)) {
            corpoBuilder.append(" AND ").append(criaClausulaNomeada("ech.echDescricao", "echDescricao", echDescricao));
        }

        if (!TextHelper.isNull(endereco)) {
            corpoBuilder.append(" AND (").append(criaClausulaNomeada("ech.echCodigo", "endereco", endereco));
            corpoBuilder.append(" OR ").append(criaClausulaNomeada("ech.echDescricao", "endereco", endereco)).append(") ");
        }

        if (!count) {
            corpoBuilder.append(" ORDER BY rse.rseMatricula");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("status", status, query);

        if (!TextHelper.isNull(decDataRetroativa)) {
            defineValorClausulaNomeada("dataOcupacao", decDataRetroativa, query);
        }

        if (!TextHelper.isNull(prmCodigo)) {
            defineValorClausulaNomeada("prmCodigo", prmCodigo, query);
        }

        if (!TextHelper.isNull(posCodigo)) {
            defineValorClausulaNomeada("posCodigo", posCodigo, query);
        }

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        if (!TextHelper.isNull(rseMatricula)) {
            defineValorClausulaNomeada("rseMatricula", rseMatricula, query);
        }

        if (!TextHelper.isNull(serCpf)) {
            defineValorClausulaNomeada("serCpf", serCpf, query);
        }

        if (!TextHelper.isNull(serNome)) {
            defineValorClausulaNomeada("serNome", serNome, query);
        }

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(echCodigo)) {
            defineValorClausulaNomeada("echCodigo", echCodigo, query);
        }

        if (!TextHelper.isNull(echDescricao)) {
            defineValorClausulaNomeada("echDescricao", echDescricao, query);
        }

        if (!TextHelper.isNull(endereco)) {
            defineValorClausulaNomeada("endereco", endereco, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.PRM_CODIGO,
                Columns.RSE_CODIGO,
                Columns.RSE_MATRICULA,
                Columns.ORG_CODIGO,
                Columns.SER_CPF,
                Columns.SER_NOME,
                Columns.SRS_CODIGO,
                Columns.SRS_DESCRICAO,
                Columns.TRS_CODIGO,
                Columns.TRS_DESCRICAO,
                Columns.POS_CODIGO,
                Columns.POS_DESCRICAO,
                Columns.CSA_CODIGO,
                Columns.CSA_NOME,
                Columns.ECH_CODIGO,
                Columns.ECH_DESCRICAO,
                Columns.PRM_TELEFONE,
                Columns.PRM_EMAIL,
                Columns.PRM_COMPL_ENDERECO,
                Columns.PRM_DATA_CADASTRO,
                Columns.PRM_DATA_OCUPACAO,
                Columns.PRM_DATA_DESOCUPACAO,
                Columns.PRM_EM_TRANSFERENCIA,
                Columns.PRM_ATIVO
        };
    }
}
