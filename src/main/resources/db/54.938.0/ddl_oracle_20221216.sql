/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     21/11/2022 15:22:11                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_param_posto_csa_svc                              */
/*==============================================================*/
create table tb_param_posto_csa_svc  (
   tps_codigo           varchar2(32)                    not null,
   svc_codigo           varchar2(32)                    not null,
   csa_codigo           varchar2(32)                    not null,
   pos_codigo           varchar2(32)                    not null,
   ppo_vlr              varchar2(255)                   not null,
   constraint pk_tb_param_posto_csa_svc primary key (tps_codigo, svc_codigo, csa_codigo, pos_codigo)
);

/*==============================================================*/
/* Index: r_895_fk                                              */
/*==============================================================*/
create index r_895_fk on tb_param_posto_csa_svc (
   tps_codigo asc
);

/*==============================================================*/
/* Index: r_896_fk                                              */
/*==============================================================*/
create index r_896_fk on tb_param_posto_csa_svc (
   svc_codigo asc
);

/*==============================================================*/
/* Index: r_897_fk                                              */
/*==============================================================*/
create index r_897_fk on tb_param_posto_csa_svc (
   csa_codigo asc
);

/*==============================================================*/
/* Index: r_898_fk                                              */
/*==============================================================*/
create index r_898_fk on tb_param_posto_csa_svc (
   pos_codigo asc
);

alter table tb_param_posto_csa_svc
   add constraint fk_tb_param_r_895_tb_tipo_ foreign key (tps_codigo)
      references tb_tipo_param_svc (tps_codigo);

alter table tb_param_posto_csa_svc
   add constraint fk_tb_param_r_896_tb_servi foreign key (svc_codigo)
      references tb_servico (svc_codigo);

alter table tb_param_posto_csa_svc
   add constraint fk_tb_param_r_897_tb_consi foreign key (csa_codigo)
      references tb_consignataria (csa_codigo);

alter table tb_param_posto_csa_svc
   add constraint fk_tb_param_r_898_tb_posto foreign key (pos_codigo)
      references tb_posto_registro_servidor (pos_codigo);

