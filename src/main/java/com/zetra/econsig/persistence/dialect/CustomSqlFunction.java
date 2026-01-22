package com.zetra.econsig.persistence.dialect;

import java.io.Serializable;

import org.hibernate.type.BasicType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>Title: CustomSqlFunction</p>
 * <p>Description: Mapeamento de funções customizadas para funções nativas do SGBD.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * @author Leonel Martins
 */
@Getter
@Setter
@AllArgsConstructor
public class CustomSqlFunction implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private BasicType<?> returnType;

    private String template;

}
