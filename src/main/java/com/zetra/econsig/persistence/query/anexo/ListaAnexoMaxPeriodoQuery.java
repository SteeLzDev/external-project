package com.zetra.econsig.persistence.query.anexo;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaAnexoMaxPeriodoQuery</p>
 * <p>Description: Query para listagem dos anexos com maior período por contrato.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaAnexoMaxPeriodoQuery extends HQuery {

	public List<String> tarCodigos;
	public boolean count = false;

	@Override
	protected Query<Object[]> preparar(Session session) throws HQueryException {
		String corpo = "";

        if (count) {
            corpo = "select count(*) as total ";
        } else {
            corpo = "select "
            	  + "aad.id.adeCodigo, "
                  + "aad.aadNome, "
                  + "aad.aadDescricao, "
                  + "aad.aadAtivo, "
                  + "aad.aadData, "
                  + "aad.aadIpAcesso, "
                  + "aad.tipoArquivo.tarCodigo, "
                  + "aad.aadPeriodo "
                  ;
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from AnexoAutorizacaoDesconto aad");
        corpoBuilder.append(" where aad.aadPeriodo in (select max(aadMax.aadPeriodo) from AnexoAutorizacaoDesconto aadMax where aadMax.id.adeCodigo = aad.id.adeCodigo)");

        if (tarCodigos != null && !tarCodigos.isEmpty()) {
            corpoBuilder.append(" and aad.tipoArquivo.tarCodigo ").append(criaClausulaNomeada("tarCodigos", tarCodigos));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (tarCodigos != null && !tarCodigos.isEmpty()) {
            defineValorClausulaNomeada("tarCodigos", tarCodigos, query);
        }

		return query;
	}

	@Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.AAD_NOME,
                Columns.AAD_DESCRICAO,
                Columns.AAD_ATIVO,
                Columns.AAD_DATA,
                Columns.AAD_IP_ACESSO,
                Columns.TAR_CODIGO,
                Columns.AAD_PERIODO
        };
	}
}
