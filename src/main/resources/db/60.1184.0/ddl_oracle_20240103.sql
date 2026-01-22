/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     02/01/2024 12:07:44                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_destinatario_email_cse                           */
/*==============================================================*/
create table tb_destinatario_email_cse  (
   fun_codigo           varchar2(32)                    not null,
   pap_codigo           varchar2(32)                    not null,
   cse_codigo           varchar2(32)                    not null,
   dee_receber          char(1)                         default 'S' not null,
   dee_email            varchar2(100),
   constraint pk_tb_destinatario_email_cse primary key (fun_codigo, pap_codigo, cse_codigo)
);

/*==============================================================*/
/* Index: r_946_fk                                              */
/*==============================================================*/
create index r_946_fk on tb_destinatario_email_cse (
   fun_codigo asc
);

/*==============================================================*/
/* Index: r_947_fk                                              */
/*==============================================================*/
create index r_947_fk on tb_destinatario_email_cse (
   pap_codigo asc
);

/*==============================================================*/
/* Index: r_948_fk                                              */
/*==============================================================*/
create index r_948_fk on tb_destinatario_email_cse (
   cse_codigo asc
);

/*==============================================================*/
/* Table: tb_destinatario_email_ser                           */
/*==============================================================*/
create table tb_destinatario_email_ser  (
   fun_codigo           varchar2(32)                    not null,
   pap_codigo           varchar2(32)                    not null,
   ser_codigo           varchar2(32)                    not null,
   des_receber          char(1)                         default 'S' not null,
   constraint pk_tb_destinatario_email_ser primary key (fun_codigo, pap_codigo, ser_codigo)
);

/*==============================================================*/
/* Index: r_949_fk                                              */
/*==============================================================*/
create index r_949_fk on tb_destinatario_email_ser (
   fun_codigo asc
);

/*==============================================================*/
/* Index: r_950_fk                                              */
/*==============================================================*/
create index r_950_fk on tb_destinatario_email_ser (
   pap_codigo asc
);

/*==============================================================*/
/* Index: r_951_fk                                              */
/*==============================================================*/
create index r_951_fk on tb_destinatario_email_ser (
   ser_codigo asc
);

alter table tb_destinatario_email_cse
   add constraint fk_tb_desti_r_946_tb_funca foreign key (fun_codigo)
      references tb_funcao (fun_codigo);

alter table tb_destinatario_email_cse
   add constraint fk_tb_desti_r_947_tb_papel foreign key (pap_codigo)
      references tb_papel (pap_codigo);

alter table tb_destinatario_email_cse
   add constraint fk_tb_desti_r_948_tb_consi foreign key (cse_codigo)
      references tb_consignante (cse_codigo);

alter table tb_destinatario_email_ser
   add constraint fk_tb_desti_r_949_tb_funca foreign key (fun_codigo)
      references tb_funcao (fun_codigo);

alter table tb_destinatario_email_ser
   add constraint fk_tb_desti_r_950_tb_papel foreign key (pap_codigo)
      references tb_papel (pap_codigo);

alter table tb_destinatario_email_ser
   add constraint fk_tb_desti_r_951_tb_servi foreign key (ser_codigo)
      references tb_servidor (ser_codigo);

