package com.zetra.econsig.persistence.query.arquivo;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarArquivoRetornoQuery</p>
 * <p>Description: Lista linhas de arquivo de retorno</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarArquivoRetornoQuery extends HQuery {

    public String nomeArquivo;
    public List<String> lstCsaIdentificador;
    public List<String> lstOrgIdentificador;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ");
        corpoBuilder.append("   art.mapeada, ");
        corpoBuilder.append("   art.processada, ");
        corpoBuilder.append("   art.linha, ");
        corpoBuilder.append("   art.nomeArquivo ");
        corpoBuilder.append(" from ArquivoRetorno art ");

        corpoBuilder.append(" where 1=1 ");

        if(!TextHelper.isNull(nomeArquivo)) {
            corpoBuilder.append(" and art.nomeArquivo ").append(criaClausulaNomeada("nomeArquivo", nomeArquivo));
        }

        corpoBuilder.append(" and ((art.cnvCodVerba is not NULL and art.cnvCodVerba in (select cnv.cnvCodVerba from Servico svc inner join svc.convenioSet cnv ");
        if(lstOrgIdentificador != null && !lstOrgIdentificador.isEmpty()) {
            corpoBuilder.append(" inner join cnv.orgao orgInner ");
        }
        if(lstCsaIdentificador != null && !lstCsaIdentificador.isEmpty()) {
            corpoBuilder.append(" inner join cnv.consignataria csaInner ");
        }
        corpoBuilder.append(" where svc.naturezaServico.nseCodigo in ('").append(CodedValues.NSE_PLANO_DE_SAUDE).append("','").append(CodedValues.NSE_PLANO_ODONTOLOGICO).append("')");
        if(lstCsaIdentificador != null && !lstCsaIdentificador.isEmpty()) {
            corpoBuilder.append(" and csaInner.csaIdentificador ").append(criaClausulaNomeada("lstCsaIdentificador", lstCsaIdentificador));
        }
        if(lstOrgIdentificador != null && !lstOrgIdentificador.isEmpty()) {
            corpoBuilder.append(" and orgInner.orgIdentificador ").append(criaClausulaNomeada("lstOrgIdentificador", lstOrgIdentificador));
        }
        corpoBuilder.append("))");

        if(lstCsaIdentificador != null && !lstCsaIdentificador.isEmpty()) {
            corpoBuilder.append(" or (art.cnvCodVerba is NULL and art.csaIdentificador ").append(criaClausulaNomeada("lstCsaIdentificador", lstCsaIdentificador));
            if(lstOrgIdentificador != null && !lstOrgIdentificador.isEmpty()) {
                corpoBuilder.append(" and art.orgIdentificador ").append(criaClausulaNomeada("lstOrgIdentificador", lstOrgIdentificador));
            }
            corpoBuilder.append(")");
        }

        if((lstCsaIdentificador == null || lstCsaIdentificador.isEmpty()) && lstOrgIdentificador != null && !lstOrgIdentificador.isEmpty()) {
            corpoBuilder.append(" or (art.cnvCodVerba is NULL and art.orgIdentificador ").append(criaClausulaNomeada("lstOrgIdentificador", lstOrgIdentificador));
            corpoBuilder.append(")");
        }
        corpoBuilder.append(")");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(nomeArquivo)){
            defineValorClausulaNomeada("nomeArquivo", nomeArquivo, query);
        }
        if (lstCsaIdentificador != null && !lstCsaIdentificador.isEmpty()) {
            defineValorClausulaNomeada("lstCsaIdentificador", lstCsaIdentificador, query);
        }
        if (lstOrgIdentificador != null && !lstOrgIdentificador.isEmpty()) {
            defineValorClausulaNomeada("lstOrgIdentificador", lstOrgIdentificador, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ART_MAPEADA,
                Columns.ART_PROCESSADA,
                Columns.ART_LINHA,
                Columns.ART_NOME_ARQUIVO
                };
    }

}
