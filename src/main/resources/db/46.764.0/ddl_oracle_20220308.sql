/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     08/03/2022 09:45:42                          */
/*==============================================================*/


alter table tb_operacao_libera_margem add olm_confirmada char(1) default 'N' not null;

alter table tb_operacao_libera_margem add ade_codigo varchar2(32);

/*==============================================================*/
/* Index: r_864_fk                                              */
/*==============================================================*/
create index r_864_fk on tb_operacao_libera_margem (
   ade_codigo asc
);

alter table tb_operacao_libera_margem
   add constraint fk_tb_opera_r_864_tb_aut_d foreign key (ade_codigo)
      references tb_aut_desconto (ade_codigo);

