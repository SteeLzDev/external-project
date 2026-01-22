/*==============================================================*/
/* DBMS name:      ORACLE Version 10g                           */
/* Created on:     07/03/2022 12:04:33                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_categoria_faq                                      */
/*==============================================================*/
create table tb_categoria_faq  (
   caf_codigo           varchar2(32)                    not null,
   caf_descricao        varchar2(100)                   not null,
   constraint pk_tb_categoria_faq primary key (caf_codigo)
);

alter table tb_faq add faq_exibe_mobile char(1) default 'N' not null;

alter table tb_faq add caf_codigo varchar2(32);

/*==============================================================*/
/* Index: r_856_fk                                              */
/*==============================================================*/
create index r_856_fk on tb_faq (
   caf_codigo asc
);

alter table tb_faq
   add constraint fk_tb_faq_r_856_tb_categ foreign key (caf_codigo)
      references tb_categoria_faq (caf_codigo);

