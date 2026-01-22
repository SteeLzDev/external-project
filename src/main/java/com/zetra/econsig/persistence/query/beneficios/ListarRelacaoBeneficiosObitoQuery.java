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
 * <p>Title: ListarRelacaoBeneficiosObitoQuery</p>
 * <p>Description: Listagem de relação de benefícios para cancelamento por óbito</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision: 29280 $
 * $Date: 2020-04-08 11:26:58 -0300 (qua, 08 abr 2020) $
 */
public class ListarRelacaoBeneficiosObitoQuery extends HQuery {

    public String serCodigo;
    public String bfcCodigo;
    public String nseCodigo;
    public String rseCodigo;
    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();
        List<String> statusInativos = new ArrayList<String>();
        statusInativos.addAll(CodedValues.SAD_CODIGOS_INATIVOS);

        corpo.append("SELECT distinct cbe.cbeNumero, "
            		+ "nse.nseDescricao, "
            		+ "ben.benCodigoPlano, "
            		+ "cbe.cbeDataInicioVigencia, "
            		+ "cbe.cbeDataFimVigencia, "
                    + "ben.benDescricao, "
                    + "bfc.bfcNome, "
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
                    + "bfc.bfcDataNascimento ");

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

        if(!TextHelper.isNull(bfcCodigo)) {
        	corpo.append("AND bfc.bfcCodigo = :bfcCodigo ");
        }

        corpo.append("AND tnt.tntCodigo in (:tntCodigo) ");
        corpo.append("AND scb.scbCodigo != :scbCodigo ");

        corpo.append("ORDER BY tib.tibCodigo ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if(!TextHelper.isNull(serCodigo)) {
            defineValorClausulaNomeada("serCodigo", serCodigo, query);
        } else {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        defineValorClausulaNomeada("sbeCodigo", StatusBeneficiarioEnum.EXCLUIDO.sbeCodigo, query);
        defineValorClausulaNomeada("scbCodigo", StatusContratoBeneficioEnum.CANCELADO.getCodigo(), query);

        if(!TextHelper.isNull(bfcCodigo)) {
        	defineValorClausulaNomeada("bfcCodigo", bfcCodigo, query);
        }

        defineValorClausulaNomeada("tntCodigo", CodedValues.TNT_BENEFICIO_MENSALIDADE, query);

        return query;
    }

    @Override
    protected String[] getFields() {
            return new String[] {
            		Columns.CBE_NUMERO,
                    Columns.NSE_DESCRICAO,
                    Columns.BEN_CODIGO,
                    Columns.CBE_DATA_INICIO_VIGENCIA,
                    Columns.CBE_DATA_FIM_VIGENCIA,
                    Columns.BEN_DESCRICAO,
                    Columns.BFC_NOME,
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
                    Columns.BFC_DATA_NASCIMENTO
            };
        }
}
