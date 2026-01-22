package com.zetra.econsig.persistence.query.relatorio;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p> Title: RelatorioUsuariosQuery</p>
 * <p> Description: Monta relatório de cadastro de usuários</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioUsuariosQuery extends ReportHQuery {
    public String corCodigo;
    public String csaCodigo;
    public String cseCodigo;
    public String orgCodigo;
    public Boolean includeSuporte;
    public List<String> funCodigos;
    public List<String> stuCodigos;
    public AcessoSistema responsavel;
    public String csaCodigoTodosCor;

    @Override
    public void setCriterios(TransferObject criterio) {
        corCodigo = (String) criterio.getAttribute(Columns.UCO_COR_CODIGO);
        csaCodigo = (String) criterio.getAttribute(Columns.UCA_CSA_CODIGO);
        cseCodigo = (String) criterio.getAttribute(Columns.UCE_CSE_CODIGO);
        orgCodigo = (String) criterio.getAttribute(Columns.UOR_ORG_CODIGO);
        includeSuporte = (Boolean) criterio.getAttribute(ReportManager.CRITERIO_INCLUDE_SUPORTE);
        funCodigos = (List<String>) criterio.getAttribute(Columns.FUN_CODIGO);
        stuCodigos = (List<String>) criterio.getAttribute(Columns.STU_CODIGO);
        csaCodigoTodosCor = (String) criterio.getAttribute("CSA_CODIGO_TODOS_COR");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String fields = "select distinct usu.usuCodigo as usu_codigo," +
                        "case " +
                        "when usu.statusLogin.stuCodigo = '" + CodedValues.STU_EXCLUIDO + "' then coalesce(nullif(concat(usu.usuTipoBloq, '(*)'), ''), usu.usuLogin) " +
                        "else usu.usuLogin end as usu_login, " +
                        "usu.usuNome as usu_nome," +
                        "coalesce(usu.usuCpf,'') as usu_cpf," +
                        "usu.usuEmail as usu_email," +
                        "usu.usuTel as usu_tel," +

                        " usuarioCsa2.csaCodigo as csa_codigo, " +
                        " usuarioCse2.cseCodigo as cse_codigo, " +
                        " usuarioCor2.corCodigo as cor_codigo, " +
                        " usuarioOrg2.orgCodigo as org_codigo, " +
                        " usuarioSer2.serCodigo as ser_codigo, " +
                        " usuarioSup2.cseCodigo as sup_cse_codigo, " +

                        "usu.statusLogin.stuCodigo as stu_codigo," +
                        "usu.statusLogin.stuDescricao as stu_descricao," +
                        "per.perCodigo as per_codigo," +
                        "coalesce(per.perDescricao, '"+ApplicationResourcesHelper.getMessage("rotulo.perfil.descricao.personalizado", (AcessoSistema) null)+"') as per_descricao," +
                        "usu.usuDataCad as usu_data_cad," +
                        "coalesce(usu.usuDataExpSenha, usu.usuDataCad) as usu_data_exp_senha,";

        boolean temCsa = ((responsavel.isCseSup() || responsavel.isCsa()) && csaCodigo != null && !csaCodigo.equals("NENHUM"));
        boolean temCor = ((responsavel.isCseSup() || responsavel.isCsaCor()) && (corCodigo != null && !corCodigo.equals("NENHUM") && (!corCodigo.equals("TODOS_DA_CSA") || !TextHelper.isNull(csaCodigoTodosCor))));
        boolean temCse = (responsavel.isCseSup() && cseCodigo != null && !cseCodigo.equals("NENHUM"));
        boolean temOrg = (responsavel.isCseSupOrg() && orgCodigo != null && !orgCodigo.equals("NENHUM"));
        boolean temSup = (responsavel.isCseSup() && includeSuporte != null && includeSuporte.booleanValue());

        List<Object> status = new ArrayList<Object>();
        status.add(CodedValues.STU_ATIVO);
        status.add(CodedValues.STU_BLOQUEADO);
        status.add(CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE);
        status.add(CodedValues.STU_BLOQUEADO_POR_CSE);
        status.add(CodedValues.STU_BLOQUEADO_AUSENCIA_TEMPORARIA);
        status.add(CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);
        status.add(CodedValues.STU_BLOQUEADO_AUTOMATICAMENTE_FIM_VIGENCIA);

        boolean exibeUsuExcluidos = ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_USUARIOS_EXCLUIDOS, AcessoSistema.getAcessoUsuarioSistema());
        if (exibeUsuExcluidos) {
            status.add(CodedValues.STU_EXCLUIDO);
        }

        String fieldCaseTipo = null;
        String fieldCaseEnt = null;

        StringBuilder join = new StringBuilder(" from Usuario usu");
        StringBuilder clause = new StringBuilder();


        if (temCse) {
            fieldCaseTipo = " case when usu.usuCodigo = uce.usuCodigo then '" + ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel).toUpperCase() + "' ";
            fieldCaseEnt = " case when usu.usuCodigo = uce.usuCodigo then cse.cseNome ";
            join.append(" left outer join usu.usuarioCseSet uce ");
            join.append(" left outer join uce.consignante cse ");

            if (clause.length() > 0) {
                clause.append(" or ");
            }

            if (!TextHelper.isNull(cseCodigo)) {
                clause.append(" uce.cseCodigo = :cseCodigo");
                clause.append(" and usu.statusLogin.stuCodigo ").append(criaClausulaNomeada("status", status));
            } else {
                clause.append(" uce.usuCodigo = usu.usuCodigo");
                clause.append(" and usu.statusLogin.stuCodigo ").append(criaClausulaNomeada("status", status));
            }

            if (funCodigos != null && !funCodigos.isEmpty()) {
                clause.append(" and (");
                clause.append(" exists (select 1 from usu.funcaoPerfilCseSet funcaoPerfil ");
                clause.append(" where funcaoPerfil.usuCodigo = usu.usuCodigo ");
                clause.append(" and funcaoPerfil.funCodigo ").append(criaClausulaNomeada("funCodigos", funCodigos));
                clause.append(" ) or ");
                clause.append(" exists (select 1 from usu.perfilUsuarioSet upe ");
                clause.append(" inner join upe.perfil per ");
                clause.append(" inner join per.funcaoPerfilSet fp ");
                clause.append(" where upe.usuCodigo = usu.usuCodigo ");
                clause.append(" and fp.funCodigo ").append(criaClausulaNomeada("funCodigos", funCodigos));
                clause.append(" )) ");
            }

            if (stuCodigos != null && !stuCodigos.isEmpty()) {
                clause.append(" and usu.statusLogin.stuCodigo ").append(criaClausulaNomeada("stuCodigos", stuCodigos));
            }
        }

        if (temSup) {
            if (cseCodigo == null || cseCodigo.equals("NENHUM")) {
                cseCodigo = CodedValues.CSE_CODIGO_SISTEMA;
            }

            if (fieldCaseTipo == null) {
                fieldCaseTipo = " case when usu.usuCodigo = usp.usuCodigo then '" + ApplicationResourcesHelper.getMessage("rotulo.suporte.singular", responsavel).toUpperCase() + "' ";
                fieldCaseEnt =  " case when usu.usuCodigo = usp.usuCodigo then '' ";
            } else {
                fieldCaseTipo += " when usu.usuCodigo = usp.usuCodigo then '" + ApplicationResourcesHelper.getMessage("rotulo.suporte.singular", responsavel).toUpperCase() + "' ";
                fieldCaseEnt +=  " when usu.usuCodigo = usp.usuCodigo then '' ";
            }
            join.append(" left outer join usu.usuarioSupSet usp ");

            if (clause.length() > 0) {
                clause.append(" or ");
            }

            clause.append(" usp.cseCodigo = :cseCodigo");
            clause.append(" and usu.statusLogin.stuCodigo ").append(criaClausulaNomeada("status", status));

            if (funCodigos != null && !funCodigos.isEmpty()) {
                clause.append(" and (");
                clause.append(" exists (select 1 from usu.funcaoPerfilSupSet funcaoPerfil ");
                clause.append(" where funcaoPerfil.usuCodigo = usu.usuCodigo ");
                clause.append(" and funcaoPerfil.funCodigo ").append(criaClausulaNomeada("funCodigos", funCodigos));
                clause.append(" ) or ");
                clause.append(" exists (select 1 from usu.perfilUsuarioSet upe ");
                clause.append(" inner join upe.perfil per ");
                clause.append(" inner join per.funcaoPerfilSet fp ");
                clause.append(" where upe.usuCodigo = usu.usuCodigo ");
                clause.append(" and fp.funCodigo ").append(criaClausulaNomeada("funCodigos", funCodigos));
                clause.append(" )) ");
            }

            if (stuCodigos != null && !stuCodigos.isEmpty()) {
                clause.append(" and usu.statusLogin.stuCodigo ").append(criaClausulaNomeada("stuCodigos", stuCodigos));
            }
        }

        if (temOrg) {
            if (fieldCaseTipo == null) {
                fieldCaseTipo = " case when usu.usuCodigo = uor.usuCodigo then '" + ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel).toUpperCase() + "' ";
                fieldCaseEnt =  " case when usu.usuCodigo = uor.usuCodigo then org.orgNome ";
            } else {
                fieldCaseTipo += " when usu.usuCodigo = uor.usuCodigo then '" + ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel).toUpperCase() + "' ";
                fieldCaseEnt +=  " when usu.usuCodigo = uor.usuCodigo then org.orgNome ";
            }

            join.append(" left outer join usu.usuarioOrgSet uor ");
            join.append(" left outer join uor.orgao org ");

            if (clause.length() > 0) {
                clause.append(" or ");
            }

            if (!TextHelper.isNull(orgCodigo)) {
                clause.append(" uor.orgCodigo = :orgCodigo");
                clause.append(" and usu.statusLogin.stuCodigo ").append(criaClausulaNomeada("status", status));
            } else {
                clause.append(" uor.usuCodigo = usu.usuCodigo");
                clause.append(" and usu.statusLogin.stuCodigo ").append(criaClausulaNomeada("status", status));
            }

            if (funCodigos != null && !funCodigos.isEmpty()) {
                clause.append(" and (");
                clause.append(" exists (select 1 from usu.funcaoPerfilOrgSet funcaoPerfil ");
                clause.append(" where funcaoPerfil.usuCodigo = usu.usuCodigo ");
                clause.append(" and funcaoPerfil.funCodigo ").append(criaClausulaNomeada("funCodigos", funCodigos));
                clause.append(" ) or ");
                clause.append(" exists (select 1 from usu.perfilUsuarioSet upe ");
                clause.append(" inner join upe.perfil per ");
                clause.append(" inner join per.funcaoPerfilSet fp ");
                clause.append(" where upe.usuCodigo = usu.usuCodigo ");
                clause.append(" and fp.funCodigo ").append(criaClausulaNomeada("funCodigos", funCodigos));
                clause.append(" )) ");
            }

            if (stuCodigos != null && !stuCodigos.isEmpty()) {
                clause.append(" and usu.statusLogin.stuCodigo ").append(criaClausulaNomeada("stuCodigos", stuCodigos));
            }
        }

        if (temCsa) {
            if (fieldCaseTipo == null) {
                fieldCaseTipo = " case when usu.usuCodigo = uca.usuCodigo then '" + ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel).toUpperCase() + "' ";
                fieldCaseEnt =  " case when usu.usuCodigo = uca.usuCodigo then csa.csaNome ";
            } else {
                fieldCaseTipo += " when usu.usuCodigo = uca.usuCodigo then '" + ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel).toUpperCase() + "' ";
                fieldCaseEnt +=  " when usu.usuCodigo = uca.usuCodigo then csa.csaNome ";
            }

            join.append(" left outer join usu.usuarioCsaSet uca ");
            join.append(" left outer join uca.consignataria csa ");

            if (clause.length() > 0) {
                clause.append(" or ");
            }

            if (!TextHelper.isNull(csaCodigo)) {
                clause.append(" uca.csaCodigo = :csaCodigo");
                clause.append(" and usu.statusLogin.stuCodigo ").append(criaClausulaNomeada("status", status));
            } else {
                clause.append(" uca.usuCodigo = usu.usuCodigo");
                clause.append(" and usu.statusLogin.stuCodigo ").append(criaClausulaNomeada("status", status));
            }

            if (funCodigos != null && !funCodigos.isEmpty()) {
                clause.append(" and (");
                clause.append(" exists (select 1 from usu.funcaoPerfilCsaSet funcaoPerfil ");
                clause.append(" where funcaoPerfil.usuCodigo = usu.usuCodigo ");
                clause.append(" and funcaoPerfil.funCodigo ").append(criaClausulaNomeada("funCodigos", funCodigos));
                clause.append(" ) or ");
                clause.append(" exists (select 1 from usu.perfilUsuarioSet upe ");
                clause.append(" inner join upe.perfil per ");
                clause.append(" inner join per.funcaoPerfilSet fp ");
                clause.append(" where upe.usuCodigo = usu.usuCodigo ");
                clause.append(" and fp.funCodigo ").append(criaClausulaNomeada("funCodigos", funCodigos));
                clause.append(" )) ");
            }

            if (stuCodigos != null && !stuCodigos.isEmpty()) {
                clause.append(" and usu.statusLogin.stuCodigo ").append(criaClausulaNomeada("stuCodigos", stuCodigos));
            }
        }

        if (temCor) {
            if (fieldCaseTipo == null) {
                fieldCaseTipo = " case when usu.usuCodigo = uco.usuCodigo then '" + ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel).toUpperCase() + "' ";
                fieldCaseEnt =  " case when usu.usuCodigo = uco.usuCodigo then concat(concat(csaCor.csaNome, ' - '), cor.corNome) ";
            } else {
                fieldCaseTipo += " when usu.usuCodigo = uco.usuCodigo then '" + ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel).toUpperCase() + "' ";
                fieldCaseEnt +=  " when usu.usuCodigo = uco.usuCodigo then concat(concat(csaCor.csaNome, ' - '), cor.corNome) ";
            }

            join.append(" left outer join usu.usuarioCorSet uco ");
            join.append(" left outer join uco.correspondente cor ");
            join.append(" left outer join cor.consignataria csaCor ");

            if (clause.length() > 0) {
                clause.append(" or ");
            }

            if (!TextHelper.isNull(corCodigo) && !corCodigo.equals("TODOS_DA_CSA")) {
                clause.append(" uco.corCodigo = :corCodigo");
                clause.append(" and usu.statusLogin.stuCodigo ").append(criaClausulaNomeada("status", status));
            } else {
                clause.append(" uco.usuCodigo = usu.usuCodigo");
                clause.append(" and usu.statusLogin.stuCodigo ").append(criaClausulaNomeada("status", status));
                if (responsavel.isCsaCor() || (corCodigo.equals("TODOS_DA_CSA") && !TextHelper.isNull(csaCodigoTodosCor))) {
                    clause.append(" and csaCor.csaCodigo ").append(criaClausulaNomeada("csaCodigoTodosCor", csaCodigoTodosCor));
                }
            }

            if (funCodigos != null && !funCodigos.isEmpty()) {
                clause.append(" and (");
                clause.append(" exists (select 1 from usu.funcaoPerfilCorSet funcaoPerfil ");
                clause.append(" where funcaoPerfil.usuCodigo = usu.usuCodigo ");
                clause.append(" and funcaoPerfil.funCodigo ").append(criaClausulaNomeada("funCodigos", funCodigos));
                clause.append(" ) or ");
                clause.append(" exists (select 1 from usu.perfilUsuarioSet upe ");
                clause.append(" inner join upe.perfil per ");
                clause.append(" inner join per.funcaoPerfilSet fp ");
                clause.append(" where upe.usuCodigo = usu.usuCodigo ");
                clause.append(" and fp.funCodigo ").append(criaClausulaNomeada("funCodigos", funCodigos));
                clause.append(" )) ");
            }

            if (stuCodigos != null && !stuCodigos.isEmpty()) {
                clause.append(" and usu.statusLogin.stuCodigo ").append(criaClausulaNomeada("stuCodigos", stuCodigos));
            }
        }

        join.append(" left outer join usu.perfilUsuarioSet upe");
        join.append(" left outer join upe.perfil per");

        if (!TextHelper.isNull(fieldCaseTipo)) {
            fieldCaseTipo += " else '' end as TIPO";
        }

        if (!TextHelper.isNull(fieldCaseEnt)) {
            fieldCaseEnt += " else '' end as ENTIDADE";
        }

        StringBuilder corpoBuilder = new StringBuilder(fields);
        corpoBuilder.append(fieldCaseTipo);
        corpoBuilder.append(",");
        corpoBuilder.append(fieldCaseEnt);
        corpoBuilder.append(join);

        corpoBuilder.append(" LEFT JOIN usu.usuarioCsaSet usuarioCsa2 ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCseSet usuarioCse2 ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCorSet usuarioCor2 ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioOrgSet usuarioOrg2 ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioSerSet usuarioSer2 ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioSupSet usuarioSup2 ");


        corpoBuilder.append(" where 1=1 ");
        corpoBuilder.append(" and (").append(clause).append(")");

        if (!responsavel.isSup()) {
            corpoBuilder.append(" and COALESCE(usu.usuVisivel, 'S') <> 'N'");
        }

        corpoBuilder.append(" order by ");
        if(fieldCaseTipo!= null){
            corpoBuilder.append(fieldCaseTipo.substring(0,fieldCaseTipo.indexOf("end") + 3)).append(",");
        }
        corpoBuilder.append("usu.usuNome");


        Query<Object[]> queryInst = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(cseCodigo) && !cseCodigo.equals("NENHUM")) {
            defineValorClausulaNomeada("cseCodigo", cseCodigo, queryInst);
        }

        if (!TextHelper.isNull(orgCodigo) && !orgCodigo.equals("NENHUM")) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, queryInst);
        }

        if (!TextHelper.isNull(csaCodigo) && !csaCodigo.equals("NENHUM")) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, queryInst);
        } else if (responsavel.isCsaCor() && temCor && TextHelper.isNull(corCodigo) && TextHelper.isNull(csaCodigoTodosCor)) {
            defineValorClausulaNomeada("csaCodigo", responsavel.isCor() ? responsavel.getCodigoEntidadePai() : responsavel.getCodigoEntidade(), queryInst);
        }

        if (!TextHelper.isNull(corCodigo) && !corCodigo.equals("NENHUM") && !corCodigo.equals("TODOS_DA_CSA")) {
            defineValorClausulaNomeada("corCodigo", corCodigo, queryInst);
        }

        if (funCodigos != null && !funCodigos.isEmpty()) {
            defineValorClausulaNomeada("funCodigos", funCodigos, queryInst);
        }

        if (stuCodigos != null && !stuCodigos.isEmpty()) {
            defineValorClausulaNomeada("stuCodigos", stuCodigos, queryInst);
        }

        if (!TextHelper.isNull(csaCodigoTodosCor)) {
            defineValorClausulaNomeada("csaCodigoTodosCor", csaCodigoTodosCor, queryInst);
        }

        defineValorClausulaNomeada("status", status, queryInst);

        return queryInst;
    }

}
