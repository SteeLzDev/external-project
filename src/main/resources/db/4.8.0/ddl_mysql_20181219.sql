/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     19/12/2018 15:56:01                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_acesso_usuario                                     */
/*==============================================================*/
create table tb_acesso_usuario
(
   ACR_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32) not null,
   ACU_NRO_ACESSO       int not null,
   primary key (ACR_CODIGO, USU_CODIGO)
) ENGINE = InnoDB;

/*==============================================================*/
/* Table: tb_ajuda_recurso                                      */
/*==============================================================*/
create table tb_ajuda_recurso
(
   AJR_CODIGO           varchar(32) not null,
   ACR_CODIGO           varchar(32) not null,
   AJR_ELEMENTO         varchar(100) not null,
   AJR_SEQUENCIA        smallint not null,
   AJR_POSICAO          varchar(40) not null,
   AJR_TEXTO            text not null,
   primary key (AJR_CODIGO)
) ENGINE = InnoDB;

alter table tb_acesso_usuario add constraint FK_R_735 foreign key (ACR_CODIGO)
      references tb_acesso_recurso (ACR_CODIGO) on delete restrict on update restrict;

alter table tb_acesso_usuario add constraint FK_R_736 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

alter table tb_ajuda_recurso add constraint FK_R_734 foreign key (ACR_CODIGO)
      references tb_acesso_recurso (ACR_CODIGO) on delete restrict on update restrict;

