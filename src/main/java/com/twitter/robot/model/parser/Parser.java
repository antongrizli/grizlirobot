package com.twitter.robot.model.parser;

import javax.xml.bind.JAXBException;
import java.io.File;

/**
 * Created by Антон on 01.10.2015.
 */
public interface Parser {
    Object getObject(File file, Class c) throws JAXBException;
    void saveObject(File file, Object o) throws JAXBException;
}
