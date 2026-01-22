/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     17/06/2022 13:57:40                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_limite_margem_csa_org                            */
/*==============================================================*/
create table tb_limite_margem_csa_org  (
   mar_codigo           smallint                        not null,
   csa_codigo           varchar2(32)                    not null,
   org_codigo           varchar2(32)                    not null,
   lmc_valor            number(13,8)                   default 1 not null,
   lmc_data             date                            not null,
   constraint pk_tb_limite_margem_csa_org primary key (mar_codigo, csa_codigo, org_codigo)
);

/*==============================================================*/
/* Index: r_875_fk                                              */
/*==============================================================*/
create index r_875_fk on tb_limite_margem_csa_org (
   mar_codigo asc
);

/*==============================================================*/
/* Index: r_876_fk                                              */
/*==============================================================*/
create index r_876_fk on tb_limite_margem_csa_org (
   csa_codigo asc
);

/*==============================================================*/
/* Index: r_877_fk                                              */
/*==============================================================*/
create index r_877_fk on tb_limite_margem_csa_org (
   org_codigo asc
);

alter table tb_limite_margem_csa_org
   add constraint fk_tb_limit_r_875_tb_marge foreign key (mar_codigo)
      references tb_margem (mar_codigo);

alter table tb_limite_margem_csa_org
   add constraint fk_tb_limit_r_876_tb_consi foreign key (csa_codigo)
      references tb_consignataria (csa_codigo);

alter table tb_limite_margem_csa_org
   add constraint fk_tb_limit_r_877_tb_orgao foreign key (org_codigo)
      references tb_orgao (org_codigo);

