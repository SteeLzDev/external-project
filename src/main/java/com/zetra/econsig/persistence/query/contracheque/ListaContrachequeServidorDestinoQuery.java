package com.zetra.econsig.persistence.query.contracheque;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.Columns;

public class ListaContrachequeServidorDestinoQuery extends HNativeQuery {

	public String rseCodigo;
	public String rseCodigoNovo;

	@Override
	public Query<Object[]> preparar(Session session) throws HQueryException {
		StringBuilder corpoBuilder = new StringBuilder();
		corpoBuilder.append("select ccq.ccq_periodo, ccq.ccq_data_carga, ccq.ccq_texto");
		corpoBuilder.append(" from tb_contracheque_registro_ser ccq");
		corpoBuilder.append(" join tb_relacionamento_registro_ser rrs on (ccq.rse_codigo = rrs.rse_codigo_origem)");
		corpoBuilder.append(" where ccq.rse_codigo" + criaClausulaNomeada("rseCodigo", rseCodigo));
		corpoBuilder.append(" and rrs.rse_codigo_destino" + criaClausulaNomeada("rseCodigoNovo", rseCodigoNovo));
		corpoBuilder.append(" and ccq.ccq_periodo not in(select ccq_periodo from tb_contracheque_registro_ser ccq2 where ccq2.rse_codigo" + criaClausulaNomeada("rseCodigoNovo", rseCodigoNovo) +" and ccq2.ccq_periodo = ccq.ccq_periodo)");

		corpoBuilder.append(" order by ccq.ccq_periodo DESC");

		Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());        
		
		defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
		defineValorClausulaNomeada("rseCodigoNovo", rseCodigoNovo, query);

		return query;
	}

	@Override
	protected String[] getFields() {
		return new String[] {
				Columns.CCQ_PERIODO,
				Columns.CCQ_DATA_CARGA,
				Columns.CCQ_TEXTO
		};
	}

	@Override
	public void setCriterios(TransferObject criterio) {
	}

}
