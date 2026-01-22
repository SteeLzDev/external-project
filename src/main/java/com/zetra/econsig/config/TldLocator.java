package com.zetra.econsig.config;

/**
 * Original code taken from https://github.com/djotanov/undertow-jsp-template
 */
public final class TldLocator {
//    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(TldLocator.class);
//
//    public static HashMap<String, TagLibraryInfo> createTldInfosFromFile(File realPath) throws IOException {
//        HashMap<String, TagLibraryInfo> tagLibInfos = new HashMap<>();
//        long time = System.currentTimeMillis();
//        String webPath = realPath.getAbsolutePath();
//
//        List<String> fileNameList = FileHelper.getFilesInDir(webPath, f -> f.isDirectory() || f.getName().endsWith(".tld"));
//        for (String fileName : fileNameList) {
//            File file = new File(webPath + File.separatorChar + fileName);
//            if (file.isFile() && file.canRead()) {
//                InputStream is = null;
//                try {
//                    is = new FileInputStream(file);
//
//                    TagLibraryInfo taglibInfo = loadTldMetaData(is);
//                    if (taglibInfo != null && !tagLibInfos.containsKey(taglibInfo.getUri())) {
//                        tagLibInfos.put(taglibInfo.getUri(), taglibInfo);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        if (is != null) {
//                            is.close();
//                        }
//                    } catch (IOException ignore) {
//                    }
//                }
//                LOG.debug("File : " + fileName);
//            }
//        }
//
//        LOG.trace("Time: " + (System.currentTimeMillis() - time));
//        return tagLibInfos;
//    }
//
//    public static HashMap<String, TagLibraryInfo> createTldInfosFromURL(URL url) throws IOException {
//        HashMap<String, TagLibraryInfo> tagLibInfos = new HashMap<>();
//        long time = System.currentTimeMillis();
//
//        final URLClassLoader loader = (URLClassLoader) Thread.currentThread().getContextClassLoader();
//        final ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(loader);
//        final Resource[] resources;
//        final String locationPattern = "classpath:WEB-INF/*.tld";
//        try {
//            resources = resolver.getResources(locationPattern);
//        } catch (IOException e) {
//            throw new IllegalStateException(String.format("Error while retrieving resources" + "for location pattern '%s'.", locationPattern, e));
//        }
//
//        for (Resource resource : resources) {
//            try (InputStream is = resource.getInputStream()) {
//                TagLibraryInfo taglibInfo = loadTldMetaData(is);
//                if (taglibInfo != null && !tagLibInfos.containsKey(taglibInfo.getUri())) {
//                    tagLibInfos.put(taglibInfo.getUri(), taglibInfo);
//                }
//            }
//        }
//
//        LOG.trace("Time: " + (System.currentTimeMillis() - time));
//        return tagLibInfos;
//    }
//
//    private static TagLibraryInfo loadTldMetaData(InputStream is) {
//        try {
//            final XMLInputFactory inputFactory = XMLInputFactory.newInstance();
//            inputFactory.setXMLResolver(NoopXMLResolver.create());
//            XMLStreamReader xmlReader = inputFactory.createXMLStreamReader(is);
//            TldMetaData tldMetadata = TldMetaDataParser.parse(xmlReader);
//            return getTagLibraryInfo(tldMetadata);
//        } catch (XMLStreamException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    private static TagLibraryInfo getTagLibraryInfo(TldMetaData tldMetaData) {
//        TagLibraryInfo tagLibraryInfo = new TagLibraryInfo();
//        tagLibraryInfo.setTlibversion(tldMetaData.getTlibVersion());
//        if (tldMetaData.getJspVersion() == null) {
//            tagLibraryInfo.setJspversion(tldMetaData.getVersion());
//        } else {
//            tagLibraryInfo.setJspversion(tldMetaData.getJspVersion());
//        }
//        tagLibraryInfo.setShortname(tldMetaData.getShortName());
//        tagLibraryInfo.setUri(tldMetaData.getUri());
//        if (tldMetaData.getDescriptionGroup() != null) {
//            tagLibraryInfo.setInfo(tldMetaData.getDescriptionGroup().getDescription());
//        }
//        // Validator
//        if (tldMetaData.getValidator() != null) {
//            TagLibraryValidatorInfo tagLibraryValidatorInfo = new TagLibraryValidatorInfo();
//            tagLibraryValidatorInfo.setValidatorClass(tldMetaData.getValidator().getValidatorClass());
//            if (tldMetaData.getValidator().getInitParams() != null) {
//                for (ParamValueMetaData paramValueMetaData : tldMetaData.getValidator().getInitParams()) {
//                    tagLibraryValidatorInfo.addInitParam(paramValueMetaData.getParamName(), paramValueMetaData.getParamValue());
//                }
//            }
//            tagLibraryInfo.setValidator(tagLibraryValidatorInfo);
//        }
//        // Tag
//        if (tldMetaData.getTags() != null) {
//            for (TagMetaData tagMetaData : tldMetaData.getTags()) {
//                TagInfo tagInfo = new TagInfo();
//                tagInfo.setTagName(tagMetaData.getName());
//                tagInfo.setTagClassName(tagMetaData.getTagClass());
//                tagInfo.setTagExtraInfo(tagMetaData.getTeiClass());
//                if (tagMetaData.getBodyContent() != null) {
//                    tagInfo.setBodyContent(tagMetaData.getBodyContent().toString());
//                }
//                tagInfo.setDynamicAttributes(tagMetaData.getDynamicAttributes());
//                // Description group
//                if (tagMetaData.getDescriptionGroup() != null) {
//                    DescriptionGroupMetaData descriptionGroup = tagMetaData.getDescriptionGroup();
//                    if (descriptionGroup.getIcons() != null && descriptionGroup.getIcons().value() != null && (descriptionGroup.getIcons().value().length > 0)) {
//                        Icon icon = descriptionGroup.getIcons().value()[0];
//                        tagInfo.setLargeIcon(icon.largeIcon());
//                        tagInfo.setSmallIcon(icon.smallIcon());
//                    }
//                    tagInfo.setInfoString(descriptionGroup.getDescription());
//                    tagInfo.setDisplayName(descriptionGroup.getDisplayName());
//                }
//                // Variable
//                if (tagMetaData.getVariables() != null) {
//                    for (VariableMetaData variableMetaData : tagMetaData.getVariables()) {
//                        TagVariableInfo tagVariableInfo = new TagVariableInfo();
//                        tagVariableInfo.setNameGiven(variableMetaData.getNameGiven());
//                        tagVariableInfo.setNameFromAttribute(variableMetaData.getNameFromAttribute());
//                        tagVariableInfo.setClassName(variableMetaData.getVariableClass());
//                        tagVariableInfo.setDeclare(variableMetaData.getDeclare());
//                        if (variableMetaData.getScope() != null) {
//                            tagVariableInfo.setScope(variableMetaData.getScope().toString());
//                        }
//                        tagInfo.addTagVariableInfo(tagVariableInfo);
//                    }
//                }
//                // Attribute
//                if (tagMetaData.getAttributes() != null) {
//                    for (AttributeMetaData attributeMetaData : tagMetaData.getAttributes()) {
//                        TagAttributeInfo tagAttributeInfo = new TagAttributeInfo();
//                        tagAttributeInfo.setName(attributeMetaData.getName());
//                        tagAttributeInfo.setType(attributeMetaData.getType());
//                        tagAttributeInfo.setReqTime(attributeMetaData.getRtexprvalue());
//                        tagAttributeInfo.setRequired(attributeMetaData.getRequired());
//                        tagAttributeInfo.setFragment(attributeMetaData.getFragment());
//                        if (attributeMetaData.getDeferredValue() != null) {
//                            tagAttributeInfo.setDeferredValue("true");
//                            tagAttributeInfo.setExpectedTypeName(attributeMetaData.getDeferredValue().getType());
//                        } else {
//                            tagAttributeInfo.setDeferredValue("false");
//                        }
//                        if (attributeMetaData.getDeferredMethod() != null) {
//                            tagAttributeInfo.setDeferredMethod("true");
//                            tagAttributeInfo.setMethodSignature(attributeMetaData.getDeferredMethod().getMethodSignature());
//                        } else {
//                            tagAttributeInfo.setDeferredMethod("false");
//                        }
//                        tagInfo.addTagAttributeInfo(tagAttributeInfo);
//                    }
//                }
//                tagLibraryInfo.addTagInfo(tagInfo);
//            }
//        }
//        // Tag files
//        if (tldMetaData.getTagFiles() != null) {
//            for (TagFileMetaData tagFileMetaData : tldMetaData.getTagFiles()) {
//                TagFileInfo tagFileInfo = new TagFileInfo();
//                tagFileInfo.setName(tagFileMetaData.getName());
//                tagFileInfo.setPath(tagFileMetaData.getPath());
//                tagLibraryInfo.addTagFileInfo(tagFileInfo);
//            }
//        }
//        // Function
//        if (tldMetaData.getFunctions() != null) {
//            for (FunctionMetaData functionMetaData : tldMetaData.getFunctions()) {
//                FunctionInfo functionInfo = new FunctionInfo();
//                functionInfo.setName(functionMetaData.getName());
//                functionInfo.setFunctionClass(functionMetaData.getFunctionClass());
//                functionInfo.setFunctionSignature(functionMetaData.getFunctionSignature());
//                tagLibraryInfo.addFunctionInfo(functionInfo);
//            }
//        }
//
//        return tagLibraryInfo;
//    }
}
