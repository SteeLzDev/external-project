package com.zetra.econsig.persistence.query.arquivo;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaParametroValidacaoArquivoQuery</p>
 * <p>Description: Listagem de parâmetros de validação de arquivo.</p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaParametroValidacaoArquivoQuery extends HQuery {

    public String tipoEntidade;
    public String codigoEntidade;
    public List<String> tvaCodigos;
    public List<String> tvaChaves;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {

        if (TextHelper.isNull(tipoEntidade) || TextHelper.isNull(codigoEntidade)) {
            throw new HQueryException("mensagem.erro.informe.tipo.codigo.entidade", (AcessoSistema) null);
        }

        if (!tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE) &&
                !tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_SUP) &&
                !tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) &&
                !tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
            throw new HQueryException("mensagem.erro.tipo.entidade.invalido", (AcessoSistema) null);
        }

        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select tva.tvaCodigo, tva.tvaChave, tva.tvaDescricao, ");

        if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_SUP)) {
            corpoBuilder.append("vac.vacValor AS VALOR, cse.cseCodigo AS CODIGO_ENTIDADE, cse.cseNome AS ENTIDADE ");
        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
            corpoBuilder.append("vao.vaoValor AS VALOR, org.orgCodigo AS CODIGO_ENTIDADE, org.orgNome AS ENTIDADE ");
        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
            corpoBuilder.append("vae.vaeValor AS VALOR, est.estCodigo AS CODIGO_ENTIDADE, est.estNome AS ENTIDADE ");
        }

        corpoBuilder.append("from TipoParamValidacaoArq tva ");

        if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_SUP)) {
            corpoBuilder.append("inner join tva.paramValidacaoArqCseSet vac ");
            corpoBuilder.append("inner join vac.consignante cse ");
        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
            corpoBuilder.append("inner join tva.paramValidacaoArqOrgSet vao ");
            corpoBuilder.append("inner join vao.orgao org ");
        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
            corpoBuilder.append("inner join tva.paramValidacaoArqEstSet vae ");
            corpoBuilder.append("inner join vae.estabelecimento est ");
        }

        corpoBuilder.append(" where 1=1 ");

        if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_SUP)) {
            corpoBuilder.append(" and cse.cseCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
        } else if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
            corpoBuilder.append(" and est.estCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
        }

        if (tvaCodigos != null && !tvaCodigos.isEmpty()) {
            corpoBuilder.append(" and tva.tvaCodigo ").append(criaClausulaNomeada("tvaCodigos", tvaCodigos));
        }

        if (tvaChaves != null && !tvaChaves.isEmpty()) {
            corpoBuilder.append(" and tva.tvaChave ").append(criaClausulaNomeada("tvaChaves", tvaChaves));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);

        if (tvaCodigos != null && !tvaCodigos.isEmpty()) {
            defineValorClausulaNomeada("tvaCodigos", tvaCodigos, query);
        }

        if (tvaChaves != null && !tvaChaves.isEmpty()) {
            defineValorClausulaNomeada("tvaChaves", tvaChaves, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TVA_CODIGO,
                Columns.TVA_CHAVE,
                Columns.TVA_DESCRICAO,
                "VALOR",
                "CODIGO_ENTIDADE",
                "ENTIDADE"
        };
    }
}
