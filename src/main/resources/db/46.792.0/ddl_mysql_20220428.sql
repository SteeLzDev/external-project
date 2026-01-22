/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     30/03/2022 15:13:40                          */
/*==============================================================*/


drop table if exists tb_arquivo_mensagem;

/*==============================================================*/
/* Table: tb_arquivo_mensagem                                   */
/*==============================================================*/
create table tb_arquivo_mensagem
(
   MEN_CODIGO           varchar(32) not null,
   ARQ_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32) not null,
   AMN_DATA_CRIACAO     datetime not null,
   AMN_NOME             varchar(255) not null,
   AMN_IP_ACESSO        varchar(45) not null,
   primary key (MEN_CODIGO, ARQ_CODIGO)
) ENGINE=InnoDB;

alter table tb_arquivo_mensagem add constraint FK_R_857 foreign key (MEN_CODIGO)
      references tb_mensagem (MEN_CODIGO) on delete restrict on update restrict;

alter table tb_arquivo_mensagem add constraint FK_R_858 foreign key (ARQ_CODIGO)
      references tb_arquivo (ARQ_CODIGO) on delete restrict on update restrict;

alter table tb_arquivo_mensagem add constraint FK_R_865 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;
