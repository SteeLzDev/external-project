/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     09/03/2023 09:37:50                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_destinatario_email_csa                           */
/*==============================================================*/
create table tb_destinatario_email_csa  (
   fun_codigo           varchar2(32)                    not null,
   pap_codigo           varchar2(32)                    not null,
   csa_codigo           varchar2(32)                    not null,
   dem_receber          char(1)                         default 'S' not null,
   dem_email            varchar2(100),
   constraint pk_tb_destinatario_email_csa primary key (fun_codigo, pap_codigo, csa_codigo)
);

/*==============================================================*/
/* Index: r_902_fk                                              */
/*==============================================================*/
create index r_902_fk on tb_destinatario_email_csa (
   fun_codigo asc
);

/*==============================================================*/
/* Index: r_903_fk                                              */
/*==============================================================*/
create index r_903_fk on tb_destinatario_email_csa (
   pap_codigo asc
);

/*==============================================================*/
/* Index: r_904_fk                                              */
/*==============================================================*/
create index r_904_fk on tb_destinatario_email_csa (
   csa_codigo asc
);

alter table tb_destinatario_email_csa
   add constraint fk_tb_desti_r_902_tb_funca foreign key (fun_codigo)
      references tb_funcao (fun_codigo);

alter table tb_destinatario_email_csa
   add constraint fk_tb_desti_r_903_tb_papel foreign key (pap_codigo)
      references tb_papel (pap_codigo);

alter table tb_destinatario_email_csa
   add constraint fk_tb_desti_r_904_tb_consi foreign key (csa_codigo)
      references tb_consignataria (csa_codigo);

