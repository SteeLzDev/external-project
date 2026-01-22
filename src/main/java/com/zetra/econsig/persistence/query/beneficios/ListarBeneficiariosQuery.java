package com.zetra.econsig.persistence.query.beneficios;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusBeneficiarioEnum;

public class ListarBeneficiariosQuery extends HQuery {

    public String rseCodigo = null;
    public String rseMatricula = null;
    public String bfcCpf = null;
    public String cbeNumero = null;
    public String filtro = null;
    public String filtro_tipo = null;

    public String tibCodigo = null;

    //Executar count ao invez da query
    public Boolean count = false;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();

        if(!count) {
            corpo.append("SELECT distinct tib.tibDescricao, "
                    + "tib.tibCodigo, "
                    + "bfc.bfcOrdemDependencia, "
                    + "bfc.bfcNome, "
                    + "bfc.bfcCpf, "
                    + "bfc.bfcDataNascimento, "
                    + "grp.grpDescricao, "
                    + "bfc.bfcTelefone, "
                    + "bfc.bfcCelular, "
                    + "bfc.bfcCodigo, "
                    + "bfc.statusBeneficiario.sbeDescricao, "
                    + "bfc.bfcIdentificador, "
                    + "bfc.bfcClassificacao " );
        }else {
            corpo.append("SELECT count(*) as total ");
        }
        corpo.append("FROM RegistroServidor rse ");
        corpo.append("INNER JOIN rse.servidor ser ");
        corpo.append("INNER JOIN ser.beneficiarioSet bfc ");
        if (!TextHelper.isNull(cbeNumero)) {
            corpo.append("INNER JOIN bfc.contratoBeneficioSet cbe ");
        }
        corpo.append("INNER JOIN bfc.tipoBeneficiario tib ");
        corpo.append("LEFT JOIN bfc.grauParentesco grp ");
        corpo.append("WHERE bfc.statusBeneficiario.sbeCodigo != :sbeCodigo ");
        if (!TextHelper.isNull(rseCodigo)) {
            corpo.append("AND (bfc.rseCodigo = :rseCodigo OR rse.rseCodigo = :rseCodigo ) ");
        }
        if (!TextHelper.isNull(tibCodigo)) {
            corpo.append("AND tib.tibCodigo = :tibCodigo ");
        }
        if (!TextHelper.isNull(rseMatricula)) {
            corpo.append("AND rse.rseMatricula = :rseMatricula ");
        }
        if (!TextHelper.isNull(bfcCpf)) {
            corpo.append("AND bfc.bfcCpf = :bfcCpf ");
        }
        if (!TextHelper.isNull(cbeNumero)) {
            corpo.append("AND cbe.cbeNumero = :cbeNumero ");
        }

        corpo.append("ORDER BY bfc.bfcOrdemDependencia ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        defineValorClausulaNomeada("sbeCodigo", StatusBeneficiarioEnum.EXCLUIDO.sbeCodigo, query);

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        if (!TextHelper.isNull(tibCodigo)) {
            defineValorClausulaNomeada("tibCodigo", tibCodigo, query);
        }

        if (!TextHelper.isNull(rseMatricula)) {
            defineValorClausulaNomeada("rseMatricula", rseMatricula, query);
        }

        if (!TextHelper.isNull(bfcCpf)) {
            defineValorClausulaNomeada("bfcCpf", bfcCpf, query);
        }

        if (!TextHelper.isNull(cbeNumero)) {
            defineValorClausulaNomeada("cbeNumero", cbeNumero, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TIB_DESCRICAO,
                Columns.TIB_CODIGO,
                Columns.BFC_ORDEM_DEPENDENCIA,
                Columns.BFC_NOME,
                Columns.BFC_CPF,
                Columns.BFC_DATA_NASCIMENTO,
                Columns.GRP_DESCRICAO,
                Columns.BFC_TELEFONE,
                Columns.BFC_CELULAR,
                Columns.BFC_CODIGO,
                Columns.SBE_DESCRICAO,
                Columns.BFC_IDENTIFICADOR,
                Columns.BFC_CLASSIFICACAO
        };
    }
}
