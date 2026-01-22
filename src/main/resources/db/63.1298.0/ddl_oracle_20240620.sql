/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     05/06/2024 14:20:53                          */
/*==============================================================*/


alter table tb_consignataria add csa_consulta_margem_sem_senha char(1) default 'N' not null;

/*==============================================================*/
/* Table: tb_consulta_margem_sem_senha                        */
/*==============================================================*/
create table tb_consulta_margem_sem_senha  (
   css_codigo           varchar2(32)                    not null,
   rse_codigo           varchar2(32)                    not null,
   csa_codigo           varchar2(32)                    not null,
   css_data_ini         date                            not null,
   css_data_fim         date                            not null,
   css_data_revogacao_sup date,
   css_data_revogacao_ser date,
   css_data_alerta      date,
   constraint pk_tb_consulta_margem_sem_senh primary key (css_codigo)
);

/*==============================================================*/
/* Index: r_959_fk                                              */
/*==============================================================*/
create index r_959_fk on tb_consulta_margem_sem_senha (
   csa_codigo asc
);

/*==============================================================*/
/* Index: r_960_fk                                              */
/*==============================================================*/
create index r_960_fk on tb_consulta_margem_sem_senha (
   rse_codigo asc
);

alter table tb_consulta_margem_sem_senha
   add constraint fk_tb_consu_r_959_tb_consi foreign key (csa_codigo)
      references tb_consignataria (csa_codigo);

alter table tb_consulta_margem_sem_senha
   add constraint fk_tb_consu_r_960_tb_regis foreign key (rse_codigo)
      references tb_registro_servidor (rse_codigo);

