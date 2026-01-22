/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     06/10/2023 09:50:18                          */
/*==============================================================*/


alter table tb_margem_registro_servidor 
  add mar_codigo_adequacao smallint
;

/*==============================================================*/
/* Index: r_943_fk                                              */
/*==============================================================*/
create index r_943_fk on tb_margem_registro_servidor (
   mar_codigo_adequacao asc
);

alter table tb_margem_registro_servidor
   add constraint fk_tb_marge_r_943_tb_marge foreign key (mar_codigo_adequacao)
      references tb_margem (mar_codigo);

