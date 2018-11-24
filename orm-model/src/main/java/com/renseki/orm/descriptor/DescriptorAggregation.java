package com.renseki.orm.descriptor;

import com.renseki.orm.RensekiConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class DescriptorAggregation {

    private final Collection<Descriptor> descriptors;

    private DescriptorAggregation(Builder b) {
        this.descriptors = b.descriptors;
    }

    public Collection<Descriptor> getDescriptors() {
        return descriptors;
    }

    public static class Builder {

        Collection<Descriptor> descriptors = new ArrayList<>();

        private String readDescriptor(String path) {
            final String res;
            try ( InputStream in = DescriptorAggregation.class.getResourceAsStream(path) ) {
                res = IOUtils.toString(in, "UTF-8");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return res;
        }

        public DescriptorAggregation build() {
            ResourcesScanner resourceScanner = new ResourcesScanner();
            Reflections reflections =
                new Reflections(
                    RensekiConstants.Module.PREFIX, resourceScanner);

            Collection<String> descriptorsPath =
                reflections.getResources(
                    Pattern.compile(
                        RensekiConstants.Module.DESCRIPTOR_REGEX));

            for ( String path : descriptorsPath ) {
                String resPath = path;
                if ( !resPath.startsWith("/") ) {
                    resPath = "/" + resPath;
                }

                final String descriptorXml = this.readDescriptor(resPath);
                this.descriptors.add(
                    new Descriptor.Builder(resPath, descriptorXml).build()
                );
            }

            return new DescriptorAggregation(this);
        }
    }

}
