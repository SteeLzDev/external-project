/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     07/03/2022 11:48:20                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_arquivo_mensagem                                   */
/*==============================================================*/
create table tb_arquivo_mensagem
(
   MEN_CODIGO           varchar(32) not null,
   ARQ_CODIGO           varchar(32) not null,
   primary key (MEN_CODIGO, ARQ_CODIGO)
) ENGINE=InnoDB;

alter table tb_arquivo_mensagem add constraint FK_R_857 foreign key (MEN_CODIGO)
      references tb_mensagem (MEN_CODIGO) on delete restrict on update restrict;

alter table tb_arquivo_mensagem add constraint FK_R_858 foreign key (ARQ_CODIGO)
      references tb_arquivo (ARQ_CODIGO) on delete restrict on update restrict;
