package com.zetra.econsig.helper.arquivo;

/**
 * <p>Title: FileInfo</p>
 * <p>Description: Representa informações básicas de um arquivo, incluíndo o próprio conteúdo em bytes.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 28926 $
 * $Date: 2020-02-26 17:25:19 -0300 (qua, 26 fev 2020) $
 */
public class FileInfo {
    public String fileName;
    public long fileSize;
    public byte [] fileContent;
}
