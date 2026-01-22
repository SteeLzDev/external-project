package com.zetra.econsig.persistence.query.anexo;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaAnexoAutorizacaoDescontoQuery</p>
 * <p>Description: Query para listagem dos anexos de uma consignação.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaAnexoAutorizacaoDescontoQuery extends HQuery {

    public AcessoSistema responsavel;
    public String adeCodigo;
    public String aadNome;
    public String tarCodigo;
    public List<String> tarCodigos;
    public Short aadAtivo;
    public boolean count = false;
    public boolean arquivado = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (count) {
            corpo = "select count(*) as total ";
        } else {
            corpo = "select "
                  + "ade.adeCodigo, "
                  + "ade.adeNumero, "
                  + "usu.usuLogin, "
                  + "usuarioCsa.csaCodigo, "
                  + "usuarioCse.cseCodigo, "
                  + "usuarioCor.corCodigo, "
                  + "usuarioOrg.orgCodigo, "
                  + "usuarioSer.serCodigo, "
                  + "usuarioSup.cseCodigo, "
                  + "aad.aadNome, "
                  + "aad.aadDescricao, "
                  + "aad.aadAtivo, "
                  + "aad.aadData, "
                  + "aad.aadIpAcesso, "
                  + "tar.tarCodigo, "
                  + "tar.tarDescricao, "
                  + "ade.adeData, "
                  + "aad.aadPeriodo "
                  ;
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(arquivado ? "from HtAnexoAutorizacaoDesconto aad " : "from AnexoAutorizacaoDesconto aad ");
        corpoBuilder.append(" inner join aad.usuario usu ");
        corpoBuilder.append(" inner join aad.tipoArquivo tar ");
        corpoBuilder.append("LEFT JOIN usu.usuarioCsaSet usuarioCsa ");
        corpoBuilder.append("LEFT JOIN usu.usuarioCseSet usuarioCse ");
        corpoBuilder.append("LEFT JOIN usu.usuarioCorSet usuarioCor ");
        corpoBuilder.append("LEFT JOIN usu.usuarioOrgSet usuarioOrg ");
        corpoBuilder.append("LEFT JOIN usu.usuarioSerSet usuarioSer ");
        corpoBuilder.append("LEFT JOIN usu.usuarioSupSet usuarioSup ");
        corpoBuilder.append(" inner join aad.autDesconto ade ");
        corpoBuilder.append(" where 1 = 1 ");

        if (!TextHelper.isNull(adeCodigo)) {
            corpoBuilder.append(" and ade.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));
        }
        if (!TextHelper.isNull(aadNome)) {
            corpoBuilder.append(" and aad.aadNome ").append(criaClausulaNomeada("aadNome", aadNome));
        }
        if (!TextHelper.isNull(tarCodigo)) {
            corpoBuilder.append(" and aad.tipoArquivo.tarCodigo ").append(criaClausulaNomeada("tarCodigo", tarCodigo));
        }
        if (tarCodigos != null && tarCodigos.size() > 0) {
            corpoBuilder.append(" and aad.tipoArquivo.tarCodigo ").append(criaClausulaNomeada("tarCodigos", tarCodigos));
        }
        if (aadAtivo != null) {
            corpoBuilder.append(" and aad.aadAtivo ").append(criaClausulaNomeada("aadAtivo", aadAtivo));
        }
        if (responsavel != null) {
            if (responsavel.isSup()) {
                corpoBuilder.append(" and aad.aadExibeSup = 'S'");
            } else if (responsavel.isCse()) {
                corpoBuilder.append(" and aad.aadExibeCse = 'S'");
            } else if (responsavel.isOrg()) {
                corpoBuilder.append(" and aad.aadExibeOrg = 'S'");
            } else if (responsavel.isCsa()) {
                corpoBuilder.append(" and aad.aadExibeCsa = 'S'");
            } else if (responsavel.isCor()) {
                corpoBuilder.append(" and aad.aadExibeCor = 'S'");
            } else if (responsavel.isSer()) {
                corpoBuilder.append(" and aad.aadExibeSer = 'S'");
            }
        }
        if (!count) {
            corpoBuilder.append(" ORDER BY aad.aadData DESC, aad.aadNome ASC");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(adeCodigo)) {
            defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        }
        if (!TextHelper.isNull(aadNome)) {
            defineValorClausulaNomeada("aadNome", aadNome, query);
        }
        if (!TextHelper.isNull(tarCodigo)) {
            defineValorClausulaNomeada("tarCodigo", tarCodigo, query);
        }
        if (tarCodigos != null && tarCodigos.size() > 0) {
            defineValorClausulaNomeada("tarCodigos", tarCodigos, query);
        }
        if (aadAtivo != null) {
            defineValorClausulaNomeada("aadAtivo", aadAtivo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.ADE_CODIGO,
                Columns.ADE_NUMERO,
                Columns.USU_LOGIN,
                Columns.UCA_CSA_CODIGO,
                Columns.UCE_CSE_CODIGO,
                Columns.UCO_COR_CODIGO,
                Columns.UOR_ORG_CODIGO,
                Columns.USE_SER_CODIGO,
                Columns.USP_CSE_CODIGO,
                Columns.AAD_NOME,
                Columns.AAD_DESCRICAO,
                Columns.AAD_ATIVO,
                Columns.AAD_DATA,
                Columns.AAD_IP_ACESSO,
                Columns.TAR_CODIGO,
                Columns.TAR_DESCRICAO,
                Columns.ADE_DATA,
                Columns.AAD_PERIODO
        };
    }
}