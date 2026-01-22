package com.zetra.econsig.persistence.query.beneficios.contrato;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoBeneficiarioEnum;


/**
 * <p>Title: ListaContratosBeneficiosRelacionamentoMigracaoOrigem</p>
 * <p>Description: Lista os Contratos Beneficio de origem em uma renegociação.</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaContratosBeneficiosRelacionamentoMigracaoOrigemQuery extends HNativeQuery {

    public String cbeCodigo;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select cbeAntigos.cbe_codigo, bfcAntigos.tib_codigo ");
        corpoBuilder.append("from tb_contrato_beneficio cbeNovos ");
        corpoBuilder.append("inner join tb_beneficiario bfcNovos on (cbeNovos.bfc_codigo = bfcNovos.bfc_codigo) ");
        corpoBuilder.append("inner join tb_beneficiario bfcAntigos on (bfcNovos.ser_codigo = bfcAntigos.ser_codigo) ");
        corpoBuilder.append("inner join tb_aut_desconto adeNovos on (cbeNovos.cbe_codigo = adeNovos.cbe_codigo) ");
        corpoBuilder.append("inner join tb_relacionamento_autorizacao rel on (rel.ade_codigo_destino = adeNovos.ade_codigo and rel.tnt_codigo = '").append(CodedValues.TNT_CONTROLE_MIGRACAO_BENEFICIO).append("') ");
        corpoBuilder.append("inner join tb_aut_desconto adePonte on (rel.ade_codigo_origem = adePonte.ade_codigo) ");
        corpoBuilder.append("inner join tb_contrato_beneficio cbePonte on (adePonte.cbe_codigo = cbePonte.cbe_codigo) ");
        corpoBuilder.append("inner join tb_contrato_beneficio cbeAntigos on (cbeAntigos.bfc_codigo = bfcAntigos.bfc_codigo and cbeAntigos.ben_codigo = cbePonte.ben_codigo) ");
        corpoBuilder.append("where 1 = 1 ");

        corpoBuilder.append("and cbeNovos.cbe_codigo = :cbeCodigo ");
        corpoBuilder.append("AND adePonte.sad_codigo in (:sadCodigos) ");
        corpoBuilder.append("AND bfcNovos.tib_codigo = :tibCodigo ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("sadCodigos", CodedValues.SAD_CODIGOS_INCLUSAO_PARCELA, query);
        defineValorClausulaNomeada("tibCodigo", TipoBeneficiarioEnum.TITULAR.tibCodigo, query);
        defineValorClausulaNomeada("cbeCodigo", cbeCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CBE_CODIGO,
                Columns.TIB_CODIGO
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }

}
