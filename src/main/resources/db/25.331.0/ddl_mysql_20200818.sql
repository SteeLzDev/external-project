/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     18/08/2020 16:57:38                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_atendimento                                        */
/*==============================================================*/
create table tb_atendimento
(
   ATE_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32),
   ATE_NOME_USUARIO     varchar(100) not null,
   ATE_EMAIL_USUARIO    varchar(100) not null,
   ATE_DATA_INICIO      datetime not null,
   ATE_DATA_ULT_MENSAGEM datetime not null,
   ATE_ID_SESSAO        varchar(36) not null,
   ATE_IP_ACESSO        varchar(45) not null,
   primary key (ATE_CODIGO)
) ENGINE=InnoDB;

/*==============================================================*/
/* Table: tb_atendimento_mensagem                               */
/*==============================================================*/
create table tb_atendimento_mensagem
(
   ATE_CODIGO           varchar(32) not null,
   AME_SEQUENCIA        int not null,
   AME_DATA             datetime not null,
   AME_TEXTO            text not null,
   AME_BOT              tinyint(1) not null default 0,
   primary key (ATE_CODIGO, AME_SEQUENCIA)
) ENGINE=InnoDB;

alter table tb_atendimento add constraint FK_R_808 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

alter table tb_atendimento_mensagem add constraint FK_R_809 foreign key (ATE_CODIGO)
      references tb_atendimento (ATE_CODIGO) on delete restrict on update restrict;

