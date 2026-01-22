/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     19/11/2019 12:29:33                          */
/*==============================================================*/


drop table if exists tb_arquivo_ser;

/*==============================================================*/
/* Table: tb_arquivo_ser                                        */
/*==============================================================*/
create table tb_arquivo_ser
(
   SER_CODIGO           varchar(32) not null,
   ARQ_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32) not null,
   ASE_DATA_CRIACAO     datetime not null,
   ASE_NOME             varchar(255) not null,
   ASE_IP_ACESSO        varchar(45) not null,
   primary key (SER_CODIGO, ARQ_CODIGO)
) ENGINE=InnoDB;

alter table tb_arquivo_ser add constraint FK_R_783 foreign key (SER_CODIGO)
      references tb_servidor (SER_CODIGO) on delete restrict on update restrict;

alter table tb_arquivo_ser add constraint FK_R_784 foreign key (ARQ_CODIGO)
      references tb_arquivo (ARQ_CODIGO) on delete restrict on update restrict;

alter table tb_arquivo_ser add constraint FK_R_785 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

