/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     31/03/2025 17:24:35                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_param_pontuacao_rse_csa                            */
/*==============================================================*/
create table tb_param_pontuacao_rse_csa  (
   ppr_codigo           varchar2(32)                    not null,
   csa_codigo           varchar2(32)                    not null,
   tpo_codigo           varchar2(32)                    not null,
   nse_codigo           varchar2(32),
   ppr_pontuacao        integer                         not null,
   ppr_lim_inferior     integer                         not null,
   ppr_lim_superior     integer                         not null,
   constraint pk_tb_param_pontuacao_rse_csa primary key (ppr_codigo)
);

/*==============================================================*/
/* Index: r_979_fk                                              */
/*==============================================================*/
create index r_979_fk on tb_param_pontuacao_rse_csa (
   tpo_codigo asc
);

/*==============================================================*/
/* Index: r_980_fk                                              */
/*==============================================================*/
create index r_980_fk on tb_param_pontuacao_rse_csa (
   csa_codigo asc
);

/*==============================================================*/
/* Index: r_981_fk                                              */
/*==============================================================*/
create index r_981_fk on tb_param_pontuacao_rse_csa (
   nse_codigo asc
);

/*==============================================================*/
/* Table: tb_pontuacao_rse_csa                                  */
/*==============================================================*/
create table tb_pontuacao_rse_csa  (
   rse_codigo           varchar2(32)                    not null,
   csa_codigo           varchar2(32)                    not null,
   pon_vlr              integer                         not null,
   pon_data             date                            not null,
   constraint pk_tb_pontuacao_rse_csa primary key (rse_codigo, csa_codigo)
);

/*==============================================================*/
/* Index: r_982_fk                                              */
/*==============================================================*/
create index r_982_fk on tb_pontuacao_rse_csa (
   rse_codigo asc
);

/*==============================================================*/
/* Index: r_983_fk                                              */
/*==============================================================*/
create index r_983_fk on tb_pontuacao_rse_csa (
   csa_codigo asc
);

alter table tb_param_pontuacao_rse_csa
   add constraint fk_tb_param_r_979_tb_tipo_ foreign key (tpo_codigo)
      references tb_tipo_param_pontuacao (tpo_codigo);

alter table tb_param_pontuacao_rse_csa
   add constraint fk_tb_param_r_980_tb_consi foreign key (csa_codigo)
      references tb_consignataria (csa_codigo);

alter table tb_param_pontuacao_rse_csa
   add constraint fk_tb_param_r_981_tb_natur foreign key (nse_codigo)
      references tb_natureza_servico (nse_codigo);

alter table tb_pontuacao_rse_csa
   add constraint fk_tb_pontu_r_982_tb_regis foreign key (rse_codigo)
      references tb_registro_servidor (rse_codigo);

alter table tb_pontuacao_rse_csa
   add constraint fk_tb_pontu_r_983_tb_consi foreign key (csa_codigo)
      references tb_consignataria (csa_codigo);

