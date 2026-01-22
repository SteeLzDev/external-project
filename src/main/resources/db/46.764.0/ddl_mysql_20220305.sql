/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     07/03/2022 11:48:20                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_categoria_faq                                      */
/*==============================================================*/
create table tb_categoria_faq
(
   CAF_CODIGO           varchar(32) not null,
   CAF_DESCRICAO        varchar(100) not null,
   primary key (CAF_CODIGO)
) ENGINE=InnoDB;

alter table tb_faq
  add FAQ_EXIBE_MOBILE char(1) not null default 'N'
;

alter table tb_faq
  add CAF_CODIGO varchar(32)
;

alter table tb_faq add constraint FK_R_856 foreign key (CAF_CODIGO)
      references tb_categoria_faq (CAF_CODIGO) on delete restrict on update restrict;
