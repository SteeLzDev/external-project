/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     08/05/2025 11:25:05                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_regra_convenio                                     */
/*==============================================================*/
create table tb_regra_convenio
(
   RCO_CODIGO           varchar(32) not null,
   CSA_CODIGO           varchar(32) not null,
   SVC_CODIGO           varchar(32) null,
   ORG_CODIGO           varchar(32) null,
   MAR_CODIGO           smallint null,
   RCO_CAMPO_CODIGO     VARCHAR(32) not null,
   RCO_CAMPO_NOME       VARCHAR(255) not null,
   RCO_CAMPO_VALOR      varchar(100) not null,
   primary key (RCO_CODIGO)
) ENGINE = InnoDB;

alter table tb_regra_convenio add constraint fk_csa_rco_1 foreign key (csa_codigo)
      references tb_consignataria (csa_codigo) on delete restrict on update restrict;

alter table tb_regra_convenio add constraint fk_svc_rco_2 foreign key (svc_codigo)
      references tb_servico (svc_codigo) on delete restrict on update restrict;

alter table tb_regra_convenio add constraint fk_org_rco_3 foreign key (org_codigo)
      references tb_orgao (org_codigo) on delete restrict on update restrict;

alter table tb_regra_convenio add constraint fk_org_rco_4 foreign key (mar_codigo)
      references tb_margem (mar_codigo) on delete restrict on update restrict;

