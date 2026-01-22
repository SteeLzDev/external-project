package com.zetra.econsig.helper.markdown;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.markdown4j.CodeBlockEmitter;
import org.markdown4j.IncludePlugin;
import org.markdown4j.Markdown4jProcessor;
import org.markdown4j.Plugin;
import org.markdown4j.WebSequencePlugin;
import org.markdown4j.YumlPlugin;

import com.github.rjeschke.txtmark.Processor;
import com.zetra.econsig.helper.markdown.Configuration.Builder;

public class Markdown4jProcessorExtended extends Markdown4jProcessor{

    private ExtDecoratorExtended decorator;

    private final Builder builder;

    public Markdown4jProcessorExtended() {
        this(true);
    }

    public Markdown4jProcessorExtended(boolean includeParagraph) {
        builder = builder(includeParagraph);
    }

    private Builder builder(boolean includeParagraph) {
        decorator = new ExtDecoratorExtended(includeParagraph);
        return Configuration.builder().enableSafeMode().forceExtentedProfile().registerPlugins(new YumlPlugin(), new WebSequencePlugin(), new IncludePlugin()).convertNewline2Br().setDecorator(decorator).setCodeBlockEmitter(new CodeBlockEmitter());
    }

    @Override
    public Markdown4jProcessor registerPlugins(Plugin ... plugins) {
        builder.registerPlugins(plugins);
        return this;
    }
    public Markdown4jProcessor setDecorator(ExtDecoratorExtended decorator) {
        this.decorator = decorator;
        builder.setDecorator(decorator);
        return this;
    }
    @Override
    public Markdown4jProcessor addHtmlAttribute(String name, String value, String ...tags) {
        decorator.addHtmlAttribute(name, value, tags);
        return this;
    }
    @Override
    public Markdown4jProcessor addStyleClass(String styleClass, String ...tags) {
        decorator.addStyleClass(styleClass, tags);
        return this;
    }
    @Override
    public String process(File file) throws IOException {
        return ProcessorExtended.process(file, builder.build());
    }
    @Override
    public String process(InputStream input) throws IOException {
        return Processor.process(input);
    }
    @Override
    public String process(Reader reader) throws IOException {
        return ProcessorExtended.process(reader, builder.build());
    }
    @Override
    public String process(String input) throws IOException {
        return ProcessorExtended.process(input, builder.build());
    }

}
