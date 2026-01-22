package com.zetra.econsig.persistence.query.relatorio;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioOcorrenciaUsuarioQuery</p>
 * <p>Description: Query para relatório de ocorrência de Usuário
 * de lote</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioOcorrenciaUsuarioQuery extends ReportHQuery {

    private String dataIni;
    private String dataFim;
    private String orgCodigo;
    private String csaCodigo;
    private String cseCodigo;
    private String corCodigo;
    private String opLogin;
    private String tipo;
    private String csaCor;
    private List<String> tocCodigos;
    private List<String> tmoCodigos;
    private Boolean includeSuporte;

    @Override
    public void setCriterios(TransferObject criterio) {
        dataIni = (String) criterio.getAttribute("DATA_INI");
        dataFim = (String) criterio.getAttribute("DATA_FIM");
        orgCodigo = (String) criterio.getAttribute("ORG_CODIGO");
        csaCodigo = (String) criterio.getAttribute("CSA_CODIGO");
        cseCodigo = (String) criterio.getAttribute("CSE_CODIGO");
        corCodigo = (String) criterio.getAttribute("COR_CODIGO");
        opLogin = (String) criterio.getAttribute("OP_LOGIN");
        tipo = (String) criterio.getAttribute("tipoEntidade");
        csaCor = (String) criterio.getAttribute("COR_CSA");
        tocCodigos = (List<String>) criterio.getAttribute(Columns.TOC_CODIGO);
        tmoCodigos = (List<String>) criterio.getAttribute(Columns.TMO_CODIGO);
        includeSuporte = (Boolean) criterio.getAttribute(ReportManager.CRITERIO_INCLUDE_SUPORTE);
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String fields = "";
        StringBuilder corpoBuilder = null;
        String[] opLoginArray = null;

        if (TextHelper.isNull(tipo)) {
            fields = "select " +
                     " case " +
                     "           when usuario.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then coalesce(nullif(concat(usuario.usuTipoBloq, '*'), ''), usuario.usuLogin)" +
                     "           else usuario.usuLogin" +
                     " end as usu_login, " +
                     " usuarioCsa2.csaCodigo as csa_codigo, " +
                     " usuarioCse2.cseCodigo as cse_codigo, " +
                     " usuarioCor2.corCodigo as cor_codigo, " +
                     " usuarioOrg2.orgCodigo as org_codigo, " +
                     " usuarioSer2.serCodigo as ser_codigo, " +
                     " usuarioSup2.cseCodigo as sup_cse_codigo, " +
                     " usuarioCsa3.csaCodigo as csa_codigo2, " +
                     " usuarioCse3.cseCodigo as cse_codigo2, " +
                     " usuarioCor3.corCodigo as cor_codigo2, " +
                     " usuarioOrg3.orgCodigo as org_codigo2, " +
                     " usuarioSer3.serCodigo as ser_codigo2, " +
                     " usuarioSup3.cseCodigo as sup_cse_codigo2, " +
                     " case ";

            corpoBuilder = new StringBuilder(fields);

            if (TextHelper.isNull(cseCodigo) || !cseCodigo.equals("NENHUM")) {
                corpoBuilder.append("           when usuario.usuCodigo = usuarioCse.usuCodigo then consignante.cseNome");
            }

            if (includeSuporte != null && includeSuporte.booleanValue()) {
                corpoBuilder.append("           when usuario.usuCodigo = usuarioSup.usuCodigo then '"+ ApplicationResourcesHelper.getMessage("rotulo.suporte.singular", (AcessoSistema) null).toUpperCase() +"' ");
            }

            if (TextHelper.isNull(csaCodigo) || !csaCodigo.equals("NENHUM")) {
                corpoBuilder.append("           when usuario.usuCodigo = usuarioCsa.usuCodigo then consignataria.csaNome");
            }

            if (TextHelper.isNull(corCodigo) || !corCodigo.equals("NENHUM")) {
                corpoBuilder.append("           when usuario.usuCodigo = usuarioCor.usuCodigo then");
                corpoBuilder.append("                concat(concat(CSA_COR.csaNome,' - '),correspondente.corNome)");
            }

            if (TextHelper.isNull(orgCodigo) || !orgCodigo.equals("NENHUM")) {
                corpoBuilder.append("           when usuario.usuCodigo = usuarioOrg.usuCodigo then orgao.orgNome");
            }

            corpoBuilder.append(" end as ent_nome, ");
            corpoBuilder.append(" case ");
            corpoBuilder.append("           when op.statusLogin.stuCodigo = '").append(CodedValues.STU_EXCLUIDO).append("' then coalesce(nullif(concat(op.usuTipoBloq, '*'), ''), op.usuLogin)");
            corpoBuilder.append("           else op.usuLogin");
            corpoBuilder.append(" end as oplogin, ");
            corpoBuilder.append(" concatenar(substituir(substituir(substituir(upper(text_to_string(ous.ousObs)),'</B>',''),'<B>',''),'<BR>',' '), case when tmo.tmoDescricao is NULL then '' else concatenar('  "+ApplicationResourcesHelper.getMessage("rotulo.motivo.singular", (AcessoSistema) null)+": ',tmo.tmoDescricao) end) as toc_descricao, ");
            corpoBuilder.append("to_locale_datetime(ous.ousData) as ous_data,");
            corpoBuilder.append("ous.ousIpAcesso as ous_ip_acesso,");
            corpoBuilder.append("usuario.usuNome as usu_nome");

            corpoBuilder.append(" from OcorrenciaUsuario ous ");
            corpoBuilder.append("inner join ous.tipoOcorrencia toc ");
            corpoBuilder.append("inner join ous.usuarioByUsuCodigo usuario ");
            corpoBuilder.append("inner join ous.usuarioByOusUsuCodigo op ");

            corpoBuilder.append(" LEFT JOIN op.usuarioCsaSet usuarioCsa2 ");
            corpoBuilder.append(" LEFT JOIN op.usuarioCseSet usuarioCse2 ");
            corpoBuilder.append(" LEFT JOIN op.usuarioCorSet usuarioCor2 ");
            corpoBuilder.append(" LEFT JOIN op.usuarioOrgSet usuarioOrg2 ");
            corpoBuilder.append(" LEFT JOIN op.usuarioSerSet usuarioSer2 ");
            corpoBuilder.append(" LEFT JOIN op.usuarioSupSet usuarioSup2 ");

            corpoBuilder.append(" LEFT JOIN usuario.usuarioCsaSet usuarioCsa3 ");
            corpoBuilder.append(" LEFT JOIN usuario.usuarioCseSet usuarioCse3 ");
            corpoBuilder.append(" LEFT JOIN usuario.usuarioCorSet usuarioCor3 ");
            corpoBuilder.append(" LEFT JOIN usuario.usuarioOrgSet usuarioOrg3 ");
            corpoBuilder.append(" LEFT JOIN usuario.usuarioSerSet usuarioSer3 ");
            corpoBuilder.append(" LEFT JOIN usuario.usuarioSupSet usuarioSup3 ");

            corpoBuilder.append(" left outer join ous.tipoMotivoOperacao tmo ");

            if (TextHelper.isNull(cseCodigo) || !cseCodigo.equals("NENHUM")) {
                corpoBuilder.append("left outer join usuario.usuarioCseSet usuarioCse ");
                corpoBuilder.append("left outer join usuarioCse.consignante consignante ");
            }

            if (includeSuporte != null && includeSuporte.booleanValue()) {
                corpoBuilder.append("left outer join usuario.usuarioSupSet usuarioSup ");
                corpoBuilder.append("left outer join usuarioSup.consignante consignante2 ");
            }

            if (TextHelper.isNull(csaCodigo) || !csaCodigo.equals("NENHUM")) {
                corpoBuilder.append("left outer join usuario.usuarioCsaSet usuarioCsa ");
                corpoBuilder.append("left outer join usuarioCsa.consignataria consignataria ");
            }

            if (TextHelper.isNull(corCodigo) || !corCodigo.equals("NENHUM")) {
                corpoBuilder.append("left outer join usuario.usuarioCorSet usuarioCor ");
                corpoBuilder.append("left outer join usuarioCor.correspondente correspondente ");
                corpoBuilder.append("left outer join correspondente.consignataria CSA_COR ");
            }

            if (TextHelper.isNull(orgCodigo) || !orgCodigo.equals("NENHUM")) {
                corpoBuilder.append("left outer join usuario.usuarioOrgSet usuarioOrg ");
                corpoBuilder.append("left outer join usuarioOrg.orgao orgao ");
            }
        } else if (tipo.equals(AcessoSistema.ENTIDADE_CSE)) {
            fields = "select " +
                     " case " +
                     "           when usuario.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then coalesce(nullif(concat(usuario.usuTipoBloq, '*'), ''), usuario.usuLogin)" +
                     "           else usuario.usuLogin" +
                     " end as usu_login, " +
                     " usuarioCsa2.csaCodigo as csa_codigo, " +
                     " usuarioCse2.cseCodigo as cse_codigo, " +
                     " usuarioCor2.corCodigo as cor_codigo, " +
                     " usuarioOrg2.orgCodigo as org_codigo, " +
                     " usuarioSer2.serCodigo as ser_codigo, " +
                     " usuarioSup2.cseCodigo as sup_cse_codigo, " +

                     " '' as csa_codigo2, " +
                     " usuarioCse.cseCodigo as cse_codigo2, " +
                     " '' as cor_codigo2, " +
                     " '' as org_codigo2, " +
                     " '' as ser_codigo2, " +
                     " '' as sup_cse_codigo2, " +
                     " consignante.cseNome as ent_nome, " +
                     " case " +
                     "           when op.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then coalesce(nullif(concat(op.usuTipoBloq, '*'), ''), op.usuLogin)" +
                     "           else op.usuLogin" +
                     " end as oplogin, " +
                     " concatenar(substituir(substituir(substituir(upper(text_to_string(ous.ousObs)),'</B>',''),'<B>',''),'<BR>',' '), case when tmo.tmoDescricao is NULL then '' else concatenar('  "+ApplicationResourcesHelper.getMessage("rotulo.motivo.singular", (AcessoSistema) null)+": ',tmo.tmoDescricao) end) as toc_descricao, " +
                     " to_locale_datetime(ous.ousData) as ous_data," +
                     " ous.ousIpAcesso as ous_ip_acesso," +
                     " usuario.usuNome as usu_nome";

            corpoBuilder = new StringBuilder(fields);

            corpoBuilder.append(" from OcorrenciaUsuario ous ");
            corpoBuilder.append("inner join ous.tipoOcorrencia toc ");
            corpoBuilder.append("inner join ous.usuarioByUsuCodigo usuario ");
            corpoBuilder.append("inner join ous.usuarioByOusUsuCodigo op ");
            corpoBuilder.append("inner join usuario.usuarioCseSet usuarioCse ");
            corpoBuilder.append("inner join usuarioCse.consignante consignante ");

            corpoBuilder.append(" left outer join ous.tipoMotivoOperacao tmo ");

            corpoBuilder.append(" LEFT JOIN op.usuarioCsaSet usuarioCsa2 ");
            corpoBuilder.append(" LEFT JOIN op.usuarioCseSet usuarioCse2 ");
            corpoBuilder.append(" LEFT JOIN op.usuarioCorSet usuarioCor2 ");
            corpoBuilder.append(" LEFT JOIN op.usuarioOrgSet usuarioOrg2 ");
            corpoBuilder.append(" LEFT JOIN op.usuarioSerSet usuarioSer2 ");
            corpoBuilder.append(" LEFT JOIN op.usuarioSupSet usuarioSup2 ");


        } else if (tipo.equals(AcessoSistema.ENTIDADE_CSA)) {
            fields = "select " +
                     " case " +
                     "           when usuario.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then coalesce(nullif(concat(usuario.usuTipoBloq, '*'), ''), usuario.usuLogin)" +
                     "           else usuario.usuLogin" +
                     " end as usu_login, " +
                     " usuarioCsa2.csaCodigo as csa_codigo, " +
                     " usuarioCse2.cseCodigo as cse_codigo, " +
                     " usuarioCor2.corCodigo as cor_codigo, " +
                     " usuarioOrg2.orgCodigo as org_codigo, " +
                     " usuarioSer2.serCodigo as ser_codigo, " +
                     " usuarioSup2.cseCodigo as sup_cse_codigo, " +

                     " usuarioCsa.csaCodigo as csa_codigo2, " +
                     " '' as cse_codigo2, " +
                     " '' as cor_codigo2, " +
                     " '' as org_codigo2, " +
                     " '' as ser_codigo2, " +
                     " '' as sup_cse_codigo2, " +
                     " case ";

            corpoBuilder = new StringBuilder(fields);

            if (TextHelper.isNull(csaCodigo) || !csaCodigo.equals("NENHUM")) {
                corpoBuilder.append("           when usuario.usuCodigo = usuarioCsa.usuCodigo then consignataria.csaNome");
            }

            if (TextHelper.isNull(corCodigo) || !corCodigo.equals("NENHUM")) {
                corpoBuilder.append("           when usuario.usuCodigo = usuarioCor.usuCodigo then");
                corpoBuilder.append("                concat(concat(CSA_COR.csaNome,' - '),correspondente.corNome)");
            }

            corpoBuilder.append(" end as ent_nome, ");
            corpoBuilder.append(" case ");
            corpoBuilder.append("           when op.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then coalesce(nullif(concat(op.usuTipoBloq, '*'), ''), op.usuLogin)");
            corpoBuilder.append("           else op.usuLogin");
            corpoBuilder.append(" end as oplogin, ");
            corpoBuilder.append(" concatenar(substituir(substituir(substituir(upper(text_to_string(ous.ousObs)),'</B>',''),'<B>',''),'<BR>',' '), case when tmo.tmoDescricao is NULL then '' else concatenar('  "+ApplicationResourcesHelper.getMessage("rotulo.motivo.singular", (AcessoSistema) null)+": ',tmo.tmoDescricao) end) as toc_descricao, ");
            corpoBuilder.append(" to_locale_datetime(ous.ousData) as ous_data,");
            corpoBuilder.append(" ous.ousIpAcesso as ous_ip_acesso,");
            corpoBuilder.append(" usuario.usuNome as usu_nome");

            corpoBuilder.append(" from OcorrenciaUsuario ous ");
            corpoBuilder.append("inner join ous.tipoOcorrencia toc ");
            corpoBuilder.append("inner join ous.usuarioByUsuCodigo usuario ");
            corpoBuilder.append("inner join ous.usuarioByOusUsuCodigo op ");

            corpoBuilder.append(" LEFT JOIN op.usuarioCsaSet usuarioCsa2 ");
            corpoBuilder.append(" LEFT JOIN op.usuarioCseSet usuarioCse2 ");
            corpoBuilder.append(" LEFT JOIN op.usuarioCorSet usuarioCor2 ");
            corpoBuilder.append(" LEFT JOIN op.usuarioOrgSet usuarioOrg2 ");
            corpoBuilder.append(" LEFT JOIN op.usuarioSerSet usuarioSer2 ");
            corpoBuilder.append(" LEFT JOIN op.usuarioSupSet usuarioSup2 ");

            corpoBuilder.append(" left outer join ous.tipoMotivoOperacao tmo ");

            if (TextHelper.isNull(csaCodigo) || !csaCodigo.equals("NENHUM")) {
                corpoBuilder.append("left outer join usuario.usuarioCsaSet usuarioCsa ");
                corpoBuilder.append("left outer join usuarioCsa.consignataria consignataria ");
            }

            if (TextHelper.isNull(corCodigo) || !corCodigo.equals("NENHUM")) {
                corpoBuilder.append("left outer join usuario.usuarioCorSet usuarioCor ");
                corpoBuilder.append("left outer join usuarioCor.correspondente correspondente ");
                corpoBuilder.append("left outer join correspondente.consignataria CSA_COR ");
            }

        } else if (tipo.equals(AcessoSistema.ENTIDADE_COR)) {
            fields = "select " +
            " case " +
            "           when usuario.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then coalesce(nullif(concat(usuario.usuTipoBloq, '*'), ''), usuario.usuLogin)" +
            "           else usuario.usuLogin" +
            " end as usu_login, " +
            " usuarioCsa2.csaCodigo as csa_codigo, " +
            " usuarioCse2.cseCodigo as cse_codigo, " +
            " usuarioCor2.corCodigo as cor_codigo, " +
            " usuarioOrg2.orgCodigo as org_codigo, " +
            " usuarioSer2.serCodigo as ser_codigo, " +
            " usuarioSup2.cseCodigo as sup_cse_codigo, " +

            " '' as csa_codigo2, " +
            " '' as cse_codigo2, " +
            " usuarioCor.corCodigo as cor_codigo2, " +
            " '' as org_codigo2, " +
            " '' as ser_codigo2, " +
            " '' as sup_cse_codigo2, " +
            " concat(concat(consignataria.csaNome,' - '),correspondente.corNome) as ent_nome, " +
            " case " +
            "           when op.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then coalesce(nullif(concat(op.usuTipoBloq, '*'), ''), op.usuLogin)" +
            "           else op.usuLogin" +
            " end as oplogin, " +
            " concatenar(substituir(substituir(substituir(upper(text_to_string(ous.ousObs)),'</B>',''),'<B>',''),'<BR>',' '), case when tmo.tmoDescricao is NULL then '' else concatenar('  "+ApplicationResourcesHelper.getMessage("rotulo.motivo.singular", (AcessoSistema) null)+": ',tmo.tmoDescricao) end) as toc_descricao, " +
            " to_locale_datetime(ous.ousData) as ous_data," +
            " ous.ousIpAcesso as ous_ip_acesso," +
            " usuario.usuNome as usu_nome";

            corpoBuilder = new StringBuilder(fields);

            corpoBuilder.append(" from OcorrenciaUsuario ous ");
            corpoBuilder.append("inner join ous.tipoOcorrencia toc ");
            corpoBuilder.append("inner join ous.usuarioByUsuCodigo usuario ");
            corpoBuilder.append("inner join ous.usuarioByOusUsuCodigo op ");
            corpoBuilder.append("inner join usuario.usuarioCorSet usuarioCor ");
            corpoBuilder.append("inner join usuarioCor.correspondente correspondente ");
            corpoBuilder.append("inner join correspondente.consignataria consignataria ");

            corpoBuilder.append(" left outer join ous.tipoMotivoOperacao tmo ");

            corpoBuilder.append(" LEFT JOIN op.usuarioCsaSet usuarioCsa2 ");
            corpoBuilder.append(" LEFT JOIN op.usuarioCseSet usuarioCse2 ");
            corpoBuilder.append(" LEFT JOIN op.usuarioCorSet usuarioCor2 ");
            corpoBuilder.append(" LEFT JOIN op.usuarioOrgSet usuarioOrg2 ");
            corpoBuilder.append(" LEFT JOIN op.usuarioSerSet usuarioSer2 ");
            corpoBuilder.append(" LEFT JOIN op.usuarioSupSet usuarioSup2 ");


        } else if (tipo.equals(AcessoSistema.ENTIDADE_ORG)) {
            fields = "select " +
            " case " +
            "           when usuario.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then coalesce(nullif(concat(usuario.usuTipoBloq, '*'), ''), usuario.usuLogin)" +
            "           else usuario.usuLogin" +
            " end as usu_login, " +
            " usuarioCsa2.csaCodigo as csa_codigo, " +
            " usuarioCse2.cseCodigo as cse_codigo, " +
            " usuarioCor2.corCodigo as cor_codigo, " +
            " usuarioOrg2.orgCodigo as org_codigo, " +
            " usuarioSer2.serCodigo as ser_codigo, " +
            " usuarioSup2.cseCodigo as sup_cse_codigo, " +

            " '' as csa_codigo2, " +
            " '' as cse_codigo2, " +
            " '' as cor_codigo2, " +
            " usuarioOrg.orgCodigo as org_codigo2, " +
            " '' as ser_codigo2, " +
            " '' as sup_cse_codigo2, " +
            " orgao.orgNome as ent_nome, " +
            " case " +
            "           when op.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then coalesce(nullif(concat(op.usuTipoBloq, '*'), ''), op.usuLogin)" +
            "           else op.usuLogin" +
            " end as oplogin, " +
            " concatenar(substituir(substituir(substituir(upper(text_to_string(ous.ousObs)),'</B>',''),'<B>',''),'<BR>',' '), case when tmo.tmoDescricao is NULL then '' else concatenar('  "+ApplicationResourcesHelper.getMessage("rotulo.motivo.singular", (AcessoSistema) null)+": ',tmo.tmoDescricao) end) as toc_descricao, " +
            " to_locale_datetime(ous.ousData) as ous_data," +
            " ous.ousIpAcesso as ous_ip_acesso," +
            " usuario.usuNome as usu_nome";

            corpoBuilder = new StringBuilder(fields);

            corpoBuilder.append(" from OcorrenciaUsuario ous ");
            corpoBuilder.append("inner join ous.tipoOcorrencia toc ");
            corpoBuilder.append("inner join ous.usuarioByUsuCodigo usuario ");
            corpoBuilder.append("inner join ous.usuarioByOusUsuCodigo op ");
            corpoBuilder.append("inner join usuario.usuarioOrgSet usuarioOrg ");
            corpoBuilder.append("inner join usuarioOrg.orgao orgao ");

            corpoBuilder.append(" LEFT JOIN op.usuarioCsaSet usuarioCsa2 ");
            corpoBuilder.append(" LEFT JOIN op.usuarioCseSet usuarioCse2 ");
            corpoBuilder.append(" LEFT JOIN op.usuarioCorSet usuarioCor2 ");
            corpoBuilder.append(" LEFT JOIN op.usuarioOrgSet usuarioOrg2 ");
            corpoBuilder.append(" LEFT JOIN op.usuarioSerSet usuarioSer2 ");
            corpoBuilder.append(" LEFT JOIN op.usuarioSupSet usuarioSup2 ");

            corpoBuilder.append(" left outer join ous.tipoMotivoOperacao tmo ");
        }

        corpoBuilder.append(" where ");
        corpoBuilder.append(" ous.ousData between :dataIni and :dataFim");

        //TODO: colocar OR ao invés de and abaixo
        StringBuilder whereClause = null;

        if (((!TextHelper.isNull(orgCodigo)) &&
            (TextHelper.isNull(tipo) && !orgCodigo.equals("NENHUM")) || tipo.equals(AcessoSistema.ENTIDADE_ORG))) {
            whereClause = new StringBuilder();
            whereClause.append(" and (orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        } else if (TextHelper.isNull(orgCodigo) && (TextHelper.isNull(tipo) || tipo.equals(AcessoSistema.ENTIDADE_ORG))) {
            whereClause = new StringBuilder();
            whereClause.append(" and (orgao.orgCodigo is not null");
        }

        if (!TextHelper.isNull(csaCodigo) &&
            (((TextHelper.isNull(tipo) || tipo.equals(AcessoSistema.ENTIDADE_CSA)) && !csaCodigo.equals("NENHUM")) || tipo.equals(AcessoSistema.ENTIDADE_COR))) {
            if (whereClause == null) {
                whereClause = new StringBuilder();
                whereClause.append(" and ((consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
            } else {
                whereClause.append(" or (consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
            }

            if (((tipo.equals(AcessoSistema.ENTIDADE_CSA) || TextHelper.isNull(tipo)) && (TextHelper.isNull(corCodigo) || !corCodigo.equals("NENHUM"))) ||
                 (!TextHelper.isNull(corCodigo) && corCodigo.equals("TODOS_DA_CSA"))) {
                whereClause.append(" or CSA_COR.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
            }
            whereClause.append(")");
        } else if (TextHelper.isNull(csaCodigo) && (TextHelper.isNull(tipo) || tipo.equals(AcessoSistema.ENTIDADE_CSA))) {
            if (whereClause != null) {
                whereClause.append(" or consignataria.csaCodigo is not null");
            } else {
                whereClause = new StringBuilder();
                whereClause.append(" and (consignataria.csaCodigo is not null");
            }
        } else if (!TextHelper.isNull(csaCodigo) && csaCodigo.equals("NENHUM") && tipo.equals(AcessoSistema.ENTIDADE_CSA) && !TextHelper.isNull(csaCor)) {
            if (whereClause != null) {
                whereClause.append(" or CSA_COR.csaCodigo").append(criaClausulaNomeada("csaCor", csaCor));
            } else {
                whereClause = new StringBuilder();
                whereClause.append(" and (CSA_COR.csaCodigo").append(criaClausulaNomeada("csaCor", csaCor));
            }
        }

        if ((!TextHelper.isNull(cseCodigo)) && ((TextHelper.isNull(tipo) && !cseCodigo.equals("NENHUM")) || tipo.equals(AcessoSistema.ENTIDADE_CSE))) {
            if (whereClause == null) {
                whereClause = new StringBuilder();
                whereClause.append(" and (consignante.cseCodigo ").append(criaClausulaNomeada("cseCodigo", cseCodigo));
            } else {
                whereClause.append(" or consignante.cseCodigo ").append(criaClausulaNomeada("cseCodigo", cseCodigo));
            }
        } else if (TextHelper.isNull(cseCodigo) && TextHelper.isNull(tipo)) {
            if (whereClause != null) {
                whereClause.append(" or consignante.cseCodigo is not null");
            } else {
                whereClause = new StringBuilder();
                whereClause.append(" and (consignante.cseCodigo is not null");
            }
        }

        if (includeSuporte != null && includeSuporte.booleanValue()) {
            if (tipo.equals(AcessoSistema.ENTIDADE_SUP)) {
                if (whereClause == null) {
                    whereClause = new StringBuilder();
                    whereClause.append(" and (consignante2.cseCodigo = '").append(CodedValues.CSE_CODIGO_SISTEMA).append("'");
                } else {
                    whereClause.append(" or consignante2.cseCodigo = '").append(CodedValues.CSE_CODIGO_SISTEMA).append("'");
                }
            } else if (whereClause != null) {
                whereClause.append(" or consignante2.cseCodigo is not null");
            } else {
                whereClause = new StringBuilder();
                whereClause.append(" and (consignante2.cseCodigo is not null");
            }
        }

        if ((!TextHelper.isNull(corCodigo)) &&
            ((TextHelper.isNull(tipo)) || tipo.equals(AcessoSistema.ENTIDADE_COR) || tipo.equals(AcessoSistema.ENTIDADE_CSA)) &&
            (!corCodigo.equals("NENHUM") && !corCodigo.equals("TODOS_DA_CSA"))) {
            if (whereClause == null) {
                whereClause = new StringBuilder();
                whereClause.append(" and (correspondente.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigo));
            } else {
                whereClause.append(" or correspondente.corCodigo ").append(criaClausulaNomeada("corCodigo", corCodigo));
            }
        } else if (TextHelper.isNull(corCodigo) && (TextHelper.isNull(tipo))) {
            if (whereClause != null) {
                whereClause.append(" or correspondente.corCodigo is not null");
            } else {
                whereClause = new StringBuilder();
                whereClause.append(" and (correspondente.corCodigo is not null");
            }
        }

        if (whereClause != null) {
            whereClause.append(")");
            corpoBuilder.append(whereClause.toString());
        }

        if (!TextHelper.isNull(opLogin)) {
            opLoginArray = TextHelper.split(opLogin.replaceAll(" ", ""), ",");
            corpoBuilder.append(" and op.usuLogin ").append(criaClausulaNomeada("opLoginArray", opLoginArray));
        }

        if (tocCodigos != null && !tocCodigos.isEmpty()) {
            corpoBuilder.append(" and toc.tocCodigo ").append(criaClausulaNomeada("tocCodigo", tocCodigos));
        }

        if (tmoCodigos != null && !tmoCodigos.isEmpty()) {
            corpoBuilder.append(" and tmo.tmoCodigo ").append(criaClausulaNomeada("tmoCodigo", tmoCodigos));
        }

        //para os dois casos em que se usam left join
        if (TextHelper.isNull(tipo)) {
            StringBuilder orClause = null;

            if (TextHelper.isNull(cseCodigo) || !cseCodigo.equals("NENHUM")) {
                orClause = new StringBuilder(" and (");
                orClause.append(" usuarioCse.usuCodigo is not null");
            }

            if (includeSuporte != null && includeSuporte.booleanValue()) {
                if (orClause == null) {
                    orClause = new StringBuilder(" and (");
                } else {
                    orClause.append(" or ");
                }
                orClause.append(" usuarioSup.usuCodigo is not null");
            }

            if (TextHelper.isNull(csaCodigo) || !csaCodigo.equals("NENHUM")) {
                if (orClause == null) {
                    orClause = new StringBuilder(" and (");
                } else {
                    orClause.append(" or ");
                }
                orClause.append(" usuarioCsa.usuCodigo is not null");
            }

            if (TextHelper.isNull(corCodigo) || !corCodigo.equals("NENHUM")) {
                if (orClause == null) {
                    orClause = new StringBuilder(" and (");
                } else {
                    orClause.append(" or ");
                }
                orClause.append(" usuarioCor.usuCodigo is not null");
            }

            if (TextHelper.isNull(orgCodigo) || !orgCodigo.equals("NENHUM")) {
                if (orClause == null) {
                    orClause = new StringBuilder(" and (");
                } else {
                    orClause.append(" or ");
                }
                orClause.append(" usuarioOrg.usuCodigo is not null");
            }

            if (orClause != null) {
                orClause.append(")");
                corpoBuilder.append(orClause.toString());
            }
        }

        corpoBuilder.append(" order by ous.ousData");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(dataIni)) {
            defineValorClausulaNomeada("dataIni", parseDateTimeString(dataIni), query);
        }

        if (!TextHelper.isNull(dataFim)) {
            defineValorClausulaNomeada("dataFim", parseDateTimeString(dataFim), query);
        }

        if (!TextHelper.isNull(opLogin)) {
            defineValorClausulaNomeada("opLoginArray", opLoginArray, query);
        }

        if ((TextHelper.isNull(tipo) || tipo.equals(AcessoSistema.ENTIDADE_ORG)) &&
            (!TextHelper.isNull(orgCodigo) && !orgCodigo.equals("NENHUM"))) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        if ((TextHelper.isNull(tipo) || tipo.equals(AcessoSistema.ENTIDADE_CSA) || tipo.equals(AcessoSistema.ENTIDADE_COR))) {
            if (!TextHelper.isNull(csaCodigo) && !csaCodigo.equals("NENHUM")) {
                defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
            } else if (!TextHelper.isNull(csaCodigo) && csaCodigo.equals("NENHUM") && tipo.equals(AcessoSistema.ENTIDADE_CSA) && !TextHelper.isNull(csaCor)) {
                defineValorClausulaNomeada("csaCor", csaCor, query);
            }
        }

        if ((TextHelper.isNull(tipo) || tipo.equals(AcessoSistema.ENTIDADE_COR) || tipo.equals(AcessoSistema.ENTIDADE_CSA)) &&
            (!TextHelper.isNull(corCodigo) && !corCodigo.equals("NENHUM") && !corCodigo.equals("TODOS_DA_CSA"))) {
            defineValorClausulaNomeada("corCodigo", corCodigo, query);
        }

        if ((TextHelper.isNull(tipo) || tipo.equals(AcessoSistema.ENTIDADE_CSE)) &&
            (!TextHelper.isNull(cseCodigo) && !cseCodigo.equals("NENHUM"))) {
            defineValorClausulaNomeada("cseCodigo", cseCodigo, query);
        }

        if (tocCodigos != null && !tocCodigos.isEmpty()) {
            defineValorClausulaNomeada("tocCodigo", tocCodigos, query);
        }

        if (tmoCodigos != null && !tmoCodigos.isEmpty()) {
            defineValorClausulaNomeada("tmoCodigo", tmoCodigos, query);
        }

        return query;
    }
}
