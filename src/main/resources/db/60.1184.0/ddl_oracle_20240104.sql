/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     21/12/2023 10:47:57                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_saldo_devedor_rse                                  */
/*==============================================================*/
create table tb_saldo_devedor_rse  (
   rse_codigo           varchar2(32)                    not null,
   csa_codigo           varchar2(32)                    not null,
   sdr_valor            number(13,2)                    default 0 not null,
   sdr_data             date                            not null,
   constraint pk_tb_saldo_devedor_rse primary key (rse_codigo, csa_codigo)
);

/*==============================================================*/
/* Index: r_944_fk                                              */
/*==============================================================*/
create index r_944_fk on tb_saldo_devedor_rse (
   rse_codigo asc
);

/*==============================================================*/
/* Index: r_945_fk                                              */
/*==============================================================*/
create index r_945_fk on tb_saldo_devedor_rse (
   csa_codigo asc
);

alter table tb_saldo_devedor_rse
   add constraint fk_tb_saldo_r_944_tb_regis foreign key (rse_codigo)
      references tb_registro_servidor (rse_codigo);

alter table tb_saldo_devedor_rse
   add constraint fk_tb_saldo_r_945_tb_consi foreign key (csa_codigo)
      references tb_consignataria (csa_codigo);

