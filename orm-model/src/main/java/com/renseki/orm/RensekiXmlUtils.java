package com.renseki.orm;

import com.amazonaws.util.StringInputStream;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RensekiXmlUtils {

    private static final List<String> VALID_ROOT_TAGS = Arrays.asList(
        RensekiConstants.Xml.LEGACY_ROOT_TAG,
        RensekiConstants.Xml.ROOT_TAG
    );

    public static String getRootTagName(String xml) {
        String res = null;
        Optional<Element> opt = read(xml);
        if ( opt.isPresent() ) {
            res = opt.get().getName();
        }

        return res;
    }

    public static Optional<Element> read(String xml) {
        SAXBuilder builder = new SAXBuilder();
        Document doc = null;

        Element res = null;
        try ( InputStream is = new StringInputStream(xml) ){
            doc = builder.build(is);
            res = doc.getRootElement();
        } catch (Exception ignored) { }
        return Optional.ofNullable(res);
    }

    public static boolean isValidRensekiXml(String xml) {
        Optional<Element> optEl = read(xml);
        if ( !optEl.isPresent() ) {
            return false;
        }

        Element el = optEl.get();
        return VALID_ROOT_TAGS.contains(el.getName());
    }

}
