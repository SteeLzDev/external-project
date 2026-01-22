/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     22/09/2022 14:43:09                          */
/*==============================================================*/


alter table tb_definicao_taxa_juros add fun_codigo varchar2(32);

/*==============================================================*/
/* Index: r_891_fk                                              */
/*==============================================================*/
create index r_891_fk on tb_definicao_taxa_juros (
   fun_codigo asc
);

alter table tb_definicao_taxa_juros
   add constraint fk_tb_defin_r_891_tb_funca foreign key (fun_codigo)
      references tb_funcao (fun_codigo);

