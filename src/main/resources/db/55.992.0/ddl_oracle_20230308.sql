/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     16/02/2023 17:58:32                          */
/*==============================================================*/


alter table tb_operacao_libera_margem add ade_codigo_ht varchar2(32);

/*==============================================================*/
/* Index: r_901_fk                                              */
/*==============================================================*/
create index r_901_fk on tb_operacao_libera_margem (
   ade_codigo_ht asc
);

alter table tb_operacao_libera_margem
   add constraint fk_tb_opera_r_901_ht_aut_d foreign key (ade_codigo_ht)
      references ht_aut_desconto (ade_codigo);

