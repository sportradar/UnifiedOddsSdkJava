package com.sportradar.unifiedodds.sdk.shared;

import com.sportradar.unifiedodds.sdk.oddsentities.UnmarshalledMessage;
import com.sportradar.utils.URN;
import java.io.StringWriter;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

@SuppressWarnings({ "HideUtilityClassConstructor", "IllegalCatch", "MagicNumber", "ParameterAssignment" })
public class Helper {

    public static void writeToOutput(String message) {
        message = String.format("%s\t%s", new Date(), message);
        System.out.println(message);
    }

    //    public static String serializeToXml(UnmarshalledMessage feedMessage){
    //        try {
    //            XmlMapper xmlMapper = new XmlMapper();
    //            String xml = xmlMapper.writeValueAsString(feedMessage);
    //            return xml;
    //        }
    //        catch (IOException e) {
    //                WriteToOutput("Error serialize: " + e.getMessage());
    //        }
    //        return "";
    //    }

    public static String serializeToJaxbXml(UnmarshalledMessage feedMessage) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(feedMessage.getClass());
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter xml = new StringWriter();
            marshaller.marshal(feedMessage, xml);
            return xml.toString();
            //            XmlMapper xmlMapper = new XmlMapper();
            //            String xml = xmlMapper.writeValueAsString(feedMessage);
            //            return xml;
        } catch (Exception e) {
            writeToOutput("Error serialize: " + e.getMessage());
        }
        return "";
    }

    public static void sleep(long sleepMs) {
        try {
            Thread.sleep(sleepMs);
        } catch (InterruptedException e) {
            writeToOutput("Error sleep: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    public static long durationBetweenDatesInMs(Date startDate, Date endDate) {
        long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
        //        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        return diffInMillies;
    }

    public static String provideCleanMsgForLog(byte[] body) {
        String s = new String(body);
        return s.replace("\n", "");
    }

    public static Date addToDate(int calendarType, int value) {
        return addToDate(new Date(), calendarType, value);
    }

    public static Date addToDate(Date date, int calendarType, int value) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(calendarType, value);
        return cal.getTime();
    }

    public static URN generateEventId() {
        return generateEventId("match");
    }

    public static URN generateEventId(String urnGroup) {
        return URN.parse("sr:" + urnGroup + ":" + Math.abs(new Random().nextInt(999999)));
    }

    public static XMLGregorianCalendar getCalendar(Instant date) {
        try {
            if (date == null) {
                date = Instant.now();
            }
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(date.toString());
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
