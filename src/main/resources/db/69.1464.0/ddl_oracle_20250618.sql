/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     08/05/2025 11:34:28                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_regra_convenio                                     */
/*==============================================================*/
create table tb_regra_convenio  (
   rco_codigo           varchar2(32)                    not null,
   csa_codigo           varchar2(32)                    not null,
   svc_codigo           varchar(32)                         null,
   org_codigo           varchar(32)                         null,
   mar_codigo           smallint                            null,
   rco_campo_codigo     varchar2(32)                    not null,
   rco_campo_nome       varchar2(255)                   not null,
   rco_campo_valor      varchar2(100)                   not null,
   constraint pk_regra_convenio primary key (rco_codigo)
);

/*==============================================================*/
/* Index: idx_csa_rco_1                                              */
/*==============================================================*/
create index idx_csa_rco_1 on tb_regra_convenio (
   csa_codigo asc
);

alter table tb_regra_convenio
   add constraint fk_csa_rco_1 foreign key (csa_codigo)
      references tb_consignataria (csa_codigo);

alter table tb_regra_convenio
   add constraint fk_svc_rco_2 foreign key (svc_codigo)
      references tb_servico (svc_codigo);

alter table tb_regra_convenio
   add constraint fk_org_rco_3 foreign key (org_codigo)
      references tb_orgao (org_codigo);

alter table tb_regra_convenio
   add constraint fk_org_rco_4 foreign key (mar_codigo)
      references tb_margem (mar_codigo);

