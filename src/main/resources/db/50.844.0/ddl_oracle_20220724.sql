/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     17/06/2022 14:55:26                          */
/*==============================================================*/


alter table tb_beneficiario add bfc_classificacao char(1);

alter table tb_beneficiario add rse_codigo varchar2(32);

/*==============================================================*/
/* Index: r_885_fk                                              */
/*==============================================================*/
create index r_885_fk on tb_beneficiario (
   rse_codigo asc
);

alter table tb_beneficiario
   add constraint fk_tb_benef_r_885_tb_regis foreign key (rse_codigo)
      references tb_registro_servidor (rse_codigo);

