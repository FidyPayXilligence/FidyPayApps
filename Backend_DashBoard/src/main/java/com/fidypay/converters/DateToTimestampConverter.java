//package com.fidypay.converters;
//
//import org.elasticsearch.common.xcontent.ToXContentObject;
//import org.elasticsearch.common.xcontent.XContentBuilder;
//
//import java.io.IOException;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//
///**
// * @author prave
// * @Date 06-09-2023
// */
//public class DateToTimestampConverter implements ToXContentObject {
//
//    private static final String FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
//
//    private final Date date;
//
//    public DateToTimestampConverter(Date date) {
//        this.date = date;
//    }
//
//    @Override
//    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
//        DateFormat dateFormat = new SimpleDateFormat(FORMAT, Locale.US);
//        String timestamp = dateFormat.format(date);
//        builder.value(timestamp);
//        return builder;
//    }
//
//    public Date getDate() {
//        return date;
//    }
//
////    public static DateToTimestampConverter parse(XContentParser parser) throws IOException {
////        Date date = null;
////        XContentParser.Token token;
////        DateFormat dateFormat = new SimpleDateFormat(FORMAT, Locale.US);
////
////        while ((token = parser.nextToken()) != XContentParser.Token.END_OBJECT) {
////            if (token == XContentParser.Token.FIELD_NAME) {
////                String fieldName = parser.currentName();
////                if ("date".equals(fieldName)) {
////                    parser.nextToken(); // Move to the field value
////                    String dateStr = parser.text();
////                    try {
////                        date = dateFormat.parse(dateStr);
////                    } catch (Exception e) {
////                        // Handle parsing error
////                        date = null;
////                    }
////                } else {
////                    XContentParserUtils.throwUnknownField(fieldName, parser);
////                }
////            } else {
////                XContentParserUtils.throwUnknownToken(token);
////            }
////        }
////
////        if (date == null) {
////            throw new IllegalArgumentException("Missing or invalid 'date' field");
////        }
////
////        return new DateToTimestampConverter(date);
////    }
//}
