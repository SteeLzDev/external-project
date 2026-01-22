package com.zetra.econsig.persistence.query.beneficios;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusBeneficiarioEnum;
import com.zetra.econsig.values.StatusContratoBeneficioEnum;

/**
 * <p>Title: ListarRelacaoBeneficiosQuery</p>
 * <p>Description: Listagem de relação de benefícios</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarRelacaoBeneficiosQuery extends HQuery {

    public String serCodigo;
    public String benCodigo;
    public boolean reativar = false;
    public String bfcCodigo;
    public Boolean contratosAtivos;
    public String nseCodigo;
    public String rseCodigo;
    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();
        List<String> statusInativos = new ArrayList<>();
        statusInativos.addAll(CodedValues.SAD_CODIGOS_INATIVOS);

        if(TextHelper.isNull(benCodigo)) {
            corpo.append("SELECT distinct ben.benCodigo, "
                    + "ben.benDescricao, "
                    + "csa.csaNome, "
                    + "nse.nseDescricao,"
                    + "scb.scbCodigo, "
                    + "nse.nseCodigo ");
        } else {
            corpo.append("SELECT distinct cbe.cbeNumero, "
            		+ "nse.nseDescricao, "
            		+ "ben.benCodigoPlano, "
            		+ "cbe.cbeDataInicioVigencia, "
            		+ "cbe.cbeDataFimVigencia, "
                    + "ben.benDescricao, "
                    + "bfc.bfcNome, "
                    + "bfc.bfcCpf, "
                    + "tib.tibDescricao, "
                    + "csa.csaNome, "
                    + "ben.benCodigoContrato, "
                    + "ben.benCodigoRegistro, "
                    + "cbe.cbeValorSubsidio, "
                    + "cbe.cbeValorTotal, "
                    + "aut.adeVlr,"
                    + "bfc.bfcCodigo, "
                    + "cbe.cbeCodigo,"
                    + "tib.tibCodigo,"
                    + "scb.scbCodigo,"
                    + "scb.scbDescricao,"
                    + "sad.sadCodigo, "
                    + "grp.grpDescricao, "
                    + "bfc.bfcSubsidioConcedido, "
                    + "bfc.bfcDataNascimento ,"
                    + "bfc.bfcEstadoCivil, "
                    + "bfc.nacionalidade.nacCodigo, "
                    + "bfc.bfcCelular, "
                    + "bfc.bfcNomeMae, "
                    + "bfc.bfcSexo, "
                    + "bfc.bfcTelefone, "
                    + "bfc.bfcIdentificador,"
                    + "aut.adeCodigo ");
        }

        corpo.append("FROM AutDesconto aut ");
        corpo.append("INNER JOIN aut.registroServidor rse ");
        corpo.append("INNER JOIN rse.servidor ser ");
        corpo.append("INNER JOIN aut.tipoLancamento tla ");
        corpo.append("INNER JOIN aut.contratoBeneficio cbe ");
        corpo.append("INNER JOIN cbe.beneficio ben ");
        corpo.append("INNER JOIN cbe.beneficiario bfc ");
        corpo.append("INNER JOIN ben.naturezaServico nse ");
        corpo.append("INNER JOIN bfc.tipoBeneficiario tib ");
        corpo.append("INNER JOIN tla.tipoNatureza tnt ");
        corpo.append("INNER JOIN aut.statusAutorizacaoDesconto sad ");
        corpo.append("INNER JOIN ben.consignataria csa ");
        corpo.append("INNER JOIN cbe.statusContratoBeneficio scb ");
        corpo.append("LEFT OUTER JOIN bfc.grauParentesco grp ");

        corpo.append("WHERE 1 = 1 ");


        if (!TextHelper.isNull(serCodigo)) {
            corpo.append("AND ser.serCodigo = :serCodigo ");
        } else {
            corpo.append("AND rse.rseCodigo = :rseCodigo ");
        }
        corpo.append("AND bfc.statusBeneficiario.sbeCodigo != :sbeCodigo ");

        if (TextHelper.isNull(benCodigo)) {
            corpo.append("AND tib.tibCodigo = :tibCodigo ");
        }

        if (!TextHelper.isNull(bfcCodigo)) {
            corpo.append("AND bfc.bfcCodigo = :bfcCodigo ");
        }

        corpo.append("AND tnt.tntCodigo in (:tntCodigo) ");

        if (contratosAtivos != null) {
            if (contratosAtivos) {
                corpo.append("AND scb.scbCodigo != :scbCodigo ");
            } else {
                corpo.append("AND scb.scbCodigo = :scbCodigo ");
            }
        }

        if (!TextHelper.isNull(nseCodigo)) {
            corpo.append("AND ben.naturezaServico.nseCodigo = :nseCodigo ");
        }

        if (reativar) {
            corpo.append("AND aut.statusAutorizacaoDesconto.sadCodigo ").append(criaClausulaNomeada("inativos", statusInativos));
            corpo.append("AND NULLIF(TRIM(cbe.cbeNumero), '') IS NOT NULL ");
            corpo.append("AND (cbe.cbeDataFimVigencia is not null or cbe.cbeDataCancelamento is not null) ");
            corpo.append("AND NOT EXISTS (SELECT 1 FROM ContratoBeneficio cbe1 WHERE cbe1.beneficiario.bfcCodigo = bfc.bfcCodigo ");
            corpo.append(" AND cbe1.beneficio.benCodigo = ben.benCodigo AND cbe1.statusContratoBeneficio.scbCodigo =: scbCodigoAtivo ) ");
        }

        if (TextHelper.isNull(benCodigo)) {
            corpo.append("ORDER BY ben.benDescricao ");
        } else {
            corpo.append("AND ben.benCodigo = :benCodigo ");
            corpo.append("ORDER BY tib.tibCodigo ");
        }

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (!TextHelper.isNull(serCodigo)) {
            defineValorClausulaNomeada("serCodigo", serCodigo, query);
        } else {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        defineValorClausulaNomeada("sbeCodigo", StatusBeneficiarioEnum.EXCLUIDO.sbeCodigo, query);

        if (contratosAtivos != null) {
            defineValorClausulaNomeada("scbCodigo", StatusContratoBeneficioEnum.CANCELADO.getCodigo(), query);
        }

        if (TextHelper.isNull(benCodigo)) {
            defineValorClausulaNomeada("tibCodigo", CodedValues.TIB_TITULAR, query);
        }

        if (!TextHelper.isNull(bfcCodigo)) {
            defineValorClausulaNomeada("bfcCodigo", bfcCodigo, query);
        }

        if (!TextHelper.isNull(nseCodigo)) {
            defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
        }

        defineValorClausulaNomeada("tntCodigo", CodedValues.TNT_BENEFICIO_MENSALIDADE, query);

        if (!TextHelper.isNull(benCodigo)) {
            defineValorClausulaNomeada("benCodigo", benCodigo, query);
        }

        if (reativar) {
            defineValorClausulaNomeada("inativos", statusInativos, query);
            defineValorClausulaNomeada("scbCodigoAtivo", StatusContratoBeneficioEnum.ATIVO.getCodigo(), query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        if(TextHelper.isNull(benCodigo)) {
            return new String[] {
                    Columns.BEN_CODIGO,
                    Columns.BEN_DESCRICAO,
                    Columns.CSA_NOME,
                    Columns.NSE_DESCRICAO,
                    Columns.SCB_CODIGO,
                    Columns.NSE_CODIGO

            };
        } else {
            return new String[] {
            		Columns.CBE_NUMERO,
                    Columns.NSE_DESCRICAO,
                    Columns.BEN_CODIGO,
                    Columns.CBE_DATA_INICIO_VIGENCIA,
                    Columns.CBE_DATA_FIM_VIGENCIA,
                    Columns.BEN_DESCRICAO,
                    Columns.BFC_NOME,
                    Columns.BFC_CPF,
                    Columns.TIB_DESCRICAO,
                    Columns.CSA_NOME,
                    Columns.BEN_CODIGO_CONTRATO,
                    Columns.BEN_CODIGO_REGISTRO,
                    Columns.CBE_VALOR_SUBSIDIO,
                    Columns.CBE_VALOR_TOTAL,
                    Columns.ADE_VLR,
                    Columns.BFC_CODIGO,
                    Columns.CBE_CODIGO,
                    Columns.TIB_CODIGO,
                    Columns.SCB_CODIGO,
                    Columns.SCB_DESCRICAO,
                    Columns.SAD_CODIGO,
                    Columns.GRP_DESCRICAO,
                    Columns.BFC_SUBSIDIO_CONCEDIDO,
                    Columns.BFC_DATA_NASCIMENTO,
                    Columns.BFC_ESTADO_CIVIL,
                    Columns.BFC_NAC_CODIGO,
                    Columns.BFC_CELULAR,
                    Columns.BFC_NOME_MAE,
                    Columns.BFC_SEXO,
                    Columns.BFC_TELEFONE,
                    Columns.BFC_IDENTIFICADOR,
                    Columns.ADE_CODIGO
            };
        }
    }
}
