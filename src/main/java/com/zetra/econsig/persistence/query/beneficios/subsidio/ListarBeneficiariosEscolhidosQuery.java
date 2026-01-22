package com.zetra.econsig.persistence.query.beneficios.subsidio;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarBeneficiariosEscolhidosQuery</p>
 * <p>Description: Lista de beneficiários selecionados para simulação de cálculo de benefícios</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarBeneficiariosEscolhidosQuery extends HNativeQuery {

    public List<String> bfcCodigos;
    public String rseCodigo;
    public List<String> notScbCodigos;
    public String nseCodigo;
    public List<String> sbeCodigos;
    public String benCodigo;

    @Override
    public void setCriterios(TransferObject criterio) {
    }

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {

        boolean buscaValorContrato = false;
        boolean somenteContratoAtivo = false;
        if (notScbCodigos != null && nseCodigo != null) {
            buscaValorContrato = true;
            if (benCodigo != null) {
                somenteContratoAtivo = true;
            }
        }

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT bfc.bfc_codigo, ");
        corpoBuilder.append("bfc.bfc_nome,");
        corpoBuilder.append("bfc.bfc_data_nascimento,");
        corpoBuilder.append("tib.tib_codigo,");
        corpoBuilder.append("bfc.bfc_subsidio_concedido,");
        corpoBuilder.append("to_numeric(bfc.bfc_ordem_dependencia),");
        corpoBuilder.append("bfc.grp_codigo,");
        corpoBuilder.append("bfc.bfc_cpf, ");
        corpoBuilder.append("tib.tib_descricao, ");
        corpoBuilder.append("bfc.mde_codigo ");

        if (buscaValorContrato) {
            corpoBuilder.append(" , cbe.cbe_valor_total ");
            corpoBuilder.append(" , cbe.cbe_valor_subsidio ");
        }

        corpoBuilder.append("FROM tb_beneficiario bfc ");
        corpoBuilder.append("inner join tb_tipo_beneficiario tib ON bfc.tib_codigo = tib.tib_codigo ");

        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append("inner join tb_servidor ser ON ser.ser_codigo = bfc.ser_codigo ");
            corpoBuilder.append("inner join tb_registro_servidor rse ON ser.ser_codigo = rse.ser_codigo ");
        }

        if (buscaValorContrato) {
            corpoBuilder.append(" left join tb_contrato_beneficio cbe ON bfc.bfc_codigo = cbe.bfc_codigo ");
            corpoBuilder.append(" AND ( ");
            corpoBuilder.append(" cbe.scb_codigo NOT IN(:scbCodigos) ");
            //quando existe plano ativo, para o caso de reativação só posso listar os beneficiário que tiveram contratos cancelados do mesmo beneficio
            // que está sendo reativado e não todos os beneficiários que estão cancelados.
            if (!somenteContratoAtivo) {
                corpoBuilder.append(" AND cbe.ben_codigo IN (SELECT ben.ben_codigo FROM tb_beneficio ben WHERE ben.nse_codigo = :nseCodigo)) ");
            } else {
                corpoBuilder.append(" AND cbe.ben_codigo IN (SELECT ben.ben_codigo FROM tb_beneficio ben WHERE ben.ben_codigo = :benCodigo)) ");
            }
        }

        corpoBuilder.append("WHERE 1 = 1 ");

        if (bfcCodigos != null) {
            corpoBuilder.append("AND bfc.bfc_codigo in (:bfcCodigos) ");
        }

        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append("AND rse.rse_codigo = :rseCodigo ");
        }

        if (sbeCodigos != null) {
            corpoBuilder.append("AND bfc.sbe_codigo in (:sbeCodigos) ");
        }

        corpoBuilder.append("AND bfc.bfc_data_obito is null ");

        corpoBuilder.append("ORDER BY bfc.bfc_ordem_dependencia, tib.tib_codigo, bfc.bfc_nome");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (bfcCodigos != null) {
            defineValorClausulaNomeada("bfcCodigos", bfcCodigos, query);
        }

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        if (buscaValorContrato) {
            defineValorClausulaNomeada("scbCodigos", notScbCodigos, query);
            if (somenteContratoAtivo) {
                defineValorClausulaNomeada("benCodigo", benCodigo, query);
            } else {
                defineValorClausulaNomeada("nseCodigo", nseCodigo, query);
            }
        }

        if (sbeCodigos != null) {
            defineValorClausulaNomeada("sbeCodigos", sbeCodigos, query);
        }
        return query;
    }

    @Override
    protected String[] getFields() {

        List<String> campos = new ArrayList<String>();
        campos.add(Columns.BFC_CODIGO);
        campos.add(Columns.BFC_NOME);
        campos.add(Columns.BFC_DATA_NASCIMENTO);
        campos.add(Columns.TIB_CODIGO);
        campos.add(Columns.BFC_SUBSIDIO_CONCEDIDO);
        campos.add(Columns.BFC_ORDEM_DEPENDENCIA);
        campos.add(Columns.BFC_GRP_CODIGO);
        campos.add(Columns.BFC_CPF);
        campos.add(Columns.TIB_DESCRICAO);
        campos.add(Columns.MDE_CODIGO);

        if (notScbCodigos != null && nseCodigo != null) {
            campos.add(Columns.CBE_VALOR_TOTAL);
            campos.add(Columns.CBE_VALOR_SUBSIDIO);
        }

        return campos.toArray(new String[campos.size()]);
    }

}
