/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     26/03/2024 09:56:18                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_funcao_altera_margem_ade                           */
/*==============================================================*/
create table tb_funcao_altera_margem_ade  (
   fun_codigo           varchar2(32)                    not null,
   pap_codigo           varchar2(32)                    not null,
   mar_codigo_origem    smallint                        not null,
   mar_codigo_destino   smallint                        not null,
   constraint pk_tb_funcao_altera_margem_ade primary key (fun_codigo, pap_codigo, mar_codigo_origem, mar_codigo_destino)
);

/*==============================================================*/
/* Index: r_955_fk                                              */
/*==============================================================*/
create index r_955_fk on tb_funcao_altera_margem_ade (
   fun_codigo asc
);

/*==============================================================*/
/* Index: r_956_fk                                              */
/*==============================================================*/
create index r_956_fk on tb_funcao_altera_margem_ade (
   pap_codigo asc
);

/*==============================================================*/
/* Index: r_957_fk                                              */
/*==============================================================*/
create index r_957_fk on tb_funcao_altera_margem_ade (
   mar_codigo_origem asc
);

/*==============================================================*/
/* Index: r_958_fk                                              */
/*==============================================================*/
create index r_958_fk on tb_funcao_altera_margem_ade (
   mar_codigo_destino asc
);

alter table tb_funcao_altera_margem_ade
   add constraint fk_tb_funca_r_955_tb_funca foreign key (fun_codigo)
      references tb_funcao (fun_codigo);

alter table tb_funcao_altera_margem_ade
   add constraint fk_tb_funca_r_956_tb_papel foreign key (pap_codigo)
      references tb_papel (pap_codigo);

alter table tb_funcao_altera_margem_ade
   add constraint fk_tb_funca_r_957_tb_marge foreign key (mar_codigo_origem)
      references tb_margem (mar_codigo);

alter table tb_funcao_altera_margem_ade
   add constraint fk_tb_funca_r_958_tb_marge foreign key (mar_codigo_destino)
      references tb_margem (mar_codigo);

