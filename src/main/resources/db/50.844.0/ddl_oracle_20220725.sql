/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     27/06/2022 11:23:45                          */
/*==============================================================*/


alter table ta_beneficiario add bfc_classificacao char(1);

alter table ta_beneficiario add rse_codigo varchar2(32);

/*==============================================================*/
/* Index: r_886_fk                                              */
/*==============================================================*/
create index r_886_fk on ta_beneficiario (
   rse_codigo asc
);

alter table ta_beneficiario
   add constraint fk_ta_benef_r_886_tb_regis foreign key (rse_codigo)
      references tb_registro_servidor (rse_codigo);

