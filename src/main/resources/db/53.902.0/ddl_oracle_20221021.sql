/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     27/09/2022 09:20:36                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_param_csa_registro_ser                             */
/*==============================================================*/
create table tb_param_csa_registro_ser  (
   csa_codigo           varchar2(32)                    not null,
   rse_codigo           varchar2(32)                    not null,
   tpa_codigo           varchar2(32)                    not null,
   prc_data_cadastro    date                            not null,
   prc_vlr              varchar2(255)                   not null,
   prc_obs              clob,
   constraint pk_tb_param_csa_registro_ser primary key (csa_codigo, rse_codigo, tpa_codigo)
);

/*==============================================================*/
/* Index: r_892_fk                                              */
/*==============================================================*/
create index r_892_fk on tb_param_csa_registro_ser (
   csa_codigo asc
);

/*==============================================================*/
/* Index: r_893_fk                                              */
/*==============================================================*/
create index r_893_fk on tb_param_csa_registro_ser (
   rse_codigo asc
);

/*==============================================================*/
/* Index: r_894_fk                                              */
/*==============================================================*/
create index r_894_fk on tb_param_csa_registro_ser (
   tpa_codigo asc
);

alter table tb_param_csa_registro_ser
   add constraint fk_tb_param_r_892_tb_consi foreign key (csa_codigo)
      references tb_consignataria (csa_codigo);

alter table tb_param_csa_registro_ser
   add constraint fk_tb_param_r_893_tb_regis foreign key (rse_codigo)
      references tb_registro_servidor (rse_codigo);

alter table tb_param_csa_registro_ser
   add constraint fk_tb_param_r_894_tb_tipo_ foreign key (tpa_codigo)
      references tb_tipo_param_consignataria (tpa_codigo);

