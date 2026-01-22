package com.zetra.econsig.persistence.query.consignacao;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoSemOcaNoPeriodoQuery.java</p>
 * <p>Description: Lista consignações que NÃO possuam ocorrências de autorização dos tocCodigos dados no período dado.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoSemOcaNoPeriodoQuery extends HQuery {
		
    public String csaCodigo;
    
    public List<String> tocCodigos;

    public boolean count;

    public Date ocaPeriodo;
    
    public boolean sum;
    
    public List<String> sadCodigos;

	@Override
	protected Query<Object[]> preparar(Session session) throws HQueryException {
		if (tocCodigos == null || tocCodigos.isEmpty()) {
			throw new HQueryException("mensagem.registrar.ocorrencia.contrato.motivo.operacao.obrigatorio", AcessoSistema.getAcessoUsuarioSistema());
		}
		
		StringBuilder corpoBuilder = new StringBuilder();

        if (count) {
            corpoBuilder.append("select count(*) as total ");
        } else if (sum) {
        	corpoBuilder.append("select sum(ade.adeVlr) ");
        }else {
        	corpoBuilder.append(" select ade.adeNumero, ade.adeVlr, sad.sadCodigo, ");
        	corpoBuilder.append(" ser.serCpf, ade.adeCodigo, svc.svcCodigo");
        }

        corpoBuilder.append(" from AutDesconto ade ");        
        corpoBuilder.append(" inner join ade.verbaConvenio vco ");
        corpoBuilder.append(" inner join ade.statusAutorizacaoDesconto sad ");        
        corpoBuilder.append(" inner join vco.convenio cnv ");
        corpoBuilder.append(" inner join ade.registroServidor.servidor ser");
        corpoBuilder.append(" inner join cnv.servico svc ");        
        corpoBuilder.append(" where not exists (select 1 from OcorrenciaAutorizacao oca where oca.adeCodigo = ade.adeCodigo ");     
        corpoBuilder.append(" and oca.tipoOcorrencia.tocCodigo ").append(criaClausulaNomeada("tocCodigos", tocCodigos));
        corpoBuilder.append(" and oca.ocaPeriodo ").append(criaClausulaNomeada("ocaPeriodo", ocaPeriodo));        
        corpoBuilder.append(")");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND cnv.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }
                        
        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            corpoBuilder.append(" and sad.sadCodigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));
        }
            
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
                        
        if (sadCodigos != null && !sadCodigos.isEmpty()) {
            defineValorClausulaNomeada("sadCodigos", sadCodigos, query);
        }
        
        if (tocCodigos != null && !tocCodigos.isEmpty()) {
            defineValorClausulaNomeada("tocCodigos", tocCodigos, query);
        }
        
        if (ocaPeriodo != null) {
            defineValorClausulaNomeada("ocaPeriodo", ocaPeriodo, query);            
        }

        return query;
	}
	
	@Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_NUMERO,                
                Columns.ADE_VLR,
                Columns.SAD_CODIGO,                
                Columns.SER_CPF,
                Columns.ADE_CODIGO,
                Columns.SVC_CODIGO
        };
    }

}
