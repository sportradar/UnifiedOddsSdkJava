package com.sportradar.unifiedodds.sdk.shared;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sportradar.unifiedodds.sdk.SDKInternalConfiguration;
import com.sportradar.unifiedodds.sdk.impl.Deserializer;
import com.sportradar.unifiedodds.sdk.impl.apireaders.HttpHelper;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.CloseableHttpClient;

import java.util.*;

public class TestHttpHelper extends HttpHelper {

    /**
     * The list of URI replacements (to get wanted response when specific url is called)
     */
    public Map<String, String> UriReplacements;

    /**
     * The list of possible post responses (to get wanted response when specific url is called)
     * string: url (or part of it) to be searched, int: 0-wanted response or HttpStatus.BadRequest; 1-wanted response; 2-wanted response or HttpStatus.NotFound
     */
    public List<UrlReplacement> PostResponses;

    /**
     * The list of possible put responses (to get wanted response when specific url is called)
     * string: url (or part of it) to be searched, int: 0-wanted response or HttpStatus.BadRequest; 1-wanted response; 2-wanted response or HttpStatus.NotFound
     */
    public  List<UrlReplacement> PutResponses;

    /**
     * The list of possible delete responses (to get wanted response when specific url is called)
     * string: url (or part of it) to be searched, int: 0-wanted response or HttpStatus.BadRequest; 1-wanted response; 2-wanted response or HttpStatus.NotFound
     */
    public List<UrlReplacement> DeleteResponses;

    /**
     * The list of called urls
     */
    public List<String> CalledUrls;

    @Inject
    public TestHttpHelper(SDKInternalConfiguration config, CloseableHttpClient httpClient, @Named("SportsApiJaxbDeserializer") Deserializer apiDeserializer) {
        super(config, httpClient, apiDeserializer);

        UriReplacements = new HashMap<>();
        PostResponses = new ArrayList<>();
        PutResponses = new ArrayList<>();
        DeleteResponses = new ArrayList<>();
        CalledUrls = new ArrayList<>();
    }

    public ResponseData post(String path) {
        CalledUrls.add(path);
        ResponseData response = new ResponseData(HttpStatus.SC_OK, "default");
        Optional<UrlReplacement> urlReplacement = PostResponses.stream().filter(f -> path.contains(f.getUrl())).findFirst();
        if (urlReplacement.isPresent())
        {
            switch (urlReplacement.get().responseType)
            {
                case 0:
                    response = urlReplacement.get().getHttpStatus() == 0
                            ? new ResponseData(HttpStatus.SC_BAD_REQUEST, null)
                            : new ResponseData(urlReplacement.get().getHttpStatus(), "pre-set");
                    break;
                case 1:
                    break;
                case 2:
                    response = urlReplacement.get().getHttpStatus() == 0
                            ? new ResponseData(HttpStatus.SC_NOT_FOUND, null)
                            : new ResponseData(urlReplacement.get().getHttpStatus(), "pre-set");
                    break;
            }
        }

        return response;
    }

    public ResponseData put(String path) {
        CalledUrls.add(path);
        ResponseData response = new ResponseData(HttpStatus.SC_OK, "default");
        Optional<UrlReplacement> urlReplacement = PutResponses.stream().filter(f -> path.contains(f.getUrl())).findFirst();
        if (urlReplacement.isPresent())
        {
            switch (urlReplacement.get().responseType)
            {
                case 0:
                    response = urlReplacement.get().getHttpStatus() == 0
                            ? new ResponseData(HttpStatus.SC_BAD_REQUEST, null)
                            : new ResponseData(urlReplacement.get().getHttpStatus(), "pre-set");
                    break;
                case 1:
                    break;
                case 2:
                    response = urlReplacement.get().getHttpStatus() == 0
                            ? new ResponseData(HttpStatus.SC_NOT_FOUND, null)
                            : new ResponseData(urlReplacement.get().getHttpStatus(), "pre-set");
                    break;
            }
        }

        return response;
    }

    public ResponseData delete(String path) {
        CalledUrls.add(path);
        ResponseData response = new ResponseData(HttpStatus.SC_OK, "default");
        Optional<UrlReplacement> urlReplacement = DeleteResponses.stream().filter(f -> path.contains(f.getUrl())).findFirst();
        if (urlReplacement.isPresent())
        {
            switch (urlReplacement.get().responseType)
            {
                case 0:
                    response = urlReplacement.get().getHttpStatus() == 0
                            ? new ResponseData(HttpStatus.SC_BAD_REQUEST, null)
                            : new ResponseData(urlReplacement.get().getHttpStatus(), "pre-set");
                    break;
                case 1:
                    break;
                case 2:
                    response = urlReplacement.get().getHttpStatus() == 0
                            ? new ResponseData(HttpStatus.SC_NOT_FOUND, null)
                            : new ResponseData(urlReplacement.get().getHttpStatus(), "pre-set");
                    break;
            }
        }

        return response;
    }

    public static class UrlReplacement{
        private final String urlPart;
        private final int responseType;
        private final int httpStatus;

        /**
         * Get the url part to be checked within request
         * @return the url part to be checked within request
         */
        public String getUrl(){ return urlPart; }

        /**
         * Get the response type
         * @return the response type
         */
        public int getResponseType(){ return responseType; }

        /**
         * Get wanted http response
         * @return wanted http response
         */
        public int getHttpStatus(){ return httpStatus; }

        /**
         * Construct new instance
         * @param url url (or part of it) to be searched
         * @param responseType int: 0-wanted response or HttpStatus.BadRequest; 1-wanted response; 2-wanted response or HttpStatus.NotFound
         * @param httpStatus wanted response
         */
        public UrlReplacement(String url, int responseType, int httpStatus){
            this.urlPart = url;
            this.responseType = responseType;
            this.httpStatus = httpStatus;
        }
    }
}
