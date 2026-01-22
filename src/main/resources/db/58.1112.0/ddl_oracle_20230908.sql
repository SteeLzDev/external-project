/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     27/07/2023 15:54:18                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_informacao_csa_servidor                          */
/*==============================================================*/
create table tb_informacao_csa_servidor  (
   ics_codigo           varchar2(32)                    not null,
   ser_codigo           varchar2(32)                    not null,
   csa_codigo           varchar2(32)                    not null,
   usu_codigo           varchar2(32)                    not null,
   ics_valor            clob                            not null,
   ics_data             date                            not null,
   ics_ip_acesso        varchar2(45),
   constraint pk_tb_informacao_csa_servidor primary key (ics_codigo)
);

/*==============================================================*/
/* Index: r_928_fk                                              */
/*==============================================================*/
create index r_928_fk on tb_informacao_csa_servidor (
   ser_codigo asc
);

/*==============================================================*/
/* Index: r_929_fk                                              */
/*==============================================================*/
create index r_929_fk on tb_informacao_csa_servidor (
   csa_codigo asc
);

/*==============================================================*/
/* Index: r_930_fk                                              */
/*==============================================================*/
create index r_930_fk on tb_informacao_csa_servidor (
   usu_codigo asc
);

alter table tb_informacao_csa_servidor
   add constraint fk_tb_infor_r_928_tb_servi foreign key (ser_codigo)
      references tb_servidor (ser_codigo);

alter table tb_informacao_csa_servidor
   add constraint fk_tb_infor_r_929_tb_consi foreign key (csa_codigo)
      references tb_consignataria (csa_codigo);

alter table tb_informacao_csa_servidor
   add constraint fk_tb_infor_r_930_tb_usuar foreign key (usu_codigo)
      references tb_usuario (usu_codigo);

