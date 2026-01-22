/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     06/06/2022 10:33:32                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_credenciamento_csa                                 */
/*==============================================================*/
create table tb_credenciamento_csa  (
   cre_codigo           varchar2(32)                    not null,
   csa_codigo           varchar2(32)                    not null,
   scr_codigo           varchar2(32)                    not null,
   cre_data_ini         date                            not null,
   cre_data_fim         date,
   constraint pk_tb_credenciamento_csa primary key (cre_codigo)
);

/*==============================================================*/
/* Index: r_871_fk                                              */
/*==============================================================*/
create index r_871_fk on tb_credenciamento_csa (
   scr_codigo asc
);

/*==============================================================*/
/* Index: r_872_fk                                              */
/*==============================================================*/
create index r_872_fk on tb_credenciamento_csa (
   csa_codigo asc
);

/*==============================================================*/
/* Table: tb_status_credenciamento                              */
/*==============================================================*/
create table tb_status_credenciamento  (
   scr_codigo           varchar2(32)                    not null,
   scr_descricao        varchar2(40)                    not null,
   constraint pk_tb_status_credenciamento primary key (scr_codigo)
);

alter table tb_credenciamento_csa
   add constraint fk_tb_crede_r_871_tb_statu foreign key (scr_codigo)
      references tb_status_credenciamento (scr_codigo);

alter table tb_credenciamento_csa
   add constraint fk_tb_crede_r_872_tb_consi foreign key (csa_codigo)
      references tb_consignataria (csa_codigo);

