package com.renseki.orm.descriptor;

import com.amazonaws.util.StringInputStream;
import com.renseki.orm.RensekiConstants;
import com.renseki.orm.RensekiXmlUtils;
import com.renseki.orm.exception.InvalidModuleDescriptorFormatException;
import com.renseki.orm.exception.InvalidModulePrefixException;
import com.renseki.orm.exception.InvalidXmlRootTagNameException;
import org.jdom2.DataConversionException;
import org.jdom2.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Descriptor {

    private final String path;
    private final String descriptorXml;

    private final String moduleName;
    private final String shortDescription;
    private final String version;
    private final String category;
    private final String author;
    private final String description;
    private final boolean installable;
    private final boolean autoInstall;
    private final List<String> models;
    private final List<String> data;
    private final List<String> demo;
    private final List<String> dependencies;

    private Descriptor(Builder b) {
        this.path = b.path;
        this.descriptorXml = b.descriptorXml;
        this.moduleName = b.moduleName;
        this.shortDescription = b.shortDescription;
        this.version = b.version;
        this.category = b.category;
        this.author = b.author;
        this.description = b.description;
        this.installable = b.installable;
        this.autoInstall = b.autoInstall;
        this.models = b.models;
        this.data = b.data;
        this.demo = b.demo;
        this.dependencies = b.dependencies;
    }

    public String getPath() {
        return path;
    }

    public String getDescriptorXml() {
        return descriptorXml;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getVersion() {
        return version;
    }

    public String getCategory() {
        return category;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public boolean isInstallable() {
        return installable;
    }

    public boolean isAutoInstall() {
        return autoInstall;
    }

    public List<String> getModels() {
        return models;
    }

    public List<String> getData() {
        return data;
    }

    public List<String> getDemo() {
        return demo;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public static class Builder {

        private final String path;
        private final String descriptorXml;

        String moduleName;
        String shortDescription;
        String version;
        String category;
        String author;
        String description;
        boolean installable = true;
        boolean autoInstall = false;
        List<String> models = new ArrayList<>();
        List<String> data = new ArrayList<>();
        List<String> demo = new ArrayList<>();
        List<String> dependencies = new ArrayList<>();

        public Builder(String path, String descriptorXml) {
            this.path = path;
            this.descriptorXml = descriptorXml;
        }

        private Optional<String> getModuleName(String path) {
            //  Descriptor Name
            String tmpPath = path;
            if ( tmpPath.startsWith("/") ) {
                tmpPath = tmpPath.substring(1);
            }

            String res = null;
            tmpPath = tmpPath.replace(RensekiConstants.Module.PREFIX, "");
            if ( !tmpPath.isEmpty() ) {
                tmpPath = tmpPath.substring(1);

                String[] splitter = tmpPath.split("/", 2);
                res = splitter[0].trim();
            }

            return Optional.ofNullable(res);
        }

        private Element unwrapXmlElement(String descriptorXml) {
            Element result;
            if ( !RensekiXmlUtils.isValidRensekiXml(descriptorXml) ) {
                final String rootTagName = RensekiXmlUtils.getRootTagName(descriptorXml);
                throw new InvalidXmlRootTagNameException(rootTagName);
            }

            Element root = RensekiXmlUtils.read(descriptorXml).get();
            if ( root.getChildren("module").size() != 1 ) {
                throw new InvalidModuleDescriptorFormatException();
            }

            result = root;
            return result;
        }

        private List<String> split(String value) {
            String[] splitter = value.split(",");
            List<String> result = new LinkedList<>();
            for (String depend : splitter) {
                final String res = depend.trim();
                if (!res.isEmpty()) {
                    result.add(depend.trim());
                }
            }

            return result;
        }

        public Descriptor build() {
            //  Descriptor moduleName
            Optional<String> optModuleName = this.getModuleName(this.path);
            if ( !optModuleName.isPresent() ) {
                throw new InvalidModulePrefixException(this.path);
            }
            this.moduleName = optModuleName.get();

            //  Descriptor Values
            final Element root = this.unwrapXmlElement(this.descriptorXml);


            Element module = root.getChild("module");

            shortDescription = module.getAttributeValue("name");
            version = module.getAttributeValue("version");
            category = module.getAttributeValue("category");
            author = module.getAttributeValue("author");

            try {
                installable = module.getAttribute("installable").getBooleanValue();
                if (module.getAttribute("auto_install") != null) {
                    autoInstall = module.getAttribute("auto_install").getBooleanValue();
                }
            } catch (DataConversionException e) {
                throw new RuntimeException(e);
            }

            /*
                Get Description
             */
            Element descriptionTag = module.getChild("description");
            description = descriptionTag == null ? null : descriptionTag.getTextTrim();

            /*
                Get Dependencies
             */
            Element dependenciesTag = module.getChild("dependencies");
            dependencies = dependenciesTag == null ?
                Collections.<String>emptyList() : this.split(dependenciesTag.getTextTrim());


            /*
                Get Models
             */
            Element modelsTag = module.getChild("models");
            models = modelsTag == null ?
                Collections.<String>emptyList() : this.split(modelsTag.getTextTrim());

            /*
                Get Data
             */
            Element dataTag = module.getChild("data");
            data = dataTag == null ? Collections.<String>emptyList() : this.split(dataTag.getTextTrim());

            /*
                Get Demo
             */
            Element demoTag = module.getChild("demo");
            demo = demoTag == null ? Collections.<String>emptyList() : this.split(demoTag.getTextTrim());

            return new Descriptor(this);
        }
    }
}
